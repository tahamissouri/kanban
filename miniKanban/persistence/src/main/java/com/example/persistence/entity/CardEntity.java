package com.example.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="card")
@NoArgsConstructor
@Setter
@Getter
@Entity
public class CardEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private int position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "column_id")
    private ColumnEntity column;

}
