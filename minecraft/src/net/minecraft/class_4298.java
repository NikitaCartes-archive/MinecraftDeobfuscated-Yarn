package net.minecraft;

public class class_4298 extends class_2804 {
	public class_4298() {
		super(128);
	}

	public class_4298(class_2804 arg, int i) {
		super(128);
		System.arraycopy(arg.method_12137(), i * 128, this.field_12783, 0, 128);
	}

	@Override
	protected int method_12140(int i, int j, int k) {
		return k << 4 | i;
	}

	@Override
	public byte[] method_12137() {
		byte[] bs = new byte[2048];

		for (int i = 0; i < 16; i++) {
			System.arraycopy(this.field_12783, 0, bs, i * 128, 128);
		}

		return bs;
	}
}
