package k.thees.myspringbootexample.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.mappers.BookMapper;
import k.thees.myspringbootexample.model.BookDto;
import k.thees.myspringbootexample.repositories.BookRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceJpaImplementation implements BookService {
	private final BookRepository bookRepository;
	private final BookMapper bookMapper;

	@Override
	public List<BookDto> getAll() {
		return bookRepository
				.findAll()
				.stream()
				.map(bookMapper::entityToDto)
				.toList();
	}

	@Override
	public Optional<BookDto> get(Integer id) {
		return bookRepository//
				.findById(id)
				.map(bookMapper::entityToDto)
				.map(Optional::of)
				.orElse(Optional.empty());
	}

	@Override
	public BookDto create(BookDto bookDto) {

		Book book = bookMapper.dtoToEntity(bookDto);
		return bookMapper.entityToDto(bookRepository.save(book));
	}

	//	@Override
	//	public Optional<BookDto> update(Integer id, BookDto bookDto) {
	//
	//		if(bookRepository.existsById(id)) {
	//			bookRepository.findById(id)
	//			.ifPresent(book -> copyAllFieldsFromDtoToEntity(bookDto, book));
	//			return Optional.of(bookDto);
	//		} else {
	//			return Optional.empty();
	//		}
	//	}
	//
	//	private void copyAllFieldsFromDtoToEntity(BookDto bookDto, Book book) {
	//		book.setAuthor(bookDto.getAuthor());
	//		book.setTitle(bookDto.getTitle());
	//		book.setPrice(bookDto.getPrice());
	//
	//		bookRepository.save(book);
	//	}

	@Override
	public boolean delete(Integer id) {

		if(bookRepository.existsById(id)) {
			bookRepository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}


	//	@Override
	//	public Optional<BookDto> patch(Integer id, BookDto bookDto) {
	//
	//		if(bookRepository.existsById(id)) {
	//			return bookRepository.findById(id)
	//					.map(book -> copyOnlySetFieldsFromDtoToEntity(bookDto, book));
	//		} else {
	//			return Optional.empty();
	//		}
	//	}
	//
	//	private BookDto copyOnlySetFieldsFromDtoToEntity(BookDto bookDto, Book book) {
	//
	//		if(!StringUtils.isNullOrEmpty(bookDto.getAuthor())) {
	//			book.setAuthor(bookDto.getAuthor());
	//		}
	//		if(!StringUtils.isNullOrEmpty(bookDto.getTitle())) {
	//			book.setTitle(bookDto.getTitle());
	//		}
	//		if(bookDto.getPrice() != null) {
	//			book.setPrice(bookDto.getPrice());
	//		}
	//
	//		bookRepository.save(book);
	//
	//		return bookMapper.entityToDto(book);
	//	}

	@Override
	public Optional<BookDto> update(Integer id, BookDto bookDto) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<BookDto> patch(Integer id, BookDto bookDto) {
		// TODO Auto-generated method stub
		//		return Optional.empty();
		Book existingBook = bookRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Book not found"));

		BookMapper.INSTANCE.updateBookFromDto(bookDto, existingBook);

		Book book = bookRepository.save(existingBook);

		return Optional.of(bookMapper.entityToDto(book));
	}

}
