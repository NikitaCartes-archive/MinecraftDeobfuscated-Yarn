package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class SwitchSlotTask extends LongRunningTask {
	private final long worldId;
	private final int slot;
	private final Runnable callback;

	public SwitchSlotTask(long worldId, int slot, Runnable callback) {
		this.worldId = worldId;
		this.slot = slot;
		this.callback = callback;
	}

	public void run() {
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		String string = I18n.translate("mco.minigame.world.slot.screen.title");
		this.setTitle(string);

		for (int i = 0; i < 25; i++) {
			try {
				if (this.aborted()) {
					return;
				}

				if (realmsClient.switchSlot(this.worldId, this.slot)) {
					this.callback.run();
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

				LOGGER.error("Couldn't switch world!");
				this.error(var6.toString());
			}
		}
	}
}
