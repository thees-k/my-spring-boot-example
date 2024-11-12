package k.thees.myspringbootexample.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.h2.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.mappers.BookMapper;
import k.thees.myspringbootexample.model.BookDto;
import k.thees.myspringbootexample.repositories.BookRepository;
import k.thees.myspringbootexample.services.BookService;

@SpringBootTest
class BookControllerIntegrationTest {

	@Autowired
	BookService bookService;

	@Autowired
	BookController bookController;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	BookMapper bookMapper;


	@Test
	void testGet() {
		Book bookEntity = bookRepository.findAll().get(0);

		BookDto dto = bookController.get(bookEntity.getId()).getBody();

		assertThat(dto).isNotNull();
	}

	@Test
	void testGetBookWithMyNotFoundException() {
		assertThrows(MyNotFoundException.class, () -> bookController.get(Integer.MAX_VALUE));
	}

	@Test
	void testGetAll() {
		List<BookDto> dtos = bookController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(3);
	}

	@Rollback
	@Transactional
	@Test
	void testGetAllReturnsEmptyList() {
		bookRepository.deleteAll();
		List<BookDto> dtos = bookController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(0);
	}

	@Rollback
	@Transactional
	@Test
	void testCreate() {

		final BookDto inputBookDto = BookDto.builder()//
				.author("test author")
				.title("test title")
				.price(new BigDecimal("100"))
				.build();

		ResponseEntity<BookDto> responseEntity = bookController.create(inputBookDto);

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.CREATED.value()));
		assertNotNull(responseEntity.getHeaders().getLocation());

		String [] pathElements = responseEntity.getHeaders().getLocation().getPath().split("/");
		String id = pathElements[pathElements.length-1];

		bookRepository.findById(Integer.parseInt(id))
		.ifPresentOrElse(newCreatedBookEntity -> validateFullyUpdatedBook(newCreatedBookEntity, inputBookDto), AssertionError::new);
	}

	@Rollback
	@Transactional
	@Test
	void testDelete() {
		Book bookEntity = bookRepository.findAll().get(0);

		ResponseEntity<BookDto> responseEntity = bookController.delete(bookEntity.getId());

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		assertFalse(bookRepository.existsById(bookEntity.getId()));
	}

	@Rollback
	@Transactional
	@Test
	void testDeleteButNotFound() {

		Integer id = Integer.MAX_VALUE;

		assert(!bookRepository.existsById(id));

		ResponseEntity<BookDto> responseEntity = bookController.delete(id);

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
	}

	@Rollback
	@Transactional
	@Test
	void testUpdate() {

		Book bookEntity = bookRepository.findAll().get(0);

		final BookDto updateBookDto = BookDto.builder()//
				.author("test author")
				.title("test title")
				.price(new BigDecimal("30.00"))
				.build();

		ResponseEntity<BookDto> responseEntity = bookController.update(bookEntity.getId(), updateBookDto);
		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		validateFullyUpdatedBook(bookEntity, updateBookDto);
	}

	private void validateFullyUpdatedBook(Book updatedBookEntity, BookDto expectedBookDto) {
		assertNotNull(updatedBookEntity.getId());

		assertEquals(updatedBookEntity.getAuthor(), expectedBookDto.getAuthor());
		assertEquals(updatedBookEntity.getTitle(), expectedBookDto.getTitle());
		assertTrue(updatedBookEntity.getPrice().compareTo(expectedBookDto.getPrice()) == 0);
	}

	@Rollback
	@Transactional
	@Test
	void testUpdateButBotFound() {

		Integer id = Integer.MAX_VALUE;

		assert(!bookRepository.existsById(id));

		assertThrows(MyNotFoundException.class, () -> bookController.update(id, BookDto.builder().build()));
	}

	@Rollback
	@Transactional
	@Test
	void testPatch() {

		Book bookEntity = bookRepository.findAll().get(0);
		BookDto oldBookDto = bookMapper.entityToDto(bookEntity);

		final BookDto updateBookDto = BookDto.builder()//
				.author(null) // -> author will not be updated
				.title("test title")
				.price(new BigDecimal("30.00"))
				.build();

		ResponseEntity<BookDto> responseEntity = bookController.patch(bookEntity.getId(), updateBookDto);
		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		validatePatchedBook(bookEntity, updateBookDto, oldBookDto);
	}

	private void validatePatchedBook(Book patchedBookEntity, BookDto expectedBookDto, BookDto oldBookDto) {
		assertNotNull(patchedBookEntity.getId());

		if(!StringUtils.isNullOrEmpty(expectedBookDto.getAuthor())) {
			assertEquals(patchedBookEntity.getAuthor(), expectedBookDto.getAuthor());
		} else {
			assertEquals(patchedBookEntity.getAuthor(), oldBookDto.getAuthor());
		}
		if(!StringUtils.isNullOrEmpty(expectedBookDto.getTitle())) {
			assertEquals(patchedBookEntity.getTitle(), expectedBookDto.getTitle());
		} else {
			assertEquals(patchedBookEntity.getTitle(), oldBookDto.getTitle());
		}
		if(expectedBookDto.getPrice() != null) {
			assertTrue(patchedBookEntity.getPrice().compareTo(expectedBookDto.getPrice()) == 0);
		} else {
			assertTrue(patchedBookEntity.getPrice().compareTo(oldBookDto.getPrice()) == 0);
		}
	}

	@Rollback
	@Transactional
	@Test
	void testPatchButBotFound() {

		Integer id = Integer.MAX_VALUE;

		assert(!bookRepository.existsById(id));

		assertThrows(MyNotFoundException.class, () -> bookController.patch(id, BookDto.builder().build()));
	}

}
