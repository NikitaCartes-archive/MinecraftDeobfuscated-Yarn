/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData headerSignature, @Nullable MessageIndicator indicator) {
    @Nullable
    public MessageSignatureData headerSignature() {
        return this.headerSignature;
    }

    @Nullable
    public MessageIndicator indicator() {
        return this.indicator;
    }

    @Environment(value=EnvType.CLIENT)
    public record Visible(int addedTime, OrderedText content, @Nullable MessageIndicator indicator, boolean endOfEntry) {
        @Nullable
        public MessageIndicator indicator() {
            return this.indicator;
        }
    }
}

