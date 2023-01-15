import React from "react";

export function SearchFileHistoryCard() {
    return null;
    // const {dispatch} = React.useContext(ProjectViewContext)
    // const {id: projectID} = React.useContext(ProjectContext)
    //
    // const [options, setOptions] = React.useState<readonly FileHistory[]>([]);
    // const [value, setValue] = React.useState<string | FileHistory | null>();
    // const [inputValue, setInputValue] = React.useState('');
    // const MINIMUM_INPUT_VALUE_LENGTH = 3
    //
    // const {loading, data} = useQuery<Query, QueryGetFileHistoriesByStringArgs>(SEARCH_FILE_HISTORY, {
    //     skip: inputValue.length < MINIMUM_INPUT_VALUE_LENGTH || projectID === -1,
    //     variables: {projectID: projectID, fileHistoryString: inputValue}
    // });
    //
    // let timeout: any
    //
    // useEffect(() => {
    //     if (inputValue.length < MINIMUM_INPUT_VALUE_LENGTH)
    //         dispatch({type: ProjectViewContextActionType.UPDATE_HIGHLIGHTED_FILE_HISTORIES, value: []})
    // }, [inputValue])
    //
    // useEffect(() => {
    //     if (data) {
    //         setOptions(data.getFileHistoriesByString)
    //         dispatch({type: ProjectViewContextActionType.UPDATE_HIGHLIGHTED_FILE_HISTORIES, value: data.getFileHistoriesByString.map(fh => fh.id)})
    //     }
    // }, [data])
    //
    // useEffect(() => {
    //     if (value) {
    //         dispatch({type: ProjectViewContextActionType.UPDATE_SELECTED_FILE_HISTORY_ID, value: (value as FileHistory).id ?? -1})
    //     }
    // }, [value])
    //
    //
    // return <Paper elevation={3} sx={{padding: 2, margin: 2,  position: "absolute",  width: 300, bottom: 10, right: 10}}>
    //     <Autocomplete
    //         id="fileHistory-search"
    //         sx={{ width: 300 }}
    //         filterOptions={(x) => x}
    //         getOptionLabel={(option) => (option as FileHistory).name ?? option}
    //         options={options}
    //         autoComplete
    //         includeInputInList
    //         filterSelectedOptions
    //         freeSolo
    //         value={value}
    //         selectOnFocus={true}
    //         loading={loading}
    //         onChange={(event: any, newValue: string | FileHistory | null) => {
    //
    //             setValue(newValue);
    //         }}
    //         onInputChange={(event, newInputValue) => {
    //             clearTimeout(timeout)
    //
    //             timeout = setTimeout(() => {
    //                 setInputValue(newInputValue);
    //             }, 500)
    //         }}
    //         renderInput={(params) => (
    //             <TextField {...params} label="Search pattern" fullWidth />
    //         )}
    //         renderOption={(props, option) => {
    //
    //             return <li {...props}>
    //                 <Grid container alignItems="center">
    //                     <Grid item>
    //                         <Typography variant="body2" color="text.secondary">
    //                             {(option as FileHistory).name ?? option}
    //                         </Typography>
    //                     </Grid>
    //                 </Grid>
    //             </li>
    //         }}
    //     />
    // </Paper>
}