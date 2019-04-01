package net.minecraft;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_408 extends class_437 {
	private static final Pattern field_2377 = Pattern.compile("(\\s+)");
	private String field_2389 = "";
	private int field_2387 = -1;
	protected class_342 field_2382;
	private String field_18973 = "";
	protected final List<String> field_18972 = Lists.<String>newArrayList();
	protected int field_2381;
	protected int field_2379;
	private ParseResults<class_2172> field_2388;
	private CompletableFuture<Suggestions> field_2386;
	private class_408.class_409 field_2385;
	private boolean field_2380;
	private boolean field_2378;

	public class_408(String string) {
		super(class_333.field_18967);
		this.field_18973 = string;
	}

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_2387 = this.minecraft.field_1705.method_1743().method_1809().size();
		this.field_2382 = new class_342(this.font, 4, this.height - 12, this.width - 4, 12);
		this.field_2382.method_1880(256);
		this.field_2382.method_1858(false);
		this.field_2382.method_1852(this.field_18973);
		this.field_2382.method_1854(this::method_2102);
		this.field_2382.method_1863(this::method_2111);
		this.children.add(this.field_2382);
		this.method_2110();
		this.method_20085(this.field_2382);
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2382.method_1882();
		this.init(arg, i, j);
		this.method_2108(string);
		this.method_2110();
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
		this.minecraft.field_1705.method_1743().method_1820();
	}

	@Override
	public void tick() {
		this.field_2382.method_1865();
	}

	private void method_2111(String string) {
		String string2 = this.field_2382.method_1882();
		this.field_2380 = !string2.equals(this.field_18973);
		this.method_2110();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2385 != null && this.field_2385.method_2124(i, j, k)) {
			return true;
		} else {
			if (i == 258) {
				this.field_2380 = true;
				this.method_2112();
			}

			if (super.keyPressed(i, j, k)) {
				return true;
			} else if (i == 256) {
				this.minecraft.method_1507(null);
				return true;
			} else if (i == 257 || i == 335) {
				String string = this.field_2382.method_1882().trim();
				if (!string.isEmpty()) {
					this.sendMessage(string);
				}

				this.minecraft.method_1507(null);
				return true;
			} else if (i == 265) {
				this.method_2114(-1);
				return true;
			} else if (i == 264) {
				this.method_2114(1);
				return true;
			} else if (i == 266) {
				this.minecraft.field_1705.method_1743().method_1802((double)(this.minecraft.field_1705.method_1743().method_1813() - 1));
				return true;
			} else if (i == 267) {
				this.minecraft.field_1705.method_1743().method_1802((double)(-this.minecraft.field_1705.method_1743().method_1813() + 1));
				return true;
			} else {
				return false;
			}
		}
	}

	public void method_2112() {
		if (this.field_2386 != null && this.field_2386.isDone()) {
			int i = 0;
			Suggestions suggestions = (Suggestions)this.field_2386.join();
			if (!suggestions.getList().isEmpty()) {
				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.font.method_1727(suggestion.getText()));
				}

				int j = class_3532.method_15340(this.field_2382.method_1889(suggestions.getRange().getStart()), 0, this.width - i);
				this.field_2385 = new class_408.class_409(j, this.height - 12, i, suggestions);
			}
		}
	}

	private static int method_2104(String string) {
		if (Strings.isNullOrEmpty(string)) {
			return 0;
		} else {
			int i = 0;
			Matcher matcher = field_2377.matcher(string);

			while (matcher.find()) {
				i = matcher.end();
			}

			return i;
		}
	}

	private void method_2110() {
		String string = this.field_2382.method_1882();
		if (this.field_2388 != null && !this.field_2388.getReader().getString().equals(string)) {
			this.field_2388 = null;
		}

		if (!this.field_2378) {
			this.field_2382.method_1887(null);
			this.field_2385 = null;
		}

		this.field_18972.clear();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
			CommandDispatcher<class_2172> commandDispatcher = this.minecraft.field_1724.field_3944.method_2886();
			if (this.field_2388 == null) {
				this.field_2388 = commandDispatcher.parse(stringReader, this.minecraft.field_1724.field_3944.method_2875());
			}

			int i = this.field_2382.method_1881();
			if (i >= 1 && (this.field_2385 == null || !this.field_2378)) {
				this.field_2386 = commandDispatcher.getCompletionSuggestions(this.field_2388, i);
				this.field_2386.thenRun(() -> {
					if (this.field_2386.isDone()) {
						this.method_2116();
					}
				});
			}
		} else {
			int i = method_2104(string);
			Collection<String> collection = this.minecraft.field_1724.field_3944.method_2875().method_9262();
			this.field_2386 = class_2172.method_9265(collection, new SuggestionsBuilder(string, i));
		}
	}

	private void method_2116() {
		if (((Suggestions)this.field_2386.join()).isEmpty()
			&& !this.field_2388.getExceptions().isEmpty()
			&& this.field_2382.method_1881() == this.field_2382.method_1882().length()) {
			int i = 0;

			for (Entry<CommandNode<class_2172>, CommandSyntaxException> entry : this.field_2388.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_18972.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_18972.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_2381 = 0;
		this.field_2379 = this.width;
		if (this.field_18972.isEmpty()) {
			this.method_2107(class_124.field_1080);
		}

		this.field_2385 = null;
		if (this.field_2380 && this.minecraft.field_1690.field_1873) {
			this.method_2112();
		}
	}

	private String method_2102(String string, int i) {
		return this.field_2388 != null ? method_2105(this.field_2388, string, i) : string;
	}

	public static String method_2105(ParseResults<class_2172> parseResults, String string, int i) {
		class_124[] lvs = new class_124[]{class_124.field_1075, class_124.field_1054, class_124.field_1060, class_124.field_1076, class_124.field_1065};
		String string2 = class_124.field_1080.toString();
		StringBuilder stringBuilder = new StringBuilder(string2);
		int j = 0;
		int k = -1;
		CommandContextBuilder<class_2172> commandContextBuilder = parseResults.getContext().getLastChild();

		for (ParsedArgument<class_2172, ?> parsedArgument : commandContextBuilder.getArguments().values()) {
			if (++k >= lvs.length) {
				k = 0;
			}

			int l = Math.max(parsedArgument.getRange().getStart() - i, 0);
			if (l >= string.length()) {
				break;
			}

			int m = Math.min(parsedArgument.getRange().getEnd() - i, string.length());
			if (m > 0) {
				stringBuilder.append(string, j, l);
				stringBuilder.append(lvs[k]);
				stringBuilder.append(string, l, m);
				stringBuilder.append(string2);
				j = m;
			}
		}

		if (parseResults.getReader().canRead()) {
			int n = Math.max(parseResults.getReader().getCursor() - i, 0);
			if (n < string.length()) {
				int o = Math.min(n + parseResults.getReader().getRemainingLength(), string.length());
				stringBuilder.append(string, j, n);
				stringBuilder.append(class_124.field_1061);
				stringBuilder.append(string, n, o);
				j = o;
			}
		}

		stringBuilder.append(string, j, string.length());
		return stringBuilder.toString();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		if (f > 1.0) {
			f = 1.0;
		}

		if (f < -1.0) {
			f = -1.0;
		}

		if (this.field_2385 != null && this.field_2385.method_2117(f)) {
			return true;
		} else {
			if (!hasShiftDown()) {
				f *= 7.0;
			}

			this.minecraft.field_1705.method_1743().method_1802(f);
			return true;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (this.field_2385 != null && this.field_2385.method_2119((int)d, (int)e, i)) {
			return true;
		} else {
			if (i == 0) {
				class_2561 lv = this.minecraft.field_1705.method_1743().method_1816(d, e);
				if (lv != null && this.handleComponentClicked(lv)) {
					return true;
				}
			}

			return this.field_2382.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
		}
	}

	@Override
	protected void insertText(String string, boolean bl) {
		if (bl) {
			this.field_2382.method_1852(string);
		} else {
			this.field_2382.method_1867(string);
		}
	}

	public void method_2114(int i) {
		int j = this.field_2387 + i;
		int k = this.minecraft.field_1705.method_1743().method_1809().size();
		j = class_3532.method_15340(j, 0, k);
		if (j != this.field_2387) {
			if (j == k) {
				this.field_2387 = k;
				this.field_2382.method_1852(this.field_2389);
			} else {
				if (this.field_2387 == k) {
					this.field_2389 = this.field_2382.method_1882();
				}

				this.field_2382.method_1852((String)this.minecraft.field_1705.method_1743().method_1809().get(j));
				this.field_2385 = null;
				this.field_2387 = j;
				this.field_2380 = false;
			}
		}
	}

	@Override
	public void render(int i, int j, float f) {
		fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.field_1690.method_19344(Integer.MIN_VALUE));
		this.field_2382.render(i, j, f);
		if (this.field_2385 != null) {
			this.field_2385.method_2120(i, j);
		} else {
			int k = 0;

			for (String string : this.field_18972) {
				fill(this.field_2381 - 1, this.height - 14 - 13 - 12 * k, this.field_2381 + this.field_2379 + 1, this.height - 2 - 13 - 12 * k, -16777216);
				this.font.method_1720(string, (float)this.field_2381, (float)(this.height - 14 - 13 + 2 - 12 * k), -1);
				k++;
			}
		}

		class_2561 lv = this.minecraft.field_1705.method_1743().method_1816((double)i, (double)j);
		if (lv != null && lv.method_10866().method_10969() != null) {
			this.renderComponentHoverEffect(lv, i, j);
		}

		super.render(i, j, f);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void method_2107(class_124 arg) {
		CommandContextBuilder<class_2172> commandContextBuilder = this.field_2388.getContext();
		SuggestionContext<class_2172> suggestionContext = commandContextBuilder.findSuggestionContext(this.field_2382.method_1881());
		Map<CommandNode<class_2172>, String> map = this.minecraft
			.field_1724
			.field_3944
			.method_2886()
			.getSmartUsage(suggestionContext.parent, this.minecraft.field_1724.field_3944.method_2875());
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (Entry<CommandNode<class_2172>, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof LiteralCommandNode)) {
				list.add(arg + (String)entry.getValue());
				i = Math.max(i, this.font.method_1727((String)entry.getValue()));
			}
		}

		if (!list.isEmpty()) {
			this.field_18972.addAll(list);
			this.field_2381 = class_3532.method_15340(this.field_2382.method_1889(suggestionContext.startPos), 0, this.width - i);
			this.field_2379 = i;
		}
	}

	@Nullable
	private static String method_2103(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	private void method_2108(String string) {
		this.field_2382.method_1852(string);
	}

	@Environment(EnvType.CLIENT)
	class class_409 {
		private final class_768 field_2396;
		private final Suggestions field_2390;
		private final String field_2394;
		private int field_2395;
		private int field_2392;
		private class_241 field_2393 = class_241.field_1340;
		private boolean field_2391;

		private class_409(int i, int j, int k, Suggestions suggestions) {
			this.field_2396 = new class_768(i - 1, j - 3 - Math.min(suggestions.getList().size(), 10) * 12, k + 1, Math.min(suggestions.getList().size(), 10) * 12);
			this.field_2390 = suggestions;
			this.field_2394 = class_408.this.field_2382.method_1882();
			this.method_2121(0);
		}

		public void method_2120(int i, int j) {
			int k = Math.min(this.field_2390.getList().size(), 10);
			int l = -5592406;
			boolean bl = this.field_2395 > 0;
			boolean bl2 = this.field_2390.getList().size() > this.field_2395 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_2393.field_1343 != (float)i || this.field_2393.field_1342 != (float)j;
			if (bl4) {
				this.field_2393 = new class_241((float)i, (float)j);
			}

			if (bl3) {
				class_332.fill(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() - 1,
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322(),
					-805306368
				);
				class_332.fill(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() + this.field_2396.method_3320(),
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322() + this.field_2396.method_3320() + 1,
					-805306368
				);
				if (bl) {
					for (int m = 0; m < this.field_2396.method_3319(); m++) {
						if (m % 2 == 0) {
							class_332.fill(
								this.field_2396.method_3321() + m, this.field_2396.method_3322() - 1, this.field_2396.method_3321() + m + 1, this.field_2396.method_3322(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int mx = 0; mx < this.field_2396.method_3319(); mx++) {
						if (mx % 2 == 0) {
							class_332.fill(
								this.field_2396.method_3321() + mx,
								this.field_2396.method_3322() + this.field_2396.method_3320(),
								this.field_2396.method_3321() + mx + 1,
								this.field_2396.method_3322() + this.field_2396.method_3320() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int n = 0; n < k; n++) {
				Suggestion suggestion = (Suggestion)this.field_2390.getList().get(n + this.field_2395);
				class_332.fill(
					this.field_2396.method_3321(),
					this.field_2396.method_3322() + 12 * n,
					this.field_2396.method_3321() + this.field_2396.method_3319(),
					this.field_2396.method_3322() + 12 * n + 12,
					-805306368
				);
				if (i > this.field_2396.method_3321()
					&& i < this.field_2396.method_3321() + this.field_2396.method_3319()
					&& j > this.field_2396.method_3322() + 12 * n
					&& j < this.field_2396.method_3322() + 12 * n + 12) {
					if (bl4) {
						this.method_2121(n + this.field_2395);
					}

					bl5 = true;
				}

				class_408.this.font
					.method_1720(
						suggestion.getText(),
						(float)(this.field_2396.method_3321() + 1),
						(float)(this.field_2396.method_3322() + 2 + 12 * n),
						n + this.field_2395 == this.field_2392 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_2390.getList().get(this.field_2392)).getTooltip();
				if (message != null) {
					class_408.this.renderTooltip(class_2564.method_10883(message).method_10863(), i, j);
				}
			}
		}

		public boolean method_2119(int i, int j, int k) {
			if (!this.field_2396.method_3318(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2396.method_3322()) / 12 + this.field_2395;
				if (l >= 0 && l < this.field_2390.getList().size()) {
					this.method_2121(l);
					this.method_2122();
				}

				return true;
			}
		}

		public boolean method_2117(double d) {
			int i = (int)(
				class_408.this.minecraft.field_1729.method_1603()
					* (double)class_408.this.minecraft.field_1704.method_4486()
					/ (double)class_408.this.minecraft.field_1704.method_4480()
			);
			int j = (int)(
				class_408.this.minecraft.field_1729.method_1604()
					* (double)class_408.this.minecraft.field_1704.method_4502()
					/ (double)class_408.this.minecraft.field_1704.method_4507()
			);
			if (this.field_2396.method_3318(i, j)) {
				this.field_2395 = class_3532.method_15340((int)((double)this.field_2395 - d), 0, Math.max(this.field_2390.getList().size() - 10, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_2124(int i, int j, int k) {
			if (i == 265) {
				this.method_2118(-1);
				this.field_2391 = false;
				return true;
			} else if (i == 264) {
				this.method_2118(1);
				this.field_2391 = false;
				return true;
			} else if (i == 258) {
				if (this.field_2391) {
					this.method_2118(class_437.hasShiftDown() ? -1 : 1);
				}

				this.method_2122();
				return true;
			} else if (i == 256) {
				this.method_2123();
				return true;
			} else {
				return false;
			}
		}

		public void method_2118(int i) {
			this.method_2121(this.field_2392 + i);
			int j = this.field_2395;
			int k = this.field_2395 + 10 - 1;
			if (this.field_2392 < j) {
				this.field_2395 = class_3532.method_15340(this.field_2392, 0, Math.max(this.field_2390.getList().size() - 10, 0));
			} else if (this.field_2392 > k) {
				this.field_2395 = class_3532.method_15340(this.field_2392 + 1 - 10, 0, Math.max(this.field_2390.getList().size() - 10, 0));
			}
		}

		public void method_2121(int i) {
			this.field_2392 = i;
			if (this.field_2392 < 0) {
				this.field_2392 = this.field_2392 + this.field_2390.getList().size();
			}

			if (this.field_2392 >= this.field_2390.getList().size()) {
				this.field_2392 = this.field_2392 - this.field_2390.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_2390.getList().get(this.field_2392);
			class_408.this.field_2382.method_1887(class_408.method_2103(class_408.this.field_2382.method_1882(), suggestion.apply(this.field_2394)));
		}

		public void method_2122() {
			Suggestion suggestion = (Suggestion)this.field_2390.getList().get(this.field_2392);
			class_408.this.field_2378 = true;
			class_408.this.method_2108(suggestion.apply(this.field_2394));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			class_408.this.field_2382.method_1875(i);
			class_408.this.field_2382.method_1884(i);
			this.method_2121(this.field_2392);
			class_408.this.field_2378 = false;
			this.field_2391 = true;
		}

		public void method_2123() {
			class_408.this.field_2385 = null;
		}
	}
}
