#!/bin/sh

(
 cd scripts &&
 perl gen_images.pl &&
 mv *.png ../res/drawable-hdpi/
)
