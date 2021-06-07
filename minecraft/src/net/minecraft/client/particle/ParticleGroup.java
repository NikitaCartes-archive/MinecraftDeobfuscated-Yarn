package net.minecraft.client.particle;

/**
 * A group for particles. This group imposes a limit on the numbers of
 * particles from this group rendered in a particle manager. Additional
 * particles will be discarded when attempted to be rendered.
 * 
 * @see Particle#getGroup()
 */
public class ParticleGroup {
	private final int maxCount;
	/**
	 * The group for the {@linkplain net.minecraft.particle.ParticleTypes#SPORE_BLOSSOM_AIR
	 * minecraft:spore_blossom_air} particle type. It has a count limit of 1000.
	 */
	public static final ParticleGroup SPORE_BLOSSOM_AIR = new ParticleGroup(1000);

	/**
	 * Creates a particle group with a custom {@code max} particle count.
	 * 
	 * @param maxCount the maximum number of a type of particle allowed
	 */
	public ParticleGroup(int maxCount) {
		this.maxCount = maxCount;
	}

	/**
	 * {@return the maximum count of particles from this group that can be
	 * rendered in a particle manager}
	 */
	public int getMaxCount() {
		return this.maxCount;
	}
}
