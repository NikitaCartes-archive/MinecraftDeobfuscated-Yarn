package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.sound.AbstractBeeSoundInstance;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.SnifferDigSoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBreezeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventListenersCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGoalSelectorCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugHiveCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugNeighborsUpdateCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPathCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiAddedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiRemovedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiTicketCountCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugRaidsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugStructuresCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugVillageSectionsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugWorldgenAttemptCustomPayload;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPaket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.ServerLinks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.tick.TickManager;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler extends ClientCommonNetworkHandler implements ClientPlayPacketListener, TickablePacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text UNSECURE_SERVER_TOAST_TITLE = Text.translatable("multiplayer.unsecureserver.toast.title");
	private static final Text UNSECURE_SERVER_TOAST_TEXT = Text.translatable("multiplayer.unsecureserver.toast");
	private static final Text INVALID_PACKET_TEXT = Text.translatable("multiplayer.disconnect.invalid_packet");
	private static final Text RECONFIGURING_TEXT = Text.translatable("connect.reconfiguring");
	private static final int ACKNOWLEDGMENT_BATCH_SIZE = 64;
	public static final int field_54852 = 64;
	private final GameProfile profile;
	private ClientWorld world;
	private ClientWorld.Properties worldProperties;
	private final Map<UUID, PlayerListEntry> playerListEntries = Maps.<UUID, PlayerListEntry>newHashMap();
	private final Set<PlayerListEntry> listedPlayerListEntries = new ReferenceOpenHashSet<>();
	private final ClientAdvancementManager advancementHandler;
	private final ClientCommandSource commandSource;
	private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
	private int chunkLoadDistance = 3;
	private int simulationDistance = 3;
	private final Random random = Random.createThreadSafe();
	private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
	private ClientRecipeManager recipeManager = new ClientRecipeManager(Map.of(), CuttingRecipeDisplay.Grouping.empty());
	private final UUID sessionId = UUID.randomUUID();
	private Set<RegistryKey<World>> worldKeys;
	private final DynamicRegistryManager.Immutable combinedDynamicRegistries;
	private final FeatureSet enabledFeatures;
	private final BrewingRecipeRegistry brewingRecipeRegistry;
	private FuelRegistry fuelRegistry;
	private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
	@Nullable
	private ClientPlayerSession session;
	private MessageChain.Packer messagePacker = MessageChain.Packer.NONE;
	private LastSeenMessagesCollector lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
	private MessageSignatureStorage signatureStorage = MessageSignatureStorage.create();
	@Nullable
	private CompletableFuture<Optional<PlayerKeyPair>> profileKeyPairFuture;
	@Nullable
	private SyncedClientOptions syncedOptions;
	private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
	private final PingMeasurer pingMeasurer;
	private final DebugSampleSubscriber debugSampleSubscriber;
	@Nullable
	private WorldLoadingState worldLoadingState;
	private boolean secureChatEnforced;
	private boolean displayedUnsecureChatWarning = false;
	private volatile boolean worldCleared;
	private final Scoreboard scoreboard = new Scoreboard();
	private final SearchManager searchManager = new SearchManager();

	public ClientPlayNetworkHandler(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
		super(client, clientConnection, clientConnectionState);
		this.profile = clientConnectionState.localGameProfile();
		this.combinedDynamicRegistries = clientConnectionState.receivedRegistries();
		this.enabledFeatures = clientConnectionState.enabledFeatures();
		this.advancementHandler = new ClientAdvancementManager(client, this.worldSession);
		this.commandSource = new ClientCommandSource(this, client);
		this.pingMeasurer = new PingMeasurer(this, client.getDebugHud().getPingLog());
		this.debugSampleSubscriber = new DebugSampleSubscriber(this, client.getDebugHud());
		if (clientConnectionState.chatState() != null) {
			client.inGameHud.getChatHud().restoreChatState(clientConnectionState.chatState());
		}

		this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.enabledFeatures);
		this.fuelRegistry = FuelRegistry.createDefault(clientConnectionState.receivedRegistries(), this.enabledFeatures);
	}

	public ClientCommandSource getCommandSource() {
		return this.commandSource;
	}

	public void unloadWorld() {
		this.worldCleared = true;
		this.clearWorld();
		this.worldSession.onUnload();
	}

	public void clearWorld() {
		this.world = null;
		this.worldLoadingState = null;
	}

	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	@Override
	public void onGameJoin(GameJoinS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
		List<RegistryKey<World>> list = Lists.<RegistryKey<World>>newArrayList(packet.dimensionIds());
		Collections.shuffle(list);
		this.worldKeys = Sets.<RegistryKey<World>>newLinkedHashSet(list);
		RegistryKey<World> registryKey = commonPlayerSpawnInfo.dimension();
		RegistryEntry<DimensionType> registryEntry = commonPlayerSpawnInfo.dimensionType();
		this.chunkLoadDistance = packet.viewDistance();
		this.simulationDistance = packet.simulationDistance();
		boolean bl = commonPlayerSpawnInfo.isDebug();
		boolean bl2 = commonPlayerSpawnInfo.isFlat();
		int i = commonPlayerSpawnInfo.seaLevel();
		ClientWorld.Properties properties = new ClientWorld.Properties(Difficulty.NORMAL, packet.hardcore(), bl2);
		this.worldProperties = properties;
		this.world = new ClientWorld(
			this,
			properties,
			registryKey,
			registryEntry,
			this.chunkLoadDistance,
			this.simulationDistance,
			this.client.worldRenderer,
			bl,
			commonPlayerSpawnInfo.seed(),
			i
		);
		this.client.joinWorld(this.world, DownloadingTerrainScreen.WorldEntryReason.OTHER);
		if (this.client.player == null) {
			this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook());
			this.client.player.setYaw(-180.0F);
			if (this.client.getServer() != null) {
				this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
			}
		}

		this.client.debugRenderer.reset();
		this.client.player.init();
		this.client.player.setId(packet.playerEntityId());
		this.world.addEntity(this.client.player);
		this.client.player.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(this.client.player);
		this.client.cameraEntity = this.client.player;
		this.startWorldLoading(this.client.player, this.world, DownloadingTerrainScreen.WorldEntryReason.OTHER);
		this.client.player.setReducedDebugInfo(packet.reducedDebugInfo());
		this.client.player.setShowsDeathScreen(packet.showDeathScreen());
		this.client.player.setLimitedCraftingEnabled(packet.doLimitedCrafting());
		this.client.player.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
		this.client.player.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
		this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.prevGameMode());
		this.client.options.setServerViewDistance(packet.viewDistance());
		this.session = null;
		this.lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
		this.signatureStorage = MessageSignatureStorage.create();
		if (this.connection.isEncrypted()) {
			this.fetchProfileKey();
		}

		this.worldSession.setGameMode(commonPlayerSpawnInfo.gameMode(), packet.hardcore());
		this.client.getQuickPlayLogger().save(this.client);
		this.secureChatEnforced = packet.enforcesSecureChat();
		if (this.serverInfo != null && !this.displayedUnsecureChatWarning && !this.isSecureChatEnforced()) {
			SystemToast systemToast = SystemToast.create(this.client, SystemToast.Type.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSECURE_SERVER_TOAST_TEXT);
			this.client.getToastManager().add(systemToast);
			this.displayedUnsecureChatWarning = true;
		}
	}

	@Override
	public void onEntitySpawn(EntitySpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getEntityId()) {
			this.removedPlayerVehicleId = OptionalInt.empty();
		}

		Entity entity = this.createEntity(packet);
		if (entity != null) {
			entity.onSpawnPacket(packet);
			this.world.addEntity(entity);
			this.playSpawnSound(entity);
		} else {
			LOGGER.warn("Skipping Entity with id {}", packet.getEntityType());
		}
	}

	@Nullable
	private Entity createEntity(EntitySpawnS2CPacket packet) {
		EntityType<?> entityType = packet.getEntityType();
		if (entityType == EntityType.PLAYER) {
			PlayerListEntry playerListEntry = this.getPlayerListEntry(packet.getUuid());
			if (playerListEntry == null) {
				LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", packet.getUuid());
				return null;
			} else {
				return new OtherClientPlayerEntity(this.world, playerListEntry.getProfile());
			}
		} else {
			return entityType.create(this.world, SpawnReason.LOAD);
		}
	}

	private void playSpawnSound(Entity entity) {
		if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
			this.client.getSoundManager().play(new MovingMinecartSoundInstance(abstractMinecartEntity));
		} else if (entity instanceof BeeEntity beeEntity) {
			boolean bl = beeEntity.hasAngerTime();
			AbstractBeeSoundInstance abstractBeeSoundInstance;
			if (bl) {
				abstractBeeSoundInstance = new AggressiveBeeSoundInstance(beeEntity);
			} else {
				abstractBeeSoundInstance = new PassiveBeeSoundInstance(beeEntity);
			}

			this.client.getSoundManager().playNextTick(abstractBeeSoundInstance);
		}
	}

	@Override
	public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		Entity entity = new ExperienceOrbEntity(this.world, d, e, f, packet.getExperience());
		entity.updateTrackedPosition(d, e, f);
		entity.setYaw(0.0F);
		entity.setPitch(0.0F);
		entity.setId(packet.getEntityId());
		this.world.addEntity(entity);
	}

	@Override
	public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			entity.setVelocityClient(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
		}
	}

	@Override
	public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.id());
		if (entity != null) {
			entity.getDataTracker().writeUpdatedEntries(packet.trackedValues());
		}
	}

	@Override
	public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.id());
		if (entity != null) {
			Vec3d vec3d = packet.values().position();
			entity.getTrackedPosition().setPos(vec3d);
			if (!entity.isLogicalSideForUpdatingMovement()) {
				float f = packet.values().yaw();
				float g = packet.values().pitch();
				boolean bl = entity.getPos().squaredDistanceTo(vec3d) > 4096.0;
				if (this.world.hasEntity(entity) && !bl) {
					entity.updateTrackedPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, g, 3);
				} else {
					entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, g);
					if (entity.hasPassengerDeep(this.client.player)) {
						entity.updatePassengerPosition(this.client.player);
						this.client.player.resetPosition();
					}
				}

				entity.setOnGround(packet.onGround());
			}
		}
	}

	@Override
	public void onEntityPosition(EntityPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.entityId());
		if (entity == null) {
			if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.entityId()) {
				LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", packet.entityId());
				setPosition(packet.change(), packet.relatives(), this.client.player, false);
				this.connection
					.send(
						new PlayerMoveC2SPacket.Full(
							this.client.player.getX(),
							this.client.player.getY(),
							this.client.player.getZ(),
							this.client.player.getYaw(),
							this.client.player.getPitch(),
							false,
							false
						)
					);
			}
		} else {
			boolean bl = packet.relatives().contains(PositionFlag.X) || packet.relatives().contains(PositionFlag.Y) || packet.relatives().contains(PositionFlag.Z);
			boolean bl2 = this.world.hasEntity(entity) || !entity.isLogicalSideForUpdatingMovement() || bl;
			boolean bl3 = setPosition(packet.change(), packet.relatives(), entity, bl2);
			entity.setOnGround(packet.onGround());
			if (!bl3 && entity.hasPassengerDeep(this.client.player)) {
				entity.updatePassengerPosition(this.client.player);
				this.client.player.resetPosition();
				if (entity.isLocalPlayerOrLogicalSideForUpdatingMovement()) {
					this.connection.send(new VehicleMoveC2SPacket(entity));
				}
			}
		}
	}

	@Override
	public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.client.world != null) {
			TickManager tickManager = this.client.world.getTickManager();
			tickManager.setTickRate(packet.tickRate());
			tickManager.setFrozen(packet.isFrozen());
		}
	}

	@Override
	public void onTickStep(TickStepS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.client.world != null) {
			TickManager tickManager = this.client.world.getTickManager();
			tickManager.setStepTicks(packet.tickSteps());
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
			this.client.player.getInventory().selectedSlot = packet.getSlot();
		}
	}

	@Override
	public void onEntity(EntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			if (entity.isLogicalSideForUpdatingMovement()) {
				TrackedPosition trackedPosition = entity.getTrackedPosition();
				Vec3d vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
				trackedPosition.setPos(vec3d);
			} else {
				if (packet.isPositionChanged()) {
					TrackedPosition trackedPosition = entity.getTrackedPosition();
					Vec3d vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
					trackedPosition.setPos(vec3d);
					float f = packet.hasRotation() ? packet.getYaw() : entity.getLerpTargetYaw();
					float g = packet.hasRotation() ? packet.getPitch() : entity.getLerpTargetPitch();
					entity.updateTrackedPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), f, g, 3);
				} else if (packet.hasRotation()) {
					entity.updateTrackedPositionAndAngles(entity.getLerpTargetX(), entity.getLerpTargetY(), entity.getLerpTargetZ(), packet.getYaw(), packet.getPitch(), 3);
				}

				entity.setOnGround(packet.isOnGround());
			}
		}
	}

	@Override
	public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
			if (!entity.isLogicalSideForUpdatingMovement()
				&& abstractMinecartEntity.getController() instanceof ExperimentalMinecartController experimentalMinecartController) {
				experimentalMinecartController.stagingLerpSteps.addAll(packet.lerpSteps());
			}
		}
	}

	@Override
	public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			entity.updateTrackedHeadRotation(packet.getHeadYaw(), 3);
		}
	}

	@Override
	public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.getEntityIds().forEach(id -> {
			Entity entity = this.world.getEntityById(id);
			if (entity != null) {
				if (entity.hasPassengerDeep(this.client.player)) {
					LOGGER.debug("Remove entity {}:{} that has player as passenger", entity.getType(), id);
					this.removedPlayerVehicleId = OptionalInt.of(id);
				}

				this.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
			}
		});
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (!playerEntity.hasVehicle()) {
			setPosition(packet.change(), packet.relatives(), playerEntity, false);
		}

		this.connection
			.send(
				new PlayerMoveC2SPacket.Full(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), playerEntity.getYaw(), playerEntity.getPitch(), false, false)
			);
		this.connection.send(new TeleportConfirmC2SPacket(packet.teleportId()));
	}

	private static boolean setPosition(PlayerPosition pos, Set<PositionFlag> flags, Entity entity, boolean bl) {
		PlayerPosition playerPosition = PlayerPosition.fromEntityLerpTarget(entity);
		PlayerPosition playerPosition2 = PlayerPosition.apply(playerPosition, pos, flags);
		boolean bl2 = playerPosition.position().squaredDistanceTo(playerPosition2.position()) > 4096.0;
		if (bl && !bl2) {
			entity.updateTrackedPositionAndAngles(
				playerPosition2.position().getX(), playerPosition2.position().getY(), playerPosition2.position().getZ(), playerPosition2.yaw(), playerPosition2.pitch(), 3
			);
			entity.setVelocity(playerPosition2.deltaMovement());
			return true;
		} else {
			entity.setPosition(playerPosition2.position());
			entity.setVelocity(playerPosition2.deltaMovement());
			entity.setYaw(playerPosition2.yaw());
			entity.setPitch(playerPosition2.pitch());
			PlayerPosition playerPosition3 = new PlayerPosition(entity.getLastRenderPos(), Vec3d.ZERO, entity.prevYaw, entity.prevPitch);
			PlayerPosition playerPosition4 = PlayerPosition.apply(playerPosition3, pos, flags);
			entity.setPrevPositionAndAngles(playerPosition4.position(), playerPosition4.yaw(), playerPosition4.pitch());
			return false;
		}
	}

	@Override
	public void onPlayerRotation(PlayerRotationS2CPaket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.setYaw(packet.yRot());
		playerEntity.setPitch(packet.xRot());
		playerEntity.updatePrevAngles();
		this.connection.send(new PlayerMoveC2SPacket.LookAndOnGround(playerEntity.getYaw(), playerEntity.getPitch(), false, false));
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.visitUpdates((pos, state) -> this.world.handleBlockUpdate(pos, state, Block.NOTIFY_ALL | Block.FORCE_STATE));
	}

	@Override
	public void onChunkData(ChunkDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getChunkX();
		int j = packet.getChunkZ();
		this.loadChunk(i, j, packet.getChunkData());
		LightData lightData = packet.getLightData();
		this.world.enqueueChunkUpdate(() -> {
			this.readLightData(i, j, lightData, false);
			WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(i, j, false);
			if (worldChunk != null) {
				this.scheduleRenderChunk(worldChunk, i, j);
			}
		});
	}

	@Override
	public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
			this.world.getChunkManager().onChunkBiomeData(serialized.pos().x, serialized.pos().z, serialized.toReadingBuf());
		}

		for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
			this.world.resetChunkColor(new ChunkPos(serialized.pos().x, serialized.pos().z));
		}

		for (ChunkBiomeDataS2CPacket.Serialized serialized : packet.chunkBiomeData()) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					for (int k = this.world.getBottomSectionCoord(); k <= this.world.getTopSectionCoord(); k++) {
						this.client.worldRenderer.scheduleChunkRender(serialized.pos().x + i, k, serialized.pos().z + j);
					}
				}
			}
		}
	}

	private void loadChunk(int x, int z, ChunkData chunkData) {
		this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
	}

	private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
		LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
		ChunkSection[] chunkSections = chunk.getSectionArray();
		ChunkPos chunkPos = chunk.getPos();

		for (int i = 0; i < chunkSections.length; i++) {
			ChunkSection chunkSection = chunkSections[i];
			int j = this.world.sectionIndexToCoord(i);
			lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
		}

		this.world.scheduleChunkRenders(x - 1, this.world.getBottomSectionCoord(), z - 1, x + 1, this.world.getTopSectionCoord(), z + 1);
	}

	@Override
	public void onUnloadChunk(UnloadChunkS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getChunkManager().unload(packet.pos());
		this.unloadChunk(packet);
	}

	private void unloadChunk(UnloadChunkS2CPacket packet) {
		ChunkPos chunkPos = packet.pos();
		this.world.enqueueChunkUpdate(() -> {
			LightingProvider lightingProvider = this.world.getLightingProvider();
			lightingProvider.setColumnEnabled(chunkPos, false);

			for (int i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); i++) {
				ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, i);
				lightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, null);
				lightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, null);
			}

			for (int i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); i++) {
				lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, i), true);
			}
		});
	}

	@Override
	public void onBlockUpdate(BlockUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.handleBlockUpdate(packet.getPos(), packet.getState(), Block.NOTIFY_ALL | Block.FORCE_STATE);
	}

	@Override
	public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getMessageHandler().processAll();
		this.sendAcknowledgment();
		ChatHud.ChatState chatState = this.client.inGameHud.getChatHud().toChatState();
		this.client.enterReconfiguration(new ReconfiguringScreen(RECONFIGURING_TEXT, this.connection));
		this.connection
			.transitionInbound(
				ConfigurationStates.S2C,
				new ClientConfigurationNetworkHandler(
					this.client,
					this.connection,
					new ClientConnectionState(
						this.profile,
						this.worldSession,
						this.combinedDynamicRegistries,
						this.enabledFeatures,
						this.brand,
						this.serverInfo,
						this.postDisconnectScreen,
						this.serverCookies,
						chatState,
						this.customReportDetails,
						this.serverLinks
					)
				)
			);
		this.sendPacket(AcknowledgeReconfigurationC2SPacket.INSTANCE);
		this.connection.transitionOutbound(ConfigurationStates.C2S);
	}

	@Override
	public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(packet.getCollectorEntityId());
		if (livingEntity == null) {
			livingEntity = this.client.player;
		}

		if (entity != null) {
			if (entity instanceof ExperienceOrbEntity) {
				this.world
					.playSound(
						entity.getX(),
						entity.getY(),
						entity.getZ(),
						SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
						SoundCategory.PLAYERS,
						0.1F,
						(this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F,
						false
					);
			} else {
				this.world
					.playSound(
						entity.getX(),
						entity.getY(),
						entity.getZ(),
						SoundEvents.ENTITY_ITEM_PICKUP,
						SoundCategory.PLAYERS,
						0.2F,
						(this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F,
						false
					);
			}

			this.client
				.particleManager
				.addParticle(new ItemPickupParticle(this.client.getEntityRenderDispatcher(), this.client.getBufferBuilders(), this.world, entity, livingEntity));
			if (entity instanceof ItemEntity itemEntity) {
				ItemStack itemStack = itemEntity.getStack();
				if (!itemStack.isEmpty()) {
					itemStack.decrement(packet.getStackAmount());
				}

				if (itemStack.isEmpty()) {
					this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
				}
			} else if (!(entity instanceof ExperienceOrbEntity)) {
				this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
			}
		}
	}

	@Override
	public void onGameMessage(GameMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getMessageHandler().onGameMessage(packet.content(), packet.overlay());
	}

	@Override
	public void onChatMessage(ChatMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Optional<MessageBody> optional = packet.body().toBody(this.signatureStorage);
		if (optional.isEmpty()) {
			this.connection.disconnect(INVALID_PACKET_TEXT);
		} else {
			this.signatureStorage.add((MessageBody)optional.get(), packet.signature());
			UUID uUID = packet.sender();
			PlayerListEntry playerListEntry = this.getPlayerListEntry(uUID);
			if (playerListEntry == null) {
				LOGGER.error("Received player chat packet for unknown player with ID: {}", uUID);
				this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.serializedParameters());
			} else {
				PublicPlayerSession publicPlayerSession = playerListEntry.getSession();
				MessageLink messageLink;
				if (publicPlayerSession != null) {
					messageLink = new MessageLink(packet.index(), uUID, publicPlayerSession.sessionId());
				} else {
					messageLink = MessageLink.of(uUID);
				}

				SignedMessage signedMessage = new SignedMessage(messageLink, packet.signature(), (MessageBody)optional.get(), packet.unsignedContent(), packet.filterMask());
				signedMessage = playerListEntry.getMessageVerifier().ensureVerified(signedMessage);
				if (signedMessage != null) {
					this.client.getMessageHandler().onChatMessage(signedMessage, playerListEntry.getProfile(), packet.serializedParameters());
				} else {
					this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.serializedParameters());
				}
			}
		}
	}

	@Override
	public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getMessageHandler().onProfilelessMessage(packet.message(), packet.chatType());
	}

	@Override
	public void onRemoveMessage(RemoveMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Optional<MessageSignatureData> optional = packet.messageSignature().getSignature(this.signatureStorage);
		if (optional.isEmpty()) {
			this.connection.disconnect(INVALID_PACKET_TEXT);
		} else {
			this.lastSeenMessagesCollector.remove((MessageSignatureData)optional.get());
			if (!this.client.getMessageHandler().removeDelayedMessage((MessageSignatureData)optional.get())) {
				this.client.inGameHud.getChatHud().removeMessage((MessageSignatureData)optional.get());
			}
		}
	}

	@Override
	public void onEntityAnimation(EntityAnimationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			if (packet.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN_HAND);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.SWING_OFF_HAND) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.OFF_HAND);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.WAKE_UP) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				playerEntity.wakeUp(false, false);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.CRIT) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.CRIT);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.ENCHANTED_HIT) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.ENCHANTED_HIT);
			}
		}
	}

	@Override
	public void onDamageTilt(DamageTiltS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.id());
		if (entity != null) {
			entity.animateDamage(packet.yaw());
		}
	}

	@Override
	public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.setTime(packet.time(), packet.timeOfDay(), packet.tickDayTime());
		this.worldSession.setTick(packet.time());
	}

	@Override
	public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setSpawnPos(packet.getPos(), packet.getAngle());
	}

	@Override
	public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.hasPassengerDeep(this.client.player);
			entity.removeAllPassengers();

			for (int i : packet.getPassengerIds()) {
				Entity entity2 = this.world.getEntityById(i);
				if (entity2 != null) {
					entity2.startRiding(entity, true);
					if (entity2 == this.client.player) {
						this.removedPlayerVehicleId = OptionalInt.empty();
						if (!bl) {
							if (entity instanceof AbstractBoatEntity) {
								this.client.player.prevYaw = entity.getYaw();
								this.client.player.setYaw(entity.getYaw());
								this.client.player.setHeadYaw(entity.getYaw());
							}

							Text text = Text.translatable("mount.onboard", this.client.options.sneakKey.getBoundKeyLocalizedText());
							this.client.inGameHud.setOverlayMessage(text, false);
							this.client.getNarratorManager().narrate(text);
						}
					}
				}
			}
		}
	}

	@Override
	public void onEntityAttach(EntityAttachS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.world.getEntityById(packet.getAttachedEntityId()) instanceof Leashable leashable) {
			leashable.setUnresolvedLeashHolderId(packet.getHoldingEntityId());
		}
	}

	private static ItemStack getActiveDeathProtector(PlayerEntity player) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.contains(DataComponentTypes.DEATH_PROTECTION)) {
				return itemStack;
			}
		}

		return new ItemStack(Items.TOTEM_OF_UNDYING);
	}

	@Override
	public void onEntityStatus(EntityStatusS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			switch (packet.getStatus()) {
				case 21:
					this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
					break;
				case 35:
					int i = 40;
					this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
					this.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
					if (entity == this.client.player) {
						this.client.gameRenderer.showFloatingItem(getActiveDeathProtector(this.client.player));
					}
					break;
				case 63:
					this.client.getSoundManager().play(new SnifferDigSoundInstance((SnifferEntity)entity));
					break;
				default:
					entity.handleStatus(packet.getStatus());
			}
		}
	}

	@Override
	public void onEntityDamage(EntityDamageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.entityId());
		if (entity != null) {
			entity.onDamaged(packet.createDamageSource(this.world));
		}
	}

	@Override
	public void onHealthUpdate(HealthUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.updateHealth(packet.getHealth());
		this.client.player.getHungerManager().setFoodLevel(packet.getFood());
		this.client.player.getHungerManager().setSaturationLevel(packet.getSaturation());
	}

	@Override
	public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
		RegistryKey<World> registryKey = commonPlayerSpawnInfo.dimension();
		RegistryEntry<DimensionType> registryEntry = commonPlayerSpawnInfo.dimensionType();
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		RegistryKey<World> registryKey2 = clientPlayerEntity.getWorld().getRegistryKey();
		boolean bl = registryKey != registryKey2;
		DownloadingTerrainScreen.WorldEntryReason worldEntryReason = this.getWorldEntryReason(clientPlayerEntity.isDead(), registryKey, registryKey2);
		if (bl) {
			Map<MapIdComponent, MapState> map = this.world.getMapStates();
			boolean bl2 = commonPlayerSpawnInfo.isDebug();
			boolean bl3 = commonPlayerSpawnInfo.isFlat();
			int i = commonPlayerSpawnInfo.seaLevel();
			ClientWorld.Properties properties = new ClientWorld.Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), bl3);
			this.worldProperties = properties;
			this.world = new ClientWorld(
				this,
				properties,
				registryKey,
				registryEntry,
				this.chunkLoadDistance,
				this.simulationDistance,
				this.client.worldRenderer,
				bl2,
				commonPlayerSpawnInfo.seed(),
				i
			);
			this.world.putMapStates(map);
			this.client.joinWorld(this.world, worldEntryReason);
		}

		this.client.cameraEntity = null;
		if (clientPlayerEntity.shouldCloseHandledScreenOnRespawn()) {
			clientPlayerEntity.closeHandledScreen();
		}

		ClientPlayerEntity clientPlayerEntity2;
		if (packet.hasFlag(PlayerRespawnS2CPacket.KEEP_TRACKED_DATA)) {
			clientPlayerEntity2 = this.client
				.interactionManager
				.createPlayer(
					this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook(), clientPlayerEntity.isSneaking(), clientPlayerEntity.isSprinting()
				);
		} else {
			clientPlayerEntity2 = this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook());
		}

		this.startWorldLoading(clientPlayerEntity2, this.world, worldEntryReason);
		clientPlayerEntity2.setId(clientPlayerEntity.getId());
		this.client.player = clientPlayerEntity2;
		if (bl) {
			this.client.getMusicTracker().stop();
		}

		this.client.cameraEntity = clientPlayerEntity2;
		if (packet.hasFlag(PlayerRespawnS2CPacket.KEEP_TRACKED_DATA)) {
			List<DataTracker.SerializedEntry<?>> list = clientPlayerEntity.getDataTracker().getChangedEntries();
			if (list != null) {
				clientPlayerEntity2.getDataTracker().writeUpdatedEntries(list);
			}

			clientPlayerEntity2.setVelocity(clientPlayerEntity.getVelocity());
			clientPlayerEntity2.setYaw(clientPlayerEntity.getYaw());
			clientPlayerEntity2.setPitch(clientPlayerEntity.getPitch());
		} else {
			clientPlayerEntity2.init();
			clientPlayerEntity2.setYaw(-180.0F);
		}

		if (packet.hasFlag(PlayerRespawnS2CPacket.KEEP_ATTRIBUTES)) {
			clientPlayerEntity2.getAttributes().setFrom(clientPlayerEntity.getAttributes());
		} else {
			clientPlayerEntity2.getAttributes().setBaseFrom(clientPlayerEntity.getAttributes());
		}

		this.world.addEntity(clientPlayerEntity2);
		clientPlayerEntity2.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(clientPlayerEntity2);
		clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.hasReducedDebugInfo());
		clientPlayerEntity2.setShowsDeathScreen(clientPlayerEntity.showsDeathScreen());
		clientPlayerEntity2.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
		clientPlayerEntity2.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
		clientPlayerEntity2.nauseaIntensity = clientPlayerEntity.nauseaIntensity;
		clientPlayerEntity2.prevNauseaIntensity = clientPlayerEntity.prevNauseaIntensity;
		if (this.client.currentScreen instanceof DeathScreen || this.client.currentScreen instanceof DeathScreen.TitleScreenConfirmScreen) {
			this.client.setScreen(null);
		}

		this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.prevGameMode());
	}

	private DownloadingTerrainScreen.WorldEntryReason getWorldEntryReason(boolean dead, RegistryKey<World> from, RegistryKey<World> to) {
		DownloadingTerrainScreen.WorldEntryReason worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.OTHER;
		if (!dead) {
			if (from == World.NETHER || to == World.NETHER) {
				worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.NETHER_PORTAL;
			} else if (from == World.END || to == World.END) {
				worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.END_PORTAL;
			}
		}

		return worldEntryReason;
	}

	@Override
	public void onExplosion(ExplosionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Vec3d vec3d = packet.center();
		this.client
			.world
			.playSound(
				vec3d.getX(),
				vec3d.getY(),
				vec3d.getZ(),
				packet.explosionSound().value(),
				SoundCategory.BLOCKS,
				4.0F,
				(1.0F + (this.client.world.random.nextFloat() - this.client.world.random.nextFloat()) * 0.2F) * 0.7F,
				false
			);
		this.client.world.addParticle(packet.explosionParticle(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0, 0.0, 0.0);
		packet.playerKnockback().ifPresent(this.client.player::addVelocityInternal);
	}

	@Override
	public void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.world.getEntityById(packet.getHorseId()) instanceof AbstractHorseEntity abstractHorseEntity) {
			ClientPlayerEntity clientPlayerEntity = this.client.player;
			int i = packet.getSlotColumnCount();
			SimpleInventory simpleInventory = new SimpleInventory(AbstractHorseEntity.getInventorySize(i));
			HorseScreenHandler horseScreenHandler = new HorseScreenHandler(
				packet.getSyncId(), clientPlayerEntity.getInventory(), simpleInventory, abstractHorseEntity, i
			);
			clientPlayerEntity.currentScreenHandler = horseScreenHandler;
			this.client.setScreen(new HorseScreen(horseScreenHandler, clientPlayerEntity.getInventory(), abstractHorseEntity, i));
		}
	}

	@Override
	public void onOpenScreen(OpenScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		HandledScreens.open(packet.getScreenHandlerType(), this.client, packet.getSyncId(), packet.getName());
	}

	@Override
	public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = packet.getStack();
		int i = packet.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		boolean bl;
		if (this.client.currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
			bl = !creativeInventoryScreen.isInventoryTabSelected();
		} else {
			bl = false;
		}

		if (packet.getSyncId() == 0) {
			if (PlayerScreenHandler.isInHotbar(i) && !itemStack.isEmpty()) {
				ItemStack itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack();
				if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
					itemStack.setBobbingAnimationTime(5);
				}
			}

			playerEntity.playerScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
		} else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && (packet.getSyncId() != 0 || !bl)) {
			playerEntity.currentScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
		}

		if (this.client.currentScreen instanceof CreativeInventoryScreen) {
			playerEntity.playerScreenHandler.setPreviousTrackedSlot(i, itemStack);
			playerEntity.playerScreenHandler.sendContentUpdates();
		}
	}

	@Override
	public void onSetCursorItem(SetCursorItemS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getTutorialManager().onSlotUpdate(packet.contents());
		if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
			this.client.player.currentScreenHandler.setCursorStack(packet.contents());
		}
	}

	@Override
	public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getTutorialManager().onSlotUpdate(packet.contents());
		this.client.player.getInventory().setStack(packet.slot(), packet.contents());
	}

	@Override
	public void onInventory(InventoryS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (packet.getSyncId() == 0) {
			playerEntity.playerScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
		} else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId) {
			playerEntity.currentScreenHandler.updateSlotStacks(packet.getRevision(), packet.getContents(), packet.getCursorStack());
		}
	}

	@Override
	public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		BlockPos blockPos = packet.getPos();
		if (this.world.getBlockEntity(blockPos) instanceof SignBlockEntity signBlockEntity) {
			this.client.player.openEditSignScreen(signBlockEntity, packet.isFront());
		} else {
			LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", this.world.getBlockEntity(blockPos), blockPos);
		}
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		BlockPos blockPos = packet.getPos();
		this.client.world.getBlockEntity(blockPos, packet.getBlockEntityType()).ifPresent(blockEntity -> {
			NbtCompound nbtCompound = packet.getNbt();
			if (!nbtCompound.isEmpty()) {
				blockEntity.read(nbtCompound, this.combinedDynamicRegistries);
			}

			if (blockEntity instanceof CommandBlockBlockEntity && this.client.currentScreen instanceof CommandBlockScreen) {
				((CommandBlockScreen)this.client.currentScreen).updateCommandBlock();
			}
		});
	}

	@Override
	public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.currentScreenHandler != null && playerEntity.currentScreenHandler.syncId == packet.getSyncId()) {
			playerEntity.currentScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
		}
	}

	@Override
	public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.world.getEntityById(packet.getEntityId()) instanceof LivingEntity livingEntity) {
			packet.getEquipmentList().forEach(pair -> livingEntity.equipStack((EquipmentSlot)pair.getFirst(), (ItemStack)pair.getSecond()));
		}
	}

	@Override
	public void onCloseScreen(CloseScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.closeScreen();
	}

	@Override
	public void onBlockEvent(BlockEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.addSyncedBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
	}

	@Override
	public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
	}

	@Override
	public void onGameStateChange(GameStateChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		GameStateChangeS2CPacket.Reason reason = packet.getReason();
		float f = packet.getValue();
		int i = MathHelper.floor(f + 0.5F);
		if (reason == GameStateChangeS2CPacket.NO_RESPAWN_BLOCK) {
			playerEntity.sendMessage(Text.translatable("block.minecraft.spawn.not_valid"), false);
		} else if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
			this.world.getLevelProperties().setRaining(true);
			this.world.setRainGradient(0.0F);
		} else if (reason == GameStateChangeS2CPacket.RAIN_STOPPED) {
			this.world.getLevelProperties().setRaining(false);
			this.world.setRainGradient(1.0F);
		} else if (reason == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
			this.client.interactionManager.setGameMode(GameMode.byId(i));
		} else if (reason == GameStateChangeS2CPacket.GAME_WON) {
			this.client.setScreen(new CreditsScreen(true, () -> {
				this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
				this.client.setScreen(null);
			}));
		} else if (reason == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN) {
			GameOptions gameOptions = this.client.options;
			if (f == GameStateChangeS2CPacket.DEMO_OPEN_SCREEN) {
				this.client.setScreen(new DemoScreen());
			} else if (f == GameStateChangeS2CPacket.DEMO_MOVEMENT_HELP) {
				this.client
					.inGameHud
					.getChatHud()
					.addMessage(
						Text.translatable(
							"demo.help.movement",
							gameOptions.forwardKey.getBoundKeyLocalizedText(),
							gameOptions.leftKey.getBoundKeyLocalizedText(),
							gameOptions.backKey.getBoundKeyLocalizedText(),
							gameOptions.rightKey.getBoundKeyLocalizedText()
						)
					);
			} else if (f == GameStateChangeS2CPacket.DEMO_JUMP_HELP) {
				this.client.inGameHud.getChatHud().addMessage(Text.translatable("demo.help.jump", gameOptions.jumpKey.getBoundKeyLocalizedText()));
			} else if (f == GameStateChangeS2CPacket.DEMO_INVENTORY_HELP) {
				this.client.inGameHud.getChatHud().addMessage(Text.translatable("demo.help.inventory", gameOptions.inventoryKey.getBoundKeyLocalizedText()));
			} else if (f == GameStateChangeS2CPacket.DEMO_EXPIRY_NOTICE) {
				this.client.inGameHud.getChatHud().addMessage(Text.translatable("demo.day.6", gameOptions.screenshotKey.getBoundKeyLocalizedText()));
			}
		} else if (reason == GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER) {
			this.world
				.playSound(
					playerEntity, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F
				);
		} else if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
			this.world.setRainGradient(f);
		} else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
			this.world.setThunderGradient(f);
		} else if (reason == GameStateChangeS2CPacket.PUFFERFISH_STING) {
			this.world
				.playSound(
					playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F
				);
		} else if (reason == GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT) {
			this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
			if (i == 1) {
				this.world
					.playSound(
						playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F
					);
			}
		} else if (reason == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
			this.client.player.setShowsDeathScreen(f == GameStateChangeS2CPacket.DEMO_OPEN_SCREEN);
		} else if (reason == GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED) {
			this.client.player.setLimitedCraftingEnabled(f == 1.0F);
		} else if (reason == GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING && this.worldLoadingState != null) {
			this.worldLoadingState.handleChunksComingPacket();
		}
	}

	private void startWorldLoading(ClientPlayerEntity player, ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
		this.worldLoadingState = new WorldLoadingState(player, world, this.client.worldRenderer);
		this.client.setScreen(new DownloadingTerrainScreen(this.worldLoadingState::isReady, worldEntryReason));
	}

	@Override
	public void onMapUpdate(MapUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		MapIdComponent mapIdComponent = packet.mapId();
		MapState mapState = this.client.world.getMapState(mapIdComponent);
		if (mapState == null) {
			mapState = MapState.of(packet.scale(), packet.locked(), this.client.world.getRegistryKey());
			this.client.world.putClientsideMapState(mapIdComponent, mapState);
		}

		packet.apply(mapState);
		this.client.getMapTextureManager().setNeedsUpdate(mapIdComponent, mapState);
	}

	@Override
	public void onWorldEvent(WorldEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.isGlobal()) {
			this.client.world.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getData());
		} else {
			this.client.world.syncWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
		}
	}

	@Override
	public void onAdvancements(AdvancementUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.advancementHandler.onAdvancements(packet);
	}

	@Override
	public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Identifier identifier = packet.getTabId();
		if (identifier == null) {
			this.advancementHandler.selectTab(null, false);
		} else {
			AdvancementEntry advancementEntry = this.advancementHandler.get(identifier);
			this.advancementHandler.selectTab(advancementEntry, false);
		}
	}

	@Override
	public void onCommandTree(CommandTreeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(packet.getCommandTree(CommandRegistryAccess.of(this.combinedDynamicRegistries, this.enabledFeatures)));
	}

	@Override
	public void onStopSound(StopSoundS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
	}

	@Override
	public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.commandSource.onCommandSuggestions(packet.id(), packet.getSuggestions());
	}

	@Override
	public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.recipeManager = new ClientRecipeManager(packet.itemSets(), packet.stonecutterRecipes());
	}

	@Override
	public void onLookAt(LookAtS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Vec3d vec3d = packet.getTargetPosition(this.world);
		if (vec3d != null) {
			this.client.player.lookAt(packet.getSelfAnchor(), vec3d);
		}
	}

	@Override
	public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (!this.dataQueryHandler.handleQueryResponse(packet.getTransactionId(), packet.getNbt())) {
			LOGGER.debug("Got unhandled response to tag query {}", packet.getTransactionId());
		}
	}

	@Override
	public void onStatistics(StatisticsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (Entry<Stat<?>> entry : packet.stats().object2IntEntrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = entry.getIntValue();
			this.client.player.getStatHandler().setStat(this.client.player, stat, i);
		}

		if (this.client.currentScreen instanceof StatsScreen statsScreen) {
			statsScreen.onStatsReady();
		}
	}

	@Override
	public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		if (packet.replace()) {
			clientRecipeBook.clear();
		}

		for (RecipeBookAddS2CPacket.Entry entry : packet.entries()) {
			clientRecipeBook.add(entry.contents());
			if (entry.isHighlighted()) {
				clientRecipeBook.markHighlighted(entry.contents().id());
			}

			if (entry.shouldShowNotification()) {
				RecipeToast.show(this.client.getToastManager(), entry.contents().display());
			}
		}

		this.refreshRecipeBook(clientRecipeBook);
	}

	@Override
	public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();

		for (NetworkRecipeId networkRecipeId : packet.recipes()) {
			clientRecipeBook.remove(networkRecipeId);
		}

		this.refreshRecipeBook(clientRecipeBook);
	}

	@Override
	public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setOptions(packet.bookSettings());
		this.refreshRecipeBook(clientRecipeBook);
	}

	private void refreshRecipeBook(ClientRecipeBook recipeBook) {
		recipeBook.refresh();
		this.searchManager.addRecipeOutputReloader(recipeBook, this.world);
		if (this.client.currentScreen instanceof RecipeBookProvider recipeBookProvider) {
			recipeBookProvider.refreshRecipeBook();
		}
	}

	@Override
	public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity instanceof LivingEntity) {
			RegistryEntry<StatusEffect> registryEntry = packet.getEffectId();
			StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
				registryEntry, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), null
			);
			if (!packet.keepFading()) {
				statusEffectInstance.skipFading();
			}

			((LivingEntity)entity).setStatusEffect(statusEffectInstance, null);
		}
	}

	private <T> Registry.PendingTagLoad<T> startTagReload(RegistryKey<? extends Registry<? extends T>> registryRef, TagPacketSerializer.Serialized serialized) {
		Registry<T> registry = this.combinedDynamicRegistries.getOrThrow(registryRef);
		return registry.startTagReload(serialized.toRegistryTags(registry));
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		List<Registry.PendingTagLoad<?>> list = new ArrayList(packet.getGroups().size());
		boolean bl = this.connection.isLocal();
		packet.getGroups().forEach((registryRef, serialized) -> {
			if (!bl || SerializableRegistries.isSynced(registryRef)) {
				list.add(this.startTagReload(registryRef, serialized));
			}
		});
		list.forEach(Registry.PendingTagLoad::apply);
		this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries, this.enabledFeatures);
		List<ItemStack> list2 = List.copyOf(ItemGroups.getSearchGroup().getDisplayStacks());
		this.searchManager.addItemTagReloader(list2);
	}

	@Override
	public void onEndCombat(EndCombatS2CPacket packet) {
	}

	@Override
	public void onEnterCombat(EnterCombatS2CPacket packet) {
	}

	@Override
	public void onDeathMessage(DeathMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.playerId());
		if (entity == this.client.player) {
			if (this.client.player.showsDeathScreen()) {
				this.client.setScreen(new DeathScreen(packet.message(), this.world.getLevelProperties().isHardcore()));
			} else {
				this.client.player.requestRespawn();
			}
		}
	}

	@Override
	public void onDifficulty(DifficultyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.worldProperties.setDifficulty(packet.getDifficulty());
		this.worldProperties.setDifficultyLocked(packet.isDifficultyLocked());
	}

	@Override
	public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			this.client.setCameraEntity(entity);
		}
	}

	@Override
	public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		WorldBorder worldBorder = this.world.getWorldBorder();
		worldBorder.setCenter(packet.getCenterX(), packet.getCenterZ());
		long l = packet.getSizeLerpTime();
		if (l > 0L) {
			worldBorder.interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), l);
		} else {
			worldBorder.setSize(packet.getSizeLerpTarget());
		}

		worldBorder.setMaxRadius(packet.getMaxRadius());
		worldBorder.setWarningBlocks(packet.getWarningBlocks());
		worldBorder.setWarningTime(packet.getWarningTime());
	}

	@Override
	public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getWorldBorder().setCenter(packet.getCenterX(), packet.getCenterZ());
	}

	@Override
	public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getWorldBorder().interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), packet.getSizeLerpTime());
	}

	@Override
	public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getWorldBorder().setSize(packet.getSizeLerpTarget());
	}

	@Override
	public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
	}

	@Override
	public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getWorldBorder().setWarningTime(packet.getWarningTime());
	}

	@Override
	public void onTitleClear(ClearTitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.clearTitle();
		if (packet.shouldReset()) {
			this.client.inGameHud.setDefaultTitleFade();
		}
	}

	@Override
	public void onServerMetadata(ServerMetadataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.serverInfo != null) {
			this.serverInfo.label = packet.description();
			packet.favicon().map(ServerInfo::validateFavicon).ifPresent(this.serverInfo::setFavicon);
			ServerList.updateServerListEntry(this.serverInfo);
		}
	}

	@Override
	public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.commandSource.onChatSuggestions(packet.action(), packet.entries());
	}

	@Override
	public void onOverlayMessage(OverlayMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setOverlayMessage(packet.text(), false);
	}

	@Override
	public void onTitle(TitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setTitle(packet.text());
	}

	@Override
	public void onSubtitle(SubtitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setSubtitle(packet.text());
	}

	@Override
	public void onTitleFade(TitleFadeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setTitleTicks(packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.getPlayerListHud().setHeader(packet.header().getString().isEmpty() ? null : packet.header());
		this.client.inGameHud.getPlayerListHud().setFooter(packet.footer().getString().isEmpty() ? null : packet.footer());
	}

	@Override
	public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getEntity(this.world) instanceof LivingEntity livingEntity) {
			livingEntity.removeStatusEffectInternal(packet.effect());
		}
	}

	@Override
	public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (UUID uUID : packet.profileIds()) {
			this.client.getSocialInteractionsManager().setPlayerOffline(uUID);
			PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.remove(uUID);
			if (playerListEntry != null) {
				this.listedPlayerListEntries.remove(playerListEntry);
			}
		}
	}

	@Override
	public void onPlayerList(PlayerListS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (PlayerListS2CPacket.Entry entry : packet.getPlayerAdditionEntries()) {
			PlayerListEntry playerListEntry = new PlayerListEntry((GameProfile)Objects.requireNonNull(entry.profile()), this.isSecureChatEnforced());
			if (this.playerListEntries.putIfAbsent(entry.profileId(), playerListEntry) == null) {
				this.client.getSocialInteractionsManager().setPlayerOnline(playerListEntry);
			}
		}

		for (PlayerListS2CPacket.Entry entryx : packet.getEntries()) {
			PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.get(entryx.profileId());
			if (playerListEntry == null) {
				LOGGER.warn("Ignoring player info update for unknown player {} ({})", entryx.profileId(), packet.getActions());
			} else {
				for (PlayerListS2CPacket.Action action : packet.getActions()) {
					this.handlePlayerListAction(action, entryx, playerListEntry);
				}
			}
		}
	}

	private void handlePlayerListAction(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
		switch (action) {
			case INITIALIZE_CHAT:
				this.setPublicSession(receivedEntry, currentEntry);
				break;
			case UPDATE_GAME_MODE:
				if (currentEntry.getGameMode() != receivedEntry.gameMode() && this.client.player != null && this.client.player.getUuid().equals(receivedEntry.profileId())) {
					this.client.player.onGameModeChanged(receivedEntry.gameMode());
				}

				currentEntry.setGameMode(receivedEntry.gameMode());
				break;
			case UPDATE_LISTED:
				if (receivedEntry.listed()) {
					this.listedPlayerListEntries.add(currentEntry);
				} else {
					this.listedPlayerListEntries.remove(currentEntry);
				}
				break;
			case UPDATE_LATENCY:
				currentEntry.setLatency(receivedEntry.latency());
				break;
			case UPDATE_DISPLAY_NAME:
				currentEntry.setDisplayName(receivedEntry.displayName());
				break;
			case UPDATE_LIST_ORDER:
				currentEntry.setListOrder(receivedEntry.listOrder());
		}
	}

	private void setPublicSession(PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
		GameProfile gameProfile = currentEntry.getProfile();
		SignatureVerifier signatureVerifier = this.client.getServicesSignatureVerifier();
		if (signatureVerifier == null) {
			LOGGER.warn("Ignoring chat session from {} due to missing Services public key", gameProfile.getName());
			currentEntry.resetSession(this.isSecureChatEnforced());
		} else {
			PublicPlayerSession.Serialized serialized = receivedEntry.chatSession();
			if (serialized != null) {
				try {
					PublicPlayerSession publicPlayerSession = serialized.toSession(gameProfile, signatureVerifier);
					currentEntry.setSession(publicPlayerSession);
				} catch (PlayerPublicKey.PublicKeyException var7) {
					LOGGER.error("Failed to validate profile key for player: '{}'", gameProfile.getName(), var7);
					currentEntry.resetSession(this.isSecureChatEnforced());
				}
			} else {
				currentEntry.resetSession(this.isSecureChatEnforced());
			}
		}
	}

	private boolean isSecureChatEnforced() {
		return this.client.providesProfileKeys() && this.secureChatEnforced;
	}

	@Override
	public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.getAbilities().flying = packet.isFlying();
		playerEntity.getAbilities().creativeMode = packet.isCreativeMode();
		playerEntity.getAbilities().invulnerable = packet.isInvulnerable();
		playerEntity.getAbilities().allowFlying = packet.allowFlying();
		playerEntity.getAbilities().setFlySpeed(packet.getFlySpeed());
		playerEntity.getAbilities().setWalkSpeed(packet.getWalkSpeed());
	}

	@Override
	public void onPlaySound(PlaySoundS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client
			.world
			.playSound(
				this.client.player,
				packet.getX(),
				packet.getY(),
				packet.getZ(),
				packet.getSound(),
				packet.getCategory(),
				packet.getVolume(),
				packet.getPitch(),
				packet.getSeed()
			);
	}

	@Override
	public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			this.client
				.world
				.playSoundFromEntity(this.client.player, entity, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch(), packet.getSeed());
		}
	}

	@Override
	public void onBossBar(BossBarS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.getBossBarHud().handlePacket(packet);
	}

	@Override
	public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.cooldown() == 0) {
			this.client.player.getItemCooldownManager().remove(packet.cooldownGroup());
		} else {
			this.client.player.getItemCooldownManager().set(packet.cooldownGroup(), packet.cooldown());
		}
	}

	@Override
	public void onVehicleMove(VehicleMoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.client.player.getRootVehicle();
		if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
			Vec3d vec3d = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
			Vec3d vec3d2 = new Vec3d(entity.getLerpTargetX(), entity.getLerpTargetY(), entity.getLerpTargetZ());
			if (vec3d.distanceTo(vec3d2) > 1.0E-5F) {
				entity.resetLerp();
				entity.updatePositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), packet.getYaw(), packet.getPitch());
			}

			this.connection.send(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
		BookScreen.Contents contents = BookScreen.Contents.create(itemStack);
		if (contents != null) {
			this.client.setScreen(new BookScreen(contents));
		}
	}

	@Override
	public void onCustomPayload(CustomPayload payload) {
		if (payload instanceof DebugPathCustomPayload debugPathCustomPayload) {
			this.client
				.debugRenderer
				.pathfindingDebugRenderer
				.addPath(debugPathCustomPayload.entityId(), debugPathCustomPayload.path(), debugPathCustomPayload.maxNodeDistance());
		} else if (payload instanceof DebugNeighborsUpdateCustomPayload debugNeighborsUpdateCustomPayload) {
			this.client.debugRenderer.neighborUpdateDebugRenderer.addNeighborUpdate(debugNeighborsUpdateCustomPayload.time(), debugNeighborsUpdateCustomPayload.pos());
		} else if (payload instanceof DebugRedstoneUpdateOrderCustomPayload debugRedstoneUpdateOrderCustomPayload) {
			this.client.debugRenderer.redstoneUpdateOrderDebugRenderer.addUpdateOrder(debugRedstoneUpdateOrderCustomPayload);
		} else if (payload instanceof DebugStructuresCustomPayload debugStructuresCustomPayload) {
			this.client
				.debugRenderer
				.structureDebugRenderer
				.addStructure(debugStructuresCustomPayload.mainBB(), debugStructuresCustomPayload.pieces(), debugStructuresCustomPayload.dimension());
		} else if (payload instanceof DebugWorldgenAttemptCustomPayload debugWorldgenAttemptCustomPayload) {
			((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer)
				.addBox(
					debugWorldgenAttemptCustomPayload.pos(),
					debugWorldgenAttemptCustomPayload.scale(),
					debugWorldgenAttemptCustomPayload.red(),
					debugWorldgenAttemptCustomPayload.green(),
					debugWorldgenAttemptCustomPayload.blue(),
					debugWorldgenAttemptCustomPayload.alpha()
				);
		} else if (payload instanceof DebugPoiTicketCountCustomPayload debugPoiTicketCountCustomPayload) {
			this.client
				.debugRenderer
				.villageDebugRenderer
				.setFreeTicketCount(debugPoiTicketCountCustomPayload.pos(), debugPoiTicketCountCustomPayload.freeTicketCount());
		} else if (payload instanceof DebugPoiAddedCustomPayload debugPoiAddedCustomPayload) {
			VillageDebugRenderer.PointOfInterest pointOfInterest = new VillageDebugRenderer.PointOfInterest(
				debugPoiAddedCustomPayload.pos(), debugPoiAddedCustomPayload.poiType(), debugPoiAddedCustomPayload.freeTicketCount()
			);
			this.client.debugRenderer.villageDebugRenderer.addPointOfInterest(pointOfInterest);
		} else if (payload instanceof DebugPoiRemovedCustomPayload debugPoiRemovedCustomPayload) {
			this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(debugPoiRemovedCustomPayload.pos());
		} else if (payload instanceof DebugVillageSectionsCustomPayload debugVillageSectionsCustomPayload) {
			VillageSectionsDebugRenderer villageSectionsDebugRenderer = this.client.debugRenderer.villageSectionsDebugRenderer;
			debugVillageSectionsCustomPayload.villageChunks().forEach(villageSectionsDebugRenderer::addSection);
			debugVillageSectionsCustomPayload.notVillageChunks().forEach(villageSectionsDebugRenderer::removeSection);
		} else if (payload instanceof DebugGoalSelectorCustomPayload debugGoalSelectorCustomPayload) {
			this.client
				.debugRenderer
				.goalSelectorDebugRenderer
				.setGoalSelectorList(debugGoalSelectorCustomPayload.entityId(), debugGoalSelectorCustomPayload.pos(), debugGoalSelectorCustomPayload.goals());
		} else if (payload instanceof DebugBrainCustomPayload debugBrainCustomPayload) {
			this.client.debugRenderer.villageDebugRenderer.addBrain(debugBrainCustomPayload.brainDump());
		} else if (payload instanceof DebugBeeCustomPayload debugBeeCustomPayload) {
			this.client.debugRenderer.beeDebugRenderer.addBee(debugBeeCustomPayload.beeInfo());
		} else if (payload instanceof DebugHiveCustomPayload debugHiveCustomPayload) {
			this.client.debugRenderer.beeDebugRenderer.addHive(debugHiveCustomPayload.hiveInfo(), this.world.getTime());
		} else if (payload instanceof DebugGameTestAddMarkerCustomPayload debugGameTestAddMarkerCustomPayload) {
			this.client
				.debugRenderer
				.gameTestDebugRenderer
				.addMarker(
					debugGameTestAddMarkerCustomPayload.pos(),
					debugGameTestAddMarkerCustomPayload.color(),
					debugGameTestAddMarkerCustomPayload.text(),
					debugGameTestAddMarkerCustomPayload.durationMs()
				);
		} else if (payload instanceof DebugGameTestClearCustomPayload) {
			this.client.debugRenderer.gameTestDebugRenderer.clear();
		} else if (payload instanceof DebugRaidsCustomPayload debugRaidsCustomPayload) {
			this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(debugRaidsCustomPayload.raidCenters());
		} else if (payload instanceof DebugGameEventCustomPayload debugGameEventCustomPayload) {
			this.client.debugRenderer.gameEventDebugRenderer.addEvent(debugGameEventCustomPayload.gameEventType(), debugGameEventCustomPayload.pos());
		} else if (payload instanceof DebugGameEventListenersCustomPayload debugGameEventListenersCustomPayload) {
			this.client
				.debugRenderer
				.gameEventDebugRenderer
				.addListener(debugGameEventListenersCustomPayload.listenerPos(), debugGameEventListenersCustomPayload.listenerRange());
		} else if (payload instanceof DebugBreezeCustomPayload debugBreezeCustomPayload) {
			this.client.debugRenderer.breezeDebugRenderer.addBreezeDebugInfo(debugBreezeCustomPayload.breezeInfo());
		} else {
			this.warnOnUnknownPayload(payload);
		}
	}

	private void warnOnUnknownPayload(CustomPayload payload) {
		LOGGER.warn("Unknown custom packet payload: {}", payload.getId().id());
	}

	@Override
	public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		String string = packet.getName();
		if (packet.getMode() == 0) {
			this.scoreboard
				.addObjective(string, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType(), false, (NumberFormat)packet.getNumberFormat().orElse(null));
		} else {
			ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
			if (scoreboardObjective != null) {
				if (packet.getMode() == ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE) {
					this.scoreboard.removeObjective(scoreboardObjective);
				} else if (packet.getMode() == ScoreboardObjectiveUpdateS2CPacket.UPDATE_MODE) {
					scoreboardObjective.setRenderType(packet.getType());
					scoreboardObjective.setDisplayName(packet.getDisplayName());
					scoreboardObjective.setNumberFormat((NumberFormat)packet.getNumberFormat().orElse(null));
				}
			}
		}
	}

	@Override
	public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		String string = packet.objectiveName();
		ScoreHolder scoreHolder = ScoreHolder.fromName(packet.scoreHolderName());
		ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
		if (scoreboardObjective != null) {
			ScoreAccess scoreAccess = this.scoreboard.getOrCreateScore(scoreHolder, scoreboardObjective, true);
			scoreAccess.setScore(packet.score());
			scoreAccess.setDisplayText((Text)packet.display().orElse(null));
			scoreAccess.setNumberFormat((NumberFormat)packet.numberFormat().orElse(null));
		} else {
			LOGGER.warn("Received packet for unknown scoreboard objective: {}", string);
		}
	}

	@Override
	public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		String string = packet.objectiveName();
		ScoreHolder scoreHolder = ScoreHolder.fromName(packet.scoreHolderName());
		if (string == null) {
			this.scoreboard.removeScores(scoreHolder);
		} else {
			ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
			if (scoreboardObjective != null) {
				this.scoreboard.removeScore(scoreHolder, scoreboardObjective);
			} else {
				LOGGER.warn("Received packet for unknown scoreboard objective: {}", string);
			}
		}
	}

	@Override
	public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		String string = packet.getName();
		ScoreboardObjective scoreboardObjective = string == null ? null : this.scoreboard.getNullableObjective(string);
		this.scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
	}

	@Override
	public void onTeam(TeamS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		TeamS2CPacket.Operation operation = packet.getTeamOperation();
		Team team;
		if (operation == TeamS2CPacket.Operation.ADD) {
			team = this.scoreboard.addTeam(packet.getTeamName());
		} else {
			team = this.scoreboard.getTeam(packet.getTeamName());
			if (team == null) {
				LOGGER.warn(
					"Received packet for unknown team {}: team action: {}, player action: {}",
					packet.getTeamName(),
					packet.getTeamOperation(),
					packet.getPlayerListOperation()
				);
				return;
			}
		}

		Optional<TeamS2CPacket.SerializableTeam> optional = packet.getTeam();
		optional.ifPresent(teamx -> {
			team.setDisplayName(teamx.getDisplayName());
			team.setColor(teamx.getColor());
			team.setFriendlyFlagsBitwise(teamx.getFriendlyFlagsBitwise());
			AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(teamx.getNameTagVisibilityRule());
			if (visibilityRule != null) {
				team.setNameTagVisibilityRule(visibilityRule);
			}

			AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(teamx.getCollisionRule());
			if (collisionRule != null) {
				team.setCollisionRule(collisionRule);
			}

			team.setPrefix(teamx.getPrefix());
			team.setSuffix(teamx.getSuffix());
		});
		TeamS2CPacket.Operation operation2 = packet.getPlayerListOperation();
		if (operation2 == TeamS2CPacket.Operation.ADD) {
			for (String string : packet.getPlayerNames()) {
				this.scoreboard.addScoreHolderToTeam(string, team);
			}
		} else if (operation2 == TeamS2CPacket.Operation.REMOVE) {
			for (String string : packet.getPlayerNames()) {
				this.scoreboard.removeScoreHolderFromTeam(string, team);
			}
		}

		if (operation == TeamS2CPacket.Operation.REMOVE) {
			this.scoreboard.removeTeam(team);
		}
	}

	@Override
	public void onParticle(ParticleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getCount() == 0) {
			double d = (double)(packet.getSpeed() * packet.getOffsetX());
			double e = (double)(packet.getSpeed() * packet.getOffsetY());
			double f = (double)(packet.getSpeed() * packet.getOffsetZ());

			try {
				this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX(), packet.getY(), packet.getZ(), d, e, f);
			} catch (Throwable var17) {
				LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
			}
		} else {
			for (int i = 0; i < packet.getCount(); i++) {
				double g = this.random.nextGaussian() * (double)packet.getOffsetX();
				double h = this.random.nextGaussian() * (double)packet.getOffsetY();
				double j = this.random.nextGaussian() * (double)packet.getOffsetZ();
				double k = this.random.nextGaussian() * (double)packet.getSpeed();
				double l = this.random.nextGaussian() * (double)packet.getSpeed();
				double m = this.random.nextGaussian() * (double)packet.getSpeed();

				try {
					this.world.addParticle(packet.getParameters(), packet.isLongDistance(), packet.getX() + g, packet.getY() + h, packet.getZ() + j, k, l, m);
				} catch (Throwable var16) {
					LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
					return;
				}
			}
		}
	}

	@Override
	public void onEntityAttributes(EntityAttributesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AttributeContainer attributeContainer = ((LivingEntity)entity).getAttributes();

				for (EntityAttributesS2CPacket.Entry entry : packet.getEntries()) {
					EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(entry.attribute());
					if (entityAttributeInstance == null) {
						LOGGER.warn("Entity {} does not have attribute {}", entity, entry.attribute().getIdAsString());
					} else {
						entityAttributeInstance.setBaseValue(entry.base());
						entityAttributeInstance.clearModifiers();

						for (EntityAttributeModifier entityAttributeModifier : entry.modifiers()) {
							entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
						}
					}
				}
			}
		}
	}

	@Override
	public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ScreenHandler screenHandler = this.client.player.currentScreenHandler;
		if (screenHandler.syncId == packet.syncId()) {
			if (this.client.currentScreen instanceof RecipeBookProvider recipeBookProvider) {
				recipeBookProvider.onCraftFailed(packet.recipeDisplay());
			}
		}
	}

	@Override
	public void onLightUpdate(LightUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getChunkX();
		int j = packet.getChunkZ();
		LightData lightData = packet.getData();
		this.world.enqueueChunkUpdate(() -> this.readLightData(i, j, lightData, true));
	}

	private void readLightData(int x, int z, LightData data, boolean bl) {
		LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
		BitSet bitSet = data.getInitedSky();
		BitSet bitSet2 = data.getUninitedSky();
		Iterator<byte[]> iterator = data.getSkyNibbles().iterator();
		this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator, bl);
		BitSet bitSet3 = data.getInitedBlock();
		BitSet bitSet4 = data.getUninitedBlock();
		Iterator<byte[]> iterator2 = data.getBlockNibbles().iterator();
		this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2, bl);
		lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
	}

	@Override
	public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ScreenHandler screenHandler = this.client.player.currentScreenHandler;
		if (packet.getSyncId() == screenHandler.syncId && screenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
			merchantScreenHandler.setOffers(packet.getOffers());
			merchantScreenHandler.setExperienceFromServer(packet.getExperience());
			merchantScreenHandler.setLevelProgress(packet.getLevelProgress());
			merchantScreenHandler.setLeveled(packet.isLeveled());
			merchantScreenHandler.setCanRefreshTrades(packet.isRefreshable());
		}
	}

	@Override
	public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.chunkLoadDistance = packet.getDistance();
		this.client.options.setServerViewDistance(this.chunkLoadDistance);
		this.world.getChunkManager().updateLoadDistance(packet.getDistance());
	}

	@Override
	public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.simulationDistance = packet.simulationDistance();
		this.world.setSimulationDistance(this.simulationDistance);
	}

	@Override
	public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
	}

	@Override
	public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.handlePlayerActionResponse(packet.sequence());
	}

	@Override
	public void onBundle(BundleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (Packet<? super ClientPlayPacketListener> packet2 : packet.getPackets()) {
			packet2.apply(this);
		}
	}

	@Override
	public void onProjectilePower(ProjectilePowerS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.world.getEntityById(packet.getEntityId()) instanceof ExplosiveProjectileEntity explosiveProjectileEntity) {
			explosiveProjectileEntity.accelerationPower = packet.getAccelerationPower();
		}
	}

	@Override
	public void onStartChunkSend(StartChunkSendS2CPacket packet) {
		this.chunkBatchSizeCalculator.onStartChunkSend();
	}

	@Override
	public void onChunkSent(ChunkSentS2CPacket packet) {
		this.chunkBatchSizeCalculator.onChunkSent(packet.batchSize());
		this.sendPacket(new AcknowledgeChunksC2SPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
	}

	@Override
	public void onDebugSample(DebugSampleS2CPacket packet) {
		this.client.getDebugHud().set(packet.sample(), packet.debugSampleType());
	}

	@Override
	public void onPingResult(PingResultS2CPacket packet) {
		this.pingMeasurer.onPingResult(packet);
	}

	private void updateLighting(
		int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles, boolean bl
	) {
		for (int i = 0; i < provider.getHeight(); i++) {
			int j = provider.getBottomY() + i;
			boolean bl2 = inited.get(i);
			boolean bl3 = uninited.get(i);
			if (bl2 || bl3) {
				provider.enqueueSectionData(
					type, ChunkSectionPos.from(chunkX, j, chunkZ), bl2 ? new ChunkNibbleArray((byte[])((byte[])nibbles.next()).clone()) : new ChunkNibbleArray()
				);
				if (bl) {
					this.world.scheduleBlockRenders(chunkX, j, chunkZ);
				}
			}
		}
	}

	public ClientConnection getConnection() {
		return this.connection;
	}

	@Override
	public boolean isConnectionOpen() {
		return this.connection.isOpen() && !this.worldCleared;
	}

	public Collection<PlayerListEntry> getListedPlayerListEntries() {
		return this.listedPlayerListEntries;
	}

	public Collection<PlayerListEntry> getPlayerList() {
		return this.playerListEntries.values();
	}

	public Collection<UUID> getPlayerUuids() {
		return this.playerListEntries.keySet();
	}

	@Nullable
	public PlayerListEntry getPlayerListEntry(UUID uuid) {
		return (PlayerListEntry)this.playerListEntries.get(uuid);
	}

	@Nullable
	public PlayerListEntry getPlayerListEntry(String profileName) {
		for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
			if (playerListEntry.getProfile().getName().equals(profileName)) {
				return playerListEntry;
			}
		}

		return null;
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	public ClientAdvancementManager getAdvancementHandler() {
		return this.advancementHandler;
	}

	public CommandDispatcher<CommandSource> getCommandDispatcher() {
		return this.commandDispatcher;
	}

	public ClientWorld getWorld() {
		return this.world;
	}

	public DataQueryHandler getDataQueryHandler() {
		return this.dataQueryHandler;
	}

	public UUID getSessionId() {
		return this.sessionId;
	}

	public Set<RegistryKey<World>> getWorldKeys() {
		return this.worldKeys;
	}

	public DynamicRegistryManager.Immutable getRegistryManager() {
		return this.combinedDynamicRegistries;
	}

	public void acknowledge(SignedMessage message, boolean displayed) {
		MessageSignatureData messageSignatureData = message.signature();
		if (messageSignatureData != null
			&& this.lastSeenMessagesCollector.add(messageSignatureData, displayed)
			&& this.lastSeenMessagesCollector.getMessageCount() > 64) {
			this.sendAcknowledgment();
		}
	}

	private void sendAcknowledgment() {
		int i = this.lastSeenMessagesCollector.resetMessageCount();
		if (i > 0) {
			this.sendPacket(new MessageAcknowledgmentC2SPacket(i));
		}
	}

	public void sendChatMessage(String content) {
		Instant instant = Instant.now();
		long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
		LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
		MessageSignatureData messageSignatureData = this.messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
		this.sendPacket(new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
	}

	public void sendChatCommand(String command) {
		SignedArgumentList<CommandSource> signedArgumentList = SignedArgumentList.of(this.parse(command));
		if (signedArgumentList.arguments().isEmpty()) {
			this.sendPacket(new CommandExecutionC2SPacket(command));
		} else {
			Instant instant = Instant.now();
			long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
			LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
			ArgumentSignatureDataMap argumentSignatureDataMap = ArgumentSignatureDataMap.sign(signedArgumentList, value -> {
				MessageBody messageBody = new MessageBody(value, instant, l, lastSeenMessages.lastSeen());
				return this.messagePacker.pack(messageBody);
			});
			this.sendPacket(new ChatCommandSignedC2SPacket(command, instant, l, argumentSignatureDataMap, lastSeenMessages.update()));
		}
	}

	public boolean sendCommand(String command) {
		if (!SignedArgumentList.isNotEmpty(this.parse(command))) {
			this.sendPacket(new CommandExecutionC2SPacket(command));
			return true;
		} else {
			return false;
		}
	}

	private ParseResults<CommandSource> parse(String command) {
		return this.commandDispatcher.parse(command, this.commandSource);
	}

	public void syncOptions(SyncedClientOptions syncedOptions) {
		if (!syncedOptions.equals(this.syncedOptions)) {
			this.sendPacket(new ClientOptionsC2SPacket(syncedOptions));
			this.syncedOptions = syncedOptions;
		}
	}

	@Override
	public void tick() {
		if (this.session != null && this.client.getProfileKeys().isExpired()) {
			this.fetchProfileKey();
		}

		if (this.profileKeyPairFuture != null && this.profileKeyPairFuture.isDone()) {
			((Optional)this.profileKeyPairFuture.join()).ifPresent(this::updateKeyPair);
			this.profileKeyPairFuture = null;
		}

		this.sendQueuedPackets();
		if (this.client.getDebugHud().shouldShowPacketSizeAndPingCharts()) {
			this.pingMeasurer.ping();
		}

		this.debugSampleSubscriber.tick();
		this.worldSession.tick();
		if (this.worldLoadingState != null) {
			this.worldLoadingState.tick();
		}
	}

	public void fetchProfileKey() {
		this.profileKeyPairFuture = this.client.getProfileKeys().fetchKeyPair();
	}

	private void updateKeyPair(PlayerKeyPair keyPair) {
		if (this.client.uuidEquals(this.profile.getId())) {
			if (this.session == null || !this.session.keyPair().equals(keyPair)) {
				this.session = ClientPlayerSession.create(keyPair);
				this.messagePacker = this.session.createPacker(this.profile.getId());
				this.sendPacket(new PlayerSessionC2SPacket(this.session.toPublicSession().toSerialized()));
			}
		}
	}

	@Nullable
	public ServerInfo getServerInfo() {
		return this.serverInfo;
	}

	public FeatureSet getEnabledFeatures() {
		return this.enabledFeatures;
	}

	public boolean hasFeature(FeatureSet feature) {
		return feature.isSubsetOf(this.getEnabledFeatures());
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public BrewingRecipeRegistry getBrewingRecipeRegistry() {
		return this.brewingRecipeRegistry;
	}

	public FuelRegistry getFuelRegistry() {
		return this.fuelRegistry;
	}

	public void refreshSearchManager() {
		this.searchManager.refresh();
	}

	public SearchManager getSearchManager() {
		return this.searchManager;
	}

	public ServerLinks getServerLinks() {
		return this.serverLinks;
	}
}
