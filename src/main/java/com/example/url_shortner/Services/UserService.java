package com.example.url_shortner.Services;


import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Dto.ShortUrlDto;
import com.example.url_shortner.Dto.UrlDataDto;
import com.example.url_shortner.Dto.UserLoginDto;
import com.example.url_shortner.Entity.UrlData;
import com.example.url_shortner.Entity.User;
import com.example.url_shortner.Repository.UrlRepository;
import com.example.url_shortner.Repository.UserRepositry;
import com.example.url_shortner.Utility.Base62EncoderWithSecretKey;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepositry userRepositry;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private PublicService publicService;
    private UrlRepository urlRepository;
    private Base62EncoderWithSecretKey base62Encoder;
    public UserService(UserRepositry userRepositry,PublicService publicService,UrlRepository urlRepository,Base62EncoderWithSecretKey base62Encoder) {

        this.userRepositry = userRepositry;
        this.publicService= publicService;
        this.urlRepository= urlRepository;
        this.base62Encoder = base62Encoder;

    }

    public List<User> getAllUsers() {
        return userRepositry.findAll();
    }

    public List<UrlData> getUrlDataByUser(String username) {
        return userRepositry.findUserByUserName(username).getUrls();
    }

    public void saveUser(UserLoginDto userLoginDto) {
        User user = new User();
        user.setUserName(userLoginDto.getUserName());
        user.setPassword(passwordEncoder.encode(userLoginDto.getPassword()));
        userRepositry.save(user);
    }
    public String save(LongUrlDto longUrlDto) {
        if(!longUrlDto.getLongUrl().startsWith("http://") && !longUrlDto.getLongUrl().startsWith("https://")){
            longUrlDto.setLongUrl("https://"+longUrlDto.getLongUrl());
        }
        User user=userRepositry.findUserByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<UrlData> data=checkifexist(longUrlDto,user);
        if(data.isPresent()){
            return data.get().getShortURL();
        }
        UrlData urlData=new UrlData();
        urlData.setLongURL(longUrlDto.getLongUrl());
        urlData= urlRepository.save(urlData);
        String shortUrl=base62Encoder.encode(urlData.getId());
        urlData.setShortURL(shortUrl);
        urlData.setUser(user);
        urlRepository.save(urlData);
        return shortUrl;
    }

    public List<UrlDataDto> getUrlData(){
        String userName=SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepositry.findUserByUserName(userName).getUrls().stream().map(
              urlData -> {
                  UrlDataDto urlDataDto=new UrlDataDto();
                  urlDataDto.setShortURL("http://localhost:8080/"+urlData.getShortURL());
                  urlDataDto.setLongURL(urlData.getLongURL());
                  return urlDataDto;
              }
        ).toList();
    }

    public Optional<UrlData> checkifexist(LongUrlDto longUrlDto,User user) {

        UrlData urlData=urlRepository.findByLongURL(longUrlDto.getLongUrl()).orElse(null);
        urlData.setUser(user);
        return Optional.ofNullable(urlData);
    }

}
