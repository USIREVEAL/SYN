import React, {useEffect} from "react";
import {GroupingStrategy, MapperStrategyName, View, ViewSpecificationInput} from "../../types/server/View.type";
import {DebuggerSettings, ViewProviderContextState} from "./ViewProvider.types";
import {useQuery} from "@apollo/client";
import {Query, QueryPartialView, QueryView} from "../../types/server/Query.types";
import {GET_PARTIAL_VIEW, GET_VIEW} from "../../api/Queries";
import {useProject} from "../../hooks/useProject";
import {debuggerSettingToViewSpecification} from "../../helpers/ProjectViewSpecHelper";
import {getDebuggerSettingsFromLocalStorage} from "../../helpers/LocalStorageHelper";

export const DEFAULT_INPUT_VIEW_SPECIFICATION: ViewSpecificationInput = {
    versionGroupingStrategy: GroupingStrategy.COMMIT_STRATEGY,
    versionGroupingChunkSize: 1,
    colorPalette: {
        addColor: "#58A55C",
        renameColor: "#134BA2",
        moveColor: "#4285F4",
        deleteColor: "#D85040",
        modifyColor: "#F1BD42",
        baseColor: "#808080"
    },
    agingGroupingStrategy: GroupingStrategy.COMMIT_STRATEGY,
    agingStepSize: 1,
    agingSteps: 2,
    mapperOptions: {
        maxHeight: 20,
        buckets: 100
    },
    mapperStrategy: MapperStrategyName.BUCKET_VALUE_STRATEGY,
    mapperMetricName: "SIZE",
    figureSize: 5,
    figureSpacing: 5,
    fileTypeShape: [],
    showDeletedEntities: false,
    showUnmappedEntities: true,
    fileTypeOpacity: []
}

export const DEFAULT_SETTINGS: DebuggerSettings = {
    ...DEFAULT_INPUT_VIEW_SPECIFICATION,
    showVRExperience: false,
    showFileHistoryHeight: true,
    versionAutoplaySpeed: 500,
    showDebugLayer: false,
    displayedFileTypeMetrics:  [
        {
            fileType: "BINARY",
            metrics: ["SIZE"]
        }, {
            fileType: "TEXT",
            metrics: ["SIZE"]
        }, {
            fileType: "JAVA",
            metrics: ["SLOC"]
        }
    ],
    makeScreenshot: false,
    computeShadows: false
}

export const ViewContext = React.createContext<ViewProviderContextState>({
    debuggerSettings: DEFAULT_SETTINGS,
    updateDebuggerSettings: () => {},
    view: undefined,
    viewLoading: false
});

export function ViewProvider({children}: any) {
    const project = useProject();
    const [debuggerSettings, setDebuggerSettings] = React.useState<DebuggerSettings | undefined>(() => getDebuggerSettingsFromLocalStorage(project.id))
    const [view, setView] = React.useState<View | undefined>(undefined);

    useEffect(() => {

    }, [debuggerSettings])

    function updateDebuggerSettings(debuggerSettings: DebuggerSettings) {
        setDebuggerSettings(debuggerSettings)
        setView(undefined)
    }


    useEffect(() => {
        if (project.id !== -1)
            setDebuggerSettings(getDebuggerSettingsFromLocalStorage(project.id))
    }, [project])

    const {loading} = useQuery<Query, QueryPartialView>(GET_PARTIAL_VIEW, {
        variables: {
            projectId: project.id,
            viewSpecification: debuggerSettingToViewSpecification(debuggerSettings),
            viewAnimationId: 0
        },
        skip: project.id === -1 || view !== undefined || debuggerSettings === undefined,
        onCompleted: (d) => {
            console.log(d)
            setView(d!.partialView!)
        },
        fetchPolicy: 'no-cache'
    });

    let viewContext: ViewProviderContextState = {
        view: view,
        debuggerSettings: debuggerSettings ?? DEFAULT_SETTINGS ,
        updateDebuggerSettings: updateDebuggerSettings,
        viewLoading: loading
    }

    return <ViewContext.Provider value={viewContext}>
        {children}
    </ViewContext.Provider>
}



