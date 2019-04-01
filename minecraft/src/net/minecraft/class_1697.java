package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1697 extends class_1688 {
	private static final class_2940<String> field_7743 = class_2945.method_12791(class_1697.class, class_2943.field_13326);
	private static final class_2940<class_2561> field_7741 = class_2945.method_12791(class_1697.class, class_2943.field_13317);
	private final class_1918 field_7744 = new class_1697.class_1698();
	private int field_7742;

	public class_1697(class_1299<? extends class_1697> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	public class_1697(class_1937 arg, double d, double e, double f) {
		super(class_1299.field_6136, arg, d, e, f);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.method_5841().method_12784(field_7743, "");
		this.method_5841().method_12784(field_7741, new class_2585(""));
	}

	@Override
	protected void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7744.method_8305(arg);
		this.method_5841().method_12778(field_7743, this.method_7567().method_8289());
		this.method_5841().method_12778(field_7741, this.method_7567().method_8292());
	}

	@Override
	protected void method_5652(class_2487 arg) {
		super.method_5652(arg);
		this.field_7744.method_8297(arg);
	}

	@Override
	public class_1688.class_1689 method_7518() {
		return class_1688.class_1689.field_7681;
	}

	@Override
	public class_2680 method_7517() {
		return class_2246.field_10525.method_9564();
	}

	public class_1918 method_7567() {
		return this.field_7744;
	}

	@Override
	public void method_7506(int i, int j, int k, boolean bl) {
		if (bl && this.field_6012 - this.field_7742 >= 4) {
			this.method_7567().method_8301(this.field_6002);
			this.field_7742 = this.field_6012;
		}
	}

	@Override
	public boolean method_5688(class_1657 arg, class_1268 arg2) {
		this.field_7744.method_8288(arg);
		return true;
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		super.method_5674(arg);
		if (field_7741.equals(arg)) {
			try {
				this.field_7744.method_8291(this.method_5841().method_12789(field_7741));
			} catch (Throwable var3) {
			}
		} else if (field_7743.equals(arg)) {
			this.field_7744.method_8286(this.method_5841().method_12789(field_7743));
		}
	}

	@Override
	public boolean method_5833() {
		return true;
	}

	public class class_1698 extends class_1918 {
		@Override
		public class_3218 method_8293() {
			return (class_3218)class_1697.this.field_6002;
		}

		@Override
		public void method_8295() {
			class_1697.this.method_5841().method_12778(class_1697.field_7743, this.method_8289());
			class_1697.this.method_5841().method_12778(class_1697.field_7741, this.method_8292());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public class_243 method_8300() {
			return new class_243(class_1697.this.field_5987, class_1697.this.field_6010, class_1697.this.field_6035);
		}

		@Environment(EnvType.CLIENT)
		public class_1697 method_7569() {
			return class_1697.this;
		}

		@Override
		public class_2168 method_8303() {
			return new class_2168(
				this,
				new class_243(class_1697.this.field_5987, class_1697.this.field_6010, class_1697.this.field_6035),
				class_1697.this.method_5802(),
				this.method_8293(),
				2,
				this.method_8299().getString(),
				class_1697.this.method_5476(),
				this.method_8293().method_8503(),
				class_1697.this
			);
		}
	}
}
