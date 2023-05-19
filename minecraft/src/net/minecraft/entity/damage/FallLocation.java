package net.minecraft.entity.damage;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public record FallLocation(String id) {
	public static final FallLocation GENERIC = new FallLocation("generic");
	public static final FallLocation LADDER = new FallLocation("ladder");
	public static final FallLocation VINES = new FallLocation("vines");
	public static final FallLocation WEEPING_VINES = new FallLocation("weeping_vines");
	public static final FallLocation TWISTING_VINES = new FallLocation("twisting_vines");
	public static final FallLocation SCAFFOLDING = new FallLocation("scaffolding");
	public static final FallLocation OTHER_CLIMBABLE = new FallLocation("other_climbable");
	public static final FallLocation WATER = new FallLocation("water");

	public static FallLocation fromBlockState(BlockState state) {
		if (state.isOf(Blocks.LADDER) || state.isIn(BlockTags.TRAPDOORS)) {
			return LADDER;
		} else if (state.isOf(Blocks.VINE)) {
			return VINES;
		} else if (state.isOf(Blocks.WEEPING_VINES) || state.isOf(Blocks.WEEPING_VINES_PLANT)) {
			return WEEPING_VINES;
		} else if (state.isOf(Blocks.TWISTING_VINES) || state.isOf(Blocks.TWISTING_VINES_PLANT)) {
			return TWISTING_VINES;
		} else {
			return state.isOf(Blocks.SCAFFOLDING) ? SCAFFOLDING : OTHER_CLIMBABLE;
		}
	}

	@Nullable
	public static FallLocation fromEntity(LivingEntity entity) {
		Optional<BlockPos> optional = entity.getClimbingPos();
		if (optional.isPresent()) {
			BlockState blockState = entity.getWorld().getBlockState((BlockPos)optional.get());
			return fromBlockState(blockState);
		} else {
			return entity.isTouchingWater() ? WATER : null;
		}
	}

	public String getDeathMessageKey() {
		return "death.fell.accident." + this.id;
	}
}
