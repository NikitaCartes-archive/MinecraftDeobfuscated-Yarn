package net.minecraft;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public abstract class class_1425 extends class_1422 {
	private class_1425 field_6734;
	private int field_6733 = 1;

	public class_1425(class_1299<? extends class_1425> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(5, new class_1349(this));
	}

	@Override
	public int method_5945() {
		return this.method_6465();
	}

	public int method_6465() {
		return super.method_5945();
	}

	@Override
	protected boolean method_6456() {
		return !this.method_6470();
	}

	public boolean method_6470() {
		return this.field_6734 != null && this.field_6734.method_5805();
	}

	public class_1425 method_6461(class_1425 arg) {
		this.field_6734 = arg;
		arg.method_6462();
		return arg;
	}

	public void method_6466() {
		this.field_6734.method_6459();
		this.field_6734 = null;
	}

	private void method_6462() {
		this.field_6733++;
	}

	private void method_6459() {
		this.field_6733--;
	}

	public boolean method_6469() {
		return this.method_6467() && this.field_6733 < this.method_6465();
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.method_6467() && this.field_6002.field_9229.nextInt(200) == 1) {
			List<class_1422> list = this.field_6002.method_18467(this.getClass(), this.method_5829().method_1009(8.0, 8.0, 8.0));
			if (list.size() <= 1) {
				this.field_6733 = 1;
			}
		}
	}

	public boolean method_6467() {
		return this.field_6733 > 1;
	}

	public boolean method_6464() {
		return this.method_5858(this.field_6734) <= 121.0;
	}

	public void method_6463() {
		if (this.method_6470()) {
			this.method_5942().method_6335(this.field_6734, 1.0);
		}
	}

	public void method_6468(Stream<class_1425> stream) {
		stream.limit((long)(this.method_6465() - this.field_6733)).filter(arg -> arg != this).forEach(arg -> arg.method_6461(this));
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg4 == null) {
			arg4 = new class_1425.class_1426(this);
		} else {
			this.method_6461(((class_1425.class_1426)arg4).field_6735);
		}

		return arg4;
	}

	public static class class_1426 implements class_1315 {
		public final class_1425 field_6735;

		public class_1426(class_1425 arg) {
			this.field_6735 = arg;
		}
	}
}
