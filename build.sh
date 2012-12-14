#!/bin/bash

SRC_DIR=src/main/java
BIN_DIR=target/classes

mkdir -p $BIN_DIR

export CLASSPATH=$(pwd)/$BIN_DIR
export CLASSPATH=$CLASSPATH:$(pwd)/lib/junit-4.11.jar
export CLASSPATH=$CLASSPATH:$(pwd)/lib/log4j-1.2.17.jar
export CLASSPATH=$CLASSPATH:$(pwd)/lib/soot-2.5.0.jar

javac $SRC_DIR/*.java

mv $SRC_DIR/*.class $BIN_DIR
cp $SRC_DIR/../resources/*.xml $BIN_DIR

