package k.thees.myspringbootexample.mappers;

import org.mapstruct.Mapper;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.model.BookDto;

@Mapper
public interface BookMapper {

	Book dtoToEntity(BookDto dto);

	BookDto entityToDto(Book book);

}
