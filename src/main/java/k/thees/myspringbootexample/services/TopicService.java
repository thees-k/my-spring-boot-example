package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import k.thees.myspringbootexample.model.TopicDto;

public interface TopicService {

	List<TopicDto> getAll();

	Optional<TopicDto> get(UUID id);

	TopicDto create(TopicDto topicDto);

	Optional<TopicDto> update(UUID id, TopicDto topicDto);

	boolean delete(UUID id);

	Optional<TopicDto> patch(UUID id, TopicDto topicDto);
}
