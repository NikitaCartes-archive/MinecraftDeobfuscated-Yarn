/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.util.Unit;

public interface class_5348 {
    public static final Optional<Unit> field_25309 = Optional.of(Unit.INSTANCE);
    public static final class_5348 field_25310 = new class_5348(){

        @Override
        public <T> Optional<T> visit(Visitor<T> visitor) {
            return Optional.empty();
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
            return Optional.empty();
        }
    };

    public <T> Optional<T> visit(Visitor<T> var1);

    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visit(StyledVisitor<T> var1, Style var2);

    public static class_5348 method_29430(final String string) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> visitor) {
                return visitor.accept(string);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
                return styledVisitor.accept(style, string);
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29431(final String string, final Style style) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> visitor) {
                return visitor.accept(string);
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style2) {
                return styledVisitor.accept(style.withParent(style2), string);
            }
        };
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29433(class_5348 ... args) {
        return class_5348.method_29432(ImmutableList.copyOf(args));
    }

    @Environment(value=EnvType.CLIENT)
    public static class_5348 method_29432(final List<class_5348> list) {
        return new class_5348(){

            @Override
            public <T> Optional<T> visit(Visitor<T> visitor) {
                for (class_5348 lv : list) {
                    Optional<T> optional = lv.visit(visitor);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
                for (class_5348 lv : list) {
                    Optional<T> optional = lv.visit(styledVisitor, style);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }
        };
    }

    public static interface Visitor<T> {
        public Optional<T> accept(String var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface StyledVisitor<T> {
        public Optional<T> accept(Style var1, String var2);
    }
}

