package net.minecraft;

public class class_1385 extends class_1352 {
	private final class_1314 field_6596;
	private class_1417 field_6595;

	public class_1385(class_1314 arg) {
		this.field_6596 = arg;
		if (!(arg.method_5942() instanceof class_1409)) {
			throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
		}
	}

	@Override
	public boolean method_6264() {
		if (this.field_6596.field_6002.method_8530()) {
			return false;
		} else {
			class_2338 lv = new class_2338(this.field_6596);
			class_1415 lv2 = this.field_6596.field_6002.method_8557().method_6438(lv, 16);
			if (lv2 == null) {
				return false;
			} else {
				this.field_6595 = lv2.method_6386(lv);
				return this.field_6595 == null ? false : (double)this.field_6595.method_6417(lv) < 2.25;
			}
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6596.field_6002.method_8530() ? false : !this.field_6595.method_6413() && this.field_6595.method_6425(new class_2338(this.field_6596));
	}

	@Override
	public void method_6269() {
		((class_1409)this.field_6596.method_5942()).method_6363(false);
		((class_1409)this.field_6596.method_5942()).method_6365(false);
	}

	@Override
	public void method_6270() {
		((class_1409)this.field_6596.method_5942()).method_6363(true);
		((class_1409)this.field_6596.method_5942()).method_6365(true);
		this.field_6595 = null;
	}

	@Override
	public void method_6268() {
		this.field_6595.method_6428();
	}
}
