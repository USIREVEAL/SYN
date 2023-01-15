import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Home from './home/Home';
import reportWebVitals from './test/reportWebVitals';
import { BrowserRouter } from "react-router-dom";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import {
    ApolloClient,
    InMemoryCache,
    ApolloProvider,
    HttpLink
} from "@apollo/client";

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
});

const client = new ApolloClient({
    link: new HttpLink({
        uri: 'http://localhost:8080/graphql',
        fetch,
        credentials: 'same-origin'
    }),

    cache: new InMemoryCache()
});

console.log(process.env.TEST)

ReactDOM.render(
    <ApolloProvider client={client}>
        <BrowserRouter basename={process.env.PUBLIC_URL}>
            <ThemeProvider theme={theme}>
                <React.StrictMode>
                    <Home />
                </React.StrictMode>
            </ThemeProvider>
        </BrowserRouter>
    </ApolloProvider>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
