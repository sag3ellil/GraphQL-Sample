package GraphQL.example.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import GraphQL.example.beans.Author;
import GraphQL.example.repositories.AuthorRepository;
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
public class AuthorController {

	@Autowired
	private AuthorRepository authorRepo;
	
	private GraphQL graphQL;
	
	//read the file graphqls
	@Value("classpath:author.graphqls")
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
		DataFetcher<List<Author>> fetcher1 = data -> {
			return (List<Author>) authorRepo.findAll();
		};

		DataFetcher<Long> fetcher2 = data -> {
			return (Long) authorRepo.count();
		};
		
		DataFetcher<Author> fetcher3 =data-> {
			String name = data.getArgument("name");
			Integer age = data.getArgument("age");
			
			return authorRepo.save(new Author(name,age));
		};
		return RuntimeWiring.newRuntimeWiring()
				.type("Query", typeWriting -> typeWriting.dataFetcher("findAllAuthors", fetcher1).dataFetcher("countAuthors", fetcher2))
				.type("Mutation",typeWriting -> typeWriting.dataFetcher("createAuthor",fetcher3))
				.build();
	}

	@GetMapping(path = "author/{id}")
	public Author getAuthor(@PathVariable("id") Long id) {
		return authorRepo.getReferenceById(id);
	}
	
	
	@PostMapping("/author")
	public ResponseEntity<Object> createAuthor(@RequestBody String query)
	{
		ExecutionResult result = graphQL.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
}
