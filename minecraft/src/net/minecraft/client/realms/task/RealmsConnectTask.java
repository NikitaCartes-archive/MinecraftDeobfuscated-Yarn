package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsConnectTask extends LongRunningTask {
	private final RealmsConnection realmsConnect;
	private final RealmsServer field_26922;
	private final RealmsServerAddress field_20223;

	public RealmsConnectTask(Screen lastScreen, RealmsServer realmsServer, RealmsServerAddress realmsServerAddress) {
		this.field_26922 = realmsServer;
		this.field_20223 = realmsServerAddress;
		this.realmsConnect = new RealmsConnection(lastScreen);
	}

	public void run() {
		this.setTitle(new TranslatableText("mco.connect.connecting"));
		net.minecraft.client.realms.RealmsServerAddress realmsServerAddress = net.minecraft.client.realms.RealmsServerAddress.parseString(this.field_20223.address);
		this.realmsConnect.connect(this.field_26922, realmsServerAddress.getHost(), realmsServerAddress.getPort());
	}

	@Override
	public void abortTask() {
		this.realmsConnect.abort();
		MinecraftClient.getInstance().getResourcePackProvider().clear();
	}

	@Override
	public void tick() {
		this.realmsConnect.tick();
	}
}
