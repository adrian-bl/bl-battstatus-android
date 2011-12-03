#!usr/bin/perl
use strict;
use Image::Magick;


use constant GOLDEN => 2;

use constant FONTPATH => "/usr/share/fonts/TTF/DejaVuSansCondensed-Bold.ttf";

my @colors = qw(f02c34 bb382c a1542c a16e2c a1872c 9da12c 81a12c 6ca12c 5aa12c 2da12c 2c91a1);

if($ARGV[0] eq 'normal') {
	die "not implemented anymore\n";
=head
	draw_circle("cr_h_%03d.png", { size=>38, stroke=>6,  points=>"18,18,15,15" });
	draw_font({ size=>38, fsize=>20, fsize_100=>19, font=>FONTPATH, x=>0, y=>0, ol=>1, invert=>0, name=>'wfb' });
	draw_font({ size=>38, fsize=>20, fsize_100=>19, font=>FONTPATH, x=>0, y=>0, ol=>1, invert=>1, name=>'bfb' });
	draw_font({ size=>38, fsize=>15, fsize_100=>15, font=>FONTPATH, x=>0, y=>0, ol=>1, invert=>0, name=>'wfs' });
	draw_font({ size=>38, fsize=>15, fsize_100=>15, font=>FONTPATH, x=>0, y=>0, ol=>1, invert=>1, name=>'bfs' });
=cut
}

if($ARGV[0] eq 'large') {
	draw_circle("cr_h_%03d.png", { size=>72, stroke=>12,  points=>"36,34,26,26" });
	draw_font({ size=>72, fsize=>36, fsize_100=>34, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>0, name=>'wfb' });
	draw_font({ size=>72, fsize=>36, fsize_100=>34, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>1, name=>'bfb' });
	draw_font({ size=>72, fsize=>28, fsize_100=>28, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>0, name=>'wfs' });
	draw_font({ size=>72, fsize=>28, fsize_100=>28, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>1, name=>'bfs' });
	draw_font({ size=>72, fsize=>28, fsize_100=>28, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>GOLDEN, name=>'gfs' });
	draw_font({ size=>72, fsize=>36, fsize_100=>34, font=>FONTPATH, x=>1, y=>-1, ol=>2, theme=>GOLDEN, name=>'gfb' });

}


sub draw_font {
	my($conf) = @_;
	
	$conf->{fulldigit} = 1;
	for((0..15,100)) { assemble_font(sprintf("digit_c_$conf->{name}_%03d.png",$_), $_, $conf);}
	
	my $real_x = $conf->{x};
	
	$conf->{fulldigit} = 0;
	$conf->{x} = int($conf->{fsize}/3)+$real_x;
	for(0..9 ) { assemble_font(sprintf("digit_l_$conf->{name}_%03d.png",$_), $_, $conf);}
	$conf->{x} = -1*int($conf->{fsize}/3)+$real_x;
	for(0..9 ) { assemble_font(sprintf("digit_r_$conf->{name}_%03d.png",$_), $_, $conf);}
}

sub assemble_font {
	my($out, $num, $config) = @_;
	
	my $im    = Image::Magick->new(size=>$config->{size}."x".$config->{size});
	my $fsize = ($num == 100 ? $config->{fsize_100} : $config->{fsize});
	my $xoff  = $config->{x};
	my $yoff  = $config->{y};
	my $ol    = $config->{ol};
	my ($tc_out, $tc_in) = qw(black white);
	   ($tc_out, $tc_in) = qw(white black) if($config->{theme} == 1);
	   ($tc_out, $tc_in) = qw(#6d6725  #ffef37) if($config->{theme} == GOLDEN);
	
	$tc_in = "red" if $num <= 15 && $config->{fulldigit} && $config->{theme} != GOLDEN;
	
	$im->Read("xc:transparent");

	foreach my $x ($ol*-1..$ol) {
		foreach my $y ($ol*-1..$ol) {
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
