package net.minecraft;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class class_2303 {
	public static final SimpleCommandExceptionType field_10875 = new SimpleCommandExceptionType(new class_2588("argument.entity.invalid"));
	public static final DynamicCommandExceptionType field_10853 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.selector.unknown", object)
	);
	public static final SimpleCommandExceptionType field_10880 = new SimpleCommandExceptionType(new class_2588("argument.entity.selector.not_allowed"));
	public static final SimpleCommandExceptionType field_10844 = new SimpleCommandExceptionType(new class_2588("argument.entity.selector.missing"));
	public static final SimpleCommandExceptionType field_10837 = new SimpleCommandExceptionType(new class_2588("argument.entity.options.unterminated"));
	public static final DynamicCommandExceptionType field_10855 = new DynamicCommandExceptionType(
		object -> new class_2588("argument.entity.options.valueless", object)
	);
	public static final BiConsumer<class_243, List<? extends class_1297>> field_10856 = (arg, list) -> {
	};
	public static final BiConsumer<class_243, List<? extends class_1297>> field_10869 = (arg, list) -> list.sort(
			(arg2, arg3) -> Doubles.compare(arg2.method_5707(arg), arg3.method_5707(arg))
		);
	public static final BiConsumer<class_243, List<? extends class_1297>> field_10882 = (arg, list) -> list.sort(
			(arg2, arg3) -> Doubles.compare(arg3.method_5707(arg), arg2.method_5707(arg))
		);
	public static final BiConsumer<class_243, List<? extends class_1297>> field_10850 = (arg, list) -> Collections.shuffle(list);
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_10867 = (suggestionsBuilder, consumer) -> suggestionsBuilder.buildFuture();
	private final StringReader field_10860;
	private final boolean field_10846;
	private int field_10858;
	private boolean field_10843;
	private boolean field_10866;
	private class_2096.class_2099 field_10838 = class_2096.class_2099.field_9705;
	private class_2096.class_2100 field_10842 = class_2096.class_2100.field_9708;
	@Nullable
	private Double field_10857;
	@Nullable
	private Double field_10872;
	@Nullable
	private Double field_10839;
	@Nullable
	private Double field_10862;
	@Nullable
	private Double field_10852;
	@Nullable
	private Double field_10881;
	private class_2152 field_10877 = class_2152.field_9780;
	private class_2152 field_10859 = class_2152.field_9780;
	private Predicate<class_1297> field_10870 = arg -> true;
	private BiConsumer<class_243, List<? extends class_1297>> field_10847 = field_10856;
	private boolean field_10879;
	@Nullable
	private String field_10876;
	private int field_10861;
	@Nullable
	private UUID field_10878;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> field_10848 = field_10867;
	private boolean field_10854;
	private boolean field_10874;
	private boolean field_10851;
	private boolean field_10873;
	private boolean field_10849;
	private boolean field_10871;
	private boolean field_10845;
	private boolean field_10868;
	@Nullable
	private class_1299<?> field_10863;
	private boolean field_10865;
	private boolean field_10841;
	private boolean field_10864;
	private boolean field_10840;

	public class_2303(StringReader stringReader) {
		this(stringReader, true);
	}

	public class_2303(StringReader stringReader, boolean bl) {
		this.field_10860 = stringReader;
		this.field_10846 = bl;
	}

	public class_2300 method_9871() {
		class_238 lv;
		if (this.field_10862 == null && this.field_10852 == null && this.field_10881 == null) {
			if (this.field_10838.method_9042() != null) {
				float f = (Float)this.field_10838.method_9042();
				lv = new class_238((double)(-f), (double)(-f), (double)(-f), (double)(f + 1.0F), (double)(f + 1.0F), (double)(f + 1.0F));
			} else {
				lv = null;
			}
		} else {
			lv = this.method_9894(
				this.field_10862 == null ? 0.0 : this.field_10862, this.field_10852 == null ? 0.0 : this.field_10852, this.field_10881 == null ? 0.0 : this.field_10881
			);
		}

		Function<class_243, class_243> function;
		if (this.field_10857 == null && this.field_10872 == null && this.field_10839 == null) {
			function = arg -> arg;
		} else {
			function = arg -> new class_243(
					this.field_10857 == null ? arg.field_1352 : this.field_10857,
					this.field_10872 == null ? arg.field_1351 : this.field_10872,
					this.field_10839 == null ? arg.field_1350 : this.field_10839
				);
		}

		return new class_2300(
			this.field_10858,
			this.field_10843,
			this.field_10866,
			this.field_10870,
			this.field_10838,
			function,
			lv,
			this.field_10847,
			this.field_10879,
			this.field_10876,
			this.field_10878,
			this.field_10863,
			this.field_10840
		);
	}

	private class_238 method_9894(double d, double e, double f) {
		boolean bl = d < 0.0;
		boolean bl2 = e < 0.0;
		boolean bl3 = f < 0.0;
		double g = bl ? d : 0.0;
		double h = bl2 ? e : 0.0;
		double i = bl3 ? f : 0.0;
		double j = (bl ? 0.0 : d) + 1.0;
		double k = (bl2 ? 0.0 : e) + 1.0;
		double l = (bl3 ? 0.0 : f) + 1.0;
		return new class_238(g, h, i, j, k, l);
	}

	private void method_9878() {
		if (this.field_10877 != class_2152.field_9780) {
			this.field_10870 = this.field_10870.and(this.method_9859(this.field_10877, arg -> (double)arg.field_5965));
		}

		if (this.field_10859 != class_2152.field_9780) {
			this.field_10870 = this.field_10870.and(this.method_9859(this.field_10859, arg -> (double)arg.field_6031));
		}

		if (!this.field_10842.method_9041()) {
			this.field_10870 = this.field_10870.and(arg -> !(arg instanceof class_3222) ? false : this.field_10842.method_9054(((class_3222)arg).field_7520));
		}
	}

	private Predicate<class_1297> method_9859(class_2152 arg, ToDoubleFunction<class_1297> toDoubleFunction) {
		double d = (double)class_3532.method_15393(arg.method_9175() == null ? 0.0F : arg.method_9175());
		double e = (double)class_3532.method_15393(arg.method_9177() == null ? 359.0F : arg.method_9177());
		return argx -> {
			double f = class_3532.method_15338(toDoubleFunction.applyAsDouble(argx));
			return d > e ? f >= d || f <= e : f >= d && f <= e;
		};
	}

	protected void method_9917() throws CommandSyntaxException {
		this.field_10840 = true;
		this.field_10848 = this::method_9834;
		if (!this.field_10860.canRead()) {
			throw field_10844.createWithContext(this.field_10860);
		} else {
			int i = this.field_10860.getCursor();
			char c = this.field_10860.read();
			if (c == 'p') {
				this.field_10858 = 1;
				this.field_10843 = false;
				this.field_10847 = field_10869;
				this.method_9842(class_1299.field_6097);
			} else if (c == 'a') {
				this.field_10858 = Integer.MAX_VALUE;
				this.field_10843 = false;
				this.field_10847 = field_10856;
				this.method_9842(class_1299.field_6097);
			} else if (c == 'r') {
				this.field_10858 = 1;
				this.field_10843 = false;
				this.field_10847 = field_10850;
				this.method_9842(class_1299.field_6097);
			} else if (c == 's') {
				this.field_10858 = 1;
				this.field_10843 = true;
				this.field_10879 = true;
			} else {
				if (c != 'e') {
					this.field_10860.setCursor(i);
					throw field_10853.createWithContext(this.field_10860, '@' + String.valueOf(c));
				}

				this.field_10858 = Integer.MAX_VALUE;
				this.field_10843 = true;
				this.field_10847 = field_10856;
				this.field_10870 = class_1297::method_5805;
			}

			this.field_10848 = this::method_9893;
			if (this.field_10860.canRead() && this.field_10860.peek() == '[') {
				this.field_10860.skip();
				this.field_10848 = this::method_9911;
				this.method_9874();
			}
		}
	}

	protected void method_9849() throws CommandSyntaxException {
		if (this.field_10860.canRead()) {
			this.field_10848 = this::method_9858;
		}

		int i = this.field_10860.getCursor();
		String string = this.field_10860.readString();

		try {
			this.field_10878 = UUID.fromString(string);
			this.field_10843 = true;
		} catch (IllegalArgumentException var4) {
			if (string.isEmpty() || string.length() > 16) {
				this.field_10860.setCursor(i);
				throw field_10875.createWithContext(this.field_10860);
			}

			this.field_10843 = false;
			this.field_10876 = string;
		}

		this.field_10858 = 1;
	}

	protected void method_9874() throws CommandSyntaxException {
		this.field_10848 = this::method_9909;
		this.field_10860.skipWhitespace();

		while (this.field_10860.canRead() && this.field_10860.peek() != ']') {
			this.field_10860.skipWhitespace();
			int i = this.field_10860.getCursor();
			String string = this.field_10860.readString();
			class_2306.class_2307 lv = class_2306.method_9976(this, string, i);
			this.field_10860.skipWhitespace();
			if (!this.field_10860.canRead() || this.field_10860.peek() != '=') {
				this.field_10860.setCursor(i);
				throw field_10855.createWithContext(this.field_10860, string);
			}

			this.field_10860.skip();
			this.field_10860.skipWhitespace();
			this.field_10848 = field_10867;
			lv.handle(this);
			this.field_10860.skipWhitespace();
			this.field_10848 = this::method_9847;
			if (this.field_10860.canRead()) {
				if (this.field_10860.peek() != ',') {
					if (this.field_10860.peek() != ']') {
						throw field_10837.createWithContext(this.field_10860);
					}
					break;
				}

				this.field_10860.skip();
				this.field_10848 = this::method_9909;
			}
		}

		if (this.field_10860.canRead()) {
			this.field_10860.skip();
			this.field_10848 = field_10867;
		} else {
			throw field_10837.createWithContext(this.field_10860);
		}
	}

	public boolean method_9892() {
		this.field_10860.skipWhitespace();
		if (this.field_10860.canRead() && this.field_10860.peek() == '!') {
			this.field_10860.skip();
			this.field_10860.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public boolean method_9915() {
		this.field_10860.skipWhitespace();
		if (this.field_10860.canRead() && this.field_10860.peek() == '#') {
			this.field_10860.skip();
			this.field_10860.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	public StringReader method_9835() {
		return this.field_10860;
	}

	public void method_9916(Predicate<class_1297> predicate) {
		this.field_10870 = this.field_10870.and(predicate);
	}

	public void method_9852() {
		this.field_10866 = true;
	}

	public class_2096.class_2099 method_9873() {
		return this.field_10838;
	}

	public void method_9870(class_2096.class_2099 arg) {
		this.field_10838 = arg;
	}

	public class_2096.class_2100 method_9895() {
		return this.field_10842;
	}

	public void method_9846(class_2096.class_2100 arg) {
		this.field_10842 = arg;
	}

	public class_2152 method_9883() {
		return this.field_10877;
	}

	public void method_9898(class_2152 arg) {
		this.field_10877 = arg;
	}

	public class_2152 method_9853() {
		return this.field_10859;
	}

	public void method_9855(class_2152 arg) {
		this.field_10859 = arg;
	}

	@Nullable
	public Double method_9902() {
		return this.field_10857;
	}

	@Nullable
	public Double method_9884() {
		return this.field_10872;
	}

	@Nullable
	public Double method_9868() {
		return this.field_10839;
	}

	public void method_9850(double d) {
		this.field_10857 = d;
	}

	public void method_9864(double d) {
		this.field_10872 = d;
	}

	public void method_9879(double d) {
		this.field_10839 = d;
	}

	public void method_9891(double d) {
		this.field_10862 = d;
	}

	public void method_9905(double d) {
		this.field_10852 = d;
	}

	public void method_9918(double d) {
		this.field_10881 = d;
	}

	@Nullable
	public Double method_9851() {
		return this.field_10862;
	}

	@Nullable
	public Double method_9840() {
		return this.field_10852;
	}

	@Nullable
	public Double method_9907() {
		return this.field_10881;
	}

	public void method_9900(int i) {
		this.field_10858 = i;
	}

	public void method_9841(boolean bl) {
		this.field_10843 = bl;
	}

	public void method_9845(BiConsumer<class_243, List<? extends class_1297>> biConsumer) {
		this.field_10847 = biConsumer;
	}

	public class_2300 method_9882() throws CommandSyntaxException {
		this.field_10861 = this.field_10860.getCursor();
		this.field_10848 = this::method_9880;
		if (this.field_10860.canRead() && this.field_10860.peek() == '@') {
			if (!this.field_10846) {
				throw field_10880.createWithContext(this.field_10860);
			}

			this.field_10860.skip();
			this.method_9917();
		} else {
			this.method_9849();
		}

		this.method_9878();
		return this.method_9871();
	}

	private static void method_9896(SuggestionsBuilder suggestionsBuilder) {
		suggestionsBuilder.suggest("@p", new class_2588("argument.entity.selector.nearestPlayer"));
		suggestionsBuilder.suggest("@a", new class_2588("argument.entity.selector.allPlayers"));
		suggestionsBuilder.suggest("@r", new class_2588("argument.entity.selector.randomPlayer"));
		suggestionsBuilder.suggest("@s", new class_2588("argument.entity.selector.self"));
		suggestionsBuilder.suggest("@e", new class_2588("argument.entity.selector.allEntities"));
	}

	private CompletableFuture<Suggestions> method_9880(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(suggestionsBuilder);
		if (this.field_10846) {
			method_9896(suggestionsBuilder);
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9858(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.field_10861);
		consumer.accept(suggestionsBuilder2);
		return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
	}

	private CompletableFuture<Suggestions> method_9834(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(suggestionsBuilder.getStart() - 1);
		method_9896(suggestionsBuilder2);
		suggestionsBuilder.add(suggestionsBuilder2);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9893(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf('['));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9911(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(']'));
		class_2306.method_9930(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9909(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		class_2306.method_9930(this, suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9847(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		suggestionsBuilder.suggest(String.valueOf(','));
		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	public boolean method_9885() {
		return this.field_10879;
	}

	public void method_9875(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
		this.field_10848 = biFunction;
	}

	public CompletableFuture<Suggestions> method_9908(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
		return (CompletableFuture<Suggestions>)this.field_10848.apply(suggestionsBuilder.createOffset(this.field_10860.getCursor()), consumer);
	}

	public boolean method_9912() {
		return this.field_10854;
	}

	public void method_9899(boolean bl) {
		this.field_10854 = bl;
	}

	public boolean method_9844() {
		return this.field_10874;
	}

	public void method_9913(boolean bl) {
		this.field_10874 = bl;
	}

	public boolean method_9866() {
		return this.field_10851;
	}

	public void method_9877(boolean bl) {
		this.field_10851 = bl;
	}

	public boolean method_9889() {
		return this.field_10873;
	}

	public void method_9887(boolean bl) {
		this.field_10873 = bl;
	}

	public boolean method_9839() {
		return this.field_10849;
	}

	public void method_9890(boolean bl) {
		this.field_10849 = bl;
	}

	public boolean method_9837() {
		return this.field_10871;
	}

	public void method_9857(boolean bl) {
		this.field_10871 = bl;
	}

	public boolean method_9904() {
		return this.field_10845;
	}

	public void method_9865(boolean bl) {
		this.field_10845 = bl;
	}

	public void method_9833(boolean bl) {
		this.field_10868 = bl;
	}

	public void method_9842(class_1299<?> arg) {
		this.field_10863 = arg;
	}

	public void method_9860() {
		this.field_10865 = true;
	}

	public boolean method_9886() {
		return this.field_10863 != null;
	}

	public boolean method_9910() {
		return this.field_10865;
	}

	public boolean method_9843() {
		return this.field_10841;
	}

	public void method_9848(boolean bl) {
		this.field_10841 = bl;
	}

	public boolean method_9861() {
		return this.field_10864;
	}

	public void method_9906(boolean bl) {
		this.field_10864 = bl;
	}
}
