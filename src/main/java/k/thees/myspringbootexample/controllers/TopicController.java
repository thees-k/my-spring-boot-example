package k.thees.myspringbootexample.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.services.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TopicController {

	public static final String TOPICS_PATH = "/api/v1/topics";
	public static final String TOPICS_PATH_ID = TOPICS_PATH + "/{topicId}";

	private final TopicService topicService;

	@PatchMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> patch(@PathVariable("topicId") UUID id, @RequestBody TopicDto topicDto) {

		topicService.patchTopicById(id, topicDto);

		return new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> delete(@PathVariable("topicId") UUID id) {

		topicService.deleteById(id);

		return new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT);
	}

	@PutMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> update(@PathVariable("topicId") UUID id, @RequestBody TopicDto topicDto) {

		topicService.updateTopicById(id, topicDto);

		return new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(TOPICS_PATH)
	public ResponseEntity<TopicDto> create(@RequestBody TopicDto topicDto) {

		TopicDto savedTopicDto = topicService.saveNewTopic(topicDto);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", TOPICS_PATH + "/" + savedTopicDto.getId().toString());

		return new ResponseEntity<TopicDto>(headers, HttpStatus.CREATED);
	}

	@GetMapping(value = TOPICS_PATH)
	public ResponseEntity<List<TopicDto>> getAll() {
		return new ResponseEntity<>(topicService.listTopics(), HttpStatus.OK);
	}

	@GetMapping(value = TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> get(@PathVariable("topicId") UUID id) {

		log.debug("Get Topic by Id - in controller");

		var topicDto = topicService.getTopicById(id).orElseThrow(MyNotFoundException::new);

		return new ResponseEntity<>(topicDto, HttpStatus.OK);
	}
}
