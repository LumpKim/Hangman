package kim.jaehoon.hangman.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;

@Data @Document
@AllArgsConstructor
public class User {

    @Id
    ObjectId id;

    @Email
    private String email;

    @JsonIgnore
    private String password;

    private String nickname;

}
