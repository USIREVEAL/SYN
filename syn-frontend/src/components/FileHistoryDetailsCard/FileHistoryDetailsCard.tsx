import React from "react";
import {Paper, Typography,} from "@mui/material";
import FileHistoryChangelog from "./components/FileHistoryChangelog";
import FileHistoryMenu from "./components/FileHistoryMenu";
import {IContextMenu} from "./components/FileHistoryMenu/FileHistoryMenu.types";
import {useEntityDetails} from "../../hooks/useEntityDetails";


export function FileHistoryDetailsCard() {
    const {displayedFileHistory} = useEntityDetails();
    const [contextMenu, setContextMenu] = React.useState<IContextMenu>(null);

    if (displayedFileHistory.id === -1) return null

    function handleContextMenuOpen(event: any) {
        event.preventDefault();
        setContextMenu(
            contextMenu === null
                ? {
                    mouseX: event.clientX - 2,
                    mouseY: event.clientY - 4,
                }
                : null,
        );
    }


    return <div onContextMenu={handleContextMenuOpen}>
        <Paper elevation={3} sx={{padding: 2, margin: 2, position: "absolute", minWidth: 300, right: 0}}>
            <Typography variant={"h5"} gutterBottom>
                {displayedFileHistory.name}
            </Typography>
            <FileHistoryChangelog fileHistory={displayedFileHistory}/>

            <FileHistoryMenu fileHistory={displayedFileHistory} contextMenu={contextMenu}
                             setContextMenu={setContextMenu}/>
        </Paper>
    </div>
}
