# Remote usage of exception unifier
## Introduction
Thought of remote usage is to set moduleId for current module 

and corresponding module exception code prefix is fetched from remote server.

In this way, for multiple projects, their exception code prefix is maintained in a central server to ensure no duplication.

And this is also the original intention of exception unifier.

We recommend this way.
## How to verify
### Presets
This is a built-in [remote server](src/main/resources/exception-code-remote-server) which plays as exception code remote server

and will be started automatically after maven lifecycle phase `process-resources` and stopped after phase `complile`,

so that, during compile phase, requests to exception code server will be handled accordingly.

### Steps to verify