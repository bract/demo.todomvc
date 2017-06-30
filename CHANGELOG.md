# demo.todomvc Change Log

## Todo
- [Todo] Move stateful vars from `config` to `global` namespace
- [Todo] Factor out Liquibase as a module


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
