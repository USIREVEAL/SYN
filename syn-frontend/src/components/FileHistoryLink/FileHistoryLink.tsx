import React from "react";
import {useEntityDetails} from "../../hooks/useEntityDetails";
import {FileHistoryLinkTypes} from "./FileHistoryLink.types";


export function FileHistoryLink ({fileHistoryID, children}: FileHistoryLinkTypes) {
    const {loadFileHistory} = useEntityDetails()

    function selectFileHistory() {
        loadFileHistory(fileHistoryID)
    }

    return <div onClick={selectFileHistory} style={{cursor: "pointer"}}>
        {children}
    </div>
}
