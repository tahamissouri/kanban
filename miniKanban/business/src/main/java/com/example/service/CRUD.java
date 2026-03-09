package com.example.service;

import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.mappers.MapperContract;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public class CRUD<R extends JpaRepository<E,Long>,
        M extends MapperContract<E,DtoRequest,DtoResponse>
        ,E,DtoRequest,DtoResponse> {

    protected final R repository;
    protected final M mapper;


    public CRUD(R repository, M mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public E findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new AppException(ExceptionMessages.ENTITY_NOT_FOUND,"Entity",id));
    }



    public DtoResponse create(DtoRequest dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    public void delete(Long id) {
        if (!existsById(id)) throw new AppException(ExceptionMessages.ENTITY_NOT_FOUND,"Entity",id);
        repository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public DtoResponse save(E entity) {
        return mapper.toDto(repository.save(entity));
    }




}
