#!/bin/bash

BIN_DIR=target/classes
MAIN_CLASS=BufferOverflowDetector

export CLASSPATH=$(pwd)/$BIN_DIR
export CLASSPATH=$CLASSPATH:$(pwd)/lib/junit-4.11.jar
export CLASSPATH=$CLASSPATH:$(pwd)/lib/log4j-1.2.17.jar
export CLASSPATH=$CLASSPATH:$(pwd)/lib/soot-2.5.0.jar

java -server $MAIN_CLASS AllTests \
    AdvancedPointerAnalysis \
    ArithmeticTests \
    ConditionalTests \
    ConstantTests \
    LoopTests \
    PointerAnalysisTests \
    StaticFieldTests \
    TemplateTests \
    WideningTests
