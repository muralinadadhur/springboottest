Checklist for CICD releases
===========================

## Table of Contents

- [Synopsis](#synopsis)
- [Features](#features)
- [Pre-Requisites](#pre-requisites)
- [QuickStart](#quickstart)
- [Contributors](#contributors)
- [Compliance](#compliance)
- [Need help?](#questions-need-help)
- [License](#license)

<br/>
<br/>

### Synopsis

This repo is a release checklist for automated releases as part of gitlab pipelines.
The tool would create a CMC ticket for the service or feature that is released and part of the pipeline to confirm all tests as necessary validations have been completed before a production deployment.
The template for creating the CMC ticket can be referred to cmc_template.yml.

<br/>

### Features

#### MultiJob feature

The job uses an argument `--type` to determine which Job to run.
This can be set in the following ways:
1. When running locally:
    ```shell script
    ./gradlew bootRun --args='--name <<CMC Ticket>>>'
    ```
<br/>

2. When running locally as a Docker image:
    ```shell script
    docker run ..args.. image-name --name <<CMC Ticket>>>
    ```
<br/>


### Contributors

Batch Working Group

Want to help improve? Check out the [contributing docs](CONTRIBUTING.md) to get involved.

* Open pull request with improvements
* Discuss ideas in issues
* Spread the word
* Reach out with any feedback
