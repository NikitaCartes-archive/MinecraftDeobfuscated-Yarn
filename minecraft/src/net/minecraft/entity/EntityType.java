package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
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
import net.minecraft.entity.mob.PigZombieEntity;
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
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
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
import net.minecraft.entity.passive.SnowmanEntity;
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
import net.minecraft.nbt.ListTag;
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
		"area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>method_5903(AreaEffectCloudEntity::new, EntityCategory.field_17715).setSize(6.0F, 0.5F)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>method_5903(ArmorStandEntity::new, EntityCategory.field_17715).setSize(0.5F, 1.975F)
	);
	public static final EntityType<ArrowEntity> ARROW = register(
		"arrow", EntityType.Builder.<ArrowEntity>method_5903(ArrowEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.method_5903(BatEntity::new, EntityCategory.field_6303).setSize(0.5F, 0.9F));
	public static final EntityType<BlazeEntity> BLAZE = register(
		"blaze", EntityType.Builder.method_5903(BlazeEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.8F)
	);
	public static final EntityType<BoatEntity> BOAT = register(
		"boat", EntityType.Builder.<BoatEntity>method_5903(BoatEntity::new, EntityCategory.field_17715).setSize(1.375F, 0.5625F)
	);
	public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.method_5903(CatEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.7F));
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.Builder.method_5903(CaveSpiderEntity::new, EntityCategory.field_6302).setSize(0.7F, 0.5F)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register(
		"chicken", EntityType.Builder.method_5903(ChickenEntity::new, EntityCategory.field_6294).setSize(0.4F, 0.7F)
	);
	public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.method_5903(CodEntity::new, EntityCategory.field_6300).setSize(0.5F, 0.3F));
	public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.method_5903(CowEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.4F));
	public static final EntityType<CreeperEntity> CREEPER = register(
		"creeper", EntityType.Builder.method_5903(CreeperEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.7F)
	);
	public static final EntityType<DonkeyEntity> DONKEY = register(
		"donkey", EntityType.Builder.method_5903(DonkeyEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<DolphinEntity> DOLPHIN = register(
		"dolphin", EntityType.Builder.method_5903(DolphinEntity::new, EntityCategory.field_6300).setSize(0.9F, 0.6F)
	);
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball", EntityType.Builder.<DragonFireballEntity>method_5903(DragonFireballEntity::new, EntityCategory.field_17715).setSize(1.0F, 1.0F)
	);
	public static final EntityType<DrownedEntity> DROWNED = register(
		"drowned", EntityType.Builder.method_5903(DrownedEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.Builder.method_5903(ElderGuardianEntity::new, EntityCategory.field_6302).setSize(1.9975F, 1.9975F)
	);
	public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register(
		"end_crystal", EntityType.Builder.<EnderCrystalEntity>method_5903(EnderCrystalEntity::new, EntityCategory.field_17715).setSize(2.0F, 2.0F)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.Builder.method_5903(EnderDragonEntity::new, EntityCategory.field_6302).setSize(16.0F, 8.0F)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register(
		"enderman", EntityType.Builder.method_5903(EndermanEntity::new, EntityCategory.field_6302).setSize(0.6F, 2.9F)
	);
	public static final EntityType<EndermiteEntity> ENDERMITE = register(
		"endermite", EntityType.Builder.method_5903(EndermiteEntity::new, EntityCategory.field_6302).setSize(0.4F, 0.3F)
	);
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs", EntityType.Builder.<EvokerFangsEntity>method_5903(EvokerFangsEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.8F)
	);
	public static final EntityType<EvokerEntity> EVOKER = register(
		"evoker", EntityType.Builder.method_5903(EvokerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb", EntityType.Builder.<ExperienceOrbEntity>method_5903(ExperienceOrbEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<EnderEyeEntity> EYE_OF_ENDER = register(
		"eye_of_ender", EntityType.Builder.<EnderEyeEntity>method_5903(EnderEyeEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block", EntityType.Builder.<FallingBlockEntity>method_5903(FallingBlockEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.98F)
	);
	public static final EntityType<FireworkEntity> FIREWORK_ROCKET = register(
		"firework_rocket", EntityType.Builder.<FireworkEntity>method_5903(FireworkEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<FoxEntity> field_17943 = register(
		"fox", EntityType.Builder.method_5903(FoxEntity::new, EntityCategory.field_6294).setSize(0.5F, 0.7F)
	);
	public static final EntityType<GhastEntity> GHAST = register(
		"ghast", EntityType.Builder.method_5903(GhastEntity::new, EntityCategory.field_6302).setSize(4.0F, 4.0F)
	);
	public static final EntityType<GiantEntity> GIANT = register(
		"giant", EntityType.Builder.method_5903(GiantEntity::new, EntityCategory.field_6302).setSize(3.6F, 11.7F)
	);
	public static final EntityType<GuardianEntity> GUARDIAN = register(
		"guardian", EntityType.Builder.method_5903(GuardianEntity::new, EntityCategory.field_6302).setSize(0.85F, 0.85F)
	);
	public static final EntityType<HorseEntity> HORSE = register(
		"horse", EntityType.Builder.method_5903(HorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<HuskEntity> HUSK = register(
		"husk", EntityType.Builder.method_5903(HuskEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<IllusionerEntity> ILLUSIONER = register(
		"illusioner", EntityType.Builder.method_5903(IllusionerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ItemEntity> ITEM = register(
		"item", EntityType.Builder.<ItemEntity>method_5903(ItemEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register(
		"item_frame", EntityType.Builder.<ItemFrameEntity>method_5903(ItemFrameEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<FireballEntity> FIREBALL = register(
		"fireball", EntityType.Builder.<FireballEntity>method_5903(FireballEntity::new, EntityCategory.field_17715).setSize(1.0F, 1.0F)
	);
	public static final EntityType<LeadKnotEntity> LEASH_KNOT = register(
		"leash_knot", EntityType.Builder.<LeadKnotEntity>method_5903(LeadKnotEntity::new, EntityCategory.field_17715).disableSaving().setSize(0.5F, 0.5F)
	);
	public static final EntityType<LlamaEntity> LLAMA = register(
		"llama", EntityType.Builder.method_5903(LlamaEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.87F)
	);
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register(
		"llama_spit", EntityType.Builder.<LlamaSpitEntity>method_5903(LlamaSpitEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register(
		"magma_cube", EntityType.Builder.method_5903(MagmaCubeEntity::new, EntityCategory.field_6302).setSize(2.04F, 2.04F)
	);
	public static final EntityType<MinecartEntity> MINECART = register(
		"minecart", EntityType.Builder.<MinecartEntity>method_5903(MinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.Builder.<ChestMinecartEntity>method_5903(ChestMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>method_5903(CommandBlockMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>method_5903(FurnaceMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.Builder.<HopperMinecartEntity>method_5903(HopperMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<MobSpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart", EntityType.Builder.<MobSpawnerMinecartEntity>method_5903(MobSpawnerMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<TNTMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.Builder.<TNTMinecartEntity>method_5903(TNTMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<MuleEntity> MULE = register(
		"mule", EntityType.Builder.method_5903(MuleEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<MooshroomEntity> MOOSHROOM = register(
		"mooshroom", EntityType.Builder.method_5903(MooshroomEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.4F)
	);
	public static final EntityType<OcelotEntity> OCELOT = register(
		"ocelot", EntityType.Builder.method_5903(OcelotEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.7F)
	);
	public static final EntityType<PaintingEntity> PAINTING = register(
		"painting", EntityType.Builder.<PaintingEntity>method_5903(PaintingEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<PandaEntity> PANDA = register(
		"panda", EntityType.Builder.method_5903(PandaEntity::new, EntityCategory.field_6294).setSize(1.3F, 1.25F)
	);
	public static final EntityType<ParrotEntity> PARROT = register(
		"parrot", EntityType.Builder.method_5903(ParrotEntity::new, EntityCategory.field_6294).setSize(0.5F, 0.9F)
	);
	public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.method_5903(PigEntity::new, EntityCategory.field_6294).setSize(0.9F, 0.9F));
	public static final EntityType<PufferfishEntity> PUFFERFISH = register(
		"pufferfish", EntityType.Builder.method_5903(PufferfishEntity::new, EntityCategory.field_6300).setSize(0.7F, 0.7F)
	);
	public static final EntityType<PigZombieEntity> ZOMBIE_PIGMAN = register(
		"zombie_pigman", EntityType.Builder.method_5903(PigZombieEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register(
		"polar_bear", EntityType.Builder.method_5903(PolarBearEntity::new, EntityCategory.field_6294).setSize(1.3F, 1.4F)
	);
	public static final EntityType<PrimedTntEntity> TNT = register(
		"tnt", EntityType.Builder.<PrimedTntEntity>method_5903(PrimedTntEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.98F)
	);
	public static final EntityType<RabbitEntity> RABBIT = register(
		"rabbit", EntityType.Builder.method_5903(RabbitEntity::new, EntityCategory.field_6294).setSize(0.4F, 0.5F)
	);
	public static final EntityType<SalmonEntity> SALMON = register(
		"salmon", EntityType.Builder.method_5903(SalmonEntity::new, EntityCategory.field_6300).setSize(0.7F, 0.4F)
	);
	public static final EntityType<SheepEntity> SHEEP = register(
		"sheep", EntityType.Builder.method_5903(SheepEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.3F)
	);
	public static final EntityType<ShulkerEntity> SHULKER = register(
		"shulker", EntityType.Builder.method_5903(ShulkerEntity::new, EntityCategory.field_6302).setSize(1.0F, 1.0F)
	);
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>method_5903(ShulkerBulletEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register(
		"silverfish", EntityType.Builder.method_5903(SilverfishEntity::new, EntityCategory.field_6302).setSize(0.4F, 0.3F)
	);
	public static final EntityType<SkeletonEntity> SKELETON = register(
		"skeleton", EntityType.Builder.method_5903(SkeletonEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.99F)
	);
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.Builder.method_5903(SkeletonHorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<SlimeEntity> SLIME = register(
		"slime", EntityType.Builder.method_5903(SlimeEntity::new, EntityCategory.field_6302).setSize(2.04F, 2.04F)
	);
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball", EntityType.Builder.<SmallFireballEntity>method_5903(SmallFireballEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<SnowmanEntity> SNOW_GOLEM = register(
		"snow_golem", EntityType.Builder.method_5903(SnowmanEntity::new, EntityCategory.field_6294).setSize(0.7F, 1.9F)
	);
	public static final EntityType<SnowballEntity> SNOWBALL = register(
		"snowball", EntityType.Builder.<SnowballEntity>method_5903(SnowballEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow", EntityType.Builder.<SpectralArrowEntity>method_5903(SpectralArrowEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<SpiderEntity> SPIDER = register(
		"spider", EntityType.Builder.method_5903(SpiderEntity::new, EntityCategory.field_6302).setSize(1.4F, 0.9F)
	);
	public static final EntityType<SquidEntity> SQUID = register(
		"squid", EntityType.Builder.method_5903(SquidEntity::new, EntityCategory.field_6300).setSize(0.8F, 0.8F)
	);
	public static final EntityType<StrayEntity> STRAY = register(
		"stray", EntityType.Builder.method_5903(StrayEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.99F)
	);
	public static final EntityType<TraderLlamaEntity> field_17714 = register(
		"trader_llama", EntityType.Builder.method_5903(TraderLlamaEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.87F)
	);
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.Builder.method_5903(TropicalFishEntity::new, EntityCategory.field_6300).setSize(0.5F, 0.4F)
	);
	public static final EntityType<TurtleEntity> TURTLE = register(
		"turtle", EntityType.Builder.method_5903(TurtleEntity::new, EntityCategory.field_6294).setSize(1.2F, 0.4F)
	);
	public static final EntityType<ThrownEggEntity> EGG = register(
		"egg", EntityType.Builder.<ThrownEggEntity>method_5903(ThrownEggEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL = register(
		"ender_pearl", EntityType.Builder.<ThrownEnderpearlEntity>method_5903(ThrownEnderpearlEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle",
		EntityType.Builder.<ThrownExperienceBottleEntity>method_5903(ThrownExperienceBottleEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownPotionEntity> POTION = register(
		"potion", EntityType.Builder.<ThrownPotionEntity>method_5903(ThrownPotionEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<TridentEntity> TRIDENT = register(
		"trident", EntityType.Builder.<TridentEntity>method_5903(TridentEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.method_5903(VexEntity::new, EntityCategory.field_6302).setSize(0.4F, 0.8F));
	public static final EntityType<VillagerEntity> VILLAGER = register(
		"villager", EntityType.Builder.<VillagerEntity>method_5903(VillagerEntity::new, EntityCategory.field_6294).setSize(0.6F, 1.95F)
	);
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register(
		"iron_golem", EntityType.Builder.method_5903(IronGolemEntity::new, EntityCategory.field_6294).setSize(1.4F, 2.7F)
	);
	public static final EntityType<VindicatorEntity> VINDICATOR = register(
		"vindicator", EntityType.Builder.method_5903(VindicatorEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<PillagerEntity> PILLAGER = register(
		"pillager", EntityType.Builder.method_5903(PillagerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WanderingTraderEntity> field_17713 = register(
		"wandering_trader", EntityType.Builder.method_5903(WanderingTraderEntity::new, EntityCategory.field_6294).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WitchEntity> WITCH = register(
		"witch", EntityType.Builder.method_5903(WitchEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WitherEntity> WITHER = register(
		"wither", EntityType.Builder.method_5903(WitherEntity::new, EntityCategory.field_6302).setSize(0.9F, 3.5F)
	);
	public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton", EntityType.Builder.method_5903(WitherSkeletonEntity::new, EntityCategory.field_6302).setSize(0.7F, 2.4F)
	);
	public static final EntityType<ExplodingWitherSkullEntity> WITHER_SKULL = register(
		"wither_skull",
		EntityType.Builder.<ExplodingWitherSkullEntity>method_5903(ExplodingWitherSkullEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<WolfEntity> WOLF = register(
		"wolf", EntityType.Builder.method_5903(WolfEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.85F)
	);
	public static final EntityType<ZombieEntity> ZOMBIE = register(
		"zombie", EntityType.Builder.<ZombieEntity>method_5903(ZombieEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.Builder.method_5903(ZombieHorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.Builder.method_5903(ZombieVillagerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register(
		"phantom", EntityType.Builder.method_5903(PhantomEntity::new, EntityCategory.field_6302).setSize(0.9F, 0.5F)
	);
	public static final EntityType<RavagerEntity> RAVAGER = register(
		"ravager", EntityType.Builder.method_5903(RavagerEntity::new, EntityCategory.field_6302).setSize(1.95F, 2.2F)
	);
	public static final EntityType<LightningEntity> LIGHTNING_BOLT = register(
		"lightning_bolt", EntityType.Builder.<LightningEntity>method_5902(EntityCategory.field_17715).disableSaving().setSize(0.0F, 0.0F)
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player", EntityType.Builder.<PlayerEntity>method_5902(EntityCategory.field_17715).disableSaving().disableSummon().setSize(0.6F, 1.8F)
	);
	public static final EntityType<FishHookEntity> FISHING_BOBBER = register(
		"fishing_bobber", EntityType.Builder.<FishHookEntity>method_5902(EntityCategory.field_17715).disableSaving().disableSummon().setSize(0.25F, 0.25F)
	);
	private final EntityType.class_4049<T> factory;
	private final EntityCategory field_6094;
	private final boolean saveable;
	private final boolean summonable;
	@Nullable
	private String translationKey;
	@Nullable
	private TextComponent field_6092;
	@Nullable
	private Identifier field_16526;
	@Nullable
	private final Type<?> dataFixerType;
	private final EntitySize size;

	private static <T extends Entity> EntityType<T> register(String string, EntityType.Builder<T> builder) {
		return Registry.register(Registry.ENTITY_TYPE, string, builder.build(string));
	}

	public static Identifier method_5890(EntityType<?> entityType) {
		return Registry.ENTITY_TYPE.method_10221(entityType);
	}

	public static Optional<EntityType<?>> get(String string) {
		return Registry.ENTITY_TYPE.method_17966(Identifier.create(string));
	}

	public EntityType(EntityType.class_4049<T> arg, EntityCategory entityCategory, boolean bl, boolean bl2, @Nullable Type<?> type, EntitySize entitySize) {
		this.factory = arg;
		this.field_6094 = entityCategory;
		this.saveable = bl;
		this.summonable = bl2;
		this.dataFixerType = type;
		this.size = entitySize;
	}

	@Nullable
	public Entity method_5894(
		World world, @Nullable ItemStack itemStack, @Nullable PlayerEntity playerEntity, BlockPos blockPos, SpawnType spawnType, boolean bl, boolean bl2
	) {
		return this.method_5899(
			world,
			itemStack == null ? null : itemStack.method_7969(),
			itemStack != null && itemStack.hasDisplayName() ? itemStack.method_7964() : null,
			playerEntity,
			blockPos,
			spawnType,
			bl,
			bl2
		);
	}

	@Nullable
	public T method_5899(
		World world,
		@Nullable CompoundTag compoundTag,
		@Nullable TextComponent textComponent,
		@Nullable PlayerEntity playerEntity,
		BlockPos blockPos,
		SpawnType spawnType,
		boolean bl,
		boolean bl2
	) {
		T entity = this.method_5888(world, compoundTag, textComponent, playerEntity, blockPos, spawnType, bl, bl2);
		world.spawnEntity(entity);
		return entity;
	}

	@Nullable
	public T method_5888(
		World world,
		@Nullable CompoundTag compoundTag,
		@Nullable TextComponent textComponent,
		@Nullable PlayerEntity playerEntity,
		BlockPos blockPos,
		SpawnType spawnType,
		boolean bl,
		boolean bl2
	) {
		T entity = this.method_5883(world);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (bl) {
				entity.setPosition((double)blockPos.getX() + 0.5, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5);
				d = method_5884(world, blockPos, bl2, entity.method_5829());
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
				mobEntity.method_5943(world, world.method_8404(new BlockPos(mobEntity)), spawnType, null, compoundTag);
				mobEntity.playAmbientSound();
			}

			if (textComponent != null && entity instanceof LivingEntity) {
				entity.method_5665(textComponent);
			}

			method_5881(world, playerEntity, entity, compoundTag);
			return entity;
		}
	}

	protected static double method_5884(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl, BoundingBox boundingBox) {
		BoundingBox boundingBox2 = new BoundingBox(blockPos);
		if (bl) {
			boundingBox2 = boundingBox2.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = viewableWorld.method_8600(null, boundingBox2, Collections.emptySet());
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream, bl ? -2.0 : -1.0);
	}

	public static void method_5881(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient || !entity.method_5833() || playerEntity != null && minecraftServer.method_3760().isOperator(playerEntity.getGameProfile())) {
					CompoundTag compoundTag2 = entity.method_5647(new CompoundTag());
					UUID uUID = entity.getUuid();
					compoundTag2.copyFrom(compoundTag.getCompound("EntityTag"));
					entity.setUuid(uUID);
					entity.method_5651(compoundTag2);
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

	public EntityCategory method_5891() {
		return this.field_6094;
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = SystemUtil.method_646("entity", Registry.ENTITY_TYPE.method_10221(this));
		}

		return this.translationKey;
	}

	public TextComponent method_5897() {
		if (this.field_6092 == null) {
			this.field_6092 = new TranslatableTextComponent(this.getTranslationKey());
		}

		return this.field_6092;
	}

	public Identifier method_16351() {
		if (this.field_16526 == null) {
			Identifier identifier = Registry.ENTITY_TYPE.method_10221(this);
			this.field_16526 = new Identifier(identifier.getNamespace(), "entities/" + identifier.getPath());
		}

		return this.field_16526;
	}

	public float getWidth() {
		return this.size.width;
	}

	public float getHeight() {
		return this.size.height;
	}

	@Nullable
	public T method_5883(World world) {
		return this.factory.create(this, world);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Entity method_5889(int i, World world) {
		return method_5886(world, Registry.ENTITY_TYPE.get(i));
	}

	public static Optional<Entity> method_5892(CompoundTag compoundTag, World world) {
		return SystemUtil.method_17974(
			method_17684(compoundTag).map(entityType -> entityType.method_5883(world)),
			entity -> entity.method_5651(compoundTag),
			() -> LOGGER.warn("Skipping Entity with id {}", compoundTag.getString("id"))
		);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static Entity method_5886(World world, @Nullable EntityType<?> entityType) {
		return entityType == null ? null : entityType.method_5883(world);
	}

	public BoundingBox method_17683(double d, double e, double f) {
		float g = this.getWidth() / 2.0F;
		return new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.getHeight(), f + (double)g);
	}

	public EntitySize getDefaultSize() {
		return this.size;
	}

	public static Optional<EntityType<?>> method_17684(CompoundTag compoundTag) {
		return Registry.ENTITY_TYPE.method_17966(new Identifier(compoundTag.getString("id")));
	}

	@Nullable
	public static Entity method_17842(CompoundTag compoundTag, World world, Function<Entity, Entity> function) {
		return (Entity)method_17848(compoundTag, world).map(function).map(entity -> {
			if (compoundTag.containsKey("Passengers", 9)) {
				ListTag listTag = compoundTag.method_10554("Passengers", 10);

				for (int i = 0; i < listTag.size(); i++) {
					Entity entity2 = method_17842(listTag.getCompoundTag(i), world, function);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}).orElse(null);
	}

	private static Optional<Entity> method_17848(CompoundTag compoundTag, World world) {
		try {
			return method_5892(compoundTag, world);
		} catch (RuntimeException var3) {
			LOGGER.warn("Exception loading entity: ", (Throwable)var3);
			return Optional.empty();
		}
	}

	public int method_18387() {
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

	public int method_18388() {
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

	public boolean method_18389() {
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

	public static class Builder<T extends Entity> {
		private final EntityType.class_4049<T> function;
		private final EntityCategory field_6149;
		private boolean saveable = true;
		private boolean summonable = true;
		private EntitySize field_18071 = EntitySize.resizeable(0.6F, 1.8F);

		private Builder(EntityType.class_4049<T> arg, EntityCategory entityCategory) {
			this.function = arg;
			this.field_6149 = entityCategory;
		}

		public static <T extends Entity> EntityType.Builder<T> method_5903(EntityType.class_4049<T> arg, EntityCategory entityCategory) {
			return new EntityType.Builder<>(arg, entityCategory);
		}

		public static <T extends Entity> EntityType.Builder<T> method_5902(EntityCategory entityCategory) {
			return new EntityType.Builder<>((entityType, world) -> null, entityCategory);
		}

		public EntityType.Builder<T> setSize(float f, float g) {
			this.field_18071 = EntitySize.resizeable(f, g);
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

			return new EntityType<>(this.function, this.field_6149, this.saveable, this.summonable, type, this.field_18071);
		}
	}

	public interface class_4049<T extends Entity> {
		T create(EntityType<T> entityType, World world);
	}
}
