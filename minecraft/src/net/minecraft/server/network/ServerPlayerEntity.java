package net.minecraft.server.network;

import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class ServerPlayerEntity extends PlayerEntity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_29769 = 32;
	private static final int field_29770 = 10;
	private static final int field_46928 = 25;
	public static final double field_47708 = 1.0;
	private static final EntityAttributeModifier CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER_UUID = new EntityAttributeModifier(
		UUID.fromString("736565d2-e1a7-403d-a3f8-1aeb3e302542"), "Creative block interaction range modifier", 0.5, EntityAttributeModifier.Operation.ADD_VALUE
	);
	private static final EntityAttributeModifier CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER_UUID = new EntityAttributeModifier(
		UUID.fromString("98491ef6-97b1-4584-ae82-71a8cc85cf73"), "Creative entity interaction range modifier", 2.0, EntityAttributeModifier.Operation.ADD_VALUE
	);
	public ServerPlayNetworkHandler networkHandler;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager interactionManager;
	private final PlayerAdvancementTracker advancementTracker;
	private final ServerStatHandler statHandler;
	private float lastHealthScore = Float.MIN_VALUE;
	private int lastFoodScore = Integer.MIN_VALUE;
	private int lastAirScore = Integer.MIN_VALUE;
	private int lastArmorScore = Integer.MIN_VALUE;
	private int lastLevelScore = Integer.MIN_VALUE;
	private int lastExperienceScore = Integer.MIN_VALUE;
	private float syncedHealth = -1.0E8F;
	private int syncedFoodLevel = -99999999;
	private boolean syncedSaturationIsZero = true;
	private int syncedExperience = -99999999;
	private int joinInvulnerabilityTicks = 60;
	private ChatVisibility clientChatVisibility = ChatVisibility.FULL;
	private boolean clientChatColorsEnabled = true;
	private long lastActionTime = Util.getMeasuringTimeMs();
	@Nullable
	private Entity cameraEntity;
	private boolean inTeleportationState;
	public boolean seenCredits;
	private final ServerRecipeBook recipeBook = new ServerRecipeBook();
	@Nullable
	private Vec3d levitationStartPos;
	private int levitationStartTick;
	private boolean disconnected;
	private int viewDistance = 2;
	private String language = "en_us";
	@Nullable
	private Vec3d fallStartPos;
	@Nullable
	private Vec3d enteredNetherPos;
	@Nullable
	private Vec3d vehicleInLavaRidingPos;
	/**
	 * A chunk section position indicating where the player's client is currently
	 * watching chunks from. Used referentially for the game to update the chunks
	 * watched by this player.
	 * 
	 * @see #getWatchedSection()
	 * @see #setWatchedSection(ChunkSectionPos)
	 */
	private ChunkSectionPos watchedSection = ChunkSectionPos.from(0, 0, 0);
	private ChunkFilter chunkFilter = ChunkFilter.IGNORE_ALL;
	private RegistryKey<World> spawnPointDimension = World.OVERWORLD;
	@Nullable
	private BlockPos spawnPointPosition;
	private boolean spawnForced;
	private float spawnAngle;
	private final TextStream textStream;
	private boolean filterText;
	private boolean allowServerListing;
	private boolean spawnExtraParticlesOnFall;
	private SculkShriekerWarningManager sculkShriekerWarningManager = new SculkShriekerWarningManager(0, 0, 0);
	@Nullable
	private BlockPos startRaidPos;
	private Vec3d movement = Vec3d.ZERO;
	private final ScreenHandlerSyncHandler screenHandlerSyncHandler = new ScreenHandlerSyncHandler() {
		@Override
		public void updateState(ScreenHandler handler, DefaultedList<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, handler.nextRevision(), stacks, cursorStack));

			for (int i = 0; i < properties.length; i++) {
				this.sendPropertyUpdate(handler, i, properties[i]);
			}
		}

		@Override
		public void updateSlot(ScreenHandler handler, int slot, ItemStack stack) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), slot, stack));
		}

		@Override
		public void updateCursorStack(ScreenHandler handler, ItemStack stack) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, handler.nextRevision(), -1, stack));
		}

		@Override
		public void updateProperty(ScreenHandler handler, int property, int value) {
			this.sendPropertyUpdate(handler, property, value);
		}

		private void sendPropertyUpdate(ScreenHandler handler, int property, int value) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
		}
	};
	private final ScreenHandlerListener screenHandlerListener = new ScreenHandlerListener() {
		@Override
		public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			Slot slot = handler.getSlot(slotId);
			if (!(slot instanceof CraftingResultSlot)) {
				if (slot.inventory == ServerPlayerEntity.this.getInventory()) {
					Criteria.INVENTORY_CHANGED.trigger(ServerPlayerEntity.this, ServerPlayerEntity.this.getInventory(), stack);
				}
			}
		}

		@Override
		public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
		}
	};
	@Nullable
	private PublicPlayerSession session;
	@Nullable
	public final Object field_49777;
	private int screenHandlerSyncId;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
		super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
		this.textStream = server.createFilterer(this);
		this.interactionManager = server.getPlayerInteractionManager(this);
		this.server = server;
		this.statHandler = server.getPlayerManager().createStatHandler(this);
		this.advancementTracker = server.getPlayerManager().getAdvancementTracker(this);
		this.moveToSpawn(world);
		this.setClientOptions(clientOptions);
		this.field_49777 = null;
	}

	private void moveToSpawn(ServerWorld world) {
		BlockPos blockPos = world.getSpawnPos();
		if (world.getDimension().hasSkyLight() && world.getServer().getSaveProperties().getGameMode() != GameMode.ADVENTURE) {
			int i = Math.max(0, this.server.getSpawnRadius(world));
			int j = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder((double)blockPos.getX(), (double)blockPos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			long l = (long)(i * 2 + 1);
			long m = l * l;
			int k = m > 2147483647L ? Integer.MAX_VALUE : (int)m;
			int n = this.calculateSpawnOffsetMultiplier(k);
			int o = Random.create().nextInt(k);

			for (int p = 0; p < k; p++) {
				int q = (o + n * p) % k;
				int r = q % (i * 2 + 1);
				int s = q / (i * 2 + 1);
				BlockPos blockPos2 = SpawnLocating.findOverworldSpawn(world, blockPos.getX() + r - i, blockPos.getZ() + s - i);
				if (blockPos2 != null) {
					this.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
					if (world.isSpaceEmpty(this)) {
						break;
					}
				}
			}
		} else {
			this.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

			while (!world.isSpaceEmpty(this) && this.getY() < (double)(world.getTopY() - 1)) {
				this.setPosition(this.getX(), this.getY() + 1.0, this.getZ());
			}
		}
	}

	private int calculateSpawnOffsetMultiplier(int horizontalSpawnArea) {
		return horizontalSpawnArea <= 16 ? horizontalSpawnArea - 1 : 17;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("warden_spawn_tracker", NbtElement.COMPOUND_TYPE)) {
			SculkShriekerWarningManager.CODEC
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.get("warden_spawn_tracker")))
				.resultOrPartial(LOGGER::error)
				.ifPresent(sculkShriekerWarningManager -> this.sculkShriekerWarningManager = sculkShriekerWarningManager);
		}

		if (nbt.contains("enteredNetherPosition", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(nbtCompound.getDouble("x"), nbtCompound.getDouble("y"), nbtCompound.getDouble("z"));
		}

		this.seenCredits = nbt.getBoolean("seenCredits");
		if (nbt.contains("recipeBook", NbtElement.COMPOUND_TYPE)) {
			this.recipeBook.readNbt(nbt.getCompound("recipeBook"), this.server.getRecipeManager());
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}

		if (nbt.contains("SpawnX", NbtElement.NUMBER_TYPE) && nbt.contains("SpawnY", NbtElement.NUMBER_TYPE) && nbt.contains("SpawnZ", NbtElement.NUMBER_TYPE)) {
			this.spawnPointPosition = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
			this.spawnForced = nbt.getBoolean("SpawnForced");
			this.spawnAngle = nbt.getFloat("SpawnAngle");
			if (nbt.contains("SpawnDimension")) {
				this.spawnPointDimension = (RegistryKey<World>)World.CODEC
					.parse(NbtOps.INSTANCE, nbt.get("SpawnDimension"))
					.resultOrPartial(LOGGER::error)
					.orElse(World.OVERWORLD);
			}
		}

		this.spawnExtraParticlesOnFall = nbt.getBoolean("spawn_extra_particles_on_fall");
		NbtElement nbtElement = nbt.get("raid_omen_position");
		if (nbtElement != null) {
			BlockPos.CODEC.parse(NbtOps.INSTANCE, nbtElement).resultOrPartial(LOGGER::error).ifPresent(startRaidPos -> this.startRaidPos = startRaidPos);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		SculkShriekerWarningManager.CODEC
			.encodeStart(NbtOps.INSTANCE, this.sculkShriekerWarningManager)
			.resultOrPartial(LOGGER::error)
			.ifPresent(encoded -> nbt.put("warden_spawn_tracker", encoded));
		this.writeGameModeNbt(nbt);
		nbt.putBoolean("seenCredits", this.seenCredits);
		if (this.enteredNetherPos != null) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putDouble("x", this.enteredNetherPos.x);
			nbtCompound.putDouble("y", this.enteredNetherPos.y);
			nbtCompound.putDouble("z", this.enteredNetherPos.z);
			nbt.put("enteredNetherPosition", nbtCompound);
		}

		Entity entity = this.getRootVehicle();
		Entity entity2 = this.getVehicle();
		if (entity2 != null && entity != this && entity.hasPlayerRider()) {
			NbtCompound nbtCompound2 = new NbtCompound();
			NbtCompound nbtCompound3 = new NbtCompound();
			entity.saveNbt(nbtCompound3);
			nbtCompound2.putUuid("Attach", entity2.getUuid());
			nbtCompound2.put("Entity", nbtCompound3);
			nbt.put("RootVehicle", nbtCompound2);
		}

		nbt.put("recipeBook", this.recipeBook.toNbt());
		nbt.putString("Dimension", this.getWorld().getRegistryKey().getValue().toString());
		if (this.spawnPointPosition != null) {
			nbt.putInt("SpawnX", this.spawnPointPosition.getX());
			nbt.putInt("SpawnY", this.spawnPointPosition.getY());
			nbt.putInt("SpawnZ", this.spawnPointPosition.getZ());
			nbt.putBoolean("SpawnForced", this.spawnForced);
			nbt.putFloat("SpawnAngle", this.spawnAngle);
			Identifier.CODEC
				.encodeStart(NbtOps.INSTANCE, this.spawnPointDimension.getValue())
				.resultOrPartial(LOGGER::error)
				.ifPresent(encoded -> nbt.put("SpawnDimension", encoded));
		}

		nbt.putBoolean("spawn_extra_particles_on_fall", this.spawnExtraParticlesOnFall);
		if (this.startRaidPos != null) {
			BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.startRaidPos).resultOrPartial(LOGGER::error).ifPresent(encoded -> nbt.put("raid_omen_position", encoded));
		}
	}

	public void setExperiencePoints(int points) {
		float f = (float)this.getNextLevelExperience();
		float g = (f - 1.0F) / f;
		this.experienceProgress = MathHelper.clamp((float)points / f, 0.0F, g);
		this.syncedExperience = -1;
	}

	public void setExperienceLevel(int level) {
		this.experienceLevel = level;
		this.syncedExperience = -1;
	}

	@Override
	public void addExperienceLevels(int levels) {
		super.addExperienceLevels(levels);
		this.syncedExperience = -1;
	}

	@Override
	public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
		super.applyEnchantmentCosts(enchantedItem, experienceLevels);
		this.syncedExperience = -1;
	}

	private void onScreenHandlerOpened(ScreenHandler screenHandler) {
		screenHandler.addListener(this.screenHandlerListener);
		screenHandler.updateSyncHandler(this.screenHandlerSyncHandler);
	}

	public void onSpawn() {
		this.onScreenHandlerOpened(this.playerScreenHandler);
	}

	@Override
	public void enterCombat() {
		super.enterCombat();
		this.networkHandler.sendPacket(EnterCombatS2CPacket.INSTANCE);
	}

	@Override
	public void endCombat() {
		super.endCombat();
		this.networkHandler.sendPacket(new EndCombatS2CPacket(this.getDamageTracker()));
	}

	@Override
	protected void onBlockCollision(BlockState state) {
		Criteria.ENTER_BLOCK.trigger(this, state);
	}

	@Override
	protected ItemCooldownManager createCooldownManager() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void tick() {
		this.interactionManager.update();
		this.sculkShriekerWarningManager.tick();
		this.joinInvulnerabilityTicks--;
		if (this.timeUntilRegen > 0) {
			this.timeUntilRegen--;
		}

		this.currentScreenHandler.sendContentUpdates();
		if (!this.getWorld().isClient && !this.currentScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.currentScreenHandler = this.playerScreenHandler;
		}

		Entity entity = this.getCameraEntity();
		if (entity != this) {
			if (entity.isAlive()) {
				this.updatePositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
				this.getServerWorld().getChunkManager().updatePosition(this);
				if (this.shouldDismount()) {
					this.setCameraEntity(this);
				}
			} else {
				this.setCameraEntity(this);
			}
		}

		Criteria.TICK.trigger(this);
		if (this.levitationStartPos != null) {
			Criteria.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
		}

		this.tickFallStartPos();
		this.tickVehicleInLavaRiding();
		this.updateCreativeInteractionRangeModifiers();
		this.advancementTracker.sendUpdate(this);
	}

	private void updateCreativeInteractionRangeModifiers() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
		if (entityAttributeInstance != null) {
			if (this.isCreative()) {
				entityAttributeInstance.updateModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER_UUID);
			} else {
				entityAttributeInstance.removeModifier(CREATIVE_BLOCK_INTERACTION_RANGE_MODIFIER_UUID);
			}
		}

		EntityAttributeInstance entityAttributeInstance2 = this.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
		if (entityAttributeInstance2 != null) {
			if (this.isCreative()) {
				entityAttributeInstance2.updateModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER_UUID);
			} else {
				entityAttributeInstance2.removeModifier(CREATIVE_ENTITY_INTERACTION_RANGE_MODIFIER_UUID);
			}
		}
	}

	public void playerTick() {
		try {
			if (!this.isSpectator() || !this.isRegionUnloaded()) {
				super.tick();
			}

			for (int i = 0; i < this.getInventory().size(); i++) {
				ItemStack itemStack = this.getInventory().getStack(i);
				if (itemStack.getItem().isNetworkSynced()) {
					Packet<?> packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.getWorld(), this);
					if (packet != null) {
						this.networkHandler.sendPacket(packet);
					}
				}
			}

			if (this.getHealth() != this.syncedHealth
				|| this.syncedFoodLevel != this.hungerManager.getFoodLevel()
				|| this.hungerManager.getSaturationLevel() == 0.0F != this.syncedSaturationIsZero) {
				this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
				this.syncedHealth = this.getHealth();
				this.syncedFoodLevel = this.hungerManager.getFoodLevel();
				this.syncedSaturationIsZero = this.hungerManager.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
				this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
				this.updateScores(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.lastHealthScore));
			}

			if (this.hungerManager.getFoodLevel() != this.lastFoodScore) {
				this.lastFoodScore = this.hungerManager.getFoodLevel();
				this.updateScores(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.lastFoodScore));
			}

			if (this.getAir() != this.lastAirScore) {
				this.lastAirScore = this.getAir();
				this.updateScores(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.lastAirScore));
			}

			if (this.getArmor() != this.lastArmorScore) {
				this.lastArmorScore = this.getArmor();
				this.updateScores(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
			}

			if (this.totalExperience != this.lastExperienceScore) {
				this.lastExperienceScore = this.totalExperience;
				this.updateScores(ScoreboardCriterion.XP, MathHelper.ceil((float)this.lastExperienceScore));
			}

			if (this.experienceLevel != this.lastLevelScore) {
				this.lastLevelScore = this.experienceLevel;
				this.updateScores(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
			}

			if (this.totalExperience != this.syncedExperience) {
				this.syncedExperience = this.totalExperience;
				this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
			}

			if (this.age % 20 == 0) {
				Criteria.LOCATION.trigger(this);
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onLanding() {
		if (this.getHealth() > 0.0F && this.fallStartPos != null) {
			Criteria.FALL_FROM_HEIGHT.trigger(this, this.fallStartPos);
		}

		this.fallStartPos = null;
		super.onLanding();
	}

	public void tickFallStartPos() {
		if (this.fallDistance > 0.0F && this.fallStartPos == null) {
			this.fallStartPos = this.getPos();
			if (this.currentExplosionImpactPos != null && this.currentExplosionImpactPos.y <= this.fallStartPos.y) {
				Criteria.FALL_AFTER_EXPLOSION.trigger(this, this.currentExplosionImpactPos, this.explodedBy);
			}
		}
	}

	public void tickVehicleInLavaRiding() {
		if (this.getVehicle() != null && this.getVehicle().isInLava()) {
			if (this.vehicleInLavaRidingPos == null) {
				this.vehicleInLavaRidingPos = this.getPos();
			} else {
				Criteria.RIDE_ENTITY_IN_LAVA.trigger(this, this.vehicleInLavaRidingPos);
			}
		}

		if (this.vehicleInLavaRidingPos != null && (this.getVehicle() == null || !this.getVehicle().isInLava())) {
			this.vehicleInLavaRidingPos = null;
		}
	}

	private void updateScores(ScoreboardCriterion criterion, int score) {
		this.getScoreboard().forEachScore(criterion, this, innerScore -> innerScore.setScore(score));
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.emitGameEvent(GameEvent.ENTITY_DIE);
		boolean bl = this.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
		if (bl) {
			Text text = this.getDamageTracker().getDeathMessage();
			this.networkHandler
				.send(
					new DeathMessageS2CPacket(this.getId(), text),
					PacketCallbacks.of(
						() -> {
							int i = 256;
							String string = text.asTruncatedString(256);
							Text text2 = Text.translatable("death.attack.message_too_long", Text.literal(string).formatted(Formatting.YELLOW));
							Text text3 = Text.translatable("death.attack.even_more_magic", this.getDisplayName())
								.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
							return new DeathMessageS2CPacket(this.getId(), text3);
						}
					)
				);
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
				this.server.getPlayerManager().broadcast(text, false);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendToTeam(this, text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
				this.server.getPlayerManager().sendToOtherTeams(this, text);
			}
		} else {
			this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getId(), ScreenTexts.EMPTY));
		}

		this.dropShoulderEntities();
		if (this.getWorld().getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
			this.forgiveMobAnger();
		}

		if (!this.isSpectator()) {
			this.drop(this.getServerWorld(), damageSource);
		}

		this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this, ScoreAccess::incrementScore);
		LivingEntity livingEntity = this.getPrimeAdversary();
		if (livingEntity != null) {
			this.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
			livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
			this.onKilledBy(livingEntity);
		}

		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
		this.incrementStat(Stats.DEATHS);
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		this.extinguish();
		this.setFrozenTicks(0);
		this.setOnFire(false);
		this.getDamageTracker().update();
		this.setLastDeathPos(Optional.of(GlobalPos.create(this.getWorld().getRegistryKey(), this.getBlockPos())));
	}

	private void forgiveMobAnger() {
		Box box = new Box(this.getBlockPos()).expand(32.0, 10.0, 32.0);
		this.getWorld()
			.getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)
			.stream()
			.filter(entity -> entity instanceof Angerable)
			.forEach(entity -> ((Angerable)entity).forgive(this));
	}

	@Override
	public void updateKilledAdvancementCriterion(Entity entityKilled, int score, DamageSource damageSource) {
		if (entityKilled != this) {
			super.updateKilledAdvancementCriterion(entityKilled, score, damageSource);
			this.addScore(score);
			this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, this, ScoreAccess::incrementScore);
			if (entityKilled instanceof PlayerEntity) {
				this.incrementStat(Stats.PLAYER_KILLS);
				this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, this, ScoreAccess::incrementScore);
			} else {
				this.incrementStat(Stats.MOB_KILLS);
			}

			this.updateScoreboardScore(this, entityKilled, ScoreboardCriterion.TEAM_KILLS);
			this.updateScoreboardScore(entityKilled, this, ScoreboardCriterion.KILLED_BY_TEAMS);
			Criteria.PLAYER_KILLED_ENTITY.trigger(this, entityKilled, damageSource);
		}
	}

	private void updateScoreboardScore(ScoreHolder targetScoreHolder, ScoreHolder aboutScoreHolder, ScoreboardCriterion[] criterions) {
		Team team = this.getScoreboard().getScoreHolderTeam(aboutScoreHolder.getNameForScoreboard());
		if (team != null) {
			int i = team.getColor().getColorIndex();
			if (i >= 0 && i < criterions.length) {
				this.getScoreboard().forEachScore(criterions[i], targetScoreHolder, ScoreAccess::incrementScore);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			boolean bl = this.server.isDedicated() && this.isPvpEnabled() && source.isIn(DamageTypeTags.IS_FALL);
			if (!bl && this.joinInvulnerabilityTicks > 0 && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return false;
			} else {
				Entity entity = source.getAttacker();
				if (entity instanceof PlayerEntity playerEntity && !this.shouldDamagePlayer(playerEntity)) {
					return false;
				}

				if (entity instanceof PersistentProjectileEntity persistentProjectileEntity
					&& persistentProjectileEntity.getOwner() instanceof PlayerEntity playerEntity2
					&& !this.shouldDamagePlayer(playerEntity2)) {
					return false;
				}

				return super.damage(source, amount);
			}
		}
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity player) {
		return !this.isPvpEnabled() ? false : super.shouldDamagePlayer(player);
	}

	private boolean isPvpEnabled() {
		return this.server.isPvpEnabled();
	}

	public TeleportTarget getRespawnTarget(boolean alive) {
		BlockPos blockPos = this.getSpawnPointPosition();
		float f = this.getSpawnAngle();
		boolean bl = this.isSpawnForced();
		ServerWorld serverWorld = this.server.getWorld(this.getSpawnPointDimension());
		if (serverWorld != null && blockPos != null) {
			Optional<ServerPlayerEntity.RespawnPos> optional = findRespawnPosition(serverWorld, blockPos, f, bl, alive);
			if (optional.isPresent()) {
				ServerPlayerEntity.RespawnPos respawnPos = (ServerPlayerEntity.RespawnPos)optional.get();
				return new TeleportTarget(serverWorld, respawnPos.pos(), Vec3d.ZERO, respawnPos.yaw(), 0.0F);
			} else {
				return TeleportTarget.missingSpawnBlock(this.server.getOverworld());
			}
		} else {
			return new TeleportTarget(this.server.getOverworld());
		}
	}

	private static Optional<ServerPlayerEntity.RespawnPos> findRespawnPosition(
		ServerWorld world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive
	) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof RespawnAnchorBlock && (spawnForced || (Integer)blockState.get(RespawnAnchorBlock.CHARGES) > 0) && RespawnAnchorBlock.isNether(world)) {
			Optional<Vec3d> optional = RespawnAnchorBlock.findRespawnPosition(EntityType.PLAYER, world, pos);
			if (!spawnForced && !alive && optional.isPresent()) {
				world.setBlockState(
					pos, blockState.with(RespawnAnchorBlock.CHARGES, Integer.valueOf((Integer)blockState.get(RespawnAnchorBlock.CHARGES) - 1)), Block.NOTIFY_ALL
				);
			}

			return optional.map(respawnPos -> ServerPlayerEntity.RespawnPos.fromCurrentPos(respawnPos, pos));
		} else if (block instanceof BedBlock && BedBlock.isBedWorking(world)) {
			return BedBlock.findWakeUpPosition(EntityType.PLAYER, world, pos, blockState.get(BedBlock.FACING), spawnAngle)
				.map(respawnPos -> ServerPlayerEntity.RespawnPos.fromCurrentPos(respawnPos, pos));
		} else if (!spawnForced) {
			return Optional.empty();
		} else {
			boolean bl = block.canMobSpawnInside(blockState);
			BlockState blockState2 = world.getBlockState(pos.up());
			boolean bl2 = blockState2.getBlock().canMobSpawnInside(blockState2);
			return bl && bl2
				? Optional.of(new ServerPlayerEntity.RespawnPos(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5), spawnAngle))
				: Optional.empty();
		}
	}

	@Nullable
	@Override
	public TeleportTarget getTeleportTarget(ServerWorld destination) {
		TeleportTarget teleportTarget = super.getTeleportTarget(destination);
		if (teleportTarget != null && this.getWorld().getRegistryKey() == World.OVERWORLD && teleportTarget.newDimension().getRegistryKey() == World.END) {
			Vec3d vec3d = teleportTarget.pos().add(0.0, -1.0, 0.0);
			this.createEndSpawnPlatform(teleportTarget.newDimension(), BlockPos.ofFloored(vec3d));
			return new TeleportTarget(teleportTarget.newDimension(), vec3d, Vec3d.ZERO, 90.0F, 0.0F);
		} else {
			return this.getWorld().getRegistryKey() == World.END && teleportTarget.newDimension().getRegistryKey() == World.OVERWORLD
				? this.getRespawnTarget(false)
				: teleportTarget;
		}
	}

	public void detachForDimensionChange() {
		this.detach();
		this.getServerWorld().removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
		if (!this.notInAnyWorld) {
			this.notInAnyWorld = true;
			this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
			this.seenCredits = true;
		}
	}

	@Nullable
	@Override
	public Entity moveToWorld(Entity.TeleportTargetSupplier teleportTargetSupplier) {
		TeleportTarget teleportTarget = teleportTargetSupplier.get();
		if (teleportTarget == null) {
			return this;
		} else {
			if (teleportTarget.missingRespawnBlock()) {
				this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
			}

			ServerWorld serverWorld = teleportTarget.newDimension();
			this.inTeleportationState = true;
			ServerWorld serverWorld2 = this.getServerWorld();
			RegistryKey<World> registryKey = serverWorld2.getRegistryKey();
			WorldProperties worldProperties = serverWorld.getLevelProperties();
			this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(this.createCommonPlayerSpawnInfo(serverWorld), PlayerRespawnS2CPacket.KEEP_ALL));
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.getPlayerManager();
			playerManager.sendCommandTree(this);
			serverWorld2.removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			serverWorld2.getProfiler().push("moving");
			if (registryKey == World.OVERWORLD && serverWorld.getRegistryKey() == World.NETHER) {
				this.enteredNetherPos = this.getPos();
			}

			serverWorld2.getProfiler().pop();
			serverWorld2.getProfiler().push("placing");
			this.setServerWorld(serverWorld);
			this.networkHandler.requestTeleport(teleportTarget.pos().x, teleportTarget.pos().y, teleportTarget.pos().z, teleportTarget.yaw(), teleportTarget.pitch());
			this.networkHandler.syncWithPlayerPosition();
			serverWorld.onDimensionChanged(this);
			serverWorld2.getProfiler().pop();
			this.worldChanged(serverWorld2);
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
			playerManager.sendWorldInfo(this, serverWorld);
			playerManager.sendPlayerStatus(this);
			playerManager.sendStatusEffects(this);
			this.networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.TRAVEL_THROUGH_PORTAL, BlockPos.ORIGIN, 0, false));
			this.syncedExperience = -1;
			this.syncedHealth = -1.0F;
			this.syncedFoodLevel = -1;
			return this;
		}
	}

	private void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos) {
		BlockPos.Mutable mutable = centerPos.mutableCopy();

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -1; k < 3; k++) {
					BlockState blockState = k == -1 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
					world.setBlockState(mutable.set(centerPos).move(j, k, i), blockState);
				}
			}
		}
	}

	@Override
	protected Optional<BlockLocating.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder) {
		Optional<BlockLocating.Rectangle> optional = super.getPortalRect(destWorld, destPos, destIsNether, worldBorder);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis = (Direction.Axis)this.getWorld()
				.getBlockState(this.lastNetherPortalPosition)
				.getOrEmpty(NetherPortalBlock.AXIS)
				.orElse(Direction.Axis.X);
			Optional<BlockLocating.Rectangle> optional2 = destWorld.getPortalForcer().createPortal(destPos, axis);
			if (optional2.isEmpty()) {
				LOGGER.error("Unable to create a portal, likely target out of worldborder");
			}

			return optional2;
		}
	}

	private void worldChanged(ServerWorld origin) {
		RegistryKey<World> registryKey = origin.getRegistryKey();
		RegistryKey<World> registryKey2 = this.getWorld().getRegistryKey();
		Criteria.CHANGED_DIMENSION.trigger(this, registryKey, registryKey2);
		if (registryKey == World.NETHER && registryKey2 == World.OVERWORLD && this.enteredNetherPos != null) {
			Criteria.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
		}

		if (registryKey2 != World.NETHER) {
			this.enteredNetherPos = null;
		}
	}

	@Override
	public boolean canBeSpectated(ServerPlayerEntity spectator) {
		if (spectator.isSpectator()) {
			return this.getCameraEntity() == this;
		} else {
			return this.isSpectator() ? false : super.canBeSpectated(spectator);
		}
	}

	@Override
	public void sendPickup(Entity item, int count) {
		super.sendPickup(item, count);
		this.currentScreenHandler.sendContentUpdates();
	}

	@Override
	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
		Direction direction = this.getWorld().getBlockState(pos).get(HorizontalFacingBlock.FACING);
		if (this.isSleeping() || !this.isAlive()) {
			return Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM);
		} else if (!this.getWorld().getDimension().natural()) {
			return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE);
		} else if (!this.isBedWithinRange(pos, direction)) {
			return Either.left(PlayerEntity.SleepFailureReason.TOO_FAR_AWAY);
		} else if (this.isBedObstructed(pos, direction)) {
			return Either.left(PlayerEntity.SleepFailureReason.OBSTRUCTED);
		} else {
			this.setSpawnPoint(this.getWorld().getRegistryKey(), pos, this.getYaw(), false, true);
			if (this.getWorld().isDay()) {
				return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW);
			} else {
				if (!this.isCreative()) {
					double d = 8.0;
					double e = 5.0;
					Vec3d vec3d = Vec3d.ofBottomCenter(pos);
					List<HostileEntity> list = this.getWorld()
						.getEntitiesByClass(
							HostileEntity.class,
							new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
							entity -> entity.isAngryAt(this)
						);
					if (!list.isEmpty()) {
						return Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE);
					}
				}

				Either<PlayerEntity.SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> {
					this.incrementStat(Stats.SLEEP_IN_BED);
					Criteria.SLEPT_IN_BED.trigger(this);
				});
				if (!this.getServerWorld().isSleepingEnabled()) {
					this.sendMessage(Text.translatable("sleep.not_possible"), true);
				}

				((ServerWorld)this.getWorld()).updateSleepingPlayers();
				return either;
			}
		}
	}

	@Override
	public void sleep(BlockPos pos) {
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		super.sleep(pos);
	}

	private boolean isBedWithinRange(BlockPos pos, Direction direction) {
		return this.isBedWithinRange(pos) || this.isBedWithinRange(pos.offset(direction.getOpposite()));
	}

	private boolean isBedWithinRange(BlockPos pos) {
		Vec3d vec3d = Vec3d.ofBottomCenter(pos);
		return Math.abs(this.getX() - vec3d.getX()) <= 3.0 && Math.abs(this.getY() - vec3d.getY()) <= 2.0 && Math.abs(this.getZ() - vec3d.getZ()) <= 3.0;
	}

	private boolean isBedObstructed(BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.up();
		return !this.doesNotSuffocate(blockPos) || !this.doesNotSuffocate(blockPos.offset(direction.getOpposite()));
	}

	@Override
	public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {
		if (this.isSleeping()) {
			this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, EntityAnimationS2CPacket.WAKE_UP));
		}

		super.wakeUp(skipSleepTimer, updateSleepingPlayers);
		if (this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
		}
	}

	@Override
	public void requestTeleportAndDismount(double destX, double destY, double destZ) {
		this.dismountVehicle();
		this.setPosition(destX, destY, destZ);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.isInTeleportationState();
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}

	@Override
	protected void applyMovementEffects(ServerWorld world, BlockPos pos) {
		if (!this.isSpectator()) {
			super.applyMovementEffects(world, pos);
		}
	}

	public void handleFall(double xDifference, double yDifference, double zDifference, boolean onGround) {
		if (!this.isRegionUnloaded()) {
			this.updateSupportingBlockPos(onGround, new Vec3d(xDifference, yDifference, zDifference));
			BlockPos blockPos = this.getLandingPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			if (this.spawnExtraParticlesOnFall && onGround && this.fallDistance > 0.0F) {
				Vec3d vec3d = blockPos.toCenterPos().add(0.0, 0.5, 0.0);
				int i = (int)(50.0F * this.fallDistance);
				this.getServerWorld().spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, i, 0.3F, 0.3F, 0.3F, 0.15F);
				this.spawnExtraParticlesOnFall = false;
			}

			super.fall(yDifference, onGround, blockState, blockPos);
		}
	}

	@Override
	public void onExplodedBy(@Nullable Entity entity) {
		super.onExplodedBy(entity);
		this.currentExplosionImpactPos = this.getPos();
		this.explodedBy = entity;
		this.ignoreFallDamageFromCurrentExplosion = this.ignoreFallDamageFromCurrentExplosion || entity != null && entity.getType() == EntityType.WIND_CHARGE;
	}

	@Override
	protected void tickCramming() {
		if (this.getWorld().getTickManager().shouldTick()) {
			super.tickCramming();
		}
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign, boolean front) {
		this.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.getWorld(), sign.getPos()));
		this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(sign.getPos(), front));
	}

	private void incrementScreenHandlerSyncId() {
		this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
	}

	@Override
	public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
		if (factory == null) {
			return OptionalInt.empty();
		} else {
			if (this.currentScreenHandler != this.playerScreenHandler) {
				this.closeHandledScreen();
			}

			this.incrementScreenHandlerSyncId();
			ScreenHandler screenHandler = factory.createMenu(this.screenHandlerSyncId, this.getInventory(), this);
			if (screenHandler == null) {
				if (this.isSpectator()) {
					this.sendMessage(Text.translatable("container.spectatorCantOpen").formatted(Formatting.RED), true);
				}

				return OptionalInt.empty();
			} else {
				this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
				this.onScreenHandlerOpened(screenHandler);
				this.currentScreenHandler = screenHandler;
				return OptionalInt.of(this.screenHandlerSyncId);
			}
		}
	}

	@Override
	public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
	}

	@Override
	public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) {
		if (this.currentScreenHandler != this.playerScreenHandler) {
			this.closeHandledScreen();
		}

		this.incrementScreenHandlerSyncId();
		this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, inventory.size(), horse.getId()));
		this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.getInventory(), inventory, horse);
		this.onScreenHandlerOpened(this.currentScreenHandler);
	}

	@Override
	public void useBook(ItemStack book, Hand hand) {
		if (book.isOf(Items.WRITTEN_BOOK)) {
			if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
				this.currentScreenHandler.sendContentUpdates();
			}

			this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
		}
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
		this.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.create(commandBlock, BlockEntity::createComponentlessNbt));
	}

	@Override
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
		this.onHandledScreenClosed();
	}

	@Override
	public void onHandledScreenClosed() {
		this.currentScreenHandler.onClosed(this);
		this.playerScreenHandler.copySharedSlots(this.currentScreenHandler);
		this.currentScreenHandler = this.playerScreenHandler;
	}

	public void updateInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
		if (this.hasVehicle()) {
			if (sidewaysSpeed >= -1.0F && sidewaysSpeed <= 1.0F) {
				this.sidewaysSpeed = sidewaysSpeed;
			}

			if (forwardSpeed >= -1.0F && forwardSpeed <= 1.0F) {
				this.forwardSpeed = forwardSpeed;
			}

			this.jumping = jumping;
			this.setSneaking(sneaking);
		}
	}

	@Override
	public void travel(Vec3d movementInput) {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.travel(movementInput);
		this.increaseTravelMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
	}

	@Override
	public void tickRiding() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		super.tickRiding();
		this.increaseRidingMotionStats(this.getX() - d, this.getY() - e, this.getZ() - f);
	}

	public void increaseTravelMotionStats(double deltaX, double deltaY, double deltaZ) {
		if (!this.hasVehicle() && !isZero(deltaX, deltaY, deltaZ)) {
			if (this.isSwimming()) {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.SWIM_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isSubmergedIn(FluidTags.WATER)) {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_UNDER_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isTouchingWater()) {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
				if (i > 0) {
					this.increaseStat(Stats.WALK_ON_WATER_ONE_CM, i);
					this.addExhaustion(0.01F * (float)i * 0.01F);
				}
			} else if (this.isClimbing()) {
				if (deltaY > 0.0) {
					this.increaseStat(Stats.CLIMB_ONE_CM, (int)Math.round(deltaY * 100.0));
				}
			} else if (this.isOnGround()) {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
				if (i > 0) {
					if (this.isSprinting()) {
						this.increaseStat(Stats.SPRINT_ONE_CM, i);
						this.addExhaustion(0.1F * (float)i * 0.01F);
					} else if (this.isInSneakingPose()) {
						this.increaseStat(Stats.CROUCH_ONE_CM, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					} else {
						this.increaseStat(Stats.WALK_ONE_CM, i);
						this.addExhaustion(0.0F * (float)i * 0.01F);
					}
				}
			} else if (this.isFallFlying()) {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
				this.increaseStat(Stats.AVIATE_ONE_CM, i);
			} else {
				int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 100.0F);
				if (i > 25) {
					this.increaseStat(Stats.FLY_ONE_CM, i);
				}
			}
		}
	}

	private void increaseRidingMotionStats(double deltaX, double deltaY, double deltaZ) {
		if (this.hasVehicle() && !isZero(deltaX, deltaY, deltaZ)) {
			int i = Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0F);
			Entity entity = this.getVehicle();
			if (entity instanceof AbstractMinecartEntity) {
				this.increaseStat(Stats.MINECART_ONE_CM, i);
			} else if (entity instanceof BoatEntity) {
				this.increaseStat(Stats.BOAT_ONE_CM, i);
			} else if (entity instanceof PigEntity) {
				this.increaseStat(Stats.PIG_ONE_CM, i);
			} else if (entity instanceof AbstractHorseEntity) {
				this.increaseStat(Stats.HORSE_ONE_CM, i);
			} else if (entity instanceof StriderEntity) {
				this.increaseStat(Stats.STRIDER_ONE_CM, i);
			}
		}
	}

	private static boolean isZero(double deltaX, double deltaY, double deltaZ) {
		return deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0;
	}

	@Override
	public void increaseStat(Stat<?> stat, int amount) {
		this.statHandler.increaseStat(this, stat, amount);
		this.getScoreboard().forEachScore(stat, this, score -> score.incrementScore(amount));
	}

	@Override
	public void resetStat(Stat<?> stat) {
		this.statHandler.setStat(this, stat, 0);
		this.getScoreboard().forEachScore(stat, this, ScoreAccess::resetScore);
	}

	@Override
	public int unlockRecipes(Collection<RecipeEntry<?>> recipes) {
		return this.recipeBook.unlockRecipes(recipes, this);
	}

	@Override
	public void onRecipeCrafted(RecipeEntry<?> recipe, List<ItemStack> ingredients) {
		Criteria.RECIPE_CRAFTED.trigger(this, recipe.id(), ingredients);
	}

	@Override
	public void unlockRecipes(List<Identifier> recipes) {
		List<RecipeEntry<?>> list = (List<RecipeEntry<?>>)recipes.stream()
			.flatMap(recipe -> this.server.getRecipeManager().get(recipe).stream())
			.collect(Collectors.toList());
		this.unlockRecipes(list);
	}

	@Override
	public int lockRecipes(Collection<RecipeEntry<?>> recipes) {
		return this.recipeBook.lockRecipes(recipes, this);
	}

	@Override
	public void addExperience(int experience) {
		super.addExperience(experience);
		this.syncedExperience = -1;
	}

	public void onDisconnect() {
		this.disconnected = true;
		this.removeAllPassengers();
		if (this.isSleeping()) {
			this.wakeUp(true, false);
		}
	}

	public boolean isDisconnected() {
		return this.disconnected;
	}

	public void markHealthDirty() {
		this.syncedHealth = -1.0E8F;
	}

	@Override
	public void sendMessage(Text message, boolean overlay) {
		this.sendMessageToClient(message, overlay);
	}

	@Override
	protected void consumeItem() {
		if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
			this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, EntityStatuses.CONSUME_ITEM));
			super.consumeItem();
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
		super.lookAt(anchorPoint, target);
		this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, target.x, target.y, target.z));
	}

	public void lookAtEntity(EntityAnchorArgumentType.EntityAnchor anchorPoint, Entity targetEntity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
		Vec3d vec3d = targetAnchor.positionAt(targetEntity);
		super.lookAt(anchorPoint, vec3d);
		this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, targetEntity, targetAnchor));
	}

	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
		this.sculkShriekerWarningManager = oldPlayer.sculkShriekerWarningManager;
		this.session = oldPlayer.session;
		this.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.getPreviousGameMode());
		this.sendAbilitiesUpdate();
		this.getAttributes().setBaseFrom(oldPlayer.getAttributes());
		if (alive) {
			this.getInventory().clone(oldPlayer.getInventory());
			this.setHealth(oldPlayer.getHealth());
			this.hungerManager = oldPlayer.hungerManager;

			for (StatusEffectInstance statusEffectInstance : oldPlayer.getStatusEffects()) {
				this.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
			}

			this.experienceLevel = oldPlayer.experienceLevel;
			this.totalExperience = oldPlayer.totalExperience;
			this.experienceProgress = oldPlayer.experienceProgress;
			this.setScore(oldPlayer.getScore());
			this.lastNetherPortalPosition = oldPlayer.lastNetherPortalPosition;
		} else if (this.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
			this.getInventory().clone(oldPlayer.getInventory());
			this.experienceLevel = oldPlayer.experienceLevel;
			this.totalExperience = oldPlayer.totalExperience;
			this.experienceProgress = oldPlayer.experienceProgress;
			this.setScore(oldPlayer.getScore());
		}

		this.enchantmentTableSeed = oldPlayer.enchantmentTableSeed;
		this.enderChestInventory = oldPlayer.enderChestInventory;
		this.getDataTracker().set(PLAYER_MODEL_PARTS, oldPlayer.getDataTracker().get(PLAYER_MODEL_PARTS));
		this.syncedExperience = -1;
		this.syncedHealth = -1.0F;
		this.syncedFoodLevel = -1;
		this.recipeBook.copyFrom(oldPlayer.recipeBook);
		this.seenCredits = oldPlayer.seenCredits;
		this.enteredNetherPos = oldPlayer.enteredNetherPos;
		this.chunkFilter = oldPlayer.chunkFilter;
		this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
		this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
		this.setLastDeathPos(oldPlayer.getLastDeathPos());
	}

	@Override
	protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
		super.onStatusEffectApplied(effect, source);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, true));
		if (effect.equals(StatusEffects.LEVITATION)) {
			this.levitationStartTick = this.age;
			this.levitationStartPos = this.getPos();
		}

		Criteria.EFFECTS_CHANGED.trigger(this, source);
	}

	@Override
	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
		super.onStatusEffectUpgraded(effect, reapplyEffect, source);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, false));
		Criteria.EFFECTS_CHANGED.trigger(this, source);
	}

	@Override
	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		super.onStatusEffectRemoved(effect);
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
		if (effect.equals(StatusEffects.LEVITATION)) {
			this.levitationStartPos = null;
		}

		Criteria.EFFECTS_CHANGED.trigger(this, null);
	}

	@Override
	public void requestTeleport(double destX, double destY, double destZ) {
		this.networkHandler.requestTeleport(destX, destY, destZ, this.getYaw(), this.getPitch(), PositionFlag.ROT);
	}

	@Override
	public void requestTeleportOffset(double offsetX, double offsetY, double offsetZ) {
		this.networkHandler.requestTeleport(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, this.getYaw(), this.getPitch(), PositionFlag.VALUES);
	}

	@Override
	public boolean teleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch) {
		ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(destX, destY, destZ));
		world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, this.getId());
		this.stopRiding();
		if (this.isSleeping()) {
			this.wakeUp(true, true);
		}

		if (world == this.getWorld()) {
			this.networkHandler.requestTeleport(destX, destY, destZ, yaw, pitch, flags);
		} else {
			this.teleport(world, destX, destY, destZ, yaw, pitch);
		}

		this.setHeadYaw(yaw);
		return true;
	}

	@Override
	public void refreshPositionAfterTeleport(double x, double y, double z) {
		super.refreshPositionAfterTeleport(x, y, z);
		this.networkHandler.syncWithPlayerPosition();
	}

	@Override
	public void addCritParticles(Entity target) {
		this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, EntityAnimationS2CPacket.CRIT));
	}

	@Override
	public void addEnchantedHitParticles(Entity target) {
		this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, EntityAnimationS2CPacket.ENCHANTED_HIT));
	}

	@Override
	public void sendAbilitiesUpdate() {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.getWorld();
	}

	public boolean changeGameMode(GameMode gameMode) {
		boolean bl = this.isSpectator();
		if (!this.interactionManager.changeGameMode(gameMode)) {
			return false;
		} else {
			this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, (float)gameMode.getId()));
			if (gameMode == GameMode.SPECTATOR) {
				this.dropShoulderEntities();
				this.stopRiding();
				EnchantmentHelper.removeLocationBasedEffects(this);
			} else {
				this.setCameraEntity(this);
				if (bl) {
					EnchantmentHelper.applyLocationBasedEffects(this.getServerWorld(), this);
				}
			}

			this.sendAbilitiesUpdate();
			this.markEffectsDirty();
			return true;
		}
	}

	@Override
	public boolean isSpectator() {
		return this.interactionManager.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		return this.interactionManager.getGameMode() == GameMode.CREATIVE;
	}

	@Override
	public void sendMessage(Text message) {
		this.sendMessageToClient(message, false);
	}

	public void sendMessageToClient(Text message, boolean overlay) {
		if (this.acceptsMessage(overlay)) {
			this.networkHandler.send(new GameMessageS2CPacket(message, overlay), PacketCallbacks.of(() -> {
				if (this.acceptsMessage(false)) {
					int i = 256;
					String string = message.asTruncatedString(256);
					Text text2 = Text.literal(string).formatted(Formatting.YELLOW);
					return new GameMessageS2CPacket(Text.translatable("multiplayer.message_not_delivered", text2).formatted(Formatting.RED), false);
				} else {
					return null;
				}
			}));
		}
	}

	/**
	 * Sends a chat message to the player.
	 * 
	 * <p>Chat messages have signatures. It is possible to use a bogus signature - such as
	 * {@link net.minecraft.network.message.SignedMessage#ofUnsigned} - to send a chat
	 * message; however if the signature is invalid (e.g. because the text's content differs
	 * from the one sent by the client, or because the passed signature is invalid) the client
	 * will show a warning and can discard it depending on the client's options.
	 * 
	 * @see #sendMessage(Text)
	 * @see #sendMessage(Text, boolean)
	 */
	public void sendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params) {
		if (this.acceptsChatMessage()) {
			message.send(this, filterMaskEnabled, params);
		}
	}

	public String getIp() {
		return this.networkHandler.getConnectionAddress() instanceof InetSocketAddress inetSocketAddress
			? InetAddresses.toAddrString(inetSocketAddress.getAddress())
			: "<unknown>";
	}

	public void setClientOptions(SyncedClientOptions clientOptions) {
		this.language = clientOptions.language();
		this.viewDistance = clientOptions.viewDistance();
		this.clientChatVisibility = clientOptions.chatVisibility();
		this.clientChatColorsEnabled = clientOptions.chatColorsEnabled();
		this.filterText = clientOptions.filtersText();
		this.allowServerListing = clientOptions.allowsServerListing();
		this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)clientOptions.playerModelParts());
		this.getDataTracker().set(MAIN_ARM, (byte)clientOptions.mainArm().getId());
	}

	public SyncedClientOptions getClientOptions() {
		int i = this.getDataTracker().get(PLAYER_MODEL_PARTS);
		Arm arm = (Arm)Arm.BY_ID.apply(this.getDataTracker().get(MAIN_ARM));
		return new SyncedClientOptions(
			this.language, this.viewDistance, this.clientChatVisibility, this.clientChatColorsEnabled, i, arm, this.filterText, this.allowServerListing
		);
	}

	public boolean areClientChatColorsEnabled() {
		return this.clientChatColorsEnabled;
	}

	public ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	private boolean acceptsMessage(boolean overlay) {
		return this.clientChatVisibility == ChatVisibility.HIDDEN ? overlay : true;
	}

	private boolean acceptsChatMessage() {
		return this.clientChatVisibility == ChatVisibility.FULL;
	}

	public int getViewDistance() {
		return this.viewDistance;
	}

	public void sendServerMetadata(ServerMetadata metadata) {
		this.networkHandler.sendPacket(new ServerMetadataS2CPacket(metadata.description(), metadata.favicon().map(ServerMetadata.Favicon::iconBytes)));
	}

	@Override
	protected int getPermissionLevel() {
		return this.server.getPermissionLevel(this.getGameProfile());
	}

	public void updateLastActionTime() {
		this.lastActionTime = Util.getMeasuringTimeMs();
	}

	public ServerStatHandler getStatHandler() {
		return this.statHandler;
	}

	public ServerRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	@Override
	protected void updatePotionVisibility() {
		if (this.isSpectator()) {
			this.clearPotionSwirls();
			this.setInvisible(true);
		} else {
			super.updatePotionVisibility();
		}
	}

	public Entity getCameraEntity() {
		return (Entity)(this.cameraEntity == null ? this : this.cameraEntity);
	}

	public void setCameraEntity(@Nullable Entity entity) {
		Entity entity2 = this.getCameraEntity();
		this.cameraEntity = (Entity)(entity == null ? this : entity);
		if (entity2 != this.cameraEntity) {
			if (this.cameraEntity.getWorld() instanceof ServerWorld serverWorld) {
				this.teleport(serverWorld, this.cameraEntity.getX(), this.cameraEntity.getY(), this.cameraEntity.getZ(), Set.of(), this.getYaw(), this.getPitch());
			}

			if (entity != null) {
				this.getServerWorld().getChunkManager().updatePosition(this);
			}

			this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
			this.networkHandler.syncWithPlayerPosition();
		}
	}

	@Override
	protected void tickPortalCooldown() {
		if (!this.inTeleportationState) {
			super.tickPortalCooldown();
		}
	}

	@Override
	public void attack(Entity target) {
		if (this.interactionManager.getGameMode() == GameMode.SPECTATOR) {
			this.setCameraEntity(target);
		} else {
			super.attack(target);
		}
	}

	public long getLastActionTime() {
		return this.lastActionTime;
	}

	@Nullable
	public Text getPlayerListName() {
		return null;
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.resetLastAttackedTicks();
	}

	public boolean isInTeleportationState() {
		return this.inTeleportationState;
	}

	public void onTeleportationDone() {
		this.inTeleportationState = false;
	}

	public PlayerAdvancementTracker getAdvancementTracker() {
		return this.advancementTracker;
	}

	public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
		this.setCameraEntity(this);
		this.stopRiding();
		if (targetWorld == this.getWorld()) {
			this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
		} else {
			this.moveToWorld(() -> new TeleportTarget(targetWorld, new Vec3d(x, y, z), Vec3d.ZERO, yaw, pitch));
		}
	}

	@Nullable
	public BlockPos getSpawnPointPosition() {
		return this.spawnPointPosition;
	}

	public float getSpawnAngle() {
		return this.spawnAngle;
	}

	public RegistryKey<World> getSpawnPointDimension() {
		return this.spawnPointDimension;
	}

	public boolean isSpawnForced() {
		return this.spawnForced;
	}

	public void setSpawnPointFrom(ServerPlayerEntity player) {
		this.setSpawnPoint(player.getSpawnPointDimension(), player.getSpawnPointPosition(), player.getSpawnAngle(), player.isSpawnForced(), false);
	}

	/**
	 * Sets the player's spawn point.
	 * 
	 * @param sendMessage if {@code true}, a game message about the spawn point change will be sent
	 * @param forced whether the new spawn point is {@linkplain #isSpawnForced() forced}
	 * @param pos the new spawn point, or {@code null} if resetting to the world spawn
	 * @param dimension the new spawn dimension
	 */
	public void setSpawnPoint(RegistryKey<World> dimension, @Nullable BlockPos pos, float angle, boolean forced, boolean sendMessage) {
		if (pos != null) {
			boolean bl = pos.equals(this.spawnPointPosition) && dimension.equals(this.spawnPointDimension);
			if (sendMessage && !bl) {
				this.sendMessage(Text.translatable("block.minecraft.set_spawn"));
			}

			this.spawnPointPosition = pos;
			this.spawnPointDimension = dimension;
			this.spawnAngle = angle;
			this.spawnForced = forced;
		} else {
			this.spawnPointPosition = null;
			this.spawnPointDimension = World.OVERWORLD;
			this.spawnAngle = 0.0F;
			this.spawnForced = false;
		}
	}

	/**
	 * Returns the chunk section position the player's client is currently watching
	 * from. This may differ from the chunk section the player is currently in.
	 * 
	 * <p>This is only for chunk loading (watching) purpose. This is updated together
	 * with entity tracking, but they are separate mechanisms.
	 * 
	 * @see #watchedSection
	 * @see #setWatchedSection(ChunkSectionPos)
	 */
	public ChunkSectionPos getWatchedSection() {
		return this.watchedSection;
	}

	/**
	 * Sets the chunk section position the player's client is currently watching
	 * from. This is usually called when the player moves to a new chunk section.
	 * 
	 * @see #watchedSection
	 * @see #getWatchedSection()
	 * 
	 * @param section the updated section position
	 */
	public void setWatchedSection(ChunkSectionPos section) {
		this.watchedSection = section;
	}

	public ChunkFilter getChunkFilter() {
		return this.chunkFilter;
	}

	public void setChunkFilter(ChunkFilter chunkFilter) {
		this.chunkFilter = chunkFilter;
	}

	@Override
	public void playSoundToPlayer(SoundEvent sound, SoundCategory category, float volume, float pitch) {
		this.networkHandler
			.sendPacket(
				new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(sound), category, this.getX(), this.getY(), this.getZ(), volume, pitch, this.random.nextLong())
			);
	}

	@Override
	public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
		ItemEntity itemEntity = super.dropItem(stack, throwRandomly, retainOwnership);
		if (itemEntity == null) {
			return null;
		} else {
			this.getWorld().spawnEntity(itemEntity);
			ItemStack itemStack = itemEntity.getStack();
			if (retainOwnership) {
				if (!itemStack.isEmpty()) {
					this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
				}

				this.incrementStat(Stats.DROP);
			}

			return itemEntity;
		}
	}

	public TextStream getTextStream() {
		return this.textStream;
	}

	public void setServerWorld(ServerWorld world) {
		this.setWorld(world);
		this.interactionManager.setWorld(world);
	}

	@Nullable
	private static GameMode gameModeFromNbt(@Nullable NbtCompound nbt, String key) {
		return nbt != null && nbt.contains(key, NbtElement.NUMBER_TYPE) ? GameMode.byId(nbt.getInt(key)) : null;
	}

	/**
	 * Returns the server game mode the player should be set to, namely the forced game mode.
	 * 
	 * <p>If the forced game mode is not set, returns the {@code backupGameMode} if not {@code null},
	 * or the server's default game mode otherwise.
	 * 
	 * @see MinecraftServer#getForcedGameMode
	 */
	private GameMode getServerGameMode(@Nullable GameMode backupGameMode) {
		GameMode gameMode = this.server.getForcedGameMode();
		if (gameMode != null) {
			return gameMode;
		} else {
			return backupGameMode != null ? backupGameMode : this.server.getDefaultGameMode();
		}
	}

	public void readGameModeNbt(@Nullable NbtCompound nbt) {
		this.interactionManager.setGameMode(this.getServerGameMode(gameModeFromNbt(nbt, "playerGameType")), gameModeFromNbt(nbt, "previousPlayerGameType"));
	}

	private void writeGameModeNbt(NbtCompound nbt) {
		nbt.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		GameMode gameMode = this.interactionManager.getPreviousGameMode();
		if (gameMode != null) {
			nbt.putInt("previousPlayerGameType", gameMode.getId());
		}
	}

	@Override
	public boolean shouldFilterText() {
		return this.filterText;
	}

	public boolean shouldFilterMessagesSentTo(ServerPlayerEntity player) {
		return player == this ? false : this.filterText || player.filterText;
	}

	@Override
	public boolean canModifyAt(World world, BlockPos pos) {
		return super.canModifyAt(world, pos) && world.canPlayerModifyAt(this, pos);
	}

	@Override
	protected void tickItemStackUsage(ItemStack stack) {
		Criteria.USING_ITEM.trigger(this, stack);
		super.tickItemStackUsage(stack);
	}

	public boolean dropSelectedItem(boolean entireStack) {
		PlayerInventory playerInventory = this.getInventory();
		ItemStack itemStack = playerInventory.dropSelectedItem(entireStack);
		this.currentScreenHandler
			.getSlotIndex(playerInventory, playerInventory.selectedSlot)
			.ifPresent(index -> this.currentScreenHandler.setPreviousTrackedSlot(index, playerInventory.getMainHandStack()));
		return this.dropItem(itemStack, false, true) != null;
	}

	public boolean allowsServerListing() {
		return this.allowServerListing;
	}

	@Override
	public Optional<SculkShriekerWarningManager> getSculkShriekerWarningManager() {
		return Optional.of(this.sculkShriekerWarningManager);
	}

	public void setSpawnExtraParticlesOnFall(boolean spawnExtraParticlesOnFall) {
		this.spawnExtraParticlesOnFall = spawnExtraParticlesOnFall;
	}

	@Override
	public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
		super.triggerItemPickedUpByEntityCriteria(item);
		Entity entity = item.getOwner();
		if (entity != null) {
			Criteria.THROWN_ITEM_PICKED_UP_BY_PLAYER.trigger(this, item.getStack(), entity);
		}
	}

	public void setSession(PublicPlayerSession session) {
		this.session = session;
	}

	@Nullable
	public PublicPlayerSession getSession() {
		return this.session != null && this.session.isKeyExpired() ? null : this.session;
	}

	@Override
	public void tiltScreen(double deltaX, double deltaZ) {
		this.damageTiltYaw = (float)(MathHelper.atan2(deltaZ, deltaX) * 180.0F / (float)Math.PI - (double)this.getYaw());
		this.networkHandler.sendPacket(new DamageTiltS2CPacket(this));
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		if (super.startRiding(entity, force)) {
			entity.updatePassengerPosition(this);
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
			if (entity instanceof LivingEntity livingEntity) {
				this.server.getPlayerManager().sendStatusEffects(livingEntity, this.networkHandler);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity instanceof LivingEntity livingEntity) {
			for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffects()) {
				this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(entity.getId(), statusEffectInstance.getEffectType()));
			}
		}
	}

	public CommonPlayerSpawnInfo createCommonPlayerSpawnInfo(ServerWorld world) {
		return new CommonPlayerSpawnInfo(
			world.getDimensionEntry(),
			world.getRegistryKey(),
			BiomeAccess.hashSeed(world.getSeed()),
			this.interactionManager.getGameMode(),
			this.interactionManager.getPreviousGameMode(),
			world.isDebugWorld(),
			world.isFlat(),
			this.getLastDeathPos(),
			this.getPortalCooldown()
		);
	}

	public void setStartRaidPos(BlockPos startRaidPos) {
		this.startRaidPos = startRaidPos;
	}

	public void clearStartRaidPos() {
		this.startRaidPos = null;
	}

	@Nullable
	public BlockPos getStartRaidPos() {
		return this.startRaidPos;
	}

	@Override
	public Vec3d getMovement() {
		return this.movement;
	}

	public void setOnGround(Vec3d movement) {
		this.movement = movement;
	}

	@Override
	protected float getDamageAgainst(Entity target, float baseDamage, DamageSource damageSource) {
		return EnchantmentHelper.getDamage(this.getServerWorld(), this.getMainHandStack(), target, damageSource, baseDamage);
	}

	@Override
	public void sendEquipmentBreakStatus(Item item, EquipmentSlot slot) {
		super.sendEquipmentBreakStatus(item, slot);
		this.incrementStat(Stats.BROKEN.getOrCreateStat(item));
	}

	static record RespawnPos(Vec3d pos, float yaw) {
		public static ServerPlayerEntity.RespawnPos fromCurrentPos(Vec3d respawnPos, BlockPos currentPos) {
			return new ServerPlayerEntity.RespawnPos(respawnPos, getYaw(respawnPos, currentPos));
		}

		private static float getYaw(Vec3d respawnPos, BlockPos currentPos) {
			Vec3d vec3d = Vec3d.ofBottomCenter(currentPos).subtract(respawnPos).normalize();
			return (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d.z, vec3d.x) * 180.0F / (float)Math.PI - 90.0);
		}
	}
}
