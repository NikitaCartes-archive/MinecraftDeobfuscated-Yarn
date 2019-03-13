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
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<Boolean> field_7740 = DataTracker.registerData(FurnaceMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int fuel;
	public double pushX;
	public double pushZ;
	private static final Ingredient field_7738 = Ingredient.method_8091(Items.field_8713, Items.field_8665);

	public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public FurnaceMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.FURNACE_MINECART, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7679;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7740, false);
	}

	@Override
	public void update() {
		super.update();
		if (this.fuel > 0) {
			this.fuel--;
		}

		if (this.fuel <= 0) {
			this.pushX = 0.0;
			this.pushZ = 0.0;
		}

		this.setLit(this.fuel > 0);
		if (this.isLit() && this.random.nextInt(4) == 0) {
			this.field_6002.method_8406(ParticleTypes.field_11237, this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected double method_7504() {
		return 0.2;
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (!damageSource.isExplosive() && this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			this.method_5706(Blocks.field_10181);
		}
	}

	@Override
	protected void method_7513(BlockPos blockPos, BlockState blockState) {
		super.method_7513(blockPos, blockState);
		double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
		Vec3d vec3d = this.method_18798();
		if (d > 1.0E-4 && method_17996(vec3d) > 0.001) {
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
			this.method_18799(this.method_18798().multiply(0.8, 0.0, 0.8).add(this.pushX, 0.0, this.pushZ));
		} else {
			this.method_18799(this.method_18798().multiply(0.98, 0.0, 0.98));
		}

		super.method_7525();
	}

	@Override
	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (field_7738.method_8093(itemStack) && this.fuel + 3600 <= 32000) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			this.fuel += 3600;
		}

		this.pushX = this.x - playerEntity.x;
		this.pushZ = this.z - playerEntity.z;
		return true;
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putDouble("PushX", this.pushX);
		compoundTag.putDouble("PushZ", this.pushZ);
		compoundTag.putShort("Fuel", (short)this.fuel);
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.pushX = compoundTag.getDouble("PushX");
		this.pushZ = compoundTag.getDouble("PushZ");
		this.fuel = compoundTag.getShort("Fuel");
	}

	protected boolean isLit() {
		return this.field_6011.get(field_7740);
	}

	protected void setLit(boolean bl) {
		this.field_6011.set(field_7740, bl);
	}

	@Override
	public BlockState method_7517() {
		return Blocks.field_10181
			.method_9564()
			.method_11657(FurnaceBlock.field_11104, Direction.NORTH)
			.method_11657(FurnaceBlock.field_11105, Boolean.valueOf(this.isLit()));
	}
}
