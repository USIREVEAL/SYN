import React from "react";
import {ProjectProviderProps} from "./ProjectProvider.types";
import {useQuery} from "@apollo/client";
import {Query, QueryProject} from "../../types/server/Query.types";
import {GET_PROJECT_QUERY} from "../../api/Queries";
import {Project} from "../../types/server/Project.types";

const initialProjectState: Project = {
    id: -1,
    metrics: [],
    lastAnalysisTs: 0,
    name: "",
    path: "",
    projectHistory: null,
}

export const ProjectContext = React.createContext<Project>(initialProjectState);


export function ProjectProvider({children, projectId}: ProjectProviderProps) {

    const {data, loading} = useQuery<Query, QueryProject>(
        GET_PROJECT_QUERY,
        {
            variables: {
                projectId: projectId,
            }
        }
    );

    if (loading) {
        console.info('Loading project ' + projectId)
    }

    let project: Project = (data !== undefined) ? data.project : initialProjectState;

    return <ProjectContext.Provider value={project}>
        {children}
    </ProjectContext.Provider>
}

