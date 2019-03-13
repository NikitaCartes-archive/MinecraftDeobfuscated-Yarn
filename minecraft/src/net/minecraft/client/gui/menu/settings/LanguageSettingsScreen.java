package net.minecraft.client.gui.menu.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
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
	protected final Screen field_2490;
	private LanguageSettingsScreen.class_4195 languageSelectionList;
	private final GameOptions settings;
	private final LanguageManager field_2488;
	private OptionButtonWidget forceUnicodeButton;
	private class_4185 doneButton;
	private int field_18740 = -1;

	public LanguageSettingsScreen(Screen screen, GameOptions gameOptions, LanguageManager languageManager) {
		this.field_2490 = screen;
		this.settings = gameOptions;
		this.field_2488 = languageManager;
	}

	@Override
	protected void onInitialized() {
		this.languageSelectionList = new LanguageSettingsScreen.class_4195(this.client);
		this.listeners.add(this.languageSelectionList);
		this.forceUnicodeButton = this.addButton(
			new OptionButtonWidget(
				this.screenWidth / 2 - 155, this.screenHeight - 38, 150, 20, GameOption.FORCE_UNICODE_FONT, GameOption.FORCE_UNICODE_FONT.method_18495(this.settings)
			) {
				@Override
				public void method_1826() {
					GameOption.FORCE_UNICODE_FONT.method_18491(LanguageSettingsScreen.this.settings);
					LanguageSettingsScreen.this.settings.write();
					this.setText(GameOption.FORCE_UNICODE_FONT.method_18495(LanguageSettingsScreen.this.settings));
					LanguageSettingsScreen.this.method_2181();
				}
			}
		);
		this.doneButton = this.addButton(
			new class_4185(this.screenWidth / 2 - 155 + 160, this.screenHeight - 38, 150, 20, I18n.translate("gui.done")) {
				@Override
				public void method_1826() {
					if (LanguageSettingsScreen.this.field_18740 > -1) {
						LanguageSettingsScreen.class_4194 lv = (LanguageSettingsScreen.class_4194)LanguageSettingsScreen.this.languageSelectionList
							.getInputListeners()
							.get(LanguageSettingsScreen.this.field_18740);
						if (!lv.field_18743.getCode().equals(LanguageSettingsScreen.this.field_2488.getLanguage().getCode())) {
							LanguageSettingsScreen.this.field_2488.setLanguage(lv.field_18743);
							LanguageSettingsScreen.this.settings.language = lv.field_18743.getCode();
							LanguageSettingsScreen.this.client.reloadResources();
							LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.field_2488.isRightToLeft());
							LanguageSettingsScreen.this.doneButton.setText(I18n.translate("gui.done"));
							LanguageSettingsScreen.this.forceUnicodeButton.setText(GameOption.FORCE_UNICODE_FONT.method_18495(LanguageSettingsScreen.this.settings));
							LanguageSettingsScreen.this.settings.write();
						}
					}

					LanguageSettingsScreen.this.client.method_1507(LanguageSettingsScreen.this.field_2490);
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
	public class class_4194 extends EntryListWidget.Entry<LanguageSettingsScreen.class_4194> {
		private final LanguageDefinition field_18743;

		public class_4194(LanguageDefinition languageDefinition) {
			this.field_18743 = languageDefinition;
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(true);
			LanguageSettingsScreen.this.drawStringCentered(
				LanguageSettingsScreen.this.fontRenderer, this.field_18743.toString(), LanguageSettingsScreen.this.screenWidth / 2, this.getY() + 1, 16777215
			);
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.field_2488.getLanguage().isRightToLeft());
		}

		@Override
		public boolean mouseClicked(double d, double e, int i) {
			if (i == 0) {
				this.method_19381();
				return true;
			} else {
				return false;
			}
		}

		private void method_19381() {
			LanguageSettingsScreen.this.field_18740 = this.parent.getInputListeners().indexOf(this);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4195 extends EntryListWidget<LanguageSettingsScreen.class_4194> {
		public class_4195(MinecraftClient minecraftClient) {
			super(
				minecraftClient,
				LanguageSettingsScreen.this.screenWidth,
				LanguageSettingsScreen.this.screenHeight,
				32,
				LanguageSettingsScreen.this.screenHeight - 65 + 4,
				18
			);

			for (LanguageDefinition languageDefinition : LanguageSettingsScreen.this.field_2488.getAllLanguages()) {
				LanguageSettingsScreen.class_4194 lv = LanguageSettingsScreen.this.new class_4194(languageDefinition);
				int i = this.addEntry(lv);
				if (LanguageSettingsScreen.this.field_2488.getLanguage().getCode().equals(languageDefinition.getCode())) {
					LanguageSettingsScreen.this.field_18740 = i;
				}
			}

			if (LanguageSettingsScreen.this.field_18740 >= 0) {
				this.method_19350(LanguageSettingsScreen.this.field_18740);
			}
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return i == LanguageSettingsScreen.this.field_18740;
		}

		@Override
		public boolean hasFocus() {
			return true;
		}

		@Override
		protected void method_19351(int i) {
			LanguageSettingsScreen.this.field_18740 = MathHelper.clamp(LanguageSettingsScreen.this.field_18740 + i, 0, this.getEntryCount() - 1);
			if (LanguageSettingsScreen.this.field_18740 >= 0) {
				this.method_19349((EntryListWidget.Entry)this.getInputListeners().get(LanguageSettingsScreen.this.field_18740));
			}
		}

		@Override
		protected void drawBackground() {
			LanguageSettingsScreen.this.drawBackground();
		}

		@Override
		public void setHasFocus(boolean bl) {
			if (bl && LanguageSettingsScreen.this.field_18740 < 0 && this.getEntryCount() > 0) {
				LanguageSettingsScreen.this.field_18740 = 0;
			}
		}

		@Override
		protected boolean method_19352() {
			return LanguageSettingsScreen.this.method_19357() == this;
		}
	}
}
