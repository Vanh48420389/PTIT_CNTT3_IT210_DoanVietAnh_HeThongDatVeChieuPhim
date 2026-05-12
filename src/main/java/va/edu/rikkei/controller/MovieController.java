package va.edu.rikkei.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import va.edu.rikkei.model.entity.Movie;
import va.edu.rikkei.service.CategoryService;
import va.edu.rikkei.service.MovieService;

@Controller
@RequiredArgsConstructor
public class MovieController {

    // Tiêm các service cần thiết để lấy dữ liệu từ Database
    private final MovieService movieService;
    private final CategoryService categoryService;

    // ================= DÀNH CHO KHÁCH HÀNG =================

    // Trang chủ hiển thị danh sách phim
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "index";
    }

    // ================= DÀNH CHO ADMIN: QUẢN LÝ PHIM (CORE-04) =================

    // 1. XEM DANH SÁCH PHIM (Read)
    @GetMapping("/admin/movies")
    public String listMoviesForAdmin(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "movie-list";
    }

    // 2. MỞ FORM THÊM PHIM MỚI (Create)
    @GetMapping("/admin/movies/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("movie", new Movie());

        // Truyền danh sách thể loại từ DB (Seed Data) lên form
        model.addAttribute("categories", categoryService.getAllCategories());

        return "add-movie";
    }

    // XỬ LÝ LƯU PHIM MỚI VÀO DB
    @PostMapping("/admin/movies/add")
    public String saveMovie(@ModelAttribute("movie") Movie movie) {
        movieService.saveMovie(movie);
        return "redirect:/admin/movies"; // Lưu xong đá về trang danh sách
    }

    // 3. MỞ FORM SỬA PHIM (Update)
    @GetMapping("/admin/movies/edit/{id}")
    public String showEditMovieForm(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);

            // Tương tự, truyền danh sách thể loại lên để Admin có thể chọn lại
            model.addAttribute("categories", categoryService.getAllCategories());

            return "edit-movie";
        }
        return "redirect:/admin/movies";
    }

    // XỬ LÝ CẬP NHẬT PHIM VÀO DB
    @PostMapping("/admin/movies/edit")
    public String updateMovie(@ModelAttribute("movie") Movie movie) {
        movieService.updateMovie(movie);
        return "redirect:/admin/movies";
    }

    // 4. XÓA PHIM (Delete)
    @GetMapping("/admin/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies";
    }
}