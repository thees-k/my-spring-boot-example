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
	public List<TopicDto> getAll() {
		return topicRepository.findAll().stream().map(topicMapper::entityToDto).collect(Collectors.toList());
	}

	@Override
	public Optional<TopicDto> get(UUID id) {
		return Optional.ofNullable(topicMapper.entityToDto(topicRepository.findById(id).orElse(null)));
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
