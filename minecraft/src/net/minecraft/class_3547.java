package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class class_3547 extends class_3560<class_3547.class_3548> {
	protected class_3547(class_2823 arg) {
		super(class_1944.field_9282, arg, new class_3547.class_3548(new Long2ObjectOpenHashMap<>()));
	}

	@Override
	protected int method_15538(long l) {
		long m = class_4076.method_18691(l);
		class_2804 lv = this.method_15522(m, false);
		return lv == null
			? 0
			: lv.method_12139(
				class_4076.method_18684(class_2338.method_10061(l)),
				class_4076.method_18684(class_2338.method_10071(l)),
				class_4076.method_18684(class_2338.method_10083(l))
			);
	}

	public static final class class_3548 extends class_3556<class_3547.class_3548> {
		public class_3548(Long2ObjectOpenHashMap<class_2804> long2ObjectOpenHashMap) {
			super(long2ObjectOpenHashMap);
		}

		public class_3547.class_3548 method_15443() {
			return new class_3547.class_3548(this.field_15791.clone());
		}
	}
}
