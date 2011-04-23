#!usr/bin/perl
use strict;
use Image::Magick;


foreach my $num (0..100) {
	my $im = Image::Magick->new(size=>'38x38');
	
	my $cc = 'blue';
	   $cc = 'green'  if $num <= 95;
	   $cc = 'orange' if $num <= 80;
	   $cc = 'yellow' if $num <= 70;
	   $cc = 'red'    if $num <= 20;
	
	$im->Read("xc:transparent");
	$im->Annotate(text=>"$num",, fill=>'#d0d0d0', font=>"DroidSans-Bold.ttf", pointsize=>15, antialias=>'true', gravity=>"Center", x=>-1, y=>0);
	$im->Draw(primitive=>'circle',stroke=>$cc, strokewidth=>3, fill=>'none',
	  points=>"18,18 7,7");
	
	
	$im->Write(sprintf("r%03d.png",$num));
}
