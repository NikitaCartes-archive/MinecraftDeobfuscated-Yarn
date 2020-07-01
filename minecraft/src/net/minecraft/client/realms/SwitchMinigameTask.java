package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SwitchMinigameTask extends LongRunningTask {
	private final long worldId;
	private final WorldTemplate worldTemplate;
	private final RealmsConfigureWorldScreen lastScreen;

	public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen) {
		this.worldId = worldId;
		this.worldTemplate = worldTemplate;
		this.lastScreen = lastScreen;
	}

	public void run() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		String string = I18n.translate("mco.minigame.world.starting.screen.title");
		this.setTitle(string);

		for (int i = 0; i < 25; i++) {
			try {
				if (this.aborted()) {
					return;
				}

				if (realmsClient.putIntoMinigameMode(this.worldId, this.worldTemplate.id)) {
					setScreen(this.lastScreen);
					break;
				}
			} catch (RetryCallException var5) {
				if (this.aborted()) {
					return;
				}

				pause(var5.delaySeconds);
			} catch (Exception var6) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't start mini game!");
				this.error(var6.toString());
			}
		}
	}
}
