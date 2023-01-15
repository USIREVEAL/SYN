import React from "react";
import {UI2} from "./TestPage2";
import {Button} from "@mui/material";
import {useQuery} from "@apollo/client";
import {GET_FILE_HISTORY_WITH_FILE_VERSIONS} from "../api/Queries";

export default function TestComponent2() {
	const renderCounter  = React.useRef(0);


	const {entity, updateValue} = React.useContext(UI2);

	const {data} = useQuery(GET_FILE_HISTORY_WITH_FILE_VERSIONS,  { variables: { projectName: "JetUML", entityID: entity} });



	return <>
		<div style={{backgroundColor: "blue"}}>
			Entity: {entity} <br />
			{data && data.getEntity.name}
			<br />
			Renders: {renderCounter.current}
			<br />
			<Button onClick={() => {updateValue({type: 'e'})}}>Entity</Button>
			<Button onClick={() => {updateValue({type: 'v'})}}>Version</Button>
		</div>
	</>

}