package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import k.thees.myspringbootexample.model.TopicDto;

public interface TopicService {

	List<TopicDto> getAll();

	Optional<TopicDto> get(UUID id);

	TopicDto create(TopicDto topicDto);

	void update(UUID id, TopicDto topicDto);

	void delete(UUID id);

	void patch(UUID id, TopicDto topicDto);
}
