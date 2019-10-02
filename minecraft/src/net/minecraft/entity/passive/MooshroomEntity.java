package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity {
	private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
	private StatusEffect stewEffect;
	private int stewEffectDuration;
	private UUID lightningId;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, WorldView worldView) {
		return worldView.getBlockState(blockPos.method_10074()).getBlock() == Blocks.MYCELIUM ? 10.0F : worldView.getBrightness(blockPos) - 0.5F;
	}

	public static boolean canSpawn(EntityType<MooshroomEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getBlockState(blockPos.method_10074()).getBlock() == Blocks.MYCELIUM && iWorld.getBaseLightLevel(blockPos, 0) > 8;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		UUID uUID = lightningEntity.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setType(this.getMooshroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
			this.lightningId = uUID;
			this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TYPE, MooshroomEntity.Type.RED.name);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.BOWL && this.getBreedingAge() >= 0 && !playerEntity.abilities.creativeMode) {
			itemStack.decrement(1);
			boolean bl = false;
			ItemStack itemStack2;
			if (this.stewEffect != null) {
				bl = true;
				itemStack2 = new ItemStack(Items.SUSPICIOUS_STEW);
				SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
				this.stewEffect = null;
				this.stewEffectDuration = 0;
			} else {
				itemStack2 = new ItemStack(Items.MUSHROOM_STEW);
			}

			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
			} else {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return true;
		} else if (itemStack.getItem() == Items.SHEARS && this.getBreedingAge() >= 0) {
			this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double)(this.getHeight() / 2.0F), this.z, 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.remove();
				CowEntity cowEntity = EntityType.COW.create(this.world);
				cowEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.bodyYaw = this.bodyYaw;
				if (this.hasCustomName()) {
					cowEntity.setCustomName(this.getCustomName());
				}

				this.world.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world
						.spawnEntity(new ItemEntity(this.world, this.x, this.y + (double)this.getHeight(), this.z, new ItemStack(this.getMooshroomType().mushroom.getBlock())));
				}

				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
				this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
			}

			return true;
		} else {
			if (this.getMooshroomType() == MooshroomEntity.Type.BROWN && itemStack.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
				if (this.stewEffect != null) {
					for (int j = 0; j < 2; j++) {
						this.world
							.addParticle(
								ParticleTypes.SMOKE,
								this.x + (double)(this.random.nextFloat() / 2.0F),
								this.y + (double)(this.getHeight() / 2.0F),
								this.z + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}
				} else {
					Pair<StatusEffect, Integer> pair = this.getStewEffectFrom(itemStack);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					for (int i = 0; i < 4; i++) {
						this.world
							.addParticle(
								ParticleTypes.EFFECT,
								this.x + (double)(this.random.nextFloat() / 2.0F),
								this.y + (double)(this.getHeight() / 2.0F),
								this.z + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}

					this.stewEffect = pair.getLeft();
					this.stewEffectDuration = pair.getRight();
					this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
				}
			}

			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putString("Type", this.getMooshroomType().name);
		if (this.stewEffect != null) {
			compoundTag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
			compoundTag.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setType(MooshroomEntity.Type.fromName(compoundTag.getString("Type")));
		if (compoundTag.contains("EffectId", 1)) {
			this.stewEffect = StatusEffect.byRawId(compoundTag.getByte("EffectId"));
		}

		if (compoundTag.contains("EffectDuration", 3)) {
			this.stewEffectDuration = compoundTag.getInt("EffectDuration");
		}
	}

	private Pair<StatusEffect, Integer> getStewEffectFrom(ItemStack itemStack) {
		FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
		return Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration());
	}

	private void setType(MooshroomEntity.Type type) {
		this.dataTracker.set(TYPE, type.name);
	}

	public MooshroomEntity.Type getMooshroomType() {
		return MooshroomEntity.Type.fromName(this.dataTracker.get(TYPE));
	}

	public MooshroomEntity method_6495(PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.create(this.world);
		mooshroomEntity.setType(this.chooseBabyType((MooshroomEntity)passiveEntity));
		return mooshroomEntity;
	}

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroomEntity) {
		MooshroomEntity.Type type = this.getMooshroomType();
		MooshroomEntity.Type type2 = mooshroomEntity.getMooshroomType();
		MooshroomEntity.Type type3;
		if (type == type2 && this.random.nextInt(1024) == 0) {
			type3 = type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
		} else {
			type3 = this.random.nextBoolean() ? type : type2;
		}

		return type3;
	}

	public static enum Type {
		RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
		BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

		private final String name;
		private final BlockState mushroom;

		private Type(String string2, BlockState blockState) {
			this.name = string2;
			this.mushroom = blockState;
		}

		@Environment(EnvType.CLIENT)
		public BlockState getMushroomState() {
			return this.mushroom;
		}

		private static MooshroomEntity.Type fromName(String string) {
			for (MooshroomEntity.Type type : values()) {
				if (type.name.equals(string)) {
					return type;
				}
			}

			return RED;
		}
	}
}
