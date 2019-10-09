package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractVertexConsumer implements VertexConsumer {
	protected boolean field_20889 = false;
	protected int field_20890 = 255;
	protected int field_20891 = 255;
	protected int field_20892 = 255;
	protected int field_20893 = 255;

	public void method_22901(int i, int j, int k, int l) {
		this.field_20890 = i;
		this.field_20891 = j;
		this.field_20892 = k;
		this.field_20893 = l;
		this.field_20889 = true;
	}
}
