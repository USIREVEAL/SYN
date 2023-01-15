import {useContext} from "react";
import {ProjectContext} from "../providers/ProjectProvider/ProjectProvider";
import {Project} from "../types/server/Project.types";

export function useProject(): Project {
    return useContext(ProjectContext)
}