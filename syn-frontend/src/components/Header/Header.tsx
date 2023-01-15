import {AppBar, Typography} from "@mui/material";
import React from "react";

export function Header() {
    return <AppBar position="static">
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            SYN Visual Inspector
        </Typography>
    </AppBar>
}
