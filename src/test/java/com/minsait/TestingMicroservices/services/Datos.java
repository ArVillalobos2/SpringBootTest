package com.minsait.TestingMicroservices.services;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Datos {

    public static final List<Cuenta> CUENTAS= Arrays.asList(
            new Cuenta(1L, "Armando", new BigDecimal(15000)),
            new Cuenta(2L, "Sebastian", new BigDecimal(10000)),
            new Cuenta(3L, "Ricardo", new BigDecimal(20000))
    );


    public static Optional<Banco> crearBanco(){
        return Optional.of(new Banco(1L,"BBVA", 0));
    }
}
