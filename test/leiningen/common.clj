(ns leiningen.common)

(def person-definition {:name    "person"
                        :columns [{:name "id" :type :int :null false :pk true :autoinc true}
                                  {:name "fooname" :type :varchar :max-length 40 :null false}
                                  {:name "age" :type :int :null false}
                                  {:name "male" :type :boolean :null false}
                                  {:name "description" :type :text}]})

(def with-foreign-keys {:name    "person"
                        :columns [{:name "id" :type :int :null false :pk true :autoinc true}
                                  {:name "fooname" :type :varchar :max-length 40 :null false}
                                  {:name "age" :type :int :null false}
                                  {:name "male" :type :boolean :null false}
                                  {:name "description" :type :text}]})
