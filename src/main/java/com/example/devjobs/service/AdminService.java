package com.example.devjobs.service;

import com.example.devjobs.dto.response.DeleteResponse;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.model.User;
import com.example.devjobs.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Admin listando todos los usuarios");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.info("Admin buscando usuario ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado - ID: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
    }

    @Transactional
    public DeleteResponse deleteUser(Long id) {
        log.info("Admin eliminando usuario ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado al eliminar - ID: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado");
                });
        userRepository.delete(user);
        log.info("Usuario ID: {} eliminado por admin", id);
        return DeleteResponse.builder().message("Usuario eliminado exitosamente.").build();
    }
}
