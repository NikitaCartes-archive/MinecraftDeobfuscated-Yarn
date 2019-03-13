package net.minecraft.client.gui.menu;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4185;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen {
	private final Screen field_2360;
	protected final BackupPromptScreen.Callback callback;
	protected final String title;
	private final String subtitle;
	private final List<String> wrappedText = Lists.<String>newArrayList();
	protected final String confirmText;
	protected final String skipText;
	protected final String cancelText;

	public BackupPromptScreen(Screen screen, BackupPromptScreen.Callback callback, String string, String string2) {
		this.field_2360 = screen;
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
		this.wrappedText.addAll(this.fontRenderer.wrapStringToWidthAsList(this.subtitle, this.screenWidth - 50));
		this.addButton(new class_4185(this.screenWidth / 2 - 155, 100 + (this.wrappedText.size() + 1) * 9, 150, 20, this.confirmText) {
			@Override
			public void method_1826() {
				BackupPromptScreen.this.callback.proceed(true);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 155 + 160, 100 + (this.wrappedText.size() + 1) * 9, 150, 20, this.skipText) {
			@Override
			public void method_1826() {
				BackupPromptScreen.this.callback.proceed(false);
			}
		});
		this.addButton(new class_4185(this.screenWidth / 2 - 155 + 80, 124 + (this.wrappedText.size() + 1) * 9, 150, 20, this.cancelText) {
			@Override
			public void method_1826() {
				BackupPromptScreen.this.client.method_1507(BackupPromptScreen.this.field_2360);
			}
		});
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, this.title, this.screenWidth / 2, 70, 16777215);
		int k = 90;

		for (String string : this.wrappedText) {
			this.drawStringCentered(this.fontRenderer, string, this.screenWidth / 2, k, 16777215);
			k += 9;
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
			this.client.method_1507(this.field_2360);
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
