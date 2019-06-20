package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_663 extends class_4003 {
	private final class_3611 field_3789;

	private class_663(class_1937 arg, double d, double e, double f, class_3611 arg2) {
		super(arg, d, e, f);
		this.method_3080(0.01F, 0.01F);
		this.field_3844 = 0.06F;
		this.field_3789 = arg2;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17828;
	}

	@Override
	public int method_3068(float f) {
		return this.field_3789.method_15791(class_3486.field_15518) ? 240 : super.method_3068(f);
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		this.method_18821();
		if (!this.field_3843) {
			this.field_3869 = this.field_3869 - (double)this.field_3844;
			this.method_3069(this.field_3852, this.field_3869, this.field_3850);
			this.method_18822();
			if (!this.field_3843) {
				this.field_3852 *= 0.98F;
				this.field_3869 *= 0.98F;
				this.field_3850 *= 0.98F;
				class_2338 lv = new class_2338(this.field_3874, this.field_3854, this.field_3871);
				class_3610 lv2 = this.field_3851.method_8316(lv);
				if (lv2.method_15772() == this.field_3789 && this.field_3854 < (double)((float)lv.method_10264() + lv2.method_15763(this.field_3851, lv))) {
					this.method_3085();
				}
			}
		}
	}

	protected void method_18821() {
		if (this.field_3847-- <= 0) {
			this.method_3085();
		}
	}

	protected void method_18822() {
	}

	@Environment(EnvType.CLIENT)
	static class class_4082 extends class_663.class_4084 {
		private class_4082(class_1937 arg, double d, double e, double f, class_3611 arg2, class_2394 arg3) {
			super(arg, d, e, f, arg2, arg3);
		}

		@Override
		protected void method_18821() {
			this.field_3861 = 1.0F;
			this.field_3842 = 16.0F / (float)(40 - this.field_3847 + 16);
			this.field_3859 = 4.0F / (float)(40 - this.field_3847 + 8);
			super.method_18821();
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4083 extends class_663 {
		private final class_2394 field_18292;

		private class_4083(class_1937 arg, double d, double e, double f, class_3611 arg2, class_2394 arg3) {
			super(arg, d, e, f, arg2);
			this.field_18292 = arg3;
			this.field_3847 = (int)(64.0 / (Math.random() * 0.8 + 0.2));
		}

		@Override
		protected void method_18822() {
			if (this.field_3845) {
				this.method_3085();
				this.field_3851.method_8406(this.field_18292, this.field_3874, this.field_3854, this.field_3871, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4084 extends class_663 {
		private final class_2394 field_18293;

		private class_4084(class_1937 arg, double d, double e, double f, class_3611 arg2, class_2394 arg3) {
			super(arg, d, e, f, arg2);
			this.field_18293 = arg3;
			this.field_3844 *= 0.02F;
			this.field_3847 = 40;
		}

		@Override
		protected void method_18821() {
			if (this.field_3847-- <= 0) {
				this.method_3085();
				this.field_3851.method_8406(this.field_18293, this.field_3874, this.field_3854, this.field_3871, this.field_3852, this.field_3869, this.field_3850);
			}
		}

		@Override
		protected void method_18822() {
			this.field_3852 *= 0.02;
			this.field_3869 *= 0.02;
			this.field_3850 *= 0.02;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4085 extends class_663 {
		private class_4085(class_1937 arg, double d, double e, double f, class_3611 arg2) {
			super(arg, d, e, f, arg2);
			this.field_3847 = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4086 implements class_707<class_2400> {
		protected final class_4002 field_18294;

		public class_4086(class_4002 arg) {
			this.field_18294 = arg;
		}

		public class_703 method_18823(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_663 lv = new class_663.class_4083(arg2, d, e, f, class_3612.field_15908, class_2398.field_18305);
			lv.method_3084(1.0F, 0.2857143F, 0.083333336F);
			lv.method_18140(this.field_18294);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4087 implements class_707<class_2400> {
		protected final class_4002 field_18296;

		public class_4087(class_4002 arg) {
			this.field_18296 = arg;
		}

		public class_703 method_18824(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_663 lv = new class_663.class_4085(arg2, d, e, f, class_3612.field_15908);
			lv.method_3084(1.0F, 0.2857143F, 0.083333336F);
			lv.method_18140(this.field_18296);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4088 implements class_707<class_2400> {
		protected final class_4002 field_18298;

		public class_4088(class_4002 arg) {
			this.field_18298 = arg;
		}

		public class_703 method_18825(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_663 lv = new class_663.class_4084(arg2, d, e, f, class_3612.field_15910, class_2398.field_18306);
			lv.method_3084(0.2F, 0.3F, 1.0F);
			lv.method_18140(this.field_18298);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_664 implements class_707<class_2400> {
		protected final class_4002 field_18295;

		public class_664(class_4002 arg) {
			this.field_18295 = arg;
		}

		public class_703 method_3017(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_663.class_4082 lv = new class_663.class_4082(arg2, d, e, f, class_3612.field_15908, class_2398.field_18304);
			lv.method_18140(this.field_18295);
			return lv;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_665 implements class_707<class_2400> {
		protected final class_4002 field_18297;

		public class_665(class_4002 arg) {
			this.field_18297 = arg;
		}

		public class_703 method_3018(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			class_663 lv = new class_663.class_4083(arg2, d, e, f, class_3612.field_15910, class_2398.field_11202);
			lv.method_3084(0.2F, 0.3F, 1.0F);
			lv.method_18140(this.field_18297);
			return lv;
		}
	}
}
