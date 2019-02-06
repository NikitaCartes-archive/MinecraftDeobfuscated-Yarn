package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class class_3998 extends Particle {
	protected class_3998(World world, double d, double e, double f) {
		super(world, d, e, f);
	}

	protected class_3998(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
	}

	@Override
	public final void buildGeometry(BufferBuilder bufferBuilder, Entity entity, float f, float g, float h, float i, float j, float k) {
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17832;
	}
}
