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

	public NoticeScreen(Runnable runnable, Text text, Text text2) {
		this(runnable, text, text2, "gui.back");
	}

	public NoticeScreen(Runnable runnable, Text text, Text text2, String string) {
		super(text);
		this.actionHandler = runnable;
		this.notice = text2;
		this.buttonString = I18n.translate(string);
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.buttonString, buttonWidget -> this.actionHandler.run()));
		this.noticeLines.clear();
		this.noticeLines.addAll(this.font.wrapStringToWidthAsList(this.notice.asFormattedString(), this.width - 50));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 70, 16777215);
		int k = 90;

		for (String string : this.noticeLines) {
			this.drawCenteredString(this.font, string, this.width / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
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
