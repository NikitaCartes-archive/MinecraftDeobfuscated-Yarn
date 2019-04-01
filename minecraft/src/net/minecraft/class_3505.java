package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class class_3505 implements class_3302 {
	private final class_3493<class_2248> field_15612 = new class_3493<>(class_2378.field_11146, "tags/blocks", "block");
	private final class_3493<class_1792> field_15613 = new class_3493<>(class_2378.field_11142, "tags/items", "item");
	private final class_3493<class_3611> field_15614 = new class_3493<>(class_2378.field_11154, "tags/fluids", "fluid");
	private final class_3493<class_1299<?>> field_15615 = new class_3493<>(class_2378.field_11145, "tags/entity_types", "entity_type");

	public class_3493<class_2248> method_15202() {
		return this.field_15612;
	}

	public class_3493<class_1792> method_15201() {
		return this.field_15613;
	}

	public class_3493<class_3611> method_15205() {
		return this.field_15614;
	}

	public class_3493<class_1299<?>> method_15203() {
		return this.field_15615;
	}

	public void method_15206() {
		this.field_15612.method_15195();
		this.field_15613.method_15195();
		this.field_15614.method_15195();
		this.field_15615.method_15195();
	}

	public void method_15204(class_2540 arg) {
		this.field_15612.method_15137(arg);
		this.field_15613.method_15137(arg);
		this.field_15614.method_15137(arg);
		this.field_15615.method_15137(arg);
	}

	public static class_3505 method_15200(class_2540 arg) {
		class_3505 lv = new class_3505();
		lv.method_15202().method_15136(arg);
		lv.method_15201().method_15136(arg);
		lv.method_15205().method_15136(arg);
		lv.method_15203().method_15136(arg);
		return lv;
	}

	@Override
	public CompletableFuture<Void> reload(class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2) {
		CompletableFuture<Map<class_2960, class_3494.class_3495<class_2248>>> completableFuture = this.field_15612.method_15192(arg2, executor);
		CompletableFuture<Map<class_2960, class_3494.class_3495<class_1792>>> completableFuture2 = this.field_15613.method_15192(arg2, executor);
		CompletableFuture<Map<class_2960, class_3494.class_3495<class_3611>>> completableFuture3 = this.field_15614.method_15192(arg2, executor);
		CompletableFuture<Map<class_2960, class_3494.class_3495<class_1299<?>>>> completableFuture4 = this.field_15615.method_15192(arg2, executor);
		return completableFuture.thenCombine(completableFuture2, Pair::of)
			.thenCombine(
				completableFuture3.thenCombine(completableFuture4, Pair::of),
				(pair, pair2) -> new class_3505.class_4015(
						(Map<class_2960, class_3494.class_3495<class_2248>>)pair.getFirst(),
						(Map<class_2960, class_3494.class_3495<class_1792>>)pair.getSecond(),
						(Map<class_2960, class_3494.class_3495<class_3611>>)pair2.getFirst(),
						(Map<class_2960, class_3494.class_3495<class_1299<?>>>)pair2.getSecond()
					)
			)
			.thenCompose(arg::method_18352)
			.thenAcceptAsync(argx -> {
				this.method_15206();
				this.field_15612.method_18242(argx.field_17938);
				this.field_15613.method_18242(argx.field_17939);
				this.field_15614.method_18242(argx.field_17940);
				this.field_15615.method_18242(argx.field_17941);
				class_3481.method_15070(this.field_15612);
				class_3489.method_15103(this.field_15613);
				class_3486.method_15096(this.field_15614);
				class_3483.method_15078(this.field_15615);
			}, executor2);
	}

	public static class class_4015 {
		final Map<class_2960, class_3494.class_3495<class_2248>> field_17938;
		final Map<class_2960, class_3494.class_3495<class_1792>> field_17939;
		final Map<class_2960, class_3494.class_3495<class_3611>> field_17940;
		final Map<class_2960, class_3494.class_3495<class_1299<?>>> field_17941;

		public class_4015(
			Map<class_2960, class_3494.class_3495<class_2248>> map,
			Map<class_2960, class_3494.class_3495<class_1792>> map2,
			Map<class_2960, class_3494.class_3495<class_3611>> map3,
			Map<class_2960, class_3494.class_3495<class_1299<?>>> map4
		) {
			this.field_17938 = map;
			this.field_17939 = map2;
			this.field_17940 = map3;
			this.field_17941 = map4;
		}
	}
}
