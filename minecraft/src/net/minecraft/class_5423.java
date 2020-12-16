package net.minecraft;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;

public interface class_5423 extends EntityView, WorldView, ModifiableTestableWorld {
	@Override
	default Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box, Predicate<Entity> predicate) {
		return EntityView.super.getEntityCollisions(entity, box, predicate);
	}

	@Override
	default boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
		return EntityView.super.intersectsEntities(except, shape);
	}

	@Override
	default BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
		return WorldView.super.getTopPosition(heightmap, pos);
	}

	DynamicRegistryManager getRegistryManager();

	default Optional<RegistryKey<Biome>> getBiomeKey(BlockPos pos) {
		return this.getRegistryManager().get(Registry.BIOME_KEY).getKey(this.getBiome(pos));
	}
}
