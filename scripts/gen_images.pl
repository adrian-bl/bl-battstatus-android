#!usr/bin/perl
use strict;
use Image::Magick;

my @colors = qw(f02c34 bb382c a1542c a16e2c a1872c 9da12c 81a12c 6ca12c 5aa12c 2da12c 2c91a1);

my $conf = {
	H => { fsize=>16, fsize_100=>16, font=>"DroidSans-Bold.ttf", size=>38, stroke=>6 , points=>"18,18 15,15", o=>-1},
	L => { fsize=>10, fsize_100=>9,  font=>"DroidSans.ttf", size=>19, stroke=>4 , points=>"9,9 6,6", o=>-1},
};


my $x = $conf->{$ARGV[0]} or die;

foreach my $num (0..100) {
	my $im = Image::Magick->new(size=>$x->{size}."x".$x->{size});
	
	my $tc = ($num < 15 ? 'red' : 'white');
	
	my $idx = int($num/10);
	   $idx = 10 if $num >= 95;
	my $cc = $colors[$idx];
	$im->Read("xc:transparent");

	
	my $bump = int(270 - ($num*3.60));
	   $bump ||= 1;
	   $bump-- if !$num;
	
	my $fsize = ($num == 100 ? $x->{fsize_100} : $x->{fsize});
	my $o     = $x->{o};
	
	$im->Draw(primitive=>'ellipse', stroke=>"grey", fill=>'none', strokewidth=>$x->{stroke} , points=>"$x->{points} 0,360");
	$im->Draw(primitive=>'ellipse', stroke=>"#$cc", fill=>'none', strokewidth=>$x->{stroke} , points=>"$x->{points} $bump,270");

	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>1+$o, y=>1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>1+$o, y=>-1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-1+$o, y=>1);
	$im->Annotate(text=>"$num",, fill=>'black', font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-1+$o, y=>-1);
	$im->Annotate(text=>"$num",, fill=>$tc,     font=>"DroidSans-Bold.ttf", pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>$o, y=>0);

	
	$im->Write(sprintf("r%03d.png",$num));
}
