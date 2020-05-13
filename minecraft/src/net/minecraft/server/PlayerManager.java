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
import net.minecraft.class_5217;
import net.minecraft.class_5218;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
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
	protected final int maxPlayers;
	private int viewDistance;
	private GameMode gameMode;
	private boolean cheatsAllowed;
	private int latencyUpdateTimer;

	public PlayerManager(MinecraftServer server, WorldSaveHandler worldSaveHandler, int i) {
		this.server = server;
		this.maxPlayers = i;
		this.saveHandler = worldSaveHandler;
		this.getUserBanList().setEnabled(true);
		this.getIpBanList().setEnabled(true);
	}

	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
		GameProfile gameProfile = player.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		CompoundTag compoundTag = this.loadPlayerData(player);
		ServerWorld serverWorld = this.server.getWorld(player.dimension);
		player.setWorld(serverWorld);
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
		class_5217 lv = serverWorld.getLevelProperties();
		this.setGameMode(player, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);
		GameRules gameRules = serverWorld.getGameRules();
		boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
		boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				player.getEntityId(),
				player.interactionManager.getGameMode(),
				BiomeAccess.method_27984(serverWorld.getSeed()),
				lv.isHardcore(),
				serverWorld.method_27983(),
				this.getMaxPlayerCount(),
				this.viewDistance,
				bl2,
				!bl,
				serverWorld.method_27982(),
				serverWorld.method_28125()
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket(CustomPayloadS2CPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(lv.getDifficulty(), lv.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
		serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.getTagManager()));
		this.sendCommandTree(player);
		player.getStatHandler().updateStatSet();
		player.getRecipeBook().sendInitRecipesPacket(player);
		this.sendScoreboard(serverWorld.getScoreboard(), player);
		this.server.forcePlayerSampleUpdate();
		MutableText mutableText;
		if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
			mutableText = new TranslatableText("multiplayer.player.joined", player.getDisplayName());
		} else {
			mutableText = new TranslatableText("multiplayer.player.joined.renamed", player.getDisplayName(), string);
		}

		this.sendToAll(mutableText.formatted(Formatting.YELLOW));
		serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.yaw, player.pitch);
		this.players.add(player);
		this.playerMap.put(player.getUuid(), player);
		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));

		for (int i = 0; i < this.players.size(); i++) {
			player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld.onPlayerConnected(player);
		this.server.getBossBarManager().onPlayerConnect(player);
		this.sendWorldInfo(player, serverWorld);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			player.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
			serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), statusEffectInstance));
		}

		if (compoundTag != null && compoundTag.contains("RootVehicle", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("RootVehicle");
			Entity entity = EntityType.loadEntityWithPassengers(
				compoundTag2.getCompound("Entity"), serverWorld, entityx -> !serverWorld.tryLoadEntity(entityx) ? null : entityx
			);
			if (entity != null) {
				UUID uUID;
				if (compoundTag2.containsUuidNew("Attach")) {
					uUID = compoundTag2.getUuidNew("Attach");
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
					serverWorld.removeEntity(entity);

					for (Entity entity2x : entity.getPassengersDeep()) {
						serverWorld.removeEntity(entity2x);
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
		CompoundTag compoundTag = this.server.method_27728().getPlayerData();
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
		this.server.getBossBarManager().onPlayerDisconnenct(player);
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
	public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.bannedProfiles.contains(gameProfile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(gameProfile);
			MutableText mutableText = new TranslatableText("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				mutableText.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return mutableText;
		} else if (!this.isWhitelisted(gameProfile)) {
			return new TranslatableText("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.isBanned(socketAddress)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(socketAddress);
			MutableText mutableText = new TranslatableText("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
			if (bannedIpEntry.getExpiryDate() != null) {
				mutableText.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
			}

			return mutableText;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile) ? new TranslatableText("multiplayer.disconnect.server_full") : null;
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

		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
		}

		return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.OVERWORLD), profile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean bl) {
		this.players.remove(player);
		player.getServerWorld().removePlayer(player);
		BlockPos blockPos = player.getSpawnPointPosition();
		boolean bl2 = player.isSpawnPointSet();
		Optional<Vec3d> optional;
		if (blockPos != null) {
			optional = PlayerEntity.findRespawnPosition(this.server.getWorld(player.getSpawnPointDimension()), blockPos, bl2, bl);
		} else {
			optional = Optional.empty();
		}

		player.dimension = optional.isPresent() ? player.getSpawnPointDimension() : DimensionType.OVERWORLD;
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(player.dimension));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(player.dimension));
		}

		ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(
			this.server, this.server.getWorld(player.dimension), player.getGameProfile(), serverPlayerInteractionManager
		);
		serverPlayerEntity.networkHandler = player.networkHandler;
		serverPlayerEntity.copyFrom(player, bl);
		serverPlayerEntity.setEntityId(player.getEntityId());
		serverPlayerEntity.setMainArm(player.getMainArm());

		for (String string : player.getScoreboardTags()) {
			serverPlayerEntity.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.getWorld(player.dimension);
		this.setGameMode(serverPlayerEntity, player, serverWorld);
		boolean bl3 = false;
		if (optional.isPresent()) {
			Vec3d vec3d = (Vec3d)optional.get();
			serverPlayerEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
			serverPlayerEntity.setSpawnPoint(player.dimension, blockPos, bl2, false);
			bl3 = !bl && serverWorld.getBlockState(blockPos).getBlock() instanceof RespawnAnchorBlock;
		} else if (blockPos != null) {
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0F));
		}

		while (!serverWorld.doesNotCollide(serverPlayerEntity) && serverPlayerEntity.getY() < 256.0) {
			serverPlayerEntity.updatePosition(serverPlayerEntity.getX(), serverPlayerEntity.getY() + 1.0, serverPlayerEntity.getZ());
		}

		class_5217 lv = serverPlayerEntity.world.getLevelProperties();
		serverPlayerEntity.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity.dimension,
					BiomeAccess.method_27984(serverPlayerEntity.getServerWorld().getSeed()),
					serverPlayerEntity.interactionManager.getGameMode(),
					serverPlayerEntity.getServerWorld().method_27982(),
					serverPlayerEntity.getServerWorld().method_28125(),
					bl
				)
			);
		serverPlayerEntity.networkHandler
			.requestTeleport(serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(serverWorld.method_27911()));
		serverPlayerEntity.networkHandler.sendPacket(new DifficultyS2CPacket(lv.getDifficulty(), lv.isDifficultyLocked()));
		serverPlayerEntity.networkHandler
			.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
		this.sendWorldInfo(serverPlayerEntity, serverWorld);
		this.sendCommandTree(serverPlayerEntity);
		serverWorld.onPlayerRespawned(serverPlayerEntity);
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		serverPlayerEntity.onSpawn();
		serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
		if (bl3) {
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

	public void sendToDimension(Packet<?> packet, DimensionType dimension) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.dimension == dimension) {
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
					serverPlayerEntity.sendSystemMessage(message);
				}
			}
		}
	}

	public void sendToOtherTeams(PlayerEntity source, Text message) {
		AbstractTeam abstractTeam = source.getScoreboardTeam();
		if (abstractTeam == null) {
			this.sendToAll(message);
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.getScoreboardTeam() != abstractTeam) {
					serverPlayerEntity.sendSystemMessage(message);
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
		return this.ops.contains(profile) || this.server.isHost(profile) && this.server.method_27728().areCommandsAllowed() || this.cheatsAllowed;
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

	public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double d, DimensionType dimension, Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity != player && serverPlayerEntity.dimension == dimension) {
				double e = x - serverPlayerEntity.getX();
				double f = y - serverPlayerEntity.getY();
				double g = z - serverPlayerEntity.getZ();
				if (e * e + f * f + g * g < d * d) {
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
		WorldBorder worldBorder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		player.networkHandler
			.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
		player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(world.method_27911()));
		if (world.isRaining()) {
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0F));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, world.getRainGradient(1.0F)));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, world.getThunderGradient(1.0F)));
		}
	}

	public void sendPlayerStatus(ServerPlayerEntity player) {
		player.openHandledScreen(player.playerScreenHandler);
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

	public CompoundTag getUserData() {
		return null;
	}

	@Environment(EnvType.CLIENT)
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	private void setGameMode(ServerPlayerEntity player, ServerPlayerEntity oldPlayer, ServerWorld world) {
		if (oldPlayer != null) {
			player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode());
		} else if (this.gameMode != null) {
			player.interactionManager.setGameMode(this.gameMode);
		}

		player.interactionManager.setGameModeIfNotPresent(world.getServer().method_27728().getGameMode());
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

	public void broadcastChatMessage(Text message, boolean system) {
		this.server.sendSystemMessage(message);
		MessageType messageType = system ? MessageType.SYSTEM : MessageType.CHAT;
		this.sendToAll(new GameMessageS2CPacket(message, messageType));
	}

	public void sendToAll(Text message) {
		this.broadcastChatMessage(message, true);
	}

	public ServerStatHandler createStatHandler(PlayerEntity player) {
		UUID uUID = player.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.statisticsMap.get(uUID);
		if (serverStatHandler == null) {
			File file = this.server.method_27050(class_5218.field_24181).toFile();
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
			File file = this.server.method_27050(class_5218.field_24180).toFile();
			File file2 = new File(file, uUID + ".json");
			playerAdvancementTracker = new PlayerAdvancementTracker(this.server, file2, player);
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
