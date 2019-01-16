package net.minecraft.item;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ClockItem extends Item {
	public ClockItem(Item.Settings settings) {
		super(settings);
		this.addProperty(new Identifier("time"), new ItemPropertyGetter() {
			@Environment(EnvType.CLIENT)
			private double field_7911;
			@Environment(EnvType.CLIENT)
			private double field_7910;
			@Environment(EnvType.CLIENT)
			private long field_7913;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				boolean bl = livingEntity != null;
				Entity entity = (Entity)(bl ? livingEntity : itemStack.getHoldingItemFrame());
				if (world == null && entity != null) {
					world = entity.world;
				}

				if (world == null) {
					return 0.0F;
				} else {
					double d;
					if (world.dimension.hasVisibleSky()) {
						d = (double)world.getSkyAngle(1.0F);
					} else {
						d = Math.random();
					}

					d = this.method_7736(world, d);
					return (float)d;
				}
			}

			@Environment(EnvType.CLIENT)
			private double method_7736(World world, double d) {
				if (world.getTime() != this.field_7913) {
					this.field_7913 = world.getTime();
					double e = d - this.field_7911;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.field_7910 += e * 0.1;
					this.field_7910 *= 0.9;
					this.field_7911 = MathHelper.floorMod(this.field_7911 + this.field_7910, 1.0);
				}

				return this.field_7911;
			}
		});
	}
}
