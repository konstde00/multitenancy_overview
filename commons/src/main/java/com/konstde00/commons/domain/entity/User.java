package com.konstde00.commons.domain.entity;

import com.konstde00.commons.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq",
        sequenceName = "users_id_seq", allocationSize = 1)
    Long id;

    @Builder.Default
    @Enumerated(STRING)
    @Column(name = "role")
    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    Collection<Role> roles = new ArrayList<>();

    String email;

    String password;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    Tenant tenant;

    public User() {}
}
