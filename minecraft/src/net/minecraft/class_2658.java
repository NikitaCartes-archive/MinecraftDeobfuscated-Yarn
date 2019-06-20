package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2658 implements class_2596<class_2602> {
	public static final class_2960 field_12158 = new class_2960("brand");
	public static final class_2960 field_12161 = new class_2960("debug/path");
	public static final class_2960 field_12157 = new class_2960("debug/neighbors_update");
	public static final class_2960 field_12156 = new class_2960("debug/caves");
	public static final class_2960 field_12163 = new class_2960("debug/structures");
	public static final class_2960 field_12164 = new class_2960("debug/worldgen_attempt");
	public static final class_2960 field_18957 = new class_2960("debug/poi_ticket_count");
	public static final class_2960 field_18958 = new class_2960("debug/poi_added");
	public static final class_2960 field_18959 = new class_2960("debug/poi_removed");
	public static final class_2960 field_18960 = new class_2960("debug/village_sections");
	public static final class_2960 field_18799 = new class_2960("debug/goal_selector");
	public static final class_2960 field_18800 = new class_2960("debug/brain");
	public static final class_2960 field_19331 = new class_2960("debug/raids");
	private class_2960 field_12165;
	private class_2540 field_12162;

	public class_2658() {
	}

	public class_2658(class_2960 arg, class_2540 arg2) {
		this.field_12165 = arg;
		this.field_12162 = arg2;
		if (arg2.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12165 = arg.method_10810();
		int i = arg.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.field_12162 = new class_2540(arg.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10812(this.field_12165);
		arg.writeBytes(this.field_12162.copy());
	}

	public void method_11457(class_2602 arg) {
		arg.method_11152(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2960 method_11456() {
		return this.field_12165;
	}

	@Environment(EnvType.CLIENT)
	public class_2540 method_11458() {
		return new class_2540(this.field_12162.copy());
	}
}
