(ns closp-crud.core
  (:require
    [clojure.pprint :as pp]
    [clj-miscutil.core :as mu]
    [clj-liquibase.core :as lb]
    [clj-liquibase.change :as ch]
    [clj-liquibase.cli :as cli]
    [clj-dbcp.core :as dbcp]
    [clj-jdbcutil.core :as spec])
  (:gen-class))

(def ds (dbcp/make-datasource :h2 {:target :memory :database :default}))
;;(def ds (dbcp/make-datasource :mysql {:host "localhost" :database "bituf" :user "root" :password "root"}))
(def dbspec (spec/make-dbspec ds))
(def ct-change1 (mu/! (ch/create-table "sampletable1"
                                       [[:id :int :null false :pk true :autoinc true]
                                        [:name [:varchar 40] :null false]
                                        [:gender [:char 1] :null false]])))
(def ct-change2 (mu/! (ch/create-table "sampletable2"
                                       [[:id :int :null false :pk true :autoinc true]
                                        [:name [:varchar 40] :null false]
                                        [:gender [:char 1] :null false]])))
(def ct-change3 (mu/! (ch/sql "SELECT * FROM sampletable1")))
(def changeset-1 ["id=1" "author=shantanu" [ct-change1]])
(def changeset-2 ["id=2" "author=shantanu" [ct-change2]])
(def changeset-3 ["id=3" "author=shantanu" [ct-change3]])
(lb/defchangelog changelog-1 "example" [changeset-1])
(lb/defchangelog changelog-2 "example" [changeset-1 changeset-2])
(lb/defchangelog changelog-3 "example" [changeset-1 changeset-2 changeset-3])
