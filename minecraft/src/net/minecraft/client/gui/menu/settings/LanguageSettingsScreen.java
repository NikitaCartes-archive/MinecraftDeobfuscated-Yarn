package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LanguageSettingsScreen extends Screen {
	protected final Screen parent;
	private LanguageSettingsScreen.LanguageSelectionListWidget languageSelectionList;
	private final GameOptions settings;
	private final LanguageManager languageManager;
	private OptionButtonWidget forceUnicodeButton;
	private ButtonWidget doneButton;
	private int selectedIndex = -1;

	public LanguageSettingsScreen(Screen screen, GameOptions gameOptions, LanguageManager languageManager) {
		this.parent = screen;
		this.settings = gameOptions;
		this.languageManager = languageManager;
	}

	@Override
	protected void onInitialized() {
		this.languageSelectionList = new LanguageSettingsScreen.LanguageSelectionListWidget(this.client);
		this.listeners.add(this.languageSelectionList);
		this.forceUnicodeButton = this.addButton(
			new OptionButtonWidget(
				this.screenWidth / 2 - 155, this.screenHeight - 38, 150, 20, GameOption.FORCE_UNICODE_FONT, GameOption.FORCE_UNICODE_FONT.method_18495(this.settings)
			) {
				@Override
				public void onPressed() {
					GameOption.FORCE_UNICODE_FONT.method_18491(LanguageSettingsScreen.this.settings);
					LanguageSettingsScreen.this.settings.write();
					this.setText(GameOption.FORCE_UNICODE_FONT.method_18495(LanguageSettingsScreen.this.settings));
					LanguageSettingsScreen.this.method_2181();
				}
			}
		);
		this.doneButton = this.addButton(
			new ButtonWidget(this.screenWidth / 2 - 155 + 160, this.screenHeight - 38, 150, 20, I18n.translate("gui.done")) {
				@Override
				public void onPressed() {
					if (LanguageSettingsScreen.this.selectedIndex > -1) {
						LanguageSettingsScreen.LanguageSelectionEntry languageSelectionEntry = (LanguageSettingsScreen.LanguageSelectionEntry)LanguageSettingsScreen.this.languageSelectionList
							.getInputListeners()
							.get(LanguageSettingsScreen.this.selectedIndex);
						if (!languageSelectionEntry.languageDefinition.getCode().equals(LanguageSettingsScreen.this.languageManager.getLanguage().getCode())) {
							LanguageSettingsScreen.this.languageManager.setLanguage(languageSelectionEntry.languageDefinition);
							LanguageSettingsScreen.this.settings.language = languageSelectionEntry.languageDefinition.getCode();
							LanguageSettingsScreen.this.client.reloadResources();
							LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.languageManager.isRightToLeft());
							LanguageSettingsScreen.this.doneButton.setText(I18n.translate("gui.done"));
							LanguageSettingsScreen.this.forceUnicodeButton.setText(GameOption.FORCE_UNICODE_FONT.method_18495(LanguageSettingsScreen.this.settings));
							LanguageSettingsScreen.this.settings.write();
						}
					}

					LanguageSettingsScreen.this.client.openScreen(LanguageSettingsScreen.this.parent);
				}
			}
		);
		super.onInitialized();
	}

	private void method_2181() {
		this.client.onResolutionChanged();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.languageSelectionList.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("options.language"), this.screenWidth / 2, 16, 16777215);
		this.drawStringCentered(this.fontRenderer, "(" + I18n.translate("options.languageWarning") + ")", this.screenWidth / 2, this.screenHeight - 56, 8421504);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	public class LanguageSelectionEntry extends EntryListWidget.Entry<LanguageSettingsScreen.LanguageSelectionEntry> {
		private final LanguageDefinition languageDefinition;

		public LanguageSelectionEntry(LanguageDefinition languageDefinition) {
			this.languageDefinition = languageDefinition;
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(true);
			LanguageSettingsScreen.this.drawStringCentered(
				LanguageSettingsScreen.this.fontRenderer, this.languageDefinition.toString(), LanguageSettingsScreen.this.screenWidth / 2, this.getY() + 1, 16777215
			);
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.languageManager.getLanguage().isRightToLeft());
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
			LanguageSettingsScreen.this.selectedIndex = this.parent.getInputListeners().indexOf(this);
		}
	}

	@Environment(EnvType.CLIENT)
	class LanguageSelectionListWidget extends EntryListWidget<LanguageSettingsScreen.LanguageSelectionEntry> {
		public LanguageSelectionListWidget(MinecraftClient minecraftClient) {
			super(
				minecraftClient,
				LanguageSettingsScreen.this.screenWidth,
				LanguageSettingsScreen.this.screenHeight,
				32,
				LanguageSettingsScreen.this.screenHeight - 65 + 4,
				18
			);

			for (LanguageDefinition languageDefinition : LanguageSettingsScreen.this.languageManager.getAllLanguages()) {
				LanguageSettingsScreen.LanguageSelectionEntry languageSelectionEntry = LanguageSettingsScreen.this.new LanguageSelectionEntry(languageDefinition);
				int i = this.addEntry(languageSelectionEntry);
				if (LanguageSettingsScreen.this.languageManager.getLanguage().getCode().equals(languageDefinition.getCode())) {
					LanguageSettingsScreen.this.selectedIndex = i;
				}
			}

			if (LanguageSettingsScreen.this.selectedIndex >= 0) {
				this.method_19350(LanguageSettingsScreen.this.selectedIndex);
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == LanguageSettingsScreen.this.selectedIndex;
		}

		@Override
		public boolean hasFocus() {
			return true;
		}

		@Override
		protected void method_19351(int i) {
			LanguageSettingsScreen.this.selectedIndex = MathHelper.clamp(LanguageSettingsScreen.this.selectedIndex + i, 0, this.getEntryCount() - 1);
			if (LanguageSettingsScreen.this.selectedIndex >= 0) {
				this.method_19349((EntryListWidget.Entry)this.getInputListeners().get(LanguageSettingsScreen.this.selectedIndex));
			}
		}

		@Override
		protected void drawBackground() {
			LanguageSettingsScreen.this.drawBackground();
		}

		@Override
		public void setHasFocus(boolean bl) {
			if (bl && LanguageSettingsScreen.this.selectedIndex < 0 && this.getEntryCount() > 0) {
				LanguageSettingsScreen.this.selectedIndex = 0;
			}
		}

		@Override
		protected boolean method_19352() {
			return LanguageSettingsScreen.this.getFocused() == this;
		}
	}
}
