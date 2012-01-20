#!/usr/bin/perl
use strict;

foreach my $font_type qw(bfb bfs wfb wfs gfs gfb) {
	
	# create special fonts: the red 0-15 and 100
	foreach my $special_font (0..15,100) {
		my $outfile = sprintf("${font_type}_%03d.xml",$special_font);
		write_xml($outfile, sprintf("digit_c_${font_type}_%03d",$special_font));
	}
	# normal fonts
	foreach my $fn (16..99) {
		my $outfile = sprintf("${font_type}_%03d.xml",$fn);
		write_xml($outfile, sprintf("digit_r_${font_type}_%03d",int($fn/10)), sprintf("digit_l_${font_type}_%03d",int($fn%10)) );
	}
	# circle edition
	foreach my $c (0..100) {
		my $outfile = sprintf("${font_type}_cr_h_%03d.xml",$c);
		write_xml($outfile,  sprintf("cr_h_%03d",$c), sprintf("${font_type}_%03d",$c));
	}
	
}


sub write_xml {
	my($outfile,@parts) = @_;
	open(XML, ">", $outfile) or die "Could not write to $outfile: $!\n";
	print XML '<?xml version="1.0" encoding="utf-8"?>'."\n";
	print XML '<layer-list xmlns:android="http://schemas.android.com/apk/res/android">'."\n";
	foreach (@parts) {
		print XML "<item android:drawable=\"\@drawable/$_\" />\n";
		print XML "<item android:drawable=\"\@drawable/$_\" />\n";
	}
	print XML "</layer-list>\n";
	close(XML);
	
}
