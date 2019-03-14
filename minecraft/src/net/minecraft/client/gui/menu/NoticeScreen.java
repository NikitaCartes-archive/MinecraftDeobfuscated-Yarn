package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
	private final Runnable actionHandler;
	protected final TextComponent title;
	protected final TextComponent notice;
	private final List<String> noticeLines = Lists.<String>newArrayList();
	protected final String buttonString;
	private int field_2347;

	public NoticeScreen(Runnable runnable, TextComponent textComponent, TextComponent textComponent2) {
		this(runnable, textComponent, textComponent2, "gui.back");
	}

	public NoticeScreen(Runnable runnable, TextComponent textComponent, TextComponent textComponent2, String string) {
		this.actionHandler = runnable;
		this.title = textComponent;
		this.notice = textComponent2;
		this.buttonString = I18n.translate(string);
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(this.screenWidth / 2 - 100, this.screenHeight / 6 + 168, this.buttonString) {
			@Override
			public void onPressed() {
				NoticeScreen.this.actionHandler.run();
			}
		});
		this.noticeLines.clear();
		this.noticeLines.addAll(this.fontRenderer.wrapStringToWidthAsList(this.notice.getFormattedText(), this.screenWidth - 50));
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.noticeLines) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
		}

		super.draw(i, j, f);
	}

	@Override
	public void update() {
		super.update();
		if (--this.field_2347 == 0) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.enabled = true;
			}
		}
	}
}
