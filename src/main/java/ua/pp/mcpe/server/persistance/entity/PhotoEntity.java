package ua.pp.mcpe.server.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "photo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PhotoEntity extends BaseEntity{

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "link", unique = true, nullable = false)
    private String link;

}
