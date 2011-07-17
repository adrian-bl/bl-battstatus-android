default:
	echo "not default - sorry ;-)"


clean: cleanres
	ant clean

release: refresh optimizepng

refresh: cleanres images xml

optimizepng:
	optipng -o7 res/drawable-hdpi/*_*.png res/drawable-large-hdpi/*_*.png


cleanres:
	rm -f res/drawable-hdpi/*_*.png	
	rm -f res/drawable-hdpi/*.xml
	rm -f res/drawable-large-hdpi/*

	
images:
	cd scripts && perl gen_images.pl normal && mv *.png ../res/drawable-hdpi/
	cd scripts && perl gen_images.pl large  && mv *.png ../res/drawable-large-hdpi/

xml:
	cd scripts && perl gen_xml.pl && mv *.xml ../res/drawable-hdpi/
