package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.h2.util.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import k.thees.myspringbootexample.entities.TopicEntity;
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

		TopicEntity topicEntity = topicMapper.dtoToEntity(topicDto);
		return topicMapper.entityToDto(topicRepository.save(topicEntity));
	}

	@Override
	public Optional<TopicDto> update(UUID id, TopicDto topicDto) {

		if(topicRepository.existsById(id)) {
			topicRepository.findById(id)
			.ifPresent(topicEntity -> copyAllFieldsFromDtoToEntity(topicDto, topicEntity));
			return Optional.of(topicDto);
		} else {
			return Optional.empty();
		}
	}

	private void copyAllFieldsFromDtoToEntity(TopicDto topicDto, TopicEntity topicEntity) {
		topicEntity.setCode(topicDto.getCode());
		topicEntity.setCreatedDate(topicDto.getCreatedDate());
		topicEntity.setName(topicDto.getName());
		topicEntity.setPrice(topicDto.getPrice());
		topicEntity.setQuantity(topicDto.getQuantity());
		topicEntity.setStyle(topicDto.getStyle());
		topicEntity.setUpdateDate(topicDto.getUpdateDate());

		topicRepository.save(topicEntity);
	}

	@Override
	public boolean delete(UUID id) {

		if(topicRepository.existsById(id)) {
			topicRepository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Optional<TopicDto> patch(UUID id, TopicDto topicDto) {

		if(topicRepository.existsById(id)) {
			return topicRepository.findById(id)
					.map(topicEntity -> copyOnlySetFieldsFromDtoToEntity(topicDto, topicEntity));
		} else {
			return Optional.empty();
		}
	}

	private TopicDto copyOnlySetFieldsFromDtoToEntity(TopicDto topicDto, TopicEntity topicEntity) {

		if(!StringUtils.isNullOrEmpty(topicDto.getCode())) {
			topicEntity.setCode(topicDto.getCode());
		}
		if(topicDto.getCreatedDate() != null) {
			topicEntity.setCreatedDate(topicDto.getCreatedDate());
		}
		if(!StringUtils.isNullOrEmpty(topicDto.getName())) {
			topicEntity.setName(topicDto.getName());
		}
		if(topicDto.getPrice() != null) {
			topicEntity.setPrice(topicDto.getPrice());
		}
		if(topicDto.getQuantity() != null) {
			topicEntity.setQuantity(topicDto.getQuantity());
		}
		if(topicDto.getStyle() != null) {
			topicEntity.setStyle(topicDto.getStyle());
		}
		if(topicDto.getUpdateDate() != null) {
			topicEntity.setUpdateDate(topicDto.getUpdateDate());
		}
		topicRepository.save(topicEntity);

		return topicMapper.entityToDto(topicEntity);
	}
}
