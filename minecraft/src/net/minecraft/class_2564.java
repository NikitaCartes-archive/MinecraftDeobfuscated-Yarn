package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_2564 {
	public static class_2561 method_10889(class_2561 arg, class_2583 arg2) {
		if (arg2.method_10967()) {
			return arg;
		} else {
			return arg.method_10866().method_10967() ? arg.method_10862(arg2.method_10976()) : new class_2585("").method_10852(arg).method_10862(arg2.method_10976());
		}
	}

	public static class_2561 method_10881(@Nullable class_2168 arg, class_2561 arg2, @Nullable class_1297 arg3) throws CommandSyntaxException {
		class_2561 lv = arg2 instanceof class_2566 ? ((class_2566)arg2).method_10890(arg, arg3) : arg2.method_10850();

		for (class_2561 lv2 : arg2.method_10855()) {
			lv.method_10852(method_10881(arg, lv2, arg3));
		}

		return method_10889(lv, arg2.method_10866());
	}

	public static class_2561 method_10882(GameProfile gameProfile) {
		if (gameProfile.getName() != null) {
			return new class_2585(gameProfile.getName());
		} else {
			return gameProfile.getId() != null ? new class_2585(gameProfile.getId().toString()) : new class_2585("(unknown)");
		}
	}

	public static class_2561 method_10888(Collection<String> collection) {
		return method_10887(collection, string -> new class_2585(string).method_10854(class_124.field_1060));
	}

	public static <T extends Comparable<T>> class_2561 method_10887(Collection<T> collection, Function<T, class_2561> function) {
		if (collection.isEmpty()) {
			return new class_2585("");
		} else if (collection.size() == 1) {
			return (class_2561)function.apply(collection.iterator().next());
		} else {
			List<T> list = Lists.<T>newArrayList(collection);
			list.sort(Comparable::compareTo);
			return method_10884(collection, function);
		}
	}

	public static <T> class_2561 method_10884(Collection<T> collection, Function<T, class_2561> function) {
		if (collection.isEmpty()) {
			return new class_2585("");
		} else if (collection.size() == 1) {
			return (class_2561)function.apply(collection.iterator().next());
		} else {
			class_2561 lv = new class_2585("");
			boolean bl = true;

			for (T object : collection) {
				if (!bl) {
					lv.method_10852(new class_2585(", ").method_10854(class_124.field_1080));
				}

				lv.method_10852((class_2561)function.apply(object));
				bl = false;
			}

			return lv;
		}
	}

	public static class_2561 method_10885(class_2561 arg) {
		return new class_2585("[").method_10852(arg).method_10864("]");
	}

	public static class_2561 method_10883(Message message) {
		return (class_2561)(message instanceof class_2561 ? (class_2561)message : new class_2585(message.getString()));
	}
}
