package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.entity.boss.WitherEntity;
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
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
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
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
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
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
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
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register(
		"area_effect_cloud",
		EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, EntityCategory.MISC).makeFireImmune().setDimensions(6.0F, 0.5F)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, EntityCategory.MISC).setDimensions(0.5F, 1.975F)
	);
	public static final EntityType<ArrowEntity> ARROW = register(
		"arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.create(BatEntity::new, EntityCategory.AMBIENT).setDimensions(0.5F, 0.9F));
	public static final EntityType<BeeEntity> BEE = register("bee", EntityType.Builder.create(BeeEntity::new, EntityCategory.CREATURE).setDimensions(0.7F, 0.6F));
	public static final EntityType<BlazeEntity> BLAZE = register(
		"blaze", EntityType.Builder.create(BlazeEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.6F, 1.8F)
	);
	public static final EntityType<BoatEntity> BOAT = register(
		"boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, EntityCategory.MISC).setDimensions(1.375F, 0.5625F)
	);
	public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.create(CatEntity::new, EntityCategory.CREATURE).setDimensions(0.6F, 0.7F));
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, EntityCategory.MONSTER).setDimensions(0.7F, 0.5F)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register(
		"chicken", EntityType.Builder.create(ChickenEntity::new, EntityCategory.CREATURE).setDimensions(0.4F, 0.7F)
	);
	public static final EntityType<CodEntity> COD = register(
		"cod", EntityType.Builder.create(CodEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.5F, 0.3F)
	);
	public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.create(CowEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 1.4F));
	public static final EntityType<CreeperEntity> CREEPER = register(
		"creeper", EntityType.Builder.create(CreeperEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.7F)
	);
	public static final EntityType<DonkeyEntity> DONKEY = register(
		"donkey", EntityType.Builder.create(DonkeyEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844F, 1.5F)
	);
	public static final EntityType<DolphinEntity> DOLPHIN = register(
		"dolphin", EntityType.Builder.create(DolphinEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.9F, 0.6F)
	);
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball", EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, EntityCategory.MISC).setDimensions(1.0F, 1.0F)
	);
	public static final EntityType<DrownedEntity> DROWNED = register(
		"drowned", EntityType.Builder.create(DrownedEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, EntityCategory.MONSTER).setDimensions(1.9975F, 1.9975F)
	);
	public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register(
		"end_crystal", EntityType.Builder.<EnderCrystalEntity>create(EnderCrystalEntity::new, EntityCategory.MISC).setDimensions(2.0F, 2.0F)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(16.0F, 8.0F)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register(
		"enderman", EntityType.Builder.create(EndermanEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 2.9F)
	);
	public static final EntityType<EndermiteEntity> ENDERMITE = register(
		"endermite", EntityType.Builder.create(EndermiteEntity::new, EntityCategory.MONSTER).setDimensions(0.4F, 0.3F)
	);
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs", EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.8F)
	);
	public static final EntityType<EvokerEntity> EVOKER = register(
		"evoker", EntityType.Builder.create(EvokerEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb", EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<EnderEyeEntity> EYE_OF_ENDER = register(
		"eye_of_ender", EntityType.Builder.<EnderEyeEntity>create(EnderEyeEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block", EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.98F)
	);
	public static final EntityType<FireworkEntity> FIREWORK_ROCKET = register(
		"firework_rocket", EntityType.Builder.<FireworkEntity>create(FireworkEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<FoxEntity> FOX = register("fox", EntityType.Builder.create(FoxEntity::new, EntityCategory.CREATURE).setDimensions(0.6F, 0.7F));
	public static final EntityType<GhastEntity> GHAST = register(
		"ghast", EntityType.Builder.create(GhastEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(4.0F, 4.0F)
	);
	public static final EntityType<GiantEntity> GIANT = register(
		"giant", EntityType.Builder.create(GiantEntity::new, EntityCategory.MONSTER).setDimensions(3.6F, 12.0F)
	);
	public static final EntityType<GuardianEntity> GUARDIAN = register(
		"guardian", EntityType.Builder.create(GuardianEntity::new, EntityCategory.MONSTER).setDimensions(0.85F, 0.85F)
	);
	public static final EntityType<HorseEntity> HORSE = register(
		"horse", EntityType.Builder.create(HorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844F, 1.6F)
	);
	public static final EntityType<HuskEntity> HUSK = register(
		"husk", EntityType.Builder.create(HuskEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<IllusionerEntity> ILLUSIONER = register(
		"illusioner", EntityType.Builder.create(IllusionerEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<ItemEntity> ITEM = register(
		"item", EntityType.Builder.<ItemEntity>create(ItemEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register(
		"item_frame", EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<FireballEntity> FIREBALL = register(
		"fireball", EntityType.Builder.<FireballEntity>create(FireballEntity::new, EntityCategory.MISC).setDimensions(1.0F, 1.0F)
	);
	public static final EntityType<LeadKnotEntity> LEASH_KNOT = register(
		"leash_knot", EntityType.Builder.<LeadKnotEntity>create(LeadKnotEntity::new, EntityCategory.MISC).disableSaving().setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<LlamaEntity> LLAMA = register(
		"llama", EntityType.Builder.create(LlamaEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 1.87F)
	);
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register(
		"llama_spit", EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register(
		"magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(2.04F, 2.04F)
	);
	public static final EntityType<MinecartEntity> MINECART = register(
		"minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart", EntityType.Builder.<SpawnerMinecartEntity>create(SpawnerMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<TntMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.Builder.<TntMinecartEntity>create(TntMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98F, 0.7F)
	);
	public static final EntityType<MuleEntity> MULE = register(
		"mule", EntityType.Builder.create(MuleEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844F, 1.6F)
	);
	public static final EntityType<MooshroomEntity> MOOSHROOM = register(
		"mooshroom", EntityType.Builder.create(MooshroomEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 1.4F)
	);
	public static final EntityType<OcelotEntity> OCELOT = register(
		"ocelot", EntityType.Builder.create(OcelotEntity::new, EntityCategory.CREATURE).setDimensions(0.6F, 0.7F)
	);
	public static final EntityType<PaintingEntity> PAINTING = register(
		"painting", EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<PandaEntity> PANDA = register(
		"panda", EntityType.Builder.create(PandaEntity::new, EntityCategory.CREATURE).setDimensions(1.3F, 1.25F)
	);
	public static final EntityType<ParrotEntity> PARROT = register(
		"parrot", EntityType.Builder.create(ParrotEntity::new, EntityCategory.CREATURE).setDimensions(0.5F, 0.9F)
	);
	public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.create(PigEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 0.9F));
	public static final EntityType<PufferfishEntity> PUFFERFISH = register(
		"pufferfish", EntityType.Builder.create(PufferfishEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.7F, 0.7F)
	);
	public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN = register(
		"zombie_pigman", EntityType.Builder.create(ZombiePigmanEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register(
		"polar_bear", EntityType.Builder.create(PolarBearEntity::new, EntityCategory.CREATURE).setDimensions(1.4F, 1.4F)
	);
	public static final EntityType<TntEntity> TNT = register(
		"tnt", EntityType.Builder.<TntEntity>create(TntEntity::new, EntityCategory.MISC).makeFireImmune().setDimensions(0.98F, 0.98F)
	);
	public static final EntityType<RabbitEntity> RABBIT = register(
		"rabbit", EntityType.Builder.create(RabbitEntity::new, EntityCategory.CREATURE).setDimensions(0.4F, 0.5F)
	);
	public static final EntityType<SalmonEntity> SALMON = register(
		"salmon", EntityType.Builder.create(SalmonEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.7F, 0.4F)
	);
	public static final EntityType<SheepEntity> SHEEP = register(
		"sheep", EntityType.Builder.create(SheepEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 1.3F)
	);
	public static final EntityType<ShulkerEntity> SHULKER = register(
		"shulker", EntityType.Builder.create(ShulkerEntity::new, EntityCategory.MONSTER).makeFireImmune().spawnableFarFromPlayer().setDimensions(1.0F, 1.0F)
	);
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, EntityCategory.MISC).setDimensions(0.3125F, 0.3125F)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register(
		"silverfish", EntityType.Builder.create(SilverfishEntity::new, EntityCategory.MONSTER).setDimensions(0.4F, 0.3F)
	);
	public static final EntityType<SkeletonEntity> SKELETON = register(
		"skeleton", EntityType.Builder.create(SkeletonEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.99F)
	);
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844F, 1.6F)
	);
	public static final EntityType<SlimeEntity> SLIME = register(
		"slime", EntityType.Builder.create(SlimeEntity::new, EntityCategory.MONSTER).setDimensions(2.04F, 2.04F)
	);
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball", EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, EntityCategory.MISC).setDimensions(0.3125F, 0.3125F)
	);
	public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register(
		"snow_golem", EntityType.Builder.create(SnowGolemEntity::new, EntityCategory.MISC).setDimensions(0.7F, 1.9F)
	);
	public static final EntityType<SnowballEntity> SNOWBALL = register(
		"snowball", EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow", EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<SpiderEntity> SPIDER = register(
		"spider", EntityType.Builder.create(SpiderEntity::new, EntityCategory.MONSTER).setDimensions(1.4F, 0.9F)
	);
	public static final EntityType<SquidEntity> SQUID = register(
		"squid", EntityType.Builder.create(SquidEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.8F, 0.8F)
	);
	public static final EntityType<StrayEntity> STRAY = register(
		"stray", EntityType.Builder.create(StrayEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.99F)
	);
	public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register(
		"trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, EntityCategory.CREATURE).setDimensions(0.9F, 1.87F)
	);
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.5F, 0.4F)
	);
	public static final EntityType<TurtleEntity> TURTLE = register(
		"turtle", EntityType.Builder.create(TurtleEntity::new, EntityCategory.CREATURE).setDimensions(1.2F, 0.4F)
	);
	public static final EntityType<ThrownEggEntity> EGG = register(
		"egg", EntityType.Builder.<ThrownEggEntity>create(ThrownEggEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL = register(
		"ender_pearl", EntityType.Builder.<ThrownEnderpearlEntity>create(ThrownEnderpearlEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle",
		EntityType.Builder.<ThrownExperienceBottleEntity>create(ThrownExperienceBottleEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<ThrownPotionEntity> POTION = register(
		"potion", EntityType.Builder.<ThrownPotionEntity>create(ThrownPotionEntity::new, EntityCategory.MISC).setDimensions(0.25F, 0.25F)
	);
	public static final EntityType<TridentEntity> TRIDENT = register(
		"trident", EntityType.Builder.<TridentEntity>create(TridentEntity::new, EntityCategory.MISC).setDimensions(0.5F, 0.5F)
	);
	public static final EntityType<VexEntity> VEX = register(
		"vex", EntityType.Builder.create(VexEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.4F, 0.8F)
	);
	public static final EntityType<VillagerEntity> VILLAGER = register(
		"villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, EntityCategory.MISC).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register(
		"iron_golem", EntityType.Builder.create(IronGolemEntity::new, EntityCategory.MISC).setDimensions(1.4F, 2.7F)
	);
	public static final EntityType<VindicatorEntity> VINDICATOR = register(
		"vindicator", EntityType.Builder.create(VindicatorEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<PillagerEntity> PILLAGER = register(
		"pillager", EntityType.Builder.create(PillagerEntity::new, EntityCategory.MONSTER).spawnableFarFromPlayer().setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register(
		"wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, EntityCategory.CREATURE).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<WitchEntity> WITCH = register(
		"witch", EntityType.Builder.create(WitchEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<WitherEntity> WITHER = register(
		"wither", EntityType.Builder.create(WitherEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.9F, 3.5F)
	);
	public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.7F, 2.4F)
	);
	public static final EntityType<WitherSkullEntity> WITHER_SKULL = register(
		"wither_skull", EntityType.Builder.<WitherSkullEntity>create(WitherSkullEntity::new, EntityCategory.MISC).setDimensions(0.3125F, 0.3125F)
	);
	public static final EntityType<WolfEntity> WOLF = register(
		"wolf", EntityType.Builder.create(WolfEntity::new, EntityCategory.CREATURE).setDimensions(0.6F, 0.85F)
	);
	public static final EntityType<ZombieEntity> ZOMBIE = register(
		"zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844F, 1.6F)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, EntityCategory.MONSTER).setDimensions(0.6F, 1.95F)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register(
		"phantom", EntityType.Builder.create(PhantomEntity::new, EntityCategory.MONSTER).setDimensions(0.9F, 0.5F)
	);
	public static final EntityType<RavagerEntity> RAVAGER = register(
		"ravager", EntityType.Builder.create(RavagerEntity::new, EntityCategory.MONSTER).setDimensions(1.95F, 2.2F)
	);
	public static final EntityType<LightningEntity> LIGHTNING_BOLT = register(
		"lightning_bolt", EntityType.Builder.<LightningEntity>create(EntityCategory.MISC).disableSaving().setDimensions(0.0F, 0.0F)
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player", EntityType.Builder.<PlayerEntity>create(EntityCategory.MISC).disableSaving().disableSummon().setDimensions(0.6F, 1.8F)
	);
	public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register(
		"fishing_bobber", EntityType.Builder.<FishingBobberEntity>create(EntityCategory.MISC).disableSaving().disableSummon().setDimensions(0.25F, 0.25F)
	);
	private final EntityType.EntityFactory<T> factory;
	private final EntityCategory category;
	private final boolean saveable;
	private final boolean summonable;
	private final boolean fireImmune;
	private final boolean field_19423;
	@Nullable
	private String translationKey;
	@Nullable
	private Text name;
	@Nullable
	private Identifier lootTableId;
	private final EntityDimensions dimensions;

	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		return Registry.register(Registry.ENTITY_TYPE, id, type.build(id));
	}

	public static Identifier getId(EntityType<?> type) {
		return Registry.ENTITY_TYPE.getId(type);
	}

	public static Optional<EntityType<?>> get(String id) {
		return Registry.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(id));
	}

	public EntityType(
		EntityType.EntityFactory<T> factory,
		EntityCategory category,
		boolean saveable,
		boolean summonable,
		boolean fireImmune,
		boolean bl,
		EntityDimensions dimensions
	) {
		this.factory = factory;
		this.category = category;
		this.field_19423 = bl;
		this.saveable = saveable;
		this.summonable = summonable;
		this.fireImmune = fireImmune;
		this.dimensions = dimensions;
	}

	@Nullable
	public Entity spawnFromItemStack(
		World world, @Nullable ItemStack stack, @Nullable PlayerEntity player, BlockPos pos, SpawnType spawnType, boolean bl, boolean bl2
	) {
		return this.spawn(
			world, stack == null ? null : stack.getTag(), stack != null && stack.hasCustomName() ? stack.getName() : null, player, pos, spawnType, bl, bl2
		);
	}

	@Nullable
	public T spawn(
		World world, @Nullable CompoundTag itemTag, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnType spawnType, boolean bl, boolean bl2
	) {
		T entity = this.create(world, itemTag, name, player, pos, spawnType, bl, bl2);
		world.spawnEntity(entity);
		return entity;
	}

	@Nullable
	public T create(
		World world,
		@Nullable CompoundTag itemTag,
		@Nullable Text name,
		@Nullable PlayerEntity player,
		BlockPos pos,
		SpawnType spawnType,
		boolean alignPosition,
		boolean bl
	) {
		T entity = this.create(world);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (alignPosition) {
				entity.setPosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
				d = getOriginY(world, pos, bl, entity.getBoundingBox());
			} else {
				d = 0.0;
			}

			entity.setPositionAndAngles(
				(double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F
			);
			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				mobEntity.headYaw = mobEntity.yaw;
				mobEntity.bodyYaw = mobEntity.yaw;
				mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), spawnType, null, itemTag);
				mobEntity.playAmbientSound();
			}

			if (name != null && entity instanceof LivingEntity) {
				entity.setCustomName(name);
			}

			loadFromEntityTag(world, player, entity, itemTag);
			return entity;
		}
	}

	protected static double getOriginY(WorldView worldView, BlockPos pos, boolean bl, Box boundingBox) {
		Box box = new Box(pos);
		if (bl) {
			box = box.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = worldView.getCollisions(null, box, Collections.emptySet());
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream, bl ? -2.0 : -1.0);
	}

	public static void loadFromEntityTag(World world, @Nullable PlayerEntity player, @Nullable Entity entity, @Nullable CompoundTag itemTag) {
		if (itemTag != null && itemTag.contains("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient || !entity.entityDataRequiresOperator() || player != null && minecraftServer.getPlayerManager().isOperator(player.getGameProfile())) {
					CompoundTag compoundTag = entity.toTag(new CompoundTag());
					UUID uUID = entity.getUuid();
					compoundTag.copyFrom(itemTag.getCompound("EntityTag"));
					entity.setUuid(uUID);
					entity.fromTag(compoundTag);
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

	public boolean isFireImmune() {
		return this.fireImmune;
	}

	public boolean isSpawnableFarFromPlayer() {
		return this.field_19423;
	}

	public EntityCategory getCategory() {
		return this.category;
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
		}

		return this.translationKey;
	}

	public Text getName() {
		if (this.name == null) {
			this.name = new TranslatableText(this.getTranslationKey());
		}

		return this.name;
	}

	public Identifier getLootTableId() {
		if (this.lootTableId == null) {
			Identifier identifier = Registry.ENTITY_TYPE.getId(this);
			this.lootTableId = new Identifier(identifier.getNamespace(), "entities/" + identifier.getPath());
		}

		return this.lootTableId;
	}

	public float getWidth() {
		return this.dimensions.width;
	}

	public float getHeight() {
		return this.dimensions.height;
	}

	@Nullable
	public T create(World world) {
		return this.factory.create(this, world);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Entity createInstanceFromId(int type, World world) {
		return newInstance(world, Registry.ENTITY_TYPE.get(type));
	}

	public static Optional<Entity> getEntityFromTag(CompoundTag tag, World world) {
		return Util.ifPresentOrElse(
			fromTag(tag).map(entityType -> entityType.create(world)),
			entity -> entity.fromTag(tag),
			() -> LOGGER.warn("Skipping Entity with id {}", tag.getString("id"))
		);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static Entity newInstance(World world, @Nullable EntityType<?> type) {
		return type == null ? null : type.create(world);
	}

	public Box createSimpleBoundingBox(double feetX, double feetY, double feetZ) {
		float f = this.getWidth() / 2.0F;
		return new Box(feetX - (double)f, feetY, feetZ - (double)f, feetX + (double)f, feetY + (double)this.getHeight(), feetZ + (double)f);
	}

	public EntityDimensions getDimensions() {
		return this.dimensions;
	}

	public static Optional<EntityType<?>> fromTag(CompoundTag compoundTag) {
		return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(compoundTag.getString("id")));
	}

	@Nullable
	public static Entity loadEntityWithPassengers(CompoundTag compoundTag, World world, Function<Entity, Entity> entityProcessor) {
		return (Entity)loadEntityFromTag(compoundTag, world).map(entityProcessor).map(entity -> {
			if (compoundTag.contains("Passengers", 9)) {
				ListTag listTag = compoundTag.getList("Passengers", 10);

				for (int i = 0; i < listTag.size(); i++) {
					Entity entity2 = loadEntityWithPassengers(listTag.getCompound(i), world, entityProcessor);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}).orElse(null);
	}

	private static Optional<Entity> loadEntityFromTag(CompoundTag compoundTag, World world) {
		try {
			return getEntityFromTag(compoundTag, world);
		} catch (RuntimeException var3) {
			LOGGER.warn("Exception loading entity: ", (Throwable)var3);
			return Optional.empty();
		}
	}

	public int getMaxTrackDistance() {
		if (this == PLAYER) {
			return 32;
		} else if (this == END_CRYSTAL) {
			return 16;
		} else if (this == ENDER_DRAGON
			|| this == TNT
			|| this == FALLING_BLOCK
			|| this == ITEM_FRAME
			|| this == LEASH_KNOT
			|| this == PAINTING
			|| this == ARMOR_STAND
			|| this == EXPERIENCE_ORB
			|| this == AREA_EFFECT_CLOUD
			|| this == EVOKER_FANGS) {
			return 10;
		} else {
			return this != FISHING_BOBBER
					&& this != ARROW
					&& this != SPECTRAL_ARROW
					&& this != TRIDENT
					&& this != SMALL_FIREBALL
					&& this != DRAGON_FIREBALL
					&& this != FIREBALL
					&& this != WITHER_SKULL
					&& this != SNOWBALL
					&& this != LLAMA_SPIT
					&& this != ENDER_PEARL
					&& this != EYE_OF_ENDER
					&& this != EGG
					&& this != POTION
					&& this != EXPERIENCE_BOTTLE
					&& this != FIREWORK_ROCKET
					&& this != ITEM
				? 5
				: 4;
		}
	}

	public int getTrackTickInterval() {
		if (this == PLAYER || this == EVOKER_FANGS) {
			return 2;
		} else if (this == EYE_OF_ENDER) {
			return 4;
		} else if (this == FISHING_BOBBER) {
			return 5;
		} else if (this == SMALL_FIREBALL
			|| this == DRAGON_FIREBALL
			|| this == FIREBALL
			|| this == WITHER_SKULL
			|| this == SNOWBALL
			|| this == LLAMA_SPIT
			|| this == ENDER_PEARL
			|| this == EGG
			|| this == POTION
			|| this == EXPERIENCE_BOTTLE
			|| this == FIREWORK_ROCKET
			|| this == TNT) {
			return 10;
		} else if (this == ARROW || this == SPECTRAL_ARROW || this == TRIDENT || this == ITEM || this == FALLING_BLOCK || this == EXPERIENCE_ORB) {
			return 20;
		} else {
			return this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != AREA_EFFECT_CLOUD && this != END_CRYSTAL ? 3 : Integer.MAX_VALUE;
		}
	}

	public boolean alwaysUpdateVelocity() {
		return this != PLAYER
			&& this != LLAMA_SPIT
			&& this != WITHER
			&& this != BAT
			&& this != ITEM_FRAME
			&& this != LEASH_KNOT
			&& this != PAINTING
			&& this != END_CRYSTAL
			&& this != EVOKER_FANGS;
	}

	public boolean isTaggedWith(Tag<EntityType<?>> tag) {
		return tag.contains(this);
	}

	public static class Builder<T extends Entity> {
		private final EntityType.EntityFactory<T> factory;
		private final EntityCategory category;
		private boolean saveable = true;
		private boolean summonable = true;
		private boolean fireImmune;
		private boolean field_19424;
		private EntityDimensions size = EntityDimensions.changing(0.6F, 1.8F);

		private Builder(EntityType.EntityFactory<T> factory, EntityCategory category) {
			this.factory = factory;
			this.category = category;
			this.field_19424 = category == EntityCategory.CREATURE || category == EntityCategory.MISC;
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityType.EntityFactory<T> factory, EntityCategory category) {
			return new EntityType.Builder<>(factory, category);
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityCategory category) {
			return new EntityType.Builder<>((entityType, world) -> null, category);
		}

		public EntityType.Builder<T> setDimensions(float width, float height) {
			this.size = EntityDimensions.changing(width, height);
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

		public EntityType.Builder<T> makeFireImmune() {
			this.fireImmune = true;
			return this;
		}

		public EntityType.Builder<T> spawnableFarFromPlayer() {
			this.field_19424 = true;
			return this;
		}

		public EntityType<T> build(String id) {
			if (this.saveable) {
				try {
					Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TREE, id);
				} catch (IllegalStateException var3) {
					if (SharedConstants.isDevelopment) {
						throw var3;
					}

					EntityType.LOGGER.warn("No data fixer registered for entity {}", id);
				}
			}

			return new EntityType<>(this.factory, this.category, this.saveable, this.summonable, this.fireImmune, this.field_19424, this.size);
		}
	}

	public interface EntityFactory<T extends Entity> {
		T create(EntityType<T> type, World world);
	}
}
