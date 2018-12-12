package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class BackupPromptGui extends Gui {
	private final Gui parent;
	protected BackupPromptGui.Callback callback;
	protected String title;
	private final String subtitle;
	private final List<String> wrappedText = Lists.<String>newArrayList();
	protected String confirmText;
	protected String skipText;
	protected String cancelText;

	public BackupPromptGui(Gui gui, BackupPromptGui.Callback callback, String string, String string2) {
		this.parent = gui;
		this.callback = callback;
		this.title = string;
		this.subtitle = string2;
		this.confirmText = I18n.translate("selectWorld.backupJoinConfirmButton");
		this.skipText = I18n.translate("selectWorld.backupJoinSkipButton");
		this.cancelText = I18n.translate("gui.cancel");
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.wrappedText.clear();
		this.wrappedText.addAll(this.fontRenderer.wrapStringToWidthAsList(this.subtitle, this.width - 50));
		this.addButton(new OptionButtonWidget(0, this.width / 2 - 155, 100 + (this.wrappedText.size() + 1) * this.fontRenderer.fontHeight, this.confirmText) {
			@Override
			public void onPressed(double d, double e) {
				BackupPromptGui.this.callback.proceed(true);
			}
		});
		this.addButton(new OptionButtonWidget(1, this.width / 2 - 155 + 160, 100 + (this.wrappedText.size() + 1) * this.fontRenderer.fontHeight, this.skipText) {
			@Override
			public void onPressed(double d, double e) {
				BackupPromptGui.this.callback.proceed(false);
			}
		});
		this.addButton(new ButtonWidget(1, this.width / 2 - 155 + 80, 124 + (this.wrappedText.size() + 1) * this.fontRenderer.fontHeight, 150, 20, this.cancelText) {
			@Override
			public void onPressed(double d, double e) {
				BackupPromptGui.this.client.openGui(BackupPromptGui.this.parent);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
		int k = 90;

		for (String string : this.wrappedText) {
			this.drawStringCentered(this.fontRenderer, string, this.width / 2, k, 16777215);
			k += this.fontRenderer.fontHeight;
		}

		super.draw(i, j, f);
	}

	@Override
	public boolean doesEscapeKeyClose() {
		return false;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (i == 256) {
			this.client.openGui(this.parent);
			return true;
		} else {
			return super.keyPressed(i, j, k);
		}
	}

	@Environment(EnvType.CLIENT)
	public interface Callback {
		void proceed(boolean bl);
	}
}
