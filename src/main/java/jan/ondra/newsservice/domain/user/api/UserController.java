package jan.ondra.newsservice.domain.user.api;

import jan.ondra.newsservice.domain.user.model.User;
import jan.ondra.newsservice.domain.user.service.UserService;
import jan.ondra.newsservice.util.UserIdExtractor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserIdExtractor userIdExtractor;

    public UserController(UserService userService, UserIdExtractor userIdExtractor) {
        this.userService = userService;
        this.userIdExtractor = userIdExtractor;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void addUser(@RequestHeader(AUTHORIZATION) String bearerToken, @RequestBody UserRequest userRequest) {
        userService.addUser(new User(userIdExtractor.extractFromBearerToken(bearerToken), userRequest.email()));
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateUser(@RequestHeader(AUTHORIZATION) String bearerToken, @RequestBody UserRequest userRequest) {
        userService.updateUser(new User(userIdExtractor.extractFromBearerToken(bearerToken), userRequest.email()));
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@RequestHeader(AUTHORIZATION) String bearerToken) {
        userService.deleteUser(userIdExtractor.extractFromBearerToken(bearerToken));
    }

}
