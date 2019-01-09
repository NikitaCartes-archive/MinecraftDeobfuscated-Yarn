package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_708 extends class_703 {
	protected final int field_3883;
	protected final int field_3882;
	private final float field_3881;
	private float field_3879 = 0.91F;
	private float field_3878;
	private float field_3877;
	private float field_3876;
	private boolean field_3880;

	public class_708(class_1937 arg, double d, double e, double f, int i, int j, float g) {
		super(arg, d, e, f);
		this.field_3883 = i;
		this.field_3882 = j;
		this.field_3881 = g;
	}

	public void method_3093(int i) {
		float f = (float)((i & 0xFF0000) >> 16) / 255.0F;
		float g = (float)((i & 0xFF00) >> 8) / 255.0F;
		float h = (float)((i & 0xFF) >> 0) / 255.0F;
		float j = 1.0F;
		this.method_3084(f * 1.0F, g * 1.0F, h * 1.0F);
	}

	public void method_3092(int i) {
		this.field_3878 = (float)((i & 0xFF0000) >> 16) / 255.0F;
		this.field_3877 = (float)((i & 0xFF00) >> 8) / 255.0F;
		this.field_3876 = (float)((i & 0xFF) >> 0) / 255.0F;
		this.field_3880 = true;
	}

	@Override
	public boolean method_3071() {
		return true;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		if (this.field_3866++ >= this.field_3847) {
			this.method_3085();
		}

		if (this.field_3866 > this.field_3847 / 2) {
			this.method_3083(1.0F - ((float)this.field_3866 - (float)(this.field_3847 / 2)) / (float)this.field_3847);
			if (this.field_3880) {
				this.field_3861 = this.field_3861 + (this.field_3878 - this.field_3861) * 0.2F;
				this.field_3842 = this.field_3842 + (this.field_3877 - this.field_3842) * 0.2F;
				this.field_3859 = this.field_3859 + (this.field_3876 - this.field_3859) * 0.2F;
			}
		}

		this.method_3076(this.field_3883 + this.field_3882 - 1 - this.field_3866 * this.field_3882 / this.field_3847);
		this.field_3869 = this.field_3869 + (double)this.field_3881;
		this.method_3069(this.field_3852, this.field_3869, this.field_3850);
		this.field_3852 = this.field_3852 * (double)this.field_3879;
		this.field_3869 = this.field_3869 * (double)this.field_3879;
		this.field_3850 = this.field_3850 * (double)this.field_3879;
		if (this.field_3845) {
			this.field_3852 *= 0.7F;
			this.field_3850 *= 0.7F;
		}
	}

	@Override
	public int method_3068(float f) {
		return 15728880;
	}

	protected void method_3091(float f) {
		this.field_3879 = f;
	}
}
