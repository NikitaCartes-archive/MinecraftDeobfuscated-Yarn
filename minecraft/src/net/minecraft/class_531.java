package net.minecraft;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_531 {
	private static final class_537 field_3261 = new class_531.class_532();
	private static final class_537 field_3262 = new class_531.class_533(-1, true);
	private static final class_537 field_3256 = new class_531.class_533(1, true);
	private static final class_537 field_3259 = new class_531.class_533(1, false);
	public static final class_537 field_3260 = new class_537() {
		@Override
		public void method_2783(class_531 arg) {
		}

		@Override
		public class_2561 method_16892() {
			return new class_2585("");
		}

		@Override
		public void method_2784(float f, int i) {
		}

		@Override
		public boolean method_16893() {
			return false;
		}
	};
	private final class_536 field_3255;
	private final List<class_539> field_3257 = Lists.<class_539>newArrayList();
	private class_535 field_3258;
	private int field_3254 = -1;
	private int field_3263;

	public class_531(class_536 arg) {
		this.field_3258 = new class_534();
		this.field_3255 = arg;
	}

	public class_537 method_2777(int i) {
		int j = i + this.field_3263 * 6;
		if (this.field_3263 > 0 && i == 0) {
			return field_3262;
		} else if (i == 7) {
			return j < this.field_3258.method_2780().size() ? field_3256 : field_3259;
		} else if (i == 8) {
			return field_3261;
		} else {
			return j >= 0 && j < this.field_3258.method_2780().size()
				? MoreObjects.firstNonNull((class_537)this.field_3258.method_2780().get(j), field_3260)
				: field_3260;
		}
	}

	public List<class_537> method_2770() {
		List<class_537> list = Lists.<class_537>newArrayList();

		for (int i = 0; i <= 8; i++) {
			list.add(this.method_2777(i));
		}

		return list;
	}

	public class_537 method_2774() {
		return this.method_2777(this.field_3254);
	}

	public class_535 method_2776() {
		return this.field_3258;
	}

	public void method_2771(int i) {
		class_537 lv = this.method_2777(i);
		if (lv != field_3260) {
			if (this.field_3254 == i && lv.method_16893()) {
				lv.method_2783(this);
			} else {
				this.field_3254 = i;
			}
		}
	}

	public void method_2779() {
		this.field_3255.method_2782(this);
	}

	public int method_2773() {
		return this.field_3254;
	}

	public void method_2778(class_535 arg) {
		this.field_3257.add(this.method_2772());
		this.field_3258 = arg;
		this.field_3254 = -1;
		this.field_3263 = 0;
	}

	public class_539 method_2772() {
		return new class_539(this.field_3258, this.method_2770(), this.field_3254);
	}

	@Environment(EnvType.CLIENT)
	static class class_532 implements class_537 {
		private class_532() {
		}

		@Override
		public void method_2783(class_531 arg) {
			arg.method_2779();
		}

		@Override
		public class_2561 method_16892() {
			return new class_2588("spectatorMenu.close");
		}

		@Override
		public void method_2784(float f, int i) {
			class_310.method_1551().method_1531().method_4618(class_365.field_2199);
			class_332.blit(0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
		}

		@Override
		public boolean method_16893() {
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_533 implements class_537 {
		private final int field_3264;
		private final boolean field_3265;

		public class_533(int i, boolean bl) {
			this.field_3264 = i;
			this.field_3265 = bl;
		}

		@Override
		public void method_2783(class_531 arg) {
			arg.field_3263 = arg.field_3263 + this.field_3264;
		}

		@Override
		public class_2561 method_16892() {
			return this.field_3264 < 0 ? new class_2588("spectatorMenu.previous_page") : new class_2588("spectatorMenu.next_page");
		}

		@Override
		public void method_2784(float f, int i) {
			class_310.method_1551().method_1531().method_4618(class_365.field_2199);
			if (this.field_3264 < 0) {
				class_332.blit(0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
			} else {
				class_332.blit(0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
			}
		}

		@Override
		public boolean method_16893() {
			return this.field_3265;
		}
	}
}
