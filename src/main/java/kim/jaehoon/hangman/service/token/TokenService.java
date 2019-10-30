package kim.jaehoon.hangman.service.token;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {

    String getIdentity(String jwt);

    String createAccessToken(String identity);

    String createRefreshToken(String identity);

    String _generateToken(String identity, Long expSeconds);

}
