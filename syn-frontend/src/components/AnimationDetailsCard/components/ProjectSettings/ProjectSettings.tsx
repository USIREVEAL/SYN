import React from "react";
import {Button, Dialog,} from "@mui/material";
import DebuggerSettingsForm from "../../../DebuggerSettingsForm";
import {useViewDetails} from "../../../../hooks/useViewDetails";
import {DebuggerSettings} from "../../../../providers/ViewProvider/ViewProvider.types";
import {useAnimation} from "../../../../hooks/useAnimation";


export function ProjectSettings() {
    const [settingModalOpened, setSettingModalOpened] = React.useState<boolean>(false);
    const { loading} = useAnimation()

    const {debuggerSettings, updateDebuggerSettings} = useViewDetails();
    const [newDebuggerSettings, setNewDebuggerSettings] = React.useState<DebuggerSettings>(debuggerSettings);

    function closeDialogAndStoreSpecs() {
        setSettingModalOpened(false);
        updateDebuggerSettings(newDebuggerSettings)
    }

    return <>
        <Button disabled={loading} sx={{float: "right"}} onClick={() => setSettingModalOpened(true)}>...</Button>

        <Dialog open={settingModalOpened} onClose={closeDialogAndStoreSpecs} maxWidth={"lg"}>
            <DebuggerSettingsForm debuggerSettings={newDebuggerSettings} updateDebuggerSettings={setNewDebuggerSettings}
                                  onSave={closeDialogAndStoreSpecs}/>
        </Dialog>
    </>
}