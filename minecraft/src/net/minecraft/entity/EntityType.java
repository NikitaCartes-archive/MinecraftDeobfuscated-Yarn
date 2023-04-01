package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
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
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
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
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MoonCowEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.RayTracingEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TadpoleEntity;
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
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements ToggleableFeature, TypeFilter<Entity, T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String ENTITY_TAG_KEY = "EntityTag";
	private final RegistryEntry.Reference<EntityType<?>> registryEntry = Registries.ENTITY_TYPE.createEntry(this);
	private static final float field_30054 = 1.3964844F;
	private static final int field_42459 = 10;
	public static final EntityType<AllayEntity> ALLAY = register(
		"allay", EntityType.Builder.create(AllayEntity::new, SpawnGroup.CREATURE).setDimensions(0.35F, 0.6F).maxTrackingRange(8).trackingTickInterval(2)
	);
	public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register(
		"area_effect_cloud",
		EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, SpawnGroup.MISC)
			.makeFireImmune()
			.setDimensions(6.0F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 1.975F).maxTrackingRange(10)
	);
	public static final EntityType<ArrowEntity> ARROW = register(
		"arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20)
	);
	public static final EntityType<AxolotlEntity> AXOLOTL = register(
		"axolotl", EntityType.Builder.create(AxolotlEntity::new, SpawnGroup.AXOLOTLS).setDimensions(0.75F, 0.42F).maxTrackingRange(10)
	);
	public static final EntityType<BatEntity> BAT = register(
		"bat", EntityType.Builder.create(BatEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5F, 0.9F).maxTrackingRange(5)
	);
	public static final EntityType<BeeEntity> BEE = register(
		"bee", EntityType.Builder.create(BeeEntity::new, SpawnGroup.CREATURE).setDimensions(0.7F, 0.6F).maxTrackingRange(8)
	);
	public static final EntityType<BlazeEntity> BLAZE = register(
		"blaze", EntityType.Builder.create(BlazeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6F, 1.8F).maxTrackingRange(8)
	);
	public static final EntityType<DisplayEntity.BlockDisplayEntity> BLOCK_DISPLAY = register(
		"block_display",
		EntityType.Builder.create(DisplayEntity.BlockDisplayEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1)
	);
	public static final EntityType<BoatEntity> BOAT = register(
		"boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F).maxTrackingRange(10)
	);
	public static final EntityType<CamelEntity> CAMEL = register(
		"camel", EntityType.Builder.create(CamelEntity::new, SpawnGroup.CREATURE).setDimensions(1.7F, 2.375F).maxTrackingRange(10)
	);
	public static final EntityType<CatEntity> CAT = register(
		"cat", EntityType.Builder.create(CatEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, SpawnGroup.MONSTER).setDimensions(0.7F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<ChestBoatEntity> CHEST_BOAT = register(
		"chest_boat", EntityType.Builder.<ChestBoatEntity>create(ChestBoatEntity::new, SpawnGroup.MISC).setDimensions(1.375F, 0.5625F).maxTrackingRange(10)
	);
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register(
		"chicken", EntityType.Builder.create(ChickenEntity::new, SpawnGroup.CREATURE).setDimensions(0.4F, 0.7F).maxTrackingRange(10)
	);
	public static final EntityType<CodEntity> COD = register(
		"cod", EntityType.Builder.create(CodEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5F, 0.3F).maxTrackingRange(4)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<CowEntity> COW = register(
		"cow", EntityType.Builder.create(CowEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<CreeperEntity> CREEPER = register(
		"creeper", EntityType.Builder.create(CreeperEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.7F).maxTrackingRange(8)
	);
	public static final EntityType<DolphinEntity> DOLPHIN = register(
		"dolphin", EntityType.Builder.create(DolphinEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.9F, 0.6F)
	);
	public static final EntityType<DonkeyEntity> DONKEY = register(
		"donkey", EntityType.Builder.create(DonkeyEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.5F).maxTrackingRange(10)
	);
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball",
		EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, SpawnGroup.MISC)
			.setDimensions(1.0F, 1.0F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<DrownedEntity> DROWNED = register(
		"drowned", EntityType.Builder.create(DrownedEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<EggEntity> EGG = register(
		"egg", EntityType.Builder.<EggEntity>create(EggEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, SpawnGroup.MONSTER).setDimensions(1.9975F, 1.9975F).maxTrackingRange(10)
	);
	public static final EntityType<EndCrystalEntity> END_CRYSTAL = register(
		"end_crystal",
		EntityType.Builder.<EndCrystalEntity>create(EndCrystalEntity::new, SpawnGroup.MISC)
			.setDimensions(2.0F, 2.0F)
			.maxTrackingRange(16)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(16.0F, 8.0F).maxTrackingRange(10)
	);
	public static final EntityType<EnderPearlEntity> ENDER_PEARL = register(
		"ender_pearl",
		EntityType.Builder.<EnderPearlEntity>create(EnderPearlEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register(
		"enderman", EntityType.Builder.create(EndermanEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 2.9F).maxTrackingRange(8)
	);
	public static final EntityType<EndermiteEntity> ENDERMITE = register(
		"endermite", EntityType.Builder.create(EndermiteEntity::new, SpawnGroup.MONSTER).setDimensions(0.4F, 0.3F).maxTrackingRange(8)
	);
	public static final EntityType<EvokerEntity> EVOKER = register(
		"evoker", EntityType.Builder.create(EvokerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs",
		EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.8F).maxTrackingRange(6).trackingTickInterval(2)
	);
	public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle",
		EntityType.Builder.<ExperienceBottleEntity>create(ExperienceBottleEntity::new, SpawnGroup.MISC)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb",
		EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, SpawnGroup.MISC)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(6)
			.trackingTickInterval(20)
	);
	public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register(
		"eye_of_ender",
		EntityType.Builder.<EyeOfEnderEntity>create(EyeOfEnderEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(4)
	);
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block",
		EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, SpawnGroup.MISC)
			.setDimensions(0.98F, 0.98F)
			.maxTrackingRange(10)
			.trackingTickInterval(20)
	);
	public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register(
		"firework_rocket",
		EntityType.Builder.<FireworkRocketEntity>create(FireworkRocketEntity::new, SpawnGroup.MISC)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<FoxEntity> FOX = register(
		"fox",
		EntityType.Builder.create(FoxEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F, 0.7F).maxTrackingRange(8).allowSpawningInside(Blocks.SWEET_BERRY_BUSH)
	);
	public static final EntityType<FrogEntity> FROG = register(
		"frog", EntityType.Builder.create(FrogEntity::new, SpawnGroup.CREATURE).setDimensions(0.5F, 0.5F).maxTrackingRange(10)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart",
		EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<GhastEntity> GHAST = register(
		"ghast", EntityType.Builder.create(GhastEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(4.0F, 4.0F).maxTrackingRange(10)
	);
	public static final EntityType<GiantEntity> GIANT = register(
		"giant", EntityType.Builder.create(GiantEntity::new, SpawnGroup.MONSTER).setDimensions(3.6F, 12.0F).maxTrackingRange(10)
	);
	public static final EntityType<GlowItemFrameEntity> GLOW_ITEM_FRAME = register(
		"glow_item_frame",
		EntityType.Builder.<GlowItemFrameEntity>create(GlowItemFrameEntity::new, SpawnGroup.MISC)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<GlowSquidEntity> GLOW_SQUID = register(
		"glow_squid", EntityType.Builder.create(GlowSquidEntity::new, SpawnGroup.UNDERGROUND_WATER_CREATURE).setDimensions(0.8F, 0.8F).maxTrackingRange(10)
	);
	public static final EntityType<GoatEntity> GOAT = register(
		"goat", EntityType.Builder.create(GoatEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.3F).maxTrackingRange(10)
	);
	public static final EntityType<GuardianEntity> GUARDIAN = register(
		"guardian", EntityType.Builder.create(GuardianEntity::new, SpawnGroup.MONSTER).setDimensions(0.85F, 0.85F).maxTrackingRange(8)
	);
	public static final EntityType<HoglinEntity> HOGLIN = register(
		"hoglin", EntityType.Builder.create(HoglinEntity::new, SpawnGroup.MONSTER).setDimensions(1.3964844F, 1.4F).maxTrackingRange(8)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<HorseEntity> HORSE = register(
		"horse", EntityType.Builder.create(HorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<HuskEntity> HUSK = register(
		"husk", EntityType.Builder.create(HuskEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<IllusionerEntity> ILLUSIONER = register(
		"illusioner", EntityType.Builder.create(IllusionerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<InteractionEntity> INTERACTION = register(
		"interaction", EntityType.Builder.create(InteractionEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(10)
	);
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register(
		"iron_golem", EntityType.Builder.create(IronGolemEntity::new, SpawnGroup.MISC).setDimensions(1.4F, 2.7F).maxTrackingRange(10)
	);
	public static final EntityType<ItemEntity> ITEM = register(
		"item", EntityType.Builder.<ItemEntity>create(ItemEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(6).trackingTickInterval(20)
	);
	public static final EntityType<DisplayEntity.ItemDisplayEntity> ITEM_DISPLAY = register(
		"item_display",
		EntityType.Builder.create(DisplayEntity.ItemDisplayEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1)
	);
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register(
		"item_frame",
		EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, SpawnGroup.MISC)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<FireballEntity> FIREBALL = register(
		"fireball",
		EntityType.Builder.<FireballEntity>create(FireballEntity::new, SpawnGroup.MISC).setDimensions(1.0F, 1.0F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<LeashKnotEntity> LEASH_KNOT = register(
		"leash_knot",
		EntityType.Builder.<LeashKnotEntity>create(LeashKnotEntity::new, SpawnGroup.MISC)
			.disableSaving()
			.setDimensions(0.375F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<LightningEntity> LIGHTNING_BOLT = register(
		"lightning_bolt",
		EntityType.Builder.create(LightningEntity::new, SpawnGroup.MISC)
			.disableSaving()
			.setDimensions(0.0F, 0.0F)
			.maxTrackingRange(16)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<LlamaEntity> LLAMA = register(
		"llama", EntityType.Builder.create(LlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.87F).maxTrackingRange(10)
	);
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register(
		"llama_spit",
		EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register(
		"magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(2.04F, 2.04F).maxTrackingRange(8)
	);
	public static final EntityType<MarkerEntity> MARKER = register(
		"marker", EntityType.Builder.create(MarkerEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(0)
	);
	public static final EntityType<MinecartEntity> MINECART = register(
		"minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<MooshroomEntity> MOOSHROOM = register(
		"mooshroom", EntityType.Builder.create(MooshroomEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<MoonCowEntity> MOON_COW = register(
		"moon_cow", EntityType.Builder.create(MoonCowEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<MuleEntity> MULE = register(
		"mule", EntityType.Builder.create(MuleEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.6F).maxTrackingRange(8)
	);
	public static final EntityType<OcelotEntity> OCELOT = register(
		"ocelot", EntityType.Builder.create(OcelotEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F, 0.7F).maxTrackingRange(10)
	);
	public static final EntityType<PaintingEntity> PAINTING = register(
		"painting",
		EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, SpawnGroup.MISC)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<PandaEntity> PANDA = register(
		"panda", EntityType.Builder.create(PandaEntity::new, SpawnGroup.CREATURE).setDimensions(1.3F, 1.25F).maxTrackingRange(10)
	);
	public static final EntityType<ParrotEntity> PARROT = register(
		"parrot", EntityType.Builder.create(ParrotEntity::new, SpawnGroup.CREATURE).setDimensions(0.5F, 0.9F).maxTrackingRange(8)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register(
		"phantom", EntityType.Builder.create(PhantomEntity::new, SpawnGroup.MONSTER).setDimensions(0.9F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<PigEntity> PIG = register(
		"pig", EntityType.Builder.create(PigEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 0.9F).maxTrackingRange(10)
	);
	public static final EntityType<PiglinEntity> PIGLIN = register(
		"piglin", EntityType.Builder.create(PiglinEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PiglinBruteEntity> PIGLIN_BRUTE = register(
		"piglin_brute", EntityType.Builder.create(PiglinBruteEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PillagerEntity> PILLAGER = register(
		"pillager", EntityType.Builder.create(PillagerEntity::new, SpawnGroup.MONSTER).spawnableFarFromPlayer().setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register(
		"polar_bear",
		EntityType.Builder.create(PolarBearEntity::new, SpawnGroup.CREATURE).allowSpawningInside(Blocks.POWDER_SNOW).setDimensions(1.4F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<PotionEntity> POTION = register(
		"potion",
		EntityType.Builder.<PotionEntity>create(PotionEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<PufferfishEntity> PUFFERFISH = register(
		"pufferfish", EntityType.Builder.create(PufferfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7F, 0.7F).maxTrackingRange(4)
	);
	public static final EntityType<RabbitEntity> RABBIT = register(
		"rabbit", EntityType.Builder.create(RabbitEntity::new, SpawnGroup.CREATURE).setDimensions(0.4F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<RavagerEntity> RAVAGER = register(
		"ravager", EntityType.Builder.create(RavagerEntity::new, SpawnGroup.MONSTER).setDimensions(1.95F, 2.2F).maxTrackingRange(10)
	);
	public static final EntityType<SalmonEntity> SALMON = register(
		"salmon", EntityType.Builder.create(SalmonEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7F, 0.4F).maxTrackingRange(4)
	);
	public static final EntityType<SheepEntity> SHEEP = register(
		"sheep", EntityType.Builder.create(SheepEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.3F).maxTrackingRange(10)
	);
	public static final EntityType<ShulkerEntity> SHULKER = register(
		"shulker",
		EntityType.Builder.create(ShulkerEntity::new, SpawnGroup.MONSTER).makeFireImmune().spawnableFarFromPlayer().setDimensions(1.0F, 1.0F).maxTrackingRange(10)
	);
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet",
		EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, SpawnGroup.MISC).setDimensions(0.3125F, 0.3125F).maxTrackingRange(8)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register(
		"silverfish", EntityType.Builder.create(SilverfishEntity::new, SpawnGroup.MONSTER).setDimensions(0.4F, 0.3F).maxTrackingRange(8)
	);
	public static final EntityType<SkeletonEntity> SKELETON = register(
		"skeleton", EntityType.Builder.create(SkeletonEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.99F).maxTrackingRange(8)
	);
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<SlimeEntity> SLIME = register(
		"slime", EntityType.Builder.create(SlimeEntity::new, SpawnGroup.MONSTER).setDimensions(2.04F, 2.04F).maxTrackingRange(10)
	);
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball",
		EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, SpawnGroup.MISC)
			.setDimensions(0.3125F, 0.3125F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<SnifferEntity> SNIFFER = register(
		"sniffer", EntityType.Builder.create(SnifferEntity::new, SpawnGroup.CREATURE).setDimensions(1.9F, 1.75F).maxTrackingRange(10)
	);
	public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register(
		"snow_golem",
		EntityType.Builder.create(SnowGolemEntity::new, SpawnGroup.MISC).allowSpawningInside(Blocks.POWDER_SNOW).setDimensions(0.7F, 1.9F).maxTrackingRange(8)
	);
	public static final EntityType<SnowballEntity> SNOWBALL = register(
		"snowball",
		EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart",
		EntityType.Builder.<SpawnerMinecartEntity>create(SpawnerMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow",
		EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, SpawnGroup.MISC)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(4)
			.trackingTickInterval(20)
	);
	public static final EntityType<SpiderEntity> SPIDER = register(
		"spider", EntityType.Builder.create(SpiderEntity::new, SpawnGroup.MONSTER).setDimensions(1.4F, 0.9F).maxTrackingRange(8)
	);
	public static final EntityType<SquidEntity> SQUID = register(
		"squid", EntityType.Builder.create(SquidEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.8F, 0.8F).maxTrackingRange(8)
	);
	public static final EntityType<DisplayEntity.StencilDisplayEntity> STENCIL_DISPLAY = register(
		"stencil_display",
		EntityType.Builder.create(DisplayEntity.StencilDisplayEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1)
	);
	public static final EntityType<StrayEntity> STRAY = register(
		"stray",
		EntityType.Builder.create(StrayEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.99F).allowSpawningInside(Blocks.POWDER_SNOW).maxTrackingRange(8)
	);
	public static final EntityType<StriderEntity> STRIDER = register(
		"strider", EntityType.Builder.create(StriderEntity::new, SpawnGroup.CREATURE).makeFireImmune().setDimensions(0.9F, 1.7F).maxTrackingRange(10)
	);
	public static final EntityType<TadpoleEntity> TADPOLE = register(
		"tadpole", EntityType.Builder.create(TadpoleEntity::new, SpawnGroup.CREATURE).setDimensions(TadpoleEntity.WIDTH, TadpoleEntity.HEIGHT).maxTrackingRange(10)
	);
	public static final EntityType<DisplayEntity.TextDisplayEntity> TEXT_DISPLAY = register(
		"text_display",
		EntityType.Builder.create(DisplayEntity.TextDisplayEntity::new, SpawnGroup.MISC).setDimensions(0.0F, 0.0F).maxTrackingRange(10).trackingTickInterval(1)
	);
	public static final EntityType<TntEntity> TNT = register(
		"tnt",
		EntityType.Builder.<TntEntity>create(TntEntity::new, SpawnGroup.MISC)
			.makeFireImmune()
			.setDimensions(0.98F, 0.98F)
			.maxTrackingRange(10)
			.trackingTickInterval(10)
	);
	public static final EntityType<TntMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.Builder.<TntMinecartEntity>create(TntMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register(
		"trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9F, 1.87F).maxTrackingRange(10)
	);
	public static final EntityType<TridentEntity> TRIDENT = register(
		"trident",
		EntityType.Builder.<TridentEntity>create(TridentEntity::new, SpawnGroup.MISC).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20)
	);
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5F, 0.4F).maxTrackingRange(4)
	);
	public static final EntityType<TurtleEntity> TURTLE = register(
		"turtle", EntityType.Builder.create(TurtleEntity::new, SpawnGroup.CREATURE).setDimensions(1.2F, 0.4F).maxTrackingRange(10)
	);
	public static final EntityType<VexEntity> VEX = register(
		"vex", EntityType.Builder.create(VexEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.4F, 0.8F).maxTrackingRange(8)
	);
	public static final EntityType<VillagerEntity> VILLAGER = register(
		"villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, SpawnGroup.MISC).setDimensions(0.6F, 1.95F).maxTrackingRange(10)
	);
	public static final EntityType<VindicatorEntity> VINDICATOR = register(
		"vindicator", EntityType.Builder.create(VindicatorEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register(
		"wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F, 1.95F).maxTrackingRange(10)
	);
	public static final EntityType<WardenEntity> WARDEN = register(
		"warden", EntityType.Builder.create(WardenEntity::new, SpawnGroup.MONSTER).setDimensions(0.9F, 2.9F).maxTrackingRange(16).makeFireImmune()
	);
	public static final EntityType<WitchEntity> WITCH = register(
		"witch", EntityType.Builder.create(WitchEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<WitherEntity> WITHER = register(
		"wither",
		EntityType.Builder.create(WitherEntity::new, SpawnGroup.MONSTER)
			.makeFireImmune()
			.allowSpawningInside(Blocks.WITHER_ROSE)
			.setDimensions(0.9F, 3.5F)
			.maxTrackingRange(10)
	);
	public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton",
		EntityType.Builder.create(WitherSkeletonEntity::new, SpawnGroup.MONSTER)
			.makeFireImmune()
			.allowSpawningInside(Blocks.WITHER_ROSE)
			.setDimensions(0.7F, 2.4F)
			.maxTrackingRange(8)
	);
	public static final EntityType<WitherSkullEntity> WITHER_SKULL = register(
		"wither_skull",
		EntityType.Builder.<WitherSkullEntity>create(WitherSkullEntity::new, SpawnGroup.MISC)
			.setDimensions(0.3125F, 0.3125F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<WolfEntity> WOLF = register(
		"wolf", EntityType.Builder.create(WolfEntity::new, SpawnGroup.CREATURE).setDimensions(0.6F, 0.85F).maxTrackingRange(10)
	);
	public static final EntityType<ZoglinEntity> ZOGLIN = register(
		"zoglin", EntityType.Builder.create(ZoglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(1.3964844F, 1.4F).maxTrackingRange(8)
	);
	public static final EntityType<ZombieEntity> ZOMBIE = register(
		"zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = register(
		"zombified_piglin", EntityType.Builder.create(ZombifiedPiglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<RayTracingEntity> RAY_TRACING = register(
		"ray_tracing",
		EntityType.Builder.create(RayTracingEntity::new, SpawnGroup.AMBIENT).disableSaving().disableSummon().setDimensions(0.6F, 1.8F).maxTrackingRange(8)
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player",
		EntityType.Builder.<PlayerEntity>create(SpawnGroup.MISC)
			.disableSaving()
			.disableSummon()
			.setDimensions(0.6F, 1.8F)
			.maxTrackingRange(32)
			.trackingTickInterval(2)
	);
	public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register(
		"fishing_bobber",
		EntityType.Builder.<FishingBobberEntity>create(FishingBobberEntity::new, SpawnGroup.MISC)
			.disableSaving()
			.disableSummon()
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(5)
	);
	private final EntityType.EntityFactory<T> factory;
	private final SpawnGroup spawnGroup;
	private final ImmutableSet<Block> canSpawnInside;
	private final boolean saveable;
	private final boolean summonable;
	private final boolean fireImmune;
	private final boolean spawnableFarFromPlayer;
	private final int maxTrackDistance;
	private final int trackTickInterval;
	@Nullable
	private String translationKey;
	@Nullable
	private Text name;
	@Nullable
	private Identifier lootTableId;
	private final EntityDimensions dimensions;
	private final FeatureSet requiredFeatures;

	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
		return Registry.register(Registries.ENTITY_TYPE, id, type.build(id));
	}

	public static Identifier getId(EntityType<?> type) {
		return Registries.ENTITY_TYPE.getId(type);
	}

	public static Optional<EntityType<?>> get(String id) {
		return Registries.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(id));
	}

	public EntityType(
		EntityType.EntityFactory<T> factory,
		SpawnGroup spawnGroup,
		boolean saveable,
		boolean summonable,
		boolean fireImmune,
		boolean spawnableFarFromPlayer,
		ImmutableSet<Block> canSpawnInside,
		EntityDimensions dimensions,
		int maxTrackDistance,
		int trackTickInterval,
		FeatureSet requiredFeatures
	) {
		this.factory = factory;
		this.spawnGroup = spawnGroup;
		this.spawnableFarFromPlayer = spawnableFarFromPlayer;
		this.saveable = saveable;
		this.summonable = summonable;
		this.fireImmune = fireImmune;
		this.canSpawnInside = canSpawnInside;
		this.dimensions = dimensions;
		this.maxTrackDistance = maxTrackDistance;
		this.trackTickInterval = trackTickInterval;
		this.requiredFeatures = requiredFeatures;
	}

	@Nullable
	public T spawnFromItemStack(
		ServerWorld world, @Nullable ItemStack stack, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY
	) {
		Consumer<T> consumer;
		NbtCompound nbtCompound;
		if (stack != null) {
			nbtCompound = stack.getNbt();
			consumer = copier(world, stack, player);
		} else {
			consumer = entity -> {
			};
			nbtCompound = null;
		}

		return this.spawn(world, nbtCompound, consumer, pos, spawnReason, alignPosition, invertY);
	}

	public static <T extends Entity> Consumer<T> copier(ServerWorld world, ItemStack stack, @Nullable PlayerEntity player) {
		return copier(entity -> {
		}, world, stack, player);
	}

	public static <T extends Entity> Consumer<T> copier(Consumer<T> chained, ServerWorld world, ItemStack stack, @Nullable PlayerEntity player) {
		return nbtCopier(customNameCopier(chained, stack), world, stack, player);
	}

	public static <T extends Entity> Consumer<T> customNameCopier(Consumer<T> chained, ItemStack stack) {
		return stack.hasCustomName() ? chained.andThen(entity -> entity.setCustomName(stack.getName())) : chained;
	}

	public static <T extends Entity> Consumer<T> nbtCopier(Consumer<T> chained, ServerWorld world, ItemStack stack, @Nullable PlayerEntity player) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null ? chained.andThen(entity -> loadFromEntityNbt(world, player, entity, nbtCompound)) : chained;
	}

	@Nullable
	public T spawn(ServerWorld world, BlockPos pos, SpawnReason reason) {
		return this.spawn(world, (NbtCompound)null, null, pos, reason, false, false);
	}

	@Nullable
	public T spawn(
		ServerWorld world,
		@Nullable NbtCompound itemNbt,
		@Nullable Consumer<T> afterConsumer,
		BlockPos pos,
		SpawnReason reason,
		boolean alignPosition,
		boolean invertY
	) {
		T entity = this.create(world, itemNbt, afterConsumer, pos, reason, alignPosition, invertY);
		if (entity != null) {
			world.spawnEntityAndPassengers(entity);
		}

		return entity;
	}

	@Nullable
	public T create(
		ServerWorld world,
		@Nullable NbtCompound itemNbt,
		@Nullable Consumer<T> afterConsumer,
		BlockPos pos,
		SpawnReason reason,
		boolean alignPosition,
		boolean invertY
	) {
		T entity = this.create(world);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (alignPosition) {
				entity.setPosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
				d = getOriginY(world, pos, invertY, entity.getBoundingBox());
			} else {
				d = 0.0;
			}

			entity.refreshPositionAndAngles(
				(double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F
			);
			if (entity instanceof MobEntity mobEntity) {
				mobEntity.headYaw = mobEntity.getYaw();
				mobEntity.bodyYaw = mobEntity.getYaw();
				mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), reason, null, itemNbt);
				mobEntity.playAmbientSound();
			}

			if (afterConsumer != null) {
				afterConsumer.accept(entity);
			}

			return entity;
		}
	}

	protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
		Box box = new Box(pos);
		if (invertY) {
			box = box.stretch(0.0, -1.0, 0.0);
		}

		Iterable<VoxelShape> iterable = world.getCollisions(null, box);
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, iterable, invertY ? -2.0 : -1.0);
	}

	public static void loadFromEntityNbt(World world, @Nullable PlayerEntity player, @Nullable Entity entity, @Nullable NbtCompound itemNbt) {
		if (itemNbt != null && itemNbt.contains("EntityTag", NbtElement.COMPOUND_TYPE)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient || !entity.entityDataRequiresOperator() || player != null && minecraftServer.getPlayerManager().isOperator(player.getGameProfile())) {
					NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
					UUID uUID = entity.getUuid();
					nbtCompound.copyFrom(itemNbt.getCompound("EntityTag"));
					entity.setUuid(uUID);
					entity.readNbt(nbtCompound);
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
		return this.spawnableFarFromPlayer;
	}

	public SpawnGroup getSpawnGroup() {
		return this.spawnGroup;
	}

	public String getTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("entity", Registries.ENTITY_TYPE.getId(this));
		}

		return this.translationKey;
	}

	public Text getName() {
		if (this.name == null) {
			this.name = Text.translatable(this.getTranslationKey());
		}

		return this.name;
	}

	public String toString() {
		return this.getTranslationKey();
	}

	public String getUntranslatedName() {
		int i = this.getTranslationKey().lastIndexOf(46);
		return i == -1 ? this.getTranslationKey() : this.getTranslationKey().substring(i + 1);
	}

	public Identifier getLootTableId() {
		if (this.lootTableId == null) {
			Identifier identifier = Registries.ENTITY_TYPE.getId(this);
			this.lootTableId = identifier.withPrefixedPath("entities/");
		}

		return this.lootTableId;
	}

	public float getWidth() {
		return this.dimensions.width;
	}

	public float getHeight() {
		return this.dimensions.height;
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return this.requiredFeatures;
	}

	@Nullable
	public T create(World world) {
		return !this.isEnabled(world.getEnabledFeatures()) ? null : this.factory.create(this, world);
	}

	public static Optional<Entity> getEntityFromNbt(NbtCompound nbt, World world) {
		return Util.ifPresentOrElse(
			fromNbt(nbt).map(entityType -> entityType.create(world)),
			entity -> entity.readNbt(nbt),
			() -> LOGGER.warn("Skipping Entity with id {}", nbt.getString("id"))
		);
	}

	public Box createSimpleBoundingBox(double feetX, double feetY, double feetZ) {
		float f = this.getWidth() / 2.0F;
		return new Box(feetX - (double)f, feetY, feetZ - (double)f, feetX + (double)f, feetY + (double)this.getHeight(), feetZ + (double)f);
	}

	/**
	 * Returns whether the EntityType can spawn inside the given block.
	 * 
	 * <p>By default, non-fire-immune mobs can't spawn in/on blocks dealing fire damage.
	 * Any mob can't spawn in wither roses, sweet berry bush, or cacti.
	 * 
	 * <p>This can be overwritten via {@link EntityType.Builder#allowSpawningInside(Block[])}
	 */
	public boolean isInvalidSpawn(BlockState state) {
		if (this.canSpawnInside.contains(state.getBlock())) {
			return false;
		} else {
			return !this.fireImmune && LandPathNodeMaker.inflictsFireDamage(state)
				? true
				: state.isOf(Blocks.WITHER_ROSE) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.POWDER_SNOW);
		}
	}

	public EntityDimensions getDimensions() {
		return this.dimensions;
	}

	public static Optional<EntityType<?>> fromNbt(NbtCompound nbt) {
		return Registries.ENTITY_TYPE.getOrEmpty(new Identifier(nbt.getString("id")));
	}

	@Nullable
	public static Entity loadEntityWithPassengers(NbtCompound nbt, World world, Function<Entity, Entity> entityProcessor) {
		return (Entity)loadEntityFromNbt(nbt, world).map(entityProcessor).map(entity -> {
			if (nbt.contains("Passengers", NbtElement.LIST_TYPE)) {
				NbtList nbtList = nbt.getList("Passengers", NbtElement.COMPOUND_TYPE);

				for (int i = 0; i < nbtList.size(); i++) {
					Entity entity2 = loadEntityWithPassengers(nbtList.getCompound(i), world, entityProcessor);
					if (entity2 != null) {
						entity2.startRiding(entity, true);
					}
				}
			}

			return entity;
		}).orElse(null);
	}

	public static Stream<Entity> streamFromNbt(List<? extends NbtElement> entityNbtList, World world) {
		final Spliterator<? extends NbtElement> spliterator = entityNbtList.spliterator();
		return StreamSupport.stream(new Spliterator<Entity>() {
			public boolean tryAdvance(Consumer<? super Entity> action) {
				return spliterator.tryAdvance(nbt -> EntityType.loadEntityWithPassengers((NbtCompound)nbt, world, entity -> {
						action.accept(entity);
						return entity;
					}));
			}

			public Spliterator<Entity> trySplit() {
				return null;
			}

			public long estimateSize() {
				return (long)entityNbtList.size();
			}

			public int characteristics() {
				return Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE;
			}
		}, false);
	}

	private static Optional<Entity> loadEntityFromNbt(NbtCompound nbt, World world) {
		try {
			return getEntityFromNbt(nbt, world);
		} catch (RuntimeException var3) {
			LOGGER.warn("Exception loading entity: ", (Throwable)var3);
			return Optional.empty();
		}
	}

	/**
	 * Returns the tracking distance, <b>in chunks</b>, of this type of entity
	 * for clients. This will be then modified by the server's tracking
	 * distance multiplier.
	 */
	public int getMaxTrackDistance() {
		return this.maxTrackDistance;
	}

	public int getTrackTickInterval() {
		return this.trackTickInterval;
	}

	public boolean alwaysUpdateVelocity() {
		return this != PLAYER
			&& this != LLAMA_SPIT
			&& this != WITHER
			&& this != BAT
			&& this != ITEM_FRAME
			&& this != GLOW_ITEM_FRAME
			&& this != LEASH_KNOT
			&& this != PAINTING
			&& this != END_CRYSTAL
			&& this != EVOKER_FANGS;
	}

	public boolean isIn(TagKey<EntityType<?>> tag) {
		return this.registryEntry.isIn(tag);
	}

	@Nullable
	public T downcast(Entity entity) {
		return (T)(entity.getType() == this ? entity : null);
	}

	@Override
	public Class<? extends Entity> getBaseClass() {
		return Entity.class;
	}

	@Deprecated
	public RegistryEntry.Reference<EntityType<?>> getRegistryEntry() {
		return this.registryEntry;
	}

	public static class Builder<T extends Entity> {
		private final EntityType.EntityFactory<T> factory;
		private final SpawnGroup spawnGroup;
		private ImmutableSet<Block> canSpawnInside = ImmutableSet.of();
		private boolean saveable = true;
		private boolean summonable = true;
		private boolean fireImmune;
		private boolean spawnableFarFromPlayer;
		private int maxTrackingRange = 5;
		private int trackingTickInterval = 3;
		private EntityDimensions dimensions = EntityDimensions.changing(0.6F, 1.8F);
		private FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;

		private Builder(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
			this.factory = factory;
			this.spawnGroup = spawnGroup;
			this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.CREATURE || spawnGroup == SpawnGroup.MISC;
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
			return new EntityType.Builder<>(factory, spawnGroup);
		}

		public static <T extends Entity> EntityType.Builder<T> create(SpawnGroup spawnGroup) {
			return new EntityType.Builder<>((type, world) -> null, spawnGroup);
		}

		public EntityType.Builder<T> setDimensions(float width, float height) {
			this.dimensions = EntityDimensions.changing(width, height);
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

		/**
		 * Allows this type of entity to spawn inside the given block, bypassing the default
		 * wither rose, sweet berry bush, cactus, and fire-damage-dealing blocks for
		 * non-fire-resistant mobs.
		 * 
		 * <p>{@code minecraft:prevent_mob_spawning_inside} tag overrides this.
		 * With this setting, fire resistant mobs can spawn on/in fire damage dealing blocks,
		 * and wither skeletons can spawn in wither roses. If a block added is not in the default
		 * blacklist, the addition has no effect.
		 */
		public EntityType.Builder<T> allowSpawningInside(Block... blocks) {
			this.canSpawnInside = ImmutableSet.copyOf(blocks);
			return this;
		}

		public EntityType.Builder<T> spawnableFarFromPlayer() {
			this.spawnableFarFromPlayer = true;
			return this;
		}

		public EntityType.Builder<T> maxTrackingRange(int maxTrackingRange) {
			this.maxTrackingRange = maxTrackingRange;
			return this;
		}

		public EntityType.Builder<T> trackingTickInterval(int trackingTickInterval) {
			this.trackingTickInterval = trackingTickInterval;
			return this;
		}

		public EntityType.Builder<T> requires(FeatureFlag... features) {
			this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
			return this;
		}

		public EntityType<T> build(String id) {
			if (this.saveable) {
				Util.getChoiceType(TypeReferences.ENTITY_TREE, id);
			}

			return new EntityType<>(
				this.factory,
				this.spawnGroup,
				this.saveable,
				this.summonable,
				this.fireImmune,
				this.spawnableFarFromPlayer,
				this.canSpawnInside,
				this.dimensions,
				this.maxTrackingRange,
				this.trackingTickInterval,
				this.requiredFeatures
			);
		}
	}

	public interface EntityFactory<T extends Entity> {
		T create(EntityType<T> type, World world);
	}
}
