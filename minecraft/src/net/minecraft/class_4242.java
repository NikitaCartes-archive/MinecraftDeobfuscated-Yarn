package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class class_4242 extends class_4097<class_1646> {
	@Nullable
	private class_3765 field_18983;

	public class_4242(int i, int j) {
		super(ImmutableMap.of(), i, j);
	}

	protected boolean method_19951(class_3218 arg, class_1646 arg2) {
		this.field_18983 = arg.method_19502(new class_2338(arg2));
		return this.field_18983 != null && this.field_18983.method_20023() && class_4248.method_20497(arg, arg2);
	}

	protected boolean method_19952(class_3218 arg, class_1646 arg2, long l) {
		return this.field_18983 != null && !this.field_18983.method_20022();
	}

	protected void method_19953(class_3218 arg, class_1646 arg2, long l) {
		this.field_18983 = null;
		arg2.method_18868().method_18871(arg.method_8532(), arg.method_8510());
	}

	protected void method_19954(class_3218 arg, class_1646 arg2, long l) {
		Random random = arg2.method_6051();
		if (random.nextInt(100) == 0) {
			arg2.method_20010();
		}

		if (random.nextInt(200) == 0 && class_4248.method_20497(arg, arg2)) {
			class_1767 lv = class_1767.values()[random.nextInt(class_1767.values().length)];
			int i = random.nextInt(3);
			class_1799 lv2 = this.method_19950(lv, i);
			class_1671 lv3 = new class_1671(arg2.field_6002, arg2.field_5987, arg2.field_6010 + (double)arg2.method_5751(), arg2.field_6035, lv2);
			arg2.field_6002.method_8649(lv3);
		}
	}

	private class_1799 method_19950(class_1767 arg, int i) {
		class_1799 lv = new class_1799(class_1802.field_8639, 1);
		class_1799 lv2 = new class_1799(class_1802.field_8450);
		class_2487 lv3 = lv2.method_7911("Explosion");
		List<Integer> list = Lists.<Integer>newArrayList();
		list.add(arg.method_7790());
		lv3.method_10572("Colors", list);
		lv3.method_10567("Type", (byte)class_1781.class_1782.field_7970.method_7816());
		class_2487 lv4 = lv.method_7911("Fireworks");
		class_2499 lv5 = new class_2499();
		class_2487 lv6 = lv2.method_7941("Explosion");
		if (lv6 != null) {
			lv5.add(lv6);
		}

		lv4.method_10567("Flight", (byte)i);
		if (!lv5.isEmpty()) {
			lv4.method_10566("Explosions", lv5);
		}

		return lv;
	}
}
