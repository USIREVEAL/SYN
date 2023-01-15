package com.usi.ch.syn.graphqlserver.provider;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.graphqlserver.dataFetcher.GraphQLDataFetchers;
import com.usi.ch.syn.graphqlserver.typeResolver.GraphQLTypeResolver;
import graphql.GraphQL;
import graphql.TypeResolutionEnvironment;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {

    private GraphQL graphQL;

    @Setter(onMethod = @__({@Autowired}))
    private GraphQLDataFetchers graphQLDataFetchers;


    @Setter(onMethod = @__({@Autowired}))
    private GraphQLTypeResolver graphQLTypeResolvers;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        String entitySchema = loadSchema("graphql/entity.graphqls");
        String querySchema = loadSchema("graphql/query.graphqls");
        String projectSchema = loadSchema("graphql/project.graphqls");
        String viewSchema = loadSchema("graphql/view.graphqls");
        String mutationSchema = loadSchema("graphql/mutation.graphqls");
        GraphQLSchema graphQLSchema = buildSchema(entitySchema, querySchema, projectSchema, viewSchema, mutationSchema);

        this.graphQL = GraphQL
                .newGraphQL(graphQLSchema)
//                .instrumentation(new TracingInstrumentation())
                .build();
    }

    private String loadSchema(String fileName) throws IOException {
        URL url = Resources.getResource(fileName);
        return Resources.toString(url, Charsets.UTF_8);
    }

    private GraphQLSchema buildSchema(String... slds) {
        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        SchemaParser schemaParser = new SchemaParser();

        for (String sld : slds) {
            typeRegistry.merge(schemaParser.parse(sld));
        }

        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {

        return RuntimeWiring.newRuntimeWiring()
            .type("Query", typeWiring -> typeWiring
                    .dataFetcher("project", graphQLDataFetchers.getProjectByIdDataFetcher())
                    .dataFetcher("projectList", graphQLDataFetchers.getProjectListDataFetcher())
                    .dataFetcher("view", graphQLDataFetchers.getViewDataFetcher())
                    .dataFetcher("partialView", graphQLDataFetchers.getPartialViewDataFetcher())
                    .dataFetcher("fileHistory", graphQLDataFetchers.getFileHistoryDataFetcher())
                    .dataFetcher("projectVersion", graphQLDataFetchers.getProjectVersionDataFetcher())
                    .dataFetcher("projectVersions", graphQLDataFetchers.getProjectVersionsDataFetcher())
                    .dataFetcher("groupingPreview", graphQLDataFetchers.getGroupingPreviewDataFetcher())
                    .dataFetcher("fileTypeCounter", graphQLDataFetchers.getFileTypeCounterDataFetcher())
                    .dataFetcher("fileTypeMetrics", graphQLDataFetchers.getFileTypeMetricsDataFetcher())
            )
            .type("Mutation", typeWiring -> typeWiring
                    .dataFetcher("createProject", graphQLDataFetchers.createNewProject()))
            .type("Project", typeWiring -> typeWiring
                    .dataFetcher("fileHistory", graphQLDataFetchers.getProjectHistoryDataFetcher())
                    .typeResolver(graphQLTypeResolvers.getProjectTypeResolver()))
            .type("View", typeWiring -> typeWiring
                    .dataFetcher("viewAnimationList", graphQLDataFetchers.getViewAnimationListDataFetcher()))
            .type("Entity", typeWiring -> typeWiring
                    .typeResolver(graphQLTypeResolvers.getEntityTypeResolver()))
            .type("CodeEntity", typeWiring -> typeWiring
                    .typeResolver(graphQLTypeResolvers.getCodeEntityTypeResolver()))
            .type("Version", typeWiring -> typeWiring
                    .typeResolver(graphQLTypeResolvers.getVersionTypeResolver()))
            .type("Change", typeWiring -> typeWiring
                    .typeResolver(graphQLTypeResolvers.getChangeTypeResolver()))
            .build();
    }
}