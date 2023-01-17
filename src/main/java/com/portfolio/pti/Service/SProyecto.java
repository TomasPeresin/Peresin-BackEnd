
package com.portfolio.pti.Service;

import com.portfolio.pti.Entity.Proyecto;
import com.portfolio.pti.Repository.RProyecto;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SProyecto {
    @Autowired
    RProyecto rProyecto;
    
    public List<Proyecto> list(){
        return rProyecto.findAll();
    }
    
    public Optional<Proyecto> getOne(int id){
        return rProyecto.findById(id);
    }
    
    public void save(Proyecto educacion){
        rProyecto.save(educacion);
    }
    
    public void delete(int id){
        rProyecto.deleteById(id);
    }
    
    public boolean existsById(int id){
        return rProyecto.existsById(id);
    }
    
    public boolean existsByNombre(String nombre){
         return rProyecto.existsByNombre(nombre);
    }
    public Optional<Proyecto> getByNombre(String nombre){
         return rProyecto.findByNombre(nombre);
    }
}