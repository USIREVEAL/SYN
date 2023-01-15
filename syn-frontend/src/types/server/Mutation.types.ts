import {Maybe} from "../Maybe";
import {Project} from "./Project.types";

export type Mutation = {
    __typename?: "Mutation"
    createProject?: Maybe<Project>
}

export type MutationCreateProjectArgs = {
    projectName: string
    projectLocation: string
}

