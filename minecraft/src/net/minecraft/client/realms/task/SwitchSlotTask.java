package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.text.TranslatableText;

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
		this.setTitle(new TranslatableText("mco.minigame.world.slot.screen.title"));

		for (int i = 0; i < 25; i++) {
			try {
				if (this.aborted()) {
					return;
				}

				if (realmsClient.switchSlot(this.worldId, this.slot)) {
					this.callback.run();
					break;
				}
			} catch (RetryCallException var4) {
				if (this.aborted()) {
					return;
				}

				pause(var4.delaySeconds);
			} catch (Exception var5) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't switch world!");
				this.error(var5.toString());
			}
		}
	}
}
