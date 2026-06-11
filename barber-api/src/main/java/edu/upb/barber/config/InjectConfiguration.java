package edu.upb.barber.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Slf4j
@Configuration
public class InjectConfiguration {
    @Value("${async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${async.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${async.queue-capacity:10}")
    private int queueCapacity;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("ADMIN");
            }
            if(authentication.getPrincipal()==null || authentication.getPrincipal() instanceof String) {
                return Optional.of("ADMIN");
            }

            //User user = (User) authentication.getPrincipal();
            //try {
             //   return Optional.ofNullable(user.getId());
            //} catch (Exception e) {
             //   return Optional.of("ADMIN");
            //}
            return Optional.of("ADMIN");
        };
    }

    @Bean(name = "taskLog")
    public ThreadPoolTaskExecutor myTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize); // Número de hilos que siempre estarán activos
        executor.setMaxPoolSize(maxPoolSize); // Número máximo de hilos
        executor.setQueueCapacity(queueCapacity); // Capacidad de la cola para tareas en espera
        executor.setThreadNamePrefix("Miguel-");
        executor.initialize();
        return executor;

    }

    @Scheduled(cron = "0 */1 * * * *")//expresion cron , la manera mas manejable de programar un job
    //con esto le decimos que se ejecute cada minuto
    public void listarEmpresas(){
        log.info("INFO: " +  "listar todos las empresas");
    }

}
