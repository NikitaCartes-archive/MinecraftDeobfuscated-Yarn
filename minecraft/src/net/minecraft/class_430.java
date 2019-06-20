package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_430 extends class_437 {
	private static final List<class_430.class_431> field_2518 = Lists.<class_430.class_431>newArrayList();
	private final class_413 field_2519;
	private String field_2520;
	private String field_2524;
	private class_430.class_4196 field_2521;
	private class_4185 field_2525;
	private class_342 field_2523;

	public class_430(class_413 arg) {
		super(new class_2588("createWorld.customize.presets.title"));
		this.field_2519 = arg;
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_2520 = class_1074.method_4662("createWorld.customize.presets.share");
		this.field_2524 = class_1074.method_4662("createWorld.customize.presets.list");
		this.field_2523 = new class_342(this.font, 50, 40, this.width - 100, 20, this.field_2520);
		this.field_2523.method_1880(1230);
		this.field_2523.method_1852(this.field_2519.method_2138());
		this.children.add(this.field_2523);
		this.field_2521 = new class_430.class_4196();
		this.children.add(this.field_2521);
		this.field_2525 = this.addButton(
			new class_4185(this.width / 2 - 155, this.height - 28, 150, 20, class_1074.method_4662("createWorld.customize.presets.select"), arg -> {
				this.field_2519.method_2139(this.field_2523.method_1882());
				this.minecraft.method_1507(this.field_2519);
			})
		);
		this.addButton(
			new class_4185(this.width / 2 + 5, this.height - 28, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_2519))
		);
		this.method_20102(this.field_2521.getSelected() != null);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2521.mouseScrolled(d, e, f);
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2523.method_1882();
		this.init(arg, i, j);
		this.field_2523.method_1852(string);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.field_2521.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 8, 16777215);
		this.drawString(this.font, this.field_2520, 50, 30, 10526880);
		this.drawString(this.font, this.field_2524, 50, 70, 10526880);
		this.field_2523.render(i, j, f);
		super.render(i, j, f);
	}

	@Override
	public void tick() {
		this.field_2523.method_1865();
		super.tick();
	}

	public void method_20102(boolean bl) {
		this.field_2525.active = bl || this.field_2523.method_1882().length() > 1;
	}

	private static void method_2195(String string, class_1935 arg, class_1959 arg2, List<String> list, class_3229... args) {
		class_3232 lv = class_2798.field_12766.method_12117();

		for (int i = args.length - 1; i >= 0; i--) {
			lv.method_14327().add(args[i]);
		}

		lv.method_14325(arg2);
		lv.method_14330();

		for (String string2 : list) {
			lv.method_14333().put(string2, Maps.newHashMap());
		}

		field_2518.add(new class_430.class_431(arg.method_8389(), string, lv.toString()));
	}

	static {
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.classic_flat"),
			class_2246.field_10219,
			class_1972.field_9451,
			Arrays.asList("village"),
			new class_3229(1, class_2246.field_10219),
			new class_3229(2, class_2246.field_10566),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.tunnelers_dream"),
			class_2246.field_10340,
			class_1972.field_9472,
			Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
			new class_3229(1, class_2246.field_10219),
			new class_3229(5, class_2246.field_10566),
			new class_3229(230, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.water_world"),
			class_1802.field_8705,
			class_1972.field_9446,
			Arrays.asList("biome_1", "oceanmonument"),
			new class_3229(90, class_2246.field_10382),
			new class_3229(5, class_2246.field_10102),
			new class_3229(5, class_2246.field_10566),
			new class_3229(5, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.overworld"),
			class_2246.field_10479,
			class_1972.field_9451,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"),
			new class_3229(1, class_2246.field_10219),
			new class_3229(3, class_2246.field_10566),
			new class_3229(59, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.snowy_kingdom"),
			class_2246.field_10477,
			class_1972.field_9452,
			Arrays.asList("village", "biome_1"),
			new class_3229(1, class_2246.field_10477),
			new class_3229(1, class_2246.field_10219),
			new class_3229(3, class_2246.field_10566),
			new class_3229(59, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.bottomless_pit"),
			class_1802.field_8153,
			class_1972.field_9451,
			Arrays.asList("village", "biome_1"),
			new class_3229(1, class_2246.field_10219),
			new class_3229(3, class_2246.field_10566),
			new class_3229(2, class_2246.field_10445)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.desert"),
			class_2246.field_10102,
			class_1972.field_9424,
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
			new class_3229(8, class_2246.field_10102),
			new class_3229(52, class_2246.field_9979),
			new class_3229(3, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.redstone_ready"),
			class_1802.field_8725,
			class_1972.field_9424,
			Collections.emptyList(),
			new class_3229(52, class_2246.field_9979),
			new class_3229(3, class_2246.field_10340),
			new class_3229(1, class_2246.field_9987)
		);
		method_2195(
			class_1074.method_4662("createWorld.customize.preset.the_void"),
			class_2246.field_10499,
			class_1972.field_9473,
			Arrays.asList("decoration"),
			new class_3229(1, class_2246.field_10124)
		);
	}

	@Environment(EnvType.CLIENT)
	class class_4196 extends class_4280<class_430.class_4196.class_432> {
		public class_4196() {
			super(class_430.this.minecraft, class_430.this.width, class_430.this.height, 80, class_430.this.height - 37, 24);

			for (int i = 0; i < class_430.field_2518.size(); i++) {
				this.addEntry(new class_430.class_4196.class_432());
			}
		}

		public void method_20103(@Nullable class_430.class_4196.class_432 arg) {
			super.setSelected(arg);
			if (arg != null) {
				class_333.field_2054
					.method_19788(new class_2588("narrator.select", ((class_430.class_431)class_430.field_2518.get(this.children().indexOf(arg))).field_2528).getString());
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			class_430.this.method_20102(true);
		}

		@Override
		protected boolean isFocused() {
			return class_430.this.getFocused() == this;
		}

		@Override
		public boolean keyPressed(int i, int j, int k) {
			if (super.keyPressed(i, j, k)) {
				return true;
			} else {
				if ((i == 257 || i == 335) && this.getSelected() != null) {
					this.getSelected().method_19389();
				}

				return false;
			}
		}

		@Environment(EnvType.CLIENT)
		public class class_432 extends class_4280.class_4281<class_430.class_4196.class_432> {
			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_430.class_431 lv = (class_430.class_431)class_430.field_2518.get(i);
				this.method_2200(k, j, lv.field_2527);
				class_430.this.font.method_1729(lv.field_2528, (float)(k + 18 + 5), (float)(j + 6), 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					this.method_19389();
				}

				return false;
			}

			private void method_19389() {
				class_4196.this.method_20103(this);
				class_430.this.method_20102(true);
				class_430.this.field_2523.method_1852(((class_430.class_431)class_430.field_2518.get(class_4196.this.children().indexOf(this))).field_2526);
				class_430.this.field_2523.method_1870();
			}

			private void method_2200(int i, int j, class_1792 arg) {
				this.method_2198(i + 1, j + 1);
				GlStateManager.enableRescaleNormal();
				class_308.method_1453();
				class_430.this.itemRenderer.method_4010(new class_1799(arg), i + 2, j + 2);
				class_308.method_1450();
				GlStateManager.disableRescaleNormal();
			}

			private void method_2198(int i, int j) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				class_4196.this.minecraft.method_1531().method_4618(class_332.STATS_ICON_LOCATION);
				class_332.blit(i, j, class_430.this.blitOffset, 0.0F, 0.0F, 18, 18, 128, 128);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_431 {
		public final class_1792 field_2527;
		public final String field_2528;
		public final String field_2526;

		public class_431(class_1792 arg, String string, String string2) {
			this.field_2527 = arg;
			this.field_2528 = string;
			this.field_2526 = string2;
		}
	}
}
