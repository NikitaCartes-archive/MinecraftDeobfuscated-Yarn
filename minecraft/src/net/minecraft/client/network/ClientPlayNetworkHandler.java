package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
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
import net.minecraft.class_2695;
import net.minecraft.class_2707;
import net.minecraft.class_2713;
import net.minecraft.class_2757;
import net.minecraft.class_2765;
import net.minecraft.class_2770;
import net.minecraft.class_2774;
import net.minecraft.class_2779;
import net.minecraft.class_2793;
import net.minecraft.class_2799;
import net.minecraft.class_2804;
import net.minecraft.class_2809;
import net.minecraft.class_2827;
import net.minecraft.class_2828;
import net.minecraft.class_2833;
import net.minecraft.class_2856;
import net.minecraft.class_300;
import net.minecraft.class_330;
import net.minecraft.class_366;
import net.minecraft.class_452;
import net.minecraft.class_516;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.GuardianAttackSoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.gui.CommandBlockGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.MainMenuGui;
import net.minecraft.client.gui.container.FurnaceGui;
import net.minecraft.client.gui.container.RecipeBookGui;
import net.minecraft.client.gui.container.VillagerGui;
import net.minecraft.client.gui.ingame.CreativeInventoryGui;
import net.minecraft.client.gui.ingame.DeathGui;
import net.minecraft.client.gui.ingame.EditBookGui;
import net.minecraft.client.gui.ingame.RecipeBookProvider;
import net.minecraft.client.gui.menu.DemoGui;
import net.minecraft.client.gui.menu.DisconnectedGui;
import net.minecraft.client.gui.menu.DownloadingTerrainGui;
import net.minecraft.client.gui.menu.EndCreditsGui;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.gui.menu.RealmsGui;
import net.minecraft.client.gui.menu.YesNoGui;
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
import net.minecraft.client.network.packet.MapUpdateClientPacket;
import net.minecraft.client.network.packet.MobSpawnClientPacket;
import net.minecraft.client.network.packet.PaintingSpawnClientPacket;
import net.minecraft.client.network.packet.ParticleClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
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
import net.minecraft.client.network.packet.SetCameraEntityClientPacket;
import net.minecraft.client.network.packet.SignEditorOpenClientPacket;
import net.minecraft.client.network.packet.StatisticsClientPacket;
import net.minecraft.client.network.packet.SynchronizeRecipesClientPacket;
import net.minecraft.client.network.packet.SynchronizeTagsClientPacket;
import net.minecraft.client.network.packet.TeamClientPacket;
import net.minecraft.client.network.packet.TitleClientPacket;
import net.minecraft.client.network.packet.UnloadChunkClientPacket;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.client.network.packet.VehicleMoveClientPacket;
import net.minecraft.client.network.packet.WorldBorderClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateClientPacket;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.client.settings.ServerEntry;
import net.minecraft.client.settings.ServerList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
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
import net.minecraft.entity.decoration.PaintingEntity;
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
import net.minecraft.inventory.HorseInventory;
import net.minecraft.inventory.Inventory;
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
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
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
import net.minecraft.village.SimpleVillager;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
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
			entity = new ThrownPotionEntity(this.world, d, e, f, ItemStack.EMPTY);
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
			this.world, paintingSpawnClientPacket.getPos(), paintingSpawnClientPacket.getFacing(), paintingSpawnClientPacket.method_11221()
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
		this.connection.sendPacket(new class_2793(playerPositionLookClientPacket.getTeleportId()));
		this.connection
			.sendPacket(new class_2828.class_2830(playerEntity.x, playerEntity.getBoundingBox().minY, playerEntity.z, playerEntity.yaw, playerEntity.pitch, false));
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
				chunkDataClientPacket.containsVerticalStrip()
			);

		for (int k = 0; k < 16; k++) {
			this.world.method_16108(i, k, j);
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
			this.world.method_16108(i, k, j);
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
		this.client.hudInGame.addChatMessage(chatMessageClientPacket.getLocation(), chatMessageClientPacket.getMessage());
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
				this.client.particleManager.method_3061(entity, ParticleTypes.field_11205);
			} else if (entityAnimationClientPacket.getAnimationId() == 5) {
				this.client.particleManager.method_3061(entity, ParticleTypes.field_11208);
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
			livingEntity.headPitch = (float)(mobSpawnClientPacket.getVelocityZ() * 360) / 256.0F;
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
						this.client.hudInGame.setOverlayMessage(I18n.translate("mount.onboard", this.client.options.keySneak.method_16007()), false);
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
				this.client.particleManager.method_3051(entity, ParticleTypes.field_11220, 30);
				this.world.playSound(entity.x, entity.y, entity.z, SoundEvents.field_14931, entity.getSoundCategory(), 1.0F, 1.0F, false);
				if (entity == this.client.player) {
					this.client.worldRenderer.method_3189(new ItemStack(Items.field_8288));
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
			this.world.method_2944(scoreboard);
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
		ClientPlayerEntity clientPlayerEntity = this.client.player;
		if ("minecraft:container".equals(guiOpenClientPacket.getType())) {
			clientPlayerEntity.openInventory(new BasicInventory(guiOpenClientPacket.getTitle(), guiOpenClientPacket.getSlotCount()));
			clientPlayerEntity.container.syncId = guiOpenClientPacket.getId();
		} else if ("minecraft:villager".equals(guiOpenClientPacket.getType())) {
			clientPlayerEntity.openVillagerGui(new SimpleVillager(clientPlayerEntity, guiOpenClientPacket.getTitle()));
			clientPlayerEntity.container.syncId = guiOpenClientPacket.getId();
		} else if ("EntityHorse".equals(guiOpenClientPacket.getType())) {
			Entity entity = this.world.getEntityById(guiOpenClientPacket.getHorseId());
			if (entity instanceof HorseBaseEntity) {
				clientPlayerEntity.openHorseInventory((HorseBaseEntity)entity, new HorseInventory(guiOpenClientPacket.getTitle(), guiOpenClientPacket.getSlotCount()));
				clientPlayerEntity.container.syncId = guiOpenClientPacket.getId();
			}
		} else if (!guiOpenClientPacket.hasSlots()) {
			clientPlayerEntity.openContainer(new ClientDummyContainerProvider(guiOpenClientPacket.getType(), guiOpenClientPacket.getTitle()));
			clientPlayerEntity.container.syncId = guiOpenClientPacket.getId();
		} else {
			Inventory inventory = new ClientBasicInventory(guiOpenClientPacket.getType(), guiOpenClientPacket.getTitle(), guiOpenClientPacket.getSlotCount());
			clientPlayerEntity.openInventory(inventory);
			clientPlayerEntity.container.syncId = guiOpenClientPacket.getId();
		}
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
			if (this.client.currentGui instanceof CreativeInventoryGui) {
				CreativeInventoryGui creativeInventoryGui = (CreativeInventoryGui)this.client.currentGui;
				bl = creativeInventoryGui.method_2469() != ItemGroup.INVENTORY.getId();
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
			this.sendPacket(new class_2809(guiActionConfirmClientPacket.getId(), guiActionConfirmClientPacket.getActionId(), true));
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

		this.client.player.openSignEditor((SignBlockEntity)blockEntity);
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
				|| i == 10 && blockEntity instanceof ShulkerBoxBlockEntity
				|| i == 11 && blockEntity instanceof BedBlockEntity
				|| i == 5 && blockEntity instanceof ConduitBlockEntity
				|| i == 12 && blockEntity instanceof JigsawBlockEntity) {
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
				this.client.player.networkHandler.sendPacket(new class_2799(class_2799.class_2800.field_12774));
				this.client.openGui(new DownloadingTerrainGui());
			} else if (j == 1) {
				this.client.openGui(new EndCreditsGui(true, () -> this.client.player.networkHandler.sendPacket(new class_2799(class_2799.class_2800.field_12774))));
			}
		} else if (i == 5) {
			GameOptions gameOptions = this.client.options;
			if (f == 0.0F) {
				this.client.openGui(new DemoGui());
			} else if (f == 101.0F) {
				this.client
					.hudInGame
					.getHudChat()
					.addMessage(
						new TranslatableTextComponent(
							"demo.help.movement",
							gameOptions.keyForward.method_16007(),
							gameOptions.keyLeft.method_16007(),
							gameOptions.keyBack.method_16007(),
							gameOptions.keyRight.method_16007()
						)
					);
			} else if (f == 102.0F) {
				this.client.hudInGame.getHudChat().addMessage(new TranslatableTextComponent("demo.help.jump", gameOptions.keyJump.method_16007()));
			} else if (f == 103.0F) {
				this.client.hudInGame.getHudChat().addMessage(new TranslatableTextComponent("demo.help.inventory", gameOptions.keyInventory.method_16007()));
			} else if (f == 104.0F) {
				this.client.hudInGame.getHudChat().addMessage(new TranslatableTextComponent("demo.day.6", gameOptions.keyScreenshot.method_16007()));
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
			this.world.method_8406(ParticleTypes.field_11250, playerEntity.x, playerEntity.y, playerEntity.z, 0.0, 0.0, 0.0);
			this.world.playSound(playerEntity, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15203, SoundCategory.field_15251, 1.0F, 1.0F);
		}
	}

	@Override
	public void onMapUpdate(MapUpdateClientPacket mapUpdateClientPacket) {
		NetworkThreadUtils.forceMainThread(mapUpdateClientPacket, this, this.client);
		class_330 lv = this.client.worldRenderer.method_3194();
		String string = "map_" + mapUpdateClientPacket.getId();
		MapState mapState = FilledMapItem.method_7997(this.client.world, string);
		if (mapState == null) {
			mapState = new MapState(string);
			if (lv.method_1768(string) != null) {
				MapState mapState2 = lv.method_1772(lv.method_1768(string));
				if (mapState2 != null) {
					mapState = mapState2;
				}
			}

			this.client.world.method_8647(DimensionType.field_13072, string, mapState);
		}

		mapUpdateClientPacket.apply(mapState);
		lv.method_1769(mapState);
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
	public void onAdvancements(class_2779 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		this.advancementHandler.onAdvancements(arg);
	}

	@Override
	public void method_11161(UnlockRecipesClientPacket unlockRecipesClientPacket) {
		NetworkThreadUtils.forceMainThread(unlockRecipesClientPacket, this, this.client);
		Identifier identifier = unlockRecipesClientPacket.method_11793();
		if (identifier == null) {
			this.advancementHandler.method_2864(null, false);
		} else {
			SimpleAdvancement simpleAdvancement = this.advancementHandler.getManager().get(identifier);
			this.advancementHandler.method_2864(simpleAdvancement, false);
		}
	}

	@Override
	public void onCommandTree(CommandTreeClientPacket commandTreeClientPacket) {
		NetworkThreadUtils.forceMainThread(commandTreeClientPacket, this, this.client);
		this.commandDispatcher = new CommandDispatcher<>(commandTreeClientPacket.method_11403());
	}

	@Override
	public void method_11082(class_2770 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		this.client.getSoundLoader().stopSounds(arg.method_11904(), arg.method_11903());
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

		for (Recipe recipe : synchronizeRecipesClientPacket.getRecipes()) {
			this.recipeManager.add(recipe);
		}

		SearchableContainer<class_516> searchableContainer = this.client.getSearchableContainer(SearchManager.field_5496);
		searchableContainer.clear();
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.method_1401();
		clientRecipeBook.method_1393().forEach(searchableContainer::add);
		searchableContainer.reload();
	}

	@Override
	public void method_11092(class_2707 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		Vec3d vec3d = arg.method_11732(this.world);
		if (vec3d != null) {
			this.client.player.method_5702(arg.method_11730(), vec3d);
		}
	}

	@Override
	public void method_11127(class_2774 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		if (!this.field_3692.method_1404(arg.method_11910(), arg.method_11911())) {
			LOGGER.debug("Got unhandled response to tag query {}", arg.method_11910());
		}
	}

	@Override
	public void onStatistics(StatisticsClientPacket statisticsClientPacket) {
		NetworkThreadUtils.forceMainThread(statisticsClientPacket, this, this.client);

		for (Entry<Stat<?>, Integer> entry : statisticsClientPacket.getStatMap().entrySet()) {
			Stat<?> stat = (Stat<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.client.player.getStats().method_15023(this.client.player, stat, i);
		}

		if (this.client.currentGui instanceof class_452) {
			((class_452)this.client.currentGui).method_2300();
		}
	}

	@Override
	public void onUnlockRecipes(class_2713 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
		clientRecipeBook.setGuiOpen(arg.isGuiOpen());
		clientRecipeBook.setFilteringCraftable(arg.isFilteringCraftable());
		clientRecipeBook.setFurnaceGuiOpen(arg.isFurnaceGuiOpen());
		clientRecipeBook.setFurnaceFilteringCraftable(arg.isFurnaceFilteringCraftable());
		class_2713.class_2714 lv = arg.method_11751();
		switch (lv) {
			case field_12417:
				for (Identifier identifierxxx : arg.method_11750()) {
					Recipe recipe = this.recipeManager.get(identifierxxx);
					if (recipe != null) {
						clientRecipeBook.method_14893(recipe);
					}
				}
				break;
			case field_12416:
				for (Identifier identifierx : arg.method_11750()) {
					Recipe recipe = this.recipeManager.get(identifierx);
					if (recipe != null) {
						clientRecipeBook.method_14876(recipe);
					}
				}

				for (Identifier identifierxx : arg.method_11757()) {
					Recipe recipe = this.recipeManager.get(identifierxx);
					if (recipe != null) {
						clientRecipeBook.method_14885(recipe);
					}
				}
				break;
			case field_12415:
				for (Identifier identifier : arg.method_11750()) {
					Recipe recipe = this.recipeManager.get(identifier);
					if (recipe != null) {
						clientRecipeBook.method_14876(recipe);
						clientRecipeBook.method_14885(recipe);
						class_366.method_1985(this.client.getToastManager(), recipe);
					}
				}
		}

		clientRecipeBook.method_1393().forEach(argx -> argx.method_2647(clientRecipeBook));
		if (this.client.currentGui instanceof RecipeBookProvider) {
			((RecipeBookProvider)this.client.currentGui).method_16891();
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

		this.client.getSearchableContainer(SearchManager.ITEMS_TAG).reload();
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
				this.client.hudInGame.setOverlayMessage(string3, false);
				return;
			case RESET:
				this.client.hudInGame.method_1763("", "", -1, -1, -1);
				this.client.hudInGame.method_1742();
				return;
		}

		this.client
			.hudInGame
			.method_1763(string, string2, titleClientPacket.getTicksFadeIn(), titleClientPacket.getTicksDisplay(), titleClientPacket.getTicksFadeOut());
	}

	@Override
	public void onPlayerListHeader(PlayerListHeaderClientPacket playerListHeaderClientPacket) {
		this.client
			.hudInGame
			.getScoreboardWidget()
			.method_1925(playerListHeaderClientPacket.getHeader().getFormattedText().isEmpty() ? null : playerListHeaderClientPacket.getHeader());
		this.client
			.hudInGame
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
		this.sendPacket(new class_2827(keepAliveClientPacket.method_11517()));
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
	public void method_11125(class_2765 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		Entity entity = this.world.getEntityById(arg.method_11883());
		if (entity != null) {
			this.client.world.method_8449(this.client.player, entity, arg.method_11882(), arg.method_11881(), arg.method_11885(), arg.method_11880());
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
						this.method_2873(class_2856.class_2857.field_13016);
						CompletableFuture<?> completableFuture = this.client.getResourcePackDownloader().loadServerPack(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.method_2873(class_2856.class_2857.field_13015);
			} else {
				ServerEntry serverEntry = this.client.getCurrentServerEntry();
				if (serverEntry != null && serverEntry.getResourcePack() == ServerEntry.ResourcePackState.ENABLED) {
					this.method_2873(class_2856.class_2857.field_13016);
					this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
				} else if (serverEntry != null && serverEntry.getResourcePack() != ServerEntry.ResourcePackState.PROMPT) {
					this.method_2873(class_2856.class_2857.field_13018);
				} else {
					this.client.execute(() -> this.client.openGui(new YesNoGui((bl, i) -> {
							this.client = MinecraftClient.getInstance();
							ServerEntry serverEntryx = this.client.getCurrentServerEntry();
							if (bl) {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.ENABLED);
								}

								this.method_2873(class_2856.class_2857.field_13016);
								this.method_2885(this.client.getResourcePackDownloader().download(string, string2));
							} else {
								if (serverEntryx != null) {
									serverEntryx.setResourcePackState(ServerEntry.ResourcePackState.DISABLED);
								}

								this.method_2873(class_2856.class_2857.field_13018);
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
			this.method_2873(class_2856.class_2857.field_13015);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.method_2873(class_2856.class_2857.field_13017)).exceptionally(throwable -> {
			this.method_2873(class_2856.class_2857.field_13015);
			return null;
		});
	}

	private void method_2873(class_2856.class_2857 arg) {
		this.connection.sendPacket(new class_2856(arg));
	}

	@Override
	public void onBossBar(BossBarClientPacket bossBarClientPacket) {
		NetworkThreadUtils.forceMainThread(bossBarClientPacket, this, this.client);
		this.client.hudInGame.getHudBossBar().handlePacket(bossBarClientPacket);
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
			this.connection.sendPacket(new class_2833(entity));
		}
	}

	@Override
	public void onCustomPayload(CustomPayloadClientPacket customPayloadClientPacket) {
		NetworkThreadUtils.forceMainThread(customPayloadClientPacket, this, this.client);
		Identifier identifier = customPayloadClientPacket.getChannel();
		PacketByteBuf packetByteBuf = null;

		try {
			packetByteBuf = customPayloadClientPacket.getData();
			if (CustomPayloadClientPacket.TRADER_LIST.equals(identifier)) {
				try {
					int i = packetByteBuf.readInt();
					Gui gui = this.client.currentGui;
					if (gui instanceof VillagerGui && i == this.client.player.container.syncId) {
						Villager villager = ((VillagerGui)gui).method_2495();
						VillagerRecipeList villagerRecipeList = VillagerRecipeList.readFromBuf(packetByteBuf);
						villager.setRecipeList(villagerRecipeList);
					}
				} catch (IOException var13) {
					LOGGER.error("Couldn't load trade info", (Throwable)var13);
				}
			} else if (CustomPayloadClientPacket.BRAND.equals(identifier)) {
				this.client.player.setServerBrand(packetByteBuf.readString(32767));
			} else if (CustomPayloadClientPacket.BOOK_OPEN.equals(identifier)) {
				Hand hand = packetByteBuf.readEnumConstant(Hand.class);
				ItemStack itemStack = hand == Hand.OFF ? this.client.player.getOffHandStack() : this.client.player.getMainHandStack();
				if (itemStack.getItem() == Items.field_8360) {
					this.client.openGui(new EditBookGui(this.client.player, itemStack, false, hand));
				}
			} else if (CustomPayloadClientPacket.DEBUG_PATH.equals(identifier)) {
				int i = packetByteBuf.readInt();
				float f = packetByteBuf.readFloat();
				Path path = Path.method_34(packetByteBuf);
				this.client.renderDebug.pathfindingDebugRenderer.method_3869(i, path, f);
			} else if (CustomPayloadClientPacket.DEBUG_NEIGHBORS_UPDATE.equals(identifier)) {
				long l = packetByteBuf.readVarLong();
				BlockPos blockPos = packetByteBuf.readBlockPos();
				((NeighborUpdateDebugRenderer)this.client.renderDebug.neighborUpdateDebugRenderer).method_3870(l, blockPos);
			} else if (CustomPayloadClientPacket.DEBUG_CAVES.equals(identifier)) {
				BlockPos blockPos2 = packetByteBuf.readBlockPos();
				int j = packetByteBuf.readInt();
				List<BlockPos> list = Lists.<BlockPos>newArrayList();
				List<Float> list2 = Lists.<Float>newArrayList();

				for (int k = 0; k < j; k++) {
					list.add(packetByteBuf.readBlockPos());
					list2.add(packetByteBuf.readFloat());
				}

				this.client.renderDebug.caveDebugRenderer.method_3704(blockPos2, list, list2);
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

				this.client.renderDebug.structureDebugRenderer.method_3871(mutableIntBoundingBox, list2, list3, i);
			} else if (CustomPayloadClientPacket.DEBUG_WORLDGEN_ATTEMPT.equals(identifier)) {
				((WorldGenAttemptDebugRenderer)this.client.renderDebug.worldGenAttemptDebugRenderer)
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
			scoreboard.method_1168(string, ScoreboardCriterion.DUMMY, scoreboardObjectiveUpdateClientPacket.getValue(), scoreboardObjectiveUpdateClientPacket.getType());
		} else if (scoreboard.containsObjective(string)) {
			ScoreboardObjective scoreboardObjective = scoreboard.method_1170(string);
			if (scoreboardObjectiveUpdateClientPacket.getMode() == 1) {
				scoreboard.removeObjective(scoreboardObjective);
			} else if (scoreboardObjectiveUpdateClientPacket.getMode() == 2) {
				scoreboardObjective.setCriterionType(scoreboardObjectiveUpdateClientPacket.getType());
				scoreboardObjective.method_1121(scoreboardObjectiveUpdateClientPacket.getValue());
			}
		}
	}

	@Override
	public void method_11118(class_2757 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		Scoreboard scoreboard = this.world.getScoreboard();
		String string = arg.method_11864();
		switch (arg.method_11863()) {
			case field_13431:
				ScoreboardObjective scoreboardObjective = scoreboard.method_1165(string);
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(arg.method_11862(), scoreboardObjective);
				scoreboardPlayerScore.setScore(arg.method_11865());
				break;
			case field_13430:
				scoreboard.resetPlayerScore(arg.method_11862(), scoreboard.method_1170(string));
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
			scoreboardTeam.method_1137(teamClientPacket.getDisplayName());
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

			scoreboardTeam.method_1138(teamClientPacket.method_11856());
			scoreboardTeam.method_1139(teamClientPacket.method_11854());
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
					.method_8466(
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
						.method_8466(
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
	public void method_11090(class_2695 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.client);
		Container container = this.client.player.container;
		if (container.syncId == arg.method_11685() && container.method_7622(this.client.player)) {
			Recipe recipe = this.recipeManager.get(arg.method_11684());
			if (recipe != null) {
				if (this.client.currentGui instanceof RecipeBookProvider) {
					RecipeBookGui recipeBookGui = ((RecipeBookProvider)this.client.currentGui).getRecipeBookGui();
					recipeBookGui.method_2596(recipe, container.slotList);
				} else if (this.client.currentGui instanceof FurnaceGui) {
					((FurnaceGui)this.client.currentGui).recipeBook.method_2596(recipe, container.slotList);
				}
			}
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
		this.method_2870(i, j, lightingProvider, LightType.field_9284, k, l, iterator);
		int m = lightUpdateClientPacket.method_11559();
		int n = lightUpdateClientPacket.method_16125();
		Iterator<byte[]> iterator2 = lightUpdateClientPacket.method_11557().iterator();
		this.method_2870(i, j, lightingProvider, LightType.field_9282, m, n, iterator2);
	}

	private void method_2870(int i, int j, LightingProvider lightingProvider, LightType lightType, int k, int l, Iterator<byte[]> iterator) {
		for (int m = 0; m < 18; m++) {
			int n = -1 + m;
			boolean bl = (k & 1 << m) != 0;
			boolean bl2 = (l & 1 << m) != 0;
			if (bl || bl2) {
				lightingProvider.method_15558(lightType, i, n, j, bl ? new class_2804((byte[])((byte[])iterator.next()).clone()) : new class_2804());
				this.world.method_16108(i, n, j);
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

	public GameProfile method_2879() {
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
