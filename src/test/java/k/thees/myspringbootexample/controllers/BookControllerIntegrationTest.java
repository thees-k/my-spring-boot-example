package k.thees.myspringbootexample.controllers;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.model.BookDto;
import k.thees.myspringbootexample.repositories.BookRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class BookControllerIntegrationTest {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testGet() throws Exception {

		Book book = bookRepository.findAll().get(0);

		// mockMvc sends a mock GET request:
		MvcResult result = mockMvc.perform(get(BookController.BOOKS_PATH_ID, book.getId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(buildMatcher("id", book.getId()))
				.andExpect(buildMatcher("author", book.getAuthor()))
				.andExpect(buildMatcher("title", book.getTitle()))
				.andReturn();

		// Get the JSON response as a string
		String jsonResponse = result.getResponse().getContentAsString();

		// Print out the JSON response
		log.debug("\nJSON Response:\n" + jsonResponse);

		// Print the formatted JSON
		log.debug(formatJson(jsonResponse));
	}

	private ResultMatcher buildMatcher(String fieldName, Object object) {
		return jsonPath("$."+fieldName).value(object + "");
	}

	private String formatJson(String jsonResponse) throws JsonProcessingException, JsonMappingException {

		if (StringUtils.isBlank(jsonResponse)) {
			return "";
		} else {
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty-printing

			// Parse the JSON string and format it
			Object json = objectMapper.readValue(jsonResponse, Object.class);
			return objectMapper.writeValueAsString(json);
		}

	}

	@Test
	void testGetButNotFound() throws Exception {

		mockMvc.perform(get(BookController.BOOKS_PATH_ID, -1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

	@Test
	void testGetAll() throws Exception {

		int recordCount = (int) bookRepository.count();

		mockMvc.perform(get(BookController.BOOKS_PATH)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.length()", is(recordCount)));

	}

	@Rollback
	@Transactional
	@Test
	void testGetAllReturnsEmptyList() throws Exception {

		bookRepository.deleteAll();

		assert(bookRepository.count() == 0);

		mockMvc.perform(get(BookController.BOOKS_PATH)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.length()", is(0)));
	}

	@Rollback
	@Transactional
	@Test
	void testCreate() throws Exception {

		final BookDto inputBookDto = BookDto.builder()//
				.author("test author")
				.title("test title")
				.price(new BigDecimal("100"))
				.build();

		String json = objectMapper.writeValueAsString(inputBookDto);

		log.debug(formatJson(json));

		MvcResult result = mockMvc
				.perform(post(BookController.BOOKS_PATH)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andReturn();

		// Print response headers
		log.debug("Response Headers:");
		result.getResponse().getHeaderNames().forEach(
				headerName -> log.debug(headerName + ": " + result.getResponse().getHeader(headerName)));

		// Print response body
		String responseJson = result.getResponse().getContentAsString();
		log.debug("Response Body:\n" + formatJson(responseJson));

		Integer id = fetchIdFromLocation(result);

		bookRepository.findById(id)
		.ifPresentOrElse(newCreatedBook -> validateBookCompletely(newCreatedBook, inputBookDto, id), AssertionError::new);
	}

	private Integer fetchIdFromLocation(MvcResult result) {
		String pathOfLocation = result.getResponse().getHeaders("Location").get(0);
		String [] pathElements = pathOfLocation.split("/");
		String id = pathElements[pathElements.length-1];
		return Integer.valueOf(id);
	}

	@Rollback
	@Transactional
	@Test
	void testDelete() throws Exception {

		Book book = bookRepository.findAll().get(0);
		Integer id = book.getId();

		mockMvc.perform(delete(BookController.BOOKS_PATH_ID, book.getId())
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());

		bookRepository.flush();

		assertTrue(bookRepository.findById(id).isEmpty());
	}

	@Rollback
	@Transactional
	@Test
	void testDeleteButNotFound() throws Exception {

		mockMvc.perform(delete(BookController.BOOKS_PATH_ID, -1)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}

	@Rollback
	@Transactional
	@Test
	void testUpdate() throws JsonProcessingException, Exception {

		Book bookEntity = bookRepository.findAll().get(0);
		Integer id = bookEntity.getId();

		final BookDto updateBookDto = BookDto.builder()//
				.author("test author")
				.title("test title")
				.price(new BigDecimal("30.00"))
				.build();

		mockMvc.perform(put(BookController.BOOKS_PATH_ID, id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateBookDto)))
		.andExpect(status().isNoContent());

		bookRepository.flush();

		bookEntity = bookRepository.findById(id).get();
		validateBookCompletely(bookEntity, updateBookDto, id);
	}

	private void validateBookCompletely(Book book, BookDto expectedBookDto, Integer expectedId) {

		assertEquals(book.getId(), expectedId);
		assertEquals(book.getAuthor(), expectedBookDto.getAuthor());
		assertEquals(book.getTitle(), expectedBookDto.getTitle());
		assertTrue(book.getPrice().compareTo(expectedBookDto.getPrice()) == 0);
	}

	@Rollback
	@Transactional
	@Test
	void testUpdateButNotFound() throws Exception {

		final BookDto updateBookDto = BookDto.builder()//
				.author("test author")
				.title("test title")
				.price(new BigDecimal("30.00"))
				.build();

		mockMvc.perform(put(BookController.BOOKS_PATH_ID, -1)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateBookDto)))
		.andExpect(status().isNotFound());
	}

	@Rollback
	@Transactional
	@Test
	void testPatch() throws Exception {

		String newTitle = "New title";

		Map<String, Object> map = new HashMap<>();
		map.put("title", newTitle);

		Book book = bookRepository.findAll().get(0);
		String author = book.getAuthor();
		BigDecimal price = book.getPrice();

		mockMvc.perform(patch(BookController.BOOKS_PATH_ID, book.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(map)))
		.andExpect(status().isNoContent());

		bookRepository.flush();

		book = bookRepository.findAll().get(0);

		assertEquals(newTitle, book.getTitle());
		assertEquals(author, book.getAuthor());
		assertTrue(book.getPrice().compareTo(price) == 0);
	}

	@Rollback
	@Transactional
	@Test
	void testPatchButNotFound() throws JsonProcessingException, Exception {

		Book book = bookRepository.findAll().get(0);

		mockMvc.perform(patch(BookController.BOOKS_PATH_ID, -1)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(book)))
		.andExpect(status().isNotFound());
	}

	@Rollback
	@Transactional
	@Test
	void testPatchWithEmptyTitle() throws Exception {

		// TODO Activate out-commented code and find out why there is no constraint validation exception!

		//		String newTitle = "";
		//
		//		Map<String, Object> map = new HashMap<>();
		//		map.put("title", newTitle);
		//
		//		Book book = bookRepository.findAll().get(0);
		//		Integer id = book.getId();
		//		String author = book.getAuthor();
		//		BigDecimal price = book.getPrice();
		//
		//		mockMvc.perform(patch(BookController.BOOKS_PATH_ID, id)
		//				.contentType(MediaType.APPLICATION_JSON)
		//				.accept(MediaType.APPLICATION_JSON)
		//				.content(objectMapper.writeValueAsString(map)))
		//		.andExpect(status().isNoContent());
		//
		//		bookRepository.flush();
		//
		//		book = bookRepository.findById(id).get();
		//
		//		assertEquals(newTitle, book.getTitle());
		//		assertEquals(author, book.getAuthor());
		//		assertTrue(book.getPrice().compareTo(price) == 0);
	}
}
