package net.minecraft.server.world;

import javax.annotation.Nullable;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Contract;

public class ChunkLevels {
	private static final int FULL = 33;
	private static final int BLOCK_TICKING = 32;
	private static final int ENTITY_TICKING = 31;
	private static final ChunkGenerationStep FULL_GENERATION_STEP = ChunkGenerationSteps.GENERATION.get(ChunkStatus.FULL);
	public static final int FULL_GENERATION_REQUIRED_LEVEL = FULL_GENERATION_STEP.accumulatedDependencies().getMaxLevel();
	public static final int INACCESSIBLE = 33 + FULL_GENERATION_REQUIRED_LEVEL;

	@Nullable
	public static ChunkStatus getStatus(int level) {
		return getStatusForAdditionalLevel(level - 33, null);
	}

	@Nullable
	@Contract("_,!null->!null;_,_->_")
	public static ChunkStatus getStatusForAdditionalLevel(int additionalLevel, @Nullable ChunkStatus emptyStatus) {
		if (additionalLevel > FULL_GENERATION_REQUIRED_LEVEL) {
			return emptyStatus;
		} else {
			return additionalLevel <= 0 ? ChunkStatus.FULL : FULL_GENERATION_STEP.accumulatedDependencies().get(additionalLevel);
		}
	}

	public static ChunkStatus getStatusForAdditionalLevel(int level) {
		return getStatusForAdditionalLevel(level, ChunkStatus.EMPTY);
	}

	public static int getLevelFromStatus(ChunkStatus status) {
		return 33 + FULL_GENERATION_STEP.getAdditionalLevel(status);
	}

	public static ChunkLevelType getType(int level) {
		if (level <= 31) {
			return ChunkLevelType.ENTITY_TICKING;
		} else if (level <= 32) {
			return ChunkLevelType.BLOCK_TICKING;
		} else {
			return level <= 33 ? ChunkLevelType.FULL : ChunkLevelType.INACCESSIBLE;
		}
	}

	public static int getLevelFromType(ChunkLevelType type) {
		return switch (type) {
			case INACCESSIBLE -> INACCESSIBLE;
			case FULL -> 33;
			case BLOCK_TICKING -> 32;
			case ENTITY_TICKING -> 31;
		};
	}

	public static boolean shouldTickEntities(int level) {
		return level <= 31;
	}

	public static boolean shouldTickBlocks(int level) {
		return level <= 32;
	}

	public static boolean isAccessible(int level) {
		return level <= INACCESSIBLE;
	}
}
