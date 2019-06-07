/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.TntEntity;
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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class EntityType<T extends Entity> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = EntityType.register("area_effect_cloud", Builder.create(AreaEffectCloudEntity::new, EntityCategory.MISC).makeFireImmune().setDimensions(6.0f, 0.5f));
    public static final EntityType<ArmorStandEntity> ARMOR_STAND = EntityType.register("armor_stand", Builder.create(ArmorStandEntity::new, EntityCategory.MISC).setDimensions(0.5f, 1.975f));
    public static final EntityType<ArrowEntity> ARROW = EntityType.register("arrow", Builder.create(ArrowEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<BatEntity> BAT = EntityType.register("bat", Builder.create(BatEntity::new, EntityCategory.AMBIENT).setDimensions(0.5f, 0.9f));
    public static final EntityType<BlazeEntity> BLAZE = EntityType.register("blaze", Builder.create(BlazeEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.6f, 1.8f));
    public static final EntityType<BoatEntity> BOAT = EntityType.register("boat", Builder.create(BoatEntity::new, EntityCategory.MISC).setDimensions(1.375f, 0.5625f));
    public static final EntityType<CatEntity> CAT = EntityType.register("cat", Builder.create(CatEntity::new, EntityCategory.CREATURE).setDimensions(0.6f, 0.7f));
    public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = EntityType.register("cave_spider", Builder.create(CaveSpiderEntity::new, EntityCategory.MONSTER).setDimensions(0.7f, 0.5f));
    public static final EntityType<ChickenEntity> CHICKEN = EntityType.register("chicken", Builder.create(ChickenEntity::new, EntityCategory.CREATURE).setDimensions(0.4f, 0.7f));
    public static final EntityType<CodEntity> COD = EntityType.register("cod", Builder.create(CodEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.5f, 0.3f));
    public static final EntityType<CowEntity> COW = EntityType.register("cow", Builder.create(CowEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 1.4f));
    public static final EntityType<CreeperEntity> CREEPER = EntityType.register("creeper", Builder.create(CreeperEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.7f));
    public static final EntityType<DonkeyEntity> DONKEY = EntityType.register("donkey", Builder.create(DonkeyEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844f, 1.5f));
    public static final EntityType<DolphinEntity> DOLPHIN = EntityType.register("dolphin", Builder.create(DolphinEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.9f, 0.6f));
    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = EntityType.register("dragon_fireball", Builder.create(DragonFireballEntity::new, EntityCategory.MISC).setDimensions(1.0f, 1.0f));
    public static final EntityType<DrownedEntity> DROWNED = EntityType.register("drowned", Builder.create(DrownedEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = EntityType.register("elder_guardian", Builder.create(ElderGuardianEntity::new, EntityCategory.MONSTER).setDimensions(1.9975f, 1.9975f));
    public static final EntityType<EnderCrystalEntity> END_CRYSTAL = EntityType.register("end_crystal", Builder.create(EnderCrystalEntity::new, EntityCategory.MISC).setDimensions(2.0f, 2.0f));
    public static final EntityType<EnderDragonEntity> ENDER_DRAGON = EntityType.register("ender_dragon", Builder.create(EnderDragonEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(16.0f, 8.0f));
    public static final EntityType<EndermanEntity> ENDERMAN = EntityType.register("enderman", Builder.create(EndermanEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 2.9f));
    public static final EntityType<EndermiteEntity> ENDERMITE = EntityType.register("endermite", Builder.create(EndermiteEntity::new, EntityCategory.MONSTER).setDimensions(0.4f, 0.3f));
    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = EntityType.register("evoker_fangs", Builder.create(EvokerFangsEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.8f));
    public static final EntityType<EvokerEntity> EVOKER = EntityType.register("evoker", Builder.create(EvokerEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = EntityType.register("experience_orb", Builder.create(ExperienceOrbEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<EnderEyeEntity> EYE_OF_ENDER = EntityType.register("eye_of_ender", Builder.create(EnderEyeEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = EntityType.register("falling_block", Builder.create(FallingBlockEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.98f));
    public static final EntityType<FireworkEntity> FIREWORK_ROCKET = EntityType.register("firework_rocket", Builder.create(FireworkEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<FoxEntity> FOX = EntityType.register("fox", Builder.create(FoxEntity::new, EntityCategory.CREATURE).setDimensions(0.6f, 0.7f));
    public static final EntityType<GhastEntity> GHAST = EntityType.register("ghast", Builder.create(GhastEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(4.0f, 4.0f));
    public static final EntityType<GiantEntity> GIANT = EntityType.register("giant", Builder.create(GiantEntity::new, EntityCategory.MONSTER).setDimensions(3.6f, 12.0f));
    public static final EntityType<GuardianEntity> GUARDIAN = EntityType.register("guardian", Builder.create(GuardianEntity::new, EntityCategory.MONSTER).setDimensions(0.85f, 0.85f));
    public static final EntityType<HorseEntity> HORSE = EntityType.register("horse", Builder.create(HorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844f, 1.6f));
    public static final EntityType<HuskEntity> HUSK = EntityType.register("husk", Builder.create(HuskEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<IllusionerEntity> ILLUSIONER = EntityType.register("illusioner", Builder.create(IllusionerEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<ItemEntity> ITEM = EntityType.register("item", Builder.create(ItemEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<ItemFrameEntity> ITEM_FRAME = EntityType.register("item_frame", Builder.create(ItemFrameEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<FireballEntity> FIREBALL = EntityType.register("fireball", Builder.create(FireballEntity::new, EntityCategory.MISC).setDimensions(1.0f, 1.0f));
    public static final EntityType<LeadKnotEntity> LEASH_KNOT = EntityType.register("leash_knot", Builder.create(LeadKnotEntity::new, EntityCategory.MISC).disableSaving().setDimensions(0.5f, 0.5f));
    public static final EntityType<LlamaEntity> LLAMA = EntityType.register("llama", Builder.create(LlamaEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 1.87f));
    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = EntityType.register("llama_spit", Builder.create(LlamaSpitEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = EntityType.register("magma_cube", Builder.create(MagmaCubeEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(2.04f, 2.04f));
    public static final EntityType<MinecartEntity> MINECART = EntityType.register("minecart", Builder.create(MinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<ChestMinecartEntity> CHEST_MINECART = EntityType.register("chest_minecart", Builder.create(ChestMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = EntityType.register("command_block_minecart", Builder.create(CommandBlockMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = EntityType.register("furnace_minecart", Builder.create(FurnaceMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = EntityType.register("hopper_minecart", Builder.create(HopperMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = EntityType.register("spawner_minecart", Builder.create(SpawnerMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<TntMinecartEntity> TNT_MINECART = EntityType.register("tnt_minecart", Builder.create(TntMinecartEntity::new, EntityCategory.MISC).setDimensions(0.98f, 0.7f));
    public static final EntityType<MuleEntity> MULE = EntityType.register("mule", Builder.create(MuleEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844f, 1.6f));
    public static final EntityType<MooshroomEntity> MOOSHROOM = EntityType.register("mooshroom", Builder.create(MooshroomEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 1.4f));
    public static final EntityType<OcelotEntity> OCELOT = EntityType.register("ocelot", Builder.create(OcelotEntity::new, EntityCategory.CREATURE).setDimensions(0.6f, 0.7f));
    public static final EntityType<PaintingEntity> PAINTING = EntityType.register("painting", Builder.create(PaintingEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<PandaEntity> PANDA = EntityType.register("panda", Builder.create(PandaEntity::new, EntityCategory.CREATURE).setDimensions(1.3f, 1.25f));
    public static final EntityType<ParrotEntity> PARROT = EntityType.register("parrot", Builder.create(ParrotEntity::new, EntityCategory.CREATURE).setDimensions(0.5f, 0.9f));
    public static final EntityType<PigEntity> PIG = EntityType.register("pig", Builder.create(PigEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 0.9f));
    public static final EntityType<PufferfishEntity> PUFFERFISH = EntityType.register("pufferfish", Builder.create(PufferfishEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.7f, 0.7f));
    public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN = EntityType.register("zombie_pigman", Builder.create(ZombiePigmanEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.6f, 1.95f));
    public static final EntityType<PolarBearEntity> POLAR_BEAR = EntityType.register("polar_bear", Builder.create(PolarBearEntity::new, EntityCategory.CREATURE).setDimensions(1.4f, 1.4f));
    public static final EntityType<TntEntity> TNT = EntityType.register("tnt", Builder.create(TntEntity::new, EntityCategory.MISC).makeFireImmune().setDimensions(0.98f, 0.98f));
    public static final EntityType<RabbitEntity> RABBIT = EntityType.register("rabbit", Builder.create(RabbitEntity::new, EntityCategory.CREATURE).setDimensions(0.4f, 0.5f));
    public static final EntityType<SalmonEntity> SALMON = EntityType.register("salmon", Builder.create(SalmonEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.7f, 0.4f));
    public static final EntityType<SheepEntity> SHEEP = EntityType.register("sheep", Builder.create(SheepEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 1.3f));
    public static final EntityType<ShulkerEntity> SHULKER = EntityType.register("shulker", Builder.create(ShulkerEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(1.0f, 1.0f));
    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = EntityType.register("shulker_bullet", Builder.create(ShulkerBulletEntity::new, EntityCategory.MISC).setDimensions(0.3125f, 0.3125f));
    public static final EntityType<SilverfishEntity> SILVERFISH = EntityType.register("silverfish", Builder.create(SilverfishEntity::new, EntityCategory.MONSTER).setDimensions(0.4f, 0.3f));
    public static final EntityType<SkeletonEntity> SKELETON = EntityType.register("skeleton", Builder.create(SkeletonEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.99f));
    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = EntityType.register("skeleton_horse", Builder.create(SkeletonHorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844f, 1.6f));
    public static final EntityType<SlimeEntity> SLIME = EntityType.register("slime", Builder.create(SlimeEntity::new, EntityCategory.MONSTER).setDimensions(2.04f, 2.04f));
    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = EntityType.register("small_fireball", Builder.create(SmallFireballEntity::new, EntityCategory.MISC).setDimensions(0.3125f, 0.3125f));
    public static final EntityType<SnowGolemEntity> SNOW_GOLEM = EntityType.register("snow_golem", Builder.create(SnowGolemEntity::new, EntityCategory.MISC).setDimensions(0.7f, 1.9f));
    public static final EntityType<SnowballEntity> SNOWBALL = EntityType.register("snowball", Builder.create(SnowballEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = EntityType.register("spectral_arrow", Builder.create(SpectralArrowEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<SpiderEntity> SPIDER = EntityType.register("spider", Builder.create(SpiderEntity::new, EntityCategory.MONSTER).setDimensions(1.4f, 0.9f));
    public static final EntityType<SquidEntity> SQUID = EntityType.register("squid", Builder.create(SquidEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.8f, 0.8f));
    public static final EntityType<StrayEntity> STRAY = EntityType.register("stray", Builder.create(StrayEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.99f));
    public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = EntityType.register("trader_llama", Builder.create(TraderLlamaEntity::new, EntityCategory.CREATURE).setDimensions(0.9f, 1.87f));
    public static final EntityType<TropicalFishEntity> TROPICAL_FISH = EntityType.register("tropical_fish", Builder.create(TropicalFishEntity::new, EntityCategory.WATER_CREATURE).setDimensions(0.5f, 0.4f));
    public static final EntityType<TurtleEntity> TURTLE = EntityType.register("turtle", Builder.create(TurtleEntity::new, EntityCategory.CREATURE).setDimensions(1.2f, 0.4f));
    public static final EntityType<ThrownEggEntity> EGG = EntityType.register("egg", Builder.create(ThrownEggEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL = EntityType.register("ender_pearl", Builder.create(ThrownEnderpearlEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE = EntityType.register("experience_bottle", Builder.create(ThrownExperienceBottleEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<ThrownPotionEntity> POTION = EntityType.register("potion", Builder.create(ThrownPotionEntity::new, EntityCategory.MISC).setDimensions(0.25f, 0.25f));
    public static final EntityType<TridentEntity> TRIDENT = EntityType.register("trident", Builder.create(TridentEntity::new, EntityCategory.MISC).setDimensions(0.5f, 0.5f));
    public static final EntityType<VexEntity> VEX = EntityType.register("vex", Builder.create(VexEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.4f, 0.8f));
    public static final EntityType<VillagerEntity> VILLAGER = EntityType.register("villager", Builder.create(VillagerEntity::new, EntityCategory.MISC).setDimensions(0.6f, 1.95f));
    public static final EntityType<IronGolemEntity> IRON_GOLEM = EntityType.register("iron_golem", Builder.create(IronGolemEntity::new, EntityCategory.MISC).setDimensions(1.4f, 2.7f));
    public static final EntityType<VindicatorEntity> VINDICATOR = EntityType.register("vindicator", Builder.create(VindicatorEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<PillagerEntity> PILLAGER = EntityType.register("pillager", Builder.create(PillagerEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = EntityType.register("wandering_trader", Builder.create(WanderingTraderEntity::new, EntityCategory.CREATURE).setDimensions(0.6f, 1.95f));
    public static final EntityType<WitchEntity> WITCH = EntityType.register("witch", Builder.create(WitchEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<WitherEntity> WITHER = EntityType.register("wither", Builder.create(WitherEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.9f, 3.5f));
    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = EntityType.register("wither_skeleton", Builder.create(WitherSkeletonEntity::new, EntityCategory.MONSTER).makeFireImmune().setDimensions(0.7f, 2.4f));
    public static final EntityType<WitherSkullEntity> WITHER_SKULL = EntityType.register("wither_skull", Builder.create(WitherSkullEntity::new, EntityCategory.MISC).setDimensions(0.3125f, 0.3125f));
    public static final EntityType<WolfEntity> WOLF = EntityType.register("wolf", Builder.create(WolfEntity::new, EntityCategory.CREATURE).setDimensions(0.6f, 0.85f));
    public static final EntityType<ZombieEntity> ZOMBIE = EntityType.register("zombie", Builder.create(ZombieEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = EntityType.register("zombie_horse", Builder.create(ZombieHorseEntity::new, EntityCategory.CREATURE).setDimensions(1.3964844f, 1.6f));
    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = EntityType.register("zombie_villager", Builder.create(ZombieVillagerEntity::new, EntityCategory.MONSTER).setDimensions(0.6f, 1.95f));
    public static final EntityType<PhantomEntity> PHANTOM = EntityType.register("phantom", Builder.create(PhantomEntity::new, EntityCategory.MONSTER).setDimensions(0.9f, 0.5f));
    public static final EntityType<RavagerEntity> RAVAGER = EntityType.register("ravager", Builder.create(RavagerEntity::new, EntityCategory.MONSTER).setDimensions(1.95f, 2.2f));
    public static final EntityType<LightningEntity> LIGHTNING_BOLT = EntityType.register("lightning_bolt", Builder.create(EntityCategory.MISC).disableSaving().setDimensions(0.0f, 0.0f));
    public static final EntityType<PlayerEntity> PLAYER = EntityType.register("player", Builder.create(EntityCategory.MISC).disableSaving().disableSummon().setDimensions(0.6f, 1.8f));
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER = EntityType.register("fishing_bobber", Builder.create(EntityCategory.MISC).disableSaving().disableSummon().setDimensions(0.25f, 0.25f));
    private final EntityFactory<T> factory;
    private final EntityCategory category;
    private final boolean saveable;
    private final boolean summonable;
    private final boolean fireImmune;
    @Nullable
    private String translationKey;
    @Nullable
    private Text name;
    @Nullable
    private Identifier lootTableId;
    @Nullable
    private final Type<?> dataFixerType;
    private final EntityDimensions dimensions;

    private static <T extends Entity> EntityType<T> register(String string, Builder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, string, builder.build(string));
    }

    public static Identifier getId(EntityType<?> entityType) {
        return Registry.ENTITY_TYPE.getId(entityType);
    }

    public static Optional<EntityType<?>> get(String string) {
        return Registry.ENTITY_TYPE.getOrEmpty(Identifier.tryParse(string));
    }

    public EntityType(EntityFactory<T> entityFactory, EntityCategory entityCategory, boolean bl, boolean bl2, boolean bl3, @Nullable Type<?> type, EntityDimensions entityDimensions) {
        this.factory = entityFactory;
        this.category = entityCategory;
        this.saveable = bl;
        this.summonable = bl2;
        this.fireImmune = bl3;
        this.dataFixerType = type;
        this.dimensions = entityDimensions;
    }

    @Nullable
    public Entity spawnFromItemStack(World world, @Nullable ItemStack itemStack, @Nullable PlayerEntity playerEntity, BlockPos blockPos, SpawnType spawnType, boolean bl, boolean bl2) {
        return this.spawn(world, itemStack == null ? null : itemStack.getTag(), itemStack != null && itemStack.hasCustomName() ? itemStack.getName() : null, playerEntity, blockPos, spawnType, bl, bl2);
    }

    @Nullable
    public T spawn(World world, @Nullable CompoundTag compoundTag, @Nullable Text text, @Nullable PlayerEntity playerEntity, BlockPos blockPos, SpawnType spawnType, boolean bl, boolean bl2) {
        T entity = this.create(world, compoundTag, text, playerEntity, blockPos, spawnType, bl, bl2);
        world.spawnEntity((Entity)entity);
        return entity;
    }

    @Nullable
    public T create(World world, @Nullable CompoundTag compoundTag, @Nullable Text text, @Nullable PlayerEntity playerEntity, BlockPos blockPos, SpawnType spawnType, boolean bl, boolean bl2) {
        double d;
        T entity = this.create(world);
        if (entity == null) {
            return null;
        }
        if (bl) {
            ((Entity)entity).setPosition((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5);
            d = EntityType.getOriginY(world, blockPos, bl2, ((Entity)entity).getBoundingBox());
        } else {
            d = 0.0;
        }
        ((Entity)entity).setPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5, MathHelper.wrapDegrees(world.random.nextFloat() * 360.0f), 0.0f);
        if (entity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)entity;
            mobEntity.headYaw = mobEntity.yaw;
            mobEntity.field_6283 = mobEntity.yaw;
            mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), spawnType, null, compoundTag);
            mobEntity.playAmbientSound();
        }
        if (text != null && entity instanceof LivingEntity) {
            ((Entity)entity).setCustomName(text);
        }
        EntityType.loadFromEntityTag(world, playerEntity, entity, compoundTag);
        return entity;
    }

    protected static double getOriginY(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl, Box box) {
        Box box2 = new Box(blockPos);
        if (bl) {
            box2 = box2.stretch(0.0, -1.0, 0.0);
        }
        Stream<VoxelShape> stream = viewableWorld.getCollisionShapes(null, box2, Collections.emptySet());
        return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, stream, bl ? -2.0 : -1.0);
    }

    public static void loadFromEntityTag(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
        if (compoundTag == null || !compoundTag.containsKey("EntityTag", 10)) {
            return;
        }
        MinecraftServer minecraftServer = world.getServer();
        if (minecraftServer == null || entity == null) {
            return;
        }
        if (!(world.isClient || !entity.entityDataRequiresOperator() || playerEntity != null && minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile()))) {
            return;
        }
        CompoundTag compoundTag2 = entity.toTag(new CompoundTag());
        UUID uUID = entity.getUuid();
        compoundTag2.copyFrom(compoundTag.getCompound("EntityTag"));
        entity.setUuid(uUID);
        entity.fromTag(compoundTag2);
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

    public EntityCategory getCategory() {
        return this.category;
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("entity", Registry.ENTITY_TYPE.getId(this));
        }
        return this.translationKey;
    }

    public Text getName() {
        if (this.name == null) {
            this.name = new TranslatableText(this.getTranslationKey(), new Object[0]);
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
    @Environment(value=EnvType.CLIENT)
    public static Entity createInstanceFromId(int i, World world) {
        return EntityType.newInstance(world, Registry.ENTITY_TYPE.get(i));
    }

    public static Optional<Entity> getEntityFromTag(CompoundTag compoundTag, World world) {
        return SystemUtil.ifPresentOrElse(EntityType.fromTag(compoundTag).map(entityType -> entityType.create(world)), entity -> entity.fromTag(compoundTag), () -> LOGGER.warn("Skipping Entity with id {}", (Object)compoundTag.getString("id")));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private static Entity newInstance(World world, @Nullable EntityType<?> entityType) {
        return entityType == null ? null : (Entity)entityType.create(world);
    }

    public Box createSimpleBoundingBox(double d, double e, double f) {
        float g = this.getWidth() / 2.0f;
        return new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.getHeight(), f + (double)g);
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> fromTag(CompoundTag compoundTag) {
        return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(compoundTag.getString("id")));
    }

    @Nullable
    public static Entity loadEntityWithPassengers(CompoundTag compoundTag, World world, Function<Entity, Entity> function) {
        return EntityType.loadEntityFromTag(compoundTag, world).map(function).map(entity -> {
            if (compoundTag.containsKey("Passengers", 9)) {
                ListTag listTag = compoundTag.getList("Passengers", 10);
                for (int i = 0; i < listTag.size(); ++i) {
                    Entity entity2 = EntityType.loadEntityWithPassengers(listTag.getCompoundTag(i), world, function);
                    if (entity2 == null) continue;
                    entity2.startRiding((Entity)entity, true);
                }
            }
            return entity;
        }).orElse(null);
    }

    private static Optional<Entity> loadEntityFromTag(CompoundTag compoundTag, World world) {
        try {
            return EntityType.getEntityFromTag(compoundTag, world);
        } catch (RuntimeException runtimeException) {
            LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException);
            return Optional.empty();
        }
    }

    public int getMaxTrackDistance() {
        if (this == PLAYER) {
            return 32;
        }
        if (this == END_CRYSTAL) {
            return 16;
        }
        if (this == ENDER_DRAGON || this == TNT || this == FALLING_BLOCK || this == ITEM_FRAME || this == LEASH_KNOT || this == PAINTING || this == ARMOR_STAND || this == EXPERIENCE_ORB || this == AREA_EFFECT_CLOUD || this == EVOKER_FANGS) {
            return 10;
        }
        if (this == FISHING_BOBBER || this == ARROW || this == SPECTRAL_ARROW || this == TRIDENT || this == SMALL_FIREBALL || this == DRAGON_FIREBALL || this == FIREBALL || this == WITHER_SKULL || this == SNOWBALL || this == LLAMA_SPIT || this == ENDER_PEARL || this == EYE_OF_ENDER || this == EGG || this == POTION || this == EXPERIENCE_BOTTLE || this == FIREWORK_ROCKET || this == ITEM) {
            return 4;
        }
        return 5;
    }

    public int getTrackTickInterval() {
        if (this == PLAYER || this == EVOKER_FANGS) {
            return 2;
        }
        if (this == EYE_OF_ENDER) {
            return 4;
        }
        if (this == FISHING_BOBBER) {
            return 5;
        }
        if (this == SMALL_FIREBALL || this == DRAGON_FIREBALL || this == FIREBALL || this == WITHER_SKULL || this == SNOWBALL || this == LLAMA_SPIT || this == ENDER_PEARL || this == EGG || this == POTION || this == EXPERIENCE_BOTTLE || this == FIREWORK_ROCKET || this == TNT) {
            return 10;
        }
        if (this == ARROW || this == SPECTRAL_ARROW || this == TRIDENT || this == ITEM || this == FALLING_BLOCK || this == EXPERIENCE_ORB) {
            return 20;
        }
        if (this == ITEM_FRAME || this == LEASH_KNOT || this == PAINTING || this == AREA_EFFECT_CLOUD || this == END_CRYSTAL) {
            return Integer.MAX_VALUE;
        }
        return 3;
    }

    public boolean alwaysUpdateVelocity() {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean isTaggedWith(Tag<EntityType<?>> tag) {
        return tag.contains(this);
    }

    public static interface EntityFactory<T extends Entity> {
        public T create(EntityType<T> var1, World var2);
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final EntityCategory category;
        private boolean saveable = true;
        private boolean summonable = true;
        private boolean fireImmune;
        private EntityDimensions size = EntityDimensions.changing(0.6f, 1.8f);

        private Builder(EntityFactory<T> entityFactory, EntityCategory entityCategory) {
            this.factory = entityFactory;
            this.category = entityCategory;
        }

        public static <T extends Entity> Builder<T> create(EntityFactory<T> entityFactory, EntityCategory entityCategory) {
            return new Builder<T>(entityFactory, entityCategory);
        }

        public static <T extends Entity> Builder<T> create(EntityCategory entityCategory) {
            return new Builder<Entity>((entityType, world) -> null, entityCategory);
        }

        public Builder<T> setDimensions(float f, float g) {
            this.size = EntityDimensions.changing(f, g);
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

        public EntityType<T> build(String string) {
            Type<?> type = null;
            if (this.saveable) {
                try {
                    type = Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TREE, string);
                } catch (IllegalStateException illegalStateException) {
                    if (SharedConstants.isDevelopment) {
                        throw illegalStateException;
                    }
                    LOGGER.warn("No data fixer registered for entity {}", (Object)string);
                }
            }
            return new EntityType<T>(this.factory, this.category, this.saveable, this.summonable, this.fireImmune, type, this.size);
        }
    }
}

