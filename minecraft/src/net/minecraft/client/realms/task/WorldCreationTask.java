package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class WorldCreationTask extends LongRunningTask {
	private final String name;
	private final String motd;
	private final long worldId;
	private final Screen lastScreen;

	public WorldCreationTask(long worldId, String name, String motd, Screen lastScreen) {
		this.worldId = worldId;
		this.name = name;
		this.motd = motd;
		this.lastScreen = lastScreen;
	}

	public void run() {
		this.setTitle(new TranslatableText("mco.create.world.wait"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		try {
			realmsClient.initializeWorld(this.worldId, this.name, this.motd);
			setScreen(this.lastScreen);
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't create world");
			this.error(var3.toString());
		} catch (Exception var4) {
			LOGGER.error("Could not create world");
			this.error(var4.getLocalizedMessage());
		}
	}
}
