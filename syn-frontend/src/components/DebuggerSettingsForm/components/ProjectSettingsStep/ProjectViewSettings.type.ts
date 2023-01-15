import {ProjectViewSpecs} from "../../../../types/ProjectContextProviderTypes";

export type ProjectViewSettingsI = {
    projectViewSpecs: ProjectViewSpecs
    onChange: (newSpecs: ProjectViewSpecs) => void
}
