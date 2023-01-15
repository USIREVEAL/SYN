import './wdyr';
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import reportWebVitals from './test/reportWebVitals';
import { BrowserRouter } from "react-router-dom";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import {
    ApolloClient,
    InMemoryCache,
    ApolloProvider,
    HttpLink
} from "@apollo/client";
import ProjectRoutes from "./routes";
import {ViewProvider} from "./providers/ViewProvider/ViewProvider";
import {
    EntityDetailsProvider
} from "./providers/EntityDetailsProvider/EntityDetailsProvider";

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
});

export const graphqlCache = new InMemoryCache();
const client = new ApolloClient({
    link: new HttpLink({
        uri: process.env.REACT_APP_GRAPHQL_SERVER,
        fetch,
        credentials: 'same-origin'
    }),
    cache: graphqlCache
});


ReactDOM.render(
    <ApolloProvider client={client}>
        <BrowserRouter basename={process.env.PUBLIC_URL}>
            <ThemeProvider theme={theme}>
                {/*<React.StrictMode>*/}
                    <ProjectRoutes />
                {/*</React.StrictMode>*/}
            </ThemeProvider>
        </BrowserRouter>
    </ApolloProvider>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
