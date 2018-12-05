package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelGui extends Gui {
	private static final List<Identifier> field_2436 = (List<Identifier>)Registry.CHUNK_GENERATOR_TYPE
		.keys()
		.stream()
		.filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get(identifier).method_12118())
		.collect(Collectors.toList());
	private final NewLevelGui field_2437;
	private final List<Identifier> field_2440 = Lists.<Identifier>newArrayList();
	private final Identifier[] field_2435 = new Identifier[Registry.BIOME.keys().size()];
	private String field_2442;
	private CustomizeBuffetLevelGui.class_416 field_2441;
	private int field_2439;
	private ButtonWidget field_2438;

	public CustomizeBuffetLevelGui(NewLevelGui newLevelGui, CompoundTag compoundTag) {
		this.field_2437 = newLevelGui;
		int i = 0;

		for (Identifier identifier : Registry.BIOME.keys()) {
			this.field_2435[i] = identifier;
			i++;
		}

		Arrays.sort(this.field_2435, (identifierx, identifier2) -> {
			String string = Registry.BIOME.get(identifierx).getTextComponent().getString();
			String string2 = Registry.BIOME.get(identifier2).getTextComponent().getString();
			return string.compareTo(string2);
		});
		this.method_2161(compoundTag);
	}

	private void method_2161(CompoundTag compoundTag) {
		if (compoundTag.containsKey("chunk_generator", 10) && compoundTag.getCompound("chunk_generator").containsKey("type", 8)) {
			Identifier identifier = new Identifier(compoundTag.getCompound("chunk_generator").getString("type"));

			for (int i = 0; i < field_2436.size(); i++) {
				if (((Identifier)field_2436.get(i)).equals(identifier)) {
					this.field_2439 = i;
					break;
				}
			}
		}

		if (compoundTag.containsKey("biome_source", 10) && compoundTag.getCompound("biome_source").containsKey("biomes", 9)) {
			ListTag listTag = compoundTag.getCompound("biome_source").getList("biomes", 8);

			for (int ix = 0; ix < listTag.size(); ix++) {
				this.field_2440.add(new Identifier(listTag.getString(ix)));
			}
		}
	}

	private CompoundTag method_2153() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
		CompoundTag compoundTag3 = new CompoundTag();
		ListTag listTag = new ListTag();

		for (Identifier identifier : this.field_2440) {
			listTag.add((Tag)(new StringTag(identifier.toString())));
		}

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

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.field_2441;
	}

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2442 = I18n.translate("createWorld.customize.buffet.title");
		this.field_2441 = new CustomizeBuffetLevelGui.class_416();
		this.listeners.add(this.field_2441);
		this.addButton(
			new ButtonWidget(
				2,
				(this.width - 200) / 2,
				40,
				200,
				20,
				I18n.translate("createWorld.customize.buffet.generatortype")
					+ " "
					+ I18n.translate(SystemUtil.createTranslationKey("generator", (Identifier)field_2436.get(this.field_2439)))
			) {
				@Override
				public void onPressed(double d, double e) {
					CustomizeBuffetLevelGui.this.field_2439++;
					if (CustomizeBuffetLevelGui.this.field_2439 >= CustomizeBuffetLevelGui.field_2436.size()) {
						CustomizeBuffetLevelGui.this.field_2439 = 0;
					}

					this.text = I18n.translate("createWorld.customize.buffet.generatortype")
						+ " "
						+ I18n.translate(
							SystemUtil.createTranslationKey("generator", (Identifier)CustomizeBuffetLevelGui.field_2436.get(CustomizeBuffetLevelGui.this.field_2439))
						);
				}
			}
		);
		this.field_2438 = this.addButton(new ButtonWidget(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeBuffetLevelGui.this.field_2437.field_3200 = CustomizeBuffetLevelGui.this.method_2153();
				CustomizeBuffetLevelGui.this.client.openGui(CustomizeBuffetLevelGui.this.field_2437);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				CustomizeBuffetLevelGui.this.client.openGui(CustomizeBuffetLevelGui.this.field_2437);
			}
		});
		this.method_2151();
	}

	public void method_2151() {
		this.field_2438.enabled = !this.field_2440.isEmpty();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawTextureBackground(0);
		this.field_2441.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, this.field_2442, this.width / 2, 8, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawStringCentered(this.fontRenderer, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_416 extends AbstractListWidget {
		private class_416() {
			super(
				CustomizeBuffetLevelGui.this.client,
				CustomizeBuffetLevelGui.this.width,
				CustomizeBuffetLevelGui.this.height,
				80,
				CustomizeBuffetLevelGui.this.height - 37,
				16
			);
		}

		@Override
		protected int getEntryCount() {
			return CustomizeBuffetLevelGui.this.field_2435.length;
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			CustomizeBuffetLevelGui.this.field_2440.clear();
			CustomizeBuffetLevelGui.this.field_2440.add(CustomizeBuffetLevelGui.this.field_2435[i]);
			CustomizeBuffetLevelGui.this.method_2151();
			return true;
		}

		@Override
		protected boolean isSelected(int i) {
			return CustomizeBuffetLevelGui.this.field_2440.contains(CustomizeBuffetLevelGui.this.field_2435[i]);
		}

		@Override
		protected void method_1936() {
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			this.drawString(
				CustomizeBuffetLevelGui.this.fontRenderer,
				Registry.BIOME.get(CustomizeBuffetLevelGui.this.field_2435[i]).getTextComponent().getString(),
				j + 5,
				k + 2,
				16777215
			);
		}
	}
}
