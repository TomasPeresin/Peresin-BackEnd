package com.portfolio.pti.Config;

import com.portfolio.pti.Security.Entity.Rol;
import com.portfolio.pti.Security.Enums.RolNombre;
import com.portfolio.pti.Security.Repository.iRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RolDataInitializer implements CommandLineRunner {

    @Autowired
    private iRolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!rolRepository.findByRolNombre(RolNombre.ROLE_USER).isPresent()) {
            rolRepository.save(new Rol(RolNombre.ROLE_USER));
        }
        if (!rolRepository.findByRolNombre(RolNombre.ROLE_ADMIN).isPresent()) {
            rolRepository.save(new Rol(RolNombre.ROLE_ADMIN));
        }
    }
}
