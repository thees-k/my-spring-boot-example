package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;

import k.thees.myspringbootexample.model.BookDto;

public interface BookService {

	List<BookDto> getAll();

	Optional<BookDto> get(Integer id);

	BookDto create(BookDto bookDto);

	Optional<BookDto> update(Integer id, BookDto bookDto);

	boolean delete(Integer id);

	Optional<BookDto> patch(Integer id, BookDto bookDto);
}
