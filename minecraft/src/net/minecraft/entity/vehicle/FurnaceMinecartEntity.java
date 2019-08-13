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
	private static final Ingredient ACCEPTABLE_FUEL = Ingredient.ofItems(Items.field_8713, Items.field_8665);

	public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public FurnaceMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6080, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7679;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(LIT, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.fuel > 0) {
			this.fuel--;
		}

		if (this.fuel <= 0) {
			this.pushX = 0.0;
			this.pushZ = 0.0;
		}

		this.setLit(this.fuel > 0);
		if (this.isLit() && this.random.nextInt(4) == 0) {
			this.world.addParticle(ParticleTypes.field_11237, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double method_7504() {
		return 0.2;
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.field_19393)) {
			this.dropItem(Blocks.field_10181);
		}
	}

	@Override
	protected void method_7513(BlockPos blockPos, BlockState blockState) {
		super.method_7513(blockPos, blockState);
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		Vec3d vec3d = this.getVelocity();
		if (d > 1.0E-4 && squaredHorizontalLength(vec3d) > 0.001) {
			d = (double)MathHelper.sqrt(d);
			this.pushX /= d;
			this.pushZ /= d;
			if (this.pushX * vec3d.x + this.pushZ * vec3d.z < 0.0) {
				this.pushX = 0.0;
				this.pushZ = 0.0;
			} else {
				double e = d / this.method_7504();
				this.pushX *= e;
				this.pushZ *= e;
			}
		}
	}

	@Override
	protected void method_7525() {
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		if (d > 1.0E-7) {
			d = (double)MathHelper.sqrt(d);
			this.pushX /= d;
			this.pushZ /= d;
			this.setVelocity(this.getVelocity().multiply(0.8, 0.0, 0.8).add(this.pushX, 0.0, this.pushZ));
		} else {
			this.setVelocity(this.getVelocity().multiply(0.98, 0.0, 0.98));
		}

		super.method_7525();
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (ACCEPTABLE_FUEL.method_8093(itemStack) && this.fuel + 3600 <= 32000) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			this.fuel += 3600;
		}

		this.pushX = this.x - playerEntity.x;
		this.pushZ = this.z - playerEntity.z;
		return true;
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putDouble("PushX", this.pushX);
		compoundTag.putDouble("PushZ", this.pushZ);
		compoundTag.putShort("Fuel", (short)this.fuel);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.pushX = compoundTag.getDouble("PushX");
		this.pushZ = compoundTag.getDouble("PushZ");
		this.fuel = compoundTag.getShort("Fuel");
	}

	protected boolean isLit() {
		return this.dataTracker.get(LIT);
	}

	protected void setLit(boolean bl) {
		this.dataTracker.set(LIT, bl);
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10181.getDefaultState().with(FurnaceBlock.FACING, Direction.field_11043).with(FurnaceBlock.LIT, Boolean.valueOf(this.isLit()));
	}
}
