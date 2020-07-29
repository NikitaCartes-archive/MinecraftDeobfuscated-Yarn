package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class RealmsConnectTask extends LongRunningTask {
	private final RealmsConnection realmsConnect;
	private final RealmsServerAddress a;

	public RealmsConnectTask(Screen lastScreen, RealmsServerAddress address) {
		this.a = address;
		this.realmsConnect = new RealmsConnection(lastScreen);
	}

	public void run() {
		this.setTitle(new TranslatableText("mco.connect.connecting"));
		net.minecraft.client.realms.RealmsServerAddress realmsServerAddress = net.minecraft.client.realms.RealmsServerAddress.parseString(this.a.address);
		this.realmsConnect.connect(realmsServerAddress.getHost(), realmsServerAddress.getPort());
	}

	@Override
	public void abortTask() {
		this.realmsConnect.abort();
		MinecraftClient.getInstance().getResourcePackDownloader().clear();
	}

	@Override
	public void tick() {
		this.realmsConnect.tick();
	}
}