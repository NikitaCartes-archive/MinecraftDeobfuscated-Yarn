package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CloseServerTask extends LongRunningTask {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final RealmsServer serverData;
	private final RealmsConfigureWorldScreen configureScreen;

	public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen) {
		this.serverData = realmsServer;
		this.configureScreen = configureWorldScreen;
	}

	public void run() {
		this.setTitle(Text.translatable("mco.configure.world.closing"));
		RealmsClient realmsClient = RealmsClient.create();

		for (int i = 0; i < 25; i++) {
			if (this.aborted()) {
				return;
			}

			try {
				boolean bl = realmsClient.close(this.serverData.id);
				if (bl) {
					this.configureScreen.stateChanged();
					this.serverData.state = RealmsServer.State.CLOSED;
					setScreen(this.configureScreen);
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

				LOGGER.error("Failed to close server", (Throwable)var5);
				this.error(var5);
			}
		}
	}
}
