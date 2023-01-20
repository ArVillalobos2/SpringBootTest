package com.minsait.TestingMicroservices.repositories;

import com.minsait.TestingMicroservices.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco,Long> {

}

