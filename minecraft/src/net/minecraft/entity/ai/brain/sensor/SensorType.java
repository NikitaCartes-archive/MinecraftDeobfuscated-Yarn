package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.CamelBrain;
import net.minecraft.entity.passive.FrogBrain;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SensorType<U extends Sensor<?>> {
	public static final SensorType<DummySensor> DUMMY = register("dummy", DummySensor::new);
	public static final SensorType<NearestItemsSensor> NEAREST_ITEMS = register("nearest_items", NearestItemsSensor::new);
	public static final SensorType<NearestLivingEntitiesSensor<LivingEntity>> NEAREST_LIVING_ENTITIES = register(
		"nearest_living_entities", NearestLivingEntitiesSensor::new
	);
	public static final SensorType<NearestPlayersSensor> NEAREST_PLAYERS = register("nearest_players", NearestPlayersSensor::new);
	public static final SensorType<NearestBedSensor> NEAREST_BED = register("nearest_bed", NearestBedSensor::new);
	public static final SensorType<HurtBySensor> HURT_BY = register("hurt_by", HurtBySensor::new);
	public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = register("villager_hostiles", VillagerHostilesSensor::new);
	public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = register("villager_babies", VillagerBabiesSensor::new);
	public static final SensorType<SecondaryPointsOfInterestSensor> SECONDARY_POIS = register("secondary_pois", SecondaryPointsOfInterestSensor::new);
	public static final SensorType<GolemLastSeenSensor> GOLEM_DETECTED = register("golem_detected", GolemLastSeenSensor::new);
	public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = register("piglin_specific_sensor", PiglinSpecificSensor::new);
	public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = register(
		"piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new
	);
	public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = register("hoglin_specific_sensor", HoglinSpecificSensor::new);
	public static final SensorType<NearestVisibleAdultSensor> NEAREST_ADULT = register("nearest_adult", NearestVisibleAdultSensor::new);
	public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = register("axolotl_attackables", AxolotlAttackablesSensor::new);
	public static final SensorType<TemptationsSensor> AXOLOTL_TEMPTATIONS = register(
		"axolotl_temptations", () -> new TemptationsSensor(AxolotlBrain.getTemptItems())
	);
	public static final SensorType<TemptationsSensor> GOAT_TEMPTATIONS = register("goat_temptations", () -> new TemptationsSensor(GoatBrain.getTemptItems()));
	public static final SensorType<TemptationsSensor> FROG_TEMPTATIONS = register("frog_temptations", () -> new TemptationsSensor(FrogBrain.getTemptItems()));
	public static final SensorType<TemptationsSensor> CAMEL_TEMPTATIONS = register("camel_temptations", () -> new TemptationsSensor(CamelBrain.getTemptItems()));
	public static final SensorType<FrogAttackablesSensor> FROG_ATTACKABLES = register("frog_attackables", FrogAttackablesSensor::new);
	public static final SensorType<IsInWaterSensor> IS_IN_WATER = register("is_in_water", IsInWaterSensor::new);
	public static final SensorType<WardenAttackablesSensor> WARDEN_ENTITY_SENSOR = register("warden_entity_sensor", WardenAttackablesSensor::new);
	public static final SensorType<TemptationsSensor> SNIFFER_TEMPTATIONS = register(
		"sniffer_temptations", () -> new TemptationsSensor(SnifferBrain.getTemptItems())
	);
	public static final SensorType<BreezeAttackablesSensor> BREEZE_ATTACK_ENTITY_SENSOR = register("breeze_attack_entity_sensor", BreezeAttackablesSensor::new);
	private final Supplier<U> factory;

	private SensorType(Supplier<U> factory) {
		this.factory = factory;
	}

	public U create() {
		return (U)this.factory.get();
	}

	private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
		return Registry.register(Registries.SENSOR_TYPE, new Identifier(id), new SensorType<>(factory));
	}
}
