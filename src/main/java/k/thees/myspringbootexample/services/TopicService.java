package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import k.thees.myspringbootexample.model.TopicDto;

public interface TopicService {

	List<TopicDto> listTopics();

	Optional<TopicDto> getTopicById(UUID id);

	TopicDto saveNewTopic(TopicDto topicDto);

	void updateTopicById(UUID id, TopicDto topicDto);

	void deleteById(UUID id);

	void patchTopicById(UUID id, TopicDto topicDto);
}
