package GraphQL.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import GraphQL.example.beans.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
