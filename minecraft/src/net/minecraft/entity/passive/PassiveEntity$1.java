package net.minecraft.entity.passive;

import net.minecraft.entity.EntityData;

public class PassiveEntity$1 implements EntityData {
	private int field_20684;
	private boolean field_20685 = true;
	private float field_20686 = 0.05F;

	public int method_22432() {
		return this.field_20684;
	}

	public void method_22435() {
		this.field_20684++;
	}

	public boolean method_22436() {
		return this.field_20685;
	}

	public void method_22434(boolean bl) {
		this.field_20685 = bl;
	}

	public float method_22437() {
		return this.field_20686;
	}

	public void method_22433(float f) {
		this.field_20686 = f;
	}
}
