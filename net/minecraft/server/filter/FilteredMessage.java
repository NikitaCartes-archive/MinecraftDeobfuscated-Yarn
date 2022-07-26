/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import java.util.Objects;
import net.minecraft.class_7649;
import org.jetbrains.annotations.Nullable;

/**
 * A message from the {@link TextFilterer}.
 * 
 *  * @param raw the raw (or "original") message
 */
public record FilteredMessage(String raw, class_7649 mask) {
    public static final FilteredMessage EMPTY = FilteredMessage.method_45060("");

    public static FilteredMessage method_45060(String string) {
        return new FilteredMessage(string, class_7649.field_39942);
    }

    public static FilteredMessage method_45062(String string) {
        return new FilteredMessage(string, class_7649.field_39941);
    }

    @Nullable
    public String method_45059() {
        return this.mask.method_45089(this.raw);
    }

    public String method_45061() {
        return Objects.requireNonNullElse(this.method_45059(), "");
    }

    public boolean method_45063() {
        return !this.mask.method_45087();
    }
}

