package va.edu.rikkei.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import va.edu.rikkei.model.entity.Movie;
import va.edu.rikkei.repository.MovieRepository;
import va.edu.rikkei.service.CategoryService;
import va.edu.rikkei.service.MovieService;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final CategoryService categoryService;
    // Tiêm thêm Repository để hỗ trợ lấy dữ liệu Phân trang nhanh chóng
    private final MovieRepository movieRepository;

    // ================= DÀNH CHO KHÁCH HÀNG =================

    // Trang chủ hiển thị danh sách phim
    @GetMapping("/")
    public String homePage(Model model) {
        // Dùng PageRequest để lấy đúng trang đầu tiên (số 0) và cắt đúng 8 bộ phim
        Pageable top8 = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("movies", movieRepository.findAll(top8).getContent());

        return "index";
    }

    // ================= DÀNH CHO ADMIN: QUẢN LÝ PHIM (CORE-04) =================

    // 1. XEM DANH SÁCH PHIM
    @GetMapping("/admin/movies")
    public String listMoviesForAdmin() {
        // Vì ta đã gộp Bảng danh sách và Form vào 1 trang add-movie, nên ta redirect luôn sang đó
        return "redirect:/admin/movies/add";
    }

    // 2. MỞ FORM THÊM PHIM MỚI & BẢNG DANH SÁCH (CÓ PHÂN TRANG)
    @GetMapping("/admin/movies/add")
    public String showAddMovieForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, // Hiển thị 5 phim 1 trang cho gọn Form
            Model model) {

        // Chuẩn bị dữ liệu cho Form thêm mới
        model.addAttribute("movie", new Movie());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Chuẩn bị dữ liệu Phân trang cho Bảng danh sách phim
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        model.addAttribute("moviePage", moviePage);

        return "add-movie";
    }

    // XỬ LÝ LƯU PHIM MỚI VÀO DB
    @PostMapping("/admin/movies/add")
    public String saveMovie(@ModelAttribute("movie") Movie movie) {
        movieService.saveMovie(movie);
        // Lưu xong đá về trang Add để thấy list phim vừa cập nhật
        return "redirect:/admin/movies/add";
    }

    // 3. MỞ FORM SỬA PHIM (Update)
    @GetMapping("/admin/movies/edit/{id}")
    public String showEditMovieForm(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-movie";
        }
        return "redirect:/admin/movies/add";
    }

    // XỬ LÝ CẬP NHẬT PHIM VÀO DB
    @PostMapping("/admin/movies/edit")
    public String updateMovie(@ModelAttribute("movie") Movie movie) {
        movieService.updateMovie(movie);
        return "redirect:/admin/movies/add";
    }

    // 4. XÓA PHIM (Delete)
    @GetMapping("/admin/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") Long id) {
        movieService.deleteMovie(id);
        return "redirect:/admin/movies/add";
    }
}