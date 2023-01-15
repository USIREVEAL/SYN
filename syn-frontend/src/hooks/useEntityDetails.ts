import {useContext} from "react";
import {EntityDetailsContext} from "../providers/EntityDetailsProvider/EntityDetailsProvider";
import {EntityDetailsActionType} from "../providers/EntityDetailsProvider/EntityDetailsProvider.types";
import {FileHistory, ProjectVersion} from "../types/server/Project.types";

export type EntityDetails = {
    displayedFileHistory: FileHistory,
    displayedProjectVersions: ProjectVersion[],
    loadFileHistory: (fileHistoryId: number) => void,
    loadProjectVersions: (projectVersionIds: number[]) => void
}


export function useEntityDetails(): EntityDetails {
    const {displayedFileHistory, displayedProjectVersions, dispatch} = useContext(EntityDetailsContext);

    return {
        displayedFileHistory: displayedFileHistory,
        displayedProjectVersions: displayedProjectVersions,
        loadFileHistory: (fileHistoryId: number) => {
            dispatch({
                type: EntityDetailsActionType.UPDATE_FILE_HISTORY_ID,
                value: {
                    displayedFileHistoryId: fileHistoryId
                }
            })
        },
        loadProjectVersions: (projectVersionIds: number[]) => {
            dispatch({
                type: EntityDetailsActionType.UPDATE_PROJECT_VERSION_IDS,
                value: {
                    displayedProjectVersionIds: projectVersionIds
                }
            })
        }
    }

}