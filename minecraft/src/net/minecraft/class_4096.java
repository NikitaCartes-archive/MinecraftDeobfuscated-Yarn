package net.minecraft;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;

public class class_4096 extends class_4097<class_1309> {
	private final class_4158 field_19287;
	private final class_4140<class_4208> field_19288;
	private final boolean field_18854;
	private long field_18332;
	private final Long2LongMap field_19289 = new Long2LongOpenHashMap();
	private int field_19290;

	public class_4096(class_4158 arg, class_4140<class_4208> arg2, boolean bl) {
		super(ImmutableMap.of(arg2, class_4141.field_18457));
		this.field_19287 = arg;
		this.field_19288 = arg2;
		this.field_18854 = bl;
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		return this.field_18854 && arg2.method_6109() ? false : arg.method_8510() - this.field_18332 >= 20L;
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		this.field_19290 = 0;
		this.field_18332 = arg.method_8510() + (long)arg.method_8409().nextInt(20);
		class_1314 lv = (class_1314)arg2;
		class_4153 lv2 = arg.method_19494();
		Predicate<class_2338> predicate = arg3 -> {
			long lx = arg3.method_10063();
			if (this.field_19289.containsKey(lx)) {
				return false;
			} else if (++this.field_19290 >= 5) {
				return false;
			} else {
				class_2338 lv2x;
				if (this.field_19287 == class_4158.field_18518) {
					class_2338.class_2339 lvx = new class_2338.class_2339(arg3);
					this.method_20496(arg, lvx);
					lvx.method_10098(class_2350.field_11036);
					lv2x = lvx;
				} else {
					lv2x = arg3;
				}

				if (lv.method_5829().method_1014(2.0).method_1006(new class_243(lv2x))) {
					return true;
				} else {
					class_11 lv3x = lv.method_5942().method_6348(lv2x);
					boolean bl = lv3x != null && lv3x.method_19313(lv2x);
					if (!bl) {
						this.field_19289.put(lx, this.field_18332 + 40L);
					}

					return bl;
				}
			}
		};
		Optional<class_2338> optional = lv2.method_19131(this.field_19287.method_19164(), predicate, new class_2338(arg2), 48);
		if (optional.isPresent()) {
			class_2338 lv3 = (class_2338)optional.get();
			arg2.method_18868().method_18878(this.field_19288, class_4208.method_19443(arg.method_8597().method_12460(), lv3));
			class_4209.method_19778(arg, lv3);
		} else if (this.field_19290 < 5) {
			this.field_19289.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.field_18332);
		}
	}

	private void method_20496(class_3218 arg, class_2338.class_2339 arg2) {
		do {
			arg2.method_10098(class_2350.field_11033);
		} while (arg.method_8320(arg2).method_11628(arg, arg2).method_1110() && arg2.method_10264() > 0);
	}
}
