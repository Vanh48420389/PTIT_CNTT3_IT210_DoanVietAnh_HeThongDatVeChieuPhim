package va.edu.rikkei.service;

import va.edu.rikkei.model.entity.Movie;
import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie saveMovie(Movie movie);

    // THÊM 3 HÀM NÀY CHO CRU-D:
    Movie getMovieById(Long id); // Lấy phim ra để Sửa
    Movie updateMovie(Movie movie); // Lưu cập nhật
    void deleteMovie(Long id); // Xóa
}