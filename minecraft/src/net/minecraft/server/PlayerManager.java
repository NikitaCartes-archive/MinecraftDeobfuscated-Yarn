package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerManager {
	public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
	public static final File BANNED_IPS_FILE = new File("banned-ips.json");
	public static final File OPERATORS_FILE = new File("ops.json");
	public static final File WHITELIST_FILE = new File("whitelist.json");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
	private final MinecraftServer server;
	private final List<ServerPlayerEntity> players = Lists.<ServerPlayerEntity>newArrayList();
	private final Map<UUID, ServerPlayerEntity> playerMap = Maps.<UUID, ServerPlayerEntity>newHashMap();
	private final BannedPlayerList bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
	private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
	private final OperatorList ops = new OperatorList(OPERATORS_FILE);
	private final Whitelist whitelist = new Whitelist(WHITELIST_FILE);
	private final Map<UUID, ServerStatHandler> statisticsMap = Maps.<UUID, ServerStatHandler>newHashMap();
	private final Map<UUID, PlayerAdvancementTracker> advancementManagerMap = Maps.<UUID, PlayerAdvancementTracker>newHashMap();
	private PlayerSaveHandler saveHandler;
	private boolean whitelistEnabled;
	protected final int maxPlayers;
	private int viewDistance;
	private GameMode gameMode;
	private boolean cheatsAllowed;
	private int latencyUpdateTimer;

	public PlayerManager(MinecraftServer minecraftServer, int i) {
		this.server = minecraftServer;
		this.maxPlayers = i;
		this.getUserBanList().setEnabled(true);
		this.getIpBanList().setEnabled(true);
	}

	public void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		CompoundTag compoundTag = this.loadPlayerData(serverPlayerEntity);
		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		serverPlayerEntity.setWorld(serverWorld);
		serverPlayerEntity.interactionManager.setWorld((ServerWorld)serverPlayerEntity.world);
		String string2 = "local";
		if (clientConnection.getAddress() != null) {
			string2 = clientConnection.getAddress().toString();
		}

		LOGGER.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			serverPlayerEntity.getName().getString(),
			string2,
			serverPlayerEntity.getEntityId(),
			serverPlayerEntity.getX(),
			serverPlayerEntity.getY(),
			serverPlayerEntity.getZ()
		);
		LevelProperties levelProperties = serverWorld.getLevelProperties();
		this.setGameMode(serverPlayerEntity, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, clientConnection, serverPlayerEntity);
		GameRules gameRules = serverWorld.getGameRules();
		boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
		boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				serverPlayerEntity.getEntityId(),
				serverPlayerEntity.interactionManager.getGameMode(),
				LevelProperties.sha256Hash(levelProperties.getSeed()),
				levelProperties.isHardcore(),
				serverWorld.dimension.getType(),
				this.getMaxPlayerCount(),
				levelProperties.getGeneratorType(),
				this.viewDistance,
				bl2,
				!bl
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
		serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
		this.sendCommandTree(serverPlayerEntity);
		serverPlayerEntity.getStatHandler().updateStatSet();
		serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
		this.sendScoreboard(serverWorld.method_14170(), serverPlayerEntity);
		this.server.forcePlayerSampleUpdate();
		Text text;
		if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) {
			text = new TranslatableText("multiplayer.player.joined", serverPlayerEntity.getDisplayName());
		} else {
			text = new TranslatableText("multiplayer.player.joined.renamed", serverPlayerEntity.getDisplayName(), string);
		}

		this.sendToAll(text.formatted(Formatting.YELLOW));
		serverPlayNetworkHandler.requestTeleport(
			serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.yaw, serverPlayerEntity.pitch
		);
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, serverPlayerEntity));

		for (int i = 0; i < this.players.size(); i++) {
			serverPlayerEntity.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld.method_18213(serverPlayerEntity);
		this.server.getBossBarManager().onPlayerConnect(serverPlayerEntity);
		this.sendWorldInfo(serverPlayerEntity, serverWorld);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			serverPlayerEntity.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getStatusEffects()) {
			serverPlayNetworkHandler.sendPacket(new EntityPotionEffectS2CPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}

		if (compoundTag != null && compoundTag.contains("RootVehicle", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("RootVehicle");
			Entity entity = EntityType.loadEntityWithPassengers(
				compoundTag2.getCompound("Entity"), serverWorld, entityx -> !serverWorld.method_18768(entityx) ? null : entityx
			);
			if (entity != null) {
				UUID uUID = compoundTag2.getUuid("Attach");
				if (entity.getUuid().equals(uUID)) {
					serverPlayerEntity.startRiding(entity, true);
				} else {
					for (Entity entity2 : entity.getPassengersDeep()) {
						if (entity2.getUuid().equals(uUID)) {
							serverPlayerEntity.startRiding(entity2, true);
							break;
						}
					}
				}

				if (!serverPlayerEntity.hasVehicle()) {
					LOGGER.warn("Couldn't reattach entity to player");
					serverWorld.removeEntity(entity);

					for (Entity entity2x : entity.getPassengersDeep()) {
						serverWorld.removeEntity(entity2x);
					}
				}
			}
		}

		serverPlayerEntity.method_14235();
	}

	protected void sendScoreboard(ServerScoreboard serverScoreboard, ServerPlayerEntity serverPlayerEntity) {
		Set<ScoreboardObjective> set = Sets.<ScoreboardObjective>newHashSet();

		for (Team team : serverScoreboard.getTeams()) {
			serverPlayerEntity.networkHandler.sendPacket(new TeamS2CPacket(team, 0));
		}

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = serverScoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
				for (Packet<?> packet : serverScoreboard.createChangePackets(scoreboardObjective)) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}

				set.add(scoreboardObjective);
			}
		}
	}

	public void setMainWorld(ServerWorld serverWorld) {
		this.saveHandler = serverWorld.getSaveHandler();
		serverWorld.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder worldBorder, double d) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_SIZE));
			}

			@Override
			public void onInterpolateSize(WorldBorder worldBorder, double d, double e, long l) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.LERP_SIZE));
			}

			@Override
			public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_CENTER));
			}

			@Override
			public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
			}

			@Override
			public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
			}
		});
	}

	@Nullable
	public CompoundTag loadPlayerData(ServerPlayerEntity serverPlayerEntity) {
		CompoundTag compoundTag = this.server.getWorld(DimensionType.OVERWORLD).getLevelProperties().getPlayerData();
		CompoundTag compoundTag2;
		if (serverPlayerEntity.getName().getString().equals(this.server.getUserName()) && compoundTag != null) {
			compoundTag2 = compoundTag;
			serverPlayerEntity.fromTag(compoundTag);
			LOGGER.debug("loading single player");
		} else {
			compoundTag2 = this.saveHandler.loadPlayerData(serverPlayerEntity);
		}

		return compoundTag2;
	}

	protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
		this.saveHandler.savePlayerData(serverPlayerEntity);
		ServerStatHandler serverStatHandler = (ServerStatHandler)this.statisticsMap.get(serverPlayerEntity.getUuid());
		if (serverStatHandler != null) {
			serverStatHandler.save();
		}

		PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementManagerMap.get(serverPlayerEntity.getUuid());
		if (playerAdvancementTracker != null) {
			playerAdvancementTracker.save();
		}
	}

	public void remove(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
		serverPlayerEntity.incrementStat(Stats.LEAVE_GAME);
		this.savePlayerData(serverPlayerEntity);
		if (serverPlayerEntity.hasVehicle()) {
			Entity entity = serverPlayerEntity.getRootVehicle();
			if (entity.hasPlayerRider()) {
				LOGGER.debug("Removing player mount");
				serverPlayerEntity.stopRiding();
				serverWorld.removeEntity(entity);

				for (Entity entity2 : entity.getPassengersDeep()) {
					serverWorld.removeEntity(entity2);
				}

				serverWorld.method_8497(serverPlayerEntity.chunkX, serverPlayerEntity.chunkZ).markDirty();
			}
		}

		serverPlayerEntity.detach();
		serverWorld.removePlayer(serverPlayerEntity);
		serverPlayerEntity.getAdvancementManager().clearCriterions();
		this.players.remove(serverPlayerEntity);
		this.server.getBossBarManager().onPlayerDisconnenct(serverPlayerEntity);
		UUID uUID = serverPlayerEntity.getUuid();
		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity2 == serverPlayerEntity) {
			this.playerMap.remove(uUID);
			this.statisticsMap.remove(uUID);
			this.advancementManagerMap.remove(uUID);
		}

		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, serverPlayerEntity));
	}

	@Nullable
	public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.bannedProfiles.contains(gameProfile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(gameProfile);
			Text text = new TranslatableText("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				text.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return text;
		} else if (!this.isWhitelisted(gameProfile)) {
			return new TranslatableText("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.isBanned(socketAddress)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(socketAddress);
			Text text = new TranslatableText("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
			if (bannedIpEntry.getExpiryDate() != null) {
				text.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
			}

			return text;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile) ? new TranslatableText("multiplayer.disconnect.server_full") : null;
		}
	}

	public ServerPlayerEntity createPlayer(GameProfile gameProfile) {
		UUID uUID = PlayerEntity.getUuidFromProfile(gameProfile);
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.getUuid().equals(uUID)) {
				list.add(serverPlayerEntity);
			}
		}

		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(gameProfile.getId());
		if (serverPlayerEntity2 != null && !list.contains(serverPlayerEntity2)) {
			list.add(serverPlayerEntity2);
		}

		for (ServerPlayerEntity serverPlayerEntity3 : list) {
			serverPlayerEntity3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.duplicate_login"));
		}

		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
		}

		return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), gameProfile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity respawnPlayer(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, boolean bl) {
		this.players.remove(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().removePlayer(serverPlayerEntity);
		BlockPos blockPos = serverPlayerEntity.getSpawnPosition();
		boolean bl2 = serverPlayerEntity.isSpawnForced();
		serverPlayerEntity.dimension = dimensionType;
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(serverPlayerEntity.dimension));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(serverPlayerEntity.dimension));
		}

		ServerPlayerEntity serverPlayerEntity2 = new ServerPlayerEntity(
			this.server, this.server.getWorld(serverPlayerEntity.dimension), serverPlayerEntity.getGameProfile(), serverPlayerInteractionManager
		);
		serverPlayerEntity2.networkHandler = serverPlayerEntity.networkHandler;
		serverPlayerEntity2.copyFrom(serverPlayerEntity, bl);
		serverPlayerEntity2.setEntityId(serverPlayerEntity.getEntityId());
		serverPlayerEntity2.setMainArm(serverPlayerEntity.getMainArm());

		for (String string : serverPlayerEntity.getScoreboardTags()) {
			serverPlayerEntity2.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		this.setGameMode(serverPlayerEntity2, serverPlayerEntity, serverWorld);
		if (blockPos != null) {
			Optional<Vec3d> optional = PlayerEntity.method_7288(this.server.getWorld(serverPlayerEntity.dimension), blockPos, bl2);
			if (optional.isPresent()) {
				Vec3d vec3d = (Vec3d)optional.get();
				serverPlayerEntity2.setPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
				serverPlayerEntity2.setPlayerSpawn(blockPos, bl2);
			} else {
				serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0F));
			}
		}

		while (!serverWorld.doesNotCollide(serverPlayerEntity2) && serverPlayerEntity2.getY() < 256.0) {
			serverPlayerEntity2.setPosition(serverPlayerEntity2.getX(), serverPlayerEntity2.getY() + 1.0, serverPlayerEntity2.getZ());
		}

		LevelProperties levelProperties = serverPlayerEntity2.world.getLevelProperties();
		serverPlayerEntity2.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity2.dimension,
					LevelProperties.sha256Hash(levelProperties.getSeed()),
					levelProperties.getGeneratorType(),
					serverPlayerEntity2.interactionManager.getGameMode()
				)
			);
		BlockPos blockPos2 = serverWorld.getSpawnPos();
		serverPlayerEntity2.networkHandler
			.requestTeleport(serverPlayerEntity2.getX(), serverPlayerEntity2.getY(), serverPlayerEntity2.getZ(), serverPlayerEntity2.yaw, serverPlayerEntity2.pitch);
		serverPlayerEntity2.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos2));
		serverPlayerEntity2.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayerEntity2.networkHandler
			.sendPacket(
				new ExperienceBarUpdateS2CPacket(serverPlayerEntity2.experienceProgress, serverPlayerEntity2.totalExperience, serverPlayerEntity2.experienceLevel)
			);
		this.sendWorldInfo(serverPlayerEntity2, serverWorld);
		this.sendCommandTree(serverPlayerEntity2);
		serverWorld.respawnPlayer(serverPlayerEntity2);
		this.players.add(serverPlayerEntity2);
		this.playerMap.put(serverPlayerEntity2.getUuid(), serverPlayerEntity2);
		serverPlayerEntity2.method_14235();
		serverPlayerEntity2.setHealth(serverPlayerEntity2.getHealth());
		return serverPlayerEntity2;
	}

	public void sendCommandTree(ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		int i = this.server.getPermissionLevel(gameProfile);
		this.sendCommandTree(serverPlayerEntity, i);
	}

	public void updatePlayerLatency() {
		if (++this.latencyUpdateTimer > 600) {
			this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
			this.latencyUpdateTimer = 0;
		}
	}

	public void sendToAll(Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).networkHandler.sendPacket(packet);
		}
	}

	public void sendToDimension(Packet<?> packet, DimensionType dimensionType) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.dimension == dimensionType) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}
	}

	public void sendToTeam(PlayerEntity playerEntity, Text text) {
		AbstractTeam abstractTeam = playerEntity.getScoreboardTeam();
		if (abstractTeam != null) {
			for (String string : abstractTeam.getPlayerList()) {
				ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
				if (serverPlayerEntity != null && serverPlayerEntity != playerEntity) {
					serverPlayerEntity.sendMessage(text);
				}
			}
		}
	}

	public void sendToOtherTeams(PlayerEntity playerEntity, Text text) {
		AbstractTeam abstractTeam = playerEntity.getScoreboardTeam();
		if (abstractTeam == null) {
			this.sendToAll(text);
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.getScoreboardTeam() != abstractTeam) {
					serverPlayerEntity.sendMessage(text);
				}
			}
		}
	}

	public String[] getPlayerNames() {
		String[] strings = new String[this.players.size()];

		for (int i = 0; i < this.players.size(); i++) {
			strings[i] = ((ServerPlayerEntity)this.players.get(i)).getGameProfile().getName();
		}

		return strings;
	}

	public BannedPlayerList getUserBanList() {
		return this.bannedProfiles;
	}

	public BannedIpList getIpBanList() {
		return this.bannedIps;
	}

	public void addToOperators(GameProfile gameProfile) {
		this.ops.add(new OperatorEntry(gameProfile, this.server.getOpPermissionLevel(), this.ops.isOp(gameProfile)));
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
		if (serverPlayerEntity != null) {
			this.sendCommandTree(serverPlayerEntity);
		}
	}

	public void removeFromOperators(GameProfile gameProfile) {
		this.ops.remove(gameProfile);
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
		if (serverPlayerEntity != null) {
			this.sendCommandTree(serverPlayerEntity);
		}
	}

	private void sendCommandTree(ServerPlayerEntity serverPlayerEntity, int i) {
		if (serverPlayerEntity.networkHandler != null) {
			byte b;
			if (i <= 0) {
				b = 24;
			} else if (i >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + i);
			}

			serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
		}

		this.server.getCommandManager().sendCommandTree(serverPlayerEntity);
	}

	public boolean isWhitelisted(GameProfile gameProfile) {
		return !this.whitelistEnabled || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
	}

	public boolean isOperator(GameProfile gameProfile) {
		return this.ops.contains(gameProfile)
			|| this.server.isOwner(gameProfile) && this.server.getWorld(DimensionType.OVERWORLD).getLevelProperties().areCommandsAllowed()
			|| this.cheatsAllowed;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(String string) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) {
				return serverPlayerEntity;
			}
		}

		return null;
	}

	public void sendToAround(@Nullable PlayerEntity playerEntity, double d, double e, double f, double g, DimensionType dimensionType, Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity != playerEntity && serverPlayerEntity.dimension == dimensionType) {
				double h = d - serverPlayerEntity.getX();
				double j = e - serverPlayerEntity.getY();
				double k = f - serverPlayerEntity.getZ();
				if (h * h + j * j + k * k < g * g) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}
			}
		}
	}

	public void saveAllPlayerData() {
		for (int i = 0; i < this.players.size(); i++) {
			this.savePlayerData((ServerPlayerEntity)this.players.get(i));
		}
	}

	public Whitelist getWhitelist() {
		return this.whitelist;
	}

	public String[] getWhitelistedNames() {
		return this.whitelist.getNames();
	}

	public OperatorList getOpList() {
		return this.ops;
	}

	public String[] getOpNames() {
		return this.ops.getNames();
	}

	public void reloadWhitelist() {
	}

	public void sendWorldInfo(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld) {
		WorldBorder worldBorder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
		serverPlayerEntity.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		serverPlayerEntity.networkHandler
			.sendPacket(
				new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE))
			);
		BlockPos blockPos = serverWorld.getSpawnPos();
		serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos));
		if (serverWorld.isRaining()) {
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0F));
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, serverWorld.getRainGradient(1.0F)));
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, serverWorld.getThunderGradient(1.0F)));
		}
	}

	public void method_14594(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.openContainer(serverPlayerEntity.playerContainer);
		serverPlayerEntity.markHealthDirty();
		serverPlayerEntity.networkHandler.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
	}

	public int getCurrentPlayerCount() {
		return this.players.size();
	}

	public int getMaxPlayerCount() {
		return this.maxPlayers;
	}

	public boolean isWhitelistEnabled() {
		return this.whitelistEnabled;
	}

	public void setWhitelistEnabled(boolean bl) {
		this.whitelistEnabled = bl;
	}

	public List<ServerPlayerEntity> getPlayersByIp(String string) {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getServerBrand().equals(string)) {
				list.add(serverPlayerEntity);
			}
		}

		return list;
	}

	public int getViewDistance() {
		return this.viewDistance;
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public CompoundTag getUserData() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	private void setGameMode(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity2, IWorld iWorld) {
		if (serverPlayerEntity2 != null) {
			serverPlayerEntity.interactionManager.setGameMode(serverPlayerEntity2.interactionManager.getGameMode());
		} else if (this.gameMode != null) {
			serverPlayerEntity.interactionManager.setGameMode(this.gameMode);
		}

		serverPlayerEntity.interactionManager.setGameModeIfNotPresent(iWorld.getLevelProperties().getGameMode());
	}

	@Environment(EnvType.CLIENT)
	public void setCheatsAllowed(boolean bl) {
		this.cheatsAllowed = bl;
	}

	public void disconnectAllPlayers() {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void broadcastChatMessage(Text text, boolean bl) {
		this.server.sendMessage(text);
		MessageType messageType = bl ? MessageType.SYSTEM : MessageType.CHAT;
		this.sendToAll(new ChatMessageS2CPacket(text, messageType));
	}

	public void sendToAll(Text text) {
		this.broadcastChatMessage(text, true);
	}

	public ServerStatHandler createStatHandler(PlayerEntity playerEntity) {
		UUID uUID = playerEntity.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.statisticsMap.get(uUID);
		if (serverStatHandler == null) {
			File file = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "stats");
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, playerEntity.getName().getString() + ".json");
				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			serverStatHandler = new ServerStatHandler(this.server, file2);
			this.statisticsMap.put(uUID, serverStatHandler);
		}

		return serverStatHandler;
	}

	public PlayerAdvancementTracker getAdvancementManager(ServerPlayerEntity serverPlayerEntity) {
		UUID uUID = serverPlayerEntity.getUuid();
		PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementManagerMap.get(uUID);
		if (playerAdvancementTracker == null) {
			File file = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "advancements");
			File file2 = new File(file, uUID + ".json");
			playerAdvancementTracker = new PlayerAdvancementTracker(this.server, file2, serverPlayerEntity);
			this.advancementManagerMap.put(uUID, playerAdvancementTracker);
		}

		playerAdvancementTracker.setOwner(serverPlayerEntity);
		return playerAdvancementTracker;
	}

	public void setViewDistance(int i) {
		this.viewDistance = i;
		this.sendToAll(new ChunkLoadDistanceS2CPacket(i));

		for (ServerWorld serverWorld : this.server.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.method_14178().applyViewDistance(i);
			}
		}
	}

	public List<ServerPlayerEntity> getPlayerList() {
		return this.players;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(UUID uUID) {
		return (ServerPlayerEntity)this.playerMap.get(uUID);
	}

	public boolean canBypassPlayerLimit(GameProfile gameProfile) {
		return false;
	}

	public void onDataPacksReloaded() {
		for (PlayerAdvancementTracker playerAdvancementTracker : this.advancementManagerMap.values()) {
			playerAdvancementTracker.reload();
		}

		this.sendToAll(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
		SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket = new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values());

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.networkHandler.sendPacket(synchronizeRecipesS2CPacket);
			serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
		}
	}

	public boolean areCheatsAllowed() {
		return this.cheatsAllowed;
	}
}
