package com.zooting.api.domain.dm.entity;

import com.zooting.api.domain.file.entity.File;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DM {
    @Id
    @Column(name = "dm_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    @JoinColumn(name = "dm_id")
    private List<File> files;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private DMRoom dmRoom;
    private String message;
    private Boolean status;

    @Builder
    public DM(List<File> files, DMRoom dmRoom, String message, Boolean status) {
        this.files = Objects.nonNull(files) ? files : new ArrayList<>();
        this.dmRoom = dmRoom;
        this.message = message;
        this.status = status;
    }
}
