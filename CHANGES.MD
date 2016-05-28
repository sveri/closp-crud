
## Changes

### 0.4.0

* **Breaking Change** - using clojure.spec instead of schema

### 0.3.0

* **Breaking change** - the schema for closp-crud entities did change. 
* Important bugfix for boolean fields
* Adding tests
* Minor stuff

### 0.2.1

* Adding defentity declaration to entities.clj
* Moving schema dependency to :provided profile

### 0.2.0

* **Breaking Change** Using id for databases instead of uuid
* Switched from core.typed to plumatic schema
* fixed some bugs


### 0.1.4

* Fix a bug where column names with underscores broke boolean html field generation
* Fix generation of clojure file names to contain underscores instead of dashes

### 0.1.3

* Every db related stuff is lowercase now - add FAQ for h2 _upper_false_  
* Fix #3 Correctly generate selmer template for boolean fields

### 0.1.2

* Add support for mysql and sqlite

### 0.1.1

* Don't overwrite existing files, cancel everything in case a file exists already.
* Fix default handler for  hiccup generation
* Add handler for text fields
