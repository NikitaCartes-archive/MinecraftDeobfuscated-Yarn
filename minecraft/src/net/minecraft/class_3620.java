package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3620 {
	public static final class_3620[] field_16006 = new class_3620[64];
	public static final class_3620 field_16008 = new class_3620(0, 0);
	public static final class_3620 field_15999 = new class_3620(1, 8368696);
	public static final class_3620 field_15986 = new class_3620(2, 16247203);
	public static final class_3620 field_15979 = new class_3620(3, 13092807);
	public static final class_3620 field_16002 = new class_3620(4, 16711680);
	public static final class_3620 field_16016 = new class_3620(5, 10526975);
	public static final class_3620 field_16005 = new class_3620(6, 10987431);
	public static final class_3620 field_16004 = new class_3620(7, 31744);
	public static final class_3620 field_16022 = new class_3620(8, 16777215);
	public static final class_3620 field_15976 = new class_3620(9, 10791096);
	public static final class_3620 field_16000 = new class_3620(10, 9923917);
	public static final class_3620 field_16023 = new class_3620(11, 7368816);
	public static final class_3620 field_16019 = new class_3620(12, 4210943);
	public static final class_3620 field_15996 = new class_3620(13, 9402184);
	public static final class_3620 field_16025 = new class_3620(14, 16776437);
	public static final class_3620 field_15987 = new class_3620(15, 14188339);
	public static final class_3620 field_15998 = new class_3620(16, 11685080);
	public static final class_3620 field_16024 = new class_3620(17, 6724056);
	public static final class_3620 field_16010 = new class_3620(18, 15066419);
	public static final class_3620 field_15997 = new class_3620(19, 8375321);
	public static final class_3620 field_16030 = new class_3620(20, 15892389);
	public static final class_3620 field_15978 = new class_3620(21, 5000268);
	public static final class_3620 field_15993 = new class_3620(22, 10066329);
	public static final class_3620 field_16026 = new class_3620(23, 5013401);
	public static final class_3620 field_16014 = new class_3620(24, 8339378);
	public static final class_3620 field_15984 = new class_3620(25, 3361970);
	public static final class_3620 field_15977 = new class_3620(26, 6704179);
	public static final class_3620 field_15995 = new class_3620(27, 6717235);
	public static final class_3620 field_16020 = new class_3620(28, 10040115);
	public static final class_3620 field_16009 = new class_3620(29, 1644825);
	public static final class_3620 field_15994 = new class_3620(30, 16445005);
	public static final class_3620 field_15983 = new class_3620(31, 6085589);
	public static final class_3620 field_15980 = new class_3620(32, 4882687);
	public static final class_3620 field_16001 = new class_3620(33, 55610);
	public static final class_3620 field_16017 = new class_3620(34, 8476209);
	public static final class_3620 field_16012 = new class_3620(35, 7340544);
	public static final class_3620 field_16003 = new class_3620(36, 13742497);
	public static final class_3620 field_15981 = new class_3620(37, 10441252);
	public static final class_3620 field_15985 = new class_3620(38, 9787244);
	public static final class_3620 field_15991 = new class_3620(39, 7367818);
	public static final class_3620 field_16013 = new class_3620(40, 12223780);
	public static final class_3620 field_16018 = new class_3620(41, 6780213);
	public static final class_3620 field_15989 = new class_3620(42, 10505550);
	public static final class_3620 field_16027 = new class_3620(43, 3746083);
	public static final class_3620 field_15988 = new class_3620(44, 8874850);
	public static final class_3620 field_15990 = new class_3620(45, 5725276);
	public static final class_3620 field_16029 = new class_3620(46, 8014168);
	public static final class_3620 field_16015 = new class_3620(47, 4996700);
	public static final class_3620 field_15992 = new class_3620(48, 4993571);
	public static final class_3620 field_16028 = new class_3620(49, 5001770);
	public static final class_3620 field_15982 = new class_3620(50, 9321518);
	public static final class_3620 field_16007 = new class_3620(51, 2430480);
	public final int field_16011;
	public final int field_16021;

	private class_3620(int i, int j) {
		if (i >= 0 && i <= 63) {
			this.field_16021 = i;
			this.field_16011 = j;
			field_16006[i] = this;
		} else {
			throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_15820(int i) {
		int j = 220;
		if (i == 3) {
			j = 135;
		}

		if (i == 2) {
			j = 255;
		}

		if (i == 1) {
			j = 220;
		}

		if (i == 0) {
			j = 180;
		}

		int k = (this.field_16011 >> 16 & 0xFF) * j / 255;
		int l = (this.field_16011 >> 8 & 0xFF) * j / 255;
		int m = (this.field_16011 & 0xFF) * j / 255;
		return 0xFF000000 | m << 16 | l << 8 | k;
	}
}
