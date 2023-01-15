import {View, ViewSpecificationInput} from "./View.type";
import {FileHistory, Project, ProjectVersion} from "./Project.types";

export type Query = {
    projectList:  Array<PartialProjectInformation>
    project: Project
    view: View
    partialView: View
    fileHistory: FileHistory
    projectVersion: ProjectVersion
    projectVersions: Array<ProjectVersion>
    groupingPreview: number
    fileTypeCounter: Array<FileTypeCounter>
    fileTypeMetrics: Array<FileTypeMetrics>
}

export type PartialProjectInformation = {
    id: number
    name: string
}

export type FileTypeCounter = {
    fileType: string
    count: number
}

export type FileTypeMetrics = {
    fileType: string
    metrics: Array<string>
}

export type QueryProjectList = { }

export type QueryProject = {
    projectId: number
}

export type QueryView = {
    projectId: number, viewSpecification: ViewSpecificationInput
}

export type QueryPartialView = {
    projectId: number, viewSpecification: ViewSpecificationInput, viewAnimationId: number
}

export type QueryFileHistory = {
    projectId: number, fileHistoryId: number
}

export type QueryProjectVersion = {
    projectId: number, projectVersionId: number
}

export type QueryProjectVersions = {
    projectId: number, projectVersionsId: Array<number>
}

export type QueryGroupingPreview = {
    projectId: number, viewSpecification: ViewSpecificationInput
}

export type QueryFileTypeCounter = {
    projectId: number
}

export type QueryFileTypeMetrics = {
    projectId:number, fileTypeFilter: Array<String>
}