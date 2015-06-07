# Cronicle

A simple Spring Boot Java server to keep track of whether your cron jobs ran or not.


## Installation

* Build like gradle build
* Make a folder where this needs to run from
* Take config/application.properties and public into the folder above. Copy .sh too.
* Adjust application.properties to set db stuff

## Usage

* Add a cron job using the UI
* Note  the guid of the job
* Change your cron job to run inside `cronicle_client_wrapper.sh`. See that script for usage.
  * This means, if your crons are in another machine, you need to copy over the wrapper script there
    and ensure that machine can do http connections to where this is running.
* If you want, you can enable event processing by setting your handler in `application.properties`.
  * Sample is given in `sample_event_handler.sh`

## Todo

* Fix dist to package all that is needed
* move schedule future to an api and call it on cron status changed event or created
* job cleaner only does timeout and not run marking
* Fix long running logic
* UI fixes; add/edit/delete; auto refresh; backbone.js

