package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.resource.language.I18n;

@Environment(EnvType.CLIENT)
public class RestoreTask extends LongRunningTask {
	private final Backup backup;
	private final long worldId;
	private final RealmsConfigureWorldScreen lastScreen;

	public RestoreTask(Backup backup, long worldId, RealmsConfigureWorldScreen lastScreen) {
		this.backup = backup;
		this.worldId = worldId;
		this.lastScreen = lastScreen;
	}

	public void run() {
		this.setTitle(I18n.translate("mco.backup.restoring"));
		RealmsClient realmsClient = RealmsClient.createRealmsClient();
		int i = 0;

		while (i < 25) {
			try {
				if (this.aborted()) {
					return;
				}

				realmsClient.restoreWorld(this.worldId, this.backup.backupId);
				pause(1);
				if (this.aborted()) {
					return;
				}

				setScreen(this.lastScreen.getNewScreen());
				return;
			} catch (RetryCallException var4) {
				if (this.aborted()) {
					return;
				}

				pause(var4.delaySeconds);
				i++;
			} catch (RealmsServiceException var5) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't restore backup", (Throwable)var5);
				setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
				return;
			} catch (Exception var6) {
				if (this.aborted()) {
					return;
				}

				LOGGER.error("Couldn't restore backup", (Throwable)var6);
				this.error(var6.getLocalizedMessage());
				return;
			}
		}
	}
}
