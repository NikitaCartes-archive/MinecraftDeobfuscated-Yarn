package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private final Runnable actionHandler;
	protected final Text notice;
	private final List<String> noticeLines = Lists.<String>newArrayList();
	protected final String buttonString;
	private int field_2347;

	public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
		this(actionHandler, title, notice, "gui.back");
	}

	public NoticeScreen(Runnable actionHandler, Text title, Text notice, String buttonString) {
		super(title);
		this.actionHandler = actionHandler;
		this.notice = notice;
		this.buttonString = I18n.translate(buttonString);
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, buttonWidget -> this.actionHandler.run()));
		this.noticeLines.clear();
		this.noticeLines.addAll(this.textRenderer.wrapStringToWidthAsList(this.notice.asFormattedString(), this.width - 50));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.textRenderer, this.title.asFormattedString(), this.width / 2, 70, 16777215);
		int i = 90;

		for (String string : this.noticeLines) {
			this.drawCenteredString(this.textRenderer, string, this.width / 2, i, 16777215);
			i += 9;
		}

		super.render(mouseX, mouseY, delta);
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
