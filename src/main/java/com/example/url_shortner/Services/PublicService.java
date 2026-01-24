package com.example.url_shortner.Services;

import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Entity.UrlData;
import com.example.url_shortner.Repository.UrlRepository;
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

    private UrlRepository urlRepository;

    private Base62EncoderWithSecretKey base62EncoderWithSecretKey;

    public PublicService(UrlRepository urlRepository, Base62EncoderWithSecretKey base62EncoderWithSecretKey) {
        this.urlRepository = urlRepository;
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
            urlData= urlRepository.save(urlData);
            String shortUrl=base62EncoderWithSecretKey.encode(urlData.getId());
            urlData.setShortURL(shortUrl);
            urlRepository.save(urlData);
            return shortUrl;
        }


    public Optional<UrlData> checkifexist(LongUrlDto longUrlDto) {
        return urlRepository.findByLongURL(longUrlDto.getLongUrl());
    }

    public ResponseEntity<String> redirect(String shortUrl) {
        long id=base62EncoderWithSecretKey.decode(shortUrl);
        UrlData urlData= urlRepository.findById(id).orElse(null);
        if(urlData==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ShortUrl");
        }
        else{
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlData.getLongURL())).build();
        }
    }
}
