package net.minecraft.world;

/**
 * Constants of World Event IDs.
 * <p>World Events are used to trigger things on the client from the server side.
 * Most commonly, playing sound events or spawning particles.
 * <p>Some events have an extra data integer sent alongside them.
 * <br>Some events are global, meaning they will be sent to every player regardless of their position.
 * <p>Events are sent from the server to the client using {@link net.minecraft.network.packet.s2c.play.WorldEventS2CPacket WorldEventS2CPacket},
 * received on the client by {@link net.minecraft.client.network.ClientPlayNetworkHandler#onWorldEvent(net.minecraft.network.packet.s2c.play.WorldEventS2CPacket) ClientPlayNetworkHandler#onWorldEvent},
 * synced by {@link net.minecraft.client.world.ClientWorld#syncWorldEvent(net.minecraft.entity.player.PlayerEntity, int, net.minecraft.util.math.BlockPos, int) ClientWorld#syncWorldEvent} and
 * {@link net.minecraft.client.world.ClientWorld#syncGlobalEvent(int, net.minecraft.util.math.BlockPos, int) ClientWorld#syncGlobalEvent} (for regular and global events respectively), and
 * finally processed by {@link net.minecraft.client.render.WorldRenderer#processWorldEvent(int, net.minecraft.util.math.BlockPos, int) WorldRenderer#processWorldEvent} and
 * {@link net.minecraft.client.render.WorldRenderer#processGlobalEvent(int, net.minecraft.util.math.BlockPos, int) WorldRenderer#processGlobalEvent} (for regular and global events respectively).
 */
public class WorldEvents {
	/**
	 * A dispenser dispenses an item.
	 * <br>Plays the dispensing sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.BoatDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) BoatDispenserBehavior#playSound},
	 * {@link net.minecraft.block.dispenser.FallibleItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) FallibleItemDispenserBehavior#playSound},
	 * {@link net.minecraft.block.dispenser.ItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) ItemDispenserBehavior#playSound},
	 * and {@link net.minecraft.item.MinecartItem#DISPENSER_BEHAVIOR MinecartItem#DISPENSER_BEHAVIOR}
	 */
	public static final int DISPENSER_DISPENSES = 1000;
	/**
	 * A dispenser fails to dispense an item.
	 * <br>Plays the dispenser fail sound event.
	 * <p>Called by {@link net.minecraft.block.DispenserBlock#dispense(net.minecraft.server.world.ServerWorld, net.minecraft.block.BlockState, net.minecraft.util.math.BlockPos) DispenserBlock#dispense},
	 * {@link net.minecraft.block.DropperBlock#dispense(net.minecraft.server.world.ServerWorld, net.minecraft.block.BlockState, net.minecraft.util.math.BlockPos) DropperBlock#dispense},
	 * and {@link net.minecraft.block.dispenser.FallibleItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) FallibleItemDispenserBehavior#playSound}
	 */
	public static final int DISPENSER_FAILS = 1001;
	/**
	 * A dispenser launches a projectile.
	 * <br>Plays the dispenser launch sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.ProjectileDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) ProjectileDispenserBehavior#playSound}
	 */
	public static final int DISPENSER_LAUNCHES_PROJECTILE = 1002;
	/**
	 * A firework rocket is shot.
	 * <br>Plays the firework shoot sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.DispenserBehavior DispenserBehavior}
	 */
	public static final int FIREWORK_ROCKET_SHOOTS = 1004;
	/**
	 * Fire is extinguished.
	 * <br>Plays the appropriate fire extinguish sound event.
	 * <p>A {@code 1} should be supplied as extra data if an entity was extinguished, and {@code 0} for a block.
	 * <p>Called by {@link net.minecraft.block.AbstractFireBlock#onBreak(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.entity.player.PlayerEntity) AbstractFireBlock#onBreak},
	 * {@link net.minecraft.entity.projectile.thrown.PotionEntity#extinguishFire(net.minecraft.util.math.BlockPos) PotionEntity#extinguishFire},
	 * and {@link net.minecraft.item.ShovelItem#useOnBlock(net.minecraft.item.ItemUsageContext) ShovelItem#useOnBlock}
	 */
	public static final int FIRE_EXTINGUISHED = 1009;
	/**
	 * A jukebox starts playing a music disc.
	 * <br>Plays the appropriate music.
	 * <p>The raw ID of the music disc item must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.entity.JukeboxBlockEntity#startPlaying() JukeboxBlockEntity#startPlaying}
	 */
	public static final int JUKEBOX_STARTS_PLAYING = 1010;
	/**
	 * A jukebox stops playing a music disc.
	 * <br>Stops any music currently playing.
	 * <p>Called by {@link net.minecraft.block.entity.JukeboxBlockEntity#stopPlaying() JukeboxBlockEntity#stopPlaying}
	 */
	public static final int JUKEBOX_STOPS_PLAYING = 1011;
	/**
	 * A ghast warns its victim.
	 * <br>Plays the ghast warn sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.GhastEntity.ShootFireballGoal#tick() GhastEntity.ShootFireballGoal#tick}
	 */
	public static final int GHAST_WARNS = 1015;
	/**
	 * A ghast shoots a fireball.
	 * <br>Plays the ghast shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.GhastEntity.ShootFireballGoal#tick() GhastEntity.ShootFireballGoal#tick}
	 */
	public static final int GHAST_SHOOTS = 1016;
	/**
	 * An ender dragon shoots a fireball.
	 * <br>Plays the ender dragon shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.phase.StrafePlayerPhase#serverTick() StrafePlayerPhase#serverTick}
	 */
	public static final int ENDER_DRAGON_SHOOTS = 1017;
	/**
	 * A blaze shoots a fireball or a fire charge is shot by a dispenser.
	 * <br>Plays the blaze shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.BlazeEntity.ShootFireballGoal#tick() BlazeEntity.ShootFireballGoal#tick},
	 * and {@link net.minecraft.block.dispenser.DispenserBehavior DispenserBehavior}
	 */
	public static final int BLAZE_SHOOTS = 1018;
	/**
	 * A zombie attacks a wooden door.
	 * <br>Plays the zombie attacking wooden door sound event.
	 * <p>Called by {@link net.minecraft.entity.ai.goal.BreakDoorGoal#tick() BreakDoorGoal#tick}
	 */
	public static final int ZOMBIE_ATTACKS_WOODEN_DOOR = 1019;
	/**
	 * A zombie attacks an iron door.
	 * <br>Plays the zombie attacking iron door sound event.
	 * <br>Goes unused.
	 */
	public static final int ZOMBIE_ATTACKS_IRON_DOOR = 1020;
	/**
	 * A zombie breaks a wooden door.
	 * <br>Plays the zombie breaking wooden door sound event.
	 * <p>Called by {@link net.minecraft.entity.ai.goal.BreakDoorGoal#tick() BreakDoorGoal#tick}
	 */
	public static final int ZOMBIE_BREAKS_WOODEN_DOOR = 1021;
	/**
	 * A wither breaks a block.
	 * <br>Plays the wither breaking block sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#mobTick() WitherEntity#mobTick}
	 */
	public static final int WITHER_BREAKS_BLOCK = 1022;
	/**
	 * A wither is spawned.
	 * <br>Plays the wither spawn sound event.
	 * <p>This is a global event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#mobTick() WitherEntity#mobTick}
	 */
	public static final int WITHER_SPAWNS = 1023;
	/**
	 * A wither shoots a wither skull.
	 * <br>Plays the wither shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#shootSkullAt(int, double, double, double, boolean) WitherEntity#shootSkullAt}
	 */
	public static final int WITHER_SHOOTS = 1024;
	/**
	 * A bat takes off.
	 * <br>Plays the bat take off sound event.
	 * <p>Called by {@link net.minecraft.entity.passive.BatEntity#mobTick() BatEntity#mobTick}
	 */
	public static final int BAT_TAKES_OFF = 1025;
	/**
	 * A zombie infects a villager.
	 * <br>Plays the zombie infect villager sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieEntity#onKilledOther(net.minecraft.server.world.ServerWorld, net.minecraft.entity.LivingEntity) ZombieEntity#onKilledOther}
	 */
	public static final int ZOMBIE_INFECTS_VILLAGER = 1026;
	/**
	 * A zombie villager is cured.
	 * <br>Plays the zombie villager cured sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieVillagerEntity#finishConversion(net.minecraft.server.world.ServerWorld) ZombieVillagerEntity#finishConversion}
	 */
	public static final int ZOMBIE_VILLAGER_CURED = 1027;
	/**
	 * An ender dragon dies.
	 * <br>Plays the ender dragon death sound event.
	 * <p>This is a global event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonEntity#updatePostDeath() EnderDragonEntity#updatePostDeath}
	 */
	public static final int ENDER_DRAGON_DIES = 1028;
	/**
	 * An anvil is destroyed from damage.
	 * <br>Plays the anvil destroyed sound event.
	 * <p>Called by {@link net.minecraft.block.AnvilBlock#onDestroyedOnLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.FallingBlockEntity) AnvilBlock#onDestroyedOnLanding},
	 * and {@link net.minecraft.screen.AnvilScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) AnvilScreenHandler#onTakeOutput}
	 */
	public static final int ANVIL_DESTROYED = 1029;
	/**
	 * An anvil is used.
	 * <br>Plays the anvil used sound event.
	 * <p>Called by {@link net.minecraft.screen.AnvilScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) AnvilScreenHandler#onTakeOutput}
	 */
	public static final int ANVIL_USED = 1030;
	/**
	 * An anvil lands after falling.
	 * <br>Plays the anvil landing sound event.
	 * <p>Called by {@link net.minecraft.block.AnvilBlock#onLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.block.BlockState, net.minecraft.entity.FallingBlockEntity) AnvilBlock#onLanding}
	 */
	public static final int ANVIL_LANDS = 1031;
	/**
	 * A portal is traveled through.
	 * <br>Plays the portal travel sound event directly through the client's sound manager.
	 * <p>Called by {@link net.minecraft.server.network.ServerPlayerEntity#moveToWorld(net.minecraft.server.world.ServerWorld) ServerPlayerEntity#moveToWorld}
	 */
	public static final int TRAVEL_THROUGH_PORTAL = 1032;
	/**
	 * A chorus flower grows.
	 * <br>Plays the chorus flower growing sound event.
	 * <p>Called by {@link net.minecraft.block.ChorusFlowerBlock#grow(net.minecraft.world.World, net.minecraft.util.math.BlockPos, int) ChorusFlowerBlock#grow}
	 */
	public static final int CHORUS_FLOWER_GROWS = 1033;
	/**
	 * A chorus flower dies.
	 * <br>Plays the chorus flower death sound event.
	 * <p>Called by {@link net.minecraft.block.ChorusFlowerBlock#die(net.minecraft.world.World, net.minecraft.util.math.BlockPos) ChorusFlowerBlock#die}
	 */
	public static final int CHORUS_FLOWER_DIES = 1034;
	/**
	 * A brewing stand brews.
	 * <br>Plays the brewing stand brewing sound event.
	 * <p>Called by {@link net.minecraft.block.entity.BrewingStandBlockEntity#craft(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.util.collection.DefaultedList) BrewingStandBlockEntity#craft}
	 */
	public static final int BREWING_STAND_BREWS = 1035;
	/**
	 * An end portal is opened.
	 * <br>Plays the end portal spawn sound event.
	 * <p>This is a global event.
	 * <p>Called by {@link net.minecraft.item.EnderEyeItem#useOnBlock(net.minecraft.item.ItemUsageContext) EnderEyeItem#useOnBlock}
	 */
	public static final int END_PORTAL_OPENED = 1038;
	/**
	 * A phantom bites its victim.
	 * <br>Plays the phantom bite sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.PhantomEntity.SwoopMovementGoal#tick() PhantomEntity.SwoopMovementGoal#tick}
	 */
	public static final int PHANTOM_BITES = 1039;
	/**
	 * A zombie converts into a drowned.
	 * <br>Plays the zombie convert to drowned sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieEntity#convertInWater() ZombieEntity#convertInWater}
	 */
	public static final int ZOMBIE_CONVERTS_TO_DROWNED = 1040;
	/**
	 * A husk converts into a zombie.
	 * <br>Plays the husk convert to zombie sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.HuskEntity#convertInWater() HuskEntity#convertInWater}
	 */
	public static final int HUSK_CONVERTS_TO_ZOMBIE = 1041;
	/**
	 * A grindstone is used.
	 * <br>Plays the grindstone used sound event.
	 * <p>Called by {@link net.minecraft.screen.GrindstoneScreenHandler GrindstoneScreenHandler}
	 */
	public static final int GRINDSTONE_USED = 1042;
	/**
	 * A page is turned in a book on a lectern.
	 * <br>Plays the page turn sound event.
	 * <p>Called by {@link net.minecraft.block.LecternBlock#setPowered(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) LecternBlock#setPowered}
	 */
	public static final int LECTERN_BOOK_PAGE_TURNED = 1043;
	/**
	 * A smithing table is used.
	 * <br>Plays the smithing table used sound event.
	 * <p>Called by {@link net.minecraft.screen.SmithingScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) SmithingScreenHandler#onTakeOutput}
	 */
	public static final int SMITHING_TABLE_USED = 1044;
	/**
	 * A pointed dripstone lands after falling.
	 * <br>Plays the pointed dripstone landing sound event.
	 * <p>Called by {@link net.minecraft.block.PointedDripstoneBlock#onDestroyedOnLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.FallingBlockEntity) PointedDripstoneBlock#onDestroyedOnLanding}
	 */
	public static final int POINTED_DRIPSTONE_LANDS = 1045;
	/**
	 * A pointed dripstone drips lava into a cauldron.
	 * <br>Plays the pointed dripstone dripping lava into cauldron sound event.
	 * <p>Called by {@link net.minecraft.block.CauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) CauldronBlock#fillFromDripstone}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS_LAVA_INTO_CAULDRON = 1046;
	/**
	 * A pointed dripstone drips water into a cauldron.
	 * <br>Plays the pointed dripstone dripping water into cauldron sound event.
	 * <p>Called by {@link net.minecraft.block.CauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) CauldronBlock#fillFromDripstone},
	 * and {@link net.minecraft.block.LeveledCauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) LeveledCauldronBlock#fillFromDripstone}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON = 1047;
	/**
	 * A skeleton converts into a stray.
	 * <br>Plays the skeleton convert to stray sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.SkeletonEntity#convertToStray() SkeletonEntity#convertToStray}
	 */
	public static final int SKELETON_CONVERTS_TO_STRAY = 1048;
	/**
	 * A crafter crafts an item.
	 * <br>Plays the crafter craft sound event.
	 * <p>Called by {@link net.minecraft.block.CrafterBlock#transferOrSpawnStack CrafterBlock#transferOrSpawnStack}
	 */
	public static final int CRAFTER_CRAFTS = 1049;
	/**
	 * A crafter fails to craft.
	 * <br>Plays the crafter fail sound event.
	 * <p>Called by {@link net.minecraft.block.CrafterBlock#craft(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos) CrafterBlock#craft}
	 */
	public static final int CRAFTER_FAILS = 1050;
	public static final int field_51787 = 1051;
	/**
	 * An item is composted in a composter.
	 * <br>Plays the appropriate composting sound event and spawns composter particles.
	 * <p>A {@code 1} should be passed as extra data if the use of the composter added to the level of compost inside.
	 * <p>Called by {@link net.minecraft.block.ComposterBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.hit.BlockHitResult) ComposterBlock#onUse},
	 * {@link net.minecraft.block.ComposterBlock.ComposterInventory#markDirty() ComposterBlock.ComposterInventory#markDirty},
	 * and {@link net.minecraft.entity.ai.brain.task.FarmerWorkTask#syncComposterEvent(net.minecraft.server.world.ServerWorld, net.minecraft.block.BlockState, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) FarmerWorkTask#syncComposterEvent}
	 */
	public static final int COMPOSTER_USED = 1500;
	/**
	 * Lava is extinguished.
	 * <br>Plays the lava extinguish sound event and spawns large smoke particles.
	 * <p>Called by {@link net.minecraft.block.FluidBlock#playExtinguishSound(net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos) FluidBlock#playExtinguishSound},
	 * and {@link net.minecraft.fluid.LavaFluid#playExtinguishEvent(net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos) LavaFluid#playExtinguishEvent}
	 */
	public static final int LAVA_EXTINGUISHED = 1501;
	/**
	 * A redstone torch burns out.
	 * <br>Plays the redstone torch burn out sound event and spawns smoke particles.
	 * <p>Called by {@link net.minecraft.block.RedstoneTorchBlock#scheduledTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, net.minecraft.util.math.random.AbstractRandom) RedstoneTorchBlock#scheduledTick}
	 */
	public static final int REDSTONE_TORCH_BURNS_OUT = 1502;
	/**
	 * An end portal frame is filled with an eye of ender.
	 * <br>Plays the end portal frame filled sound event and spawns smoke particles.
	 * <p>Called by {@link net.minecraft.item.EnderEyeItem#useOnBlock(net.minecraft.item.ItemUsageContext) EnderEyeItem#useOnBlock}
	 */
	public static final int END_PORTAL_FRAME_FILLED = 1503;
	/**
	 * A pointed dripstone drips fluid particles.
	 * <br>Spawns dripping fluid particles.
	 * <p>Called by {@link net.minecraft.block.PointedDripstoneBlock#dripTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, float) PointedDripstoneBlock#dripTick}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS = 1504;
	/**
	 * Bone meal is used.
	 * <br>Plays the bone meal item used sound event and spawns happy villager particles.
	 * <p>The amount of particles to spawn must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.item.BoneMealItem#useOnBlock(net.minecraft.item.ItemUsageContext) BoneMealItem#useOnBlock},
	 * an anonymous class in {@link net.minecraft.block.dispenser.DispenserBehavior#registerDefaults() DispenserBehavior#registerDefaults},
	 * and {@link net.minecraft.entity.ai.brain.task.BoneMealTask#keepRunning(net.minecraft.server.world.ServerWorld, net.minecraft.entity.passive.VillagerEntity, long) BoneMealTask#keepRunning}
	 */
	public static final int BONE_MEAL_USED = 1505;
	/**
	 * A dispenser is activated.
	 * <br>Shoots smoke particles.
	 * <p>The ordinal direction the dispenser is facing must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.dispenser.ItemDispenserBehavior#spawnParticles(net.minecraft.util.math.BlockPointer, net.minecraft.util.math.Direction) ItemDispenserBehavior#spawnParticles}
	 */
	public static final int DISPENSER_ACTIVATED = 2000;
	/**
	 * A block is broken.
	 * <br>Plays the appropriate block breaking sound event and spawns block breaking particles.
	 * <p>The raw ID of the block must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.Block#spawnBreakParticles(net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) Block#spawnBreakParticles},
	 * {@link net.minecraft.block.TallPlantBlock#onBreakInCreative(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.entity.player.PlayerEntity) TallPlantBlock#onBreakInCreative},
	 * {@link net.minecraft.entity.ai.goal.BreakDoorGoal#tick() BreakDoorGoal#tick},
	 * {@link net.minecraft.block.CarvedPumpkinBlock#trySpawnEntity(net.minecraft.world.World, net.minecraft.util.math.BlockPos) CarvedPumpkinBlock#trySpawnEntity},
	 * {@link net.minecraft.entity.ai.goal.EatGrassGoal#tick() EatGrassGoal#tick},
	 * {@link net.minecraft.entity.passive.FoxEntity#tick() FoxEntity#tick},
	 * {@link net.minecraft.block.PowderSnowBlock#tryDrainFluid(net.minecraft.entity.player.PlayerEntity, net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) PowderSnowBlock#tryDrainFluid},
	 * {@link net.minecraft.entity.passive.RabbitEntity.EatCarrotCropGoal#tick() RabbitEntity.EatCarrotCropGoal#tick},
	 * {@link net.minecraft.block.SpongeBlock#update(net.minecraft.world.World, net.minecraft.util.math.BlockPos) SpongeBlock#update},
	 * {@link net.minecraft.block.TurtleEggBlock#breakEgg(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) TurtleEggBlock#breakEgg},
	 * {@link net.minecraft.block.TurtleEggBlock#randomTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, net.minecraft.util.math.random.AbstractRandom) TurtleEggBlock#randomTick},
	 * {@link net.minecraft.entity.passive.TurtleEntity#tickMovement() TurtleEntity#tickMovement},
	 * {@link net.minecraft.block.WitherSkullBlock#onPlaced(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.entity.SkullBlockEntity) WitherSkullBlock#onPlaced},
	 * and {@link net.minecraft.world.World#breakBlock(net.minecraft.util.math.BlockPos, boolean, net.minecraft.entity.Entity, int) World#breakBlock}
	 */
	public static final int BLOCK_BROKEN = 2001;
	/**
	 * A non-instant splash potion is splashed.
	 * <br>Plays the splash potion breaking sound event and spawns splash potion particles.
	 * <p>The hex color of the potion must be supplied as extra data.
	 * <p>For instant effects such as Instant Health and Instant Damage, use {@link #INSTANT_SPLASH_POTION_SPLASHED}.
	 * <p>Called by {@link net.minecraft.entity.projectile.thrown.ExperienceBottleEntity#onCollision(net.minecraft.util.hit.HitResult) ExperienceBottleEntity#onCollision},
	 * and {@link net.minecraft.entity.projectile.thrown.PotionEntity#onCollision(net.minecraft.util.hit.HitResult) PotionEntity#onCollision}
	 */
	public static final int SPLASH_POTION_SPLASHED = 2002;
	/**
	 * A thrown eye of ender breaks.
	 * <br>Spawns several particles.
	 * <p>Called by {@link net.minecraft.entity.EyeOfEnderEntity#tick() EyeOfEnderEntity#tick}
	 */
	public static final int EYE_OF_ENDER_BREAKS = 2003;
	/**
	 * A spawner spawns a mob.
	 * <br>Spawns smoke and flame particles.
	 * <p>Called by {@link net.minecraft.block.spawner.MobSpawnerLogic#serverTick(net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos) MobSpawnerLogic#serverTick}
	 */
	public static final int SPAWNER_SPAWNS_MOB = 2004;
	/**
	 * A dragon breath cloud spawns.
	 * <br>Plays the dragon fireball explode sound event and spawns dragon breath particles.
	 * <p>Called by {@link net.minecraft.entity.projectile.DragonFireballEntity#onCollision(net.minecraft.util.hit.HitResult) DragonFireballEntity#onCollision}
	 */
	public static final int DRAGON_BREATH_CLOUD_SPAWNS = 2006;
	/**
	 * An instant splash potion is splashed.
	 * <br>Plays the splash potion breaking sound event and spawns instant splash potion particles.
	 * <p>The hex color of the potion must be supplied as extra data.
	 * <p>For non-instant effects, use {@link #SPLASH_POTION_SPLASHED}.
	 * <p>Called by {@link net.minecraft.entity.projectile.thrown.PotionEntity#onCollision(net.minecraft.util.hit.HitResult) PotionEntity#onCollision}
	 */
	public static final int INSTANT_SPLASH_POTION_SPLASHED = 2007;
	/**
	 * An ender dragon breaks a block.
	 * <br>Spawns an explosion particle.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonEntity#destroyBlocks(net.minecraft.util.math.Box) EnderDragonEntity#destroyBlocks}
	 */
	public static final int ENDER_DRAGON_BREAKS_BLOCK = 2008;
	/**
	 * A wet sponge dries out in an ultrawarm dimension.
	 * <br>Spawns cloud particles.
	 * <p>Called by {@link net.minecraft.block.WetSpongeBlock#onBlockAdded(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean) WetSpongeBlock#onBlockAdded}
	 */
	public static final int WET_SPONGE_DRIES_OUT = 2009;
	/**
	 * A crafter shoots out an item.
	 * <br>Shoots white smoke particles.
	 * <p>The ordinal direction the crafter is facing must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.CrafterBlock#transferOrSpawnStack(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.entity.CrafterBlockEntity, net.minecraft.item.ItemStack, net.minecraft.block.BlockState) CrafterBlock#transferOrSpawnStack}
	 */
	public static final int CRAFTER_SHOOTS = 2010;
	/**
	 * A plant is fertilized by a bee.
	 * <br>Spawns happy villager particles.
	 * <p>The amount of particles to spawn must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.entity.passive.BeeEntity.GrowCropsGoal#tick() BeeEntity.GrowCropsGoal#tick}
	 */
	public static final int BEE_FERTILIZES_PLANT = 2011;
	/**
	 * A turtle egg is placed on sand.
	 * <br>Spawns happy villager particles.
	 * <p>The amount of particles to spawn must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.TurtleEggBlock#onBlockAdded(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean) TurtleEggBlock#onBlockAdded}
	 */
	public static final int TURTLE_EGG_PLACED = 2012;
	/**
	 * A mace is used to execute a smash attack.
	 * <br>Spawns dust pillar particles.
	 * <p>The amount of particles to spawn must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.item.MaceItem#knockbackNearbyEntities}
	 */
	public static final int SMASH_ATTACK = 2013;
	/**
	 * An end gateway spawns.
	 * <br>Plays the end gateway spawn sound event and spawns an explosion emitter particle.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonFight#generateEndGateway(net.minecraft.util.math.BlockPos) EnderDragonFight#generateEndGateway}
	 */
	public static final int END_GATEWAY_SPAWNS = 3000;
	/**
	 * The ender dragon is being resurrected.
	 * <br>Plays the ender dragon growl sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonSpawnState#run(net.minecraft.server.world.ServerWorld, net.minecraft.entity.boss.dragon.EnderDragonFight, java.util.List, int, net.minecraft.util.math.BlockPos) EnderDragonSpawnState#run}
	 */
	public static final int ENDER_DRAGON_RESURRECTED = 3001;
	/**
	 * Electricity sparks after lightning hits a lightning rod or oxidizable blocks.
	 * <br>Spawns electric spark particles.
	 * <p>The ordinal direction the lightning rod is facing must be supplied as extra data.
	 * <br>A {@code -1} should be passed if the event is called by a lightning entity itself.
	 * <p>Called by {@link net.minecraft.block.LightningRodBlock#setPowered(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos) LightningRodBlock#setPowered},
	 * and {@link net.minecraft.entity.LightningEntity#cleanOxidationAround(net.minecraft.world.World, net.minecraft.util.math.BlockPos) LightningEntity#cleanOxidationAround}
	 */
	public static final int ELECTRICITY_SPARKS = 3002;
	/**
	 * A block is waxed.
	 * <br>Plays the block waxing sound event and spawns waxing particles.
	 * <p>Called by {@link net.minecraft.item.HoneycombItem#useOnBlock(net.minecraft.item.ItemUsageContext) HoneycombItem#useOnBlock}
	 */
	public static final int BLOCK_WAXED = 3003;
	/**
	 * Wax is removed from a block.
	 * <br>Spawns wax removal particles.
	 * <p>Called by {@link net.minecraft.item.AxeItem#useOnBlock(net.minecraft.item.ItemUsageContext) AxeItem#useOnBlock}
	 */
	public static final int WAX_REMOVED = 3004;
	/**
	 * A block is scraped.
	 * <br>Spawns scraping particles.
	 * <p>Called by {@link net.minecraft.item.AxeItem#useOnBlock(net.minecraft.item.ItemUsageContext) AxeItem#useOnBlock}
	 */
	public static final int BLOCK_SCRAPED = 3005;
	/**
	 * Sculk releases a charge.
	 * <br>Spawns sculk charge particles.
	 * <p>Called by {@link net.minecraft.block.entity.SculkSpreadManager#tick(net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos,  net.minecraft.util.math.random.Random, boolean) SculkSpreadManager#tick}
	 */
	public static final int SCULK_CHARGE = 3006;
	/**
	 * A sculk shrieker shrieks.
	 * <br>Spawns shriek particles and plays the shriek sound event.
	 * <p>Called by {@link net.minecraft.block.entity.SculkShriekerBlockEntity#shriek(net.minecraft.server.world.ServerWorld, net.minecraft.entity.Entity) SculkShriekerBlockEntity#shriek}
	 */
	public static final int SCULK_SHRIEKS = 3007;
	/**
	 * A block has been completely brushed.
	 * <br>Spawns block break particles and plays the block's brushing complete sound.
	 * <p>The block's raw ID must be supplied as extra data.
	 * <p>Called by {@link net.minecraft.block.entity.BrushableBlockEntity#finishBrushing(net.minecraft.entity.player.PlayerEntity) BrushableBlockEntity#finishBrushing}
	 */
	public static final int BLOCK_FINISHED_BRUSHING = 3008;
	/**
	 * A sniffer egg cracks.
	 * <br>Spawns between 1 and 3 egg crack particles.
	 * <p>If a {@code 1} is passed as extra data, between 3 and 6 egg crack particles are spawned instead.
	 * <p>Called by {@link net.minecraft.block.SnifferEggBlock#onBlockAdded(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean) SnifferEggBlock#onBlockAdded}
	 */
	public static final int SNIFFER_EGG_CRACKS = 3009;
	/**
	 * A trial spawner spawns a mob. Triggered at the position of the spawner.
	 * <br>Spawns mob spawn particles.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerLogic#trySpawnMob}.
	 */
	public static final int TRIAL_SPAWNER_SPAWNS_MOB = 3011;
	/**
	 * A trial spawner spawns a mob. Triggered at the position of the spawned mob.
	 * <br>Plays the spawn sound and spawns mob spawn particles.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerLogic#trySpawnMob}.
	 */
	public static final int TRIAL_SPAWNER_SPAWNS_MOB_AT_SPAWN_POS = 3012;
	/**
	 * A trial spawner detects survival-mode players.
	 * <br>Plays the detection sound and spawns detection particles.
	 * <p>The extra data denotes the number of players.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerData#updatePlayers}.
	 */
	public static final int TRIAL_SPAWNER_DETECTS_PLAYER = 3013;
	/**
	 * A trial spawner ejects loot.
	 * <br>Plays the ejection sound and spawns ejection particles.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerLogic#ejectLootTable}.
	 */
	public static final int TRIAL_SPAWNER_EJECTS_ITEM = 3014;
	/**
	 * A vault is activated.
	 * <br>Plays the activate sound and spawns smoke and small flame particles.
	 * <p>Called by {@link net.minecraft.block.enums.VaultState#ACTIVE}.
	 */
	public static final int VAULT_ACTIVATES = 3015;
	/**
	 * A vault is deactivated.
	 * <br>Plays the deactivate sound and spawns small flame particles.
	 * <p>Called by {@link net.minecraft.block.enums.VaultState#INACTIVE}.
	 */
	public static final int VAULT_DEACTIVATES = 3016;
	/**
	 * A vault ejects loot.
	 * <br>Spawns ejection particles.
	 * <p>Called by {@link net.minecraft.block.enums.VaultState#ejectItem}.
	 */
	public static final int VAULT_EJECTS_ITEM = 3017;
	/**
	 * A cobweb is placed by the weaving effect.
	 * <br>Spawns poof particles and plays the cobweb place sound.
	 * <p>Called by {@link net.minecraft.entity.effect.WeavingStatusEffect#tryPlaceCobweb}.
	 */
	public static final int COBWEB_WEAVED = 3018;
	/**
	 * An ominous trial spawner detects survival-mode players.
	 * <br>Plays the detection sound and spawns detection particles.
	 * <p>The extra data denotes the number of players.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerData#updatePlayers}.
	 */
	public static final int OMINOUS_TRIAL_SPAWNER_DETECTS_PLAYER = 3019;
	/**
	 * A trial spawner becomes ominous.
	 * <br>Plays the activate sound and spawns both detection and omen particles.
	 * <p>If a {@code 0} is passed as extra data, the activate sound will be played at 0.3 volume.
	 * <br>Otherwise, it is played at full volume.
	 * <p>Called by {@link net.minecraft.block.spawner.TrialSpawnerLogic#setOminous},
	 * and {@link net.minecraft.block.spawner.TrialSpawnerData#updatePlayers}.
	 */
	public static final int TRIAL_SPAWNER_TURNS_OMINOUS = 3020;
	/**
	 * An ominous item spawner spawns an item.
	 * <br>Plays the item spawn sound and spawns mob spawn particles.
	 * <p>Called by {@link net.minecraft.entity.OminousItemSpawnerEntity#spawnItem}
	 */
	public static final int OMINOUS_ITEM_SPAWNER_SPAWNS_ITEM = 3021;
}
