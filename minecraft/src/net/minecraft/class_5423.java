package net.minecraft;

import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldView;

public interface class_5423 extends EntityView, WorldView, ModifiableTestableWorld {
	@Override
	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return EntityView.super.getEntityCollisions(entity, box, predicate);
	}

	@Override
	default boolean intersectsEntities(@Nullable Entity entity, VoxelShape shape) {
		return EntityView.super.intersectsEntities(entity, shape);
	}

	@Override
	default BlockPos getTopPosition(Heightmap.Type type, BlockPos pos) {
		return WorldView.super.getTopPosition(type, pos);
	}
}
