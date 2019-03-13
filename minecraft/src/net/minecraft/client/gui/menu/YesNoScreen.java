package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;

@Environment(EnvType.CLIENT)
public class YesNoScreen extends Screen {
	public final YesNoCallback callback;
	protected final String title;
	private final String message;
	private final List<String> messageSplit = Lists.<String>newArrayList();
	protected String yesTranslated;
	protected String noTranslated;
	public final int callbackId;
	private int buttonEnableTimer;

	public YesNoScreen(YesNoCallback yesNoCallback, String string, String string2, int i) {
		this.callback = yesNoCallback;
		this.title = string;
		this.message = string2;
		this.callbackId = i;
		this.yesTranslated = I18n.translate("gui.yes");
		this.noTranslated = I18n.translate("gui.no");
	}

	public YesNoScreen(YesNoCallback yesNoCallback, String string, String string2, String string3, String string4, int i) {
		this.callback = yesNoCallback;
		this.title = string;
		this.message = string2;
		this.yesTranslated = string3;
		this.noTranslated = string4;
		this.callbackId = i;
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new class_4185(this.screenWidth / 2 - 155, this.screenHeight / 6 + 96, 150, 20, this.yesTranslated) {
			@Override
			public void method_1826() {
				YesNoScreen.this.callback.confirmResult(true, YesNoScreen.this.callbackId);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 155 + 160, this.screenHeight / 6 + 96, 150, 20, this.noTranslated) {
			@Override
			public void method_1826() {
				YesNoScreen.this.callback.confirmResult(false, YesNoScreen.this.callbackId);
			}
		});
		this.messageSplit.clear();
		this.messageSplit.addAll(this.fontRenderer.wrapStringToWidthAsList(this.message, this.screenWidth - 50));
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.messageSplit) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
		}

		super.draw(i, j, f);
	}

	public void disableButtons(int i) {
		this.buttonEnableTimer = i;

		for (ButtonWidget buttonWidget : this.buttons) {
			buttonWidget.enabled = false;
		}
	}

	@Override
	public void update() {
		super.update();
		if (--this.buttonEnableTimer == 0) {
			for (ButtonWidget buttonWidget : this.buttons) {
				buttonWidget.enabled = true;
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
