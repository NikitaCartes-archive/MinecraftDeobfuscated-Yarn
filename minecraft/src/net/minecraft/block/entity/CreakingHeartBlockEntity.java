package net.minecraft.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.TransientCreakingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CreakingHeartBlockEntity extends BlockEntity {
	private static final int field_54776 = 32;
	public static final int field_54775 = 1024;
	private static final int field_54777 = 1156;
	private static final int field_54778 = 16;
	private static final int field_54779 = 8;
	private static final int field_54780 = 5;
	private static final int field_54781 = 20;
	private static final int field_54782 = 100;
	private static final int field_54783 = 10;
	private static final int field_54784 = 10;
	private static final int field_54785 = 50;
	@Nullable
	private TransientCreakingEntity creakingPuppet;
	private int creakingUpdateTimer;
	private int trailParticlesSpawnTimer;
	@Nullable
	private Vec3d lastCreakingPuppetPos;

	public CreakingHeartBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CREAKING_HEART, pos, state);
	}

	public static void tick(World world, BlockPos pos, BlockState state, CreakingHeartBlockEntity blockEntity) {
		if (blockEntity.trailParticlesSpawnTimer > 0) {
			if (blockEntity.trailParticlesSpawnTimer > 50) {
				blockEntity.spawnTrailParticles((ServerWorld)world, 1, true);
				blockEntity.spawnTrailParticles((ServerWorld)world, 1, false);
			}

			if (blockEntity.trailParticlesSpawnTimer % 10 == 0 && world instanceof ServerWorld serverWorld && blockEntity.lastCreakingPuppetPos != null) {
				if (blockEntity.creakingPuppet != null) {
					blockEntity.lastCreakingPuppetPos = blockEntity.creakingPuppet.getBoundingBox().getCenter();
				}

				Vec3d vec3d = Vec3d.ofCenter(pos);
				float f = 0.2F + 0.8F * (float)(100 - blockEntity.trailParticlesSpawnTimer) / 100.0F;
				Vec3d vec3d2 = vec3d.subtract(blockEntity.lastCreakingPuppetPos).multiply((double)f).add(blockEntity.lastCreakingPuppetPos);
				BlockPos blockPos = BlockPos.ofFloored(vec3d2);
				float g = (float)blockEntity.trailParticlesSpawnTimer / 2.0F / 100.0F + 0.5F;
				serverWorld.playSound(null, blockPos, SoundEvents.BLOCK_CREAKING_HEART_HURT, SoundCategory.BLOCKS, g, 1.0F);
			}

			blockEntity.trailParticlesSpawnTimer--;
		}

		if (blockEntity.creakingUpdateTimer-- < 0) {
			blockEntity.creakingUpdateTimer = 20;
			if (blockEntity.creakingPuppet != null) {
				if (CreakingHeartBlock.isWorldNaturalAndNight(world) && !(blockEntity.creakingPuppet.squaredDistanceTo(Vec3d.ofBottomCenter(pos)) > 1156.0)) {
					if (blockEntity.creakingPuppet.isRemoved()) {
						blockEntity.creakingPuppet = null;
					}

					if (!CreakingHeartBlock.shouldBeEnabled(state, world, pos) && blockEntity.creakingPuppet == null) {
						world.setBlockState(pos, state.with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.DISABLED), Block.NOTIFY_ALL);
					}
				} else {
					blockEntity.onBreak(null);
				}
			} else if (!CreakingHeartBlock.shouldBeEnabled(state, world, pos)) {
				world.setBlockState(pos, state.with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.DISABLED), Block.NOTIFY_ALL);
			} else {
				if (!CreakingHeartBlock.isWorldNaturalAndNight(world)) {
					if (state.get(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.Creaking.ACTIVE) {
						world.setBlockState(pos, state.with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.DORMANT), Block.NOTIFY_ALL);
						return;
					}
				} else if (state.get(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.Creaking.DORMANT) {
					world.setBlockState(pos, state.with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.ACTIVE), Block.NOTIFY_ALL);
					return;
				}

				if (state.get(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.Creaking.ACTIVE) {
					if (world.getDifficulty() != Difficulty.PEACEFUL) {
						PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 32.0, false);
						if (playerEntity != null) {
							blockEntity.creakingPuppet = spawnCreakingPuppet((ServerWorld)world, blockEntity);
							if (blockEntity.creakingPuppet != null) {
								blockEntity.creakingPuppet.playSound(SoundEvents.ENTITY_CREAKING_SPAWN);
								world.playSound(null, blockEntity.getPos(), SoundEvents.BLOCK_CREAKING_HEART_SPAWN, SoundCategory.BLOCKS, 1.0F, 1.0F);
							}
						}
					}
				}
			}
		}
	}

	@Nullable
	private static TransientCreakingEntity spawnCreakingPuppet(ServerWorld world, CreakingHeartBlockEntity blockEntity) {
		BlockPos blockPos = blockEntity.getPos();
		Optional<TransientCreakingEntity> optional = LargeEntitySpawnHelper.trySpawnAt(
			EntityType.CREAKING_TRANSIENT, SpawnReason.SPAWNER, world, blockPos, 5, 16, 8, LargeEntitySpawnHelper.Requirements.CREAKING
		);
		if (optional.isEmpty()) {
			return null;
		} else {
			TransientCreakingEntity transientCreakingEntity = (TransientCreakingEntity)optional.get();
			world.emitGameEvent(transientCreakingEntity, GameEvent.ENTITY_PLACE, transientCreakingEntity.getPos());
			transientCreakingEntity.playSpawnEffects();
			transientCreakingEntity.setHeartPos(blockPos);
			return transientCreakingEntity;
		}
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		return this.createComponentlessNbt(registries);
	}

	public void onPuppetDamage() {
		if (this.creakingPuppet != null) {
			if (this.world instanceof ServerWorld serverWorld) {
				this.spawnTrailParticles(serverWorld, 20, false);
				this.trailParticlesSpawnTimer = 100;
				this.lastCreakingPuppetPos = this.creakingPuppet.getBoundingBox().getCenter();
			}
		}
	}

	private void spawnTrailParticles(ServerWorld world, int count, boolean bl) {
		if (this.creakingPuppet != null) {
			int i = bl ? 16545810 : 6250335;
			Random random = world.random;

			for (double d = 0.0; d < (double)count; d++) {
				Vec3d vec3d = this.creakingPuppet
					.getBoundingBox()
					.getMinPos()
					.add(
						random.nextDouble() * this.creakingPuppet.getBoundingBox().getLengthX(),
						random.nextDouble() * this.creakingPuppet.getBoundingBox().getLengthY(),
						random.nextDouble() * this.creakingPuppet.getBoundingBox().getLengthZ()
					);
				Vec3d vec3d2 = Vec3d.of(this.getPos()).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
				if (bl) {
					Vec3d vec3d3 = vec3d;
					vec3d = vec3d2;
					vec3d2 = vec3d3;
				}

				TrailParticleEffect trailParticleEffect = new TrailParticleEffect(vec3d2, i);
				world.spawnParticles(trailParticleEffect, vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
			}
		}
	}

	public void onBreak(@Nullable DamageSource damageSource) {
		if (this.creakingPuppet != null) {
			this.creakingPuppet.damageFromHeart(damageSource);
			this.creakingPuppet = null;
		}
	}

	public boolean isPuppet(CreakingEntity creaking) {
		return this.creakingPuppet == creaking;
	}
}
