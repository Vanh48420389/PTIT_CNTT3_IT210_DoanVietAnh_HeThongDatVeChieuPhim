package va.edu.rikkei.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private int duration;

    private LocalDate releaseDate;

    private String posterUrl;

    private int status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}