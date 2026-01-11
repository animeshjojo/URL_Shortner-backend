package com.example.url_shortner.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="url_data")
@Data
public class UrlData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String shortURL;

    @Column(unique = true, nullable = false)
    private String longURL;
}
