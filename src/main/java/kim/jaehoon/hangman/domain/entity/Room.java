package kim.jaehoon.hangman.domain.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import reactor.core.publisher.Flux;

@Data
@Document
public class Room {
    @Id
    private ObjectId id;

    private String name;

    @Field("max_player")
    private int maxPlayer;

    private Flux<User> participants;

    private String answer;

}
