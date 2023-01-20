package com.minsait.TestingMicroservices;
import com.minsait.TestingMicroservices.models.Cuenta;
import com.minsait.TestingMicroservices.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegrationTest {

    @Autowired
    CuentaRepository repository;

    @Test
    void testFindById(){
        Optional<Cuenta> cuenta = repository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Ricardo", cuenta.get().getPersona());
    }

    @Test
    void testFindByPersona(){
        Optional<Cuenta> cuenta = repository.findByPersona("Ricardo");
        assertTrue(cuenta.isPresent());
        assertEquals(1L, cuenta.get().getId());
    }

    @Test
    void testBuscarPorPersona(){
        Optional<Cuenta> cuenta = repository.buscarPorPersona("Ricardo");
        assertTrue(cuenta.isPresent());
        assertEquals(1L, cuenta.get().getId());
    }

    @Test
    void testFindPersonaException(){
        Optional<Cuenta> cuenta = repository.buscarPorPersona("Armando");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testSave(){
        Cuenta cuentaArmando = new Cuenta(null, "Armando", new BigDecimal(100000));
        Cuenta cuenta =  repository.save(cuentaArmando);

        assertEquals("Armando", cuenta.getPersona());
        assertEquals(100000, cuenta.getSaldo().intValue());
        assertEquals(3, cuenta.getId());
    }

    @Test
    void testDelete(){
        Cuenta cuenta =  repository.findById(2L).orElseThrow();
        assertEquals("Sebastian", cuenta.getPersona());

        repository.delete(cuenta);

        assertNotEquals(cuenta, repository.findById(2L));


        assertThrows(NoSuchElementException.class, () -> repository.findById(2L).orElseThrow());
    }

    @Test
    void testUpdate(){
        Cuenta cuentaArmando = new Cuenta(null, "Jose Luis Armando", new BigDecimal(100000));
        Cuenta cuenta = repository.save(cuentaArmando);

        assertEquals("Jose Luis Armando", cuenta.getPersona());
        assertEquals(100000, cuenta.getSaldo().intValue());
        //assertEquals(4, cuenta.getId());

        cuenta.setPersona("Luis Armando");
        cuenta.setSaldo(new BigDecimal(200000));
        Cuenta cuentaActualizada = repository.save(cuenta);

        assertEquals("Luis Armando", cuenta.getPersona());
        assertEquals(200000, cuenta.getSaldo().intValue());
        //assertEquals(4, cuenta.getId());
    }

    @Test
    void testFindAll(){
        Optional<Cuenta> cuenta = repository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Ricardo", cuenta.get().getPersona());
    }

}
