/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

public interface class_7417 {
    public static final class_7417 field_39004 = new class_7417(){

        public String toString() {
            return "empty";
        }
    };

    default public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return Optional.empty();
    }

    default public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
        return Optional.empty();
    }

    default public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
        return MutableText.method_43477(this);
    }
}

