import React, {useEffect} from "react";

import {DebuggerSettingsStepProps} from "../../DebuggerSettingsForm.types";
import {useProject} from "../../../../hooks/useProject";
import {useQuery} from "@apollo/client";
import {GET_FILE_TYPE_METRICS} from "../../../../api/Queries";
import {Query, QueryFileTypeMetrics} from "../../../../types/server/Query.types";
import {
    Checkbox,
    Container,
    FormControl, FormControlLabel,
    Grid,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    SelectChangeEvent,
    TextField,
    Typography
} from "@mui/material";
import FileTypeSettingsCard from "./components/FileTypeSettingsCard";
import {MapperStrategyName} from "../../../../types/server/View.type";
import {DebuggerSettings} from "../../../../providers/ViewProvider/ViewProvider.types";


export function MetricSelectionForm({debuggerSettings, onChange}: DebuggerSettingsStepProps) {
    const displayedFileTypeMetrics = debuggerSettings.displayedFileTypeMetrics;

    const project = useProject()
    const {data} = useQuery<Query, QueryFileTypeMetrics>(GET_FILE_TYPE_METRICS, {
        variables: {
            projectId: project.id,
            fileTypeFilter: displayedFileTypeMetrics.map(fileTypeMetric => fileTypeMetric.fileType)
        }
    })

    const selectedMetrics = Array.from(new Set(debuggerSettings.displayedFileTypeMetrics.flatMap(fileTypeMetric => fileTypeMetric.metrics)))
    if (selectedMetrics.length === 0 && debuggerSettings.mapperStrategy !== MapperStrategyName.NONE) {
        onChange({...debuggerSettings, mapperStrategy: MapperStrategyName.NONE})
    }

    const handleCheck =
        (prop: keyof DebuggerSettings) => (event: React.ChangeEvent<HTMLInputElement>) => {
            onChange({...debuggerSettings, [prop]: event.target.checked});
        };

    if (data === undefined) return null;

    return <Container>
        <Typography variant="h6" component="span" style={{color: "gray", textAlign: "left"}}>
            File type based settings
        </Typography>
        <Grid container>
            {displayedFileTypeMetrics.map((displayedFileTypeMetric, i) => {
                const serverFileTypeMetrics = data.fileTypeMetrics.find(fileTypeMetric => fileTypeMetric.fileType === displayedFileTypeMetric.fileType)
                if (serverFileTypeMetrics === undefined) return null
                return <Grid item xs={12}>
                    <FileTypeSettingsCard key={i} fileType={displayedFileTypeMetric.fileType}
                                             debuggerSettings={debuggerSettings} onChange={onChange}
                                             serverFileTypeMetrics={serverFileTypeMetrics}/>
                </Grid>
            })}
        </Grid>


        {selectedMetrics.length > 0 && <>
			<div style={{marginTop: 40}}>
				<Typography variant="h6" component="span" style={{color: "gray", textAlign: "left"}}>
					Mapper settings
				</Typography>
			</div>
			<Paper elevation={10} style={{borderRadius: 5, margin: 10, padding: 30}}>
				<Grid container justifyContent={"space-evenly"}>
					<Grid item xs={3}>
						<MapperStrategySelector/>
					</Grid>
					<Grid item xs={3}>
                        {debuggerSettings.mapperStrategy !== MapperStrategyName.NONE && <MapperMetricNameSelector/>}
					</Grid>
					<Grid item xs={3}>
                        {debuggerSettings.mapperStrategy !== MapperStrategyName.NONE && <TextField
							value={debuggerSettings.mapperOptions.maxHeight}
							type={"number"}
							label="Max Height"
							onChange={handleMaxHeightChange}
						/>}
					</Grid>
                    <Grid item xs={12} style={{marginTop: 10, textAlign: "right"}}>
	                    <FormControlLabel control={
                            <Checkbox
                                onChange={handleCheck("showUnmappedEntities")}
                                checked={debuggerSettings.showUnmappedEntities}/>
                        } label="Show show unmapped entities"/>
                    </Grid>

				</Grid>
			</Paper>
        </>}
    </Container>

    function handleMaxHeightChange(event: React.ChangeEvent<HTMLInputElement>) {
        let val = parseInt(event.target.value);
        if (isNaN(val) || val < 0)
            val = -1
        let newProjectViewSpecs = {...debuggerSettings}
        newProjectViewSpecs.mapperOptions.maxHeight = val
        onChange(newProjectViewSpecs)
    }

    function MapperMetricNameSelector() {
        const handleChange = (event: SelectChangeEvent) => {
            const newProjectViewSpecs = {...debuggerSettings};
            newProjectViewSpecs.mapperMetricName = event.target.value;
            onChange(newProjectViewSpecs)
        };

        useEffect(() => {
            if (!selectedMetrics.includes(debuggerSettings.mapperMetricName)) {
                if (selectedMetrics.length > 0) {
                    onChange({...debuggerSettings, mapperMetricName: selectedMetrics[0]})
                }
            }
        }, [])

        if (selectedMetrics.includes(debuggerSettings.mapperMetricName)) {
            return <FormControl fullWidth>
                <InputLabel>Map height to</InputLabel>
                <Select
                    value={debuggerSettings.mapperMetricName}
                    label="Map height to"
                    onChange={handleChange}
                >
                    {selectedMetrics.map(metricName => <MenuItem value={metricName} key={metricName}>{metricName}</MenuItem>)}
                </Select>
            </FormControl>
        } else {
            return null
        }
    }

    function MapperStrategySelector() {
        const handleChange = (event: SelectChangeEvent) => {
            const newProjectViewSpecs = {...debuggerSettings};
            newProjectViewSpecs.mapperStrategy = event.target.value as MapperStrategyName
            onChange(newProjectViewSpecs)
        };

        return <FormControl fullWidth>
            <InputLabel>Mapper strategy</InputLabel>
            <Select
                value={debuggerSettings.mapperStrategy}
                label="Mapper strategy"
                onChange={handleChange}
            >
                {Object.keys(MapperStrategyName).map(strategyName => <MenuItem value={strategyName}
                                                                               key={strategyName}>{strategyName}</MenuItem>)}
            </Select>
        </FormControl>
    }
}