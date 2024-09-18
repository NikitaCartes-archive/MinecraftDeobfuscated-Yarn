package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<Boolean> LIT = DataTracker.registerData(FurnaceMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final int FUEL_PER_ITEM = 3600;
	private static final int MAX_FUEL = 32000;
	private int fuel;
	public Vec3d pushVec = Vec3d.ZERO;

	public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean isSelfPropelling() {
		return true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(LIT, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getWorld().isClient()) {
			if (this.fuel > 0) {
				this.fuel--;
			}

			if (this.fuel <= 0) {
				this.pushVec = Vec3d.ZERO;
			}

			this.setLit(this.fuel > 0);
		}

		if (this.isLit() && this.random.nextInt(4) == 0) {
			this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double getMaxSpeed() {
		return this.isTouchingWater() ? super.getMaxSpeed() * 0.75 : super.getMaxSpeed() * 0.5;
	}

	@Override
	protected Item asItem() {
		return Items.FURNACE_MINECART;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(Items.FURNACE_MINECART);
	}

	@Override
	protected Vec3d applySlowdown(Vec3d velocity) {
		Vec3d vec3d;
		if (this.pushVec.lengthSquared() > 1.0E-7) {
			this.pushVec = this.method_64276(velocity);
			vec3d = velocity.multiply(0.8, 0.0, 0.8).add(this.pushVec);
			if (this.isTouchingWater()) {
				vec3d = vec3d.multiply(0.1);
			}
		} else {
			vec3d = velocity.multiply(0.98, 0.0, 0.98);
		}

		return super.applySlowdown(vec3d);
	}

	private Vec3d method_64276(Vec3d velocity) {
		double d = 1.0E-4;
		double e = 0.001;
		return this.pushVec.horizontalLengthSquared() > 1.0E-4 && velocity.horizontalLengthSquared() > 0.001
			? this.pushVec.projectOnto(velocity).normalize().multiply(this.pushVec.length())
			: this.pushVec;
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isIn(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000) {
			itemStack.decrementUnlessCreative(1, player);
			this.fuel += 3600;
		}

		if (this.fuel > 0) {
			this.pushVec = this.getPos().subtract(player.getPos()).getHorizontal();
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putDouble("PushX", this.pushVec.x);
		nbt.putDouble("PushZ", this.pushVec.z);
		nbt.putShort("Fuel", (short)this.fuel);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		double d = nbt.getDouble("PushX");
		double e = nbt.getDouble("PushZ");
		this.pushVec = new Vec3d(d, 0.0, e);
		this.fuel = nbt.getShort("Fuel");
	}

	protected boolean isLit() {
		return this.dataTracker.get(LIT);
	}

	protected void setLit(boolean lit) {
		this.dataTracker.set(LIT, lit);
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH).with(FurnaceBlock.LIT, Boolean.valueOf(this.isLit()));
	}
}
