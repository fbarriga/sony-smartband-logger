#!/bin/bash

INKSCAPE=inkscape

if !command -v $INKSCAPE >/dev/null 2>&1; then
    echo "You need to install inkscape"
    exit 1
fi

$INKSCAPE -z -e play/ic_sd_card_512.png -w 512 -h 512 icon.svg
#$INKSCAPE -z -e ../src/main/res/drawable/icon_extension.png -w 36 -h 36 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xxxhdpi/icon.png -w 192 -h 192 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xxhdpi/icon.png -w 144 -h 144 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xhdpi/icon.png -w 96 -h 96 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-hdpi/icon.png -w 72 -h 72 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-ldpi/icon.png -w 36 -h 36 icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-mdpi/icon.png -w 48 -h 48 icon.svg 

$INKSCAPE -z -e ../src/main/res/drawable-xxxhdpi/csv_icon.png -w 192 -h 192 csv_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xxhdpi/csv_icon.png -w 144 -h 144 csv_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xhdpi/csv_icon.png -w 96 -h 96 csv_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-hdpi/csv_icon.png -w 72 -h 72 csv_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-ldpi/csv_icon.png -w 36 -h 36 csv_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-mdpi/csv_icon.png -w 48 -h 48 csv_icon.svg 

$INKSCAPE -z -e ../src/main/res/drawable-xxxhdpi/ods_icon.png -w 192 -h 192 ods_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xxhdpi/ods_icon.png -w 144 -h 144 ods_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xhdpi/ods_icon.png -w 96 -h 96 ods_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-hdpi/ods_icon.png -w 72 -h 72 ods_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-ldpi/ods_icon.png -w 36 -h 36 ods_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-mdpi/ods_icon.png -w 48 -h 48 ods_icon.svg 

$INKSCAPE -z -e ../src/main/res/drawable-xxxhdpi/unknown_icon.png -w 192 -h 192 unknown_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xxhdpi/unknown_icon.png -w 144 -h 144 unknown_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-xhdpi/unknown_icon.png -w 96 -h 96 unknown_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-hdpi/unknown_icon.png -w 72 -h 72 unknown_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-ldpi/unknown_icon.png -w 36 -h 36 unknown_icon.svg
$INKSCAPE -z -e ../src/main/res/drawable-mdpi/unknown_icon.png -w 48 -h 48 unknown_icon.svg 