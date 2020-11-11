package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.ResetWorldInfo;
import net.minecraft.client.realms.task.ResettingWorldTask;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_5673 extends ResettingWorldTask {
	private final ResetWorldInfo field_27951;

	public class_5673(ResetWorldInfo resetWorldInfo, long l, Text text, Runnable runnable) {
		super(l, text, runnable);
		this.field_27951 = resetWorldInfo;
	}

	@Override
	protected void method_32517(RealmsClient realmsClient, long l) throws RealmsServiceException {
		realmsClient.resetWorldWithSeed(l, this.field_27951);
	}
}
