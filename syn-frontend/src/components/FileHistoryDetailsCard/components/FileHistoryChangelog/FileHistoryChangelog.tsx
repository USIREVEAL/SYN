import {
    Button,
    Divider,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Tooltip,
} from "@mui/material";
import React from "react";
import "./style.css"
import {getChangeDisplayName} from "../../../../helpers/ChangeHelper";
import {useEntityDetails} from "../../../../hooks/useEntityDetails";
import {
    FileHistory,
    FileMoving,
    FileRenaming,
    FileVersion,
    isMoveChange, isRemoteProject,
    isRenameChange
} from "../../../../types/server/Project.types";
import {ProjectContext} from "../../../../providers/ProjectProvider/ProjectProvider";
import {useAnimation} from "../../../../hooks/useAnimation";


export function FileHistoryChangelog({fileHistory}: { fileHistory: FileHistory }) {
    const projectContext = React.useContext(ProjectContext);
    const {displayedProjectVersions} = useEntityDetails();
    const {viewAnimation} = useAnimation();
    const lastDisplayedProjectVersion = displayedProjectVersions[displayedProjectVersions.length - 1]
    const displayedVersions = lastDisplayedProjectVersion ? fileHistory.fileVersions.filter(v => v.parentProjectVersion.id <= lastDisplayedProjectVersion.id) : [];
    const entityMetrics = displayedVersions.length > 0 ? displayedVersions[displayedVersions.length - 1].metrics : undefined;
    const displayedVersionsIDs = displayedVersions.map(fv => fv.id);

    let projectURL = "";
    if (isRemoteProject(projectContext)) {
        if (projectContext.projectURL.endsWith(".git")) {
            projectURL = projectContext.projectURL.substring(0, projectContext.projectURL.lastIndexOf(".git"))
        } else {
            projectURL = projectContext.projectURL
        }
    }


    return <>


        <span>Path: {getCurrentFilePath()}</span> <br/>
        <span>Age: {getCurrentAge()}</span>


        {entityMetrics && <Table size="small" sx={{marginTop: 2}}>
		    <TableBody>
                {entityMetrics.map((metric, index) => {
                    return <TableRow
                        key={index}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                    >
                        <TableCell component="th" scope="row">
                            {metric.name}
                        </TableCell>
                        <TableCell>
                            {metric.value}
                        </TableCell>
                    </TableRow>
                })}


		    </TableBody>
	    </Table>}

        <TableContainer component={Paper} sx={{marginTop: 1, maxHeight: 200}}>
            <Table size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>Version</TableCell>
                        <TableCell>Action</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <EntityHistoryRows/>
                </TableBody>
            </Table>
        </TableContainer>
    </>

    function fileVersionWasDisplayed(fileVersionID: number): boolean {
        for (let i = 0; i < displayedVersionsIDs.length; i++) {
            if (displayedVersionsIDs[i] === fileVersionID)
                return true;
        }
        return false;
    }

    function getCurrentFilePath() {
        let path = "";
        const displayedProjectVersion = lastDisplayedProjectVersion ? lastDisplayedProjectVersion.id : -1;
        fileHistory.fileVersions.forEach(fv => {
            if (path === "") {
                if (isRenameChange(fv.change)) {
                    path = fv.change.fromName;
                } else if (isMoveChange(fv.change)) {
                    path = fv.change.fromPath;
                }
            }

            if (path !== "" && fv.parentProjectVersion.id <= displayedProjectVersion) {
                if (isRenameChange(fv.change)) {
                    path = fv.change.toName;
                } else if (isMoveChange(fv.change)) {
                    path = fv.change.toPath;
                }
            }
        });

        if (path === "") {
            path = fileHistory.path;
        }

        return path;

    }

    function getCurrentAge(): number{
        if (viewAnimation !== undefined) {
            const viewFigure = viewAnimation.viewFigures.find(vf => vf.fileHistoryId === fileHistory.id);
            if (viewFigure !== undefined) {
                return viewFigure.age;
            }
        }
        return 0;
    }


    function EntityHistoryRows(): any {
        if (fileHistory.fileVersions !== undefined) {


            return fileHistory.fileVersions
                .map(fileVersion => {

                    let style = {}
                    if (!fileVersionWasDisplayed(fileVersion.id)) {
                        style = {backgroundColor: "#000"}
                    }

                    const date = new Date(fileVersion.parentProjectVersion.timestamp * 1000)

                    return <TableRow
                        key={fileVersion.id}
                        sx={{'&:last-child td, &:last-child th': {border: 0}}}
                        style={style}
                    >
                        <TableCell component="th" scope="row">
                            <a href={projectURL + "/commit/" + fileVersion.parentProjectVersion.commitHash} target={"_blank"} style={{color: "white", textDecoration: "none"}}>
                                {date.toUTCString().substring(4, date.toUTCString().length - 12)} ({fileVersion.parentProjectVersion.commitHash.substring(0, 4)}...)</a>


                        </TableCell>
                        <TableCell>
                            <FileVersionTooltip fileVersion={fileVersion}/>
                        </TableCell>
                    </TableRow>
                })
        }
        return null
    }

    function FileVersionTooltip({fileVersion}: { fileVersion: FileVersion }) {
        const change = fileVersion.change;
        return <div>
            <Tooltip title={<React.Fragment>
                {change.linesAdded + change.linesDeleted} changes: {change.linesAdded} additions & {change.linesDeleted} deletions
                <p>
                    <Divider/>
                </p>

                {isRenameChange(change) &&
					<div className={"rename_changelog_container"}>
                        {(change as FileRenaming).fromName} <br/> &darr;  <br/> {(change as FileRenaming).toName}
					</div>
                }
                {isMoveChange(change) &&
					<div className={"rename_changelog_container"}>
                        {(change as FileMoving).fromPath} <br/> &darr;  <br/> {(change as FileMoving).toPath}
					</div>
                }
            </React.Fragment>} placement="top">
                <Button className={"rename_btn"}> {getChangeDisplayName(fileVersion.change)} </Button>
            </Tooltip>

        </div>
    }
}




