#!/bin/bash
# usage: eventlatencycomputer.sh <events_input_file> <latency_output_file>

VERSION=1.0
MAIN_JAR_NAME=EventLatencyComputer


#Find the the absolute path to the input and output files
REL_INPUT_PATH=$1
INPUT_FILE_NAME=`basename ${REL_INPUT_PATH}`
REL_INPUT_DIR=`dirname ${REL_INPUT_PATH}`
echo "relative path of input events file " ${REL_INPUT_PATH}
ABS_INPUT_PATH=`cd "${REL_INPUT_DIR}"; pwd`/${INPUT_FILE_NAME}

REL_OUTPUT_PATH=$2
OUTPUT_FILE_NAME=`basename ${REL_OUTPUT_PATH}`
REL_OUTPUT_DIR=`dirname ${REL_OUTPUT_PATH}`
ABS_OUTPUT_PATH=`cd "${REL_OUTPUT_DIR}"; pwd`/${OUTPUT_FILE_NAME}

#Find out where we're installed.
INSTALL_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CURRENT_DIR=`pwd`
echo ""
echo "======STARTUP PARAMS========="
echo "Absolute path of input file: " ${ABS_INPUT_PATH}
echo "Absolute path of output file: " ${ABS_OUTPUT_PATH}
echo "Installed Dir: " ${INSTALL_DIR}
echo "Current Working Dir: " ${CURRENT_DIR}
echo "============================="
echo ""


java -Devents.log.file=${ABS_INPUT_PATH} -Doutput.file=${ABS_OUTPUT_PATH} -jar ${INSTALL_DIR}/${MAIN_JAR_NAME}-${VERSION}.jar