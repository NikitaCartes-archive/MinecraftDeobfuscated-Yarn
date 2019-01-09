package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_415 extends class_437 {
	private static final List<class_2960> field_2436 = (List<class_2960>)class_2378.field_11149
		.method_10235()
		.stream()
		.filter(arg -> class_2378.field_11149.method_10223(arg).method_12118())
		.collect(Collectors.toList());
	private final class_525 field_2437;
	private final List<class_2960> field_2440 = Lists.<class_2960>newArrayList();
	private final class_2960[] field_2435 = new class_2960[class_2378.field_11153.method_10235().size()];
	private String field_2442;
	private class_415.class_416 field_2441;
	private int field_2439;
	private class_339 field_2438;

	public class_415(class_525 arg, class_2487 arg2) {
		this.field_2437 = arg;
		int i = 0;

		for (class_2960 lv : class_2378.field_11153.method_10235()) {
			this.field_2435[i] = lv;
			i++;
		}

		Arrays.sort(this.field_2435, (argx, arg2x) -> {
			String string = class_2378.field_11153.method_10223(argx).method_8693().getString();
			String string2 = class_2378.field_11153.method_10223(arg2x).method_8693().getString();
			return string.compareTo(string2);
		});
		this.method_2161(arg2);
	}

	private void method_2161(class_2487 arg) {
		if (arg.method_10573("chunk_generator", 10) && arg.method_10562("chunk_generator").method_10573("type", 8)) {
			class_2960 lv = new class_2960(arg.method_10562("chunk_generator").method_10558("type"));

			for (int i = 0; i < field_2436.size(); i++) {
				if (((class_2960)field_2436.get(i)).equals(lv)) {
					this.field_2439 = i;
					break;
				}
			}
		}

		if (arg.method_10573("biome_source", 10) && arg.method_10562("biome_source").method_10573("biomes", 9)) {
			class_2499 lv2 = arg.method_10562("biome_source").method_10554("biomes", 8);

			for (int ix = 0; ix < lv2.size(); ix++) {
				this.field_2440.add(new class_2960(lv2.method_10608(ix)));
			}
		}
	}

	private class_2487 method_2153() {
		class_2487 lv = new class_2487();
		class_2487 lv2 = new class_2487();
		lv2.method_10582("type", class_2378.field_11151.method_10221(class_1969.field_9401).toString());
		class_2487 lv3 = new class_2487();
		class_2499 lv4 = new class_2499();

		for (class_2960 lv5 : this.field_2440) {
			lv4.method_10606(new class_2519(lv5.toString()));
		}

		lv3.method_10566("biomes", lv4);
		lv2.method_10566("options", lv3);
		class_2487 lv6 = new class_2487();
		class_2487 lv7 = new class_2487();
		lv6.method_10582("type", ((class_2960)field_2436.get(this.field_2439)).toString());
		lv7.method_10582("default_block", "minecraft:stone");
		lv7.method_10582("default_fluid", "minecraft:water");
		lv6.method_10566("options", lv7);
		lv.method_10566("biome_source", lv2);
		lv.method_10566("chunk_generator", lv6);
		return lv;
	}

	@Nullable
	@Override
	public class_364 getFocused() {
		return this.field_2441;
	}

	@Override
	protected void method_2224() {
		this.field_2563.field_1774.method_1462(true);
		this.field_2442 = class_1074.method_4662("createWorld.customize.buffet.title");
		this.field_2441 = new class_415.class_416();
		this.field_2557.add(this.field_2441);
		this.method_2219(
			new class_339(
				2,
				(this.field_2561 - 200) / 2,
				40,
				200,
				20,
				class_1074.method_4662("createWorld.customize.buffet.generatortype")
					+ " "
					+ class_1074.method_4662(class_156.method_646("generator", (class_2960)field_2436.get(this.field_2439)))
			) {
				@Override
				public void method_1826(double d, double e) {
					class_415.this.field_2439++;
					if (class_415.this.field_2439 >= class_415.field_2436.size()) {
						class_415.this.field_2439 = 0;
					}

					this.field_2074 = class_1074.method_4662("createWorld.customize.buffet.generatortype")
						+ " "
						+ class_1074.method_4662(class_156.method_646("generator", (class_2960)class_415.field_2436.get(class_415.this.field_2439)));
				}
			}
		);
		this.field_2438 = this.method_2219(new class_339(0, this.field_2561 / 2 - 155, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.done")) {
			@Override
			public void method_1826(double d, double e) {
				class_415.this.field_2437.field_3200 = class_415.this.method_2153();
				class_415.this.field_2563.method_1507(class_415.this.field_2437);
			}
		});
		this.method_2219(new class_339(1, this.field_2561 / 2 + 5, this.field_2559 - 28, 150, 20, class_1074.method_4662("gui.cancel")) {
			@Override
			public void method_1826(double d, double e) {
				class_415.this.field_2563.method_1507(class_415.this.field_2437);
			}
		});
		this.method_2151();
	}

	public void method_2151() {
		this.field_2438.field_2078 = !this.field_2440.isEmpty();
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.method_2220(0);
		this.field_2441.method_1930(i, j, f);
		this.method_1789(this.field_2554, this.field_2442, this.field_2561 / 2, 8, 16777215);
		this.method_1789(this.field_2554, class_1074.method_4662("createWorld.customize.buffet.generator"), this.field_2561 / 2, 30, 10526880);
		this.method_1789(this.field_2554, class_1074.method_4662("createWorld.customize.buffet.biome"), this.field_2561 / 2, 68, 10526880);
		super.method_2214(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_416 extends class_358 {
		private class_416() {
			super(class_415.this.field_2563, class_415.this.field_2561, class_415.this.field_2559, 80, class_415.this.field_2559 - 37, 16);
		}

		@Override
		protected int method_1947() {
			return class_415.this.field_2435.length;
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			class_415.this.field_2440.clear();
			class_415.this.field_2440.add(class_415.this.field_2435[i]);
			class_415.this.method_2151();
			return true;
		}

		@Override
		protected boolean method_1955(int i) {
			return class_415.this.field_2440.contains(class_415.this.field_2435[i]);
		}

		@Override
		protected void method_1936() {
		}

		@Override
		protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
			this.method_1780(
				class_415.this.field_2554, class_2378.field_11153.method_10223(class_415.this.field_2435[i]).method_8693().getString(), j + 5, k + 2, 16777215
			);
		}
	}
}
