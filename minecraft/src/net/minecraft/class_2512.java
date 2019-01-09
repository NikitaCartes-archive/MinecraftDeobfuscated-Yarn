package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class class_2512 {
	private static final Logger field_11582 = LogManager.getLogger();

	@Nullable
	public static GameProfile method_10683(class_2487 arg) {
		String string = null;
		String string2 = null;
		if (arg.method_10573("Name", 8)) {
			string = arg.method_10558("Name");
		}

		if (arg.method_10573("Id", 8)) {
			string2 = arg.method_10558("Id");
		}

		try {
			UUID uUID;
			try {
				uUID = UUID.fromString(string2);
			} catch (Throwable var12) {
				uUID = null;
			}

			GameProfile gameProfile = new GameProfile(uUID, string);
			if (arg.method_10573("Properties", 10)) {
				class_2487 lv = arg.method_10562("Properties");

				for (String string3 : lv.method_10541()) {
					class_2499 lv2 = lv.method_10554(string3, 10);

					for (int i = 0; i < lv2.size(); i++) {
						class_2487 lv3 = lv2.method_10602(i);
						String string4 = lv3.method_10558("Value");
						if (lv3.method_10573("Signature", 8)) {
							gameProfile.getProperties().put(string3, new Property(string3, string4, lv3.method_10558("Signature")));
						} else {
							gameProfile.getProperties().put(string3, new Property(string3, string4));
						}
					}
				}
			}

			return gameProfile;
		} catch (Throwable var13) {
			return null;
		}
	}

	public static class_2487 method_10684(class_2487 arg, GameProfile gameProfile) {
		if (!class_3544.method_15438(gameProfile.getName())) {
			arg.method_10582("Name", gameProfile.getName());
		}

		if (gameProfile.getId() != null) {
			arg.method_10582("Id", gameProfile.getId().toString());
		}

		if (!gameProfile.getProperties().isEmpty()) {
			class_2487 lv = new class_2487();

			for (String string : gameProfile.getProperties().keySet()) {
				class_2499 lv2 = new class_2499();

				for (Property property : gameProfile.getProperties().get(string)) {
					class_2487 lv3 = new class_2487();
					lv3.method_10582("Value", property.getValue());
					if (property.hasSignature()) {
						lv3.method_10582("Signature", property.getSignature());
					}

					lv2.method_10606(lv3);
				}

				lv.method_10566(string, lv2);
			}

			arg.method_10566("Properties", lv);
		}

		return arg;
	}

	@VisibleForTesting
	public static boolean method_10687(@Nullable class_2520 arg, @Nullable class_2520 arg2, boolean bl) {
		if (arg == arg2) {
			return true;
		} else if (arg == null) {
			return true;
		} else if (arg2 == null) {
			return false;
		} else if (!arg.getClass().equals(arg2.getClass())) {
			return false;
		} else if (arg instanceof class_2487) {
			class_2487 lv = (class_2487)arg;
			class_2487 lv2 = (class_2487)arg2;

			for (String string : lv.method_10541()) {
				class_2520 lv3 = lv.method_10580(string);
				if (!method_10687(lv3, lv2.method_10580(string), bl)) {
					return false;
				}
			}

			return true;
		} else if (arg instanceof class_2499 && bl) {
			class_2499 lv4 = (class_2499)arg;
			class_2499 lv5 = (class_2499)arg2;
			if (lv4.isEmpty()) {
				return lv5.isEmpty();
			} else {
				for (int i = 0; i < lv4.size(); i++) {
					class_2520 lv6 = lv4.method_10534(i);
					boolean bl2 = false;

					for (int j = 0; j < lv5.size(); j++) {
						if (method_10687(lv6, lv5.method_10534(j), bl)) {
							bl2 = true;
							break;
						}
					}

					if (!bl2) {
						return false;
					}
				}

				return true;
			}
		} else {
			return arg.equals(arg2);
		}
	}

	public static class_2487 method_10689(UUID uUID) {
		class_2487 lv = new class_2487();
		lv.method_10544("M", uUID.getMostSignificantBits());
		lv.method_10544("L", uUID.getLeastSignificantBits());
		return lv;
	}

	public static UUID method_10690(class_2487 arg) {
		return new UUID(arg.method_10537("M"), arg.method_10537("L"));
	}

	public static class_2338 method_10691(class_2487 arg) {
		return new class_2338(arg.method_10550("X"), arg.method_10550("Y"), arg.method_10550("Z"));
	}

	public static class_2487 method_10692(class_2338 arg) {
		class_2487 lv = new class_2487();
		lv.method_10569("X", arg.method_10263());
		lv.method_10569("Y", arg.method_10264());
		lv.method_10569("Z", arg.method_10260());
		return lv;
	}

	public static class_2680 method_10681(class_2487 arg) {
		if (!arg.method_10573("Name", 8)) {
			return class_2246.field_10124.method_9564();
		} else {
			class_2248 lv = class_2378.field_11146.method_10223(new class_2960(arg.method_10558("Name")));
			class_2680 lv2 = lv.method_9564();
			if (arg.method_10573("Properties", 10)) {
				class_2487 lv3 = arg.method_10562("Properties");
				class_2689<class_2248, class_2680> lv4 = lv.method_9595();

				for (String string : lv3.method_10541()) {
					class_2769<?> lv5 = lv4.method_11663(string);
					if (lv5 != null) {
						lv2 = method_10682(lv2, lv5, string, lv3, arg);
					}
				}
			}

			return lv2;
		}
	}

	private static <S extends class_2688<S>, T extends Comparable<T>> S method_10682(S arg, class_2769<T> arg2, String string, class_2487 arg3, class_2487 arg4) {
		Optional<T> optional = arg2.method_11900(arg3.method_10558(string));
		if (optional.isPresent()) {
			return arg.method_11657(arg2, (Comparable)optional.get());
		} else {
			field_11582.warn("Unable to read property: {} with value: {} for blockstate: {}", string, arg3.method_10558(string), arg4.toString());
			return arg;
		}
	}

	public static class_2487 method_10686(class_2680 arg) {
		class_2487 lv = new class_2487();
		lv.method_10582("Name", class_2378.field_11146.method_10221(arg.method_11614()).toString());
		ImmutableMap<class_2769<?>, Comparable<?>> immutableMap = arg.method_11656();
		if (!immutableMap.isEmpty()) {
			class_2487 lv2 = new class_2487();

			for (Entry<class_2769<?>, Comparable<?>> entry : immutableMap.entrySet()) {
				class_2769<?> lv3 = (class_2769<?>)entry.getKey();
				lv2.method_10582(lv3.method_11899(), method_10685(lv3, (Comparable<?>)entry.getValue()));
			}

			lv.method_10566("Properties", lv2);
		}

		return lv;
	}

	private static <T extends Comparable<T>> String method_10685(class_2769<T> arg, Comparable<?> comparable) {
		return arg.method_11901((T)comparable);
	}

	public static class_2487 method_10688(DataFixer dataFixer, TypeReference typeReference, class_2487 arg, int i) {
		return method_10693(dataFixer, typeReference, arg, i, class_155.method_16673().getWorldVersion());
	}

	public static class_2487 method_10693(DataFixer dataFixer, TypeReference typeReference, class_2487 arg, int i, int j) {
		return (class_2487)dataFixer.update(typeReference, new Dynamic<>(class_2509.field_11560, arg), i, j).getValue();
	}
}
