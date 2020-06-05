/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.util.Unit;

/**
 * An object that can supply strings to a visitor, with or without a style
 * context, for rendering the strings.
 */
public interface StringRenderable {
    /**
     * Convenience object indicating the termination of a string visit.
     */
    public static final Optional<Unit> TERMINATE_VISIT = Optional.of(Unit.INSTANCE);
    /**
     * An empty renderable that does not call the visitors.
     */
    public static final StringRenderable EMPTY = new StringRenderable(){

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

    /**
     * Supplies this renderable's literal content to the visitor.
     * 
     * @return {@code Optional.empty()} if the visit finished, or a terminating
     * result from the {@code visitor}
     * 
     * @param visitor the visitor
     */
    public <T> Optional<T> visit(Visitor<T> var1);

    /**
     * Supplies this renderable's literal content and contextual style to
     * the visitor.
     * 
     * @return {@code Optional.empty()} if the visit finished, or a terminating
     * result from the {@code visitor}
     * 
     * @param styledVisitor the visitor
     * @param style the contextual style
     */
    @Environment(value=EnvType.CLIENT)
    public <T> Optional<T> visit(StyledVisitor<T> var1, Style var2);

    /**
     * Creates a renderable from a plain string.
     * 
     * @param string the plain string
     */
    public static StringRenderable plain(final String string) {
        return new StringRenderable(){

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

    /**
     * Creates a visitable from a plain string and a root style.
     * 
     * @param string the plain string
     * @param style the root style
     */
    @Environment(value=EnvType.CLIENT)
    public static StringRenderable styled(final String string, final Style style) {
        return new StringRenderable(){

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

    /**
     * Concats multiple string renderables by the order they appear in the array.
     * 
     * @param visitables an array or varargs of visitables
     */
    @Environment(value=EnvType.CLIENT)
    public static StringRenderable concat(StringRenderable ... visitables) {
        return StringRenderable.concat(ImmutableList.copyOf(visitables));
    }

    /**
     * Concats multiple string renderables by the order they appear in the list.
     * 
     * @param visitables a list of visitables
     */
    @Environment(value=EnvType.CLIENT)
    public static StringRenderable concat(final List<StringRenderable> visitables) {
        return new StringRenderable(){

            @Override
            public <T> Optional<T> visit(Visitor<T> visitor) {
                for (StringRenderable stringRenderable : visitables) {
                    Optional<T> optional = stringRenderable.visit(visitor);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> visit(StyledVisitor<T> styledVisitor, Style style) {
                for (StringRenderable stringRenderable : visitables) {
                    Optional<T> optional = stringRenderable.visit(styledVisitor, style);
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

