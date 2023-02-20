package GraphQL.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import GraphQL.example.beans.Author;
import GraphQL.example.beans.Tutorial;
import GraphQL.example.repositories.AuthorRepository;
import GraphQL.example.repositories.TutorialRepository;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.PostConstruct;

@RestController
public class TutorialController {
	@Autowired
	private TutorialRepository tutorialRepo;
	@Autowired
	private AuthorRepository authorRepo;
	private GraphQL graphQL;
	
	//read the file graphqls
	@Value("classpath:tutorial.graphqls")
	private Resource schemaResource;
	
	//download the graphqls file content and build it 
	@PostConstruct
	public void loadSchema() throws IOException {
		File schemaFile = schemaResource.getFile();
		TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
		RuntimeWiring wiring = buildWiring();
		GraphQLSchema schema= new SchemaGenerator().makeExecutableSchema(registry, wiring);
		graphQL = GraphQL.newGraphQL(schema).build();
	}
	
	//define the methods that exist on the file
	private RuntimeWiring buildWiring() {
		DataFetcher<List<Tutorial>> fetcher1 = data -> {
			return (List<Tutorial>) tutorialRepo.findAll();
		};

		DataFetcher<Long> fetcher2 = data -> {
			return (Long) tutorialRepo.count();
		};
		
		DataFetcher<Tutorial> createTutorial = data -> {
			 String title= data.getArgument("title");
			 String description= data.getArgument("description");
			 Integer author= data.getArgument("author");
//				Author au=	 authorRepo.findById(Long.valueOf(author)).orElseThrow( ()-> new Exception("author with id : "+author+" not found"));
			return  tutorialRepo.save(new Tutorial(title,description,Long.valueOf(author)));
		};
		
		
		DataFetcher<String> deleteTutorial = data -> {
			 Integer id= data.getArgument("id");
			 tutorialRepo.deleteById(Long.valueOf(id));
			 return "Deleted !";
		};
		return RuntimeWiring.newRuntimeWiring()
				 .type("Query", typeWriting -> typeWriting
				 .dataFetcher("findAllTutorials", fetcher1).dataFetcher("countTutorials", fetcher2))
				 .type("Mutation", typeWriting -> typeWriting
							.dataFetcher("createTutorial", createTutorial)
				 .dataFetcher("updateTutorial", createTutorial)
				 .dataFetcher("deleteTutorial", deleteTutorial))
				 .build();
	}
	
	@PostMapping("/tutorials")
	public ResponseEntity<Object> getAll(@RequestBody String query)
	{
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
}
