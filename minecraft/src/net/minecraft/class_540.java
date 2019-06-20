package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_540 implements class_535, class_537 {
	private final List<class_537> field_3272 = Lists.<class_537>newArrayList();

	public class_540() {
		class_310 lv = class_310.method_1551();

		for (class_268 lv2 : lv.field_1687.method_8428().method_1159()) {
			this.field_3272.add(new class_540.class_541(lv2));
		}
	}

	@Override
	public List<class_537> method_2780() {
		return this.field_3272;
	}

	@Override
	public class_2561 method_2781() {
		return new class_2588("spectatorMenu.team_teleport.prompt");
	}

	@Override
	public void method_2783(class_531 arg) {
		arg.method_2778(this);
	}

	@Override
	public class_2561 method_16892() {
		return new class_2588("spectatorMenu.team_teleport");
	}

	@Override
	public void method_2784(float f, int i) {
		class_310.method_1551().method_1531().method_4618(class_365.field_2199);
		class_332.blit(0, 0, 16.0F, 0.0F, 16, 16, 256, 256);
	}

	@Override
	public boolean method_16893() {
		for (class_537 lv : this.field_3272) {
			if (lv.method_16893()) {
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	class class_541 implements class_537 {
		private final class_268 field_3275;
		private final class_2960 field_3276;
		private final List<class_640> field_3274;

		public class_541(class_268 arg2) {
			this.field_3275 = arg2;
			this.field_3274 = Lists.<class_640>newArrayList();

			for (String string : arg2.method_1204()) {
				class_640 lv = class_310.method_1551().method_1562().method_2874(string);
				if (lv != null) {
					this.field_3274.add(lv);
				}
			}

			if (this.field_3274.isEmpty()) {
				this.field_3276 = class_1068.method_4649();
			} else {
				String string2 = ((class_640)this.field_3274.get(new Random().nextInt(this.field_3274.size()))).method_2966().getName();
				this.field_3276 = class_742.method_3124(string2);
				class_742.method_3120(this.field_3276, string2);
			}
		}

		@Override
		public void method_2783(class_531 arg) {
			arg.method_2778(new class_538(this.field_3274));
		}

		@Override
		public class_2561 method_16892() {
			return this.field_3275.method_1140();
		}

		@Override
		public void method_2784(float f, int i) {
			Integer integer = this.field_3275.method_1202().method_532();
			if (integer != null) {
				float g = (float)(integer >> 16 & 0xFF) / 255.0F;
				float h = (float)(integer >> 8 & 0xFF) / 255.0F;
				float j = (float)(integer & 0xFF) / 255.0F;
				class_332.fill(1, 1, 15, 15, class_3532.method_15353(g * f, h * f, j * f) | i << 24);
			}

			class_310.method_1551().method_1531().method_4618(this.field_3276);
			GlStateManager.color4f(f, f, f, (float)i / 255.0F);
			class_332.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
			class_332.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
		}

		@Override
		public boolean method_16893() {
			return !this.field_3274.isEmpty();
		}
	}
}
