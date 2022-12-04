package com.konstde00.lab.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "researches")
@FieldDefaults(level = PRIVATE)
public class Research {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "researches_id_seq")
    @SequenceGenerator(name = "researches_id_seq",
            sequenceName = "researches_id_seq", allocationSize = 1)
    Long id;

    String name;

    String description;
}
