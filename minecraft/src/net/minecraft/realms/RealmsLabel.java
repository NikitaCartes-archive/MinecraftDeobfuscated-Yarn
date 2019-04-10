package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(EnvType.CLIENT)
public class RealmsLabel extends RealmsGuiEventListener {
	private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
	private final String text;
	private final int field_19208;
	private final int field_19209;
	private final int color;

	public RealmsLabel(String string, int i, int j, int k) {
		this.text = string;
		this.field_19208 = i;
		this.field_19209 = j;
		this.color = k;
	}

	public void render(RealmsScreen realmsScreen) {
		realmsScreen.drawCenteredString(this.text, this.field_19208, this.field_19209, this.color);
	}

	@Override
	public Element getProxy() {
		return this.proxy;
	}

	public String getText() {
		return this.text;
	}
}
