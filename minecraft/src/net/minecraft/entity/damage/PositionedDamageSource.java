package net.minecraft.entity.damage;

import net.minecraft.util.math.Vec3d;

public class PositionedDamageSource extends DamageSource {
	private final Vec3d pos;

	public PositionedDamageSource(String name, Vec3d pos) {
		super(name);
		this.pos = pos;
	}

	@Override
	public Vec3d getPosition() {
		return this.pos;
	}
}
