package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ContainerScreenRegistry;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.EndCreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.packet.AdvancementUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockActionS2CPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressS2CPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.BossBarS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.ChunkLoadDistanceS2CPacket;
import net.minecraft.client.network.packet.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.CommandSuggestionsS2CPacket;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.client.network.packet.ConfirmGuiActionS2CPacket;
import net.minecraft.client.network.packet.CooldownUpdateS2CPacket;
import net.minecraft.client.network.packet.CraftResponseS2CPacket;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.DisconnectS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityAttachS2CPacket;
import net.minecraft.client.network.packet.EntityAttributesS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityPassengersSetS2CPacket;
import net.minecraft.client.network.packet.EntityPositionS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalS2CPacket;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateS2CPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnS2CPacket;
import net.minecraft.client.network.packet.ExplosionS2CPacket;
import net.minecraft.client.network.packet.GameJoinS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.network.packet.HeldItemChangeS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.KeepAliveS2CPacket;
import net.minecraft.client.network.packet.LightUpdateS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.network.packet.MapUpdateS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.PaintingSpawnS2CPacket;
import net.minecraft.client.network.packet.ParticleS2CPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityS2CPacket;
import net.minecraft.client.network.packet.PlaySoundIdS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.StatisticsS2CPacket;
import net.minecraft.client.network.packet.StopSoundS2CPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesS2CPacket;
import net.minecraft.client.network.packet.SynchronizeTagsS2CPacket;
import net.minecraft.client.network.packet.TagQueryResponseS2CPacket;
import net.minecraft.client.network.packet.TeamS2CPacket;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.client.network.packet.VehicleMoveS2CPacket;
import net.minecraft.client.network.packet.WorldBorderS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerEntry;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.PointOfInterestDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.RidingMinecartSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler implements ClientPlayPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ClientConnection connection;
	private final GameProfile profile;
	private final Screen loginScreen;
	private MinecraftClient client;
	private ClientWorld world;
	private boolean field_3698;
	private final Map<UUID, PlayerListEntry> playerListEntries = Maps.<UUID, PlayerListEntry>newHashMap();
	private final ClientAdvancementManager advancementHandler;
	private final ClientCommandSource commandSource;
	private RegistryTagManager tagManager = new RegistryTagManager();
	private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
	private int chunkLoadDistance = 3;
	private final Random random = new Random();
	private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
	private final RecipeManager recipeManager = new RecipeManager();
	private final UUID sessionId = UUID.randomUUID();

	public ClientPlayNetworkHandler(MinecraftClient minecraftClient, Screen screen, ClientConnection clientConnection, GameProfile gameProfile) {
		this.client = minecraftClient;
		this.loginScreen = screen;
		this.connection = clientConnection;
		this.profile = gameProfile;
		this.advancementHandler = new ClientAdvancementManager(minecraftClient);
		this.commandSource = new ClientCommandSource(this, minecraftClient);
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
	public void onGameJoin(GameJoinS2CPacket gameJoinS2CPacket) {
		NetworkThreadUtils.forceMainThread(gameJoinS2CPacket, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		this.chunkLoadDistance = gameJoinS2CPacket.getChunkLoadDistance();
		this.world = new ClientWorld(
			this,
			new LevelInfo(0L, gameJoinS2CPacket.getGameMode(), false, gameJoinS2CPacket.isHardcore(), gameJoinS2CPacket.getGeneratorType()),
			gameJoinS2CPacket.getDimension(),
			this.chunkLoadDistance,
			this.client.getProfiler(),
			this.client.worldRenderer
		);
		this.client.joinWorld(this.world);
		if (this.client.player == null) {
			this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook(this.world.getRecipeManager()));
			this.client.player.yaw = -180.0F;
			if (this.client.getServer() != null) {
				this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
			}
		}

		this.client.debugRenderer.method_20413();
		this.client.player.afterSpawn();
		int i = gameJoinS2CPacket.getEntityId();
		this.world.addPlayer(i, this.client.player);
		this.client.player.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(this.client.player);
		this.client.cameraEntity = this.client.player;
		this.client.player.dimension = gameJoinS2CPacket.getDimension();
		this.client.openScreen(new DownloadingTerrainScreen());
		this.client.player.setEntityId(i);
		this.client.player.setReducedDebugInfo(gameJoinS2CPacket.hasReducedDebugInfo());
		this.client.interactionManager.setGameMode(gameJoinS2CPacket.getGameMode());
		this.client.options.onPlayerModelPartChange();
		this.connection
			.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
		this.client.getGame().onStartGameSession();
	}

	@Override
	public void onEntitySpawn(EntitySpawnS2CPacket entitySpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitySpawnS2CPacket, this, this.client);
		double d = entitySpawnS2CPacket.getX();
		double e = entitySpawnS2CPacket.getY();
		double f = entitySpawnS2CPacket.getZ();
		EntityType<?> entityType = entitySpawnS2CPacket.getEntityTypeId();
		Entity entity;
		if (entityType == EntityType.field_6126) {
			entity = new ChestMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6080) {
			entity = new FurnaceMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6053) {
			entity = new TNTMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6142) {
			entity = new MobSpawnerMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6058) {
			entity = new HopperMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6136) {
			entity = new CommandBlockMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6096) {
			entity = new MinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6103) {
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 instanceof PlayerEntity) {
				entity = new FishHookEntity(this.world, (PlayerEntity)entity2, d, e, f);
			} else {
				entity = null;
			}
		} else if (entityType == EntityType.field_6122) {
			entity = new ArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.field_6135) {
			entity = new SpectralArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.field_6127) {
			entity = new TridentEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.field_6068) {
			entity = new SnowballEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6124) {
			entity = new LlamaSpitEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6043) {
			entity = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(entitySpawnS2CPacket.getEntityData()));
		} else if (entityType == EntityType.field_6138) {
			entity = new LeadKnotEntity(this.world, new BlockPos(d, e, f));
		} else if (entityType == EntityType.field_6082) {
			entity = new ThrownEnderpearlEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6061) {
			entity = new EnderEyeEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6133) {
			entity = new FireworkEntity(this.world, d, e, f, ItemStack.EMPTY);
		} else if (entityType == EntityType.field_6066) {
			entity = new FireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6129) {
			entity = new DragonFireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6049) {
			entity = new SmallFireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6130) {
			entity = new ExplodingWitherSkullEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6100) {
			entity = new ShulkerBulletEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.field_6144) {
			entity = new ThrownEggEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6060) {
			entity = new EvokerFangsEntity(this.world, d, e, f, 0.0F, 0, null);
		} else if (entityType == EntityType.field_6045) {
			entity = new ThrownPotionEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6064) {
			entity = new ThrownExperienceBottleEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6121) {
			entity = new BoatEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6063) {
			entity = new TntEntity(this.world, d, e, f, null);
		} else if (entityType == EntityType.field_6131) {
			entity = new ArmorStandEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6110) {
			entity = new EnderCrystalEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6052) {
			entity = new ItemEntity(this.world, d, e, f);
		} else if (entityType == EntityType.field_6089) {
			entity = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(entitySpawnS2CPacket.getEntityData()));
		} else if (entityType == EntityType.field_6083) {
			entity = new AreaEffectCloudEntity(this.world, d, e, f);
		} else {
			entity = null;
		}

		if (entity != null) {
			int i = entitySpawnS2CPacket.getId();
			entity.method_18003(d, e, f);
			entity.pitch = (float)(entitySpawnS2CPacket.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(entitySpawnS2CPacket.getYaw() * 360) / 256.0F;
			entity.setEntityId(i);
			entity.setUuid(entitySpawnS2CPacket.getUuid());
			this.world.addEntity(i, entity);
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundManager().play(new RidingMinecartSoundInstance((AbstractMinecartEntity)entity));
			}
		}
	}

	@Override
	public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket experienceOrbSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(experienceOrbSpawnS2CPacket, this, this.client);
		double d = experienceOrbSpawnS2CPacket.getX();
		double e = experienceOrbSpawnS2CPacket.getY();
		double f = experienceOrbSpawnS2CPacket.getZ();
		Entity entity = new ExperienceOrbEntity(this.world, d, e, f, experienceOrbSpawnS2CPacket.getExperience());
		entity.method_18003(d, e, f);
		entity.yaw = 0.0F;
		entity.pitch = 0.0F;
		entity.setEntityId(experienceOrbSpawnS2CPacket.getId());
		this.world.addEntity(experienceOrbSpawnS2CPacket.getId(), entity);
	}

	@Override
	public void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket entitySpawnGlobalS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitySpawnGlobalS2CPacket, this, this.client);
		double d = entitySpawnGlobalS2CPacket.getX();
		double e = entitySpawnGlobalS2CPacket.getY();
		double f = entitySpawnGlobalS2CPacket.getZ();
		if (entitySpawnGlobalS2CPacket.getEntityTypeId() == 1) {
			LightningEntity lightningEntity = new LightningEntity(this.world, d, e, f, false);
			lightningEntity.method_18003(d, e, f);
			lightningEntity.yaw = 0.0F;
			lightningEntity.pitch = 0.0F;
			lightningEntity.setEntityId(entitySpawnGlobalS2CPacket.getId());
			this.world.addLightning(lightningEntity);
		}
	}

	@Override
	public void onPaintingSpawn(PaintingSpawnS2CPacket paintingSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(paintingSpawnS2CPacket, this, this.client);
		PaintingEntity paintingEntity = new PaintingEntity(
			this.world, paintingSpawnS2CPacket.getPos(), paintingSpawnS2CPacket.getFacing(), paintingSpawnS2CPacket.getMotive()
		);
		paintingEntity.setEntityId(paintingSpawnS2CPacket.getId());
		paintingEntity.setUuid(paintingSpawnS2CPacket.getPaintingUuid());
		this.world.addEntity(paintingSpawnS2CPacket.getId(), paintingEntity);
	}

	@Override
	public void onVelocityUpdate(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityVelocityUpdateS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityVelocityUpdateS2CPacket.getId());
		if (entity != null) {
			entity.setVelocityClient(
				(double)entityVelocityUpdateS2CPacket.getVelocityX() / 8000.0,
				(double)entityVelocityUpdateS2CPacket.getVelocityY() / 8000.0,
				(double)entityVelocityUpdateS2CPacket.getVelocityZ() / 8000.0
			);
		}
	}

	@Override
	public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityTrackerUpdateS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityTrackerUpdateS2CPacket.id());
		if (entity != null && entityTrackerUpdateS2CPacket.getTrackedValues() != null) {
			entity.getDataTracker().writeUpdatedEntries(entityTrackerUpdateS2CPacket.getTrackedValues());
		}
	}

	@Override
	public void onPlayerSpawn(PlayerSpawnS2CPacket playerSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnS2CPacket, this, this.client);
		double d = playerSpawnS2CPacket.getX();
		double e = playerSpawnS2CPacket.getY();
		double f = playerSpawnS2CPacket.getZ();
		float g = (float)(playerSpawnS2CPacket.getYaw() * 360) / 256.0F;
		float h = (float)(playerSpawnS2CPacket.getPitch() * 360) / 256.0F;
		int i = playerSpawnS2CPacket.getId();
		OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(
			this.client.world, this.getPlayerListEntry(playerSpawnS2CPacket.getPlayerUuid()).getProfile()
		);
		otherClientPlayerEntity.setEntityId(i);
		otherClientPlayerEntity.prevX = d;
		otherClientPlayerEntity.prevRenderX = d;
		otherClientPlayerEntity.prevY = e;
		otherClientPlayerEntity.prevRenderY = e;
		otherClientPlayerEntity.prevZ = f;
		otherClientPlayerEntity.prevRenderZ = f;
		otherClientPlayerEntity.method_18003(d, e, f);
		otherClientPlayerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.world.addPlayer(i, otherClientPlayerEntity);
		List<DataTracker.Entry<?>> list = playerSpawnS2CPacket.getTrackedValues();
		if (list != null) {
			otherClientPlayerEntity.getDataTracker().writeUpdatedEntries(list);
		}
	}

	@Override
	public void onEntityPosition(EntityPositionS2CPacket entityPositionS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPositionS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPositionS2CPacket.getId());
		if (entity != null) {
			double d = entityPositionS2CPacket.getX();
			double e = entityPositionS2CPacket.getY();
			double f = entityPositionS2CPacket.getZ();
			entity.method_18003(d, e, f);
			if (!entity.isLogicalSideForUpdatingMovement()) {
				float g = (float)(entityPositionS2CPacket.getYaw() * 360) / 256.0F;
				float h = (float)(entityPositionS2CPacket.getPitch() * 360) / 256.0F;
				if (!(Math.abs(entity.x - d) >= 0.03125) && !(Math.abs(entity.y - e) >= 0.015625) && !(Math.abs(entity.z - f) >= 0.03125)) {
					entity.setPositionAndRotations(entity.x, entity.y, entity.z, g, h, 0, true);
				} else {
					entity.setPositionAndRotations(d, e, f, g, h, 3, true);
				}

				entity.onGround = entityPositionS2CPacket.isOnGround();
			}
		}
	}

	@Override
	public void onHeldItemChange(HeldItemChangeS2CPacket heldItemChangeS2CPacket) {
		NetworkThreadUtils.forceMainThread(heldItemChangeS2CPacket, this, this.client);
		if (PlayerInventory.isValidHotbarIndex(heldItemChangeS2CPacket.getSlot())) {
			this.client.player.inventory.selectedSlot = heldItemChangeS2CPacket.getSlot();
		}
	}

	@Override
	public void onEntityUpdate(EntityS2CPacket entityS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityS2CPacket, this, this.client);
		Entity entity = entityS2CPacket.getEntity(this.world);
		if (entity != null) {
			entity.field_6001 = entity.field_6001 + (long)entityS2CPacket.getDeltaXShort();
			entity.field_6023 = entity.field_6023 + (long)entityS2CPacket.getDeltaYShort();
			entity.field_5954 = entity.field_5954 + (long)entityS2CPacket.getDeltaZShort();
			Vec3d vec3d = EntityS2CPacket.method_18695(entity.field_6001, entity.field_6023, entity.field_5954);
			if (!entity.isLogicalSideForUpdatingMovement()) {
				float f = entityS2CPacket.hasRotation() ? (float)(entityS2CPacket.getYaw() * 360) / 256.0F : entity.yaw;
				float g = entityS2CPacket.hasRotation() ? (float)(entityS2CPacket.getPitch() * 360) / 256.0F : entity.pitch;
				entity.setPositionAndRotations(vec3d.x, vec3d.y, vec3d.z, f, g, 3, false);
				entity.onGround = entityS2CPacket.isOnGround();
			}
		}
	}

	@Override
	public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket entitySetHeadYawS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitySetHeadYawS2CPacket, this, this.client);
		Entity entity = entitySetHeadYawS2CPacket.getEntity(this.world);
		if (entity != null) {
			float f = (float)(entitySetHeadYawS2CPacket.getHeadYaw() * 360) / 256.0F;
			entity.method_5683(f, 3);
		}
	}

	@Override
	public void onEntitiesDestroy(EntitiesDestroyS2CPacket entitiesDestroyS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitiesDestroyS2CPacket, this, this.client);

		for (int i = 0; i < entitiesDestroyS2CPacket.getEntityIds().length; i++) {
			int j = entitiesDestroyS2CPacket.getEntityIds()[i];
			this.world.removeEntity(j);
		}
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket playerPositionLookS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerPositionLookS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		double d = playerPositionLookS2CPacket.getX();
		double e = playerPositionLookS2CPacket.getY();
		double f = playerPositionLookS2CPacket.getZ();
		float g = playerPositionLookS2CPacket.getYaw();
		float h = playerPositionLookS2CPacket.getPitch();
		Vec3d vec3d = playerEntity.getVelocity();
		double i = vec3d.x;
		double j = vec3d.y;
		double k = vec3d.z;
		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.field_12400)) {
			playerEntity.prevRenderX += d;
			d += playerEntity.x;
		} else {
			playerEntity.prevRenderX = d;
			i = 0.0;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.field_12398)) {
			playerEntity.prevRenderY += e;
			e += playerEntity.y;
		} else {
			playerEntity.prevRenderY = e;
			j = 0.0;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.field_12403)) {
			playerEntity.prevRenderZ += f;
			f += playerEntity.z;
		} else {
			playerEntity.prevRenderZ = f;
			k = 0.0;
		}

		playerEntity.setVelocity(i, j, k);
		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.field_12397)) {
			h += playerEntity.pitch;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.field_12401)) {
			g += playerEntity.yaw;
		}

		playerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.connection.send(new TeleportConfirmC2SPacket(playerPositionLookS2CPacket.getTeleportId()));
		this.connection
			.send(new PlayerMoveC2SPacket.Both(playerEntity.x, playerEntity.getBoundingBox().minY, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false));
		if (!this.field_3698) {
			this.client.player.prevX = this.client.player.x;
			this.client.player.prevY = this.client.player.y;
			this.client.player.prevZ = this.client.player.z;
			this.field_3698 = true;
			this.client.openScreen(null);
		}
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkDeltaUpdateS2CPacket, this, this.client);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : chunkDeltaUpdateS2CPacket.getRecords()) {
			this.world.setBlockStateWithoutNeighborUpdates(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
		}
	}

	@Override
	public void onChunkData(ChunkDataS2CPacket chunkDataS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkDataS2CPacket, this, this.client);
		int i = chunkDataS2CPacket.getX();
		int j = chunkDataS2CPacket.getZ();
		WorldChunk worldChunk = this.world
			.method_2935()
			.loadChunkFromPacket(
				this.world,
				i,
				j,
				chunkDataS2CPacket.getReadBuffer(),
				chunkDataS2CPacket.getHeightmaps(),
				chunkDataS2CPacket.getVerticalStripBitmask(),
				chunkDataS2CPacket.isFullChunk()
			);
		if (worldChunk != null && chunkDataS2CPacket.isFullChunk()) {
			this.world.addEntitiesToChunk(worldChunk);
		}

		for (int k = 0; k < 16; k++) {
			this.world.scheduleBlockRenders(i, k, j);
		}

		for (CompoundTag compoundTag : chunkDataS2CPacket.getBlockEntityTagList()) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity != null) {
				blockEntity.fromTag(compoundTag);
			}
		}
	}

	@Override
	public void onUnloadChunk(UnloadChunkS2CPacket unloadChunkS2CPacket) {
		NetworkThreadUtils.forceMainThread(unloadChunkS2CPacket, this, this.client);
		int i = unloadChunkS2CPacket.getX();
		int j = unloadChunkS2CPacket.getZ();
		this.world.method_2935().unload(i, j);

		for (int k = 0; k < 16; k++) {
			this.world.scheduleBlockRenders(i, k, j);
		}
	}

	@Override
	public void onBlockUpdate(BlockUpdateS2CPacket blockUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockUpdateS2CPacket, this, this.client);
		this.world.setBlockStateWithoutNeighborUpdates(blockUpdateS2CPacket.getPos(), blockUpdateS2CPacket.getState());
	}

	@Override
	public void onDisconnect(DisconnectS2CPacket disconnectS2CPacket) {
		this.connection.disconnect(disconnectS2CPacket.getReason());
	}

	@Override
	public void onDisconnected(Component component) {
		this.client.disconnect();
		if (this.loginScreen != null) {
			if (this.loginScreen instanceof RealmsScreenProxy) {
				this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.loginScreen).getScreen(), "disconnect.lost", component).getProxy());
			} else {
				this.client.openScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", component));
			}
		} else {
			this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", component));
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.connection.send(packet);
	}

	@Override
	public void onItemPickupAnimation(ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket) {
		NetworkThreadUtils.forceMainThread(itemPickupAnimationS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(itemPickupAnimationS2CPacket.getEntityId());
		LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(itemPickupAnimationS2CPacket.getCollectorEntityId());
		if (livingEntity == null) {
			livingEntity = this.client.player;
		}

		if (entity != null) {
			if (entity instanceof ExperienceOrbEntity) {
				this.world
					.playSound(
						entity.x,
						entity.y,
						entity.z,
						SoundEvents.field_14627,
						SoundCategory.PLAYERS,
						0.1F,
						(this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F,
						false
					);
			} else {
				this.world
					.playSound(
						entity.x,
						entity.y,
						entity.z,
						SoundEvents.field_15197,
						SoundCategory.PLAYERS,
						0.2F,
						(this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F,
						false
					);
			}

			if (entity instanceof ItemEntity) {
				((ItemEntity)entity).getStack().setAmount(itemPickupAnimationS2CPacket.getStackAmount());
			}

			this.client.particleManager.addParticle(new ItemPickupParticle(this.world, entity, livingEntity, 0.5F));
			this.world.removeEntity(itemPickupAnimationS2CPacket.getEntityId());
		}
	}

	@Override
	public void onChatMessage(ChatMessageS2CPacket chatMessageS2CPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageS2CPacket, this, this.client);
		this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage());
	}

	@Override
	public void onEntityAnimation(EntityAnimationS2CPacket entityAnimationS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAnimationS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAnimationS2CPacket.getId());
		if (entity != null) {
			if (entityAnimationS2CPacket.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.field_5808);
			} else if (entityAnimationS2CPacket.getAnimationId() == 3) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.field_5810);
			} else if (entityAnimationS2CPacket.getAnimationId() == 1) {
				entity.animateDamage();
			} else if (entityAnimationS2CPacket.getAnimationId() == 2) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				playerEntity.wakeUp(false, false, false);
			} else if (entityAnimationS2CPacket.getAnimationId() == 4) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11205);
			} else if (entityAnimationS2CPacket.getAnimationId() == 5) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11208);
			}
		}
	}

	@Override
	public void onMobSpawn(MobSpawnS2CPacket mobSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(mobSpawnS2CPacket, this, this.client);
		double d = mobSpawnS2CPacket.getX();
		double e = mobSpawnS2CPacket.getY();
		double f = mobSpawnS2CPacket.getZ();
		float g = (float)(mobSpawnS2CPacket.getVelocityX() * 360) / 256.0F;
		float h = (float)(mobSpawnS2CPacket.getVelocityY() * 360) / 256.0F;
		LivingEntity livingEntity = (LivingEntity)EntityType.createInstanceFromId(mobSpawnS2CPacket.getEntityTypeId(), this.client.world);
		if (livingEntity != null) {
			livingEntity.method_18003(d, e, f);
			livingEntity.field_6283 = (float)(mobSpawnS2CPacket.getVelocityZ() * 360) / 256.0F;
			livingEntity.headYaw = (float)(mobSpawnS2CPacket.getVelocityZ() * 360) / 256.0F;
			if (livingEntity instanceof EnderDragonEntity) {
				EnderDragonPart[] enderDragonParts = ((EnderDragonEntity)livingEntity).method_5690();

				for (int i = 0; i < enderDragonParts.length; i++) {
					enderDragonParts[i].setEntityId(i + mobSpawnS2CPacket.getId());
				}
			}

			livingEntity.setEntityId(mobSpawnS2CPacket.getId());
			livingEntity.setUuid(mobSpawnS2CPacket.getUuid());
			livingEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
			livingEntity.setVelocity(
				(double)((float)mobSpawnS2CPacket.getYaw() / 8000.0F),
				(double)((float)mobSpawnS2CPacket.getPitch() / 8000.0F),
				(double)((float)mobSpawnS2CPacket.getHeadPitch() / 8000.0F)
			);
			this.world.addEntity(mobSpawnS2CPacket.getId(), livingEntity);
			List<DataTracker.Entry<?>> list = mobSpawnS2CPacket.getTrackedValues();
			if (list != null) {
				livingEntity.getDataTracker().writeUpdatedEntries(list);
			}
		} else {
			LOGGER.warn("Skipping Entity with id {}", mobSpawnS2CPacket.getEntityTypeId());
		}
	}

	@Override
	public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldTimeUpdateS2CPacket, this, this.client);
		this.client.world.setTime(worldTimeUpdateS2CPacket.getTime());
		this.client.world.setTimeOfDay(worldTimeUpdateS2CPacket.getTimeOfDay());
	}

	@Override
	public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket playerSpawnPositionS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnPositionS2CPacket, this, this.client);
		this.client.player.setPlayerSpawn(playerSpawnPositionS2CPacket.getPos(), true);
		this.client.world.getLevelProperties().setSpawnPos(playerSpawnPositionS2CPacket.getPos());
	}

	@Override
	public void onEntityPassengersSet(EntityPassengersSetS2CPacket entityPassengersSetS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPassengersSetS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPassengersSetS2CPacket.getId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.hasPassengerDeep(this.client.player);
			entity.removeAllPassengers();

			for (int i : entityPassengersSetS2CPacket.getPassengerIds()) {
				Entity entity2 = this.world.getEntityById(i);
				if (entity2 != null) {
					entity2.startRiding(entity, true);
					if (entity2 == this.client.player && !bl) {
						this.client.inGameHud.setOverlayMessage(I18n.translate("mount.onboard", this.client.options.keySneak.getLocalizedName()), false);
					}
				}
			}
		}
	}

	@Override
	public void onEntityAttach(EntityAttachS2CPacket entityAttachS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAttachS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttachS2CPacket.getAttachedEntityId());
		if (entity instanceof MobEntity) {
			((MobEntity)entity).setHoldingEntityId(entityAttachS2CPacket.getHoldingEntityId());
		}
	}

	private static ItemStack method_19691(PlayerEntity playerEntity) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.getItem() == Items.field_8288) {
				return itemStack;
			}
		}

		return new ItemStack(Items.field_8288);
	}

	@Override
	public void onEntityStatus(EntityStatusS2CPacket entityStatusS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityStatusS2CPacket, this, this.client);
		Entity entity = entityStatusS2CPacket.getEntity(this.world);
		if (entity != null) {
			if (entityStatusS2CPacket.getStatus() == 21) {
				this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else if (entityStatusS2CPacket.getStatus() == 35) {
				int i = 40;
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11220, 30);
				this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.field_14931, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.gameRenderer.showFloatingItem(method_19691(this.client.player));
				}
			} else {
				entity.handleStatus(entityStatusS2CPacket.getStatus());
			}
		}
	}

	@Override
	public void onHealthUpdate(HealthUpdateS2CPacket healthUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(healthUpdateS2CPacket, this, this.client);
		this.client.player.updateHealth(healthUpdateS2CPacket.getHealth());
		this.client.player.getHungerManager().setFoodLevel(healthUpdateS2CPacket.getFood());
		this.client.player.getHungerManager().setSaturationLevelClient(healthUpdateS2CPacket.getSaturation());
	}

	@Override
	public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket experienceBarUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(experienceBarUpdateS2CPacket, this, this.client);
		this.client
			.player
			.method_3145(experienceBarUpdateS2CPacket.getBarProgress(), experienceBarUpdateS2CPacket.getExperienceLevel(), experienceBarUpdateS2CPacket.getExperience());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnS2CPacket playerRespawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerRespawnS2CPacket, this, this.client);
		DimensionType dimensionType = playerRespawnS2CPacket.getDimension();
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		int i = clientPlayerEntity.getEntityId();
		if (dimensionType != clientPlayerEntity.dimension) {
			this.field_3698 = false;
			Scoreboard scoreboard = this.world.getScoreboard();
			this.world = new ClientWorld(
				this,
				new LevelInfo(
					0L, playerRespawnS2CPacket.getGameMode(), false, this.client.world.getLevelProperties().isHardcore(), playerRespawnS2CPacket.getGeneratorType()
				),
				playerRespawnS2CPacket.getDimension(),
				this.chunkLoadDistance,
				this.client.getProfiler(),
				this.client.worldRenderer
			);
			this.world.setScoreboard(scoreboard);
			this.client.joinWorld(this.world);
			this.client.openScreen(new DownloadingTerrainScreen());
		}

		this.world.setDefaultSpawnClient();
		this.world.finishRemovingEntities();
		String string = clientPlayerEntity.getServerBrand();
		this.client.cameraEntity = null;
		ClientPlayerEntity clientPlayerEntity2 = this.client
			.interactionManager
			.createPlayer(this.world, clientPlayerEntity.getStats(), clientPlayerEntity.getRecipeBook());
		clientPlayerEntity2.setEntityId(i);
		clientPlayerEntity2.dimension = dimensionType;
		this.client.player = clientPlayerEntity2;
		this.client.cameraEntity = clientPlayerEntity2;
		clientPlayerEntity2.getDataTracker().writeUpdatedEntries(clientPlayerEntity.getDataTracker().getAllEntries());
		clientPlayerEntity2.afterSpawn();
		clientPlayerEntity2.setServerBrand(string);
		this.world.addPlayer(i, clientPlayerEntity2);
		clientPlayerEntity2.yaw = -180.0F;
		clientPlayerEntity2.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(clientPlayerEntity2);
		clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.getReducedDebugInfo());
		if (this.client.currentScreen instanceof DeathScreen) {
			this.client.openScreen(null);
		}

		this.client.interactionManager.setGameMode(playerRespawnS2CPacket.getGameMode());
	}

	@Override
	public void onExplosion(ExplosionS2CPacket explosionS2CPacket) {
		NetworkThreadUtils.forceMainThread(explosionS2CPacket, this, this.client);
		Explosion explosion = new Explosion(
			this.client.world,
			null,
			explosionS2CPacket.getX(),
			explosionS2CPacket.getY(),
			explosionS2CPacket.getZ(),
			explosionS2CPacket.getRadius(),
			explosionS2CPacket.getAffectedBlocks()
		);
		explosion.affectWorld(true);
		this.client
			.player
			.setVelocity(
				this.client
					.player
					.getVelocity()
					.add((double)explosionS2CPacket.getPlayerVelocityX(), (double)explosionS2CPacket.getPlayerVelocityY(), (double)explosionS2CPacket.getPlayerVelocityZ())
			);
	}

	@Override
	public void onGuiOpen(GuiOpenS2CPacket guiOpenS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiOpenS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(guiOpenS2CPacket.getHorseId());
		if (entity instanceof HorseBaseEntity) {
			ClientPlayerEntity clientPlayerEntity = this.client.player;
			HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
			BasicInventory basicInventory = new BasicInventory(guiOpenS2CPacket.getSlotCount());
			HorseContainer horseContainer = new HorseContainer(guiOpenS2CPacket.getId(), clientPlayerEntity.inventory, basicInventory, horseBaseEntity);
			clientPlayerEntity.container = horseContainer;
			this.client.openScreen(new HorseScreen(horseContainer, clientPlayerEntity.inventory, horseBaseEntity));
		}
	}

	@Override
	public void onOpenContainer(OpenContainerPacket openContainerPacket) {
		NetworkThreadUtils.forceMainThread(openContainerPacket, this, this.client);
		ContainerScreenRegistry.openScreen(openContainerPacket.getContainerType(), this.client, openContainerPacket.getSyncId(), openContainerPacket.getName());
	}

	@Override
	public void onGuiSlotUpdate(GuiSlotUpdateS2CPacket guiSlotUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiSlotUpdateS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = guiSlotUpdateS2CPacket.getItemStack();
		int i = guiSlotUpdateS2CPacket.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		if (guiSlotUpdateS2CPacket.getId() == -1) {
			if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
				playerEntity.inventory.setCursorStack(itemStack);
			}
		} else if (guiSlotUpdateS2CPacket.getId() == -2) {
			playerEntity.inventory.setInvStack(i, itemStack);
		} else {
			boolean bl = false;
			if (this.client.currentScreen instanceof CreativeInventoryScreen) {
				CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen)this.client.currentScreen;
				bl = creativeInventoryScreen.method_2469() != ItemGroup.INVENTORY.getIndex();
			}

			if (guiSlotUpdateS2CPacket.getId() == 0 && guiSlotUpdateS2CPacket.getSlot() >= 36 && i < 45) {
				if (!itemStack.isEmpty()) {
					ItemStack itemStack2 = playerEntity.playerContainer.getSlot(i).getStack();
					if (itemStack2.isEmpty() || itemStack2.getAmount() < itemStack.getAmount()) {
						itemStack.setUpdateCooldown(5);
					}
				}

				playerEntity.playerContainer.setStackInSlot(i, itemStack);
			} else if (guiSlotUpdateS2CPacket.getId() == playerEntity.container.syncId && (guiSlotUpdateS2CPacket.getId() != 0 || !bl)) {
				playerEntity.container.setStackInSlot(i, itemStack);
			}
		}
	}

	@Override
	public void onGuiActionConfirm(ConfirmGuiActionS2CPacket confirmGuiActionS2CPacket) {
		NetworkThreadUtils.forceMainThread(confirmGuiActionS2CPacket, this, this.client);
		Container container = null;
		PlayerEntity playerEntity = this.client.player;
		if (confirmGuiActionS2CPacket.getId() == 0) {
			container = playerEntity.playerContainer;
		} else if (confirmGuiActionS2CPacket.getId() == playerEntity.container.syncId) {
			container = playerEntity.container;
		}

		if (container != null && !confirmGuiActionS2CPacket.wasAccepted()) {
			this.sendPacket(new GuiActionConfirmC2SPacket(confirmGuiActionS2CPacket.getId(), confirmGuiActionS2CPacket.getActionId(), true));
		}
	}

	@Override
	public void onInventory(InventoryS2CPacket inventoryS2CPacket) {
		NetworkThreadUtils.forceMainThread(inventoryS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (inventoryS2CPacket.getGuiId() == 0) {
			playerEntity.playerContainer.updateSlotStacks(inventoryS2CPacket.getSlotStacks());
		} else if (inventoryS2CPacket.getGuiId() == playerEntity.container.syncId) {
			playerEntity.container.updateSlotStacks(inventoryS2CPacket.getSlotStacks());
		}
	}

	@Override
	public void onSignEditorOpen(SignEditorOpenS2CPacket signEditorOpenS2CPacket) {
		NetworkThreadUtils.forceMainThread(signEditorOpenS2CPacket, this, this.client);
		BlockEntity blockEntity = this.world.getBlockEntity(signEditorOpenS2CPacket.getPos());
		if (!(blockEntity instanceof SignBlockEntity)) {
			blockEntity = new SignBlockEntity();
			blockEntity.setWorld(this.world);
			blockEntity.setPos(signEditorOpenS2CPacket.getPos());
		}

		this.client.player.openEditSignScreen((SignBlockEntity)blockEntity);
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockEntityUpdateS2CPacket, this, this.client);
		if (this.client.world.isBlockLoaded(blockEntityUpdateS2CPacket.getPos())) {
			BlockEntity blockEntity = this.client.world.getBlockEntity(blockEntityUpdateS2CPacket.getPos());
			int i = blockEntityUpdateS2CPacket.getActionId();
			boolean bl = i == 2 && blockEntity instanceof CommandBlockBlockEntity;
			if (i == 1 && blockEntity instanceof MobSpawnerBlockEntity
				|| bl
				|| i == 3 && blockEntity instanceof BeaconBlockEntity
				|| i == 4 && blockEntity instanceof SkullBlockEntity
				|| i == 6 && blockEntity instanceof BannerBlockEntity
				|| i == 7 && blockEntity instanceof StructureBlockBlockEntity
				|| i == 8 && blockEntity instanceof EndGatewayBlockEntity
				|| i == 9 && blockEntity instanceof SignBlockEntity
				|| i == 11 && blockEntity instanceof BedBlockEntity
				|| i == 5 && blockEntity instanceof ConduitBlockEntity
				|| i == 12 && blockEntity instanceof JigsawBlockEntity
				|| i == 13 && blockEntity instanceof CampfireBlockEntity) {
				blockEntity.fromTag(blockEntityUpdateS2CPacket.getCompoundTag());
			}

			if (bl && this.client.currentScreen instanceof CommandBlockScreen) {
				((CommandBlockScreen)this.client.currentScreen).method_2457();
			}
		}
	}

	@Override
	public void onGuiUpdate(GuiUpdateS2CPacket guiUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiUpdateS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.container != null && playerEntity.container.syncId == guiUpdateS2CPacket.getId()) {
			playerEntity.container.setProperties(guiUpdateS2CPacket.getPropertyId(), guiUpdateS2CPacket.getValue());
		}
	}

	@Override
	public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket entityEquipmentUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityEquipmentUpdateS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityEquipmentUpdateS2CPacket.getId());
		if (entity != null) {
			entity.setEquippedStack(entityEquipmentUpdateS2CPacket.getSlot(), entityEquipmentUpdateS2CPacket.getStack());
		}
	}

	@Override
	public void onGuiClose(GuiCloseS2CPacket guiCloseS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseS2CPacket, this, this.client);
		this.client.player.closeScreen();
	}

	@Override
	public void onBlockAction(BlockActionS2CPacket blockActionS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockActionS2CPacket, this, this.client);
		this.client
			.world
			.addBlockAction(blockActionS2CPacket.getPos(), blockActionS2CPacket.getBlock(), blockActionS2CPacket.getType(), blockActionS2CPacket.getData());
	}

	@Override
	public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket blockBreakingProgressS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockBreakingProgressS2CPacket, this, this.client);
		this.client
			.world
			.setBlockBreakingProgress(
				blockBreakingProgressS2CPacket.getEntityId(), blockBreakingProgressS2CPacket.getPos(), blockBreakingProgressS2CPacket.getProgress()
			);
	}

	@Override
	public void onGameStateChange(GameStateChangeS2CPacket gameStateChangeS2CPacket) {
		NetworkThreadUtils.forceMainThread(gameStateChangeS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		int i = gameStateChangeS2CPacket.getReason();
		float f = gameStateChangeS2CPacket.getValue();
		int j = MathHelper.floor(f + 0.5F);
		if (i >= 0 && i < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[i] != null) {
			playerEntity.addChatMessage(new TranslatableComponent(GameStateChangeS2CPacket.REASON_MESSAGES[i]), false);
		}

		if (i == 1) {
			this.world.getLevelProperties().setRaining(true);
			this.world.setRainGradient(0.0F);
		} else if (i == 2) {
			this.world.getLevelProperties().setRaining(false);
			this.world.setRainGradient(1.0F);
		} else if (i == 3) {
			this.client.interactionManager.setGameMode(GameMode.byId(j));
		} else if (i == 4) {
			if (j == 0) {
				this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12774));
				this.client.openScreen(new DownloadingTerrainScreen());
			} else if (j == 1) {
				this.client
					.openScreen(
						new EndCreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.field_12774)))
					);
			}
		} else if (i == 5) {
			GameOptions gameOptions = this.client.options;
			if (f == 0.0F) {
				this.client.openScreen(new DemoScreen());
			} else if (f == 101.0F) {
				this.client
					.inGameHud
					.getChatHud()
					.addMessage(
						new TranslatableComponent(
							"demo.help.movement",
							gameOptions.keyForward.getLocalizedName(),
							gameOptions.keyLeft.getLocalizedName(),
							gameOptions.keyBack.getLocalizedName(),
							gameOptions.keyRight.getLocalizedName()
						)
					);
			} else if (f == 102.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableComponent("demo.help.jump", gameOptions.keyJump.getLocalizedName()));
			} else if (f == 103.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableComponent("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()));
			} else if (f == 104.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableComponent("demo.day.6", gameOptions.keyScreenshot.getLocalizedName()));
			}
		} else if (i == 6) {
			this.world
				.playSound(
					playerEntity,
					playerEntity.x,
					playerEntity.y + (double)playerEntity.getStandingEyeHeight(),
					playerEntity.z,
					SoundEvents.field_15224,
					SoundCategory.PLAYERS,
					0.18F,
					0.45F
				);
		} else if (i == 7) {
			this.world.setRainGradient(f);
		} else if (i == 8) {
			this.world.setThunderGradient(f);
		} else if (i == 9) {
			this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14848, SoundCategory.field_15254, 1.0F, 1.0F);
		} else if (i == 10) {
			this.world.addParticle(ParticleTypes.field_11250, playerEntity.x, playerEntity.y, playerEntity.z, 0.0, 0.0, 0.0);
			this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15203, SoundCategory.field_15251, 1.0F, 1.0F);
		}
	}

	@Override
	public void onMapUpdate(MapUpdateS2CPacket mapUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(mapUpdateS2CPacket, this, this.client);
		MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
		String string = FilledMapItem.getMapStorageName(mapUpdateS2CPacket.getId());
		MapState mapState = this.client.world.getMapState(string);
		if (mapState == null) {
			mapState = new MapState(string);
			if (mapRenderer.getTexture(string) != null) {
				MapState mapState2 = mapRenderer.getState(mapRenderer.getTexture(string));
				if (mapState2 != null) {
					mapState = mapState2;
				}
			}

			this.client.world.putMapState(mapState);
		}

		mapUpdateS2CPacket.apply(mapState);
		mapRenderer.updateTexture(mapState);
	}

	@Override
	public void onWorldEvent(WorldEventS2CPacket worldEventS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldEventS2CPacket, this, this.client);
		if (worldEventS2CPacket.isGlobal()) {
			this.client.world.playGlobalEvent(worldEventS2CPacket.getEventId(), worldEventS2CPacket.getPos(), worldEventS2CPacket.getEffectData());
		} else {
			this.client.world.playLevelEvent(worldEventS2CPacket.getEventId(), worldEventS2CPacket.getPos(), worldEventS2CPacket.getEffectData());
		}
	}

	@Override
	public void onAdvancements(AdvancementUpdateS2CPacket advancementUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(advancementUpdateS2CPacket, this, this.client);
		this.advancementHandler.onAdvancements(advancementUpdateS2CPacket);
	}

	@Override
	public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket selectAdvancementTabS2CPacket) {
		NetworkThreadUtils.forceMainThread(selectAdvancementTabS2CPacket, this, this.client);
		Identifier identifier = selectAdvancementTabS2CPacket.getTabId();
		if (identifier == null) {
			this.advancementHandler.selectTab(null, false);
		} else {
			Advancement advancement = this.advancementHandler.getManager().get(identifier);
			this.advancementHandler.selectTab(advancement, false);
		}
	}

	@Override
	public void onCommandTree(CommandTreeS2CPacket commandTreeS2CPacket) {
		NetworkThreadUtils.forceMainThread(commandTreeS2CPacket, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(commandTreeS2CPacket.getCommandTree());
	}

	@Override
	public void onStopSound(StopSoundS2CPacket stopSoundS2CPacket) {
		NetworkThreadUtils.forceMainThread(stopSoundS2CPacket, this, this.client);
		this.client.getSoundManager().stopSounds(stopSoundS2CPacket.getSoundId(), stopSoundS2CPacket.getCategory());
	}

	@Override
	public void onCommandSuggestions(CommandSuggestionsS2CPacket commandSuggestionsS2CPacket) {
		NetworkThreadUtils.forceMainThread(commandSuggestionsS2CPacket, this, this.client);
		this.commandSource.onCommandSuggestions(commandSuggestionsS2CPacket.getCompletionId(), commandSuggestionsS2CPacket.getSuggestions());
	}

	@Override
	public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket) {
		NetworkThreadUtils.forceMainThread(synchronizeRecipesS2CPacket, this, this.client);
		this.recipeManager.clear();

		for (Recipe<?> recipe : synchronizeRecipesS2CPacket.getRecipes()) {
			this.recipeManager.add(recipe);
		}

		SearchableContainer<RecipeResultCollection> searchableContainer = this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT);
		searchableContainer.clear();
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.reload();
		clientRecipeBook.getOrderedResults().forEach(searchableContainer::add);
		searchableContainer.reload();
	}

	@Override
	public void onLookAt(LookAtS2CPacket lookAtS2CPacket) {
		NetworkThreadUtils.forceMainThread(lookAtS2CPacket, this, this.client);
		Vec3d vec3d = lookAtS2CPacket.getTargetPosition(this.world);
		if (vec3d != null) {
			this.client.player.lookAt(lookAtS2CPacket.getSelfAnchor(), vec3d);
		}
	}

	@Override
	public void onTagQuery(TagQueryResponseS2CPacket tagQueryResponseS2CPacket) {
		NetworkThreadUtils.forceMainThread(tagQueryResponseS2CPacket, this, this.client);
		if (!this.dataQueryHandler.handleQueryResponse(tagQueryResponseS2CPacket.getTransactionId(), tagQueryResponseS2CPacket.getTag())) {
			LOGGER.debug("Got unhandled response to tag query {}", tagQueryResponseS2CPacket.getTransactionId());
		}
	}

	@Override
	public void onStatistics(StatisticsS2CPacket statisticsS2CPacket) {
		NetworkThreadUtils.forceMainThread(statisticsS2CPacket, this, this.client);

		for (Entry<Stat<?>, Integer> entry : statisticsS2CPacket.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStats().setStat(this.client.player, stat, i);
		}

		if (this.client.currentScreen instanceof StatsListener) {
			((StatsListener)this.client.currentScreen).onStatsReady();
		}
	}

	@Override
	public void onUnlockRecipes(UnlockRecipesS2CPacket unlockRecipesS2CPacket) {
		NetworkThreadUtils.forceMainThread(unlockRecipesS2CPacket, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setGuiOpen(unlockRecipesS2CPacket.isGuiOpen());
		clientRecipeBook.setFilteringCraftable(unlockRecipesS2CPacket.isFilteringCraftable());
		clientRecipeBook.setFurnaceGuiOpen(unlockRecipesS2CPacket.isFurnaceGuiOpen());
		clientRecipeBook.setFurnaceFilteringCraftable(unlockRecipesS2CPacket.isFurnaceFilteringCraftable());
		UnlockRecipesS2CPacket.Action action = unlockRecipesS2CPacket.getAction();
		switch (action) {
			case field_12417:
				for (Identifier identifier : unlockRecipesS2CPacket.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::remove);
				}
				break;
			case field_12416:
				for (Identifier identifier : unlockRecipesS2CPacket.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::add);
				}

				for (Identifier identifier : unlockRecipesS2CPacket.getRecipeIdsToInit()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::display);
				}
				break;
			case field_12415:
				for (Identifier identifier : unlockRecipesS2CPacket.getRecipeIdsToChange()) {
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
	public void onEntityPotionEffect(EntityPotionEffectS2CPacket entityPotionEffectS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPotionEffectS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPotionEffectS2CPacket.getEntityId());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = StatusEffect.byRawId(entityPotionEffectS2CPacket.getEffectId());
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect,
					entityPotionEffectS2CPacket.getDuration(),
					entityPotionEffectS2CPacket.getAmplifier(),
					entityPotionEffectS2CPacket.isAmbient(),
					entityPotionEffectS2CPacket.shouldShowParticles(),
					entityPotionEffectS2CPacket.shouldShowIcon()
				);
				statusEffectInstance.setPermanent(entityPotionEffectS2CPacket.isPermanent());
				((LivingEntity)entity).addPotionEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsS2CPacket synchronizeTagsS2CPacket) {
		NetworkThreadUtils.forceMainThread(synchronizeTagsS2CPacket, this, this.client);
		this.tagManager = synchronizeTagsS2CPacket.getTagManager();
		if (!this.connection.isLocal()) {
			BlockTags.setContainer(this.tagManager.blocks());
			ItemTags.setContainer(this.tagManager.items());
			FluidTags.setContainer(this.tagManager.fluids());
			EntityTypeTags.setContainer(this.tagManager.entityTypes());
		}

		this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
	}

	@Override
	public void onCombatEvent(CombatEventS2CPacket combatEventS2CPacket) {
		NetworkThreadUtils.forceMainThread(combatEventS2CPacket, this, this.client);
		if (combatEventS2CPacket.type == CombatEventS2CPacket.Type.field_12350) {
			Entity entity = this.world.getEntityById(combatEventS2CPacket.entityId);
			if (entity == this.client.player) {
				this.client.openScreen(new DeathScreen(combatEventS2CPacket.deathMessage, this.world.getLevelProperties().isHardcore()));
			}
		}
	}

	@Override
	public void onDifficulty(DifficultyS2CPacket difficultyS2CPacket) {
		NetworkThreadUtils.forceMainThread(difficultyS2CPacket, this, this.client);
		this.client.world.getLevelProperties().setDifficulty(difficultyS2CPacket.getDifficulty());
		this.client.world.getLevelProperties().setDifficultyLocked(difficultyS2CPacket.isDifficultyLocked());
	}

	@Override
	public void onSetCameraEntity(SetCameraEntityS2CPacket setCameraEntityS2CPacket) {
		NetworkThreadUtils.forceMainThread(setCameraEntityS2CPacket, this, this.client);
		Entity entity = setCameraEntityS2CPacket.getEntity(this.world);
		if (entity != null) {
			this.client.setCameraEntity(entity);
		}
	}

	@Override
	public void onWorldBorder(WorldBorderS2CPacket worldBorderS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldBorderS2CPacket, this, this.client);
		worldBorderS2CPacket.apply(this.world.getWorldBorder());
	}

	@Override
	public void onTitle(TitleS2CPacket titleS2CPacket) {
		NetworkThreadUtils.forceMainThread(titleS2CPacket, this, this.client);
		TitleS2CPacket.Action action = titleS2CPacket.getAction();
		String string = null;
		String string2 = null;
		String string3 = titleS2CPacket.getText() != null ? titleS2CPacket.getText().getFormattedText() : "";
		switch (action) {
			case field_12630:
				string = string3;
				break;
			case field_12632:
				string2 = string3;
				break;
			case field_12627:
				this.client.inGameHud.setOverlayMessage(string3, false);
				return;
			case field_12628:
				this.client.inGameHud.setTitles("", "", -1, -1, -1);
				this.client.inGameHud.setDefaultTitleFade();
				return;
		}

		this.client.inGameHud.setTitles(string, string2, titleS2CPacket.getFadeInTicks(), titleS2CPacket.getStayTicks(), titleS2CPacket.getFadeOutTicks());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderS2CPacket playerListHeaderS2CPacket) {
		this.client
			.inGameHud
			.getPlayerListWidget()
			.setHeader(playerListHeaderS2CPacket.getHeader().getFormattedText().isEmpty() ? null : playerListHeaderS2CPacket.getHeader());
		this.client
			.inGameHud
			.getPlayerListWidget()
			.setFooter(playerListHeaderS2CPacket.getFooter().getFormattedText().isEmpty() ? null : playerListHeaderS2CPacket.getFooter());
	}

	@Override
	public void onRemoveEntityEffect(RemoveEntityEffectS2CPacket removeEntityEffectS2CPacket) {
		NetworkThreadUtils.forceMainThread(removeEntityEffectS2CPacket, this, this.client);
		Entity entity = removeEntityEffectS2CPacket.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).removePotionEffect(removeEntityEffectS2CPacket.getEffectType());
		}
	}

	@Override
	public void onPlayerList(PlayerListS2CPacket playerListS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerListS2CPacket, this, this.client);

		for (PlayerListS2CPacket.Entry entry : playerListS2CPacket.getEntries()) {
			if (playerListS2CPacket.getAction() == PlayerListS2CPacket.Action.field_12376) {
				this.playerListEntries.remove(entry.getProfile().getId());
			} else {
				PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.getProfile().getId());
				if (playerListS2CPacket.getAction() == PlayerListS2CPacket.Action.field_12372) {
					playerListEntry = new PlayerListEntry(entry);
					this.playerListEntries.put(playerListEntry.getProfile().getId(), playerListEntry);
				}

				if (playerListEntry != null) {
					switch (playerListS2CPacket.getAction()) {
						case field_12372:
							playerListEntry.setGameMode(entry.getGameMode());
							playerListEntry.setLatency(entry.getLatency());
							playerListEntry.setDisplayName(entry.getDisplayName());
							break;
						case field_12375:
							playerListEntry.setGameMode(entry.getGameMode());
							break;
						case field_12371:
							playerListEntry.setLatency(entry.getLatency());
							break;
						case field_12374:
							playerListEntry.setDisplayName(entry.getDisplayName());
					}
				}
			}
		}
	}

	@Override
	public void onKeepAlive(KeepAliveS2CPacket keepAliveS2CPacket) {
		this.sendPacket(new KeepAliveC2SPacket(keepAliveS2CPacket.getId()));
	}

	@Override
	public void onPlayerAbilities(PlayerAbilitiesS2CPacket playerAbilitiesS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerAbilitiesS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.abilities.flying = playerAbilitiesS2CPacket.isFlying();
		playerEntity.abilities.creativeMode = playerAbilitiesS2CPacket.isCreativeMode();
		playerEntity.abilities.invulnerable = playerAbilitiesS2CPacket.isInvulnerable();
		playerEntity.abilities.allowFlying = playerAbilitiesS2CPacket.allowFlying();
		playerEntity.abilities.setFlySpeed(playerAbilitiesS2CPacket.getFlySpeed());
		playerEntity.abilities.setWalkSpeed(playerAbilitiesS2CPacket.getFovModifier());
	}

	@Override
	public void onPlaySound(PlaySoundS2CPacket playSoundS2CPacket) {
		NetworkThreadUtils.forceMainThread(playSoundS2CPacket, this, this.client);
		this.client
			.world
			.playSound(
				this.client.player,
				playSoundS2CPacket.getX(),
				playSoundS2CPacket.getY(),
				playSoundS2CPacket.getZ(),
				playSoundS2CPacket.getSound(),
				playSoundS2CPacket.getCategory(),
				playSoundS2CPacket.getVolume(),
				playSoundS2CPacket.getPitch()
			);
	}

	@Override
	public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket) {
		NetworkThreadUtils.forceMainThread(playSoundFromEntityS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(playSoundFromEntityS2CPacket.getEntityId());
		if (entity != null) {
			this.client
				.world
				.playSoundFromEntity(
					this.client.player,
					entity,
					playSoundFromEntityS2CPacket.getSound(),
					playSoundFromEntityS2CPacket.getCategory(),
					playSoundFromEntityS2CPacket.getVolume(),
					playSoundFromEntityS2CPacket.getPitch()
				);
		}
	}

	@Override
	public void onPlaySoundId(PlaySoundIdS2CPacket playSoundIdS2CPacket) {
		NetworkThreadUtils.forceMainThread(playSoundIdS2CPacket, this, this.client);
		this.client
			.getSoundManager()
			.play(
				new PositionedSoundInstance(
					playSoundIdS2CPacket.getSoundId(),
					playSoundIdS2CPacket.getCategory(),
					playSoundIdS2CPacket.getVolume(),
					playSoundIdS2CPacket.getPitch(),
					false,
					0,
					SoundInstance.AttenuationType.field_5476,
					(float)playSoundIdS2CPacket.getX(),
					(float)playSoundIdS2CPacket.getY(),
					(float)playSoundIdS2CPacket.getZ(),
					false
				)
			);
	}

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket resourcePackSendS2CPacket) {
		String string = resourcePackSendS2CPacket.getURL();
		String string2 = resourcePackSendS2CPacket.getSHA1();
		if (this.validateResourcePackUrl(string)) {
			if (string.startsWith("level://")) {
				try {
					String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
					File file = new File(this.client.runDirectory, "saves");
					File file2 = new File(file, string3);
					if (file2.isFile()) {
						this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13016);
						CompletableFuture<?> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13015);
			} else {
				ServerEntry serverEntry = this.client.getCurrentServerEntry();
				if (serverEntry != null && serverEntry.getResourcePack() == ServerEntry.ResourcePackState.field_3768) {
					this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13016);
					this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
				} else if (serverEntry != null && serverEntry.getResourcePack() != ServerEntry.ResourcePackState.field_3767) {
					this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13018);
				} else {
					this.client.execute(() -> this.client.openScreen(new ConfirmScreen(bl -> {
							this.client = MinecraftClient.getInstance();
							ServerEntry serverEntryx = this.client.getCurrentServerEntry();
							if (bl) {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.field_3768);
								}

								this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13016);
								this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
							} else {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.field_3764);
								}

								this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13018);
							}

							ServerList.updateServerListEntry(serverEntryx);
							this.client.openScreen(null);
						}, new TranslatableComponent("multiplayer.texturePrompt.line1"), new TranslatableComponent("multiplayer.texturePrompt.line2"))));
				}
			}
		}
	}

	private boolean validateResourcePackUrl(String string) {
		try {
			URI uRI = new URI(string);
			String string2 = uRI.getScheme();
			boolean bl = "level".equals(string2);
			if (!"http".equals(string2) && !"https".equals(string2) && !bl) {
				throw new URISyntaxException(string, "Wrong protocol");
			} else if (!bl || !string.contains("..") && string.endsWith("/resources.zip")) {
				return true;
			} else {
				throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
			}
		} catch (URISyntaxException var5) {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13015);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13017)).exceptionally(throwable -> {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.field_13015);
			return null;
		});
	}

	private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status status) {
		this.connection.send(new ResourcePackStatusC2SPacket(status));
	}

	@Override
	public void onBossBar(BossBarS2CPacket bossBarS2CPacket) {
		NetworkThreadUtils.forceMainThread(bossBarS2CPacket, this, this.client);
		this.client.inGameHud.getBossBarHud().handlePacket(bossBarS2CPacket);
	}

	@Override
	public void onCooldownUpdate(CooldownUpdateS2CPacket cooldownUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(cooldownUpdateS2CPacket, this, this.client);
		if (cooldownUpdateS2CPacket.getCooldown() == 0) {
			this.client.player.getItemCooldownManager().remove(cooldownUpdateS2CPacket.getItem());
		} else {
			this.client.player.getItemCooldownManager().set(cooldownUpdateS2CPacket.getItem(), cooldownUpdateS2CPacket.getCooldown());
		}
	}

	@Override
	public void onVehicleMove(VehicleMoveS2CPacket vehicleMoveS2CPacket) {
		NetworkThreadUtils.forceMainThread(vehicleMoveS2CPacket, this, this.client);
		Entity entity = this.client.player.getTopmostVehicle();
		if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
			entity.setPositionAnglesAndUpdate(
				vehicleMoveS2CPacket.getX(), vehicleMoveS2CPacket.getY(), vehicleMoveS2CPacket.getZ(), vehicleMoveS2CPacket.getYaw(), vehicleMoveS2CPacket.getPitch()
			);
			this.connection.send(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void onOpenWrittenBook(OpenWrittenBookS2CPacket openWrittenBookS2CPacket) {
		NetworkThreadUtils.forceMainThread(openWrittenBookS2CPacket, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(openWrittenBookS2CPacket.getHand());
		if (itemStack.getItem() == Items.field_8360) {
			this.client.openScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
		}
	}

	@Override
	public void onCustomPayload(CustomPayloadS2CPacket customPayloadS2CPacket) {
		NetworkThreadUtils.forceMainThread(customPayloadS2CPacket, this, this.client);
		Identifier identifier = customPayloadS2CPacket.getChannel();
		PacketByteBuf packetByteBuf = null;

		try {
			packetByteBuf = customPayloadS2CPacket.getData();
			if (CustomPayloadS2CPacket.BRAND.equals(identifier)) {
				this.client.player.setServerBrand(packetByteBuf.readString(32767));
			} else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(identifier)) {
				int i = packetByteBuf.readInt();
				float f = packetByteBuf.readFloat();
				Path path = Path.fromBuffer(packetByteBuf);
				this.client.debugRenderer.pathfindingDebugRenderer.addPath(i, path, f);
			} else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
				long l = packetByteBuf.readVarLong();
				BlockPos blockPos = packetByteBuf.readBlockPos();
				((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).method_3870(l, blockPos);
			} else if (CustomPayloadS2CPacket.DEBUG_CAVES.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				List<BlockPos> list = Lists.<BlockPos>newArrayList();
				List<Float> list2 = Lists.<Float>newArrayList();

				for (int k = 0; k < j; k++) {
					list.add(packetByteBuf.readBlockPos());
					list2.add(packetByteBuf.readFloat());
				}

				this.client.debugRenderer.caveDebugRenderer.method_3704(blockPos2, list, list2);
			} else if (CustomPayloadS2CPacket.DEBUG_STRUCTURES.equals(identifier)) {
				int i = packetByteBuf.readInt();
				MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(
					packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
				);
				int m = packetByteBuf.readInt();
				List<MutableIntBoundingBox> list2 = Lists.<MutableIntBoundingBox>newArrayList();
				List<Boolean> list3 = Lists.<Boolean>newArrayList();

				for (int n = 0; n < m; n++) {
					list2.add(
						new MutableIntBoundingBox(
							packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
						)
					);
					list3.add(packetByteBuf.readBoolean());
				}

				this.client.debugRenderer.structureDebugRenderer.method_3871(mutableIntBoundingBox, list2, list3, i);
			} else if (CustomPayloadS2CPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier)) {
				((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer)
					.method_3872(
						packetByteBuf.readBlockPos(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat(),
						packetByteBuf.readFloat()
					);
			} else if (CustomPayloadS2CPacket.DEBUG_VILLAGE_SECTIONS.equals(identifier)) {
				int i = packetByteBuf.readInt();

				for (int j = 0; j < i; j++) {
					this.client.debugRenderer.pointsOfInterestDebugRenderer.method_19433(packetByteBuf.readChunkSectionPos());
				}

				int j = packetByteBuf.readInt();

				for (int m = 0; m < j; m++) {
					this.client.debugRenderer.pointsOfInterestDebugRenderer.method_19435(packetByteBuf.readChunkSectionPos());
				}
			} else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				String string = packetByteBuf.readString();
				int m = packetByteBuf.readInt();
				PointOfInterestDebugRenderer.class_4233 lv = new PointOfInterestDebugRenderer.class_4233(blockPos2, string, m);
				this.client.debugRenderer.pointsOfInterestDebugRenderer.method_19701(lv);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				this.client.debugRenderer.pointsOfInterestDebugRenderer.removePointOfInterest(blockPos2);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				this.client.debugRenderer.pointsOfInterestDebugRenderer.method_19702(blockPos2, j);
			} else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(identifier)) {
				int i = packetByteBuf.readInt();
				BlockPos blockPos3 = packetByteBuf.readBlockPos();
				int m = packetByteBuf.readInt();
				int o = packetByteBuf.readInt();
				List<GoalSelectorDebugRenderer.class_4206> list3 = Lists.<GoalSelectorDebugRenderer.class_4206>newArrayList();

				for (int n = 0; n < o; n++) {
					int p = packetByteBuf.readInt();
					boolean bl = packetByteBuf.readBoolean();
					String string2 = packetByteBuf.readString(255);
					list3.add(new GoalSelectorDebugRenderer.class_4206(blockPos3, p, string2, bl));
				}

				this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(m, list3);
			} else if (CustomPayloadS2CPacket.DEBUG_RAIDS.equals(identifier)) {
				int i = packetByteBuf.readInt();
				Collection<BlockPos> collection = Lists.<BlockPos>newArrayList();

				for (int m = 0; m < i; m++) {
					collection.add(packetByteBuf.readBlockPos());
				}

				this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(collection);
			} else if (CustomPayloadS2CPacket.DEBUG_BRAIN.equals(identifier)) {
				double d = packetByteBuf.readDouble();
				double e = packetByteBuf.readDouble();
				double g = packetByteBuf.readDouble();
				Position position = new PositionImpl(d, e, g);
				UUID uUID = packetByteBuf.readUuid();
				int q = packetByteBuf.readInt();
				String string3 = packetByteBuf.readString();
				String string4 = packetByteBuf.readString();
				int r = packetByteBuf.readInt();
				boolean bl2 = packetByteBuf.readBoolean();
				Path path2;
				if (bl2) {
					path2 = Path.fromBuffer(packetByteBuf);
				} else {
					path2 = null;
				}

				PointOfInterestDebugRenderer.class_4232 lv2 = new PointOfInterestDebugRenderer.class_4232(uUID, q, string3, string4, r, position, path2);
				int s = packetByteBuf.readInt();

				for (int t = 0; t < s; t++) {
					String string5 = packetByteBuf.readString();
					lv2.field_18927.add(string5);
				}

				int t = packetByteBuf.readInt();

				for (int u = 0; u < t; u++) {
					String string6 = packetByteBuf.readString();
					lv2.field_18928.add(string6);
				}

				int u = packetByteBuf.readInt();

				for (int v = 0; v < u; v++) {
					String string7 = packetByteBuf.readString();
					lv2.field_18929.add(string7);
				}

				int v = packetByteBuf.readInt();

				for (int w = 0; w < v; w++) {
					BlockPos blockPos4 = packetByteBuf.readBlockPos();
					lv2.field_18930.add(blockPos4);
				}

				this.client.debugRenderer.pointsOfInterestDebugRenderer.addPointOfInterest(lv2);
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
	public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardObjectiveUpdateS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardObjectiveUpdateS2CPacket.getName();
		if (scoreboardObjectiveUpdateS2CPacket.getMode() == 0) {
			scoreboard.addObjective(
				string, ScoreboardCriterion.field_1468, scoreboardObjectiveUpdateS2CPacket.getDisplayName(), scoreboardObjectiveUpdateS2CPacket.getType()
			);
		} else if (scoreboard.containsObjective(string)) {
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
			if (scoreboardObjectiveUpdateS2CPacket.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (scoreboardObjectiveUpdateS2CPacket.getMode() == 2) {
				scoreboardObjective.setRenderType(scoreboardObjectiveUpdateS2CPacket.getType());
				scoreboardObjective.setDisplayName(scoreboardObjectiveUpdateS2CPacket.getDisplayName());
			}
		}
	}

	@Override
	public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket scoreboardPlayerUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardPlayerUpdateS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardPlayerUpdateS2CPacket.getObjectiveName();
		switch (scoreboardPlayerUpdateS2CPacket.getUpdateMode()) {
			case field_13431:
				ScoreboardObjective scoreboardObjective = scoreboard.getObjective(string);
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(scoreboardPlayerUpdateS2CPacket.getPlayerName(), scoreboardObjective);
				scoreboardPlayerScore.setScore(scoreboardPlayerUpdateS2CPacket.getScore());
				break;
			case field_13430:
				scoreboard.resetPlayerScore(scoreboardPlayerUpdateS2CPacket.getPlayerName(), scoreboard.getNullableObjective(string));
		}
	}

	@Override
	public void onScoreboardDisplay(ScoreboardDisplayS2CPacket scoreboardDisplayS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardDisplayS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardDisplayS2CPacket.getName();
		ScoreboardObjective scoreboardObjective = string == null ? null : scoreboard.getObjective(string);
		scoreboard.setObjectiveSlot(scoreboardDisplayS2CPacket.getSlot(), scoreboardObjective);
	}

	@Override
	public void onTeam(TeamS2CPacket teamS2CPacket) {
		NetworkThreadUtils.forceMainThread(teamS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		Team team;
		if (teamS2CPacket.getMode() == 0) {
			team = scoreboard.addTeam(teamS2CPacket.getTeamName());
		} else {
			team = scoreboard.getTeam(teamS2CPacket.getTeamName());
		}

		if (teamS2CPacket.getMode() == 0 || teamS2CPacket.getMode() == 2) {
			team.setDisplayName(teamS2CPacket.getDisplayName());
			team.setColor(teamS2CPacket.getPlayerPrefix());
			team.setFriendlyFlagsBitwise(teamS2CPacket.getFlags());
			AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(teamS2CPacket.getNameTagVisibilityRule());
			if (visibilityRule != null) {
				team.setNameTagVisibilityRule(visibilityRule);
			}

			AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(teamS2CPacket.getCollisionRule());
			if (collisionRule != null) {
				team.setCollisionRule(collisionRule);
			}

			team.setPrefix(teamS2CPacket.getPrefix());
			team.setSuffix(teamS2CPacket.getSuffix());
		}

		if (teamS2CPacket.getMode() == 0 || teamS2CPacket.getMode() == 3) {
			for (String string : teamS2CPacket.getPlayerList()) {
				scoreboard.addPlayerToTeam(string, team);
			}
		}

		if (teamS2CPacket.getMode() == 4) {
			for (String string : teamS2CPacket.getPlayerList()) {
				scoreboard.removePlayerFromTeam(string, team);
			}
		}

		if (teamS2CPacket.getMode() == 1) {
			scoreboard.removeTeam(team);
		}
	}

	@Override
	public void onParticle(ParticleS2CPacket particleS2CPacket) {
		NetworkThreadUtils.forceMainThread(particleS2CPacket, this, this.client);
		if (particleS2CPacket.getCount() == 0) {
			double d = (double)(particleS2CPacket.getSpeed() * particleS2CPacket.getOffsetX());
			double e = (double)(particleS2CPacket.getSpeed() * particleS2CPacket.getOffsetY());
			double f = (double)(particleS2CPacket.getSpeed() * particleS2CPacket.getOffsetZ());

			try {
				this.world
					.addParticle(
						particleS2CPacket.getParameters(),
						particleS2CPacket.isLongDistance(),
						particleS2CPacket.getX(),
						particleS2CPacket.getY(),
						particleS2CPacket.getZ(),
						d,
						e,
						f
					);
			} catch (Throwable var17) {
				LOGGER.warn("Could not spawn particle effect {}", particleS2CPacket.getParameters());
			}
		} else {
			for (int i = 0; i < particleS2CPacket.getCount(); i++) {
				double g = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetX();
				double h = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetY();
				double j = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetZ();
				double k = this.random.nextGaussian() * (double)particleS2CPacket.getSpeed();
				double l = this.random.nextGaussian() * (double)particleS2CPacket.getSpeed();
				double m = this.random.nextGaussian() * (double)particleS2CPacket.getSpeed();

				try {
					this.world
						.addParticle(
							particleS2CPacket.getParameters(),
							particleS2CPacket.isLongDistance(),
							particleS2CPacket.getX() + g,
							particleS2CPacket.getY() + h,
							particleS2CPacket.getZ() + j,
							k,
							l,
							m
						);
				} catch (Throwable var16) {
					LOGGER.warn("Could not spawn particle effect {}", particleS2CPacket.getParameters());
					return;
				}
			}
		}
	}

	@Override
	public void onEntityAttributes(EntityAttributesS2CPacket entityAttributesS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAttributesS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttributesS2CPacket.getEntityId());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributeContainer();

				for (EntityAttributesS2CPacket.Entry entry : entityAttributesS2CPacket.getEntries()) {
					EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getId());
					if (entityAttributeInstance == null) {
						entityAttributeInstance = abstractEntityAttributeContainer.register(
							new ClampedEntityAttribute(null, entry.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE)
						);
					}

					entityAttributeInstance.setBaseValue(entry.getBaseValue());
					entityAttributeInstance.clearModifiers();

					for (EntityAttributeModifier entityAttributeModifier : entry.getModifiers()) {
						entityAttributeInstance.addModifier(entityAttributeModifier);
					}
				}
			}
		}
	}

	@Override
	public void onCraftResponse(CraftResponseS2CPacket craftResponseS2CPacket) {
		NetworkThreadUtils.forceMainThread(craftResponseS2CPacket, this, this.client);
		Container container = this.client.player.container;
		if (container.syncId == craftResponseS2CPacket.getSyncId() && container.isRestricted(this.client.player)) {
			this.recipeManager.get(craftResponseS2CPacket.getRecipeId()).ifPresent(recipe -> {
				if (this.client.currentScreen instanceof RecipeBookProvider) {
					RecipeBookScreen recipeBookScreen = ((RecipeBookProvider)this.client.currentScreen).getRecipeBookGui();
					recipeBookScreen.showGhostRecipe(recipe, container.slotList);
				}
			});
		}
	}

	@Override
	public void onLightUpdate(LightUpdateS2CPacket lightUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(lightUpdateS2CPacket, this, this.client);
		int i = lightUpdateS2CPacket.getChunkX();
		int j = lightUpdateS2CPacket.getChunkZ();
		LightingProvider lightingProvider = this.world.method_2935().getLightingProvider();
		int k = lightUpdateS2CPacket.getSkyLightMask();
		int l = lightUpdateS2CPacket.getFilledSkyLightMask();
		Iterator<byte[]> iterator = lightUpdateS2CPacket.getSkyLightUpdates().iterator();
		this.method_2870(i, j, lightingProvider, LightType.field_9284, k, l, iterator);
		int m = lightUpdateS2CPacket.getBlockLightMask();
		int n = lightUpdateS2CPacket.getFilledBlockLightMask();
		Iterator<byte[]> iterator2 = lightUpdateS2CPacket.getBlockLightUpdates().iterator();
		this.method_2870(i, j, lightingProvider, LightType.field_9282, m, n, iterator2);
	}

	@Override
	public void onSetTradeOffers(SetTradeOffersPacket setTradeOffersPacket) {
		NetworkThreadUtils.forceMainThread(setTradeOffersPacket, this, this.client);
		Container container = this.client.player.container;
		if (setTradeOffersPacket.getSyncId() == container.syncId && container instanceof MerchantContainer) {
			((MerchantContainer)container).setOffers(new TraderOfferList(setTradeOffersPacket.getOffers().toTag()));
			((MerchantContainer)container).setExperienceFromServer(setTradeOffersPacket.getExperience());
			((MerchantContainer)container).setLevelProgress(setTradeOffersPacket.getLevelProgress());
			((MerchantContainer)container).setCanLevel(setTradeOffersPacket.isLeveled());
		}
	}

	@Override
	public void handleChunkLoadDistance(ChunkLoadDistanceS2CPacket chunkLoadDistanceS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkLoadDistanceS2CPacket, this, this.client);
		this.chunkLoadDistance = chunkLoadDistanceS2CPacket.getDistance();
		this.world.method_2935().updateLoadDistance(chunkLoadDistanceS2CPacket.getDistance());
	}

	@Override
	public void handleChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket chunkRenderDistanceCenterS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkRenderDistanceCenterS2CPacket, this, this.client);
		this.world.method_2935().setChunkMapCenter(chunkRenderDistanceCenterS2CPacket.getChunkX(), chunkRenderDistanceCenterS2CPacket.getChunkZ());
	}

	private void method_2870(int i, int j, LightingProvider lightingProvider, LightType lightType, int k, int l, Iterator<byte[]> iterator) {
		for (int m = 0; m < 18; m++) {
			int n = -1 + m;
			boolean bl = (k & 1 << m) != 0;
			boolean bl2 = (l & 1 << m) != 0;
			if (bl || bl2) {
				lightingProvider.queueData(
					lightType, ChunkSectionPos.from(i, n, j), bl ? new ChunkNibbleArray((byte[])((byte[])iterator.next()).clone()) : new ChunkNibbleArray()
				);
				this.world.scheduleBlockRenders(i, n, j);
			}
		}
	}

	public ClientConnection getClientConnection() {
		return this.connection;
	}

	public Collection<PlayerListEntry> getPlayerList() {
		return this.playerListEntries.values();
	}

	@Nullable
	public PlayerListEntry getPlayerListEntry(UUID uUID) {
		return (PlayerListEntry)this.playerListEntries.get(uUID);
	}

	@Nullable
	public PlayerListEntry getPlayerListEntry(String string) {
		for (PlayerListEntry playerListEntry : this.playerListEntries.values()) {
			if (playerListEntry.getProfile().getName().equals(string)) {
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

	public RegistryTagManager getTagManager() {
		return this.tagManager;
	}

	public DataQueryHandler getDataQueryHandler() {
		return this.dataQueryHandler;
	}

	public UUID getSessionId() {
		return this.sessionId;
	}
}
