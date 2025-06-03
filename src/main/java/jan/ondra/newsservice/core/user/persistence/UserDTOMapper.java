package jan.ondra.newsservice.core.user.persistence;

import jan.ondra.newsservice.core.user.model.User;

public class UserDTOMapper {

    public static User getDomainModel(UserDTO userDTO) {
        return new User(
            userDTO.id(),
            userDTO.email()
        );
    }

}
