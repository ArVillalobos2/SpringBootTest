package com.minsait.TestingMicroservices.services;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;
import com.minsait.TestingMicroservices.repositories.BancoRepository;
import com.minsait.TestingMicroservices.repositories.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    BancoRepository bancoRepository;

    @Mock
    CuentaRepository cuentaRepository;

    @InjectMocks
    CuentaServiceImpl service;


    @Test
    void findAll() {
        Mockito.when(cuentaRepository.findAll()).thenReturn(Datos.CUENTAS);
        List<Cuenta> cuentas = service.findAll();

        assertNotNull(cuentas);
        assertTrue(cuentas.get(0).getPersona().contains("Armando"));
        assertTrue(cuentas.get(0).getId().equals(1L));
    }

    @Test
    void findById() {
        Cuenta cuentaEsperada = new Cuenta(1L,"Armando", new BigDecimal(15000));
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Optional.ofNullable(cuentaEsperada));
        Cuenta cuenta = service.findById(1L);

        assertNotNull(cuenta);
        assertTrue(cuenta.getPersona().contains("Armando"));
        assertTrue(cuenta.getSaldo().intValue() == 15000);
    }

    @Test
    void revisarTotalTransferencias() {
        Banco banco = new Banco(1L, "BBVA", 10);
        Mockito.when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));
        Integer transferencias = service.revisarTotalTransferencias(banco.getId());

        assertEquals(transferencias, 10);
        verify(bancoRepository, times(1)).findById(1L);
    }

    @Test
    void revisarSaldo() {
        Cuenta cuentaEsperada = new Cuenta(1L,"Armando", new BigDecimal(15000));
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaEsperada));
        BigDecimal saldo = service.revisarSaldo(1L);

        assertEquals(saldo, new BigDecimal(15000));
        verify(cuentaRepository, times(1)).findById(1L);

    }

    @Test
    void transferir() {
        Cuenta origen = new Cuenta(1L,"Armando", new BigDecimal(15000));
        Cuenta destino = new Cuenta(2L,"Sebastian", new BigDecimal(10000));
        Banco banco = new Banco(1L, "BBVA", 10);
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Optional.of(origen));
        Mockito.when(cuentaRepository.findById(2L)).thenReturn(Optional.of(destino));
        Mockito.when(bancoRepository.findById(1L)).thenReturn(Optional.of(banco));

        service.transferir(1L, 2L, new BigDecimal(1234), 1L);
        assertEquals(destino.getSaldo(), new BigDecimal(11234));
        assertTrue(origen.getSaldo().intValue() == 13766);
        verify(cuentaRepository, times(1)).findById(1L);
        verify(cuentaRepository, times(1)).findById(2L);
        verify(bancoRepository, times(1)).findById(1L);


    }

    @Test
    void save() {
        Cuenta cuentaNueva = new Cuenta(2L,"Sebastian", new BigDecimal(10000));
        Mockito.when(cuentaRepository.save(cuentaNueva)).thenReturn(cuentaNueva);
        service.save(cuentaNueva);

        assertTrue(cuentaNueva.getPersona().equals("Sebastian"));
        assertTrue(cuentaNueva.getSaldo().intValue()==10000);
        verify(cuentaRepository, times(1)).save(cuentaNueva);

    }

    @Test
    void deleteById() {
        service.deleteById(1L);

        verify(cuentaRepository, times(1)).deleteById(1L);
    }

    @Test
    void update() {
        Cuenta cuentaModificada = new Cuenta(1L,"Armando", new BigDecimal(15000));
        Cuenta datosNuevos = new Cuenta(2L,"Sebastian", new BigDecimal(10000));
        Mockito.when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaModificada));

        service.update(datosNuevos, 1L);

        assertEquals(cuentaModificada.getPersona(), datosNuevos.getPersona());
        assertEquals(cuentaModificada.getId(), 1L);
        assertEquals(cuentaModificada.getPersona(), "Sebastian");
        verify(cuentaRepository, times(1)).findById(1L);


    }

    @Test
    void saveBanco() {
        Banco bancoNuevo = new Banco(1L,"Santander",2);

        Mockito.when(bancoRepository.save(bancoNuevo)).thenReturn(bancoNuevo);
        service.saveBanco(bancoNuevo);

        assertTrue(bancoNuevo.getNombre().equals("Santander"));
        assertTrue(bancoNuevo.getTotalTransferencia().intValue()==2);
        verify(bancoRepository, times(1)).save(bancoNuevo);

    }
}