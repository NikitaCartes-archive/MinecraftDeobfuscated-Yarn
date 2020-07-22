package net.minecraft.client.gui.screen;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen {
	@Nullable
	private final Screen parent;
	protected final BackupPromptScreen.Callback callback;
	private final Text subtitle;
	private final boolean showEraseCacheCheckbox;
	private class_5489 wrappedText = class_5489.field_26528;
	private CheckboxWidget eraseCacheCheckbox;

	public BackupPromptScreen(@Nullable Screen parent, BackupPromptScreen.Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckBox) {
		super(title);
		this.parent = parent;
		this.callback = callback;
		this.subtitle = subtitle;
		this.showEraseCacheCheckbox = showEraseCacheCheckBox;
	}

	@Override
	protected void init() {
		super.init();
		this.wrappedText = class_5489.method_30890(this.textRenderer, this.subtitle, this.width - 50);
		int i = (this.wrappedText.method_30887() + 1) * 9;
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				100 + i,
				150,
				20,
				new TranslatableText("selectWorld.backupJoinConfirmButton"),
				buttonWidget -> this.callback.proceed(true, this.eraseCacheCheckbox.isChecked())
			)
		);
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155 + 160,
				100 + i,
				150,
				20,
				new TranslatableText("selectWorld.backupJoinSkipButton"),
				buttonWidget -> this.callback.proceed(false, this.eraseCacheCheckbox.isChecked())
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 80, 124 + i, 150, 20, ScreenTexts.CANCEL, buttonWidget -> this.client.openScreen(this.parent)));
		this.eraseCacheCheckbox = new CheckboxWidget(this.width / 2 - 155 + 80, 76 + i, 150, 20, new TranslatableText("selectWorld.backupEraseCache"), false);
		if (this.showEraseCacheCheckbox) {
			this.addButton(this.eraseCacheCheckbox);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 50, 16777215);
		this.wrappedText.method_30888(matrices, this.width / 2, 70);
		super.render(matrices, mouseX, mouseY, delta);
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
