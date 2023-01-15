import {Link, Menu, MenuItem} from "@mui/material";
import React from "react";
import {ProjectContext} from "../../../../providers/ProjectProvider/ProjectProvider";
import {useEntityDetails} from "../../../../hooks/useEntityDetails";
import {IContextMenu} from "./FileHistoryMenu.types";
import {
    FileHistory,
    FileMoving,
    FileRenaming,
    isMoveChange,
    isRemoteProject,
    isRenameChange
} from "../../../../types/server/Project.types";


export function FileHistoryMenu({
                                    fileHistory,
                                    contextMenu,
                                    setContextMenu
                                }: { fileHistory: FileHistory, contextMenu: IContextMenu, setContextMenu: any }) {
    const projectContext = React.useContext(ProjectContext);
    const {displayedProjectVersions} = useEntityDetails()
    let lastDisplayedProjectVersion = displayedProjectVersions[displayedProjectVersions.length - 1]

    if (!lastDisplayedProjectVersion) return null


    const handleClose = () => {
        setContextMenu(null);
    };

    let entityURL = "";
    if (isRemoteProject(projectContext)) {
        if (projectContext.projectURL.endsWith(".git")) {
            entityURL = projectContext.projectURL.substring(0, projectContext.projectURL.lastIndexOf(".git"))
        } else {
            entityURL = projectContext.projectURL
        }

        let entityPath = fileHistory.path;
        fileHistory.fileVersions.forEach(fileVersion => {
            if (fileVersion.parentProjectVersion.id <= lastDisplayedProjectVersion.id) {
                let change = fileVersion.change;
                if (isRenameChange(change)) {
                    entityPath = (change as FileRenaming).toName
                }
                if (isMoveChange(change)) {
                    entityPath = (change as FileMoving).toPath
                }
            }
        })

        entityURL += "/blob/" + lastDisplayedProjectVersion.commitHash + "/" + entityPath
    }

    return <Menu
        open={contextMenu !== null}
        onClose={handleClose}
        anchorReference="anchorPosition"
        anchorPosition={
            contextMenu !== null
                ? {top: contextMenu.mouseY, left: contextMenu.mouseX}
                : undefined
        }
    >
        <MenuItem>Entity: {fileHistory.id}</MenuItem>
        <MenuItem component={Link} href={entityURL} target={"_blank"}>Jump to source</MenuItem>
    </Menu>
}

export default FileHistoryMenu