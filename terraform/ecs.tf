resource "aws_ecs_cluster" "main" {
  name = "barber-cluster"
}

resource "aws_security_group" "alb" {
  name   = "barber-alb-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_tasks" {
  name   = "barber-ecs-sg"
  vpc_id = aws_vpc.main.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_lb" "main" {
  name               = "barber-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = [aws_subnet.public_a.id, aws_subnet.public_b.id]
}

resource "aws_lb_target_group" "backend" {
  name        = "barber-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    path                = "/actuator/health"
    healthy_threshold   = 2
    unhealthy_threshold = 5
    interval            = 30
    timeout             = 10
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}

resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/barber-backend"
  retention_in_days = 7
}

# Rol mínimo de ejecución — solo para que ECS pueda hacer pull de la imagen ECR
# No requiere iam:CreateRole con permisos amplios, solo los básicos de ECS
resource "aws_iam_role" "ecs_task_execution" {
  name = "barber-ecs-execution-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "barber-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([{
    name  = "barber-backend"
    image = var.ecr_image_url

    portMappings = [{ containerPort = 8080 }]

    environment = [
      { name = "SPRING_DATASOURCE_URL",      value = "jdbc:postgresql://${aws_db_instance.postgres.address}:5432/barber" },
      { name = "USER_DB",                    value = var.db_username },
      { name = "PASSWORD_DB",                value = var.db_password },
      { name = "BARBER_SECRET_KEY",          value = var.jwt_secret },
      { name = "STEREUM_API_KEY",            value = var.stereum_api_key },
      { name = "STEREUM_SECRET_KEY",         value = var.stereum_secret_key },
      { name = "STEREUM_ACCOUNT_ID",         value = var.stereum_account_id },
      { name = "MAIL_SMTP_USERNAME",         value = var.mail_username },
      { name = "MAIL_SMTP_PASSWORD",         value = var.mail_password },
      { name = "AWS_ACCESS_KEY_ID",          value = var.aws_access_key_id },
      { name = "AWS_SECRET_ACCESS_KEY",      value = var.aws_secret_access_key },
      { name = "AWS_REGION",                 value = var.aws_region },
      { name = "APP_FRONTEND_URL",           value = "https://${aws_cloudfront_distribution.frontend.domain_name}" }
    ]

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = "/ecs/barber-backend"
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}

resource "aws_ecs_service" "backend" {
  name                               = "barber-backend"
  cluster                            = aws_ecs_cluster.main.id
  task_definition                    = aws_ecs_task_definition.backend.arn
  desired_count                      = 1
  launch_type                        = "FARGATE"
  health_check_grace_period_seconds  = 120

  network_configuration {
    subnets          = [aws_subnet.public_a.id, aws_subnet.public_b.id]
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = "barber-backend"
    container_port   = 8080
  }

  lifecycle {
    ignore_changes = [task_definition]
  }
}
