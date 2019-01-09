package net.minecraft;

public class class_1372 extends class_1352 {
	private final class_1439 field_6542;
	private class_1646 field_6544;
	private int field_6543;

	public class_1372(class_1439 arg) {
		this.field_6542 = arg;
		this.method_6265(3);
	}

	@Override
	public boolean method_6264() {
		if (!this.field_6542.field_6002.method_8530()) {
			return false;
		} else if (this.field_6542.method_6051().nextInt(8000) != 0) {
			return false;
		} else {
			this.field_6544 = this.field_6542.field_6002.method_8472(class_1646.class, this.field_6542.method_5829().method_1009(6.0, 2.0, 6.0), this.field_6542);
			return this.field_6544 != null;
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6543 > 0;
	}

	@Override
	public void method_6269() {
		this.field_6543 = 400;
		this.field_6542.method_6497(true);
	}

	@Override
	public void method_6270() {
		this.field_6542.method_6497(false);
		this.field_6544 = null;
	}

	@Override
	public void method_6268() {
		this.field_6542.method_5988().method_6226(this.field_6544, 30.0F, 30.0F);
		this.field_6543--;
	}
}
