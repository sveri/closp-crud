# closp-crud


# This projet is deprecated and integrated directly within <https://github.com/sveri/closp>





A Leiningen plugin to provide CRUD functionality for clojure web projects. Optimized for the usage
within <https://github.com/sveri/closp>.

## Rationale

I find it tedious to create a lot of stuff manually like most developers do. And some day while playing around 
with clojure web stuff it occured to me that I need some kind of CRUD functionality again and again.

That's why I started **closp-crud**. It's the first release and does not contain much functionality. However, it should
be enough to get going with a very basic UI.

### Disclaimer

I only did some basic tests. Given the fact that there are many million differnt ways this library can be used, please
be aware that there will be bugs and missing functionality. I am happy about either, bug reports or pull requests.
Also suggestions of any kind are welcome.

## Usage

There are two ways to use **closp-crud**. First, you can use it standalone or second
as part of [closp](https://github.com/sveri/closp) which it comes integrated with.

### As part of **closp**

You need a table definition in a file somewhere. It may look like this:

    ; closp-definitions/person.edn
    {:name    "person"
     :columns [{:name "id" :type :int :null false :pk true :autoinc true}
               {:name "first_name" :type :varchar :max-length 30}
               {:name "last_name" :type :varchar :max-length 30}
               {:name "role" :type :varchar :max-length 30}
               {:name "email" :type :varchar :max-length 30 :null false :unique true}]}
               
Then run

    $ lein run -m de.sveri.clospcrud.closp-crud/closp-crud -f closp-definitions/person.edn
    
This will generate several files:

* Up and down file in `./migrations` containing the SQL code to create and drop a person table.
* A database clj source file in `de/sveri/siwf/db/person.clj` containing the source to do CRUD operations on 
the person table using [korma](https://github.com/korma/Korma/)
* A routes clj source file in `de/sveri/siwf/routes/person.clj` containing the route definitions using compojure
and noir.
* Several html files containing three templates: `index.html, create.html and delete.html`

Finally you need to add the generated routes definition (person-routes) from `routes/person.clj` to your handler in
`ns.handler` in the `get-handler` function under *;; add your application routes here*.
 


## Options

* **-f** **Mandatory** The path to the table definition file. 

## Dependencies 

This plugin only works if the following libraries exist in your classpath:

* lib-noir
* compojure
* timbre
* korma
* plumatic/schema

## Prerequisites

`closp-crud` expects a layout namespace containing a render function. This will be used to render the templates and
can be configured via the project settings.
`closp` provides a template here: 
<https://github.com/sveri/closp/blob/master/resources/leiningen/new/closp/clj/layout.clj>

## closp-crud.edn options

`closp-crud` expects a `closp-crud.edn` file in your resources path. It expects several keys there, namely:

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
### :ns-db-entities 
The namespace where generated db entity declaration will be put.

Example:

    :ns-db-entities "de.sveri.siwf.db.entities"
    
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
     :columns [{:name "id" :type :int :null false :pk true :autoinc true}
              {:name "first_name" :type :varchar :max-length 30}
              {:name "last_name" :type :varchar :max-length 30}
              {:name "role" :type :varchar :max-length 30}
              {:name "email" :type :varchar :max-length 30 :null false :unique true}]}

### Structure
Two key / value pairs are expected:

* **:name** The name of the entity
* **:columns** A vector of maps containing the description for each single column in the table.

### Column definitions
The structure of the columns is somewhat flexible.

* **Mandatory** `:name` - The name of the column
* **Mandatory** `:type` - The type of the column wih can be one of the following:
    :int :varchar :boolean :text :time :date
    :char :binary :smallint :bigint :decimal
    :float :double :real :timestamp
    If you choose a `:char` or `:varchar` type it makes sense to add the `:max-length` option to the map. 

* **Optional** 
    :null       Bool
    :max-length Num
    :required   Bool
    :pk         Bool
    :autoinc    Bool
    :unique     Bool
    :default    s/Any

Examples:

* **{:name "id" :type :int :null false :pk true :autoinc true}** creates a primary key named "id" of type "int" which will 
autoincrement 
* **{:name "age" :type :int :null false}** creates an "age" column of type "int" which cannot be null
* **{:name "male" :tpye :boolean :default true}** creates a "male" column of type "boolean"  which defaults to true and can be null.


For a complete reference of supported tags please look at: <http://www.liquibase.org/documentation/column.html>

## FAQ

### Using h2 I get an error it cannot find columns or tables

This is due to the fact the h2 sticks to the SQL standards and some others don't.  
TLDR: add _;DATABASE_TO_UPPER=FALSE_ to your database uris like this: _"jdbc:h2:./db/korma.db;DATABASE_TO_UPPER=FALSE"_  

Long version is the SQL standards and this SO post: 
<http://stackoverflow.com/questions/10789994/make-h2-treat-quoted-name-and-unquoted-name-as-the-same>

### Standalone

**Deprecated** I will still work standalone, but the documentation is not up to date. For a complete
example look at: <http://github.com/sveri/closp>

Please be aware that **closp-crud** is tied to *closp*. It is possible to run it standalone,
but expects a certain structure, some dependencies and existing functions to call.

Put `[de.sveri/closp-crud "0.2.1"]` into the `:dependencies` vector of your `project.clj` file.

Then add the configuration to your `project.clj` file like this (see below for explanation of options):

    :closp-crud {:jdbc-url "jdbc:h2:mem:test_mem"
                 :migrations-output-path "./migrations"
                 :clj-src "src/clj"
                 :ns-db "de.sveri.siwf.db"
                 :ns-routes "de.sveri.siwf.routes"
                 :ns-layout "de.sveri.siwf.layout"
                 :templates "resources/templates"}

Next you need a table definition in a file somewhere. It may look like this:

    ; closp-definitions/person.edn
    {:name    "person"
     :columns [[:id :int :null false :pk true :autoinc true]
               [:name [:varchar 40] :null false]
               [:age :int :null false]
               [:male :boolean :default true]]}
               
Then run

    $ lein run -m de.sveri.clospcrud.closp-crud/closp-crud -f closp-definitions/person.edn
    
This will generate several files:

* Up and down file in `./migrations` containing the SQL code to create and drop a person table.
* A database clj source file in `de/sveri/siwf/db/person.clj` containing the source to do CRUD operations on 
the person table using [korma](https://github.com/korma/Korma/)
* A routes clj source file in `de/sveri/siwf/routes/person.clj` containing the route definitions using compojure
and noir.
* Several html files containing three templates: `index.html, create.html and delete.html`

Finally you need to add the generated routes definition from `routes/person.clj` to your handler.

## License

Copyright © 2015 Sven Richter

Distributed under the Eclipse Public License version 1.0.
