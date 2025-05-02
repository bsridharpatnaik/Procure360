package com.gb.p360.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileUuid;

    private String filename;

    @Lob
    @Column(name = "data")
    private byte[] data;

    Date uploadDate;
}
