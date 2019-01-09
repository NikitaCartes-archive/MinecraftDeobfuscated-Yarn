package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_637 implements class_2172 {
	private final class_634 field_3722;
	private final class_310 field_3725;
	private int field_3724 = -1;
	private CompletableFuture<Suggestions> field_3723;

	public class_637(class_634 arg, class_310 arg2) {
		this.field_3722 = arg;
		this.field_3725 = arg2;
	}

	@Override
	public Collection<String> method_9262() {
		List<String> list = Lists.<String>newArrayList();

		for (class_640 lv : this.field_3722.method_2880()) {
			list.add(lv.method_2966().getName());
		}

		return list;
	}

	@Override
	public Collection<String> method_9269() {
		return (Collection<String>)(this.field_3725.field_1765 != null && this.field_3725.field_1765.field_1330 == class_239.class_240.field_1331
			? Collections.singleton(this.field_3725.field_1765.field_1326.method_5845())
			: Collections.emptyList());
	}

	@Override
	public Collection<String> method_9267() {
		return this.field_3722.method_2890().method_8428().method_1196();
	}

	@Override
	public Collection<class_2960> method_9254() {
		return this.field_3725.method_1483().method_4864();
	}

	@Override
	public Collection<class_2960> method_9273() {
		return this.field_3722.method_2877().method_8127();
	}

	@Override
	public boolean method_9259(int i) {
		class_746 lv = this.field_3725.field_1724;
		return lv != null ? lv.method_5687(i) : i == 0;
	}

	@Override
	public CompletableFuture<Suggestions> method_9261(CommandContext<class_2172> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (this.field_3723 != null) {
			this.field_3723.cancel(false);
		}

		this.field_3723 = new CompletableFuture();
		int i = ++this.field_3724;
		this.field_3722.method_2883(new class_2805(i, commandContext.getInput()));
		return this.field_3723;
	}

	private static String method_2929(double d) {
		return String.format(Locale.ROOT, "%.2f", d);
	}

	private static String method_2930(int i) {
		return Integer.toString(i);
	}

	@Override
	public Collection<class_2172.class_2173> method_9274(boolean bl) {
		if (this.field_3725.field_1765 == null || this.field_3725.field_1765.field_1330 != class_239.class_240.field_1332) {
			return Collections.singleton(class_2172.class_2173.field_9838);
		} else if (bl) {
			class_243 lv = this.field_3725.field_1765.field_1329;
			return Collections.singleton(new class_2172.class_2173(method_2929(lv.field_1352), method_2929(lv.field_1351), method_2929(lv.field_1350)));
		} else {
			class_2338 lv2 = this.field_3725.field_1765.method_1015();
			return Collections.singleton(new class_2172.class_2173(method_2930(lv2.method_10263()), method_2930(lv2.method_10264()), method_2930(lv2.method_10260())));
		}
	}

	public void method_2931(int i, Suggestions suggestions) {
		if (i == this.field_3724) {
			this.field_3723.complete(suggestions);
			this.field_3723 = null;
			this.field_3724 = -1;
		}
	}
}
