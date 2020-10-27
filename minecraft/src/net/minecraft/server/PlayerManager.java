package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionType;
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
	private final Map<UUID, PlayerAdvancementTracker> advancementTrackers = Maps.<UUID, PlayerAdvancementTracker>newHashMap();
	private final WorldSaveHandler saveHandler;
	private boolean whitelistEnabled;
	private final DynamicRegistryManager.Impl registryManager;
	protected final int maxPlayers;
	private int viewDistance;
	private GameMode gameMode;
	private boolean cheatsAllowed;
	private int latencyUpdateTimer;

	public PlayerManager(MinecraftServer server, DynamicRegistryManager.Impl registryManager, WorldSaveHandler saveHandler, int maxPlayers) {
		this.server = server;
		this.registryManager = registryManager;
		this.maxPlayers = maxPlayers;
		this.saveHandler = saveHandler;
	}

	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
		GameProfile gameProfile = player.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		CompoundTag compoundTag = this.loadPlayerData(player);
		RegistryKey<World> registryKey = compoundTag != null
			? (RegistryKey)DimensionType.method_28521(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("Dimension")))
				.resultOrPartial(LOGGER::error)
				.orElse(World.OVERWORLD)
			: World.OVERWORLD;
		ServerWorld serverWorld = this.server.getWorld(registryKey);
		ServerWorld serverWorld2;
		if (serverWorld == null) {
			LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", registryKey);
			serverWorld2 = this.server.getOverworld();
		} else {
			serverWorld2 = serverWorld;
		}

		player.setWorld(serverWorld2);
		player.interactionManager.setWorld((ServerWorld)player.world);
		String string2 = "local";
		if (connection.getAddress() != null) {
			string2 = connection.getAddress().toString();
		}

		LOGGER.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			player.getName().getString(),
			string2,
			player.getEntityId(),
			player.getX(),
			player.getY(),
			player.getZ()
		);
		WorldProperties worldProperties = serverWorld2.getLevelProperties();
		this.setGameMode(player, null, serverWorld2);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);
		GameRules gameRules = serverWorld2.getGameRules();
		boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
		boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				player.getEntityId(),
				player.interactionManager.getGameMode(),
				player.interactionManager.getPreviousGameMode(),
				BiomeAccess.hashSeed(serverWorld2.getSeed()),
				worldProperties.isHardcore(),
				this.server.getWorldRegistryKeys(),
				this.registryManager,
				serverWorld2.getDimension(),
				serverWorld2.getRegistryKey(),
				this.getMaxPlayerCount(),
				this.viewDistance,
				bl2,
				!bl,
				serverWorld2.isDebugWorld(),
				serverWorld2.isFlat()
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
		serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
		this.sendCommandTree(player);
		player.getStatHandler().updateStatSet();
		player.getRecipeBook().sendInitRecipesPacket(player);
		this.sendScoreboard(serverWorld2.getScoreboard(), player);
		this.server.forcePlayerSampleUpdate();
		MutableText mutableText;
		if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
			mutableText = new TranslatableText("multiplayer.player.joined", player.getDisplayName());
		} else {
			mutableText = new TranslatableText("multiplayer.player.joined.renamed", player.getDisplayName(), string);
		}

		this.broadcastChatMessage(mutableText.formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
		serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch);
		this.players.add(player);
		this.playerMap.put(player.getUuid(), player);
		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));

		for (int i = 0; i < this.players.size(); i++) {
			player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld2.onPlayerConnected(player);
		this.server.getBossBarManager().onPlayerConnect(player);
		this.sendWorldInfo(player, serverWorld2);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			player.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
			serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), statusEffectInstance));
		}

		if (compoundTag != null && compoundTag.contains("RootVehicle", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("RootVehicle");
			Entity entity = EntityType.loadEntityWithPassengers(
				compoundTag2.getCompound("Entity"), serverWorld2, vehicle -> !serverWorld2.tryLoadEntity(vehicle) ? null : vehicle
			);
			if (entity != null) {
				UUID uUID;
				if (compoundTag2.containsUuid("Attach")) {
					uUID = compoundTag2.getUuid("Attach");
				} else {
					uUID = null;
				}

				if (entity.getUuid().equals(uUID)) {
					player.startRiding(entity, true);
				} else {
					for (Entity entity2 : entity.getPassengersDeep()) {
						if (entity2.getUuid().equals(uUID)) {
							player.startRiding(entity2, true);
							break;
						}
					}
				}

				if (!player.hasVehicle()) {
					LOGGER.warn("Couldn't reattach entity to player");
					serverWorld2.removeEntity(entity);

					for (Entity entity2x : entity.getPassengersDeep()) {
						serverWorld2.removeEntity(entity2x);
					}
				}
			}
		}

		player.onSpawn();
	}

	protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
		Set<ScoreboardObjective> set = Sets.<ScoreboardObjective>newHashSet();

		for (Team team : scoreboard.getTeams()) {
			player.networkHandler.sendPacket(new TeamS2CPacket(team, 0));
		}

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
				for (Packet<?> packet : scoreboard.createChangePackets(scoreboardObjective)) {
					player.networkHandler.sendPacket(packet);
				}

				set.add(scoreboardObjective);
			}
		}
	}

	public void setMainWorld(ServerWorld world) {
		world.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder border, double size) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
			}

			@Override
			public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.LERP_SIZE));
			}

			@Override
			public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_CENTER));
			}

			@Override
			public void onWarningTimeChanged(WorldBorder border, int warningTime) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
			}

			@Override
			public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
			}
		});
	}

	@Nullable
	public CompoundTag loadPlayerData(ServerPlayerEntity player) {
		CompoundTag compoundTag = this.server.getSaveProperties().getPlayerData();
		CompoundTag compoundTag2;
		if (player.getName().getString().equals(this.server.getUserName()) && compoundTag != null) {
			compoundTag2 = compoundTag;
			player.fromTag(compoundTag);
			LOGGER.debug("loading single player");
		} else {
			compoundTag2 = this.saveHandler.loadPlayerData(player);
		}

		return compoundTag2;
	}

	protected void savePlayerData(ServerPlayerEntity player) {
		this.saveHandler.savePlayerData(player);
		ServerStatHandler serverStatHandler = (ServerStatHandler)this.statisticsMap.get(player.getUuid());
		if (serverStatHandler != null) {
			serverStatHandler.save();
		}

		PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementTrackers.get(player.getUuid());
		if (playerAdvancementTracker != null) {
			playerAdvancementTracker.save();
		}
	}

	public void remove(ServerPlayerEntity player) {
		ServerWorld serverWorld = player.getServerWorld();
		player.incrementStat(Stats.LEAVE_GAME);
		this.savePlayerData(player);
		if (player.hasVehicle()) {
			Entity entity = player.getRootVehicle();
			if (entity.hasPlayerRider()) {
				LOGGER.debug("Removing player mount");
				player.stopRiding();
				serverWorld.removeEntity(entity);
				entity.removed = true;

				for (Entity entity2 : entity.getPassengersDeep()) {
					serverWorld.removeEntity(entity2);
					entity2.removed = true;
				}

				serverWorld.getChunk(player.chunkX, player.chunkZ).markDirty();
			}
		}

		player.detach();
		serverWorld.removePlayer(player);
		player.getAdvancementTracker().clearCriteria();
		this.players.remove(player);
		this.server.getBossBarManager().onPlayerDisconnect(player);
		UUID uUID = player.getUuid();
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity == player) {
			this.playerMap.remove(uUID);
			this.statisticsMap.remove(uUID);
			this.advancementTrackers.remove(uUID);
		}

		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, player));
	}

	@Nullable
	public Text checkCanJoin(SocketAddress address, GameProfile profile) {
		if (this.bannedProfiles.contains(profile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(profile);
			MutableText mutableText = new TranslatableText("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				mutableText.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return mutableText;
		} else if (!this.isWhitelisted(profile)) {
			return new TranslatableText("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.isBanned(address)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(address);
			MutableText mutableText = new TranslatableText("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
			if (bannedIpEntry.getExpiryDate() != null) {
				mutableText.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
			}

			return mutableText;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile) ? new TranslatableText("multiplayer.disconnect.server_full") : null;
		}
	}

	public ServerPlayerEntity createPlayer(GameProfile profile) {
		UUID uUID = PlayerEntity.getUuidFromProfile(profile);
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.getUuid().equals(uUID)) {
				list.add(serverPlayerEntity);
			}
		}

		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(profile.getId());
		if (serverPlayerEntity2 != null && !list.contains(serverPlayerEntity2)) {
			list.add(serverPlayerEntity2);
		}

		for (ServerPlayerEntity serverPlayerEntity3 : list) {
			serverPlayerEntity3.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.duplicate_login"));
		}

		ServerWorld serverWorld = this.server.getOverworld();
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(serverWorld);
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(serverWorld);
		}

		return new ServerPlayerEntity(this.server, serverWorld, profile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean alive) {
		this.players.remove(player);
		player.getServerWorld().removePlayer(player);
		BlockPos blockPos = player.getSpawnPointPosition();
		float f = player.getSpawnAngle();
		boolean bl = player.isSpawnPointSet();
		ServerWorld serverWorld = this.server.getWorld(player.getSpawnPointDimension());
		Optional<Vec3d> optional;
		if (serverWorld != null && blockPos != null) {
			optional = PlayerEntity.findRespawnPosition(serverWorld, blockPos, f, bl, alive);
		} else {
			optional = Optional.empty();
		}

		ServerWorld serverWorld2 = serverWorld != null && optional.isPresent() ? serverWorld : this.server.getOverworld();
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(serverWorld2);
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(serverWorld2);
		}

		ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(this.server, serverWorld2, player.getGameProfile(), serverPlayerInteractionManager);
		serverPlayerEntity.networkHandler = player.networkHandler;
		serverPlayerEntity.copyFrom(player, alive);
		serverPlayerEntity.setEntityId(player.getEntityId());
		serverPlayerEntity.setMainArm(player.getMainArm());

		for (String string : player.getScoreboardTags()) {
			serverPlayerEntity.addScoreboardTag(string);
		}

		this.setGameMode(serverPlayerEntity, player, serverWorld2);
		boolean bl2 = false;
		if (optional.isPresent()) {
			BlockState blockState = serverWorld2.getBlockState(blockPos);
			boolean bl3 = blockState.isOf(Blocks.RESPAWN_ANCHOR);
			Vec3d vec3d = (Vec3d)optional.get();
			float g;
			if (!blockState.isIn(BlockTags.BEDS) && !bl3) {
				g = f;
			} else {
				Vec3d vec3d2 = Vec3d.ofBottomCenter(blockPos).subtract(vec3d).normalize();
				g = (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d2.z, vec3d2.x) * 180.0F / (float)Math.PI - 90.0);
			}

			serverPlayerEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, g, 0.0F);
			serverPlayerEntity.setSpawnPoint(serverWorld2.getRegistryKey(), blockPos, f, bl, false);
			bl2 = !alive && bl3;
		} else if (blockPos != null) {
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0F));
		}

		while (!serverWorld2.isSpaceEmpty(serverPlayerEntity) && serverPlayerEntity.getY() < 256.0) {
			serverPlayerEntity.updatePosition(serverPlayerEntity.getX(), serverPlayerEntity.getY() + 1.0, serverPlayerEntity.getZ());
		}

		WorldProperties worldProperties = serverPlayerEntity.world.getLevelProperties();
		serverPlayerEntity.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity.world.getDimension(),
					serverPlayerEntity.world.getRegistryKey(),
					BiomeAccess.hashSeed(serverPlayerEntity.getServerWorld().getSeed()),
					serverPlayerEntity.interactionManager.getGameMode(),
					serverPlayerEntity.interactionManager.getPreviousGameMode(),
					serverPlayerEntity.getServerWorld().isDebugWorld(),
					serverPlayerEntity.getServerWorld().isFlat(),
					alive
				)
			);
		serverPlayerEntity.networkHandler
			.requestTeleport(serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(serverWorld2.getSpawnPos(), serverWorld2.getSpawnAngle()));
		serverPlayerEntity.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
		serverPlayerEntity.networkHandler
			.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
		this.sendWorldInfo(serverPlayerEntity, serverWorld2);
		this.sendCommandTree(serverPlayerEntity);
		serverWorld2.onPlayerRespawned(serverPlayerEntity);
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		serverPlayerEntity.onSpawn();
		serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
		if (bl2) {
			serverPlayerEntity.networkHandler
				.sendPacket(
					new PlaySoundS2CPacket(
						SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0F, 1.0F
					)
				);
		}

		return serverPlayerEntity;
	}

	public void sendCommandTree(ServerPlayerEntity player) {
		GameProfile gameProfile = player.getGameProfile();
		int i = this.server.getPermissionLevel(gameProfile);
		this.sendCommandTree(player, i);
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

	public void sendToDimension(Packet<?> packet, RegistryKey<World> dimension) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.world.getRegistryKey() == dimension) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}
	}

	public void sendToTeam(PlayerEntity source, Text message) {
		AbstractTeam abstractTeam = source.getScoreboardTeam();
		if (abstractTeam != null) {
			for (String string : abstractTeam.getPlayerList()) {
				ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
				if (serverPlayerEntity != null && serverPlayerEntity != source) {
					serverPlayerEntity.sendSystemMessage(message, source.getUuid());
				}
			}
		}
	}

	public void sendToOtherTeams(PlayerEntity source, Text message) {
		AbstractTeam abstractTeam = source.getScoreboardTeam();
		if (abstractTeam == null) {
			this.broadcastChatMessage(message, MessageType.SYSTEM, source.getUuid());
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.getScoreboardTeam() != abstractTeam) {
					serverPlayerEntity.sendSystemMessage(message, source.getUuid());
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

	public void addToOperators(GameProfile profile) {
		this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.isOp(profile)));
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
		if (serverPlayerEntity != null) {
			this.sendCommandTree(serverPlayerEntity);
		}
	}

	public void removeFromOperators(GameProfile profile) {
		this.ops.remove(profile);
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
		if (serverPlayerEntity != null) {
			this.sendCommandTree(serverPlayerEntity);
		}
	}

	private void sendCommandTree(ServerPlayerEntity player, int permissionLevel) {
		if (player.networkHandler != null) {
			byte b;
			if (permissionLevel <= 0) {
				b = 24;
			} else if (permissionLevel >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + permissionLevel);
			}

			player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, b));
		}

		this.server.getCommandManager().sendCommandTree(player);
	}

	public boolean isWhitelisted(GameProfile profile) {
		return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
	}

	public boolean isOperator(GameProfile profile) {
		return this.ops.contains(profile) || this.server.isHost(profile) && this.server.getSaveProperties().areCommandsAllowed() || this.cheatsAllowed;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(String name) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(name)) {
				return serverPlayerEntity;
			}
		}

		return null;
	}

	public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double distance, RegistryKey<World> worldKey, Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity != player && serverPlayerEntity.world.getRegistryKey() == worldKey) {
				double d = x - serverPlayerEntity.getX();
				double e = y - serverPlayerEntity.getY();
				double f = z - serverPlayerEntity.getZ();
				if (d * d + e * e + f * f < distance * distance) {
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

	public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
		WorldBorder worldBorder = this.server.getOverworld().getWorldBorder();
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		player.networkHandler
			.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
		player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(world.getSpawnPos(), world.getSpawnAngle()));
		if (world.isRaining()) {
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0F));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1.0F)));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1.0F)));
		}
	}

	public void sendPlayerStatus(ServerPlayerEntity player) {
		player.refreshScreenHandler(player.playerScreenHandler);
		player.markHealthDirty();
		player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
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

	public void setWhitelistEnabled(boolean whitelistEnabled) {
		this.whitelistEnabled = whitelistEnabled;
	}

	public List<ServerPlayerEntity> getPlayersByIp(String ip) {
		List<ServerPlayerEntity> list = Lists.<ServerPlayerEntity>newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getIp().equals(ip)) {
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

	/**
	 * Gets the user data of the player hosting the Minecraft server.
	 * 
	 * @return the user data of the host of the server if the server is an integrated server, otherwise {@code null}
	 */
	public CompoundTag getUserData() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	private void setGameMode(ServerPlayerEntity player, @Nullable ServerPlayerEntity oldPlayer, ServerWorld world) {
		if (oldPlayer != null) {
			player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.getPreviousGameMode());
		} else if (this.gameMode != null) {
			player.interactionManager.setGameMode(this.gameMode, GameMode.NOT_SET);
		}

		player.interactionManager.setGameModeIfNotPresent(world.getServer().getSaveProperties().getGameMode());
	}

	@Environment(EnvType.CLIENT)
	public void setCheatsAllowed(boolean cheatsAllowed) {
		this.cheatsAllowed = cheatsAllowed;
	}

	public void disconnectAllPlayers() {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void broadcastChatMessage(Text message, MessageType type, UUID senderUuid) {
		this.server.sendSystemMessage(message, senderUuid);
		this.sendToAll(new GameMessageS2CPacket(message, type, senderUuid));
	}

	public ServerStatHandler createStatHandler(PlayerEntity player) {
		UUID uUID = player.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.statisticsMap.get(uUID);
		if (serverStatHandler == null) {
			File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, player.getName().getString() + ".json");
				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			serverStatHandler = new ServerStatHandler(this.server, file2);
			this.statisticsMap.put(uUID, serverStatHandler);
		}

		return serverStatHandler;
	}

	public PlayerAdvancementTracker getAdvancementTracker(ServerPlayerEntity player) {
		UUID uUID = player.getUuid();
		PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementTrackers.get(uUID);
		if (playerAdvancementTracker == null) {
			File file = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile();
			File file2 = new File(file, uUID + ".json");
			playerAdvancementTracker = new PlayerAdvancementTracker(this.server.getDataFixer(), this, this.server.getAdvancementLoader(), file2, player);
			this.advancementTrackers.put(uUID, playerAdvancementTracker);
		}

		playerAdvancementTracker.setOwner(player);
		return playerAdvancementTracker;
	}

	public void setViewDistance(int viewDistance) {
		this.viewDistance = viewDistance;
		this.sendToAll(new ChunkLoadDistanceS2CPacket(viewDistance));

		for (ServerWorld serverWorld : this.server.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.getChunkManager().applyViewDistance(viewDistance);
			}
		}
	}

	/**
	 * Gets a list of all players on a Minecraft server.
	 * This list should not be modified!
	 */
	public List<ServerPlayerEntity> getPlayerList() {
		return this.players;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(UUID uuid) {
		return (ServerPlayerEntity)this.playerMap.get(uuid);
	}

	public boolean canBypassPlayerLimit(GameProfile profile) {
		return false;
	}

	public void onDataPacksReloaded() {
		for (PlayerAdvancementTracker playerAdvancementTracker : this.advancementTrackers.values()) {
			playerAdvancementTracker.reload(this.server.getAdvancementLoader());
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
