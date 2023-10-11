package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldCreationTask extends LongRunningTask {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE = Text.translatable("mco.create.world.wait");
	private final String name;
	private final String motd;
	private final long worldId;

	public WorldCreationTask(long worldId, String name, String motd) {
		this.worldId = worldId;
		this.name = name;
		this.motd = motd;
	}

	public void run() {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			realmsClient.initializeWorld(this.worldId, this.name, this.motd);
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't create world", (Throwable)var3);
			this.error(var3);
		} catch (Exception var4) {
			LOGGER.error("Could not create world", (Throwable)var4);
			this.error(var4);
		}
	}

	@Override
	public Text getTitle() {
		return TITLE;
	}
}
