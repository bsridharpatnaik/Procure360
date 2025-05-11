package com.gb.p360.models;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String method;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @Column(columnDefinition = "TEXT")
    private String body;

    private Date timestamp;
}