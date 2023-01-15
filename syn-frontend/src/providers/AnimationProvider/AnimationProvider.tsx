import React, {useContext, useEffect} from "react";
import {ViewContext} from "../ViewProvider/ViewProvider";
import {AnimationProviderContextState} from "./AnimationProvider.types";
import {useEntityDetails} from "../../hooks/useEntityDetails";
import {View, ViewAnimation} from "../../types/server/View.type";
import {useQuery} from "@apollo/client";
import {Query, QueryPartialView, QueryView} from "../../types/server/Query.types";
import {GET_PARTIAL_VIEW, GET_VIEW} from "../../api/Queries";
import {debuggerSettingToViewSpecification} from "../../helpers/ProjectViewSpecHelper";
import {useProject} from "../../hooks/useProject";

export const AnimationViewContext = React.createContext<AnimationProviderContextState>({
    viewAnimation: undefined,
    loadAnimation: () => {},
    loading: false
});

const ANIMATIONS_PER_VIEWS = 100

export function AnimationProvider({children}: any) {
    const [viewAnimationId, setViewAnimationId] = React.useState<number>(1);
    const [viewAnimation, setAnimation] = React.useState<ViewAnimation|undefined>();
    const [currentView, setCurrentView] = React.useState<View|undefined>();
    const [futureView, setFutureView] = React.useState<View|undefined>();
    const {debuggerSettings, view, viewLoading} = useContext(ViewContext);
    const {loadProjectVersions} = useEntityDetails();
    const project = useProject()



    useEffect(() => {
        setCurrentView(view)
        setFutureView(undefined)
    }, [view])

    let skipFutureViewQuery = true;
    if (view && futureView === undefined && viewAnimationId + ANIMATIONS_PER_VIEWS <= view.animationsCount && viewAnimationId % ANIMATIONS_PER_VIEWS >= ANIMATIONS_PER_VIEWS / 2) {
        skipFutureViewQuery = false;
    }

    const {data: viewQueryData} = useQuery<Query, QueryPartialView>(GET_PARTIAL_VIEW, {
        variables: {
            projectId: project.id,
            viewSpecification: debuggerSettingToViewSpecification(debuggerSettings),
            viewAnimationId: Math.ceil(viewAnimationId / ANIMATIONS_PER_VIEWS) * ANIMATIONS_PER_VIEWS
        },
        skip: skipFutureViewQuery,
        fetchPolicy: "no-cache"
    });

    const {data: currentViewQueryData, loading: currentViewLoading} = useQuery<Query, QueryPartialView>(GET_PARTIAL_VIEW, {
        variables: {
            projectId: project.id,
            viewSpecification: debuggerSettingToViewSpecification(debuggerSettings),
            viewAnimationId: Math.floor((viewAnimationId - 1) / ANIMATIONS_PER_VIEWS) * ANIMATIONS_PER_VIEWS
        },
        skip: currentView !== undefined,
        fetchPolicy: "no-cache"
    });

    useEffect(() => {
        if (viewQueryData) {
            setFutureView(viewQueryData.partialView)
        }
    }, [viewQueryData])

    useEffect(() => {
        if (currentViewQueryData) {
            setCurrentView(currentViewQueryData.partialView)
        }
    }, [currentViewQueryData])

    useEffect(() => {
        if (currentView) {
            if (viewAnimationId >= currentView.viewAnimationList[0].id && viewAnimationId <= currentView.viewAnimationList[ currentView.viewAnimationList.length - 1].id) {
                let newViewAnimation: ViewAnimation | undefined = undefined;
                if (currentView.viewAnimationList) {
                    const animationIndex = ((viewAnimationId - 1) % ANIMATIONS_PER_VIEWS)
                    if (currentView.viewAnimationList[animationIndex]) {
                        newViewAnimation = currentView.viewAnimationList[animationIndex]
                        if (viewAnimation === undefined || viewAnimation.id !== newViewAnimation.id) {
                            setAnimation(newViewAnimation)
                        }
                    }
                }
            } else if (futureView && futureView.viewAnimationList[0].id === viewAnimationId) {
                setCurrentView(futureView)
                setFutureView(undefined)
            } else {
                setCurrentView(undefined)
                setFutureView(undefined)
            }
        }

    }, [viewAnimationId, currentView, futureView])

    useEffect(() => {
        if (viewAnimation) {
            if (viewAnimation.projectVersionIds)
                loadProjectVersions(viewAnimation.projectVersionIds)
        }
    }, [viewAnimation])


    const viewContext = {
        viewAnimation: viewAnimation,
        loadAnimation: setViewAnimationId,
        loading: viewLoading || currentViewLoading
    }

    return <AnimationViewContext.Provider value={viewContext}>
        {children}
    </AnimationViewContext.Provider>
}


