package k.thees.myspringbootexample.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import k.thees.myspringbootexample.model.BookDto;
import k.thees.myspringbootexample.services.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BookController {

	public static final String BOOKS_PATH = "/books";
	public static final String BOOKS_PATH_ID = BOOKS_PATH + "/{id}";

	private final BookService bookService;

	@GetMapping(value = BOOKS_PATH)
	public ResponseEntity<List<BookDto>> getAll() {
		return new ResponseEntity<>(bookService.getAll(), HttpStatus.OK);
	}

	@GetMapping(value = BOOKS_PATH_ID)
	public ResponseEntity<BookDto> get(@PathVariable Integer id) {

		log.debug("Get Book by Id - in controller");

		var bookDto = bookService.get(id).orElseThrow(MyNotFoundException::new);

		return new ResponseEntity<>(bookDto, HttpStatus.OK);
	}

	@PostMapping(BOOKS_PATH)
	public ResponseEntity<BookDto> create(@Validated @RequestBody BookDto bookDto) {

		BookDto savedTopicDto = bookService.create(bookDto);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", BOOKS_PATH + "/" + savedTopicDto.getId().toString());

		return new ResponseEntity<BookDto>(headers, HttpStatus.CREATED);
	}

	@PatchMapping(BOOKS_PATH_ID)
	public ResponseEntity<BookDto> patch(@PathVariable Integer id, @RequestBody BookDto bookDto) {

		return bookService.patch(id, bookDto)
				.map(it -> new ResponseEntity<BookDto>(HttpStatus.NO_CONTENT))
				.orElseThrow(MyNotFoundException::new);
	}

	@DeleteMapping(BOOKS_PATH_ID)
	public ResponseEntity<BookDto> delete(@PathVariable Integer id) {

		if (bookService.delete(id)) {
			return new ResponseEntity<BookDto>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<BookDto>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(BOOKS_PATH_ID)
	public ResponseEntity<BookDto> update(@PathVariable Integer id, @Validated @RequestBody BookDto bookDto) {

		return bookService.update(id, bookDto)
				.map(it -> new ResponseEntity<BookDto>(HttpStatus.NO_CONTENT))
				.orElseThrow(MyNotFoundException::new);
	}
}
