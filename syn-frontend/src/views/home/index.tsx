import React from 'react';
import './style.css';
import {NavLink} from "react-router-dom";
import {Grid, Paper, Typography} from "@mui/material";
import {useQuery} from "@apollo/client";
import {PROJECT_LIST} from "../../api/Queries";
import AddProjectButton from "../../components/AddProjectButton";
import {Query, QueryProjectList} from "../../types/server/Query.types";


function ListProjects() {
    const BLOCKED_PROJECTS = ["linux", "elastic", "libreoffice"];
    const {loading, data} = useQuery<Query, QueryProjectList>(PROJECT_LIST);

    if (loading) return (<Typography> loading </Typography>);
    if (data !== undefined) {
        const projects = data.projectList
        return (
            <div className={"Home"}>
                <Grid container spacing={3}>
                    <Grid item container xs={4}/>

                    <Grid item
                          container
                          xs={4}
                          direction="row"
                          justifyContent="center"
                          alignItems="center"
                          spacing={2}>
                        {projects && projects.map((project) => {
                            if (BLOCKED_PROJECTS.includes(project.name)) {
                                return  <div/>
                            } else {
                                return  <Grid item xs={12} key={project.id}>
                                    <NavLink to={"/project/" + project.id} className={"RVLink"} >
                                        <Paper elevation={5} className={"RVLinkPaper"}>
                                            {project.name}
                                        </Paper>
                                    </NavLink>
                                </Grid>
                            }
                        })}
                    </Grid>
                    <Grid item container xs={4}>
                        <Grid item xs={6}/>
                        <Grid item xs={2}>
                            {/* <AddProjectButton/> */}
                        </Grid>
                    </Grid>
                </Grid>


            </div>
        );
    }
    return (<Typography> error :C </Typography>);


}

export default ListProjects;
