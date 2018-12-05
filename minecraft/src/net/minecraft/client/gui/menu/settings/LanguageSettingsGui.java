package net.minecraft.client.gui.menu.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.settings.GameOptions;

@Environment(EnvType.CLIENT)
public class LanguageSettingsGui extends Gui {
	protected Gui field_2490;
	private LanguageSettingsGui.class_427 field_2486;
	private final GameOptions settings;
	private final LanguageManager languageManager;
	private OptionButtonWidget field_2487;
	private OptionButtonWidget field_2491;

	public LanguageSettingsGui(Gui gui, GameOptions gameOptions, LanguageManager languageManager) {
		this.field_2490 = gui;
		this.settings = gameOptions;
		this.languageManager = languageManager;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.field_2486;
	}

	@Override
	protected void onInitialized() {
		this.field_2486 = new LanguageSettingsGui.class_427(this.client);
		this.listeners.add(this.field_2486);
		this.field_2487 = this.addButton(
			new OptionButtonWidget(
				100, this.width / 2 - 155, this.height - 38, GameOptions.Option.FORCE_UNICODE_FONT, this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT)
			) {
				@Override
				public void onPressed(double d, double e) {
					LanguageSettingsGui.this.settings.updateOption(this.getOption(), 1);
					this.text = LanguageSettingsGui.this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT);
					LanguageSettingsGui.this.method_2181();
				}
			}
		);
		this.field_2491 = this.addButton(new OptionButtonWidget(6, this.width / 2 - 155 + 160, this.height - 38, I18n.translate("gui.done")) {
			@Override
			public void onPressed(double d, double e) {
				LanguageSettingsGui.this.client.openGui(LanguageSettingsGui.this.field_2490);
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
			super(minecraftClient, LanguageSettingsGui.this.width, LanguageSettingsGui.this.height, 32, LanguageSettingsGui.this.height - 65 + 4, 18);

			for (LanguageDefinition languageDefinition : LanguageSettingsGui.this.languageManager.getAllLanguages()) {
				this.field_2493.put(languageDefinition.getCode(), languageDefinition);
				this.field_2492.add(languageDefinition.getCode());
			}
		}

		@Override
		protected int getEntryCount() {
			return this.field_2492.size();
		}

		@Override
		protected boolean method_1937(int i, int j, double d, double e) {
			LanguageDefinition languageDefinition = (LanguageDefinition)this.field_2493.get(this.field_2492.get(i));
			LanguageSettingsGui.this.languageManager.setLanguage(languageDefinition);
			LanguageSettingsGui.this.settings.language = languageDefinition.getCode();
			this.client.reloadResources();
			LanguageSettingsGui.this.fontRenderer.setRightToLeft(LanguageSettingsGui.this.languageManager.isRightToLeft());
			LanguageSettingsGui.this.field_2491.text = I18n.translate("gui.done");
			LanguageSettingsGui.this.field_2487.text = LanguageSettingsGui.this.settings.getTranslatedName(GameOptions.Option.FORCE_UNICODE_FONT);
			LanguageSettingsGui.this.settings.write();
			LanguageSettingsGui.this.method_2181();
			return true;
		}

		@Override
		protected boolean isSelected(int i) {
			return ((String)this.field_2492.get(i)).equals(LanguageSettingsGui.this.languageManager.getLanguage().getCode());
		}

		@Override
		protected int getContentHeight() {
			return this.getEntryCount() * 18;
		}

		@Override
		protected void method_1936() {
			LanguageSettingsGui.this.drawBackground();
		}

		@Override
		protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
			LanguageSettingsGui.this.fontRenderer.setRightToLeft(true);
			this.drawStringCentered(
				LanguageSettingsGui.this.fontRenderer, ((LanguageDefinition)this.field_2493.get(this.field_2492.get(i))).toString(), this.width / 2, k + 1, 16777215
			);
			LanguageSettingsGui.this.fontRenderer.setRightToLeft(LanguageSettingsGui.this.languageManager.getLanguage().isRightToLeft());
		}
	}
}
