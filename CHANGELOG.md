# demo.todomvc Change Log

## Todo & Ideas

- [Todo] Drop Liquibase
- [Idea] Use PostgreSQL
- [Idea] Use Reagent
- [Todo] Use bi-directional routes


## [WIP] 0.6.2-0.3.0 / 2021-February-??

- Bract
  - Use Bract 0.6.2
  - Process CLI args in DEV mode just as main mode
  - Use `bract.dev.repl` for REPL support
    - Use port 3001 for nREPL
    - Drop `dev` files
  - Use `bract.core.dev` as main entrypoint in DEV
- Application
  - Replace discontinued `goog.dom.query` with `js/document.querySelectorAll`
  - Move `dev/liquibase.clj` to `test/clj/liquibase.clj` (uptake Bract 0.6.2)
  - Use separate URIs for `/todos/*` and `/todo/:id/*` (conflict resolution for Calfpath 0.8.0)
- Version upgrades
  - Clojure 1.10.2
  - ClojureScript 1.10.758
  - ring-core 1.8.2
  - ring-jetty-adapter (commented) 1.8.2
  - http-kit 2.5.1
  - cljs-ajax 0.8.1
  - cljstache 2.0.6
  - H2 database 1.4.200
  - asphalt 0.6.7


## 0.6.1-0.2.0 / 2019-January-10

- Upgrade Clojure to version 1.10.0
- Use Gossamer 0.6.1-0.2.0
  - Trim down bract-context.edn
  - Trim down bract-context.dev.edn
  - Remove `bract.cli`, `bract.ring` modules
  - Remove redundant `app.version` key definition
  - Remove redundant print-version command


## 0.6.1 / 2018-October-10

- Use Bract 0.6.1
- Use Gossamer 0.6.1
- Upgrade CLJS to 1.10.339
- Bump Aleph version to 0.4.6
- Fix disabling JS minification in dev
- Use global atom to hold runtime state


## 0.6.0 / 2018-May-18

- Use Gossamer 0.6.0
  - Use logback-included in logback.xml
  - Use bract.ring and Gossamer config resources
  - Move all inducer reference from config files to context files
  - Use Calfpath routes instead of direct dispatch
- Replace Clostache (unmaintained) with Cljstache
  - https://github.com/fotoetienne/cljstache
- Add shutdown hook and global uncaught exception handler


## 0.5.1 / 2018-March-05

- Use Bract 0.5.1
- Add fallback config (in `baseconfig.edn`) for application version and hostname
- Use `bract.cli.main` as CLI entry point
- Use soft-delete to remove TODO items in database
- Add CLI command to purge soft-deleted TODO items
- Use `bract.core.dev-init` to initialize test namespaces


## 0.5.0 / 2018-February-18

- Update dependencies
  - Bract 0.5.0
  - Clojure 1.9.0, ClojureScript 1.9.946
  - Asphalt 0.6.5
  - Calfpath 0.5.0
  - Cambium: API 0.9.1 and Logback module 0.4.1
  - Ring 1.6.3
  - Cljs-ajax 0.7.3
- Use bract.ring wrappers
  - JSON output for /info and /health endpoints
  - Display app version in /info result


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
