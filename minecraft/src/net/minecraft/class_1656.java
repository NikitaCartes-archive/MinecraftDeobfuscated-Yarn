package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1656 {
	public boolean field_7480;
	public boolean field_7479;
	public boolean field_7478;
	public boolean field_7477;
	public boolean field_7476 = true;
	private float field_7481 = 0.05F;
	private float field_7482 = 0.1F;

	public void method_7251(class_2487 arg) {
		class_2487 lv = new class_2487();
		lv.method_10556("invulnerable", this.field_7480);
		lv.method_10556("flying", this.field_7479);
		lv.method_10556("mayfly", this.field_7478);
		lv.method_10556("instabuild", this.field_7477);
		lv.method_10556("mayBuild", this.field_7476);
		lv.method_10548("flySpeed", this.field_7481);
		lv.method_10548("walkSpeed", this.field_7482);
		arg.method_10566("abilities", lv);
	}

	public void method_7249(class_2487 arg) {
		if (arg.method_10573("abilities", 10)) {
			class_2487 lv = arg.method_10562("abilities");
			this.field_7480 = lv.method_10577("invulnerable");
			this.field_7479 = lv.method_10577("flying");
			this.field_7478 = lv.method_10577("mayfly");
			this.field_7477 = lv.method_10577("instabuild");
			if (lv.method_10573("flySpeed", 99)) {
				this.field_7481 = lv.method_10583("flySpeed");
				this.field_7482 = lv.method_10583("walkSpeed");
			}

			if (lv.method_10573("mayBuild", 1)) {
				this.field_7476 = lv.method_10577("mayBuild");
			}
		}
	}

	public float method_7252() {
		return this.field_7481;
	}

	@Environment(EnvType.CLIENT)
	public void method_7248(float f) {
		this.field_7481 = f;
	}

	public float method_7253() {
		return this.field_7482;
	}

	@Environment(EnvType.CLIENT)
	public void method_7250(float f) {
		this.field_7482 = f;
	}
}
