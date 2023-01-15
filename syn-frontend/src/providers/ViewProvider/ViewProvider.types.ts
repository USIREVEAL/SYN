import React from "react";
import {View, ViewSpecificationInput} from "../../types/server/View.type";
import {FileTypeMetrics} from "../../types/server/Query.types";

export type ProjectProviderProps = {
    children: React.ReactChildren | React.ReactChildren[] | JSX.Element[]
}

export type DebuggerSettings = ViewSpecificationInput & {
    showVRExperience: boolean,
    showFileHistoryHeight: boolean,
    versionAutoplaySpeed: number,
    showDebugLayer: boolean,
    makeScreenshot: boolean,
    computeShadows: boolean,
    displayedFileTypeMetrics: FileTypeMetrics[]
}

export type ViewProviderContextState = {
    debuggerSettings: DebuggerSettings
    updateDebuggerSettings: (debuggerSettings: DebuggerSettings) => void
    view: View | undefined
    viewLoading: boolean
}

