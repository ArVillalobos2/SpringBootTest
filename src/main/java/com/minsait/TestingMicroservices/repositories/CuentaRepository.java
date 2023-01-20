package com.minsait.TestingMicroservices.repositories;

import com.minsait.TestingMicroservices.models.Banco;
import com.minsait.TestingMicroservices.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta,Long> {
    Optional<Cuenta> findByPersona(String persona);

    @Query("select c from Cuenta c where c.persona = ?1")//HQL
    Optional<Cuenta> buscarPorPersona(String persona);
}
