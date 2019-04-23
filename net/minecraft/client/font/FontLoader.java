/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface FontLoader {
    @Nullable
    public Font load(ResourceManager var1);
}

