import React from "react";
import TestComponent from "./TestComponent";
import TestComponent2 from "./TestComponent2";

interface UI {
    version: number;
    entity: number;
    settings: {
        a: boolean;
        b: boolean;
    }
}

type Action = {
    type: string
}

interface UIProvider extends UI {
    updateValue: React.Dispatch<Action>
}

const UI2 = React.createContext<UI | null>(null);

const TestPage2 = () => {
    function reducer(state: UI, action: Action): UI {
        switch (action.type) {
            case 'e':
                return {...state, entity: state.entity + 1};
            case 'v':
                return {...state, version: state.version + 1};
            default:
                throw new Error();
        }
    }

    const initialState: UI = {
        version: 0,
        entity: 0,
        settings: {
            a: true,
            b: false
        }
    }

    const [state, dispatch] = React.useReducer(reducer, initialState);

    const UIProvider: UIProvider = { ...state, updateValue: dispatch };

    return <>
        <UI2.Provider value={UIProvider}>
            <TestComponent />
            <TestComponent2 />
        </UI2.Provider >
    </>
}

export default TestPage2;
export {UI2};