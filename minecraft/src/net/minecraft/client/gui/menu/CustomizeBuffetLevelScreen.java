package net.minecraft.client.gui.menu;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private static final List<Identifier> field_2436 = (List<Identifier>)Registry.CHUNK_GENERATOR_TYPE
		.getIds()
		.stream()
		.filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get(identifier).isBuffetScreenOption())
		.collect(Collectors.toList());
	private final NewLevelScreen field_2437;
	private final CompoundTag field_19098;
	private CustomizeBuffetLevelScreen.BuffetBiomesListWidget field_2441;
	private int field_2439;
	private ButtonWidget field_2438;

	public CustomizeBuffetLevelScreen(NewLevelScreen newLevelScreen, CompoundTag compoundTag) {
		super(new TranslatableTextComponent("createWorld.customize.buffet.title"));
		this.field_2437 = newLevelScreen;
		this.field_19098 = compoundTag;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.addButton(
			new ButtonWidget(
				(this.width - 200) / 2,
				40,
				200,
				20,
				I18n.translate("createWorld.customize.buffet.generatortype")
					+ " "
					+ I18n.translate(SystemUtil.createTranslationKey("generator", (Identifier)field_2436.get(this.field_2439))),
				buttonWidget -> {
					this.field_2439++;
					if (this.field_2439 >= field_2436.size()) {
						this.field_2439 = 0;
					}

					buttonWidget.setMessage(
						I18n.translate("createWorld.customize.buffet.generatortype")
							+ " "
							+ I18n.translate(SystemUtil.createTranslationKey("generator", (Identifier)field_2436.get(this.field_2439)))
					);
				}
			)
		);
		this.field_2441 = new CustomizeBuffetLevelScreen.BuffetBiomesListWidget();
		this.children.add(this.field_2441);
		this.field_2438 = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.field_2437.field_18979 = this.method_2153();
			this.minecraft.openScreen(this.field_2437);
		}));
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.field_2437))
		);
		this.method_2161();
		this.method_2151();
	}

	private void method_2161() {
		if (this.field_19098.containsKey("chunk_generator", 10) && this.field_19098.getCompound("chunk_generator").containsKey("type", 8)) {
			Identifier identifier = new Identifier(this.field_19098.getCompound("chunk_generator").getString("type"));

			for (int i = 0; i < field_2436.size(); i++) {
				if (((Identifier)field_2436.get(i)).equals(identifier)) {
					this.field_2439 = i;
					break;
				}
			}
		}

		if (this.field_19098.containsKey("biome_source", 10) && this.field_19098.getCompound("biome_source").containsKey("biomes", 9)) {
			ListTag listTag = this.field_19098.getCompound("biome_source").getList("biomes", 8);

			for (int ix = 0; ix < listTag.size(); ix++) {
				Identifier identifier2 = new Identifier(listTag.getString(ix));
				this.field_2441
					.method_20089(
						(CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem)this.field_2441
							.children()
							.stream()
							.filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.field_19099, identifier2))
							.findFirst()
							.orElse(null)
					);
			}
		}

		this.field_19098.remove("chunk_generator");
		this.field_19098.remove("biome_source");
	}

	private CompoundTag method_2153() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
		CompoundTag compoundTag3 = new CompoundTag();
		ListTag listTag = new ListTag();
		listTag.add(new StringTag(this.field_2441.getSelectedItem().field_19099.toString()));
		compoundTag3.put("biomes", listTag);
		compoundTag2.put("options", compoundTag3);
		CompoundTag compoundTag4 = new CompoundTag();
		CompoundTag compoundTag5 = new CompoundTag();
		compoundTag4.putString("type", ((Identifier)field_2436.get(this.field_2439)).toString());
		compoundTag5.putString("default_block", "minecraft:stone");
		compoundTag5.putString("default_fluid", "minecraft:water");
		compoundTag4.put("options", compoundTag5);
		compoundTag.put("biome_source", compoundTag2);
		compoundTag.put("chunk_generator", compoundTag4);
		return compoundTag;
	}

	public void method_2151() {
		this.field_2438.active = this.field_2441.getSelectedItem() != null;
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderDirtBackground(0);
		this.field_2441.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
		this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class BuffetBiomesListWidget extends AlwaysSelectedItemListWidget<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
		private BuffetBiomesListWidget() {
			super(
				CustomizeBuffetLevelScreen.this.minecraft,
				CustomizeBuffetLevelScreen.this.width,
				CustomizeBuffetLevelScreen.this.height,
				80,
				CustomizeBuffetLevelScreen.this.height - 37,
				16
			);
			Registry.BIOME
				.getIds()
				.stream()
				.sorted(Comparator.comparing(identifier -> Registry.BIOME.get(identifier).getTextComponent().getString()))
				.forEach(identifier -> this.addItem(new CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem(identifier)));
		}

		@Override
		protected boolean isFocused() {
			return CustomizeBuffetLevelScreen.this.getFocused() == this;
		}

		public void method_20089(@Nullable CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem buffetBiomeItem) {
			super.selectItem(buffetBiomeItem);
			if (buffetBiomeItem != null) {
				NarratorManager.INSTANCE
					.method_19788(new TranslatableTextComponent("narrator.select", Registry.BIOME.get(buffetBiomeItem.field_19099).getTextComponent().getString()).getString());
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			CustomizeBuffetLevelScreen.this.method_2151();
		}

		@Environment(EnvType.CLIENT)
		class BuffetBiomeItem extends AlwaysSelectedItemListWidget.class_4281<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
			private final Identifier field_19099;

			public BuffetBiomeItem(Identifier identifier) {
				this.field_19099 = identifier;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				BuffetBiomesListWidget.this.drawString(
					CustomizeBuffetLevelScreen.this.font, Registry.BIOME.get(this.field_19099).getTextComponent().getString(), k + 5, j + 2, 16777215
				);
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					BuffetBiomesListWidget.this.method_20089(this);
					CustomizeBuffetLevelScreen.this.method_2151();
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
