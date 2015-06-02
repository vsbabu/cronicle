# Cronicle

A simple Spring Boot Java server to keep track of whether your cron jobs ran or not.


## Installation

* Build like gradle build
* Make a folder where this needs to run from
* Take config/application.properties and public into the folder above. Copy .sh too.
* Adjust application.properties to set db stuff

## Todo

* Fix dist to package all that is needed
* move schedule future to an api and call it on cron status changed event or created
* job cleaner only does timeout and not run marking
* Fix long running logic
* UI fixes; add/edit/delete; auto refresh; backbone.js

