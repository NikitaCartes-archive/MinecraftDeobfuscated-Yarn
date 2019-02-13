package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LocalScanProgressListEntry extends ServerListWidget.Entry {
	private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY() + j / 2 - 9 / 2;
		this.client
			.textRenderer
			.draw(
				I18n.translate("lanServer.scanning"),
				(float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
				(float)m,
				16777215
			);
		String string;
		switch ((int)(SystemUtil.getMeasuringTimeMs() / 300L % 4L)) {
			case 0:
			default:
				string = "O o o";
				break;
			case 1:
			case 3:
				string = "o O o";
				break;
			case 2:
				string = "o o O";
		}

		this.client
			.textRenderer
			.draw(string, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2), (float)(m + 9), 8421504);
	}
}
