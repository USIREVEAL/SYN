import {useEffect, useRef} from "react";
import {Engine, Scene} from "@babylonjs/core";
import {BabylonCoreWrapperArgs} from "./BabylonCoreWrapper.types";

export function BabylonCore({
                                sceneRef,
                                antialias,
                                engineOptions,
                                adaptToDeviceRatio,
                                sceneOptions,
                                onRender,
                                onSceneReady,
                                ...rest
                            }: BabylonCoreWrapperArgs) {
    const tmp = useRef(1)
    const canvasRef = useRef(null)

    // set up basic engine and scene
    useEffect(() => {
        const {current: canvas} = canvasRef;

        if (!canvas) return;

        const engine = new Engine(canvas, antialias, engineOptions, adaptToDeviceRatio);
        const scene = new Scene(engine, sceneOptions);
        if (scene.isReady()) {
            onSceneReady(scene);
        } else {
            scene.onReadyObservable.addOnce((scene) => onSceneReady(scene));
        }

        engine.runRenderLoop(() => {
            if (typeof onRender === "function") onRender(scene);
            scene.render();
        });

        const resize = () => {
            scene.getEngine().resize();
        };

        if (window) {
            window.addEventListener("resize", resize);
        }

        return () => {
            scene.getEngine().dispose();

            if (window) {
                window.removeEventListener("resize", resize);
            }
        };
    }, [antialias, engineOptions, adaptToDeviceRatio, sceneOptions, onRender, onSceneReady]);


    tmp.current++
    return <>
        {tmp.current}
        <canvas ref={canvasRef} {...rest} style={{width: "100%"}}/>
    </>
};