package com.minsait.TestingMicroservices.services;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;
import com.minsait.TestingMicroservices.repositories.BancoRepository;
import com.minsait.TestingMicroservices.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CuentaServiceImpl implements CuentaService{
    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private BancoRepository bancoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long cuentaId) {
        return cuentaRepository.findById(cuentaId).orElseThrow().getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long cuentaOrigen, Long cuentaDestino, BigDecimal monto, Long bancoId) {
        Cuenta origen = cuentaRepository.findById(cuentaOrigen).orElseThrow();
        origen.retirar(monto);
        cuentaRepository.save(origen);

        Cuenta destino = cuentaRepository.findById(cuentaDestino).orElseThrow();
        destino.depositor(monto);
        cuentaRepository.save(destino);

        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        int totalTransferencias = banco.getTotalTransferencia();
        banco.setTotalTransferencia(++totalTransferencias);
        bancoRepository.save(banco);
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);

    }

    @Override
    public Cuenta update(Cuenta cuenta, Long id) {
        Cuenta cuenta2 = cuentaRepository.findById(id).orElseThrow(NoSuchElementException::new);

        String persona = (cuenta.getPersona() != null) ? cuenta.getPersona() : cuenta2.getPersona();
        BigDecimal saldo = (cuenta.getSaldo() != null) ? cuenta.getSaldo() : cuenta2.getSaldo();
        cuenta2.setPersona(persona);
        cuenta2.setSaldo(saldo);

        return cuentaRepository.save(cuenta2);

    }

    @Override
    public Banco saveBanco(Banco banco) {
        return bancoRepository.save(banco);
    }

}
