# GraphQL-Sample 

# env :

Maven 3.8.7

Java 11

# description:

It's a small sample where we use graphQL to control the fields to be returned on the results 

# how to test

download the project and open cmd on the project folder: write mvn install then :

java -jar pathofthefolder\newAccountBank\target\example-0.0.1-SNAPSHOT.jar GraphQL.example

# Rest end point:

curl "http://localhost:8080/tutorials"  -X POST   -H "content-type: text/plain"   --data " { findAllTutorials { id title description author } } "

curl "http://localhost:8080/tutorials"  -X POST   -H "content-type: text/plain"   --data " mutation { createTutorial (title : \"csdzdzc\" , description : \"scsc\" , author : 1 ) { id title description author } } "
