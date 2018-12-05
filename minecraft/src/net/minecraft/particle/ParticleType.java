package net.minecraft.particle;

public class ParticleType<T extends Particle> {
	private final boolean alwaysShow;
	private final Particle.class_2395<T> field_11197;

	protected ParticleType(boolean bl, Particle.class_2395<T> arg) {
		this.alwaysShow = bl;
		this.field_11197 = arg;
	}

	public boolean alwaysShow() {
		return this.alwaysShow;
	}

	public Particle.class_2395<T> method_10298() {
		return this.field_11197;
	}
}
