package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsCreateWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.ResetWorldInfo;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CreatingSnapshotWorldTask extends LongRunningTask {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text TITLE_TEXT = Text.translatable("mco.snapshot.creating");
	private final long parentId;
	private final ResetWorldInfo resetWorldInfo;
	private final String name;
	private final String motd;
	private final RealmsMainScreen mainScreen;
	@Nullable
	private WorldCreationTask worldCreationTask;
	@Nullable
	private ResettingNormalWorldTask resettingNormalWorldTask;

	public CreatingSnapshotWorldTask(RealmsMainScreen mainScreen, long parentId, ResetWorldInfo resetWorldInfo, String name, String motd) {
		this.parentId = parentId;
		this.resetWorldInfo = resetWorldInfo;
		this.name = name;
		this.motd = motd;
		this.mainScreen = mainScreen;
	}

	public void run() {
		RealmsClient realmsClient = RealmsClient.create();

		try {
			RealmsServer realmsServer = realmsClient.createPrereleaseServer(this.parentId);
			this.worldCreationTask = new WorldCreationTask(realmsServer.id, this.name, this.motd);
			this.resettingNormalWorldTask = new ResettingNormalWorldTask(
				this.resetWorldInfo,
				realmsServer.id,
				RealmsCreateWorldScreen.CREATING_TEXT,
				() -> MinecraftClient.getInstance().execute(() -> RealmsMainScreen.play(realmsServer, this.mainScreen, true))
			);
			if (this.aborted()) {
				return;
			}

			this.worldCreationTask.run();
			if (this.aborted()) {
				return;
			}

			this.resettingNormalWorldTask.run();
		} catch (RealmsServiceException var3) {
			LOGGER.error("Couldn't create snapshot world", (Throwable)var3);
			this.error(var3);
		} catch (Exception var4) {
			LOGGER.error("Couldn't create snapshot world", (Throwable)var4);
			this.error(var4);
		}
	}

	@Override
	public Text getTitle() {
		return TITLE_TEXT;
	}

	@Override
	public void abortTask() {
		super.abortTask();
		if (this.worldCreationTask != null) {
			this.worldCreationTask.abortTask();
		}

		if (this.resettingNormalWorldTask != null) {
			this.resettingNormalWorldTask.abortTask();
		}
	}
}
