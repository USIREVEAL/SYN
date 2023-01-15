import {
    Checkbox,
    FormControl,
    FormControlLabel,
    FormGroup,
    FormLabel,
    Grid,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    SelectChangeEvent, Slider, Stack,
    Typography
} from "@mui/material";
import React from "react";
import {FileTypeMetricCardTypeArgs} from "./FileTypeSettingsCard.type";
import {FigureShape} from "../../../../../../types/BabylonVisualizerTypes";
import {BabylonWithSpinningFigure} from "../BabylonWithSpinningFigure/BabylonWithSpinningFigure";

export function FileTypeSettingsCard({
                                         fileType,
                                         debuggerSettings,
                                         onChange,
                                         serverFileTypeMetrics
                                     }: FileTypeMetricCardTypeArgs) {

    function metricChecked(metricName: string) {
        const localFileTypeMetrics = debuggerSettings.displayedFileTypeMetrics.find(fileTypeMetric => fileTypeMetric.fileType === fileType);
        if (localFileTypeMetrics) {
            return localFileTypeMetrics.metrics.includes(metricName);
        }
        return false;
    }

    function handleMetricClick(metricName: string) {
        const localFileTypeMetrics = debuggerSettings.displayedFileTypeMetrics.find(fileTypeMetric => fileTypeMetric.fileType === fileType);
        const newDebuggerSettings = {...debuggerSettings};
        if (localFileTypeMetrics) {
            let oldMetricsArray = [...localFileTypeMetrics.metrics]
            if (localFileTypeMetrics.metrics.includes(metricName)) {
                oldMetricsArray = oldMetricsArray.filter(mName => mName !== metricName);
            } else {
                oldMetricsArray.push(metricName)
            }
            newDebuggerSettings.displayedFileTypeMetrics.find(fileTypeMetric => fileTypeMetric.fileType === fileType)!.metrics = oldMetricsArray;
        }
        onChange(newDebuggerSettings);
    }

    function setFileTypeOpacity(opacity: number) {
        const newProjectViewSpecs = {...debuggerSettings};
        const fileTypeShape = newProjectViewSpecs.fileTypeOpacity.find(fileTypeShape => fileTypeShape.fileType === fileType);
        if (fileTypeShape === undefined) {
            newProjectViewSpecs.fileTypeOpacity.push({
                fileType: fileType,
                opacity: opacity
            })
        } else {
            fileTypeShape.opacity = opacity
        }
        onChange(newProjectViewSpecs)
    }

    function setFileTypeShape(newShape: FigureShape) {
        const newProjectViewSpecs = {...debuggerSettings};
        const fileTypeShape = newProjectViewSpecs.fileTypeShape.find(fileTypeShape => fileTypeShape.fileType === fileType);
        if (fileTypeShape === undefined) {
            newProjectViewSpecs.fileTypeShape.push({
                fileType: fileType,
                shapeName: FigureShape[FigureShape.BOX]
            })
        } else {
            fileTypeShape.shapeName = FigureShape[newShape]
        }
        onChange(newProjectViewSpecs)
    }

    const handleChangeShape = (event: SelectChangeEvent) => {
        // @ts-ignore
        setFileTypeShape(FigureShape[event.target.value])
    };

    const fileTypeShape = debuggerSettings.fileTypeShape.find(fileTypeShape => fileTypeShape.fileType === fileType);
    if (fileTypeShape === undefined) {
        setFileTypeShape(FigureShape.BOX);
        return null;
    }

    const fileTypeOpacity = debuggerSettings.fileTypeOpacity.find(fileTypeShape => fileTypeShape.fileType === fileType);
    if (fileTypeOpacity === undefined) {
        setFileTypeOpacity(1);
        return null;
    }

    // @ts-ignore
    const shape = FigureShape[fileTypeShape.shapeName];
    const opacity = fileTypeOpacity.opacity;

    return <Paper elevation={10} style={{borderRadius: 5, margin: 10}}>
        <Grid container>
            <Grid item xs={2} style={{display: "flex", justifyContent: "center", alignItems: "center"}}>
                <Typography variant="h5" component="span">{fileType}</Typography>
            </Grid>
            <Grid item xs={4} container>
                <Grid item xs={12}>
                    <FormControl sx={{m: 3, mb: 1}} component="fieldset" variant="standard">
                        <FormLabel component="legend">Displayed metrics</FormLabel>
                        <FormGroup style={{
                            display: "flex",
                            flexDirection: "row"
                        }}>
                            {serverFileTypeMetrics.metrics.map(metricName => {
                                const checked = metricChecked(metricName);

                                return <FormControlLabel
                                    control={
                                        <Checkbox checked={checked}
                                                  onClick={() => handleMetricClick(metricName)}/>
                                    }
                                    label={metricName}
                                    key={metricName}
                                />
                            })}
                        </FormGroup>
                    </FormControl>
                </Grid>
            </Grid>
            <Grid item xs={3} container>
                <Grid item xs={12}>
                    <FormControl sx={{m: 3}} component="fieldset" fullWidth>
                        <InputLabel>Figure shape</InputLabel>
                        <Select
                            value={shape}
                            label="Figure shape"
                            onChange={handleChangeShape}

                            style={{width: "calc(100% - 30px)"}}
                        >
                            {Object.keys(FigureShape).map(figureShape => <MenuItem value={figureShape}
                                                                                   key={figureShape}>{figureShape}</MenuItem>)}
                        </Select>

                    </FormControl>
                    <Stack spacing={2} direction="row" sx={{ mb: 1 }} alignItems="center">
                        <span>Opacity</span>
                        <Slider aria-label="Volume" value={opacity} min={0} max={1} step={0.01} onChange={(e, newValue: number | number[]) => setFileTypeOpacity(newValue as number)}/>
                    </Stack>

                </Grid>



            </Grid>
            <Grid item xs={2} sx={{m: 3}}>
                <BabylonWithSpinningFigure shape={shape} fileType={fileType} opacity={opacity}/>
            </Grid>
        </Grid>
    </Paper>
}

