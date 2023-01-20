package com.minsait.TestingMicroservices.services;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    Integer revisarTotalTransferencias(Long bancoId);
    BigDecimal revisarSaldo(Long cuentaId);
    void transferir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long bancoId);
    Cuenta save(Cuenta cuenta);
    void deleteById(Long id);
    Cuenta update(Cuenta cuenta, Long id);
    Banco saveBanco(Banco banco);

}
