package net.minecraft;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
	private final class_2487 field_19098;
	private class_415.class_4190 field_2441;
	private int field_2439;
	private class_4185 field_2438;

	public class_415(class_525 arg, class_2487 arg2) {
		super(new class_2588("createWorld.customize.buffet.title"));
		this.field_2437 = arg;
		this.field_19098 = arg2;
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.addButton(
			new class_4185(
				(this.width - 200) / 2,
				40,
				200,
				20,
				class_1074.method_4662("createWorld.customize.buffet.generatortype")
					+ " "
					+ class_1074.method_4662(class_156.method_646("generator", (class_2960)field_2436.get(this.field_2439))),
				arg -> {
					this.field_2439++;
					if (this.field_2439 >= field_2436.size()) {
						this.field_2439 = 0;
					}

					arg.setMessage(
						class_1074.method_4662("createWorld.customize.buffet.generatortype")
							+ " "
							+ class_1074.method_4662(class_156.method_646("generator", (class_2960)field_2436.get(this.field_2439)))
					);
				}
			)
		);
		this.field_2441 = new class_415.class_4190();
		this.children.add(this.field_2441);
		this.field_2438 = this.addButton(new class_4185(this.width / 2 - 155, this.height - 28, 150, 20, class_1074.method_4662("gui.done"), arg -> {
			this.field_2437.field_18979 = this.method_2153();
			this.minecraft.method_1507(this.field_2437);
		}));
		this.addButton(
			new class_4185(this.width / 2 + 5, this.height - 28, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.minecraft.method_1507(this.field_2437))
		);
		this.method_2161();
		this.method_2151();
	}

	private void method_2161() {
		if (this.field_19098.method_10573("chunk_generator", 10) && this.field_19098.method_10562("chunk_generator").method_10573("type", 8)) {
			class_2960 lv = new class_2960(this.field_19098.method_10562("chunk_generator").method_10558("type"));

			for (int i = 0; i < field_2436.size(); i++) {
				if (((class_2960)field_2436.get(i)).equals(lv)) {
					this.field_2439 = i;
					break;
				}
			}
		}

		if (this.field_19098.method_10573("biome_source", 10) && this.field_19098.method_10562("biome_source").method_10573("biomes", 9)) {
			class_2499 lv2 = this.field_19098.method_10562("biome_source").method_10554("biomes", 8);

			for (int ix = 0; ix < lv2.size(); ix++) {
				class_2960 lv3 = new class_2960(lv2.method_10608(ix));
				this.field_2441
					.method_20089(
						(class_415.class_4190.class_4191)this.field_2441.children().stream().filter(arg2 -> Objects.equals(arg2.field_19099, lv3)).findFirst().orElse(null)
					);
			}
		}

		this.field_19098.method_10551("chunk_generator");
		this.field_19098.method_10551("biome_source");
	}

	private class_2487 method_2153() {
		class_2487 lv = new class_2487();
		class_2487 lv2 = new class_2487();
		lv2.method_10582("type", class_2378.field_11151.method_10221(class_1969.field_9401).toString());
		class_2487 lv3 = new class_2487();
		class_2499 lv4 = new class_2499();
		lv4.add(new class_2519(this.field_2441.getSelected().field_19099.toString()));
		lv3.method_10566("biomes", lv4);
		lv2.method_10566("options", lv3);
		class_2487 lv5 = new class_2487();
		class_2487 lv6 = new class_2487();
		lv5.method_10582("type", ((class_2960)field_2436.get(this.field_2439)).toString());
		lv6.method_10582("default_block", "minecraft:stone");
		lv6.method_10582("default_fluid", "minecraft:water");
		lv5.method_10566("options", lv6);
		lv.method_10566("biome_source", lv2);
		lv.method_10566("chunk_generator", lv5);
		return lv;
	}

	public void method_2151() {
		this.field_2438.active = this.field_2441.getSelected() != null;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.field_2441.render(i, j, f);
		this.drawCenteredString(this.font, this.title.method_10863(), this.width / 2, 8, 16777215);
		this.drawCenteredString(this.font, class_1074.method_4662("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawCenteredString(this.font, class_1074.method_4662("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_4190 extends class_4280<class_415.class_4190.class_4191> {
		private class_4190() {
			super(class_415.this.minecraft, class_415.this.width, class_415.this.height, 80, class_415.this.height - 37, 16);
			class_2378.field_11153
				.method_10235()
				.stream()
				.sorted(Comparator.comparing(argx -> class_2378.field_11153.method_10223(argx).method_8693().getString()))
				.forEach(argx -> this.addEntry(new class_415.class_4190.class_4191(argx)));
		}

		@Override
		protected boolean isFocused() {
			return class_415.this.getFocused() == this;
		}

		public void method_20089(@Nullable class_415.class_4190.class_4191 arg) {
			super.setSelected(arg);
			if (arg != null) {
				class_333.field_2054
					.method_19788(new class_2588("narrator.select", class_2378.field_11153.method_10223(arg.field_19099).method_8693().getString()).getString());
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			class_415.this.method_2151();
		}

		@Environment(EnvType.CLIENT)
		class class_4191 extends class_4280.class_4281<class_415.class_4190.class_4191> {
			private final class_2960 field_19099;

			public class_4191(class_2960 arg2) {
				this.field_19099 = arg2;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				class_4190.this.drawString(class_415.this.font, class_2378.field_11153.method_10223(this.field_19099).method_8693().getString(), k + 5, j + 2, 16777215);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					class_4190.this.method_20089(this);
					class_415.this.method_2151();
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
