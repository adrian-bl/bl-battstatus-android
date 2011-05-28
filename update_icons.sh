#!/bin/sh

(
 rm res/drawable-hdpi/*_*.png &&
 rm res/drawable-hdpi/*.xml && 
 
 cd scripts &&
 perl gen_images.pl &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable-hdpi/

 perl gen_xml.pl &&
 mv *.xml ../res/drawable-hdpi/
)
