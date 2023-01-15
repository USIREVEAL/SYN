import React, {useRef, useState} from 'react'
import {Engine, Scene, useBeforeRender, useClick,} from 'react-babylonjs'
import {Color3, Color4, Vector3} from '@babylonjs/core'
import {FigureShape} from "../../../../../../types/BabylonVisualizerTypes";

const DefaultScale = new Vector3(1, 1, 1)
const BiggerScale = new Vector3(1.25, 1.25, 1.25)

const SpinningBox = (props: { shape: FigureShape, opacity: number}) => {
    // access Babylon scene objects with same React hook as regular DOM elements
    const boxRef = useRef(null)

    const [clicked, setClicked] = useState(false)
    useClick(() => setClicked((clicked) => !clicked), boxRef)

    // This will rotate the box on every Babylon frame.
    const rpm = 5
    useBeforeRender((scene) => {
        if (boxRef.current) {
            // Delta time smoothes the animation.
            var deltaTimeInMillis = scene.getEngine().getDeltaTime()

            // @ts-ignore
            boxRef.current.rotation.y +=
                (rpm / 60) * Math.PI * 2 * (deltaTimeInMillis / 1000)
        }
    })

    const COLOR = '#c90c46'

    switch (props.shape) {
        case FigureShape.BOX:
            return (
                <box
                    name={"entity"}
                    ref={boxRef}
                    size={3}
                    position={new Vector3(0, 0, 0)}
                    visibility={props.opacity}
                >
                    <standardMaterial
                        name={`entity-mat`}
                        diffuseColor={Color3.FromHexString(COLOR)}
                    />
                </box>
            )
        case FigureShape.SPHERE:
            return (
                <sphere
                    name={"entity"}
                    ref={boxRef}
                    position={new Vector3(0, 0, 0)}
                    visibility={props.opacity}
                >
                    <standardMaterial
                        name={`entity-mat`}
                        diffuseColor={Color3.FromHexString(COLOR)}
                    />
                </sphere>
            )

        case FigureShape.CYLINDER:
            return (
                <cylinder
                    name={"entity"}
                    ref={boxRef}
                    height={10}
                    position={new Vector3(0, 0, 0)}
                    visibility={props.opacity}
                >
                    <standardMaterial
                        name={`entity-mat`}
                        diffuseColor={Color3.FromHexString(COLOR)}
                    />
                </cylinder>
            )
        case FigureShape.CONE:
            return (
                <cylinder
                    name={"entity"}
                    ref={boxRef}
                    height={4}
                    position={new Vector3(0, 0, 0)}
                    diameterTop={0}
                    visibility={props.opacity}
                >
                    <standardMaterial
                        name={`entity-mat`}
                        diffuseColor={Color3.FromHexString(COLOR)}
                    />
                </cylinder>
            )
        case FigureShape.TRIANGULAR:
            return (
                <cylinder
                    name={"entity"}
                    ref={boxRef}
                    height={4}
                    position={new Vector3(0, 0, 0)}
                    tessellation={3}
                    visibility={props.opacity}
                >
                    <standardMaterial
                        name={`entity-mat`}
                        diffuseColor={Color3.FromHexString(COLOR)}
                    />
                </cylinder>
            )


        default:
            return null
    }

}

export const BabylonWithSpinningFigure = (props: { shape: FigureShape, fileType: string, opacity: number}) => (
    <div style={{width: "100%"}}>
        <Engine antialias canvasId={"babylon-canvas-" + props.fileType} width={200} height={100}>
            <Scene
                clearColor={Color4.FromHexString('#121212')}
            >
                <arcRotateCamera
                    name="camera1"
                    target={Vector3.Zero()}
                    alpha={Math.PI / 2}
                    beta={Math.PI / 4}
                    radius={8}

                />
                <hemisphericLight
                    name="light1"
                    intensity={0.7}
                    direction={Vector3.Up()}
                />
                <SpinningBox shape={props.shape} opacity={props.opacity}/>
            </Scene>
        </Engine>
    </div>
)