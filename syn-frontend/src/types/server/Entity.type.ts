export type Entity = {
    id: number
}

export type CodeEntity = Entity & {
    metrics: Array<Metric>
}

export type Metric ={
    name: string,
    value: number
}




