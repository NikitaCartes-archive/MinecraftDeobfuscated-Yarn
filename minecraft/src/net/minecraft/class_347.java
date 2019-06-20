package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_347 extends class_4185 {
	private boolean field_2131;

	public class_347(int i, int j, class_4185.class_4241 arg) {
		super(i, j, 20, 20, class_1074.method_4662("narrator.button.difficulty_lock"), arg);
	}

	@Override
	protected String getNarrationMessage() {
		return super.getNarrationMessage()
			+ ". "
			+ (
				this.method_1896() ? class_1074.method_4662("narrator.button.difficulty_lock.locked") : class_1074.method_4662("narrator.button.difficulty_lock.unlocked")
			);
	}

	public boolean method_1896() {
		return this.field_2131;
	}

	public void method_1895(boolean bl) {
		this.field_2131 = bl;
	}

	@Override
	public void renderButton(int i, int j, float f) {
		class_310.method_1551().method_1531().method_4618(class_4185.WIDGETS_LOCATION);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_347.class_348 lv;
		if (!this.active) {
			lv = this.field_2131 ? class_347.class_348.field_2139 : class_347.class_348.field_2140;
		} else if (this.isHovered()) {
			lv = this.field_2131 ? class_347.class_348.field_2138 : class_347.class_348.field_2133;
		} else {
			lv = this.field_2131 ? class_347.class_348.field_2137 : class_347.class_348.field_2132;
		}

		this.blit(this.field_23658, this.field_23659, lv.method_1897(), lv.method_1898(), this.width, this.height);
	}

	@Environment(EnvType.CLIENT)
	static enum class_348 {
		field_2137(0, 146),
		field_2138(0, 166),
		field_2139(0, 186),
		field_2132(20, 146),
		field_2133(20, 166),
		field_2140(20, 186);

		private final int field_2135;
		private final int field_2134;

		private class_348(int j, int k) {
			this.field_2135 = j;
			this.field_2134 = k;
		}

		public int method_1897() {
			return this.field_2135;
		}

		public int method_1898() {
			return this.field_2134;
		}
	}
}
