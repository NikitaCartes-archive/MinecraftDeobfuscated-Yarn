package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_463 extends class_437 {
	protected class_342 field_2751;
	protected class_342 field_2755;
	protected class_4185 field_2762;
	protected class_4185 field_2753;
	protected class_4185 field_2760;
	protected boolean field_2752;
	protected final List<String> field_2761 = Lists.<String>newArrayList();
	protected int field_2757;
	protected int field_2756;
	protected ParseResults<class_2172> field_2758;
	protected CompletableFuture<Suggestions> field_2754;
	protected class_463.class_464 field_2759;
	private boolean field_2750;

	public class_463() {
		super(class_333.field_18967);
	}

	@Override
	public void tick() {
		this.field_2751.method_1865();
	}

	abstract class_1918 method_2351();

	abstract int method_2364();

	@Override
	protected void init() {
		this.minecraft.field_1774.method_1462(true);
		this.field_2762 = this.addButton(
			new class_4185(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, class_1074.method_4662("gui.done"), arg -> this.method_2359())
		);
		this.field_2753 = this.addButton(
			new class_4185(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, class_1074.method_4662("gui.cancel"), arg -> this.onClose())
		);
		this.field_2760 = this.addButton(new class_4185(this.width / 2 + 150 - 20, this.method_2364(), 20, 20, "O", arg -> {
			class_1918 lv = this.method_2351();
			lv.method_8287(!lv.method_8296());
			this.method_2368();
		}));
		this.field_2751 = new class_342(this.font, this.width / 2 - 150, 50, 300, 20, class_1074.method_4662("advMode.command"));
		this.field_2751.method_1880(32500);
		this.field_2751.method_1854(this::method_2348);
		this.field_2751.method_1863(this::method_2360);
		this.children.add(this.field_2751);
		this.field_2755 = new class_342(this.font, this.width / 2 - 150, this.method_2364(), 276, 20, class_1074.method_4662("advMode.previousOutput"));
		this.field_2755.method_1880(32500);
		this.field_2755.method_1888(false);
		this.field_2755.method_1852("-");
		this.children.add(this.field_2755);
		this.method_20085(this.field_2751);
		this.field_2751.method_1876(true);
		this.method_2353();
	}

	@Override
	public void resize(class_310 arg, int i, int j) {
		String string = this.field_2751.method_1882();
		this.init(arg, i, j);
		this.method_2346(string);
		this.method_2353();
	}

	protected void method_2368() {
		if (this.method_2351().method_8296()) {
			this.field_2760.setMessage("O");
			this.field_2755.method_1852(this.method_2351().method_8292().getString());
		} else {
			this.field_2760.setMessage("X");
			this.field_2755.method_1852("-");
		}
	}

	protected void method_2359() {
		class_1918 lv = this.method_2351();
		this.method_2352(lv);
		if (!lv.method_8296()) {
			lv.method_8291(null);
		}

		this.minecraft.method_1507(null);
	}

	@Override
	public void removed() {
		this.minecraft.field_1774.method_1462(false);
	}

	protected abstract void method_2352(class_1918 arg);

	@Override
	public void onClose() {
		this.method_2351().method_8287(this.field_2752);
		this.minecraft.method_1507(null);
	}

	private void method_2360(String string) {
		this.method_2353();
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.field_2759 != null && this.field_2759.method_2377(i, j, k)) {
			return true;
		} else if (this.getFocused() == this.field_2751 && i == 258) {
			this.method_2357();
			return true;
		} else if (super.keyPressed(i, j, k)) {
			return true;
		} else if (i != 257 && i != 335) {
			if (i == 258 && this.getFocused() == this.field_2751) {
				this.method_2357();
			}

			return false;
		} else {
			this.method_2359();
			return true;
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2759 != null && this.field_2759.method_2370(class_3532.method_15350(f, -1.0, 1.0)) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2759 != null && this.field_2759.method_2372((int)d, (int)e, i) ? true : super.mouseClicked(d, e, i);
	}

	protected void method_2353() {
		String string = this.field_2751.method_1882();
		if (this.field_2758 != null && !this.field_2758.getReader().getString().equals(string)) {
			this.field_2758 = null;
		}

		if (!this.field_2750) {
			this.field_2751.method_1887(null);
			this.field_2759 = null;
		}

		this.field_2761.clear();
		CommandDispatcher<class_2172> commandDispatcher = this.minecraft.field_1724.field_3944.method_2886();
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		int i = stringReader.getCursor();
		if (this.field_2758 == null) {
			this.field_2758 = commandDispatcher.parse(stringReader, this.minecraft.field_1724.field_3944.method_2875());
		}

		int j = this.field_2751.method_1881();
		if (j >= i && (this.field_2759 == null || !this.field_2750)) {
			this.field_2754 = commandDispatcher.getCompletionSuggestions(this.field_2758, j);
			this.field_2754.thenRun(() -> {
				if (this.field_2754.isDone()) {
					this.method_2354();
				}
			});
		}
	}

	private void method_2354() {
		if (((Suggestions)this.field_2754.join()).isEmpty()
			&& !this.field_2758.getExceptions().isEmpty()
			&& this.field_2751.method_1881() == this.field_2751.method_1882().length()) {
			int i = 0;

			for (Entry<CommandNode<class_2172>, CommandSyntaxException> entry : this.field_2758.getExceptions().entrySet()) {
				CommandSyntaxException commandSyntaxException = (CommandSyntaxException)entry.getValue();
				if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
					i++;
				} else {
					this.field_2761.add(commandSyntaxException.getMessage());
				}
			}

			if (i > 0) {
				this.field_2761.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
			}
		}

		this.field_2757 = 0;
		this.field_2756 = this.width;
		if (this.field_2761.isEmpty()) {
			this.method_2356(class_124.field_1080);
		}

		this.field_2759 = null;
		if (this.minecraft.field_1690.field_1873) {
			this.method_2357();
		}
	}

	private String method_2348(String string, int i) {
		return this.field_2758 != null ? class_408.method_2105(this.field_2758, string, i) : string;
	}

	private void method_2356(class_124 arg) {
		CommandContextBuilder<class_2172> commandContextBuilder = this.field_2758.getContext();
		SuggestionContext<class_2172> suggestionContext = commandContextBuilder.findSuggestionContext(this.field_2751.method_1881());
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
			this.field_2761.addAll(list);
			this.field_2757 = class_3532.method_15340(
				this.field_2751.method_1889(suggestionContext.startPos), 0, this.field_2751.method_1889(0) + this.field_2751.method_1859() - i
			);
			this.field_2756 = i;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		this.drawCenteredString(this.font, class_1074.method_4662("advMode.setCommand"), this.width / 2, 20, 16777215);
		this.drawString(this.font, class_1074.method_4662("advMode.command"), this.width / 2 - 150, 40, 10526880);
		this.field_2751.render(i, j, f);
		int k = 75;
		if (!this.field_2755.method_1882().isEmpty()) {
			k += 5 * 9 + 1 + this.method_2364() - 135;
			this.drawString(this.font, class_1074.method_4662("advMode.previousOutput"), this.width / 2 - 150, k + 4, 10526880);
			this.field_2755.render(i, j, f);
		}

		super.render(i, j, f);
		if (this.field_2759 != null) {
			this.field_2759.method_2373(i, j);
		} else {
			k = 0;

			for (String string : this.field_2761) {
				fill(this.field_2757 - 1, 72 + 12 * k, this.field_2757 + this.field_2756 + 1, 84 + 12 * k, Integer.MIN_VALUE);
				this.font.method_1720(string, (float)this.field_2757, (float)(74 + 12 * k), -1);
				k++;
			}
		}
	}

	public void method_2357() {
		if (this.field_2754 != null && this.field_2754.isDone()) {
			Suggestions suggestions = (Suggestions)this.field_2754.join();
			if (!suggestions.isEmpty()) {
				int i = 0;

				for (Suggestion suggestion : suggestions.getList()) {
					i = Math.max(i, this.font.method_1727(suggestion.getText()));
				}

				int j = class_3532.method_15340(
					this.field_2751.method_1889(suggestions.getRange().getStart()), 0, this.field_2751.method_1889(0) + this.field_2751.method_1859() - i
				);
				this.field_2759 = new class_463.class_464(j, 72, i, suggestions);
			}
		}
	}

	protected void method_2346(String string) {
		this.field_2751.method_1852(string);
	}

	@Nullable
	private static String method_2350(String string, String string2) {
		return string2.startsWith(string) ? string2.substring(string.length()) : null;
	}

	@Environment(EnvType.CLIENT)
	class class_464 {
		private final class_768 field_2771;
		private final Suggestions field_2764;
		private final String field_2768;
		private int field_2769;
		private int field_2766;
		private class_241 field_2767 = class_241.field_1340;
		private boolean field_2765;

		private class_464(int i, int j, int k, Suggestions suggestions) {
			this.field_2771 = new class_768(i - 1, j, k + 1, Math.min(suggestions.getList().size(), 7) * 12);
			this.field_2764 = suggestions;
			this.field_2768 = class_463.this.field_2751.method_1882();
			this.method_2374(0);
		}

		public void method_2373(int i, int j) {
			int k = Math.min(this.field_2764.getList().size(), 7);
			int l = Integer.MIN_VALUE;
			int m = -5592406;
			boolean bl = this.field_2769 > 0;
			boolean bl2 = this.field_2764.getList().size() > this.field_2769 + k;
			boolean bl3 = bl || bl2;
			boolean bl4 = this.field_2767.field_1343 != (float)i || this.field_2767.field_1342 != (float)j;
			if (bl4) {
				this.field_2767 = new class_241((float)i, (float)j);
			}

			if (bl3) {
				class_332.fill(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() - 1,
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322(),
					Integer.MIN_VALUE
				);
				class_332.fill(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() + this.field_2771.method_3320(),
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322() + this.field_2771.method_3320() + 1,
					Integer.MIN_VALUE
				);
				if (bl) {
					for (int n = 0; n < this.field_2771.method_3319(); n++) {
						if (n % 2 == 0) {
							class_332.fill(
								this.field_2771.method_3321() + n, this.field_2771.method_3322() - 1, this.field_2771.method_3321() + n + 1, this.field_2771.method_3322(), -1
							);
						}
					}
				}

				if (bl2) {
					for (int nx = 0; nx < this.field_2771.method_3319(); nx++) {
						if (nx % 2 == 0) {
							class_332.fill(
								this.field_2771.method_3321() + nx,
								this.field_2771.method_3322() + this.field_2771.method_3320(),
								this.field_2771.method_3321() + nx + 1,
								this.field_2771.method_3322() + this.field_2771.method_3320() + 1,
								-1
							);
						}
					}
				}
			}

			boolean bl5 = false;

			for (int o = 0; o < k; o++) {
				Suggestion suggestion = (Suggestion)this.field_2764.getList().get(o + this.field_2769);
				class_332.fill(
					this.field_2771.method_3321(),
					this.field_2771.method_3322() + 12 * o,
					this.field_2771.method_3321() + this.field_2771.method_3319(),
					this.field_2771.method_3322() + 12 * o + 12,
					Integer.MIN_VALUE
				);
				if (i > this.field_2771.method_3321()
					&& i < this.field_2771.method_3321() + this.field_2771.method_3319()
					&& j > this.field_2771.method_3322() + 12 * o
					&& j < this.field_2771.method_3322() + 12 * o + 12) {
					if (bl4) {
						this.method_2374(o + this.field_2769);
					}

					bl5 = true;
				}

				class_463.this.font
					.method_1720(
						suggestion.getText(),
						(float)(this.field_2771.method_3321() + 1),
						(float)(this.field_2771.method_3322() + 2 + 12 * o),
						o + this.field_2769 == this.field_2766 ? -256 : -5592406
					);
			}

			if (bl5) {
				Message message = ((Suggestion)this.field_2764.getList().get(this.field_2766)).getTooltip();
				if (message != null) {
					class_463.this.renderTooltip(class_2564.method_10883(message).method_10863(), i, j);
				}
			}
		}

		public boolean method_2372(int i, int j, int k) {
			if (!this.field_2771.method_3318(i, j)) {
				return false;
			} else {
				int l = (j - this.field_2771.method_3322()) / 12 + this.field_2769;
				if (l >= 0 && l < this.field_2764.getList().size()) {
					this.method_2374(l);
					this.method_2375();
				}

				return true;
			}
		}

		public boolean method_2370(double d) {
			int i = (int)(
				class_463.this.minecraft.field_1729.method_1603()
					* (double)class_463.this.minecraft.field_1704.method_4486()
					/ (double)class_463.this.minecraft.field_1704.method_4480()
			);
			int j = (int)(
				class_463.this.minecraft.field_1729.method_1604()
					* (double)class_463.this.minecraft.field_1704.method_4502()
					/ (double)class_463.this.minecraft.field_1704.method_4507()
			);
			if (this.field_2771.method_3318(i, j)) {
				this.field_2769 = class_3532.method_15340((int)((double)this.field_2769 - d), 0, Math.max(this.field_2764.getList().size() - 7, 0));
				return true;
			} else {
				return false;
			}
		}

		public boolean method_2377(int i, int j, int k) {
			if (i == 265) {
				this.method_2371(-1);
				this.field_2765 = false;
				return true;
			} else if (i == 264) {
				this.method_2371(1);
				this.field_2765 = false;
				return true;
			} else if (i == 258) {
				if (this.field_2765) {
					this.method_2371(class_437.hasShiftDown() ? -1 : 1);
				}

				this.method_2375();
				return true;
			} else if (i == 256) {
				this.method_2376();
				return true;
			} else {
				return false;
			}
		}

		public void method_2371(int i) {
			this.method_2374(this.field_2766 + i);
			int j = this.field_2769;
			int k = this.field_2769 + 7 - 1;
			if (this.field_2766 < j) {
				this.field_2769 = class_3532.method_15340(this.field_2766, 0, Math.max(this.field_2764.getList().size() - 7, 0));
			} else if (this.field_2766 > k) {
				this.field_2769 = class_3532.method_15340(this.field_2766 - 7, 0, Math.max(this.field_2764.getList().size() - 7, 0));
			}
		}

		public void method_2374(int i) {
			this.field_2766 = i;
			if (this.field_2766 < 0) {
				this.field_2766 = this.field_2766 + this.field_2764.getList().size();
			}

			if (this.field_2766 >= this.field_2764.getList().size()) {
				this.field_2766 = this.field_2766 - this.field_2764.getList().size();
			}

			Suggestion suggestion = (Suggestion)this.field_2764.getList().get(this.field_2766);
			class_463.this.field_2751.method_1887(class_463.method_2350(class_463.this.field_2751.method_1882(), suggestion.apply(this.field_2768)));
		}

		public void method_2375() {
			Suggestion suggestion = (Suggestion)this.field_2764.getList().get(this.field_2766);
			class_463.this.field_2750 = true;
			class_463.this.method_2346(suggestion.apply(this.field_2768));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			class_463.this.field_2751.method_1875(i);
			class_463.this.field_2751.method_1884(i);
			this.method_2374(this.field_2766);
			class_463.this.field_2750 = false;
			this.field_2765 = true;
		}

		public void method_2376() {
			class_463.this.field_2759 = null;
		}
	}
}
