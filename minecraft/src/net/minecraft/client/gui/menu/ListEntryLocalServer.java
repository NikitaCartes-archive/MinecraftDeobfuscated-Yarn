package net.minecraft.client.gui.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ServerListWidget;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class ListEntryLocalServer extends ServerListWidget.class_504 {
	private final MultiplayerGui gui;
	protected final MinecraftClient client;
	protected final LanServerEntry entry;
	private long lastUpdateMillis;

	public ListEntryLocalServer(MultiplayerGui multiplayerGui, LanServerEntry lanServerEntry) {
		this.gui = multiplayerGui;
		this.entry = lanServerEntry;
		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void drawEntry(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.method_1907();
		int n = this.method_1906();
		this.client.fontRenderer.draw(I18n.translate("lanServer.title"), (float)(m + 32 + 3), (float)(n + 1), 16777215);
		this.client.fontRenderer.draw(this.entry.getMotd(), (float)(m + 32 + 3), (float)(n + 12), 8421504);
		if (this.client.options.hideServerAddress) {
			this.client.fontRenderer.draw(I18n.translate("selectServer.hiddenAddress"), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		} else {
			this.client.fontRenderer.draw(this.entry.getAddressPort(), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.gui.setIndex(this.method_1908());
		if (SystemUtil.getMeasuringTimeMili() - this.lastUpdateMillis < 250L) {
			this.gui.method_2536();
		}

		this.lastUpdateMillis = SystemUtil.getMeasuringTimeMili();
		return false;
	}

	public LanServerEntry getLanServerEntry() {
		return this.entry;
	}
}
