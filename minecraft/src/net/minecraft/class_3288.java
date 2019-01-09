package net.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3288 {
	private static final Logger field_14279 = LogManager.getLogger();
	private static final class_3272 field_14276 = new class_3272(
		new class_2588("resourcePack.broken_assets").method_10856(new class_124[]{class_124.field_1061, class_124.field_1056}),
		class_155.method_16673().getPackVersion()
	);
	private final String field_14272;
	private final Supplier<class_3262> field_14273;
	private final class_2561 field_14274;
	private final class_2561 field_14275;
	private final class_3281 field_14278;
	private final class_3288.class_3289 field_14277;
	private final boolean field_14271;
	private final boolean field_14270;

	@Nullable
	public static <T extends class_3288> T method_14456(
		String string, boolean bl, Supplier<class_3262> supplier, class_3288.class_3290<T> arg, class_3288.class_3289 arg2
	) {
		try {
			class_3262 lv = (class_3262)supplier.get();
			Throwable var6 = null;

			class_3288 var8;
			try {
				class_3272 lv2 = lv.method_14407(class_3272.field_14202);
				if (bl && lv2 == null) {
					field_14279.error(
						"Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!"
					);
					lv2 = field_14276;
				}

				if (lv2 == null) {
					field_14279.warn("Couldn't find pack meta for pack {}", string);
					return null;
				}

				var8 = arg.create(string, bl, supplier, lv, lv2, arg2);
			} catch (Throwable var19) {
				var6 = var19;
				throw var19;
			} finally {
				if (lv != null) {
					if (var6 != null) {
						try {
							lv.close();
						} catch (Throwable var18) {
							var6.addSuppressed(var18);
						}
					} else {
						lv.close();
					}
				}
			}

			return (T)var8;
		} catch (IOException var21) {
			field_14279.warn("Couldn't get pack info for: {}", var21.toString());
			return null;
		}
	}

	public class_3288(
		String string, boolean bl, Supplier<class_3262> supplier, class_2561 arg, class_2561 arg2, class_3281 arg3, class_3288.class_3289 arg4, boolean bl2
	) {
		this.field_14272 = string;
		this.field_14273 = supplier;
		this.field_14274 = arg;
		this.field_14275 = arg2;
		this.field_14278 = arg3;
		this.field_14271 = bl;
		this.field_14277 = arg4;
		this.field_14270 = bl2;
	}

	public class_3288(String string, boolean bl, Supplier<class_3262> supplier, class_3262 arg, class_3272 arg2, class_3288.class_3289 arg3) {
		this(string, bl, supplier, new class_2585(arg.method_14409()), arg2.method_14423(), class_3281.method_14436(arg2.method_14424()), arg3, false);
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_14457() {
		return this.field_14274;
	}

	@Environment(EnvType.CLIENT)
	public class_2561 method_14459() {
		return this.field_14275;
	}

	public class_2561 method_14461(boolean bl) {
		return class_2564.method_10885(new class_2585(this.field_14272))
			.method_10859(
				arg -> arg.method_10977(bl ? class_124.field_1060 : class_124.field_1061)
						.method_10975(StringArgumentType.escapeIfRequired(this.field_14272))
						.method_10949(
							new class_2568(class_2568.class_2569.field_11762, new class_2585("").method_10852(this.field_14274).method_10864("\n").method_10852(this.field_14275))
						)
			);
	}

	public class_3281 method_14460() {
		return this.field_14278;
	}

	public class_3262 method_14458() {
		return (class_3262)this.field_14273.get();
	}

	public String method_14463() {
		return this.field_14272;
	}

	public boolean method_14464() {
		return this.field_14271;
	}

	public boolean method_14465() {
		return this.field_14270;
	}

	public class_3288.class_3289 method_14466() {
		return this.field_14277;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_3288)) {
			return false;
		} else {
			class_3288 lv = (class_3288)object;
			return this.field_14272.equals(lv.field_14272);
		}
	}

	public int hashCode() {
		return this.field_14272.hashCode();
	}

	public static enum class_3289 {
		field_14280,
		field_14281;

		public <T, P extends class_3288> int method_14468(List<T> list, T object, Function<T, P> function, boolean bl) {
			class_3288.class_3289 lv = bl ? this.method_14467() : this;
			if (lv == field_14281) {
				int i;
				for (i = 0; i < list.size(); i++) {
					P lv2 = (P)function.apply(list.get(i));
					if (!lv2.method_14465() || lv2.method_14466() != this) {
						break;
					}
				}

				list.add(i, object);
				return i;
			} else {
				int i;
				for (i = list.size() - 1; i >= 0; i--) {
					P lv2 = (P)function.apply(list.get(i));
					if (!lv2.method_14465() || lv2.method_14466() != this) {
						break;
					}
				}

				list.add(i + 1, object);
				return i + 1;
			}
		}

		public class_3288.class_3289 method_14467() {
			return this == field_14280 ? field_14281 : field_14280;
		}
	}

	@FunctionalInterface
	public interface class_3290<T extends class_3288> {
		@Nullable
		T create(String string, boolean bl, Supplier<class_3262> supplier, class_3262 arg, class_3272 arg2, class_3288.class_3289 arg3);
	}
}
