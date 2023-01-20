package com.minsait.TestingMicroservices.controllers;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
   /* INSERT INTO cuentas (persona, saldo) VALUES ('Ricardo', 1000);
    INSERT INTO cuentas (persona, saldo) VALUES ('Sebastian', 50000);
    INSERT INTO bancos (nombre, total_transferencias) VALUES ('BBVA',0);*/

    public static Optional<Cuenta> crearCuenta(){
        return Optional.of(new Cuenta(1L,"Ricardo", new BigDecimal(1000)));
    }

    public static Optional<Cuenta> crearCuenta2(){
        return Optional.of(new Cuenta(2L,"Sebastian", new BigDecimal(50000)));
    }

    public static Optional<Banco> crearBanco(){
        return Optional.of(new Banco(1L,"BBVA", 0));
    }
}
