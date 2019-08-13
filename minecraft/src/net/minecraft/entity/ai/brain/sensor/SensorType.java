package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SensorType<U extends Sensor<?>> {
	public static final SensorType<DummySensor> field_18465 = register("dummy", DummySensor::new);
	public static final SensorType<NearestLivingEntitiesSensor> field_18466 = register("nearest_living_entities", NearestLivingEntitiesSensor::new);
	public static final SensorType<NearestPlayersSensor> field_18467 = register("nearest_players", NearestPlayersSensor::new);
	public static final SensorType<InteractableDoorsSensor> field_18468 = register("interactable_doors", InteractableDoorsSensor::new);
	public static final SensorType<NearestBedSensor> field_19010 = register("nearest_bed", NearestBedSensor::new);
	public static final SensorType<HurtBySensor> field_18469 = register("hurt_by", HurtBySensor::new);
	public static final SensorType<VillagerHostilesSensor> field_18470 = register("villager_hostiles", VillagerHostilesSensor::new);
	public static final SensorType<VillagerBabiesSensor> field_19011 = register("villager_babies", VillagerBabiesSensor::new);
	public static final SensorType<SecondaryPointsOfInterestSensor> field_18875 = register("secondary_pois", SecondaryPointsOfInterestSensor::new);
	public static final SensorType<GolemLastSeenSensor> field_19356 = register("golem_last_seen", GolemLastSeenSensor::new);
	private final Supplier<U> factory;

	private SensorType(Supplier<U> supplier) {
		this.factory = supplier;
	}

	public U create() {
		return (U)this.factory.get();
	}

	private static <U extends Sensor<?>> SensorType<U> register(String string, Supplier<U> supplier) {
		return Registry.register(Registry.SENSOR_TYPE, new Identifier(string), new SensorType<>(supplier));
	}
}
