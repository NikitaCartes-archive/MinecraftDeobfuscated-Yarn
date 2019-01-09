package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_111 extends class_120 {
	private static final Logger field_1038 = LogManager.getLogger();
	public static final class_20.class_21 field_1034 = class_20.class_21.field_88;
	private final String field_1035;
	private final class_20.class_21 field_1036;
	private final byte field_1037;
	private final int field_1032;
	private final boolean field_1033;

	private class_111(class_209[] args, String string, class_20.class_21 arg, byte b, int i, boolean bl) {
		super(args);
		this.field_1035 = string;
		this.field_1036 = arg;
		this.field_1037 = b;
		this.field_1032 = i;
		this.field_1033 = bl;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(class_181.field_1232);
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7909() != class_1802.field_8895) {
			return arg;
		} else {
			class_2338 lv = arg2.method_296(class_181.field_1232);
			if (lv != null) {
				class_3218 lv2 = arg2.method_299();
				class_2338 lv3 = lv2.method_8487(this.field_1035, lv, this.field_1032, this.field_1033);
				if (lv3 != null) {
					class_1799 lv4 = class_1806.method_8005(lv2, lv3.method_10263(), lv3.method_10260(), this.field_1037, true, true);
					class_1806.method_8002(lv2, lv4);
					class_22.method_110(lv4, lv3, "+", this.field_1036);
					lv4.method_7977(new class_2588("filled_map." + this.field_1035.toLowerCase(Locale.ROOT)));
					return lv4;
				}
			}

			return arg;
		}
	}

	public static class_111.class_112 method_492() {
		return new class_111.class_112();
	}

	public static class class_112 extends class_120.class_121<class_111.class_112> {
		private String field_1039 = "Buried_Treasure";
		private class_20.class_21 field_1042 = class_111.field_1034;
		private byte field_1043 = 2;
		private int field_1040 = 50;
		private boolean field_1041 = true;

		protected class_111.class_112 method_501() {
			return this;
		}

		public class_111.class_112 method_502(String string) {
			this.field_1039 = string;
			return this;
		}

		public class_111.class_112 method_499(class_20.class_21 arg) {
			this.field_1042 = arg;
			return this;
		}

		public class_111.class_112 method_500(byte b) {
			this.field_1043 = b;
			return this;
		}

		public class_111.class_112 method_503(boolean bl) {
			this.field_1041 = bl;
			return this;
		}

		@Override
		public class_117 method_515() {
			return new class_111(this.method_526(), this.field_1039, this.field_1042, this.field_1043, this.field_1040, this.field_1041);
		}
	}

	public static class class_113 extends class_120.class_123<class_111> {
		protected class_113() {
			super(new class_2960("exploration_map"), class_111.class);
		}

		public void method_505(JsonObject jsonObject, class_111 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			if (!arg.field_1035.equals("Buried_Treasure")) {
				jsonObject.add("destination", jsonSerializationContext.serialize(arg.field_1035));
			}

			if (arg.field_1036 != class_111.field_1034) {
				jsonObject.add("decoration", jsonSerializationContext.serialize(arg.field_1036.toString().toLowerCase(Locale.ROOT)));
			}

			if (arg.field_1037 != 2) {
				jsonObject.addProperty("zoom", arg.field_1037);
			}

			if (arg.field_1032 != 50) {
				jsonObject.addProperty("search_radius", arg.field_1032);
			}

			if (!arg.field_1033) {
				jsonObject.addProperty("skip_existing_chunks", arg.field_1033);
			}
		}

		public class_111 method_504(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			String string = jsonObject.has("destination") ? class_3518.method_15265(jsonObject, "destination") : "Buried_Treasure";
			string = class_3031.field_13557.containsKey(string.toLowerCase(Locale.ROOT)) ? string : "Buried_Treasure";
			String string2 = jsonObject.has("decoration") ? class_3518.method_15265(jsonObject, "decoration") : "mansion";
			class_20.class_21 lv = class_111.field_1034;

			try {
				lv = class_20.class_21.valueOf(string2.toUpperCase(Locale.ROOT));
			} catch (IllegalArgumentException var10) {
				class_111.field_1038.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + class_111.field_1034, string2);
			}

			byte b = class_3518.method_15271(jsonObject, "zoom", (byte)2);
			int i = class_3518.method_15282(jsonObject, "search_radius", 50);
			boolean bl = class_3518.method_15258(jsonObject, "skip_existing_chunks", true);
			return new class_111(args, string, lv, b, i, bl);
		}
	}
}
