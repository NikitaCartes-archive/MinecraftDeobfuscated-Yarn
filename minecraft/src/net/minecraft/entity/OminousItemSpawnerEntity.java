package net.minecraft.entity;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class OminousItemSpawnerEntity extends Entity {
	private static final int MIN_SPAWN_ITEM_AFTER_TICKS = 60;
	private static final int MAX_SPAWN_ITEM_AFTER_TICKS = 120;
	private static final String SPAWN_ITEM_AFTER_TICKS_NBT_KEY = "spawn_item_after_ticks";
	private static final String ITEM_NBT_KEY = "item";
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(OminousItemSpawnerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	public static final int field_50128 = 36;
	private long spawnItemAfterTicks;

	public OminousItemSpawnerEntity(EntityType<? extends OminousItemSpawnerEntity> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	public static OminousItemSpawnerEntity create(World world, ItemStack stack) {
		OminousItemSpawnerEntity ominousItemSpawnerEntity = new OminousItemSpawnerEntity(EntityType.OMINOUS_ITEM_SPAWNER, world);
		ominousItemSpawnerEntity.spawnItemAfterTicks = (long)world.random.nextBetween(60, 120);
		ominousItemSpawnerEntity.setItem(stack);
		return ominousItemSpawnerEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			this.tickClient();
		} else {
			this.tickServer();
		}
	}

	private void tickServer() {
		if ((long)this.age == this.spawnItemAfterTicks - 36L) {
			this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, SoundCategory.NEUTRAL);
		}

		if ((long)this.age >= this.spawnItemAfterTicks) {
			this.spawnItem();
			this.kill();
		}
	}

	private void tickClient() {
		if (this.getWorld().getTime() % 5L == 0L) {
			this.addParticles();
		}
	}

	private void spawnItem() {
		World world = this.getWorld();
		ItemStack itemStack = this.getItem();
		if (!itemStack.isEmpty()) {
			Entity entity;
			if (itemStack.getItem() instanceof ProjectileItem projectileItem) {
				Direction direction = Direction.DOWN;
				ProjectileEntity projectileEntity = projectileItem.createEntity(world, this.getPos(), itemStack, direction);
				projectileEntity.setOwner(this);
				ProjectileItem.Settings settings = projectileItem.getProjectileSettings();
				projectileItem.initializeProjectile(
					projectileEntity, (double)direction.getOffsetX(), (double)direction.getOffsetY(), (double)direction.getOffsetZ(), settings.power(), settings.uncertainty()
				);
				settings.overrideDispenseEvent().ifPresent(event -> world.syncWorldEvent(event, this.getBlockPos(), 0));
				entity = projectileEntity;
			} else {
				entity = new ItemEntity(world, this.getX(), this.getY(), this.getZ(), itemStack);
			}

			world.spawnEntity(entity);
			world.syncWorldEvent(WorldEvents.OMINOUS_ITEM_SPAWNER_SPAWNS_ITEM, this.getBlockPos(), 1);
			world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, this.getPos());
			this.setItem(ItemStack.EMPTY);
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(ITEM, ItemStack.EMPTY);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		ItemStack itemStack = nbt.contains("item", NbtElement.COMPOUND_TYPE)
			? (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("item")).orElse(ItemStack.EMPTY)
			: ItemStack.EMPTY;
		this.setItem(itemStack);
		this.spawnItemAfterTicks = nbt.getLong("spawn_item_after_ticks");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (!this.getItem().isEmpty()) {
			nbt.put("item", this.getItem().encode(this.getRegistryManager()).copy());
		}

		nbt.putLong("spawn_item_after_ticks", this.spawnItemAfterTicks);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return false;
	}

	@Override
	protected boolean couldAcceptPassenger() {
		return false;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}

	public void addParticles() {
		Vec3d vec3d = this.getPos();
		int i = this.random.nextBetween(1, 3);

		for (int j = 0; j < i; j++) {
			double d = 0.4;
			Vec3d vec3d2 = new Vec3d(
				this.getX() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
				this.getY() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
				this.getZ() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian())
			);
			Vec3d vec3d3 = vec3d.relativize(vec3d2);
			this.getWorld().addParticle(ParticleTypes.OMINOUS_SPAWNING, vec3d.getX(), vec3d.getY(), vec3d.getZ(), vec3d3.getX(), vec3d3.getY(), vec3d3.getZ());
		}
	}

	public ItemStack getItem() {
		return this.getDataTracker().get(ITEM);
	}

	private void setItem(ItemStack stack) {
		this.getDataTracker().set(ITEM, stack);
	}
}
