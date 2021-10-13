package net.minecraft.entity.mob;

/**
 * This interface marks that an entity class belongs to monsters, including
 * hostile mobs like zombies. It also contains constants to be used for
 * setting {@link MobEntity#experiencePoints}.
 * 
 * @see net.minecraft.entity.SpawnGroup#MONSTER
 */
public interface Monster {
	/**
	 * @apiNote This is used for mobs that do not drop experience.
	 */
	int ZERO_XP = 0;
	/**
	 * @apiNote This is used for monsters such as
	 * {@linkplain EndermiteEntity endermites} and
	 * {@linkplain VexEntity vexes}.
	 */
	int SMALL_MONSTER_XP = 3;
	/**
	 * @apiNote This is used for most of the monsters, such as
	 * {@linkplain ZombieEntity zombies} and
	 * {@linkplain SkeletonEntity skeletons}.
	 */
	int NORMAL_MONSTER_XP = 5;
	/**
	 * @apiNote This is used for monsters such as
	 * {@linkplain BlazeEntity blazes} and
	 * {@linkplain GuardianEntity guardians}.
	 */
	int STRONG_MONSTER_XP = 10;
	/**
	 * @apiNote This is used for {@linkplain PiglinBruteEntity piglin brutes}
	 * and {@linkplain RavagerEntity ravagers}.
	 */
	int STRONGER_MONSTER_XP = 20;
	/**
	 * @apiNote This is used for
	 * {@linkplain net.minecraft.entity.boss.WitherEntity withers}.
	 */
	int WITHER_XP = 50;
}
