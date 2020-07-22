package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;

@Environment(EnvType.CLIENT)
public class class_5492 {
	private final String field_26626;
	private final List<Style> field_26627;
	private final Int2IntFunction field_26628;

	private class_5492(String string, List<Style> list, Int2IntFunction int2IntFunction) {
		this.field_26626 = string;
		this.field_26627 = ImmutableList.copyOf(list);
		this.field_26628 = int2IntFunction;
	}

	public String method_30939() {
		return this.field_26626;
	}

	public List<class_5481> method_30940(int i, int j, boolean bl) {
		if (j == 0) {
			return ImmutableList.of();
		} else {
			List<class_5481> list = Lists.<class_5481>newArrayList();
			Style style = (Style)this.field_26627.get(i);
			int k = i;

			for (int l = 1; l < j; l++) {
				int m = i + l;
				Style style2 = (Style)this.field_26627.get(m);
				if (!style2.equals(style)) {
					String string = this.field_26626.substring(k, m);
					list.add(bl ? class_5481.method_30754(string, style, this.field_26628) : class_5481.method_30747(string, style));
					style = style2;
					k = m;
				}
			}

			if (k < i + j) {
				String string2 = this.field_26626.substring(k, i + j);
				list.add(bl ? class_5481.method_30754(string2, style, this.field_26628) : class_5481.method_30747(string2, style));
			}

			return bl ? Lists.reverse(list) : list;
		}
	}

	public static class_5492 method_30943(StringRenderable stringRenderable, Int2IntFunction int2IntFunction, UnaryOperator<String> unaryOperator) {
		StringBuilder stringBuilder = new StringBuilder();
		List<Style> list = Lists.<Style>newArrayList();
		stringRenderable.visit((style, string) -> {
			TextVisitFactory.visitFormatted(string, style, (i, stylex, j) -> {
				stringBuilder.appendCodePoint(j);
				int k = Character.charCount(j);

				for (int l = 0; l < k; l++) {
					list.add(stylex);
				}

				return true;
			});
			return Optional.empty();
		}, Style.EMPTY);
		return new class_5492((String)unaryOperator.apply(stringBuilder.toString()), list, int2IntFunction);
	}
}
