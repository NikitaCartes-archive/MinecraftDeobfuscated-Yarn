package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3988 extends class_1296 implements class_1655, class_1915 {
	@Nullable
	private class_1657 field_17722;
	@Nullable
	protected class_1916 field_17721;
	private final class_1277 field_17723 = new class_1277(8);

	public class_3988(class_1299<? extends class_3988> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public int method_19269() {
		return 0;
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return this.method_6109() ? 0.81F : 1.62F;
	}

	@Override
	public void method_8259(@Nullable class_1657 arg) {
		this.field_17722 = arg;
	}

	@Nullable
	@Override
	public class_1657 method_8257() {
		return this.field_17722;
	}

	public boolean method_18009() {
		return this.field_17722 != null;
	}

	@Override
	public class_1916 method_8264() {
		if (this.field_17721 == null) {
			this.field_17721 = new class_1916();
			this.method_7237();
		}

		return this.field_17721;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable class_1916 arg) {
	}

	@Override
	public void method_19271(int i) {
	}

	@Override
	public void method_8262(class_1914 arg) {
		this.field_6191 = -this.method_5970();
		this.method_5783(this.method_18010(), this.method_6107(), this.method_6017());
		this.method_18008(arg);
		if (this.field_17722 instanceof class_3222) {
			class_174.field_1206.method_9146((class_3222)this.field_17722, this, arg.method_8250());
		}
	}

	protected abstract void method_18008(class_1914 arg);

	@Override
	public boolean method_19270() {
		return true;
	}

	@Override
	public void method_8258(class_1799 arg) {
		if (!this.field_6002.field_9236 && this.field_6191 > -this.method_5970() + 20) {
			this.field_6191 = -this.method_5970();
			this.method_5783(this.method_18012(!arg.method_7960()), this.method_6107(), this.method_6017());
		}
	}

	protected class_3414 method_18010() {
		return class_3417.field_14815;
	}

	protected class_3414 method_18012(boolean bl) {
		return bl ? class_3417.field_14815 : class_3417.field_15008;
	}

	public void method_20010() {
		this.method_5783(class_3417.field_19152, this.method_6107(), this.method_6017());
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		class_1916 lv = this.method_8264();
		if (!lv.isEmpty()) {
			arg.method_10566("Offers", lv.method_8268());
		}

		class_2499 lv2 = new class_2499();

		for (int i = 0; i < this.field_17723.method_5439(); i++) {
			class_1799 lv3 = this.field_17723.method_5438(i);
			if (!lv3.method_7960()) {
				lv2.add(lv3.method_7953(new class_2487()));
			}
		}

		arg.method_10566("Inventory", lv2);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		if (arg.method_10573("Offers", 10)) {
			this.field_17721 = new class_1916(arg.method_10562("Offers"));
		}

		class_2499 lv = arg.method_10554("Inventory", 10);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = class_1799.method_7915(lv.method_10602(i));
			if (!lv2.method_7960()) {
				this.field_17723.method_5491(lv2);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	protected void method_18007(class_2394 arg) {
		for (int i = 0; i < 5; i++) {
			double d = this.field_5974.nextGaussian() * 0.02;
			double e = this.field_5974.nextGaussian() * 0.02;
			double f = this.field_5974.nextGaussian() * 0.02;
			this.field_6002
				.method_8406(
					arg,
					this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					this.field_6010 + 1.0 + (double)(this.field_5974.nextFloat() * this.method_17682()),
					this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
					d,
					e,
					f
				);
		}
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return false;
	}

	public class_1277 method_18011() {
		return this.field_17723;
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (super.method_5758(i, arg)) {
			return true;
		} else {
			int j = i - 300;
			if (j >= 0 && j < this.field_17723.method_5439()) {
				this.field_17723.method_5447(j, arg);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public class_1937 method_8260() {
		return this.field_6002;
	}

	protected abstract void method_7237();

	protected void method_19170(class_1916 arg, class_3853.class_1652[] args, int i) {
		Set<Integer> set = Sets.<Integer>newHashSet();
		if (args.length > i) {
			while (set.size() < i) {
				set.add(this.field_5974.nextInt(args.length));
			}
		} else {
			for (int j = 0; j < args.length; j++) {
				set.add(j);
			}
		}

		for (Integer integer : set) {
			class_3853.class_1652 lv = args[integer];
			class_1914 lv2 = lv.method_7246(this, this.field_5974);
			if (lv2 != null) {
				arg.add(lv2);
			}
		}
	}
}
