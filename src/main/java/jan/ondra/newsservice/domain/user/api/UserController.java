package jan.ondra.newsservice.domain.user.api;

import jakarta.validation.Valid;
import jan.ondra.newsservice.domain.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void addUser(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UserDTO userDTO) {
        userService.addUser(userDTO.toUser(jwt.getSubject()));
    }

    @GetMapping
    @ResponseStatus(OK)
    public UserDTO getUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUser(jwt.getSubject()).toUserDTO();
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UserDTO userDTO) {
        userService.updateUser(userDTO.toUser(jwt.getSubject()));
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(jwt.getSubject());
    }

}
