variable "aws_region" {
  default = "us-east-1"
}

variable "db_password" {
  sensitive = true
}

variable "db_username" {
  default = "barber_user"
}

variable "jwt_secret" {
  sensitive = true
}

variable "stereum_api_key" {
  sensitive = true
}

variable "stereum_secret_key" {
  sensitive = true
}

variable "stereum_account_id" {
  description = "Account ID de Stereum Pay"
}

variable "mail_username" {
  sensitive = true
}

variable "mail_password" {
  sensitive = true
}

variable "ecr_image_url" {
  description = "URL de la imagen Docker en ECR"
  default     = "placeholder"
}

variable "aws_access_key_id" {
  sensitive = true
}

variable "aws_secret_access_key" {
  sensitive = true
}
