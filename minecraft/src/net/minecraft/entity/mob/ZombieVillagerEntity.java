package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3730;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ZombieVillagerEntity extends ZombieEntity {
	private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> PROFESSION = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int conversionTimer;
	private UUID field_7421;

	public ZombieVillagerEntity(World world) {
		super(EntityType.ZOMBIE_VILLAGER, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CONVERTING, false);
		this.dataTracker.startTracking(PROFESSION, 0);
	}

	public void method_7195(int i) {
		this.dataTracker.set(PROFESSION, i);
	}

	public int getProfession() {
		return Math.max(this.dataTracker.get(PROFESSION) % 6, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Profession", this.getProfession());
		compoundTag.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
		if (this.field_7421 != null) {
			compoundTag.putUuid("ConversionPlayer", this.field_7421);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_7195(compoundTag.getInt("Profession"));
		if (compoundTag.containsKey("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
			this.setConverting(compoundTag.hasUuid("ConversionPlayer") ? compoundTag.getUuid("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.method_7195(this.world.random.nextInt(6));
		return super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
	}

	@Override
	public void update() {
		if (!this.world.isRemote && this.isConverting()) {
			int i = this.method_7194();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) {
				this.finishConversion();
			}
		}

		super.update();
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8463 && this.hasPotionEffect(StatusEffects.field_5911)) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			if (!this.world.isRemote) {
				this.setConverting(playerEntity.getUuid(), this.random.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	public boolean method_5974(double d) {
		return !this.isConverting();
	}

	public boolean isConverting() {
		return this.getDataTracker().get(CONVERTING);
	}

	protected void setConverting(@Nullable UUID uUID, int i) {
		this.field_7421 = uUID;
		this.conversionTimer = i;
		this.getDataTracker().set(CONVERTING, true);
		this.method_6016(StatusEffects.field_5911);
		this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5910, i, Math.min(this.world.getDifficulty().getId() - 1, 0)));
		this.world.method_8421(this, (byte)16);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 16) {
			if (!this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						SoundEvents.field_14905,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}
		} else {
			super.method_5711(b);
		}
	}

	protected void finishConversion() {
		VillagerEntity villagerEntity = new VillagerEntity(this.world);
		villagerEntity.setPositionAndAngles(this);
		villagerEntity.setVillagerType(this.getProfession());
		villagerEntity.method_7240(this.world, this.world.getLocalDifficulty(new BlockPos(villagerEntity)), class_3730.field_16468, null, null, false);
		villagerEntity.method_7238();
		if (this.isChild()) {
			villagerEntity.setBreedingAge(-24000);
		}

		this.world.method_8463(this);
		villagerEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			villagerEntity.setCustomName(this.getCustomName());
			villagerEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(villagerEntity);
		if (this.field_7421 != null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.field_7421);
			if (playerEntity instanceof ServerPlayerEntity) {
				CriterionCriterions.CURED_ZOMBIE_VILLAGER.handle((ServerPlayerEntity)playerEntity, this, villagerEntity);
			}
		}

		villagerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5916, 200, 0));
		this.world.fireWorldEvent(null, 1027, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	protected int method_7194() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = (int)this.x - 4; k < (int)this.x + 4 && j < 14; k++) {
				for (int l = (int)this.y - 4; l < (int)this.y + 4 && j < 14; l++) {
					for (int m = (int)this.z - 4; m < (int)this.z + 4 && j < 14; m++) {
						Block block = this.world.getBlockState(mutable.set(k, l, m)).getBlock();
						if (block == Blocks.field_10576 || block instanceof BedBlock) {
							if (this.random.nextFloat() < 0.3F) {
								i++;
							}

							j++;
						}
					}
				}
			}
		}

		return i;
	}

	@Override
	protected float getSoundPitch() {
		return this.isChild() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.field_15056;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14728;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.field_14996;
	}

	@Override
	public SoundEvent getSoundStep() {
		return SoundEvents.field_14841;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
