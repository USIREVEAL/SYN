import React from "react";
import {ViewSpecificationInput} from "./server/View.type";

export interface ProjectViewContextState {
    selectedFileHistoryID: number
    projectViewSpecs: ProjectViewSpecs
    dispatch: React.Dispatch<ProjectViewContextAction>
    numberOfDisplayedHistories: number
    highlightedFileHistoryIDs: number[]
}

export type ProjectViewSpecs = ViewSpecificationInput & {
    showDifferentShape: boolean
    showFileHistoryHeight: boolean
    showDeletedFileHistories: boolean
    versionAutoplaySpeed: number
    showDebugLayer: boolean
    blockPositionStrategy: BlockPositionStrategy
}

export enum BlockPositionStrategy {
    MATRIX = "MATRIX",
    OUTWARD_SPIRAL = "OUTWARD_SPIRAL"
}

export interface ProjectViewContextAction {
    type: ProjectViewContextActionType
    value: ProjectViewContextActionValue
}

export type ProjectViewContextActionValue = ProjectViewSpecs | number | boolean | string | number[]


export interface ProjectViewContextReducer {
    (prevState: ProjectViewContextState, action: ProjectViewContextAction): ProjectViewContextState
}

export enum ProjectViewContextActionType {
    UPDATE_PROJECT_VIEW_SPECS,
    UPDATE_SELECTED_FILE_HISTORY_ID,
    UPDATE_NUMBER_DISPLAYED_HISTORIES,
    UPDATE_HIGHLIGHTED_FILE_HISTORIES
}

export enum EntityShape {
    CYLINDER,
    CONE,
    TRIANGULAR_PRISM,
    BOX
}