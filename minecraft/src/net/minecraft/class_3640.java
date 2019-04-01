package net.minecraft;

public class class_3640 implements class_3661 {
	private static final int field_16074 = class_2378.field_11153.method_10249(class_1972.field_9412);
	private static final int field_16073 = class_2378.field_11153.method_10249(class_1972.field_9424);
	private static final int field_16072 = class_2378.field_11153.method_10249(class_1972.field_9472);
	private static final int field_16071 = class_2378.field_11153.method_10249(class_1972.field_9409);
	private static final int field_16069 = class_2378.field_11153.method_10249(class_1972.field_9452);
	private static final int field_16067 = class_2378.field_11153.method_10249(class_1972.field_9417);
	private static final int field_16065 = class_2378.field_11153.method_10249(class_1972.field_9433);
	private static final int field_16063 = class_2378.field_11153.method_10249(class_1972.field_9410);
	private static final int field_16061 = class_2378.field_11153.method_10249(class_1972.field_9462);
	private static final int field_16083 = class_2378.field_11153.method_10249(class_1972.field_9451);
	private static final int field_16081 = class_2378.field_11153.method_10249(class_1972.field_9477);
	private static final int field_16080 = class_2378.field_11153.method_10249(class_1972.field_9475);
	private static final int field_16079 = class_2378.field_11153.method_10249(class_1972.field_9449);
	private static final int field_16078 = class_2378.field_11153.method_10249(class_1972.field_9471);
	private static final int field_16077 = class_2378.field_11153.method_10249(class_1972.field_9420);
	private static final int field_16076 = class_2378.field_11153.method_10249(class_1972.field_9454);
	private static final int[] field_16082 = new int[]{field_16073, field_16071, field_16072, field_16078, field_16083, field_16077};
	private static final int[] field_16064 = new int[]{field_16073, field_16073, field_16073, field_16079, field_16079, field_16083};
	private static final int[] field_16062 = new int[]{field_16071, field_16080, field_16072, field_16083, field_16074, field_16078};
	private static final int[] field_16068 = new int[]{field_16071, field_16072, field_16077, field_16083};
	private static final int[] field_16066 = new int[]{field_16069, field_16069, field_16069, field_16076};
	private final class_2906 field_16075;
	private int[] field_16070 = field_16064;

	public class_3640(class_1942 arg, class_2906 arg2) {
		if (arg == class_1942.field_9268) {
			this.field_16070 = field_16082;
			this.field_16075 = null;
		} else {
			this.field_16075 = arg2;
		}
	}

	@Override
	public int method_15866(class_3630 arg, int i) {
		if (this.field_16075 != null && this.field_16075.method_12615() >= 0) {
			return this.field_16075.method_12615();
		} else {
			int j = (i & 3840) >> 8;
			i &= -3841;
			if (!class_3645.method_15845(i) && i != field_16061) {
				switch (i) {
					case 1:
						if (j > 0) {
							return arg.method_15834(3) == 0 ? field_16065 : field_16063;
						}

						return this.field_16070[arg.method_15834(this.field_16070.length)];
					case 2:
						if (j > 0) {
							return field_16067;
						}

						return field_16062[arg.method_15834(field_16062.length)];
					case 3:
						if (j > 0) {
							return field_16081;
						}

						return field_16068[arg.method_15834(field_16068.length)];
					case 4:
						return field_16066[arg.method_15834(field_16066.length)];
					default:
						return field_16061;
				}
			} else {
				return i;
			}
		}
	}
}
