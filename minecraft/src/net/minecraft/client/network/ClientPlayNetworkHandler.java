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
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.gui.CommandBlockGui;
import net.minecraft.client.gui.ContainerGuiRegistry;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.MainMenuGui;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.WrittenBookGui;
import net.minecraft.client.gui.container.HorseGui;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryGui;
import net.minecraft.client.gui.ingame.DeathGui;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.menu.DemoGui;
import net.minecraft.client.gui.menu.DisconnectedGui;
import net.minecraft.client.gui.menu.DownloadingTerrainGui;
import net.minecraft.client.gui.menu.EndCreditsGui;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.gui.menu.RealmsGui;
import net.minecraft.client.gui.menu.YesNoGui;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.network.packet.AdvancementUpdateClientPacket;
import net.minecraft.client.network.packet.BlockActionClientPacket;
import net.minecraft.client.network.packet.BlockBreakingProgressClientPacket;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.BlockUpdateClientPacket;
import net.minecraft.client.network.packet.BossBarClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.ChunkDataClientPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateClientPacket;
import net.minecraft.client.network.packet.CombatEventClientPacket;
import net.minecraft.client.network.packet.CommandSuggestionsClientPacket;
import net.minecraft.client.network.packet.CommandTreeClientPacket;
import net.minecraft.client.network.packet.CooldownUpdateClientPacket;
import net.minecraft.client.network.packet.CraftResponseClientPacket;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.network.packet.DifficultyClientPacket;
import net.minecraft.client.network.packet.DisconnectClientPacket;
import net.minecraft.client.network.packet.EntitiesDestroyClientPacket;
import net.minecraft.client.network.packet.EntityAnimationClientPacket;
import net.minecraft.client.network.packet.EntityAttachClientPacket;
import net.minecraft.client.network.packet.EntityAttributesClientPacket;
import net.minecraft.client.network.packet.EntityClientPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateClientPacket;
import net.minecraft.client.network.packet.EntityPassengersSetClientPacket;
import net.minecraft.client.network.packet.EntityPositionClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntitySetHeadYawClientPacket;
import net.minecraft.client.network.packet.EntitySpawnClientPacket;
import net.minecraft.client.network.packet.EntitySpawnGlobalClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.EntityTrackerUpdateClientPacket;
import net.minecraft.client.network.packet.EntityVelocityUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateClientPacket;
import net.minecraft.client.network.packet.ExperienceOrbSpawnClientPacket;
import net.minecraft.client.network.packet.ExplosionClientPacket;
import net.minecraft.client.network.packet.GameJoinClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.GuiActionConfirmClientPacket;
import net.minecraft.client.network.packet.GuiCloseClientPacket;
import net.minecraft.client.network.packet.GuiOpenClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.GuiUpdateClientPacket;
import net.minecraft.client.network.packet.HealthUpdateClientPacket;
import net.minecraft.client.network.packet.HeldItemChangeClientPacket;
import net.minecraft.client.network.packet.InventoryClientPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationClientPacket;
import net.minecraft.client.network.packet.KeepAliveClientPacket;
import net.minecraft.client.network.packet.LightUpdateClientPacket;
import net.minecraft.client.network.packet.LookAtClientPacket;
import net.minecraft.client.network.packet.MapUpdateClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookClientPacket;
import net.minecraft.client.network.packet.PaintingSpawnClientPacket;
import net.minecraft.client.network.packet.ParticleClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.client.network.packet.PlaySoundFromEntityClientPacket;
import net.minecraft.client.network.packet.PlaySoundIdClientPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesClientPacket;
import net.minecraft.client.network.packet.PlayerListClientPacket;
import net.minecraft.client.network.packet.PlayerListHeaderClientPacket;
import net.minecraft.client.network.packet.PlayerPositionLookClientPacket;
import net.minecraft.client.network.packet.PlayerRespawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnClientPacket;
import net.minecraft.client.network.packet.PlayerSpawnPositionClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectClientPacket;
import net.minecraft.client.network.packet.ResourcePackSendClientPacket;
import net.minecraft.client.network.packet.ScoreboardDisplayClientPacket;
import net.minecraft.client.network.packet.ScoreboardObjectiveUpdateClientPacket;
import net.minecraft.client.network.packet.ScoreboardPlayerUpdateClientPacket;
import net.minecraft.client.network.packet.SelectAdvancementTabClientPacket;
import net.minecraft.client.network.packet.SetCameraEntityClientPacket;
import net.minecraft.client.network.packet.SetVillagerRecipesPacket;
import net.minecraft.client.network.packet.SignEditorOpenClientPacket;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.client.network.packet.StopSoundClientPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesClientPacket;
import net.minecraft.client.network.packet.SynchronizeTagsClientPacket;
import net.minecraft.client.network.packet.TagQueryResponseClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.client.network.packet.TitleClientPacket;
import net.minecraft.client.network.packet.UnloadChunkClientPacket;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.client.network.packet.WorldBorderClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;
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
import net.minecraft.entity.PrimedTNTEntity;
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
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.network.packet.ClientStatusServerPacket;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.server.network.packet.GuiActionConfirmServerPacket;
import net.minecraft.server.network.packet.KeepAliveServerPacket;
import net.minecraft.server.network.packet.PlayerMoveServerMessage;
import net.minecraft.server.network.packet.ResourcePackStatusServerPacket;
import net.minecraft.server.network.packet.TeleportConfirmServerPacket;
import net.minecraft.server.network.packet.VehicleMoveServerPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
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
	private final Gui field_3701;
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

	public ClientPlayNetworkHandler(MinecraftClient minecraftClient, Gui gui, ClientConnection clientConnection, GameProfile gameProfile) {
		this.client = minecraftClient;
		this.field_3701 = gui;
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
	public void onGameJoin(GameJoinClientPacket gameJoinClientPacket) {
		NetworkThreadUtils.forceMainThread(gameJoinClientPacket, this, this.client);
		this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
		this.world = new ClientWorld(
			this,
			new LevelInfo(0L, gameJoinClientPacket.getGameMode(), false, gameJoinClientPacket.isHardcore(), gameJoinClientPacket.method_11563()),
			gameJoinClientPacket.getDimension(),
			gameJoinClientPacket.getDifficulty(),
			this.client.getProfiler()
		);
		this.client.options.difficulty = gameJoinClientPacket.getDifficulty();
		this.client.method_1481(this.world);
		this.client.player.dimension = gameJoinClientPacket.getDimension();
		this.client.openGui(new DownloadingTerrainGui());
		this.client.player.setEntityId(gameJoinClientPacket.getEntityId());
		this.client.player.setReducedDebugInfo(gameJoinClientPacket.hasReducedDebugInfo());
		this.client.interactionManager.setGameMode(gameJoinClientPacket.getGameMode());
		this.client.options.onPlayerModelPartChange();
		this.connection
			.sendPacket(
				new CustomPayloadServerPacket(CustomPayloadServerPacket.BRAND, new PacketByteBuf(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName()))
			);
		this.client.getGame().onStartGameSession();
	}

	@Override
	public void onEntitySpawn(EntitySpawnClientPacket entitySpawnClientPacket) {
		NetworkThreadUtils.forceMainThread(entitySpawnClientPacket, this, this.client);
		double d = entitySpawnClientPacket.getX();
		double e = entitySpawnClientPacket.getY();
		double f = entitySpawnClientPacket.getZ();
		Entity entity = null;
		if (entitySpawnClientPacket.getEntityTypeId() == 10) {
			entity = AbstractMinecartEntity.create(this.world, d, e, f, AbstractMinecartEntity.Type.byId(entitySpawnClientPacket.getEntityData()));
		} else if (entitySpawnClientPacket.getEntityTypeId() == 90) {
			Entity entity2 = this.world.getEntityById(entitySpawnClientPacket.getEntityData());
			if (entity2 instanceof PlayerEntity) {
				entity = new FishHookEntity(this.world, (PlayerEntity)entity2, d, e, f);
			}

			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 60) {
			entity = new ArrowEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 91) {
			entity = new SpectralArrowEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 94) {
			entity = new TridentEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 61) {
			entity = new SnowballEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 68) {
			entity = new LlamaSpitEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 71) {
			entity = new ItemFrameEntity(this.world, new BlockPos(d, e, f), Direction.byId(entitySpawnClientPacket.getEntityData()));
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 77) {
			entity = new LeadKnotEntity(this.world, new BlockPos(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f)));
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 65) {
			entity = new ThrownEnderpearlEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 72) {
			entity = new EnderEyeEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 76) {
			entity = new FireworkEntity(this.world, d, e, f, ItemStack.EMPTY);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 63) {
			entity = new FireballEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 93) {
			entity = new DragonFireballEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 64) {
			entity = new SmallFireballEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 66) {
			entity = new ExplodingWitherSkullEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 67) {
			entity = new ShulkerBulletEntity(
				this.world,
				d,
				e,
				f,
				(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
				(double)entitySpawnClientPacket.getVelocityz() / 8000.0
			);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 62) {
			entity = new ThrownEggEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 79) {
			entity = new EvokerFangsEntity(this.world, d, e, f, 0.0F, 0, null);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 73) {
			entity = new ThrownPotionEntity(this.world, d, e, f);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 75) {
			entity = new ThrownExperienceBottleEntity(this.world, d, e, f);
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 1) {
			entity = new BoatEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 50) {
			entity = new PrimedTNTEntity(this.world, d, e, f, null);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 78) {
			entity = new ArmorStandEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 51) {
			entity = new EnderCrystalEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 2) {
			entity = new ItemEntity(this.world, d, e, f);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 70) {
			entity = new FallingBlockEntity(this.world, d, e, f, Block.getStateFromRawId(entitySpawnClientPacket.getEntityData()));
			entitySpawnClientPacket.setEntityData(0);
		} else if (entitySpawnClientPacket.getEntityTypeId() == 3) {
			entity = new AreaEffectCloudEntity(this.world, d, e, f);
		}

		if (entity != null) {
			EntityTracker.method_14070(entity, d, e, f);
			entity.pitch = (float)(entitySpawnClientPacket.getPitch() * 360) / 256.0F;
			entity.yaw = (float)(entitySpawnClientPacket.getYaw() * 360) / 256.0F;
			Entity[] entitys = entity.getParts();
			if (entitys != null) {
				int i = entitySpawnClientPacket.getId() - entity.getEntityId();

				for (Entity entity3 : entitys) {
					entity3.setEntityId(entity3.getEntityId() + i);
				}
			}

			entity.setEntityId(entitySpawnClientPacket.getId());
			entity.setUuid(entitySpawnClientPacket.getUuid());
			this.world.method_2942(entitySpawnClientPacket.getId(), entity);
			if (entitySpawnClientPacket.getEntityData() > 0) {
				if (entitySpawnClientPacket.getEntityTypeId() == 60 || entitySpawnClientPacket.getEntityTypeId() == 91 || entitySpawnClientPacket.getEntityTypeId() == 94) {
					Entity entity4 = this.world.getEntityById(entitySpawnClientPacket.getEntityData() - 1);
					if (entity4 instanceof LivingEntity && entity instanceof ProjectileEntity) {
						ProjectileEntity projectileEntity = (ProjectileEntity)entity;
						projectileEntity.setOwner(entity4);
						if (entity4 instanceof PlayerEntity) {
							projectileEntity.pickupType = ProjectileEntity.PickupType.PICKUP;
							if (((PlayerEntity)entity4).abilities.creativeMode) {
								projectileEntity.pickupType = ProjectileEntity.PickupType.CREATIVE_PICKUP;
							}
						}
					}
				}

				entity.setVelocityClient(
					(double)entitySpawnClientPacket.getVelocityX() / 8000.0,
					(double)entitySpawnClientPacket.getVelocityY() / 8000.0,
					(double)entitySpawnClientPacket.getVelocityz() / 8000.0
				);
			}
		}
	}

	@Override
	public void onExperienceOrbSpawn(ExperienceOrbSpawnClientPacket experienceOrbSpawnClientPacket) {
		NetworkThreadUtils.forceMainThread(experienceOrbSpawnClientPacket, this, this.client);
		double d = experienceOrbSpawnClientPacket.getX();
		double e = experienceOrbSpawnClientPacket.getY();
		double f = experienceOrbSpawnClientPacket.getZ();
		Entity entity = new ExperienceOrbEntity(this.world, d, e, f, experienceOrbSpawnClientPacket.getExperience());
		EntityTracker.method_14070(entity, d, e, f);
		entity.yaw = 0.0F;
		entity.pitch = 0.0F;
		entity.setEntityId(experienceOrbSpawnClientPacket.getId());
		this.world.method_2942(experienceOrbSpawnClientPacket.getId(), entity);
	}

	@Override
	public void onEntitySpawnGlobal(EntitySpawnGlobalClientPacket entitySpawnGlobalClientPacket) {
		NetworkThreadUtils.forceMainThread(entitySpawnGlobalClientPacket, this, this.client);
		double d = entitySpawnGlobalClientPacket.getX();
		double e = entitySpawnGlobalClientPacket.getY();
		double f = entitySpawnGlobalClientPacket.getZ();
		Entity entity = null;
		if (entitySpawnGlobalClientPacket.getEntityTypeId() == 1) {
			entity = new LightningEntity(this.world, d, e, f, false);
		}

		if (entity != null) {
			EntityTracker.method_14070(entity, d, e, f);
			entity.yaw = 0.0F;
			entity.pitch = 0.0F;
			entity.setEntityId(entitySpawnGlobalClientPacket.getId());
			this.world.addGlobalEntity(entity);
		}
	}

	@Override
	public void onPaintingSpawn(PaintingSpawnClientPacket paintingSpawnClientPacket) {
		NetworkThreadUtils.forceMainThread(paintingSpawnClientPacket, this, this.client);
		PaintingEntity paintingEntity = new PaintingEntity(
			this.world, paintingSpawnClientPacket.getPos(), paintingSpawnClientPacket.getFacing(), paintingSpawnClientPacket.getMotive()
		);
		paintingEntity.setUuid(paintingSpawnClientPacket.getPaintingUuid());
		this.world.method_2942(paintingSpawnClientPacket.getId(), paintingEntity);
	}

	@Override
	public void onVelocityUpdate(EntityVelocityUpdateClientPacket entityVelocityUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(entityVelocityUpdateClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityVelocityUpdateClientPacket.getId());
		if (entity != null) {
			entity.setVelocityClient(
				(double)entityVelocityUpdateClientPacket.getVelocityX() / 8000.0,
				(double)entityVelocityUpdateClientPacket.getVelocityY() / 8000.0,
				(double)entityVelocityUpdateClientPacket.getVelocityZ() / 8000.0
			);
		}
	}

	@Override
	public void onEntityTrackerUpdate(EntityTrackerUpdateClientPacket entityTrackerUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(entityTrackerUpdateClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityTrackerUpdateClientPacket.id());
		if (entity != null && entityTrackerUpdateClientPacket.getTrackedValues() != null) {
			entity.getDataTracker().method_12779(entityTrackerUpdateClientPacket.getTrackedValues());
		}
	}

	@Override
	public void onPlayerSpawn(PlayerSpawnClientPacket playerSpawnClientPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnClientPacket, this, this.client);
		double d = playerSpawnClientPacket.getX();
		double e = playerSpawnClientPacket.getY();
		double f = playerSpawnClientPacket.getZ();
		float g = (float)(playerSpawnClientPacket.getYaw() * 360) / 256.0F;
		float h = (float)(playerSpawnClientPacket.getPitch() * 360) / 256.0F;
		OtherClientPlayerEntity otherClientPlayerEntity = new OtherClientPlayerEntity(
			this.client.world, this.method_2871(playerSpawnClientPacket.getPlayerUuid()).getProfile()
		);
		otherClientPlayerEntity.prevX = d;
		otherClientPlayerEntity.prevRenderX = d;
		otherClientPlayerEntity.prevY = e;
		otherClientPlayerEntity.prevRenderY = e;
		otherClientPlayerEntity.prevZ = f;
		otherClientPlayerEntity.prevRenderZ = f;
		EntityTracker.method_14070(otherClientPlayerEntity, d, e, f);
		otherClientPlayerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.world.method_2942(playerSpawnClientPacket.getId(), otherClientPlayerEntity);
		List<DataTracker.Entry<?>> list = playerSpawnClientPacket.getTrackedValues();
		if (list != null) {
			otherClientPlayerEntity.getDataTracker().method_12779(list);
		}
	}

	@Override
	public void onEntityPosition(EntityPositionClientPacket entityPositionClientPacket) {
		NetworkThreadUtils.forceMainThread(entityPositionClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPositionClientPacket.getId());
		if (entity != null) {
			double d = entityPositionClientPacket.getX();
			double e = entityPositionClientPacket.getY();
			double f = entityPositionClientPacket.getZ();
			EntityTracker.method_14070(entity, d, e, f);
			if (!entity.method_5787()) {
				float g = (float)(entityPositionClientPacket.getYaw() * 360) / 256.0F;
				float h = (float)(entityPositionClientPacket.getPitch() * 360) / 256.0F;
				if (!(Math.abs(entity.x - d) >= 0.03125) && !(Math.abs(entity.y - e) >= 0.015625) && !(Math.abs(entity.z - f) >= 0.03125)) {
					entity.setPositionAndRotations(entity.x, entity.y, entity.z, g, h, 0, true);
				} else {
					entity.setPositionAndRotations(d, e, f, g, h, 3, true);
				}

				entity.onGround = entityPositionClientPacket.isOnGround();
			}
		}
	}

	@Override
	public void onHeldItemChange(HeldItemChangeClientPacket heldItemChangeClientPacket) {
		NetworkThreadUtils.forceMainThread(heldItemChangeClientPacket, this, this.client);
		if (PlayerInventory.isValidHotbarIndex(heldItemChangeClientPacket.getSlot())) {
			this.client.player.inventory.selectedSlot = heldItemChangeClientPacket.getSlot();
		}
	}

	@Override
	public void onEntityUpdate(EntityClientPacket entityClientPacket) {
		NetworkThreadUtils.forceMainThread(entityClientPacket, this, this.client);
		Entity entity = entityClientPacket.getEntity(this.world);
		if (entity != null) {
			entity.field_6001 = entity.field_6001 + (long)entityClientPacket.getDeltaXShort();
			entity.field_6023 = entity.field_6023 + (long)entityClientPacket.getDeltaYShort();
			entity.field_5954 = entity.field_5954 + (long)entityClientPacket.getDeltaZShort();
			double d = (double)entity.field_6001 / 4096.0;
			double e = (double)entity.field_6023 / 4096.0;
			double f = (double)entity.field_5954 / 4096.0;
			if (!entity.method_5787()) {
				float g = entityClientPacket.hasRotation() ? (float)(entityClientPacket.getYaw() * 360) / 256.0F : entity.yaw;
				float h = entityClientPacket.hasRotation() ? (float)(entityClientPacket.getPitch() * 360) / 256.0F : entity.pitch;
				entity.setPositionAndRotations(d, e, f, g, h, 3, false);
				entity.onGround = entityClientPacket.isOnGround();
			}
		}
	}

	@Override
	public void onEntitySetHeadYaw(EntitySetHeadYawClientPacket entitySetHeadYawClientPacket) {
		NetworkThreadUtils.forceMainThread(entitySetHeadYawClientPacket, this, this.client);
		Entity entity = entitySetHeadYawClientPacket.getEntity(this.world);
		if (entity != null) {
			float f = (float)(entitySetHeadYawClientPacket.getHeadYaw() * 360) / 256.0F;
			entity.method_5683(f, 3);
		}
	}

	@Override
	public void onEntitiesDestroy(EntitiesDestroyClientPacket entitiesDestroyClientPacket) {
		NetworkThreadUtils.forceMainThread(entitiesDestroyClientPacket, this, this.client);

		for (int i = 0; i < entitiesDestroyClientPacket.getEntityIds().length; i++) {
			this.world.method_2945(entitiesDestroyClientPacket.getEntityIds()[i]);
		}
	}

	@Override
	public void onPlayerPositionLook(PlayerPositionLookClientPacket playerPositionLookClientPacket) {
		NetworkThreadUtils.forceMainThread(playerPositionLookClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		double d = playerPositionLookClientPacket.getX();
		double e = playerPositionLookClientPacket.getY();
		double f = playerPositionLookClientPacket.getZ();
		float g = playerPositionLookClientPacket.getYaw();
		float h = playerPositionLookClientPacket.getPitch();
		if (playerPositionLookClientPacket.getFlags().contains(PlayerPositionLookClientPacket.Flag.X)) {
			playerEntity.prevRenderX += d;
			d += playerEntity.x;
		} else {
			playerEntity.prevRenderX = d;
			playerEntity.velocityX = 0.0;
		}

		if (playerPositionLookClientPacket.getFlags().contains(PlayerPositionLookClientPacket.Flag.Y)) {
			playerEntity.prevRenderY += e;
			e += playerEntity.y;
		} else {
			playerEntity.prevRenderY = e;
			playerEntity.velocityY = 0.0;
		}

		if (playerPositionLookClientPacket.getFlags().contains(PlayerPositionLookClientPacket.Flag.Z)) {
			playerEntity.prevRenderZ += f;
			f += playerEntity.z;
		} else {
			playerEntity.prevRenderZ = f;
			playerEntity.velocityZ = 0.0;
		}

		if (playerPositionLookClientPacket.getFlags().contains(PlayerPositionLookClientPacket.Flag.X_ROT)) {
			h += playerEntity.pitch;
		}

		if (playerPositionLookClientPacket.getFlags().contains(PlayerPositionLookClientPacket.Flag.Y_ROT)) {
			g += playerEntity.yaw;
		}

		playerEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
		this.connection.sendPacket(new TeleportConfirmServerPacket(playerPositionLookClientPacket.getTeleportId()));
		this.connection
			.sendPacket(
				new PlayerMoveServerMessage.Both(playerEntity.x, playerEntity.getBoundingBox().minY, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false)
			);
		if (!this.field_3698) {
			this.client.player.prevX = this.client.player.x;
			this.client.player.prevY = this.client.player.y;
			this.client.player.prevZ = this.client.player.z;
			this.field_3698 = true;
			this.client.openGui(null);
		}
	}

	@Override
	public void onChunkDeltaUpdate(ChunkDeltaUpdateClientPacket chunkDeltaUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(chunkDeltaUpdateClientPacket, this, this.client);

		for (ChunkDeltaUpdateClientPacket.ChunkDeltaRecord chunkDeltaRecord : chunkDeltaUpdateClientPacket.getRecords()) {
			this.world.method_2937(chunkDeltaRecord.getBlockPos(), chunkDeltaRecord.getState());
		}
	}

	@Override
	public void onChunkData(ChunkDataClientPacket chunkDataClientPacket) {
		NetworkThreadUtils.forceMainThread(chunkDataClientPacket, this, this.client);
		int i = chunkDataClientPacket.getX();
		int j = chunkDataClientPacket.getZ();
		this.world
			.getChunkProvider()
			.method_16020(
				this.world,
				i,
				j,
				chunkDataClientPacket.getReadBuffer(),
				chunkDataClientPacket.method_16123(),
				chunkDataClientPacket.getVerticalStripBitmask(),
				chunkDataClientPacket.isFullChunk()
			);

		for (int k = 0; k < 16; k++) {
			this.world.scheduleNeighborChunksRender(i, k, j);
		}

		for (CompoundTag compoundTag : chunkDataClientPacket.getBlockEntityTagList()) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity != null) {
				blockEntity.fromTag(compoundTag);
			}
		}
	}

	@Override
	public void onUnloadChunk(UnloadChunkClientPacket unloadChunkClientPacket) {
		NetworkThreadUtils.forceMainThread(unloadChunkClientPacket, this, this.client);
		int i = unloadChunkClientPacket.getX();
		int j = unloadChunkClientPacket.getZ();
		this.world.getChunkProvider().method_2859(i, j);

		for (int k = 0; k < 16; k++) {
			this.world.scheduleNeighborChunksRender(i, k, j);
		}
	}

	@Override
	public void onBlockUpdate(BlockUpdateClientPacket blockUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(blockUpdateClientPacket, this, this.client);
		this.world.method_2937(blockUpdateClientPacket.getPos(), blockUpdateClientPacket.getState());
	}

	@Override
	public void onDisconnect(DisconnectClientPacket disconnectClientPacket) {
		this.connection.disconnect(disconnectClientPacket.getReason());
	}

	@Override
	public void onConnectionLost(TextComponent textComponent) {
		this.client.method_1481(null);
		if (this.field_3701 != null) {
			if (this.field_3701 instanceof RealmsGui) {
				this.client.openGui(new DisconnectedRealmsScreen(((RealmsGui)this.field_3701).getRealmsScreen(), "disconnect.lost", textComponent).getProxy());
			} else {
				this.client.openGui(new DisconnectedGui(this.field_3701, "disconnect.lost", textComponent));
			}
		} else {
			this.client.openGui(new DisconnectedGui(new MultiplayerGui(new MainMenuGui()), "disconnect.lost", textComponent));
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.connection.sendPacket(packet);
	}

	@Override
	public void onItemPickupAnimation(ItemPickupAnimationClientPacket itemPickupAnimationClientPacket) {
		NetworkThreadUtils.forceMainThread(itemPickupAnimationClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(itemPickupAnimationClientPacket.getEntityId());
		LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(itemPickupAnimationClientPacket.getCollectorEntityId());
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
				((ItemEntity)entity).getStack().setAmount(itemPickupAnimationClientPacket.getStackAmount());
			}

			this.client.particleManager.addParticle(new ItemPickupParticle(this.world, entity, livingEntity, 0.5F));
			this.world.method_2945(itemPickupAnimationClientPacket.getEntityId());
		}
	}

	@Override
	public void onChatMessage(ChatMessageClientPacket chatMessageClientPacket) {
		NetworkThreadUtils.forceMainThread(chatMessageClientPacket, this, this.client);
		this.client.inGameHud.addChatMessage(chatMessageClientPacket.getLocation(), chatMessageClientPacket.getMessage());
	}

	@Override
	public void onEntityAnimation(EntityAnimationClientPacket entityAnimationClientPacket) {
		NetworkThreadUtils.forceMainThread(entityAnimationClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAnimationClientPacket.getId());
		if (entity != null) {
			if (entityAnimationClientPacket.getAnimationId() == 0) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.MAIN);
			} else if (entityAnimationClientPacket.getAnimationId() == 3) {
				LivingEntity livingEntity = (LivingEntity)entity;
				livingEntity.swingHand(Hand.OFF);
			} else if (entityAnimationClientPacket.getAnimationId() == 1) {
				entity.method_5879();
			} else if (entityAnimationClientPacket.getAnimationId() == 2) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				playerEntity.method_7358(false, false, false);
			} else if (entityAnimationClientPacket.getAnimationId() == 4) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11205);
			} else if (entityAnimationClientPacket.getAnimationId() == 5) {
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11208);
			}
		}
	}

	@Override
	public void onPlayerUseBed(PlayerUseBedClientPacket playerUseBedClientPacket) {
		NetworkThreadUtils.forceMainThread(playerUseBedClientPacket, this, this.client);
		playerUseBedClientPacket.getPlayer(this.world).trySleep(playerUseBedClientPacket.getBedHeadPos());
	}

	@Override
	public void onMobSpawn(MobSpawnClientPacket mobSpawnClientPacket) {
		NetworkThreadUtils.forceMainThread(mobSpawnClientPacket, this, this.client);
		double d = mobSpawnClientPacket.getX();
		double e = mobSpawnClientPacket.getY();
		double f = mobSpawnClientPacket.getZ();
		float g = (float)(mobSpawnClientPacket.getVelocityX() * 360) / 256.0F;
		float h = (float)(mobSpawnClientPacket.getVelocityY() * 360) / 256.0F;
		LivingEntity livingEntity = (LivingEntity)EntityType.createInstanceFromId(mobSpawnClientPacket.getEntityTypeId(), this.client.world);
		if (livingEntity != null) {
			EntityTracker.method_14070(livingEntity, d, e, f);
			livingEntity.field_6283 = (float)(mobSpawnClientPacket.getVelocityZ() * 360) / 256.0F;
			livingEntity.headYaw = (float)(mobSpawnClientPacket.getVelocityZ() * 360) / 256.0F;
			Entity[] entitys = livingEntity.getParts();
			if (entitys != null) {
				int i = mobSpawnClientPacket.getId() - livingEntity.getEntityId();

				for (Entity entity : entitys) {
					entity.setEntityId(entity.getEntityId() + i);
				}
			}

			livingEntity.setEntityId(mobSpawnClientPacket.getId());
			livingEntity.setUuid(mobSpawnClientPacket.getUuid());
			livingEntity.setPositionAnglesAndUpdate(d, e, f, g, h);
			livingEntity.velocityX = (double)((float)mobSpawnClientPacket.getYaw() / 8000.0F);
			livingEntity.velocityY = (double)((float)mobSpawnClientPacket.getPitch() / 8000.0F);
			livingEntity.velocityZ = (double)((float)mobSpawnClientPacket.getHeadPitch() / 8000.0F);
			this.world.method_2942(mobSpawnClientPacket.getId(), livingEntity);
			List<DataTracker.Entry<?>> list = mobSpawnClientPacket.getTrackedValues();
			if (list != null) {
				livingEntity.getDataTracker().method_12779(list);
			}
		} else {
			LOGGER.warn("Skipping Entity with id {}", mobSpawnClientPacket.getEntityTypeId());
		}
	}

	@Override
	public void onWorldTimeUpdate(WorldTimeUpdateClientPacket worldTimeUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(worldTimeUpdateClientPacket, this, this.client);
		this.client.world.setTime(worldTimeUpdateClientPacket.getTime());
		this.client.world.setTimeOfDay(worldTimeUpdateClientPacket.getTimeOfDay());
	}

	@Override
	public void onPlayerSpawnPosition(PlayerSpawnPositionClientPacket playerSpawnPositionClientPacket) {
		NetworkThreadUtils.forceMainThread(playerSpawnPositionClientPacket, this, this.client);
		this.client.player.setPlayerSpawn(playerSpawnPositionClientPacket.getPos(), true);
		this.client.world.getLevelProperties().setSpawnPos(playerSpawnPositionClientPacket.getPos());
	}

	@Override
	public void onEntityPassengersSet(EntityPassengersSetClientPacket entityPassengersSetClientPacket) {
		NetworkThreadUtils.forceMainThread(entityPassengersSetClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPassengersSetClientPacket.getId());
		if (entity == null) {
			LOGGER.warn("Received passengers for unknown entity");
		} else {
			boolean bl = entity.method_5821(this.client.player);
			entity.removeAllPassengers();

			for (int i : entityPassengersSetClientPacket.getPassengerIds()) {
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
	public void onEntityAttach(EntityAttachClientPacket entityAttachClientPacket) {
		NetworkThreadUtils.forceMainThread(entityAttachClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttachClientPacket.getAttachedEntityId());
		Entity entity2 = this.world.getEntityById(entityAttachClientPacket.getHoldingEntityId());
		if (entity instanceof MobEntity) {
			if (entity2 != null) {
				((MobEntity)entity).attachLeash(entity2, false);
			} else {
				((MobEntity)entity).detachLeash(false, false);
			}
		}
	}

	@Override
	public void onEntityStatus(EntityStatusClientPacket entityStatusClientPacket) {
		NetworkThreadUtils.forceMainThread(entityStatusClientPacket, this, this.client);
		Entity entity = entityStatusClientPacket.getEntity(this.world);
		if (entity != null) {
			if (entityStatusClientPacket.getStatus() == 21) {
				this.client.getSoundLoader().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
			} else if (entityStatusClientPacket.getStatus() == 35) {
				int i = 40;
				this.client.particleManager.addEmitter(entity, ParticleTypes.field_11220, 30);
				this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.field_14931, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.gameRenderer.showFloatingItem(new ItemStack(Items.field_8288));
				}
			} else {
				entity.method_5711(entityStatusClientPacket.getStatus());
			}
		}
	}

	@Override
	public void onHealthUpdate(HealthUpdateClientPacket healthUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(healthUpdateClientPacket, this, this.client);
		this.client.player.updateHealth(healthUpdateClientPacket.getHealth());
		this.client.player.getHungerManager().setFoodLevel(healthUpdateClientPacket.getFood());
		this.client.player.getHungerManager().setSaturationLevelClient(healthUpdateClientPacket.getSaturation());
	}

	@Override
	public void onExperienceBarUpdate(ExperienceBarUpdateClientPacket experienceBarUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(experienceBarUpdateClientPacket, this, this.client);
		this.client
			.player
			.method_3145(
				experienceBarUpdateClientPacket.getBarProgress(), experienceBarUpdateClientPacket.getExperienceLevel(), experienceBarUpdateClientPacket.getExperience()
			);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnClientPacket playerRespawnClientPacket) {
		NetworkThreadUtils.forceMainThread(playerRespawnClientPacket, this, this.client);
		DimensionType dimensionType = playerRespawnClientPacket.getDimension();
		if (dimensionType != this.client.player.dimension) {
			this.field_3698 = false;
			Scoreboard scoreboard = this.world.getScoreboard();
			this.world = new ClientWorld(
				this,
				new LevelInfo(
					0L, playerRespawnClientPacket.getGameMode(), false, this.client.world.getLevelProperties().isHardcore(), playerRespawnClientPacket.getGeneratorType()
				),
				playerRespawnClientPacket.getDimension(),
				playerRespawnClientPacket.getDifficulty(),
				this.client.getProfiler()
			);
			this.world.setScoreboard(scoreboard);
			this.client.method_1481(this.world);
			this.client.player.dimension = dimensionType;
			this.client.openGui(new DownloadingTerrainGui());
		}

		this.client.method_1585(playerRespawnClientPacket.getDimension());
		this.client.interactionManager.setGameMode(playerRespawnClientPacket.getGameMode());
	}

	@Override
	public void onExplosion(ExplosionClientPacket explosionClientPacket) {
		NetworkThreadUtils.forceMainThread(explosionClientPacket, this, this.client);
		Explosion explosion = new Explosion(
			this.client.world,
			null,
			explosionClientPacket.getX(),
			explosionClientPacket.getY(),
			explosionClientPacket.getZ(),
			explosionClientPacket.getRadius(),
			explosionClientPacket.getAffectedBlocks()
		);
		explosion.affectWorld(true);
		this.client.player.velocityX = this.client.player.velocityX + (double)explosionClientPacket.getPlayerVelocityX();
		this.client.player.velocityY = this.client.player.velocityY + (double)explosionClientPacket.getPlayerVelocityY();
		this.client.player.velocityZ = this.client.player.velocityZ + (double)explosionClientPacket.getPlayerVelocityZ();
	}

	@Override
	public void onGuiOpen(GuiOpenClientPacket guiOpenClientPacket) {
		NetworkThreadUtils.forceMainThread(guiOpenClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(guiOpenClientPacket.getHorseId());
		if (entity instanceof HorseBaseEntity) {
			ClientPlayerEntity clientPlayerEntity = this.client.player;
			HorseBaseEntity horseBaseEntity = (HorseBaseEntity)entity;
			BasicInventory basicInventory = new BasicInventory(guiOpenClientPacket.getSlotCount());
			HorseContainer horseContainer = new HorseContainer(guiOpenClientPacket.getId(), clientPlayerEntity.inventory, basicInventory, horseBaseEntity);
			clientPlayerEntity.container = horseContainer;
			this.client.openGui(new HorseGui(horseContainer, clientPlayerEntity.inventory, horseBaseEntity));
		}
	}

	@Override
	public void onOpenContainer(OpenContainerPacket openContainerPacket) {
		NetworkThreadUtils.forceMainThread(openContainerPacket, this, this.client);
		ContainerGuiRegistry.openGui(openContainerPacket.getContainerType(), this.client, openContainerPacket.getSyncId(), openContainerPacket.getName());
	}

	@Override
	public void onGuiSlotUpdate(GuiSlotUpdateClientPacket guiSlotUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(guiSlotUpdateClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		ItemStack itemStack = guiSlotUpdateClientPacket.getItemStack();
		int i = guiSlotUpdateClientPacket.getSlot();
		this.client.getTutorialManager().onSlotUpdate(itemStack);
		if (guiSlotUpdateClientPacket.getId() == -1) {
			playerEntity.inventory.setCursorStack(itemStack);
		} else if (guiSlotUpdateClientPacket.getId() == -2) {
			playerEntity.inventory.setInvStack(i, itemStack);
		} else {
			boolean bl = false;
			if (this.client.currentGui instanceof CreativePlayerInventoryGui) {
				CreativePlayerInventoryGui creativePlayerInventoryGui = (CreativePlayerInventoryGui)this.client.currentGui;
				bl = creativePlayerInventoryGui.method_2469() != ItemGroup.INVENTORY.getId();
			}

			if (guiSlotUpdateClientPacket.getId() == 0 && guiSlotUpdateClientPacket.getSlot() >= 36 && i < 45) {
				if (!itemStack.isEmpty()) {
					ItemStack itemStack2 = playerEntity.containerPlayer.getSlot(i).getStack();
					if (itemStack2.isEmpty() || itemStack2.getAmount() < itemStack.getAmount()) {
						itemStack.setUpdateCooldown(5);
					}
				}

				playerEntity.containerPlayer.setStackInSlot(i, itemStack);
			} else if (guiSlotUpdateClientPacket.getId() == playerEntity.container.syncId && (guiSlotUpdateClientPacket.getId() != 0 || !bl)) {
				playerEntity.container.setStackInSlot(i, itemStack);
			}
		}
	}

	@Override
	public void onGuiActionConfirm(GuiActionConfirmClientPacket guiActionConfirmClientPacket) {
		NetworkThreadUtils.forceMainThread(guiActionConfirmClientPacket, this, this.client);
		Container container = null;
		PlayerEntity playerEntity = this.client.player;
		if (guiActionConfirmClientPacket.getId() == 0) {
			container = playerEntity.containerPlayer;
		} else if (guiActionConfirmClientPacket.getId() == playerEntity.container.syncId) {
			container = playerEntity.container;
		}

		if (container != null && !guiActionConfirmClientPacket.wasAccepted()) {
			this.sendPacket(new GuiActionConfirmServerPacket(guiActionConfirmClientPacket.getId(), guiActionConfirmClientPacket.getActionId(), true));
		}
	}

	@Override
	public void onInventory(InventoryClientPacket inventoryClientPacket) {
		NetworkThreadUtils.forceMainThread(inventoryClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (inventoryClientPacket.getGuiId() == 0) {
			playerEntity.containerPlayer.updateSlotStacks(inventoryClientPacket.getSlotStacks());
		} else if (inventoryClientPacket.getGuiId() == playerEntity.container.syncId) {
			playerEntity.container.updateSlotStacks(inventoryClientPacket.getSlotStacks());
		}
	}

	@Override
	public void onSignEditorOpen(SignEditorOpenClientPacket signEditorOpenClientPacket) {
		NetworkThreadUtils.forceMainThread(signEditorOpenClientPacket, this, this.client);
		BlockEntity blockEntity = this.world.getBlockEntity(signEditorOpenClientPacket.getPos());
		if (!(blockEntity instanceof SignBlockEntity)) {
			blockEntity = new SignBlockEntity();
			blockEntity.setWorld(this.world);
			blockEntity.setPos(signEditorOpenClientPacket.getPos());
		}

		this.client.player.openSignEditorGui((SignBlockEntity)blockEntity);
	}

	@Override
	public void onBlockEntityUpdate(BlockEntityUpdateClientPacket blockEntityUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(blockEntityUpdateClientPacket, this, this.client);
		if (this.client.world.isBlockLoaded(blockEntityUpdateClientPacket.getPos())) {
			BlockEntity blockEntity = this.client.world.getBlockEntity(blockEntityUpdateClientPacket.getPos());
			int i = blockEntityUpdateClientPacket.getActionId();
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
				blockEntity.fromTag(blockEntityUpdateClientPacket.getCompoundTag());
			}

			if (bl && this.client.currentGui instanceof CommandBlockGui) {
				((CommandBlockGui)this.client.currentGui).method_2457();
			}
		}
	}

	@Override
	public void onGuiUpdate(GuiUpdateClientPacket guiUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(guiUpdateClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		if (playerEntity.container != null && playerEntity.container.syncId == guiUpdateClientPacket.getId()) {
			playerEntity.container.setProperty(guiUpdateClientPacket.getPropertyId(), guiUpdateClientPacket.getValue());
		}
	}

	@Override
	public void onEquipmentUpdate(EntityEquipmentUpdateClientPacket entityEquipmentUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(entityEquipmentUpdateClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityEquipmentUpdateClientPacket.getId());
		if (entity != null) {
			entity.setEquippedStack(entityEquipmentUpdateClientPacket.getSlot(), entityEquipmentUpdateClientPacket.getStack());
		}
	}

	@Override
	public void onGuiClose(GuiCloseClientPacket guiCloseClientPacket) {
		NetworkThreadUtils.forceMainThread(guiCloseClientPacket, this, this.client);
		this.client.player.method_3137();
	}

	@Override
	public void onBlockAction(BlockActionClientPacket blockActionClientPacket) {
		NetworkThreadUtils.forceMainThread(blockActionClientPacket, this, this.client);
		this.client
			.world
			.addBlockAction(
				blockActionClientPacket.getPos(),
				blockActionClientPacket.getBlock(),
				blockActionClientPacket.getArgumentFirst(),
				blockActionClientPacket.getArgumentSecond()
			);
	}

	@Override
	public void onBlockDestroyProgress(BlockBreakingProgressClientPacket blockBreakingProgressClientPacket) {
		NetworkThreadUtils.forceMainThread(blockBreakingProgressClientPacket, this, this.client);
		this.client
			.world
			.setBlockBreakingProgress(
				blockBreakingProgressClientPacket.getEntityId(), blockBreakingProgressClientPacket.getPos(), blockBreakingProgressClientPacket.getProgress()
			);
	}

	@Override
	public void onGameStateChange(GameStateChangeClientPacket gameStateChangeClientPacket) {
		NetworkThreadUtils.forceMainThread(gameStateChangeClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		int i = gameStateChangeClientPacket.getReason();
		float f = gameStateChangeClientPacket.getValue();
		int j = MathHelper.floor(f + 0.5F);
		if (i >= 0 && i < GameStateChangeClientPacket.REASON_MESSAGES.length && GameStateChangeClientPacket.REASON_MESSAGES[i] != null) {
			playerEntity.addChatMessage(new TranslatableTextComponent(GameStateChangeClientPacket.REASON_MESSAGES[i]), false);
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
				this.client.player.networkHandler.sendPacket(new ClientStatusServerPacket(ClientStatusServerPacket.Mode.field_12774));
				this.client.openGui(new DownloadingTerrainGui());
			} else if (j == 1) {
				this.client
					.openGui(
						new EndCreditsGui(true, () -> this.client.player.networkHandler.sendPacket(new ClientStatusServerPacket(ClientStatusServerPacket.Mode.field_12774)))
					);
			}
		} else if (i == 5) {
			GameOptions gameOptions = this.client.options;
			if (f == 0.0F) {
				this.client.openGui(new DemoGui());
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
	public void onMapUpdate(MapUpdateClientPacket mapUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(mapUpdateClientPacket, this, this.client);
		MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
		String string = FilledMapItem.method_17440(mapUpdateClientPacket.getId());
		MapState mapState = this.client.world.method_17891(string);
		if (mapState == null) {
			mapState = new MapState(string);
			if (mapRenderer.getTexture(string) != null) {
				MapState mapState2 = mapRenderer.getState(mapRenderer.getTexture(string));
				if (mapState2 != null) {
					mapState = mapState2;
				}
			}

			this.client.world.method_17890(mapState);
		}

		mapUpdateClientPacket.apply(mapState);
		mapRenderer.updateTexture(mapState);
	}

	@Override
	public void onWorldEvent(WorldEventClientPacket worldEventClientPacket) {
		NetworkThreadUtils.forceMainThread(worldEventClientPacket, this, this.client);
		if (worldEventClientPacket.isGlobal()) {
			this.client.world.fireGlobalWorldEvent(worldEventClientPacket.getEventId(), worldEventClientPacket.getPos(), worldEventClientPacket.getEffectData());
		} else {
			this.client.world.fireWorldEvent(worldEventClientPacket.getEventId(), worldEventClientPacket.getPos(), worldEventClientPacket.getEffectData());
		}
	}

	@Override
	public void onAdvancements(AdvancementUpdateClientPacket advancementUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(advancementUpdateClientPacket, this, this.client);
		this.advancementHandler.onAdvancements(advancementUpdateClientPacket);
	}

	@Override
	public void onSelectAdvancementTab(SelectAdvancementTabClientPacket selectAdvancementTabClientPacket) {
		NetworkThreadUtils.forceMainThread(selectAdvancementTabClientPacket, this, this.client);
		Identifier identifier = selectAdvancementTabClientPacket.getTabId();
		if (identifier == null) {
			this.advancementHandler.selectTab(null, false);
		} else {
			SimpleAdvancement simpleAdvancement = this.advancementHandler.getManager().get(identifier);
			this.advancementHandler.selectTab(simpleAdvancement, false);
		}
	}

	@Override
	public void onCommandTree(CommandTreeClientPacket commandTreeClientPacket) {
		NetworkThreadUtils.forceMainThread(commandTreeClientPacket, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(commandTreeClientPacket.method_11403());
	}

	@Override
	public void onStopSound(StopSoundClientPacket stopSoundClientPacket) {
		NetworkThreadUtils.forceMainThread(stopSoundClientPacket, this, this.client);
		this.client.getSoundLoader().stopSounds(stopSoundClientPacket.getSoundId(), stopSoundClientPacket.getCategory());
	}

	@Override
	public void onCommandSuggestions(CommandSuggestionsClientPacket commandSuggestionsClientPacket) {
		NetworkThreadUtils.forceMainThread(commandSuggestionsClientPacket, this, this.client);
		this.commandSource.method_2931(commandSuggestionsClientPacket.method_11399(), commandSuggestionsClientPacket.getSuggestions());
	}

	@Override
	public void onSynchronizeRecipes(SynchronizeRecipesClientPacket synchronizeRecipesClientPacket) {
		NetworkThreadUtils.forceMainThread(synchronizeRecipesClientPacket, this, this.client);
		this.recipeManager.clear();

		for (Recipe<?> recipe : synchronizeRecipesClientPacket.getRecipes()) {
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
	public void onLookAt(LookAtClientPacket lookAtClientPacket) {
		NetworkThreadUtils.forceMainThread(lookAtClientPacket, this, this.client);
		Vec3d vec3d = lookAtClientPacket.getTargetPosition(this.world);
		if (vec3d != null) {
			this.client.player.lookAt(lookAtClientPacket.getSelfAnchor(), vec3d);
		}
	}

	@Override
	public void onTagQuery(TagQueryResponseClientPacket tagQueryResponseClientPacket) {
		NetworkThreadUtils.forceMainThread(tagQueryResponseClientPacket, this, this.client);
		if (!this.field_3692.method_1404(tagQueryResponseClientPacket.getTransactionId(), tagQueryResponseClientPacket.getTag())) {
			LOGGER.debug("Got unhandled response to tag query {}", tagQueryResponseClientPacket.getTransactionId());
		}
	}

	@Override
	public void onStatistics(StatisticsClientPacket statisticsClientPacket) {
		NetworkThreadUtils.forceMainThread(statisticsClientPacket, this, this.client);

		for (Entry<Stat<?>, Integer> entry : statisticsClientPacket.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStats().setStat(this.client.player, stat, i);
		}

		if (this.client.currentGui instanceof class_452) {
			((class_452)this.client.currentGui).method_2300();
		}
	}

	@Override
	public void onUnlockRecipes(UnlockRecipesClientPacket unlockRecipesClientPacket) {
		NetworkThreadUtils.forceMainThread(unlockRecipesClientPacket, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setGuiOpen(unlockRecipesClientPacket.isGuiOpen());
		clientRecipeBook.setFilteringCraftable(unlockRecipesClientPacket.isFilteringCraftable());
		clientRecipeBook.setFurnaceGuiOpen(unlockRecipesClientPacket.isFurnaceGuiOpen());
		clientRecipeBook.setFurnaceFilteringCraftable(unlockRecipesClientPacket.isFurnaceFilteringCraftable());
		UnlockRecipesClientPacket.Action action = unlockRecipesClientPacket.getAction();
		switch (action) {
			case field_12417:
				for (Identifier identifier : unlockRecipesClientPacket.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::remove);
				}
				break;
			case field_12416:
				for (Identifier identifier : unlockRecipesClientPacket.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::add);
				}

				for (Identifier identifier : unlockRecipesClientPacket.getRecipeIdsToInit()) {
					this.recipeManager.get(identifier).ifPresent(clientRecipeBook::display);
				}
				break;
			case field_12415:
				for (Identifier identifier : unlockRecipesClientPacket.getRecipeIdsToChange()) {
					this.recipeManager.get(identifier).ifPresent(recipe -> {
						clientRecipeBook.add(recipe);
						clientRecipeBook.display(recipe);
						RecipeToast.method_1985(this.client.getToastManager(), recipe);
					});
				}
		}

		clientRecipeBook.getOrderedResults().forEach(recipeResultCollection -> recipeResultCollection.initialize(clientRecipeBook));
		if (this.client.currentGui instanceof RecipeBookProvider) {
			((RecipeBookProvider)this.client.currentGui).refreshRecipeBook();
		}
	}

	@Override
	public void onEntityPotionEffect(EntityPotionEffectClientPacket entityPotionEffectClientPacket) {
		NetworkThreadUtils.forceMainThread(entityPotionEffectClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityPotionEffectClientPacket.method_11943());
		if (entity instanceof LivingEntity) {
			StatusEffect statusEffect = StatusEffect.byRawId(entityPotionEffectClientPacket.method_11946());
			if (statusEffect != null) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
					statusEffect,
					entityPotionEffectClientPacket.method_11944(),
					entityPotionEffectClientPacket.method_11945(),
					entityPotionEffectClientPacket.method_11950(),
					entityPotionEffectClientPacket.method_11949(),
					entityPotionEffectClientPacket.method_11942()
				);
				statusEffectInstance.setPermanent(entityPotionEffectClientPacket.method_11947());
				((LivingEntity)entity).addPotionEffect(statusEffectInstance);
			}
		}
	}

	@Override
	public void onSynchronizeTags(SynchronizeTagsClientPacket synchronizeTagsClientPacket) {
		NetworkThreadUtils.forceMainThread(synchronizeTagsClientPacket, this, this.client);
		this.tagManager = synchronizeTagsClientPacket.getTagManager();
		if (!this.connection.isLocal()) {
			BlockTags.setContainer(this.tagManager.blocks());
			ItemTags.setContainer(this.tagManager.items());
			FluidTags.setContainer(this.tagManager.fluids());
			EntityTags.setContainer(this.tagManager.entities());
		}

		this.client.getSearchableContainer(SearchManager.ITEM_TAG).reload();
	}

	@Override
	public void onCombatEvent(CombatEventClientPacket combatEventClientPacket) {
		NetworkThreadUtils.forceMainThread(combatEventClientPacket, this, this.client);
		if (combatEventClientPacket.type == CombatEventClientPacket.Type.DEATH) {
			Entity entity = this.world.getEntityById(combatEventClientPacket.entityId);
			if (entity == this.client.player) {
				this.client.openGui(new DeathGui(combatEventClientPacket.deathMessage));
			}
		}
	}

	@Override
	public void onDifficulty(DifficultyClientPacket difficultyClientPacket) {
		NetworkThreadUtils.forceMainThread(difficultyClientPacket, this, this.client);
		this.client.world.getLevelProperties().setDifficulty(difficultyClientPacket.getDifficulty());
		this.client.world.getLevelProperties().setDifficultyLocked(difficultyClientPacket.method_11340());
	}

	@Override
	public void onSetCameraEntity(SetCameraEntityClientPacket setCameraEntityClientPacket) {
		NetworkThreadUtils.forceMainThread(setCameraEntityClientPacket, this, this.client);
		Entity entity = setCameraEntityClientPacket.getEntity(this.world);
		if (entity != null) {
			this.client.setCameraEntity(entity);
		}
	}

	@Override
	public void onWorldBorder(WorldBorderClientPacket worldBorderClientPacket) {
		NetworkThreadUtils.forceMainThread(worldBorderClientPacket, this, this.client);
		worldBorderClientPacket.apply(this.world.getWorldBorder());
	}

	@Override
	public void onTitle(TitleClientPacket titleClientPacket) {
		NetworkThreadUtils.forceMainThread(titleClientPacket, this, this.client);
		TitleClientPacket.Action action = titleClientPacket.getAction();
		String string = null;
		String string2 = null;
		String string3 = titleClientPacket.getText() != null ? titleClientPacket.getText().getFormattedText() : "";
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

		this.client
			.inGameHud
			.method_1763(string, string2, titleClientPacket.getTicksFadeIn(), titleClientPacket.getTicksDisplay(), titleClientPacket.getTicksFadeOut());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderClientPacket playerListHeaderClientPacket) {
		this.client
			.inGameHud
			.getScoreboardWidget()
			.method_1925(playerListHeaderClientPacket.getHeader().getFormattedText().isEmpty() ? null : playerListHeaderClientPacket.getHeader());
		this.client
			.inGameHud
			.getScoreboardWidget()
			.method_1924(playerListHeaderClientPacket.getFooter().getFormattedText().isEmpty() ? null : playerListHeaderClientPacket.getFooter());
	}

	@Override
	public void onRemoveEntityEffect(RemoveEntityEffectClientPacket removeEntityEffectClientPacket) {
		NetworkThreadUtils.forceMainThread(removeEntityEffectClientPacket, this, this.client);
		Entity entity = removeEntityEffectClientPacket.getEntity(this.world);
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).removePotionEffect(removeEntityEffectClientPacket.getEffectType());
		}
	}

	@Override
	public void onPlayerList(PlayerListClientPacket playerListClientPacket) {
		NetworkThreadUtils.forceMainThread(playerListClientPacket, this, this.client);

		for (PlayerListClientPacket.class_2705 lv : playerListClientPacket.method_11722()) {
			if (playerListClientPacket.getType() == PlayerListClientPacket.Type.REMOVE) {
				this.field_3693.remove(lv.method_11726().getId());
			} else {
				ScoreboardEntry scoreboardEntry = (ScoreboardEntry)this.field_3693.get(lv.method_11726().getId());
				if (playerListClientPacket.getType() == PlayerListClientPacket.Type.ADD) {
					scoreboardEntry = new ScoreboardEntry(lv);
					this.field_3693.put(scoreboardEntry.getProfile().getId(), scoreboardEntry);
				}

				if (scoreboardEntry != null) {
					switch (playerListClientPacket.getType()) {
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
	public void onKeepAlive(KeepAliveClientPacket keepAliveClientPacket) {
		this.sendPacket(new KeepAliveServerPacket(keepAliveClientPacket.method_11517()));
	}

	@Override
	public void onPlayerAbilities(PlayerAbilitiesClientPacket playerAbilitiesClientPacket) {
		NetworkThreadUtils.forceMainThread(playerAbilitiesClientPacket, this, this.client);
		PlayerEntity playerEntity = this.client.player;
		playerEntity.abilities.flying = playerAbilitiesClientPacket.isFlying();
		playerEntity.abilities.creativeMode = playerAbilitiesClientPacket.isCreativeMode();
		playerEntity.abilities.invulnerable = playerAbilitiesClientPacket.isInvulnerable();
		playerEntity.abilities.allowFlying = playerAbilitiesClientPacket.allowFlying();
		playerEntity.abilities.setFlySpeed((double)playerAbilitiesClientPacket.getFlySpeed());
		playerEntity.abilities.setWalkSpeed(playerAbilitiesClientPacket.getFovModifier());
	}

	@Override
	public void onPlaySound(PlaySoundClientPacket playSoundClientPacket) {
		NetworkThreadUtils.forceMainThread(playSoundClientPacket, this, this.client);
		this.client
			.world
			.playSound(
				this.client.player,
				playSoundClientPacket.getX(),
				playSoundClientPacket.getY(),
				playSoundClientPacket.getZ(),
				playSoundClientPacket.getSound(),
				playSoundClientPacket.getCategory(),
				playSoundClientPacket.getVolume(),
				playSoundClientPacket.getPitch()
			);
	}

	@Override
	public void onPlaySoundFromEntity(PlaySoundFromEntityClientPacket playSoundFromEntityClientPacket) {
		NetworkThreadUtils.forceMainThread(playSoundFromEntityClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(playSoundFromEntityClientPacket.getEntityId());
		if (entity != null) {
			this.client
				.world
				.playSoundFromEntity(
					this.client.player,
					entity,
					playSoundFromEntityClientPacket.getSound(),
					playSoundFromEntityClientPacket.getCategory(),
					playSoundFromEntityClientPacket.getVolume(),
					playSoundFromEntityClientPacket.getPitch()
				);
		}
	}

	@Override
	public void onPlaySoundId(PlaySoundIdClientPacket playSoundIdClientPacket) {
		NetworkThreadUtils.forceMainThread(playSoundIdClientPacket, this, this.client);
		this.client
			.getSoundLoader()
			.play(
				new PositionedSoundInstance(
					playSoundIdClientPacket.getSoundId(),
					playSoundIdClientPacket.getCategory(),
					playSoundIdClientPacket.getVolume(),
					playSoundIdClientPacket.getPitch(),
					false,
					0,
					SoundInstance.AttenuationType.LINEAR,
					(float)playSoundIdClientPacket.getX(),
					(float)playSoundIdClientPacket.getY(),
					(float)playSoundIdClientPacket.getZ()
				)
			);
	}

	@Override
	public void onResourcePackSend(ResourcePackSendClientPacket resourcePackSendClientPacket) {
		String string = resourcePackSendClientPacket.getURL();
		String string2 = resourcePackSendClientPacket.getSHA1();
		if (this.validateResourcePackUrl(string)) {
			if (string.startsWith("level://")) {
				try {
					String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
					File file = new File(this.client.runDirectory, "saves");
					File file2 = new File(file, string3);
					if (file2.isFile()) {
						this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13016);
						CompletableFuture<?> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13015);
			} else {
				ServerEntry serverEntry = this.client.getCurrentServerEntry();
				if (serverEntry != null && serverEntry.getResourcePack() == ServerEntry.ResourcePackState.ENABLED) {
					this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13016);
					this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
				} else if (serverEntry != null && serverEntry.getResourcePack() != ServerEntry.ResourcePackState.PROMPT) {
					this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13018);
				} else {
					this.client.execute(() -> this.client.openGui(new YesNoGui((bl, i) -> {
							this.client = MinecraftClient.getInstance();
							ServerEntry serverEntryx = this.client.getCurrentServerEntry();
							if (bl) {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.ENABLED);
								}

								this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13016);
								this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
							} else {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.DISABLED);
								}

								this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13018);
							}

							ServerList.updateServerListEntry(serverEntryx);
							this.client.openGui(null);
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
			this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13015);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13017)).exceptionally(throwable -> {
			this.sendResourcePackStatus(ResourcePackStatusServerPacket.Status.field_13015);
			return null;
		});
	}

	private void sendResourcePackStatus(ResourcePackStatusServerPacket.Status status) {
		this.connection.sendPacket(new ResourcePackStatusServerPacket(status));
	}

	@Override
	public void onBossBar(BossBarClientPacket bossBarClientPacket) {
		NetworkThreadUtils.forceMainThread(bossBarClientPacket, this, this.client);
		this.client.inGameHud.getHudBossBar().handlePacket(bossBarClientPacket);
	}

	@Override
	public void onCooldownUpdate(CooldownUpdateClientPacket cooldownUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(cooldownUpdateClientPacket, this, this.client);
		if (cooldownUpdateClientPacket.getCooldown() == 0) {
			this.client.player.getItemCooldownManager().remove(cooldownUpdateClientPacket.getItem());
		} else {
			this.client.player.getItemCooldownManager().set(cooldownUpdateClientPacket.getItem(), cooldownUpdateClientPacket.getCooldown());
		}
	}

	@Override
	public void onVehicleMove(VehicleMoveClientPacket vehicleMoveClientPacket) {
		NetworkThreadUtils.forceMainThread(vehicleMoveClientPacket, this, this.client);
		Entity entity = this.client.player.getTopmostRiddenEntity();
		if (entity != this.client.player && entity.method_5787()) {
			entity.setPositionAnglesAndUpdate(
				vehicleMoveClientPacket.getX(),
				vehicleMoveClientPacket.getY(),
				vehicleMoveClientPacket.getZ(),
				vehicleMoveClientPacket.getYaw(),
				vehicleMoveClientPacket.getPitch()
			);
			this.connection.sendPacket(new VehicleMoveServerPacket(entity));
		}
	}

	@Override
	public void onOpenWrittenBook(OpenWrittenBookClientPacket openWrittenBookClientPacket) {
		NetworkThreadUtils.forceMainThread(openWrittenBookClientPacket, this, this.client);
		ItemStack itemStack = this.client.player.getStackInHand(openWrittenBookClientPacket.getHand());
		if (itemStack.getItem() == Items.field_8360) {
			this.client.openGui(new WrittenBookGui(new WrittenBookGui.class_3933(itemStack)));
		}
	}

	@Override
	public void onCustomPayload(CustomPayloadClientPacket customPayloadClientPacket) {
		NetworkThreadUtils.forceMainThread(customPayloadClientPacket, this, this.client);
		Identifier identifier = customPayloadClientPacket.getChannel();
		PacketByteBuf packetByteBuf = null;

		try {
			packetByteBuf = customPayloadClientPacket.getData();
			if (CustomPayloadClientPacket.BRAND.equals(identifier)) {
				this.client.player.setServerBrand(packetByteBuf.readString(32767));
			} else if (CustomPayloadClientPacket.DEBUG_PATH.equals(identifier)) {
				int i = packetByteBuf.readInt();
				float f = packetByteBuf.readFloat();
				Path path = Path.method_34(packetByteBuf);
				this.client.debugRenderer.pathfindingDebugRenderer.method_3869(i, path, f);
			} else if (CustomPayloadClientPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
				long l = packetByteBuf.readVarLong();
				BlockPos blockPos = packetByteBuf.readBlockPos();
				((NeighborUpdateDebugRenderer)this.client.debugRenderer.neighborUpdateDebugRenderer).method_3870(l, blockPos);
			} else if (CustomPayloadClientPacket.DEBUG_CAVES.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				List<BlockPos> list = Lists.<BlockPos>newArrayList();
				List<Float> list2 = Lists.<Float>newArrayList();

				for (int k = 0; k < j; k++) {
					list.add(packetByteBuf.readBlockPos());
					list2.add(packetByteBuf.readFloat());
				}

				this.client.debugRenderer.caveDebugRenderer.method_3704(blockPos2, list, list2);
			} else if (CustomPayloadClientPacket.DEBUG_STRUCTURES.equals(identifier)) {
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
			} else if (CustomPayloadClientPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier)) {
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
	public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateClientPacket scoreboardObjectiveUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardObjectiveUpdateClientPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardObjectiveUpdateClientPacket.getName();
		if (scoreboardObjectiveUpdateClientPacket.getMode() == 0) {
			scoreboard.method_1168(
				string, ScoreboardCriterion.DUMMY, scoreboardObjectiveUpdateClientPacket.getDisplayName(), scoreboardObjectiveUpdateClientPacket.getType()
			);
		} else if (scoreboard.containsObjective(string)) {
			ScoreboardObjective scoreboardObjective = scoreboard.method_1170(string);
			if (scoreboardObjectiveUpdateClientPacket.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (scoreboardObjectiveUpdateClientPacket.getMode() == 2) {
				scoreboardObjective.setCriterionType(scoreboardObjectiveUpdateClientPacket.getType());
				scoreboardObjective.method_1121(scoreboardObjectiveUpdateClientPacket.getDisplayName());
			}
		}
	}

	@Override
	public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateClientPacket scoreboardPlayerUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardPlayerUpdateClientPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardPlayerUpdateClientPacket.getObjectiveName();
		switch (scoreboardPlayerUpdateClientPacket.getUpdateMode()) {
			case field_13431:
				ScoreboardObjective scoreboardObjective = scoreboard.method_1165(string);
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(scoreboardPlayerUpdateClientPacket.getPlayerName(), scoreboardObjective);
				scoreboardPlayerScore.setScore(scoreboardPlayerUpdateClientPacket.getScore());
				break;
			case field_13430:
				scoreboard.resetPlayerScore(scoreboardPlayerUpdateClientPacket.getPlayerName(), scoreboard.method_1170(string));
		}
	}

	@Override
	public void onScoreboardDisplay(ScoreboardDisplayClientPacket scoreboardDisplayClientPacket) {
		NetworkThreadUtils.forceMainThread(scoreboardDisplayClientPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = scoreboardDisplayClientPacket.method_11804();
		ScoreboardObjective scoreboardObjective = string == null ? null : scoreboard.method_1165(string);
		scoreboard.setObjectiveSlot(scoreboardDisplayClientPacket.getLocation(), scoreboardObjective);
	}

	@Override
	public void onTeam(TeamClientPacket teamClientPacket) {
		NetworkThreadUtils.forceMainThread(teamClientPacket, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		ScoreboardTeam scoreboardTeam;
		if (teamClientPacket.getMode() == 0) {
			scoreboardTeam = scoreboard.addTeam(teamClientPacket.getTeamName());
		} else {
			scoreboardTeam = scoreboard.getTeam(teamClientPacket.getTeamName());
		}

		if (teamClientPacket.getMode() == 0 || teamClientPacket.getMode() == 2) {
			scoreboardTeam.setDisplayName(teamClientPacket.getDisplayName());
			scoreboardTeam.setColor(teamClientPacket.getPlayerPrefix());
			scoreboardTeam.setFriendlyFlagsBitwise(teamClientPacket.getFlags());
			AbstractScoreboardTeam.VisibilityRule visibilityRule = AbstractScoreboardTeam.VisibilityRule.method_1213(teamClientPacket.getNameTagVisibilityRule());
			if (visibilityRule != null) {
				scoreboardTeam.setNameTagVisibilityRule(visibilityRule);
			}

			AbstractScoreboardTeam.CollisionRule collisionRule = AbstractScoreboardTeam.CollisionRule.method_1210(teamClientPacket.getCollisionRule());
			if (collisionRule != null) {
				scoreboardTeam.setCollisionRule(collisionRule);
			}

			scoreboardTeam.setPrefix(teamClientPacket.method_11856());
			scoreboardTeam.setSuffix(teamClientPacket.method_11854());
		}

		if (teamClientPacket.getMode() == 0 || teamClientPacket.getMode() == 3) {
			for (String string : teamClientPacket.getPlayerList()) {
				scoreboard.addPlayerToTeam(string, scoreboardTeam);
			}
		}

		if (teamClientPacket.getMode() == 4) {
			for (String string : teamClientPacket.getPlayerList()) {
				scoreboard.removePlayerFromTeam(string, scoreboardTeam);
			}
		}

		if (teamClientPacket.getMode() == 1) {
			scoreboard.removeTeam(scoreboardTeam);
		}
	}

	@Override
	public void onParticle(ParticleClientPacket particleClientPacket) {
		NetworkThreadUtils.forceMainThread(particleClientPacket, this, this.client);
		if (particleClientPacket.getParticleCount() == 0) {
			double d = (double)(particleClientPacket.method_11543() * particleClientPacket.getOffsetX());
			double e = (double)(particleClientPacket.method_11543() * particleClientPacket.getOffsetY());
			double f = (double)(particleClientPacket.method_11543() * particleClientPacket.getOffsetZ());

			try {
				this.world
					.addParticle(
						particleClientPacket.method_11551(),
						particleClientPacket.isLongDistance(),
						particleClientPacket.getX(),
						particleClientPacket.getY(),
						particleClientPacket.getZ(),
						d,
						e,
						f
					);
			} catch (Throwable var17) {
				LOGGER.warn("Could not spawn particle effect {}", particleClientPacket.method_11551());
			}
		} else {
			for (int i = 0; i < particleClientPacket.getParticleCount(); i++) {
				double g = this.random.nextGaussian() * (double)particleClientPacket.getOffsetX();
				double h = this.random.nextGaussian() * (double)particleClientPacket.getOffsetY();
				double j = this.random.nextGaussian() * (double)particleClientPacket.getOffsetZ();
				double k = this.random.nextGaussian() * (double)particleClientPacket.method_11543();
				double l = this.random.nextGaussian() * (double)particleClientPacket.method_11543();
				double m = this.random.nextGaussian() * (double)particleClientPacket.method_11543();

				try {
					this.world
						.addParticle(
							particleClientPacket.method_11551(),
							particleClientPacket.isLongDistance(),
							particleClientPacket.getX() + g,
							particleClientPacket.getY() + h,
							particleClientPacket.getZ() + j,
							k,
							l,
							m
						);
				} catch (Throwable var16) {
					LOGGER.warn("Could not spawn particle effect {}", particleClientPacket.method_11551());
					return;
				}
			}
		}
	}

	@Override
	public void onEntityAttributes(EntityAttributesClientPacket entityAttributesClientPacket) {
		NetworkThreadUtils.forceMainThread(entityAttributesClientPacket, this, this.client);
		Entity entity = this.world.getEntityById(entityAttributesClientPacket.method_11937());
		if (entity != null) {
			if (!(entity instanceof LivingEntity)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
			} else {
				AbstractEntityAttributeContainer abstractEntityAttributeContainer = ((LivingEntity)entity).getAttributeContainer();

				for (EntityAttributesClientPacket.Entry entry : entityAttributesClientPacket.getEntries()) {
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
	public void onCraftResponse(CraftResponseClientPacket craftResponseClientPacket) {
		NetworkThreadUtils.forceMainThread(craftResponseClientPacket, this, this.client);
		Container container = this.client.player.container;
		if (container.syncId == craftResponseClientPacket.getSyncId() && container.method_7622(this.client.player)) {
			this.recipeManager.get(craftResponseClientPacket.getRecipeId()).ifPresent(recipe -> {
				if (this.client.currentGui instanceof RecipeBookProvider) {
					RecipeBookGui recipeBookGui = ((RecipeBookProvider)this.client.currentGui).getRecipeBookGui();
					recipeBookGui.showGhostRecipe(recipe, container.slotList);
				}
			});
		}
	}

	@Override
	public void onLightUpdate(LightUpdateClientPacket lightUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(lightUpdateClientPacket, this, this.client);
		int i = lightUpdateClientPacket.method_11558();
		int j = lightUpdateClientPacket.method_11554();
		LightingProvider lightingProvider = this.world.getChunkProvider().getLightingProvider();
		int k = lightUpdateClientPacket.method_11556();
		int l = lightUpdateClientPacket.method_16124();
		Iterator<byte[]> iterator = lightUpdateClientPacket.method_11555().iterator();
		this.method_2870(i, j, lightingProvider, LightType.SKY_LIGHT, k, l, iterator);
		int m = lightUpdateClientPacket.method_11559();
		int n = lightUpdateClientPacket.method_16125();
		Iterator<byte[]> iterator2 = lightUpdateClientPacket.method_11557().iterator();
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
				this.world.scheduleNeighborChunksRender(i, n, j);
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
