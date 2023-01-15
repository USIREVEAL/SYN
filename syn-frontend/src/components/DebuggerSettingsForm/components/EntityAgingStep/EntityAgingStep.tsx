import {DebuggerSettingsStepProps} from "../../DebuggerSettingsForm.types";
import React, {useEffect} from "react";
import {GroupingStrategy} from "../../../../types/server/View.type";
import {getUsedMultiplier, multiplierValues} from "../../helpers/TimestampHelper";
import {
    Container,
    FormControl,
    Grid,
    InputAdornment,
    InputLabel,
    MenuItem,
    Select,
    SelectChangeEvent,
    TextField
} from "@mui/material";
import {DebuggerSettings} from "../../../../providers/ViewProvider/ViewProvider.types";
import {BlockPicker, ColorResult} from "react-color";

export function EntityAgingStep({debuggerSettings, onChange}: DebuggerSettingsStepProps) {

    const [timestampMultiplier, setTimestampMultiplier] = React.useState<number>(() => {
        if (debuggerSettings.agingGroupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            return getUsedMultiplier(debuggerSettings.agingStepSize).value;
        } else {
            return multiplierValues[1].value;
        }
    })

    const [timestampSize, setTimestampSize] = React.useState<number>(() => {
        if (debuggerSettings.agingGroupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            return debuggerSettings.agingStepSize / getUsedMultiplier(debuggerSettings.agingStepSize).value;
        } else {
            return 1;
        }
    })

    const handleStrategyChange = (event: SelectChangeEvent) => {
        let newProjectViewSettings = {...debuggerSettings};
        newProjectViewSettings.agingGroupingStrategy = event.target.value as GroupingStrategy;
        newProjectViewSettings.agingStepSize = 1;
        onChange(newProjectViewSettings);
    };

    const handleChange =
        (prop: keyof DebuggerSettings) => (event: React.ChangeEvent<HTMLInputElement>) => {
            onChange({...debuggerSettings, [prop]: parseInt(event.target.value)});
        };



    function handleAgeStepSizeChange(event: React.ChangeEvent<HTMLInputElement>) {
        if (debuggerSettings.agingGroupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            setTimestampSize(parseInt(event.target.value));
        } else {
            handleChange('agingStepSize')(event);
        }
    }

    const handleTimestampMultiplierChange = (event: SelectChangeEvent<number>) => {
        setTimestampMultiplier(event.target.value as number);
    }

    useEffect(() => {
        if (debuggerSettings.agingGroupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            onChange({...debuggerSettings, agingStepSize: timestampSize * timestampMultiplier})
        }
    }, [timestampMultiplier, timestampSize])

    function AgingCustomization() {
        return <Container>
            <Grid item xs={12} container spacing={3}>
                <Grid item xs={4}>
                    <FormControl fullWidth>
                        <InputLabel>Aging Strategy strategy</InputLabel>
                        <Select
                            value={debuggerSettings.agingGroupingStrategy}
                            label="Aging Strategy strategy"
                            onChange={handleStrategyChange}
                        >
                            {Object.keys(GroupingStrategy).map(groupingStrategyName => <MenuItem
                                value={groupingStrategyName}
                                key={groupingStrategyName}>{groupingStrategyName}</MenuItem>)}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={4}>
                    <FormControl fullWidth>
                        <TextField
                            value={debuggerSettings.agingGroupingStrategy == GroupingStrategy.COMMIT_STRATEGY ? debuggerSettings.agingStepSize : timestampSize}
                            type={"number"}
                            label="Step size"
                            onChange={handleAgeStepSizeChange}
                            InputProps={{
                                endAdornment: <InputAdornment position="end" style={{marginLeft: 20}}>
                                    {debuggerSettings.agingGroupingStrategy == GroupingStrategy.COMMIT_STRATEGY ?
                                        "commits" :
                                        <Select
                                            value={timestampMultiplier}
                                            onChange={handleTimestampMultiplierChange}
                                            variant="standard"
                                        >
                                            {multiplierValues.map(mV => <MenuItem key={mV.value}
                                                                                  value={mV.value}>{mV.name}{timestampSize > 1 ? "s" : ""}</MenuItem>)}
                                        </Select>
                                    }</InputAdornment>,
                            }}
                        />
                    </FormControl>
                </Grid>
                <Grid item xs={4}>
                    <FormControl fullWidth>
                        <TextField
                            value={debuggerSettings.agingSteps}
                            type={"number"}
                            label="Total number of steps"
                            onChange={handleChange('agingSteps')}
                            sx={{height: "100%"}}
                        />
                    </FormControl>
                </Grid>
            </Grid>
        </Container>
    }

    function ColorEditor({colorPropertyName}: { colorPropertyName: string }) {
        // @ts-ignore
        const color = debuggerSettings.colorPalette[colorPropertyName];

        const onChangeComplete = (color: ColorResult) => {
            let newColorPalette = {...debuggerSettings.colorPalette};
            // @ts-ignore
            newColorPalette[colorPropertyName] = color.hex;
            onChange({...debuggerSettings, colorPalette: newColorPalette})
        }

        return <Grid item xs={"auto"} container justifyContent={"center"}>
            <Grid item xs={12} style={{textAlign: "center", marginBottom: 20}}>
                <span> {colorPropertyName} </span>
            </Grid>
            <Grid item xs={"auto"}>
                <BlockPicker triangle={"hide"} color={color} onChangeComplete={onChangeComplete}/>
            </Grid>
        </Grid>
    }

    return <Container>
        <Grid container spacing={4}>
            <AgingCustomization/>
            <Grid item xs={12} container alignContent={"center"} spacing={2} rowSpacing={6}>
                {Object.keys(debuggerSettings.colorPalette).map((v, i) => {
                    return <ColorEditor colorPropertyName={v} key={i}/>
                })}
            </Grid>

        </Grid>
    </Container>
}


