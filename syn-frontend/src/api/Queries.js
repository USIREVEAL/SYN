import {gql} from "@apollo/client";
import {FigurePosition} from "../types/server/View.type";

export const PROJECT_LIST = gql`
    query ListProject {
        projectList {
            id
            name
        }
    }
`;

export const GET_PROJECT_QUERY = gql`
    query getProject($projectId: Int!) {
        project(projectId: $projectId) {
            id
            name
            metrics {
                name
                value
            }
            path
            ... on RemoteProject {
                projectURL
            }
        }
    }
`;

export const GET_FILE_HISTORY = gql`
    query getProject($projectId: Int!, $fileHistoryId: Int!) {
        fileHistory(projectId: $projectId, fileHistoryId: $fileHistoryId) {
            path
            id
            fileTypes
            name
            fileVersions {
                change {
                    linesAdded
                    linesDeleted
                    ... on FileMoving {
                        fromPath
                        toPath
                    }
                    ... on FileRenaming {
                        fromName
                        toName
                    }
                }
                metrics {
                    name
                    value
                }
                parentProjectVersion {
                    id
                    commitHash
                    timestamp
                }
                id
            }
        }
    }
`;


export const GET_PROJECT_VERSION = gql`
    query projectVersions($projectId: Int!, $projectVersionsId: [Int]!) {
        projectVersions(projectId: $projectId, projectVersionsId: $projectVersionsId) {
            id
            commitHash
            commitMessage
            timestamp
        }
    }
`;

export const GET_VIEW = gql`
    query ComputeView($viewSpecification: ViewSpecificationInput!, $projectId: Int!) {
        view(viewSpecification: $viewSpecification, projectId: $projectId) {
            viewAnimationList {
	            id
                tsFrom
                tsTo
                viewFigures {
                    position {
                        x, y, z
                    },
                    color,
                    height,
                    age,
                    shape,
                    enabled,
                    opacity, 
                    fileHistoryId
                }
                projectVersionIds
            }
        }
    }
`

export const GET_PARTIAL_VIEW = gql`
    query ComputeView($viewSpecification: ViewSpecificationInput!, $projectId: Int!, $viewAnimationId: Int) {
        partialView(viewSpecification: $viewSpecification, projectId: $projectId, viewAnimationId: $viewAnimationId) {
            animationsCount
            viewAnimationList {
                id
                tsFrom
                tsTo
                viewFigures {
                    id
                    fileHistoryId
                    position {
                        x, y, z
                    },
                    color,
                    height,
                    age,
                    shape,
                    opacity,
                    enabled,
                    size
                    
                }
                projectVersionIds
            }
        }
    }
`



// export const SEARCH_FILE_HISTORY = gql`

//         getFileHistoriesByString(projectID: $projectID, fileHistoryString: $fileHistoryString) {
//             id
//             name
//             path
//         }
//     }
// `;

export const GET_FILE_TYPE_COUNTER = gql`
    query getProjectFileTypeCounter($projectId: Int!) {
        fileTypeCounter(projectId: $projectId) {
            fileType
            count
        }
    }
`;

export const GET_GROUPING_PREVIEW = gql`
    query getProjectVersionGroupingCount($projectId: Int!, $viewSpecification: ViewSpecificationInput!) {
        groupingPreview(projectId: $projectId, viewSpecification: $viewSpecification)
    }
`;

export const GET_FILE_TYPE_METRICS = gql`
    query getProjectFileTypeMetrics($projectId: Int!, $fileTypeFilter: [String]) {
        fileTypeMetrics(projectId: $projectId, fileTypeFilter: $fileTypeFilter) {
            fileType
            metrics
        }
    }
`;



