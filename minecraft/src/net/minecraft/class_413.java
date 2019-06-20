package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_413 extends class_437 {
	private final class_525 field_2422;
	private class_3232 field_2419 = class_3232.method_14309();
	private String field_2418;
	private String field_2425;
	private class_413.class_4192 field_2424;
	private class_4185 field_2421;

	public class_413(class_525 arg, class_2487 arg2) {
		super(new class_2588("createWorld.customize.flat.title"));
		this.field_2422 = arg;
		this.method_2144(arg2);
	}

	public String method_2138() {
		return this.field_2419.toString();
	}

	public class_2487 method_2140() {
		return (class_2487)this.field_2419.method_14313(class_2509.field_11560).getValue();
	}

	public void method_2139(String string) {
		this.field_2419 = class_3232.method_14319(string);
	}

	public void method_2144(class_2487 arg) {
		this.field_2419 = class_3232.method_14323(new Dynamic<>(class_2509.field_11560, arg));
	}

	@Override
	protected void init() {
		this.field_2418 = class_1074.method_4662("createWorld.customize.flat.tile");
		this.field_2425 = class_1074.method_4662("createWorld.customize.flat.height");
		this.field_2424 = new class_413.class_4192();
		this.children.add(this.field_2424);
		this.field_2421 = this.addButton(
			new class_4185(this.width / 2 - 155, this.height - 52, 150, 20, class_1074.method_4662("createWorld.customize.flat.removeLayer"), arg -> {
				if (this.method_2147()) {
					List<class_3229> list = this.field_2419.method_14327();
					int i = this.field_2424.children().indexOf(this.field_2424.getSelected());
					int j = list.size() - i - 1;
					list.remove(j);
					this.field_2424.method_20094(list.isEmpty() ? null : (class_413.class_4192.class_4193)this.field_2424.children().get(Math.min(i, list.size() - 1)));
					this.field_2419.method_14330();
					this.method_2145();
				}
			})
		);
		this.addButton(new class_4185(this.width / 2 + 5, this.height - 52, 150, 20, class_1074.method_4662("createWorld.customize.presets"), arg -> {
			this.minecraft.method_1507(new class_430(this));
			this.field_2419.method_14330();
			this.method_2145();
		}));
		this.addButton(new class_4185(this.width / 2 - 155, this.height - 28, 150, 20, class_1074.method_4662("gui.done"), arg -> {
			this.field_2422.field_18979 = this.method_2140();
			this.minecraft.method_1507(this.field_2422);
			this.field_2419.method_14330();
			this.method_2145();
		}));
		this.addButton(new class_4185(this.width / 2 + 5, this.height - 28, 150, 20, class_1074.method_4662("gui.cancel"), arg -> {
			this.minecraft.method_1507(this.field_2422);
			this.field_2419.method_14330();
			this.method_2145();
		}));
		this.field_2419.method_14330();
		this.method_2145();
	}

	public void method_2145() {
		this.field_2421.active = this.method_2147();
		this.field_2424.method_19372();
	}

	private boolean method_2147() {
		return this.field_2424.getSelected() != null;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2424.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 8, 16777215);
		int k = this.width / 2 - 92 - 16;
		this.drawString(this.font, this.field_2418, k, 32, 16777215);
		this.drawString(this.font, this.field_2425, k + 2 + 213 - this.font.method_1727(this.field_2425), 32, 16777215);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_4192 extends class_4280<class_413.class_4192.class_4193> {
		public class_4192() {
			super(class_413.this.minecraft, class_413.this.width, class_413.this.height, 43, class_413.this.height - 60, 24);

			for (int i = 0; i < class_413.this.field_2419.method_14327().size(); i++) {
				this.addEntry(new class_413.class_4192.class_4193());
			}
		}

		public void method_20094(@Nullable class_413.class_4192.class_4193 arg) {
			super.setSelected(arg);
			if (arg != null) {
				class_3229 lv = (class_3229)class_413.this.field_2419
					.method_14327()
					.get(class_413.this.field_2419.method_14327().size() - this.children().indexOf(arg) - 1);
				class_1792 lv2 = lv.method_14286().method_11614().method_8389();
				if (lv2 != class_1802.field_8162) {
					class_333.field_2054.method_19788(new class_2588("narrator.select", lv2.method_7864(new class_1799(lv2))).getString());
				}
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			class_413.this.method_2145();
		}

		@Override
		protected boolean isFocused() {
			return class_413.this.getFocused() == this;
		}

		@Override
		protected int getScrollbarPosition() {
			return this.width - 70;
		}

		public void method_19372() {
			int i = this.children().indexOf(this.getSelected());
			this.clearEntries();

			for (int j = 0; j < class_413.this.field_2419.method_14327().size(); j++) {
				this.addEntry(new class_413.class_4192.class_4193());
			}

			List<class_413.class_4192.class_4193> list = this.children();
			if (i >= 0 && i < list.size()) {
				this.method_20094((class_413.class_4192.class_4193)list.get(i));
			}
		}

		@Environment(EnvType.CLIENT)
		class class_4193 extends class_4280.class_4281<class_413.class_4192.class_4193> {
			private class_4193() {
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_3229 lv = (class_3229)class_413.this.field_2419.method_14327().get(class_413.this.field_2419.method_14327().size() - i - 1);
				class_2680 lv2 = lv.method_14286();
				class_2248 lv3 = lv2.method_11614();
				class_1792 lv4 = lv3.method_8389();
				if (lv4 == class_1802.field_8162) {
					if (lv3 == class_2246.field_10382) {
						lv4 = class_1802.field_8705;
					} else if (lv3 == class_2246.field_10164) {
						lv4 = class_1802.field_8187;
					}
				}

				class_1799 lv5 = new class_1799(lv4);
				String string = lv4.method_7864(lv5).method_10863();
				this.method_19375(k, j, lv5);
				class_413.this.font.method_1729(string, (float)(k + 18 + 5), (float)(j + 3), 16777215);
				String string2;
				if (i == 0) {
					string2 = class_1074.method_4662("createWorld.customize.flat.layer.top", lv.method_14289());
				} else if (i == class_413.this.field_2419.method_14327().size() - 1) {
					string2 = class_1074.method_4662("createWorld.customize.flat.layer.bottom", lv.method_14289());
				} else {
					string2 = class_1074.method_4662("createWorld.customize.flat.layer", lv.method_14289());
				}

				class_413.this.font.method_1729(string2, (float)(k + 2 + 213 - class_413.this.font.method_1727(string2)), (float)(j + 3), 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					class_4192.this.method_20094(this);
					class_413.this.method_2145();
					return true;
				} else {
					return false;
				}
			}

			private void method_19375(int i, int j, class_1799 arg) {
				this.method_19373(i + 1, j + 1);
				GlStateManager.enableRescaleNormal();
				if (!arg.method_7960()) {
					class_308.method_1453();
					class_413.this.itemRenderer.method_4010(arg, i + 2, j + 2);
					class_308.method_1450();
				}

				GlStateManager.disableRescaleNormal();
			}

			private void method_19373(int i, int j) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				class_4192.this.minecraft.method_1531().method_4618(class_332.STATS_ICON_LOCATION);
				class_332.blit(i, j, class_413.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}
}
