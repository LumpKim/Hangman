package kim.jaehoon.hangman.service.room;

import kim.jaehoon.hangman.domain.repository.RoomRepository;
import kim.jaehoon.hangman.exception.RoomNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Mono findById(ObjectId id) {
        return roomRepository.findById(id)
                .switchIfEmpty(Mono.error(RoomNotFoundException::new));
    }

    @Override
    public Flux findAll() {
        return roomRepository.findAll();
    }
}
