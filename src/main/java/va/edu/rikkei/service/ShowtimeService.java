package va.edu.rikkei.service;

import va.edu.rikkei.model.entity.Showtime;
import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeService {
    List<Showtime> getAllShowtimes();
    // Hàm này sẽ ném ra lỗi nếu phát hiện xung đột
    void createShowtime(Long movieId, Long roomId, LocalDateTime startTime) throws Exception;
    // Lấy suất chiếu của phim X, thời gian > hiện tại, sắp xếp tăng dần
}