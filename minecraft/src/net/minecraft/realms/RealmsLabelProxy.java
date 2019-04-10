package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(EnvType.CLIENT)
public class RealmsLabelProxy implements Element {
	private final RealmsLabel label;

	public RealmsLabelProxy(RealmsLabel realmsLabel) {
		this.label = realmsLabel;
	}

	public RealmsLabel getLabel() {
		return this.label;
	}
}
