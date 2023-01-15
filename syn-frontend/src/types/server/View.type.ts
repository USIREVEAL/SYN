export type View = {
    viewAnimationList: Array<ViewAnimation>
    animationsCount: number
}


export type ViewAnimation = {
    id: number
    viewFigures: Array<ViewFigure>
    projectVersionIds?:  Array<number>
    projectVersionSummary?: ProjectVersionSummary
    tsFrom: number
    tsTo: number
}

export type ProjectVersionSummary = {
    firstTs: number
    lastTs: number
    changeSummary: Array<ChangeSummary>
}

export type ChangeSummary = {
    type: string,
    count: number
}

export type ViewFigure = {
    id: number
    fileHistoryId: number
    position: FigurePosition
    color: string
    height: number
    age: number
    enabled: boolean
    shape: string
    opacity: number
    size: number
}

export type FigurePosition = {
    x: number,
    y: number,
    z: number
}

export type ViewSpecificationInput = {
    versionGroupingStrategy: GroupingStrategy
    versionGroupingChunkSize: number
    colorPalette: ColorPaletteInput
    agingGroupingStrategy: GroupingStrategy
    agingStepSize: number
    agingSteps: number
    mapperOptions: MapperStrategyOptionsInput
    mapperStrategy: MapperStrategyName
    mapperMetricName: string
    figureSize: number
    figureSpacing: number
    fileTypeShape: FileTypeShape[]
    showDeletedEntities: boolean
    showUnmappedEntities: boolean
    fileTypeOpacity: FileTypeOpacity[]
} | undefined

export type FileTypeOpacity = {
    fileType: string,
    opacity: number
}


export type FileTypeShape = {
    fileType: string,
    shapeName: string
}

export type ColorPaletteInput = {
    addColor: string
    renameColor: string
    moveColor: string
    deleteColor: string
    modifyColor: string
    baseColor: string
}

export enum GroupingStrategy {
    COMMIT_STRATEGY = "COMMIT_STRATEGY" ,
    TIMESTAMP_STRATEGY = "TIMESTAMP_STRATEGY"
}


export type MapperStrategyOptionsInput = {
    buckets: number
    maxHeight: number
}

export enum MapperStrategyName {
    BUCKET_COUNT_STRATEGY = "BUCKET_COUNT_STRATEGY",
    LINEAR_STRATEGY = "LINEAR_STRATEGY",
    NORMALIZER_STRATEGY = "NORMALIZER_STRATEGY",
    DEFAULT_STRATEGY = "DEFAULT_STRATEGY",
    NONE = "NONE",
    BUCKET_VALUE_STRATEGY = "BUCKET_VALUE_STRATEGY",
    BUCKET_VALUE_LINEAR_STRATEGY = "BUCKET_VALUE_LINEAR_STRATEGY"
}