package net.minecraft;

public class class_2968 extends class_1750 {
	private final class_2350 field_13362;

	public class_2968(class_1937 arg, class_2338 arg2, class_2350 arg3, class_1799 arg4, class_2350 arg5) {
		super(
			arg,
			null,
			arg4,
			new class_3965(new class_243((double)arg2.method_10263() + 0.5, (double)arg2.method_10264(), (double)arg2.method_10260() + 0.5), arg5, arg2, false)
		);
		this.field_13362 = arg3;
	}

	@Override
	public class_2338 method_8037() {
		return this.field_17543.method_17777();
	}

	@Override
	public boolean method_7716() {
		return this.field_8945.method_8320(this.field_17543.method_17777()).method_11587(this);
	}

	@Override
	public boolean method_7717() {
		return this.method_7716();
	}

	@Override
	public class_2350 method_7715() {
		return class_2350.field_11033;
	}

	@Override
	public class_2350[] method_7718() {
		switch (this.field_13362) {
			case field_11033:
			default:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11043, class_2350.field_11034, class_2350.field_11035, class_2350.field_11039, class_2350.field_11036
				};
			case field_11036:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11036, class_2350.field_11043, class_2350.field_11034, class_2350.field_11035, class_2350.field_11039
				};
			case field_11043:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11043, class_2350.field_11034, class_2350.field_11039, class_2350.field_11036, class_2350.field_11035
				};
			case field_11035:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11036, class_2350.field_11043
				};
			case field_11039:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11039, class_2350.field_11035, class_2350.field_11036, class_2350.field_11043, class_2350.field_11034
				};
			case field_11034:
				return new class_2350[]{
					class_2350.field_11033, class_2350.field_11034, class_2350.field_11035, class_2350.field_11036, class_2350.field_11043, class_2350.field_11039
				};
		}
	}

	@Override
	public class_2350 method_8042() {
		return this.field_13362.method_10166() == class_2350.class_2351.field_11052 ? class_2350.field_11043 : this.field_13362;
	}

	@Override
	public boolean method_8046() {
		return false;
	}

	@Override
	public float method_8044() {
		return (float)(this.field_13362.method_10161() * 90);
	}
}
