import {EntityDetailsAction, EntityDetailsActionType, EntityDetailsContextState} from "./EntityDetailsProvider.types";

export function entityDetailsReducer(state: EntityDetailsContextState, action: EntityDetailsAction): EntityDetailsContextState {
    switch (action.type) {
        case EntityDetailsActionType.UPDATE_FILE_HISTORY_ID:
            return {...state, displayedFileHistoryId: action.value.displayedFileHistoryId!};
        case EntityDetailsActionType.UPDATE_PROJECT_VERSION_IDS:
            return {...state, displayedProjectVersionIds: action.value.displayedProjectVersionIds!};
        default:
            throw new Error();
    }
}




