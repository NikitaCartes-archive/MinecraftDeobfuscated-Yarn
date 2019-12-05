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
import net.minecraft.block.entity.BeeHiveBlockEntity;
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
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.Screens;
import net.minecraft.client.gui.screen.StatsListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
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
import net.minecraft.client.network.packet.CraftFailedResponseS2CPacket;
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
import net.minecraft.client.network.packet.PlayerActionResponseS2CPacket;
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
import net.minecraft.client.options.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.client.render.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.sound.AbstractBeeSoundInstance;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.RidingMinecartSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.world.ClientChunkManager;
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
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
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
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
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

	public ClientPlayNetworkHandler(MinecraftClient client, Screen screen, ClientConnection clientConnection, GameProfile gameProfile) {
		this.client = client;
		this.loginScreen = screen;
		this.connection = clientConnection;
		this.profile = gameProfile;
		this.advancementHandler = new ClientAdvancementManager(client);
		this.commandSource = new ClientCommandSource(this, client);
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
		this.chunkLoadDistance = packet.getChunkLoadDistance();
		this.world = new ClientWorld(
			this,
			new LevelInfo(packet.getSeed(), packet.getGameMode(), false, packet.isHardcore(), packet.getGeneratorType()),
			packet.getDimension(),
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

		this.client.debugRenderer.reset();
		this.client.player.afterSpawn();
		int i = packet.getEntityId();
		this.world.addPlayer(i, this.client.player);
		this.client.player.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(this.client.player);
		this.client.cameraEntity = this.client.player;
		this.client.player.dimension = packet.getDimension();
		this.client.openScreen(new DownloadingTerrainScreen());
		this.client.player.setEntityId(i);
		this.client.player.setReducedDebugInfo(packet.hasReducedDebugInfo());
		this.client.player.setShowsDeathScreen(packet.showsDeathScreen());
		this.client.interactionManager.setGameMode(packet.getGameMode());
		this.client.options.onPlayerModelPartChange();
		this.connection
			.send(new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
		this.client.getGame().onStartGameSession();
	}

	@Override
	public void onEntitySpawn(EntitySpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		EntityType<?> entityType = packet.getEntityTypeId();
		Entity entity;
		if (entityType == EntityType.CHEST_MINECART) {
			entity = new ChestMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FURNACE_MINECART) {
			entity = new FurnaceMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.TNT_MINECART) {
			entity = new TntMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.SPAWNER_MINECART) {
			entity = new SpawnerMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.HOPPER_MINECART) {
			entity = new HopperMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.COMMAND_BLOCK_MINECART) {
			entity = new CommandBlockMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.MINECART) {
			entity = new MinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FISHING_BOBBER) {
			Entity entity2 = this.world.getEntityById(packet.getEntityData());
			if (entity2 instanceof PlayerEntity) {
				entity = new FishingBobberEntity(this.world, (PlayerEntity)entity2, d, e, f);
			} else {
				entity = null;
			}
		} else if (entityType == EntityType.ARROW) {
			entity = new ArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(packet.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.SPECTRAL_ARROW) {
			entity = new SpectralArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(packet.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.TRIDENT) {
			entity = new TridentEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(packet.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.SNOWBALL) {
			entity = new SnowballEntity(this.world, d, e, f);
		} else if (entityType == EntityType.LLAMA_SPIT) {
			entity = new LlamaSpitEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.ITEM_FRAME) {
			entity = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(packet.getEntityData()));
		} else if (entityType == EntityType.LEASH_KNOT) {
			entity = new LeadKnotEntity(this.world, new BlockPos(d, e, f));
		} else if (entityType == EntityType.ENDER_PEARL) {
			entity = new ThrownEnderpearlEntity(this.world, d, e, f);
		} else if (entityType == EntityType.EYE_OF_ENDER) {
			entity = new EnderEyeEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FIREWORK_ROCKET) {
			entity = new FireworkEntity(this.world, d, e, f, ItemStack.EMPTY);
		} else if (entityType == EntityType.FIREBALL) {
			entity = new FireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.DRAGON_FIREBALL) {
			entity = new DragonFireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.SMALL_FIREBALL) {
			entity = new SmallFireballEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.WITHER_SKULL) {
			entity = new WitherSkullEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.SHULKER_BULLET) {
			entity = new ShulkerBulletEntity(this.world, d, e, f, packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityz());
		} else if (entityType == EntityType.EGG) {
			entity = new ThrownEggEntity(this.world, d, e, f);
		} else if (entityType == EntityType.EVOKER_FANGS) {
			entity = new EvokerFangsEntity(this.world, d, e, f, 0.0F, 0, null);
		} else if (entityType == EntityType.POTION) {
			entity = new ThrownPotionEntity(this.world, d, e, f);
		} else if (entityType == EntityType.EXPERIENCE_BOTTLE) {
			entity = new ThrownExperienceBottleEntity(this.world, d, e, f);
		} else if (entityType == EntityType.BOAT) {
			entity = new BoatEntity(this.world, d, e, f);
		} else if (entityType == EntityType.TNT) {
			entity = new TntEntity(this.world, d, e, f, null);
		} else if (entityType == EntityType.ARMOR_STAND) {
			entity = new ArmorStandEntity(this.world, d, e, f);
		} else if (entityType == EntityType.END_CRYSTAL) {
			entity = new EnderCrystalEntity(this.world, d, e, f);
		} else if (entityType == EntityType.ITEM) {
			entity = new ItemEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FALLING_BLOCK) {
			entity = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(packet.getEntityData()));
		} else if (entityType == EntityType.AREA_EFFECT_CLOUD) {
			entity = new AreaEffectCloudEntity(this.world, d, e, f);
		} else {
			entity = null;
		}

		if (entity != null) {
			int i = packet.getId();
			entity.updateTrackedPosition(d, e, f);
			entity.pitch = (float)(packet.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(packet.getYaw() * 360) / 256.0F;
			entity.setEntityId(i);
			entity.setUuid(packet.getUuid());
			this.world.addEntity(i, entity);
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundManager().play(new RidingMinecartSoundInstance((AbstractMinecartEntity)entity));
			}
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
		entity.yaw = 0.0F;
		entity.pitch = 0.0F;
		entity.setEntityId(packet.getId());
		this.world.addEntity(packet.getId(), entity);
	}

	@Override
	public void onEntitySpawnGlobal(EntitySpawnGlobalS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		if (packet.getEntityTypeId() == 1) {
			LightningEntity lightningEntity = new LightningEntity(this.world, d, e, f, false);
			lightningEntity.updateTrackedPosition(d, e, f);
			lightningEntity.yaw = 0.0F;
			lightningEntity.pitch = 0.0F;
			lightningEntity.setEntityId(packet.getId());
			this.world.addLightning(lightningEntity);
		}
	}

	@Override
	public void onPaintingSpawn(PaintingSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PaintingEntity paintingEntity = new PaintingEntity(this.world, packet.getPos(), packet.getFacing(), packet.getMotive());
		paintingEntity.setEntityId(packet.getId());
		paintingEntity.setUuid(packet.getPaintingUuid());
		this.world.addEntity(packet.getId(), paintingEntity);
	}

	@Override
	public void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
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
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = (float)(packet.getYaw() * 360) / 256.0F;
		float h = (float)(packet.getPitch() * 360) / 256.0F;
		int i = packet.getId();
		OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(this.client.world, this.getPlayerListEntry(packet.getPlayerUuid()).getProfile());
		otherClientPlayerEntity.setEntityId(i);
		otherClientPlayerEntity.resetPosition(d, e, f);
		otherClientPlayerEntity.updateTrackedPosition(d, e, f);
		otherClientPlayerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.world.addPlayer(i, otherClientPlayerEntity);
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
				if (!(Math.abs(entity.getX() - d) >= 0.03125) && !(Math.abs(entity.getY() - e) >= 0.015625) && !(Math.abs(entity.getZ() - f) >= 0.03125)) {
					entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), g, h, 3, true);
				} else {
					entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
				}

				entity.onGround = packet.isOnGround();
			}
		}
	}

	@Override
	public void onHeldItemChange(HeldItemChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (PlayerInventory.isValidHotbarIndex(packet.getSlot())) {
			this.client.player.inventory.selectedSlot = packet.getSlot();
		}
	}

	@Override
	public void onEntityUpdate(EntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity != null) {
			if (!entity.isLogicalSideForUpdatingMovement()) {
				if (packet.method_22826()) {
					entity.trackedX = entity.trackedX + (long)packet.getDeltaXShort();
					entity.trackedY = entity.trackedY + (long)packet.getDeltaYShort();
					entity.trackedZ = entity.trackedZ + (long)packet.getDeltaZShort();
					Vec3d vec3d = EntityS2CPacket.decodePacketCoordinates(entity.trackedX, entity.trackedY, entity.trackedZ);
					float f = packet.hasRotation() ? (float)(packet.getYaw() * 360) / 256.0F : entity.yaw;
					float g = packet.hasRotation() ? (float)(packet.getPitch() * 360) / 256.0F : entity.pitch;
					entity.updateTrackedPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f, g, 3, false);
				} else if (packet.hasRotation()) {
					float h = (float)(packet.getYaw() * 360) / 256.0F;
					float f = (float)(packet.getPitch() * 360) / 256.0F;
					entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), h, f, 3, false);
				}

				entity.onGround = packet.isOnGround();
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

		for (int i = 0; i < packet.getEntityIds().length; i++) {
			int j = packet.getEntityIds()[i];
			this.world.removeEntity(j);
		}
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
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
			k += playerEntity.pitch;
		}

		if (packet.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			j += playerEntity.yaw;
		}

		playerEntity.setPositionAnglesAndUpdate(e, g, i, j, k);
		this.connection.send(new TeleportConfirmC2SPacket(packet.getTeleportId()));
		this.connection
			.send(new PlayerMoveC2SPacket.Both(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), playerEntity.yaw, playerEntity.pitch, false));
		if (!this.field_3698) {
			this.field_3698 = true;
			this.client.openScreen(null);
		}
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : packet.getRecords()) {
			this.world.setBlockStateWithoutNeighborUpdates(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
		}
	}

	@Override
	public void onChunkData(ChunkDataS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getX();
		int j = packet.getZ();
		WorldChunk worldChunk = this.world
			.getChunkManager()
			.loadChunkFromPacket(i, j, packet.getBiomeArray(), packet.getReadBuffer(), packet.getHeightmaps(), packet.getVerticalStripBitmask());
		if (worldChunk != null && packet.isFullChunk()) {
			this.world.addEntitiesToChunk(worldChunk);
		}

		for (int k = 0; k < 16; k++) {
			this.world.scheduleBlockRenders(i, k, j);
		}

		for (CompoundTag compoundTag : packet.getBlockEntityTagList()) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity != null) {
				blockEntity.fromTag(compoundTag);
			}
		}
	}

	@Override
	public void onUnloadChunk(UnloadChunkS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getX();
		int j = packet.getZ();
		ClientChunkManager clientChunkManager = this.world.getChunkManager();
		clientChunkManager.unload(i, j);
		LightingProvider lightingProvider = clientChunkManager.getLightingProvider();

		for (int k = 0; k < 16; k++) {
			this.world.scheduleBlockRenders(i, k, j);
			lightingProvider.updateSectionStatus(ChunkSectionPos.from(i, k, j), true);
		}

		lightingProvider.setLightEnabled(new ChunkPos(i, j), false);
	}

	@Override
	public void onBlockUpdate(BlockUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.setBlockStateWithoutNeighborUpdates(packet.getPos(), packet.getState());
	}

	@Override
	public void onDisconnect(DisconnectS2CPacket packet) {
		this.connection.disconnect(packet.getReason());
	}

	@Override
	public void onDisconnected(Text reason) {
		this.client.disconnect();
		if (this.loginScreen != null) {
			if (this.loginScreen instanceof RealmsScreenProxy) {
				this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.loginScreen).getScreen(), "disconnect.lost", reason).getProxy());
			} else {
				this.client.openScreen(new DisconnectedScreen(this.loginScreen, "disconnect.lost", reason));
			}
		} else {
			this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", reason));
		}
	}

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

			if (entity instanceof ItemEntity) {
				((ItemEntity)entity).getStack().setCount(packet.getStackAmount());
			}

			this.client
				.particleManager
				.addParticle(new ItemPickupParticle(this.client.getEntityRenderManager(), this.client.getBufferBuilders(), this.world, entity, livingEntity));
			this.world.removeEntity(packet.getEntityId());
		}
	}

	@Override
	public void onChatMessage(ChatMessageS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.inGameHud.addChatMessage(packet.getLocation(), packet.getMessage());
	}

	@Override
	public void onEntityAnimation(EntityAnimationS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			if (packet.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN_HAND);
			} else if (packet.getAnimationId() == 3) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.OFF_HAND);
			} else if (packet.getAnimationId() == 1) {
				entity.animateDamage();
			} else if (packet.getAnimationId() == 2) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				playerEntity.wakeUp(false, false);
			} else if (packet.getAnimationId() == 4) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.CRIT);
			} else if (packet.getAnimationId() == 5) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.ENCHANTED_HIT);
			}
		}
	}

	@Override
	public void onMobSpawn(MobSpawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = (float)(packet.getYaw() * 360) / 256.0F;
		float h = (float)(packet.getPitch() * 360) / 256.0F;
		LivingEntity livingEntity = (LivingEntity)EntityType.createInstanceFromId(packet.getEntityTypeId(), this.client.world);
		if (livingEntity != null) {
			livingEntity.updateTrackedPosition(d, e, f);
			livingEntity.bodyYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
			livingEntity.headYaw = (float)(packet.getHeadYaw() * 360) / 256.0F;
			if (livingEntity instanceof EnderDragonEntity) {
				EnderDragonPart[] enderDragonParts = ((EnderDragonEntity)livingEntity).getBodyParts();

				for (int i = 0; i < enderDragonParts.length; i++) {
					enderDragonParts[i].setEntityId(i + packet.getId());
				}
			}

			livingEntity.setEntityId(packet.getId());
			livingEntity.setUuid(packet.getUuid());
			livingEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
			livingEntity.setVelocity(
				(double)((float)packet.getVelocityX() / 8000.0F), (double)((float)packet.getVelocityY() / 8000.0F), (double)((float)packet.getVelocityZ() / 8000.0F)
			);
			this.world.addEntity(packet.getId(), livingEntity);
			if (livingEntity instanceof BeeEntity) {
				boolean bl = ((BeeEntity)livingEntity).isAngry();
				AbstractBeeSoundInstance abstractBeeSoundInstance;
				if (bl) {
					abstractBeeSoundInstance = new AggressiveBeeSoundInstance((BeeEntity)livingEntity);
				} else {
					abstractBeeSoundInstance = new PassiveBeeSoundInstance((BeeEntity)livingEntity);
				}

				this.client.getSoundManager().play(abstractBeeSoundInstance);
			}
		} else {
			LOGGER.warn("Skipping Entity with id {}", packet.getEntityTypeId());
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
		this.client.player.setPlayerSpawn(packet.getPos(), true, false);
		this.client.world.getLevelProperties().setSpawnPos(packet.getPos());
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
						this.client.inGameHud.setOverlayMessage(I18n.translate("mount.onboard", this.client.options.keySneak.getLocalizedName()), false);
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

	private static ItemStack method_19691(PlayerEntity playerEntity) {
		for (Hand hand : Hand.values()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
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
			if (packet.getStatus() == 21) {
				this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else if (packet.getStatus() == 35) {
				int i = 40;
				this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
				this.world.playSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.gameRenderer.showFloatingItem(method_19691(this.client.player));
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
		this.client.player.getHungerManager().setSaturationLevelClient(packet.getSaturation());
	}

	@Override
	public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		DimensionType dimensionType = packet.getDimension();
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		int i = clientPlayerEntity.getEntityId();
		if (dimensionType != clientPlayerEntity.dimension) {
			this.field_3698 = false;
			Scoreboard scoreboard = this.world.getScoreboard();
			this.world = new ClientWorld(
				this,
				new LevelInfo(packet.method_22425(), packet.getGameMode(), false, this.client.world.getLevelProperties().isHardcore(), packet.getGeneratorType()),
				packet.getDimension(),
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
		clientPlayerEntity2.getAttributes().method_22324(clientPlayerEntity.getAttributes());
		clientPlayerEntity2.afterSpawn();
		clientPlayerEntity2.setServerBrand(string);
		this.world.addPlayer(i, clientPlayerEntity2);
		clientPlayerEntity2.yaw = -180.0F;
		clientPlayerEntity2.input = new KeyboardInput(this.client.options);
		this.client.interactionManager.copyAbilities(clientPlayerEntity2);
		clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.getReducedDebugInfo());
		clientPlayerEntity2.setShowsDeathScreen(clientPlayerEntity.showsDeathScreen());
		if (this.client.currentScreen instanceof DeathScreen) {
			this.client.openScreen(null);
		}

		this.client.interactionManager.setGameMode(packet.getGameMode());
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
	public void onGuiOpen(GuiOpenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getHorseId());
		if (entity instanceof HorseBaseEntity) {
			ClientPlayerEntity clientPlayerEntity = this.client.player;
			HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
			BasicInventory basicInventory = new BasicInventory(packet.getSlotCount());
			HorseContainer horseContainer = new HorseContainer(packet.getId(), clientPlayerEntity.inventory, basicInventory, horseBaseEntity);
			clientPlayerEntity.container = horseContainer;
			this.client.openScreen(new HorseScreen(horseContainer, clientPlayerEntity.inventory, horseBaseEntity));
		}
	}

	@Override
	public void onOpenContainer(OpenContainerPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Screens.open(packet.getContainerType(), this.client, packet.getSyncId(), packet.getName());
	}

	@Override
	public void onGuiSlotUpdate(GuiSlotUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = packet.getItemStack();
		int i = packet.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		if (packet.getId() == -1) {
			if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
				playerEntity.inventory.setCursorStack(itemStack);
			}
		} else if (packet.getId() == -2) {
			playerEntity.inventory.setInvStack(i, itemStack);
		} else {
			boolean bl = false;
			if (this.client.currentScreen instanceof CreativeInventoryScreen) {
				CreativeInventoryScreen creativeInventoryScreen = (CreativeInventoryScreen)this.client.currentScreen;
				bl = creativeInventoryScreen.getSelectedTab() != ItemGroup.INVENTORY.getIndex();
			}

			if (packet.getId() == 0 && packet.getSlot() >= 36 && i < 45) {
				if (!itemStack.isEmpty()) {
					ItemStack itemStack2 = playerEntity.playerContainer.getSlot(i).getStack();
					if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
						itemStack.setCooldown(5);
					}
				}

				playerEntity.playerContainer.setStackInSlot(i, itemStack);
			} else if (packet.getId() == playerEntity.container.syncId && (packet.getId() != 0 || !bl)) {
				playerEntity.container.setStackInSlot(i, itemStack);
			}
		}
	}

	@Override
	public void onGuiActionConfirm(ConfirmGuiActionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Container container = null;
		PlayerEntity playerEntity = this.client.player;
		if (packet.getId() == 0) {
			container = playerEntity.playerContainer;
		} else if (packet.getId() == playerEntity.container.syncId) {
			container = playerEntity.container;
		}

		if (container != null && !packet.wasAccepted()) {
			this.sendPacket(new GuiActionConfirmC2SPacket(packet.getId(), packet.getActionId(), true));
		}
	}

	@Override
	public void onInventory(InventoryS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (packet.getGuiId() == 0) {
			playerEntity.playerContainer.updateSlotStacks(packet.getSlotStacks());
		} else if (packet.getGuiId() == playerEntity.container.syncId) {
			playerEntity.container.updateSlotStacks(packet.getSlotStacks());
		}
	}

	@Override
	public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		BlockEntity blockEntity = this.world.getBlockEntity(packet.getPos());
		if (!(blockEntity instanceof SignBlockEntity)) {
			blockEntity = new SignBlockEntity();
			blockEntity.setWorld(this.world, packet.getPos());
		}

		this.client.player.openEditSignScreen((SignBlockEntity)blockEntity);
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (this.client.world.isChunkLoaded(packet.getPos())) {
			BlockEntity blockEntity = this.client.world.getBlockEntity(packet.getPos());
			int i = packet.getBlockEntityType();
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
				|| i == 13 && blockEntity instanceof CampfireBlockEntity
				|| i == 14 && blockEntity instanceof BeeHiveBlockEntity) {
				blockEntity.fromTag(packet.getCompoundTag());
			}

			if (bl && this.client.currentScreen instanceof CommandBlockScreen) {
				((CommandBlockScreen)this.client.currentScreen).updateCommandBlock();
			}
		}
	}

	@Override
	public void onGuiUpdate(GuiUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.container != null && playerEntity.container.syncId == packet.getId()) {
			playerEntity.container.setProperties(packet.getPropertyId(), packet.getValue());
		}
	}

	@Override
	public void onEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getId());
		if (entity != null) {
			entity.equipStack(packet.getSlot(), packet.getStack());
		}
	}

	@Override
	public void onGuiClose(GuiCloseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.player.closeScreen();
	}

	@Override
	public void onBlockAction(BlockActionS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.addBlockAction(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
	}

	@Override
	public void onBlockDestroyProgress(BlockBreakingProgressS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
	}

	@Override
	public void onGameStateChange(GameStateChangeS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		int i = packet.getReason();
		float f = packet.getValue();
		int j = MathHelper.floor(f + 0.5F);
		if (i >= 0 && i < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[i] != null) {
			playerEntity.addChatMessage(new TranslatableText(GameStateChangeS2CPacket.REASON_MESSAGES[i]), false);
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
				this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
				this.client.openScreen(new DownloadingTerrainScreen());
			} else if (j == 1) {
				this.client
					.openScreen(
						new CreditsScreen(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN)))
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
						new TranslatableText(
							"demo.help.movement",
							gameOptions.keyForward.getLocalizedName(),
							gameOptions.keyLeft.getLocalizedName(),
							gameOptions.keyBack.getLocalizedName(),
							gameOptions.keyRight.getLocalizedName()
						)
					);
			} else if (f == 102.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.jump", gameOptions.keyJump.getLocalizedName()));
			} else if (f == 103.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()));
			} else if (f == 104.0F) {
				this.client.inGameHud.getChatHud().addMessage(new TranslatableText("demo.day.6", gameOptions.keyScreenshot.getLocalizedName()));
			}
		} else if (i == 6) {
			this.world
				.playSound(
					playerEntity, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F
				);
		} else if (i == 7) {
			this.world.setRainGradient(f);
		} else if (i == 8) {
			this.world.setThunderGradient(f);
		} else if (i == 9) {
			this.world
				.playSound(
					playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F
				);
		} else if (i == 10) {
			this.world.addParticle(ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
			this.world
				.playSound(
					playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F
				);
		} else if (i == 11) {
			this.client.player.setShowsDeathScreen(f == 0.0F);
		}
	}

	@Override
	public void onMapUpdate(MapUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
		String string = FilledMapItem.getMapName(packet.getId());
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

		packet.apply(mapState);
		mapRenderer.updateTexture(mapState);
	}

	@Override
	public void onWorldEvent(WorldEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.isGlobal()) {
			this.client.world.playGlobalEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
		} else {
			this.client.world.playLevelEvent(packet.getEventId(), packet.getPos(), packet.getEffectData());
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
		this.commandDispatcher = new CommandDispatcher<>(packet.getCommandTree());
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
		SearchableContainer<RecipeResultCollection> searchableContainer = this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT);
		searchableContainer.clear();
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.reload();
		clientRecipeBook.getOrderedResults().forEach(searchableContainer::add);
		searchableContainer.reload();
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
	public void onTagQuery(TagQueryResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (!this.dataQueryHandler.handleQueryResponse(packet.getTransactionId(), packet.getTag())) {
			LOGGER.debug("Got unhandled response to tag query {}", packet.getTransactionId());
		}
	}

	@Override
	public void onStatistics(StatisticsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (Entry<Stat<?>, Integer> entry : packet.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStats().setStat(this.client.player, stat, i);
		}

		if (this.client.currentScreen instanceof StatsListener) {
			((StatsListener)this.client.currentScreen).onStatsReady();
		}
	}

	@Override
	public void onUnlockRecipes(UnlockRecipesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setGuiOpen(packet.isGuiOpen());
		clientRecipeBook.setFilteringCraftable(packet.isFilteringCraftable());
		clientRecipeBook.setFurnaceGuiOpen(packet.isFurnaceGuiOpen());
		clientRecipeBook.setFurnaceFilteringCraftable(packet.isFurnaceFilteringCraftable());
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
	public void onEntityPotionEffect(EntityPotionEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = StatusEffect.byRawId(packet.getEffectId());
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon()
				);
				statusEffectInstance.setPermanent(packet.isPermanent());
				((LivingEntity)entity).addStatusEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.tagManager = packet.getTagManager();
		if (!this.connection.isLocal()) {
			BlockTags.setContainer(this.tagManager.blocks());
			ItemTags.setContainer(this.tagManager.items());
			FluidTags.setContainer(this.tagManager.fluids());
			EntityTypeTags.setContainer(this.tagManager.entityTypes());
		}

		this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
	}

	@Override
	public void onCombatEvent(CombatEventS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		if (packet.type == CombatEventS2CPacket.Type.ENTITY_DIED) {
			Entity entity = this.world.getEntityById(packet.entityId);
			if (entity == this.client.player) {
				if (this.client.player.showsDeathScreen()) {
					this.client.openScreen(new DeathScreen(packet.deathMessage, this.world.getLevelProperties().isHardcore()));
				} else {
					this.client.player.requestRespawn();
				}
			}
		}
	}

	@Override
	public void onDifficulty(DifficultyS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.world.getLevelProperties().setDifficulty(packet.getDifficulty());
		this.client.world.getLevelProperties().setDifficultyLocked(packet.isDifficultyLocked());
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
	public void onWorldBorder(WorldBorderS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		packet.apply(this.world.getWorldBorder());
	}

	@Override
	public void onTitle(TitleS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		TitleS2CPacket.Action action = packet.getAction();
		String string = null;
		String string2 = null;
		String string3 = packet.getText() != null ? packet.getText().asFormattedString() : "";
		switch (action) {
			case TITLE:
				string = string3;
				break;
			case SUBTITLE:
				string2 = string3;
				break;
			case ACTIONBAR:
				this.client.inGameHud.setOverlayMessage(string3, false);
				return;
			case RESET:
				this.client.inGameHud.setTitles("", "", -1, -1, -1);
				this.client.inGameHud.setDefaultTitleFade();
				return;
		}

		this.client.inGameHud.setTitles(string, string2, packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
		this.client.inGameHud.getPlayerListWidget().setHeader(packet.getHeader().asFormattedString().isEmpty() ? null : packet.getHeader());
		this.client.inGameHud.getPlayerListWidget().setFooter(packet.getFooter().asFormattedString().isEmpty() ? null : packet.getFooter());
	}

	@Override
	public void onRemoveEntityEffect(RemoveEntityEffectS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = packet.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).removeStatusEffect(packet.getEffectType());
		}
	}

	@Override
	public void onPlayerList(PlayerListS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);

		for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
			if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
				this.playerListEntries.remove(entry.getProfile().getId());
			} else {
				PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.getProfile().getId());
				if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
					playerListEntry = new PlayerListEntry(entry);
					this.playerListEntries.put(playerListEntry.getProfile().getId(), playerListEntry);
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
		playerEntity.abilities.flying = packet.isFlying();
		playerEntity.abilities.creativeMode = packet.isCreativeMode();
		playerEntity.abilities.invulnerable = packet.isInvulnerable();
		playerEntity.abilities.allowFlying = packet.allowFlying();
		playerEntity.abilities.setFlySpeed(packet.getFlySpeed());
		playerEntity.abilities.setWalkSpeed(packet.getFovModifier());
	}

	@Override
	public void onPlaySound(PlaySoundS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client
			.world
			.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
	}

	@Override
	public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			this.client.world.playSoundFromEntity(this.client.player, entity, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch());
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
					false,
					0,
					SoundInstance.AttenuationType.LINEAR,
					(float)packet.getX(),
					(float)packet.getY(),
					(float)packet.getZ(),
					false
				)
			);
	}

	@Override
	public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
		String string = packet.getURL();
		String string2 = packet.getSHA1();
		if (this.validateResourcePackUrl(string)) {
			if (string.startsWith("level://")) {
				try {
					String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
					File file = new File(this.client.runDirectory, "saves");
					File file2 = new File(file, string3);
					if (file2.isFile()) {
						this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
						CompletableFuture<?> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
			} else {
				ServerInfo serverInfo = this.client.getCurrentServerEntry();
				if (serverInfo != null && serverInfo.getResourcePack() == ServerInfo.ResourcePackState.ENABLED) {
					this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
					this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
				} else if (serverInfo != null && serverInfo.getResourcePack() != ServerInfo.ResourcePackState.PROMPT) {
					this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
				} else {
					this.client.execute(() -> this.client.openScreen(new ConfirmScreen(bl -> {
							this.client = MinecraftClient.getInstance();
							ServerInfo serverInfox = this.client.getCurrentServerEntry();
							if (bl) {
								if (serverInfox != null) {
									serverInfox.setResourcePackState(ServerInfo.ResourcePackState.ENABLED);
								}

								this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.ACCEPTED);
								this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
							} else {
								if (serverInfox != null) {
									serverInfox.setResourcePackState(ServerInfo.ResourcePackState.DISABLED);
								}

								this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.DECLINED);
							}

							ServerList.updateServerListEntry(serverInfox);
							this.client.openScreen(null);
						}, new TranslatableText("multiplayer.texturePrompt.line1"), new TranslatableText("multiplayer.texturePrompt.line2"))));
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
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED)).exceptionally(throwable -> {
			this.sendResourcePackStatus(ResourcePackStatusC2SPacket.Status.FAILED_DOWNLOAD);
			return null;
		});
	}

	private void sendResourcePackStatus(ResourcePackStatusC2SPacket.Status status) {
		this.connection.send(new ResourcePackStatusC2SPacket(status));
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
			entity.setPositionAnglesAndUpdate(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
			this.connection.send(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
		if (itemStack.getItem() == Items.WRITTEN_BOOK) {
			this.client.openScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
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
				this.client.player.setServerBrand(packetByteBuf.readString(32767));
			} else if (CustomPayloadS2CPacket.DEBUG_PATH.equals(identifier)) {
				int i = packetByteBuf.readInt();
				float f = packetByteBuf.readFloat();
				Path path = Path.fromBuffer(packetByteBuf);
				this.client.debugRenderer.pathfindingDebugRenderer.addPath(i, path, f);
			} else if (CustomPayloadS2CPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
				long l = packetByteBuf.readVarLong();
				BlockPos blockPos = packetByteBuf.readBlockPos();
				((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).addNeighborUpdate(l, blockPos);
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
				DimensionType dimensionType = DimensionType.byRawId(packetByteBuf.readInt());
				BlockBox blockBox = new BlockBox(
					packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
				);
				int m = packetByteBuf.readInt();
				List<BlockBox> list2 = Lists.<BlockBox>newArrayList();
				List<Boolean> list3 = Lists.<Boolean>newArrayList();

				for (int n = 0; n < m; n++) {
					list2.add(
						new BlockBox(
							packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt()
						)
					);
					list3.add(packetByteBuf.readBoolean());
				}

				this.client.debugRenderer.structureDebugRenderer.method_3871(blockBox, list2, list3, dimensionType);
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
					this.client.debugRenderer.villageDebugRenderer.addSection(packetByteBuf.readChunkSectionPos());
				}

				int j = packetByteBuf.readInt();

				for (int m = 0; m < j; m++) {
					this.client.debugRenderer.villageDebugRenderer.removeSection(packetByteBuf.readChunkSectionPos());
				}
			} else if (CustomPayloadS2CPacket.DEBUG_POI_ADDED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				String string = packetByteBuf.readString();
				int m = packetByteBuf.readInt();
				VillageDebugRenderer.PointOfInterest pointOfInterest = new VillageDebugRenderer.PointOfInterest(blockPos2, string, m);
				this.client.debugRenderer.villageDebugRenderer.addPointOfInterest(pointOfInterest);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_REMOVED.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(blockPos2);
			} else if (CustomPayloadS2CPacket.DEBUG_POI_TICKET_COUNT.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				this.client.debugRenderer.villageDebugRenderer.setFreeTicketCount(blockPos2, j);
			} else if (CustomPayloadS2CPacket.DEBUG_GOAL_SELECTOR.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				int m = packetByteBuf.readInt();
				List<GoalSelectorDebugRenderer.GoalSelector> list2 = Lists.<GoalSelectorDebugRenderer.GoalSelector>newArrayList();

				for (int k = 0; k < m; k++) {
					int n = packetByteBuf.readInt();
					boolean bl = packetByteBuf.readBoolean();
					String string2 = packetByteBuf.readString(255);
					list2.add(new GoalSelectorDebugRenderer.GoalSelector(blockPos2, n, string2, bl));
				}

				this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(j, list2);
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
				int o = packetByteBuf.readInt();
				String string3 = packetByteBuf.readString();
				String string4 = packetByteBuf.readString();
				int p = packetByteBuf.readInt();
				String string5 = packetByteBuf.readString();
				boolean bl2 = packetByteBuf.readBoolean();
				Path path2;
				if (bl2) {
					path2 = Path.fromBuffer(packetByteBuf);
				} else {
					path2 = null;
				}

				boolean bl3 = packetByteBuf.readBoolean();
				VillageDebugRenderer.Brain brain = new VillageDebugRenderer.Brain(uUID, o, string3, string4, p, position, string5, path2, bl3);
				int q = packetByteBuf.readInt();

				for (int r = 0; r < q; r++) {
					String string6 = packetByteBuf.readString();
					brain.field_18927.add(string6);
				}

				int r = packetByteBuf.readInt();

				for (int s = 0; s < r; s++) {
					String string7 = packetByteBuf.readString();
					brain.field_18928.add(string7);
				}

				int s = packetByteBuf.readInt();

				for (int t = 0; t < s; t++) {
					String string8 = packetByteBuf.readString();
					brain.field_19374.add(string8);
				}

				int t = packetByteBuf.readInt();

				for (int u = 0; u < t; u++) {
					BlockPos blockPos3 = packetByteBuf.readBlockPos();
					brain.pointsOfInterest.add(blockPos3);
				}

				int u = packetByteBuf.readInt();

				for (int v = 0; v < u; v++) {
					String string9 = packetByteBuf.readString();
					brain.field_19375.add(string9);
				}

				this.client.debugRenderer.villageDebugRenderer.addBrain(brain);
			} else if (CustomPayloadS2CPacket.DEBUG_BEE.equals(identifier)) {
				double d = packetByteBuf.readDouble();
				double e = packetByteBuf.readDouble();
				double g = packetByteBuf.readDouble();
				Position position = new PositionImpl(d, e, g);
				UUID uUID = packetByteBuf.readUuid();
				int o = packetByteBuf.readInt();
				boolean bl4 = packetByteBuf.readBoolean();
				BlockPos blockPos4 = null;
				if (bl4) {
					blockPos4 = packetByteBuf.readBlockPos();
				}

				boolean bl5 = packetByteBuf.readBoolean();
				BlockPos blockPos5 = null;
				if (bl5) {
					blockPos5 = packetByteBuf.readBlockPos();
				}

				int w = packetByteBuf.readInt();
				boolean bl6 = packetByteBuf.readBoolean();
				Path path3 = null;
				if (bl6) {
					path3 = Path.fromBuffer(packetByteBuf);
				}

				BeeDebugRenderer.Bee bee = new BeeDebugRenderer.Bee(uUID, o, position, path3, blockPos4, blockPos5, w);
				int q = packetByteBuf.readInt();

				for (int r = 0; r < q; r++) {
					String string6 = packetByteBuf.readString();
					bee.field_21542.add(string6);
				}

				int r = packetByteBuf.readInt();

				for (int s = 0; s < r; s++) {
					BlockPos blockPos6 = packetByteBuf.readBlockPos();
					bee.blacklistedHives.add(blockPos6);
				}

				this.client.debugRenderer.beeDebugRenderer.addBee(bee);
			} else if (CustomPayloadS2CPacket.DEBUG_HIVE.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				String string = packetByteBuf.readString();
				int m = packetByteBuf.readInt();
				int x = packetByteBuf.readInt();
				boolean bl7 = packetByteBuf.readBoolean();
				BeeDebugRenderer.Hive hive = new BeeDebugRenderer.Hive(blockPos2, string, m, x, bl7, this.world.getTime());
				this.client.debugRenderer.beeDebugRenderer.addHive(hive);
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR.equals(identifier)) {
				this.client.debugRenderer.gameTestDebugRenderer.clear();
			} else if (CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				String string10 = packetByteBuf.readString();
				int x = packetByteBuf.readInt();
				this.client.debugRenderer.gameTestDebugRenderer.addMarker(blockPos2, j, string10, x);
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
			if (packet.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (packet.getMode() == 2) {
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
		Team team;
		if (packet.getMode() == 0) {
			team = scoreboard.addTeam(packet.getTeamName());
		} else {
			team = scoreboard.getTeam(packet.getTeamName());
		}

		if (packet.getMode() == 0 || packet.getMode() == 2) {
			team.setDisplayName(packet.getDisplayName());
			team.setColor(packet.getPlayerPrefix());
			team.setFriendlyFlagsBitwise(packet.getFlags());
			AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(packet.getNameTagVisibilityRule());
			if (visibilityRule != null) {
				team.setNameTagVisibilityRule(visibilityRule);
			}

			AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(packet.getCollisionRule());
			if (collisionRule != null) {
				team.setCollisionRule(collisionRule);
			}

			team.setPrefix(packet.getPrefix());
			team.setSuffix(packet.getSuffix());
		}

		if (packet.getMode() == 0 || packet.getMode() == 3) {
			for (String string : packet.getPlayerList()) {
				scoreboard.addPlayerToTeam(string, team);
			}
		}

		if (packet.getMode() == 4) {
			for (String string : packet.getPlayerList()) {
				scoreboard.removePlayerFromTeam(string, team);
			}
		}

		if (packet.getMode() == 1) {
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
	public void onEntityAttributes(EntityAttributesS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Entity entity = this.world.getEntityById(packet.getEntityId());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributes();

				for (EntityAttributesS2CPacket.Entry entry : packet.getEntries()) {
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
	public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Container container = this.client.player.container;
		if (container.syncId == packet.getSyncId() && container.isRestricted(this.client.player)) {
			this.recipeManager.get(packet.getRecipeId()).ifPresent(recipe -> {
				if (this.client.currentScreen instanceof RecipeBookProvider) {
					RecipeBookWidget recipeBookWidget = ((RecipeBookProvider)this.client.currentScreen).getRecipeBookGui();
					recipeBookWidget.showGhostRecipe(recipe, container.slotList);
				}
			});
		}
	}

	@Override
	public void onLightUpdate(LightUpdateS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		int i = packet.getChunkX();
		int j = packet.getChunkZ();
		LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
		int k = packet.getSkyLightMask();
		int l = packet.getFilledSkyLightMask();
		Iterator<byte[]> iterator = packet.getSkyLightUpdates().iterator();
		this.method_2870(i, j, lightingProvider, LightType.SKY, k, l, iterator);
		int m = packet.getBlockLightMask();
		int n = packet.getFilledBlockLightMask();
		Iterator<byte[]> iterator2 = packet.getBlockLightUpdates().iterator();
		this.method_2870(i, j, lightingProvider, LightType.BLOCK, m, n, iterator2);
	}

	@Override
	public void onSetTradeOffers(SetTradeOffersPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		Container container = this.client.player.container;
		if (packet.getSyncId() == container.syncId && container instanceof MerchantContainer) {
			((MerchantContainer)container).setOffers(new TraderOfferList(packet.getOffers().toTag()));
			((MerchantContainer)container).setExperienceFromServer(packet.getExperience());
			((MerchantContainer)container).setLevelProgress(packet.getLevelProgress());
			((MerchantContainer)container).setCanLevel(packet.isLeveled());
			((MerchantContainer)container).setRefreshTrades(packet.method_20722());
		}
	}

	@Override
	public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.chunkLoadDistance = packet.getDistance();
		this.world.getChunkManager().updateLoadDistance(packet.getDistance());
	}

	@Override
	public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
	}

	@Override
	public void handlePlayerActionResponse(PlayerActionResponseS2CPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.client);
		this.client.interactionManager.processPlayerActionResponse(this.world, packet.getBlockPos(), packet.getBlockState(), packet.getAction(), packet.isApproved());
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

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

	public Collection<PlayerListEntry> getPlayerList() {
		return this.playerListEntries.values();
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
