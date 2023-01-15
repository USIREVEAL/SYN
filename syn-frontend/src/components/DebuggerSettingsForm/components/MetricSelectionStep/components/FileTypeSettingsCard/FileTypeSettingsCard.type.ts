import {DebuggerSettingsStepProps} from "../../../../DebuggerSettingsForm.types";
import {FileTypeMetrics} from "../../../../../../types/server/Query.types";

export type FileTypeMetricCardTypeArgs = DebuggerSettingsStepProps & {
    fileType: string
    serverFileTypeMetrics: FileTypeMetrics
}
