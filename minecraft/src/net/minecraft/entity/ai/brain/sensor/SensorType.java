package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.class_5356;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SensorType<U extends Sensor<?>> {
	public static final SensorType<DummySensor> DUMMY = register("dummy", DummySensor::new);
	public static final SensorType<NearestItemsSensor> NEAREST_ITEMS = register("nearest_items", NearestItemsSensor::new);
	public static final SensorType<NearestLivingEntitiesSensor> NEAREST_LIVING_ENTITIES = register("nearest_living_entities", NearestLivingEntitiesSensor::new);
	public static final SensorType<NearestPlayersSensor> NEAREST_PLAYERS = register("nearest_players", NearestPlayersSensor::new);
	public static final SensorType<InteractableDoorsSensor> INTERACTABLE_DOORS = register("interactable_doors", InteractableDoorsSensor::new);
	public static final SensorType<NearestBedSensor> NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
	public static final SensorType<HurtBySensor> HURT_BY = register("hurt_by", HurtBySensor::new);
	public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
	public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
	public static final SensorType<SecondaryPointsOfInterestSensor> SECONDARY_POIS = register("secondary_pois", SecondaryPointsOfInterestSensor::new);
	public static final SensorType<GolemLastSeenSensor> GOLEM_LAST_SEEN = register("golem_last_seen", GolemLastSeenSensor::new);
	public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinSpecificSensor::new);
	public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinSpecificSensor::new);
	public static final SensorType<class_5356> NEAREST_ADULT = register("nearest_adult", class_5356::new);
	private final Supplier<U> factory;

	private SensorType(Supplier<U> supplier) {
		this.factory = supplier;
	}

	public U create() {
		return (U)this.factory.get();
	}

	private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> supplier) {
		return Registry.register(Registry.SENSOR_TYPE, new Identifier(id), new SensorType<>(supplier));
	}
}
