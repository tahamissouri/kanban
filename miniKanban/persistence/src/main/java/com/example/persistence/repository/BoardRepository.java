package com.example.persistence.repository;

import com.example.persistence.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity,Long> {

    List<BoardEntity> findAllByOwnerId(Long ownerId);

    @Query("""
          Select DISTINCT b from BoardEntity b left join b.members m where
          m.id= :userId or b.owner.id = :userId
          """)
    List<BoardEntity> getAllByOwnerOrUser(@Param("userId") Long userId);

}
