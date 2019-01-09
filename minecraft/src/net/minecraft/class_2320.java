package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2320 extends class_2261 {
	public static final class_2754<class_2756> field_10929 = class_2741.field_12533;

	public class_2320(class_2248.class_2251 arg) {
		super(arg);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10929, class_2756.field_12607));
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		class_2756 lv = arg.method_11654(field_10929);
		if (arg2.method_10166() != class_2350.class_2351.field_11052
			|| lv == class_2756.field_12607 != (arg2 == class_2350.field_11036)
			|| arg3.method_11614() == this && arg3.method_11654(field_10929) != lv) {
			return lv == class_2756.field_12607 && arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
				? class_2246.field_10124.method_9564()
				: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
		} else {
			return class_2246.field_10124.method_9564();
		}
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2338 lv = arg.method_8037();
		return lv.method_10264() < 255 && arg.method_8045().method_8320(lv.method_10084()).method_11587(arg) ? super.method_9605(arg) : null;
	}

	@Override
	public void method_9567(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1309 arg4, class_1799 arg5) {
		arg.method_8652(arg2.method_10084(), this.method_9564().method_11657(field_10929, class_2756.field_12609), 3);
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		if (arg.method_11654(field_10929) != class_2756.field_12609) {
			return super.method_9558(arg, arg2, arg3);
		} else {
			class_2680 lv = arg2.method_8320(arg3.method_10074());
			return lv.method_11614() == this && lv.method_11654(field_10929) == class_2756.field_12607;
		}
	}

	public void method_10021(class_1936 arg, class_2338 arg2, int i) {
		arg.method_8652(arg2, this.method_9564().method_11657(field_10929, class_2756.field_12607), i);
		arg.method_8652(arg2.method_10084(), this.method_9564().method_11657(field_10929, class_2756.field_12609), i);
	}

	@Override
	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		super.method_9556(arg, arg2, arg3, class_2246.field_10124.method_9564(), arg5, arg6);
	}

	@Override
	public void method_9576(class_1937 arg, class_2338 arg2, class_2680 arg3, class_1657 arg4) {
		class_2756 lv = arg3.method_11654(field_10929);
		class_2338 lv2 = lv == class_2756.field_12607 ? arg2.method_10084() : arg2.method_10074();
		class_2680 lv3 = arg.method_8320(lv2);
		if (lv3.method_11614() == this && lv3.method_11654(field_10929) != lv) {
			arg.method_8652(lv2, class_2246.field_10124.method_9564(), 35);
			arg.method_8444(arg4, 2001, lv2, class_2248.method_9507(lv3));
			if (!arg.field_9236 && !arg4.method_7337()) {
				method_9511(arg3, arg, arg2, null, arg4, arg4.method_6047());
				method_9511(lv3, arg, lv2, null, arg4, arg4.method_6047());
			}
		}

		super.method_9576(arg, arg2, arg3, arg4);
	}

	@Override
	protected void method_9515(class_2689.class_2690<class_2248, class_2680> arg) {
		arg.method_11667(field_10929);
	}

	@Override
	public class_2248.class_2250 method_16841() {
		return class_2248.class_2250.field_10657;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public long method_9535(class_2680 arg, class_2338 arg2) {
		return class_3532.method_15371(
			arg2.method_10263(), arg2.method_10087(arg.method_11654(field_10929) == class_2756.field_12607 ? 0 : 1).method_10264(), arg2.method_10260()
		);
	}
}
