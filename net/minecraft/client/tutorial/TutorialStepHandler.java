/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public interface TutorialStepHandler {
    default public void destroy() {
    }

    default public void tick() {
    }

    default public void onMovement(Input input) {
    }

    default public void onMouseUpdate(double d, double e) {
    }

    default public void onTarget(ClientWorld clientWorld, HitResult hitResult) {
    }

    default public void onBlockAttacked(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
    }

    default public void onInventoryOpened() {
    }

    default public void onSlotUpdate(ItemStack itemStack) {
    }
}

