import React, {
    MutableRefObject,
    useEffect,
    useMemo,
    useRef,
} from "react";
import {Engine, Scene, useScene, useEngine} from "react-babylonjs";
import "@babylonjs/core/Debug/debugLayer";
import "@babylonjs/inspector";
import "../style.css"
import {
    ProjectViewSpecs
} from "../../../../../types/ProjectContextProviderTypes";
import {BabylonProjectVisualizerProps} from "../BabylonProjectVisualizer.types";
import {
    AbstractMesh, Axis, Camera,
    Color3, EventState,
    FreeCameraDeviceOrientationInput,
    HighlightLayer, IShadowLight,
    Matrix,
    Mesh, Quaternion, RenderTargetTexture, ShadowGenerator,
    Vector3, WebXRState
} from "@babylonjs/core";
import Core from "@babylonjs/core"
// import Tools from "@babylonjs/core/Misc/tools";
import { Tools } from '@babylonjs/core/Misc/tools'
import {useEntityDetails} from "../../../../../hooks/useEntityDetails";
import {useViewDetails} from "../../../../../hooks/useViewDetails";
import {FigureShape} from "../../../../../types/BabylonVisualizerTypes";
import {ViewFigure} from "../../../../../types/server/View.type";
import {Box, CircularProgress} from "@mui/material";






function EntityGrid({viewAnimation}: BabylonProjectVisualizerProps) {
    if (viewAnimation === undefined) return null;

    return viewAnimation.viewFigures.length === 0 ? null : (
        <>
            {viewAnimation.viewFigures.map(viewFigure => {
                if (viewFigure.fileHistoryId !== -1) {
                    const color = Color3.FromHexString(viewFigure.color);
                    return <DynamicShape viewFigure={viewFigure}>
                        <standardMaterial name={"mat-block-" + viewFigure.fileHistoryId} diffuseColor={color} specularColor={Color3.Black()} />
                    </DynamicShape>
                }
            })}

        </>
    )

}

function DynamicShape({viewFigure, children}: {viewFigure: ViewFigure, children: JSX.Element}) {

    switch (viewFigure.shape) {
        case FigureShape.BOX:
            return (
                <box
                    name={"block-" + viewFigure.fileHistoryId}
                    size={viewFigure.size}
                    position={new Vector3(viewFigure.position.x, (viewFigure.height / 2) * 5, viewFigure.position.z)}
                    key={viewFigure.fileHistoryId}
                    scaling-y={viewFigure.height + 0.1}
                    visibility={viewFigure.opacity}
                >
                    {children}
                </box>
            )
        case FigureShape.SPHERE:
            return (
                <sphere
                    name={"block-" + viewFigure.fileHistoryId}
                    diameter={viewFigure.size}
                    position={new Vector3(viewFigure.position.x, (viewFigure.height / 2) * 5, viewFigure.position.z)}
                    key={viewFigure.fileHistoryId}
                    scaling-y={viewFigure.height + 0.1}
                    visibility={viewFigure.opacity}
                >
                    {children}
                </sphere>
            )

        case FigureShape.CYLINDER:
            return (
                <cylinder
                    name={"block-" + viewFigure.fileHistoryId}
                    diameter={viewFigure.size }
                    position={new Vector3(viewFigure.position.x, (viewFigure.height / 2) * 5, viewFigure.position.z)}
                    key={viewFigure.fileHistoryId}
                    height={viewFigure.height * 10 + 0.1}
                    visibility={viewFigure.opacity}
                >
                    {children}
                </cylinder>
            )
        case FigureShape.CONE:
            return (
                <cylinder
                    name={"block-" + viewFigure.fileHistoryId}
                    diameter={viewFigure.size * 2}
                    diameterTop={0}
                    position={new Vector3(viewFigure.position.x, 0, viewFigure.position.z)}
                    key={viewFigure.fileHistoryId}
                    height={viewFigure.height * 10 + 0.1}
                    visibility={viewFigure.opacity}
                >
                    {children}
                </cylinder>
            )
        case FigureShape.TRIANGULAR:
            return (
                <cylinder
                    name={"block-" + viewFigure.fileHistoryId}
                    diameter={viewFigure.size}
                    position={new Vector3(viewFigure.position.x, 0, viewFigure.position.z)}
                    key={viewFigure.fileHistoryId}
                    height={viewFigure.height * 5}
                    scaling-y={1}
                    tessellation={3}
                    visibility={viewFigure.opacity}
                >
                    {children}
                </cylinder>
            )


        default:
            return null
    }
}



export function InteractiveBabylonProjectVisualizer({viewAnimation}: BabylonProjectVisualizerProps) {
    const {loadFileHistory} = useEntityDetails();
    const highlightLayerFH = useRef<HighlightLayer>(null);
    const highlightLayerSH = useRef<HighlightLayer>(null);
    const sceneRef = React.useRef<Core.Scene>();
    const engineRef = React.useRef<Core.Engine>();
    const timeoutRef = React.useRef<NodeJS.Timeout | undefined>()
    const timeoutRef2 = React.useRef<NodeJS.Timeout | undefined>()
    const {debuggerSettings} = useViewDetails();

    function handleMeshClick(mesh: AbstractMesh) {
        let entityID = parseInt(mesh.name.split("-")[1])
        if (!isNaN(entityID)) {
            loadFileHistory(entityID);
            if (highlightLayerFH.current) {
                const highlightLayer = highlightLayerFH.current;
                highlightLayer.removeAllMeshes();
                if (isMesh(mesh)) {
                    highlightLayer.addMesh(mesh, Color3.White());
                }
            }
        } else {
            loadFileHistory(-1);
            if (highlightLayerFH.current) {
                const highlightLayer = highlightLayerFH.current;
                highlightLayer.removeAllMeshes();
            }
        }
    }

    if (timeoutRef.current !== undefined) {
        clearTimeout(timeoutRef.current)
    }

    if (debuggerSettings.computeShadows) {
        timeoutRef.current = setTimeout(() => {
            const scene = sceneRef.current;
            if (scene) {
                scene.executeWhenReady(() => {
                    let shadowLight = scene.getLightByID("shadow-light");
                    if (shadowLight) {

                        if(shadowLight.getShadowGenerator() !== null && shadowLight.getShadowGenerator() !== undefined) {
                            // @ts-ignore
                            shadowLight.getShadowGenerator().dispose()
                        }

                        let shadowGenerator00 = new ShadowGenerator(1024, shadowLight as IShadowLight);

                        scene.getActiveMeshes().forEach(mesh => {
                            if (mesh.name !== "ground") {
                                // @ts-ignore
                                shadowGenerator00.getShadowMap().renderList.push(mesh)
                            }
                        })
                        // shadowGenerator00.useCloseExponentialShadowMap = true;
                        // @ts-ignore
                        // shadowGenerator00.getShadowMap().refreshRate = RenderTargetTexture.REFRESHRATE_RENDER_ONCE;

                    }
                });
            }
        }, debuggerSettings.versionAutoplaySpeed * 1.2)
    }


    useEffect(() => {
        const scene = sceneRef.current;
        const engine = engineRef.current;

        if (debuggerSettings.makeScreenshot) {
            if (scene != null && engine !== null && engine !== undefined && viewAnimation) {
                timeoutRef2.current = setTimeout(() => {
                    scene.executeWhenReady(() => {
                        Tools.CreateScreenshotUsingRenderTarget(
                            engine,
                            scene.getCameraByName("camera")!,
                            {width:1920, height:1080},
                            (img) => {
                                const canvas = document.createElement('canvas');
                                const ctx = canvas.getContext("2d");
                                canvas.width = 1920;
                                canvas.height = 1080;

                                const rectXPos = 1500;
                                const rectYPos = 1000;
                                const rectWidth = 400;
                                const rectHeight = 50;

                                const image = new Image();
                                image.onload = function() {
                                    if (ctx) {
                                        // Background image
                                        ctx.drawImage(image, 0, 0);

                                        // Rect border
                                        ctx.fillStyle='#FFF';
                                        let thickness = 3;
                                        ctx.fillRect(rectXPos - (thickness), rectYPos - (thickness), rectWidth + (thickness * 2), rectHeight + (thickness * 2));

                                        // Rect background
                                        ctx.fillStyle='#FFF';
                                        ctx.fillRect(rectXPos, rectYPos, rectWidth, rectHeight);

                                        // Text
                                        const tsFrom = new Date(viewAnimation.tsFrom * 1000);
                                        ctx.font = "20pt Arial";
                                        ctx.fillStyle = 'black';
                                        ctx.fillText(viewAnimation.id + " - " + tsFrom.toUTCString().substring(0, tsFrom.toUTCString().length - 11), rectXPos + 20, rectYPos + 35);

                                        // Download
                                        var lnk = document.createElement('a'), e;
                                        lnk.download = "Animation" + String(viewAnimation.id).padStart(4, "0");
                                        lnk.href = canvas.toDataURL("image/png;base64");
                                        if (document.createEvent) {
                                            e = document.createEvent("MouseEvents");
                                            e.initMouseEvent("click", true, true, window,
                                                0, 0, 0, 0, 0, false, false, false,
                                                false, 0, null);

                                            lnk.dispatchEvent(e);
                                        }
                                    }


                                };
                                image.src = img
                            }
                        );
                    });
                }, debuggerSettings.versionAutoplaySpeed * 2)
            }
        }



        if (debuggerSettings.showDebugLayer && scene) {
             scene!.debugLayer.show();
        }

        if (debuggerSettings.showVRExperience && scene) {
            scene!.createDefaultXRExperienceAsync({
                floorMeshes: [scene?.getMeshByID("ground")!],
                disableTeleportation: true
            }).then(xrHelper => {
                var webXRInput = xrHelper.input
                let rotationValue = 0;

                xrHelper.baseExperience.onStateChangedObservable.add(function (state: WebXRState) {
                    switch (state) {
                        case WebXRState.IN_XR:
                        case WebXRState.ENTERING_XR:
                            webXRInput.xrCamera.position = Vector3.Zero();
                            webXRInput.xrCamera.position.z = 0;
                            webXRInput.xrCamera.position.y = 100;
                            webXRInput.xrCamera.rotationQuaternion = Quaternion.Identity();
                            break;
                    }
                });

                webXRInput.onControllerAddedObservable.add((controller) => {
                    const moveSpeed = 1;
                    controller.onMotionControllerInitObservable.add((controller) => {
                        if (controller.handness === "left") {
                            let ids = controller.getComponentIds()
                            for (let i = 0; i < ids.length; i++) {
                                let component = controller.getComponent(ids[i])
                                switch (ids[i]) {
                                    case "xr-standard-thumbstick":
                                        component.onAxisValueChangedObservable.add(function (
                                            eventData: { x: number, y: number }, _: EventState) {
                                            const {x, y} = eventData;
                                            webXRInput.xrCamera.position = webXRInput.xrCamera.position.add(new Vector3(x * moveSpeed, 0, -y * moveSpeed));
                                        })
                                        break
                                }
                            }
                        } else if (controller.handness === "right") {

                            let ids = controller.getComponentIds()
                            for (let i = 0; i < ids.length; i++) {
                                let component = controller.getComponent(ids[i])
                                switch (ids[i]) {
                                    case "xr-standard-thumbstick":
                                        component.onAxisValueChangedObservable.add(function (
                                            eventData: { x: number, y: number }, _: EventState) {
                                            const {x, y} = eventData;
                                            webXRInput.xrCamera.position = webXRInput.xrCamera.position.add(new Vector3(0, -y * moveSpeed, 0));
                                            webXRInput.xrCamera.rotation = webXRInput.xrCamera.rotation.add(new Vector3(-x * moveSpeed, -x * moveSpeed, -x * moveSpeed))
                                        })
                                        break
                                }
                            }
                        }
                    })
                })
            })
        }
    }, [viewAnimation, debuggerSettings])

/*    useEffect(() => {
        if (highlightLayerSH.current && sceneRef.current) {
            const scene = sceneRef.current;
            const highlightLayer = highlightLayerSH.current;
            highlightLayer.removeAllMeshes();
            // projectViewState.highlightedFileHistoryIDs.forEach(id => {
            //     const mesh = scene.getMeshByID("block-" + id);
            //     if (mesh && isMesh(mesh)) {
            //         highlightLayer.addMesh(mesh, Color3.Blue());
            //     }
            // })
        }
    }, [projectViewState.highlightedFileHistoryIDs])*/

/*    useEffect(() => {
        if (highlightLayerSH.current && sceneRef.current) {
            const scene = sceneRef.current;
            const mesh = scene.getMeshByID("block-" + projectViewState.selectedFileHistoryID);
            if (mesh)
                handleMeshClick(mesh)
        }
    }, [projectViewState.selectedFileHistoryID])*/

    function Test({sceneRef}: {sceneRef: MutableRefObject<Core.Scene | undefined>}) {
        const scene = useScene();
        const engine = useEngine();

        if (scene)
            sceneRef.current = scene;
        if (engine)
            engineRef.current = engine;

        return null
    }

    function isMesh(mesh: any): mesh is Mesh {
        return (mesh as Mesh).name !== undefined
    }

    return useMemo(() => {

        let groundSize = 0

        if (viewAnimation) {
            console.info("Rendering animation " + viewAnimation.id)
            console.info(viewAnimation)

            let figureGround = viewAnimation.viewFigures.find(vf => vf.shape === "GROUND")
            if (figureGround) {
                groundSize = figureGround.size
            }
        }

        if (groundSize === 0) {
            // @ts-ignore
            return <div style={{position: "absolute", left: "50%", top: "50%"}}>
                <CircularProgress />
            </div>
        }

        return <div style={{width: "100%", height: "calc(100vh - 40px)"}}>

            <Engine antialias canvasId="babylonJS" engineOptions={{ preserveDrawingBuffer: true }}>
                <Scene onMeshPicked={handleMeshClick}>
                    <arcRotateCamera
                        name="camera"
                        alpha={0}
                        beta={0}
                        radius={600}
                        target={Vector3.Zero()}
                        position={new Vector3(0, 10, 0)}
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
                        intensity={0.9}
                        direction={Vector3.Up()}
                    />

                    <pointLight name="shadow-light"
                                      position={new Vector3(-(groundSize / 2), 350, -(groundSize / 2))}
                                      intensity={0.5}
                                      shadowMinZ={1}
                                      shadowMaxZ={groundSize * 1.5}>
                    </pointLight>

                    <ground name='ground' width={groundSize} height={groundSize} receiveShadows={true}/>
                    <highlightLayer name='highlight-fileHistory' ref={highlightLayerFH} />
                    <highlightLayer name='highlight-search' ref={highlightLayerSH} />
                    <EntityGrid viewAnimation={viewAnimation} />
                    <Test sceneRef={sceneRef}/>
                </Scene>
            </Engine>
        </div>
    }, [viewAnimation])
}