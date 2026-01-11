package com.example.url_shortner.Repository;

import com.example.url_shortner.Entity.UrlData;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface DatabaseRepository extends JpaRepository<UrlData,Long> {
    Optional<UrlData> findByLongURL(String longURL);
}
