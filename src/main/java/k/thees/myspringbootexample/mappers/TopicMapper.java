package k.thees.myspringbootexample.mappers;

import org.mapstruct.Mapper;

import k.thees.myspringbootexample.entities.TopicEntity;
import k.thees.myspringbootexample.model.TopicDto;

@Mapper
public interface TopicMapper {

	TopicEntity dtoToEntity(TopicDto dto);

	TopicDto entityToDto(TopicEntity entity);

}
