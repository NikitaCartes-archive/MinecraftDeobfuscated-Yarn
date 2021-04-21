package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.ResetWorldInfo;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ResettingNormalWorldTask extends ResettingWorldTask {
	private final ResetWorldInfo info;

	public ResettingNormalWorldTask(ResetWorldInfo info, long serverId, Text title, Runnable callback) {
		super(serverId, title, callback);
		this.info = info;
	}

	@Override
	protected void resetWorld(RealmsClient client, long worldId) throws RealmsServiceException {
		client.resetWorldWithSeed(worldId, this.info);
	}
}
