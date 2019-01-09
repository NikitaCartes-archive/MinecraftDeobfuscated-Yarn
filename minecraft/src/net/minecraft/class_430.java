package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_430 extends class_437 {
	private static final List<class_430.class_431> field_2518 = Lists.<class_430.class_431>newArrayList();
	private final class_413 field_2519;
	private String field_2522;
	private String field_2520;
	private String field_2524;
	private class_430.class_432 field_2521;
	private class_339 field_2525;
	private class_342 field_2523;

	public class_430(class_413 arg) {
		this.field_2519 = arg;
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_2522 = class_1074.method_4662("createWorld.customize.presets.title");
		this.field_2520 = class_1074.method_4662("createWorld.customize.presets.share");
		this.field_2524 = class_1074.method_4662("createWorld.customize.presets.list");
		this.field_2523 = new class_342(2, this.field_2554, 50, 40, this.field_2561 - 100, 20);
		this.field_2521 = new class_430.class_432();
		this.field_2557.add(this.field_2521);
		this.field_2523.method_1880(1230);
		this.field_2523.method_1852(this.field_2519.method_2138());
		this.field_2557.add(this.field_2523);
		this.field_2525 = this.method_2219(
			new class_339(0, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, class_1074.method_4662("createWorld.customize.presets.select")) {
				@Override
				public void method_1826(double d, double e) {
					class_430.this.field_2519.method_2139(class_430.this.field_2523.method_1882());
					class_430.this.field_2563.method_1507(class_430.this.field_2519);
				}
			}
		);
		this.method_2219(new class_339(1, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_430.this.field_2563.method_1507(class_430.this.field_2519);
			}
		});
		this.method_2191();
		this.method_1967(this.field_2521);
	}

	@Override
	public boolean method_16802(double d) {
		return this.field_2521.method_16802(d);
	}

	@Override
	public void method_2228(class_310 arg, int i, int j) {
		String string = this.field_2523.method_1882();
		this.method_2233(arg, i, j);
		this.field_2523.method_1852(string);
	}

	@Override
	public void method_2234() {
		this.field_2563.field_1774.method_1462(false);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2240();
		this.field_2521.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2522, this.field_2561 / 2, 8, 16777215);
		this.method_1780(this.field_2554, this.field_2520, 50, 30, 10526880);
		this.method_1780(this.field_2554, this.field_2524, 50, 70, 10526880);
		this.field_2523.method_1857(i, j, f);
		super.method_2214(i, j, f);
	}

	@Override
	public void method_2225() {
		this.field_2523.method_1865();
		super.method_2225();
	}

	public void method_2191() {
		this.field_2525.field_2078 = this.method_2197();
	}

	private boolean method_2197() {
		return this.field_2521.field_2531 > -1 && this.field_2521.field_2531 < field_2518.size() || this.field_2523.method_1882().length() > 1;
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
			Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
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

	@Environment(EnvType.CLIENT)
	class class_432 extends class_358 {
		public int field_2531 = -1;

		public class_432() {
			super(class_430.this.field_2563, class_430.this.field_2561, class_430.this.field_2559, 80, class_430.this.field_2559 - 37, 24);
		}

		private void method_2200(int i, int j, class_1792 arg) {
			this.method_2198(i + 1, j + 1);
			GlStateManager.enableRescaleNormal();
			class_308.method_1453();
			class_430.this.field_2560.method_4010(new class_1799(arg), i + 2, j + 2);
			class_308.method_1450();
			GlStateManager.disableRescaleNormal();
		}

		private void method_2198(int i, int j) {
			this.method_2199(i, j, 0, 0);
		}

		private void method_2199(int i, int j, int k, int l) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_2164.method_1531().method_4618(class_332.field_2052);
			float f = 0.0078125F;
			float g = 0.0078125F;
			int m = 18;
			int n = 18;
			class_289 lv = class_289.method_1348();
			class_287 lv2 = lv.method_1349();
			lv2.method_1328(7, class_290.field_1585);
			lv2.method_1315((double)(i + 0), (double)(j + 18), (double)this.field_2050)
				.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 18), (double)(j + 18), (double)this.field_2050)
				.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 18) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 18), (double)(j + 0), (double)this.field_2050)
				.method_1312((double)((float)(k + 18) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.method_1344();
			lv2.method_1315((double)(i + 0), (double)(j + 0), (double)this.field_2050)
				.method_1312((double)((float)(k + 0) * 0.0078125F), (double)((float)(l + 0) * 0.0078125F))
				.method_1344();
			lv.method_1350();
		}

		@Override
		protected int method_1947() {
			return class_430.field_2518.size();
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			this.field_2531 = i;
			class_430.this.method_2191();
			class_430.this.field_2523.method_1852(((class_430.class_431)class_430.field_2518.get(class_430.this.field_2521.field_2531)).field_2526);
			class_430.this.field_2523.method_1870();
			return true;
		}

		@Override
		protected boolean method_1955(int i) {
			return i == this.field_2531;
		}

		@Override
		protected void method_1936() {
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			class_430.class_431 lv = (class_430.class_431)class_430.field_2518.get(i);
			this.method_2200(j, k, lv.field_2527);
			class_430.this.field_2554.method_1729(lv.field_2528, (float)(j + 18 + 5), (float)(k + 6), 16777215);
		}
	}
}
