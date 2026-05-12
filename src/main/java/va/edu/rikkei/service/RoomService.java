package va.edu.rikkei.service;

import va.edu.rikkei.model.entity.Room;
import java.util.List;

public interface RoomService {
    // Hàm lấy danh sách tất cả các phòng chiếu
    List<Room> getAllRooms();
}