package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LocalScanProgressListEntry extends ServerListGuiWidget.Entry {
	private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getY() + j / 2 - this.client.fontRenderer.fontHeight / 2;
		this.client
			.fontRenderer
			.draw(
				I18n.translate("lanServer.scanning"),
				(float)(this.client.currentGui.width / 2 - this.client.fontRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
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
			.fontRenderer
			.draw(
				string,
				(float)(this.client.currentGui.width / 2 - this.client.fontRenderer.getStringWidth(string) / 2),
				(float)(m + this.client.fontRenderer.fontHeight),
				8421504
			);
	}
}
