package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.Vector3f;

@Environment(EnvType.CLIENT)
public final class class_5604 {
	@Nullable
	private final String field_27708;
	private final Vector3f field_27709;
	private final Vector3f field_27710;
	private final class_5605 field_27711;
	private final boolean field_27712;
	private final class_5611 field_27713;
	private final class_5611 field_27714;

	protected class_5604(
		@Nullable String string, float f, float g, float h, float i, float j, float k, float l, float m, class_5605 arg, boolean bl, float n, float o
	) {
		this.field_27708 = string;
		this.field_27713 = new class_5611(f, g);
		this.field_27709 = new Vector3f(h, i, j);
		this.field_27710 = new Vector3f(k, l, m);
		this.field_27711 = arg;
		this.field_27712 = bl;
		this.field_27714 = new class_5611(n, o);
	}

	public ModelPart.Cuboid method_32093(int i, int j) {
		return new ModelPart.Cuboid(
			(int)this.field_27713.method_32118(),
			(int)this.field_27713.method_32119(),
			this.field_27709.getX(),
			this.field_27709.getY(),
			this.field_27709.getZ(),
			this.field_27710.getX(),
			this.field_27710.getY(),
			this.field_27710.getZ(),
			this.field_27711.field_27716,
			this.field_27711.field_27717,
			this.field_27711.field_27718,
			this.field_27712,
			(float)i * this.field_27714.method_32118(),
			(float)j * this.field_27714.method_32119()
		);
	}
}
