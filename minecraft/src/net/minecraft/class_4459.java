package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.PacketByteBuf;

public class class_4459 extends PathNode {
	private float field_20304 = Float.MAX_VALUE;
	private PathNode field_20305;
	private boolean field_20306;

	public class_4459(PathNode pathNode) {
		super(pathNode.x, pathNode.y, pathNode.z);
	}

	@Environment(EnvType.CLIENT)
	public class_4459(int i, int j, int k) {
		super(i, j, k);
	}

	public void method_21662(float f, PathNode pathNode) {
		if (f < this.field_20304) {
			this.field_20304 = f;
			this.field_20305 = pathNode;
		}
	}

	public PathNode method_21664() {
		return this.field_20305;
	}

	public void method_21665() {
		this.field_20306 = true;
	}

	public boolean method_21666() {
		return this.field_20306;
	}

	@Environment(EnvType.CLIENT)
	public static class_4459 method_21663(PacketByteBuf packetByteBuf) {
		class_4459 lv = new class_4459(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
		lv.field_46 = packetByteBuf.readFloat();
		lv.field_43 = packetByteBuf.readFloat();
		lv.field_42 = packetByteBuf.readBoolean();
		lv.type = PathNodeType.values()[packetByteBuf.readInt()];
		lv.heapWeight = packetByteBuf.readFloat();
		return lv;
	}
}
