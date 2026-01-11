package com.example.url_shortner.Controller;

import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Services.PublicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PublicController {

   private PublicService publicService;

   public PublicController(PublicService publicService) {
       this.publicService = publicService;
   }

   @PostMapping
   public ResponseEntity<String> saveData(@RequestBody LongUrlDto longUrlDto) {
       try{
           if(longUrlDto.getLongUrl()==null || longUrlDto.getLongUrl().isEmpty()){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LongUrl is empty");
           }
           String shorturl=publicService.save(longUrlDto);
           return ResponseEntity.status(HttpStatus.CREATED).body("http://localhost:8080/"+shorturl);
       }
       catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please Try again");
       }

   }

   @GetMapping("/{shortUrl}")
   public ResponseEntity<?> redirect(@PathVariable String shortUrl) {
       return publicService.redirect(shortUrl);
   }
}
