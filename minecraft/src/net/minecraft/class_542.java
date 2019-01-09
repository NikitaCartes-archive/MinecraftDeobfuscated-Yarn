package net.minecraft;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_542 {
	public final class_542.class_547 field_3278;
	public final class_543 field_3279;
	public final class_542.class_544 field_3277;
	public final class_542.class_545 field_3280;
	public final class_542.class_546 field_3281;

	public class_542(class_542.class_547 arg, class_543 arg2, class_542.class_544 arg3, class_542.class_545 arg4, class_542.class_546 arg5) {
		this.field_3278 = arg;
		this.field_3279 = arg2;
		this.field_3277 = arg3;
		this.field_3280 = arg4;
		this.field_3281 = arg5;
	}

	@Environment(EnvType.CLIENT)
	public static class class_544 {
		public final File field_3287;
		public final File field_3290;
		public final File field_3289;
		public final String field_3288;

		public class_544(File file, File file2, File file3, @Nullable String string) {
			this.field_3287 = file;
			this.field_3290 = file2;
			this.field_3289 = file3;
			this.field_3288 = string;
		}

		public class_1064 method_2788() {
			return (class_1064)(this.field_3288 == null ? new class_1067(this.field_3289) : new class_1064(this.field_3289, this.field_3288));
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_545 {
		public final boolean field_3292;
		public final String field_3293;
		public final String field_3291;

		public class_545(boolean bl, String string, String string2) {
			this.field_3292 = bl;
			this.field_3293 = string;
			this.field_3291 = string2;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_546 {
		public final String field_3294;
		public final int field_3295;

		public class_546(String string, int i) {
			this.field_3294 = string;
			this.field_3295 = i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_547 {
		public final class_320 field_3299;
		public final PropertyMap field_3298;
		public final PropertyMap field_3297;
		public final Proxy field_3296;

		public class_547(class_320 arg, PropertyMap propertyMap, PropertyMap propertyMap2, Proxy proxy) {
			this.field_3299 = arg;
			this.field_3298 = propertyMap;
			this.field_3297 = propertyMap2;
			this.field_3296 = proxy;
		}
	}
}
