package net.minecraft.realms;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class CloseServerTask extends LongRunningTask {
	private final RealmsServer serverData;
	private final RealmsConfigureWorldScreen configureScreen;

	public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen configureWorldScreen) {
		this.serverData = realmsServer;
		this.configureScreen = configureWorldScreen;
	}

	public void run() {
		this.setTitle(I18n.translate("mco.configure.world.closing"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

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

				pause(var4.delaySeconds);
			} catch (Exception var5) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Failed to close server", (Throwable)var5);
				this.method_27453("Failed to close the server");
			}
		}
	}
}
