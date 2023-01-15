import {useContext} from "react";
import {AnimationViewContext} from "../providers/AnimationProvider/AnimationProvider";
import {AnimationProviderContextState} from "../providers/AnimationProvider/AnimationProvider.types";


export function useAnimation(): AnimationProviderContextState {
    const animationProviderContextState = useContext(AnimationViewContext)
    return animationProviderContextState
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

