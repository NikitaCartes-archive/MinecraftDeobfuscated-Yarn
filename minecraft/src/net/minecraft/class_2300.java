package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2300 {
	private final int field_10822;
	private final boolean field_10830;
	private final boolean field_10829;
	private final Predicate<class_1297> field_10820;
	private final class_2096.class_2099 field_10825;
	private final Function<class_243, class_243> field_10823;
	@Nullable
	private final class_238 field_10824;
	private final BiConsumer<class_243, List<? extends class_1297>> field_10826;
	private final boolean field_10828;
	@Nullable
	private final String field_10831;
	@Nullable
	private final UUID field_10821;
	@Nullable
	private final class_1299<?> field_10832;
	private final boolean field_10827;

	public class_2300(
		int i,
		boolean bl,
		boolean bl2,
		Predicate<class_1297> predicate,
		class_2096.class_2099 arg,
		Function<class_243, class_243> function,
		@Nullable class_238 arg2,
		BiConsumer<class_243, List<? extends class_1297>> biConsumer,
		boolean bl3,
		@Nullable String string,
		@Nullable UUID uUID,
		@Nullable class_1299<?> arg3,
		boolean bl4
	) {
		this.field_10822 = i;
		this.field_10830 = bl;
		this.field_10829 = bl2;
		this.field_10820 = predicate;
		this.field_10825 = arg;
		this.field_10823 = function;
		this.field_10824 = arg2;
		this.field_10826 = biConsumer;
		this.field_10828 = bl3;
		this.field_10831 = string;
		this.field_10821 = uUID;
		this.field_10832 = arg3;
		this.field_10827 = bl4;
	}

	public int method_9815() {
		return this.field_10822;
	}

	public boolean method_9819() {
		return this.field_10830;
	}

	public boolean method_9820() {
		return this.field_10828;
	}

	public boolean method_9821() {
		return this.field_10829;
	}

	private void method_9818(class_2168 arg) throws CommandSyntaxException {
		if (this.field_10827 && !arg.method_9259(2)) {
			throw class_2186.field_9862.create();
		}
	}

	public class_1297 method_9809(class_2168 arg) throws CommandSyntaxException {
		this.method_9818(arg);
		List<? extends class_1297> list = this.method_9816(arg);
		if (list.isEmpty()) {
			throw class_2186.field_9863.create();
		} else if (list.size() > 1) {
			throw class_2186.field_9860.create();
		} else {
			return (class_1297)list.get(0);
		}
	}

	public List<? extends class_1297> method_9816(class_2168 arg) throws CommandSyntaxException {
		this.method_9818(arg);
		if (!this.field_10830) {
			return this.method_9813(arg);
		} else if (this.field_10831 != null) {
			class_3222 lv = arg.method_9211().method_3760().method_14566(this.field_10831);
			return (List<? extends class_1297>)(lv == null ? Collections.emptyList() : Lists.newArrayList(lv));
		} else if (this.field_10821 != null) {
			for (class_3218 lv2 : arg.method_9211().method_3738()) {
				class_1297 lv3 = lv2.method_14190(this.field_10821);
				if (lv3 != null) {
					return Lists.newArrayList(lv3);
				}
			}

			return Collections.emptyList();
		} else {
			class_243 lv4 = (class_243)this.field_10823.apply(arg.method_9222());
			Predicate<class_1297> predicate = this.method_9817(lv4);
			if (this.field_10828) {
				return (List<? extends class_1297>)(arg.method_9228() != null && predicate.test(arg.method_9228())
					? Lists.newArrayList(arg.method_9228())
					: Collections.emptyList());
			} else {
				List<class_1297> list = Lists.<class_1297>newArrayList();
				if (this.method_9821()) {
					this.method_9823(list, arg.method_9225(), lv4, predicate);
				} else {
					for (class_3218 lv5 : arg.method_9211().method_3738()) {
						this.method_9823(list, lv5, lv4, predicate);
					}
				}

				return this.method_9814(lv4, list);
			}
		}
	}

	private void method_9823(List<class_1297> list, class_3218 arg, class_243 arg2, Predicate<class_1297> predicate) {
		if (this.field_10824 != null) {
			list.addAll(arg.method_18023(this.field_10832, this.field_10824.method_997(arg2), predicate));
		} else {
			list.addAll(arg.method_18198(this.field_10832, predicate));
		}
	}

	public class_3222 method_9811(class_2168 arg) throws CommandSyntaxException {
		this.method_9818(arg);
		List<class_3222> list = this.method_9813(arg);
		if (list.size() != 1) {
			throw class_2186.field_9856.create();
		} else {
			return (class_3222)list.get(0);
		}
	}

	public List<class_3222> method_9813(class_2168 arg) throws CommandSyntaxException {
		this.method_9818(arg);
		if (this.field_10831 != null) {
			class_3222 lv = arg.method_9211().method_3760().method_14566(this.field_10831);
			return (List<class_3222>)(lv == null ? Collections.emptyList() : Lists.<class_3222>newArrayList(lv));
		} else if (this.field_10821 != null) {
			class_3222 lv = arg.method_9211().method_3760().method_14602(this.field_10821);
			return (List<class_3222>)(lv == null ? Collections.emptyList() : Lists.<class_3222>newArrayList(lv));
		} else {
			class_243 lv2 = (class_243)this.field_10823.apply(arg.method_9222());
			Predicate<class_1297> predicate = this.method_9817(lv2);
			if (this.field_10828) {
				if (arg.method_9228() instanceof class_3222) {
					class_3222 lv3 = (class_3222)arg.method_9228();
					if (predicate.test(lv3)) {
						return Lists.<class_3222>newArrayList(lv3);
					}
				}

				return Collections.emptyList();
			} else {
				List<class_3222> list;
				if (this.method_9821()) {
					list = arg.method_9225().method_18766(predicate::test);
				} else {
					list = Lists.<class_3222>newArrayList();

					for (class_3222 lv4 : arg.method_9211().method_3760().method_14571()) {
						if (predicate.test(lv4)) {
							list.add(lv4);
						}
					}
				}

				return this.method_9814(lv2, list);
			}
		}
	}

	private Predicate<class_1297> method_9817(class_243 arg) {
		Predicate<class_1297> predicate = this.field_10820;
		if (this.field_10824 != null) {
			class_238 lv = this.field_10824.method_997(arg);
			predicate = predicate.and(arg2 -> lv.method_994(arg2.method_5829()));
		}

		if (!this.field_10825.method_9041()) {
			predicate = predicate.and(arg2 -> this.field_10825.method_9045(arg2.method_5707(arg)));
		}

		return predicate;
	}

	private <T extends class_1297> List<T> method_9814(class_243 arg, List<T> list) {
		if (list.size() > 1) {
			this.field_10826.accept(arg, list);
		}

		return list.subList(0, Math.min(this.field_10822, list.size()));
	}

	public static class_2561 method_9822(List<? extends class_1297> list) {
		return class_2564.method_10884(list, class_1297::method_5476);
	}
}
