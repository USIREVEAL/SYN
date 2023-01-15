import BabylonProjectVisualizer from "./components/BabylonProjectVisualizer/";
import React, {useEffect} from "react";
import {useAnimation} from "../../hooks/useAnimation";


export function Project3DVisualizer() {
    // const {view} = useView()
    const ref = React.createRef<BabylonProjectVisualizer>();

    const animation = useAnimation()

    useEffect(() => {
        if (animation.viewAnimation) {
            ref.current?.updateEntityData(animation.viewAnimation)
        }
    }, [animation])


    return <BabylonProjectVisualizer ref={ref} viewAnimation={{
        id: -1,
        viewFigures: [],
        tsFrom: 0, tsTo: 0
    }}/>
}

