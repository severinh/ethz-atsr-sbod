#!/bin/bash

PACKAGE=ch.ethz.atsr.sbod
MAIN_CLASS=BufferOverflowDetector
TEST_CLASS=TestClass1

mvn -q exec:java \
    -Dexec.mainClass="${MAIN_CLASS}" \
    -Dexec.arguments="${TEST_CLASS}" \
    -Dexec.classpathScope="test"
