package com.plantilla.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Prueba unitaria: el servicio se prueba solo, con sus dependencias simuladas
 * (mocks). No se necesita base de datos ni servidor, por eso es muy rápida.
 */
@ExtendWith(MockitoExtension.class)
class PingServiceTest {

    @Mock
    private PingRepository pingRepository;

    @Mock
    private HoraBaseDeDatosRepository horaBaseDeDatos;

    @InjectMocks
    private PingService pingService;

    @Test
    void registrar_guardaElMensajeSinEspaciosSobrantes() {
        when(pingRepository.saveAndFlush(any(Ping.class))).thenAnswer(inv -> conDatosDeBd(inv.getArgument(0), 7L));

        PingResponse respuesta = pingService.registrar(new PingRequest("  Hola  "));

        ArgumentCaptor<Ping> guardado = ArgumentCaptor.forClass(Ping.class);
        verify(pingRepository).saveAndFlush(guardado.capture());
        assertThat(guardado.getValue().getMensaje()).isEqualTo("Hola");
        assertThat(respuesta.id()).isEqualTo(7L);
        assertThat(respuesta.mensaje()).isEqualTo("Hola");
    }

    @Test
    void registrar_conviertePingVacioEnNull() {
        when(pingRepository.saveAndFlush(any(Ping.class))).thenAnswer(inv -> conDatosDeBd(inv.getArgument(0), 1L));

        PingResponse respuesta = pingService.registrar(new PingRequest("   "));

        assertThat(respuesta.mensaje()).isNull();
    }

    @Test
    void resumen_devuelveUltimosTotalYHora() {
        var ahora = OffsetDateTime.now();
        when(pingRepository.findTop20ByOrderByCreadoEnDescIdDesc()).thenReturn(List.of(conDatosDeBd(new Ping("a"), 2L)));
        when(pingRepository.count()).thenReturn(5L);
        when(horaBaseDeDatos.ahora()).thenReturn(ahora);

        PingResumen resumen = pingService.resumen();

        assertThat(resumen.ultimos()).extracting(PingResponse::id).containsExactly(2L);
        assertThat(resumen.total()).isEqualTo(5L);
        assertThat(resumen.horaBaseDeDatos()).isEqualTo(ahora);
    }

    /** Simula lo que haría PostgreSQL al guardar: asignar id y fecha. */
    private static Ping conDatosDeBd(Ping ping, Long id) {
        ReflectionTestUtils.setField(ping, "id", id);
        ReflectionTestUtils.setField(ping, "creadoEn", OffsetDateTime.now());
        return ping;
    }
}
