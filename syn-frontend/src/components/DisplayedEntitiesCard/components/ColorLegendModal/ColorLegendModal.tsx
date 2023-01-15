import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import {Grid, IconButton, Stack} from "@mui/material";
import ArticleIcon from '@mui/icons-material/Article';

export function ColorLegendModal() {
    const [open, setOpen] = React.useState(false);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <div style={{position: "absolute", left: 2, top: 2}}>
            <IconButton aria-label="info" size="small" onClick={handleClickOpen}>
               <ArticleIcon fontSize="small"/>
            </IconButton>
            <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                   Color legend
                </DialogTitle>
                <DialogContent>
                    <Stack spacing={2}>
                        <ColorItem  color={"#58A55C"} action={"ADD"}/>
                        <ColorItem  color={"#D85040"} action={"DELETE"}/>
                        <ColorItem  color={"#F1BD42"} action={"MODIFY"}/>
                        <ColorItem  color={"#4285F4"} action={"MOVE"}/>
                        <ColorItem  color={"#134BA2"} action={"RENAME"}/>
                    </Stack>
                </DialogContent>
            </Dialog>
        </div>
    );
}

function ColorItem({color, action}: {color: string, action: string}) {
    return <Grid
        container
        direction="row"
        justifyContent="center"
        alignItems="center"
    >
        <Grid xs={4}>
            <span style={{width: 30, borderRadius: 3, backgroundColor: color, marginRight: 1, aspectRatio: "1", display: "inline-block"}}/>
        </Grid>
        <Grid xs={8}>
            {action}
        </Grid>
    </Grid>
}



