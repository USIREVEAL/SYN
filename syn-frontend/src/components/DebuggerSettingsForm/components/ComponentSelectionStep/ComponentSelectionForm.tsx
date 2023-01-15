import React from "react";
import {Button, Container, Grid, Paper, Typography} from "@mui/material";
import {DebuggerSettingsStepProps} from "../../DebuggerSettingsForm.types";
import {useProject} from "../../../../hooks/useProject";
import {useQuery} from "@apollo/client";
import {FileTypeCounter, FileTypeMetrics, Query, QueryFileTypeCounter} from "../../../../types/server/Query.types";
import {GET_FILE_TYPE_COUNTER} from "../../../../api/Queries";


export function ComponentSelectionForm({debuggerSettings, onChange}: DebuggerSettingsStepProps) {
    const selectedTags = getSelectedTags()
    const project = useProject()
    const [showMore, setShowMore] = React.useState(false)

    const {data, loading} = useQuery<Query, QueryFileTypeCounter>(GET_FILE_TYPE_COUNTER, {
        variables: {
            projectId: project.id
        }
    })

    if (loading || data === undefined) return null;

    const topTags = new Array<FileTypeCounter>();
    const bottomTags = new Array<FileTypeCounter>();
    data.fileTypeCounter.forEach(tagCounter => {
        if (tagCounter.fileType === "TEXT" || tagCounter.fileType === "BINARY") {
            topTags.push(tagCounter);
        } else {
            bottomTags.push(tagCounter);
        }
    })

    function getSelectedTags(): string[] {
        let tags: string[] = [];
        debuggerSettings.displayedFileTypeMetrics.forEach(fileTypeMetrics => tags.push(fileTypeMetrics.fileType));
        return tags;
    }

    function onTagSelectionChange(tags: Array<string>) {
        let tagMetrics = new Array<FileTypeMetrics>();
        tags.forEach(t => tagMetrics.push({fileType: t, metrics: []}));
        onChange({...debuggerSettings, displayedFileTypeMetrics: tagMetrics})
    }

    function handleTagClick(tag: string) {
        if (selectedTags.includes(tag)) {
            onTagSelectionChange(selectedTags.filter(t => t !== tag))
        } else {
            onTagSelectionChange([...selectedTags, tag])
        }
    }

    return <>
        <Typography variant="h6" component="span" style={{color: "gray"}}>
            Choose which kind of files you want to consider in this visualization.
        </Typography><br />
        <Typography variant="h6" component="span" style={{color: "gray"}}>Type
            selected: <b>{selectedTags.length}</b></Typography>
        <Container maxWidth="lg">
            <Grid container justifyContent={"center"}>
                <Grid item xs={12} style={{display: "flex", justifyContent: "center"}}>
                    {topTags.map(tagCounter => <div key={tagCounter.fileType}>
                            <ComponentsCard tag={tagCounter.fileType} count={tagCounter.count}
                                            selected={selectedTags.includes(tagCounter.fileType)} onClick={handleTagClick}/>
                        </div>
                    )}
                </Grid>
                <Grid item xs={12} style={{
                    display: "flex",
                    flexWrap: "wrap",
                    justifyContent: "center",
                    height: showMore ? 'auto' : 250,
                    overflowY: "hidden"
                }}>
                    {bottomTags.sort((a, b) => b.count - a.count).map(tagCounter => <div key={tagCounter.fileType}>
                        <ComponentsCard tag={tagCounter.fileType} count={tagCounter.count}
                                        selected={selectedTags.includes(tagCounter.fileType)} onClick={handleTagClick}/>
                    </div>)}
                </Grid>
                <Button onClick={() => setShowMore(!showMore)}>Show {showMore ? "Less" : "More"}</Button>
            </Grid>
        </ Container>
    </>
}

function ComponentsCard({
                            tag,
                            count,
                            selected,
                            onClick
                        }: { tag: string, count: number, selected: boolean, onClick: (tag: string) => void }) {
    return <Paper elevation={selected ? 10 : 3} sx={{
        maxWidth: 200,
        display: "flex",
        flexDirection: "column",
        padding: 2,
        borderRadius: 5,
        margin: 5,
        flexGrow: 1,
        cursor: "pointer"
    }} onClick={() => onClick(tag)}>
        <div style={{flexGrow: 1, display: "flex", justifyContent: "center"}}>
            <div style={{
                border: "2px solid #242424",
                borderRadius: "100%",
                padding: 10,
                aspectRatio: "1/1",
                width: 60,
                display: "flex",
                justifyContent: "center",
                alignItems: "center"
            }}>
                <Typography variant="h6" component="div">  {count} </Typography>
            </div>
        </div>
        <div style={{flexGrow: 2, textAlign: "center", marginTop: 15}}>
            <Typography variant="h5" component="div" gutterBottom>  {tag} </Typography>
        </div>
    </Paper>
}