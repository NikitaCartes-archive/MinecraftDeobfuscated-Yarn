package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen {
	private final Screen parent;
	protected final BackupPromptScreen.Callback field_18971;
	private final TextComponent subtitle;
	private final List<String> wrappedText = Lists.<String>newArrayList();
	protected final String confirmText;
	protected final String skipText;
	protected final String cancelText;

	public BackupPromptScreen(Screen screen, BackupPromptScreen.Callback callback, TextComponent textComponent, TextComponent textComponent2) {
		super(textComponent);
		this.parent = screen;
		this.field_18971 = callback;
		this.subtitle = textComponent2;
		this.confirmText = I18n.translate("selectWorld.backupJoinConfirmButton");
		this.skipText = I18n.translate("selectWorld.backupJoinSkipButton");
		this.cancelText = I18n.translate("gui.cancel");
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.wrappedText.clear();
		this.wrappedText.addAll(this.fontRenderer.wrapStringToWidthAsList(this.subtitle.getFormattedText(), this.screenWidth - 50));
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155, 100 + (this.wrappedText.size() + 1) * 9, 150, 20, this.confirmText, buttonWidget -> this.field_18971.proceed(true)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155 + 160, 100 + (this.wrappedText.size() + 1) * 9, 150, 20, this.skipText, buttonWidget -> this.field_18971.proceed(false)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155 + 80, 124 + (this.wrappedText.size() + 1) * 9, 150, 20, this.cancelText, buttonWidget -> this.client.openScreen(this.parent)
			)
		);
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.wrappedText) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Callback {
		void proceed(boolean bl);
	}
}
