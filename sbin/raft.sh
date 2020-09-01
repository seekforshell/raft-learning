#!/usr/bin/env bash

PWD=$(cd "$(dirname "$0")";pwd)

JVM_OPTS=""

java -cp ${PWD}/../jars/*:${PWD}/../jars/lib/* Main