package va.edu.rikkei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import va.edu.rikkei.model.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}