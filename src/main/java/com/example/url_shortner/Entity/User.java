package com.example.url_shortner.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="userData")
public class User {

    @Id
    private long id;

    private String userName;

    private String password;

    @OneToMany
    private List<UrlData> urls;

}
