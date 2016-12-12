#!/bin/bash

ftext=ftext.dat
tmp=tmp.dat
for var in "$@"
do
	if [ -e "$var" ]
  then
		paste -s -d ' ' $var > $tmp
    sed -e "s/[[:space:]]\+/ /g"  $tmp >> $ftext
    echo "Your file ($var) has been converted successfully!"
  else
    echo "$var file doesn't exist."
  fi
done
rm $tmp
