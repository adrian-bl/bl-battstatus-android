#!/bin/sh

(
 cd scripts &&
 perl gen_images.pl &&
 optipng -o7 *.png &&
 mv *.png ../res/drawable-hdpi/
)
