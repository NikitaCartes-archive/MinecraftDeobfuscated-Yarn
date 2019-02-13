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
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TagHelper;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ConduitBlockEntity extends BlockEntity implements Tickable {
	private static final Block[] ACTIVATING_BLOCKS = new Block[]{Blocks.field_10135, Blocks.field_10006, Blocks.field_10174, Blocks.field_10297};
	public int ticks;
	private float ticksActive;
	private boolean active;
	private boolean eyeOpen;
	private final List<BlockPos> activatingBlocks = Lists.<BlockPos>newArrayList();
	private LivingEntity targetEntity;
	private UUID targetUuid;
	private long nextAmbientSoundTime;

	public ConduitBlockEntity() {
		this(BlockEntityType.CONDUIT);
	}

	public ConduitBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("target_uuid")) {
			this.targetUuid = TagHelper.deserializeUuid(compoundTag.getCompound("target_uuid"));
		} else {
			this.targetUuid = null;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.targetEntity != null) {
			compoundTag.put("target_uuid", TagHelper.serializeUuid(this.targetEntity.getUuid()));
		}

		return compoundTag;
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

	@Override
	public void tick() {
		this.ticks++;
		long l = this.world.getTime();
		if (l % 40L == 0L) {
			this.setActive(this.updateActivatingBlocks());
			if (!this.world.isClient && this.isActive()) {
				this.givePlayersEffects();
				this.attackHostileEntity();
			}
		}

		if (l % 80L == 0L && this.isActive()) {
			this.playSound(SoundEvents.field_14632);
		}

		if (l > this.nextAmbientSoundTime && this.isActive()) {
			this.nextAmbientSoundTime = l + 60L + (long)this.world.getRandom().nextInt(40);
			this.playSound(SoundEvents.field_15071);
		}

		if (this.world.isClient) {
			this.updateTargetEntity();
			this.spawnNautilusParticles();
			if (this.isActive()) {
				this.ticksActive++;
			}
		}
	}

	private boolean updateActivatingBlocks() {
		this.activatingBlocks.clear();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					BlockPos blockPos = this.pos.add(i, j, k);
					if (!this.world.isWaterAt(blockPos)) {
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
						BlockPos blockPos2 = this.pos.add(i, j, kx);
						BlockState blockState = this.world.getBlockState(blockPos2);

						for (Block block : ACTIVATING_BLOCKS) {
							if (blockState.getBlock() == block) {
								this.activatingBlocks.add(blockPos2);
							}
						}
					}
				}
			}
		}

		this.setEyeOpen(this.activatingBlocks.size() >= 42);
		return this.activatingBlocks.size() >= 16;
	}

	private void givePlayersEffects() {
		int i = this.activatingBlocks.size();
		int j = i / 7 * 16;
		int k = this.pos.getX();
		int l = this.pos.getY();
		int m = this.pos.getZ();
		BoundingBox boundingBox = new BoundingBox((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
			.expand((double)j)
			.stretch(0.0, (double)this.world.getHeight(), 0.0);
		List<PlayerEntity> list = this.world.getVisibleEntities(PlayerEntity.class, boundingBox);
		if (!list.isEmpty()) {
			for (PlayerEntity playerEntity : list) {
				if (this.pos.distanceTo(new BlockPos(playerEntity)) <= (double)j && playerEntity.isInsideWaterOrRain()) {
					playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5927, 260, 0, true, true));
				}
			}
		}
	}

	private void attackHostileEntity() {
		LivingEntity livingEntity = this.targetEntity;
		int i = this.activatingBlocks.size();
		if (i < 42) {
			this.targetEntity = null;
		} else if (this.targetEntity == null && this.targetUuid != null) {
			this.targetEntity = this.findTargetEntity();
			this.targetUuid = null;
		} else if (this.targetEntity == null) {
			List<LivingEntity> list = this.world
				.getEntities(LivingEntity.class, this.getAttackZone(), livingEntityx -> livingEntityx instanceof Monster && livingEntityx.isInsideWaterOrRain());
			if (!list.isEmpty()) {
				this.targetEntity = (LivingEntity)list.get(this.world.random.nextInt(list.size()));
			}
		} else if (!this.targetEntity.isValid() || this.pos.distanceTo(new BlockPos(this.targetEntity)) > 8.0) {
			this.targetEntity = null;
		}

		if (this.targetEntity != null) {
			this.world.playSound(null, this.targetEntity.x, this.targetEntity.y, this.targetEntity.z, SoundEvents.field_15177, SoundCategory.field_15245, 1.0F, 1.0F);
			this.targetEntity.damage(DamageSource.MAGIC, 4.0F);
		}

		if (livingEntity != this.targetEntity) {
			BlockState blockState = this.getCachedState();
			this.world.updateListeners(this.pos, blockState, blockState, 2);
		}
	}

	private void updateTargetEntity() {
		if (this.targetUuid == null) {
			this.targetEntity = null;
		} else if (this.targetEntity == null || !this.targetEntity.getUuid().equals(this.targetUuid)) {
			this.targetEntity = this.findTargetEntity();
			if (this.targetEntity == null) {
				this.targetUuid = null;
			}
		}
	}

	private BoundingBox getAttackZone() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		return new BoundingBox((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1)).expand(8.0);
	}

	@Nullable
	private LivingEntity findTargetEntity() {
		List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getAttackZone(), livingEntity -> livingEntity.getUuid().equals(this.targetUuid));
		return list.size() == 1 ? (LivingEntity)list.get(0) : null;
	}

	private void spawnNautilusParticles() {
		Random random = this.world.random;
		float f = MathHelper.sin((float)(this.ticks + 35) * 0.1F) / 2.0F + 0.5F;
		f = (f * f + f) * 0.3F;
		Vec3d vec3d = new Vec3d((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 1.5F + f), (double)((float)this.pos.getZ() + 0.5F));

		for (BlockPos blockPos : this.activatingBlocks) {
			if (random.nextInt(50) == 0) {
				float g = -0.5F + random.nextFloat();
				float h = -2.0F + random.nextFloat();
				float i = -0.5F + random.nextFloat();
				BlockPos blockPos2 = blockPos.subtract(this.pos);
				Vec3d vec3d2 = new Vec3d((double)g, (double)h, (double)i).add((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				this.world.addParticle(ParticleTypes.field_11229, vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
			}
		}

		if (this.targetEntity != null) {
			Vec3d vec3d3 = new Vec3d(this.targetEntity.x, this.targetEntity.y + (double)this.targetEntity.getEyeHeight(), this.targetEntity.z);
			float j = (-0.5F + random.nextFloat()) * (3.0F + this.targetEntity.getWidth());
			float g = -1.0F + random.nextFloat() * this.targetEntity.getHeight();
			float h = (-0.5F + random.nextFloat()) * (3.0F + this.targetEntity.getWidth());
			Vec3d vec3d4 = new Vec3d((double)j, (double)g, (double)h);
			this.world.addParticle(ParticleTypes.field_11229, vec3d3.x, vec3d3.y, vec3d3.z, vec3d4.x, vec3d4.y, vec3d4.z);
		}
	}

	public boolean isActive() {
		return this.active;
	}

	@Environment(EnvType.CLIENT)
	public boolean isEyeOpen() {
		return this.eyeOpen;
	}

	private void setActive(boolean bl) {
		if (bl != this.active) {
			this.playSound(bl ? SoundEvents.field_14700 : SoundEvents.field_14979);
		}

		this.active = bl;
	}

	private void setEyeOpen(boolean bl) {
		this.eyeOpen = bl;
	}

	@Environment(EnvType.CLIENT)
	public float getRotation(float f) {
		return (this.ticksActive + f) * -0.0375F;
	}

	public void playSound(SoundEvent soundEvent) {
		this.world.playSound(null, this.pos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}
}
