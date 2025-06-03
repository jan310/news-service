package jan.ondra.newsservice.core.user.persistence;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDTORowMapper implements RowMapper<UserDTO> {

    @Override
    @NonNull
    public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserDTO(
            rs.getString("id"),
            rs.getString("email")
        );
    }

}
