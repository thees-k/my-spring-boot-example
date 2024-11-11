package k.thees.myspringbootexample.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.h2.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import k.thees.myspringbootexample.entities.TopicEntity;
import k.thees.myspringbootexample.mappers.TopicMapper;
import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.model.TopicStyle;
import k.thees.myspringbootexample.repositories.TopicRepository;
import k.thees.myspringbootexample.services.TopicService;

@SpringBootTest
class TopicControllerIntegrationTest {

	@Autowired
	TopicService topicService;

	@Autowired
	TopicController topicController;

	@Autowired
	TopicRepository topicRepository;

	@Autowired
	TopicMapper topicMapper;


	@Test
	void testGet() {
		TopicEntity topicEntity = topicRepository.findAll().get(0);

		TopicDto dto = topicController.get(topicEntity.getId()).getBody();

		assertThat(dto).isNotNull();
	}

	@Test
	void testGetTopicWithMyNotFoundException() {
		assertThrows(MyNotFoundException.class, () -> topicController.get(UUID.randomUUID()));
	}

	@Test
	void testGetAll() {
		List<TopicDto> dtos = topicController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(3);
	}

	@Rollback
	@Transactional
	@Test
	void testGetAllReturnsEmptyList() {
		topicRepository.deleteAll();
		List<TopicDto> dtos = topicController.getAll().getBody();

		assertThat(dtos.size()).isEqualTo(0);
	}

	@Rollback
	@Transactional
	@Test
	void testCreate() {

		LocalDateTime updatedAt = LocalDateTime.of(2024, 11, 9, 17, 45);
		LocalDateTime createdAt = updatedAt.minusDays(1L);

		final TopicDto inputTopicDto = TopicDto.builder()//
				.code("test code")
				.createdDate(createdAt)
				.name("test name")
				.price(new BigDecimal("42.00"))
				.quantity(42)
				.style(TopicStyle.YELLOW)
				.updateDate(updatedAt)
				.build();

		ResponseEntity<TopicDto> responseEntity = topicController.create(inputTopicDto);

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.CREATED.value()));
		assertNotNull(responseEntity.getHeaders().getLocation());

		String [] pathElements = responseEntity.getHeaders().getLocation().getPath().split("/");
		String uuid = pathElements[pathElements.length-1];

		topicRepository.findById(UUID.fromString(uuid))
		.ifPresentOrElse(newCreatedTopicEntity -> validateFullyUpdatedTopic(newCreatedTopicEntity, inputTopicDto), AssertionError::new);
	}

	@Rollback
	@Transactional
	@Test
	void testDelete() {
		TopicEntity topicEntity = topicRepository.findAll().get(0);

		ResponseEntity<TopicDto> responseEntity = topicController.delete(topicEntity.getId());

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		assertFalse(topicRepository.existsById(topicEntity.getId()));
	}

	@Rollback
	@Transactional
	@Test
	void testDeleteButNotFound() {

		UUID randomUuid = UUID.randomUUID();

		assert(!topicRepository.existsById(randomUuid));

		ResponseEntity<TopicDto> responseEntity = topicController.delete(randomUuid);

		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
	}

	@Rollback
	@Transactional
	@Test
	void testUpdate() {

		TopicEntity topicEntity = topicRepository.findAll().get(0);

		LocalDateTime updatedAt = LocalDateTime.of(2024, 11, 9, 17, 45);
		LocalDateTime createdAt = updatedAt.minusDays(1L);

		final TopicDto updateTopicDto = TopicDto.builder()//
				.code("test code")
				.createdDate(createdAt)
				.name("test name")
				.price(new BigDecimal("42.00"))
				.quantity(null)
				.style(TopicStyle.YELLOW)
				.updateDate(updatedAt)
				.build();

		ResponseEntity<TopicDto> responseEntity = topicController.update(topicEntity.getId(), updateTopicDto);
		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		validateFullyUpdatedTopic(topicEntity, updateTopicDto);
	}

	private void validateFullyUpdatedTopic(TopicEntity updatedTopicEntity, TopicDto expectedTopicDto) {
		assertNotNull(updatedTopicEntity.getId());
		assertNotNull(updatedTopicEntity.getVersion());

		assertEquals(updatedTopicEntity.getCode(), expectedTopicDto.getCode());
		assertEquals(updatedTopicEntity.getCreatedDate(), expectedTopicDto.getCreatedDate());
		assertEquals(updatedTopicEntity.getName(), expectedTopicDto.getName());
		assertTrue(updatedTopicEntity.getPrice().compareTo(expectedTopicDto.getPrice()) == 0);
		assertEquals(updatedTopicEntity.getQuantity(), expectedTopicDto.getQuantity());
		assertEquals(updatedTopicEntity.getStyle(), expectedTopicDto.getStyle());
		assertEquals(updatedTopicEntity.getUpdateDate(), expectedTopicDto.getUpdateDate());
	}

	@Rollback
	@Transactional
	@Test
	void testUpdateButBotFound() {

		UUID randomUuid = UUID.randomUUID();

		assert(!topicRepository.existsById(randomUuid));

		assertThrows(MyNotFoundException.class, () -> topicController.update(randomUuid, TopicDto.builder().build()));
	}

	@Rollback
	@Transactional
	@Test
	void testPatch() {

		TopicEntity topicEntity = topicRepository.findAll().get(0);
		TopicDto oldTopicDto = topicMapper.entityToDto(topicEntity);

		LocalDateTime updatedAt = LocalDateTime.of(2024, 11, 9, 17, 45);

		final TopicDto updateTopicDto = TopicDto.builder()//
				.code("test code")
				.createdDate(null) // -> 'createdDate' will not be updated
				.name("test name")
				.price(new BigDecimal("42.00"))
				.quantity(null) // -> quantity will not be updated
				.style(TopicStyle.YELLOW)
				.updateDate(updatedAt)
				.build();

		ResponseEntity<TopicDto> responseEntity = topicController.patch(topicEntity.getId(), updateTopicDto);
		assertEquals(responseEntity.getStatusCode(), HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()));

		validatePatchedTopic(topicEntity, updateTopicDto, oldTopicDto);
	}

	private void validatePatchedTopic(TopicEntity patchedTopicEntity, TopicDto expectedTopicDto, TopicDto oldTopicDto) {
		assertNotNull(patchedTopicEntity.getId());
		assertNotNull(patchedTopicEntity.getVersion());

		if(!StringUtils.isNullOrEmpty(expectedTopicDto.getCode())) {
			assertEquals(patchedTopicEntity.getCode(), expectedTopicDto.getCode());
		} else {
			assertEquals(patchedTopicEntity.getCode(), oldTopicDto.getCode());
		}
		if(expectedTopicDto.getCreatedDate() != null) {
			assertEquals(patchedTopicEntity.getCreatedDate(), expectedTopicDto.getCreatedDate());
		} else {
			assertEquals(patchedTopicEntity.getCreatedDate(), oldTopicDto.getCreatedDate());
		}
		if(!StringUtils.isNullOrEmpty(expectedTopicDto.getName())) {
			assertEquals(patchedTopicEntity.getName(), expectedTopicDto.getName());
		} else {
			assertEquals(patchedTopicEntity.getName(), oldTopicDto.getName());
		}
		if(expectedTopicDto.getPrice() != null) {
			assertTrue(patchedTopicEntity.getPrice().compareTo(expectedTopicDto.getPrice()) == 0);
		} else {
			assertTrue(patchedTopicEntity.getPrice().compareTo(oldTopicDto.getPrice()) == 0);
		}
		if(expectedTopicDto.getQuantity() != null) {
			assertEquals(patchedTopicEntity.getQuantity(), expectedTopicDto.getQuantity());
		} else {
			assertEquals(patchedTopicEntity.getQuantity(), oldTopicDto.getQuantity());
		}
		if(expectedTopicDto.getStyle() != null) {
			assertEquals(patchedTopicEntity.getStyle(), expectedTopicDto.getStyle());
		} else {
			assertEquals(patchedTopicEntity.getStyle(), oldTopicDto.getStyle());
		}
		if(expectedTopicDto.getUpdateDate() != null) {
			assertEquals(patchedTopicEntity.getUpdateDate(), expectedTopicDto.getUpdateDate());
		} else {
			assertEquals(patchedTopicEntity.getUpdateDate(), oldTopicDto.getUpdateDate());
		}
	}

	@Rollback
	@Transactional
	@Test
	void testPatchButBotFound() {

		UUID randomUuid = UUID.randomUUID();

		assert(!topicRepository.existsById(randomUuid));

		assertThrows(MyNotFoundException.class, () -> topicController.patch(randomUuid, TopicDto.builder().build()));
	}

}
