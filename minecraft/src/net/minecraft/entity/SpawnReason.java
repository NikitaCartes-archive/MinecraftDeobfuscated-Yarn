package net.minecraft.entity;

public enum SpawnReason {
	NATURAL,
	CHUNK_GENERATION,
	SPAWNER,
	STRUCTURE,
	BREEDING,
	MOB_SUMMONED,
	JOCKEY,
	EVENT,
	CONVERSION,
	REINFORCEMENT,
	TRIGGERED,
	BUCKET,
	SPAWN_EGG,
	COMMAND,
	DISPENSER,
	PATROL,
	TRIAL_SPAWNER;

	public static boolean isAnySpawner(SpawnReason reason) {
		return reason == SPAWNER || reason == TRIAL_SPAWNER;
	}

	public static boolean isTrialSpawner(SpawnReason reason) {
		return reason == TRIAL_SPAWNER;
	}
}
