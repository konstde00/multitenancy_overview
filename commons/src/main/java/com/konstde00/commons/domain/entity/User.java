package com.konstde00.commons.domain.entity;

import com.konstde00.commons.domain.enums.Role;
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
@Table(name = "users")
@FieldDefaults(level = PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq",
        sequenceName = "users_id_seq", allocationSize = 1)
    Long id;

    @Enumerated(EnumType.STRING)
    Role role;

    String name;

    String password;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    Tenant tenant;
}
