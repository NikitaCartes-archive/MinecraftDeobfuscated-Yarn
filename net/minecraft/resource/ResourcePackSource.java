/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
    public static final ResourcePackSource PACK_SOURCE_NONE = ResourcePackSource.onlyName();
    public static final ResourcePackSource PACK_SOURCE_BUILTIN = ResourcePackSource.nameAndSource("pack.source.builtin");
    public static final ResourcePackSource PACK_SOURCE_WORLD = ResourcePackSource.nameAndSource("pack.source.world");
    public static final ResourcePackSource PACK_SOURCE_SERVER = ResourcePackSource.nameAndSource("pack.source.server");

    public Text decorate(Text var1);

    public static ResourcePackSource onlyName() {
        return name -> name;
    }

    public static ResourcePackSource nameAndSource(String source) {
        MutableText text = Text.method_43471(source);
        return name -> Text.method_43469("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
    }
}

