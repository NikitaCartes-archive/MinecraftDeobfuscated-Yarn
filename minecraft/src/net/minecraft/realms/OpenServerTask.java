package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class OpenServerTask extends LongRunningTask {
	private final RealmsServer serverData;
	private final Screen returnScreen;
	private final boolean join;
	private final RealmsMainScreen mainScreen;

	public OpenServerTask(RealmsServer realmsServer, Screen returnScreen, RealmsMainScreen mainScreen, boolean join) {
		this.serverData = realmsServer;
		this.returnScreen = returnScreen;
		this.join = join;
		this.mainScreen = mainScreen;
	}

	public void run() {
		this.setTitle(I18n.translate("mco.configure.world.opening"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();

		for (int i = 0; i < 25; i++) {
			if (this.aborted()) {
				return;
			}

			try {
				boolean bl = realmsClient.open(this.serverData.id);
				if (bl) {
					if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
						((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
					}

					this.serverData.state = RealmsServer.State.OPEN;
					if (this.join) {
						this.mainScreen.play(this.serverData, this.returnScreen);
					} else {
						setScreen(this.returnScreen);
					}
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

				LOGGER.error("Failed to open server", (Throwable)var5);
				this.method_27453("Failed to open the server");
			}
		}
	}
}
