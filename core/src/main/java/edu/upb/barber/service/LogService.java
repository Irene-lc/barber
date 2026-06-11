package edu.upb.barber.service;

import edu.upb.barber.repository.LogRepository;
import edu.upb.barber.repository.entity.Log;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository repository;


    @Async("taskLog")
    @Transactional(propagation = Propagation.REQUIRES_NEW) //con este decorador le decimos que cree una transaccion nueva
    public void info(String message) {

        log.info("Registrando log INFO: " + message);
        repository.save(
                Log.builder()
                        .level(LogLevel.INFO)
                        .message(message)
                        .build()
        );
    }


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)

    public void error(String message) {
        repository.save(
                Log.builder()
                        .level(LogLevel.INFO)
                        .message(message)
                        .build()
        );
    }

    @Async

    @Transactional(propagation = Propagation.REQUIRES_NEW)

    public void waring(String message) {
        repository.save(
                Log.builder()
                        .level(LogLevel.INFO)
                        .message(message)
                        .build()
        );
    }
}
