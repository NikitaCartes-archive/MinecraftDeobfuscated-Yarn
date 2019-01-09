package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1781 extends class_1792 {
	public class_1781(class_1792.class_1793 arg) {
		super(arg);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		if (!lv.field_9236) {
			class_2338 lv2 = arg.method_8037();
			class_1799 lv3 = arg.method_8041();
			class_1671 lv4 = new class_1671(
				lv,
				(double)((float)lv2.method_10263() + arg.method_8043()),
				(double)((float)lv2.method_10264() + arg.method_8039()),
				(double)((float)lv2.method_10260() + arg.method_8040()),
				lv3
			);
			lv.method_8649(lv4);
			lv3.method_7934(1);
		}

		return class_1269.field_5812;
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		if (arg2.method_6128()) {
			class_1799 lv = arg2.method_5998(arg3);
			if (!arg.field_9236) {
				class_1671 lv2 = new class_1671(arg, lv, arg2);
				arg.method_8649(lv2);
				if (!arg2.field_7503.field_7477) {
					lv.method_7934(1);
				}
			}

			return new class_1271<>(class_1269.field_5812, arg2.method_5998(arg3));
		} else {
			return new class_1271<>(class_1269.field_5811, arg2.method_5998(arg3));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		class_2487 lv = arg.method_7941("Fireworks");
		if (lv != null) {
			if (lv.method_10573("Flight", 99)) {
				list.add(
					new class_2588("item.minecraft.firework_rocket.flight")
						.method_10864(" ")
						.method_10864(String.valueOf(lv.method_10571("Flight")))
						.method_10854(class_124.field_1080)
				);
			}

			class_2499 lv2 = lv.method_10554("Explosions", 10);
			if (!lv2.isEmpty()) {
				for (int i = 0; i < lv2.size(); i++) {
					class_2487 lv3 = lv2.method_10602(i);
					List<class_2561> list2 = Lists.<class_2561>newArrayList();
					class_1780.method_7809(lv3, list2);
					if (!list2.isEmpty()) {
						for (int j = 1; j < list2.size(); j++) {
							list2.set(j, new class_2585("  ").method_10852((class_2561)list2.get(j)).method_10854(class_124.field_1080));
						}

						list.addAll(list2);
					}
				}
			}
		}
	}

	public static enum class_1782 {
		field_7976(0, "small_ball"),
		field_7977(1, "large_ball"),
		field_7973(2, "star"),
		field_7974(3, "creeper"),
		field_7970(4, "burst");

		private static final class_1781.class_1782[] field_7975 = (class_1781.class_1782[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(arg -> arg.field_7972))
			.toArray(class_1781.class_1782[]::new);
		private final int field_7972;
		private final String field_7971;

		private class_1782(int j, String string2) {
			this.field_7972 = j;
			this.field_7971 = string2;
		}

		public int method_7816() {
			return this.field_7972;
		}

		@Environment(EnvType.CLIENT)
		public String method_7812() {
			return this.field_7971;
		}

		@Environment(EnvType.CLIENT)
		public static class_1781.class_1782 method_7813(int i) {
			return i >= 0 && i < field_7975.length ? field_7975[i] : field_7976;
		}
	}
}
