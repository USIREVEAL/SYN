import {useContext} from "react";
import {ViewContext} from "../providers/ViewProvider/ViewProvider";
import {DebuggerSettings} from "../providers/ViewProvider/ViewProvider.types";


export type ViewDetails = {
    // projectViewState: ProjectViewContextState,
    // projectViewSpecs: ProjectViewSpecs,
    // loadProjectViewSpecs: (projectViewSpecID: number) => void,
    // storeProjectViewSpecs: (projectViewSpecs: ProjectViewSpecs) => void,
    // highlightFileHistory: (fileHistoryID: number) => void,
    // highlightFileHistories: (fileHistoriesID: Array<number>) => void,
    // setDisplayedFileHistoriesCounter: (counterValue: number) => void,
    animationsCount: number,
    maximumNumberOfFigures: number,
    debuggerSettings: DebuggerSettings,
    updateDebuggerSettings: (debuggerSettings: DebuggerSettings) => void
}


export function useViewDetails(): ViewDetails {
    const view = useContext(ViewContext)

    if (!view.view) {
        return {
            animationsCount: 0,
            maximumNumberOfFigures: 0,
            debuggerSettings: view.debuggerSettings,
            updateDebuggerSettings: view.updateDebuggerSettings
        }
    }

    return {
        animationsCount: view.view?.animationsCount,
        maximumNumberOfFigures: 0,
        debuggerSettings: view.debuggerSettings,
        updateDebuggerSettings: view.updateDebuggerSettings
    }

    // const {loadVersion, clearCachedVersions} = useEntityDetails();
    // const dispatch = projectViewContextState.dispatch;
    //
    // function loadProjectViewSpecs(projectViewSpecID: number) {
    //
    // }
    //
    // function storeProjectViewSpecs(newProjectViewSpecs: ProjectViewSpecs) {
    //     let resetProjectVersion = false;
    //     const oldProjectViewSpec = projectViewContextState.projectViewSpecs;
    //     if (oldProjectViewSpec.versionGroupingChunkSize !== newProjectViewSpecs.versionGroupingChunkSize ||
    //         oldProjectViewSpec.versionGroupingStrategy !== newProjectViewSpecs.versionGroupingStrategy)
    //         resetProjectVersion = true
    //
    //     dispatch({type: ProjectViewContextActionType.UPDATE_PROJECT_VIEW_SPECS, value: newProjectViewSpecs})
    //     if (resetProjectVersion) {
    //         clearCachedVersions();
    //         loadVersion(1);
    //     }
    //
    // }
    //
    // function highlightFileHistory(fileHistoryID: number) {
    //     dispatch({type: ProjectViewContextActionType.UPDATE_SELECTED_FILE_HISTORY_ID, value: fileHistoryID})
    // }
    //
    // function highlightFileHistories(fileHistoriesID: Array<number>) {
    //
    // }
    //
    // function setDisplayedFileHistoriesCounter(counterValue: number) {
    //     dispatch({type: ProjectViewContextActionType.UPDATE_NUMBER_DISPLAYED_HISTORIES, value: counterValue})
    // }
    //
    //
    // return {
    //     projectViewState: projectViewContextState,
    //     projectViewSpecs: projectViewContextState.projectViewSpecs,
    //     loadProjectViewSpecs: loadProjectViewSpecs,
    //     storeProjectViewSpecs: storeProjectViewSpecs,
    //     highlightFileHistory: highlightFileHistory,
    //     highlightFileHistories: highlightFileHistories,
    //     setDisplayedFileHistoriesCounter: setDisplayedFileHistoriesCounter,
    // }
}

