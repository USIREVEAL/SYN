import React from "react";
import {useParams} from "react-router-dom";
import ProjectVersionChangelog from "../../components/ProjectVersionChangelog";
import DisplayedEntitiesCard from "../../components/DisplayedEntitiesCard";
import AnimationDetailsCard from "../../components/AnimationDetailsCard";
import FileHistoryDetailsCard from "../../components/FileHistoryDetailsCard";


import Project3DVisualizer from "../../components/Project3DVisualizer";
import SearchFileHistoryCard from "../../components/SearchFileHistoryCard";
import DebuggerProvider from "../../providers/DebuggerProvider";
import {getDebuggerSettingsFromLocalStorage, putDebuggerSettingsOnLocalStorage} from "../../helpers/LocalStorageHelper";
import DebuggerSettingsForm from "../../components/DebuggerSettingsForm";
import {DebuggerSettings} from "../../providers/ViewProvider/ViewProvider.types";
import {DEFAULT_SETTINGS} from "../../providers/ViewProvider/ViewProvider";
import { useNavigate } from "react-router-dom";

export function ProjectView() {
    const {projectID} = useParams<string>()
    if (projectID === undefined) return <>Missing projectID</>

    const projectIDNumeric = parseInt(projectID)
    if (isNaN(projectIDNumeric)) return <>Malformed projectID</>



    const debuggerSettings = getDebuggerSettingsFromLocalStorage(projectIDNumeric)
    if (debuggerSettings === undefined) {
        return <InitialSetup  projectID={projectIDNumeric} />
    }


    return <DebuggerProvider projectId={projectIDNumeric}>
        <AnimationDetailsCard/>
        <ProjectVersionChangelog/>
        <DisplayedEntitiesCard/>
        <FileHistoryDetailsCard/>
        <Project3DVisualizer/>
        <SearchFileHistoryCard/>
    </DebuggerProvider>
}

function InitialSetup({projectID}: {projectID: number}) {
    const [newDebuggerSettings, setNewDebuggerSettings] = React.useState<DebuggerSettings>(DEFAULT_SETTINGS);
    const history = useNavigate();

    function onSave() {
        putDebuggerSettingsOnLocalStorage(newDebuggerSettings, projectID);
        history(0)
    }

    return <DebuggerProvider projectId={projectID}>
        <DebuggerSettingsForm debuggerSettings={newDebuggerSettings} updateDebuggerSettings={setNewDebuggerSettings}
                          onSave={onSave} firstStep={0} title={"Initial setup"}/>
    </DebuggerProvider>

}


