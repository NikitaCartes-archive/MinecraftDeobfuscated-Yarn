package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldListener;

public class PathingCoordinator implements WorldListener {
	private final List<EntityNavigation> entityPathings = Lists.<EntityNavigation>newArrayList();

	@Override
	public void onBlockUpdate(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2, int i) {
		if (this.method_6368(blockView, blockPos, blockState, blockState2)) {
			int j = 0;

			for (int k = this.entityPathings.size(); j < k; j++) {
				EntityNavigation entityNavigation = (EntityNavigation)this.entityPathings.get(j);
				if (entityNavigation != null && !entityNavigation.isIdle()) {
					Path path = entityNavigation.method_6345();
					if (path != null && !path.isFinished() && path.getPathLength() != 0) {
						PathNode pathNode = entityNavigation.field_6681.getEnd();
						double d = blockPos.squaredDistanceTo(
							((double)pathNode.x + entityNavigation.entity.x) / 2.0,
							((double)pathNode.y + entityNavigation.entity.y) / 2.0,
							((double)pathNode.z + entityNavigation.entity.z) / 2.0
						);
						int l = (path.getPathLength() - path.getCurrentNodeIndex()) * (path.getPathLength() - path.getCurrentNodeIndex());
						if (d < (double)l) {
							entityNavigation.method_6356();
						}
					}
				}
			}
		}
	}

	protected boolean method_6368(BlockView blockView, BlockPos blockPos, BlockState blockState, BlockState blockState2) {
		VoxelShape voxelShape = blockState.getCollisionShape(blockView, blockPos);
		VoxelShape voxelShape2 = blockState2.getCollisionShape(blockView, blockPos);
		return VoxelShapes.compareShapes(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME);
	}

	@Override
	public void onSound(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, double d, double e, double f, float g, float h) {
	}

	@Override
	public void onSoundFromEntity(@Nullable PlayerEntity playerEntity, SoundEvent soundEvent, SoundCategory soundCategory, Entity entity, float f, float g) {
	}

	@Override
	public void method_8568(ParticleParameters particleParameters, boolean bl, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void method_8563(ParticleParameters particleParameters, boolean bl, boolean bl2, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public void onEntityAdded(Entity entity) {
		if (entity instanceof MobEntity) {
			this.entityPathings.add(((MobEntity)entity).getNavigation());
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		if (entity instanceof MobEntity) {
			this.entityPathings.remove(((MobEntity)entity).getNavigation());
		}
	}

	@Override
	public void playRecord(SoundEvent soundEvent, BlockPos blockPos) {
	}

	@Override
	public void onGlobalWorldEvent(int i, BlockPos blockPos, int j) {
	}

	@Override
	public void onWorldEvent(PlayerEntity playerEntity, int i, BlockPos blockPos, int j) {
	}

	@Override
	public void onBlockBreakingStage(int i, BlockPos blockPos, int j) {
	}
}
