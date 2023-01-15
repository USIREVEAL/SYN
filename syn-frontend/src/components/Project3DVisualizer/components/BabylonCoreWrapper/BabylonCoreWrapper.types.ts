import {Nullable} from "@babylonjs/core/types";
import {EngineOptions} from "@babylonjs/core/Engines/thinEngine";
import {SceneOptions} from "@babylonjs/core/scene";
import {Scene} from "@babylonjs/core";
import React, {LegacyRef, MutableRefObject} from "react";

export type BabylonCoreWrapperArgs = {
    antialias?: boolean,
    adaptToDeviceRatio?: boolean,
    engineOptions?: EngineOptions,
    sceneOptions?: SceneOptions,
    onRender?:  (scene: Scene) => void,
    onSceneReady:  (scene: Scene) => void,
    sceneRef: MutableRefObject<Scene | null>
}
