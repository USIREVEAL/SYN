# syntax=docker/dockerfile:1

# Stage 0, "build-stage", based on Node.js to build the frontend
FROM node:16
ENV REACT_APP_GRAPHQL_SERVER http://localhost:8080/graphql
WORKDIR /app
COPY package*.json yarn.lock /app/
RUN ls
RUN yarn install
COPY . /app/
RUN ls
RUN yarn build

# Stage 1, based on NGINX to provide a configuration to be used with react-router
FROM nginx:alpine
COPY --from=0 /app/build /usr/share/nginx/html
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
