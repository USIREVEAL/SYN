import React from "react";
import {FileHistory, ProjectVersion} from "../../types/server/Project.types";

export type EntityDetailsProviderTypes = {
    children: React.ReactChildren | React.ReactChildren[] | JSX.Element[] | JSX.Element
}

export type EntityDetailsContextState = {
    displayedProjectVersions: ProjectVersion[]
    displayedFileHistory: FileHistory
    displayedProjectVersionIds: number[]
    displayedFileHistoryId: number
    dispatch: React.Dispatch<EntityDetailsAction>
}

export type EntityDetailsActionValue = {
    displayedProjectVersionIds?: number[]
    displayedFileHistoryId?: number
}

export interface EntityDetailsAction {
    type: EntityDetailsActionType
    value: EntityDetailsActionValue
}

export enum EntityDetailsActionType {
    UPDATE_PROJECT_VERSION_IDS,
    UPDATE_FILE_HISTORY_ID
}

export interface EntityDetailsReducer {
    (prevState: EntityDetailsContextState, action: EntityDetailsAction): EntityDetailsContextState
}
