package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private final Screen field_24562;
	private final Consumer<Biome> field_24563;
	private final MutableRegistry<Biome> field_25888;
	private CustomizeBuffetLevelScreen.BuffetBiomesListWidget biomeSelectionList;
	private Biome field_25040;
	private ButtonWidget confirmButton;

	public CustomizeBuffetLevelScreen(Screen screen, DynamicRegistryManager dynamicRegistryManager, Consumer<Biome> consumer, Biome biome) {
		super(new TranslatableText("createWorld.customize.buffet.title"));
		this.field_24562 = screen;
		this.field_24563 = consumer;
		this.field_25040 = biome;
		this.field_25888 = dynamicRegistryManager.get(Registry.BIOME_KEY);
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.field_24562);
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.biomeSelectionList = new CustomizeBuffetLevelScreen.BuffetBiomesListWidget();
		this.children.add(this.biomeSelectionList);
		this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.field_24563.accept(this.field_25040);
			this.client.openScreen(this.field_24562);
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.field_24562)));
		this.biomeSelectionList
			.setSelected(
				(CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList
					.children()
					.stream()
					.filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.field_24564, this.field_25040))
					.findFirst()
					.orElse(null)
			);
	}

	private void refreshConfirmButton() {
		this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 28, 10526880);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class BuffetBiomesListWidget extends AlwaysSelectedEntryListWidget<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
		private BuffetBiomesListWidget() {
			super(
				CustomizeBuffetLevelScreen.this.client,
				CustomizeBuffetLevelScreen.this.width,
				CustomizeBuffetLevelScreen.this.height,
				40,
				CustomizeBuffetLevelScreen.this.height - 37,
				16
			);
			CustomizeBuffetLevelScreen.this.field_25888
				.getEntries()
				.stream()
				.sorted(Comparator.comparing(entry -> ((RegistryKey)entry.getKey()).getValue().toString()))
				.forEach(entry -> this.addEntry(new CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem((Biome)entry.getValue())));
		}

		@Override
		protected boolean isFocused() {
			return CustomizeBuffetLevelScreen.this.getFocused() == this;
		}

		public void setSelected(@Nullable CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem buffetBiomeItem) {
			super.setSelected(buffetBiomeItem);
			if (buffetBiomeItem != null) {
				CustomizeBuffetLevelScreen.this.field_25040 = buffetBiomeItem.field_24564;
				NarratorManager.INSTANCE
					.narrate(new TranslatableText("narrator.select", CustomizeBuffetLevelScreen.this.field_25888.getId(buffetBiomeItem.field_24564)).getString());
			}

			CustomizeBuffetLevelScreen.this.refreshConfirmButton();
		}

		@Environment(EnvType.CLIENT)
		class BuffetBiomeItem extends AlwaysSelectedEntryListWidget.Entry<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
			private final Biome field_24564;

			public BuffetBiomeItem(Biome biome) {
				this.field_24564 = biome;
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				BuffetBiomesListWidget.this.drawStringWithShadow(
					matrices,
					CustomizeBuffetLevelScreen.this.textRenderer,
					CustomizeBuffetLevelScreen.this.field_25888.getId(this.field_24564).toString(),
					x + 5,
					y + 2,
					16777215
				);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					BuffetBiomesListWidget.this.setSelected(this);
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
