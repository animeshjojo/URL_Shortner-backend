package com.example.url_shortner.Controller;

import com.example.url_shortner.Dto.LongUrlDto;
import com.example.url_shortner.Dto.ShortUrlDto;
import com.example.url_shortner.Dto.UserLoginDto;
import com.example.url_shortner.Entity.User;
import com.example.url_shortner.Services.PublicService;
import com.example.url_shortner.Utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class PublicController {

   private PublicService publicService;
   private BcryptPassword4jPasswordEncoder passwordEncoder;
   private AuthenticationManager authenticationManager;
   private JwtUtil jwtUtil;

   public PublicController(PublicService publicService,AuthenticationManager authenticationManager) {
       this.publicService = publicService;
       this.passwordEncoder = new BcryptPassword4jPasswordEncoder();
       this.authenticationManager=authenticationManager;
   }

   @PostMapping
   public ResponseEntity<?> saveData(@RequestBody LongUrlDto longUrlDto) {
       try{
           if(longUrlDto.getLongUrl()==null || longUrlDto.getLongUrl().isEmpty()){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LongUrl is empty");
           }
           ShortUrlDto shortUrlDto = new ShortUrlDto();
           shortUrlDto.setShortUrl("http://localhost:8080/"+publicService.save(longUrlDto));
           return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlDto);
       }
       catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please Try again");
       }
   }

   @GetMapping("/{shortUrl}")
   public ResponseEntity<String> redirect(@PathVariable String shortUrl) {
       return publicService.redirect(shortUrl);
   }

   @GetMapping("/health-check")
   public ResponseEntity<String> healthCheck() {
       return ResponseEntity.status(HttpStatus.OK).body("OK");
   }

   @PostMapping("/signUp")
   public ResponseEntity<String> signUp(@RequestBody UserLoginDto userLoginDto) {
       try{
           if(userLoginDto.getPassword().isEmpty() || userLoginDto.getUserName().isEmpty()){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or Password is empty");
           }
           User user = new User();
           user.setUserName(userLoginDto.getUserName());
           user.setPassword(passwordEncoder.encode(userLoginDto.getPassword()));
           return ResponseEntity.status(HttpStatus.CREATED).build();
       }
       catch(Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
       }
   }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userdto){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userdto.getUserName(), userdto.getPassword()));
            String jwt = jwtUtil.generateToken(userdto.getUserName());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

}
