package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public interface TutorialStepHandler {
	default void destroy() {
	}

	default void tick() {
	}

	default void method_4903(Input input) {
	}

	default void method_4901(double d, double e) {
	}

	default void method_4898(ClientWorld clientWorld, HitResult hitResult) {
	}

	default void onBlockAttacked(ClientWorld clientWorld, BlockPos blockPos, BlockState blockState, float f) {
	}

	default void onInventoryOpened() {
	}

	default void onSlotUpdate(ItemStack itemStack) {
	}
}
