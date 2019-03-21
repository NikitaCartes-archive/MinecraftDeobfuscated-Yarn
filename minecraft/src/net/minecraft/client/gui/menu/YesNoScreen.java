package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.text.TextComponent;

@Environment(EnvType.CLIENT)
public class YesNoScreen extends Screen {
	protected final YesNoCallback callback;
	private final TextComponent message;
	private final List<String> messageSplit = Lists.<String>newArrayList();
	protected String yesTranslated;
	protected String noTranslated;
	protected final int callbackId;
	private int buttonEnableTimer;

	public YesNoScreen(YesNoCallback yesNoCallback, TextComponent textComponent, TextComponent textComponent2, int i) {
		this(yesNoCallback, textComponent, textComponent2, I18n.translate("gui.yes"), I18n.translate("gui.no"), i);
	}

	public YesNoScreen(YesNoCallback yesNoCallback, TextComponent textComponent, TextComponent textComponent2, String string, String string2, int i) {
		super(textComponent);
		this.callback = yesNoCallback;
		this.message = textComponent2;
		this.yesTranslated = string;
		this.noTranslated = string2;
		this.callbackId = i;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155, this.screenHeight / 6 + 96, 150, 20, this.yesTranslated, buttonWidget -> this.callback.confirmResult(true, this.callbackId)
			)
		);
		this.addButton(
			new ButtonWidget(
				this.screenWidth / 2 - 155 + 160,
				this.screenHeight / 6 + 96,
				150,
				20,
				this.noTranslated,
				buttonWidget -> this.callback.confirmResult(false, this.callbackId)
			)
		);
		this.messageSplit.clear();
		this.messageSplit.addAll(this.fontRenderer.wrapStringToWidthAsList(this.message.getFormattedText(), this.screenWidth - 50));
	}

	@Override
	public void render(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title.getFormattedText(), this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.messageSplit) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
		}

		super.render(i, j, f);
	}

	public void disableButtons(int i) {
		this.buttonEnableTimer = i;

		for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
			abstractButtonWidget.active = false;
		}
	}

	@Override
	public void update() {
		super.update();
		if (--this.buttonEnableTimer == 0) {
			for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
				abstractButtonWidget.active = true;
			}
		}
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.callback.confirmResult(false, this.callbackId);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}
}
