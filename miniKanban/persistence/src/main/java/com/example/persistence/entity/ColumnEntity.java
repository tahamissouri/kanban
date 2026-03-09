package com.example.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="columnn")
@Setter
@Getter
@NoArgsConstructor
@Entity
public class ColumnEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="name",nullable=false)
    private String name;

    @Column(nullable=false)
    private int position;

    @ManyToOne(optional = false)
    @JoinColumn(name="board_id")
    private BoardEntity board;


}
