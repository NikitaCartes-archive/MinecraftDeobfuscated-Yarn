package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5285;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen {
	private static final class_5285.class_5286[] field_24561 = class_5285.class_5286.values();
	private final Screen field_24562;
	private final Consumer<Pair<class_5285.class_5286, Set<Biome>>> field_24563;
	private CustomizeBuffetLevelScreen.BuffetBiomesListWidget biomeSelectionList;
	private int biomeListLength;
	private ButtonWidget confirmButton;

	public CustomizeBuffetLevelScreen(Screen screen, Consumer<Pair<class_5285.class_5286, Set<Biome>>> consumer, Pair<class_5285.class_5286, Set<Biome>> pair) {
		super(new TranslatableText("createWorld.customize.buffet.title"));
		this.field_24562 = screen;
		this.field_24563 = consumer;

		for (int i = 0; i < field_24561.length; i++) {
			if (field_24561[i].equals(pair.getFirst())) {
				this.biomeListLength = i;
				break;
			}
		}

		for (Biome biome : pair.getSecond()) {
			this.biomeSelectionList
				.setSelected(
					(CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList
						.children()
						.stream()
						.filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.field_24564, biome))
						.findFirst()
						.orElse(null)
				);
		}
	}

	@Override
	protected void init() {
		this.client.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20, field_24561[this.biomeListLength].method_28043(), buttonWidget -> {
			this.biomeListLength++;
			if (this.biomeListLength >= field_24561.length) {
				this.biomeListLength = 0;
			}

			buttonWidget.setMessage(field_24561[this.biomeListLength].method_28043());
		}));
		this.biomeSelectionList = new CustomizeBuffetLevelScreen.BuffetBiomesListWidget();
		this.children.add(this.biomeSelectionList);
		this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, buttonWidget -> {
			this.field_24563.accept(Pair.of(field_24561[this.biomeListLength], ImmutableSet.of(this.biomeSelectionList.getSelected().field_24564)));
			this.client.openScreen(this.field_24562);
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.field_24562)));
		this.refreshConfirmButton();
	}

	public void refreshConfirmButton() {
		this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(0);
		this.biomeSelectionList.render(matrices, mouseX, mouseY, delta);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
		this.drawCenteredString(matrices, this.textRenderer, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Environment(EnvType.CLIENT)
	class BuffetBiomesListWidget extends AlwaysSelectedEntryListWidget<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
		private BuffetBiomesListWidget() {
			super(
				CustomizeBuffetLevelScreen.this.client,
				CustomizeBuffetLevelScreen.this.width,
				CustomizeBuffetLevelScreen.this.height,
				80,
				CustomizeBuffetLevelScreen.this.height - 37,
				16
			);
			Registry.BIOME
				.stream()
				.sorted(Comparator.comparing(biome -> biome.getName().getString()))
				.forEach(biome -> this.addEntry(new CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem(biome)));
		}

		@Override
		protected boolean isFocused() {
			return CustomizeBuffetLevelScreen.this.getFocused() == this;
		}

		public void setSelected(@Nullable CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem buffetBiomeItem) {
			super.setSelected(buffetBiomeItem);
			if (buffetBiomeItem != null) {
				NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", buffetBiomeItem.field_24564.getName().getString()).getString());
			}
		}

		@Override
		protected void moveSelection(int amount) {
			super.moveSelection(amount);
			CustomizeBuffetLevelScreen.this.refreshConfirmButton();
		}

		@Environment(EnvType.CLIENT)
		class BuffetBiomeItem extends AlwaysSelectedEntryListWidget.Entry<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
			private final Biome field_24564;

			public BuffetBiomeItem(Biome biome) {
				this.field_24564 = biome;
			}

			@Override
			public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
				BuffetBiomesListWidget.this.drawStringWithShadow(
					matrices, CustomizeBuffetLevelScreen.this.textRenderer, this.field_24564.getName().getString(), width + 5, y + 2, 16777215
				);
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
