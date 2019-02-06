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
import net.minecraft.class_300;
import net.minecraft.class_452;
import net.minecraft.advancement.SimpleAdvancement;
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
import net.minecraft.client.audio.GuardianAttackSoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.RidingMinecartSoundInstance;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.gui.CommandBlockScreen;
import net.minecraft.client.gui.ContainerScreenRegistry;
import net.minecraft.client.gui.MainMenuScreen;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.WrittenBookScreen;
import net.minecraft.client.gui.container.HorseScreen;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;
import net.minecraft.client.gui.ingame.DeathScreen;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.menu.DemoScreen;
import net.minecraft.client.gui.menu.DisconnectedScreen;
import net.minecraft.client.gui.menu.DownloadingTerrainScreen;
import net.minecraft.client.gui.menu.EndCreditsScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.RealmsScreen;
import net.minecraft.client.gui.menu.YesNoScreen;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
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
import net.minecraft.client.network.packet.PlayerUseBedS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayS2CPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetVillagerRecipesPacket;
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
import net.minecraft.client.recipe.book.RecipeResultCollection;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
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
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.network.packet.GuiActionConfirmC2SPacket;
import net.minecraft.server.network.packet.KeepAliveC2SPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.packet.TeleportConfirmC2SPacket;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.math.Vec3d;
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
	private final Screen field_3701;
	private MinecraftClient client;
	private ClientWorld world;
	private boolean field_3698;
	private final Map<UUID, ScoreboardEntry> field_3693 = Maps.<UUID, ScoreboardEntry>newHashMap();
	private final ClientAdvancementManager advancementHandler;
	private final ClientCommandSource commandSource;
	private TagManager tagManager = new TagManager();
	private final class_300 field_3692 = new class_300(this);
	private final Random random = new Random();
	private CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();
	private final RecipeManager recipeManager = new RecipeManager();
	private final UUID field_16771 = UUID.randomUUID();

	public ClientPlayNetworkHandler(MinecraftClient minecraftClient, Screen screen, ClientConnection clientConnection, GameProfile gameProfile) {
		this.client = minecraftClient;
		this.field_3701 = screen;
		this.connection = clientConnection;
		this.profile = gameProfile;
		this.advancementHandler = new ClientAdvancementManager(minecraftClient);
		this.commandSource = new ClientCommandSource(this, minecraftClient);
	}

	public ClientCommandSource getCommandSource() {
		return this.commandSource;
	}

	public void method_2868() {
		this.world = null;
	}

	public RecipeManager getRecipeManager() {
		return this.recipeManager;
	}

	@Override
	public void method_11120(GameJoinS2CPacket gameJoinS2CPacket) {
		NetworkThreadUtils.forceMainThread(gameJoinS2CPacket, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		this.world = new ClientWorld(
			this,
			new LevelInfo(0L, gameJoinS2CPacket.getGameMode(), false, gameJoinS2CPacket.isHardcore(), gameJoinS2CPacket.method_11563()),
			gameJoinS2CPacket.getDimension(),
			gameJoinS2CPacket.getDifficulty(),
			this.client.getProfiler(),
			this.client.worldRenderer
		);
		this.client.options.difficulty = gameJoinS2CPacket.getDifficulty();
		this.client.method_1481(this.world);
		if (this.client.player == null) {
			this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook(this.world.getRecipeManager()));
			this.client.player.yaw = -180.0F;
			if (this.client.getServer() != null) {
				this.client.getServer().method_4817(this.client.player.getUuid());
			}
		}

		this.client.player.method_5823();
		int i = gameJoinS2CPacket.getEntityId();
		this.world.method_18107(i, this.client.player);
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
			.sendPacket(
				new CustomPayloadC2SPacket(CustomPayloadC2SPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName()))
			);
		this.client.getGame().onStartGameSession();
	}

	@Override
	public void method_11112(EntitySpawnS2CPacket entitySpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitySpawnS2CPacket, this, this.client);
		double d = entitySpawnS2CPacket.getX();
		double e = entitySpawnS2CPacket.getY();
		double f = entitySpawnS2CPacket.getZ();
		EntityType<?> entityType = entitySpawnS2CPacket.getEntityTypeId();
		Entity entity;
		if (entityType == EntityType.CHEST_MINECART) {
			entity = new ChestMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FURNACE_MINECART) {
			entity = new FurnaceMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.TNT_MINECART) {
			entity = new TNTMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.SPAWNER_MINECART) {
			entity = new MobSpawnerMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.HOPPER_MINECART) {
			entity = new HopperMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.COMMAND_BLOCK_MINECART) {
			entity = new CommandBlockMinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.MINECART) {
			entity = new MinecartEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FISHING_BOBBER) {
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 instanceof PlayerEntity) {
				entity = new FishHookEntity(this.world, (PlayerEntity)entity2, d, e, f);
			} else {
				entity = null;
			}
		} else if (entityType == EntityType.ARROW) {
			entity = new ArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.SPECTRAL_ARROW) {
			entity = new SpectralArrowEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.TRIDENT) {
			entity = new TridentEntity(this.world, d, e, f);
			Entity entity2 = this.world.getEntityById(entitySpawnS2CPacket.getEntityData());
			if (entity2 != null) {
				((ProjectileEntity)entity).setOwner(entity2);
			}
		} else if (entityType == EntityType.SNOWBALL) {
			entity = new SnowballEntity(this.world, d, e, f);
		} else if (entityType == EntityType.LLAMA_SPIT) {
			entity = new LlamaSpitEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.ITEM_FRAME) {
			entity = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(entitySpawnS2CPacket.getEntityData()));
		} else if (entityType == EntityType.LEASH_KNOT) {
			entity = new LeadKnotEntity(this.world, new BlockPos(d, e, f));
		} else if (entityType == EntityType.ENDER_PEARL) {
			entity = new ThrownEnderpearlEntity(this.world, d, e, f);
		} else if (entityType == EntityType.EYE_OF_ENDER) {
			entity = new EnderEyeEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FIREWORK_ROCKET) {
			entity = new FireworkEntity(this.world, d, e, f, ItemStack.EMPTY);
		} else if (entityType == EntityType.FIREBALL) {
			entity = new FireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.DRAGON_FIREBALL) {
			entity = new DragonFireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.SMALL_FIREBALL) {
			entity = new SmallFireballEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.WITHER_SKULL) {
			entity = new ExplodingWitherSkullEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
		} else if (entityType == EntityType.SHULKER_BULLET) {
			entity = new ShulkerBulletEntity(
				this.world, d, e, f, entitySpawnS2CPacket.getVelocityX(), entitySpawnS2CPacket.getVelocityY(), entitySpawnS2CPacket.getVelocityz()
			);
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
			entity = new PrimedTntEntity(this.world, d, e, f, null);
		} else if (entityType == EntityType.ARMOR_STAND) {
			entity = new ArmorStandEntity(this.world, d, e, f);
		} else if (entityType == EntityType.END_CRYSTAL) {
			entity = new EnderCrystalEntity(this.world, d, e, f);
		} else if (entityType == EntityType.ITEM) {
			entity = new ItemEntity(this.world, d, e, f);
		} else if (entityType == EntityType.FALLING_BLOCK) {
			entity = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(entitySpawnS2CPacket.getEntityData()));
		} else if (entityType == EntityType.AREA_EFFECT_CLOUD) {
			entity = new AreaEffectCloudEntity(this.world, d, e, f);
		} else {
			entity = null;
		}

		if (entity != null) {
			int i = entitySpawnS2CPacket.getId();
			entity.method_18003(d, e, f);
			entity.pitch = (float)(entitySpawnS2CPacket.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(entitySpawnS2CPacket.getYaw() * 360) / 256.0F;
			Entity[] entitys = entity.getParts();
			if (entitys != null) {
				int j = i - entity.getEntityId();

				for (Entity entity3 : entitys) {
					entity3.setEntityId(entity3.getEntityId() + j);
				}
			}

			entity.setEntityId(i);
			entity.setUuid(entitySpawnS2CPacket.getUuid());
			this.world.method_2942(i, entity);
			if (entity instanceof AbstractMinecartEntity) {
				this.client.getSoundLoader().play(new RidingMinecartSoundInstance((AbstractMinecartEntity)entity));
			}
		}
	}

	@Override
	public void method_11091(ExperienceOrbSpawnS2CPacket experienceOrbSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(experienceOrbSpawnS2CPacket, this, this.client);
		double d = experienceOrbSpawnS2CPacket.getX();
		double e = experienceOrbSpawnS2CPacket.getY();
		double f = experienceOrbSpawnS2CPacket.getZ();
		Entity entity = new ExperienceOrbEntity(this.world, d, e, f, experienceOrbSpawnS2CPacket.getExperience());
		entity.method_18003(d, e, f);
		entity.yaw = 0.0F;
		entity.pitch = 0.0F;
		entity.setEntityId(experienceOrbSpawnS2CPacket.getId());
		this.world.method_2942(experienceOrbSpawnS2CPacket.getId(), entity);
	}

	@Override
	public void method_11156(EntitySpawnGlobalS2CPacket entitySpawnGlobalS2CPacket) {
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
			this.world.method_18108(lightningEntity);
		}
	}

	@Override
	public void method_11114(PaintingSpawnS2CPacket paintingSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(paintingSpawnS2CPacket, this, this.client);
		PaintingEntity paintingEntity = new PaintingEntity(
			this.world, paintingSpawnS2CPacket.getPos(), paintingSpawnS2CPacket.getFacing(), paintingSpawnS2CPacket.getMotive()
		);
		paintingEntity.setEntityId(paintingSpawnS2CPacket.getId());
		paintingEntity.setUuid(paintingSpawnS2CPacket.getPaintingUuid());
		this.world.method_2942(paintingSpawnS2CPacket.getId(), paintingEntity);
	}

	@Override
	public void method_11132(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket) {
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
	public void method_11093(EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityTrackerUpdateS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityTrackerUpdateS2CPacket.id());
		if (entity != null && entityTrackerUpdateS2CPacket.getTrackedValues() != null) {
			entity.getDataTracker().method_12779(entityTrackerUpdateS2CPacket.getTrackedValues());
		}
	}

	@Override
	public void method_11097(PlayerSpawnS2CPacket playerSpawnS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnS2CPacket, this, this.client);
		double d = playerSpawnS2CPacket.getX();
		double e = playerSpawnS2CPacket.getY();
		double f = playerSpawnS2CPacket.getZ();
		float g = (float)(playerSpawnS2CPacket.getYaw() * 360) / 256.0F;
		float h = (float)(playerSpawnS2CPacket.getPitch() * 360) / 256.0F;
		int i = playerSpawnS2CPacket.getId();
		OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(
			this.client.world, this.method_2871(playerSpawnS2CPacket.getPlayerUuid()).getProfile()
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
		this.world.method_18107(i, otherClientPlayerEntity);
		List<DataTracker.Entry<?>> list = playerSpawnS2CPacket.getTrackedValues();
		if (list != null) {
			otherClientPlayerEntity.getDataTracker().method_12779(list);
		}
	}

	@Override
	public void method_11086(EntityPositionS2CPacket entityPositionS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPositionS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPositionS2CPacket.getId());
		if (entity != null) {
			double d = entityPositionS2CPacket.getX();
			double e = entityPositionS2CPacket.getY();
			double f = entityPositionS2CPacket.getZ();
			entity.method_18003(d, e, f);
			if (!entity.method_5787()) {
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
	public void method_11135(HeldItemChangeS2CPacket heldItemChangeS2CPacket) {
		NetworkThreadUtils.forceMainThread(heldItemChangeS2CPacket, this, this.client);
		if (PlayerInventory.isValidHotbarIndex(heldItemChangeS2CPacket.getSlot())) {
			this.client.player.inventory.selectedSlot = heldItemChangeS2CPacket.getSlot();
		}
	}

	@Override
	public void method_11155(EntityS2CPacket entityS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityS2CPacket, this, this.client);
		Entity entity = entityS2CPacket.getEntity(this.world);
		if (entity != null) {
			entity.field_6001 = entity.field_6001 + (long)entityS2CPacket.getDeltaXShort();
			entity.field_6023 = entity.field_6023 + (long)entityS2CPacket.getDeltaYShort();
			entity.field_5954 = entity.field_5954 + (long)entityS2CPacket.getDeltaZShort();
			double d = (double)entity.field_6001 / 4096.0;
			double e = (double)entity.field_6023 / 4096.0;
			double f = (double)entity.field_5954 / 4096.0;
			if (!entity.method_5787()) {
				float g = entityS2CPacket.hasRotation() ? (float)(entityS2CPacket.getYaw() * 360) / 256.0F : entity.yaw;
				float h = entityS2CPacket.hasRotation() ? (float)(entityS2CPacket.getPitch() * 360) / 256.0F : entity.pitch;
				entity.setPositionAndRotations(d, e, f, g, h, 3, false);
				entity.onGround = entityS2CPacket.isOnGround();
			}
		}
	}

	@Override
	public void method_11139(EntitySetHeadYawS2CPacket entitySetHeadYawS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitySetHeadYawS2CPacket, this, this.client);
		Entity entity = entitySetHeadYawS2CPacket.getEntity(this.world);
		if (entity != null) {
			float f = (float)(entitySetHeadYawS2CPacket.getHeadYaw() * 360) / 256.0F;
			entity.method_5683(f, 3);
		}
	}

	@Override
	public void method_11095(EntitiesDestroyS2CPacket entitiesDestroyS2CPacket) {
		NetworkThreadUtils.forceMainThread(entitiesDestroyS2CPacket, this, this.client);

		for (int i = 0; i < entitiesDestroyS2CPacket.getEntityIds().length; i++) {
			this.world.method_2945(entitiesDestroyS2CPacket.getEntityIds()[i]);
		}
	}

	@Override
	public void method_11157(PlayerPositionLookS2CPacket playerPositionLookS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerPositionLookS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		double d = playerPositionLookS2CPacket.getX();
		double e = playerPositionLookS2CPacket.getY();
		double f = playerPositionLookS2CPacket.getZ();
		float g = playerPositionLookS2CPacket.getYaw();
		float h = playerPositionLookS2CPacket.getPitch();
		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X)) {
			playerEntity.prevRenderX += d;
			d += playerEntity.x;
		} else {
			playerEntity.prevRenderX = d;
			playerEntity.velocityX = 0.0;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y)) {
			playerEntity.prevRenderY += e;
			e += playerEntity.y;
		} else {
			playerEntity.prevRenderY = e;
			playerEntity.velocityY = 0.0;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Z)) {
			playerEntity.prevRenderZ += f;
			f += playerEntity.z;
		} else {
			playerEntity.prevRenderZ = f;
			playerEntity.velocityZ = 0.0;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
			h += playerEntity.pitch;
		}

		if (playerPositionLookS2CPacket.getFlags().contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			g += playerEntity.yaw;
		}

		playerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.connection.sendPacket(new TeleportConfirmC2SPacket(playerPositionLookS2CPacket.getTeleportId()));
		this.connection
			.sendPacket(
				new PlayerMoveServerMessage.Both(playerEntity.x, playerEntity.getBoundingBox().minY, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false)
			);
		if (!this.field_3698) {
			this.client.player.prevX = this.client.player.x;
			this.client.player.prevY = this.client.player.y;
			this.client.player.prevZ = this.client.player.z;
			this.field_3698 = true;
			this.client.openScreen(null);
		}
	}

	@Override
	public void method_11100(ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkDeltaUpdateS2CPacket, this, this.client);

		for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord chunkDeltaRecord : chunkDeltaUpdateS2CPacket.getRecords()) {
			this.world.method_2937(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
		}
	}

	@Override
	public void method_11128(ChunkDataS2CPacket chunkDataS2CPacket) {
		NetworkThreadUtils.forceMainThread(chunkDataS2CPacket, this, this.client);
		int i = chunkDataS2CPacket.getX();
		int j = chunkDataS2CPacket.getZ();
		WorldChunk worldChunk = this.world
			.getChunkProvider()
			.loadChunkFromPacket(
				this.world,
				i,
				j,
				chunkDataS2CPacket.getReadBuffer(),
				chunkDataS2CPacket.method_16123(),
				chunkDataS2CPacket.getVerticalStripBitmask(),
				chunkDataS2CPacket.isFullChunk()
			);
		if (worldChunk != null) {
			this.world.method_18115(worldChunk);
		}

		for (int k = 0; k < 16; k++) {
			this.world.method_18113(i, k, j);
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
	public void method_11107(UnloadChunkS2CPacket unloadChunkS2CPacket) {
		NetworkThreadUtils.forceMainThread(unloadChunkS2CPacket, this, this.client);
		int i = unloadChunkS2CPacket.getX();
		int j = unloadChunkS2CPacket.getZ();
		this.world.getChunkProvider().unload(i, j);

		for (int k = 0; k < 16; k++) {
			this.world.method_18113(i, k, j);
		}
	}

	@Override
	public void method_11136(BlockUpdateS2CPacket blockUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockUpdateS2CPacket, this, this.client);
		this.world.method_2937(blockUpdateS2CPacket.getPos(), blockUpdateS2CPacket.getState());
	}

	@Override
	public void method_11083(DisconnectS2CPacket disconnectS2CPacket) {
		this.connection.disconnect(disconnectS2CPacket.getReason());
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		this.client.method_18099();
		if (this.field_3701 != null) {
			if (this.field_3701 instanceof RealmsScreen) {
				this.client.openScreen(new DisconnectedRealmsScreen(((RealmsScreen)this.field_3701).getRealmsScreen(), "disconnect.lost", textComponent).getProxy());
			} else {
				this.client.openScreen(new DisconnectedScreen(this.field_3701, "disconnect.lost", textComponent));
			}
		} else {
			this.client.openScreen(new DisconnectedScreen(new MultiplayerScreen(new MainMenuScreen()), "disconnect.lost", textComponent));
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.connection.sendPacket(packet);
	}

	@Override
	public void method_11150(ItemPickupAnimationS2CPacket itemPickupAnimationS2CPacket) {
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
						SoundCategory.field_15248,
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
						SoundCategory.field_15248,
						0.2F,
						(this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F,
						false
					);
			}

			if (entity instanceof ItemEntity) {
				((ItemEntity)entity).getStack().setAmount(itemPickupAnimationS2CPacket.getStackAmount());
			}

			this.client.particleManager.addParticle(new ItemPickupParticle(this.world, entity, livingEntity, 0.5F));
			this.world.method_2945(itemPickupAnimationS2CPacket.getEntityId());
		}
	}

	@Override
	public void method_11121(ChatMessageS2CPacket chatMessageS2CPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageS2CPacket, this, this.client);
		this.client.inGameHud.addChatMessage(chatMessageS2CPacket.getLocation(), chatMessageS2CPacket.getMessage());
	}

	@Override
	public void method_11160(EntityAnimationS2CPacket entityAnimationS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAnimationS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAnimationS2CPacket.getId());
		if (entity != null) {
			if (entityAnimationS2CPacket.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN);
			} else if (entityAnimationS2CPacket.getAnimationId() == 3) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.OFF);
			} else if (entityAnimationS2CPacket.getAnimationId() == 1) {
				entity.method_5879();
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
	public void method_11137(PlayerUseBedS2CPacket playerUseBedS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerUseBedS2CPacket, this, this.client);
		playerUseBedS2CPacket.getPlayer(this.world).trySleep(playerUseBedS2CPacket.getBedHeadPos());
	}

	@Override
	public void method_11138(MobSpawnS2CPacket mobSpawnS2CPacket) {
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
			Entity[] entitys = livingEntity.getParts();
			if (entitys != null) {
				int i = mobSpawnS2CPacket.getId() - livingEntity.getEntityId();

				for (Entity entity : entitys) {
					entity.setEntityId(entity.getEntityId() + i);
				}
			}

			livingEntity.setEntityId(mobSpawnS2CPacket.getId());
			livingEntity.setUuid(mobSpawnS2CPacket.getUuid());
			livingEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
			livingEntity.velocityX = (double)((float)mobSpawnS2CPacket.getYaw() / 8000.0F);
			livingEntity.velocityY = (double)((float)mobSpawnS2CPacket.getPitch() / 8000.0F);
			livingEntity.velocityZ = (double)((float)mobSpawnS2CPacket.getHeadPitch() / 8000.0F);
			this.world.method_2942(mobSpawnS2CPacket.getId(), livingEntity);
			List<DataTracker.Entry<?>> list = mobSpawnS2CPacket.getTrackedValues();
			if (list != null) {
				livingEntity.getDataTracker().method_12779(list);
			}
		} else {
			LOGGER.warn("Skipping Entity with id {}", mobSpawnS2CPacket.getEntityTypeId());
		}
	}

	@Override
	public void method_11079(WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldTimeUpdateS2CPacket, this, this.client);
		this.client.world.setTime(worldTimeUpdateS2CPacket.getTime());
		this.client.world.setTimeOfDay(worldTimeUpdateS2CPacket.getTimeOfDay());
	}

	@Override
	public void method_11142(PlayerSpawnPositionS2CPacket playerSpawnPositionS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnPositionS2CPacket, this, this.client);
		this.client.player.setPlayerSpawn(playerSpawnPositionS2CPacket.getPos(), true);
		this.client.world.getLevelProperties().setSpawnPos(playerSpawnPositionS2CPacket.getPos());
	}

	@Override
	public void method_11080(EntityPassengersSetS2CPacket entityPassengersSetS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPassengersSetS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPassengersSetS2CPacket.getId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.method_5821(this.client.player);
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
	public void method_11110(EntityAttachS2CPacket entityAttachS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAttachS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttachS2CPacket.getAttachedEntityId());
		Entity entity2 = this.world.getEntityById(entityAttachS2CPacket.getHoldingEntityId());
		if (entity instanceof MobEntity) {
			if (entity2 != null) {
				((MobEntity)entity).attachLeash(entity2, false);
			} else {
				((MobEntity)entity).detachLeash(false, false);
			}
		}
	}

	@Override
	public void method_11148(EntityStatusS2CPacket entityStatusS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityStatusS2CPacket, this, this.client);
		Entity entity = entityStatusS2CPacket.getEntity(this.world);
		if (entity != null) {
			if (entityStatusS2CPacket.getStatus() == 21) {
				this.client.getSoundLoader().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else if (entityStatusS2CPacket.getStatus() == 35) {
				int i = 40;
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11220, 30);
				this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.field_14931, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.gameRenderer.showFloatingItem(new ItemStack(Items.field_8288));
				}
			} else {
				entity.method_5711(entityStatusS2CPacket.getStatus());
			}
		}
	}

	@Override
	public void method_11122(HealthUpdateS2CPacket healthUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(healthUpdateS2CPacket, this, this.client);
		this.client.player.updateHealth(healthUpdateS2CPacket.getHealth());
		this.client.player.getHungerManager().setFoodLevel(healthUpdateS2CPacket.getFood());
		this.client.player.getHungerManager().setSaturationLevelClient(healthUpdateS2CPacket.getSaturation());
	}

	@Override
	public void method_11101(ExperienceBarUpdateS2CPacket experienceBarUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(experienceBarUpdateS2CPacket, this, this.client);
		this.client
			.player
			.method_3145(experienceBarUpdateS2CPacket.getBarProgress(), experienceBarUpdateS2CPacket.getExperienceLevel(), experienceBarUpdateS2CPacket.getExperience());
	}

	@Override
	public void method_11117(PlayerRespawnS2CPacket playerRespawnS2CPacket) {
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
				playerRespawnS2CPacket.getDifficulty(),
				this.client.getProfiler(),
				this.client.worldRenderer
			);
			this.world.setScoreboard(scoreboard);
			this.client.method_1481(this.world);
			this.client.openScreen(new DownloadingTerrainScreen());
		}

		this.world.setDefaultSpawnClient();
		this.world.method_2936();
		String string = clientPlayerEntity.getServerBrand();
		this.client.cameraEntity = null;
		ClientPlayerEntity clientPlayerEntity2 = this.client
			.interactionManager
			.createPlayer(this.world, clientPlayerEntity.getStats(), clientPlayerEntity.getRecipeBook());
		clientPlayerEntity2.setEntityId(i);
		clientPlayerEntity2.dimension = dimensionType;
		this.client.player = clientPlayerEntity2;
		this.client.cameraEntity = clientPlayerEntity2;
		clientPlayerEntity2.getDataTracker().method_12779(clientPlayerEntity.getDataTracker().getAllEntries());
		clientPlayerEntity2.method_5823();
		clientPlayerEntity2.setServerBrand(string);
		this.world.method_18107(i, clientPlayerEntity2);
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
	public void method_11124(ExplosionS2CPacket explosionS2CPacket) {
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
		this.client.player.velocityX = this.client.player.velocityX + (double)explosionS2CPacket.getPlayerVelocityX();
		this.client.player.velocityY = this.client.player.velocityY + (double)explosionS2CPacket.getPlayerVelocityY();
		this.client.player.velocityZ = this.client.player.velocityZ + (double)explosionS2CPacket.getPlayerVelocityZ();
	}

	@Override
	public void method_11089(GuiOpenS2CPacket guiOpenS2CPacket) {
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
	public void method_11109(GuiSlotUpdateS2CPacket guiSlotUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiSlotUpdateS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = guiSlotUpdateS2CPacket.getItemStack();
		int i = guiSlotUpdateS2CPacket.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		if (guiSlotUpdateS2CPacket.getId() == -1) {
			playerEntity.inventory.setCursorStack(itemStack);
		} else if (guiSlotUpdateS2CPacket.getId() == -2) {
			playerEntity.inventory.setInvStack(i, itemStack);
		} else {
			boolean bl = false;
			if (this.client.currentScreen instanceof CreativePlayerInventoryScreen) {
				CreativePlayerInventoryScreen creativePlayerInventoryScreen = (CreativePlayerInventoryScreen)this.client.currentScreen;
				bl = creativePlayerInventoryScreen.method_2469() != ItemGroup.INVENTORY.getId();
			}

			if (guiSlotUpdateS2CPacket.getId() == 0 && guiSlotUpdateS2CPacket.getSlot() >= 36 && i < 45) {
				if (!itemStack.isEmpty()) {
					ItemStack itemStack2 = playerEntity.containerPlayer.getSlot(i).getStack();
					if (itemStack2.isEmpty() || itemStack2.getAmount() < itemStack.getAmount()) {
						itemStack.setUpdateCooldown(5);
					}
				}

				playerEntity.containerPlayer.setStackInSlot(i, itemStack);
			} else if (guiSlotUpdateS2CPacket.getId() == playerEntity.container.syncId && (guiSlotUpdateS2CPacket.getId() != 0 || !bl)) {
				playerEntity.container.setStackInSlot(i, itemStack);
			}
		}
	}

	@Override
	public void method_11123(ConfirmGuiActionS2CPacket confirmGuiActionS2CPacket) {
		NetworkThreadUtils.forceMainThread(confirmGuiActionS2CPacket, this, this.client);
		Container container = null;
		PlayerEntity playerEntity = this.client.player;
		if (confirmGuiActionS2CPacket.getId() == 0) {
			container = playerEntity.containerPlayer;
		} else if (confirmGuiActionS2CPacket.getId() == playerEntity.container.syncId) {
			container = playerEntity.container;
		}

		if (container != null && !confirmGuiActionS2CPacket.wasAccepted()) {
			this.sendPacket(new GuiActionConfirmC2SPacket(confirmGuiActionS2CPacket.getId(), confirmGuiActionS2CPacket.getActionId(), true));
		}
	}

	@Override
	public void method_11153(InventoryS2CPacket inventoryS2CPacket) {
		NetworkThreadUtils.forceMainThread(inventoryS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (inventoryS2CPacket.getGuiId() == 0) {
			playerEntity.containerPlayer.updateSlotStacks(inventoryS2CPacket.getSlotStacks());
		} else if (inventoryS2CPacket.getGuiId() == playerEntity.container.syncId) {
			playerEntity.container.updateSlotStacks(inventoryS2CPacket.getSlotStacks());
		}
	}

	@Override
	public void method_11108(SignEditorOpenS2CPacket signEditorOpenS2CPacket) {
		NetworkThreadUtils.forceMainThread(signEditorOpenS2CPacket, this, this.client);
		BlockEntity blockEntity = this.world.getBlockEntity(signEditorOpenS2CPacket.getPos());
		if (!(blockEntity instanceof SignBlockEntity)) {
			blockEntity = new SignBlockEntity();
			blockEntity.setWorld(this.world);
			blockEntity.setPos(signEditorOpenS2CPacket.getPos());
		}

		this.client.player.openSignEditorGui((SignBlockEntity)blockEntity);
	}

	@Override
	public void method_11094(BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket) {
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
	public void method_11131(GuiUpdateS2CPacket guiUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiUpdateS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.container != null && playerEntity.container.syncId == guiUpdateS2CPacket.getId()) {
			playerEntity.container.setProperty(guiUpdateS2CPacket.getPropertyId(), guiUpdateS2CPacket.getValue());
		}
	}

	@Override
	public void method_11151(EntityEquipmentUpdateS2CPacket entityEquipmentUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityEquipmentUpdateS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityEquipmentUpdateS2CPacket.getId());
		if (entity != null) {
			entity.setEquippedStack(entityEquipmentUpdateS2CPacket.getSlot(), entityEquipmentUpdateS2CPacket.getStack());
		}
	}

	@Override
	public void method_11102(GuiCloseS2CPacket guiCloseS2CPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseS2CPacket, this, this.client);
		this.client.player.method_3137();
	}

	@Override
	public void method_11158(BlockActionS2CPacket blockActionS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockActionS2CPacket, this, this.client);
		this.client
			.world
			.addBlockAction(blockActionS2CPacket.getPos(), blockActionS2CPacket.getBlock(), blockActionS2CPacket.getType(), blockActionS2CPacket.getData());
	}

	@Override
	public void method_11116(BlockBreakingProgressS2CPacket blockBreakingProgressS2CPacket) {
		NetworkThreadUtils.forceMainThread(blockBreakingProgressS2CPacket, this, this.client);
		this.client
			.world
			.setBlockBreakingProgress(
				blockBreakingProgressS2CPacket.getEntityId(), blockBreakingProgressS2CPacket.getPos(), blockBreakingProgressS2CPacket.getProgress()
			);
	}

	@Override
	public void method_11085(GameStateChangeS2CPacket gameStateChangeS2CPacket) {
		NetworkThreadUtils.forceMainThread(gameStateChangeS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		int i = gameStateChangeS2CPacket.getReason();
		float f = gameStateChangeS2CPacket.getValue();
		int j = MathHelper.floor(f + 0.5F);
		if (i >= 0 && i < GameStateChangeS2CPacket.REASON_MESSAGES.length && GameStateChangeS2CPacket.REASON_MESSAGES[i] != null) {
			playerEntity.addChatMessage(new TranslatableTextComponent(GameStateChangeS2CPacket.REASON_MESSAGES[i]), false);
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
					.getHudChat()
					.addMessage(
						new TranslatableTextComponent(
							"demo.help.movement",
							gameOptions.keyForward.getLocalizedName(),
							gameOptions.keyLeft.getLocalizedName(),
							gameOptions.keyBack.getLocalizedName(),
							gameOptions.keyRight.getLocalizedName()
						)
					);
			} else if (f == 102.0F) {
				this.client.inGameHud.getHudChat().addMessage(new TranslatableTextComponent("demo.help.jump", gameOptions.keyJump.getLocalizedName()));
			} else if (f == 103.0F) {
				this.client.inGameHud.getHudChat().addMessage(new TranslatableTextComponent("demo.help.inventory", gameOptions.keyInventory.getLocalizedName()));
			} else if (f == 104.0F) {
				this.client.inGameHud.getHudChat().addMessage(new TranslatableTextComponent("demo.day.6", gameOptions.keyScreenshot.getLocalizedName()));
			}
		} else if (i == 6) {
			this.world
				.playSound(
					playerEntity,
					playerEntity.x,
					playerEntity.y + (double)playerEntity.getEyeHeight(),
					playerEntity.z,
					SoundEvents.field_15224,
					SoundCategory.field_15248,
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
	public void method_11088(MapUpdateS2CPacket mapUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(mapUpdateS2CPacket, this, this.client);
		MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
		String string = FilledMapItem.method_17440(mapUpdateS2CPacket.getId());
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
	public void method_11098(WorldEventS2CPacket worldEventS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldEventS2CPacket, this, this.client);
		if (worldEventS2CPacket.isGlobal()) {
			this.client.world.playGlobalEvent(worldEventS2CPacket.getEventId(), worldEventS2CPacket.getPos(), worldEventS2CPacket.getEffectData());
		} else {
			this.client.world.playEvent(worldEventS2CPacket.getEventId(), worldEventS2CPacket.getPos(), worldEventS2CPacket.getEffectData());
		}
	}

	@Override
	public void method_11130(AdvancementUpdateS2CPacket advancementUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(advancementUpdateS2CPacket, this, this.client);
		this.advancementHandler.method_2861(advancementUpdateS2CPacket);
	}

	@Override
	public void method_11161(SelectAdvancementTabS2CPacket selectAdvancementTabS2CPacket) {
		NetworkThreadUtils.forceMainThread(selectAdvancementTabS2CPacket, this, this.client);
		Identifier identifier = selectAdvancementTabS2CPacket.getTabId();
		if (identifier == null) {
			this.advancementHandler.selectTab(null, false);
		} else {
			SimpleAdvancement simpleAdvancement = this.advancementHandler.getManager().get(identifier);
			this.advancementHandler.selectTab(simpleAdvancement, false);
		}
	}

	@Override
	public void method_11145(CommandTreeS2CPacket commandTreeS2CPacket) {
		NetworkThreadUtils.forceMainThread(commandTreeS2CPacket, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(commandTreeS2CPacket.method_11403());
	}

	@Override
	public void method_11082(StopSoundS2CPacket stopSoundS2CPacket) {
		NetworkThreadUtils.forceMainThread(stopSoundS2CPacket, this, this.client);
		this.client.getSoundLoader().stopSounds(stopSoundS2CPacket.getSoundId(), stopSoundS2CPacket.getCategory());
	}

	@Override
	public void method_11081(CommandSuggestionsS2CPacket commandSuggestionsS2CPacket) {
		NetworkThreadUtils.forceMainThread(commandSuggestionsS2CPacket, this, this.client);
		this.commandSource.method_2931(commandSuggestionsS2CPacket.method_11399(), commandSuggestionsS2CPacket.getSuggestions());
	}

	@Override
	public void method_11106(SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket) {
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
	public void method_11092(LookAtS2CPacket lookAtS2CPacket) {
		NetworkThreadUtils.forceMainThread(lookAtS2CPacket, this, this.client);
		Vec3d vec3d = lookAtS2CPacket.getTargetPosition(this.world);
		if (vec3d != null) {
			this.client.player.lookAt(lookAtS2CPacket.getSelfAnchor(), vec3d);
		}
	}

	@Override
	public void method_11127(TagQueryResponseS2CPacket tagQueryResponseS2CPacket) {
		NetworkThreadUtils.forceMainThread(tagQueryResponseS2CPacket, this, this.client);
		if (!this.field_3692.method_1404(tagQueryResponseS2CPacket.getTransactionId(), tagQueryResponseS2CPacket.getTag())) {
			LOGGER.debug("Got unhandled response to tag query {}", tagQueryResponseS2CPacket.getTransactionId());
		}
	}

	@Override
	public void method_11129(StatisticsS2CPacket statisticsS2CPacket) {
		NetworkThreadUtils.forceMainThread(statisticsS2CPacket, this, this.client);

		for (Entry<Stat<?>, Integer> entry : statisticsS2CPacket.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStats().setStat(this.client.player, stat, i);
		}

		if (this.client.currentScreen instanceof class_452) {
			((class_452)this.client.currentScreen).method_2300();
		}
	}

	@Override
	public void method_11115(UnlockRecipesS2CPacket unlockRecipesS2CPacket) {
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
						RecipeToast.method_1985(this.client.getToastManager(), recipe);
					});
				}
		}

		clientRecipeBook.getOrderedResults().forEach(recipeResultCollection -> recipeResultCollection.initialize(clientRecipeBook));
		if (this.client.currentScreen instanceof RecipeBookProvider) {
			((RecipeBookProvider)this.client.currentScreen).refreshRecipeBook();
		}
	}

	@Override
	public void method_11084(EntityPotionEffectS2CPacket entityPotionEffectS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityPotionEffectS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPotionEffectS2CPacket.method_11943());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = StatusEffect.byRawId(entityPotionEffectS2CPacket.method_11946());
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect,
					entityPotionEffectS2CPacket.method_11944(),
					entityPotionEffectS2CPacket.method_11945(),
					entityPotionEffectS2CPacket.method_11950(),
					entityPotionEffectS2CPacket.method_11949(),
					entityPotionEffectS2CPacket.method_11942()
				);
				statusEffectInstance.setPermanent(entityPotionEffectS2CPacket.method_11947());
				((LivingEntity)entity).addPotionEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public void method_11126(SynchronizeTagsS2CPacket synchronizeTagsS2CPacket) {
		NetworkThreadUtils.forceMainThread(synchronizeTagsS2CPacket, this, this.client);
		this.tagManager = synchronizeTagsS2CPacket.getTagManager();
		if (!this.connection.isLocal()) {
			BlockTags.setContainer(this.tagManager.blocks());
			ItemTags.setContainer(this.tagManager.items());
			FluidTags.setContainer(this.tagManager.fluids());
			EntityTags.setContainer(this.tagManager.entities());
		}

		this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
	}

	@Override
	public void method_11133(CombatEventS2CPacket combatEventS2CPacket) {
		NetworkThreadUtils.forceMainThread(combatEventS2CPacket, this, this.client);
		if (combatEventS2CPacket.type == CombatEventS2CPacket.Type.DEATH) {
			Entity entity = this.world.getEntityById(combatEventS2CPacket.entityId);
			if (entity == this.client.player) {
				this.client.openScreen(new DeathScreen(combatEventS2CPacket.deathMessage));
			}
		}
	}

	@Override
	public void method_11140(DifficultyS2CPacket difficultyS2CPacket) {
		NetworkThreadUtils.forceMainThread(difficultyS2CPacket, this, this.client);
		this.client.world.getLevelProperties().setDifficulty(difficultyS2CPacket.getDifficulty());
		this.client.world.getLevelProperties().setDifficultyLocked(difficultyS2CPacket.method_11340());
	}

	@Override
	public void method_11111(SetCameraEntityS2CPacket setCameraEntityS2CPacket) {
		NetworkThreadUtils.forceMainThread(setCameraEntityS2CPacket, this, this.client);
		Entity entity = setCameraEntityS2CPacket.getEntity(this.world);
		if (entity != null) {
			this.client.setCameraEntity(entity);
		}
	}

	@Override
	public void method_11096(WorldBorderS2CPacket worldBorderS2CPacket) {
		NetworkThreadUtils.forceMainThread(worldBorderS2CPacket, this, this.client);
		worldBorderS2CPacket.apply(this.world.getWorldBorder());
	}

	@Override
	public void method_11103(TitleS2CPacket titleS2CPacket) {
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
			case RESET:
				this.client.inGameHud.method_1763("", "", -1, -1, -1);
				this.client.inGameHud.method_1742();
				return;
		}

		this.client.inGameHud.method_1763(string, string2, titleS2CPacket.getTicksFadeIn(), titleS2CPacket.getTicksDisplay(), titleS2CPacket.getTicksFadeOut());
	}

	@Override
	public void method_11105(PlayerListHeaderS2CPacket playerListHeaderS2CPacket) {
		this.client
			.inGameHud
			.getScoreboardWidget()
			.method_1925(playerListHeaderS2CPacket.getHeader().getFormattedText().isEmpty() ? null : playerListHeaderS2CPacket.getHeader());
		this.client
			.inGameHud
			.getScoreboardWidget()
			.method_1924(playerListHeaderS2CPacket.getFooter().getFormattedText().isEmpty() ? null : playerListHeaderS2CPacket.getFooter());
	}

	@Override
	public void method_11119(RemoveEntityEffectS2CPacket removeEntityEffectS2CPacket) {
		NetworkThreadUtils.forceMainThread(removeEntityEffectS2CPacket, this, this.client);
		Entity entity = removeEntityEffectS2CPacket.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).removePotionEffect(removeEntityEffectS2CPacket.getEffectType());
		}
	}

	@Override
	public void method_11113(PlayerListS2CPacket playerListS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerListS2CPacket, this, this.client);

		for (PlayerListS2CPacket.class_2705 lv : playerListS2CPacket.method_11722()) {
			if (playerListS2CPacket.getType() == PlayerListS2CPacket.Type.REMOVE) {
				this.field_3693.remove(lv.method_11726().getId());
			} else {
				ScoreboardEntry scoreboardEntry = (ScoreboardEntry)this.field_3693.get(lv.method_11726().getId());
				if (playerListS2CPacket.getType() == PlayerListS2CPacket.Type.ADD) {
					scoreboardEntry = new ScoreboardEntry(lv);
					this.field_3693.put(scoreboardEntry.getProfile().getId(), scoreboardEntry);
				}

				if (scoreboardEntry != null) {
					switch (playerListS2CPacket.getType()) {
						case ADD:
							scoreboardEntry.setGameMode(lv.method_11725());
							scoreboardEntry.setLatency(lv.method_11727());
							scoreboardEntry.setDisplayName(lv.method_11724());
							break;
						case UPDATE_GAMEMODE:
							scoreboardEntry.setGameMode(lv.method_11725());
							break;
						case UPDATE_LATENCY:
							scoreboardEntry.setLatency(lv.method_11727());
							break;
						case UPDATE_DISPLAY_NAME:
							scoreboardEntry.setDisplayName(lv.method_11724());
					}
				}
			}
		}
	}

	@Override
	public void method_11147(KeepAliveS2CPacket keepAliveS2CPacket) {
		this.sendPacket(new KeepAliveC2SPacket(keepAliveS2CPacket.method_11517()));
	}

	@Override
	public void method_11154(PlayerAbilitiesS2CPacket playerAbilitiesS2CPacket) {
		NetworkThreadUtils.forceMainThread(playerAbilitiesS2CPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.abilities.flying = playerAbilitiesS2CPacket.isFlying();
		playerEntity.abilities.creativeMode = playerAbilitiesS2CPacket.isCreativeMode();
		playerEntity.abilities.invulnerable = playerAbilitiesS2CPacket.isInvulnerable();
		playerEntity.abilities.allowFlying = playerAbilitiesS2CPacket.allowFlying();
		playerEntity.abilities.setFlySpeed((double)playerAbilitiesS2CPacket.getFlySpeed());
		playerEntity.abilities.setWalkSpeed(playerAbilitiesS2CPacket.getFovModifier());
	}

	@Override
	public void method_11146(PlaySoundS2CPacket playSoundS2CPacket) {
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
	public void method_11125(PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket) {
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
	public void method_11104(PlaySoundIdS2CPacket playSoundIdS2CPacket) {
		NetworkThreadUtils.forceMainThread(playSoundIdS2CPacket, this, this.client);
		this.client
			.getSoundLoader()
			.play(
				new PositionedSoundInstance(
					playSoundIdS2CPacket.getSoundId(),
					playSoundIdS2CPacket.getCategory(),
					playSoundIdS2CPacket.getVolume(),
					playSoundIdS2CPacket.getPitch(),
					false,
					0,
					SoundInstance.AttenuationType.LINEAR,
					(float)playSoundIdS2CPacket.getX(),
					(float)playSoundIdS2CPacket.getY(),
					(float)playSoundIdS2CPacket.getZ()
				)
			);
	}

	@Override
	public void method_11141(ResourcePackSendS2CPacket resourcePackSendS2CPacket) {
		String string = resourcePackSendS2CPacket.getURL();
		String string2 = resourcePackSendS2CPacket.getSHA1();
		if (this.validateResourcePackUrl(string)) {
			if (string.startsWith("level://")) {
				try {
					String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
					File file = new File(this.client.runDirectory, "saves");
					File file2 = new File(file, string3);
					if (file2.isFile()) {
						this.method_2873(ResourcePackStatusC2SPacket.Status.field_13016);
						CompletableFuture<?> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.method_2873(ResourcePackStatusC2SPacket.Status.field_13015);
			} else {
				ServerEntry serverEntry = this.client.getCurrentServerEntry();
				if (serverEntry != null && serverEntry.getResourcePack() == ServerEntry.ResourcePackState.ENABLED) {
					this.method_2873(ResourcePackStatusC2SPacket.Status.field_13016);
					this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
				} else if (serverEntry != null && serverEntry.getResourcePack() != ServerEntry.ResourcePackState.PROMPT) {
					this.method_2873(ResourcePackStatusC2SPacket.Status.field_13018);
				} else {
					this.client.execute(() -> this.client.openScreen(new YesNoScreen((bl, i) -> {
							this.client = MinecraftClient.getInstance();
							ServerEntry serverEntryx = this.client.getCurrentServerEntry();
							if (bl) {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.ENABLED);
								}

								this.method_2873(ResourcePackStatusC2SPacket.Status.field_13016);
								this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
							} else {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.DISABLED);
								}

								this.method_2873(ResourcePackStatusC2SPacket.Status.field_13018);
							}

							ServerList.updateServerListEntry(serverEntryx);
							this.client.openScreen(null);
						}, I18n.translate("multiplayer.texturePrompt.line1"), I18n.translate("multiplayer.texturePrompt.line2"), 0)));
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
			this.method_2873(ResourcePackStatusC2SPacket.Status.field_13015);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.method_2873(ResourcePackStatusC2SPacket.Status.field_13017)).exceptionally(throwable -> {
			this.method_2873(ResourcePackStatusC2SPacket.Status.field_13015);
			return null;
		});
	}

	private void method_2873(ResourcePackStatusC2SPacket.Status status) {
		this.connection.sendPacket(new ResourcePackStatusC2SPacket(status));
	}

	@Override
	public void method_11078(BossBarS2CPacket bossBarS2CPacket) {
		NetworkThreadUtils.forceMainThread(bossBarS2CPacket, this, this.client);
		this.client.inGameHud.getHudBossBar().method_1795(bossBarS2CPacket);
	}

	@Override
	public void method_11087(CooldownUpdateS2CPacket cooldownUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(cooldownUpdateS2CPacket, this, this.client);
		if (cooldownUpdateS2CPacket.getCooldown() == 0) {
			this.client.player.getItemCooldownManager().remove(cooldownUpdateS2CPacket.getItem());
		} else {
			this.client.player.getItemCooldownManager().set(cooldownUpdateS2CPacket.getItem(), cooldownUpdateS2CPacket.getCooldown());
		}
	}

	@Override
	public void method_11134(VehicleMoveS2CPacket vehicleMoveS2CPacket) {
		NetworkThreadUtils.forceMainThread(vehicleMoveS2CPacket, this, this.client);
		Entity entity = this.client.player.getTopmostRiddenEntity();
		if (entity != this.client.player && entity.method_5787()) {
			entity.setPositionAnglesAndUpdate(
				vehicleMoveS2CPacket.getX(), vehicleMoveS2CPacket.getY(), vehicleMoveS2CPacket.getZ(), vehicleMoveS2CPacket.getYaw(), vehicleMoveS2CPacket.getPitch()
			);
			this.connection.sendPacket(new VehicleMoveC2SPacket(entity));
		}
	}

	@Override
	public void method_17186(OpenWrittenBookS2CPacket openWrittenBookS2CPacket) {
		NetworkThreadUtils.forceMainThread(openWrittenBookS2CPacket, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(openWrittenBookS2CPacket.getHand());
		if (itemStack.getItem() == Items.field_8360) {
			this.client.openScreen(new WrittenBookScreen(new WrittenBookScreen.class_3933(itemStack)));
		}
	}

	@Override
	public void method_11152(CustomPayloadS2CPacket customPayloadS2CPacket) {
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
				Path path = Path.method_34(packetByteBuf);
				this.client.debugRenderer.pathfindingDebugRenderer.method_3869(i, path, f);
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
				LOGGER.warn("Unknown custom packed identifier: {}", identifier);
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
	public void method_11144(ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardObjectiveUpdateS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardObjectiveUpdateS2CPacket.getName();
		if (scoreboardObjectiveUpdateS2CPacket.getMode() == 0) {
			scoreboard.method_1168(string, ScoreboardCriterion.DUMMY, scoreboardObjectiveUpdateS2CPacket.getDisplayName(), scoreboardObjectiveUpdateS2CPacket.getType());
		} else if (scoreboard.containsObjective(string)) {
			ScoreboardObjective scoreboardObjective = scoreboard.method_1170(string);
			if (scoreboardObjectiveUpdateS2CPacket.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (scoreboardObjectiveUpdateS2CPacket.getMode() == 2) {
				scoreboardObjective.setCriterionType(scoreboardObjectiveUpdateS2CPacket.getType());
				scoreboardObjective.method_1121(scoreboardObjectiveUpdateS2CPacket.getDisplayName());
			}
		}
	}

	@Override
	public void method_11118(ScoreboardPlayerUpdateS2CPacket scoreboardPlayerUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardPlayerUpdateS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardPlayerUpdateS2CPacket.getObjectiveName();
		switch (scoreboardPlayerUpdateS2CPacket.getUpdateMode()) {
			case field_13431:
				ScoreboardObjective scoreboardObjective = scoreboard.method_1165(string);
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(scoreboardPlayerUpdateS2CPacket.getPlayerName(), scoreboardObjective);
				scoreboardPlayerScore.setScore(scoreboardPlayerUpdateS2CPacket.getScore());
				break;
			case field_13430:
				scoreboard.resetPlayerScore(scoreboardPlayerUpdateS2CPacket.getPlayerName(), scoreboard.method_1170(string));
		}
	}

	@Override
	public void method_11159(ScoreboardDisplayS2CPacket scoreboardDisplayS2CPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardDisplayS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardDisplayS2CPacket.method_11804();
		ScoreboardObjective scoreboardObjective = string == null ? null : scoreboard.method_1165(string);
		scoreboard.setObjectiveSlot(scoreboardDisplayS2CPacket.getLocation(), scoreboardObjective);
	}

	@Override
	public void method_11099(TeamS2CPacket teamS2CPacket) {
		NetworkThreadUtils.forceMainThread(teamS2CPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		ScoreboardTeam scoreboardTeam;
		if (teamS2CPacket.getMode() == 0) {
			scoreboardTeam = scoreboard.addTeam(teamS2CPacket.getTeamName());
		} else {
			scoreboardTeam = scoreboard.getTeam(teamS2CPacket.getTeamName());
		}

		if (teamS2CPacket.getMode() == 0 || teamS2CPacket.getMode() == 2) {
			scoreboardTeam.setDisplayName(teamS2CPacket.getDisplayName());
			scoreboardTeam.setColor(teamS2CPacket.getPlayerPrefix());
			scoreboardTeam.setFriendlyFlagsBitwise(teamS2CPacket.getFlags());
			AbstractScoreboardTeam.VisibilityRule visibilityRule = AbstractScoreboardTeam.VisibilityRule.method_1213(teamS2CPacket.getNameTagVisibilityRule());
			if (visibilityRule != null) {
				scoreboardTeam.setNameTagVisibilityRule(visibilityRule);
			}

			AbstractScoreboardTeam.CollisionRule collisionRule = AbstractScoreboardTeam.CollisionRule.method_1210(teamS2CPacket.getCollisionRule());
			if (collisionRule != null) {
				scoreboardTeam.setCollisionRule(collisionRule);
			}

			scoreboardTeam.setPrefix(teamS2CPacket.method_11856());
			scoreboardTeam.setSuffix(teamS2CPacket.method_11854());
		}

		if (teamS2CPacket.getMode() == 0 || teamS2CPacket.getMode() == 3) {
			for (String string : teamS2CPacket.getPlayerList()) {
				scoreboard.addPlayerToTeam(string, scoreboardTeam);
			}
		}

		if (teamS2CPacket.getMode() == 4) {
			for (String string : teamS2CPacket.getPlayerList()) {
				scoreboard.removePlayerFromTeam(string, scoreboardTeam);
			}
		}

		if (teamS2CPacket.getMode() == 1) {
			scoreboard.removeTeam(scoreboardTeam);
		}
	}

	@Override
	public void method_11077(ParticleS2CPacket particleS2CPacket) {
		NetworkThreadUtils.forceMainThread(particleS2CPacket, this, this.client);
		if (particleS2CPacket.getParticleCount() == 0) {
			double d = (double)(particleS2CPacket.method_11543() * particleS2CPacket.getOffsetX());
			double e = (double)(particleS2CPacket.method_11543() * particleS2CPacket.getOffsetY());
			double f = (double)(particleS2CPacket.method_11543() * particleS2CPacket.getOffsetZ());

			try {
				this.world
					.addParticle(
						particleS2CPacket.method_11551(),
						particleS2CPacket.isLongDistance(),
						particleS2CPacket.getX(),
						particleS2CPacket.getY(),
						particleS2CPacket.getZ(),
						d,
						e,
						f
					);
			} catch (Throwable var17) {
				LOGGER.warn("Could not spawn particle effect {}", particleS2CPacket.method_11551());
			}
		} else {
			for (int i = 0; i < particleS2CPacket.getParticleCount(); i++) {
				double g = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetX();
				double h = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetY();
				double j = this.random.nextGaussian() * (double)particleS2CPacket.getOffsetZ();
				double k = this.random.nextGaussian() * (double)particleS2CPacket.method_11543();
				double l = this.random.nextGaussian() * (double)particleS2CPacket.method_11543();
				double m = this.random.nextGaussian() * (double)particleS2CPacket.method_11543();

				try {
					this.world
						.addParticle(
							particleS2CPacket.method_11551(),
							particleS2CPacket.isLongDistance(),
							particleS2CPacket.getX() + g,
							particleS2CPacket.getY() + h,
							particleS2CPacket.getZ() + j,
							k,
							l,
							m
						);
				} catch (Throwable var16) {
					LOGGER.warn("Could not spawn particle effect {}", particleS2CPacket.method_11551());
					return;
				}
			}
		}
	}

	@Override
	public void method_11149(EntityAttributesS2CPacket entityAttributesS2CPacket) {
		NetworkThreadUtils.forceMainThread(entityAttributesS2CPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttributesS2CPacket.method_11937());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributeContainer();

				for (EntityAttributesS2CPacket.Entry entry : entityAttributesS2CPacket.getEntries()) {
					EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.method_11940());
					if (entityAttributeInstance == null) {
						entityAttributeInstance = abstractEntityAttributeContainer.register(
							new ClampedEntityAttribute(null, entry.method_11940(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE)
						);
					}

					entityAttributeInstance.setBaseValue(entry.method_11941());
					entityAttributeInstance.clearModifiers();

					for (EntityAttributeModifier entityAttributeModifier : entry.method_11939()) {
						entityAttributeInstance.addModifier(entityAttributeModifier);
					}
				}
			}
		}
	}

	@Override
	public void method_11090(CraftResponseS2CPacket craftResponseS2CPacket) {
		NetworkThreadUtils.forceMainThread(craftResponseS2CPacket, this, this.client);
		Container container = this.client.player.container;
		if (container.syncId == craftResponseS2CPacket.getSyncId() && container.isRestricted(this.client.player)) {
			this.recipeManager.get(craftResponseS2CPacket.getRecipeId()).ifPresent(recipe -> {
				if (this.client.currentScreen instanceof RecipeBookProvider) {
					RecipeBookGui recipeBookGui = ((RecipeBookProvider)this.client.currentScreen).getRecipeBookGui();
					recipeBookGui.showGhostRecipe(recipe, container.slotList);
				}
			});
		}
	}

	@Override
	public void method_11143(LightUpdateS2CPacket lightUpdateS2CPacket) {
		NetworkThreadUtils.forceMainThread(lightUpdateS2CPacket, this, this.client);
		int i = lightUpdateS2CPacket.method_11558();
		int j = lightUpdateS2CPacket.method_11554();
		LightingProvider lightingProvider = this.world.getChunkProvider().getLightingProvider();
		int k = lightUpdateS2CPacket.method_11556();
		int l = lightUpdateS2CPacket.method_16124();
		Iterator<byte[]> iterator = lightUpdateS2CPacket.method_11555().iterator();
		this.method_2870(i, j, lightingProvider, LightType.SKY_LIGHT, k, l, iterator);
		int m = lightUpdateS2CPacket.method_11559();
		int n = lightUpdateS2CPacket.method_16125();
		Iterator<byte[]> iterator2 = lightUpdateS2CPacket.method_11557().iterator();
		this.method_2870(i, j, lightingProvider, LightType.BLOCK_LIGHT, m, n, iterator2);
	}

	@Override
	public void onSetVillagerRecipes(SetVillagerRecipesPacket setVillagerRecipesPacket) {
		NetworkThreadUtils.forceMainThread(setVillagerRecipesPacket, this, this.client);
		Container container = this.client.player.container;
		if (setVillagerRecipesPacket.getSyncId() == container.syncId && container instanceof MerchantContainer) {
			((MerchantContainer)container).method_17437(setVillagerRecipesPacket.getRecipes());
		}
	}

	private void method_2870(int i, int j, LightingProvider lightingProvider, LightType lightType, int k, int l, Iterator<byte[]> iterator) {
		for (int m = 0; m < 18; m++) {
			int n = -1 + m;
			boolean bl = (k & 1 << m) != 0;
			boolean bl2 = (l & 1 << m) != 0;
			if (bl || bl2) {
				lightingProvider.setSection(lightType, i, n, j, bl ? new ChunkNibbleArray((byte[])((byte[])iterator.next()).clone()) : new ChunkNibbleArray());
				this.world.method_18113(i, n, j);
			}
		}
	}

	public ClientConnection getClientConnection() {
		return this.connection;
	}

	public Collection<ScoreboardEntry> method_2880() {
		return this.field_3693.values();
	}

	@Nullable
	public ScoreboardEntry method_2871(UUID uUID) {
		return (ScoreboardEntry)this.field_3693.get(uUID);
	}

	@Nullable
	public ScoreboardEntry method_2874(String string) {
		for (ScoreboardEntry scoreboardEntry : this.field_3693.values()) {
			if (scoreboardEntry.getProfile().getName().equals(string)) {
				return scoreboardEntry;
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

	public CommandDispatcher<CommandSource> method_2886() {
		return this.commandDispatcher;
	}

	public ClientWorld getWorld() {
		return this.world;
	}

	public TagManager getTagManager() {
		return this.tagManager;
	}

	public class_300 method_2876() {
		return this.field_3692;
	}

	public UUID method_16690() {
		return this.field_16771;
	}
}
