package kim.jaehoon.hangman.domain.repository;

import kim.jaehoon.hangman.domain.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {

    Mono<User> findById(String id);

}
