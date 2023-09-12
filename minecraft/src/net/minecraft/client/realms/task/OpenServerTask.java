package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class OpenServerTask extends LongRunningTask {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE = Text.translatable("mco.configure.world.opening");
	private final RealmsServer serverData;
	private final Screen returnScreen;
	private final boolean join;
	private final MinecraftClient client;

	public OpenServerTask(RealmsServer realmsServer, Screen returnScreen, boolean join, MinecraftClient client) {
		this.serverData = realmsServer;
		this.returnScreen = returnScreen;
		this.join = join;
		this.client = client;
	}

	public void run() {
		RealmsClient realmsClient = RealmsClient.create();

		for (int i = 0; i < 25; i++) {
			if (this.aborted()) {
				return;
			}

			try {
				boolean bl = realmsClient.open(this.serverData.id);
				if (bl) {
					this.client.execute(() -> {
						if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
							((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
						}

						this.serverData.state = RealmsServer.State.OPEN;
						if (this.join) {
							RealmsMainScreen.play(this.serverData, this.returnScreen);
						} else {
							this.client.setScreen(this.returnScreen);
						}
					});
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

				LOGGER.error("Failed to open server", (Throwable)var5);
				this.error(var5);
			}
		}
	}

	@Override
	public Text getTitle() {
		return TITLE;
	}
}
