package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;

public abstract class class_3790 extends class_3443 {
	protected final class_3784 field_16693;
	protected class_2338 field_16695;
	private final int field_16692;
	protected final class_2470 field_16694;
	private final List<class_3780> field_16696 = Lists.<class_3780>newArrayList();

	public class_3790(class_3773 arg, class_3485 arg2, class_3784 arg3, class_2338 arg4, int i, class_2470 arg5) {
		super(arg, 0);
		this.field_16693 = arg3;
		this.field_16695 = arg4;
		this.field_16692 = i;
		this.field_16694 = arg5;
		this.field_15315 = arg3.method_16628(arg2, arg4, arg5);
	}

	public class_3790(class_3485 arg, class_2487 arg2, class_3773 arg3) {
		super(arg3, arg2);
		this.field_16695 = new class_2338(arg2.method_10550("PosX"), arg2.method_10550("PosY"), arg2.method_10550("PosZ"));
		this.field_16692 = arg2.method_10550("ground_level_delta");
		this.field_16693 = class_3817.method_16758(
			new Dynamic<>(class_2509.field_11560, arg2.method_10562("pool_element")), class_2378.field_16793, "element_type", class_3777.field_16663
		);
		this.field_16694 = class_2470.valueOf(arg2.method_10558("rotation"));
		this.field_15315 = this.field_16693.method_16628(arg, this.field_16695, this.field_16694);
		class_2499 lv = arg2.method_10554("junctions", 10);
		this.field_16696.clear();
		lv.forEach(argx -> this.field_16696.add(class_3780.method_16613(new Dynamic<>(class_2509.field_11560, argx))));
	}

	@Override
	protected void method_14943(class_2487 arg) {
		arg.method_10569("PosX", this.field_16695.method_10263());
		arg.method_10569("PosY", this.field_16695.method_10264());
		arg.method_10569("PosZ", this.field_16695.method_10260());
		arg.method_10569("ground_level_delta", this.field_16692);
		arg.method_10566("pool_element", this.field_16693.method_16755(class_2509.field_11560).getValue());
		arg.method_10582("rotation", this.field_16694.name());
		class_2499 lv = new class_2499();

		for (class_3780 lv2 : this.field_16696) {
			lv.method_10606(lv2.method_16612(class_2509.field_11560).getValue());
		}

		arg.method_10566("junctions", lv);
	}

	@Override
	public boolean method_14931(class_1936 arg, Random random, class_3341 arg2, class_1923 arg3) {
		return this.field_16693.method_16626(arg, this.field_16695, this.field_16694, arg2, random);
	}

	@Override
	public void method_14922(int i, int j, int k) {
		super.method_14922(i, j, k);
		this.field_16695 = this.field_16695.method_10069(i, j, k);
	}

	@Override
	public class_2470 method_16888() {
		return this.field_16694;
	}

	public String toString() {
		return String.format("<%s | %s | %s>", this.getClass().getSimpleName(), this.field_16695, this.field_16694);
	}

	public class_3784 method_16644() {
		return this.field_16693;
	}

	public class_2338 method_16648() {
		return this.field_16695;
	}

	public int method_16646() {
		return this.field_16692;
	}

	public void method_16647(class_3780 arg) {
		this.field_16696.add(arg);
	}

	public List<class_3780> method_16645() {
		return this.field_16696;
	}
}
