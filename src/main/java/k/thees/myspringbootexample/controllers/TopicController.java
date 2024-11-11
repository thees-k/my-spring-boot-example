package k.thees.myspringbootexample.controllers;

import java.util.List;
import java.util.UUID;

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

import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.services.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // From Lombok, enables logging, something like:  log.debug("Get Topic by Id - in controller");
@RequiredArgsConstructor // Generates a constructor for all final fields and all fields that are marked with @NonNull
@RestController
public class TopicController {

	public static final String TOPICS_PATH = "/api/v1/topics";
	public static final String TOPICS_PATH_ID = TOPICS_PATH + "/{topicId}";

	private final TopicService topicService;

	@GetMapping(value = TOPICS_PATH) // GET http://localhost:8080/api/v1/topics
	public ResponseEntity<List<TopicDto>> getAll() {
		return new ResponseEntity<>(topicService.getAll(), HttpStatus.OK);
	}

	@GetMapping(value = TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> get(@PathVariable("topicId") UUID id) {

		log.debug("Get Topic by Id - in controller");

		var topicDto = topicService.get(id).orElseThrow(MyNotFoundException::new);

		return new ResponseEntity<>(topicDto, HttpStatus.OK);
	}


	//	POST http://localhost:8080/api/v1/topics HTTP/1.1
	//	content-type: application/json
	//
	//	{
	//	    "name": "New topic",
	//	    "style": "GREEN",
	//	    "code": "42",
	//	    "quantity": 100,
	//	    "price": 99.99,
	//	    "createdDate": "2024-11-08T13:22:51.388979",
	//	    "updateDate": "2024-11-08T13:22:51.388993"
	//	}
	//
	// (Note that the empty line after the header is important!)
	@PostMapping(TOPICS_PATH)
	public ResponseEntity<TopicDto> create(@Validated @RequestBody TopicDto topicDto) {

		TopicDto savedTopicDto = topicService.create(topicDto);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", TOPICS_PATH + "/" + savedTopicDto.getId().toString());

		return new ResponseEntity<TopicDto>(headers, HttpStatus.CREATED);
	}

	//	PATCH http://localhost:8080/api/v1/topics/f92c1891-0034-474c-83e9-60b44e378b58 HTTP/1.1
	//	content-type: application/json
	//
	//	{
	//	    "name": "Updated topic name",
	//	}
	//
	// (Note that the empty line after the header is important!)
	//
	// Patch = To update only the fields listed inside the JSON
	// The values of the not listed fields will be null inside the topicDto (-> here: code == null, price == null, createdDate == null ...)
	@PatchMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> patch(@PathVariable("topicId") UUID id, @RequestBody TopicDto topicDto) {

		return topicService.patch(id, topicDto)
				.map(it -> new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT))
				.orElseThrow(MyNotFoundException::new);
	}

	@DeleteMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> delete(@PathVariable("topicId") UUID id) {

		if (topicService.delete(id)) {
			return new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<TopicDto>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(TOPICS_PATH_ID)
	public ResponseEntity<TopicDto> update(@PathVariable("topicId") UUID id, @Validated @RequestBody TopicDto topicDto) {

		return topicService.update(id, topicDto)
				.map(it -> new ResponseEntity<TopicDto>(HttpStatus.NO_CONTENT))
				.orElseThrow(MyNotFoundException::new);
	}
}
