package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private static final List<Identifier> CHUNK_GENERATOR_TYPES = (List<Identifier>)Registry.CHUNK_GENERATOR_TYPE
		.getIds()
		.stream()
		.filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get(identifier).isBuffetScreenOption())
		.collect(Collectors.toList());
	private final CreateWorldScreen parent;
	private final CompoundTag generatorOptionsTag;
	private CustomizeBuffetLevelScreen.BuffetBiomesListWidget biomeSelectionList;
	private int biomeListLength;
	private ButtonWidget confirmButton;

	public CustomizeBuffetLevelScreen(CreateWorldScreen parent, CompoundTag generatorOptionsTag) {
		super(new TranslatableText("createWorld.customize.buffet.title"));
		this.parent = parent;
		this.generatorOptionsTag = generatorOptionsTag;
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
					+ I18n.translate(Util.createTranslationKey("generator", (Identifier)CHUNK_GENERATOR_TYPES.get(this.biomeListLength))),
				buttonWidget -> {
					this.biomeListLength++;
					if (this.biomeListLength >= CHUNK_GENERATOR_TYPES.size()) {
						this.biomeListLength = 0;
					}

					buttonWidget.setMessage(
						I18n.translate("createWorld.customize.buffet.generatortype")
							+ " "
							+ I18n.translate(Util.createTranslationKey("generator", (Identifier)CHUNK_GENERATOR_TYPES.get(this.biomeListLength)))
					);
				}
			)
		);
		this.biomeSelectionList = new CustomizeBuffetLevelScreen.BuffetBiomesListWidget();
		this.children.add(this.biomeSelectionList);
		this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			this.parent.generatorOptionsTag = this.getGeneratorTag();
			this.minecraft.openScreen(this.parent);
		}));
		this.addButton(
			new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent))
		);
		this.initListSelectLogic();
		this.refreshConfirmButton();
	}

	private void initListSelectLogic() {
		if (this.generatorOptionsTag.contains("chunk_generator", 10) && this.generatorOptionsTag.getCompound("chunk_generator").contains("type", 8)) {
			Identifier identifier = new Identifier(this.generatorOptionsTag.getCompound("chunk_generator").getString("type"));

			for (int i = 0; i < CHUNK_GENERATOR_TYPES.size(); i++) {
				if (((Identifier)CHUNK_GENERATOR_TYPES.get(i)).equals(identifier)) {
					this.biomeListLength = i;
					break;
				}
			}
		}

		if (this.generatorOptionsTag.contains("biome_source", 10) && this.generatorOptionsTag.getCompound("biome_source").contains("biomes", 9)) {
			ListTag listTag = this.generatorOptionsTag.getCompound("biome_source").getList("biomes", 8);

			for (int ix = 0; ix < listTag.size(); ix++) {
				Identifier identifier2 = new Identifier(listTag.getString(ix));
				this.biomeSelectionList
					.setSelected(
						(CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList
							.children()
							.stream()
							.filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.biome, identifier2))
							.findFirst()
							.orElse(null)
					);
			}
		}

		this.generatorOptionsTag.remove("chunk_generator");
		this.generatorOptionsTag.remove("biome_source");
	}

	private CompoundTag getGeneratorTag() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
		CompoundTag compoundTag3 = new CompoundTag();
		ListTag listTag = new ListTag();
		listTag.add(StringTag.of(this.biomeSelectionList.getSelected().biome.toString()));
		compoundTag3.put("biomes", listTag);
		compoundTag2.put("options", compoundTag3);
		CompoundTag compoundTag4 = new CompoundTag();
		CompoundTag compoundTag5 = new CompoundTag();
		compoundTag4.putString("type", ((Identifier)CHUNK_GENERATOR_TYPES.get(this.biomeListLength)).toString());
		compoundTag5.putString("default_block", "minecraft:stone");
		compoundTag5.putString("default_fluid", "minecraft:water");
		compoundTag4.put("options", compoundTag5);
		compoundTag.put("biome_source", compoundTag2);
		compoundTag.put("chunk_generator", compoundTag4);
		return compoundTag;
	}

	public void refreshConfirmButton() {
		this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.biomeSelectionList.render(mouseX, mouseY, delta);
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
		this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.render(mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class BuffetBiomesListWidget extends AlwaysSelectedEntryListWidget<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
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
				.sorted(Comparator.comparing(identifier -> Registry.BIOME.get(identifier).getName().getString()))
				.forEach(identifier -> this.addEntry(new CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem(identifier)));
		}

		@Override
		protected boolean isFocused() {
			return CustomizeBuffetLevelScreen.this.getFocused() == this;
		}

		public void setSelected(@Nullable CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem buffetBiomeItem) {
			super.setSelected(buffetBiomeItem);
			if (buffetBiomeItem != null) {
				NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", Registry.BIOME.get(buffetBiomeItem.biome).getName().getString()).getString());
			}
		}

		@Override
		protected void moveSelection(int i) {
			super.moveSelection(i);
			CustomizeBuffetLevelScreen.this.refreshConfirmButton();
		}

		@Environment(EnvType.CLIENT)
		class BuffetBiomeItem extends AlwaysSelectedEntryListWidget.Entry<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
			private final Identifier biome;

			public BuffetBiomeItem(Identifier biome) {
				this.biome = biome;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				BuffetBiomesListWidget.this.drawString(CustomizeBuffetLevelScreen.this.font, Registry.BIOME.get(this.biome).getName().getString(), k + 5, j + 2, 16777215);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					BuffetBiomesListWidget.this.setSelected(this);
					CustomizeBuffetLevelScreen.this.refreshConfirmButton();
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
