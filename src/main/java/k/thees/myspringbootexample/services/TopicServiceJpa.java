package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import k.thees.myspringbootexample.mappers.TopicMapper;
import k.thees.myspringbootexample.model.TopicDto;
import k.thees.myspringbootexample.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;

@Service
@Primary // Because there is another implementation of TopicService we need to tell Spring Boot what is the default one that is to take for dependency injection
@RequiredArgsConstructor
public class TopicServiceJpa implements TopicService {
	private final TopicRepository topicRepository;
	private final TopicMapper topicMapper;

	@Override
	public List<TopicDto> getAll() {
		return topicRepository
				.findAll()
				.stream()
				.map(topicMapper::entityToDto)
				.toList();
	}

	@Override
	public Optional<TopicDto> get(UUID id) {
		return topicRepository//
				.findById(id)
				.map(topicMapper::entityToDto)
				.map(Optional::of)
				.orElse(Optional.empty());
	}

	@Override
	public TopicDto create(TopicDto topicDto) {
		return null;
	}

	@Override
	public void update(UUID id, TopicDto topicDto) {

	}

	@Override
	public void delete(UUID id) {

	}

	@Override
	public void patch(UUID id, TopicDto topicDto) {

	}
}
