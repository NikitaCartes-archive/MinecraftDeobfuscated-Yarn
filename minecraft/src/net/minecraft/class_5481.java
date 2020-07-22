package net.minecraft;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.Style;

@FunctionalInterface
public interface class_5481 {
	class_5481 field_26385 = characterVisitor -> true;

	@Environment(EnvType.CLIENT)
	boolean accept(CharacterVisitor characterVisitor);

	@Environment(EnvType.CLIENT)
	static class_5481 method_30741(int i, Style style) {
		return characterVisitor -> characterVisitor.accept(0, style, i);
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30747(String string, Style style) {
		return string.isEmpty() ? field_26385 : characterVisitor -> TextVisitFactory.visitForwards(string, style, characterVisitor);
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30754(String string, Style style, Int2IntFunction int2IntFunction) {
		return string.isEmpty() ? field_26385 : characterVisitor -> TextVisitFactory.visitBackwards(string, style, method_30745(characterVisitor, int2IntFunction));
	}

	@Environment(EnvType.CLIENT)
	static CharacterVisitor method_30745(CharacterVisitor characterVisitor, Int2IntFunction int2IntFunction) {
		return (i, style, j) -> characterVisitor.accept(i, style, int2IntFunction.apply(Integer.valueOf(j)));
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30742(class_5481 arg, class_5481 arg2) {
		return method_30752(arg, arg2);
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30749(List<class_5481> list) {
		int i = list.size();
		switch (i) {
			case 0:
				return field_26385;
			case 1:
				return (class_5481)list.get(0);
			case 2:
				return method_30752((class_5481)list.get(0), (class_5481)list.get(1));
			default:
				return method_30755(ImmutableList.copyOf(list));
		}
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30752(class_5481 arg, class_5481 arg2) {
		return characterVisitor -> arg.accept(characterVisitor) && arg2.accept(characterVisitor);
	}

	@Environment(EnvType.CLIENT)
	static class_5481 method_30755(List<class_5481> list) {
		return characterVisitor -> {
			for (class_5481 lv : list) {
				if (!lv.accept(characterVisitor)) {
					return false;
				}
			}

			return true;
		};
	}
}
