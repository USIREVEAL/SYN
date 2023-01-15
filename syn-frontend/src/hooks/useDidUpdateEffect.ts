import React, {useEffect, useRef} from "react";


export function useDidUpdateEffect(fn:  React.EffectCallback, inputs: React.DependencyList | undefined) {
    const didMountRef = useRef(false);

    useEffect(() => {
        if (didMountRef.current) {
            return fn();
        }
        didMountRef.current = true;
    }, inputs);
}