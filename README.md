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

## Data Deletion Warning
Running `lein closp-crud` silently overwrites existing namespaces with the same name as defined in the 
table definition.

## Options

* **-f** **Mandatory** The path to the table definition file. 

## Dependencies 

This plugin only works if the following libraries exist in your classpath:

* lib-noir
* compojure
* timbre
* korma
* selmer

## Prerequisites

`closp-crud` expects a layout namespace containing a render function. This will be used to render the templates and
can be configured via the project settings.
`closp` provides a template here: 
<https://github.com/sveri/closp/blob/master/resources/leiningen/new/closp/clj/layout.clj>

## project.clj options

`closp-crud` expects a :closp-crud entry in your `project.clj` file. It expects several keys there, namely:

### :jdbc-url 
This is the jdbc uri that closp-crud will use to create the SQL files. This must be an accessible database. You cannot
provide fake uris here. 

This will create a h2 database in memory. 
    
    :jdbc-url "jdbc:h2:mem:test_mem"
    
### :migrations-output-path 
The path where the generated SQL files will be stored. Can be anywhere, but should be somewhere where a migration
library can pick it up.

Example:

    :migrations-output-path "./migrations"
    
### :clj-src 
The file path to your projects clj source files. Should be relativ to the current project.

Example:

    :clj-src "src/clj"
    
### :ns-db 
The namespace where generated db functions will be written to.

Example:

    :ns-db "de.sveri.siwf.db"
    
### :ns-routes 
The namespace where the generated routes will be written to.

Example:

    :ns-routes "de.sveri.siwf.routes"

### :ns-layout 
The namespace where your layout.clj with a given render function resides.

Example:

    :ns-layout "de.sveri.siwf.layout"
    
### :templates
A file path where the generated html templates will be stored

Example:

    :templates "resources/templates"
    
    
## Table Definition
Table definitions are stored in edn files whereas one definition has to be in one file. Multiple definitions per
file are not supported.

Example: 

    {:name    "person"
     :columns [[:id :int :null false :pk true :autoinc true]
               [:name [:varchar 40] :null false]
               [:age :int :null false]
               [:male :boolean :default true]]}

### Structure
Two key / value pairs are expected:

* **:name** The name of the entity
* **:columns** A vector of vectors containing the description for each single column in the table.

### Column definitions
The structure of the columns is somewhat flexible.

* **Mandatory** The first entry will be the name of the column
* **Mandatory** The second entry will be the type of the column. It has to be of the form `Keyword|[Keyword Int]` which means
it is either of a fixed length type like boolean or a variable length type like varchar.
* **Optional** Then come the optional entries like a given default value or if it is nullable or not

Examples:

* **[:id :int :null false :pk true :autoinc true]** creates a primary key named "id" of type "int" which will 
autoincrement 
* **[:age :int :null false]** creates an "age" column of type "int" which cannot be null
* **[:male :boolean :default true]** creates a "male" column of type "boolean"  which defaults to true and can be null.


For a complete reference of supported tags please look at: <http://www.liquibase.org/documentation/column.html>

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
