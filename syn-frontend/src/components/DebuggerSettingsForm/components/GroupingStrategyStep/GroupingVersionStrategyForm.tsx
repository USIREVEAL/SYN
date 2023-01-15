import React from "react";
import {useProject} from "../../../../hooks/useProject";
import {DebuggerSettingsStepProps} from "../../DebuggerSettingsForm.types";
import {Query, QueryGroupingPreview} from "../../../../types/server/Query.types";
import {useQuery} from "@apollo/client";
import {GET_GROUPING_PREVIEW} from "../../../../api/Queries";
import {debuggerSettingToViewSpecification} from "../../../../helpers/ProjectViewSpecHelper";
import {GroupingStrategy} from "../../../../types/server/View.type";
import {getUsedMultiplier, multiplierValues} from "../../helpers/TimestampHelper";
import {useDidUpdateEffect} from "../../../../hooks/useDidUpdateEffect";
import {
    Container,
    FormControl,
    FormControlLabel,
    MenuItem,
    Radio,
    RadioGroup,
    Select,
    SelectChangeEvent,
    TextField,
    Typography
} from "@mui/material";


export function GroupingVersionStrategyForm({debuggerSettings, onChange}: DebuggerSettingsStepProps) {

    const groupingStrategy = debuggerSettings.versionGroupingStrategy;
    const chunkSize = debuggerSettings.versionGroupingChunkSize;

    const project = useProject();
    const {data, loading} = useQuery<Query, QueryGroupingPreview>(GET_GROUPING_PREVIEW, {
        variables: {
            projectId: project.id,
            viewSpecification: debuggerSettingToViewSpecification(debuggerSettings)
        }
    });

    const [timestampMultiplier, setTimestampMultiplier] = React.useState<number>(() => {
        if (groupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            return getUsedMultiplier(chunkSize).value;
        } else {
            return multiplierValues[1].value;
        }
    })

    const [commitSize, setCommitSize] = React.useState(() => {
        if (groupingStrategy === GroupingStrategy.COMMIT_STRATEGY) {
            return chunkSize;
        } else {
            return 1;
        }
    })

    const [timestampSize, setTimestampSize] = React.useState(() => {
        if (groupingStrategy === GroupingStrategy.TIMESTAMP_STRATEGY) {
            return chunkSize / getUsedMultiplier(chunkSize).value;
        } else {
            return 1;
        }
    })

    function onGroupingValueChange(groupingStrategy: GroupingStrategy, groupingVersionChunkSize: number) {
        let newProjectViewSettings = {...debuggerSettings};
        newProjectViewSettings.versionGroupingStrategy = groupingStrategy;
        newProjectViewSettings.versionGroupingChunkSize = groupingVersionChunkSize;
        onChange(newProjectViewSettings);
    }

    useDidUpdateEffect(() => {
        onGroupingValueChange(GroupingStrategy.COMMIT_STRATEGY, commitSize);
    }, [commitSize])

    useDidUpdateEffect(() => {
        onGroupingValueChange(GroupingStrategy.TIMESTAMP_STRATEGY, timestampMultiplier * timestampSize);
    }, [timestampMultiplier, timestampSize])


    const handleStrategyChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.value === "COMMIT_STRATEGY") {
            onGroupingValueChange(GroupingStrategy.COMMIT_STRATEGY, commitSize);
        } else {
            onGroupingValueChange(GroupingStrategy.TIMESTAMP_STRATEGY, timestampMultiplier * timestampSize);
        }

    };

    const handleSizeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        let value = parseInt(event.target.value);
        if (isNaN(value) || value <= 0)
            value = 1

        if (groupingStrategy === GroupingStrategy.COMMIT_STRATEGY) {
            setCommitSize(value);
        } else {
            setTimestampSize(value);
        }
    };

    const handleTimestampMultiplierChange = (event: SelectChangeEvent<number>) => {
        setTimestampMultiplier(event.target.value as number);
    }


    return <Container maxWidth="lg">
        <div style={{display: "flex", flexDirection: "column"}}>
            <Typography variant="h6" component="span" style={{color: "gray"}}>Select which strategy do you want to use
                to group project's versions: </Typography>
            <FormControl>
                <RadioGroup
                    value={groupingStrategy}
                    onChange={handleStrategyChange}
                >
                    <FormControlLabel value={GroupingStrategy.COMMIT_STRATEGY} control={<Radio/>} label={
                        <div>Create a new version every
                            <TextField
                                disabled={groupingStrategy !== GroupingStrategy.COMMIT_STRATEGY}
                                variant="standard"
                                value={commitSize}
                                type={"number"}
                                onChange={handleSizeChange}
                                size={"small"}
                                sx={{width: 70, marginLeft: 1, marginRight: 1}}
                            /> commit{commitSize > 1 ? "s" : ""} </div>
                    }/>
                    <FormControlLabel value={GroupingStrategy.TIMESTAMP_STRATEGY} control={<Radio/>} label={
                        <div>Create a new version every
                            <TextField
                                disabled={groupingStrategy !== GroupingStrategy.TIMESTAMP_STRATEGY}
                                variant="standard"
                                value={timestampSize}
                                type={"number"}
                                onChange={handleSizeChange}
                                size={"small"}
                                sx={{width: 70, height: 29, marginLeft: 1, marginRight: 2}}
                            />
                            <Select
                                value={timestampMultiplier}
                                onChange={handleTimestampMultiplierChange}
                                variant="standard"
                                sx={{height: 29}}
                            >
                                {multiplierValues.map(mV => <MenuItem key={mV.value}
                                                                      value={mV.value}>{mV.name}{timestampSize > 1 ? "s" : ""}</MenuItem>)}
                            </Select>
                        </div>
                    }/>
                </RadioGroup>
            </FormControl>
            <p style={{marginTop: 50}}>
                <Typography variant="h6" component="span">Number of versions that will be displayed:
                    {loading && <span> ...</span>}
                    {data && <Typography variant="h5" component="span"
					                     style={{fontWeight: 700}}> {data.groupingPreview} </Typography>}

                </Typography>
            </p>
        </div>
    </Container>
}