/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;

@Environment(value=EnvType.CLIENT)
public interface OrderableTooltip {
    public Optional<List<OrderedText>> getOrderedTooltip();
}

