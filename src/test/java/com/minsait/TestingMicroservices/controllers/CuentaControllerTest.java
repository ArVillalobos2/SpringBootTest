package com.minsait.TestingMicroservices.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minsait.TestingMicroservices.exceptions.DineroInsuficienteException;
import com.minsait.TestingMicroservices.models.Cuenta;
import com.minsait.TestingMicroservices.models.TransferirDTO;
import com.minsait.TestingMicroservices.services.CuentaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {
    //Mock mvc simula la respuesta del servidor web

    @Autowired
    private MockMvc mvc;

    ObjectMapper mapper;

    @BeforeEach
    void setUp(){
        mapper = new ObjectMapper();
    }

    @MockBean
    private CuentaService service;

    @Test
    void testListar() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(Datos.crearCuenta().get(), Datos.crearCuenta2().get()));
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/listar").contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Ricardo"))
                .andExpect(jsonPath("$[1].persona").value("Sebastian"));
    }

    @Test
    void testListarId() throws Exception {
        Mockito.when(service.findById(1L)).thenReturn(Datos.crearCuenta().get());
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/listar/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona", Matchers.is("Ricardo")))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(service, times(1)).findById(1L);
    }

    @Test
    void testListarIdException() throws Exception {
        Mockito.when(service.findById(3L)).thenThrow(NoSuchElementException.class);

        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/listar/3").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void testGuardar() throws Exception {
        Cuenta cuenta = new Cuenta(null, "Prueba", new BigDecimal(2));
        Mockito.when(service.save(any())).then(
                invocationOnMock -> {
                    Cuenta cuenta1 = invocationOnMock.getArgument(0);
                    cuenta1.setId(5L);
                    return cuenta1;
                }
        );

        mvc.perform(post("/api/cuentas/guardar").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cuenta)))
                .andExpect(jsonPath("$.persona", Matchers.is("Prueba")))
                .andExpect(jsonPath("$.id", Matchers.is(5)))
                .andExpect(jsonPath("$.saldo", Matchers.is(2)));

    }

    @Test
    @DisplayName("Testing transfer")
    void testTransferencia() throws Exception {
        TransferirDTO dto = new TransferirDTO();
        dto.setCuentaDestinoId(2L);
        dto.setCuentaOrigenId(1L);
        dto.setMonto(new BigDecimal(1));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("transaccion", dto);
        response.put("status","OK");
        response.put("message","Transferencia realizada con éxito");

        mvc.perform(post("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(response))
                );
    }

    @Test
    @DisplayName("Testing transfer")
    void testTransferenciaException() throws Exception {
        Cuenta cuenta = Datos.crearCuenta().get();
        Exception exception = assertThrows(DineroInsuficienteException.class,()-> {
            cuenta.retirar(new BigDecimal(1001));
        });
        doThrow(exception).when(service).transferir(anyLong(),anyLong(), any(), anyLong());

        TransferirDTO dto = new TransferirDTO();
        dto.setCuentaDestinoId(2L);
        dto.setCuentaOrigenId(1L);
        dto.setMonto(new BigDecimal(1));
        dto.setBancoId(1L);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("transaccion", dto);
        response.put("status","OK");
        response.put("message",exception.getMessage());

        mvc.perform(post("/api/cuentas/transferir").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message", Matchers.is(exception.getMessage())),
                        content().json(mapper.writeValueAsString(response))
                );
    }

    @Test
    void testDelete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/cuentas/delete/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.is("La cuenta se elimino correctamente")));

        verify(service, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteException() throws Exception {

        doThrow(NoSuchElementException.class).when(service).deleteById(anyLong());
        mvc.perform(MockMvcRequestBuilders.delete("/api/cuentas/delete/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status",Matchers.is("No se encontró el id")))
                .andExpect(jsonPath("message", Matchers.is(new NoSuchElementException().getMessage())));
    }

    @Test
    void testUpdate() throws Exception {
        Cuenta cambio = new Cuenta(1L, "Armando", new BigDecimal(1000));
        Cuenta cuenta1 = new Cuenta(2L, "Pedro", new BigDecimal(1));
        Mockito.when(service.update(any(), anyLong())).then(
                invocationOnMock -> {
                    cuenta1.setPersona(cambio.getPersona());
                    cuenta1.setSaldo(cambio.getSaldo());
                    return cuenta1;
                }
        );

        mvc.perform(patch("/api/cuentas/update/1").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cambio)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("message", Matchers.is("La cuenta se modificó correctamente"))
                );

        assertEquals(cambio.getPersona(), cuenta1.getPersona());
        assertEquals(cambio.getSaldo(), cuenta1.getSaldo());
        assertNotEquals(cambio.getId(), cuenta1.getId());
    }

    @Test
    void testUpdateException() throws Exception {
        Cuenta cambio = new Cuenta(1L, "Armando", new BigDecimal(1000));
        doThrow(NoSuchElementException.class).when(service).update(any(),anyLong());

        mvc.perform(patch("/api/cuentas/update/5").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cambio)))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("message", Matchers.is(new NoSuchElementException().getMessage())),
                        jsonPath("$.status").value("No se encontró el id")
                );
    }

    @Test
    void testSaldo() throws Exception {
        Mockito.when(service.revisarSaldo(1L)).thenReturn(Datos.crearCuenta().get().getSaldo());
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/saldo/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Saldo").value("1000"))
                .andExpect(jsonPath("$.status").value("Ok"));

        verify(service, times(1)).revisarSaldo(1L);
    }

    @Test
    void testSaldoException() throws Exception {
        Mockito.when(service.revisarSaldo(3L)).thenThrow(NoSuchElementException.class);

        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/saldo/3").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("No se encontró el id"));


    }

    @Test
    void testTransferenciasBanco() throws Exception {
        Mockito.when(service.revisarTotalTransferencias(1L)).thenReturn(Datos.crearBanco().get().getTotalTransferencia());
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/transferencias/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Transferencias").value("0"))
                .andExpect(jsonPath("$.status").value("Ok"));

        verify(service, times(1)).revisarTotalTransferencias(1L);
    }

    @Test
    void testTransferenciasBancoException() throws Exception {
        Mockito.when(service.revisarTotalTransferencias(3L)).thenThrow(NoSuchElementException.class);

        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/transferencias/3").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("No se encontró el id del banco"));
    }


}