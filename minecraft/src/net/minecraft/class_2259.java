package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;

public class class_2259 {
	public static final SimpleCommandExceptionType field_10691 = new SimpleCommandExceptionType(new class_2588("argument.block.tag.disallowed"));
	public static final DynamicCommandExceptionType field_10690 = new DynamicCommandExceptionType(object -> new class_2588("argument.block.id.invalid", object));
	public static final Dynamic2CommandExceptionType field_10695 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("argument.block.property.unknown", object, object2)
	);
	public static final Dynamic2CommandExceptionType field_10692 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("argument.block.property.duplicate", object2, object)
	);
	public static final Dynamic3CommandExceptionType field_10683 = new Dynamic3CommandExceptionType(
		(object, object2, object3) -> new class_2588("argument.block.property.invalid", object, object3, object2)
	);
	public static final Dynamic2CommandExceptionType field_10688 = new Dynamic2CommandExceptionType(
		(object, object2) -> new class_2588("argument.block.property.novalue", object, object2)
	);
	public static final SimpleCommandExceptionType field_10684 = new SimpleCommandExceptionType(new class_2588("argument.block.property.unclosed"));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10682 = SuggestionsBuilder::buildFuture;
	private final StringReader field_10698;
	private final boolean field_10687;
	private final Map<class_2769<?>, Comparable<?>> field_10699 = Maps.<class_2769<?>, Comparable<?>>newHashMap();
	private final Map<String, String> field_10685 = Maps.<String, String>newHashMap();
	private class_2960 field_10697 = new class_2960("");
	private class_2689<class_2248, class_2680> field_10689;
	private class_2680 field_10686;
	@Nullable
	private class_2487 field_10693;
	private class_2960 field_10681 = new class_2960("");
	private int field_10694;
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> field_10696 = field_10682;

	public class_2259(StringReader stringReader, boolean bl) {
		this.field_10698 = stringReader;
		this.field_10687 = bl;
	}

	public Map<class_2769<?>, Comparable<?>> method_9692() {
		return this.field_10699;
	}

	@Nullable
	public class_2680 method_9669() {
		return this.field_10686;
	}

	@Nullable
	public class_2487 method_9694() {
		return this.field_10693;
	}

	@Nullable
	public class_2960 method_9664() {
		return this.field_10681;
	}

	public class_2259 method_9678(boolean bl) throws CommandSyntaxException {
		this.field_10696 = this::method_9673;
		if (this.field_10698.canRead() && this.field_10698.peek() == '#') {
			this.method_9677();
			this.field_10696 = this::method_9679;
			if (this.field_10698.canRead() && this.field_10698.peek() == '[') {
				this.method_9680();
				this.field_10696 = this::method_9687;
			}
		} else {
			this.method_9675();
			this.field_10696 = this::method_9681;
			if (this.field_10698.canRead() && this.field_10698.peek() == '[') {
				this.method_9659();
				this.field_10696 = this::method_9687;
			}
		}

		if (bl && this.field_10698.canRead() && this.field_10698.peek() == '{') {
			this.field_10696 = field_10682;
			this.method_9672();
		}

		return this;
	}

	private CompletableFuture<Suggestions> method_9671(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_9665(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9674(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		return this.method_9667(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9665(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

		for (class_2769<?> lv : this.field_10686.method_11569()) {
			if (!this.field_10699.containsKey(lv) && lv.method_11899().startsWith(string)) {
				suggestionsBuilder.suggest(lv.method_11899() + '=');
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9667(SuggestionsBuilder suggestionsBuilder) {
		String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
		if (this.field_10681 != null && !this.field_10681.method_12832().isEmpty()) {
			class_3494<class_2248> lv = class_3481.method_15073().method_15193(this.field_10681);
			if (lv != null) {
				for (class_2248 lv2 : lv.method_15138()) {
					for (class_2769<?> lv3 : lv2.method_9595().method_11659()) {
						if (!this.field_10685.containsKey(lv3.method_11899()) && lv3.method_11899().startsWith(string)) {
							suggestionsBuilder.suggest(lv3.method_11899() + '=');
						}
					}
				}
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9687(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty() && this.method_9676()) {
			suggestionsBuilder.suggest(String.valueOf('{'));
		}

		return suggestionsBuilder.buildFuture();
	}

	private boolean method_9676() {
		if (this.field_10686 != null) {
			return this.field_10686.method_11614().method_9570();
		} else {
			if (this.field_10681 != null) {
				class_3494<class_2248> lv = class_3481.method_15073().method_15193(this.field_10681);
				if (lv != null) {
					for (class_2248 lv2 : lv.method_15138()) {
						if (lv2.method_9570()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	private CompletableFuture<Suggestions> method_9693(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf('='));
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9689(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			suggestionsBuilder.suggest(String.valueOf(']'));
		}

		if (suggestionsBuilder.getRemaining().isEmpty() && this.field_10699.size() < this.field_10686.method_11569().size()) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		return suggestionsBuilder.buildFuture();
	}

	private static <T extends Comparable<T>> SuggestionsBuilder method_9662(SuggestionsBuilder suggestionsBuilder, class_2769<T> arg) {
		for (T comparable : arg.method_11898()) {
			if (comparable instanceof Integer) {
				suggestionsBuilder.suggest((Integer)comparable);
			} else {
				suggestionsBuilder.suggest(arg.method_11901(comparable));
			}
		}

		return suggestionsBuilder;
	}

	private CompletableFuture<Suggestions> method_9690(SuggestionsBuilder suggestionsBuilder, String string) {
		boolean bl = false;
		if (this.field_10681 != null && !this.field_10681.method_12832().isEmpty()) {
			class_3494<class_2248> lv = class_3481.method_15073().method_15193(this.field_10681);
			if (lv != null) {
				for (class_2248 lv2 : lv.method_15138()) {
					class_2769<?> lv3 = lv2.method_9595().method_11663(string);
					if (lv3 != null) {
						method_9662(suggestionsBuilder, lv3);
					}

					if (!bl) {
						for (class_2769<?> lv4 : lv2.method_9595().method_11659()) {
							if (!this.field_10685.containsKey(lv4.method_11899())) {
								bl = true;
								break;
							}
						}
					}
				}
			}
		}

		if (bl) {
			suggestionsBuilder.suggest(String.valueOf(','));
		}

		suggestionsBuilder.suggest(String.valueOf(']'));
		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9679(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			class_3494<class_2248> lv = class_3481.method_15073().method_15193(this.field_10681);
			if (lv != null) {
				boolean bl = false;
				boolean bl2 = false;

				for (class_2248 lv2 : lv.method_15138()) {
					bl |= !lv2.method_9595().method_11659().isEmpty();
					bl2 |= lv2.method_9570();
					if (bl && bl2) {
						break;
					}
				}

				if (bl) {
					suggestionsBuilder.suggest(String.valueOf('['));
				}

				if (bl2) {
					suggestionsBuilder.suggest(String.valueOf('{'));
				}
			}
		}

		return this.method_9670(suggestionsBuilder);
	}

	private CompletableFuture<Suggestions> method_9681(SuggestionsBuilder suggestionsBuilder) {
		if (suggestionsBuilder.getRemaining().isEmpty()) {
			if (!this.field_10686.method_11614().method_9595().method_11659().isEmpty()) {
				suggestionsBuilder.suggest(String.valueOf('['));
			}

			if (this.field_10686.method_11614().method_9570()) {
				suggestionsBuilder.suggest(String.valueOf('{'));
			}
		}

		return suggestionsBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> method_9670(SuggestionsBuilder suggestionsBuilder) {
		return class_2172.method_9270(class_3481.method_15073().method_15189(), suggestionsBuilder.createOffset(this.field_10694).add(suggestionsBuilder));
	}

	private CompletableFuture<Suggestions> method_9673(SuggestionsBuilder suggestionsBuilder) {
		if (this.field_10687) {
			class_2172.method_9258(class_3481.method_15073().method_15189(), suggestionsBuilder, String.valueOf('#'));
		}

		class_2172.method_9270(class_2378.field_11146.method_10235(), suggestionsBuilder);
		return suggestionsBuilder.buildFuture();
	}

	public void method_9675() throws CommandSyntaxException {
		int i = this.field_10698.getCursor();
		this.field_10697 = class_2960.method_12835(this.field_10698);
		if (class_2378.field_11146.method_10250(this.field_10697)) {
			class_2248 lv = class_2378.field_11146.method_10223(this.field_10697);
			this.field_10689 = lv.method_9595();
			this.field_10686 = lv.method_9564();
		} else {
			this.field_10698.setCursor(i);
			throw field_10690.createWithContext(this.field_10698, this.field_10697.toString());
		}
	}

	public void method_9677() throws CommandSyntaxException {
		if (!this.field_10687) {
			throw field_10691.create();
		} else {
			this.field_10696 = this::method_9670;
			this.field_10698.expect('#');
			this.field_10694 = this.field_10698.getCursor();
			this.field_10681 = class_2960.method_12835(this.field_10698);
		}
	}

	public void method_9659() throws CommandSyntaxException {
		this.field_10698.skip();
		this.field_10696 = this::method_9671;
		this.field_10698.skipWhitespace();

		while (this.field_10698.canRead() && this.field_10698.peek() != ']') {
			this.field_10698.skipWhitespace();
			int i = this.field_10698.getCursor();
			String string = this.field_10698.readString();
			class_2769<?> lv = this.field_10689.method_11663(string);
			if (lv == null) {
				this.field_10698.setCursor(i);
				throw field_10695.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			if (this.field_10699.containsKey(lv)) {
				this.field_10698.setCursor(i);
				throw field_10692.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skipWhitespace();
			this.field_10696 = this::method_9693;
			if (!this.field_10698.canRead() || this.field_10698.peek() != '=') {
				throw field_10688.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skip();
			this.field_10698.skipWhitespace();
			this.field_10696 = suggestionsBuilder -> method_9662(suggestionsBuilder, lv).buildFuture();
			int j = this.field_10698.getCursor();
			this.method_9668(lv, this.field_10698.readString(), j);
			this.field_10696 = this::method_9689;
			this.field_10698.skipWhitespace();
			if (this.field_10698.canRead()) {
				if (this.field_10698.peek() != ',') {
					if (this.field_10698.peek() != ']') {
						throw field_10684.createWithContext(this.field_10698);
					}
					break;
				}

				this.field_10698.skip();
				this.field_10696 = this::method_9665;
			}
		}

		if (this.field_10698.canRead()) {
			this.field_10698.skip();
		} else {
			throw field_10684.createWithContext(this.field_10698);
		}
	}

	public void method_9680() throws CommandSyntaxException {
		this.field_10698.skip();
		this.field_10696 = this::method_9674;
		int i = -1;
		this.field_10698.skipWhitespace();

		while (this.field_10698.canRead() && this.field_10698.peek() != ']') {
			this.field_10698.skipWhitespace();
			int j = this.field_10698.getCursor();
			String string = this.field_10698.readString();
			if (this.field_10685.containsKey(string)) {
				this.field_10698.setCursor(j);
				throw field_10692.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skipWhitespace();
			if (!this.field_10698.canRead() || this.field_10698.peek() != '=') {
				this.field_10698.setCursor(j);
				throw field_10688.createWithContext(this.field_10698, this.field_10697.toString(), string);
			}

			this.field_10698.skip();
			this.field_10698.skipWhitespace();
			this.field_10696 = suggestionsBuilder -> this.method_9690(suggestionsBuilder, string);
			i = this.field_10698.getCursor();
			String string2 = this.field_10698.readString();
			this.field_10685.put(string, string2);
			this.field_10698.skipWhitespace();
			if (this.field_10698.canRead()) {
				i = -1;
				if (this.field_10698.peek() != ',') {
					if (this.field_10698.peek() != ']') {
						throw field_10684.createWithContext(this.field_10698);
					}
					break;
				}

				this.field_10698.skip();
				this.field_10696 = this::method_9667;
			}
		}

		if (this.field_10698.canRead()) {
			this.field_10698.skip();
		} else {
			if (i >= 0) {
				this.field_10698.setCursor(i);
			}

			throw field_10684.createWithContext(this.field_10698);
		}
	}

	public void method_9672() throws CommandSyntaxException {
		this.field_10693 = new class_2522(this.field_10698).method_10727();
	}

	private <T extends Comparable<T>> void method_9668(class_2769<T> arg, String string, int i) throws CommandSyntaxException {
		Optional<T> optional = arg.method_11900(string);
		if (optional.isPresent()) {
			this.field_10686 = this.field_10686.method_11657(arg, (Comparable)optional.get());
			this.field_10699.put(arg, optional.get());
		} else {
			this.field_10698.setCursor(i);
			throw field_10683.createWithContext(this.field_10698, this.field_10697.toString(), arg.method_11899(), string);
		}
	}

	public static String method_9685(class_2680 arg) {
		StringBuilder stringBuilder = new StringBuilder(class_2378.field_11146.method_10221(arg.method_11614()).toString());
		if (!arg.method_11569().isEmpty()) {
			stringBuilder.append('[');
			boolean bl = false;

			for (Entry<class_2769<?>, Comparable<?>> entry : arg.method_11656().entrySet()) {
				if (bl) {
					stringBuilder.append(',');
				}

				method_9663(stringBuilder, (class_2769)entry.getKey(), (Comparable<?>)entry.getValue());
				bl = true;
			}

			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	private static <T extends Comparable<T>> void method_9663(StringBuilder stringBuilder, class_2769<T> arg, Comparable<?> comparable) {
		stringBuilder.append(arg.method_11899());
		stringBuilder.append('=');
		stringBuilder.append(arg.method_11901((T)comparable));
	}

	public CompletableFuture<Suggestions> method_9666(SuggestionsBuilder suggestionsBuilder) {
		return (CompletableFuture<Suggestions>)this.field_10696.apply(suggestionsBuilder.createOffset(this.field_10698.getCursor()));
	}

	public Map<String, String> method_9688() {
		return this.field_10685;
	}
}
