export type ProjectVersionDetailsCardParams = {
    getNextVersion: (currentVersion: number) => void
    getPreviousVersion: (currentVersion: number) => void
    getVersion: (currentVersion: number, selectedVersion: number) => void
}