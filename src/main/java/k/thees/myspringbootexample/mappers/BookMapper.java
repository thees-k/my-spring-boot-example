package k.thees.myspringbootexample.mappers;

import java.math.BigDecimal;
import java.util.Optional;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.model.BookDto;

@Mapper
public interface BookMapper {

	BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

	// Mapping from Book entity to BookDto
	@Mapping(target = "author", expression = "java(java.util.Optional.ofNullable(book.getAuthor()))")
	@Mapping(target = "title", expression = "java(java.util.Optional.ofNullable(book.getTitle()))")
	@Mapping(target = "price", expression = "java(java.util.Optional.ofNullable(book.getPrice()))")
	BookDto entityToDto(Book book);

	// Mapping from BookDto to Book entity
	@Mapping(target = "author", source = "author")
	@Mapping(target = "title", source = "title")
	@Mapping(target = "price", source = "price")
	Book dtoToEntity(BookDto bookDto);

	// Update an existing Book entity with fields from BookDto for PATCH requests
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateBookFromDto(BookDto bookDto, @MappingTarget Book book);

	// Helper methods to unwrap Optional values in BookDto -> Book mappings
	default String unwrapOptionalString(Optional<String> optional) {
		return optional.orElse(null);
	}

	default BigDecimal unwrapOptionalBigDecimal(Optional<BigDecimal> optional) {
		return optional.orElse(null);
	}
}
