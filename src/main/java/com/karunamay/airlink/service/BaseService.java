package com.karunamay.airlink.service;

import com.karunamay.airlink.exceptions.ResourceNotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

    public <T, R extends CrudRepository<T, Long>> T findByIdOrThrow(Long id, R repository) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Object not found with id " + id));
    }
}
