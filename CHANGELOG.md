# demo.todomvc Change Log

## Todo
- [Todo] Factor out Liquibase as a module


## [WIP] 0.3.1 / 2017-June-??

- Update ClojureScript version to 1.9.660
- [TODO] Replace implicit page load with `window.onload = function () { demo.todomvc.app.setup(); }`
  - Ref: https://github.com/Day8/re-frame/blob/master/examples/todomvc/resources/public/index.html
  - Ref: https://github.com/Day8/re-frame/blob/master/examples/todomvc/project.clj
- [TODO] Use a Hiccup like library instead of string concatenation to create DOM nodes
- [TODO] Use Bract `0.3.1` components


## 0.3.0 / 2017-June-19

- ClojureScript based browser DOM manipulation
- File based H2 database storage
- Server-side web endpoints to talk to the database
- Database migration using Liquibase
- File based application and metrics logging
- Use Bract 0.3.0 components
