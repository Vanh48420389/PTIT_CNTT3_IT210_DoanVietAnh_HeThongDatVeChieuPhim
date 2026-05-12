package va.edu.rikkei.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Suất chiếu này chiếu phim nào?
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Suất chiếu này ở phòng nào?
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDateTime startTime; // Giờ bắt đầu

    @Column(nullable = false)
    private LocalDateTime endTime; // Giờ kết thúc (Đã bao gồm thời lượng phim + dọn phòng)
}