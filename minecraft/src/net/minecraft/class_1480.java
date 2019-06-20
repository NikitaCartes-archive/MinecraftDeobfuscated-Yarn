package net.minecraft;

public abstract class class_1480 extends class_1314 {
	protected class_1480(class_1299<? extends class_1480> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	public boolean method_6094() {
		return true;
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6292;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return arg.method_8606(this);
	}

	@Override
	public int method_5970() {
		return 120;
	}

	@Override
	public boolean method_5974(double d) {
		return true;
	}

	@Override
	protected int method_6110(class_1657 arg) {
		return 1 + this.field_6002.field_9229.nextInt(3);
	}

	protected void method_6673(int i) {
		if (this.method_5805() && !this.method_5816()) {
			this.method_5855(i - 1);
			if (this.method_5669() == -20) {
				this.method_5855(0);
				this.method_5643(class_1282.field_5859, 2.0F);
			}
		} else {
			this.method_5855(300);
		}
	}

	@Override
	public void method_5670() {
		int i = this.method_5669();
		super.method_5670();
		this.method_6673(i);
	}

	@Override
	public boolean method_5675() {
		return false;
	}

	@Override
	public boolean method_5931(class_1657 arg) {
		return false;
	}
}
