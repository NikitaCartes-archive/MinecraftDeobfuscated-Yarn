package net.minecraft;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class class_1399 extends class_1405 {
	private static final class_4051 field_18091 = new class_4051().method_18422().method_18424();
	private boolean field_6639;
	private int field_6638;
	private final Class<?>[] field_6637;
	private Class<?>[] field_6640;

	public class_1399(class_1314 arg, Class<?>... classs) {
		super(arg, true);
		this.field_6637 = classs;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18408));
	}

	@Override
	public boolean method_6264() {
		int i = this.field_6660.method_6117();
		class_1309 lv = this.field_6660.method_6065();
		if (i != this.field_6638 && lv != null) {
			for (Class<?> class_ : this.field_6637) {
				if (class_.isAssignableFrom(lv.getClass())) {
					return false;
				}
			}

			return this.method_6328(lv, field_18091);
		} else {
			return false;
		}
	}

	public class_1399 method_6318(Class<?>... classs) {
		this.field_6639 = true;
		this.field_6640 = classs;
		return this;
	}

	@Override
	public void method_6269() {
		this.field_6660.method_5980(this.field_6660.method_6065());
		this.field_6664 = this.field_6660.method_5968();
		this.field_6638 = this.field_6660.method_6117();
		this.field_6657 = 300;
		if (this.field_6639) {
			this.method_6317();
		}

		super.method_6269();
	}

	protected void method_6317() {
		double d = this.method_6326();
		List<class_1308> list = this.field_6660
			.field_6002
			.method_18467(
				this.field_6660.getClass(),
				new class_238(
						this.field_6660.field_5987,
						this.field_6660.field_6010,
						this.field_6660.field_6035,
						this.field_6660.field_5987 + 1.0,
						this.field_6660.field_6010 + 1.0,
						this.field_6660.field_6035 + 1.0
					)
					.method_1009(d, 10.0, d)
			);
		Iterator var4 = list.iterator();

		while (true) {
			class_1308 lv;
			while (true) {
				if (!var4.hasNext()) {
					return;
				}

				lv = (class_1308)var4.next();
				if (this.field_6660 != lv
					&& lv.method_5968() == null
					&& (!(this.field_6660 instanceof class_1321) || ((class_1321)this.field_6660).method_6177() == ((class_1321)lv).method_6177())
					&& !lv.method_5722(this.field_6660.method_6065())) {
					if (this.field_6640 == null) {
						break;
					}

					boolean bl = false;

					for (Class<?> class_ : this.field_6640) {
						if (lv.getClass() == class_) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						break;
					}
				}
			}

			this.method_6319(lv, this.field_6660.method_6065());
		}
	}

	protected void method_6319(class_1308 arg, class_1309 arg2) {
		arg.method_5980(arg2);
	}
}
