
# SYN

SYN is our tool to visualize and comprehend system’s evolution. It is composed of a set of modules, as follows:
- **Core**: it holds classes representing basic SYN concepts such as ProjectHistories, ProjectVersions, and FileVersions.
- **CLI**: provides a Command Line Interface (CLI) to users.
- **Analyzer**: implement the algorithms to analyze the project's history.
- **Server**: provides GraphQL endpoints to retrieve data.


## Requirements
- Java 18
- Docker

## Build
- run `build.sh`

## How to run the analysis
Once you built the docker containers run
- `mkdir syn_data`
- `alias syn='docker run --rm --volume "$(pwd)/syn_data:/syn_data" -e SYN_HOME=/syn_data syn-cli'`
- `syn project create -n :projectName -p :projectGitHubURL`
- `syn analyze auto -p 1 -t 1`

Once the project is analyzed you can run the server and inspect it through the web ui
- `docker run --rm -p 8080:8080 --volume "$(pwd)/syn_data:/syn_data" -e SYN_HOME=/syn_data syn-backend`