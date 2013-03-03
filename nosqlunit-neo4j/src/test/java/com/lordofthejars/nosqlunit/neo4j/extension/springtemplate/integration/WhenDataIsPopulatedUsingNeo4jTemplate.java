package com.lordofthejars.nosqlunit.neo4j.extension.springtemplate.integration;


import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static com.lordofthejars.nosqlunit.neo4j.Neo4jRule.Neo4jRuleBuilder.newNeo4jRule;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lordofthejars.nosqlunit.annotation.CustomInsertionStrategy;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.neo4j.Neo4jRule;
import com.lordofthejars.nosqlunit.neo4j.extension.springtemplate.SpringTemplateInsertionStrategy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="embedded-neo4j-spring-definition.xml")
@CustomInsertionStrategy(insertionStrategy = SpringTemplateInsertionStrategy.class)
public class WhenDataIsPopulatedUsingNeo4jTemplate {

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private GraphDatabaseService graphDatabaseService;
	
	@Rule
	public Neo4jRule neo4jRule = newNeo4jRule().defaultSpringGraphDatabaseServiceNeo4j();

	@Test
	@UsingDataSet(locations="person.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	public void defined_data_should_be_inserted_into_neo4j() {
		
		Neo4jTemplate neo4jTemplate = new Neo4jTemplate(graphDatabaseService);
		EndResult<Person> persons = neo4jTemplate.findAll(Person.class);
		
		Person person = selectFirst(persons, having(on(Person.class).getName(), is("alex")));
		
		assertThat(person, is(new Person("alex")));
		
		Set<Person> friends = person.getFriends();
		assertThat(friends, containsInAnyOrder(new Person("soto")));
		
	}
	
}