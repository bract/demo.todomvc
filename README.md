# demo.todomvc

Demo project to implement [TodoMVC](http://todomvc.com/) using Bract.


## Usage

### Setup the database

You must setup the database as a one-time task before running the application:

```shell
$ lein do clean, run -m liquibase update
```

The `project.clj` file has a shorter alias defined for this task, so you may run the following command instead:

```shell
$ lein liquibase update
```


### Starting the server

Once you start the server you should visit http://localhost:3000 in your web browser.

You can build the app locally and start the server as follows:

```shell
$ lein do clean, uberjar         # implicitly calls `lein cljsbuild once`
$ java -jar target/uberjar/demo.todomvc-0.4.0-standalone.jar -vf config.edn
```

Alternatively, you may want to run it as follows in development (but logging configuration may be out of control):

```shell
$ lein do clean, cljsbuild auto  # in a separate terminal
$ lein ring server-headless      # ensure not to include clean task
```

Should you need a browser REPL during development, run the following commands instead (with logging config caveat):

```shell
$ lein do clean, figwheel        # in a separate terminal, preferably with rlwrap
$ lein ring server-headless      # ensure not to include clean task
```


### REPL based development

The `dev/user.clj` file is loaded when you run `lein repl` where the following may be helpful:

```clojure
(go)       ; stop app if running, then reload namespaces and reinit
(reset)    ; same as (go)
(start)    ; start the application
(stop)     ; stop app if running, leaving it still initialized
(restart)  ; stop app if running, reload namespaces, reinitialize and start up
(verbose true) ; enable verbose mode
(config "config/config.qa.edn") ; switch config file
```


### Inspect and toggle logs

Running the application should by default emit logs in the `logs` folder. To enable/disable certain kinds of logs
you may override the default configuration by specifying as such in the config file:

```clojure
"logback.appender.console.enabled"  "true"
"logback.appender.jsonfile.enabled" "true"
"logback.appender.textfile.enabled" "false"
```

**Note:** Jetty server logs (lein-ring) are configured in `test/clj/jetty-logging.properties` to appear in console only.


## License

Copyright © 2017 Shantanu Kumar (kumar.shntanu@gmail.com, shantanu.kumar@concur.com)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
