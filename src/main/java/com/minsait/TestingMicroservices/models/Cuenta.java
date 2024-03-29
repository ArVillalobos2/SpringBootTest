package com.minsait.TestingMicroservices.models;

import com.minsait.TestingMicroservices.exceptions.DineroInsuficienteException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cuentas")

public class Cuenta implements Serializable {
    static private final long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String persona;
    private BigDecimal saldo;

    public void retirar(BigDecimal monto){
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO)<0)
            throw new DineroInsuficienteException("Dinero Insuficiente");
        this.saldo= nuevoSaldo;

    }

    public void depositor(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }
}