package kim.jaehoon.hangman.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
@AllArgsConstructor
public class Index {
    private ArrayList<Integer> index;
}
