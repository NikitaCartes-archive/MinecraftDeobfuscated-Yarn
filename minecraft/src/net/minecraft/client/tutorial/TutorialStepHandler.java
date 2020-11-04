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

	default void onMovement(Input input) {
	}

	default void onMouseUpdate(double deltaX, double deltaY) {
	}

	default void onTarget(ClientWorld world, HitResult hitResult) {
	}

	default void onBlockAttacked(ClientWorld client, BlockPos pos, BlockState state, float f) {
	}

	default void onInventoryOpened() {
	}

	default void onSlotUpdate(ItemStack stack) {
	}
}
