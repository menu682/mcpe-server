package ua.pp.mcpe.server.persistance.entity.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.pp.mcpe.server.persistance.ERole;
import ua.pp.mcpe.server.persistance.entity.BaseEntity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleEntity extends BaseEntity {

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private ERole name;

}
