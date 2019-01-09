package net.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_745 extends class_742 {
	public class_745(class_1937 arg, GameProfile gameProfile) {
		super(arg, gameProfile);
		this.field_6013 = 1.0F;
		this.field_5960 = true;
		this.field_7485 = 0.25F;
	}

	@Override
	public boolean method_5640(double d) {
		double e = this.method_5829().method_995() * 10.0;
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * method_5824();
		return d < e * e;
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		return true;
	}

	@Override
	public void method_5773() {
		this.field_7485 = 0.0F;
		super.method_5773();
		this.field_6211 = this.field_6225;
		double d = this.field_5987 - this.field_6014;
		double e = this.field_6035 - this.field_5969;
		float f = class_3532.method_15368(d * d + e * e) * 4.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		this.field_6225 = this.field_6225 + (f - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	@Override
	public void method_6007() {
		if (this.field_6210 > 0) {
			double d = this.field_5987 + (this.field_6224 - this.field_5987) / (double)this.field_6210;
			double e = this.field_6010 + (this.field_6245 - this.field_6010) / (double)this.field_6210;
			double f = this.field_6035 + (this.field_6263 - this.field_6035) / (double)this.field_6210;
			this.field_6031 = (float)((double)this.field_6031 + class_3532.method_15338(this.field_6284 - (double)this.field_6031) / (double)this.field_6210);
			this.field_5965 = (float)((double)this.field_5965 + (this.field_6221 - (double)this.field_5965) / (double)this.field_6210);
			this.field_6210--;
			this.method_5814(d, e, f);
			this.method_5710(this.field_6031, this.field_5965);
		}

		if (this.field_6265 > 0) {
			this.field_6241 = (float)((double)this.field_6241 + class_3532.method_15338(this.field_6242 - (double)this.field_6241) / (double)this.field_6265);
			this.field_6265--;
		}

		this.field_7505 = this.field_7483;
		this.method_6119();
		float g = class_3532.method_15368(this.field_5967 * this.field_5967 + this.field_6006 * this.field_6006);
		float h = (float)Math.atan(-this.field_5984 * 0.2F) * 15.0F;
		if (g > 0.1F) {
			g = 0.1F;
		}

		if (!this.field_5952 || this.method_6032() <= 0.0F) {
			g = 0.0F;
		}

		if (this.field_5952 || this.method_6032() <= 0.0F) {
			h = 0.0F;
		}

		this.field_7483 = this.field_7483 + (g - this.field_7483) * 0.4F;
		this.field_6223 = this.field_6223 + (h - this.field_6223) * 0.8F;
		this.field_6002.method_16107().method_15396("push");
		this.method_6070();
		this.field_6002.method_16107().method_15407();
	}

	@Override
	public void method_9203(class_2561 arg) {
		class_310.method_1551().field_1705.method_1743().method_1812(arg);
	}
}
