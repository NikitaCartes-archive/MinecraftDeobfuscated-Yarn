package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private static final Text BUFFET_BIOME_TEXT = new TranslatableText("createWorld.customize.buffet.biome");
	private final Screen parent;
	private final Consumer<Biome> onDone;
	final Registry<Biome> biomeRegistry;
	private CustomizeBuffetLevelScreen.BuffetBiomesListWidget biomeSelectionList;
	Biome biome;
	private ButtonWidget confirmButton;

	public CustomizeBuffetLevelScreen(Screen parent, DynamicRegistryManager registryManager, Consumer<Biome> onDone, Biome biome) {
		super(new TranslatableText("createWorld.customize.buffet.title"));
		this.parent = parent;
		this.onDone = onDone;
		this.biome = biome;
		this.biomeRegistry = registryManager.get(Registry.BIOME_KEY);
	}

	@Override
	public void onClose() {
		this.client.openScreen(this.parent);
	}

	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		this.biomeSelectionList = new CustomizeBuffetLevelScreen.BuffetBiomesListWidget();
		this.children.add(this.biomeSelectionList);
		this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.onDone.accept(this.biome);
			this.client.openScreen(this.parent);
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.biomeSelectionList
			.setSelected(
				(CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList
					.children()
					.stream()
					.filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.biome, this.biome))
					.findFirst()
					.orElse(null)
			);
	}

	void refreshConfirmButton() {
		this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		drawCenteredText(matrices, this.textRenderer, BUFFET_BIOME_TEXT, this.width / 2, 28, 10526880);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class BuffetBiomesListWidget extends AlwaysSelectedEntryListWidget<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
		BuffetBiomesListWidget() {
			super(
				CustomizeBuffetLevelScreen.this.client,
				CustomizeBuffetLevelScreen.this.width,
				CustomizeBuffetLevelScreen.this.height,
				40,
				CustomizeBuffetLevelScreen.this.height - 37,
				16
			);
			CustomizeBuffetLevelScreen.this.biomeRegistry
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
				CustomizeBuffetLevelScreen.this.biome = buffetBiomeItem.biome;
				NarratorManager.INSTANCE
					.narrate(new TranslatableText("narrator.select", CustomizeBuffetLevelScreen.this.biomeRegistry.getId(buffetBiomeItem.biome)).getString());
			}

			CustomizeBuffetLevelScreen.this.refreshConfirmButton();
		}

		@Environment(EnvType.CLIENT)
		class BuffetBiomeItem extends AlwaysSelectedEntryListWidget.Entry<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
			final Biome biome;
			private final Text text;

			public BuffetBiomeItem(Biome biome) {
				this.biome = biome;
				Identifier identifier = CustomizeBuffetLevelScreen.this.biomeRegistry.getId(biome);
				String string = "biome." + identifier.getNamespace() + "." + identifier.getPath();
				if (Language.getInstance().hasTranslation(string)) {
					this.text = new TranslatableText(string);
				} else {
					this.text = new LiteralText(identifier.toString());
				}
			}

			@Override
			public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				DrawableHelper.drawTextWithShadow(matrices, CustomizeBuffetLevelScreen.this.textRenderer, this.text, x + 5, y + 2, 16777215);
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
