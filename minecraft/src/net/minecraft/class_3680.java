package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3680<T extends class_1451> extends class_582<T> {
	private float field_16245;
	private float field_16244;
	private float field_16243;

	public class_3680(float f) {
		super(f);
	}

	public void method_17074(T arg, float f, float g, float h) {
		this.field_16245 = arg.method_16082(h);
		this.field_16244 = arg.method_16091(h);
		this.field_16243 = arg.method_16095(h);
		if (this.field_16245 <= 0.0F) {
			this.field_3435.field_3654 = 0.0F;
			this.field_3435.field_3674 = 0.0F;
			this.field_3440.field_3654 = 0.0F;
			this.field_3440.field_3674 = 0.0F;
			this.field_3438.field_3654 = 0.0F;
			this.field_3438.field_3674 = 0.0F;
			this.field_3438.field_3657 = -1.2F;
			this.field_3439.field_3654 = 0.0F;
			this.field_3441.field_3654 = 0.0F;
			this.field_3441.field_3674 = 0.0F;
			this.field_3441.field_3657 = -1.1F;
			this.field_3441.field_3656 = 18.0F;
		}

		super.method_2816(arg, f, g, h);
		if (arg.method_6172()) {
			this.field_3437.field_3654 = (float) (Math.PI / 4);
			this.field_3437.field_3656 += -4.0F;
			this.field_3437.field_3655 += 5.0F;
			this.field_3435.field_3656 += -3.3F;
			this.field_3435.field_3655++;
			this.field_3436.field_3656 += 8.0F;
			this.field_3436.field_3655 += -2.0F;
			this.field_3442.field_3656 += 2.0F;
			this.field_3442.field_3655 += -0.8F;
			this.field_3436.field_3654 = 1.7278761F;
			this.field_3442.field_3654 = 2.670354F;
			this.field_3440.field_3654 = (float) (-Math.PI / 20);
			this.field_3440.field_3656 = 16.1F;
			this.field_3440.field_3655 = -7.0F;
			this.field_3438.field_3654 = (float) (-Math.PI / 20);
			this.field_3438.field_3656 = 16.1F;
			this.field_3438.field_3655 = -7.0F;
			this.field_3439.field_3654 = (float) (-Math.PI / 2);
			this.field_3439.field_3656 = 21.0F;
			this.field_3439.field_3655 = 1.0F;
			this.field_3441.field_3654 = (float) (-Math.PI / 2);
			this.field_3441.field_3656 = 21.0F;
			this.field_3441.field_3655 = 1.0F;
			this.field_3434 = 3;
		}
	}

	public void method_17075(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17080(arg, f, g, h, i, j, k);
		if (this.field_16245 > 0.0F) {
			this.field_3435.field_3674 = this.method_16018(this.field_3435.field_3674, -1.2707963F, this.field_16245);
			this.field_3435.field_3675 = this.method_16018(this.field_3435.field_3675, 1.2707963F, this.field_16245);
			this.field_3440.field_3654 = -1.2707963F;
			this.field_3438.field_3654 = -0.47079635F;
			this.field_3438.field_3674 = -0.2F;
			this.field_3438.field_3657 = -0.2F;
			this.field_3439.field_3654 = -0.4F;
			this.field_3441.field_3654 = 0.5F;
			this.field_3441.field_3674 = -0.5F;
			this.field_3441.field_3657 = -0.3F;
			this.field_3441.field_3656 = 20.0F;
			this.field_3436.field_3654 = this.method_16018(this.field_3436.field_3654, 0.8F, this.field_16244);
			this.field_3442.field_3654 = this.method_16018(this.field_3442.field_3654, -0.4F, this.field_16244);
		}

		if (this.field_16243 > 0.0F) {
			this.field_3435.field_3654 = this.method_16018(this.field_3435.field_3654, -0.58177644F, this.field_16243);
		}
	}

	protected float method_16018(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}
}
