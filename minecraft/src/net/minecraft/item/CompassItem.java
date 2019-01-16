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
		this.addProperty(new Identifier("angle"), new ItemPropertyGetter() {
			@Environment(EnvType.CLIENT)
			private double field_7907;
			@Environment(EnvType.CLIENT)
			private double field_7906;
			@Environment(EnvType.CLIENT)
			private long field_7908;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				if (livingEntity == null && !itemStack.isHeldInItemFrame()) {
					return 0.0F;
				} else {
					boolean bl = livingEntity != null;
					Entity entity = (Entity)(bl ? livingEntity : itemStack.getHoldingItemFrame());
					if (world == null) {
						world = entity.world;
					}

					double f;
					if (world.dimension.hasVisibleSky()) {
						double d = bl ? (double)entity.yaw : this.method_7733((ItemFrameEntity)entity);
						d = MathHelper.floorMod(d / 360.0, 1.0);
						double e = this.method_7734(world, entity) / (float) (Math.PI * 2);
						f = 0.5 - (d - 0.25 - e);
					} else {
						f = Math.random();
					}

					if (bl) {
						f = this.method_7735(world, f);
					}

					return MathHelper.floorMod((float)f, 1.0F);
				}
			}

			@Environment(EnvType.CLIENT)
			private double method_7735(World world, double d) {
				if (world.getTime() != this.field_7908) {
					this.field_7908 = world.getTime();
					double e = d - this.field_7907;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.field_7906 += e * 0.1;
					this.field_7906 *= 0.8;
					this.field_7907 = MathHelper.floorMod(this.field_7907 + this.field_7906, 1.0);
				}

				return this.field_7907;
			}

			@Environment(EnvType.CLIENT)
			private double method_7733(ItemFrameEntity itemFrameEntity) {
				return (double)MathHelper.wrapDegrees(180 + itemFrameEntity.facing.getHorizontal() * 90);
			}

			@Environment(EnvType.CLIENT)
			private double method_7734(IWorld iWorld, Entity entity) {
				BlockPos blockPos = iWorld.getSpawnPos();
				return Math.atan2((double)blockPos.getZ() - entity.z, (double)blockPos.getX() - entity.x);
			}
		});
	}
}
