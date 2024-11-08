package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import k.thees.myspringbootexample.mappers.TopicMapper;
import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class TopicServiceJPA implements TopicService {
	private final TopicRepository topicRepository;
	private final TopicMapper topicMapper;

	@Override
	public List<TopicDto> listTopics() {
		return topicRepository.findAll().stream().map(topicMapper::entityToDto).collect(Collectors.toList());
	}

	@Override
	public Optional<TopicDto> getTopicById(UUID id) {
		return Optional.ofNullable(topicMapper.entityToDto(topicRepository.findById(id).orElse(null)));
	}

	@Override
	public TopicDto saveNewTopic(TopicDto topicDto) {
		return null;
	}

	@Override
	public void updateTopicById(UUID id, TopicDto topicDto) {

	}

	@Override
	public void deleteById(UUID id) {

	}

	@Override
	public void patchTopicById(UUID id, TopicDto topicDto) {

	}
}
