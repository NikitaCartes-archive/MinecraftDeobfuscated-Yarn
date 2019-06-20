package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_364;

@Environment(EnvType.CLIENT)
public class RealmsLabelProxy implements class_364 {
	private final RealmsLabel label;

	public RealmsLabelProxy(RealmsLabel realmsLabel) {
		this.label = realmsLabel;
	}

	public RealmsLabel getLabel() {
		return this.label;
	}
}
