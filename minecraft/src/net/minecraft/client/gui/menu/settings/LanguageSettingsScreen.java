package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;

@Environment(EnvType.CLIENT)
public class LanguageSettingsScreen extends Screen {
	protected final Screen field_2490;
	private LanguageSettingsScreen.class_427 field_2486;
	private final GameOptions settings;
	private final LanguageManager languageManager;
	private OptionButtonWidget field_2487;
	private OptionButtonWidget field_2491;

	public LanguageSettingsScreen(Screen screen, GameOptions gameOptions, LanguageManager languageManager) {
		this.field_2490 = screen;
		this.settings = gameOptions;
		this.languageManager = languageManager;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.field_2486;
	}

	@Override
	protected void onInitialized() {
		this.field_2486 = new LanguageSettingsScreen.class_427(this.client);
		this.listeners.add(this.field_2486);
		this.field_2487 = this.addButton(
			new OptionButtonWidget(
				100, this.width / 2 - 155, this.height - 38, GameOptions.Option.FORCE_UNICODE_FONT, this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT)
			) {
				@Override
				public void onPressed(double d, double e) {
					LanguageSettingsScreen.this.settings.updateOption(this.getOption(), 1);
					this.text = LanguageSettingsScreen.this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT);
					LanguageSettingsScreen.this.method_2181();
				}
			}
		);
		this.field_2491 = this.addButton(new OptionButtonWidget(6, this.width / 2 - 155 + 160, this.height - 38, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				LanguageSettingsScreen.this.client.openScreen(LanguageSettingsScreen.this.field_2490);
			}
		});
		super.onInitialized();
	}

	private void method_2181() {
		this.client.onResolutionChanged();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.field_2486.draw(i, j, f);
		this.drawStringCentered(this.fontRenderer, I18n.translate("options.language"), this.width / 2, 16, 16777215);
		this.drawStringCentered(this.fontRenderer, "(" + I18n.translate("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.draw(i, j, f);
	}

	@Environment(EnvType.CLIENT)
	class class_427 extends AbstractListWidget {
		private final List<String> field_2492 = Lists.<String>newArrayList();
		private final Map<String, LanguageDefinition> field_2493 = Maps.<String, LanguageDefinition>newHashMap();

		public class_427(MinecraftClient minecraftClient) {
			super(minecraftClient, LanguageSettingsScreen.this.width, LanguageSettingsScreen.this.height, 32, LanguageSettingsScreen.this.height - 65 + 4, 18);

			for (LanguageDefinition languageDefinition : LanguageSettingsScreen.this.languageManager.getAllLanguages()) {
				this.field_2493.put(languageDefinition.getCode(), languageDefinition);
				this.field_2492.add(languageDefinition.getCode());
			}
		}

		@Override
		protected int getEntryCount() {
			return this.field_2492.size();
		}

		@Override
		protected boolean selectEntry(int i, int j, double d, double e) {
			LanguageDefinition languageDefinition = (LanguageDefinition)this.field_2493.get(this.field_2492.get(i));
			LanguageSettingsScreen.this.languageManager.setLanguage(languageDefinition);
			LanguageSettingsScreen.this.settings.language = languageDefinition.getCode();
			this.client.reloadResources();
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.languageManager.isRightToLeft());
			LanguageSettingsScreen.this.field_2491.text = I18n.translate("gui.done");
			LanguageSettingsScreen.this.field_2487.text = LanguageSettingsScreen.this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT);
			LanguageSettingsScreen.this.settings.write();
			LanguageSettingsScreen.this.method_2181();
			return true;
		}

		@Override
		protected boolean isSelectedEntry(int i) {
			return ((String)this.field_2492.get(i)).equals(LanguageSettingsScreen.this.languageManager.getLanguage().getCode());
		}

		@Override
		protected int getMaxScrollPosition() {
			return this.getEntryCount() * 18;
		}

		@Override
		protected void drawBackground() {
			LanguageSettingsScreen.this.drawBackground();
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(true);
			this.drawStringCentered(
				LanguageSettingsScreen.this.fontRenderer, ((LanguageDefinition)this.field_2493.get(this.field_2492.get(i))).toString(), this.width / 2, k + 1, 16777215
			);
			LanguageSettingsScreen.this.fontRenderer.setRightToLeft(LanguageSettingsScreen.this.languageManager.getLanguage().isRightToLeft());
		}
	}
}
