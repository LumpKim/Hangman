package kim.jaehoon.hangman.service.room;

import kim.jaehoon.hangman.domain.entity.Room;
import kim.jaehoon.hangman.domain.repository.RoomRepository;
import kim.jaehoon.hangman.domain.repository.UserRepository;
import kim.jaehoon.hangman.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private UserRepository userRepository;

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

    @Override
    public Mono save(String adminId, String name, int maxPlayer) {

        ArrayList<String> participants = new ArrayList<>();
        participants.add(adminId);

        Room room = new Room(new ObjectId().toString(), name, maxPlayer, participants, "on_board", adminId, "");

        return roomRepository.findByAdmin(adminId)
                .handle((dbAdmin, sink) -> {
                    sink.error(new AdminConflictException());
                }).switchIfEmpty(roomRepository.save(room).flatMap(room1 -> Mono.just(room)));
    }

    @Override
    public Mono setParticipant(ObjectId roomId, String userId) {
        return roomRepository.findById(roomId)
                .flatMap(room -> {
                    if (room.getAdmin().equals(userId)) {
                        return Mono.error(new AdminConflictException());
                    } else if (room.getParticipants().size() + 1 > room.getMaxPlayer() || room.getParticipants().contains(userId)) {
                        return Mono.error(new FullRoomException());
                    } else {
                        List<String> participants = room.getParticipants();
                        participants.add(userId);
                        room.setParticipants(participants);
                        return roomRepository.save(room);
                    }
                }).switchIfEmpty(Mono.error(new RoomNotFoundException()));
    }

    @Override
    public Mono removeParticipant(ObjectId roomId, String userId) {
        return roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new RoomNotFoundException()))
                .flatMap(room -> {
                    if (room.getAdmin().equals(userId)) {
                        return roomRepository.delete(room);
                    } else {
                        List<String> participants = room.getParticipants();
                        participants.remove(userId);
                        room.setParticipants(participants);
                        return roomRepository.save(room);
                    }
                });
    }

    @Override
    public Mono setReady(ObjectId roomId, String userId) {
        Mono room = roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new RoomNotFoundException()));
        Mono user = userRepository.findById(userId)
                .flatMap(dbUser -> {
                    boolean ready = dbUser.isReady();
                    dbUser.setReady(!ready);
                    return userRepository.save(dbUser);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException()));

        return Mono.when(room, user);
    }
}
