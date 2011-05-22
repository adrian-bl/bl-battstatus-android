#!/usr/bin/perl

#FontBig CircleHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "fb_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/fb_$num" />
</layer-list>
EOF
	close(X);
}

#FontBig CircleFilled
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "fb_cr_f_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_f_$num" />
  <item android:drawable="\@drawable/fb_$num" />
</layer-list>
EOF
	close(X);
}

#FontBig CircleHole
foreach my $num (0..100) {
	$num = sprintf("%03d",$num);
	open(X, ">", "fs_cr_h_$num.xml");
	print X << "EOF";
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:drawable="\@drawable/cr_h_$num" />
  <item android:drawable="\@drawable/fs_$num" />
</layer-list>
EOF
	close(X);
}
