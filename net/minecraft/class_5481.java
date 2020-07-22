/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CharacterVisitor;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.Style;

@FunctionalInterface
public interface class_5481 {
    public static final class_5481 field_26385 = characterVisitor -> true;

    @Environment(value=EnvType.CLIENT)
    public boolean accept(CharacterVisitor var1);

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30741(int i, Style style) {
        return characterVisitor -> characterVisitor.accept(0, style, i);
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30747(String string, Style style) {
        if (string.isEmpty()) {
            return field_26385;
        }
        return characterVisitor -> TextVisitFactory.visitForwards(string, style, characterVisitor);
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30754(String string, Style style, Int2IntFunction int2IntFunction) {
        if (string.isEmpty()) {
            return field_26385;
        }
        return characterVisitor -> TextVisitFactory.visitBackwards(string, style, class_5481.method_30745(characterVisitor, int2IntFunction));
    }

    @Environment(value=EnvType.CLIENT)
    public static CharacterVisitor method_30745(CharacterVisitor characterVisitor, Int2IntFunction int2IntFunction) {
        return (i, style, j) -> characterVisitor.accept(i, style, (Integer)int2IntFunction.apply(j));
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30742(class_5481 arg, class_5481 arg2) {
        return class_5481.method_30752(arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30749(List<class_5481> list) {
        int i = list.size();
        switch (i) {
            case 0: {
                return field_26385;
            }
            case 1: {
                return list.get(0);
            }
            case 2: {
                return class_5481.method_30752(list.get(0), list.get(1));
            }
        }
        return class_5481.method_30755(ImmutableList.copyOf(list));
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30752(class_5481 arg, class_5481 arg2) {
        return characterVisitor -> arg.accept(characterVisitor) && arg2.accept(characterVisitor);
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5481 method_30755(List<class_5481> list) {
        return characterVisitor -> {
            for (class_5481 lv : list) {
                if (lv.accept(characterVisitor)) continue;
                return false;
            }
            return true;
        };
    }
}

