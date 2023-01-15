import React from 'react';
import '../../views/home/style.css'; //TODO fix

import {
    Button,
    CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle,
    Fab,
    TextField,
} from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import {CREATE_PROJECT} from "../../api/Mutations";
import {useApolloClient, useMutation} from "@apollo/client";
import {PROJECT_LIST} from "../../api/Queries";
import {Project} from "../../types/server/Project.types";
import {MutationCreateProjectArgs} from "../../types/server/Mutation.types";

export function AddProjectButton() {

    // Modal Handler
    const [open, setOpen] = React.useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    // Project Name
    const [projectName, setProjectName] = React.useState('');
    const [projectNameAF, setProjectNameAF] = React.useState(false);
    const handleChangeProjectName = (event: React.ChangeEvent<HTMLInputElement>) => {
        setProjectName(event.target.value);
        setProjectNameAF(true)
        setProjectLocatioAF(false)
    };


    // Project Location
    const[projectLocation, setProjectLocation] = React.useState('');
    const [projectLocationAF,setProjectLocatioAF] = React.useState(false);

    const handleChangeProjectLocation = (event: React.ChangeEvent<HTMLInputElement>) => {
        let projectLocation = event.target.value;

        setProjectLocation(projectLocation);

        if (projectName === "") {
            let projectNameSplit = projectLocation.split("/");
            let projectName = projectNameSplit[projectNameSplit.length - 1];

            if (projectName.lastIndexOf(".") !== -1) {
                projectName = projectName.substr(0, projectName.lastIndexOf("."))
            }

            setProjectName(projectName)

        }

        setProjectNameAF(false)
        setProjectLocatioAF(true)

    };

    // Add project mutation
    const [createProject, {loading }] = useMutation<Project, MutationCreateProjectArgs>(CREATE_PROJECT,
        {
            variables: {
                projectName: projectName,
                projectLocation: projectLocation
            },
            onCompleted: () => {
                window.location.reload()
            }
        });

    // Needed to update ui after project creation
    let apolloClient = useApolloClient();

    function DialogBody() {
      return <>
          <DialogContent>
              <TextField
                  autoFocus={projectNameAF}
                  required
                  fullWidth
                  margin="normal"
                  id="projectNameField"
                  label="Project Name"
                  value={projectName}
                  onChange={handleChangeProjectName}
              />

              <TextField
                  autoFocus={projectLocationAF}
                  required
                  fullWidth
                  margin="normal"
                  id="projectLocationField"
                  label="Project Location"
                  value={projectLocation}
                  onChange={handleChangeProjectLocation}
              />
          </DialogContent>
          <DialogActions>
              {
                  projectName !== ""
                  && projectLocation !== ""
                  && <Button
                      sx={{float: "right", marginTop: 2}}
                      onClick={() => createProject()}
                  >
                      add
                  </Button>
              }
          </DialogActions>
      </>
    }

    function LoadingBody() {
        return <DialogContent style={{textAlign: "center"}}>
            <CircularProgress />
        </DialogContent>
    }


    return <Fab aria-label="add">
        <AddIcon onClick={handleOpen} />

        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>  {loading ? "Adding" : "Add"} a new project</DialogTitle>
            {loading ? <LoadingBody /> : <DialogBody />}
        </Dialog>
    </Fab>
}


