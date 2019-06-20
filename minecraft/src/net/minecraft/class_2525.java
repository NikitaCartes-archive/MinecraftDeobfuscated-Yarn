package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2525 extends class_2475 implements class_2402 {
	public static final class_2754<class_2756> field_11616 = class_2475.field_11484;
	protected static final class_265 field_11615 = class_2248.method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public class_2525(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_265 method_9530(class_2680 arg, class_1922 arg2, class_2338 arg3, class_3726 arg4) {
		return field_11615;
	}

	@Override
	protected boolean method_9695(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return class_2248.method_20045(arg, arg2, arg3, class_2350.field_11036) && arg.method_11614() != class_2246.field_10092;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return new class_1799(class_2246.field_10376);
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		class_2680 lv = super.method_9605(arg);
		if (lv != null) {
			class_3610 lv2 = arg.method_8045().method_8316(arg.method_8037().method_10084());
			if (lv2.method_15767(class_3486.field_15517) && lv2.method_15761() == 8) {
				return lv;
			}
		}

		return null;
	}

	@Override
	public boolean method_9558(class_2680 arg, class_1941 arg2, class_2338 arg3) {
		if (arg.method_11654(field_11616) == class_2756.field_12609) {
			class_2680 lv = arg2.method_8320(arg3.method_10074());
			return lv.method_11614() == this && lv.method_11654(field_11616) == class_2756.field_12607;
		} else {
			class_3610 lv2 = arg2.method_8316(arg3);
			return super.method_9558(arg, arg2, arg3) && lv2.method_15767(class_3486.field_15517) && lv2.method_15761() == 8;
		}
	}

	@Override
	public class_3610 method_9545(class_2680 arg) {
		return class_3612.field_15910.method_15729(false);
	}

	@Override
	public boolean method_10310(class_1922 arg, class_2338 arg2, class_2680 arg3, class_3611 arg4) {
		return false;
	}

	@Override
	public boolean method_10311(class_1936 arg, class_2338 arg2, class_2680 arg3, class_3610 arg4) {
		return false;
	}
}
