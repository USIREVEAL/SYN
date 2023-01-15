
import {DebuggerSettings} from "../../providers/ViewProvider/ViewProvider.types";

export type DebuggerSettingsFormProps = {
    debuggerSettings: DebuggerSettings
    updateDebuggerSettings: (debuggerSettings: DebuggerSettings) => void
    onSave: () => void,
    firstStep?: number,
    title?:string
}

export type DebuggerSettingsStepProps = {
    debuggerSettings: DebuggerSettings,
    onChange: (newDebuggerSettings: DebuggerSettings) => void
}
