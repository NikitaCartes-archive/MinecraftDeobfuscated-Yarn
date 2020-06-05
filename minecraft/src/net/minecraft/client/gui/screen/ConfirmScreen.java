package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfirmScreen extends Screen {
	private final Text message;
	private final List<StringRenderable> messageSplit = Lists.<StringRenderable>newArrayList();
	protected Text yesTranslated;
	protected Text noTranslated;
	private int buttonEnableTimer;
	protected final BooleanConsumer callback;

	public ConfirmScreen(BooleanConsumer callback, Text title, Text message) {
		this(callback, title, message, ScreenTexts.YES, ScreenTexts.NO);
	}

	public ConfirmScreen(BooleanConsumer callback, Text title, Text message, Text text, Text text2) {
		super(title);
		this.callback = callback;
		this.message = message;
		this.yesTranslated = text;
		this.noTranslated = text2;
	}

	@Override
	public String getNarrationMessage() {
		return super.getNarrationMessage() + ". " + this.message.getString();
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesTranslated, buttonWidget -> this.callback.accept(true)));
		this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noTranslated, buttonWidget -> this.callback.accept(false)));
		this.messageSplit.clear();
		this.messageSplit.addAll(this.textRenderer.wrapLines(this.message, this.width - 50));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		int i = 90;

		for (StringRenderable stringRenderable : this.messageSplit) {
			this.drawCenteredText(matrices, this.textRenderer, stringRenderable, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	public void disableButtons(int i) {
		this.buttonEnableTimer = i;

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			abstractButtonWidget.active = false;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.buttonEnableTimer == 0) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.active = true;
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.callback.accept(false);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
}
