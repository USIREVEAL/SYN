import {DebuggerSettings} from "../providers/ViewProvider/ViewProvider.types";

export function getDebuggerSettingsFromLocalStorage(projectID: number): DebuggerSettings | undefined {
    let storedDebuggerSettings = localStorage.getItem(`SYN-${projectID}-settings`);
    if (storedDebuggerSettings) {
        return JSON.parse(storedDebuggerSettings)
    } else {
        return undefined;
    }
}

export function putDebuggerSettingsOnLocalStorage(debuggerSettings: DebuggerSettings, projectID: number) {
    localStorage.setItem(`SYN-${projectID}-settings`, JSON.stringify(debuggerSettings))
}