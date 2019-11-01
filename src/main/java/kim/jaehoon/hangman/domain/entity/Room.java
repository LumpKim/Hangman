package kim.jaehoon.hangman.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data @Document
@AllArgsConstructor
public class Room {
    @Id
    private String id;

    private String name;

    @Field("max_player")
    private int maxPlayer;

    private List<String> participants;

    private String status;

    private String admin;

    private String word;

}
