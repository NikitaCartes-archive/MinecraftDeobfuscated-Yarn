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
		this.addPropertyGetter(new Identifier("time"), new ItemPropertyGetter() {
			@Environment(EnvType.CLIENT)
			private double time;
			@Environment(EnvType.CLIENT)
			private double step;
			@Environment(EnvType.CLIENT)
			private long lastTick;

			@Environment(EnvType.CLIENT)
			@Override
			public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
				boolean bl = livingEntity != null;
				Entity entity = (Entity)(bl ? livingEntity : itemStack.getFrame());
				if (world == null && entity != null) {
					world = entity.field_6002;
				}

				if (world == null) {
					return 0.0F;
				} else {
					double d;
					if (world.field_9247.hasVisibleSky()) {
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
				if (world.getTime() != this.lastTick) {
					this.lastTick = world.getTime();
					double e = d - this.time;
					e = MathHelper.floorMod(e + 0.5, 1.0) - 0.5;
					this.step += e * 0.1;
					this.step *= 0.9;
					this.time = MathHelper.floorMod(this.time + this.step, 1.0);
				}

				return this.time;
			}
		});
	}
}
