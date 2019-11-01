package kim.jaehoon.hangman.service.room;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface RoomService {

    Mono findById(ObjectId id);

    Flux findAll();

    Mono save(String adminId, String name, int maxPlayer);

    Mono setParticipant(ObjectId roomId, String adminId);

    Mono removeParticipant(ObjectId roomId, String userId);

    Mono setReady(ObjectId roomId, String userId);
}
