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
	public float getPathfindingFavor(BlockPos pos, WorldView worldView) {
		return worldView.getBlockState(pos.method_10074()).getBlock() == Blocks.MYCELIUM ? 10.0F : worldView.getBrightness(pos) - 0.5F;
	}

	public static boolean canSpawn(EntityType<MooshroomEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return world.getBlockState(pos.method_10074()).getBlock() == Blocks.MYCELIUM && world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public void onStruckByLightning(LightningEntity lightning) {
		UUID uUID = lightning.getUuid();
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
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.BOWL && !this.isBaby() && !player.abilities.creativeMode) {
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
				player.setStackInHand(hand, itemStack2);
			} else if (!player.inventory.insertStack(itemStack2)) {
				player.dropItem(itemStack2, false);
			}

			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
			} else {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return true;
		} else if (itemStack.getItem() == Items.SHEARS && !this.isBaby()) {
			this.world.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getHeightAt(0.5), this.getZ(), 0.0, 0.0, 0.0);
			if (!this.world.isClient) {
				this.remove();
				CowEntity cowEntity = EntityType.COW.create(this.world);
				cowEntity.setPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
				cowEntity.setHealth(this.getHealth());
				cowEntity.bodyYaw = this.bodyYaw;
				if (this.hasCustomName()) {
					cowEntity.setCustomName(this.getCustomName());
					cowEntity.setCustomNameVisible(this.isCustomNameVisible());
				}

				if (this.isPersistent()) {
					cowEntity.setPersistent();
				}

				cowEntity.setInvulnerable(this.isInvulnerable());
				this.world.spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.world
						.spawnEntity(new ItemEntity(this.world, this.getX(), this.getHeightAt(1.0), this.getZ(), new ItemStack(this.getMooshroomType().mushroom.getBlock())));
				}

				itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
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
								this.getX() + (double)(this.random.nextFloat() / 2.0F),
								this.getHeightAt(0.5),
								this.getZ() + (double)(this.random.nextFloat() / 2.0F),
								0.0,
								(double)(this.random.nextFloat() / 5.0F),
								0.0
							);
					}
				} else {
					Pair<StatusEffect, Integer> pair = this.getStewEffectFrom(itemStack);
					if (!player.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					for (int i = 0; i < 4; i++) {
						this.world
							.addParticle(
								ParticleTypes.EFFECT,
								this.getX() + (double)(this.random.nextFloat() / 2.0F),
								this.getHeightAt(0.5),
								this.getZ() + (double)(this.random.nextFloat() / 2.0F),
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

			return super.interactMob(player, hand);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putString("Type", this.getMooshroomType().name);
		if (this.stewEffect != null) {
			tag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
			tag.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setType(MooshroomEntity.Type.fromName(tag.getString("Type")));
		if (tag.contains("EffectId", 1)) {
			this.stewEffect = StatusEffect.byRawId(tag.getByte("EffectId"));
		}

		if (tag.contains("EffectDuration", 3)) {
			this.stewEffectDuration = tag.getInt("EffectDuration");
		}
	}

	private Pair<StatusEffect, Integer> getStewEffectFrom(ItemStack flower) {
		FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)flower.getItem()).getBlock();
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

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroom) {
		MooshroomEntity.Type type = this.getMooshroomType();
		MooshroomEntity.Type type2 = mooshroom.getMooshroomType();
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

		private Type(String name, BlockState mushroom) {
			this.name = name;
			this.mushroom = mushroom;
		}

		@Environment(EnvType.CLIENT)
		public BlockState getMushroomState() {
			return this.mushroom;
		}

		private static MooshroomEntity.Type fromName(String name) {
			for (MooshroomEntity.Type type : values()) {
				if (type.name.equals(name)) {
					return type;
				}
			}

			return RED;
		}
	}
}
