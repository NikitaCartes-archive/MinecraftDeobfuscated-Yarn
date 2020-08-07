package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5489;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private final Runnable actionHandler;
	protected final Text notice;
	private class_5489 noticeLines = class_5489.field_26528;
	protected final Text buttonString;
	private int field_2347;

	public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
		this(actionHandler, title, notice, ScreenTexts.BACK);
	}

	public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text text) {
		super(title);
		this.actionHandler = actionHandler;
		this.notice = notice;
		this.buttonString = text;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, buttonWidget -> this.actionHandler.run()));
		this.noticeLines = class_5489.method_30890(this.textRenderer, this.notice, this.width - 50);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		this.noticeLines.method_30888(matrices, this.width / 2, 90);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void tick() {
		super.tick();
		if (--this.field_2347 == 0) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.active = true;
			}
		}
	}
}
