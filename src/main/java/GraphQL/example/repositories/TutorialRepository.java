package GraphQL.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import GraphQL.example.beans.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {

}
