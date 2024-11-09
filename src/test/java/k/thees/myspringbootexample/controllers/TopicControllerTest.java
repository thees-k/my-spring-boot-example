package k.thees.myspringbootexample.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.model.TopicStyle;
import k.thees.myspringbootexample.services.TopicService;
import k.thees.myspringbootexample.services.TopicServiceImpl;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

	// It's pretty common to have properties with package visibility in test
	// classes.
	// And to use @Autowired to inject dependencies.

	@Autowired
	MockMvc mockMvc;

	@Autowired // Spring Boot is auto configuring an ObjectMapper for the use within the Spring context. Here we use that ObjectMapper.
	ObjectMapper objectMapper;

	@MockBean
	TopicService topicService;

	TopicServiceImpl topicServiceImpl;

	@Captor
	ArgumentCaptor<UUID> uuidArgumentCaptor;

	@Captor
	ArgumentCaptor<TopicDto> topicArgumentCaptor;

	@BeforeEach
	void setUp() {
		topicServiceImpl = new TopicServiceImpl();
	}

	@Test
	void testGet() throws Exception {
		TopicDto topicDto = topicServiceImpl.getAll().get(0);

		given(topicService.get(topicDto.getId())).willReturn(Optional.of(topicDto));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TopicDto.LOCAL_DATE_TIME_FORMAT);

		// mockMvc sends a mock GET request:
		MvcResult result = mockMvc.perform(get(TopicController.TOPICS_PATH_ID, topicDto.getId())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(buildMatcher("id", topicDto.getId()))
				.andExpect(buildMatcher("version", topicDto.getVersion()))
				.andExpect(buildMatcher("name", topicDto.getName()))
				.andExpect(buildMatcher("style", topicDto.getStyle()))
				.andExpect(buildMatcher("code", topicDto.getCode()))
				.andExpect(buildMatcher("quantity", topicDto.getQuantity()))
				.andExpect(buildMatcher("price", topicDto.getPrice()))
				.andExpect(buildMatcher("createdDate", topicDto.getCreatedDate().format(formatter)))
				.andExpect(buildMatcher("updateDate", topicDto.getUpdateDate().format(formatter)))
				.andReturn();

		// Get the JSON response as a string
		String jsonResponse = result.getResponse().getContentAsString();

		// Print out the JSON response
		System.out.println("\nJSON Response:\n" + jsonResponse);

		// Print the formatted JSON
		System.out.println(formatJson(jsonResponse));
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
	void testCreate() throws Exception {

		var localDateTime = LocalDateTime.now();

		TopicDto inputTopicDto = TopicDto.builder().build();

		TopicDto outputTopicDto = TopicDto.builder().createdDate(localDateTime).id(UUID.randomUUID()).name("test topic")
				.price(new BigDecimal("10.0")).quantity(10).style(TopicStyle.MAGENTA).code("test code")
				.updateDate(localDateTime).version(0).build();

		given(topicService.create(inputTopicDto)).willReturn(outputTopicDto);

		String json = objectMapper.writeValueAsString(inputTopicDto);

		System.out.println(formatJson(json));

		MvcResult result = mockMvc
				.perform(post(TopicController.TOPICS_PATH)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(header().string("location", TopicController.TOPICS_PATH + "/" +outputTopicDto.getId()))
				.andReturn();

		// Print response headers
		System.out.println("Response Headers:");
		result.getResponse().getHeaderNames().forEach(
				headerName -> System.out.println(headerName + ": " + result.getResponse().getHeader(headerName)));

		// Print response body
		String responseJson = result.getResponse().getContentAsString();
		System.out.println("Response Body:\n" + formatJson(responseJson));

	}

	@Test
	void testPatch() throws Exception {
		TopicDto topicDto = topicServiceImpl.getAll().get(0);

		Map<String, Object> topicMap = new HashMap<>();
		topicMap.put("name", "New Name");

		mockMvc.perform(patch(TopicController.TOPICS_PATH_ID, topicDto.getId()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(topicMap)))
		.andExpect(status().isNoContent());

		verify(topicService).patch(uuidArgumentCaptor.capture(), topicArgumentCaptor.capture());

		assertThat(topicDto.getId()).isEqualTo(uuidArgumentCaptor.getValue());
		assertThat(topicMap.get("name")).isEqualTo(topicArgumentCaptor.getValue().getName());
	}

	@Test
	void testDelete() throws Exception {
		TopicDto topicDto = topicServiceImpl.getAll().get(0);

		mockMvc.perform(delete(TopicController.TOPICS_PATH_ID, topicDto.getId()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());

		verify(topicService).delete(uuidArgumentCaptor.capture());

		assertThat(topicDto.getId()).isEqualTo(uuidArgumentCaptor.getValue());
	}

	@Test
	void testUpdate() throws Exception {
		TopicDto topicDto = topicServiceImpl.getAll().get(0);

		mockMvc.perform(put(TopicController.TOPICS_PATH_ID, topicDto.getId())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(topicDto)))
		.andExpect(status().isNoContent());

		verify(topicService).update(any(UUID.class), any(TopicDto.class));
	}

	@Test
	void testGetAll() throws Exception {
		given(topicService.getAll()).willReturn(topicServiceImpl.getAll());

		mockMvc.perform(get(TopicController.TOPICS_PATH).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.length()", is(3)));
	}

	@Test
	void testGetNotFound() throws Exception {

		given(topicService.get(any(UUID.class))).willReturn(Optional.empty());

		mockMvc.perform(get(TopicController.TOPICS_PATH_ID, UUID.randomUUID())).andExpect(status().isNotFound());
	}
}