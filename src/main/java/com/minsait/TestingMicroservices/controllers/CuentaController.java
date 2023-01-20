package com.minsait.TestingMicroservices.controllers;

import com.minsait.TestingMicroservices.exceptions.DineroInsuficienteException;
import com.minsait.TestingMicroservices.models.Cuenta;
import com.minsait.TestingMicroservices.models.TransferirDTO;
import com.minsait.TestingMicroservices.services.CuentaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
//localhost:8080/api/cuentas
@RequestMapping("/api/cuentas")
@Slf4j
public class CuentaController {
    @Autowired
    private CuentaService service;

    @GetMapping("/listar")
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> findAll(){
        log.info("Corriendo el metodo listar");
        return service.findAll();
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Cuenta> findById(@PathVariable Long id){

        try{
            return ResponseEntity.ok(service.findById(id));
        }catch(NoSuchElementException e){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/guardar")
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return service.save(cuenta);

    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransferirDTO dto){
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("transaccion", dto);

        try {
            service.transferir(dto.getCuentaOrigenId(), dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());
            response.put("status","OK");
            response.put("message","Transferencia realizada con éxito");

        }
        catch(DineroInsuficienteException exception){
            response.put("status","OK");
            response.put("message", exception.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());

        try{
            service.deleteById(id);
            response.put("status","Ok");
            response.put("message","La cuenta se elimino correctamente");
            return ResponseEntity.ok(response);

        }catch(NoSuchElementException exception){
            response.put("status","No se encontró el id");
            response.put("message", exception.getMessage());
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }



    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Cuenta cuenta) {
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());

        try{
            service.update(cuenta, id);
            response.put("status","Ok");
            response.put("message","La cuenta se modificó correctamente");
            return ResponseEntity.ok(response);
        }catch (NoSuchElementException exception){
            response.put("status","No se encontró el id");
            response.put("message", exception.getMessage());
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }



    }

    @GetMapping("/saldo/{cuentaId}")
    public ResponseEntity<?> revisarSaldo(@PathVariable Long cuentaId){
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        try{
            response.put("Saldo",service.revisarSaldo(cuentaId));
            response.put("status","Ok");
            return ResponseEntity.ok(response);
        }catch(NoSuchElementException exception){
            response.put("status","No se encontró el id");
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/transferencias/{bancoId}")
    public ResponseEntity<?> revisarTotalTransferencias(@PathVariable Long bancoId){
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        try{
            response.put("Transferencias",service.revisarTotalTransferencias(bancoId));
            response.put("status","Ok");
            return ResponseEntity.ok(response);
        }catch(NoSuchElementException exception){
            response.put("status","No se encontró el id del banco");
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }


    }

    //STATUS CUANDO NO SE REALIZA LA TRANSFERENCIA
    //Métodos para borrar y actualizar una cuenta
    //Métodos para revisar el saldo y total de transferencias
}
