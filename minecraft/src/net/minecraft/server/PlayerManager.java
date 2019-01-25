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
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.network.packet.DifficultyClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateClientPacket;
import net.minecraft.client.network.packet.GameJoinClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesClientPacket;
import net.minecraft.client.network.packet.PlayerListClientPacket;
import net.minecraft.client.network.packet.PlayerRespawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionClientPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesClientPacket;
import net.minecraft.client.network.packet.SynchronizeTagsClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.client.network.packet.WorldBorderClientPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;
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
import net.minecraft.util.math.MathHelper;
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
	private final BannedPlayerList bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
	private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
	private final OperatorList ops = new OperatorList(OPERATORS_FILE);
	private final WhitelistList whitelist = new WhitelistList(WHITELIST_FILE);
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
		this.getUserBanList().setEnabled(true);
		this.getIpBanList().setEnabled(true);
	}

	public void onPlayerConnect(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		CompoundTag compoundTag = this.method_14600(serverPlayerEntity);
		serverPlayerEntity.setWorld(this.server.getWorld(serverPlayerEntity.dimension));
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
			serverPlayerEntity.x,
			serverPlayerEntity.y,
			serverPlayerEntity.z
		);
		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		LevelProperties levelProperties = serverWorld.getLevelProperties();
		this.method_14615(serverPlayerEntity, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, clientConnection, serverPlayerEntity);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinClientPacket(
				serverPlayerEntity.getEntityId(),
				serverPlayerEntity.interactionManager.getGameMode(),
				levelProperties.isHardcore(),
				serverWorld.dimension.getType(),
				serverWorld.getDifficulty(),
				this.getMaxPlayerCount(),
				levelProperties.getGeneratorType(),
				serverWorld.getGameRules().getBoolean("reducedDebugInfo")
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadClientPacket(CustomPayloadClientPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyClientPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesClientPacket(serverPlayerEntity.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeClientPacket(serverPlayerEntity.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesClientPacket(this.server.getRecipeManager().values()));
		serverPlayNetworkHandler.sendPacket(new SynchronizeTagsClientPacket(this.server.getTagManager()));
		this.method_14576(serverPlayerEntity);
		serverPlayerEntity.method_14248().method_14914();
		serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
		this.method_14588(serverWorld.getScoreboard(), serverPlayerEntity);
		this.server.method_3856();
		TextComponent textComponent;
		if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) {
			textComponent = new TranslatableTextComponent("multiplayer.player.joined", serverPlayerEntity.getDisplayName());
		} else {
			textComponent = new TranslatableTextComponent("multiplayer.player.joined.renamed", serverPlayerEntity.getDisplayName(), string);
		}

		this.sendToAll(textComponent.applyFormat(TextFormat.YELLOW));
		serverPlayNetworkHandler.teleportRequest(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		this.method_14610(serverPlayerEntity);
		this.method_14606(serverPlayerEntity, serverWorld);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			serverPlayerEntity.method_14255(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getPotionEffects()) {
			serverPlayNetworkHandler.sendPacket(new EntityPotionEffectClientPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}

		if (compoundTag != null && compoundTag.containsKey("RootVehicle", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("RootVehicle");
			Entity entity = EntityType.method_17844(compoundTag2.getCompound("Entity"), serverWorld, true);
			if (entity != null) {
				UUID uUID = compoundTag2.getUuid("Attach");
				if (entity.getUuid().equals(uUID)) {
					serverPlayerEntity.startRiding(entity, true);
				} else {
					for (Entity entity2 : entity.method_5736()) {
						if (entity2.getUuid().equals(uUID)) {
							serverPlayerEntity.startRiding(entity2, true);
							break;
						}
					}
				}

				if (!serverPlayerEntity.hasVehicle()) {
					LOGGER.warn("Couldn't reattach entity to player");
					serverWorld.method_8507(entity);

					for (Entity entity2x : entity.method_5736()) {
						serverWorld.method_8507(entity2x);
					}
				}
			}
		}

		serverPlayerEntity.method_14235();
	}

	protected void method_14588(ServerScoreboard serverScoreboard, ServerPlayerEntity serverPlayerEntity) {
		Set<ScoreboardObjective> set = Sets.<ScoreboardObjective>newHashSet();

		for (ScoreboardTeam scoreboardTeam : serverScoreboard.getTeams()) {
			serverPlayerEntity.networkHandler.sendPacket(new TeamClientPacket(scoreboardTeam, 0));
		}

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = serverScoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
				for (Packet<?> packet : serverScoreboard.method_12937(scoreboardObjective)) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}

				set.add(scoreboardObjective);
			}
		}
	}

	public void method_14591(ServerWorld serverWorld) {
		this.field_14358 = serverWorld.method_17982();
		serverWorld.getWorldBorder().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder worldBorder, double d) {
				PlayerManager.this.sendToAll(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.SET_SIZE));
			}

			@Override
			public void method_11931(WorldBorder worldBorder, double d, double e, long l) {
				PlayerManager.this.sendToAll(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.INTERPOLATE_SIZE));
			}

			@Override
			public void onCenterChanged(WorldBorder worldBorder, double d, double e) {
				PlayerManager.this.sendToAll(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.SET_CENTER));
			}

			@Override
			public void onWarningTimeChanged(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.SET_WARNING_TIME));
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder worldBorder, int i) {
				PlayerManager.this.sendToAll(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.SET_WARNING_BLOCKS));
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder worldBorder, double d) {
			}

			@Override
			public void onSafeZoneChanged(WorldBorder worldBorder, double d) {
			}
		});
	}

	public void method_14612(ServerPlayerEntity serverPlayerEntity, @Nullable ServerWorld serverWorld) {
		ServerWorld serverWorld2 = serverPlayerEntity.getServerWorld();
		if (serverWorld != null) {
			serverWorld.getChunkManager().removePlayer(serverPlayerEntity);
		}

		serverWorld2.getChunkManager().addPlayer(serverPlayerEntity);
		if (serverWorld != null) {
			Criterions.CHANGED_DIMENSION.handle(serverPlayerEntity, serverWorld.dimension.getType(), serverWorld2.dimension.getType());
			if (serverWorld.dimension.getType() == DimensionType.field_13076
				&& serverPlayerEntity.world.dimension.getType() == DimensionType.field_13072
				&& serverPlayerEntity.getEnteredNetherPosition() != null) {
				Criterions.NETHER_TRAVEL.handle(serverPlayerEntity, serverPlayerEntity.getEnteredNetherPosition());
			}
		}
	}

	@Nullable
	public CompoundTag method_14600(ServerPlayerEntity serverPlayerEntity) {
		CompoundTag compoundTag = this.server.getWorld(DimensionType.field_13072).getLevelProperties().getPlayerData();
		CompoundTag compoundTag2;
		if (serverPlayerEntity.getName().getString().equals(this.server.getUserName()) && compoundTag != null) {
			compoundTag2 = compoundTag;
			serverPlayerEntity.fromTag(compoundTag);
			LOGGER.debug("loading single player");
		} else {
			compoundTag2 = this.field_14358.loadPlayerData(serverPlayerEntity);
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

	public void method_14610(ServerPlayerEntity serverPlayerEntity) {
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		this.sendToAll(new PlayerListClientPacket(PlayerListClientPacket.Type.ADD, serverPlayerEntity));
		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);

		for (int i = 0; i < this.players.size(); i++) {
			serverPlayerEntity.networkHandler.sendPacket(new PlayerListClientPacket(PlayerListClientPacket.Type.ADD, (ServerPlayerEntity)this.players.get(i)));
		}

		this.method_14612(serverPlayerEntity, null);
		serverWorld.spawnEntity(serverPlayerEntity);
		this.server.getBossBarManager().method_12975(serverPlayerEntity);
	}

	public void method_14575(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.getServerWorld().getChunkManager().updateChunkWatchingForPlayer(serverPlayerEntity);
	}

	public void method_14611(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
		serverPlayerEntity.increaseStat(Stats.field_15389);
		this.savePlayerData(serverPlayerEntity);
		if (serverPlayerEntity.hasVehicle()) {
			Entity entity = serverPlayerEntity.getTopmostRiddenEntity();
			if (entity.method_5817()) {
				LOGGER.debug("Removing player mount");
				serverPlayerEntity.stopRiding();
				serverWorld.method_8507(entity);

				for (Entity entity2 : entity.method_5736()) {
					serverWorld.method_8507(entity2);
				}

				serverWorld.getWorldChunk(serverPlayerEntity.chunkX, serverPlayerEntity.chunkZ).markDirty();
			}
		}

		serverWorld.removeEntity(serverPlayerEntity);
		serverWorld.getChunkManager().removePlayer(serverPlayerEntity);
		serverPlayerEntity.getAdvancementManager().clearCriterions();
		this.players.remove(serverPlayerEntity);
		this.server.getBossBarManager().method_12976(serverPlayerEntity);
		UUID uUID = serverPlayerEntity.getUuid();
		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity2 == serverPlayerEntity) {
			this.playerMap.remove(uUID);
			this.statisticsMap.remove(uUID);
			this.advancementManagerMap.remove(uUID);
		}

		this.sendToAll(new PlayerListClientPacket(PlayerListClientPacket.Type.REMOVE, serverPlayerEntity));
	}

	@Nullable
	public TextComponent method_14586(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.bannedProfiles.contains(gameProfile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(gameProfile);
			TextComponent textComponent = new TranslatableTextComponent("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				textComponent.append(new TranslatableTextComponent("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return textComponent;
		} else if (!this.isWhitelisted(gameProfile)) {
			return new TranslatableTextComponent("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.contains(socketAddress)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(socketAddress);
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
			serverPlayerEntity3.networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.duplicate_login"));
		}

		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(DimensionType.field_13072));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(DimensionType.field_13072));
		}

		return new ServerPlayerEntity(this.server, this.server.getWorld(DimensionType.field_13072), gameProfile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity method_14556(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, boolean bl) {
		serverPlayerEntity.getServerWorld().getEntityTracker().method_14072(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().getEntityTracker().remove(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().getChunkManager().removePlayer(serverPlayerEntity);
		this.players.remove(serverPlayerEntity);
		this.server.getWorld(serverPlayerEntity.dimension).method_8507(serverPlayerEntity);
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
		serverPlayerEntity2.method_14203(serverPlayerEntity, bl);
		serverPlayerEntity2.setEntityId(serverPlayerEntity.getEntityId());
		serverPlayerEntity2.setMainHand(serverPlayerEntity.getMainHand());

		for (String string : serverPlayerEntity.getScoreboardTags()) {
			serverPlayerEntity2.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		this.method_14615(serverPlayerEntity2, serverPlayerEntity, serverWorld);
		if (blockPos != null) {
			BlockPos blockPos2 = PlayerEntity.method_7288(this.server.getWorld(serverPlayerEntity.dimension), blockPos, bl2);
			if (blockPos2 != null) {
				serverPlayerEntity2.setPositionAndAngles(
					(double)((float)blockPos2.getX() + 0.5F), (double)((float)blockPos2.getY() + 0.1F), (double)((float)blockPos2.getZ() + 0.5F), 0.0F, 0.0F
				);
				serverPlayerEntity2.setPlayerSpawn(blockPos, bl2);
			} else {
				serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeClientPacket(0, 0.0F));
			}
		}

		while (!serverWorld.method_8587(serverPlayerEntity2, serverPlayerEntity2.getBoundingBox()) && serverPlayerEntity2.y < 256.0) {
			serverPlayerEntity2.setPosition(serverPlayerEntity2.x, serverPlayerEntity2.y + 1.0, serverPlayerEntity2.z);
		}

		serverPlayerEntity2.networkHandler
			.sendPacket(
				new PlayerRespawnClientPacket(
					serverPlayerEntity2.dimension,
					serverPlayerEntity2.world.getDifficulty(),
					serverPlayerEntity2.world.getLevelProperties().getGeneratorType(),
					serverPlayerEntity2.interactionManager.getGameMode()
				)
			);
		BlockPos blockPos2 = serverWorld.getSpawnPos();
		serverPlayerEntity2.networkHandler
			.teleportRequest(serverPlayerEntity2.x, serverPlayerEntity2.y, serverPlayerEntity2.z, serverPlayerEntity2.yaw, serverPlayerEntity2.pitch);
		serverPlayerEntity2.networkHandler.sendPacket(new PlayerSpawnPositionClientPacket(blockPos2));
		serverPlayerEntity2.networkHandler
			.sendPacket(
				new ExperienceBarUpdateClientPacket(serverPlayerEntity2.experienceBarProgress, serverPlayerEntity2.experienceLevel, serverPlayerEntity2.experience)
			);
		this.method_14606(serverPlayerEntity2, serverWorld);
		this.method_14576(serverPlayerEntity2);
		serverWorld.getChunkManager().addPlayer(serverPlayerEntity2);
		serverWorld.spawnEntity(serverPlayerEntity2);
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

	public void method_14598(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType) {
		DimensionType dimensionType2 = serverPlayerEntity.dimension;
		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		serverPlayerEntity.dimension = dimensionType;
		ServerWorld serverWorld2 = this.server.getWorld(serverPlayerEntity.dimension);
		serverPlayerEntity.networkHandler
			.sendPacket(
				new PlayerRespawnClientPacket(
					serverPlayerEntity.dimension,
					serverPlayerEntity.world.getDifficulty(),
					serverPlayerEntity.world.getLevelProperties().getGeneratorType(),
					serverPlayerEntity.interactionManager.getGameMode()
				)
			);
		this.method_14576(serverPlayerEntity);
		serverWorld.method_8507(serverPlayerEntity);
		serverPlayerEntity.invalid = false;
		this.method_14558(serverPlayerEntity, dimensionType2, serverWorld, serverWorld2);
		this.method_14612(serverPlayerEntity, serverWorld);
		serverPlayerEntity.networkHandler
			.teleportRequest(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		serverPlayerEntity.interactionManager.setWorld(serverWorld2);
		serverPlayerEntity.networkHandler.sendPacket(new PlayerAbilitiesClientPacket(serverPlayerEntity.abilities));
		this.method_14606(serverPlayerEntity, serverWorld2);
		this.method_14594(serverPlayerEntity);

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getPotionEffects()) {
			serverPlayerEntity.networkHandler.sendPacket(new EntityPotionEffectClientPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}
	}

	public void method_14558(Entity entity, DimensionType dimensionType, ServerWorld serverWorld, ServerWorld serverWorld2) {
		double d = entity.x;
		double e = entity.z;
		double f = 8.0;
		float g = entity.yaw;
		serverWorld.getProfiler().push("moving");
		if (entity.dimension == DimensionType.field_13076) {
			d = MathHelper.clamp(d / 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
			e = MathHelper.clamp(e / 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
			entity.setPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isValid()) {
				serverWorld.method_8553(entity, false);
			}
		} else if (entity.dimension == DimensionType.field_13072) {
			d = MathHelper.clamp(d * 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
			e = MathHelper.clamp(e * 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
			entity.setPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isValid()) {
				serverWorld.method_8553(entity, false);
			}
		} else {
			BlockPos blockPos;
			if (dimensionType == DimensionType.field_13078) {
				blockPos = serverWorld2.getSpawnPos();
			} else {
				blockPos = serverWorld2.getForcedSpawnPoint();
			}

			d = (double)blockPos.getX();
			entity.y = (double)blockPos.getY();
			e = (double)blockPos.getZ();
			entity.setPositionAndAngles(d, entity.y, e, 90.0F, 0.0F);
			if (entity.isValid()) {
				serverWorld.method_8553(entity, false);
			}
		}

		serverWorld.getProfiler().pop();
		if (dimensionType != DimensionType.field_13078) {
			serverWorld.getProfiler().push("placing");
			d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
			e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
			if (entity.isValid()) {
				entity.setPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
				serverWorld2.getPortalForcer().method_8655(entity, g);
				serverWorld2.spawnEntity(entity);
				serverWorld2.method_8553(entity, false);
			}

			serverWorld.getProfiler().pop();
		}

		entity.setWorld(serverWorld2);
	}

	public void updatePlayerLatency() {
		if (++this.latencyUpdateTimer > 600) {
			this.sendToAll(new PlayerListClientPacket(PlayerListClientPacket.Type.UPDATE_LATENCY, this.players));
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

	public void sendToTeam(PlayerEntity playerEntity, TextComponent textComponent) {
		AbstractScoreboardTeam abstractScoreboardTeam = playerEntity.getScoreboardTeam();
		if (abstractScoreboardTeam != null) {
			for (String string : abstractScoreboardTeam.getPlayerList()) {
				ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
				if (serverPlayerEntity != null && serverPlayerEntity != playerEntity) {
					serverPlayerEntity.appendCommandFeedback(textComponent);
				}
			}
		}
	}

	public void sendToOtherTeams(PlayerEntity playerEntity, TextComponent textComponent) {
		AbstractScoreboardTeam abstractScoreboardTeam = playerEntity.getScoreboardTeam();
		if (abstractScoreboardTeam == null) {
			this.sendToAll(textComponent);
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.getScoreboardTeam() != abstractScoreboardTeam) {
					serverPlayerEntity.appendCommandFeedback(textComponent);
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
			this.method_14576(serverPlayerEntity);
		}
	}

	public void removeFromOperators(GameProfile gameProfile) {
		this.ops.remove(gameProfile);
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(gameProfile.getId());
		if (serverPlayerEntity != null) {
			this.method_14576(serverPlayerEntity);
		}
	}

	private void method_14596(ServerPlayerEntity serverPlayerEntity, int i) {
		if (serverPlayerEntity.networkHandler != null) {
			byte b;
			if (i <= 0) {
				b = 24;
			} else if (i >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + i);
			}

			serverPlayerEntity.networkHandler.sendPacket(new EntityStatusClientPacket(serverPlayerEntity, b));
		}

		this.server.getCommandManager().sendCommandTree(serverPlayerEntity);
	}

	public boolean isWhitelisted(GameProfile gameProfile) {
		return !this.whitelistEnabled || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
	}

	public boolean isOperator(GameProfile gameProfile) {
		return this.ops.contains(gameProfile)
			|| this.server.isSinglePlayer()
				&& this.server.getWorld(DimensionType.field_13072).getLevelProperties().areCommandsAllowed()
				&& this.server.getUserName().equalsIgnoreCase(gameProfile.getName())
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
				double h = d - serverPlayerEntity.x;
				double j = e - serverPlayerEntity.y;
				double k = f - serverPlayerEntity.z;
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

	public WhitelistList getWhitelist() {
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

	public void method_14606(ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld) {
		WorldBorder worldBorder = this.server.getWorld(DimensionType.field_13072).getWorldBorder();
		serverPlayerEntity.networkHandler.sendPacket(new WorldBorderClientPacket(worldBorder, WorldBorderClientPacket.Type.INITIALIZE));
		serverPlayerEntity.networkHandler
			.sendPacket(new WorldTimeUpdateClientPacket(serverWorld.getTime(), serverWorld.getTimeOfDay(), serverWorld.getGameRules().getBoolean("doDaylightCycle")));
		BlockPos blockPos = serverWorld.getSpawnPos();
		serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionClientPacket(blockPos));
		if (serverWorld.isRaining()) {
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeClientPacket(1, 0.0F));
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeClientPacket(7, serverWorld.getRainGradient(1.0F)));
			serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeClientPacket(8, serverWorld.getThunderGradient(1.0F)));
		}
	}

	public void method_14594(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.method_14204(serverPlayerEntity.containerPlayer);
		serverPlayerEntity.method_14217();
		serverPlayerEntity.networkHandler.sendPacket(new HeldItemChangeClientPacket(serverPlayerEntity.inventory.selectedSlot));
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
			((ServerPlayerEntity)this.players.get(i)).networkHandler.disconnect(new TranslatableTextComponent("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void broadcastChatMessage(TextComponent textComponent, boolean bl) {
		this.server.appendCommandFeedback(textComponent);
		ChatMessageType chatMessageType = bl ? ChatMessageType.field_11735 : ChatMessageType.field_11737;
		this.sendToAll(new ChatMessageClientPacket(textComponent, chatMessageType));
	}

	public void sendToAll(TextComponent textComponent) {
		this.broadcastChatMessage(textComponent, true);
	}

	public ServerStatHandler method_14583(PlayerEntity playerEntity) {
		UUID uUID = playerEntity.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.statisticsMap.get(uUID);
		if (serverStatHandler == null) {
			File file = new File(this.server.getWorld(DimensionType.field_13072).method_17982().getWorldDir(), "stats");
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
			File file = new File(this.server.getWorld(DimensionType.field_13072).method_17982().getWorldDir(), "advancements");
			File file2 = new File(file, uUID + ".json");
			playerAdvancementTracker = new PlayerAdvancementTracker(this.server, file2, serverPlayerEntity);
			this.advancementManagerMap.put(uUID, playerAdvancementTracker);
		}

		playerAdvancementTracker.setOwner(serverPlayerEntity);
		return playerAdvancementTracker;
	}

	public void setViewDistance(int i) {
		this.viewDistance = i;

		for (ServerWorld serverWorld : this.server.getWorlds()) {
			if (serverWorld != null) {
				serverWorld.getChunkManager().applyViewDistance(i);
				serverWorld.getEntityTracker().setViewDistance(i);
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

		this.sendToAll(new SynchronizeTagsClientPacket(this.server.getTagManager()));
		SynchronizeRecipesClientPacket synchronizeRecipesClientPacket = new SynchronizeRecipesClientPacket(this.server.getRecipeManager().values());

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.networkHandler.sendPacket(synchronizeRecipesClientPacket);
			serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
		}
	}

	public boolean areCheatsAllowed() {
		return this.cheatsAllowed;
	}
}
