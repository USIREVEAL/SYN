import {Maybe} from "../Maybe";
import {CodeEntity, Entity} from "./Entity.type";

export type Project = CodeEntity & {
    name: string
    path: string
    lastAnalysisTs:number
    projectHistory: Maybe<ProjectHistory>
}
export type LocalProject = Project & {
    __typename?: "LocalProject"
}

export type RemoteProject = Project & {
    __typename?: "RemoteProject"
    projectURL: string
}

export type ProjectHistory = CodeEntity & {
    __typename?: "ProjectHistory"
    fileHistories: Array<FileHistory>
    projectVersions: Array<ProjectVersion>
}

export type FileHistory = CodeEntity & {
    __typename?: "FileHistory"
    name: string
    path: string
    fileTypes: Array<string>
    fileVersions: Array<FileVersion>
    aliases: Array<string>
}

export type Version =
    CodeEntity & {
    __typename?: "Version"
    next: Maybe<Version>
    previous: Maybe<Version>
}


export type ProjectVersion = Version & {
    __typename?: "ProjectVersion"
    next: Maybe<ProjectVersion>
    previous: Maybe<ProjectVersion>
    fileVersions: Array<FileVersion>
    timestamp:number
    commitHash: string
    commitMessage: string
}


export type FileVersion = Version & {
    __typename?: "FileVersion"
    next: Maybe<FileVersion>
    previous: Maybe<FileVersion>
    parentProjectVersion: ProjectVersion
    fileHistory: FileHistory
    change: Change
}

export type Change = Entity & {
    id: number

    linesAdded: number
    linesDeleted: number
}

export type FileAddition = Change & {
    __typename?: "FileAddition"
}

export type FileDeletion = Change & {
    __typename?: "FileDeletion"
}

export type FileModification = Change & {
    __typename?: "FileModification"
}

export type FileMoving = Change & {
    __typename?: "FileMoving"
    fromPath: string
    toPath: string
}

export type FileRenaming = Change & {
    __typename?: "FileRenaming"
    fromName: string
    toName: string
}

export function isAddChange(change: Change): change is FileAddition {
    return (change as FileAddition).__typename === "FileAddition";
}

export function isDeleteChange(change: Change): change is FileDeletion {
    return (change as FileDeletion).__typename === "FileDeletion";
}

export function isModifyChange(change: Change): change is FileModification {
    return (change as FileModification).__typename === "FileModification";
}

export function isMoveChange(change: Change): change is FileMoving {
    return (change as FileMoving).__typename === "FileMoving";
}

export function isRenameChange(change: Change): change is FileRenaming {
    return (change as FileRenaming).__typename === "FileRenaming";
}

export function isFileHistory(fileHistory: any): fileHistory is FileHistory {
    return (fileHistory as FileHistory).aliases !== undefined;
}


export function isRemoteProject(project: Project): project is RemoteProject {
    return (project as RemoteProject).projectURL !== undefined;
}


