package com.usi.ch.syn.graphqlserver.typeResolver;

import com.usi.ch.syn.core.model.change.*;
import com.usi.ch.syn.core.model.project.RemoteProject;
import com.usi.ch.syn.core.model.version.ProjectVersion;
import graphql.schema.TypeResolver;
import org.springframework.stereotype.Component;

@Component
public class GraphQLTypeResolver {
    public TypeResolver getProjectTypeResolver() {
        return env -> {
            Object project = env.getObject();
            if (project instanceof RemoteProject) {
                return env.getSchema().getObjectType("RemoteProject");
            } else {
                return env.getSchema().getObjectType("LocalProject");
            }
        };
    }

    public TypeResolver getEntityTypeResolver() {
        return env -> {
            Object project = env.getObject();
            if (project instanceof RemoteProject) {
                return env.getSchema().getObjectType("RemoteProject");
            } else {
                return env.getSchema().getObjectType("LocalProject");
            }
        };
    }

    public TypeResolver getCodeEntityTypeResolver() {
        return env -> {
            Object project = env.getObject();
            if (project instanceof RemoteProject) {
                return env.getSchema().getObjectType("RemoteProject");
            } else {
                return env.getSchema().getObjectType("LocalProject");
            }
        };
    }

    public TypeResolver getVersionTypeResolver() {
        return env -> {
            Object version = env.getObject();
            if (version instanceof ProjectVersion) {
                return env.getSchema().getObjectType("ProjectVersion");
            } else {
                return env.getSchema().getObjectType("FileVersion");
            }
        };
    }

    public TypeResolver getChangeTypeResolver() {
        return env -> {
            Object change = env.getObject();
            if (change instanceof FileAddition) {
                return env.getSchema().getObjectType("FileAddition");
            } else if (change instanceof FileDeletion) {
                return env.getSchema().getObjectType("FileDeletion");
            } else if (change instanceof FileModification) {
                return env.getSchema().getObjectType("FileModification");
            } else if (change instanceof FileMoving) {
                return env.getSchema().getObjectType("FileMoving");
            } else {
                return env.getSchema().getObjectType("FileRenaming");
            }
        };
    }
}
