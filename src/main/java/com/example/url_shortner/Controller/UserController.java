package com.example.url_shortner.Controller;

import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Dto.ShortUrlDto;
import com.example.url_shortner.Dto.UrlDataDto;
import com.example.url_shortner.Entity.UrlData;
import com.example.url_shortner.Entity.User;
import com.example.url_shortner.Services.PublicService;
import com.example.url_shortner.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?>  getUrlData(){
        List<UrlDataDto> data=userService.getUrlData();
        if(data.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(data, HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<?> saveData(@RequestBody LongUrlDto longUrlDto) {
        try{
            if(longUrlDto.getLongUrl()==null || longUrlDto.getLongUrl().isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LongUrl is empty");
            }
            ShortUrlDto shortUrlDto = new ShortUrlDto();
            shortUrlDto.setShortUrl("http://localhost:8080/"+userService.save(longUrlDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlDto);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please Try again");
        }
    }

}
