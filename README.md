# closp-crud

A Leiningen plugin to provide CRUD functionality for clojure web projects. Optimized for the usage
within <https://github.com/sveri/closp>.

## Usage

Put `[closp-crud "0.1.0"]` into the `:plugins` vector of your `project.clj` file.

Then add the configuration to your `project.clj` file like this (see below for explanation of options):

    :closp-crud {:jdbc-url "jdbc:h2:mem:test_mem"
                 :migrations-output-path "./migrations"
                 :clj-src "src/clj"
                 :ns-db "de.sveri.siwf.db"
                 :ns-routes "de.sveri.siwf.routes"
                 :ns-layout "de.sveri.siwf.layout"
                 :templates "resources/templates"}

Next you need a table definition in a file somwhere. It may look like this:

    ; closp-definitions/person.edn
    {:name    "person"
     :columns [[:id :int :null false :pk true :autoinc true]
               [:name [:varchar 40] :null false]
               [:age :int :null false]
               [:male :boolean :default true]]}
               
Then run

    $ lein closp-crud -f closp-definitions/person.edn
    
This will generate several files:

* Up and down file in `./migrations` containing the SQL code to create and drop a person table.
* A database clj source file in `de/sveri/siwf/db/person.clj` containing the source to do CRUD operations on 
the person table using [korma](https://github.com/korma/Korma/)
* A routes clj source file in `de/sveri/siwf/routes/person.clj` containing the route definitions using compojure
and noir.
* Several html files containing three templates: `index.html, create.html and delete.html`

Finally you need to add the generated routes definiton from `routes/person.clj` to your handler.

## Dependencies 

This plugin only works if the following libraries exist in your classpath:

* lib-noir
* compojure
* timbre
* korma
* selmer

## Prerequisites

`closp-crud` expects a layout namespace containing a render function. This will be used to render the templates. 
`closp` provides a template here: 
<https://github.com/sveri/closp/blob/master/resources/leiningen/new/closp/clj/layout.clj>





## `project.clj` options
    
## Column definitions

For a complete reference of supported tags please look at: <http://www.liquibase.org/documentation/column.html>

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
