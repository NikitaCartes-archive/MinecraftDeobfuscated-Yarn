package net.minecraft;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public record class_8572(String id) {
	public static final class_8572 GENERIC = new class_8572("generic");
	public static final class_8572 LADDER = new class_8572("ladder");
	public static final class_8572 VINES = new class_8572("vines");
	public static final class_8572 WEEPING_VINES = new class_8572("weeping_vines");
	public static final class_8572 TWISTING_VINES = new class_8572("twisting_vines");
	public static final class_8572 SCAFFOLDING = new class_8572("scaffolding");
	public static final class_8572 OTHER_CLIMBABLE = new class_8572("other_climbable");
	public static final class_8572 WATER = new class_8572("water");

	public static class_8572 method_52196(BlockState blockState) {
		if (blockState.isOf(Blocks.LADDER) || blockState.isIn(BlockTags.TRAPDOORS)) {
			return LADDER;
		} else if (blockState.isOf(Blocks.VINE)) {
			return VINES;
		} else if (blockState.isOf(Blocks.WEEPING_VINES) || blockState.isOf(Blocks.WEEPING_VINES_PLANT)) {
			return WEEPING_VINES;
		} else if (blockState.isOf(Blocks.TWISTING_VINES) || blockState.isOf(Blocks.TWISTING_VINES_PLANT)) {
			return TWISTING_VINES;
		} else {
			return blockState.isOf(Blocks.SCAFFOLDING) ? SCAFFOLDING : OTHER_CLIMBABLE;
		}
	}

	@Nullable
	public static class_8572 method_52195(LivingEntity livingEntity) {
		Optional<BlockPos> optional = livingEntity.getClimbingPos();
		if (optional.isPresent()) {
			BlockState blockState = livingEntity.getWorld().getBlockState((BlockPos)optional.get());
			return method_52196(blockState);
		} else {
			return livingEntity.isTouchingWater() ? WATER : null;
		}
	}

	public String method_52194() {
		return "death.fell.accident." + this.id;
	}
}
