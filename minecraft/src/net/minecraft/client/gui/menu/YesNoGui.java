package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;

@Environment(EnvType.CLIENT)
public class YesNoGui extends Gui {
	public YesNoCallback callback;
	protected String title;
	private final String message;
	private final List<String> messageSplit = Lists.<String>newArrayList();
	protected String yesTranslated;
	protected String noTranslated;
	public int callbackId;
	private int buttonEnableTimer;

	public YesNoGui(YesNoCallback yesNoCallback, String string, String string2, int i) {
		this.callback = yesNoCallback;
		this.title = string;
		this.message = string2;
		this.callbackId = i;
		this.yesTranslated = I18n.translate("gui.yes");
		this.noTranslated = I18n.translate("gui.no");
	}

	public YesNoGui(YesNoCallback yesNoCallback, String string, String string2, String string3, String string4, int i) {
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
		this.addButton(new OptionButtonWidget(0, this.width / 2 - 155, this.height / 6 + 96, this.yesTranslated) {
			@Override
			public void onPressed(double d, double e) {
				YesNoGui.this.callback.handle(true, YesNoGui.this.callbackId);
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.noTranslated) {
			@Override
			public void onPressed(double d, double e) {
				YesNoGui.this.callback.handle(false, YesNoGui.this.callbackId);
			}
		});
		this.messageSplit.clear();
		this.messageSplit.addAll(this.fontRenderer.wrapStringToWidthAsList(this.message, this.width - 50));
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
		int k = 90;

		for (String string : this.messageSplit) {
			this.drawStringCentered(this.fontRenderer, string, this.width / 2, k, 16777215);
			k += this.fontRenderer.FONT_HEIGHT;
		}

		super.draw(i, j, f);
	}

	public void disableButtons(int i) {
		this.buttonEnableTimer = i;

		for (ButtonWidget buttonWidget : this.buttonWidgets) {
			buttonWidget.enabled = false;
		}
	}

	@Override
	public void update() {
		super.update();
		if (--this.buttonEnableTimer == 0) {
			for (ButtonWidget buttonWidget : this.buttonWidgets) {
				buttonWidget.enabled = true;
			}
		}
	}

	@Override
	public boolean canClose() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.callback.handle(false, this.callbackId);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}
}
