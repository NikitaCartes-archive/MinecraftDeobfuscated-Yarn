package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.network.LanServerEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LocalServerListEntry extends ServerListWidget.Entry {
	private final MultiplayerScreen gui;
	protected final MinecraftClient client;
	protected final LanServerEntry field_3070;
	private long lastUpdateMillis;

	protected LocalServerListEntry(MultiplayerScreen multiplayerScreen, LanServerEntry lanServerEntry) {
		this.gui = multiplayerScreen;
		this.field_3070 = lanServerEntry;
		this.client = MinecraftClient.getInstance();
	}

	@Override
	public void draw(int i, int j, int k, int l, boolean bl, float f) {
		int m = this.getX();
		int n = this.getY();
		this.client.field_1772.draw(I18n.translate("lanServer.title"), (float)(m + 32 + 3), (float)(n + 1), 16777215);
		this.client.field_1772.draw(this.field_3070.getMotd(), (float)(m + 32 + 3), (float)(n + 12), 8421504);
		if (this.client.field_1690.hideServerAddress) {
			this.client.field_1772.draw(I18n.translate("selectServer.hiddenAddress"), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		} else {
			this.client.field_1772.draw(this.field_3070.getAddressPort(), (float)(m + 32 + 3), (float)(n + 12 + 11), 3158064);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		this.gui.setIndex(this.method_1908());
		if (SystemUtil.getMeasuringTimeMs() - this.lastUpdateMillis < 250L) {
			this.gui.method_2536();
		}

		this.lastUpdateMillis = SystemUtil.getMeasuringTimeMs();
		return false;
	}

	public LanServerEntry method_2559() {
		return this.field_3070;
	}
}
