/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public enum ChatPreviewMode implements TranslatableOption
{
    OFF(0, "options.off"),
    LIVE(1, "options.chatPreview.live"),
    CONFIRM(2, "options.chatPreview.confirm");

    private static final ChatPreviewMode[] VALUES;
    private final int id;
    private final String translationKey;

    private ChatPreviewMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public static ChatPreviewMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    static {
        VALUES = (ChatPreviewMode[])Arrays.stream(ChatPreviewMode.values()).sorted(Comparator.comparingInt(ChatPreviewMode::getId)).toArray(ChatPreviewMode[]::new);
    }
}

