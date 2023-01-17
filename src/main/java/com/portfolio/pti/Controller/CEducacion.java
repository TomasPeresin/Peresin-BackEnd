/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portfolio.pti.Controller;

import com.portfolio.pti.Dto.dtoEducacion;
import com.portfolio.pti.Entity.Educacion;
import com.portfolio.pti.Security.Controller.Mensaje;
import com.portfolio.pti.Service.Seducacion;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/educacion")
@CrossOrigin(origins = {"https://frontendpti.web.app", "http://localhost:4200"})
public class CEducacion {
    @Autowired
    Seducacion sEducacion;
    
    @GetMapping("/lista")
    public ResponseEntity<List<Educacion>> list(){
        List<Educacion> list = sEducacion.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<Educacion> getById(@PathVariable("id")int id){
        if(!sEducacion.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.BAD_REQUEST);
        }
        
        Educacion educacion = sEducacion.getOne(id).get();
        return new ResponseEntity(educacion, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        if(!sEducacion.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.NOT_FOUND);
        }
        sEducacion.delete(id);
        return new ResponseEntity(new Mensaje("Educacion eliminada"), HttpStatus.OK);
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody dtoEducacion dtoeducacion){
        if(StringUtils.isBlank(dtoeducacion.getInstitucionE())){
            return new ResponseEntity(new Mensaje("La institucion es obligatoria"), HttpStatus.BAD_REQUEST);
        }
        
        Educacion educacion = new Educacion(
                dtoeducacion.getInstitucionE(), dtoeducacion.getTituloE(), dtoeducacion.getEstadoE(), 
                dtoeducacion.getFechaInicio(), dtoeducacion.getFechaFin()
            );
        sEducacion.save(educacion);
        return new ResponseEntity(new Mensaje("Educacion creada"), HttpStatus.OK);
                
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody dtoEducacion dtoeducacion){
        if(!sEducacion.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el ID"), HttpStatus.NOT_FOUND);
        }
        if(StringUtils.isBlank(dtoeducacion.getInstitucionE())){
            return new ResponseEntity(new Mensaje("El campo Institucion no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(dtoeducacion.getEstadoE())){
            return new ResponseEntity(new Mensaje("El campo Estado no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(dtoeducacion.getFechaInicio().toString())){
            return new ResponseEntity(new Mensaje("El campo Fecha inicio no puede estar vacio"), HttpStatus.BAD_REQUEST);
        }
        
        Educacion educacion = sEducacion.getOne(id).get();
        
        educacion.setInstitucionE(dtoeducacion.getInstitucionE());
        educacion.setTituloE(dtoeducacion.getTituloE());
        educacion.setEstadoE(dtoeducacion.getEstadoE());
        educacion.setFechaInicio(dtoeducacion.getFechaInicio());
        educacion.setFechaFin(dtoeducacion.getFechaFin());
        
        sEducacion.save(educacion);
        
        return new ResponseEntity(new Mensaje("Educacion actualizada"), HttpStatus.OK);
    }
}
