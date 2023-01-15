import React from "react";
import {Paper, Table, TableBody, TableCell, TableContainer, TableRow, Typography} from "@mui/material";
import {getChangeDisplayName} from "../../helpers/ChangeHelper";
import LinkToFilHistoryID from "../FileHistoryLink/";
import {useEntityDetails} from "../../hooks/useEntityDetails";

export function ProjectVersionChangelog() {
    // const {displayedProjectVersions} = useEntityDetails()
    // const fileVersions = displayedProjectVersions.flatMap(projectVersion => projectVersion.fileVersions);
    //
    // if (fileVersions === undefined || fileVersions.length === 0) return null
    // return <Paper elevation={3} sx={{padding: 2, margin: 2, position: "absolute", width: 300, top: 350}}>
    //     <Typography variant={"h5"} gutterBottom>
    //         Changelog ({fileVersions.length})
    //     </Typography>
    //     <TableContainer component={Paper} sx={{marginTop: 2, maxHeight: 200}}>
    //         <Table size="small">
    //             <TableBody>
    //                 {fileVersions.map((fileVersion, index) => {
    //                     return <TableRow
    //                         key={index}
    //                         sx={{'&:last-child td, &:last-child th': {border: 0}}}
    //                     >
    //                         <TableCell component="th" scope="row" style={{maxWidth: 100, wordBreak: "break-word"}}>
    //                             <LinkToFilHistoryID fileHistoryID={fileVersion.fileHistory.id}>
    //                                 <span> {fileVersion.fileHistory.name} </span>
    //                             </LinkToFilHistoryID>
    //                         </TableCell>
    //                         <TableCell>
    //                             {getChangeDisplayName(fileVersion.change)}
    //                         </TableCell>
    //                     </TableRow>
    //                 })}
    //             </TableBody>
    //         </Table>
    //     </TableContainer>
    // </Paper>

    return null
}