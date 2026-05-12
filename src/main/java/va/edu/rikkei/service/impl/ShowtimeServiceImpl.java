package va.edu.rikkei.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import va.edu.rikkei.model.entity.Movie;
import va.edu.rikkei.model.entity.Room;
import va.edu.rikkei.model.entity.Showtime;
import va.edu.rikkei.repository.MovieRepository;
import va.edu.rikkei.repository.RoomRepository;
import va.edu.rikkei.repository.ShowtimeRepository;
import va.edu.rikkei.service.ShowtimeService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    @Override
    public void createShowtime(Long movieId, Long roomId, LocalDateTime startTime) throws Exception {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new Exception("Không tìm thấy phim"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new Exception("Không tìm thấy phòng"));

        // 1. Tính toán giờ kết thúc: Giờ bắt đầu + Thời lượng phim + 30 PHÚT DỌN PHÒNG
        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration()).plusMinutes(30);

        // 2. Lấy đầu ngày và cuối ngày của cái ngày định chiếu để lọc dữ liệu cho nhẹ DB
        LocalDateTime startOfDay = startTime.with(LocalTime.MIN);
        LocalDateTime endOfDay = startTime.with(LocalTime.MAX);

        // 3. Lấy các suất chiếu đã có của phòng này trong ngày hôm đó
        List<Showtime> existingShowtimes = showtimeRepository.findShowtimesByRoomAndDate(roomId, startOfDay, endOfDay);

        // 4. THUẬT TOÁN KIỂM TRA XUNG ĐỘT
        for (Showtime existing : existingShowtimes) {
            // Nếu (Bắt đầu mới < Kết thúc cũ) VÀ (Kết thúc mới > Bắt đầu cũ) => BỊ TRÙNG LỊCH!
            if (startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                throw new Exception("LỖI XUNG ĐỘT: Phòng " + room.getName() + " đang bận chiếu phim '"
                        + existing.getMovie().getTitle() + "' từ " + existing.getStartTime().toLocalTime()
                        + " đến " + existing.getEndTime().toLocalTime() + " (Đã tính giờ dọn phòng).");
            }
        }

        // 5. Nếu vượt qua được vòng lặp check lỗi trên, nghĩa là an toàn. Tiến hành lưu!
        Showtime newShowtime = new Showtime();
        newShowtime.setMovie(movie);
        newShowtime.setRoom(room);
        newShowtime.setStartTime(startTime);
        newShowtime.setEndTime(endTime);

        showtimeRepository.save(newShowtime);
    }
}