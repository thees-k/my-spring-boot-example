package k.thees.myspringbootexample.other;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import lombok.Getter;
import lombok.Setter;


/**
 * Testing purpose only!
 */
@Mapper
public interface MyMapStructMapper {

	MyMapStructMapper INSTANCE = Mappers.getMapper(MyMapStructMapper.class);

	@Mapping(source = "name", target = "fullName")
	MyDto entityToDto(MyEntity entity);

	@Mapping(source = "fullName", target = "name")
	MyEntity dtoToEntity(MyDto dto);
}

@Getter
@Setter
class MyEntity {
	private String name;
	// getters and setters
}

@Getter
@Setter
class MyDto {
	private String fullName;
	// getters and setters
}



