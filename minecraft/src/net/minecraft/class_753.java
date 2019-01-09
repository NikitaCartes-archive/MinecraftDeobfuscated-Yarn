package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum class_753 {
	field_3965(
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3972)
	),
	field_3960(
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3969)
	),
	field_3962(
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3969)
	),
	field_3963(
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3972)
	),
	field_3966(
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3968, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3967, class_753.class_754.field_3971, class_753.class_754.field_3972)
	),
	field_3961(
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3972),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3968, class_753.class_754.field_3969),
		new class_753.class_755(class_753.class_754.field_3970, class_753.class_754.field_3971, class_753.class_754.field_3969)
	);

	private static final class_753[] field_3958 = class_156.method_654(new class_753[6], args -> {
		args[class_753.class_754.field_3968] = field_3965;
		args[class_753.class_754.field_3971] = field_3960;
		args[class_753.class_754.field_3969] = field_3962;
		args[class_753.class_754.field_3972] = field_3963;
		args[class_753.class_754.field_3967] = field_3966;
		args[class_753.class_754.field_3970] = field_3961;
	});
	private final class_753.class_755[] field_3959;

	public static class_753 method_3163(class_2350 arg) {
		return field_3958[arg.method_10146()];
	}

	private class_753(class_753.class_755... args) {
		this.field_3959 = args;
	}

	public class_753.class_755 method_3162(int i) {
		return this.field_3959[i];
	}

	@Environment(EnvType.CLIENT)
	public static final class class_754 {
		public static final int field_3972 = class_2350.field_11035.method_10146();
		public static final int field_3971 = class_2350.field_11036.method_10146();
		public static final int field_3970 = class_2350.field_11034.method_10146();
		public static final int field_3969 = class_2350.field_11043.method_10146();
		public static final int field_3968 = class_2350.field_11033.method_10146();
		public static final int field_3967 = class_2350.field_11039.method_10146();
	}

	@Environment(EnvType.CLIENT)
	public static class class_755 {
		public final int field_3975;
		public final int field_3974;
		public final int field_3973;

		private class_755(int i, int j, int k) {
			this.field_3975 = i;
			this.field_3974 = j;
			this.field_3973 = k;
		}
	}
}
