package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultAttributeRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> DEFAULT_ATTRIBUTE_REGISTRY = ImmutableMap.<EntityType<? extends LivingEntity>, DefaultAttributeContainer>builder()
		.put(EntityType.field_6131, LivingEntity.createLivingAttributes().build())
		.put(EntityType.field_6108, BatEntity.createBatAttributes().build())
		.put(EntityType.field_20346, BeeEntity.createBeeAttributes().build())
		.put(EntityType.field_6099, BlazeEntity.createBlazeAttributes().build())
		.put(EntityType.field_16281, CatEntity.createCatAttributes().build())
		.put(EntityType.field_6084, CaveSpiderEntity.createCaveSpiderAttributes().build())
		.put(EntityType.field_6132, ChickenEntity.createChickenAttributes().build())
		.put(EntityType.field_6070, FishEntity.createFishAttributes().build())
		.put(EntityType.field_6085, CowEntity.createCowAttributes().build())
		.put(EntityType.field_6046, CreeperEntity.createCreeperAttributes().build())
		.put(EntityType.field_6087, DolphinEntity.createDolphinAttributes().build())
		.put(EntityType.field_6067, AbstractDonkeyEntity.createAbstractDonkeyAttributes().build())
		.put(EntityType.field_6123, ZombieEntity.createZombieAttributes().build())
		.put(EntityType.field_6086, ElderGuardianEntity.createElderGuardianAttributes().build())
		.put(EntityType.field_6091, EndermanEntity.createEndermanAttributes().build())
		.put(EntityType.field_6128, EndermiteEntity.createEndermiteAttributes().build())
		.put(EntityType.field_6116, EnderDragonEntity.createEnderDragonAttributes().build())
		.put(EntityType.field_6090, EvokerEntity.createEvokerAttributes().build())
		.put(EntityType.field_17943, FoxEntity.createFoxAttributes().build())
		.put(EntityType.field_6107, GhastEntity.createGhastAttributes().build())
		.put(EntityType.field_6095, GiantEntity.createGiantAttributes().build())
		.put(EntityType.field_6118, GuardianEntity.createGuardianAttributes().build())
		.put(EntityType.field_21973, HoglinEntity.createHoglinAttributes().build())
		.put(EntityType.field_6139, HorseBaseEntity.createBaseHorseAttributes().build())
		.put(EntityType.field_6071, ZombieEntity.createZombieAttributes().build())
		.put(EntityType.field_6065, IllusionerEntity.createIllusionerAttributes().build())
		.put(EntityType.field_6147, IronGolemEntity.createIronGolemAttributes().build())
		.put(EntityType.field_6074, LlamaEntity.createLlamaAttributes().build())
		.put(EntityType.field_6102, MagmaCubeEntity.createMagmaCubeAttributes().build())
		.put(EntityType.field_6143, CowEntity.createCowAttributes().build())
		.put(EntityType.field_6057, AbstractDonkeyEntity.createAbstractDonkeyAttributes().build())
		.put(EntityType.field_6081, OcelotEntity.createOcelotAttributes().build())
		.put(EntityType.field_6146, PandaEntity.createPandaAttributes().build())
		.put(EntityType.field_6104, ParrotEntity.createParrotAttributes().build())
		.put(EntityType.field_6078, HostileEntity.createHostileAttributes().build())
		.put(EntityType.field_6093, PigEntity.createPigAttributes().build())
		.put(EntityType.field_22281, PiglinEntity.createPiglinAttributes().build())
		.put(EntityType.field_25751, PiglinBruteEntity.createPiglinBruteAttributes().build())
		.put(EntityType.field_6105, PillagerEntity.createPillagerAttributes().build())
		.put(EntityType.field_6097, PlayerEntity.createPlayerAttributes().build())
		.put(EntityType.field_6042, PolarBearEntity.createPolarBearAttributes().build())
		.put(EntityType.field_6062, FishEntity.createFishAttributes().build())
		.put(EntityType.field_6140, RabbitEntity.createRabbitAttributes().build())
		.put(EntityType.field_6134, RavagerEntity.createRavagerAttributes().build())
		.put(EntityType.field_6073, FishEntity.createFishAttributes().build())
		.put(EntityType.field_6115, SheepEntity.createSheepAttributes().build())
		.put(EntityType.field_6109, ShulkerEntity.createShulkerAttributes().build())
		.put(EntityType.field_6125, SilverfishEntity.createSilverfishAttributes().build())
		.put(EntityType.field_6137, AbstractSkeletonEntity.createAbstractSkeletonAttributes().build())
		.put(EntityType.field_6075, SkeletonHorseEntity.createSkeletonHorseAttributes().build())
		.put(EntityType.field_6069, HostileEntity.createHostileAttributes().build())
		.put(EntityType.field_6047, SnowGolemEntity.createSnowGolemAttributes().build())
		.put(EntityType.field_6079, SpiderEntity.createSpiderAttributes().build())
		.put(EntityType.field_6114, SquidEntity.createSquidAttributes().build())
		.put(EntityType.field_6098, AbstractSkeletonEntity.createAbstractSkeletonAttributes().build())
		.put(EntityType.field_23214, StriderEntity.createStriderAttributes().build())
		.put(EntityType.field_17714, LlamaEntity.createLlamaAttributes().build())
		.put(EntityType.field_6111, FishEntity.createFishAttributes().build())
		.put(EntityType.field_6113, TurtleEntity.createTurtleAttributes().build())
		.put(EntityType.field_6059, VexEntity.createVexAttributes().build())
		.put(EntityType.field_6077, VillagerEntity.createVillagerAttributes().build())
		.put(EntityType.field_6117, VindicatorEntity.createVindicatorAttributes().build())
		.put(EntityType.field_17713, MobEntity.createMobAttributes().build())
		.put(EntityType.field_6145, WitchEntity.createWitchAttributes().build())
		.put(EntityType.field_6119, WitherEntity.createWitherAttributes().build())
		.put(EntityType.field_6076, AbstractSkeletonEntity.createAbstractSkeletonAttributes().build())
		.put(EntityType.field_6055, WolfEntity.createWolfAttributes().build())
		.put(EntityType.field_23696, ZoglinEntity.createZoglinAttributes().build())
		.put(EntityType.field_6051, ZombieEntity.createZombieAttributes().build())
		.put(EntityType.field_6048, ZombieHorseEntity.createZombieHorseAttributes().build())
		.put(EntityType.field_6054, ZombieEntity.createZombieAttributes().build())
		.put(EntityType.field_6050, ZombifiedPiglinEntity.createZombifiedPiglinAttributes().build())
		.build();

	public static DefaultAttributeContainer get(EntityType<? extends LivingEntity> type) {
		return (DefaultAttributeContainer)DEFAULT_ATTRIBUTE_REGISTRY.get(type);
	}

	public static boolean hasDefinitionFor(EntityType<?> type) {
		return DEFAULT_ATTRIBUTE_REGISTRY.containsKey(type);
	}

	public static void checkMissing() {
		Registry.ENTITY_TYPE
			.stream()
			.filter(entityType -> entityType.getSpawnGroup() != SpawnGroup.field_17715)
			.filter(entityType -> !hasDefinitionFor(entityType))
			.map(Registry.ENTITY_TYPE::getId)
			.forEach(identifier -> {
				if (SharedConstants.isDevelopment) {
					throw new IllegalStateException("Entity " + identifier + " has no attributes");
				} else {
					LOGGER.error("Entity {} has no attributes", identifier);
				}
			});
	}
}
