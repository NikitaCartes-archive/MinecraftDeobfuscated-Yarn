package net.minecraft.server.world;

import javax.annotation.Nullable;
import net.minecraft.class_9768;
import net.minecraft.class_9770;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Contract;

public class ChunkLevels {
	private static final int FULL = 33;
	private static final int BLOCK_TICKING = 32;
	private static final int ENTITY_TICKING = 31;
	private static final class_9770 field_51860 = class_9768.field_51900.method_60518(ChunkStatus.FULL);
	public static final int field_51859 = field_51860.accumulatedDependencies().method_60517();
	public static final int INACCESSIBLE = 33 + field_51859;

	@Nullable
	public static ChunkStatus getStatus(int level) {
		return method_60437(level - 33, null);
	}

	@Nullable
	@Contract("_,!null->!null;_,_->_")
	public static ChunkStatus method_60437(int i, @Nullable ChunkStatus chunkStatus) {
		if (i > field_51859) {
			return chunkStatus;
		} else {
			return i <= 0 ? ChunkStatus.FULL : field_51860.accumulatedDependencies().method_60514(i);
		}
	}

	public static ChunkStatus method_60438(int i) {
		return method_60437(i, ChunkStatus.EMPTY);
	}

	public static int getLevelFromStatus(ChunkStatus status) {
		return 33 + field_51860.method_60559(status);
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
