import Header from "./components/Header/";
import {Route, Routes} from "react-router-dom";
import ProjectPage from "./views/project";
import React from "react";
import Home from "./views/home";
import TestPage from "./test/TestPage";

function ProjectRoutes() {
    return (
        <div className="App" style={{display: "flex", flexDirection: "column"}}>
            <Header />
            <div style={{flex: "1 1 auto", display: "flex"}}>
                <Routes>
                    <Route path="/" element={<Home />} />
                    {/*<Route path="/project/:project/fileHistory/:fileHistory" element={<FileHistoryView />} />*/}
                    <Route path="/project/:projectID" element={<ProjectPage />} />
                    <Route path="/test" element={<TestPage />} />
                    {/*<Route path="/test2" element={<TestPage2 />} />*/}
                </Routes>
            </div>

        </div>
    );
}

export default ProjectRoutes