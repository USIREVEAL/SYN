import {StaticBabylonProjectVisualizer} from "./static/StaticBabylonProjectVisualizer";
import {InteractiveBabylonProjectVisualizer} from "./interactive/InteractiveBabylonProjectVisualizer";


import React from "react";
import {BabylonProjectVisualizerProps, BabylonProjectVisualizerState} from "./BabylonProjectVisualizer.types";
import {ViewAnimation} from "../../../../types/server/View.type";

class BabylonProjectVisualizer extends React.Component<BabylonProjectVisualizerProps, BabylonProjectVisualizerState> {
    constructor(props: Readonly<BabylonProjectVisualizerProps>) {
        super(props);
        this.state = {
            viewAnimation: props.viewAnimation,
        };
    }

    // shouldComponentUpdate(nextProps: Readonly<BabylonProjectVisualizerProps>, nextState: Readonly<BabylonProjectVisualizerState>, nextContext: any): boolean {
    //     if (nextState.entityData.projectVersionID === -1) return false
    //     return nextState.entityData.projectVersionID != this.state.entityData.projectVersionID;
    // }

    updateEntityData(newViewAnimation: ViewAnimation) {
        this.setState({viewAnimation: newViewAnimation})
    }

    render() {
        return <InteractiveBabylonProjectVisualizer viewAnimation={this.state.viewAnimation}/>
    }
}

export default BabylonProjectVisualizer