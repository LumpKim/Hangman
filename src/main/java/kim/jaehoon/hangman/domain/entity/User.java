package kim.jaehoon.hangman.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data @Document
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @JsonIgnore
    private String password;

    private int level;

    private boolean ready;

    @Field("winning_rate")
    private WinningRate winningRate;

    private String roomId;

}
