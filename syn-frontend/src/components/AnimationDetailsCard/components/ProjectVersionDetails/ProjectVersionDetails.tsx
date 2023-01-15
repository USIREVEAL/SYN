import React from "react";
import {isRemoteProject, ProjectVersion} from "../../../../types/server/Project.types";
import {Button, Dialog, DialogTitle, Grid, IconButton, List, ListItem, ListItemText} from "@mui/material";
import {useEntityDetails} from "../../../../hooks/useEntityDetails";
import GitHubIcon from '@mui/icons-material/GitHub';
import {useProject} from "../../../../hooks/useProject";
import {ViewAnimation} from "../../../../types/server/View.type";

export function ProjectVersionDetails({viewAnimation}: {viewAnimation: ViewAnimation}) {
    const {displayedProjectVersions} = useEntityDetails();
    const [dialogOpen, setDialogOpen] = React.useState(false);

    const tsFrom = new Date(viewAnimation.tsFrom * 1000);
    const tsTo = new Date(viewAnimation.tsTo * 1000);
    return <>
        <Grid item xs={12} style={{marginBottom: 20}} container justifyContent={"center"} spacing={1}>
            <Grid item xs={2} style={{textAlign: "right"}}>
                {tsTo !== tsFrom ? "From" : "Date"}
            </Grid>
            <Grid item xs={10}>
                {tsFrom.toUTCString().substring(0, tsFrom.toUTCString().length - 3)}
            </Grid>
            {tsTo !== tsFrom && <>
				<Grid item xs={2} style={{textAlign: "right"}}>
					To
				</Grid>
				<Grid item xs={10}>
                    {tsTo.toUTCString().substring(0, tsTo.toUTCString().length - 3)}
				</Grid>
			</>}
        </Grid>
        <Grid item xs={12} style={{marginBottom: 20}} container spacing={4}>
            <Grid item xs={6} style={{
                textAlign: "right",
                display: "flex",
                alignItems: "center",
                justifyContent: "center"
            }}>
                <span>Commits: {displayedProjectVersions.length}</span>
            </Grid>

            <Grid item xs={6} style={{textAlign: "right"}}>
                <Button variant="outlined" onClick={() => setDialogOpen(true)} style={{width: "100%"}}>
                    List
                </Button>
                <ProjectVersionsDialog
                    open={dialogOpen}
                    onClose={() => setDialogOpen(false)}
                />
            </Grid>
        </Grid>
    </>
}

function ProjectVersionsDialog(props: { open: boolean; onClose: () => void }) {
    const {displayedProjectVersions} = useEntityDetails();
    const project = useProject()

    function openOnGithub(projectVersion: ProjectVersion) {

        if (isRemoteProject(project)) {
            let entityURL = "";
            if (project.projectURL.endsWith(".git")) {
                entityURL = project.projectURL.substring(0, project.projectURL.lastIndexOf(".git"))
            } else {
                entityURL = project.projectURL
            }
            entityURL += "/commit/" + projectVersion.commitHash

            window.open(
                entityURL, "_blank");
        } else {
            alert("Unable to retrieve remote from this project")
        }
    }


    return (
        <Dialog open={props.open} onClose={props.onClose}>
            <DialogTitle>Commits inside this animation</DialogTitle>
            <List dense={true}>
                {displayedProjectVersions.map((projectVersion: ProjectVersion) => {
                    return <ListItem key={projectVersion.id} secondaryAction={
                        <IconButton edge="end" aria-label="comments" onClick={() => openOnGithub(projectVersion)}>
                            <GitHubIcon/>
                        </IconButton>
                    }>
                        <ListItemText
                            primary={projectVersion.commitMessage}
                            secondary={projectVersion.commitHash}
                        />
                    </ListItem>
                })}
            </List>
        </Dialog>
    );
}

