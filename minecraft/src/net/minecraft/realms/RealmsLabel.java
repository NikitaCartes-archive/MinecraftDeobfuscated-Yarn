package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_364;

@Environment(EnvType.CLIENT)
public class RealmsLabel extends RealmsGuiEventListener {
	private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
	private final String text;
	private final int field_23663;
	private final int field_23664;
	private final int color;

	public RealmsLabel(String string, int i, int j, int k) {
		this.text = string;
		this.field_23663 = i;
		this.field_23664 = j;
		this.color = k;
	}

	public void render(RealmsScreen realmsScreen) {
		realmsScreen.drawCenteredString(this.text, this.field_23663, this.field_23664, this.color);
	}

	@Override
	public class_364 getProxy() {
		return this.proxy;
	}

	public String getText() {
		return this.text;
	}
}
