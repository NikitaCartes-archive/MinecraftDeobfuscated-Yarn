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
 * finally processed by {@link net.minecraft.client.render.WorldRenderer#processWorldEvent(net.minecraft.entity.player.PlayerEntity, int, net.minecraft.util.math.BlockPos, int) WorldRenderer#processWorldEvent} and
 * {@link net.minecraft.client.render.WorldRenderer#processGlobalEvent(int, net.minecraft.util.math.BlockPos, int) WorldRenderer#processGlobalEvent} (for regular and global events respectively).
 */
public class WorldEvents {
	/**
	 * A Dispenser dispenses an item.
	 * <br>Plays the dispensing sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.BoatDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) BoatDispenserBehavior#playSound},
	 * {@link net.minecraft.block.dispenser.FallibleItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) FallibleItemDispenserBehavior#playSound},
	 * {@link net.minecraft.block.dispenser.ItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) ItemDispenserBehavior#playSound},
	 * and {@link net.minecraft.item.MinecartItem#DISPENSER_BEHAVIOR MinecartItem#DISPENSER_BEHAVIOR}
	 */
	public static final int DISPENSER_DISPENSES = 1000;
	/**
	 * A Dispenser fails to dispense an item.
	 * <br>Plays the dispenser fail sound event.
	 * <p>Called by {@link net.minecraft.block.DispenserBlock#dispense(net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos) DispenserBlock#dispense},
	 * {@link net.minecraft.block.DropperBlock#dispense(net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos) DropperBlock#dispense},
	 * and {@link net.minecraft.block.dispenser.FallibleItemDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) FallibleItemDispenserBehavior#playSound}
	 */
	public static final int DISPENSER_FAILS = 1001;
	/**
	 * A Dispenser launches a projectile.
	 * <br>Plays the dispenser launch sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.ProjectileDispenserBehavior#playSound(net.minecraft.util.math.BlockPointer) ProjectileDispenserBehavior#playSound}
	 */
	public static final int DISPENSER_LAUNCHES_PROJECTILE = 1002;
	/**
	 * An Eye of Ender is launched.
	 * <br>Plays the eye of ender launching sound event.
	 * <p>Called by {@link net.minecraft.item.EnderEyeItem#use(net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand) EnderEyeItem#use}
	 */
	public static final int EYE_OF_ENDER_LAUNCHES = 1003;
	/**
	 * A Firework Rocket is shot.
	 * <br>Plays the firework shoot sound event.
	 * <p>Called by {@link net.minecraft.block.dispenser.DispenserBehavior DispenserBehavior}
	 */
	public static final int FIREWORK_ROCKET_SHOOTS = 1004;
	/**
	 * An Iron Door is opened.
	 * <br>Plays the iron door opening sound event.
	 * <p>Called by {@link net.minecraft.block.DoorBlock#playOpenCloseSound(net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) DoorBlock#playOpenCloseSound}
	 */
	public static final int IRON_DOOR_OPENS = 1005;
	/**
	 * A Wooden Door is opened.
	 * <br>Plays the wooden door opening sound event.
	 * <p>Called by {@link net.minecraft.block.DoorBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.util.hit.BlockHitResult) DoorBlock#onUse},
	 * and {@link net.minecraft.block.DoorBlock#playOpenCloseSound(net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) DoorBlock#playOpenCloseSound}
	 */
	public static final int WOODEN_DOOR_OPENS = 1006;
	/**
	 * A Wooden Trapdoor is opened.
	 * <br>Plays the wooden trapdoor opening sound event.
	 * <p>Called by {@link net.minecraft.block.TrapdoorBlock#playToggleSound(net.minecraft.entity.player.PlayerEntity, net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) TrapdoorBlock#playToggleSound}
	 */
	public static final int WOODEN_TRAPDOOR_OPENS = 1007;
	/**
	 * A Fence Gate is opened.
	 * <br>Plays the fence gate opening sound event.
	 * <p>Called by {@link net.minecraft.block.FenceGateBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.util.hit.BlockHitResult) FenceGateBlock#onUse},
	 * and {@link net.minecraft.block.FenceGateBlock#neighborUpdate(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.Block, net.minecraft.util.math.BlockPos, boolean) FenceGateBlock#neighborUpdate}
	 */
	public static final int FENCE_GATE_OPENS = 1008;
	/**
	 * Fire is extinguished.
	 * <br>Plays the appropriate fire extinguish sound event.
	 * <br>A {@code 1} should be supplied as extra data if an entity was extinguished, and {@code 0} for a block.
	 * <p>Called by {@link net.minecraft.block.AbstractFireBlock#onBreak(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.entity.player.PlayerEntity) AbstractFireBlock#onBreak},
	 * {@link net.minecraft.entity.projectile.thrown.PotionEntity#extinguishFire(net.minecraft.util.math.BlockPos) PotionEntity#extinguishFire},
	 * and {@link net.minecraft.item.ShovelItem#useOnBlock(net.minecraft.item.ItemUsageContext) ShovelItem#useOnBlock}
	 */
	public static final int FIRE_EXTINGUISHED = 1009;
	/**
	 * A Music Disc is played.
	 * <br>Plays the appropriate music.
	 * <br>The Raw ID of the Music Disc item must be supplied as extra data.
	 * <br>A {@code 0} should be passed to stop music.
	 * <p>Called by {@link net.minecraft.item.MusicDiscItem#useOnBlock(net.minecraft.item.ItemUsageContext) MusicDiscItem#useOnBlock},
	 * and {@link net.minecraft.block.JukeboxBlock#removeRecord(net.minecraft.world.World, net.minecraft.util.math.BlockPos) JukeboxBlock#removeRecord}
	 */
	public static final int MUSIC_DISC_PLAYED = 1010;
	/**
	 * An Iron Door is closed.
	 * <br>Plays the iron door closing sound event.
	 * <p>Called by {@link net.minecraft.block.DoorBlock#playOpenCloseSound(net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) DoorBlock#playOpenCloseSound}
	 */
	public static final int IRON_DOOR_CLOSES = 1011;
	/**
	 * A Wooden Door is closed.
	 * <br>Plays the wooden door closing sound event.
	 * <p>Called by {@link net.minecraft.block.DoorBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.util.hit.BlockHitResult) DoorBlock#onUse},
	 * and {@link net.minecraft.block.DoorBlock#playOpenCloseSound(net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) DoorBlock#playOpenCloseSound}
	 */
	public static final int WOODEN_DOOR_CLOSES = 1012;
	/**
	 * A Wooden Trapdoor is closed.
	 * <br>Plays the wooden trapdoor closing sound event.
	 * <p>Called by {@link net.minecraft.block.TrapdoorBlock#playToggleSound(net.minecraft.entity.player.PlayerEntity, net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) TrapdoorBlock#playToggleSound}
	 */
	public static final int WOODEN_TRAPDOOR_CLOSES = 1013;
	/**
	 * A Fence Gate is closed.
	 * <br>Plays the fence gate closing sound event.
	 * <p>Called by {@link net.minecraft.block.FenceGateBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.util.hit.BlockHitResult) FenceGateBlock#onUse},
	 * and {@link net.minecraft.block.FenceGateBlock#neighborUpdate(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.Block, net.minecraft.util.math.BlockPos, boolean) FenceGateBlock#neighborUpdate}
	 */
	public static final int FENCE_GATE_CLOSES = 1014;
	/**
	 * A Ghast warns its victim.
	 * <br>Plays the ghast warn sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.GhastEntity.ShootFireballGoal#tick() GhastEntity.ShootFireballGoal#tick}
	 */
	public static final int GHAST_WARNS = 1015;
	/**
	 * A Ghast shoots a fireball.
	 * <br>Plays the ghast shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.GhastEntity.ShootFireballGoal#tick() GhastEntity.ShootFireballGoal#tick}
	 */
	public static final int GHAST_SHOOTS = 1016;
	/**
	 * An Ender Dragon shoots a fireball.
	 * <br>Plays the ender dragon shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.phase.StrafePlayerPhase#serverTick() StrafePlayerPhase#serverTick}
	 */
	public static final int ENDER_DRAGON_SHOOTS = 1017;
	/**
	 * A Blaze shoots a fireball or a Fire Charge is shot by a dispenser.
	 * <br>Plays the blaze shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.BlazeEntity.ShootFireballGoal#tick() BlazeEntity.ShootFireballGoal#tick},
	 * and {@link net.minecraft.block.dispenser.DispenserBehavior DispenserBehavior}
	 */
	public static final int BLAZE_SHOOTS = 1018;
	/**
	 * A Zombie attacks a Wooden Door.
	 * <br>Plays the zombie attacking wooden door sound event.
	 * <p>Called by {@link net.minecraft.entity.ai.goal.BreakDoorGoal#tick() BreakDoorGoal#tick}
	 */
	public static final int ZOMBIE_ATTACKS_WOODEN_DOOR = 1019;
	/**
	 * A Zombie attacks an Iron Door.
	 * <br>Plays the zombie attacking iron door sound event.
	 * <br>Goes unused.
	 */
	public static final int ZOMBIE_ATTACKS_IRON_DOOR = 1020;
	/**
	 * A Zombie breaks a Wooden Door.
	 * <br>Plays the zombie breaking wooden door sound event.
	 * <p>Called by {@link net.minecraft.entity.ai.goal.BreakDoorGoal#tick() BreakDoorGoal#tick}
	 */
	public static final int ZOMBIE_BREAKS_WOODEN_DOOR = 1021;
	/**
	 * A Wither breaks a block.
	 * <br>Plays the wither breaking block sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#mobTick() WitherEntity#mobTick}
	 */
	public static final int WITHER_BREAKS_BLOCK = 1022;
	/**
	 * A Wither is spawned.
	 * <br>Plays the wither spawn sound event.
	 * <br>This is a global event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#mobTick() WitherEntity#mobTick}
	 */
	public static final int WITHER_SPAWNS = 1023;
	/**
	 * A Wither shoots a wither skull.
	 * <br>Plays the wither shoot sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.WitherEntity#shootSkullAt(int, double, double, double, boolean) WitherEntity#shootSkullAt}
	 */
	public static final int WITHER_SHOOTS = 1024;
	/**
	 * A Bat takes off.
	 * <br>Plays the bat take off sound event.
	 * <p>Called by {@link net.minecraft.entity.passive.BatEntity#mobTick() BatEntity#mobTick}
	 */
	public static final int BAT_TAKES_OFF = 1025;
	/**
	 * A Zombie infects a Villager.
	 * <br>Plays the zombie infect villager sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieEntity#onKilledOther(net.minecraft.server.world.ServerWorld, net.minecraft.entity.LivingEntity) ZombieEntity#onKilledOther}
	 */
	public static final int ZOMBIE_INFECTS_VILLAGER = 1026;
	/**
	 * A Zombie Villager is cured.
	 * <br>Plays the zombie villager cured sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieVillagerEntity#finishConversion(net.minecraft.server.world.ServerWorld) ZombieVillagerEntity#finishConversion}
	 */
	public static final int ZOMBIE_VILLAGER_CURED = 1027;
	/**
	 * An Ender Dragon dies.
	 * <br>Plays the ender dragon death sound event.
	 * <br>This is a global event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonEntity#updatePostDeath() EnderDragonEntity#updatePostDeath}
	 */
	public static final int ENDER_DRAGON_DIES = 1028;
	/**
	 * An Anvil is destroyed from damage.
	 * <br>Plays the anvil destroyed sound event.
	 * <p>Called by {@link net.minecraft.block.AnvilBlock#onDestroyedOnLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.FallingBlockEntity) AnvilBlock#onDestroyedOnLanding},
	 * and {@link net.minecraft.screen.AnvilScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) AnvilScreenHandler#onTakeOutput}
	 */
	public static final int ANVIL_DESTROYED = 1029;
	/**
	 * An Anvil is used.
	 * <br>Plays the anvil used sound event.
	 * <p>Called by {@link net.minecraft.screen.AnvilScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) AnvilScreenHandler#onTakeOutput}
	 */
	public static final int ANVIL_USED = 1030;
	/**
	 * An Anvil lands after falling.
	 * <br>Plays the anvil landing sound event.
	 * <p>Called by {@link net.minecraft.block.AnvilBlock#onLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.block.BlockState, net.minecraft.entity.FallingBlockEntity) AnvilBlock#onLanding}
	 */
	public static final int ANVIL_LANDS = 1031;
	/**
	 * A Portal is travelled through.
	 * <br>Plays the portal travel sound event directly through the client's sound manager.
	 * <p>Called by {@link net.minecraft.server.network.ServerPlayerEntity#moveToWorld(net.minecraft.server.world.ServerWorld) ServerPlayerEntity#moveToWorld}
	 */
	public static final int TRAVEL_THROUGH_PORTAL = 1032;
	/**
	 * A Chorus Flower grows.
	 * <br>Plays the chorus flower growing sound event.
	 * <p>Called by {@link net.minecraft.block.ChorusFlowerBlock#grow(net.minecraft.world.World, net.minecraft.util.math.BlockPos, int) ChorusFlowerBlock#grow}
	 */
	public static final int CHORUS_FLOWER_GROWS = 1033;
	/**
	 * A Chorus Flower dies.
	 * <br>Plays the chorus flower death sound event.
	 * <p>Called by {@link net.minecraft.block.ChorusFlowerBlock#die(net.minecraft.world.World, net.minecraft.util.math.BlockPos) ChorusFlowerBlock#die}
	 */
	public static final int CHORUS_FLOWER_DIES = 1034;
	/**
	 * A Brewing Stand brews.
	 * <br>Plays the brewing stand brewing sound event.
	 * <p>Called by {@link net.minecraft.block.entity.BrewingStandBlockEntity#craft(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.util.collection.DefaultedList) BrewingStandBlockEntity#craft}
	 */
	public static final int BREWING_STAND_BREWS = 1035;
	/**
	 * An Iron Trapdoor closes.
	 * <br>Plays the iron trapdoor closing sound event.
	 * <p>Called by {@link net.minecraft.block.TrapdoorBlock#playToggleSound(net.minecraft.entity.player.PlayerEntity, net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) TrapdoorBlock#playToggleSound}
	 */
	public static final int IRON_TRAPDOOR_CLOSES = 1036;
	/**
	 * An Iron Trapdoor opens.
	 * <br>Plays the iron trapdoor opening sound event.
	 * <p>Called by {@link net.minecraft.block.TrapdoorBlock#playToggleSound(net.minecraft.entity.player.PlayerEntity, net.minecraft.world.World, net.minecraft.util.math.BlockPos, boolean) TrapdoorBlock#playToggleSound}
	 */
	public static final int IRON_TRAPDOOR_OPENS = 1037;
	/**
	 * An End Portal is opened.
	 * <br>Plays the end portal spawn sound event.
	 * <br>This is a global event.
	 * <p>Called by {@link net.minecraft.item.EnderEyeItem#useOnBlock(net.minecraft.item.ItemUsageContext) EnderEyeItem#useOnBlock}
	 */
	public static final int END_PORTAL_OPENED = 1038;
	/**
	 * A Phantom bites its victim.
	 * <br>Plays the phantom bite sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.PhantomEntity.SwoopMovementGoal#tick() PhantomEntity.SwoopMovementGoal#tick}
	 */
	public static final int PHANTOM_BITES = 1039;
	/**
	 * A Zombie converts into a Drowned.
	 * <br>Plays the zombie convert to drowned sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.ZombieEntity#convertInWater() ZombieEntity#convertInWater}
	 */
	public static final int ZOMBIE_CONVERTS_TO_DROWNED = 1040;
	/**
	 * A Husk converts into a Zombie.
	 * <br>Plays the husk convert to zombie sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.HuskEntity#convertInWater() HuskEntity#convertInWater}
	 */
	public static final int HUSK_CONVERTS_TO_ZOMBIE = 1041;
	/**
	 * A Grindstone is used.
	 * <br>Plays the grindstone used sound event.
	 * <p>Called by {@link net.minecraft.screen.GrindstoneScreenHandler GrindstoneScreenHandler}
	 */
	public static final int GRINDSTONE_USED = 1042;
	/**
	 * A page is turned in a Book on a Lectern.
	 * <br>Plays the page turn sound event.
	 * <p>Called by {@link net.minecraft.block.LecternBlock#setPowered(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) LecternBlock#setPowered}
	 */
	public static final int LECTERN_BOOK_PAGE_TURNED = 1043;
	/**
	 * A Smithing Table is used.
	 * <br>Plays the smithing table used sound event.
	 * <p>Called by {@link net.minecraft.screen.SmithingScreenHandler#onTakeOutput(net.minecraft.entity.player.PlayerEntity, net.minecraft.item.ItemStack) SmithingScreenHandler#onTakeOutput}
	 */
	public static final int SMITHING_TABLE_USED = 1044;
	/**
	 * Pointed Dripstone lands after falling.
	 * <br>Plays the pointed dripstone landing sound event.
	 * <p>Called by {@link net.minecraft.block.PointedDripstoneBlock#onDestroyedOnLanding(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.FallingBlockEntity) PointedDripstoneBlock#onDestroyedOnLanding}
	 */
	public static final int POINTED_DRIPSTONE_LANDS = 1045;
	/**
	 * Pointed Dripstone drips Lava into a Cauldron.
	 * <br>Plays the pointed dripstone dripping lava into cauldron sound event.
	 * <p>Called by {@link net.minecraft.block.CauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) CauldronBlock#fillFromDripstone}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS_LAVA_INTO_CAULDRON = 1046;
	/**
	 * Pointed Dripstone drips Water into a Cauldron.
	 * <br>Plays the pointed dripstone dripping water into cauldron sound event.
	 * <p>Called by {@link net.minecraft.block.CauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) CauldronBlock#fillFromDripstone},
	 * and {@link net.minecraft.block.LeveledCauldronBlock#fillFromDripstone(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.fluid.Fluid) LeveledCauldronBlock#fillFromDripstone}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS_WATER_INTO_CAULDRON = 1047;
	/**
	 * A Skeleton converts into a Stray.
	 * <br>Plays the skeleton convert to stray sound event.
	 * <p>Called by {@link net.minecraft.entity.mob.SkeletonEntity#convertToStray() SkeletonEntity#convertToStray}
	 */
	public static final int SKELETON_CONVERTS_TO_STRAY = 1048;
	/**
	 * An item is composted in a Composter.
	 * <br>Plays the appropriate composting sound event and spawns composter particles.
	 * <p>A {@code 1} should be passed as extra data if the use of the composter added to the level of compost inside.
	 * <p>Called by {@link net.minecraft.block.ComposterBlock#onUse(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.util.hit.BlockHitResult) ComposterBlock#onUse},
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
	 * A Redstone Torch burns out.
	 * <br>Plays the redstone torch burn out sound event and spawns smoke particles.
	 * <p>Called by {@link net.minecraft.block.RedstoneTorchBlock#scheduledTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, java.util.Random) RedstoneTorchBlock#scheduledTick}
	 */
	public static final int REDSTONE_TORCH_BURNS_OUT = 1502;
	/**
	 * An End Portal Frame is filled with an Eye of Ender.
	 * <br>Plays the end portal frame filled sound event and spawns smoke particles.
	 * <p>Called by {@link net.minecraft.item.EnderEyeItem#useOnBlock(net.minecraft.item.ItemUsageContext) EnderEyeItem#useOnBlock}
	 */
	public static final int END_PORTAL_FRAME_FILLED = 1503;
	/**
	 * Pointed Dripstone drips fluid particles.
	 * <br>Spawns dripping fluid particles.
	 * <p>Called by {@link net.minecraft.block.PointedDripstoneBlock#dripTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, float) PointedDripstoneBlock#dripTick}
	 */
	public static final int POINTED_DRIPSTONE_DRIPS = 1504;
	public static final int field_33511 = 1505;
	/**
	 * A Dispenser is activated.
	 * <br>Spawns smoke particles.
	 * <br>The ordinal direction the dispenser is facing must be supplied as extra data.
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
	 * {@link net.minecraft.block.PowderSnowBlock#tryDrainFluid(net.minecraft.world.WorldAccess, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) PowderSnowBlock#tryDrainFluid},
	 * {@link net.minecraft.entity.passive.RabbitEntity.EatCarrotCropGoal#tick() RabbitEntity.EatCarrotCropGoal#tick},
	 * {@link net.minecraft.block.SpongeBlock#update(net.minecraft.world.World, net.minecraft.util.math.BlockPos) SpongeBlock#update},
	 * {@link net.minecraft.block.TurtleEggBlock#breakEgg(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState) TurtleEggBlock#breakEgg},
	 * {@link net.minecraft.block.TurtleEggBlock#randomTick(net.minecraft.block.BlockState, net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos, java.util.Random) TurtleEggBlock#randomTick},
	 * {@link net.minecraft.entity.passive.TurtleEntity#tickMovement() TurtleEntity#tickMovement},
	 * {@link net.minecraft.block.WitherSkullBlock#onPlaced(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.entity.SkullBlockEntity) WitherSkullBlock#onPlaced},
	 * and {@link net.minecraft.world.World#breakBlock(net.minecraft.util.math.BlockPos, boolean, net.minecraft.entity.Entity, int) World#breakBlock}
	 */
	public static final int BLOCK_BROKEN = 2001;
	/**
	 * A non-instant Splash Potion is splashed.
	 * <br>Plays the splash potion breaking sound event and spawns splash potion particles.
	 * <p>The hex color of the potion must be supplied as extra data.
	 * <p>For instant effects such as Instant Health and Instant Damage, use {@link #INSTANT_SPLASH_POTION_SPLASHED}.
	 * <p>Called by {@link net.minecraft.entity.projectile.thrown.ExperienceBottleEntity#onCollision(net.minecraft.util.hit.HitResult) ExperienceBottleEntity#onCollision},
	 * and {@link net.minecraft.entity.projectile.thrown.PotionEntity#onCollision(net.minecraft.util.hit.HitResult) PotionEntity#onCollision}
	 */
	public static final int SPLASH_POTION_SPLASHED = 2002;
	/**
	 * A thrown Eye of Ender breaks.
	 * <br>Spawns several particles.
	 * <p>Called by {@link net.minecraft.entity.EyeOfEnderEntity#tick() EyeOfEnderEntity#tick}
	 */
	public static final int EYE_OF_ENDER_BREAKS = 2003;
	/**
	 * A Spawner spawns a mob.
	 * <br>Spawns smoke and flame particles.
	 * <p>Called by {@link net.minecraft.world.MobSpawnerLogic#serverTick(net.minecraft.server.world.ServerWorld, net.minecraft.util.math.BlockPos) MobSpawnerLogic#serverTick}
	 */
	public static final int SPAWNER_SPAWNS_MOB = 2004;
	/**
	 * A plant is fertilized with Bone Meal or by a Bee, or a Turtle Egg is placed
	 * <br>Spawns happy villager particles.
	 * <p>Called by {@link net.minecraft.entity.passive.BeeEntity.GrowCropsGoal#tick() BeeEntity.GrowCropsGoal#tick},
	 * {@link net.minecraft.item.BoneMealItem#useOnBlock(net.minecraft.item.ItemUsageContext) BoneMealItem#useOnBlock},
	 * {@link net.minecraft.entity.ai.brain.task.BoneMealTask#keepRunning(net.minecraft.server.world.ServerWorld, net.minecraft.entity.passive.VillagerEntity, long) BoneMealTask#keepRunning},
	 * {@link net.minecraft.block.dispenser.DispenserBehavior DispenserBehavior},
	 * and {@link net.minecraft.block.TurtleEggBlock#onBlockAdded(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean) TurtleEggBlock#onBlockAdded}
	 */
	public static final int PLANT_FERTILIZED = 2005;
	/**
	 * A Dragon Breath Cloud spawns.
	 * <br>Plays the dragon fireball explode sound event and spawns dragon breath particles.
	 * <p>Called by {@link net.minecraft.entity.projectile.DragonFireballEntity#onCollision(net.minecraft.util.hit.HitResult) DragonFireballEntity#onCollision}
	 */
	public static final int DRAGON_BREATH_CLOUD_SPAWNS = 2006;
	/**
	 * An instant Splash Potion is splashed.
	 * <br>Plays the splash potion breaking sound event and spawns instant splash potion particles.
	 * <p>The hex color of the potion must be supplied as extra data.
	 * <p>For non-instant effects, use {@link #SPLASH_POTION_SPLASHED}.
	 * <p>Called by {@link net.minecraft.entity.projectile.thrown.PotionEntity#onCollision(net.minecraft.util.hit.HitResult) PotionEntity#onCollision}
	 */
	public static final int INSTANT_SPLASH_POTION_SPLASHED = 2007;
	/**
	 * An Ender Dragon breaks a block.
	 * <br>Spawns an explosion particle.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonEntity#destroyBlocks(net.minecraft.util.math.Box) EnderDragonEntity#destroyBlocks}
	 */
	public static final int ENDER_DRAGON_BREAKS_BLOCK = 2008;
	/**
	 * A Wet Sponge dries out in a hot dimension.
	 * <br>Spawns cloud particles.
	 * <p>Called by {@link net.minecraft.block.WetSpongeBlock#onBlockAdded(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, boolean) WetSpongeBlock#onBlockAdded}
	 * lic static final int WET_SPONGE_DRIES_OUT = 2009;
	 */
	public static final int WET_SPONGE_DRIES_OUT = 2009;
	/**
	 * An End Gateway spawns.
	 * <br>Plays the end gateway spawn sound event and spawns an explosion emitter particle.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonFight#generateEndGateway(net.minecraft.util.math.BlockPos) EnderDragonFight#generateEndGateway}
	 */
	public static final int END_GATEWAY_SPAWNS = 3000;
	/**
	 * The Ender Dragon is being resurrected.
	 * <br>Plays the ender dragon growl sound event.
	 * <p>Called by {@link net.minecraft.entity.boss.dragon.EnderDragonSpawnState#run(net.minecraft.server.world.ServerWorld, net.minecraft.entity.boss.dragon.EnderDragonFight, java.util.List, int, net.minecraft.util.math.BlockPos) EnderDragonSpawnState#run}
	 */
	public static final int ENDER_DRAGON_RESURRECTED = 3001;
	/**
	 * Electricity sparks after Lightning hits a Lightning Rod or Oxidizable blocks.
	 * <br>Spawns electric spark particles.
	 * <p>The ordinal direction the lightning rod is facing must be supplied as extra data.
	 * <br>A {@code -1} should be passed if the event is called by a lightning entity itself.
	 * <p>Called by {@link net.minecraft.block.LightningRodBlock#setPowered(net.minecraft.block.BlockState, net.minecraft.world.World, net.minecraft.util.math.BlockPos) LightningRodBlock#setPowered},
	 * and {@link net.minecraft.entity.LightningEntity#cleanOxidizationAround(net.minecraft.world.World, net.minecraft.util.math.BlockPos) LightningEntity#cleanOxidizationAround}
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
}
