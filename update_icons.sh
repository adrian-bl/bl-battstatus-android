#!/bin/sh

(
 cd scripts &&
 perl gen_images.pl H &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable-hdpi/

 perl gen_images.pl L &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable/

)
