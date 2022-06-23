package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.multiplayer.ChatPreviewWarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.report.ReceivedMessage;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.sound.AbstractBeeSoundInstance;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatPreviewS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatPreviewStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
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
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.TagPacketSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOfferList;
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
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.explosion.Explosion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler implements ClientPlayPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Text DISCONNECT_LOST_TEXT = Text.translatable("disconnect.lost");
	private final ClientConnection connection;
	private final GameProfile profile;
	private final Screen loginScreen;
	private final MinecraftClient client;
	private ClientWorld world;
	private ClientWorld.Properties worldProperties;
	private final Map<UUID, PlayerListEntry> playerListEntries = Maps.<UUID, PlayerListEntry>newHashMap();
	private final ClientAdvancementManager advancementHandler;
	private final ClientCommandSource commandSource;
	private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
	private int chunkLoadDistance = 3;
	private int simulationDistance = 3;
	private final Random random = Random.createThreadSafe();
	private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
	private final RecipeManager recipeManager = new RecipeManager();
	private final UUID sessionId = UUID.randomUUID();
	private Set<RegistryKey<World>> worldKeys;
	private DynamicRegistryManager.Immutable registryManager = (DynamicRegistryManager.Immutable)DynamicRegistryManager.BUILTIN.get();
	private final TelemetrySender telemetrySender;

	public ClientPlayNetworkHandler(MinecraftClient client, Screen screen, ClientConnection connection, GameProfile profile, TelemetrySender telemetrySender) {
		this.client = client;
		this.loginScreen = screen;
		this.connection = connection;
		this.profile = profile;
		this.advancementHandler = new ClientAdvancementManager(client);
		this.commandSource = new ClientCommandSource(this, client);
		this.telemetrySender = telemetrySender;
	}

	public ClientCommandSource getCommandSource() {
		return this.commandSource;
	}

	public void clearWorld() {
		this.world = null;
	}

	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	@Override
	public void onGameJoin(GameJoinS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		this.registryManager = packet.registryManager();
		if (!this.connection.isLocal()) {
			this.registryManager.streamAllRegistries().forEach(entry -> entry.value().clearTags());
		}

		List<RegistryKey<World>> list = Lists.<RegistryKey<World>>newArrayList(packet.dimensionIds());
		Collections.shuffle(list);
		this.worldKeys = Sets.<RegistryKey<World>>newLinkedHashSet(list);
		RegistryKey<World> registryKey = packet.dimensionId();
		RegistryEntry<DimensionType> registryEntry = this.registryManager.get(Registry.DIMENSION_TYPE_KEY).entryOf(packet.dimensionType());
		this.chunkLoadDistance = packet.viewDistance();
		this.simulationDistance = packet.simulationDistance();
		boolean bl = packet.debugWorld();
		boolean bl2 = packet.flatWorld();
		ClientWorld.Properties properties = new ClientWorld.Properties(Difficulty.NORMAL, packet.hardcore(), bl2);
		this.worldProperties = properties;
		this.world = new ClientWorld(
			this,
			properties,
			registryKey,
			registryEntry,
			this.chunkLoadDistance,
			this.simulationDistance,
			this.client::getProfiler,
			this.client.worldRenderer,
			bl,
			packet.sha256Seed()
		);
		this.client.joinWorld(this.world);
		if (this.client.player == null) {
			this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook());
			this.client.player.setYaw(-180.0F);
			if (this.client.getServer() != null) {
				this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
			}
		}

		this.client.debugRenderer.reset();
		this.client.player.init();
		int i = packet.playerEntityId();
		this.client.player.setId(i);
		this.world.addPlayer(i, this.client.player);
		this.client.player.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(this.client.player);
		this.client.cameraEntity = this.client.player;
		this.client.setScreen(new DownloadingTerrainScreen());
		this.client.player.setReducedDebugInfo(packet.reducedDebugInfo());
		this.client.player.setShowsDeathScreen(packet.showDeathScreen());
		this.client.player.setLastDeathPos(packet.lastDeathLocation());
		this.client.interactionManager.setGameModes(packet.gameMode(), packet.previousGameMode());
		this.client.options.setServerViewDistance(packet.viewDistance());
		this.client.options.sendClientSettings();
		this.connection
			.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
		this.client.getGame().onStartGameSession();
		this.telemetrySender.setGameModeAndSend(packet.gameMode(), packet.hardcore());
	}

	@Override
	public void onEntitySpawn(EntitySpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		EntityType<?> entityType = packet.getEntityTypeId();
		Entity entity = entityType.create(this.world);
		if (entity != null) {
			entity.onSpawnPacket(packet);
			int i = packet.getId();
			this.world.addEntity(i, entity);
			this.playSpawnSound(entity);
		} else {
			LOGGER.warn("Skipping Entity with id {}", entityType);
		}
	}

	private void playSpawnSound(Entity entity) {
		if (entity instanceof AbstractMinecartEntity) {
			this.client.getSoundManager().play(new MovingMinecartSoundInstance((AbstractMinecartEntity)entity));
		} else if (entity instanceof BeeEntity) {
			boolean bl = ((BeeEntity)entity).hasAngerTime();
			AbstractBeeSoundInstance abstractBeeSoundInstance;
			if (bl) {
				abstractBeeSoundInstance = new AggressiveBeeSoundInstance((BeeEntity)entity);
			} else {
				abstractBeeSoundInstance = new PassiveBeeSoundInstance((BeeEntity)entity);
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
		entity.setId(packet.getId());
		this.world.addEntity(packet.getId(), entity);
	}

	@Override
	public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			entity.setVelocityClient((double)packet.getVelocityX() / 8000.0, (double)packet.getVelocityY() / 8000.0, (double)packet.getVelocityZ() / 8000.0);
		}
	}

	@Override
	public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.id());
		if (entity != null && packet.getTrackedValues() != null) {
			entity.getDataTracker().writeUpdatedEntries(packet.getTrackedValues());
		}
	}

	@Override
	public void onPlayerSpawn(PlayerSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerListEntry playerListEntry = this.getPlayerListEntry(packet.getPlayerUuid());
		if (playerListEntry == null) {
			LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", packet.getPlayerUuid());
		} else {
			double d = packet.getX();
			double e = packet.getY();
			double f = packet.getZ();
			float g = (float)(packet.getYaw() * 360) / 256.0F;
			float h = (float)(packet.getPitch() * 360) / 256.0F;
			int i = packet.getId();
			OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(
				this.client.world, playerListEntry.getProfile(), playerListEntry.getPublicKeyData()
			);
			otherClientPlayerEntity.setId(i);
			otherClientPlayerEntity.updateTrackedPosition(d, e, f);
			otherClientPlayerEntity.updatePositionAndAngles(d, e, f, g, h);
			otherClientPlayerEntity.resetPosition();
			this.world.addPlayer(i, otherClientPlayerEntity);
		}
	}

	@Override
	public void onEntityPosition(EntityPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			double d = packet.getX();
			double e = packet.getY();
			double f = packet.getZ();
			entity.updateTrackedPosition(d, e, f);
			if (!entity.isLogicalSideForUpdatingMovement()) {
				float g = (float)(packet.getYaw() * 360) / 256.0F;
				float h = (float)(packet.getPitch() * 360) / 256.0F;
				entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
				entity.setOnGround(packet.isOnGround());
			}
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
			if (!entity.isLogicalSideForUpdatingMovement()) {
				if (packet.isPositionChanged()) {
					TrackedPosition trackedPosition = entity.getTrackedPosition();
					Vec3d vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
					trackedPosition.setPos(vec3d);
					float f = packet.hasRotation() ? (float)(packet.getYaw() * 360) / 256.0F : entity.getYaw();
					float g = packet.hasRotation() ? (float)(packet.getPitch() * 360) / 256.0F : entity.getPitch();
					entity.updateTrackedPositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), f, g, 3, false);
				} else if (packet.hasRotation()) {
					float h = (float)(packet.getYaw() * 360) / 256.0F;
					float i = (float)(packet.getPitch() * 360) / 256.0F;
					entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), h, i, 3, false);
				}

				entity.setOnGround(packet.isOnGround());
			}
		}
	}

	@Override
	public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			float f = (float)(packet.getHeadYaw() * 360) / 256.0F;
			entity.updateTrackedHeadRotation(f, 3);
		}
	}

	@Override
	public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.getEntityIds().forEach(entityId -> this.world.removeEntity(entityId, Entity.RemovalReason.DISCARDED));
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (packet.shouldDismount()) {
			playerEntity.dismountVehicle();
		}

		Vec3d vec3d = playerEntity.getVelocity();
		boolean bl = packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X);
		boolean bl2 = packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y);
		boolean bl3 = packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Z);
		double d;
		double e;
		if (bl) {
			d = vec3d.getX();
			e = playerEntity.getX() + packet.getX();
			playerEntity.lastRenderX = playerEntity.lastRenderX + packet.getX();
		} else {
			d = 0.0;
			e = packet.getX();
			playerEntity.lastRenderX = e;
		}

		double f;
		double g;
		if (bl2) {
			f = vec3d.getY();
			g = playerEntity.getY() + packet.getY();
			playerEntity.lastRenderY = playerEntity.lastRenderY + packet.getY();
		} else {
			f = 0.0;
			g = packet.getY();
			playerEntity.lastRenderY = g;
		}

		double h;
		double i;
		if (bl3) {
			h = vec3d.getZ();
			i = playerEntity.getZ() + packet.getZ();
			playerEntity.lastRenderZ = playerEntity.lastRenderZ + packet.getZ();
		} else {
			h = 0.0;
			i = packet.getZ();
			playerEntity.lastRenderZ = i;
		}

		playerEntity.setPos(e, g, i);
		playerEntity.prevX = e;
		playerEntity.prevY = g;
		playerEntity.prevZ = i;
		playerEntity.setVelocity(d, f, h);
		float j = packet.getYaw();
		float k = packet.getPitch();
		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
			k += playerEntity.getPitch();
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			j += playerEntity.getYaw();
		}

		playerEntity.updatePositionAndAngles(e, g, i, j, k);
		this.connection.send(new TeleportConfirmC2SPacket(packet.getTeleportId()));
		this.connection
			.send(new PlayerMoveC2SPacket.Full(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), playerEntity.getYaw(), playerEntity.getPitch(), false));
	}

	@Override
	public void onChatPreview(ChatPreviewS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ChatScreen chatScreen = this.client.inGameHud.getChatHud().getChatScreen();
		if (chatScreen != null) {
			chatScreen.getChatPreviewer().onResponse(packet.queryId(), packet.preview());
		}
	}

	@Override
	public void onChatPreviewStateChange(ChatPreviewStateChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ServerInfo serverInfo = this.client.getCurrentServerEntry();
		if (serverInfo != null) {
			serverInfo.setTemporaryChatPreviewState(packet.enabled());
		}
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = Block.NOTIFY_ALL | Block.FORCE_STATE | (packet.shouldSkipLightingUpdates() ? Block.SKIP_LIGHTING_UPDATES : 0);
		packet.visitUpdates((pos, state) -> this.world.handleBlockUpdate(pos, state, i));
	}

	@Override
	public void onChunkData(ChunkDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.loadChunk(packet.getX(), packet.getZ(), packet.getChunkData());
		this.updateChunk(packet.getX(), packet.getZ(), packet.getLightData());
	}

	private void loadChunk(int x, int z, ChunkData chunkData) {
		this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
	}

	private void updateChunk(int x, int z, LightData lightData) {
		this.world.enqueueChunkUpdate(() -> {
			this.readLightData(x, z, lightData);
			WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(x, z, false);
			if (worldChunk != null) {
				this.scheduleRenderChunk(worldChunk, x, z);
			}
		});
	}

	private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
		LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
		ChunkSection[] chunkSections = chunk.getSectionArray();
		ChunkPos chunkPos = chunk.getPos();
		lightingProvider.setColumnEnabled(chunkPos, true);

		for (int i = 0; i < chunkSections.length; i++) {
			ChunkSection chunkSection = chunkSections[i];
			int j = this.world.sectionIndexToCoord(i);
			lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
			this.world.scheduleBlockRenders(x, j, z);
		}

		this.world.markChunkRenderability(x, z);
	}

	@Override
	public void onUnloadChunk(UnloadChunkS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getX();
		int j = packet.getZ();
		ClientChunkManager clientChunkManager = this.world.getChunkManager();
		clientChunkManager.unload(i, j);
		this.unloadChunk(packet);
	}

	private void unloadChunk(UnloadChunkS2CPacket packet) {
		this.world.enqueueChunkUpdate(() -> {
			LightingProvider lightingProvider = this.world.getLightingProvider();

			for (int i = this.world.getBottomSectionCoord(); i < this.world.getTopSectionCoord(); i++) {
				lightingProvider.setSectionStatus(ChunkSectionPos.from(packet.getX(), i, packet.getZ()), true);
			}

			lightingProvider.setColumnEnabled(new ChunkPos(packet.getX(), packet.getZ()), false);
			this.world.markChunkRenderability(packet.getX(), packet.getZ());
		});
	}

	@Override
	public void onBlockUpdate(BlockUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.handleBlockUpdate(packet.getPos(), packet.getState(), 19);
	}

	@Override
	public void onDisconnect(DisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	@Override
	public void onDisconnected(Text reason) {
		this.client.disconnect();
		this.telemetrySender.send();
		if (this.loginScreen != null) {
			if (this.loginScreen instanceof RealmsScreen) {
				this.client.setScreen(new DisconnectedRealmsScreen(this.loginScreen, DISCONNECT_LOST_TEXT, reason));
			} else {
				this.client.setScreen(new DisconnectedScreen(this.loginScreen, DISCONNECT_LOST_TEXT, reason));
			}
		} else {
			this.client.setScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), DISCONNECT_LOST_TEXT, reason));
		}
	}

	/**
	 * Sends a packet to the server.
	 * 
	 * @param packet the packet to send
	 */
	public void sendPacket(Packet<?> packet) {
		this.connection.send(packet);
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
				itemStack.decrement(packet.getStackAmount());
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
		if (!this.client.options.getHideMatchedNames().getValue() || !this.client.shouldBlockMessages(this.extractSender(packet.content()))) {
			Registry<MessageType> registry = this.registryManager.get(Registry.MESSAGE_TYPE_KEY);
			MessageType messageType = packet.getMessageType(registry);
			this.client.inGameHud.onGameMessage(messageType, packet.content());
			Instant instant = Instant.now();
			this.client.getAbuseReportContext().chatLog().add(ReceivedMessage.of(packet.content(), instant));
		}
	}

	private UUID extractSender(Text content) {
		String string = TextVisitFactory.removeFormattingCodes(content);
		String string2 = StringUtils.substringBetween(string, "<", ">");
		return string2 == null ? Util.NIL_UUID : this.client.getSocialInteractionsManager().getUuid(string2);
	}

	@Override
	public void onChatMessage(ChatMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		MessageSender messageSender = packet.sender();
		if (packet.isExpired(Instant.now())) {
			LOGGER.warn("Received expired chat packet from {}", messageSender.name().getString());
		}

		Registry<MessageType> registry = this.registryManager.get(Registry.MESSAGE_TYPE_KEY);
		MessageType messageType = packet.getMessageType(registry);
		SignedMessage signedMessage = packet.getSignedMessage();
		this.handleMessage(messageType, signedMessage, messageSender);
	}

	/**
	 * Handles an incoming chat message.
	 */
	private void handleMessage(MessageType type, SignedMessage message, MessageSender sender) {
		if (!this.client.shouldBlockMessages(sender.uuid())) {
			boolean bl = this.client.options.getOnlyShowSecureChat().getValue();
			PlayerListEntry playerListEntry = this.getPlayerListEntry(message.signature().sender());
			if (playerListEntry != null && !this.isSignatureValid(message, playerListEntry)) {
				LOGGER.warn("Received chat packet without valid signature from {}", playerListEntry.getProfile().getName());
				if (bl) {
					return;
				}
			}

			SignedMessage signedMessage = bl ? message.withoutUnsigned() : message;
			Text text = signedMessage.getContent();
			this.client.inGameHud.onChatMessage(type, text, sender);
			GameProfile gameProfile = this.getProfile(sender);
			this.client.getAbuseReportContext().chatLog().add(ReceivedMessage.of(gameProfile, sender.name(), signedMessage));
		}
	}

	/**
	 * {@return the game profile of {@code sender}}
	 * 
	 * <p>If {@code sender} points to a non-player (such as entities sending
	 * chat messages through {@code /say} command) or a player not in the game,
	 * this will create a new game profile based on the UUID and the display
	 * name.
	 */
	private GameProfile getProfile(MessageSender sender) {
		PlayerListEntry playerListEntry = this.getPlayerListEntry(sender.uuid());
		return playerListEntry == null ? new GameProfile(sender.uuid(), sender.name().getString()) : playerListEntry.getProfile();
	}

	/**
	 * {@return whether the chat message has a valid signature}
	 * 
	 * <p>This returns {@code false} when the chat sender is unknown.
	 */
	private boolean isSignatureValid(SignedMessage message, PlayerListEntry playerListEntry) {
		PlayerPublicKey playerPublicKey = playerListEntry.getPublicKeyData();
		return playerPublicKey != null && message.verify(playerPublicKey);
	}

	@Override
	public void onEntityAnimation(EntityAnimationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			if (packet.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN_HAND);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.SWING_OFF_HAND) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.OFF_HAND);
			} else if (packet.getAnimationId() == EntityAnimationS2CPacket.DAMAGE) {
				entity.animateDamage();
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
	public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setTime(packet.getTime());
		this.client.world.setTimeOfDay(packet.getTimeOfDay());
	}

	@Override
	public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setSpawnPos(packet.getPos(), packet.getAngle());
		if (this.client.currentScreen instanceof DownloadingTerrainScreen downloadingTerrainScreen) {
			downloadingTerrainScreen.setReady();
		}
	}

	@Override
	public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.hasPassengerDeep(this.client.player);
			entity.removeAllPassengers();

			for (int i : packet.getPassengerIds()) {
				Entity entity2 = this.world.getEntityById(i);
				if (entity2 != null) {
					entity2.startRiding(entity, true);
					if (entity2 == this.client.player && !bl) {
						if (entity instanceof BoatEntity) {
							this.client.player.prevYaw = entity.getYaw();
							this.client.player.setYaw(entity.getYaw());
							this.client.player.setHeadYaw(entity.getYaw());
						}

						Text text = Text.translatable("mount.onboard", this.client.options.sneakKey.getBoundKeyLocalizedText());
						this.client.inGameHud.setOverlayMessage(text, false);
						NarratorManager.INSTANCE.narrate(text);
					}
				}
			}
		}
	}

	@Override
	public void onEntityAttach(EntityAttachS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
		if (entity instanceof MobEntity) {
			((MobEntity)entity).setHoldingEntityId(packet.getHoldingEntityId());
		}
	}

	private static ItemStack getActiveTotemOfUndying(PlayerEntity player) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (itemStack.isOf(Items.TOTEM_OF_UNDYING)) {
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
			if (packet.getStatus() == EntityStatuses.PLAY_GUARDIAN_ATTACK_SOUND) {
				this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else if (packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
				int i = 40;
				this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
				this.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.gameRenderer.showFloatingItem(getActiveTotemOfUndying(this.client.player));
				}
			} else {
				entity.handleStatus(packet.getStatus());
			}
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
		RegistryKey<World> registryKey = packet.getDimension();
		RegistryEntry<DimensionType> registryEntry = this.registryManager.get(Registry.DIMENSION_TYPE_KEY).entryOf(packet.getDimensionType());
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		int i = clientPlayerEntity.getId();
		if (registryKey != clientPlayerEntity.world.getRegistryKey()) {
			Scoreboard scoreboard = this.world.getScoreboard();
			Map<String, MapState> map = this.world.getMapStates();
			boolean bl = packet.isDebugWorld();
			boolean bl2 = packet.isFlatWorld();
			ClientWorld.Properties properties = new ClientWorld.Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), bl2);
			this.worldProperties = properties;
			this.world = new ClientWorld(
				this,
				properties,
				registryKey,
				registryEntry,
				this.chunkLoadDistance,
				this.simulationDistance,
				this.client::getProfiler,
				this.client.worldRenderer,
				bl,
				packet.getSha256Seed()
			);
			this.world.setScoreboard(scoreboard);
			this.world.putMapStates(map);
			this.client.joinWorld(this.world);
			this.client.setScreen(new DownloadingTerrainScreen());
		}

		String string = clientPlayerEntity.getServerBrand();
		this.client.cameraEntity = null;
		ClientPlayerEntity clientPlayerEntity2 = this.client
			.interactionManager
			.createPlayer(
				this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook(), clientPlayerEntity.isSneaking(), clientPlayerEntity.isSprinting()
			);
		clientPlayerEntity2.setId(i);
		this.client.player = clientPlayerEntity2;
		if (registryKey != clientPlayerEntity.world.getRegistryKey()) {
			this.client.getMusicTracker().stop();
		}

		this.client.cameraEntity = clientPlayerEntity2;
		clientPlayerEntity2.getDataTracker().writeUpdatedEntries(clientPlayerEntity.getDataTracker().getAllEntries());
		if (packet.shouldKeepPlayerAttributes()) {
			clientPlayerEntity2.getAttributes().setFrom(clientPlayerEntity.getAttributes());
		}

		clientPlayerEntity2.init();
		clientPlayerEntity2.setServerBrand(string);
		this.world.addPlayer(i, clientPlayerEntity2);
		clientPlayerEntity2.setYaw(-180.0F);
		clientPlayerEntity2.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(clientPlayerEntity2);
		clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.hasReducedDebugInfo());
		clientPlayerEntity2.setShowsDeathScreen(clientPlayerEntity.showsDeathScreen());
		clientPlayerEntity2.setLastDeathPos(packet.getLastDeathPos());
		if (this.client.currentScreen instanceof DeathScreen) {
			this.client.setScreen(null);
		}

		this.client.interactionManager.setGameModes(packet.getGameMode(), packet.getPreviousGameMode());
	}

	@Override
	public void onExplosion(ExplosionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Explosion explosion = new Explosion(this.client.world, null, packet.getX(), packet.getY(), packet.getZ(), packet.getRadius(), packet.getAffectedBlocks());
		explosion.affectWorld(true);
		this.client
			.player
			.setVelocity(
				this.client.player.getVelocity().add((double)packet.getPlayerVelocityX(), (double)packet.getPlayerVelocityY(), (double)packet.getPlayerVelocityZ())
			);
	}

	@Override
	public void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getHorseId());
		if (entity instanceof AbstractHorseEntity) {
			ClientPlayerEntity clientPlayerEntity = this.client.player;
			AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
			SimpleInventory simpleInventory = new SimpleInventory(packet.getSlotCount());
			HorseScreenHandler horseScreenHandler = new HorseScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), simpleInventory, abstractHorseEntity);
			clientPlayerEntity.currentScreenHandler = horseScreenHandler;
			this.client.setScreen(new HorseScreen(horseScreenHandler, clientPlayerEntity.getInventory(), abstractHorseEntity));
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
		ItemStack itemStack = packet.getItemStack();
		int i = packet.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		if (packet.getSyncId() == ScreenHandlerSlotUpdateS2CPacket.UPDATE_CURSOR_SYNC_ID) {
			if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
				playerEntity.currentScreenHandler.setCursorStack(itemStack);
			}
		} else if (packet.getSyncId() == ScreenHandlerSlotUpdateS2CPacket.UPDATE_PLAYER_INVENTORY_SYNC_ID) {
			playerEntity.getInventory().setStack(i, itemStack);
		} else {
			boolean bl = false;
			if (this.client.currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
				bl = creativeInventoryScreen.getSelectedTab() != ItemGroup.INVENTORY.getIndex();
			}

			if (packet.getSyncId() == 0 && PlayerScreenHandler.isInHotbar(i)) {
				if (!itemStack.isEmpty()) {
					ItemStack itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack();
					if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
						itemStack.setBobbingAnimationTime(5);
					}
				}

				playerEntity.playerScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
			} else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && (packet.getSyncId() != 0 || !bl)) {
				playerEntity.currentScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
			}
		}
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
		BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
		if (!(blockEntity instanceof SignBlockEntity)) {
			BlockState blockState = this.world.getBlockState(blockPos);
			blockEntity = new SignBlockEntity(blockPos, blockState);
			blockEntity.setWorld(this.world);
		}

		this.client.player.openEditSignScreen((SignBlockEntity)blockEntity);
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		BlockPos blockPos = packet.getPos();
		this.client.world.getBlockEntity(blockPos, packet.getBlockEntityType()).ifPresent(blockEntity -> {
			NbtCompound nbtCompound = packet.getNbt();
			if (nbtCompound != null) {
				blockEntity.readNbt(nbtCompound);
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
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			packet.getEquipmentList().forEach(pair -> entity.equipStack((EquipmentSlot)pair.getFirst(), (ItemStack)pair.getSecond()));
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
			if (i == 0) {
				this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
				this.client.setScreen(new DownloadingTerrainScreen());
			} else if (i == 1) {
				this.client
					.setScreen(
						new CreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN)))
					);
			}
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
		}
	}

	@Override
	public void onMapUpdate(MapUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
		int i = packet.getId();
		String string = FilledMapItem.getMapName(i);
		MapState mapState = this.client.world.getMapState(string);
		if (mapState == null) {
			mapState = MapState.of(packet.getScale(), packet.isLocked(), this.client.world.getRegistryKey());
			this.client.world.putMapState(string, mapState);
		}

		packet.apply(mapState);
		mapRenderer.updateTexture(i, mapState);
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
			Advancement advancement = this.advancementHandler.getManager().get(identifier);
			this.advancementHandler.selectTab(advancement, false);
		}
	}

	@Override
	public void onCommandTree(CommandTreeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(packet.getCommandTree(new CommandRegistryAccess(this.registryManager)));
	}

	@Override
	public void onStopSound(StopSoundS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
	}

	@Override
	public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.commandSource.onCommandSuggestions(packet.getCompletionId(), packet.getSuggestions());
	}

	@Override
	public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.recipeManager.setRecipes(packet.getRecipes());
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.reload(this.recipeManager.values());
		this.client.reloadSearchProvider(SearchManager.RECIPE_OUTPUT, clientRecipeBook.getOrderedResults());
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

		for (Entry<Stat<?>, Integer> entry : packet.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStatHandler().setStat(this.client.player, stat, i);
		}

		if (this.client.currentScreen instanceof StatsListener) {
			((StatsListener)this.client.currentScreen).onStatsReady();
		}
	}

	@Override
	public void onUnlockRecipes(UnlockRecipesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setOptions(packet.getOptions());
		UnlockRecipesS2CPacket.Action action = packet.getAction();
		switch (action) {
			case REMOVE:
				for (Identifier identifier : packet.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::remove);
				}
				break;
			case INIT:
				for (Identifier identifier : packet.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::add);
				}

				for (Identifier identifier : packet.getRecipeIdsToInit()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::display);
				}
				break;
			case ADD:
				for (Identifier identifier : packet.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(recipe -> {
						clientRecipeBook.add(recipe);
						clientRecipeBook.display(recipe);
						RecipeToast.show(this.client.getToastManager(), recipe);
					});
				}
		}

		clientRecipeBook.getOrderedResults().forEach(recipeResultCollection -> recipeResultCollection.initialize(clientRecipeBook));
		if (this.client.currentScreen instanceof RecipeBookProvider) {
			((RecipeBookProvider)this.client.currentScreen).refreshRecipeBook();
		}
	}

	@Override
	public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = packet.getEffectId();
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect,
					packet.getDuration(),
					packet.getAmplifier(),
					packet.isAmbient(),
					packet.shouldShowParticles(),
					packet.shouldShowIcon(),
					null,
					Optional.ofNullable(packet.getFactorCalculationData())
				);
				statusEffectInstance.setPermanent(packet.isPermanent());
				((LivingEntity)entity).setStatusEffect(statusEffectInstance, null);
			}
		}
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.getGroups().forEach(this::loadTags);
		if (!this.connection.isLocal()) {
			Blocks.refreshShapeCache();
		}

		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Item item : Registry.ITEM) {
			item.appendStacks(ItemGroup.SEARCH, defaultedList);
		}

		this.client.reloadSearchProvider(SearchManager.ITEM_TOOLTIP, defaultedList);
		this.client.reloadSearchProvider(SearchManager.ITEM_TAG, defaultedList);
	}

	private <T> void loadTags(RegistryKey<? extends Registry<? extends T>> registryKey, TagPacketSerializer.Serialized serialized) {
		if (!serialized.isEmpty()) {
			Registry<T> registry = (Registry<T>)this.registryManager
				.getOptional(registryKey)
				.orElseThrow(() -> new IllegalStateException("Unknown registry " + registryKey));
			Map<TagKey<T>, List<RegistryEntry<T>>> map = new HashMap();
			TagPacketSerializer.loadTags(registryKey, registry, serialized, map::put);
			registry.populateTags(map);
		}
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
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity == this.client.player) {
			if (this.client.player.showsDeathScreen()) {
				this.client.setScreen(new DeathScreen(packet.getMessage(), this.world.getLevelProperties().isHardcore()));
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
		ServerInfo serverInfo = this.client.getCurrentServerEntry();
		if (serverInfo != null) {
			packet.getDescription().ifPresent(description -> serverInfo.label = description);
			packet.getFavicon().ifPresent(favicon -> {
				try {
					serverInfo.setIcon(ServerInfo.parseFavicon(favicon));
				} catch (ParseException var3x) {
					LOGGER.error("Invalid server icon", (Throwable)var3x);
				}
			});
			serverInfo.setPreviewsChat(packet.shouldPreviewChat());
			ServerList.updateServerListEntry(serverInfo);
			if (this.client.options.getChatPreview().getValue()) {
				ServerInfo.ChatPreview chatPreview = serverInfo.getChatPreview();
				if (chatPreview != null && !chatPreview.isAcknowledged()) {
					this.client.execute(() -> this.client.setScreen(new ChatPreviewWarningScreen(this.client.currentScreen, serverInfo)));
				}
			}
		}
	}

	@Override
	public void onOverlayMessage(OverlayMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setOverlayMessage(packet.getMessage(), false);
	}

	@Override
	public void onTitle(TitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setTitle(packet.getTitle());
	}

	@Override
	public void onSubtitle(SubtitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setSubtitle(packet.getSubtitle());
	}

	@Override
	public void onTitleFade(TitleFadeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.setTitleTicks(packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.getPlayerListHud().setHeader(packet.getHeader().getString().isEmpty() ? null : packet.getHeader());
		this.client.inGameHud.getPlayerListHud().setFooter(packet.getFooter().getString().isEmpty() ? null : packet.getFooter());
	}

	@Override
	public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).removeStatusEffectInternal(packet.getEffectType());
		}
	}

	@Override
	public void onPlayerList(PlayerListS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
			if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
				this.client.getSocialInteractionsManager().setPlayerOffline(entry.getProfile().getId());
				this.playerListEntries.remove(entry.getProfile().getId());
			} else {
				PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.getProfile().getId());
				if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
					playerListEntry = new PlayerListEntry(entry, this.client.getServicesSignatureVerifier());
					this.playerListEntries.put(playerListEntry.getProfile().getId(), playerListEntry);
					this.client.getSocialInteractionsManager().setPlayerOnline(playerListEntry);
				}

				if (playerListEntry != null) {
					switch (packet.getAction()) {
						case ADD_PLAYER:
							playerListEntry.setGameMode(entry.getGameMode());
							playerListEntry.setLatency(entry.getLatency());
							playerListEntry.setDisplayName(entry.getDisplayName());
							break;
						case UPDATE_GAME_MODE:
							playerListEntry.setGameMode(entry.getGameMode());
							break;
						case UPDATE_LATENCY:
							playerListEntry.setLatency(entry.getLatency());
							break;
						case UPDATE_DISPLAY_NAME:
							playerListEntry.setDisplayName(entry.getDisplayName());
					}
				}
			}
		}
	}

	@Override
	public void onKeepAlive(KeepAliveS2CPacket packet) {
		this.sendPacket(new KeepAliveC2SPacket(packet.getId()));
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
	public void onPlaySoundId(PlaySoundIdS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client
			.getSoundManager()
			.play(
				new PositionedSoundInstance(
					packet.getSoundId(),
					packet.getCategory(),
					packet.getVolume(),
					packet.getPitch(),
					Random.create(packet.getSeed()),
					false,
					0,
					SoundInstance.AttenuationType.LINEAR,
					packet.getX(),
					packet.getY(),
					packet.getZ(),
					false
				)
			);
	}

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
		URL uRL = resolveUrl(packet.getURL());
		if (uRL == null) {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
		} else {
			String string = packet.getSHA1();
			boolean bl = packet.isRequired();
			ServerInfo serverInfo = this.client.getCurrentServerEntry();
			if (serverInfo != null && serverInfo.getResourcePackPolicy() == ServerInfo.ResourcePackPolicy.ENABLED) {
				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
				this.feedbackAfterDownload(this.client.getResourcePackProvider().download(uRL, string, true));
			} else if (serverInfo != null
				&& serverInfo.getResourcePackPolicy() != ServerInfo.ResourcePackPolicy.PROMPT
				&& (!bl || serverInfo.getResourcePackPolicy() != ServerInfo.ResourcePackPolicy.DISABLED)) {
				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
				if (bl) {
					this.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
				}
			} else {
				this.client
					.execute(
						() -> this.client
								.setScreen(
									new ConfirmScreen(
										enabled -> {
											this.client.setScreen(null);
											ServerInfo serverInfox = this.client.getCurrentServerEntry();
											if (enabled) {
												if (serverInfox != null) {
													serverInfox.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
												}

												this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
												this.feedbackAfterDownload(this.client.getResourcePackProvider().download(uRL, string, true));
											} else {
												this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
												if (bl) {
													this.connection.disconnect(Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
												} else if (serverInfox != null) {
													serverInfox.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
												}
											}

											if (serverInfox != null) {
												ServerList.updateServerListEntry(serverInfox);
											}
										},
										bl ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"),
										getServerResourcePackPrompt(
											bl
												? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD)
												: Text.translatable("multiplayer.texturePrompt.line2"),
											packet.getPrompt()
										),
										bl ? ScreenTexts.PROCEED : ScreenTexts.YES,
										(Text)(bl ? Text.translatable("menu.disconnect") : ScreenTexts.NO)
									)
								)
					);
			}
		}
	}

	private static Text getServerResourcePackPrompt(Text defaultPrompt, @Nullable Text customPrompt) {
		return (Text)(customPrompt == null ? defaultPrompt : Text.translatable("multiplayer.texturePrompt.serverPrompt", defaultPrompt, customPrompt));
	}

	@Nullable
	private static URL resolveUrl(String url) {
		try {
			URL uRL = new URL(url);
			String string = uRL.getProtocol();
			return !"http".equals(string) && !"https".equals(string) ? null : uRL;
		} catch (MalformedURLException var3) {
			return null;
		}
	}

	private void feedbackAfterDownload(CompletableFuture<?> downloadFuture) {
		downloadFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED)).exceptionally(throwable -> {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
			return null;
		});
	}

	private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status packStatus) {
		this.connection.send(new ResourcePackStatusC2SPacket(packStatus));
	}

	@Override
	public void onBossBar(BossBarS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.getBossBarHud().handlePacket(packet);
	}

	@Override
	public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.getCooldown() == 0) {
			this.client.player.getItemCooldownManager().remove(packet.getItem());
		} else {
			this.client.player.getItemCooldownManager().set(packet.getItem(), packet.getCooldown());
		}
	}

	@Override
	public void onVehicleMove(VehicleMoveS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.client.player.getRootVehicle();
		if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
			entity.updatePositionAndAngles(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
			this.connection.send(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
		if (itemStack.isOf(Items.WRITTEN_BOOK)) {
			this.client.setScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
		}
	}

	@Override
	public void onCustomPayload(CustomPayloadS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Identifier identifier = packet.getChannel();
		PacketByteBuf packetByteBuf = null;

		try {
			packetByteBuf = packet.getData();
			if (CustomPayloadS2CPacket.BRAND.equals(identifier)) {
				String string = packetByteBuf.readString();
				this.client.player.setServerBrand(string);
				this.telemetrySender.setServerBrandAndSend(string);
			} else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(identifier)) {
				int i = packetByteBuf.readInt();
				float f = packetByteBuf.readFloat();
				Path path = Path.fromBuffer(packetByteBuf);
				this.client.debugRenderer.pathfindingDebugRenderer.addPath(i, path, f);
			} else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
				long l = packetByteBuf.readVarLong();
				BlockPos blockPos = packetByteBuf.readBlockPos();
				((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).addNeighborUpdate(l, blockPos);
			} else if (CustomPayloadS2CPacket.DEBUG_STRUCTURES.equals(identifier)) {
				DimensionType dimensionType = this.registryManager.get(Registry.DIMENSION_TYPE_KEY).get(packetByteBuf.readIdentifier());
				BlockBox blockBox = new BlockBox(
					packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
				);
				int j = packetByteBuf.readInt();
				List<BlockBox> list = Lists.<BlockBox>newArrayList();
				List<Boolean> list2 = Lists.<Boolean>newArrayList();

				for (int k = 0; k < j; k++) {
					list.add(
						new BlockBox(
							packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
						)
					);
					list2.add(packetByteBuf.readBoolean());
				}

				this.client.debugRenderer.structureDebugRenderer.addStructure(blockBox, list, list2, dimensionType);
			} else if (CustomPayloadS2CPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier)) {
				((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer)
					.addBox(
						packetByteBuf.readBlockPos(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat()
					);
			} else if (CustomPayloadS2CPacket.DEBUG_VILLAGE_SECTIONS.equals(identifier)) {
				int i = packetByteBuf.readInt();

				for (int m = 0; m < i; m++) {
					this.client.debugRenderer.villageSectionsDebugRenderer.addSection(packetByteBuf.readChunkSectionPos());
				}

				int m = packetByteBuf.readInt();

				for (int j = 0; j < m; j++) {
					this.client.debugRenderer.villageSectionsDebugRenderer.removeSection(packetByteBuf.readChunkSectionPos());
				}
			} else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				String string2 = packetByteBuf.readString();
				int j = packetByteBuf.readInt();
				VillageDebugRenderer.PointOfInterest pointOfInterest = new VillageDebugRenderer.PointOfInterest(blockPos2, string2, j);
				this.client.debugRenderer.villageDebugRenderer.addPointOfInterest(pointOfInterest);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(blockPos2);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int m = packetByteBuf.readInt();
				this.client.debugRenderer.villageDebugRenderer.setFreeTicketCount(blockPos2, m);
			} else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int m = packetByteBuf.readInt();
				int j = packetByteBuf.readInt();
				List<GoalSelectorDebugRenderer.GoalSelector> list = Lists.<GoalSelectorDebugRenderer.GoalSelector>newArrayList();

				for (int n = 0; n < j; n++) {
					int k = packetByteBuf.readInt();
					boolean bl = packetByteBuf.readBoolean();
					String string3 = packetByteBuf.readString(255);
					list.add(new GoalSelectorDebugRenderer.GoalSelector(blockPos2, k, string3, bl));
				}

				this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(m, list);
			} else if (CustomPayloadS2CPacket.DEBUG_RAIDS.equals(identifier)) {
				int i = packetByteBuf.readInt();
				Collection<BlockPos> collection = Lists.<BlockPos>newArrayList();

				for (int j = 0; j < i; j++) {
					collection.add(packetByteBuf.readBlockPos());
				}

				this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(collection);
			} else if (CustomPayloadS2CPacket.DEBUG_BRAIN.equals(identifier)) {
				double d = packetByteBuf.readDouble();
				double e = packetByteBuf.readDouble();
				double g = packetByteBuf.readDouble();
				Position position = new PositionImpl(d, e, g);
				UUID uUID = packetByteBuf.readUuid();
				int o = packetByteBuf.readInt();
				String string4 = packetByteBuf.readString();
				String string5 = packetByteBuf.readString();
				int p = packetByteBuf.readInt();
				float h = packetByteBuf.readFloat();
				float q = packetByteBuf.readFloat();
				String string6 = packetByteBuf.readString();
				Path path2 = packetByteBuf.readNullable(Path::fromBuffer);
				boolean bl2 = packetByteBuf.readBoolean();
				int r = packetByteBuf.readInt();
				VillageDebugRenderer.Brain brain = new VillageDebugRenderer.Brain(uUID, o, string4, string5, p, h, q, position, string6, path2, bl2, r);
				int s = packetByteBuf.readVarInt();

				for (int t = 0; t < s; t++) {
					String string7 = packetByteBuf.readString();
					brain.possibleActivities.add(string7);
				}

				int t = packetByteBuf.readVarInt();

				for (int u = 0; u < t; u++) {
					String string8 = packetByteBuf.readString();
					brain.runningTasks.add(string8);
				}

				int u = packetByteBuf.readVarInt();

				for (int v = 0; v < u; v++) {
					String string9 = packetByteBuf.readString();
					brain.memories.add(string9);
				}

				int v = packetByteBuf.readVarInt();

				for (int w = 0; w < v; w++) {
					BlockPos blockPos3 = packetByteBuf.readBlockPos();
					brain.pointsOfInterest.add(blockPos3);
				}

				int w = packetByteBuf.readVarInt();

				for (int x = 0; x < w; x++) {
					BlockPos blockPos4 = packetByteBuf.readBlockPos();
					brain.potentialJobSites.add(blockPos4);
				}

				int x = packetByteBuf.readVarInt();

				for (int y = 0; y < x; y++) {
					String string10 = packetByteBuf.readString();
					brain.gossips.add(string10);
				}

				this.client.debugRenderer.villageDebugRenderer.addBrain(brain);
			} else if (CustomPayloadS2CPacket.DEBUG_BEE.equals(identifier)) {
				double d = packetByteBuf.readDouble();
				double e = packetByteBuf.readDouble();
				double g = packetByteBuf.readDouble();
				Position position = new PositionImpl(d, e, g);
				UUID uUID = packetByteBuf.readUuid();
				int o = packetByteBuf.readInt();
				BlockPos blockPos5 = packetByteBuf.readNullable(PacketByteBuf::readBlockPos);
				BlockPos blockPos6 = packetByteBuf.readNullable(PacketByteBuf::readBlockPos);
				int p = packetByteBuf.readInt();
				Path path3 = packetByteBuf.readNullable(Path::fromBuffer);
				BeeDebugRenderer.Bee bee = new BeeDebugRenderer.Bee(uUID, o, position, path3, blockPos5, blockPos6, p);
				int z = packetByteBuf.readVarInt();

				for (int aa = 0; aa < z; aa++) {
					String string11 = packetByteBuf.readString();
					bee.labels.add(string11);
				}

				int aa = packetByteBuf.readVarInt();

				for (int ab = 0; ab < aa; ab++) {
					BlockPos blockPos7 = packetByteBuf.readBlockPos();
					bee.blacklist.add(blockPos7);
				}

				this.client.debugRenderer.beeDebugRenderer.addBee(bee);
			} else if (CustomPayloadS2CPacket.DEBUG_HIVE.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				String string2 = packetByteBuf.readString();
				int j = packetByteBuf.readInt();
				int ac = packetByteBuf.readInt();
				boolean bl3 = packetByteBuf.readBoolean();
				BeeDebugRenderer.Hive hive = new BeeDebugRenderer.Hive(blockPos2, string2, j, ac, bl3, this.world.getTime());
				this.client.debugRenderer.beeDebugRenderer.addHive(hive);
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR.equals(identifier)) {
				this.client.debugRenderer.gameTestDebugRenderer.clear();
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int m = packetByteBuf.readInt();
				String string12 = packetByteBuf.readString();
				int ac = packetByteBuf.readInt();
				this.client.debugRenderer.gameTestDebugRenderer.addMarker(blockPos2, m, string12, ac);
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_EVENT.equals(identifier)) {
				GameEvent gameEvent = Registry.GAME_EVENT.get(new Identifier(packetByteBuf.readString()));
				Vec3d vec3d = new Vec3d(packetByteBuf.readDouble(), packetByteBuf.readDouble(), packetByteBuf.readDouble());
				this.client.debugRenderer.gameEventDebugRenderer.addEvent(gameEvent, vec3d);
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_EVENT_LISTENERS.equals(identifier)) {
				Identifier identifier2 = packetByteBuf.readIdentifier();
				PositionSource positionSource = ((PositionSourceType)Registry.POSITION_SOURCE_TYPE
						.getOrEmpty(identifier2)
						.orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + identifier2)))
					.readFromBuf(packetByteBuf);
				int j = packetByteBuf.readVarInt();
				this.client.debugRenderer.gameEventDebugRenderer.addListener(positionSource, j);
			} else {
				LOGGER.warn("Unknown custom packed identifier: {}", identifier);
			}
		} finally {
			if (packetByteBuf != null) {
				packetByteBuf.release();
			}
		}
	}

	@Override
	public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = packet.getName();
		if (packet.getMode() == 0) {
			scoreboard.addObjective(string, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType());
		} else if (scoreboard.containsObjective(string)) {
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
			if (packet.getMode() == ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (packet.getMode() == ScoreboardObjectiveUpdateS2CPacket.UPDATE_MODE) {
				scoreboardObjective.setRenderType(packet.getType());
				scoreboardObjective.setDisplayName(packet.getDisplayName());
			}
		}
	}

	@Override
	public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = packet.getObjectiveName();
		switch (packet.getUpdateMode()) {
			case CHANGE:
				ScoreboardObjective scoreboardObjective = scoreboard.getObjective(string);
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(packet.getPlayerName(), scoreboardObjective);
				scoreboardPlayerScore.setScore(packet.getScore());
				break;
			case REMOVE:
				scoreboard.resetPlayerScore(packet.getPlayerName(), scoreboard.getNullableObjective(string));
		}
	}

	@Override
	public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = packet.getName();
		ScoreboardObjective scoreboardObjective = string == null ? null : scoreboard.getObjective(string);
		scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
	}

	@Override
	public void onTeam(TeamS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		TeamS2CPacket.Operation operation = packet.getTeamOperation();
		Team team;
		if (operation == TeamS2CPacket.Operation.ADD) {
			team = scoreboard.addTeam(packet.getTeamName());
		} else {
			team = scoreboard.getTeam(packet.getTeamName());
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
				scoreboard.addPlayerToTeam(string, team);
			}
		} else if (operation2 == TeamS2CPacket.Operation.REMOVE) {
			for (String string : packet.getPlayerNames()) {
				scoreboard.removePlayerFromTeam(string, team);
			}
		}

		if (operation == TeamS2CPacket.Operation.REMOVE) {
			scoreboard.removeTeam(team);
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
	public void onPing(PlayPingS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.sendPacket(new PlayPongC2SPacket(packet.getParameter()));
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
					EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(entry.getId());
					if (entityAttributeInstance == null) {
						LOGGER.warn("Entity {} does not have attribute {}", entity, Registry.ATTRIBUTE.getId(entry.getId()));
					} else {
						entityAttributeInstance.setBaseValue(entry.getBaseValue());
						entityAttributeInstance.clearModifiers();

						for (EntityAttributeModifier entityAttributeModifier : entry.getModifiers()) {
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
		if (screenHandler.syncId == packet.getSyncId()) {
			this.recipeManager.get(packet.getRecipeId()).ifPresent(recipe -> {
				if (this.client.currentScreen instanceof RecipeBookProvider) {
					RecipeBookWidget recipeBookWidget = ((RecipeBookProvider)this.client.currentScreen).getRecipeBookWidget();
					recipeBookWidget.showGhostRecipe(recipe, screenHandler.slots);
				}
			});
		}
	}

	@Override
	public void onLightUpdate(LightUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getChunkX();
		int j = packet.getChunkZ();
		LightData lightData = packet.getData();
		this.world.enqueueChunkUpdate(() -> this.readLightData(i, j, lightData));
	}

	private void readLightData(int x, int z, LightData data) {
		LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
		BitSet bitSet = data.getInitedSky();
		BitSet bitSet2 = data.getUninitedSky();
		Iterator<byte[]> iterator = data.getSkyNibbles().iterator();
		this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator, data.isNonEdge());
		BitSet bitSet3 = data.getInitedBlock();
		BitSet bitSet4 = data.getUninitedBlock();
		Iterator<byte[]> iterator2 = data.getBlockNibbles().iterator();
		this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2, data.isNonEdge());
		this.world.markChunkRenderability(x, z);
	}

	@Override
	public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ScreenHandler screenHandler = this.client.player.currentScreenHandler;
		if (packet.getSyncId() == screenHandler.syncId && screenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
			merchantScreenHandler.setOffers(new TradeOfferList(packet.getOffers().toNbt()));
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

	private void updateLighting(
		int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles, boolean nonEdge
	) {
		for (int i = 0; i < provider.getHeight(); i++) {
			int j = provider.getBottomY() + i;
			boolean bl = inited.get(i);
			boolean bl2 = uninited.get(i);
			if (bl || bl2) {
				provider.enqueueSectionData(
					type, ChunkSectionPos.from(chunkX, j, chunkZ), bl ? new ChunkNibbleArray((byte[])((byte[])nibbles.next()).clone()) : new ChunkNibbleArray(), nonEdge
				);
				this.world.scheduleBlockRenders(chunkX, j, chunkZ);
			}
		}
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
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

	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}
}
