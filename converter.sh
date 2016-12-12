#!/bin/bash

for var in "$@"
do
	if [ -e "$var" ]
  then
  	paste -s -d ' ' $var > "test_$var"
    rm $var
    mv "test_$var" $var
    echo "Your file ($var) has been converted successfully!"
  else
  	echo "$var file doesn't exist."
  fi
done
