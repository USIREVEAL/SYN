import {ProjectViewSpecs} from "../../../../types/ProjectContextProviderTypes";
import {GroupingStrategy} from "../../../../types/server/View.type";

export type IGroupingVersionStrategyForm = {
    projectViewSpecs: ProjectViewSpecs,
    onValueChange: (groupingStrategy: GroupingStrategy, chunkSize: number) => void,
}
