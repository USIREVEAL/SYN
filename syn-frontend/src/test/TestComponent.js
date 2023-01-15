import React from "react";
import {UI2} from "./TestPage2";
import {Button} from "@mui/material";
import TestUtility from "./TestUtility";

export default function TestComponent() {
	const renderCounter  = React.useRef(0);


	const {version, updateValue} = React.useContext(UI2);

	//const {data, loading} = useQuery(GET_ENTITY_DETAILS_WITH_HISTORY,  { variables: { projectName: "JetUML", entityID: version} });


	return <>
		<div style={{backgroundColor: "red"}}>
			Version: {version} <br />
			Renders: {renderCounter.current}s

			<Button onClick={() => {updateValue({type: 'e'})}}>Entity</Button>
			<Button onClick={() => {updateValue({type: 'v'})}}>Version</Button>
			<Button onClick={() => {TestUtility.test()}}>Test</Button>
		</div>
	</>
}