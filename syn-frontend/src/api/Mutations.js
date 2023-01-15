import {gql} from "@apollo/client";

export const CREATE_PROJECT = gql`
    mutation createProject($projectName: String!, $projectLocation: String!) {
        createProject(projectName: $projectName, projectLocation: $projectLocation) {
            name
            lastAnalysisTs
        }
    }
`;


export const CREATE_PROJECT_VIEW = gql`
    mutation createProjectView($projectId: Int!, $projectViewSettings: ProjectViewSettings) {
        createProjectView(projectID: $projectId, projectViewSettings: $projectViewSettings)
    }
`;
