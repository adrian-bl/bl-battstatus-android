#!/usr/bin/perl

#BlackFontBigCircledHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "bfb_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/bfb_$num" />
</layer-list>
EOF
	close(X);
}

#BlackFontSmallCircledHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "bfs_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/bfs_$num" />
</layer-list>
EOF
	close(X);
}

#WhiteFontBigCircledHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "wfb_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/wfb_$num" />
</layer-list>
EOF
	close(X);
}

#WhiteFontSmallCircledHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "wfs_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/wfs_$num" />
</layer-list>
EOF
	close(X);
}

