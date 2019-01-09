package net.minecraft;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

public class class_1747 extends class_1792 {
	@Deprecated
	private final class_2248 field_7901;

	public class_1747(class_2248 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7901 = arg;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		return this.method_7712(new class_1750(arg));
	}

	public class_1269 method_7712(class_1750 arg) {
		if (!arg.method_7716()) {
			return class_1269.field_5814;
		} else {
			class_1750 lv = this.method_16356(arg);
			class_2680 lv2 = this.method_7707(lv);
			if (lv2 == null) {
				return class_1269.field_5814;
			} else if (!this.method_7708(lv, lv2)) {
				return class_1269.field_5814;
			} else {
				class_2338 lv3 = lv.method_8037();
				class_1937 lv4 = lv.method_8045();
				class_1657 lv5 = lv.method_8036();
				class_1799 lv6 = lv.method_8041();
				class_2680 lv7 = lv4.method_8320(lv3);
				class_2248 lv8 = lv7.method_11614();
				if (lv8 == lv2.method_11614()) {
					this.method_7710(lv3, lv4, lv5, lv6, lv7);
					lv8.method_9567(lv4, lv3, lv7, lv5, lv6);
					if (lv5 instanceof class_3222) {
						class_174.field_1191.method_9087((class_3222)lv5, lv3, lv6);
					}
				}

				class_2498 lv9 = lv7.method_11638();
				lv4.method_8396(lv5, lv3, lv9.method_10598(), class_3419.field_15245, (lv9.method_10597() + 1.0F) / 2.0F, lv9.method_10599() * 0.8F);
				lv6.method_7934(1);
				return class_1269.field_5812;
			}
		}
	}

	public class_1750 method_16356(class_1750 arg) {
		return arg;
	}

	protected boolean method_7710(class_2338 arg, class_1937 arg2, @Nullable class_1657 arg3, class_1799 arg4, class_2680 arg5) {
		return method_7714(arg2, arg3, arg, arg4);
	}

	@Nullable
	protected class_2680 method_7707(class_1750 arg) {
		class_2680 lv = this.method_7711().method_9605(arg);
		return lv != null && this.method_7709(arg, lv) ? lv : null;
	}

	protected boolean method_7709(class_1750 arg, class_2680 arg2) {
		return arg2.method_11591(arg.method_8045(), arg.method_8037()) && arg.method_8045().method_8628(arg2, arg.method_8037());
	}

	protected boolean method_7708(class_1750 arg, class_2680 arg2) {
		return arg.method_8045().method_8652(arg.method_8037(), arg2, 11);
	}

	public static boolean method_7714(class_1937 arg, @Nullable class_1657 arg2, class_2338 arg3, class_1799 arg4) {
		MinecraftServer minecraftServer = arg.method_8503();
		if (minecraftServer == null) {
			return false;
		} else {
			class_2487 lv = arg4.method_7941("BlockEntityTag");
			if (lv != null) {
				class_2586 lv2 = arg.method_8321(arg3);
				if (lv2 != null) {
					if (!arg.field_9236 && lv2.method_11011() && (arg2 == null || !arg2.method_7338())) {
						return false;
					}

					class_2487 lv3 = lv2.method_11007(new class_2487());
					class_2487 lv4 = lv3.method_10553();
					lv3.method_10543(lv);
					lv3.method_10569("x", arg3.method_10263());
					lv3.method_10569("y", arg3.method_10264());
					lv3.method_10569("z", arg3.method_10260());
					if (!lv3.equals(lv4)) {
						lv2.method_11014(lv3);
						lv2.method_5431();
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public String method_7876() {
		return this.method_7711().method_9539();
	}

	@Override
	public void method_7850(class_1761 arg, class_2371<class_1799> arg2) {
		if (this.method_7877(arg)) {
			this.method_7711().method_9578(arg, arg2);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		super.method_7851(arg, arg2, list, arg3);
		this.method_7711().method_9568(arg, arg2, list, arg3);
	}

	public class_2248 method_7711() {
		return this.field_7901;
	}

	public void method_7713(Map<class_2248, class_1792> map, class_1792 arg) {
		map.put(this.method_7711(), arg);
	}
}
