/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public enum ChatVisibility implements TranslatableOption
{
    FULL(0, "options.chat.visibility.full"),
    SYSTEM(1, "options.chat.visibility.system"),
    HIDDEN(2, "options.chat.visibility.hidden");

    private static final IntFunction<ChatVisibility> BY_ID;
    private final int id;
    private final String translationKey;

    private ChatVisibility(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public static ChatVisibility byId(int id) {
        return BY_ID.apply(id);
    }

    static {
        BY_ID = ValueLists.createIdToValueFunction(ChatVisibility::getId, ChatVisibility.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

