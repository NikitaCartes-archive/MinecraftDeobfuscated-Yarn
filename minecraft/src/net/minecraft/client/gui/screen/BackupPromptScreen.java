package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen {
	private final Screen parent;
	protected final BackupPromptScreen.Callback callback;
	private final Text subtitle;
	private final boolean showEraseCacheCheckbox;
	private final List<String> wrappedText = Lists.<String>newArrayList();
	private final String eraseCacheText;
	private final String confirmText;
	private final String skipText;
	private final String cancelText;
	private CheckboxWidget eraseCacheCheckbox;

	public BackupPromptScreen(Screen parent, BackupPromptScreen.Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckBox) {
		super(title);
		this.parent = parent;
		this.callback = callback;
		this.subtitle = subtitle;
		this.showEraseCacheCheckbox = showEraseCacheCheckBox;
		this.eraseCacheText = I18n.translate("selectWorld.backupEraseCache");
		this.confirmText = I18n.translate("selectWorld.backupJoinConfirmButton");
		this.skipText = I18n.translate("selectWorld.backupJoinSkipButton");
		this.cancelText = I18n.translate("gui.cancel");
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText.clear();
		this.wrappedText.addAll(this.textRenderer.wrapStringToWidthAsList(this.subtitle.asFormattedString(), this.width - 50));
		int i = (this.wrappedText.size() + 1) * 9;
		this.addButton(
			new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, this.confirmText, buttonWidget -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked()))
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160, 100 + i, 150, 20, this.skipText, buttonWidget -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 80, 124 + i, 150, 20, this.cancelText, buttonWidget -> this.client.openScreen(this.parent)));
		this.eraseCacheCheckbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, this.eraseCacheText, false);
		if (this.showEraseCacheCheckbox) {
			this.addButton(this.eraseCacheCheckbox);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 50, 16777215);
		int i = 70;

		for (String string : this.wrappedText) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.client.openScreen(this.parent);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Callback {
		void proceed(boolean bl, boolean bl2);
	}
}
