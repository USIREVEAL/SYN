import {ViewProvider} from "../ViewProvider/ViewProvider";
import {EntityDetailsProvider} from "../EntityDetailsProvider/EntityDetailsProvider";
import React from "react";
import {DebuggerProviderTypes} from "./DebuggerProvider.types";
import {ProjectProvider} from "../ProjectProvider/ProjectProvider";
import {AnimationProvider} from "../AnimationProvider/AnimationProvider";

export const DebuggerProvider = ({children, projectId}: DebuggerProviderTypes) => <ProjectProvider
    projectId={projectId}>
    <EntityDetailsProvider>
        <ViewProvider>
            <AnimationProvider>
                {children}
            </AnimationProvider>
        </ViewProvider>
    </EntityDetailsProvider>
</ProjectProvider>

