package va.edu.rikkei.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import va.edu.rikkei.model.entity.Room;
import va.edu.rikkei.repository.RoomRepository;
import va.edu.rikkei.service.RoomService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}