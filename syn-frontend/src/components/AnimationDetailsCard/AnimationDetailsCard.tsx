import {Button, Grid, Paper, Typography} from "@mui/material";
import React, {useContext} from "react";
import ProjectSettings from "./components/ProjectSettings";
import {ProjectContext} from "../../providers/ProjectProvider/ProjectProvider";
import {useAnimation} from "../../hooks/useAnimation";
import {useViewDetails} from "../../hooks/useViewDetails";
import BufferedSlider from "./components/BufferedSlider";
import {ProjectVersionDetails} from "./components/ProjectVersionDetails/ProjectVersionDetails";
import {useDidUpdateEffect} from "../../hooks/useDidUpdateEffect";


export function AnimationDetailsCard() {
    const project = useContext(ProjectContext)
    const {loadAnimation, viewAnimation, loading} = useAnimation()
    const {animationsCount, debuggerSettings} = useViewDetails()

    const [drawingOngoing, setDrawingOngoing] = React.useState(false);
    let updateVersionTimeout: NodeJS.Timeout;

    useDidUpdateEffect(() => {
        if (drawingOngoing && viewAnimation) {
            if (viewAnimation.id  < animationsCount) {
                updateVersionTimeout = setTimeout(() => loadAnimation(viewAnimation.id + 1), debuggerSettings.versionAutoplaySpeed)
            } else {
                setDrawingOngoing(false)
            }
        } else {
            clearTimeout(updateVersionTimeout);
        }
    }, [viewAnimation, drawingOngoing]);

    if (viewAnimation === undefined) return null;

    return <Paper elevation={3} sx={{padding: 2, margin: 2, position: "absolute", width: 300}}>
        <Grid container
              direction="row"
              justifyContent="space-evenly"
              alignItems="center">
            <Grid item xs={12} style={{marginBottom: 10}}>
                <Typography variant={"h6"}>
                    {project.name}
                </Typography>
                <div style={{position: "absolute", right: 0, top: 0}}>
                    <ProjectSettings/>
                </div>
            </Grid>
            <Grid item xs={12}>
                <Typography variant={"h5"} gutterBottom>
                    Animation {viewAnimation.id === -1 ? "-" : viewAnimation.id} {viewAnimation.id === animationsCount && " (end)"}
                </Typography>
            </Grid>
            <ProjectVersionDetails viewAnimation={viewAnimation}/>
            <Grid item xs={10} style={{position: "relative"}}>
                {!loading && <BufferedSlider sliderValue={viewAnimation.id} maxValue={animationsCount}
                                onValueChange={newAnimationId => loadAnimation(newAnimationId)}/> }
                {loading && <>Loading</>}
            </Grid>
            <Grid item xs={10} container
                  direction="row"
                  justifyContent="space-between"
                  alignItems="center"
                  sx={{marginTop: 3}}
            >
                <Grid item xs={4}>
                    <Button
                        disabled={drawingOngoing || viewAnimation.id <= 1 || loading}
                        onClick={() => loadAnimation(viewAnimation.id - 1)}
                    > Previous </Button>
                </Grid>
                <Grid item xs={4}>
                    <Button disabled={viewAnimation.id === animationsCount || loading} onClick={() => setDrawingOngoing(!drawingOngoing)} >
                        {drawingOngoing ? "Pause" : "Play"} </Button>
                </Grid>
                <Grid item xs={4}>
                    <Button
                        disabled={viewAnimation.id >= animationsCount || loading}
                        onClick={() => loadAnimation(viewAnimation.id + 1)}
                    > Next </Button>
                </Grid>
            </Grid>
        </Grid>
    </Paper>
}

    