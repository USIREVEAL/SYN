import React, {useReducer} from "react";
import {
    EntityDetailsContextState,
    EntityDetailsProviderTypes,
    EntityDetailsReducer
} from "./EntityDetailsProvider.types";
import {entityDetailsReducer} from "./EntityDetailsReducer";
import {useQuery} from "@apollo/client";
import {Query, QueryFileHistory, QueryProjectVersions} from "../../types/server/Query.types";
import {GET_FILE_HISTORY, GET_PROJECT_VERSION} from "../../api/Queries";
import {useProject} from "../../hooks/useProject";


const initialState: EntityDetailsContextState = {
    displayedProjectVersionIds: [],
    displayedFileHistoryId: -1,
    displayedProjectVersions: [],
    displayedFileHistory: {
        id: -1,
        metrics: [],
        name: "",
        path: "",
        fileTypes: [],
        fileVersions: [],
        aliases: []
    },
    dispatch: (prevState) => prevState
}

export const EntityDetailsContext = React.createContext<EntityDetailsContextState>(initialState);

export function EntityDetailsProvider({children}: EntityDetailsProviderTypes) {
    const project = useProject();
    const [projectVersionState, dispatch] = useReducer<EntityDetailsReducer>(entityDetailsReducer, initialState);
    const ProjectViewProviderState: EntityDetailsContextState = {...projectVersionState, dispatch: dispatch};

    const {data: projectVersionQueryData} = useQuery<Query, QueryProjectVersions>(GET_PROJECT_VERSION, {
        variables: {
            projectId: project.id,
            projectVersionsId: projectVersionState.displayedProjectVersionIds
        },
        skip: project.id <= 0 || projectVersionState.displayedProjectVersionIds.length === 0
    })

    const {data: fileHistoryQueryData} = useQuery<Query, QueryFileHistory>(GET_FILE_HISTORY, {
        variables: {
            projectId: project.id,
            fileHistoryId: projectVersionState.displayedFileHistoryId
        },
        skip: project.id <= 0|| projectVersionState.displayedFileHistoryId <= 0
    })

    let entityDetailsState = {...ProjectViewProviderState};

    if (projectVersionQueryData !== undefined) {
        entityDetailsState.displayedProjectVersions = projectVersionQueryData.projectVersions
    }

    if (fileHistoryQueryData !== undefined) {
        entityDetailsState.displayedFileHistory = fileHistoryQueryData.fileHistory
    }

    return <EntityDetailsContext.Provider value={entityDetailsState}>
        {children}
    </EntityDetailsContext.Provider>
}

