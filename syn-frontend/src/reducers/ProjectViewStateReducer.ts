import {
    ProjectViewContextAction,
    ProjectViewContextActionType, ProjectViewContextActionValue,
    ProjectViewContextState, ProjectViewSpecs
} from "../types/ProjectContextProviderTypes";


export function projectViewStateReducer(state: ProjectViewContextState, action: ProjectViewContextAction): ProjectViewContextState {


    switch (action.type) {
        case ProjectViewContextActionType.UPDATE_PROJECT_VIEW_SPECS:
            if (isProjectViewSpecs(action.value)) {
                return {...state, projectViewSpecs: action.value};
            }
            break;
        case ProjectViewContextActionType.UPDATE_SELECTED_FILE_HISTORY_ID:
            if (isNumber(action.value) && state.selectedFileHistoryID !== action.value) {
                return {...state, selectedFileHistoryID: action.value};
            }
            break;
        case ProjectViewContextActionType.UPDATE_NUMBER_DISPLAYED_HISTORIES:
            if (isNumber(action.value)) {
                return {...state, numberOfDisplayedHistories: action.value};
            }
            break;
        case ProjectViewContextActionType.UPDATE_HIGHLIGHTED_FILE_HISTORIES:
            if (isNumberArray(action.value)) {
                return {...state, highlightedFileHistoryIDs: action.value};
            }
            break;
        default:
            throw new Error();
    }

    return state;
}


function isProjectViewSpecs(value: ProjectViewContextActionValue): value is ProjectViewSpecs {
    return (value as ProjectViewSpecs).versionAutoplaySpeed !== undefined;
}

function isNumber(value: ProjectViewContextActionValue): value is number {
    return typeof value === "number" && !isNaN(value);
}

function isBoolean(value: ProjectViewContextActionValue): value is boolean {
    return typeof value === "boolean";
}

function isNumberArray(value: ProjectViewContextActionValue): value is number[] {
    return  (value as number[]).length !== undefined;
}




