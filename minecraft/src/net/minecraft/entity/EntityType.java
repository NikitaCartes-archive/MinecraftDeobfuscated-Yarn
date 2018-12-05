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
import net.minecraft.class_3730;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
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
		"area_effect_cloud", EntityType.Builder.create(AreaEffectCloudEntity.class, AreaEffectCloudEntity::new)
	);
	public static final EntityType<ArmorStandEntity> ARMOR_STAND = register(
		"armor_stand", EntityType.Builder.create(ArmorStandEntity.class, ArmorStandEntity::new)
	);
	public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.Builder.create(ArrowEntity.class, ArrowEntity::new));
	public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.create(BatEntity.class, BatEntity::new));
	public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.Builder.create(BlazeEntity.class, BlazeEntity::new));
	public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.Builder.create(BoatEntity.class, BoatEntity::new));
	public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.create(CatEntity.class, CatEntity::new));
	public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register(
		"cave_spider", EntityType.Builder.create(CaveSpiderEntity.class, CaveSpiderEntity::new)
	);
	public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.Builder.create(ChickenEntity.class, ChickenEntity::new));
	public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.create(CodEntity.class, CodEntity::new));
	public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.create(CowEntity.class, CowEntity::new));
	public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.Builder.create(CreeperEntity.class, CreeperEntity::new));
	public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.Builder.create(DonkeyEntity.class, DonkeyEntity::new));
	public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.Builder.create(DolphinEntity.class, DolphinEntity::new));
	public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register(
		"dragon_fireball", EntityType.Builder.create(DragonFireballEntity.class, DragonFireballEntity::new)
	);
	public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.Builder.create(DrownedEntity.class, DrownedEntity::new));
	public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register(
		"elder_guardian", EntityType.Builder.create(ElderGuardianEntity.class, ElderGuardianEntity::new)
	);
	public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register(
		"end_crystal", EntityType.Builder.create(EnderCrystalEntity.class, EnderCrystalEntity::new)
	);
	public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register(
		"ender_dragon", EntityType.Builder.create(EnderDragonEntity.class, EnderDragonEntity::new)
	);
	public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.Builder.create(EndermanEntity.class, EndermanEntity::new));
	public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.Builder.create(EndermiteEntity.class, EndermiteEntity::new));
	public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register(
		"evoker_fangs", EntityType.Builder.create(EvokerFangsEntity.class, EvokerFangsEntity::new)
	);
	public static final EntityType<EvokerEntity> EVOKER = register("evoker", EntityType.Builder.create(EvokerEntity.class, EvokerEntity::new));
	public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register(
		"experience_orb", EntityType.Builder.create(ExperienceOrbEntity.class, ExperienceOrbEntity::new)
	);
	public static final EntityType<EnderEyeEntity> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.create(EnderEyeEntity.class, EnderEyeEntity::new));
	public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
		"falling_block", EntityType.Builder.create(FallingBlockEntity.class, FallingBlockEntity::new)
	);
	public static final EntityType<FireworkEntity> FIREWORK_ROCKET = register(
		"firework_rocket", EntityType.Builder.create(FireworkEntity.class, FireworkEntity::new)
	);
	public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.Builder.create(GhastEntity.class, GhastEntity::new));
	public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.Builder.create(GiantEntity.class, GiantEntity::new));
	public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.create(GuardianEntity.class, GuardianEntity::new));
	public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.create(HorseEntity.class, HorseEntity::new));
	public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.Builder.create(HuskEntity.class, HuskEntity::new));
	public static final EntityType<IllusionerEntity> ILLUSIONER = register("illusioner", EntityType.Builder.create(IllusionerEntity.class, IllusionerEntity::new));
	public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.create(ItemEntity.class, ItemEntity::new));
	public static final EntityType<ItemFrameEntity> ITEM_FRAME = register("item_frame", EntityType.Builder.create(ItemFrameEntity.class, ItemFrameEntity::new));
	public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.Builder.create(FireballEntity.class, FireballEntity::new));
	public static final EntityType<LeadKnotEntity> LEASH_KNOT = register(
		"leash_knot", EntityType.Builder.<LeadKnotEntity>create(LeadKnotEntity.class, LeadKnotEntity::new).disableSaving()
	);
	public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.Builder.create(LlamaEntity.class, LlamaEntity::new));
	public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register("llama_spit", EntityType.Builder.create(LlamaSpitEntity.class, LlamaSpitEntity::new));
	public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register("magma_cube", EntityType.Builder.create(MagmaCubeEntity.class, MagmaCubeEntity::new));
	public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.Builder.create(MinecartEntity.class, MinecartEntity::new));
	public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register(
		"chest_minecart", EntityType.Builder.create(ChestMinecartEntity.class, ChestMinecartEntity::new)
	);
	public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register(
		"command_block_minecart", EntityType.Builder.create(CommandBlockMinecartEntity.class, CommandBlockMinecartEntity::new)
	);
	public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register(
		"furnace_minecart", EntityType.Builder.create(FurnaceMinecartEntity.class, FurnaceMinecartEntity::new)
	);
	public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register(
		"hopper_minecart", EntityType.Builder.create(HopperMinecartEntity.class, HopperMinecartEntity::new)
	);
	public static final EntityType<MobSpawnerMinecartEntity> SPAWNER_MINECART = register(
		"spawner_minecart", EntityType.Builder.create(MobSpawnerMinecartEntity.class, MobSpawnerMinecartEntity::new)
	);
	public static final EntityType<TNTMinecartEntity> TNT_MINECART = register(
		"tnt_minecart", EntityType.Builder.create(TNTMinecartEntity.class, TNTMinecartEntity::new)
	);
	public static final EntityType<MuleEntity> MULE = register("mule", EntityType.Builder.create(MuleEntity.class, MuleEntity::new));
	public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.Builder.create(MooshroomEntity.class, MooshroomEntity::new));
	public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.Builder.create(OcelotEntity.class, OcelotEntity::new));
	public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.Builder.create(PaintingEntity.class, PaintingEntity::new));
	public static final EntityType<PandaEntity> PANDA = register("panda", EntityType.Builder.create(PandaEntity.class, PandaEntity::new));
	public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.Builder.create(ParrotEntity.class, ParrotEntity::new));
	public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.create(PigEntity.class, PigEntity::new));
	public static final EntityType<PufferfishEntity> PUFFERFISH = register("pufferfish", EntityType.Builder.create(PufferfishEntity.class, PufferfishEntity::new));
	public static final EntityType<PigZombieEntity> ZOMBIE_PIGMAN = register(
		"zombie_pigman", EntityType.Builder.create(PigZombieEntity.class, PigZombieEntity::new)
	);
	public static final EntityType<PolarBearEntity> POLAR_BEAR = register("polar_bear", EntityType.Builder.create(PolarBearEntity.class, PolarBearEntity::new));
	public static final EntityType<PrimedTNTEntity> TNT = register("tnt", EntityType.Builder.create(PrimedTNTEntity.class, PrimedTNTEntity::new));
	public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.Builder.create(RabbitEntity.class, RabbitEntity::new));
	public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.Builder.create(SalmonEntity.class, SalmonEntity::new));
	public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.Builder.create(SheepEntity.class, SheepEntity::new));
	public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.Builder.create(ShulkerEntity.class, ShulkerEntity::new));
	public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register(
		"shulker_bullet", EntityType.Builder.create(ShulkerBulletEntity.class, ShulkerBulletEntity::new)
	);
	public static final EntityType<SilverfishEntity> SILVERFISH = register("silverfish", EntityType.Builder.create(SilverfishEntity.class, SilverfishEntity::new));
	public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.Builder.create(SkeletonEntity.class, SkeletonEntity::new));
	public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register(
		"skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity.class, SkeletonHorseEntity::new)
	);
	public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.Builder.create(SlimeEntity.class, SlimeEntity::new));
	public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register(
		"small_fireball", EntityType.Builder.create(SmallFireballEntity.class, SmallFireballEntity::new)
	);
	public static final EntityType<SnowmanEntity> SNOW_GOLEM = register("snow_golem", EntityType.Builder.create(SnowmanEntity.class, SnowmanEntity::new));
	public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.Builder.create(SnowballEntity.class, SnowballEntity::new));
	public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register(
		"spectral_arrow", EntityType.Builder.create(SpectralArrowEntity.class, SpectralArrowEntity::new)
	);
	public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.create(SpiderEntity.class, SpiderEntity::new));
	public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.Builder.create(SquidEntity.class, SquidEntity::new));
	public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.Builder.create(StrayEntity.class, StrayEntity::new));
	public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register(
		"tropical_fish", EntityType.Builder.create(TropicalFishEntity.class, TropicalFishEntity::new)
	);
	public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.Builder.create(TurtleEntity.class, TurtleEntity::new));
	public static final EntityType<ThrownEggEntity> EGG = register("egg", EntityType.Builder.create(ThrownEggEntity.class, ThrownEggEntity::new));
	public static final EntityType<ThrownEnderpearlEntity> ENDER_PEARL = register(
		"ender_pearl", EntityType.Builder.create(ThrownEnderpearlEntity.class, ThrownEnderpearlEntity::new)
	);
	public static final EntityType<ThrownExperienceBottleEntity> EXPERIENCE_BOTTLE = register(
		"experience_bottle", EntityType.Builder.create(ThrownExperienceBottleEntity.class, ThrownExperienceBottleEntity::new)
	);
	public static final EntityType<ThrownPotionEntity> POTION = register("potion", EntityType.Builder.create(ThrownPotionEntity.class, ThrownPotionEntity::new));
	public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.create(VexEntity.class, VexEntity::new));
	public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.Builder.create(VillagerEntity.class, VillagerEntity::new));
	public static final EntityType<IronGolemEntity> IRON_GOLEM = register("iron_golem", EntityType.Builder.create(IronGolemEntity.class, IronGolemEntity::new));
	public static final EntityType<VindicatorEntity> VINDICATOR = register("vindicator", EntityType.Builder.create(VindicatorEntity.class, VindicatorEntity::new));
	public static final EntityType<PillagerEntity> PILLAGER = register("pillager", EntityType.Builder.create(PillagerEntity.class, PillagerEntity::new));
	public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.Builder.create(WitchEntity.class, WitchEntity::new));
	public static final EntityType<EntityWither> WITHER = register("wither", EntityType.Builder.create(EntityWither.class, EntityWither::new));
	public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register(
		"wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity.class, WitherSkeletonEntity::new)
	);
	public static final EntityType<ExplodingWitherSkullEntity> WITHER_SKULL = register(
		"wither_skull", EntityType.Builder.create(ExplodingWitherSkullEntity.class, ExplodingWitherSkullEntity::new)
	);
	public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.Builder.create(WolfEntity.class, WolfEntity::new));
	public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.Builder.create(ZombieEntity.class, ZombieEntity::new));
	public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register(
		"zombie_horse", EntityType.Builder.create(ZombieHorseEntity.class, ZombieHorseEntity::new)
	);
	public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register(
		"zombie_villager", EntityType.Builder.create(ZombieVillagerEntity.class, ZombieVillagerEntity::new)
	);
	public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.Builder.create(PhantomEntity.class, PhantomEntity::new));
	public static final EntityType<IllagerBeastEntity> ILLAGER_BEAST = register(
		"illager_beast", EntityType.Builder.create(IllagerBeastEntity.class, IllagerBeastEntity::new)
	);
	public static final EntityType<LightningEntity> LIGHTNING_BOLT = register(
		"lightning_bolt", EntityType.Builder.<LightningEntity>create(LightningEntity.class).disableSaving()
	);
	public static final EntityType<PlayerEntity> PLAYER = register(
		"player", EntityType.Builder.<PlayerEntity>create(PlayerEntity.class).disableSaving().disableSummon()
	);
	public static final EntityType<FishHookEntity> FISHING_BOBBER = register(
		"fishing_bobber", EntityType.Builder.<FishHookEntity>create(FishHookEntity.class).disableSaving().disableSummon()
	);
	public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.Builder.create(TridentEntity.class, TridentEntity::new));
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

	public EntityType(Class<? extends T> class_, Function<? super World, ? extends T> function, boolean bl, boolean bl2, @Nullable Type<?> type) {
		this.entityClass = class_;
		this.factory = function;
		this.saveable = bl;
		this.summonable = bl2;
		this.dataFixerType = type;
	}

	@Nullable
	public Entity spawnFromItemStack(
		World world, @Nullable ItemStack itemStack, @Nullable PlayerEntity playerEntity, BlockPos blockPos, class_3730 arg, boolean bl, boolean bl2
	) {
		return this.spawn(
			world,
			itemStack == null ? null : itemStack.getTag(),
			itemStack != null && itemStack.hasDisplayName() ? itemStack.getDisplayName() : null,
			playerEntity,
			blockPos,
			arg,
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
		class_3730 arg,
		boolean bl,
		boolean bl2
	) {
		T entity = this.create(world, compoundTag, textComponent, playerEntity, blockPos, arg, bl, bl2);
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
		class_3730 arg,
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
				mobEntity.headPitch = mobEntity.yaw;
				mobEntity.field_6283 = mobEntity.yaw;
				mobEntity.method_5943(world, world.getLocalDifficulty(new BlockPos(mobEntity)), arg, null, compoundTag);
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
		return 1.0 + VoxelShapes.method_1085(Direction.Axis.Y, boundingBox, stream, bl ? -2.0 : -1.0);
	}

	public static void loadFromEntityTag(World world, @Nullable PlayerEntity playerEntity, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
		if (compoundTag != null && compoundTag.containsKey("EntityTag", 10)) {
			MinecraftServer minecraftServer = world.getServer();
			if (minecraftServer != null && entity != null) {
				if (world.isRemote || !entity.method_5833() || playerEntity != null && minecraftServer.getConfigurationManager().isOperator(playerEntity.getGameProfile())) {
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

	@Nullable
	public T create(World world) {
		return (T)this.factory.apply(world);
	}

	@Nullable
	public static Entity createInstance(World world, Identifier identifier) {
		return newInstance(world, Registry.ENTITY_TYPE.get(identifier));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Entity createInstanceFromId(int i, World world) {
		return newInstance(world, Registry.ENTITY_TYPE.getInt(i));
	}

	@Nullable
	public static Entity fromTag(CompoundTag compoundTag, World world) {
		Identifier identifier = new Identifier(compoundTag.getString("id"));
		Entity entity = createInstance(world, identifier);
		if (entity == null) {
			LOGGER.warn("Skipping Entity with id {}", identifier);
		} else {
			entity.fromTag(compoundTag);
		}

		return entity;
	}

	@Nullable
	private static Entity newInstance(World world, @Nullable EntityType<?> entityType) {
		return entityType == null ? null : entityType.create(world);
	}

	public static class Builder<T extends Entity> {
		private final Class<? extends T> entityClass;
		private final Function<? super World, ? extends T> function;
		private boolean saveable = true;
		private boolean summonable = true;

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

			return new EntityType<>(this.entityClass, this.function, this.saveable, this.summonable, type);
		}
	}
}
