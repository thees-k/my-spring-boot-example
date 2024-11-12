package k.thees.myspringbootexample.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import k.thees.myspringbootexample.entities.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
