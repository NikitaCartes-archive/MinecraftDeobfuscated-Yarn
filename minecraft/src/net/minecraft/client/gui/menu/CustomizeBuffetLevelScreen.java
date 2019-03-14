package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;
import org.apache.commons.lang3.ArrayUtils;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private static final List<Identifier> field_2436 = (List<Identifier>)Registry.CHUNK_GENERATOR_TYPE
		.getIds()
		.stream()
		.filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get(identifier).isBuffetScreenOption())
		.collect(Collectors.toList());
	private final NewLevelScreen field_2437;
	private final List<Identifier> field_2440 = Lists.<Identifier>newArrayList();
	private final Identifier[] field_2435 = new Identifier[Registry.BIOME.getIds().size()];
	private String field_2442;
	private CustomizeBuffetLevelScreen.class_4190 field_2441;
	private int field_2439;
	private ButtonWidget field_2438;

	public CustomizeBuffetLevelScreen(NewLevelScreen newLevelScreen, CompoundTag compoundTag) {
		this.field_2437 = newLevelScreen;
		int i = 0;

		for (Identifier identifier : Registry.BIOME.getIds()) {
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
			listTag.add(new StringTag(identifier.toString()));
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

	@Override
	protected void onInitialized() {
		this.client.keyboard.enableRepeatEvents(true);
		this.field_2442 = I18n.translate("createWorld.customize.buffet.title");
		this.addButton(
			new ButtonWidget(
				(this.screenWidth - 200) / 2,
				40,
				200,
				20,
				I18n.translate("createWorld.customize.buffet.generatortype")
					+ " "
					+ I18n.translate(SystemUtil.createTranslationKey("generator", (Identifier)field_2436.get(this.field_2439)))
			) {
				@Override
				public void onPressed() {
					CustomizeBuffetLevelScreen.this.field_2439++;
					if (CustomizeBuffetLevelScreen.this.field_2439 >= CustomizeBuffetLevelScreen.field_2436.size()) {
						CustomizeBuffetLevelScreen.this.field_2439 = 0;
					}

					this.setText(
						I18n.translate("createWorld.customize.buffet.generatortype")
							+ " "
							+ I18n.translate(
								SystemUtil.createTranslationKey("generator", (Identifier)CustomizeBuffetLevelScreen.field_2436.get(CustomizeBuffetLevelScreen.this.field_2439))
							)
					);
				}
			}
		);
		this.field_2441 = new CustomizeBuffetLevelScreen.class_4190();
		this.listeners.add(this.field_2441);
		this.field_2438 = this.addButton(new ButtonWidget(this.screenWidth / 2 - 155, this.screenHeight - 28, 150, 20, I18n.translate("gui.done")) {
			@Override
			public void onPressed() {
				CustomizeBuffetLevelScreen.this.field_2437.field_3200 = CustomizeBuffetLevelScreen.this.method_2153();
				CustomizeBuffetLevelScreen.this.client.openScreen(CustomizeBuffetLevelScreen.this.field_2437);
			}
		});
		this.addButton(new ButtonWidget(this.screenWidth / 2 + 5, this.screenHeight - 28, 150, 20, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed() {
				CustomizeBuffetLevelScreen.this.client.openScreen(CustomizeBuffetLevelScreen.this.field_2437);
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
		this.drawStringCentered(this.fontRenderer, this.field_2442, this.screenWidth / 2, 8, 16777215);
		this.drawStringCentered(this.fontRenderer, I18n.translate("createWorld.customize.buffet.generator"), this.screenWidth / 2, 30, 10526880);
		this.drawStringCentered(this.fontRenderer, I18n.translate("createWorld.customize.buffet.biome"), this.screenWidth / 2, 68, 10526880);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_4190 extends EntryListWidget<CustomizeBuffetLevelScreen.class_4191> {
		private class_4190() {
			super(
				CustomizeBuffetLevelScreen.this.client,
				CustomizeBuffetLevelScreen.this.screenWidth,
				CustomizeBuffetLevelScreen.this.screenHeight,
				80,
				CustomizeBuffetLevelScreen.this.screenHeight - 37,
				16
			);

			for (int i = 0; i < CustomizeBuffetLevelScreen.this.field_2435.length; i++) {
				this.addEntry(CustomizeBuffetLevelScreen.this.new class_4191());
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return CustomizeBuffetLevelScreen.this.field_2440.contains(CustomizeBuffetLevelScreen.this.field_2435[i]);
		}

		@Override
		public boolean hasFocus() {
			return true;
		}

		@Override
		protected boolean method_19352() {
			return CustomizeBuffetLevelScreen.this.getFocused() == this;
		}

		@Override
		public void setHasFocus(boolean bl) {
			if (bl && CustomizeBuffetLevelScreen.this.field_2440.isEmpty() && this.getEntryCount() > 0) {
				CustomizeBuffetLevelScreen.this.field_2440.add(CustomizeBuffetLevelScreen.this.field_2435[0]);
			}
		}

		@Override
		protected void method_19351(int i) {
			if (CustomizeBuffetLevelScreen.this.field_2440.size() == 1) {
				CustomizeBuffetLevelScreen.this.field_2440
					.set(
						0,
						CustomizeBuffetLevelScreen.this.field_2435[MathHelper.clamp(
							ArrayUtils.indexOf(CustomizeBuffetLevelScreen.this.field_2435, CustomizeBuffetLevelScreen.this.field_2440.get(0)) + i, 0, this.getEntryCount() - 1
						)]
					);
				this.method_19349(
					(EntryListWidget.Entry)this.getInputListeners()
						.get(ArrayUtils.indexOf(CustomizeBuffetLevelScreen.this.field_2435, CustomizeBuffetLevelScreen.this.field_2440.get(0)))
				);
				CustomizeBuffetLevelScreen.this.method_2151();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4191 extends EntryListWidget.Entry<CustomizeBuffetLevelScreen.class_4191> {
		private class_4191() {
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			CustomizeBuffetLevelScreen.this.drawString(
				CustomizeBuffetLevelScreen.this.fontRenderer,
				Registry.BIOME.get(CustomizeBuffetLevelScreen.this.field_2435[this.field_2143]).getTextComponent().getString(),
				this.getX() + 5,
				this.getY() + 2,
				16777215
			);
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0) {
				CustomizeBuffetLevelScreen.this.field_2440.clear();
				CustomizeBuffetLevelScreen.this.field_2440.add(CustomizeBuffetLevelScreen.this.field_2435[this.field_2143]);
				CustomizeBuffetLevelScreen.this.method_2151();
				return true;
			} else {
				return false;
			}
		}
	}
}
