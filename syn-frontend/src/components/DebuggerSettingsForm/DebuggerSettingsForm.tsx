import React from "react";
import {Button, Grid, Paper, Step, StepLabel, Stepper, Typography} from "@mui/material";
import ProjectViewSettings from "./components/ProjectSettingsStep";
import EntityAgingStep from "./components/EntityAgingStep";
import MetricSelectionForm from "./components/MetricSelectionStep";
import GroupingVersionStrategyForm from "./components/GroupingStrategyStep";
import ComponentSelectionForm from "./components/ComponentSelectionStep";
import {DebuggerSettingsFormProps} from "./DebuggerSettingsForm.types";

export function DebuggerSettingsForm({
                                         debuggerSettings: newDebuggerSettings,
                                         onSave,
                                         updateDebuggerSettings: setNewDebuggerSettings,
                                         firstStep = 4,
                                         title = "Debugger settings"
                                     }: DebuggerSettingsFormProps) {
    const [activeStep, setActiveStep] = React.useState(firstStep);

    const steps = [
        {
            label: 'Component selection',
            component: <ComponentSelectionForm
                debuggerSettings={newDebuggerSettings}
                onChange={setNewDebuggerSettings}
            />
        },
        {
            label: 'Grouping version strategy',
            component: <GroupingVersionStrategyForm
                debuggerSettings={newDebuggerSettings}
                onChange={setNewDebuggerSettings}
            />
        },
        {
            label: 'Figure settings',
            component: <MetricSelectionForm
                debuggerSettings={newDebuggerSettings}
                onChange={setNewDebuggerSettings}
            />
        },
        {
            label: 'View color',
            component: <EntityAgingStep
                debuggerSettings={newDebuggerSettings}
                onChange={setNewDebuggerSettings}
            />
        }, {
            label: 'View settings',
            component: <ProjectViewSettings
                debuggerSettings={newDebuggerSettings}
                onChange={setNewDebuggerSettings}
            />
        },
    ];

    const handleNext = () => {
        if (activeStep < steps.length - 1) {
            setActiveStep(activeStep + 1);
        } else {
            onSave()
        }
    };

    const handleBack = () => {
        setActiveStep(activeStep - 1);
    };

    return <Paper elevation={3} style={{padding: 40, flex: 1}}>
        <Grid container spacing={2}>
            <Grid item xs={12} container>
                <Grid item xs={4}>
                    <Typography variant="h5" component="div" gutterBottom> {title} </Typography>
                </Grid>
                <Grid item xs={8}>
                    <Stepper activeStep={activeStep}>
                        {steps.map((step, i) => (
                            <Step key={step.label} onClick={() => setActiveStep(i)} style={{cursor: "pointer"}}>
                                <StepLabel>{step.label}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                </Grid>
            </Grid>
            <Grid item xs={12} style={{marginTop: 40}}>
                {steps[activeStep].component}
            </Grid>
            <Grid item xs={12} style={{textAlign: "right"}}>
                {activeStep > 0 && <Button
					onClick={handleBack}
					sx={{mt: 1, mr: 1}}
				>
					Previous
				</Button>}
                <Button
                    onClick={handleNext}
                    sx={{mt: 1, mr: 1}}
                >
                    {activeStep <= steps.length - 2 ? "Next" : "Close"}
                </Button>
            </Grid>
        </Grid>
    </Paper>
}