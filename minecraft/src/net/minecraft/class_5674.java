package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.ResettingWorldTask;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_5674 extends ResettingWorldTask {
	private final WorldTemplate field_27952;

	public class_5674(WorldTemplate worldTemplate, long l, Text text, Runnable runnable) {
		super(l, text, runnable);
		this.field_27952 = worldTemplate;
	}

	@Override
	protected void method_32517(RealmsClient realmsClient, long l) throws RealmsServiceException {
		realmsClient.resetWorldWithTemplate(l, this.field_27952.id);
	}
}
