/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.class_5575;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
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
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AxolotlEntity;
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
import net.minecraft.entity.passive.StriderEntity;
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
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
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
import org.jetbrains.annotations.Nullable;

public class EntityType<T extends Entity>
implements class_5575<Entity, T> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = EntityType.register("area_effect_cloud", Builder.create(AreaEffectCloudEntity::new, SpawnGroup.MISC).makeFireImmune().setDimensions(6.0f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<ArmorStandEntity> ARMOR_STAND = EntityType.register("armor_stand", Builder.create(ArmorStandEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 1.975f).maxTrackingRange(10));
    public static final EntityType<ArrowEntity> ARROW = EntityType.register("arrow", Builder.create(ArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<AxolotlEntity> AXOLOTL = EntityType.register("axolotl", Builder.create(AxolotlEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.75f, 0.42f).maxTrackingRange(10));
    public static final EntityType<BatEntity> BAT = EntityType.register("bat", Builder.create(BatEntity::new, SpawnGroup.AMBIENT).setDimensions(0.5f, 0.9f).maxTrackingRange(5));
    public static final EntityType<BeeEntity> BEE = EntityType.register("bee", Builder.create(BeeEntity::new, SpawnGroup.CREATURE).setDimensions(0.7f, 0.6f).maxTrackingRange(8));
    public static final EntityType<BlazeEntity> BLAZE = EntityType.register("blaze", Builder.create(BlazeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6f, 1.8f).maxTrackingRange(8));
    public static final EntityType<BoatEntity> BOAT = EntityType.register("boat", Builder.create(BoatEntity::new, SpawnGroup.MISC).setDimensions(1.375f, 0.5625f).maxTrackingRange(10));
    public static final EntityType<CatEntity> CAT = EntityType.register("cat", Builder.create(CatEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(8));
    public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = EntityType.register("cave_spider", Builder.create(CaveSpiderEntity::new, SpawnGroup.MONSTER).setDimensions(0.7f, 0.5f).maxTrackingRange(8));
    public static final EntityType<ChickenEntity> CHICKEN = EntityType.register("chicken", Builder.create(ChickenEntity::new, SpawnGroup.CREATURE).setDimensions(0.4f, 0.7f).maxTrackingRange(10));
    public static final EntityType<CodEntity> COD = EntityType.register("cod", Builder.create(CodEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.3f).maxTrackingRange(4));
    public static final EntityType<CowEntity> COW = EntityType.register("cow", Builder.create(CowEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.4f).maxTrackingRange(10));
    public static final EntityType<CreeperEntity> CREEPER = EntityType.register("creeper", Builder.create(CreeperEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.7f).maxTrackingRange(8));
    public static final EntityType<DolphinEntity> DOLPHIN = EntityType.register("dolphin", Builder.create(DolphinEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.9f, 0.6f));
    public static final EntityType<DonkeyEntity> DONKEY = EntityType.register("donkey", Builder.create(DonkeyEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.5f).maxTrackingRange(10));
    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = EntityType.register("dragon_fireball", Builder.create(DragonFireballEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<DrownedEntity> DROWNED = EntityType.register("drowned", Builder.create(DrownedEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = EntityType.register("elder_guardian", Builder.create(ElderGuardianEntity::new, SpawnGroup.MONSTER).setDimensions(1.9975f, 1.9975f).maxTrackingRange(10));
    public static final EntityType<EndCrystalEntity> END_CRYSTAL = EntityType.register("end_crystal", Builder.create(EndCrystalEntity::new, SpawnGroup.MISC).setDimensions(2.0f, 2.0f).maxTrackingRange(16).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<EnderDragonEntity> ENDER_DRAGON = EntityType.register("ender_dragon", Builder.create(EnderDragonEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(16.0f, 8.0f).maxTrackingRange(10));
    public static final EntityType<EndermanEntity> ENDERMAN = EntityType.register("enderman", Builder.create(EndermanEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 2.9f).maxTrackingRange(8));
    public static final EntityType<EndermiteEntity> ENDERMITE = EntityType.register("endermite", Builder.create(EndermiteEntity::new, SpawnGroup.MONSTER).setDimensions(0.4f, 0.3f).maxTrackingRange(8));
    public static final EntityType<EvokerEntity> EVOKER = EntityType.register("evoker", Builder.create(EvokerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = EntityType.register("evoker_fangs", Builder.create(EvokerFangsEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.8f).maxTrackingRange(6).trackingTickInterval(2));
    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = EntityType.register("experience_orb", Builder.create(ExperienceOrbEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(6).trackingTickInterval(20));
    public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = EntityType.register("eye_of_ender", Builder.create(EyeOfEnderEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(4));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = EntityType.register("falling_block", Builder.create(FallingBlockEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(20));
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = EntityType.register("firework_rocket", Builder.create(FireworkRocketEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<FoxEntity> FOX = EntityType.register("fox", Builder.create(FoxEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(8).allowSpawningInside(Blocks.SWEET_BERRY_BUSH));
    public static final EntityType<GhastEntity> GHAST = EntityType.register("ghast", Builder.create(GhastEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(4.0f, 4.0f).maxTrackingRange(10));
    public static final EntityType<GiantEntity> GIANT = EntityType.register("giant", Builder.create(GiantEntity::new, SpawnGroup.MONSTER).setDimensions(3.6f, 12.0f).maxTrackingRange(10));
    public static final EntityType<GuardianEntity> GUARDIAN = EntityType.register("guardian", Builder.create(GuardianEntity::new, SpawnGroup.MONSTER).setDimensions(0.85f, 0.85f).maxTrackingRange(8));
    public static final EntityType<HoglinEntity> HOGLIN = EntityType.register("hoglin", Builder.create(HoglinEntity::new, SpawnGroup.MONSTER).setDimensions(1.3964844f, 1.4f).maxTrackingRange(8));
    public static final EntityType<HorseEntity> HORSE = EntityType.register("horse", Builder.create(HorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<HuskEntity> HUSK = EntityType.register("husk", Builder.create(HuskEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<IllusionerEntity> ILLUSIONER = EntityType.register("illusioner", Builder.create(IllusionerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<IronGolemEntity> IRON_GOLEM = EntityType.register("iron_golem", Builder.create(IronGolemEntity::new, SpawnGroup.MISC).setDimensions(1.4f, 2.7f).maxTrackingRange(10));
    public static final EntityType<ItemEntity> ITEM = EntityType.register("item", Builder.create(ItemEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(6).trackingTickInterval(20));
    public static final EntityType<ItemFrameEntity> ITEM_FRAME = EntityType.register("item_frame", Builder.create(ItemFrameEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<FireballEntity> FIREBALL = EntityType.register("fireball", Builder.create(FireballEntity::new, SpawnGroup.MISC).setDimensions(1.0f, 1.0f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<LeashKnotEntity> LEASH_KNOT = EntityType.register("leash_knot", Builder.create(LeashKnotEntity::new, SpawnGroup.MISC).disableSaving().setDimensions(0.375f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<LightningEntity> LIGHTNING_BOLT = EntityType.register("lightning_bolt", Builder.create(LightningEntity::new, SpawnGroup.MISC).disableSaving().setDimensions(0.0f, 0.0f).maxTrackingRange(16).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<LlamaEntity> LLAMA = EntityType.register("llama", Builder.create(LlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.87f).maxTrackingRange(10));
    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = EntityType.register("llama_spit", Builder.create(LlamaSpitEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = EntityType.register("magma_cube", Builder.create(MagmaCubeEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(2.04f, 2.04f).maxTrackingRange(8));
    public static final EntityType<MinecartEntity> MINECART = EntityType.register("minecart", Builder.create(MinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<ChestMinecartEntity> CHEST_MINECART = EntityType.register("chest_minecart", Builder.create(ChestMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = EntityType.register("command_block_minecart", Builder.create(CommandBlockMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = EntityType.register("furnace_minecart", Builder.create(FurnaceMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = EntityType.register("hopper_minecart", Builder.create(HopperMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = EntityType.register("spawner_minecart", Builder.create(SpawnerMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<TntMinecartEntity> TNT_MINECART = EntityType.register("tnt_minecart", Builder.create(TntMinecartEntity::new, SpawnGroup.MISC).setDimensions(0.98f, 0.7f).maxTrackingRange(8));
    public static final EntityType<MuleEntity> MULE = EntityType.register("mule", Builder.create(MuleEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(8));
    public static final EntityType<MooshroomEntity> MOOSHROOM = EntityType.register("mooshroom", Builder.create(MooshroomEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.4f).maxTrackingRange(10));
    public static final EntityType<OcelotEntity> OCELOT = EntityType.register("ocelot", Builder.create(OcelotEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.7f).maxTrackingRange(10));
    public static final EntityType<PaintingEntity> PAINTING = EntityType.register("painting", Builder.create(PaintingEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(10).trackingTickInterval(Integer.MAX_VALUE));
    public static final EntityType<PandaEntity> PANDA = EntityType.register("panda", Builder.create(PandaEntity::new, SpawnGroup.CREATURE).setDimensions(1.3f, 1.25f).maxTrackingRange(10));
    public static final EntityType<ParrotEntity> PARROT = EntityType.register("parrot", Builder.create(ParrotEntity::new, SpawnGroup.CREATURE).setDimensions(0.5f, 0.9f).maxTrackingRange(8));
    public static final EntityType<PhantomEntity> PHANTOM = EntityType.register("phantom", Builder.create(PhantomEntity::new, SpawnGroup.MONSTER).setDimensions(0.9f, 0.5f).maxTrackingRange(8));
    public static final EntityType<PigEntity> PIG = EntityType.register("pig", Builder.create(PigEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 0.9f).maxTrackingRange(10));
    public static final EntityType<PiglinEntity> PIGLIN = EntityType.register("piglin", Builder.create(PiglinEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PiglinBruteEntity> PIGLIN_BRUTE = EntityType.register("piglin_brute", Builder.create(PiglinBruteEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PillagerEntity> PILLAGER = EntityType.register("pillager", Builder.create(PillagerEntity::new, SpawnGroup.MONSTER).spawnableFarFromPlayer().setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PolarBearEntity> POLAR_BEAR = EntityType.register("polar_bear", Builder.create(PolarBearEntity::new, SpawnGroup.CREATURE).setDimensions(1.4f, 1.4f).maxTrackingRange(10));
    public static final EntityType<TntEntity> TNT = EntityType.register("tnt", Builder.create(TntEntity::new, SpawnGroup.MISC).makeFireImmune().setDimensions(0.98f, 0.98f).maxTrackingRange(10).trackingTickInterval(10));
    public static final EntityType<PufferfishEntity> PUFFERFISH = EntityType.register("pufferfish", Builder.create(PufferfishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7f, 0.7f).maxTrackingRange(4));
    public static final EntityType<RabbitEntity> RABBIT = EntityType.register("rabbit", Builder.create(RabbitEntity::new, SpawnGroup.CREATURE).setDimensions(0.4f, 0.5f).maxTrackingRange(8));
    public static final EntityType<RavagerEntity> RAVAGER = EntityType.register("ravager", Builder.create(RavagerEntity::new, SpawnGroup.MONSTER).setDimensions(1.95f, 2.2f).maxTrackingRange(10));
    public static final EntityType<SalmonEntity> SALMON = EntityType.register("salmon", Builder.create(SalmonEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.7f, 0.4f).maxTrackingRange(4));
    public static final EntityType<SheepEntity> SHEEP = EntityType.register("sheep", Builder.create(SheepEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.3f).maxTrackingRange(10));
    public static final EntityType<ShulkerEntity> SHULKER = EntityType.register("shulker", Builder.create(ShulkerEntity::new, SpawnGroup.MONSTER).makeFireImmune().spawnableFarFromPlayer().setDimensions(1.0f, 1.0f).maxTrackingRange(10));
    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = EntityType.register("shulker_bullet", Builder.create(ShulkerBulletEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(8));
    public static final EntityType<SilverfishEntity> SILVERFISH = EntityType.register("silverfish", Builder.create(SilverfishEntity::new, SpawnGroup.MONSTER).setDimensions(0.4f, 0.3f).maxTrackingRange(8));
    public static final EntityType<SkeletonEntity> SKELETON = EntityType.register("skeleton", Builder.create(SkeletonEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.99f).maxTrackingRange(8));
    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = EntityType.register("skeleton_horse", Builder.create(SkeletonHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<SlimeEntity> SLIME = EntityType.register("slime", Builder.create(SlimeEntity::new, SpawnGroup.MONSTER).setDimensions(2.04f, 2.04f).maxTrackingRange(10));
    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = EntityType.register("small_fireball", Builder.create(SmallFireballEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<SnowGolemEntity> SNOW_GOLEM = EntityType.register("snow_golem", Builder.create(SnowGolemEntity::new, SpawnGroup.MISC).setDimensions(0.7f, 1.9f).maxTrackingRange(8));
    public static final EntityType<SnowballEntity> SNOWBALL = EntityType.register("snowball", Builder.create(SnowballEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = EntityType.register("spectral_arrow", Builder.create(SpectralArrowEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<SpiderEntity> SPIDER = EntityType.register("spider", Builder.create(SpiderEntity::new, SpawnGroup.MONSTER).setDimensions(1.4f, 0.9f).maxTrackingRange(8));
    public static final EntityType<SquidEntity> SQUID = EntityType.register("squid", Builder.create(SquidEntity::new, SpawnGroup.WATER_CREATURE).setDimensions(0.8f, 0.8f).maxTrackingRange(8));
    public static final EntityType<StrayEntity> STRAY = EntityType.register("stray", Builder.create(StrayEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.99f).maxTrackingRange(8));
    public static final EntityType<StriderEntity> STRIDER = EntityType.register("strider", Builder.create(StriderEntity::new, SpawnGroup.CREATURE).makeFireImmune().setDimensions(0.9f, 1.7f).maxTrackingRange(10));
    public static final EntityType<EggEntity> EGG = EntityType.register("egg", Builder.create(EggEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<EnderPearlEntity> ENDER_PEARL = EntityType.register("ender_pearl", Builder.create(EnderPearlEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = EntityType.register("experience_bottle", Builder.create(ExperienceBottleEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<PotionEntity> POTION = EntityType.register("potion", Builder.create(PotionEntity::new, SpawnGroup.MISC).setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<TridentEntity> TRIDENT = EntityType.register("trident", Builder.create(TridentEntity::new, SpawnGroup.MISC).setDimensions(0.5f, 0.5f).maxTrackingRange(4).trackingTickInterval(20));
    public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = EntityType.register("trader_llama", Builder.create(TraderLlamaEntity::new, SpawnGroup.CREATURE).setDimensions(0.9f, 1.87f).maxTrackingRange(10));
    public static final EntityType<TropicalFishEntity> TROPICAL_FISH = EntityType.register("tropical_fish", Builder.create(TropicalFishEntity::new, SpawnGroup.WATER_AMBIENT).setDimensions(0.5f, 0.4f).maxTrackingRange(4));
    public static final EntityType<TurtleEntity> TURTLE = EntityType.register("turtle", Builder.create(TurtleEntity::new, SpawnGroup.CREATURE).setDimensions(1.2f, 0.4f).maxTrackingRange(10));
    public static final EntityType<VexEntity> VEX = EntityType.register("vex", Builder.create(VexEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.4f, 0.8f).maxTrackingRange(8));
    public static final EntityType<VillagerEntity> VILLAGER = EntityType.register("villager", Builder.create(VillagerEntity::new, SpawnGroup.MISC).setDimensions(0.6f, 1.95f).maxTrackingRange(10));
    public static final EntityType<VindicatorEntity> VINDICATOR = EntityType.register("vindicator", Builder.create(VindicatorEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = EntityType.register("wandering_trader", Builder.create(WanderingTraderEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 1.95f).maxTrackingRange(10));
    public static final EntityType<WitchEntity> WITCH = EntityType.register("witch", Builder.create(WitchEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<WitherEntity> WITHER = EntityType.register("wither", Builder.create(WitherEntity::new, SpawnGroup.MONSTER).makeFireImmune().allowSpawningInside(Blocks.WITHER_ROSE).setDimensions(0.9f, 3.5f).maxTrackingRange(10));
    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = EntityType.register("wither_skeleton", Builder.create(WitherSkeletonEntity::new, SpawnGroup.MONSTER).makeFireImmune().allowSpawningInside(Blocks.WITHER_ROSE).setDimensions(0.7f, 2.4f).maxTrackingRange(8));
    public static final EntityType<WitherSkullEntity> WITHER_SKULL = EntityType.register("wither_skull", Builder.create(WitherSkullEntity::new, SpawnGroup.MISC).setDimensions(0.3125f, 0.3125f).maxTrackingRange(4).trackingTickInterval(10));
    public static final EntityType<WolfEntity> WOLF = EntityType.register("wolf", Builder.create(WolfEntity::new, SpawnGroup.CREATURE).setDimensions(0.6f, 0.85f).maxTrackingRange(10));
    public static final EntityType<ZoglinEntity> ZOGLIN = EntityType.register("zoglin", Builder.create(ZoglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(1.3964844f, 1.4f).maxTrackingRange(8));
    public static final EntityType<ZombieEntity> ZOMBIE = EntityType.register("zombie", Builder.create(ZombieEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = EntityType.register("zombie_horse", Builder.create(ZombieHorseEntity::new, SpawnGroup.CREATURE).setDimensions(1.3964844f, 1.6f).maxTrackingRange(10));
    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = EntityType.register("zombie_villager", Builder.create(ZombieVillagerEntity::new, SpawnGroup.MONSTER).setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = EntityType.register("zombified_piglin", Builder.create(ZombifiedPiglinEntity::new, SpawnGroup.MONSTER).makeFireImmune().setDimensions(0.6f, 1.95f).maxTrackingRange(8));
    public static final EntityType<PlayerEntity> PLAYER = EntityType.register("player", Builder.create(SpawnGroup.MISC).disableSaving().disableSummon().setDimensions(0.6f, 1.8f).maxTrackingRange(32).trackingTickInterval(2));
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER = EntityType.register("fishing_bobber", Builder.create(FishingBobberEntity::new, SpawnGroup.MISC).disableSaving().disableSummon().setDimensions(0.25f, 0.25f).maxTrackingRange(4).trackingTickInterval(5));
    private final EntityFactory<T> factory;
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

    private static <T extends Entity> EntityType<T> register(String id, Builder<T> type) {
        return Registry.register(Registry.ENTITY_TYPE, id, type.build(id));
    }

    public static Identifier getId(EntityType<?> type) {
        return Registry.ENTITY_TYPE.getId(type);
    }

    public static Optional<EntityType<?>> get(String id) {
        return Registry.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(id));
    }

    public EntityType(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> immutableSet, EntityDimensions entityDimensions, int i, int j) {
        this.factory = factory;
        this.spawnGroup = spawnGroup;
        this.spawnableFarFromPlayer = spawnableFarFromPlayer;
        this.saveable = saveable;
        this.summonable = summonable;
        this.fireImmune = fireImmune;
        this.canSpawnInside = immutableSet;
        this.dimensions = entityDimensions;
        this.maxTrackDistance = i;
        this.trackTickInterval = j;
    }

    @Nullable
    public Entity spawnFromItemStack(ServerWorld serverWorld, @Nullable ItemStack stack, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
        return this.spawn(serverWorld, stack == null ? null : stack.getTag(), stack != null && stack.hasCustomName() ? stack.getName() : null, player, pos, spawnReason, alignPosition, invertY);
    }

    @Nullable
    public T spawn(ServerWorld serverWorld, @Nullable CompoundTag itemTag, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
        T entity = this.create(serverWorld, itemTag, name, player, pos, spawnReason, alignPosition, invertY);
        if (entity != null) {
            serverWorld.spawnEntityAndPassengers((Entity)entity);
        }
        return entity;
    }

    @Nullable
    public T create(ServerWorld serverWorld, @Nullable CompoundTag itemTag, @Nullable Text name, @Nullable PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY) {
        double d;
        T entity = this.create(serverWorld);
        if (entity == null) {
            return null;
        }
        if (alignPosition) {
            ((Entity)entity).setPosition((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5);
            d = EntityType.getOriginY(serverWorld, pos, invertY, ((Entity)entity).getBoundingBox());
        } else {
            d = 0.0;
        }
        ((Entity)entity).refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5, MathHelper.wrapDegrees(serverWorld.random.nextFloat() * 360.0f), 0.0f);
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            mobEntity.headYaw = mobEntity.yaw;
            mobEntity.bodyYaw = mobEntity.yaw;
            mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(mobEntity.getBlockPos()), spawnReason, null, itemTag);
            mobEntity.playAmbientSound();
        }
        if (name != null && entity instanceof LivingEntity) {
            ((Entity)entity).setCustomName(name);
        }
        EntityType.loadFromEntityTag(serverWorld, player, entity, itemTag);
        return entity;
    }

    protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
        Box box = new Box(pos);
        if (invertY) {
            box = box.stretch(0.0, -1.0, 0.0);
        }
        Stream<VoxelShape> stream = world.getCollisions(null, box, entity -> true);
        return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream, invertY ? -2.0 : -1.0);
    }

    public static void loadFromEntityTag(World world, @Nullable PlayerEntity player, @Nullable Entity entity, @Nullable CompoundTag itemTag) {
        if (itemTag == null || !itemTag.contains("EntityTag", 10)) {
            return;
        }
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null || entity == null) {
            return;
        }
        if (!(world.isClient || !entity.entityDataRequiresOperator() || player != null && minecraftServer.getPlayerManager().isOperator(player.getGameProfile()))) {
            return;
        }
        CompoundTag compoundTag = entity.toTag(new CompoundTag());
        UUID uUID = entity.getUuid();
        compoundTag.copyFrom(itemTag.getCompound("EntityTag"));
        entity.setUuid(uUID);
        entity.fromTag(compoundTag);
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

    public String toString() {
        return this.getTranslationKey();
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
    @Environment(value=EnvType.CLIENT)
    public static Entity createInstanceFromId(int type, World world) {
        return EntityType.newInstance(world, Registry.ENTITY_TYPE.get(type));
    }

    public static Optional<Entity> getEntityFromTag(CompoundTag tag, World world) {
        return Util.ifPresentOrElse(EntityType.fromTag(tag).map(entityType -> entityType.create(world)), entity -> entity.fromTag(tag), () -> LOGGER.warn("Skipping Entity with id {}", (Object)tag.getString("id")));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private static Entity newInstance(World world, @Nullable EntityType<?> type) {
        return type == null ? null : (Entity)type.create(world);
    }

    public Box createSimpleBoundingBox(double feetX, double feetY, double feetZ) {
        float f = this.getWidth() / 2.0f;
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
        }
        if (!this.fireImmune && (state.isIn(BlockTags.FIRE) || state.isOf(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(state) || state.isOf(Blocks.LAVA))) {
            return true;
        }
        return state.isOf(Blocks.WITHER_ROSE) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.CACTUS);
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> fromTag(CompoundTag compoundTag) {
        return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(compoundTag.getString("id")));
    }

    @Nullable
    public static Entity loadEntityWithPassengers(CompoundTag compoundTag, World world, Function<Entity, Entity> entityProcessor) {
        return EntityType.loadEntityFromTag(compoundTag, world).map(entityProcessor).map(entity -> {
            if (compoundTag.contains("Passengers", 9)) {
                ListTag listTag = compoundTag.getList("Passengers", 10);
                for (int i = 0; i < listTag.size(); ++i) {
                    Entity entity2 = EntityType.loadEntityWithPassengers(listTag.getCompound(i), world, entityProcessor);
                    if (entity2 == null) continue;
                    entity2.startRiding((Entity)entity, true);
                }
            }
            return entity;
        }).orElse(null);
    }

    public static Stream<Entity> method_31489(final List<? extends Tag> list, final World world) {
        final Spliterator<? extends Tag> spliterator = list.spliterator();
        return StreamSupport.stream(new Spliterator<Entity>(){

            @Override
            public boolean tryAdvance(Consumer<? super Entity> consumer) {
                return spliterator.tryAdvance((? super T tag) -> EntityType.loadEntityWithPassengers((CompoundTag)tag, world, entity -> {
                    consumer.accept((Entity)entity);
                    return entity;
                }));
            }

            @Override
            public Spliterator<Entity> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return list.size();
            }

            @Override
            public int characteristics() {
                return 1297;
            }
        }, false);
    }

    private static Optional<Entity> loadEntityFromTag(CompoundTag compoundTag, World world) {
        try {
            return EntityType.getEntityFromTag(compoundTag, world);
        } catch (RuntimeException runtimeException) {
            LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException);
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
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean isIn(net.minecraft.tag.Tag<EntityType<?>> tag) {
        return tag.contains(this);
    }

    @Override
    @Nullable
    public T method_31796(Entity entity) {
        return (T)(entity.getType() == this ? entity : null);
    }

    @Override
    public Class<? extends Entity> method_31794() {
        return Entity.class;
    }

    public static interface EntityFactory<T extends Entity> {
        public T create(EntityType<T> var1, World var2);
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final SpawnGroup spawnGroup;
        private ImmutableSet<Block> canSpawnInside = ImmutableSet.of();
        private boolean saveable = true;
        private boolean summonable = true;
        private boolean fireImmune;
        private boolean spawnableFarFromPlayer;
        private int maxTrackingRange = 5;
        private int trackingTickInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.changing(0.6f, 1.8f);

        private Builder(EntityFactory<T> factory, SpawnGroup spawnGroup) {
            this.factory = factory;
            this.spawnGroup = spawnGroup;
            this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.CREATURE || spawnGroup == SpawnGroup.MISC;
        }

        public static <T extends Entity> Builder<T> create(EntityFactory<T> factory, SpawnGroup spawnGroup) {
            return new Builder<T>(factory, spawnGroup);
        }

        public static <T extends Entity> Builder<T> create(SpawnGroup spawnGroup) {
            return new Builder<Entity>((entityType, world) -> null, spawnGroup);
        }

        public Builder<T> setDimensions(float width, float height) {
            this.dimensions = EntityDimensions.changing(width, height);
            return this;
        }

        public Builder<T> disableSummon() {
            this.summonable = false;
            return this;
        }

        public Builder<T> disableSaving() {
            this.saveable = false;
            return this;
        }

        public Builder<T> makeFireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> allowSpawningInside(Block ... blocks) {
            this.canSpawnInside = ImmutableSet.copyOf(blocks);
            return this;
        }

        public Builder<T> spawnableFarFromPlayer() {
            this.spawnableFarFromPlayer = true;
            return this;
        }

        public Builder<T> maxTrackingRange(int maxTrackingRange) {
            this.maxTrackingRange = maxTrackingRange;
            return this;
        }

        public Builder<T> trackingTickInterval(int trackingTickInterval) {
            this.trackingTickInterval = trackingTickInterval;
            return this;
        }

        public EntityType<T> build(String id) {
            if (this.saveable) {
                Util.getChoiceType(TypeReferences.ENTITY_TREE, id);
            }
            return new EntityType<T>(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.canSpawnInside, this.dimensions, this.maxTrackingRange, this.trackingTickInterval);
        }
    }
}

