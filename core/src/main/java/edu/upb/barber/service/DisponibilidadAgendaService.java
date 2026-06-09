package edu.upb.barber.service;

import edu.upb.barber.repository.AgendaEventoEmpleadoRepository;
import edu.upb.barber.repository.EmpleadoSucursalRepository;
import edu.upb.barber.repository.HorarioEmpleadoRepository;
import edu.upb.barber.repository.SucursalRepository;
import edu.upb.barber.repository.dto.response.DisponibilidadAgendaResponseDto;
import edu.upb.barber.repository.dto.response.EmpleadoDisponibleResponseDto;
import edu.upb.barber.repository.dto.response.SlotDisponibilidadResponseDto;
import edu.upb.barber.repository.entity.Empleado;
import edu.upb.barber.repository.entity.HorarioEmpleado;
import edu.upb.barber.repository.entity.Sucursal;
import edu.upb.barber.repository.entity.enums.DiaSemana;
import edu.upb.barber.repository.entity.enums.EstadoEvento;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class DisponibilidadAgendaService {

    private static final int SLOT_MINUTOS = 60;

    private final SucursalRepository sucursalRepository;
    private final HorarioEmpleadoRepository horarioEmpleadoRepository;
    private final EmpleadoSucursalRepository empleadoSucursalRepository;
    private final AgendaEventoEmpleadoRepository agendaEventoEmpleadoRepository;

    @Transactional(readOnly = true)
    public DisponibilidadAgendaResponseDto consultar(String sucursalId, LocalDate fecha, String empleadoId) throws Exception {
        if (sucursalId == null || sucursalId.isBlank()) {
            throw new Exception("sucursal_id es requerido");
        }
        if (fecha == null) {
            throw new Exception("fecha es requerida");
        }

        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new Exception("Sucursal no encontrada con ID: " + sucursalId));

        List<HorarioEmpleado> horarios = horarioEmpleadoRepository
                .findBySucursalIdAndDiaSemanaAndActivoTrue(sucursalId, toDiaSemana(fecha.getDayOfWeek()));

        ZoneId zoneId = ZoneId.systemDefault();
        Map<String, SlotDisponibilidadResponseDto> slotsByInicio = new LinkedHashMap<>();

        for (HorarioEmpleado horario : horarios) {
            Empleado empleado = horario.getEmpleado();
            if (!empleadoPuedeAtender(empleado, sucursalId, empleadoId)) {
                continue;
            }

            LocalTime cursor = horario.getHoraInicio();
            LocalTime ultimoInicio = horario.getHoraFin().minusMinutes(SLOT_MINUTOS);

            while (!cursor.isAfter(ultimoInicio)) {
                OffsetDateTime inicio = fecha.atTime(cursor).atZone(zoneId).toOffsetDateTime();
                OffsetDateTime fin = fecha.atTime(cursor.plusMinutes(SLOT_MINUTOS)).atZone(zoneId).toOffsetDateTime();

                if (!tieneConflicto(empleado.getId(), inicio, fin)) {
                    String key = inicio.toString();
                    SlotDisponibilidadResponseDto slot = slotsByInicio.computeIfAbsent(key, ignored ->
                            SlotDisponibilidadResponseDto.builder()
                                    .inicio(inicio)
                                    .fin(fin)
                                    .empleados(new ArrayList<>())
                                    .build()
                    );
                    slot.getEmpleados().add(toEmpleadoDisponible(empleado));
                }

                cursor = cursor.plusMinutes(SLOT_MINUTOS);
            }
        }

        return DisponibilidadAgendaResponseDto.builder()
                .sucursalId(sucursal.getId())
                .sucursalNombre(sucursal.getNombre())
                .fecha(fecha)
                .duracionMinutos(SLOT_MINUTOS)
                .slots(slotsByInicio.values().stream()
                        .filter(slot -> !slot.getEmpleados().isEmpty())
                        .toList())
                .build();
    }

    private boolean empleadoPuedeAtender(Empleado empleado, String sucursalId, String empleadoIdFiltro) {
        if (empleado == null || !empleado.isActivo() || !empleado.isDisponible()) {
            return false;
        }
        if (empleadoIdFiltro != null && !empleadoIdFiltro.isBlank() && !empleado.getId().equals(empleadoIdFiltro)) {
            return false;
        }
        return empleadoSucursalRepository.existsByEmpleadoIdAndSucursalIdAndActivoTrue(empleado.getId(), sucursalId);
    }

    private boolean tieneConflicto(String empleadoId, OffsetDateTime inicio, OffsetDateTime fin) {
        return agendaEventoEmpleadoRepository.existsConflictoHorarioEmpleado(
                empleadoId,
                inicio,
                fin,
                Arrays.asList(EstadoEvento.CANCELADO, EstadoEvento.NO_SHOW)
        );
    }

    private EmpleadoDisponibleResponseDto toEmpleadoDisponible(Empleado empleado) {
        return EmpleadoDisponibleResponseDto.builder()
                .empleadoId(empleado.getId())
                .empleadoNombre(empleado.getNombre())
                .cargo(empleado.getCargo() != null ? empleado.getCargo().name() : null)
                .especialidad(empleado.getEspecialidad())
                .fotoUrl(empleado.getFotoUrl())
                .build();
    }

    private DiaSemana toDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.LUNES;
            case TUESDAY -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY -> DiaSemana.JUEVES;
            case FRIDAY -> DiaSemana.VIERNES;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
