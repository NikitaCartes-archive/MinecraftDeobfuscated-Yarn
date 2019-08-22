package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CompassItem extends Item {
	public CompassItem(Item.Settings settings) {
		super(settings);
		this.addPropertyGetter(new Identifier("angle"), new ItemPropertyGetter() {
			@Environment(EnvType.CLIENT)
			private double angle;
			@Environment(EnvType.CLIENT)
			private double step;
			@Environment(EnvType.CLIENT)
			private long lastTick;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				if (livingEntity == null && !itemStack.isInFrame()) {
					return 0.0F;
				} else {
					boolean bl = livingEntity != null;
					Entity entity = (Entity)(bl ? livingEntity : itemStack.getFrame());
					if (world == null) {
						world = entity.world;
					}

					double f;
					if (world.dimension.hasVisibleSky()) {
						double d = bl ? (double)entity.yaw : this.getYaw((ItemFrameEntity)entity);
						d = MathHelper.floorMod(d / 360.0, 1.0);
						double e = this.getAngleToSpawn(world, entity) / (float) (Math.PI * 2);
						f = 0.5 - (d - 0.25 - e);
					} else {
						f = Math.random();
					}

					if (bl) {
						f = this.getAngle(world, f);
					}

					return MathHelper.floorMod((float)f, 1.0F);
				}
			}

			@Environment(EnvType.CLIENT)
			private double getAngle(World world, double d) {
				if (world.getTime() != this.lastTick) {
					this.lastTick = world.getTime();
					double e = d - this.angle;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.step += e * 0.1;
					this.step *= 0.8;
					this.angle = MathHelper.floorMod(this.angle + this.step, 1.0);
				}

				return this.angle;
			}

			@Environment(EnvType.CLIENT)
			private double getYaw(ItemFrameEntity itemFrameEntity) {
				return (double)MathHelper.wrapDegrees(180 + itemFrameEntity.getHorizontalFacing().getHorizontal() * 90);
			}

			@Environment(EnvType.CLIENT)
			private double getAngleToSpawn(IWorld iWorld, Entity entity) {
				BlockPos blockPos = iWorld.getSpawnPos();
				return Math.atan2((double)blockPos.getZ() - entity.z, (double)blockPos.getX() - entity.x);
			}
		});
	}
}
