# Cronicle

A simple Spring Boot Java server to keep track of whether your cron jobs ran or not.


## Installation

* Build like `gradle buildDistZip`
* Distribution is in `build/distributions`
  * Note that those tar/zip can be exploded and rsync'ed to your installation host to save bandwidth on changes
* Take `sample/application.properties in the distribution to root of your installation and edit it
* Run `bin/cronicle`

## Usage

* Add a cron job using the UI
* Note  the guid of the job
* Change your cron job to run inside `cronicle-client-wrapper.sh`. See that script for usage.
  * This means, if your crons are in another machine, you need to copy over the wrapper script there
    and ensure that machine can do http connections to where this is running.
* If you want, you can enable event processing by setting your handler in `application.properties`.
  * Sample is given in `sample-event-handler.sh`

## Todo

* move schedule future to an api and call it on cron status changed event or created
* job cleaner only does timeout and not run marking
* Fix long running logic
* UI fixes; add/edit/delete; auto refresh; backbone.js

