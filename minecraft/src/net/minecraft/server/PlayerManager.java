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
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
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
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.config.BannedIpEntry;
import net.minecraft.server.config.BannedIpList;
import net.minecraft.server.config.BannedPlayerEntry;
import net.minecraft.server.config.BannedPlayerList;
import net.minecraft.server.config.OperatorEntry;
import net.minecraft.server.config.OperatorList;
import net.minecraft.server.config.WhitelistList;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
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
	private final BannedPlayerList field_14344 = new BannedPlayerList(BANNED_PLAYERS_FILE);
	private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
	private final OperatorList field_14353 = new OperatorList(OPERATORS_FILE);
	private final WhitelistList field_14361 = new WhitelistList(WHITELIST_FILE);
	private final Map<UUID, ServerStatHandler> statisticsMap = Maps.<UUID, ServerStatHandler>newHashMap();
	private final Map<UUID, PlayerAdvancementTracker> advancementManagerMap = Maps.<UUID, PlayerAdvancementTracker>newHashMap();
	private PlayerSaveHandler field_14358;
	private boolean whitelistEnabled;
	protected final int maxPlayers;
	private int viewDistance;
	private GameMode gameMode;
	private boolean cheatsAllowed;
	private int latencyUpdateTimer;

	public PlayerManager(MinecraftServer minecraftServer, int i) {
		this.server = minecraftServer;
		this.maxPlayers = i;
		this.method_14563().setEnabled(true);
		this.getIpBanList().setEnabled(true);
	}

	public void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		UserCache userCache = this.server.method_3793();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		CompoundTag compoundTag = this.method_14600(serverPlayerEntity);
		ServerWorld serverWorld = this.server.method_3847(serverPlayerEntity.field_6026);
		serverPlayerEntity.method_5866(serverWorld);
		serverPlayerEntity.field_13974.setWorld((ServerWorld)serverPlayerEntity.field_6002);
		String string2 = "local";
		if (clientConnection.getAddress() != null) {
			string2 = clientConnection.getAddress().toString();
		}

		LOGGER.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			serverPlayerEntity.method_5477().getString(),
			string2,
			serverPlayerEntity.getEntityId(),
			serverPlayerEntity.x,
			serverPlayerEntity.y,
			serverPlayerEntity.z
		);
		LevelProperties levelProperties = serverWorld.method_8401();
		this.method_14615(serverPlayerEntity, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, clientConnection, serverPlayerEntity);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				serverPlayerEntity.getEntityId(),
				serverPlayerEntity.field_13974.getGameMode(),
				levelProperties.isHardcore(),
				serverWorld.field_9247.method_12460(),
				this.getMaxPlayerCount(),
				levelProperties.getGeneratorType(),
				serverWorld.getGameRules().getBoolean("reducedDebugInfo")
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket(CustomPayloadS2CPacket.field_12158, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values()));
		serverPlayNetworkHandler.sendPacket(new SynchronizeTagsS2CPacket(this.server.method_3801()));
		this.method_14576(serverPlayerEntity);
		serverPlayerEntity.method_14248().method_14914();
		serverPlayerEntity.method_14253().sendInitRecipesPacket(serverPlayerEntity);
		this.method_14588(serverWorld.method_14170(), serverPlayerEntity);
		this.server.method_3856();
		TextComponent textComponent;
		if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) {
			textComponent = new TranslatableTextComponent("multiplayer.player.joined", serverPlayerEntity.method_5476());
		} else {
			textComponent = new TranslatableTextComponent("multiplayer.player.joined.renamed", serverPlayerEntity.method_5476(), string);
		}

		this.sendToAll(textComponent.applyFormat(TextFormat.field_1054));
		serverPlayNetworkHandler.teleportRequest(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Type.ADD, serverPlayerEntity));

		for (int i = 0; i < this.players.size(); i++) {
			serverPlayerEntity.field_13987.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Type.ADD, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld.method_18213(serverPlayerEntity);
		this.server.method_3837().method_12975(serverPlayerEntity);
		this.method_14606(serverPlayerEntity, serverWorld);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			serverPlayerEntity.method_14255(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getPotionEffects()) {
			serverPlayNetworkHandler.sendPacket(new EntityPotionEffectS2CPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}

		if (compoundTag != null && compoundTag.containsKey("RootVehicle", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("RootVehicle");
			Entity entity = EntityType.method_17842(compoundTag2.getCompound("Entity"), serverWorld, entityx -> !serverWorld.method_18768(entityx) ? null : entityx);
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
					serverWorld.method_18774(entity);

					for (Entity entity2x : entity.getPassengersDeep()) {
						serverWorld.method_18774(entity2x);
					}
				}
			}
		}

		serverPlayerEntity.method_14235();
	}

	protected void method_14588(ServerScoreboard serverScoreboard, ServerPlayerEntity serverPlayerEntity) {
		Set<ScoreboardObjective> set = Sets.<ScoreboardObjective>newHashSet();

		for (ScoreboardTeam scoreboardTeam : serverScoreboard.getTeams()) {
			serverPlayerEntity.field_13987.sendPacket(new TeamS2CPacket(scoreboardTeam, 0));
		}

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = serverScoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
				for (Packet<?> packet : serverScoreboard.method_12937(scoreboardObjective)) {
					serverPlayerEntity.field_13987.sendPacket(packet);
				}

				set.add(scoreboardObjective);
			}
		}
	}

	public void method_14591(ServerWorld serverWorld) {
		this.field_14358 = serverWorld.getSaveHandler();
		serverWorld.method_8621().addListener(new WorldBorderListener() {
			@Override
			public void method_11934(WorldBorder worldBorder, double d) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_SIZE));
			}

			@Override
			public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INTERPOLATE_SIZE));
			}

			@Override
			public void method_11930(WorldBorder worldBorder, double d, double e) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_CENTER));
			}

			@Override
			public void method_11932(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
			}

			@Override
			public void method_11933(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
			}

			@Override
			public void method_11929(WorldBorder worldBorder, double d) {
			}

			@Override
			public void method_11935(WorldBorder worldBorder, double d) {
			}
		});
	}

	@Nullable
	public CompoundTag method_14600(ServerPlayerEntity serverPlayerEntity) {
		CompoundTag compoundTag = this.server.method_3847(DimensionType.field_13072).method_8401().method_226();
		CompoundTag compoundTag2;
		if (serverPlayerEntity.method_5477().getString().equals(this.server.getUserName()) && compoundTag != null) {
			compoundTag2 = compoundTag;
			serverPlayerEntity.method_5651(compoundTag);
			LOGGER.debug("loading single player");
		} else {
			compoundTag2 = this.field_14358.method_261(serverPlayerEntity);
		}

		return compoundTag2;
	}

	protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
		this.field_14358.savePlayerData(serverPlayerEntity);
		ServerStatHandler serverStatHandler = (ServerStatHandler)this.statisticsMap.get(serverPlayerEntity.getUuid());
		if (serverStatHandler != null) {
			serverStatHandler.save();
		}

		PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementManagerMap.get(serverPlayerEntity.getUuid());
		if (playerAdvancementTracker != null) {
			playerAdvancementTracker.save();
		}
	}

	public void method_14611(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
		serverPlayerEntity.method_7281(Stats.field_15389);
		this.savePlayerData(serverPlayerEntity);
		if (serverPlayerEntity.hasVehicle()) {
			Entity entity = serverPlayerEntity.getTopmostRiddenEntity();
			if (entity.method_5817()) {
				LOGGER.debug("Removing player mount");
				serverPlayerEntity.stopRiding();
				serverWorld.method_18774(entity);

				for (Entity entity2 : entity.getPassengersDeep()) {
					serverWorld.method_18774(entity2);
				}

				serverWorld.method_8497(serverPlayerEntity.chunkX, serverPlayerEntity.chunkZ).markDirty();
			}
		}

		serverPlayerEntity.method_18375();
		serverWorld.method_18770(serverPlayerEntity);
		serverPlayerEntity.getAdvancementManager().clearCriterions();
		this.players.remove(serverPlayerEntity);
		this.server.method_3837().method_12976(serverPlayerEntity);
		UUID uUID = serverPlayerEntity.getUuid();
		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity2 == serverPlayerEntity) {
			this.playerMap.remove(uUID);
			this.statisticsMap.remove(uUID);
			this.advancementManagerMap.remove(uUID);
		}

		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Type.REMOVE, serverPlayerEntity));
	}

	@Nullable
	public TextComponent checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.field_14344.contains(gameProfile)) {
			BannedPlayerEntry bannedPlayerEntry = this.field_14344.get(gameProfile);
			TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				textComponent.append(new TranslatableTextComponent("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return textComponent;
		} else if (!this.isWhitelisted(gameProfile)) {
			return new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.contains(socketAddress)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.method_14528(socketAddress);
			TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
			if (bannedIpEntry.getExpiryDate() != null) {
				textComponent.append(new TranslatableTextComponent("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
			}

			return textComponent;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)
				? new TranslatableTextComponent("multiplayer.disconnect.server_full")
				: null;
		}
	}

	public ServerPlayerEntity method_14613(GameProfile gameProfile) {
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
			serverPlayerEntity3.field_13987.disconnect(new TranslatableTextComponent("multiplayer.disconnect.duplicate_login"));
		}

		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.method_3847(DimensionType.field_13072));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.method_3847(DimensionType.field_13072));
		}

		return new ServerPlayerEntity(this.server, this.server.method_3847(DimensionType.field_13072), gameProfile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity method_14556(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, boolean bl) {
		this.players.remove(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().method_18770(serverPlayerEntity);
		BlockPos blockPos = serverPlayerEntity.method_7280();
		boolean bl2 = serverPlayerEntity.isSpawnForced();
		serverPlayerEntity.field_6026 = dimensionType;
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.method_3847(serverPlayerEntity.field_6026));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.method_3847(serverPlayerEntity.field_6026));
		}

		ServerPlayerEntity serverPlayerEntity2 = new ServerPlayerEntity(
			this.server, this.server.method_3847(serverPlayerEntity.field_6026), serverPlayerEntity.getGameProfile(), serverPlayerInteractionManager
		);
		serverPlayerEntity2.field_13987 = serverPlayerEntity.field_13987;
		serverPlayerEntity2.method_14203(serverPlayerEntity, bl);
		serverPlayerEntity2.setEntityId(serverPlayerEntity.getEntityId());
		serverPlayerEntity2.setMainHand(serverPlayerEntity.getMainHand());

		for (String string : serverPlayerEntity.getScoreboardTags()) {
			serverPlayerEntity2.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.method_3847(serverPlayerEntity.field_6026);
		this.method_14615(serverPlayerEntity2, serverPlayerEntity, serverWorld);
		if (blockPos != null) {
			BlockPos blockPos2 = PlayerEntity.method_7288(this.server.method_3847(serverPlayerEntity.field_6026), blockPos, bl2);
			if (blockPos2 != null) {
				serverPlayerEntity2.setPositionAndAngles(
					(double)((float)blockPos2.getX() + 0.5F), (double)((float)blockPos2.getY() + 0.1F), (double)((float)blockPos2.getZ() + 0.5F), 0.0F, 0.0F
				);
				serverPlayerEntity2.method_7289(blockPos, bl2);
			} else {
				serverPlayerEntity2.field_13987.sendPacket(new GameStateChangeS2CPacket(0, 0.0F));
			}
		}

		while (!serverWorld.method_17892(serverPlayerEntity2) && serverPlayerEntity2.y < 256.0) {
			serverPlayerEntity2.setPosition(serverPlayerEntity2.x, serverPlayerEntity2.y + 1.0, serverPlayerEntity2.z);
		}

		LevelProperties levelProperties = serverPlayerEntity2.field_6002.method_8401();
		serverPlayerEntity2.field_13987
			.sendPacket(new PlayerRespawnS2CPacket(serverPlayerEntity2.field_6026, levelProperties.getGeneratorType(), serverPlayerEntity2.field_13974.getGameMode()));
		BlockPos blockPos3 = serverWorld.method_8395();
		serverPlayerEntity2.field_13987
			.teleportRequest(serverPlayerEntity2.x, serverPlayerEntity2.y, serverPlayerEntity2.z, serverPlayerEntity2.yaw, serverPlayerEntity2.pitch);
		serverPlayerEntity2.field_13987.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos3));
		serverPlayerEntity2.field_13987.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayerEntity2.field_13987
			.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity2.experienceBarProgress, serverPlayerEntity2.experienceLevel, serverPlayerEntity2.experience));
		this.method_14606(serverPlayerEntity2, serverWorld);
		this.method_14576(serverPlayerEntity2);
		serverWorld.method_18215(serverPlayerEntity2);
		this.players.add(serverPlayerEntity2);
		this.playerMap.put(serverPlayerEntity2.getUuid(), serverPlayerEntity2);
		serverPlayerEntity2.method_14235();
		serverPlayerEntity2.setHealth(serverPlayerEntity2.getHealth());
		return serverPlayerEntity2;
	}

	public void method_14576(ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		int i = this.server.getPermissionLevel(gameProfile);
		this.method_14596(serverPlayerEntity, i);
	}

	public void updatePlayerLatency() {
		if (++this.latencyUpdateTimer > 600) {
			this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Type.UPDATE_LATENCY, this.players));
			this.latencyUpdateTimer = 0;
		}
	}

	public void sendToAll(Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).field_13987.sendPacket(packet);
		}
	}

	public void sendToDimension(Packet<?> packet, DimensionType dimensionType) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.field_6026 == dimensionType) {
				serverPlayerEntity.field_13987.sendPacket(packet);
			}
		}
	}

	public void sendToTeam(PlayerEntity playerEntity, TextComponent textComponent) {
		AbstractScoreboardTeam abstractScoreboardTeam = playerEntity.method_5781();
		if (abstractScoreboardTeam != null) {
			for (String string : abstractScoreboardTeam.getPlayerList()) {
				ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
				if (serverPlayerEntity != null && serverPlayerEntity != playerEntity) {
					serverPlayerEntity.method_9203(textComponent);
				}
			}
		}
	}

	public void sendToOtherTeams(PlayerEntity playerEntity, TextComponent textComponent) {
		AbstractScoreboardTeam abstractScoreboardTeam = playerEntity.method_5781();
		if (abstractScoreboardTeam == null) {
			this.sendToAll(textComponent);
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.method_5781() != abstractScoreboardTeam) {
					serverPlayerEntity.method_9203(textComponent);
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

	public BannedPlayerList method_14563() {
		return this.field_14344;
	}

	public BannedIpList getIpBanList() {
		return this.bannedIps;
	}

	public void addToOperators(GameProfile gameProfile) {
		this.field_14353.add(new OperatorEntry(gameProfile, this.server.getOpPermissionLevel(), this.field_14353.isOp(gameProfile)));
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
		if (serverPlayerEntity != null) {
			this.method_14576(serverPlayerEntity);
		}
	}

	public void removeFromOperators(GameProfile gameProfile) {
		this.field_14353.remove(gameProfile);
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
		if (serverPlayerEntity != null) {
			this.method_14576(serverPlayerEntity);
		}
	}

	private void method_14596(ServerPlayerEntity serverPlayerEntity, int i) {
		if (serverPlayerEntity.field_13987 != null) {
			byte b;
			if (i <= 0) {
				b = 24;
			} else if (i >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + i);
			}

			serverPlayerEntity.field_13987.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
		}

		this.server.getCommandManager().method_9241(serverPlayerEntity);
	}

	public boolean isWhitelisted(GameProfile gameProfile) {
		return !this.whitelistEnabled || this.field_14353.contains(gameProfile) || this.field_14361.contains(gameProfile);
	}

	public boolean isOperator(GameProfile gameProfile) {
		return this.field_14353.contains(gameProfile)
			|| this.server.method_19466(gameProfile) && this.server.method_3847(DimensionType.field_13072).method_8401().areCommandsAllowed()
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
			if (serverPlayerEntity != playerEntity && serverPlayerEntity.field_6026 == dimensionType) {
				double h = d - serverPlayerEntity.x;
				double j = e - serverPlayerEntity.y;
				double k = f - serverPlayerEntity.z;
				if (h * h + j * j + k * k < g * g) {
					serverPlayerEntity.field_13987.sendPacket(packet);
				}
			}
		}
	}

	public void saveAllPlayerData() {
		for (int i = 0; i < this.players.size(); i++) {
			this.savePlayerData((ServerPlayerEntity)this.players.get(i));
		}
	}

	public WhitelistList method_14590() {
		return this.field_14361;
	}

	public String[] getWhitelistedNames() {
		return this.field_14361.getNames();
	}

	public OperatorList method_14603() {
		return this.field_14353;
	}

	public String[] getOpNames() {
		return this.field_14353.getNames();
	}

	public void reloadWhitelist() {
	}

	public void method_14606(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld) {
		WorldBorder worldBorder = this.server.method_3847(DimensionType.field_13072).method_8621();
		serverPlayerEntity.field_13987.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		serverPlayerEntity.field_13987
			.sendPacket(new WorldTimeUpdateS2CPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")));
		BlockPos blockPos = serverWorld.method_8395();
		serverPlayerEntity.field_13987.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos));
		if (serverWorld.isRaining()) {
			serverPlayerEntity.field_13987.sendPacket(new GameStateChangeS2CPacket(1, 0.0F));
			serverPlayerEntity.field_13987.sendPacket(new GameStateChangeS2CPacket(7, serverWorld.getRainGradient(1.0F)));
			serverPlayerEntity.field_13987.sendPacket(new GameStateChangeS2CPacket(8, serverWorld.getThunderGradient(1.0F)));
		}
	}

	public void method_14594(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.method_14204(serverPlayerEntity.field_7498);
		serverPlayerEntity.method_14217();
		serverPlayerEntity.field_13987.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
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

	private void method_14615(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity2, IWorld iWorld) {
		if (serverPlayerEntity2 != null) {
			serverPlayerEntity.field_13974.setGameMode(serverPlayerEntity2.field_13974.getGameMode());
		} else if (this.gameMode != null) {
			serverPlayerEntity.field_13974.setGameMode(this.gameMode);
		}

		serverPlayerEntity.field_13974.setGameModeIfNotPresent(iWorld.method_8401().getGameMode());
	}

	@Environment(EnvType.CLIENT)
	public void setCheatsAllowed(boolean bl) {
		this.cheatsAllowed = bl;
	}

	public void disconnectAllPlayers() {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).field_13987.disconnect(new TranslatableTextComponent("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void broadcastChatMessage(TextComponent textComponent, boolean bl) {
		this.server.method_9203(textComponent);
		ChatMessageType chatMessageType = bl ? ChatMessageType.field_11735 : ChatMessageType.field_11737;
		this.sendToAll(new ChatMessageS2CPacket(textComponent, chatMessageType));
	}

	public void sendToAll(TextComponent textComponent) {
		this.broadcastChatMessage(textComponent, true);
	}

	public ServerStatHandler method_14583(PlayerEntity playerEntity) {
		UUID uUID = playerEntity.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.statisticsMap.get(uUID);
		if (serverStatHandler == null) {
			File file = new File(this.server.method_3847(DimensionType.field_13072).getSaveHandler().getWorldDir(), "stats");
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, playerEntity.method_5477().getString() + ".json");
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
			File file = new File(this.server.method_3847(DimensionType.field_13072).getSaveHandler().getWorldDir(), "advancements");
			File file2 = new File(file, uUID + ".json");
			playerAdvancementTracker = new PlayerAdvancementTracker(this.server, file2, serverPlayerEntity);
			this.advancementManagerMap.put(uUID, playerAdvancementTracker);
		}

		playerAdvancementTracker.method_12875(serverPlayerEntity);
		return playerAdvancementTracker;
	}

	public void setViewDistance(int i, int j) {
		this.viewDistance = i;

		for (ServerWorld serverWorld : this.server.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.method_14178().applyViewDistance(i, j);
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

		this.sendToAll(new SynchronizeTagsS2CPacket(this.server.method_3801()));
		SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket = new SynchronizeRecipesS2CPacket(this.server.getRecipeManager().values());

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.field_13987.sendPacket(synchronizeRecipesS2CPacket);
			serverPlayerEntity.method_14253().sendInitRecipesPacket(serverPlayerEntity);
		}
	}

	public boolean areCheatsAllowed() {
		return this.cheatsAllowed;
	}
}
