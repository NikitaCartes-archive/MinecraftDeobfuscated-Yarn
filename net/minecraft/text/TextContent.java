/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

/**
 * Represents type-specific content of text. It is stored in each tree node
 * in a text tree structure. Its implementations are immutable.
 */
public interface TextContent {
    /**
     * An empty text content.
     */
    public static final TextContent EMPTY = new TextContent(){

        public String toString() {
            return "empty";
        }
    };

    /**
     * Visits this content. Returns a value if the visitor terminates amid
     * the visit, or {@code Optional.empty()} if it proceeds.
     * 
     * @return {@code Optional.empty()} if the visit finished, or a terminating
     * result from the {@code visitor}
     * @see Text#visit(StringVisitable.StyledVisitor, Style)
     */
    default public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return Optional.empty();
    }

    /**
     * Visits this content. Returns a value if the visitor terminates amid
     * the visit, or {@code Optional.empty()} if it proceeds.
     * 
     * @return {@code Optional.empty()} if the visit finished, or a terminating
     * result from the {@code visitor}
     * @see Text#visit(StringVisitable.Visitor)
     */
    default public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return Optional.empty();
    }

    /**
     * Parses this content into a basic mutable text without custom style or
     * siblings. The resulting text may or may not have this content.
     */
    default public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        return MutableText.of(this);
    }
}

