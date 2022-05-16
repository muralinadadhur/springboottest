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

#### SecretsManager/ParamStore

Configured to use AWS Secrets Manager / Parameter Store to use secret params.
the secrets can be found at ECS_CLUSTER/APP_NAME. (Ex: test/boots-template-batch).

To enable secrets manager, simply set `aws.secretsmanager.enabled=true` in *resources/bootstrap.properties* file.

The secret can be stored as the same param name as its stored in application.properties file.
Ex. `appl.prop.variable`
For any property present in both the properties file and in secrets-manager, secrets-manager will override whatever is preset in the file.

<br/>

#### Pipeline

This uses GitLab's inbuilt CI/CD pipelines.

<br/>

### Pre-Requisites

Below pre-requisites should be fulfilled before deploying batch job to AWS environment.
* IAM Role "{environment}-{repository_name}" role is assumed by batch job for execution.

By default, the pipeline gives access for its **secretsmanager** resource. Rest has to be done through SRE.
> NOTE: Batch Roles are managed as Least privilege basis by SRE. Batch execution role needs to be provided required permissions to access resources.

##### Setup local environment

Use the OKTA STS to locally have access to AWS resource. Ref: [Chegg-Okta AWS Setup](https://chegg.atlassian.net/wiki/spaces/CLOUD/pages/386602/AWS+Account+Federation+Okta+and+AWS+CLI+RUNBOOK)

<br/>

### QuickStart

* Fork from this repo using the fork option of the repo.
* Clone your repo
* To modify this from change me to your app name run `sh templatizer.sh <your-app-name>`. This will change all change me occurrences to your app name.
* Run `./gradlew clean build` to install all dependencies.
* To run the app locally, use: `./gradlew bootRun --args='--type JOB_NAME'` . The type argument details have been mentioned earlier.
* Before pushing the code, make sure in the *gitlab-ci.yml* file, the `GITLAB_GROUP: required` is changed to your group (ex: study, content, etc) and the `file: '/required.yml'` is changed to your group's filename ex: *study.yml*
* After all changes are made and code is pushed and merged, the pipeline will update the AWS batch job.
* Once the batch job is created on AWS, create a Rundeck job for the same if job is to be run using Rundeck.
> To set up Rundeck for this job, follow this document: [Rundeck-Setup](https://chegg.atlassian.net/wiki/spaces/~116261519/pages/73930210/How+to+set+up+a+Runde@ckPro+job)

<br/>

### Contributors

Batch Working Group

Want to help improve? Check out the [contributing docs](CONTRIBUTING.md) to get involved.

* Open pull request with improvements
* Discuss ideas in issues
* Spread the word
* Reach out with any feedback

<br/>

### Compliance

**Level 1:** Visit [party of five template compliance](https://chegg.atlassian.net/wiki/spaces/ARCH/pages/84988/Party+of+5#Partyof5-TemplateComplianceLevel) for details

<br/>

### Questions? Need help?

Please reach out to **#india-batch-and-offline-architecture** on Slack channel, and your request will be routed to the right team at Chegg.

<br/>

## License

Chegg Copyright 2020