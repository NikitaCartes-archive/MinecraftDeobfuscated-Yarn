package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
public abstract class class_5294 {
	private static final Map<DimensionType, class_5294> field_24609 = Maps.<DimensionType, class_5294>newHashMap();
	private final float[] field_24610 = new float[4];
	private final float field_24611;
	private final boolean field_24612;
	private final boolean field_24613;

	public class_5294(float f, boolean bl, boolean bl2) {
		this.field_24611 = f;
		this.field_24612 = bl;
		this.field_24613 = bl2;
	}

	public static class_5294 method_28111(DimensionType dimensionType) {
		return (class_5294)field_24609.getOrDefault(dimensionType, field_24609.get(DimensionType.OVERWORLD));
	}

	@Nullable
	public float[] method_28109(float f, float g) {
		float h = 0.4F;
		float i = MathHelper.cos(f * (float) (Math.PI * 2)) - 0.0F;
		float j = -0.0F;
		if (i >= -0.4F && i <= 0.4F) {
			float k = (i - -0.0F) / 0.4F * 0.5F + 0.5F;
			float l = 1.0F - (1.0F - MathHelper.sin(k * (float) Math.PI)) * 0.99F;
			l *= l;
			this.field_24610[0] = k * 0.3F + 0.7F;
			this.field_24610[1] = k * k * 0.7F + 0.2F;
			this.field_24610[2] = k * k * 0.0F + 0.2F;
			this.field_24610[3] = l;
			return this.field_24610;
		} else {
			return null;
		}
	}

	public float method_28108() {
		return this.field_24611;
	}

	public boolean method_28113() {
		return this.field_24612;
	}

	public abstract Vec3d method_28112(Vec3d vec3d, float f);

	public abstract boolean method_28110(int i, int j);

	public boolean method_28114() {
		return this.field_24613;
	}

	static {
		field_24609.put(DimensionType.OVERWORLD, new class_5294.class_5297());
		field_24609.put(DimensionType.THE_NETHER, new class_5294.class_5296());
		field_24609.put(DimensionType.THE_END, new class_5294.class_5295());
	}

	@Environment(EnvType.CLIENT)
	public static class class_5295 extends class_5294 {
		public class_5295() {
			super(Float.NaN, false, false);
		}

		@Override
		public Vec3d method_28112(Vec3d vec3d, float f) {
			return vec3d.multiply(0.15F);
		}

		@Override
		public boolean method_28110(int i, int j) {
			return false;
		}

		@Nullable
		@Override
		public float[] method_28109(float f, float g) {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_5296 extends class_5294 {
		public class_5296() {
			super(Float.NaN, true, false);
		}

		@Override
		public Vec3d method_28112(Vec3d vec3d, float f) {
			return vec3d;
		}

		@Override
		public boolean method_28110(int i, int j) {
			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_5297 extends class_5294 {
		public class_5297() {
			super(128.0F, true, true);
		}

		@Override
		public Vec3d method_28112(Vec3d vec3d, float f) {
			return vec3d.multiply((double)(f * 0.94F + 0.06F), (double)(f * 0.94F + 0.06F), (double)(f * 0.91F + 0.09F));
		}

		@Override
		public boolean method_28110(int i, int j) {
			return false;
		}
	}
}
