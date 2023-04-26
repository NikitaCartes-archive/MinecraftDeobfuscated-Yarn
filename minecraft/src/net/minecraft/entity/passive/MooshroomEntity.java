package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements Shearable, VariantHolder<MooshroomEntity.Type> {
	private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final int MUTATION_CHANCE = 1024;
	@Nullable
	private StatusEffect stewEffect;
	private int stewEffectDuration;
	@Nullable
	private UUID lightningId;

	public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBlockState(pos.down()).isOf(Blocks.MYCELIUM) ? 10.0F : world.getPhototaxisFavor(pos);
	}

	public static boolean canSpawn(EntityType<MooshroomEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		UUID uUID = lightning.getUuid();
		if (!uUID.equals(this.lightningId)) {
			this.setVariant(this.getVariant() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
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
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.BOWL) && !this.isBaby()) {
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

			ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, player, itemStack2, false);
			player.setStackInHand(hand, itemStack3);
			SoundEvent soundEvent;
			if (bl) {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
			} else {
				soundEvent = SoundEvents.ENTITY_MOOSHROOM_MILK;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
			return ActionResult.success(this.getWorld().isClient);
		} else if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			this.sheared(SoundCategory.PLAYERS);
			this.emitGameEvent(GameEvent.SHEAR, player);
			if (!this.getWorld().isClient) {
				itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
			}

			return ActionResult.success(this.getWorld().isClient);
		} else if (this.getVariant() == MooshroomEntity.Type.BROWN && itemStack.isIn(ItemTags.SMALL_FLOWERS)) {
			if (this.stewEffect != null) {
				for (int i = 0; i < 2; i++) {
					this.getWorld()
						.addParticle(
							ParticleTypes.SMOKE,
							this.getX() + this.random.nextDouble() / 2.0,
							this.getBodyY(0.5),
							this.getZ() + this.random.nextDouble() / 2.0,
							0.0,
							this.random.nextDouble() / 5.0,
							0.0
						);
				}
			} else {
				Optional<Pair<StatusEffect, Integer>> optional = this.getStewEffectFrom(itemStack);
				if (!optional.isPresent()) {
					return ActionResult.PASS;
				}

				Pair<StatusEffect, Integer> pair = (Pair<StatusEffect, Integer>)optional.get();
				if (!player.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				for (int j = 0; j < 4; j++) {
					this.getWorld()
						.addParticle(
							ParticleTypes.EFFECT,
							this.getX() + this.random.nextDouble() / 2.0,
							this.getBodyY(0.5),
							this.getZ() + this.random.nextDouble() / 2.0,
							0.0,
							this.random.nextDouble() / 5.0,
							0.0
						);
				}

				this.stewEffect = pair.getLeft();
				this.stewEffectDuration = pair.getRight();
				this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
			}

			return ActionResult.success(this.getWorld().isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public void sheared(SoundCategory shearedSoundCategory) {
		this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		if (!this.getWorld().isClient()) {
			CowEntity cowEntity = EntityType.COW.create(this.getWorld());
			if (cowEntity != null) {
				((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getBodyY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
				this.discard();
				cowEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
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
				this.getWorld().spawnEntity(cowEntity);

				for (int i = 0; i < 5; i++) {
					this.getWorld()
						.spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getBodyY(1.0), this.getZ(), new ItemStack(this.getVariant().mushroom.getBlock())));
				}
			}
		}
	}

	@Override
	public boolean isShearable() {
		return this.isAlive() && !this.isBaby();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("Type", this.getVariant().asString());
		if (this.stewEffect != null) {
			nbt.putInt("EffectId", StatusEffect.getRawId(this.stewEffect));
			nbt.putInt("EffectDuration", this.stewEffectDuration);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(MooshroomEntity.Type.fromName(nbt.getString("Type")));
		if (nbt.contains("EffectId", NbtElement.NUMBER_TYPE)) {
			this.stewEffect = StatusEffect.byRawId(nbt.getInt("EffectId"));
		}

		if (nbt.contains("EffectDuration", NbtElement.NUMBER_TYPE)) {
			this.stewEffectDuration = nbt.getInt("EffectDuration");
		}
	}

	private Optional<Pair<StatusEffect, Integer>> getStewEffectFrom(ItemStack flower) {
		SuspiciousStewIngredient suspiciousStewIngredient = SuspiciousStewIngredient.of(flower.getItem());
		return suspiciousStewIngredient != null
			? Optional.of(Pair.of(suspiciousStewIngredient.getEffectInStew(), suspiciousStewIngredient.getEffectInStewDuration()))
			: Optional.empty();
	}

	public void setVariant(MooshroomEntity.Type type) {
		this.dataTracker.set(TYPE, type.name);
	}

	public MooshroomEntity.Type getVariant() {
		return MooshroomEntity.Type.fromName(this.dataTracker.get(TYPE));
	}

	@Nullable
	public MooshroomEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.create(serverWorld);
		if (mooshroomEntity != null) {
			mooshroomEntity.setVariant(this.chooseBabyType((MooshroomEntity)passiveEntity));
		}

		return mooshroomEntity;
	}

	private MooshroomEntity.Type chooseBabyType(MooshroomEntity mooshroom) {
		MooshroomEntity.Type type = this.getVariant();
		MooshroomEntity.Type type2 = mooshroom.getVariant();
		MooshroomEntity.Type type3;
		if (type == type2 && this.random.nextInt(1024) == 0) {
			type3 = type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
		} else {
			type3 = this.random.nextBoolean() ? type : type2;
		}

		return type3;
	}

	public static enum Type implements StringIdentifiable {
		RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
		BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

		public static final StringIdentifiable.Codec<MooshroomEntity.Type> CODEC = StringIdentifiable.createCodec(MooshroomEntity.Type::values);
		final String name;
		final BlockState mushroom;

		private Type(String name, BlockState mushroom) {
			this.name = name;
			this.mushroom = mushroom;
		}

		public BlockState getMushroomState() {
			return this.mushroom;
		}

		@Override
		public String asString() {
			return this.name;
		}

		static MooshroomEntity.Type fromName(String name) {
			return (MooshroomEntity.Type)CODEC.byId(name, RED);
		}
	}
}
