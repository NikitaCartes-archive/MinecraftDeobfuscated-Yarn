package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SwitchMinigameTask extends LongRunningTask {
	private static final Logger field_36360 = LogUtils.getLogger();
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
		this.setTitle(Text.method_43471("mco.minigame.world.starting.screen.title"));

		for (int i = 0; i < 25; i++) {
			try {
				if (this.aborted()) {
					return;
				}

				if (realmsClient.putIntoMinigameMode(this.worldId, this.worldTemplate.id)) {
					setScreen(this.lastScreen);
					break;
				}
			} catch (RetryCallException var4) {
				if (this.aborted()) {
					return;
				}

				pause((long)var4.delaySeconds);
			} catch (Exception var5) {
				if (this.aborted()) {
					return;
				}

				field_36360.error("Couldn't start mini game!");
				this.error(var5.toString());
			}
		}
	}
}
