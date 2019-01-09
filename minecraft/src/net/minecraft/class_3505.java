package net.minecraft;

public class class_3505 implements class_3302 {
	private final class_3493<class_2248> field_15612 = new class_3493<>(class_2378.field_11146, "tags/blocks", "block");
	private final class_3493<class_1792> field_15613 = new class_3493<>(class_2378.field_11142, "tags/items", "item");
	private final class_3493<class_3611> field_15614 = new class_3493<>(class_2378.field_11154, "tags/fluids", "fluid");
	private final class_3493<class_1299<?>> field_15615 = new class_3493<>(class_2378.field_11145, "tags/entity_types", "entity_type");

	public class_3493<class_2248> method_15202() {
		return this.field_15612;
	}

	public class_3493<class_1792> method_15201() {
		return this.field_15613;
	}

	public class_3493<class_3611> method_15205() {
		return this.field_15614;
	}

	public class_3493<class_1299<?>> method_15203() {
		return this.field_15615;
	}

	public void method_15206() {
		this.field_15612.method_15195();
		this.field_15613.method_15195();
		this.field_15614.method_15195();
		this.field_15615.method_15195();
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.method_15206();
		this.field_15612.method_15192(arg);
		this.field_15613.method_15192(arg);
		this.field_15614.method_15192(arg);
		this.field_15615.method_15192(arg);
		class_3481.method_15070(this.field_15612);
		class_3489.method_15103(this.field_15613);
		class_3486.method_15096(this.field_15614);
		class_3483.method_15078(this.field_15615);
	}

	public void method_15204(class_2540 arg) {
		this.field_15612.method_15137(arg);
		this.field_15613.method_15137(arg);
		this.field_15614.method_15137(arg);
		this.field_15615.method_15137(arg);
	}

	public static class_3505 method_15200(class_2540 arg) {
		class_3505 lv = new class_3505();
		lv.method_15202().method_15136(arg);
		lv.method_15201().method_15136(arg);
		lv.method_15205().method_15136(arg);
		lv.method_15203().method_15136(arg);
		return lv;
	}
}
