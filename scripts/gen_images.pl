#!usr/bin/perl
use strict;
use Image::Magick;

use constant FONTPATH => "/usr/share/fonts/TTF/DejaVuSansCondensed-Bold.ttf";

my @colors = qw(f02c34 bb382c a1542c a16e2c a1872c 9da12c 81a12c 6ca12c 5aa12c 2da12c 2c91a1);

my $crh  = { size=>72, stroke=>12,  points=>"35,36,28,28" };
my $wfb  = { size=>72, fsize=>36, fsize_100=>34, font=>FONTPATH, x=>0, y=>0, invert=>0, name=>'wfb' };
my $bfb  = { size=>72, fsize=>36, fsize_100=>34, font=>FONTPATH, x=>0, y=>0, invert=>1, name=>'bfb' };
my $wfs  = { size=>72, fsize=>28, fsize_100=>28, font=>FONTPATH, x=>0, y=>1, invert=>0, name=>'wfs' };
my $bfs  = { size=>72, fsize=>28, fsize_100=>28, font=>FONTPATH, x=>0, y=>1, invert=>1, name=>'bfs' };


draw_circle("cr_h_%03d.png", $crh);
draw_font($wfb);
draw_font($wfs);
draw_font($bfb);
draw_font($bfs);


sub draw_font {
	my($conf) = @_;
	
	for((0..15,100)) { assemble_font(sprintf("digit_c_$conf->{name}_%03d.png",$_), $_, $conf);}
	
	$conf->{x} = int($conf->{fsize}/3);
	for(0..9 ) { assemble_font(sprintf("digit_l_$conf->{name}_%03d.png",$_), $_, $conf);}
	$conf->{x} = $conf->{x}*-1;
	for(0..9 ) { assemble_font(sprintf("digit_r_$conf->{name}_%03d.png",$_), $_, $conf);}
}

sub assemble_font {
	my($out, $num, $config) = @_;
	
	my $im    = Image::Magick->new(size=>$config->{size}."x".$config->{size});
	my $fsize = ($num == 100 ? $config->{fsize_100} : $config->{fsize});
	my $xoff  = $config->{x};
	my $yoff  = $config->{y};
	my ($tc_out, $tc_in) = ( $config->{invert} ? ('white', 'black') : ('black' , 'white') );
	$tc_in = "red" if $num <= 15 && $xoff == 0;
	
	$im->Read("xc:transparent");

	foreach my $x (-2..2) {
		foreach my $y (-2..2) {
			$im->Annotate(text=>"$num", fill=>$tc_out, font=>$config->{font}, pointsize=>$fsize+0, antialias=>'true', gravity=>"Center", x=>$xoff+$x, y=>$yoff+$y);
		}
	}
	
	$im->Annotate(text=>"$num", fill=>'grey', font=>$config->{font}, pointsize=>$fsize+0, antialias=>'true', gravity=>"Center", x=>$xoff, y=>$yoff); # creates somewhat of a gardient
	$im->Annotate(text=>"$num", fill=>$tc_in, font=>$config->{font}, pointsize=>$fsize+0, antialias=>'true', gravity=>"Center", x=>$xoff, y=>$yoff);
	
	$im->write($out);
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

