/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface class_5352 {
    public static final class_5352 field_25347 = class_5352.method_29485();
    public static final class_5352 field_25348 = class_5352.method_29486("pack.source.builtin");
    public static final class_5352 field_25349 = class_5352.method_29486("pack.source.world");
    public static final class_5352 field_25350 = class_5352.method_29486("pack.source.server");

    public Text decorate(Text var1);

    public static class_5352 method_29485() {
        return text -> text;
    }

    public static class_5352 method_29486(String string) {
        TranslatableText text = new TranslatableText(string);
        return text2 -> new TranslatableText("pack.nameAndSource", text2, text);
    }
}

