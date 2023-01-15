import React from "react";
import {ViewAnimation} from "../../types/server/View.type";

export type AnimationProviderProps = {
    children: React.ReactChildren | React.ReactChildren[] | JSX.Element[]
}

export type AnimationProviderContextState = {
    viewAnimation: ViewAnimation | undefined,
    loading: boolean
    loadAnimation: (animationId: number) => void
}



