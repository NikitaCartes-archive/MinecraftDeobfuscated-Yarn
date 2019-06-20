package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_382 {
	private final class_2960 field_2277;
	private final float field_2276;
	private final float field_2275;
	private final float field_2274;
	private final float field_2273;
	private final float field_2272;
	private final float field_2280;
	private final float field_2279;
	private final float field_2278;

	public class_382(class_2960 arg, float f, float g, float h, float i, float j, float k, float l, float m) {
		this.field_2277 = arg;
		this.field_2276 = f;
		this.field_2275 = g;
		this.field_2274 = h;
		this.field_2273 = i;
		this.field_2272 = j;
		this.field_2280 = k;
		this.field_2279 = l;
		this.field_2278 = m;
	}

	public void method_2025(class_1060 arg, boolean bl, float f, float g, class_287 arg2, float h, float i, float j, float k) {
		int l = 3;
		float m = f + this.field_2272;
		float n = f + this.field_2280;
		float o = this.field_2279 - 3.0F;
		float p = this.field_2278 - 3.0F;
		float q = g + o;
		float r = g + p;
		float s = bl ? 1.0F - 0.25F * o : 0.0F;
		float t = bl ? 1.0F - 0.25F * p : 0.0F;
		arg2.method_1315((double)(m + s), (double)q, 0.0).method_1312((double)this.field_2276, (double)this.field_2274).method_1336(h, i, j, k).method_1344();
		arg2.method_1315((double)(m + t), (double)r, 0.0).method_1312((double)this.field_2276, (double)this.field_2273).method_1336(h, i, j, k).method_1344();
		arg2.method_1315((double)(n + t), (double)r, 0.0).method_1312((double)this.field_2275, (double)this.field_2273).method_1336(h, i, j, k).method_1344();
		arg2.method_1315((double)(n + s), (double)q, 0.0).method_1312((double)this.field_2275, (double)this.field_2274).method_1336(h, i, j, k).method_1344();
	}

	@Nullable
	public class_2960 method_2026() {
		return this.field_2277;
	}
}
