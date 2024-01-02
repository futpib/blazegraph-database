#!/bin/bash

exec \
    java \
    $JAVA_OPTS \
    -Dcom.bigdata.rdf.sail.webapp.ConfigParams.propertyFile=/var/lib/jetty/RWStore.properties \
    -jar /usr/local/jetty/start.jar \
    ;
