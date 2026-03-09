package com.example.persistence.repository;

import com.example.persistence.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<CardEntity,Long> {

    List<CardEntity> getAllByColumnId(Long columnId);
}
