package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ServerListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ListEntryLocalScanProgress extends ServerListWidget.class_504 {
	private final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1906() + j / 2 - this.client.fontRenderer.FONT_HEIGHT / 2;
		this.client
			.fontRenderer
			.draw(
				I18n.translate("lanServer.scanning"),
				(float)(this.client.currentGui.width / 2 - this.client.fontRenderer.getStringWidth(I18n.translate("lanServer.scanning")) / 2),
				(float)m,
				16777215
			);
		String string;
		switch ((int)(SystemUtil.getMeasuringTimeMili() / 300L % 4L)) {
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
				(float)(m + this.client.fontRenderer.FONT_HEIGHT),
				8421504
			);
	}
}
