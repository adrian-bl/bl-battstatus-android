#!usr/bin/perl
use strict;
use Image::Magick;

my @colors = qw(f02c34 bb382c a1542c a16e2c a1872c 9da12c 81a12c 6ca12c 5aa12c 2da12c 2c91a1);

foreach my $num (0..100) {
	my $im = Image::Magick->new(size=>'38x38');
	
	my $tc = ($num < 15 ? 'red' : 'white');
	
	my $idx = int($num/10);
	   $idx = 10 if $num >= 95;
	my $cc = $colors[$idx];
	$im->Read("xc:transparent");

	
	my $bump = int(270 - ($num*3.60));
	   $bump ||= 1;
	   $bump-- if !$num;
	
	my $fsize = 16;
	   $fsize = 16 if $num==100;
	
	$im->Draw(primitive=>'ellipse', stroke=>"grey", fill=>'none', strokewidth=>6 , points=>"18,18 15,15 0,360");
	$im->Draw(primitive=>'ellipse', stroke=>"#$cc", fill=>'none', strokewidth=>6 , points=>"18,18 15,15 $bump,270");

	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>0, y=>1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>0, y=>-1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-2, y=>1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-2, y=>-1);
	$im->Annotate(text=>"$num",, fill=>$tc,     font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-1, y=>0);

	
	$im->Write(sprintf("r%03d.png",$num));
}
