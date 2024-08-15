package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DataPool;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SalmonEntity extends SchoolingFishEntity implements VariantHolder<SalmonEntity.Variant> {
	private static final TrackedData<String> VARIANT = DataTracker.registerData(SalmonEntity.class, TrackedDataHandlerRegistry.STRING);

	public SalmonEntity(EntityType<? extends SalmonEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public int getMaxGroupSize() {
		return 5;
	}

	@Override
	public ItemStack getBucketItem() {
		return new ItemStack(Items.SALMON_BUCKET);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SALMON_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SALMON_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SALMON_HURT;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.ENTITY_SALMON_FLOP;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, SalmonEntity.Variant.MEDIUM.id);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("type", this.getVariant().asString());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(SalmonEntity.Variant.byId(nbt.getString("type")));
	}

	public void setVariant(SalmonEntity.Variant variant) {
		this.dataTracker.set(VARIANT, variant.id);
	}

	public SalmonEntity.Variant getVariant() {
		return SalmonEntity.Variant.byId(this.dataTracker.get(VARIANT));
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		DataPool.Builder<SalmonEntity.Variant> builder = DataPool.builder();
		builder.add(SalmonEntity.Variant.SMALL, 30);
		builder.add(SalmonEntity.Variant.MEDIUM, 50);
		builder.add(SalmonEntity.Variant.LARGE, 15);
		builder.build().getDataOrEmpty(this.random).ifPresent(this::setVariant);
		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	public static enum Variant implements StringIdentifiable {
		SMALL("small"),
		MEDIUM("medium"),
		LARGE("large");

		public static final StringIdentifiable.EnumCodec<SalmonEntity.Variant> CODEC = StringIdentifiable.createCodec(SalmonEntity.Variant::values);
		final String id;

		private Variant(final String id) {
			this.id = id;
		}

		@Override
		public String asString() {
			return this.id;
		}

		static SalmonEntity.Variant byId(String id) {
			return (SalmonEntity.Variant)CODEC.byId(id, SMALL);
		}
	}
}
