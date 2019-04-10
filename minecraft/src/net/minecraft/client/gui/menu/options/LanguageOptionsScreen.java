package net.minecraft.client.gui.menu.options;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.AlwaysSelectedItemListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class LanguageOptionsScreen extends Screen {
	protected final Screen parent;
	private LanguageOptionsScreen.LanguageSelectionListWidget languageSelectionList;
	private final GameOptions options;
	private final LanguageManager languageManager;
	private OptionButtonWidget forceUnicodeButton;
	private ButtonWidget doneButton;

	public LanguageOptionsScreen(Screen screen, GameOptions gameOptions, LanguageManager languageManager) {
		super(new TranslatableTextComponent("options.language"));
		this.parent = screen;
		this.options = gameOptions;
		this.languageManager = languageManager;
	}

	@Override
	protected void init() {
		this.languageSelectionList = new LanguageOptionsScreen.LanguageSelectionListWidget(this.minecraft);
		this.children.add(this.languageSelectionList);
		this.forceUnicodeButton = this.addButton(
			new OptionButtonWidget(
				this.width / 2 - 155,
				this.height - 38,
				150,
				20,
				GameOption.FORCE_UNICODE_FONT,
				GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options),
				buttonWidget -> {
					GameOption.FORCE_UNICODE_FONT.set(this.options);
					this.options.write();
					buttonWidget.setMessage(GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options));
					this.minecraft.onResolutionChanged();
				}
			)
		);
		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 38, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
			LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem languageItem = this.languageSelectionList.getSelectedItem();
			if (languageItem != null && !languageItem.languageDefinition.getCode().equals(this.languageManager.getLanguage().getCode())) {
				this.languageManager.setLanguage(languageItem.languageDefinition);
				this.options.language = languageItem.languageDefinition.getCode();
				this.minecraft.reloadResources();
				this.font.setRightToLeft(this.languageManager.isRightToLeft());
				this.doneButton.setMessage(I18n.translate("gui.done"));
				this.forceUnicodeButton.setMessage(GameOption.FORCE_UNICODE_FONT.getDisplayString(this.options));
				this.options.write();
			}

			this.minecraft.openScreen(this.parent);
		}));
		super.init();
	}

	@Override
	public void render(int i, int j, float f) {
		this.languageSelectionList.render(i, j, f);
		this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.font, "(" + I18n.translate("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.render(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class LanguageSelectionListWidget extends AlwaysSelectedItemListWidget<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem> {
		public LanguageSelectionListWidget(MinecraftClient minecraftClient) {
			super(minecraftClient, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height, 32, LanguageOptionsScreen.this.height - 65 + 4, 18);

			for (LanguageDefinition languageDefinition : LanguageOptionsScreen.this.languageManager.getAllLanguages()) {
				LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem languageItem = new LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem(
					languageDefinition
				);
				this.addItem(languageItem);
				if (LanguageOptionsScreen.this.languageManager.getLanguage().getCode().equals(languageDefinition.getCode())) {
					this.method_20100(languageItem);
				}
			}

			if (this.getSelectedItem() != null) {
				this.centerScrollOn(this.getSelectedItem());
			}
		}

		@Override
		protected int getScrollbarPosition() {
			return super.getScrollbarPosition() + 20;
		}

		@Override
		public int getItemWidth() {
			return super.getItemWidth() + 50;
		}

		public void method_20100(@Nullable LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem languageItem) {
			super.selectItem(languageItem);
			if (languageItem != null) {
				NarratorManager.INSTANCE.method_19788(new TranslatableTextComponent("narrator.select", languageItem.languageDefinition).getString());
			}
		}

		@Override
		protected void drawBackground() {
			LanguageOptionsScreen.this.renderBackground();
		}

		@Override
		protected boolean isFocused() {
			return LanguageOptionsScreen.this.getFocused() == this;
		}

		@Environment(EnvType.CLIENT)
		public class LanguageItem extends AlwaysSelectedItemListWidget.class_4281<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageItem> {
			private final LanguageDefinition languageDefinition;

			public LanguageItem(LanguageDefinition languageDefinition) {
				this.languageDefinition = languageDefinition;
			}

			@Override
			public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
				LanguageOptionsScreen.this.font.setRightToLeft(true);
				LanguageSelectionListWidget.this.drawCenteredString(
					LanguageOptionsScreen.this.font, this.languageDefinition.toString(), LanguageSelectionListWidget.this.width / 2, j + 1, 16777215
				);
				LanguageOptionsScreen.this.font.setRightToLeft(LanguageOptionsScreen.this.languageManager.getLanguage().isRightToLeft());
			}

			@Override
			public boolean mouseClicked(double d, double e, int i) {
				if (i == 0) {
					this.onPressed();
					return true;
				} else {
					return false;
				}
			}

			private void onPressed() {
				LanguageSelectionListWidget.this.method_20100(this);
			}
		}
	}
}
