#!/bin/sh

ftext=ftext.dat
tmp=tmp.dat
for var in "$@"
do
    if [ -e "$var" ]
    then
        paste -s -d ' ' $var > $tmp
        sed -e "s/[[:space:]]\+/ /g"  $tmp >> $ftext
    else
        echo "$var file doesn't exist."
    fi
done
echo "Your file ($ftext) has been converted successfully!"
rm $tmp
