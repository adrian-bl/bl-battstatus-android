#!/bin/sh

(
 rm -f res/drawable-hdpi/*_*.png   &&
 rm -f res/drawable-hdpi/*.xml     && 
 rm -f res/drawable-large-hdpi/*   &&
 
 cd scripts &&
 perl gen_images.pl normal &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable-hdpi/

 perl gen_images.pl large &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable-large-hdpi/

 perl gen_xml.pl &&
 mv *.xml ../res/drawable-hdpi/
)
