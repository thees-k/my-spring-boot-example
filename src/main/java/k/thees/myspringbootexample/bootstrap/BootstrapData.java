package k.thees.myspringbootexample.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import k.thees.myspringbootexample.entities.Book;
import k.thees.myspringbootexample.entities.TopicEntity;
import k.thees.myspringbootexample.model.TopicStyle;
import k.thees.myspringbootexample.repositories.BookRepository;
import k.thees.myspringbootexample.repositories.TopicRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {
	private final TopicRepository topicRepository;
	private final BookRepository bookRepository;

	@Override
	public void run(String... args) throws Exception {
		insertTopicData();
		insertBookData();
	}

	private void insertTopicData() {
		if (topicRepository.count() == 0) {
			TopicEntity topic1 = TopicEntity.builder().name("Galaxy Cat").style(TopicStyle.BLUE).code("12356")
					.price(new BigDecimal("12.99")).quantity(122).createdDate(LocalDateTime.now())
					.updateDate(LocalDateTime.now()).build();

			TopicEntity topic2 = TopicEntity.builder().name("Crank").style(TopicStyle.BLUE).code("12356222")
					.price(new BigDecimal("11.99")).quantity(392).createdDate(LocalDateTime.now())
					.updateDate(LocalDateTime.now()).build();

			TopicEntity topic3 = TopicEntity.builder().name("Sunshine City").style(TopicStyle.YELLOW).code("1704")
					.price(new BigDecimal("13.99")).quantity(144).createdDate(LocalDateTime.now())
					.updateDate(LocalDateTime.now()).build();

			topicRepository.save(topic1);
			topicRepository.save(topic2);
			topicRepository.save(topic3);
		}
	}


	private void insertBookData() {
		if (bookRepository.count() == 0) {

			Book book1 = Book.builder()
					.author("F. Scott Fitzgerald")
					.price(new BigDecimal("19.90"))
					.title("The Great Gatsby")
					.build();
			Book book2 = Book.builder()
					.author("James Joyce")
					.price(new BigDecimal("27.50"))
					.title("Ulysses")
					.build();
			Book book3 = Book.builder()
					.author("Marcel Proust")
					.price(new BigDecimal("48.90"))
					.title("In Search of Lost Time")
					.build();

			bookRepository.save(book1);
			bookRepository.save(book2);
			bookRepository.save(book3);
		}
	}

}
