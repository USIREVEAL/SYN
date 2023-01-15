import {DebuggerSettings} from "../providers/ViewProvider/ViewProvider.types";
import {ViewSpecificationInput} from "../types/server/View.type";


export function debuggerSettingToViewSpecification(debuggerSettings: DebuggerSettings | undefined): ViewSpecificationInput | undefined {
    if (debuggerSettings === undefined) return undefined
    return {
        versionGroupingStrategy: debuggerSettings.versionGroupingStrategy,
        versionGroupingChunkSize: debuggerSettings.versionGroupingChunkSize,
        colorPalette: {
            addColor: debuggerSettings.colorPalette.addColor,
            renameColor: debuggerSettings.colorPalette.renameColor,
            moveColor: debuggerSettings.colorPalette.moveColor,
            deleteColor: debuggerSettings.colorPalette.deleteColor,
            modifyColor: debuggerSettings.colorPalette.modifyColor,
            baseColor: debuggerSettings.colorPalette.baseColor
        },
        agingGroupingStrategy: debuggerSettings.agingGroupingStrategy,
        agingStepSize: debuggerSettings.agingStepSize,
        agingSteps: debuggerSettings.agingSteps,
        mapperOptions: {
            maxHeight: debuggerSettings.mapperOptions.maxHeight,
            buckets: debuggerSettings.mapperOptions.buckets
        },
        mapperStrategy: debuggerSettings.mapperStrategy,
        mapperMetricName: debuggerSettings.mapperMetricName,
        figureSize: debuggerSettings.figureSize,
        figureSpacing: debuggerSettings.figureSpacing,
        fileTypeShape: debuggerSettings.fileTypeShape,
        showDeletedEntities: debuggerSettings.showDeletedEntities,
        showUnmappedEntities: debuggerSettings.showUnmappedEntities,
        fileTypeOpacity: debuggerSettings.fileTypeOpacity
    }
}
