import React from "react";
import {Engine, Scene, useScene} from "react-babylonjs";
import "@babylonjs/core/Debug/debugLayer";
import "@babylonjs/inspector";
import "../style.css"
import {Matrix, Mesh, Vector3} from "@babylonjs/core";
import {BabylonProjectVisualizerProps} from "../BabylonProjectVisualizer.types";


const FIGURE_SIZE = 5;

export function StaticBabylonProjectVisualizer({viewAnimation}: BabylonProjectVisualizerProps) {

    // const {projectViewSpecs} = useProjectViewState();
    // const tmp = useRef(1)

    // const onSceneReady = (scene: any) => {
    //     const camera = new ArcRotateCamera("camera", -Math.PI/5, Math.PI/3, 10, Vector3.Zero(), scene);
    //     camera.attachControl(scene.getEngine().getRenderingCanvas(), true);
    //     const light = new DirectionalLight("light", new Vector3(-1, -1, -1), scene);
    //     light.intensity = 0.7;
    //     const box = BoxBuilder.CreateBox("root", {size: 5});
    //
    //     const sg = new ShadowGenerator(1024, light);
    //
    //     //@ts-ignore
    //     sg.getShadowMap().renderList.push(box);
    //
    //     light.shadowMinZ = 0;
    //     light.shadowMaxZ = 3;
    //     const ground = MeshBuilder.CreateGround("ground", {width: 300, height: 300}, scene);
    //     ground.receiveShadows = true;
    //
    // };
    //
    function getColorVector(colorHex: string) {
        let intColor = parseInt(colorHex, 16)
        return [
            ((intColor & 0xff0000) >> 16) / 255,
            ((intColor & 0x00ff00) >> 8) / 255,
            ((intColor & 0x0000ff) >> 0) / 255,
            1
        ]
    }

    function ThinInstances() {
        const scene = useScene();
        if (viewAnimation === undefined) return  null;



        const viewFigures = Array.from(viewAnimation.viewFigures)


        // scene.debugLayer.show();

        const bufferMatrices = new Float32Array(viewFigures.length * 16);
        const bufferColor = new Float32Array(viewFigures.length * 4);

        for (let i = 0; i < viewFigures.length; i++) {
            const figure = viewFigures[i];
            const fileHistoryId = i;

            const position = new Vector3(figure.position.x, figure.position.y, figure.position.z);
            const matrix = Matrix.FromArray(new Float32Array([
                1, 0, 0, 0,
                0, figure.height, 0, 0,
                0, 0, 1, 0,
                position.x, ((figure.height + 0.05) / 2) * FIGURE_SIZE, position.z, 1
            ]))

            matrix.copyToArray(bufferMatrices, 16 * fileHistoryId);
            const color = getColorVector(figure.color.substring(1));
            bufferColor[fileHistoryId * 4] = color[0]
            bufferColor[fileHistoryId * 4 + 1] = color[1]
            bufferColor[fileHistoryId * 4 + 2] = color[2]
            bufferColor[fileHistoryId * 4 + 3] = color[3]
        }
        viewFigures.forEach((figure) => {
            console.log(figure.fileHistoryId)

        })
        console.log(bufferMatrices)
        const box = (scene?.getMeshByID("box") as Mesh)
        // const box = BoxBuilder.CreateBox("root", {size: 5});
        if (box) {
            box.thinInstanceSetBuffer("matrix", bufferMatrices, 16, true);
            box.thinInstanceSetBuffer("color", bufferColor, 4, true);
        }
        //
        return <></>
        //     const sg = new ShadowGenerator(1024, light);
        // @ts-ignore
        //     sg.getShadowMap().renderList.push(box);
        //
        //     light.shadowMinZ = 0;
        //     light.shadowMaxZ = 3;
        //
        //
        //     scene.debugLayer.show({ embedMode: true }).then(() => {
        //         scene.debugLayer.select(light);
        //     });
    }


    // scene.onBeforeRenderObservable.add(()=>{
    //     light.position = camera.position;
    //     light.setDirectionToTarget(camera.getFrontPosition(1))
    // })
    // }, [projectViewSpecs, entityData])

    return <div style={{width: "100%", height: "calc(100vh - 40px)"}}>
        <Engine antialias canvasId="babylonJS">
            <Scene>
                <arcRotateCamera
                    name="camera"
                    alpha={0}
                    beta={0}
                    radius={30}
                    target={Vector3.Zero()}
                    position={Vector3.Zero()}
                    // lowerAlphaLimit={0}
                    // upperAlphaLimit={0}
                    //  lowerBetaLimit={1.6}
                    upperBetaLimit={1.5}
                    lowerRadiusLimit={20}
                    zoomOnFactor={1.5}
                    maxZ={50000}
                />

                <hemisphericLight
                    name="light1"
                    intensity={1}
                    direction={Vector3.Up()}
                />

                <ground name='ground' width={300} height={300}/>
                <box name='box' size={FIGURE_SIZE}/>
                <ThinInstances/>
            </Scene>
        </Engine>
    </div>

}