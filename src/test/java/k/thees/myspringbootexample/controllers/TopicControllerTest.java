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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.services.TopicService;
import k.thees.myspringbootexample.services.TopicServiceImpl;

@WebMvcTest(TopicController.class)
class TopicControllerTest {

	// It's pretty common to have properties with package visibility in test
	// classes.
	// And to use @Autowired to inject dependencies.

	@Autowired
	MockMvc mockMvc;

	@Autowired
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

		mockMvc.perform(get(TopicController.TOPICS_PATH_ID, topicDto.getId()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id", is(topicDto.getId().toString())))
		.andExpect(jsonPath("$.name", is(topicDto.getName())));
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

		mockMvc.perform(put(TopicController.TOPICS_PATH_ID, topicDto.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(topicDto)))
		.andExpect(status().isNoContent());

		verify(topicService).update(any(UUID.class), any(TopicDto.class));
	}

	@Test
	void testCreate() throws Exception {
		TopicDto topicDto = topicServiceImpl.getAll().get(0);
		topicDto.setVersion(null);
		topicDto.setId(null);

		given(topicService.create(any(TopicDto.class))).willReturn(topicServiceImpl.getAll().get(1));

		mockMvc.perform(post(TopicController.TOPICS_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(topicDto)))
		.andExpect(status().isCreated()).andExpect(header().exists("Location"));
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