package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllagerBeastEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PigZombieEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register(
		"area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity.class, AreaEffectCloudEntity::new).method_17687(6.0F, 0.5F)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity.class, ArmorStandEntity::new).method_17687(0.5F, 1.975F)
	);
	public static final EntityType<ArrowEntity> ARROW = register(
		"arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity.class, ArrowEntity::new).method_17687(0.5F, 0.5F)
	);
	public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.<BatEntity>create(BatEntity.class, BatEntity::new).method_17687(0.5F, 0.9F));
	public static final EntityType<BlazeEntity> BLAZE = register(
		"blaze", EntityType.Builder.<BlazeEntity>create(BlazeEntity.class, BlazeEntity::new).method_17687(0.6F, 1.8F)
	);
	public static final EntityType<BoatEntity> BOAT = register(
		"boat", EntityType.Builder.<BoatEntity>create(BoatEntity.class, BoatEntity::new).method_17687(1.375F, 0.5625F)
	);
	public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.<CatEntity>create(CatEntity.class, CatEntity::new).method_17687(0.6F, 0.7F));
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.Builder.<CaveSpiderEntity>create(CaveSpiderEntity.class, CaveSpiderEntity::new).method_17687(0.7F, 0.5F)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register(
		"chicken", EntityType.Builder.<ChickenEntity>create(ChickenEntity.class, ChickenEntity::new).method_17687(0.4F, 0.7F)
	);
	public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.<CodEntity>create(CodEntity.class, CodEntity::new).method_17687(0.5F, 0.3F));
	public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.<CowEntity>create(CowEntity.class, CowEntity::new).method_17687(0.9F, 1.4F));
	public static final EntityType<CreeperEntity> CREEPER = register(
		"creeper", EntityType.Builder.<CreeperEntity>create(CreeperEntity.class, CreeperEntity::new).method_17687(0.6F, 1.7F)
	);
	public static final EntityType<DonkeyEntity> DONKEY = register(
		"donkey", EntityType.Builder.<DonkeyEntity>create(DonkeyEntity.class, DonkeyEntity::new).method_17687(1.3964844F, 1.6F)
	);
	public static final EntityType<DolphinEntity> DOLPHIN = register(
		"dolphin", EntityType.Builder.<DolphinEntity>create(DolphinEntity.class, DolphinEntity::new).method_17687(0.9F, 0.6F)
	);
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball", EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity.class, DragonFireballEntity::new).method_17687(1.0F, 1.0F)
	);
	public static final EntityType<DrownedEntity> DROWNED = register(
		"drowned", EntityType.Builder.<DrownedEntity>create(DrownedEntity.class, DrownedEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.Builder.<ElderGuardianEntity>create(ElderGuardianEntity.class, ElderGuardianEntity::new).method_17687(1.9975F, 1.9975F)
	);
	public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register(
		"end_crystal", EntityType.Builder.<EnderCrystalEntity>create(EnderCrystalEntity.class, EnderCrystalEntity::new).method_17687(2.0F, 2.0F)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.Builder.<EnderDragonEntity>create(EnderDragonEntity.class, EnderDragonEntity::new).method_17687(16.0F, 8.0F)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register(
		"enderman", EntityType.Builder.<EndermanEntity>create(EndermanEntity.class, EndermanEntity::new).method_17687(0.6F, 2.9F)
	);
	public static final EntityType<EndermiteEntity> ENDERMITE = register(
		"endermite", EntityType.Builder.<EndermiteEntity>create(EndermiteEntity.class, EndermiteEntity::new).method_17687(0.4F, 0.3F)
	);
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs", EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity.class, EvokerFangsEntity::new).method_17687(0.5F, 0.8F)
	);
	public static final EntityType<EvokerEntity> EVOKER = register(
		"evoker", EntityType.Builder.<EvokerEntity>create(EvokerEntity.class, EvokerEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb", EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity.class, ExperienceOrbEntity::new).method_17687(0.5F, 0.5F)
	);
	public static final EntityType<EnderEyeEntity> EYE_OF_ENDER = register(
		"eye_of_ender", EntityType.Builder.<EnderEyeEntity>create(EnderEyeEntity.class, EnderEyeEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block", EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity.class, FallingBlockEntity::new).method_17687(0.98F, 0.98F)
	);
	public static final EntityType<FireworkEntity> FIREWORK_ROCKET = register(
		"firework_rocket", EntityType.Builder.<FireworkEntity>create(FireworkEntity.class, FireworkEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<GhastEntity> GHAST = register(
		"ghast", EntityType.Builder.<GhastEntity>create(GhastEntity.class, GhastEntity::new).method_17687(4.0F, 4.0F)
	);
	public static final EntityType<GiantEntity> GIANT = register(
		"giant", EntityType.Builder.<GiantEntity>create(GiantEntity.class, GiantEntity::new).method_17687(3.6F, 11.7F)
	);
	public static final EntityType<GuardianEntity> GUARDIAN = register(
		"guardian", EntityType.Builder.<GuardianEntity>create(GuardianEntity.class, GuardianEntity::new).method_17687(0.85F, 0.85F)
	);
	public static final EntityType<HorseEntity> HORSE = register(
		"horse", EntityType.Builder.<HorseEntity>create(HorseEntity.class, HorseEntity::new).method_17687(1.3964844F, 1.6F)
	);
	public static final EntityType<HuskEntity> HUSK = register(
		"husk", EntityType.Builder.<HuskEntity>create(HuskEntity.class, HuskEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<IllusionerEntity> ILLUSIONER = register(
		"illusioner", EntityType.Builder.<IllusionerEntity>create(IllusionerEntity.class, IllusionerEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<ItemEntity> ITEM = register(
		"item", EntityType.Builder.<ItemEntity>create(ItemEntity.class, ItemEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register(
		"item_frame", EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity.class, ItemFrameEntity::new).method_17687(0.5F, 0.5F)
	);
	public static final EntityType<FireballEntity> FIREBALL = register(
		"fireball", EntityType.Builder.<FireballEntity>create(FireballEntity.class, FireballEntity::new).method_17687(1.0F, 1.0F)
	);
	public static final EntityType<LeadKnotEntity> LEASH_KNOT = register(
		"leash_knot", EntityType.Builder.<LeadKnotEntity>create(LeadKnotEntity.class, LeadKnotEntity::new).disableSaving().method_17687(0.5F, 0.5F)
	);
	public static final EntityType<LlamaEntity> LLAMA = register(
		"llama", EntityType.Builder.<LlamaEntity>create(LlamaEntity.class, LlamaEntity::new).method_17687(0.9F, 1.87F)
	);
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register(
		"llama_spit", EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity.class, LlamaSpitEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register(
		"magma_cube", EntityType.Builder.<MagmaCubeEntity>create(MagmaCubeEntity.class, MagmaCubeEntity::new).method_17687(2.04F, 2.04F)
	);
	public static final EntityType<MinecartEntity> MINECART = register(
		"minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity.class, MinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity.class, ChestMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity.class, CommandBlockMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity.class, FurnaceMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity.class, HopperMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<MobSpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart",
		EntityType.Builder.<MobSpawnerMinecartEntity>create(MobSpawnerMinecartEntity.class, MobSpawnerMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<TNTMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.Builder.<TNTMinecartEntity>create(TNTMinecartEntity.class, TNTMinecartEntity::new).method_17687(0.98F, 0.7F)
	);
	public static final EntityType<MuleEntity> MULE = register(
		"mule", EntityType.Builder.<MuleEntity>create(MuleEntity.class, MuleEntity::new).method_17687(1.3964844F, 1.6F)
	);
	public static final EntityType<MooshroomEntity> MOOSHROOM = register(
		"mooshroom", EntityType.Builder.<MooshroomEntity>create(MooshroomEntity.class, MooshroomEntity::new).method_17687(0.9F, 1.4F)
	);
	public static final EntityType<OcelotEntity> OCELOT = register(
		"ocelot", EntityType.Builder.<OcelotEntity>create(OcelotEntity.class, OcelotEntity::new).method_17687(0.6F, 0.7F)
	);
	public static final EntityType<PaintingEntity> PAINTING = register(
		"painting", EntityType.Builder.<PaintingEntity>create(PaintingEntity.class, PaintingEntity::new).method_17687(0.5F, 0.5F)
	);
	public static final EntityType<PandaEntity> PANDA = register(
		"panda", EntityType.Builder.<PandaEntity>create(PandaEntity.class, PandaEntity::new).method_17687(1.3F, 1.25F)
	);
	public static final EntityType<ParrotEntity> PARROT = register(
		"parrot", EntityType.Builder.<ParrotEntity>create(ParrotEntity.class, ParrotEntity::new).method_17687(0.5F, 0.9F)
	);
	public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.<PigEntity>create(PigEntity.class, PigEntity::new).method_17687(0.9F, 0.9F));
	public static final EntityType<PufferfishEntity> PUFFERFISH = register(
		"pufferfish", EntityType.Builder.<PufferfishEntity>create(PufferfishEntity.class, PufferfishEntity::new).method_17687(0.7F, 0.7F)
	);
	public static final EntityType<PigZombieEntity> ZOMBIE_PIGMAN = register(
		"zombie_pigman", EntityType.Builder.<PigZombieEntity>create(PigZombieEntity.class, PigZombieEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register(
		"polar_bear", EntityType.Builder.<PolarBearEntity>create(PolarBearEntity.class, PolarBearEntity::new).method_17687(1.3F, 1.4F)
	);
	public static final EntityType<PrimedTNTEntity> TNT = register(
		"tnt", EntityType.Builder.<PrimedTNTEntity>create(PrimedTNTEntity.class, PrimedTNTEntity::new).method_17687(0.98F, 0.98F)
	);
	public static final EntityType<RabbitEntity> RABBIT = register(
		"rabbit", EntityType.Builder.<RabbitEntity>create(RabbitEntity.class, RabbitEntity::new).method_17687(0.4F, 0.5F)
	);
	public static final EntityType<SalmonEntity> SALMON = register(
		"salmon", EntityType.Builder.<SalmonEntity>create(SalmonEntity.class, SalmonEntity::new).method_17687(0.7F, 0.4F)
	);
	public static final EntityType<SheepEntity> SHEEP = register(
		"sheep", EntityType.Builder.<SheepEntity>create(SheepEntity.class, SheepEntity::new).method_17687(0.9F, 1.3F)
	);
	public static final EntityType<ShulkerEntity> SHULKER = register(
		"shulker", EntityType.Builder.<ShulkerEntity>create(ShulkerEntity.class, ShulkerEntity::new).method_17687(1.0F, 1.0F)
	);
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity.class, ShulkerBulletEntity::new).method_17687(0.3125F, 0.3125F)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register(
		"silverfish", EntityType.Builder.<SilverfishEntity>create(SilverfishEntity.class, SilverfishEntity::new).method_17687(0.4F, 0.3F)
	);
	public static final EntityType<SkeletonEntity> SKELETON = register(
		"skeleton", EntityType.Builder.<SkeletonEntity>create(SkeletonEntity.class, SkeletonEntity::new).method_17687(0.6F, 1.99F)
	);
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.Builder.<SkeletonHorseEntity>create(SkeletonHorseEntity.class, SkeletonHorseEntity::new).method_17687(1.3964844F, 1.6F)
	);
	public static final EntityType<SlimeEntity> SLIME = register(
		"slime", EntityType.Builder.<SlimeEntity>create(SlimeEntity.class, SlimeEntity::new).method_17687(2.04F, 2.04F)
	);
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball", EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity.class, SmallFireballEntity::new).method_17687(0.3125F, 0.3125F)
	);
	public static final EntityType<SnowmanEntity> SNOW_GOLEM = register(
		"snow_golem", EntityType.Builder.<SnowmanEntity>create(SnowmanEntity.class, SnowmanEntity::new).method_17687(0.7F, 1.9F)
	);
	public static final EntityType<SnowballEntity> SNOWBALL = register(
		"snowball", EntityType.Builder.<SnowballEntity>create(SnowballEntity.class, SnowballEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow", EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity.class, SpectralArrowEntity::new).method_17687(0.5F, 0.5F)
	);
	public static final EntityType<SpiderEntity> SPIDER = register(
		"spider", EntityType.Builder.<SpiderEntity>create(SpiderEntity.class, SpiderEntity::new).method_17687(1.4F, 0.9F)
	);
	public static final EntityType<SquidEntity> SQUID = register(
		"squid", EntityType.Builder.<SquidEntity>create(SquidEntity.class, SquidEntity::new).method_17687(0.8F, 0.8F)
	);
	public static final EntityType<StrayEntity> STRAY = register(
		"stray", EntityType.Builder.<StrayEntity>create(StrayEntity.class, StrayEntity::new).method_17687(0.6F, 1.99F)
	);
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.Builder.<TropicalFishEntity>create(TropicalFishEntity.class, TropicalFishEntity::new).method_17687(0.5F, 0.4F)
	);
	public static final EntityType<TurtleEntity> TURTLE = register(
		"turtle", EntityType.Builder.<TurtleEntity>create(TurtleEntity.class, TurtleEntity::new).method_17687(1.2F, 0.4F)
	);
	public static final EntityType<ThrownEggEntity> EGG = register(
		"egg", EntityType.Builder.<ThrownEggEntity>create(ThrownEggEntity.class, ThrownEggEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL = register(
		"ender_pearl", EntityType.Builder.<ThrownEnderpearlEntity>create(ThrownEnderpearlEntity.class, ThrownEnderpearlEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle",
		EntityType.Builder.<ThrownExperienceBottleEntity>create(ThrownExperienceBottleEntity.class, ThrownExperienceBottleEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<ThrownPotionEntity> POTION = register(
		"potion", EntityType.Builder.<ThrownPotionEntity>create(ThrownPotionEntity.class, ThrownPotionEntity::new).method_17687(0.25F, 0.25F)
	);
	public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.<VexEntity>create(VexEntity.class, VexEntity::new).method_17687(0.4F, 0.8F));
	public static final EntityType<VillagerEntity> VILLAGER = register(
		"villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity.class, VillagerEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register(
		"iron_golem", EntityType.Builder.<IronGolemEntity>create(IronGolemEntity.class, IronGolemEntity::new).method_17687(1.4F, 2.7F)
	);
	public static final EntityType<VindicatorEntity> VINDICATOR = register(
		"vindicator", EntityType.Builder.<VindicatorEntity>create(VindicatorEntity.class, VindicatorEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<PillagerEntity> PILLAGER = register(
		"pillager", EntityType.Builder.<PillagerEntity>create(PillagerEntity.class, PillagerEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<WitchEntity> WITCH = register(
		"witch", EntityType.Builder.<WitchEntity>create(WitchEntity.class, WitchEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<EntityWither> WITHER = register(
		"wither", EntityType.Builder.<EntityWither>create(EntityWither.class, EntityWither::new).method_17687(0.9F, 3.5F)
	);
	public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton", EntityType.Builder.<WitherSkeletonEntity>create(WitherSkeletonEntity.class, WitherSkeletonEntity::new).method_17687(0.7F, 2.4F)
	);
	public static final EntityType<ExplodingWitherSkullEntity> WITHER_SKULL = register(
		"wither_skull",
		EntityType.Builder.<ExplodingWitherSkullEntity>create(ExplodingWitherSkullEntity.class, ExplodingWitherSkullEntity::new).method_17687(0.3125F, 0.3125F)
	);
	public static final EntityType<WolfEntity> WOLF = register(
		"wolf", EntityType.Builder.<WolfEntity>create(WolfEntity.class, WolfEntity::new).method_17687(0.6F, 0.85F)
	);
	public static final EntityType<ZombieEntity> ZOMBIE = register(
		"zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity.class, ZombieEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.Builder.<ZombieHorseEntity>create(ZombieHorseEntity.class, ZombieHorseEntity::new).method_17687(1.3964844F, 1.6F)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.Builder.<ZombieVillagerEntity>create(ZombieVillagerEntity.class, ZombieVillagerEntity::new).method_17687(0.6F, 1.95F)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register(
		"phantom", EntityType.Builder.<PhantomEntity>create(PhantomEntity.class, PhantomEntity::new).method_17687(0.9F, 0.5F)
	);
	public static final EntityType<IllagerBeastEntity> ILLAGER_BEAST = register(
		"illager_beast", EntityType.Builder.<IllagerBeastEntity>create(IllagerBeastEntity.class, IllagerBeastEntity::new).method_17687(1.95F, 2.2F)
	);
	public static final EntityType<LightningEntity> LIGHTNING_BOLT = register(
		"lightning_bolt", EntityType.Builder.<LightningEntity>create(LightningEntity.class).disableSaving().method_17687(0.0F, 0.0F)
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player", EntityType.Builder.<PlayerEntity>create(PlayerEntity.class).disableSaving().disableSummon().method_17687(0.6F, 1.8F)
	);
	public static final EntityType<FishHookEntity> FISHING_BOBBER = register(
		"fishing_bobber", EntityType.Builder.<FishHookEntity>create(FishHookEntity.class).disableSaving().disableSummon().method_17687(0.25F, 0.25F)
	);
	public static final EntityType<TridentEntity> TRIDENT = register(
		"trident", EntityType.Builder.<TridentEntity>create(TridentEntity.class, TridentEntity::new).method_17687(0.5F, 0.5F)
	);
	private final Class<? extends T> entityClass;
	private final Function<? super World, ? extends T> factory;
	private final boolean saveable;
	private final boolean summonable;
	@Nullable
	private String translationKey;
	@Nullable
	private TextComponent textComponent;
	@Nullable
	private Identifier lootTableId;
	@Nullable
	private final Type<?> dataFixerType;
	private final float field_17488;
	private final float field_17489;

	private static <T extends Entity> EntityType<T> register(String string, EntityType.Builder<T> builder) {
		return Registry.register(Registry.ENTITY_TYPE, string, builder.build(string));
	}

	@Nullable
	public static Identifier getId(EntityType<?> entityType) {
		return Registry.ENTITY_TYPE.getId(entityType);
	}

	@Nullable
	public static EntityType<?> get(String string) {
		return Registry.ENTITY_TYPE.get(Identifier.create(string));
	}

	public EntityType(Class<? extends T> class_, Function<? super World, ? extends T> function, boolean bl, boolean bl2, @Nullable Type<?> type, float f, float g) {
		this.entityClass = class_;
		this.factory = function;
		this.saveable = bl;
		this.summonable = bl2;
		this.dataFixerType = type;
		this.field_17488 = f;
		this.field_17489 = g;
	}

	@Nullable
	public Entity spawnFromItemStack(
		World world, @Nullable ItemStack itemStack, @Nullable PlayerEntity playerEntity, BlockPos blockPos, SpawnType spawnType, boolean bl, boolean bl2
	) {
		return this.spawn(
			world,
			itemStack == null ? null : itemStack.getTag(),
			itemStack != null && itemStack.hasDisplayName() ? itemStack.getDisplayName() : null,
			playerEntity,
			blockPos,
			spawnType,
			bl,
			bl2
		);
	}

	@Nullable
	public T spawn(
		World world,
		@Nullable CompoundTag compoundTag,
		@Nullable TextComponent textComponent,
		@Nullable PlayerEntity playerEntity,
		BlockPos blockPos,
		SpawnType spawnType,
		boolean bl,
		boolean bl2
	) {
		T entity = this.create(world, compoundTag, textComponent, playerEntity, blockPos, spawnType, bl, bl2);
		world.spawnEntity(entity);
		return entity;
	}

	@Nullable
	public T create(
		World world,
		@Nullable CompoundTag compoundTag,
		@Nullable TextComponent textComponent,
		@Nullable PlayerEntity playerEntity,
		BlockPos blockPos,
		SpawnType spawnType,
		boolean bl,
		boolean bl2
	) {
		T entity = this.create(world);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (bl) {
				entity.setPosition((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5);
				d = method_5884(world, blockPos, bl2, entity.getBoundingBox());
			} else {
				d = 0.0;
			}

			entity.setPositionAndAngles(
				(double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F
			);
			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				mobEntity.headYaw = mobEntity.yaw;
				mobEntity.field_6283 = mobEntity.yaw;
				mobEntity.prepareEntityData(world, world.getLocalDifficulty(new BlockPos(mobEntity)), spawnType, null, compoundTag);
				mobEntity.playAmbientSound();
			}

			if (textComponent != null && entity instanceof LivingEntity) {
				entity.setCustomName(textComponent);
			}

			loadFromEntityTag(world, playerEntity, entity, compoundTag);
			return entity;
		}
	}

	protected static double method_5884(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl, BoundingBox boundingBox) {
		BoundingBox boundingBox2 = new BoundingBox(blockPos);
		if (bl) {
			boundingBox2 = boundingBox2.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = viewableWorld.method_8607(null, boundingBox2);
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream, bl ? -2.0 : -1.0);
	}

	public static void loadFromEntityTag(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient || !entity.method_5833() || playerEntity != null && minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
					CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
					UUID uUID = entity.getUuid();
					compoundTag2.copyFrom(compoundTag.getCompound("EntityTag"));
					entity.setUuid(uUID);
					entity.fromTag(compoundTag2);
				}
			}
		}
	}

	public boolean isSaveable() {
		return this.saveable;
	}

	public boolean isSummonable() {
		return this.summonable;
	}

	public Class<? extends T> getEntityClass() {
		return this.entityClass;
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
		}

		return this.translationKey;
	}

	public TextComponent getTextComponent() {
		if (this.textComponent == null) {
			this.textComponent = new TranslatableTextComponent(this.getTranslationKey());
		}

		return this.textComponent;
	}

	public Identifier getLootTableId() {
		if (this.lootTableId == null) {
			Identifier identifier = Registry.ENTITY_TYPE.getId(this);
			this.lootTableId = new Identifier(identifier.getNamespace(), "entities/" + identifier.getPath());
		}

		return this.lootTableId;
	}

	public float method_17685() {
		return this.field_17488;
	}

	public float method_17686() {
		return this.field_17489;
	}

	@Nullable
	public T create(World world) {
		return (T)this.factory.apply(world);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Entity createInstanceFromId(int i, World world) {
		return newInstance(world, Registry.ENTITY_TYPE.getInt(i));
	}

	@Nullable
	public static Entity fromTag(CompoundTag compoundTag, World world) {
		Entity entity = newInstance(world, method_17684(compoundTag));
		if (entity == null) {
			LOGGER.warn("Skipping Entity with id {}", compoundTag.getString("id"));
		} else {
			entity.fromTag(compoundTag);
		}

		return entity;
	}

	@Nullable
	private static Entity newInstance(World world, @Nullable EntityType<?> entityType) {
		return entityType == null ? null : entityType.create(world);
	}

	public BoundingBox method_17683(double d, double e, double f) {
		float g = this.method_17685() / 2.0F;
		return new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.method_17686(), f + (double)g);
	}

	public static EntityType<?> method_17684(CompoundTag compoundTag) {
		return Registry.ENTITY_TYPE.get(new Identifier(compoundTag.getString("id")));
	}

	public static class Builder<T extends Entity> {
		private final Class<? extends T> entityClass;
		private final Function<? super World, ? extends T> function;
		private boolean saveable = true;
		private boolean summonable = true;
		private float field_17490 = -1.0F;
		private float field_17491 = -1.0F;

		private Builder(Class<? extends T> class_, Function<? super World, ? extends T> function) {
			this.entityClass = class_;
			this.function = function;
		}

		public static <T extends Entity> EntityType.Builder<T> create(Class<? extends T> class_, Function<? super World, ? extends T> function) {
			return new EntityType.Builder<>(class_, function);
		}

		public static <T extends Entity> EntityType.Builder<T> create(Class<? extends T> class_) {
			return new EntityType.Builder<>(class_, world -> null);
		}

		public EntityType.Builder<T> method_17687(float f, float g) {
			this.field_17490 = f;
			this.field_17491 = g;
			return this;
		}

		public EntityType.Builder<T> disableSummon() {
			this.summonable = false;
			return this;
		}

		public EntityType.Builder<T> disableSaving() {
			this.saveable = false;
			return this;
		}

		public EntityType<T> build(String string) {
			Type<?> type = null;
			if (this.saveable) {
				try {
					type = Schemas.getFixer()
						.getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
						.getChoiceType(TypeReferences.ENTITY_TREE, string);
				} catch (IllegalStateException var4) {
					if (SharedConstants.isDevelopment) {
						throw var4;
					}

					EntityType.LOGGER.warn("No data fixer registered for entity {}", string);
				}
			}

			return new EntityType<>(this.entityClass, this.function, this.saveable, this.summonable, type, this.field_17490, this.field_17491);
		}
	}
}
