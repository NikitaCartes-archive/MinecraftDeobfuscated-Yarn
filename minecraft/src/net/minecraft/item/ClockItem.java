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
			public float call(ItemStack stack, @Nullable World world, @Nullable LivingEntity user) {
				boolean bl = user != null;
				Entity entity = (Entity)(bl ? user : stack.getFrame());
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

					d = this.getTime(world, d);
					return (float)d;
				}
			}

			@Environment(EnvType.CLIENT)
			private double getTime(World world, double skyAngle) {
				if (world.getTime() != this.lastTick) {
					this.lastTick = world.getTime();
					double d = skyAngle - this.time;
					d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
					this.step += d * 0.1;
					this.step *= 0.9;
					this.time = MathHelper.floorMod(this.time + this.step, 1.0);
				}

				return this.time;
			}
		});
	}
}
