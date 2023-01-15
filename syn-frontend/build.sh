#/bin/sh

REACT_APP_GRAPHQL_SERVER=http://localhost:8080/graphql
yarn build
serve -s build
