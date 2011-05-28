#!usr/bin/perl
use strict;
use Image::Magick;

use constant FONTPATH => "/usr/share/fonts/TTF/DejaVuSansCondensed-Bold.ttf";

my @colors = qw(f02c34 bb382c a1542c a16e2c a1872c 9da12c 81a12c 6ca12c 5aa12c 2da12c 2c91a1);

my $crh = { size=>38, stroke=>6,  points=>"18,18,15,15" };
my $wfb  = { size=>38, fsize=>20, fsize_100=>19, font=>FONTPATH, o=>0, o_10=>0, invert=>0 };
my $bfb  = { size=>38, fsize=>20, fsize_100=>19, font=>FONTPATH, o=>0, o_10=>0, invert=>1 };
my $wfs  = { size=>38, fsize=>15, fsize_100=>15, font=>FONTPATH, o=>0, o_10=>-1, invert=>0 };
my $bfs  = { size=>38, fsize=>15, fsize_100=>15, font=>FONTPATH, o=>0, o_10=>-1, invert=>1 };


draw_circle("cr_h_%03d.png", $crh);
draw_font("wfb_%03d.png", $wfb);
draw_font("wfs_%03d.png", $wfs);

draw_font("bfb_%03d.png", $bfb);
draw_font("bfs_%03d.png", $bfs);

sub draw_font {
	my($out, $config) = @_;
	
	foreach my $num (0..100) {
		my $fsize = ($num == 100 ? $config->{fsize_100} : $config->{fsize});
		my $o     = ($num >= 10  ? $config->{o_10}      : $config->{o}    );
		my $im = Image::Magick->new(size=>$config->{size}."x".$config->{size});
		my ($tc_out, $tc_in) = ( $config->{invert} ? ('white', 'black') : ('black' , 'white') );
		$tc_in = "red" if $num <= 15;
		
		$im->Read("xc:transparent");
		$im->Annotate(text=>"$num",, fill=>$tc_out, font=>$config->{font}, pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>1+$o, y=>1);
		$im->Annotate(text=>"$num",, fill=>$tc_out, font=>$config->{font}, pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>1+$o, y=>-1);
		$im->Annotate(text=>"$num",, fill=>$tc_out, font=>$config->{font}, pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-1+$o, y=>1);
		$im->Annotate(text=>"$num",, fill=>$tc_out, font=>$config->{font}, pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>-1+$o, y=>-1);
		$im->Annotate(text=>"$num",, fill=>$tc_in,  font=>$config->{font}, pointsize=>$fsize, antialias=>'true', gravity=>"Center", x=>$o, y=>0);
		$im->write(sprintf($out,$num));
	}
}

sub draw_circle {
	my($out, $config) = @_;
	
	foreach my $num (0..100) {
		my $idx  = int($num/10);
		   $idx  = 10 if $num >= 95;
			 
		my $cc   = $colors[$idx];
		
		my $bump = int(270 - ($num*3.60));
		   $bump ||= 1;
		   $bump-- if !$num;
		
		my $im = Image::Magick->new(size=>$config->{size}."x".$config->{size});
		$im->Read("xc:transparent");
		$im->Draw(primitive=>'ellipse', stroke=>"grey", fill=>'none', strokewidth=>$config->{stroke} , points=>"$config->{points} 0,363");
		$im->Draw(primitive=>'ellipse', stroke=>"#$cc", fill=>'none', strokewidth=>$config->{stroke} , points=>"$config->{points} $bump,272");
		$im->write(sprintf($out, $num));
	}
	
}

