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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tag.Tag;
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
	public static final EntityType<AreaEffectCloudEntity> field_6083 = register(
		"area_effect_cloud",
		EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, EntityCategory.field_17715).makeFireImmune().setSize(6.0F, 0.5F)
	);
	public static final EntityType<ArmorStandEntity> field_6131 = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, EntityCategory.field_17715).setSize(0.5F, 1.975F)
	);
	public static final EntityType<ArrowEntity> field_6122 = register(
		"arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<BatEntity> field_6108 = register(
		"bat", EntityType.Builder.create(BatEntity::new, EntityCategory.field_6303).setSize(0.5F, 0.9F)
	);
	public static final EntityType<BlazeEntity> field_6099 = register(
		"blaze", EntityType.Builder.create(BlazeEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(0.6F, 1.8F)
	);
	public static final EntityType<BoatEntity> field_6121 = register(
		"boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, EntityCategory.field_17715).setSize(1.375F, 0.5625F)
	);
	public static final EntityType<CatEntity> field_16281 = register(
		"cat", EntityType.Builder.create(CatEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.7F)
	);
	public static final EntityType<CaveSpiderEntity> field_6084 = register(
		"cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, EntityCategory.field_6302).setSize(0.7F, 0.5F)
	);
	public static final EntityType<ChickenEntity> field_6132 = register(
		"chicken", EntityType.Builder.create(ChickenEntity::new, EntityCategory.field_6294).setSize(0.4F, 0.7F)
	);
	public static final EntityType<CodEntity> field_6070 = register(
		"cod", EntityType.Builder.create(CodEntity::new, EntityCategory.field_6300).setSize(0.5F, 0.3F)
	);
	public static final EntityType<CowEntity> field_6085 = register(
		"cow", EntityType.Builder.create(CowEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.4F)
	);
	public static final EntityType<CreeperEntity> field_6046 = register(
		"creeper", EntityType.Builder.create(CreeperEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.7F)
	);
	public static final EntityType<DonkeyEntity> field_6067 = register(
		"donkey", EntityType.Builder.create(DonkeyEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.5F)
	);
	public static final EntityType<DolphinEntity> field_6087 = register(
		"dolphin", EntityType.Builder.create(DolphinEntity::new, EntityCategory.field_6300).setSize(0.9F, 0.6F)
	);
	public static final EntityType<DragonFireballEntity> field_6129 = register(
		"dragon_fireball", EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, EntityCategory.field_17715).setSize(1.0F, 1.0F)
	);
	public static final EntityType<DrownedEntity> field_6123 = register(
		"drowned", EntityType.Builder.create(DrownedEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ElderGuardianEntity> field_6086 = register(
		"elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, EntityCategory.field_6302).setSize(1.9975F, 1.9975F)
	);
	public static final EntityType<EnderCrystalEntity> field_6110 = register(
		"end_crystal", EntityType.Builder.<EnderCrystalEntity>create(EnderCrystalEntity::new, EntityCategory.field_17715).setSize(2.0F, 2.0F)
	);
	public static final EntityType<EnderDragonEntity> field_6116 = register(
		"ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(16.0F, 8.0F)
	);
	public static final EntityType<EndermanEntity> field_6091 = register(
		"enderman", EntityType.Builder.create(EndermanEntity::new, EntityCategory.field_6302).setSize(0.6F, 2.9F)
	);
	public static final EntityType<EndermiteEntity> field_6128 = register(
		"endermite", EntityType.Builder.create(EndermiteEntity::new, EntityCategory.field_6302).setSize(0.4F, 0.3F)
	);
	public static final EntityType<EvokerFangsEntity> field_6060 = register(
		"evoker_fangs", EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.8F)
	);
	public static final EntityType<EvokerEntity> field_6090 = register(
		"evoker", EntityType.Builder.create(EvokerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ExperienceOrbEntity> field_6044 = register(
		"experience_orb", EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<EnderEyeEntity> field_6061 = register(
		"eye_of_ender", EntityType.Builder.<EnderEyeEntity>create(EnderEyeEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<FallingBlockEntity> field_6089 = register(
		"falling_block", EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.98F)
	);
	public static final EntityType<FireworkEntity> field_6133 = register(
		"firework_rocket", EntityType.Builder.<FireworkEntity>create(FireworkEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<FoxEntity> field_17943 = register(
		"fox", EntityType.Builder.create(FoxEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.7F)
	);
	public static final EntityType<GhastEntity> field_6107 = register(
		"ghast", EntityType.Builder.create(GhastEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(4.0F, 4.0F)
	);
	public static final EntityType<GiantEntity> field_6095 = register(
		"giant", EntityType.Builder.create(GiantEntity::new, EntityCategory.field_6302).setSize(3.6F, 12.0F)
	);
	public static final EntityType<GuardianEntity> field_6118 = register(
		"guardian", EntityType.Builder.create(GuardianEntity::new, EntityCategory.field_6302).setSize(0.85F, 0.85F)
	);
	public static final EntityType<HorseEntity> field_6139 = register(
		"horse", EntityType.Builder.create(HorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<HuskEntity> field_6071 = register(
		"husk", EntityType.Builder.create(HuskEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<IllusionerEntity> field_6065 = register(
		"illusioner", EntityType.Builder.create(IllusionerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ItemEntity> field_6052 = register(
		"item", EntityType.Builder.<ItemEntity>create(ItemEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ItemFrameEntity> field_6043 = register(
		"item_frame", EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<FireballEntity> field_6066 = register(
		"fireball", EntityType.Builder.<FireballEntity>create(FireballEntity::new, EntityCategory.field_17715).setSize(1.0F, 1.0F)
	);
	public static final EntityType<LeadKnotEntity> field_6138 = register(
		"leash_knot", EntityType.Builder.<LeadKnotEntity>create(LeadKnotEntity::new, EntityCategory.field_17715).disableSaving().setSize(0.5F, 0.5F)
	);
	public static final EntityType<LlamaEntity> field_6074 = register(
		"llama", EntityType.Builder.create(LlamaEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.87F)
	);
	public static final EntityType<LlamaSpitEntity> field_6124 = register(
		"llama_spit", EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<MagmaCubeEntity> field_6102 = register(
		"magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(2.04F, 2.04F)
	);
	public static final EntityType<MinecartEntity> field_6096 = register(
		"minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<ChestMinecartEntity> field_6126 = register(
		"chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<CommandBlockMinecartEntity> field_6136 = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<FurnaceMinecartEntity> field_6080 = register(
		"furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<HopperMinecartEntity> field_6058 = register(
		"hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<MobSpawnerMinecartEntity> field_6142 = register(
		"spawner_minecart", EntityType.Builder.<MobSpawnerMinecartEntity>create(MobSpawnerMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<TNTMinecartEntity> field_6053 = register(
		"tnt_minecart", EntityType.Builder.<TNTMinecartEntity>create(TNTMinecartEntity::new, EntityCategory.field_17715).setSize(0.98F, 0.7F)
	);
	public static final EntityType<MuleEntity> field_6057 = register(
		"mule", EntityType.Builder.create(MuleEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<MooshroomEntity> field_6143 = register(
		"mooshroom", EntityType.Builder.create(MooshroomEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.4F)
	);
	public static final EntityType<OcelotEntity> field_6081 = register(
		"ocelot", EntityType.Builder.create(OcelotEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.7F)
	);
	public static final EntityType<PaintingEntity> field_6120 = register(
		"painting", EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<PandaEntity> field_6146 = register(
		"panda", EntityType.Builder.create(PandaEntity::new, EntityCategory.field_6294).setSize(1.3F, 1.25F)
	);
	public static final EntityType<ParrotEntity> field_6104 = register(
		"parrot", EntityType.Builder.create(ParrotEntity::new, EntityCategory.field_6294).setSize(0.5F, 0.9F)
	);
	public static final EntityType<PigEntity> field_6093 = register(
		"pig", EntityType.Builder.create(PigEntity::new, EntityCategory.field_6294).setSize(0.9F, 0.9F)
	);
	public static final EntityType<PufferfishEntity> field_6062 = register(
		"pufferfish", EntityType.Builder.create(PufferfishEntity::new, EntityCategory.field_6300).setSize(0.7F, 0.7F)
	);
	public static final EntityType<ZombiePigmanEntity> field_6050 = register(
		"zombie_pigman", EntityType.Builder.create(ZombiePigmanEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(0.6F, 1.95F)
	);
	public static final EntityType<PolarBearEntity> field_6042 = register(
		"polar_bear", EntityType.Builder.create(PolarBearEntity::new, EntityCategory.field_6294).setSize(1.4F, 1.4F)
	);
	public static final EntityType<PrimedTntEntity> field_6063 = register(
		"tnt", EntityType.Builder.<PrimedTntEntity>create(PrimedTntEntity::new, EntityCategory.field_17715).makeFireImmune().setSize(0.98F, 0.98F)
	);
	public static final EntityType<RabbitEntity> field_6140 = register(
		"rabbit", EntityType.Builder.create(RabbitEntity::new, EntityCategory.field_6294).setSize(0.4F, 0.5F)
	);
	public static final EntityType<SalmonEntity> field_6073 = register(
		"salmon", EntityType.Builder.create(SalmonEntity::new, EntityCategory.field_6300).setSize(0.7F, 0.4F)
	);
	public static final EntityType<SheepEntity> field_6115 = register(
		"sheep", EntityType.Builder.create(SheepEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.3F)
	);
	public static final EntityType<ShulkerEntity> field_6109 = register(
		"shulker", EntityType.Builder.create(ShulkerEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(1.0F, 1.0F)
	);
	public static final EntityType<ShulkerBulletEntity> field_6100 = register(
		"shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<SilverfishEntity> field_6125 = register(
		"silverfish", EntityType.Builder.create(SilverfishEntity::new, EntityCategory.field_6302).setSize(0.4F, 0.3F)
	);
	public static final EntityType<SkeletonEntity> field_6137 = register(
		"skeleton", EntityType.Builder.create(SkeletonEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.99F)
	);
	public static final EntityType<SkeletonHorseEntity> field_6075 = register(
		"skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<SlimeEntity> field_6069 = register(
		"slime", EntityType.Builder.create(SlimeEntity::new, EntityCategory.field_6302).setSize(2.04F, 2.04F)
	);
	public static final EntityType<SmallFireballEntity> field_6049 = register(
		"small_fireball", EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<SnowGolemEntity> field_6047 = register(
		"snow_golem", EntityType.Builder.create(SnowGolemEntity::new, EntityCategory.field_6294).setSize(0.7F, 1.9F)
	);
	public static final EntityType<SnowballEntity> field_6068 = register(
		"snowball", EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<SpectralArrowEntity> field_6135 = register(
		"spectral_arrow", EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<SpiderEntity> field_6079 = register(
		"spider", EntityType.Builder.create(SpiderEntity::new, EntityCategory.field_6302).setSize(1.4F, 0.9F)
	);
	public static final EntityType<SquidEntity> field_6114 = register(
		"squid", EntityType.Builder.create(SquidEntity::new, EntityCategory.field_6300).setSize(0.8F, 0.8F)
	);
	public static final EntityType<StrayEntity> field_6098 = register(
		"stray", EntityType.Builder.create(StrayEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.99F)
	);
	public static final EntityType<TraderLlamaEntity> field_17714 = register(
		"trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, EntityCategory.field_6294).setSize(0.9F, 1.87F)
	);
	public static final EntityType<TropicalFishEntity> field_6111 = register(
		"tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, EntityCategory.field_6300).setSize(0.5F, 0.4F)
	);
	public static final EntityType<TurtleEntity> field_6113 = register(
		"turtle", EntityType.Builder.create(TurtleEntity::new, EntityCategory.field_6294).setSize(1.2F, 0.4F)
	);
	public static final EntityType<ThrownEggEntity> field_6144 = register(
		"egg", EntityType.Builder.<ThrownEggEntity>create(ThrownEggEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownEnderpearlEntity> field_6082 = register(
		"ender_pearl", EntityType.Builder.<ThrownEnderpearlEntity>create(ThrownEnderpearlEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownExperienceBottleEntity> field_6064 = register(
		"experience_bottle",
		EntityType.Builder.<ThrownExperienceBottleEntity>create(ThrownExperienceBottleEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<ThrownPotionEntity> field_6045 = register(
		"potion", EntityType.Builder.<ThrownPotionEntity>create(ThrownPotionEntity::new, EntityCategory.field_17715).setSize(0.25F, 0.25F)
	);
	public static final EntityType<TridentEntity> field_6127 = register(
		"trident", EntityType.Builder.<TridentEntity>create(TridentEntity::new, EntityCategory.field_17715).setSize(0.5F, 0.5F)
	);
	public static final EntityType<VexEntity> field_6059 = register(
		"vex", EntityType.Builder.create(VexEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(0.4F, 0.8F)
	);
	public static final EntityType<VillagerEntity> field_6077 = register(
		"villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, EntityCategory.field_6294).setSize(0.6F, 1.95F)
	);
	public static final EntityType<IronGolemEntity> field_6147 = register(
		"iron_golem", EntityType.Builder.create(IronGolemEntity::new, EntityCategory.field_6294).setSize(1.4F, 2.7F)
	);
	public static final EntityType<VindicatorEntity> field_6117 = register(
		"vindicator", EntityType.Builder.create(VindicatorEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<PillagerEntity> field_6105 = register(
		"pillager", EntityType.Builder.create(PillagerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WanderingTraderEntity> field_17713 = register(
		"wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, EntityCategory.field_6294).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WitchEntity> field_6145 = register(
		"witch", EntityType.Builder.create(WitchEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<WitherEntity> field_6119 = register(
		"wither", EntityType.Builder.create(WitherEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(0.9F, 3.5F)
	);
	public static final EntityType<WitherSkeletonEntity> field_6076 = register(
		"wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity::new, EntityCategory.field_6302).makeFireImmune().setSize(0.7F, 2.4F)
	);
	public static final EntityType<ExplodingWitherSkullEntity> field_6130 = register(
		"wither_skull", EntityType.Builder.<ExplodingWitherSkullEntity>create(ExplodingWitherSkullEntity::new, EntityCategory.field_17715).setSize(0.3125F, 0.3125F)
	);
	public static final EntityType<WolfEntity> field_6055 = register(
		"wolf", EntityType.Builder.create(WolfEntity::new, EntityCategory.field_6294).setSize(0.6F, 0.85F)
	);
	public static final EntityType<ZombieEntity> field_6051 = register(
		"zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<ZombieHorseEntity> field_6048 = register(
		"zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, EntityCategory.field_6294).setSize(1.3964844F, 1.6F)
	);
	public static final EntityType<ZombieVillagerEntity> field_6054 = register(
		"zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, EntityCategory.field_6302).setSize(0.6F, 1.95F)
	);
	public static final EntityType<PhantomEntity> field_6078 = register(
		"phantom", EntityType.Builder.create(PhantomEntity::new, EntityCategory.field_6302).setSize(0.9F, 0.5F)
	);
	public static final EntityType<RavagerEntity> field_6134 = register(
		"ravager", EntityType.Builder.create(RavagerEntity::new, EntityCategory.field_6302).setSize(1.95F, 2.2F)
	);
	public static final EntityType<LightningEntity> field_6112 = register(
		"lightning_bolt", EntityType.Builder.<LightningEntity>create(EntityCategory.field_17715).disableSaving().setSize(0.0F, 0.0F)
	);
	public static final EntityType<PlayerEntity> field_6097 = register(
		"player", EntityType.Builder.<PlayerEntity>create(EntityCategory.field_17715).disableSaving().disableSummon().setSize(0.6F, 1.8F)
	);
	public static final EntityType<FishHookEntity> field_6103 = register(
		"fishing_bobber", EntityType.Builder.<FishHookEntity>create(EntityCategory.field_17715).disableSaving().disableSummon().setSize(0.25F, 0.25F)
	);
	private final EntityType.EntityFactory<T> factory;
	private final EntityCategory category;
	private final boolean saveable;
	private final boolean summonable;
	private final boolean fireImmune;
	@Nullable
	private String translationKey;
	@Nullable
	private Component textComponent;
	@Nullable
	private Identifier lootTableId;
	@Nullable
	private final Type<?> dataFixerType;
	private final EntitySize size;

	private static <T extends Entity> EntityType<T> register(String string, EntityType.Builder<T> builder) {
		return Registry.register(Registry.ENTITY_TYPE, string, builder.build(string));
	}

	public static Identifier getId(EntityType<?> entityType) {
		return Registry.ENTITY_TYPE.getId(entityType);
	}

	public static Optional<EntityType<?>> get(String string) {
		return Registry.ENTITY_TYPE.getOrEmpty(Identifier.create(string));
	}

	public EntityType(
		EntityType.EntityFactory<T> entityFactory, EntityCategory entityCategory, boolean bl, boolean bl2, boolean bl3, @Nullable Type<?> type, EntitySize entitySize
	) {
		this.factory = entityFactory;
		this.category = entityCategory;
		this.saveable = bl;
		this.summonable = bl2;
		this.fireImmune = bl3;
		this.dataFixerType = type;
		this.size = entitySize;
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
		@Nullable Component component,
		@Nullable PlayerEntity playerEntity,
		BlockPos blockPos,
		SpawnType spawnType,
		boolean bl,
		boolean bl2
	) {
		T entity = this.create(world, compoundTag, component, playerEntity, blockPos, spawnType, bl, bl2);
		world.spawnEntity(entity);
		return entity;
	}

	@Nullable
	public T create(
		World world,
		@Nullable CompoundTag compoundTag,
		@Nullable Component component,
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
				d = getOriginY(world, blockPos, bl2, entity.getBoundingBox());
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
				mobEntity.initialize(world, world.getLocalDifficulty(new BlockPos(mobEntity)), spawnType, null, compoundTag);
				mobEntity.playAmbientSound();
			}

			if (component != null && entity instanceof LivingEntity) {
				entity.setCustomName(component);
			}

			loadFromEntityTag(world, playerEntity, entity, compoundTag);
			return entity;
		}
	}

	protected static double getOriginY(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl, BoundingBox boundingBox) {
		BoundingBox boundingBox2 = new BoundingBox(blockPos);
		if (bl) {
			boundingBox2 = boundingBox2.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = viewableWorld.getCollisionShapes(null, boundingBox2, Collections.emptySet());
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox, stream, bl ? -2.0 : -1.0);
	}

	public static void loadFromEntityTag(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isClient
					|| !entity.entityDataRequiresOperator()
					|| playerEntity != null && minecraftServer.getPlayerManager().isOperator(playerEntity.getGameProfile())) {
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

	public Component getTextComponent() {
		if (this.textComponent == null) {
			this.textComponent = new TranslatableComponent(this.getTranslationKey());
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

	public float getWidth() {
		return this.size.width;
	}

	public float getHeight() {
		return this.size.height;
	}

	@Nullable
	public T create(World world) {
		return this.factory.create(this, world);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Entity createInstanceFromId(int i, World world) {
		return newInstance(world, Registry.ENTITY_TYPE.get(i));
	}

	public static Optional<Entity> getEntityFromTag(CompoundTag compoundTag, World world) {
		return SystemUtil.ifPresentOrElse(
			fromTag(compoundTag).map(entityType -> entityType.create(world)),
			entity -> entity.fromTag(compoundTag),
			() -> LOGGER.warn("Skipping Entity with id {}", compoundTag.getString("id"))
		);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	private static Entity newInstance(World world, @Nullable EntityType<?> entityType) {
		return entityType == null ? null : entityType.create(world);
	}

	public BoundingBox createSimpleBoundingBox(double d, double e, double f) {
		float g = this.getWidth() / 2.0F;
		return new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)this.getHeight(), f + (double)g);
	}

	public EntitySize getDefaultSize() {
		return this.size;
	}

	public static Optional<EntityType<?>> fromTag(CompoundTag compoundTag) {
		return Registry.ENTITY_TYPE.getOrEmpty(new Identifier(compoundTag.getString("id")));
	}

	@Nullable
	public static Entity loadEntityWithPassengers(CompoundTag compoundTag, World world, Function<Entity, Entity> function) {
		return (Entity)loadEntityFromTag(compoundTag, world).map(function).map(entity -> {
			if (compoundTag.containsKey("Passengers", 9)) {
				ListTag listTag = compoundTag.getList("Passengers", 10);

				for (int i = 0; i < listTag.size(); i++) {
					Entity entity2 = loadEntityWithPassengers(listTag.getCompoundTag(i), world, function);
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
		if (this == field_6097) {
			return 32;
		} else if (this == field_6110) {
			return 16;
		} else if (this == field_6116
			|| this == field_6063
			|| this == field_6089
			|| this == field_6043
			|| this == field_6138
			|| this == field_6120
			|| this == field_6131
			|| this == field_6044
			|| this == field_6083
			|| this == field_6060) {
			return 10;
		} else {
			return this != field_6103
					&& this != field_6122
					&& this != field_6135
					&& this != field_6127
					&& this != field_6049
					&& this != field_6129
					&& this != field_6066
					&& this != field_6130
					&& this != field_6068
					&& this != field_6124
					&& this != field_6082
					&& this != field_6061
					&& this != field_6144
					&& this != field_6045
					&& this != field_6064
					&& this != field_6133
					&& this != field_6052
				? 5
				: 4;
		}
	}

	public int getTrackTickInterval() {
		if (this == field_6097 || this == field_6060) {
			return 2;
		} else if (this == field_6061) {
			return 4;
		} else if (this == field_6103) {
			return 5;
		} else if (this == field_6049
			|| this == field_6129
			|| this == field_6066
			|| this == field_6130
			|| this == field_6068
			|| this == field_6124
			|| this == field_6082
			|| this == field_6144
			|| this == field_6045
			|| this == field_6064
			|| this == field_6133
			|| this == field_6063) {
			return 10;
		} else if (this == field_6122 || this == field_6135 || this == field_6127 || this == field_6052 || this == field_6089 || this == field_6044) {
			return 20;
		} else {
			return this != field_6043 && this != field_6138 && this != field_6120 && this != field_6083 && this != field_6110 ? 3 : Integer.MAX_VALUE;
		}
	}

	public boolean alwaysUpdateVelocity() {
		return this != field_6097
			&& this != field_6124
			&& this != field_6119
			&& this != field_6108
			&& this != field_6043
			&& this != field_6138
			&& this != field_6120
			&& this != field_6110
			&& this != field_6060;
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
		private EntitySize size = EntitySize.resizeable(0.6F, 1.8F);

		private Builder(EntityType.EntityFactory<T> entityFactory, EntityCategory entityCategory) {
			this.factory = entityFactory;
			this.category = entityCategory;
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityType.EntityFactory<T> entityFactory, EntityCategory entityCategory) {
			return new EntityType.Builder<>(entityFactory, entityCategory);
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityCategory entityCategory) {
			return new EntityType.Builder<>((entityType, world) -> null, entityCategory);
		}

		public EntityType.Builder<T> setSize(float f, float g) {
			this.size = EntitySize.resizeable(f, g);
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

			return new EntityType<>(this.factory, this.category, this.saveable, this.summonable, this.fireImmune, type, this.size);
		}
	}

	public interface EntityFactory<T extends Entity> {
		T create(EntityType<T> entityType, World world);
	}
}
