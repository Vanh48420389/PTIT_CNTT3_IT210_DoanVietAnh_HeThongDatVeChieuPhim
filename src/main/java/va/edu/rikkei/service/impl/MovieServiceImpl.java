package va.edu.rikkei.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import va.edu.rikkei.model.entity.Movie;
import va.edu.rikkei.repository.MovieRepository;
import va.edu.rikkei.service.MovieService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    @Override
    public Movie updateMovie(Movie updatedMovie) {
        Movie existingMovie = getMovieById(updatedMovie.getId());
        if (existingMovie != null) {
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setDuration(updatedMovie.getDuration());
            existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
            existingMovie.setStatus(updatedMovie.getStatus());
            existingMovie.setPosterUrl(updatedMovie.getPosterUrl());
            existingMovie.setDescription(updatedMovie.getDescription());

            return movieRepository.save(existingMovie);
        }
        return null;
    }

    @Override
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}