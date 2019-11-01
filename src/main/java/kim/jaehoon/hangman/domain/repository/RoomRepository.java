package kim.jaehoon.hangman.domain.repository;

import kim.jaehoon.hangman.domain.entity.Room;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RoomRepository extends ReactiveMongoRepository<Room, ObjectId> {

    Mono<Room> findById(ObjectId id);

    Mono<Room> findByAdmin(String admin);

}
