package ua.pp.mcpe.server.persistance.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "mod")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ModEntity extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "views")
    private Integer views;

    @Column(name = "downloads")
    private Integer downloads;

    @ManyToOne
    private CategoryEntity category;

    @Column(name = "photos")
    @OneToMany(fetch = FetchType.EAGER)
    private Set<PhotoEntity> photos;

    @Column(name = "files")
    @OneToMany(fetch = FetchType.EAGER)
    private Set<FileEntity> files;

}
