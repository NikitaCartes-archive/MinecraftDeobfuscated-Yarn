package net.minecraft;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1826 extends class_1792 {
	private static final Map<class_1299<?>, class_1826> field_8914 = Maps.<class_1299<?>, class_1826>newIdentityHashMap();
	private final int field_8916;
	private final int field_8915;
	private final class_1299<?> field_8917;

	public class_1826(class_1299<?> arg, int i, int j, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_8917 = arg;
		this.field_8916 = i;
		this.field_8915 = j;
		field_8914.put(arg, this);
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		if (lv.field_9236) {
			return class_1269.field_5812;
		} else {
			class_1799 lv2 = arg.method_8041();
			class_2338 lv3 = arg.method_8037();
			class_2350 lv4 = arg.method_8038();
			class_2680 lv5 = lv.method_8320(lv3);
			class_2248 lv6 = lv5.method_11614();
			if (lv6 == class_2246.field_10260) {
				class_2586 lv7 = lv.method_8321(lv3);
				if (lv7 instanceof class_2636) {
					class_1917 lv8 = ((class_2636)lv7).method_11390();
					class_1299<?> lv9 = this.method_8015(lv2.method_7969());
					lv8.method_8274(lv9);
					lv7.method_5431();
					lv.method_8413(lv3, lv5, lv5, 3);
					lv2.method_7934(1);
					return class_1269.field_5812;
				}
			}

			class_2338 lv10;
			if (lv5.method_11628(lv, lv3).method_1110()) {
				lv10 = lv3;
			} else {
				lv10 = lv3.method_10093(lv4);
			}

			class_1299<?> lv11 = this.method_8015(lv2.method_7969());
			if (lv11.method_5894(lv, lv2, arg.method_8036(), lv10, class_3730.field_16465, true, !Objects.equals(lv3, lv10) && lv4 == class_2350.field_11036) != null) {
				lv2.method_7934(1);
			}

			return class_1269.field_5812;
		}
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		if (arg.field_9236) {
			return new class_1271<>(class_1269.field_5811, lv);
		} else {
			class_239 lv2 = method_7872(arg, arg2, class_3959.class_242.field_1345);
			if (lv2.method_17783() != class_239.class_240.field_1332) {
				return new class_1271<>(class_1269.field_5811, lv);
			} else {
				class_3965 lv3 = (class_3965)lv2;
				class_2338 lv4 = lv3.method_17777();
				if (!(arg.method_8320(lv4).method_11614() instanceof class_2404)) {
					return new class_1271<>(class_1269.field_5811, lv);
				} else if (arg.method_8505(arg2, lv4) && arg2.method_7343(lv4, lv3.method_17780(), lv)) {
					class_1299<?> lv5 = this.method_8015(lv.method_7969());
					if (lv5.method_5894(arg, lv, arg2, lv4, class_3730.field_16465, false, false) == null) {
						return new class_1271<>(class_1269.field_5811, lv);
					} else {
						if (!arg2.field_7503.field_7477) {
							lv.method_7934(1);
						}

						arg2.method_7259(class_3468.field_15372.method_14956(this));
						return new class_1271<>(class_1269.field_5812, lv);
					}
				} else {
					return new class_1271<>(class_1269.field_5814, lv);
				}
			}
		}
	}

	public boolean method_8018(@Nullable class_2487 arg, class_1299<?> arg2) {
		return Objects.equals(this.method_8015(arg), arg2);
	}

	@Environment(EnvType.CLIENT)
	public int method_8016(int i) {
		return i == 0 ? this.field_8916 : this.field_8915;
	}

	@Environment(EnvType.CLIENT)
	public static class_1826 method_8019(@Nullable class_1299<?> arg) {
		return (class_1826)field_8914.get(arg);
	}

	public static Iterable<class_1826> method_8017() {
		return Iterables.unmodifiableIterable(field_8914.values());
	}

	public class_1299<?> method_8015(@Nullable class_2487 arg) {
		if (arg != null && arg.method_10573("EntityTag", 10)) {
			class_2487 lv = arg.method_10562("EntityTag");
			if (lv.method_10573("id", 8)) {
				return (class_1299<?>)class_1299.method_5898(lv.method_10558("id")).orElse(this.field_8917);
			}
		}

		return this.field_8917;
	}
}
