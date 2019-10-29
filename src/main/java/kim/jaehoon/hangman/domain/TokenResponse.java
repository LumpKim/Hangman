package kim.jaehoon.hangman.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class TokenResponse {
    private String access;
    private String refresh;
}
