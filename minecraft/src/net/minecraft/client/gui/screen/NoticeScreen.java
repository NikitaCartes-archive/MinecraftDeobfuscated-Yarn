package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private final Runnable actionHandler;
	protected final Text notice;
	private final List<Text> noticeLines = Lists.<Text>newArrayList();
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
		this.noticeLines.clear();
		this.noticeLines.addAll(this.textRenderer.wrapLines(this.notice, this.width - 50));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawStringWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
		int i = 90;

		for (Text text : this.noticeLines) {
			this.drawStringWithShadow(matrices, this.textRenderer, text, this.width / 2, i, 16777215);
			i += 9;
		}

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
