/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface UnclampedModelPredicateProvider
extends ModelPredicateProvider {
    @Override
    @Deprecated
    default public float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
        return MathHelper.clamp(this.unclampedCall(itemStack, clientWorld, livingEntity, i), 0.0f, 1.0f);
    }

    public float unclampedCall(ItemStack var1, @Nullable ClientWorld var2, @Nullable LivingEntity var3, int var4);
}

