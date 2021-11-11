package net.minecraft.world;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.biome.Biome;

/**
 * A world view or {@link World}'s superinterface that exposes access to
 * a registry manager.
 * 
 * @see #getRegistryManager()
 */
public interface RegistryWorldView extends EntityView, WorldView, ModifiableTestableWorld {
	@Override
	default <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
		return WorldView.super.getBlockEntity(pos, type);
	}

	@Override
	default List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box) {
		return EntityView.super.getEntityCollisions(entity, box);
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
