# Cronicle

![UI](docs/screenshot.png)

A simple Spring Boot Java server to keep track of whether your cron jobs ran or not. There are many paid
services that do this, but I found it too expensive for doing such a thing.

At just about 1000 sloc (<700 for Java; rest for HTML), it is meeting my needs pretty well. Feel free to 
fork and use/change it.

## Questions Answered by *Cronicle*

* Did my cron job start at all?
* Did it successfully complete?
* How much time did it take for a run?
* When is it supposed to run next?
* Can I mark an SLA for a cron to run and flag if it took too much time to run?
* Can I do some central processing when a job didn't run/ran/failed etc?

For more screenshots of the simpleton UI, go to [Quick Demo](#quick-demo) section. This is the UI from the demo
script that creates sample cron jobs for you and adds to cronicle for a trial run.

## *Cronicle* is not for

* Scheduling your jobs in a central system. I like jobs being set up where they
  belong, ie., in the systems they are supposed to be. Not this all in one place
  thing that reaches out and runs in remote machines. This is a central
  monitoring place for your crons in whatever machines you've as long as those
  machines can hit a URL in Cronicle.
* Monitoring 1000s of jobs scheduled in a minute by minute basis. It works for
  what most people consider as regular jobs.
* Highly security conscious. In fact, there is no security. Use it wisely
  within your intranet!

## Design

![Design](docs/cronicle.png)

There are two tables viz., `cron` and `run`. `cron` holds the crons and
`run` is a child table that has past runs and *one* future runs record added
for each job. State machine for `run` is like below.

* UNSCHEDULED -> SCHEDULED
* SCHEDULED -> RUNNING
* RUNNING -> SUCCESS 
* RUNNING -> FAILED
* SCHEDULED -> DID_NOT_RUN
  
There is one cron inside the JVM that marks old jobs that didn't get to
a terminal state, as FAILED or DID_NOT_RUN.

You will need to call APIs to mark your job as started or finished. See
the script `cronicle-client-wrapper.sh` to just wrap your existing job in your
crontab entry with minimal effort.

Finally, when a `run` gets into terminal state via API or via cron inside
the JVM, it raises a reactor event. You can add your own program in property
file to be called on this event. 


## Installation

* Build like `gradle buildDistZip`
* Distribution is in `build/distributions`
* Take `sample/application.properties` in the distribution to root of your installation and edit it. Default DB is h2; in `tmp` folder!
  If you want to change to mysql or postgres, change `build.gradle` to add dependencies and to edit property file with correct connect strings and credentials.
* Run `bin/cronicle`

## Quick Demo
* Install `jq`  and `curl` utilities
* Unzip the distribution somewhere
* Setup `application.properties` and run `bin/cronicle`
* Go to http://localhost:8080/ - once you get the UI
* Run `cd demo; setup_demo_jobs.sh`
* Add the output to your crontab
* After a while, check the UI again - you should see status getting updated
* Note that  `cronicle-client-wrapper.sh` in `demo` folder is slightly
  different - it uses *jobId* as a header parameter rather than as path
  variable. In case you don't want to expose job-ids in request logs.

**Regular Job View**

![Regular Job View](docs/screenshot_01_view.png)

**Edit Job** (plus to create new, pencil to edit)

![Edit the job](docs/screenshot_02_edit.png)

**Get Job Key** (hamburger menu)

![Get the job key](docs/screenshot_02_key.png)

**View Runs of a Job** (menu icon at the bottom of the card)

![View job runs](docs/screenshot_03_log.png)

## Usage

* Add a cron job using the UI. UI is a quick hack SPA :)
* Note  the guid of the job
* Change your cron job to run inside `cronicle-client-wrapper.sh`. See that script for usage.
  * This means, if your crons are in another machine, you need to copy over the wrapper script there
    and ensure that machine can do http connections to where this is running.
* If you want, you can enable event processing by setting your handler in `application.properties`.
  * Sample is given in `sample-event-handler.sh`. Bbe careful to make that program non-blocking.  For example, if you are sending a mail there, you should handle exiting quickly if email server is taking lot of time.

## Todo

* [ ] Fix the "Yet to Run" marker on the UI - it is overly convoluted.
* [X] Security - add guid as a header variable so that it doesn't have to be passed via path.
* [X] Gradle - mark versions of dependencies so that I don't need to fix this when latest versions of deps are not backward compatible.
* [ ] Security - add basic auth at least.
* [ ] Move schedule future to an api and call it on cron status changed event or created
* [ ] Add another background JVM task to archive or delete older-than-X `run` records


