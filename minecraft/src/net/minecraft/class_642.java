package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_642 {
	public String field_3752;
	public String field_3761;
	public String field_3753;
	public String field_3757;
	public long field_3758;
	public int field_3756 = class_155.method_16673().getProtocolVersion();
	public String field_3760 = class_155.method_16673().getName();
	public boolean field_3754;
	public String field_3762;
	private class_642.class_643 field_3755 = class_642.class_643.field_3767;
	private String field_3759;
	private boolean field_3763;

	public class_642(String string, String string2, boolean bl) {
		this.field_3752 = string;
		this.field_3761 = string2;
		this.field_3763 = bl;
	}

	public class_2487 method_2992() {
		class_2487 lv = new class_2487();
		lv.method_10582("name", this.field_3752);
		lv.method_10582("ip", this.field_3761);
		if (this.field_3759 != null) {
			lv.method_10582("icon", this.field_3759);
		}

		if (this.field_3755 == class_642.class_643.field_3768) {
			lv.method_10556("acceptTextures", true);
		} else if (this.field_3755 == class_642.class_643.field_3764) {
			lv.method_10556("acceptTextures", false);
		}

		return lv;
	}

	public class_642.class_643 method_2990() {
		return this.field_3755;
	}

	public void method_2995(class_642.class_643 arg) {
		this.field_3755 = arg;
	}

	public static class_642 method_2993(class_2487 arg) {
		class_642 lv = new class_642(arg.method_10558("name"), arg.method_10558("ip"), false);
		if (arg.method_10573("icon", 8)) {
			lv.method_2989(arg.method_10558("icon"));
		}

		if (arg.method_10573("acceptTextures", 1)) {
			if (arg.method_10577("acceptTextures")) {
				lv.method_2995(class_642.class_643.field_3768);
			} else {
				lv.method_2995(class_642.class_643.field_3764);
			}
		} else {
			lv.method_2995(class_642.class_643.field_3767);
		}

		return lv;
	}

	@Nullable
	public String method_2991() {
		return this.field_3759;
	}

	public void method_2989(@Nullable String string) {
		this.field_3759 = string;
	}

	public boolean method_2994() {
		return this.field_3763;
	}

	public void method_2996(class_642 arg) {
		this.field_3761 = arg.field_3761;
		this.field_3752 = arg.field_3752;
		this.method_2995(arg.method_2990());
		this.field_3759 = arg.field_3759;
		this.field_3763 = arg.field_3763;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_643 {
		field_3768("enabled"),
		field_3764("disabled"),
		field_3767("prompt");

		private final class_2561 field_3765;

		private class_643(String string2) {
			this.field_3765 = new class_2588("addServer.resourcePack." + string2);
		}

		public class_2561 method_2997() {
			return this.field_3765;
		}
	}
}
