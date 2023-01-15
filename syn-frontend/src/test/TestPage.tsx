import React from "react";
import {
    BabylonWithSpinningFigure
} from "../components/DebuggerSettingsForm/components/MetricSelectionStep/components/BabylonWithSpinningFigure/BabylonWithSpinningFigure";
import {FigureShape} from "../types/BabylonVisualizerTypes";

const TestPage = ()  => {

    return <>
        <div style={{backgroundColor: "black", width: "100%"}}>
            <BabylonWithSpinningFigure shape={FigureShape.TRIANGULAR} fileType={"a"} opacity={1}/>

        </div>
    </>
}


export default TestPage;