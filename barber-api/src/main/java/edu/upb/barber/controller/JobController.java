package edu.upb.barber.controller;

import edu.upb.barber.quartz.service.JobDto;
import edu.upb.barber.quartz.service.JobService;
import edu.upb.barber.quartz.service.JobUtil;
import edu.upb.barber.repository.dto.request.EmpresaRequestDto;
import edu.upb.barber.repository.dto.response.EmpresaResponseDto;
import edu.upb.barber.repository.entity.Usuario;
import edu.upb.barber.service.EmpresaService;
import edu.upb.barber.service.exception.OperationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobDto>> listar() {
        try {
            return ResponseEntity.ok(jobService.getAllJobs(JobUtil.GROUP_NAME));
        } catch (Exception e) {
            log.error("Error al listar job", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/pausar")
    public ResponseEntity<Void> guardar(@RequestParam("jobName") String jobName) {
        try {
            this.jobService.pauseJob(JobUtil.GROUP_NAME,jobName);
            return ResponseEntity.ok().build();
        } catch (OperationException e) {
            log.error("Error al guardar empresa. Message: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }catch (Exception e){
            log.error("Error inesperado al guardar empresa", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}