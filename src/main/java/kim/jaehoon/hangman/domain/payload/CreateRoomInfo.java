package kim.jaehoon.hangman.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class CreateRoomInfo {

    private String name;
    private int maxPlayer;

}
