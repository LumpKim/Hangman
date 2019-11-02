package kim.jaehoon.hangman.service.room;

import kim.jaehoon.hangman.domain.entity.Room;
import kim.jaehoon.hangman.domain.entity.User;
import kim.jaehoon.hangman.domain.entity.WinningRate;
import kim.jaehoon.hangman.domain.payload.Index;
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
                    } else if (room.getParticipants().size() + 1 > room.getMaxPlayer()) {
                        return Mono.error(new FullRoomException());
                    } else {
                        List<String> participants = room.getParticipants();
                        if (participants.contains(userId))
                            return Mono.error(UserAlreadyExistsException::new);
                        participants.add(userId);
                        room.setParticipants(participants);
                        Mono<User> userMono = userRepository.findById(userId)
                                .flatMap(user -> {
                                    user.setRoomId(roomId.toString());
                                    return userRepository.save(user);
                                });
                        return roomRepository.save(room).and(userMono);
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

    @Override
    public Flux setAnswer(ObjectId roomId, String adminId, String answer) {

        return userRepository.findAllByReadyAndRoomId(false, roomId.toString())
            .flatMap(value -> Mono.error(new UnreadyUserDetectedException()))
            .switchIfEmpty(roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(RoomNotFoundException::new))
                .flatMap(room -> roomRepository.findByIdAndAdmin(roomId.toString(), adminId)
                    .switchIfEmpty(Mono.error(AdminMismatchedException::new))
                    .flatMap(room1 -> {
                        if (room.getStatus().equals("on_game"))
                            return Mono.error(GameAlreadyStartedException::new);

                        room.setWord(answer);
                        room.setStatus("on_game");
                        return roomRepository.save(room);
                    })));
    }

    @Override
    public Mono submitAnswer(ObjectId roomId, String userId, String answer) {
        return roomRepository.findById(roomId)
                .flatMap(room -> {
                    Mono<User> defaultUser = userRepository.findById(userId)
                            .switchIfEmpty(Mono.error(UserNotFoundException::new));

                    if (room.getWord().contains(answer)) {
                        Index index = new Index(corrects(room.getWord(), answer));
                        String cutAnswer = room.getWord().replace(answer, "_");
                        room.setWord(cutAnswer);
                        if (cutAnswer.replace("_", "").length() == 0) {
                            List<String> users = room.getParticipants();
                            users.remove(userId);
                            Mono defeatIncrement = Mono.fromRunnable(() -> {
                                for (String user : users) {
                                    userRepository.findById(user)
                                            .flatMap(tempUser -> {
                                                WinningRate rate = tempUser.getWinningRate();
                                                rate.incrementDefeatCount();
                                                tempUser.setWinningRate(rate);
                                                return userRepository.save(tempUser);
                                            });
                                }
                            });
                            Mono userMono = defaultUser.flatMap(savedUser -> {
                                int level = savedUser.getLevel();
                                savedUser.setLevel(++level);
                                WinningRate rate = savedUser.getWinningRate();
                                rate.incrementWinCount();
                                rate.incrementWinCount();
                                savedUser.setWinningRate(rate);
                                return userRepository.save(savedUser)
                                        .flatMap(saveUser -> Mono.error(CorrectAnswerException::new));
                            });
                            Mono roomMono = roomRepository.delete(room);
                            return Mono.when(defeatIncrement, userMono, roomMono).then();
                        } else return roomRepository.save(room).flatMap(roomed -> Mono.just(index));
                    } else {
                        return Mono.error(InvalidAnswerException::new);
                    }
                }).switchIfEmpty(Mono.error(RoomNotFoundException::new));
    }

    private ArrayList<Integer> corrects(String word, String compareTo) {
        int index = 0;
        ArrayList<Integer> indexes = new ArrayList<>();
        while (word.length() >= index) {
            int result = word.indexOf(compareTo.toCharArray()[0], index++);
            if (result == -1) {
                break;
            } else {
                indexes.add(result);
                index = result + 1;
            }
        }
        return indexes;
    }
}
