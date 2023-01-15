import React from "react";
import {Checkbox, Container, FormControlLabel, FormGroup, Grid, Slider, Typography} from "@mui/material";
import {DebuggerSettingsStepProps} from "../../DebuggerSettingsForm.types";
import {DebuggerSettings} from "../../../../providers/ViewProvider/ViewProvider.types";

export function ProjectViewSettings({debuggerSettings, onChange}: DebuggerSettingsStepProps) {
    const handleDrawingSpeedChange = (event: Event, newValue: number | number[]) => {
        if (typeof newValue === 'number') {
            onChange({...debuggerSettings, versionAutoplaySpeed: newValue})
        }
    };

    const handleCheck =
        (prop: keyof DebuggerSettings) => (event: React.ChangeEvent<HTMLInputElement>) => {
            onChange({...debuggerSettings, [prop]: event.target.checked});
        };

    return <Container maxWidth="lg">
        <Grid container spacing={3} justifyContent={"space-between"}>
            <Grid item xs={3}>
                <FormControlLabel control={
                    <Checkbox
                        onChange={handleCheck("showVRExperience")}
                        checked={debuggerSettings.showVRExperience}/>
                } label="Show VR button"/>

                <FormControlLabel control={
                    <Checkbox
                        onChange={handleCheck("showDeletedEntities")}
                        checked={debuggerSettings.showDeletedEntities}/>
                } label="Keep deleted entities"/>
            </Grid>
            <Grid item xs={3}>
                <FormControlLabel control={
                    <Checkbox
                        onChange={handleCheck("showDebugLayer")}
                        checked={debuggerSettings.showDebugLayer}/>
                } label="Show debug layer"/>

                <FormControlLabel control={
                    <Checkbox
                        onChange={handleCheck("makeScreenshot")}
                        checked={debuggerSettings.makeScreenshot}/>
                } label="Auto screenshot"/>

                <FormControlLabel control={
                    <Checkbox
                        onChange={handleCheck("computeShadows")}
                        checked={debuggerSettings.computeShadows}/>
                } label="Shadows"/>

            </Grid>
            <Grid item xs={3}>
                <FormGroup style={{marginTop: 10}}>
                    <Typography id="input-slider" gutterBottom>
                        Animation switching speed
                    </Typography>
                    <Slider
                        value={debuggerSettings.versionAutoplaySpeed}
                        aria-label="Speed"
                        sx={{
                            color: '#fff',
                            height: 4,
                            '& .MuiSlider-thumb': {
                                width: 8,
                                height: 8,
                                transition: '0.3s cubic-bezier(.47,1.64,.41,.8)',
                                '&:before': {
                                    boxShadow: '0 2px 12px 0 rgba(0,0,0,0.4)',
                                },
                                '&:hover, &.Mui-focusVisible': {
                                    boxShadow: `0px 0px 0px 8px ${
                                        'rgb(255 255 255 / 16%)'
                                    }`,
                                },
                                '&.Mui-active': {
                                    width: 20,
                                    height: 20,
                                },
                            },
                            '& .MuiSlider-rail': {
                                opacity: 0.28,
                            },
                        }}
                        onChange={handleDrawingSpeedChange}
                        min={50}
                        max={2000}
                        marks={[
                            {value: 50, label: "50ms"},
                            {value: 500, label: "500ms"},
                            {value: 1000, label: "1s"},
                            {value: 2000, label: "2s"}
                        ]}
                    />
                </FormGroup>
            </Grid>


        </Grid>

    </Container>
}



