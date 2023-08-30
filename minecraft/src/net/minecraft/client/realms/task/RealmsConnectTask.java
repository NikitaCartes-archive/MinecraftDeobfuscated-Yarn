package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsConnectTask extends LongRunningTask {
	private static final Text TITLE = Text.translatable("mco.connect.connecting");
	private final RealmsConnection realmsConnect;
	private final RealmsServer server;
	private final RealmsServerAddress address;

	public RealmsConnectTask(Screen lastScreen, RealmsServer server, RealmsServerAddress address) {
		this.server = server;
		this.address = address;
		this.realmsConnect = new RealmsConnection(lastScreen);
	}

	public void run() {
		this.realmsConnect.connect(this.server, ServerAddress.parse(this.address.address));
	}

	@Override
	public void abortTask() {
		super.abortTask();
		this.realmsConnect.abort();
		MinecraftClient.getInstance().getServerResourcePackProvider().clear();
	}

	@Override
	public void tick() {
		this.realmsConnect.tick();
	}

	@Override
	public Text getTitle() {
		return TITLE;
	}
}
