# demo.todomvc Change Log

## Todo
- [Todo] Factor out Liquibase as a module


## [WIP] 0.5.0 / 2018-February-??

- Update dependencies
  - Bract 0.5.0
  - Clojure 1.9.0, ClojureScript 1.9.946
  - Asphalt 0.6.5
  - Calfpath 0.5.0
  - Cambium: API 0.9.1 and Logback-module 0.4.1
  - Ring 1.6.3
  - Cljs-ajax 0.7.3
- Use bract.ring wrappers
  - JSON output for /info and /health endpoints
- [TODO] Display app version in /info result


## 0.4.1 / 2017-August-08

- Use Bract 0.4.1
- Bump Asphalt version to 0.6.3
- Use new Asphalt 0.6.2 option `:result-set-worker` in tests


## 0.4.0 / 2017-August-05

- Use Bract 0.4.0
- Bump CLJS version to 1.9.854
- Bump Asphalt version to 0.6.2
- Move global/stateful vars from `demo.todomvc.config` to `demo.todomvc.global` namespace
- Rename namespace `demo.todomvc.config` to `demo.todomvc.keydef`
- Implement `version` CLI command using custom `demo.todomvc.main/-main` entrypoint
- Make logging config work when using _lein-ring_
  - Workaround for issue https://github.com/weavejester/lein-ring/issues/190
- Improve log archive management by capping the total archive size


## 0.3.1 / 2017-June-30

- Use Clostache template on the server-side to render HTML with minified JS
- Update ClojureScript version to 1.9.660
- Replace implicit page load with JavaScript `window.onload` event handler
  - Ref: https://github.com/Day8/re-frame/blob/master/examples/todomvc/resources/public/index.html
  - Ref: https://github.com/Day8/re-frame/blob/master/examples/todomvc/project.clj
- Use Hiccups CLJS library (instead of string concatenation) to generate TODO item HTML
- Hide unnecessary controls when there are no TODO items
- Implement toggle-complete-all feature
- Use Bract 0.3.1 components


## 0.3.0 / 2017-June-19

- ClojureScript based browser DOM manipulation
- File based H2 database storage
- Server-side web endpoints to talk to the database
- Database migration using Liquibase
- File based application and metrics logging
- Use Bract 0.3.0 components
