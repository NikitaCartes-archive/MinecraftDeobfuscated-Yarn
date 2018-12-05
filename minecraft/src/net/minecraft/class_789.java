package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sortme.Vector3f;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class class_789 {
	public final Vector3f field_4236;
	public final Direction.Axis field_4239;
	public final float field_4237;
	public final boolean field_4238;

	public class_789(Vector3f vector3f, Direction.Axis axis, float f, boolean bl) {
		this.field_4236 = vector3f;
		this.field_4239 = axis;
		this.field_4237 = f;
		this.field_4238 = bl;
	}
}
