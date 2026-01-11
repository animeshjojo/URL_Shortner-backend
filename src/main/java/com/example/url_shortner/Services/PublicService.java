package com.example.url_shortner.Services;

import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Entity.UrlData;
import com.example.url_shortner.Repository.DatabaseRepository;
import com.example.url_shortner.Utility.Base62EncoderWithSecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
@Slf4j
public class PublicService {

    private DatabaseRepository databaseRepository;

    private Base62EncoderWithSecretKey base62EncoderWithSecretKey;

    public PublicService(DatabaseRepository databaseRepository, Base62EncoderWithSecretKey base62EncoderWithSecretKey) {
        this.databaseRepository = databaseRepository;
        this.base62EncoderWithSecretKey = base62EncoderWithSecretKey;
    }

    public String save(LongUrlDto longUrlDto) {
            if(!longUrlDto.getLongUrl().startsWith("http://") && !longUrlDto.getLongUrl().startsWith("https://")){
                longUrlDto.setLongUrl("https://"+longUrlDto.getLongUrl());
            }
            Optional<UrlData> data=checkifexist(longUrlDto);
            if(data.isPresent()){
                return data.get().getShortURL();
            }
            UrlData urlData=new UrlData();
            urlData.setLongURL(longUrlDto.getLongUrl());
            urlData=databaseRepository.save(urlData);
            String shortUrl=base62EncoderWithSecretKey.Encode(urlData.getId());
            urlData.setShortURL(shortUrl);
            databaseRepository.save(urlData);
            return shortUrl;
        }


    public Optional<UrlData> checkifexist(LongUrlDto longUrlDto) {
        return databaseRepository.findByLongURL(longUrlDto.getLongUrl());
    }

    public ResponseEntity<?> redirect(String shortUrl) {
        long id=base62EncoderWithSecretKey.decode(shortUrl);
        UrlData urlData=databaseRepository.findById(id).orElse(null);
        if(urlData==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid ShortUrl");
        }
        else{
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlData.getLongURL())).build();
        }
    }
}
