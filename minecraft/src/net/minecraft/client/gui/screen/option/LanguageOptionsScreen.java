package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class LanguageOptionsScreen extends GameOptionsScreen {
	private static final Text LANGUAGE_WARNING_TEXT = Text.literal("(")
		.append(Text.translatable("options.languageWarning"))
		.append(")")
		.formatted(Formatting.GRAY);
	private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;
	final LanguageManager languageManager;

	public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
		super(parent, options, Text.translatable("options.language"));
		this.languageManager = languageManager;
	}

	@Override
	protected void init() {
		this.languageSelectionList = new LanguageOptionsScreen.LanguageSelectionListWidget(this.client);
		this.addSelectableChild(this.languageSelectionList);
		this.addDrawableChild(this.gameOptions.getForceUnicodeFont().createWidget(this.gameOptions, this.width / 2 - 155, this.height - 38, 150));
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, button -> this.onDone()).dimensions(this.width / 2 - 155 + 160, this.height - 38, 150, 20).build()
		);
		super.init();
	}

	void onDone() {
		LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry languageEntry = this.languageSelectionList.getSelectedOrNull();
		if (languageEntry != null && !languageEntry.languageCode.equals(this.languageManager.getLanguage())) {
			this.languageManager.setLanguage(languageEntry.languageCode);
			this.gameOptions.language = languageEntry.languageCode;
			this.client.reloadResources();
			this.gameOptions.write();
		}

		this.client.setScreen(this.parent);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (KeyCodes.isToggle(keyCode)) {
			LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry languageEntry = this.languageSelectionList.getSelectedOrNull();
			if (languageEntry != null) {
				languageEntry.onPressed();
				this.onDone();
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.languageSelectionList.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 16, 16777215);
		context.drawCenteredTextWithShadow(this.textRenderer, LANGUAGE_WARNING_TEXT, this.width / 2, this.height - 56, -8355712);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
	}

	@Environment(EnvType.CLIENT)
	class LanguageSelectionListWidget extends AlwaysSelectedEntryListWidget<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> {
		public LanguageSelectionListWidget(MinecraftClient client) {
			super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);
			String string = LanguageOptionsScreen.this.languageManager.getLanguage();
			LanguageOptionsScreen.this.languageManager
				.getAllLanguages()
				.forEach(
					(languageCode, languageDefinition) -> {
						LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry languageEntry = new LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry(
							languageCode, languageDefinition
						);
						this.addEntry(languageEntry);
						if (string.equals(languageCode)) {
							this.setSelected(languageEntry);
						}
					}
				);
			if (this.getSelectedOrNull() != null) {
				this.centerScrollOn(this.getSelectedOrNull());
			}
		}

		@Override
		protected int getScrollbarPositionX() {
			return super.getScrollbarPositionX() + 20;
		}

		@Override
		public int getRowWidth() {
			return super.getRowWidth() + 50;
		}

		@Environment(EnvType.CLIENT)
		public class LanguageEntry extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> {
			final String languageCode;
			private final Text languageDefinition;
			private long clickTime;

			public LanguageEntry(String languageCode, LanguageDefinition languageDefinition) {
				this.languageCode = languageCode;
				this.languageDefinition = languageDefinition.getDisplayText();
			}

			@Override
			public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
				context.drawCenteredTextWithShadow(
					LanguageOptionsScreen.this.textRenderer, this.languageDefinition, LanguageSelectionListWidget.this.width / 2, y + 1, 16777215
				);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				this.onPressed();
				if (Util.getMeasuringTimeMs() - this.clickTime < 250L) {
					LanguageOptionsScreen.this.onDone();
				}

				this.clickTime = Util.getMeasuringTimeMs();
				return true;
			}

			void onPressed() {
				LanguageSelectionListWidget.this.setSelected(this);
			}

			@Override
			public Text getNarration() {
				return Text.translatable("narrator.select", this.languageDefinition);
			}
		}
	}
}
