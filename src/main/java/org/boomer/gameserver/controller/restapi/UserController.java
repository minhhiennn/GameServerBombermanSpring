package org.boomer.gameserver.controller.restapi;


import org.boomer.gameserver.dto.request.UserDTORequest;
import org.boomer.gameserver.dto.response.UserDTOResponse;
import org.boomer.gameserver.entities.User;
import org.boomer.gameserver.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServices userServices;
    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }
    @GetMapping("/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("User API is working");
    }
    @PostMapping("/login")
    public ResponseEntity<?> getUser(@RequestBody UserDTORequest req) {
        System.out.println("Login attempt for username: " + req.getUsername());
        try {
            User user = userServices.findUserByUsernameAndPassword(req.getUsername(), req.getPassword());
            System.out.println("Login successful for user: " + user);
            return ResponseEntity.ok(new UserDTOResponse(user.getUsername()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("login failed, please check your username and password");
        }
    }
}
