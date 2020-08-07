package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.datafixer.TypeReferences;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
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
	public static final EntityType<AreaEffectCloudEntity> field_6083 = register(
		"area_effect_cloud",
		EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, SpawnGroup.field_17715)
			.makeFireImmune()
			.setDimensions(6.0F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<ArmorStandEntity> field_6131 = register(
		"armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, SpawnGroup.field_17715).setDimensions(0.5F, 1.975F).maxTrackingRange(10)
	);
	public static final EntityType<ArrowEntity> field_6122 = register(
		"arrow",
		EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, SpawnGroup.field_17715).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20)
	);
	public static final EntityType<BatEntity> field_6108 = register(
		"bat", EntityType.Builder.create(BatEntity::new, SpawnGroup.field_6303).setDimensions(0.5F, 0.9F).maxTrackingRange(5)
	);
	public static final EntityType<BeeEntity> field_20346 = register(
		"bee", EntityType.Builder.create(BeeEntity::new, SpawnGroup.field_6294).setDimensions(0.7F, 0.6F).maxTrackingRange(8)
	);
	public static final EntityType<BlazeEntity> field_6099 = register(
		"blaze", EntityType.Builder.create(BlazeEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(0.6F, 1.8F).maxTrackingRange(8)
	);
	public static final EntityType<BoatEntity> field_6121 = register(
		"boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, SpawnGroup.field_17715).setDimensions(1.375F, 0.5625F).maxTrackingRange(10)
	);
	public static final EntityType<CatEntity> field_16281 = register(
		"cat", EntityType.Builder.create(CatEntity::new, SpawnGroup.field_6294).setDimensions(0.6F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<CaveSpiderEntity> field_6084 = register(
		"cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, SpawnGroup.field_6302).setDimensions(0.7F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<ChickenEntity> field_6132 = register(
		"chicken", EntityType.Builder.create(ChickenEntity::new, SpawnGroup.field_6294).setDimensions(0.4F, 0.7F).maxTrackingRange(10)
	);
	public static final EntityType<CodEntity> field_6070 = register(
		"cod", EntityType.Builder.create(CodEntity::new, SpawnGroup.field_24460).setDimensions(0.5F, 0.3F).maxTrackingRange(4)
	);
	public static final EntityType<CowEntity> field_6085 = register(
		"cow", EntityType.Builder.create(CowEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<CreeperEntity> field_6046 = register(
		"creeper", EntityType.Builder.create(CreeperEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.7F).maxTrackingRange(8)
	);
	public static final EntityType<DolphinEntity> field_6087 = register(
		"dolphin", EntityType.Builder.create(DolphinEntity::new, SpawnGroup.field_6300).setDimensions(0.9F, 0.6F)
	);
	public static final EntityType<DonkeyEntity> field_6067 = register(
		"donkey", EntityType.Builder.create(DonkeyEntity::new, SpawnGroup.field_6294).setDimensions(1.3964844F, 1.5F).maxTrackingRange(10)
	);
	public static final EntityType<DragonFireballEntity> field_6129 = register(
		"dragon_fireball",
		EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, SpawnGroup.field_17715)
			.setDimensions(1.0F, 1.0F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<DrownedEntity> field_6123 = register(
		"drowned", EntityType.Builder.create(DrownedEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<ElderGuardianEntity> field_6086 = register(
		"elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, SpawnGroup.field_6302).setDimensions(1.9975F, 1.9975F).maxTrackingRange(10)
	);
	public static final EntityType<EndCrystalEntity> field_6110 = register(
		"end_crystal",
		EntityType.Builder.<EndCrystalEntity>create(EndCrystalEntity::new, SpawnGroup.field_17715)
			.setDimensions(2.0F, 2.0F)
			.maxTrackingRange(16)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<EnderDragonEntity> field_6116 = register(
		"ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(16.0F, 8.0F).maxTrackingRange(10)
	);
	public static final EntityType<EndermanEntity> field_6091 = register(
		"enderman", EntityType.Builder.create(EndermanEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 2.9F).maxTrackingRange(8)
	);
	public static final EntityType<EndermiteEntity> field_6128 = register(
		"endermite", EntityType.Builder.create(EndermiteEntity::new, SpawnGroup.field_6302).setDimensions(0.4F, 0.3F).maxTrackingRange(8)
	);
	public static final EntityType<EvokerEntity> field_6090 = register(
		"evoker", EntityType.Builder.create(EvokerEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<EvokerFangsEntity> field_6060 = register(
		"evoker_fangs",
		EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.5F, 0.8F)
			.maxTrackingRange(6)
			.trackingTickInterval(2)
	);
	public static final EntityType<ExperienceOrbEntity> field_6044 = register(
		"experience_orb",
		EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(6)
			.trackingTickInterval(20)
	);
	public static final EntityType<EyeOfEnderEntity> field_6061 = register(
		"eye_of_ender",
		EntityType.Builder.<EyeOfEnderEntity>create(EyeOfEnderEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(4)
	);
	public static final EntityType<FallingBlockEntity> field_6089 = register(
		"falling_block",
		EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.98F, 0.98F)
			.maxTrackingRange(10)
			.trackingTickInterval(20)
	);
	public static final EntityType<FireworkRocketEntity> field_6133 = register(
		"firework_rocket",
		EntityType.Builder.<FireworkRocketEntity>create(FireworkRocketEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<FoxEntity> field_17943 = register(
		"fox", EntityType.Builder.create(FoxEntity::new, SpawnGroup.field_6294).setDimensions(0.6F, 0.7F).maxTrackingRange(8).allowSpawningInside(Blocks.field_16999)
	);
	public static final EntityType<GhastEntity> field_6107 = register(
		"ghast", EntityType.Builder.create(GhastEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(4.0F, 4.0F).maxTrackingRange(10)
	);
	public static final EntityType<GiantEntity> field_6095 = register(
		"giant", EntityType.Builder.create(GiantEntity::new, SpawnGroup.field_6302).setDimensions(3.6F, 12.0F).maxTrackingRange(10)
	);
	public static final EntityType<GuardianEntity> field_6118 = register(
		"guardian", EntityType.Builder.create(GuardianEntity::new, SpawnGroup.field_6302).setDimensions(0.85F, 0.85F).maxTrackingRange(8)
	);
	public static final EntityType<HoglinEntity> field_21973 = register(
		"hoglin", EntityType.Builder.create(HoglinEntity::new, SpawnGroup.field_6302).setDimensions(1.3964844F, 1.4F).maxTrackingRange(8)
	);
	public static final EntityType<HorseEntity> field_6139 = register(
		"horse", EntityType.Builder.create(HorseEntity::new, SpawnGroup.field_6294).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<HuskEntity> field_6071 = register(
		"husk", EntityType.Builder.create(HuskEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<IllusionerEntity> field_6065 = register(
		"illusioner", EntityType.Builder.create(IllusionerEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<IronGolemEntity> field_6147 = register(
		"iron_golem", EntityType.Builder.create(IronGolemEntity::new, SpawnGroup.field_17715).setDimensions(1.4F, 2.7F).maxTrackingRange(10)
	);
	public static final EntityType<ItemEntity> field_6052 = register(
		"item",
		EntityType.Builder.<ItemEntity>create(ItemEntity::new, SpawnGroup.field_17715).setDimensions(0.25F, 0.25F).maxTrackingRange(6).trackingTickInterval(20)
	);
	public static final EntityType<ItemFrameEntity> field_6043 = register(
		"item_frame",
		EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<FireballEntity> field_6066 = register(
		"fireball",
		EntityType.Builder.<FireballEntity>create(FireballEntity::new, SpawnGroup.field_17715).setDimensions(1.0F, 1.0F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<LeashKnotEntity> field_6138 = register(
		"leash_knot",
		EntityType.Builder.<LeashKnotEntity>create(LeashKnotEntity::new, SpawnGroup.field_17715)
			.disableSaving()
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<LightningEntity> field_6112 = register(
		"lightning_bolt",
		EntityType.Builder.create(LightningEntity::new, SpawnGroup.field_17715)
			.disableSaving()
			.setDimensions(0.0F, 0.0F)
			.maxTrackingRange(16)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<LlamaEntity> field_6074 = register(
		"llama", EntityType.Builder.create(LlamaEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 1.87F).maxTrackingRange(10)
	);
	public static final EntityType<LlamaSpitEntity> field_6124 = register(
		"llama_spit",
		EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<MagmaCubeEntity> field_6102 = register(
		"magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(2.04F, 2.04F).maxTrackingRange(8)
	);
	public static final EntityType<MinecartEntity> field_6096 = register(
		"minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<ChestMinecartEntity> field_6126 = register(
		"chest_minecart",
		EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<CommandBlockMinecartEntity> field_6136 = register(
		"command_block_minecart",
		EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<FurnaceMinecartEntity> field_6080 = register(
		"furnace_minecart",
		EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<HopperMinecartEntity> field_6058 = register(
		"hopper_minecart",
		EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<SpawnerMinecartEntity> field_6142 = register(
		"spawner_minecart",
		EntityType.Builder.<SpawnerMinecartEntity>create(SpawnerMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<TntMinecartEntity> field_6053 = register(
		"tnt_minecart", EntityType.Builder.<TntMinecartEntity>create(TntMinecartEntity::new, SpawnGroup.field_17715).setDimensions(0.98F, 0.7F).maxTrackingRange(8)
	);
	public static final EntityType<MuleEntity> field_6057 = register(
		"mule", EntityType.Builder.create(MuleEntity::new, SpawnGroup.field_6294).setDimensions(1.3964844F, 1.6F).maxTrackingRange(8)
	);
	public static final EntityType<MooshroomEntity> field_6143 = register(
		"mooshroom", EntityType.Builder.create(MooshroomEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<OcelotEntity> field_6081 = register(
		"ocelot", EntityType.Builder.create(OcelotEntity::new, SpawnGroup.field_6294).setDimensions(0.6F, 0.7F).maxTrackingRange(10)
	);
	public static final EntityType<PaintingEntity> field_6120 = register(
		"painting",
		EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(10)
			.trackingTickInterval(Integer.MAX_VALUE)
	);
	public static final EntityType<PandaEntity> field_6146 = register(
		"panda", EntityType.Builder.create(PandaEntity::new, SpawnGroup.field_6294).setDimensions(1.3F, 1.25F).maxTrackingRange(10)
	);
	public static final EntityType<ParrotEntity> field_6104 = register(
		"parrot", EntityType.Builder.create(ParrotEntity::new, SpawnGroup.field_6294).setDimensions(0.5F, 0.9F).maxTrackingRange(8)
	);
	public static final EntityType<PhantomEntity> field_6078 = register(
		"phantom", EntityType.Builder.create(PhantomEntity::new, SpawnGroup.field_6302).setDimensions(0.9F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<PigEntity> field_6093 = register(
		"pig", EntityType.Builder.create(PigEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 0.9F).maxTrackingRange(10)
	);
	public static final EntityType<PiglinEntity> field_22281 = register(
		"piglin", EntityType.Builder.create(PiglinEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PiglinBruteEntity> field_25751 = register(
		"piglin_brute", EntityType.Builder.create(PiglinBruteEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PillagerEntity> field_6105 = register(
		"pillager", EntityType.Builder.create(PillagerEntity::new, SpawnGroup.field_6302).spawnableFarFromPlayer().setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PolarBearEntity> field_6042 = register(
		"polar_bear", EntityType.Builder.create(PolarBearEntity::new, SpawnGroup.field_6294).setDimensions(1.4F, 1.4F).maxTrackingRange(10)
	);
	public static final EntityType<TntEntity> field_6063 = register(
		"tnt",
		EntityType.Builder.<TntEntity>create(TntEntity::new, SpawnGroup.field_17715)
			.makeFireImmune()
			.setDimensions(0.98F, 0.98F)
			.maxTrackingRange(10)
			.trackingTickInterval(10)
	);
	public static final EntityType<PufferfishEntity> field_6062 = register(
		"pufferfish", EntityType.Builder.create(PufferfishEntity::new, SpawnGroup.field_24460).setDimensions(0.7F, 0.7F).maxTrackingRange(4)
	);
	public static final EntityType<RabbitEntity> field_6140 = register(
		"rabbit", EntityType.Builder.create(RabbitEntity::new, SpawnGroup.field_6294).setDimensions(0.4F, 0.5F).maxTrackingRange(8)
	);
	public static final EntityType<RavagerEntity> field_6134 = register(
		"ravager", EntityType.Builder.create(RavagerEntity::new, SpawnGroup.field_6302).setDimensions(1.95F, 2.2F).maxTrackingRange(10)
	);
	public static final EntityType<SalmonEntity> field_6073 = register(
		"salmon", EntityType.Builder.create(SalmonEntity::new, SpawnGroup.field_24460).setDimensions(0.7F, 0.4F).maxTrackingRange(4)
	);
	public static final EntityType<SheepEntity> field_6115 = register(
		"sheep", EntityType.Builder.create(SheepEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 1.3F).maxTrackingRange(10)
	);
	public static final EntityType<ShulkerEntity> field_6109 = register(
		"shulker",
		EntityType.Builder.create(ShulkerEntity::new, SpawnGroup.field_6302).makeFireImmune().spawnableFarFromPlayer().setDimensions(1.0F, 1.0F).maxTrackingRange(10)
	);
	public static final EntityType<ShulkerBulletEntity> field_6100 = register(
		"shulker_bullet",
		EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, SpawnGroup.field_17715).setDimensions(0.3125F, 0.3125F).maxTrackingRange(8)
	);
	public static final EntityType<SilverfishEntity> field_6125 = register(
		"silverfish", EntityType.Builder.create(SilverfishEntity::new, SpawnGroup.field_6302).setDimensions(0.4F, 0.3F).maxTrackingRange(8)
	);
	public static final EntityType<SkeletonEntity> field_6137 = register(
		"skeleton", EntityType.Builder.create(SkeletonEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.99F).maxTrackingRange(8)
	);
	public static final EntityType<SkeletonHorseEntity> field_6075 = register(
		"skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, SpawnGroup.field_6294).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<SlimeEntity> field_6069 = register(
		"slime", EntityType.Builder.create(SlimeEntity::new, SpawnGroup.field_6302).setDimensions(2.04F, 2.04F).maxTrackingRange(10)
	);
	public static final EntityType<SmallFireballEntity> field_6049 = register(
		"small_fireball",
		EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.3125F, 0.3125F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<SnowGolemEntity> field_6047 = register(
		"snow_golem", EntityType.Builder.create(SnowGolemEntity::new, SpawnGroup.field_17715).setDimensions(0.7F, 1.9F).maxTrackingRange(8)
	);
	public static final EntityType<SnowballEntity> field_6068 = register(
		"snowball",
		EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<SpectralArrowEntity> field_6135 = register(
		"spectral_arrow",
		EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.5F, 0.5F)
			.maxTrackingRange(4)
			.trackingTickInterval(20)
	);
	public static final EntityType<SpiderEntity> field_6079 = register(
		"spider", EntityType.Builder.create(SpiderEntity::new, SpawnGroup.field_6302).setDimensions(1.4F, 0.9F).maxTrackingRange(8)
	);
	public static final EntityType<SquidEntity> field_6114 = register(
		"squid", EntityType.Builder.create(SquidEntity::new, SpawnGroup.field_6300).setDimensions(0.8F, 0.8F).maxTrackingRange(8)
	);
	public static final EntityType<StrayEntity> field_6098 = register(
		"stray", EntityType.Builder.create(StrayEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.99F).maxTrackingRange(8)
	);
	public static final EntityType<StriderEntity> field_23214 = register(
		"strider", EntityType.Builder.create(StriderEntity::new, SpawnGroup.field_6294).makeFireImmune().setDimensions(0.9F, 1.7F).maxTrackingRange(10)
	);
	public static final EntityType<EggEntity> field_6144 = register(
		"egg", EntityType.Builder.<EggEntity>create(EggEntity::new, SpawnGroup.field_17715).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<EnderPearlEntity> field_6082 = register(
		"ender_pearl",
		EntityType.Builder.<EnderPearlEntity>create(EnderPearlEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<ExperienceBottleEntity> field_6064 = register(
		"experience_bottle",
		EntityType.Builder.<ExperienceBottleEntity>create(ExperienceBottleEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<PotionEntity> field_6045 = register(
		"potion",
		EntityType.Builder.<PotionEntity>create(PotionEntity::new, SpawnGroup.field_17715).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
	);
	public static final EntityType<TridentEntity> field_6127 = register(
		"trident",
		EntityType.Builder.<TridentEntity>create(TridentEntity::new, SpawnGroup.field_17715).setDimensions(0.5F, 0.5F).maxTrackingRange(4).trackingTickInterval(20)
	);
	public static final EntityType<TraderLlamaEntity> field_17714 = register(
		"trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, SpawnGroup.field_6294).setDimensions(0.9F, 1.87F).maxTrackingRange(10)
	);
	public static final EntityType<TropicalFishEntity> field_6111 = register(
		"tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, SpawnGroup.field_24460).setDimensions(0.5F, 0.4F).maxTrackingRange(4)
	);
	public static final EntityType<TurtleEntity> field_6113 = register(
		"turtle", EntityType.Builder.create(TurtleEntity::new, SpawnGroup.field_6294).setDimensions(1.2F, 0.4F).maxTrackingRange(10)
	);
	public static final EntityType<VexEntity> field_6059 = register(
		"vex", EntityType.Builder.create(VexEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(0.4F, 0.8F).maxTrackingRange(8)
	);
	public static final EntityType<VillagerEntity> field_6077 = register(
		"villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, SpawnGroup.field_17715).setDimensions(0.6F, 1.95F).maxTrackingRange(10)
	);
	public static final EntityType<VindicatorEntity> field_6117 = register(
		"vindicator", EntityType.Builder.create(VindicatorEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<WanderingTraderEntity> field_17713 = register(
		"wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, SpawnGroup.field_6294).setDimensions(0.6F, 1.95F).maxTrackingRange(10)
	);
	public static final EntityType<WitchEntity> field_6145 = register(
		"witch", EntityType.Builder.create(WitchEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<WitherEntity> field_6119 = register(
		"wither",
		EntityType.Builder.create(WitherEntity::new, SpawnGroup.field_6302)
			.makeFireImmune()
			.allowSpawningInside(Blocks.field_10606)
			.setDimensions(0.9F, 3.5F)
			.maxTrackingRange(10)
	);
	public static final EntityType<WitherSkeletonEntity> field_6076 = register(
		"wither_skeleton",
		EntityType.Builder.create(WitherSkeletonEntity::new, SpawnGroup.field_6302)
			.makeFireImmune()
			.allowSpawningInside(Blocks.field_10606)
			.setDimensions(0.7F, 2.4F)
			.maxTrackingRange(8)
	);
	public static final EntityType<WitherSkullEntity> field_6130 = register(
		"wither_skull",
		EntityType.Builder.<WitherSkullEntity>create(WitherSkullEntity::new, SpawnGroup.field_17715)
			.setDimensions(0.3125F, 0.3125F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
	);
	public static final EntityType<WolfEntity> field_6055 = register(
		"wolf", EntityType.Builder.create(WolfEntity::new, SpawnGroup.field_6294).setDimensions(0.6F, 0.85F).maxTrackingRange(10)
	);
	public static final EntityType<ZoglinEntity> field_23696 = register(
		"zoglin", EntityType.Builder.create(ZoglinEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(1.3964844F, 1.4F).maxTrackingRange(8)
	);
	public static final EntityType<ZombieEntity> field_6051 = register(
		"zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<ZombieHorseEntity> field_6048 = register(
		"zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, SpawnGroup.field_6294).setDimensions(1.3964844F, 1.6F).maxTrackingRange(10)
	);
	public static final EntityType<ZombieVillagerEntity> field_6054 = register(
		"zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, SpawnGroup.field_6302).setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<ZombifiedPiglinEntity> field_6050 = register(
		"zombified_piglin",
		EntityType.Builder.create(ZombifiedPiglinEntity::new, SpawnGroup.field_6302).makeFireImmune().setDimensions(0.6F, 1.95F).maxTrackingRange(8)
	);
	public static final EntityType<PlayerEntity> field_6097 = register(
		"player",
		EntityType.Builder.<PlayerEntity>create(SpawnGroup.field_17715)
			.disableSaving()
			.disableSummon()
			.setDimensions(0.6F, 1.8F)
			.maxTrackingRange(32)
			.trackingTickInterval(2)
	);
	public static final EntityType<FishingBobberEntity> field_6103 = register(
		"fishing_bobber",
		EntityType.Builder.<FishingBobberEntity>create(SpawnGroup.field_17715)
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
		SpawnGroup spawnGroup,
		boolean saveable,
		boolean summonable,
		boolean fireImmune,
		boolean spawnableFarFromPlayer,
		ImmutableSet<Block> immutableSet,
		EntityDimensions entityDimensions,
		int i,
		int j
	) {
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
	public Entity spawnFromItemStack(
		ServerWorld serverWorld,
		@Nullable ItemStack stack,
		@Nullable PlayerEntity player,
		BlockPos pos,
		SpawnReason spawnReason,
		boolean alignPosition,
		boolean invertY
	) {
		return this.spawn(
			serverWorld,
			stack == null ? null : stack.getTag(),
			stack != null && stack.hasCustomName() ? stack.getName() : null,
			player,
			pos,
			spawnReason,
			alignPosition,
			invertY
		);
	}

	@Nullable
	public T spawn(
		ServerWorld serverWorld,
		@Nullable CompoundTag itemTag,
		@Nullable Text name,
		@Nullable PlayerEntity player,
		BlockPos pos,
		SpawnReason spawnReason,
		boolean alignPosition,
		boolean invertY
	) {
		T entity = this.create(serverWorld, itemTag, name, player, pos, spawnReason, alignPosition, invertY);
		if (entity != null) {
			serverWorld.spawnEntityAndPassengers(entity);
		}

		return entity;
	}

	@Nullable
	public T create(
		ServerWorld serverWorld,
		@Nullable CompoundTag itemTag,
		@Nullable Text name,
		@Nullable PlayerEntity player,
		BlockPos pos,
		SpawnReason spawnReason,
		boolean alignPosition,
		boolean invertY
	) {
		T entity = this.create(serverWorld);
		if (entity == null) {
			return null;
		} else {
			double d;
			if (alignPosition) {
				entity.updatePosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
				d = getOriginY(serverWorld, pos, invertY, entity.getBoundingBox());
			} else {
				d = 0.0;
			}

			entity.refreshPositionAndAngles(
				(double)pos.getX() + 0.5, (double)pos.getY() + d, (double)pos.getZ() + 0.5, MathHelper.wrapDegrees(serverWorld.random.nextFloat() * 360.0F), 0.0F
			);
			if (entity instanceof MobEntity) {
				MobEntity mobEntity = (MobEntity)entity;
				mobEntity.headYaw = mobEntity.yaw;
				mobEntity.bodyYaw = mobEntity.yaw;
				mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(mobEntity.getBlockPos()), spawnReason, null, itemTag);
				mobEntity.playAmbientSound();
			}

			if (name != null && entity instanceof LivingEntity) {
				entity.setCustomName(name);
			}

			loadFromEntityTag(serverWorld, player, entity, itemTag);
			return entity;
		}
	}

	protected static double getOriginY(WorldView world, BlockPos pos, boolean invertY, Box boundingBox) {
		Box box = new Box(pos);
		if (invertY) {
			box = box.stretch(0.0, -1.0, 0.0);
		}

		Stream<VoxelShape> stream = world.getCollisions(null, box, entity -> true);
		return 1.0 + VoxelShapes.calculateMaxOffset(Direction.Axis.field_11052, boundingBox, stream, invertY ? -2.0 : -1.0);
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

	/**
	 * Returns whether the EntityType can spawn inside the given block.
	 * 
	 * <p>By default, non-fire-immune mobs can't spawn in/on blocks dealing fire damage.
	 * Any mob can't spawn in wither roses, sweet berry bush, or cacti.
	 * 
	 * <p>This can be overwritten via {@link EntityType.Builder#allowSpawningInside(Block[])}
	 */
	public boolean isInvalidSpawn(BlockState blockState) {
		if (this.canSpawnInside.contains(blockState.getBlock())) {
			return false;
		} else {
			return this.fireImmune
					|| !blockState.isIn(BlockTags.field_21952)
						&& !blockState.isOf(Blocks.field_10092)
						&& !CampfireBlock.isLitCampfire(blockState)
						&& !blockState.isOf(Blocks.field_10164)
				? blockState.isOf(Blocks.field_10606) || blockState.isOf(Blocks.field_16999) || blockState.isOf(Blocks.field_10029)
				: true;
		}
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

	public boolean isIn(Tag<EntityType<?>> tag) {
		return tag.contains(this);
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

		private Builder(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
			this.factory = factory;
			this.spawnGroup = spawnGroup;
			this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.field_6294 || spawnGroup == SpawnGroup.field_17715;
		}

		public static <T extends Entity> EntityType.Builder<T> create(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
			return new EntityType.Builder<>(factory, spawnGroup);
		}

		public static <T extends Entity> EntityType.Builder<T> create(SpawnGroup spawnGroup) {
			return new EntityType.Builder<>((entityType, world) -> null, spawnGroup);
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
				this.trackingTickInterval
			);
		}
	}

	public interface EntityFactory<T extends Entity> {
		T create(EntityType<T> type, World world);
	}
}
