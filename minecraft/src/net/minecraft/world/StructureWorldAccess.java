package net.minecraft.world;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureWorldAccess extends ServerWorldAccess {
	long getSeed();

	List<? extends StructureStart<?>> getStructures(ChunkSectionPos pos, StructureFeature<?> feature);

	/**
	 * {@return {@code true} if the given position is an accessible position
	 * for the {@code setBlockState} function}
	 */
	default boolean isValidForSetBlock(BlockPos pos) {
		return true;
	}

	default void method_36972(@Nullable Supplier<String> supplier) {
	}
}
