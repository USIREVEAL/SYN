import {Paper, Typography} from "@mui/material";
import React from "react";
import {useAnimation} from "../../hooks/useAnimation";

export function DisplayedEntitiesCard() {

    const animation = useAnimation()

    return <Paper elevation={3} sx={{padding: 2, margin: 2, position: "absolute", width: 300, bottom: 10}}>
        <Typography variant={"h5"} gutterBottom>
            Displayed entities: {animation && animation.viewAnimation ? animation.viewAnimation.viewFigures.length - 1 : "-"}
        </Typography>
    </Paper>
}