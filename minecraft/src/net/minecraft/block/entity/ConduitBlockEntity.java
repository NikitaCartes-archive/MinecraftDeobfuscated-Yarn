package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ConduitBlockEntity extends BlockEntity {
	private static final Block[] ACTIVATING_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
	public int ticks;
	private float ticksActive;
	private boolean active;
	private boolean eyeOpen;
	private final List<BlockPos> activatingBlocks = Lists.<BlockPos>newArrayList();
	@Nullable
	private LivingEntity targetEntity;
	@Nullable
	private UUID targetUuid;
	private long nextAmbientSoundTime;

	public ConduitBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.CONDUIT, blockPos, blockState);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		if (tag.containsUuid("Target")) {
			this.targetUuid = tag.getUuid("Target");
		} else {
			this.targetUuid = null;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		if (this.targetEntity != null) {
			tag.putUuid("Target", this.targetEntity.getUuid());
		}

		return tag;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 5, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public static void clientTick(World world, BlockPos blockPos, BlockState blockState, ConduitBlockEntity conduitBlockEntity) {
		conduitBlockEntity.ticks++;
		long l = world.getTime();
		List<BlockPos> list = conduitBlockEntity.activatingBlocks;
		if (l % 40L == 0L) {
			conduitBlockEntity.active = updateActivatingBlocks(world, blockPos, list);
			method_31676(conduitBlockEntity, list);
		}

		updateTargetEntity(world, blockPos, conduitBlockEntity);
		spawnNautilusParticles(world, blockPos, list, conduitBlockEntity.targetEntity, conduitBlockEntity.ticks);
		if (conduitBlockEntity.isActive()) {
			conduitBlockEntity.ticksActive++;
		}
	}

	public static void serverTick(World world, BlockPos blockPos, BlockState blockState, ConduitBlockEntity conduitBlockEntity) {
		conduitBlockEntity.ticks++;
		long l = world.getTime();
		List<BlockPos> list = conduitBlockEntity.activatingBlocks;
		if (l % 40L == 0L) {
			boolean bl = updateActivatingBlocks(world, blockPos, list);
			if (bl != conduitBlockEntity.active) {
				SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
				world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			conduitBlockEntity.active = bl;
			method_31676(conduitBlockEntity, list);
			if (bl) {
				givePlayersEffects(world, blockPos, list);
				attackHostileEntity(world, blockPos, blockState, list, conduitBlockEntity);
			}
		}

		if (conduitBlockEntity.isActive()) {
			if (l % 80L == 0L) {
				world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}

			if (l > conduitBlockEntity.nextAmbientSoundTime) {
				conduitBlockEntity.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
				world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	private static void method_31676(ConduitBlockEntity conduitBlockEntity, List<BlockPos> list) {
		conduitBlockEntity.setEyeOpen(list.size() >= 42);
	}

	private static boolean updateActivatingBlocks(World world, BlockPos blockPos, List<BlockPos> list) {
		list.clear();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos2 = blockPos.add(i, j, k);
					if (!world.isWater(blockPos2)) {
						return false;
					}
				}
			}
		}

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int kx = -2; kx <= 2; kx++) {
					int l = Math.abs(i);
					int m = Math.abs(j);
					int n = Math.abs(kx);
					if ((l > 1 || m > 1 || n > 1) && (i == 0 && (m == 2 || n == 2) || j == 0 && (l == 2 || n == 2) || kx == 0 && (l == 2 || m == 2))) {
						BlockPos blockPos3 = blockPos.add(i, j, kx);
						BlockState blockState = world.getBlockState(blockPos3);

						for (Block block : ACTIVATING_BLOCKS) {
							if (blockState.isOf(block)) {
								list.add(blockPos3);
							}
						}
					}
				}
			}
		}

		return list.size() >= 16;
	}

	private static void givePlayersEffects(World world, BlockPos blockPos, List<BlockPos> list) {
		int i = list.size();
		int j = i / 7 * 16;
		int k = blockPos.getX();
		int l = blockPos.getY();
		int m = blockPos.getZ();
		Box box = new Box((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
			.expand((double)j)
			.stretch(0.0, (double)world.getBottomSectionLimit(), 0.0);
		List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, box);
		if (!list2.isEmpty()) {
			for (PlayerEntity playerEntity : list2) {
				if (blockPos.isWithinDistance(playerEntity.getBlockPos(), (double)j) && playerEntity.isTouchingWaterOrRain()) {
					playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
				}
			}
		}
	}

	private static void attackHostileEntity(World world, BlockPos blockPos, BlockState blockState, List<BlockPos> list, ConduitBlockEntity conduitBlockEntity) {
		LivingEntity livingEntity = conduitBlockEntity.targetEntity;
		int i = list.size();
		if (i < 42) {
			conduitBlockEntity.targetEntity = null;
		} else if (conduitBlockEntity.targetEntity == null && conduitBlockEntity.targetUuid != null) {
			conduitBlockEntity.targetEntity = findTargetEntity(world, blockPos, conduitBlockEntity.targetUuid);
			conduitBlockEntity.targetUuid = null;
		} else if (conduitBlockEntity.targetEntity == null) {
			List<LivingEntity> list2 = world.getEntitiesByClass(
				LivingEntity.class, getAttackZone(blockPos), livingEntityx -> livingEntityx instanceof Monster && livingEntityx.isTouchingWaterOrRain()
			);
			if (!list2.isEmpty()) {
				conduitBlockEntity.targetEntity = (LivingEntity)list2.get(world.random.nextInt(list2.size()));
			}
		} else if (!conduitBlockEntity.targetEntity.isAlive() || !blockPos.isWithinDistance(conduitBlockEntity.targetEntity.getBlockPos(), 8.0)) {
			conduitBlockEntity.targetEntity = null;
		}

		if (conduitBlockEntity.targetEntity != null) {
			world.playSound(
				null,
				conduitBlockEntity.targetEntity.getX(),
				conduitBlockEntity.targetEntity.getY(),
				conduitBlockEntity.targetEntity.getZ(),
				SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET,
				SoundCategory.BLOCKS,
				1.0F,
				1.0F
			);
			conduitBlockEntity.targetEntity.damage(DamageSource.MAGIC, 4.0F);
		}

		if (livingEntity != conduitBlockEntity.targetEntity) {
			world.updateListeners(blockPos, blockState, blockState, 2);
		}
	}

	private static void updateTargetEntity(World world, BlockPos blockPos, ConduitBlockEntity conduitBlockEntity) {
		if (conduitBlockEntity.targetUuid == null) {
			conduitBlockEntity.targetEntity = null;
		} else if (conduitBlockEntity.targetEntity == null || !conduitBlockEntity.targetEntity.getUuid().equals(conduitBlockEntity.targetUuid)) {
			conduitBlockEntity.targetEntity = findTargetEntity(world, blockPos, conduitBlockEntity.targetUuid);
			if (conduitBlockEntity.targetEntity == null) {
				conduitBlockEntity.targetUuid = null;
			}
		}
	}

	private static Box getAttackZone(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		return new Box((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)).expand(8.0);
	}

	@Nullable
	private static LivingEntity findTargetEntity(World world, BlockPos blockPos, UUID uUID) {
		List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, getAttackZone(blockPos), livingEntity -> livingEntity.getUuid().equals(uUID));
		return list.size() == 1 ? (LivingEntity)list.get(0) : null;
	}

	private static void spawnNautilusParticles(World world, BlockPos blockPos, List<BlockPos> list, @Nullable Entity entity, int i) {
		Random random = world.random;
		double d = (double)(MathHelper.sin((float)(i + 35) * 0.1F) / 2.0F + 0.5F);
		d = (d * d + d) * 0.3F;
		Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.5 + d, (double)blockPos.getZ() + 0.5);

		for (BlockPos blockPos2 : list) {
			if (random.nextInt(50) == 0) {
				BlockPos blockPos3 = blockPos2.subtract(blockPos);
				float f = -0.5F + random.nextFloat() + (float)blockPos3.getX();
				float g = -2.0F + random.nextFloat() + (float)blockPos3.getY();
				float h = -0.5F + random.nextFloat() + (float)blockPos3.getZ();
				world.addParticle(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, (double)f, (double)g, (double)h);
			}
		}

		if (entity != null) {
			Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
			float j = (-0.5F + random.nextFloat()) * (3.0F + entity.getWidth());
			float k = -1.0F + random.nextFloat() * entity.getHeight();
			float f = (-0.5F + random.nextFloat()) * (3.0F + entity.getWidth());
			Vec3d vec3d3 = new Vec3d((double)j, (double)k, (double)f);
			world.addParticle(ParticleTypes.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
		}
	}

	public boolean isActive() {
		return this.active;
	}

	@Environment(EnvType.CLIENT)
	public boolean isEyeOpen() {
		return this.eyeOpen;
	}

	private void setEyeOpen(boolean eyeOpen) {
		this.eyeOpen = eyeOpen;
	}

	@Environment(EnvType.CLIENT)
	public float getRotation(float tickDelta) {
		return (this.ticksActive + tickDelta) * -0.0375F;
	}
}
