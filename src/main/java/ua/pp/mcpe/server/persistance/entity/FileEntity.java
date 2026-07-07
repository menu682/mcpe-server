package ua.pp.mcpe.server.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "file")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FileEntity extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "link", nullable = false, unique = true)
    private String link;

    @ManyToOne
    private VersionEntity version;

}
