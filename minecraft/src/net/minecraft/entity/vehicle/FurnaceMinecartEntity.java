package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<Boolean> LIT = DataTracker.registerData(FurnaceMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int fuel;
	public double pushX;
	public double pushZ;
	private static final Ingredient ACCEPTABLE_FUEL = Ingredient.ofItems(Items.COAL, Items.CHARCOAL);

	public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public FurnaceMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.FURNACE_MINECART, world, x, y, z);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.FURNACE;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LIT, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient()) {
			if (this.fuel > 0) {
				this.fuel--;
			}

			if (this.fuel <= 0) {
				this.pushX = 0.0;
				this.pushZ = 0.0;
			}

			this.setLit(this.fuel > 0);
		}

		if (this.isLit() && this.random.nextInt(4) == 0) {
			this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double getMaxOffRailSpeed() {
		return 0.2;
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.dropItem(Blocks.FURNACE);
		}
	}

	@Override
	protected void moveOnRail(BlockPos pos, BlockState state) {
		double d = 1.0E-4;
		double e = 0.001;
		super.moveOnRail(pos, state);
		Vec3d vec3d = this.getVelocity();
		double f = squaredHorizontalLength(vec3d);
		double g = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (g > 1.0E-4 && f > 0.001) {
			double h = (double)MathHelper.sqrt(f);
			double i = (double)MathHelper.sqrt(g);
			this.pushX = vec3d.x / h * i;
			this.pushZ = vec3d.z / h * i;
		}
	}

	@Override
	protected void applySlowdown() {
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (d > 1.0E-7) {
			d = (double)MathHelper.sqrt(d);
			this.pushX /= d;
			this.pushZ /= d;
			this.setVelocity(this.getVelocity().multiply(0.8, 0.0, 0.8).add(this.pushX, 0.0, this.pushZ));
		} else {
			this.setVelocity(this.getVelocity().multiply(0.98, 0.0, 0.98));
		}

		super.applySlowdown();
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (ACCEPTABLE_FUEL.test(itemStack) && this.fuel + 3600 <= 32000) {
			if (!player.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			this.fuel += 3600;
		}

		if (this.fuel > 0) {
			this.pushX = this.getX() - player.getX();
			this.pushZ = this.getZ() - player.getZ();
		}

		return ActionResult.success(this.world.isClient);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putDouble("PushX", this.pushX);
		tag.putDouble("PushZ", this.pushZ);
		tag.putShort("Fuel", (short)this.fuel);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.pushX = tag.getDouble("PushX");
		this.pushZ = tag.getDouble("PushZ");
		this.fuel = tag.getShort("Fuel");
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
