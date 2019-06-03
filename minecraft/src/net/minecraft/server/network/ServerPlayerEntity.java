package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.CombatEventS2CPacket;
import net.minecraft.client.network.packet.DifficultyS2CPacket;
import net.minecraft.client.network.packet.EntitiesDestroyS2CPacket;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.client.network.packet.GuiCloseS2CPacket;
import net.minecraft.client.network.packet.GuiOpenS2CPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.client.network.packet.GuiUpdateS2CPacket;
import net.minecraft.client.network.packet.HealthUpdateS2CPacket;
import net.minecraft.client.network.packet.InventoryS2CPacket;
import net.minecraft.client.network.packet.LookAtS2CPacket;
import net.minecraft.client.network.packet.OpenContainerPacket;
import net.minecraft.client.network.packet.OpenWrittenBookS2CPacket;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesS2CPacket;
import net.minecraft.client.network.packet.PlayerRespawnS2CPacket;
import net.minecraft.client.network.packet.PlayerSpawnS2CPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectS2CPacket;
import net.minecraft.client.network.packet.ResourcePackSendS2CPacket;
import net.minecraft.client.network.packet.SetCameraEntityS2CPacket;
import net.minecraft.client.network.packet.SetTradeOffersPacket;
import net.minecraft.client.network.packet.SignEditorOpenS2CPacket;
import net.minecraft.client.network.packet.UnloadChunkS2CPacket;
import net.minecraft.client.network.packet.WorldEventS2CPacket;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements ContainerListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private String clientLanguage = "en_US";
	public ServerPlayNetworkHandler networkHandler;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager interactionManager;
	private final List<Integer> removedEntities = Lists.<Integer>newLinkedList();
	private final PlayerAdvancementTracker advancementManager;
	private final ServerStatHandler statHandler;
	private float field_13963 = Float.MIN_VALUE;
	private int field_13983 = Integer.MIN_VALUE;
	private int field_13968 = Integer.MIN_VALUE;
	private int field_13982 = Integer.MIN_VALUE;
	private int field_13965 = Integer.MIN_VALUE;
	private int field_13980 = Integer.MIN_VALUE;
	private float field_13997 = -1.0E8F;
	private int field_13979 = -99999999;
	private boolean field_13972 = true;
	private int field_13978 = -99999999;
	private int field_13998 = 60;
	private ChatVisibility clientChatVisibility;
	private boolean field_13971 = true;
	private long lastActionTime = SystemUtil.getMeasuringTimeMs();
	private Entity cameraEntity;
	private boolean inTeleportationState;
	private boolean seenCredits;
	private final ServerRecipeBook recipeBook;
	private Vec3d field_13992;
	private int field_13973;
	private boolean field_13964;
	@Nullable
	private Vec3d enteredNetherPos;
	private ChunkSectionPos cameraPosition = ChunkSectionPos.from(0, 0, 0);
	private int containerSyncId;
	public boolean field_13991;
	public int field_13967;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(
		MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager
	) {
		super(serverWorld, gameProfile);
		serverPlayerInteractionManager.player = this;
		this.interactionManager = serverPlayerInteractionManager;
		this.server = minecraftServer;
		this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
		this.statHandler = minecraftServer.getPlayerManager().createStatHandler(this);
		this.advancementManager = minecraftServer.getPlayerManager().getAdvancementManager(this);
		this.stepHeight = 1.0F;
		this.method_14245(serverWorld);
	}

	private void method_14245(ServerWorld serverWorld) {
		BlockPos blockPos = serverWorld.getSpawnPos();
		if (serverWorld.dimension.hasSkyLight() && serverWorld.getLevelProperties().getGameMode() != GameMode.field_9216) {
			int i = Math.max(0, this.server.getSpawnRadius(serverWorld));
			int j = MathHelper.floor(serverWorld.getWorldBorder().contains((double)blockPos.getX(), (double)blockPos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			long l = (long)(i * 2 + 1);
			long m = l * l;
			int k = m > 2147483647L ? Integer.MAX_VALUE : (int)m;
			int n = this.method_14244(k);
			int o = new Random().nextInt(k);

			for (int p = 0; p < k; p++) {
				int q = (o + n * p) % k;
				int r = q % (i * 2 + 1);
				int s = q / (i * 2 + 1);
				BlockPos blockPos2 = serverWorld.getDimension().getTopSpawningBlockPosition(blockPos.getX() + r - i, blockPos.getZ() + s - i, false);
				if (blockPos2 != null) {
					this.setPositionAndAngles(blockPos2, 0.0F, 0.0F);
					if (serverWorld.doesNotCollide(this)) {
						break;
					}
				}
			}
		} else {
			this.setPositionAndAngles(blockPos, 0.0F, 0.0F);

			while (!serverWorld.doesNotCollide(this) && this.y < 255.0) {
				this.setPosition(this.x, this.y + 1.0, this.z);
			}
		}
	}

	private int method_14244(int i) {
		return i <= 16 ? i - 1 : 17;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("playerGameType", 99)) {
			if (this.getServer().shouldForceGameMode()) {
				this.interactionManager.setGameMode(this.getServer().getDefaultGameMode());
			} else {
				this.interactionManager.setGameMode(GameMode.byId(compoundTag.getInt("playerGameType")));
			}
		}

		if (compoundTag.containsKey("enteredNetherPosition", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(compoundTag2.getDouble("x"), compoundTag2.getDouble("y"), compoundTag2.getDouble("z"));
		}

		this.seenCredits = compoundTag.getBoolean("seenCredits");
		if (compoundTag.containsKey("recipeBook", 10)) {
			this.recipeBook.fromTag(compoundTag.getCompound("recipeBook"));
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		compoundTag.putBoolean("seenCredits", this.seenCredits);
		if (this.enteredNetherPos != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag2.putDouble("x", this.enteredNetherPos.x);
			compoundTag2.putDouble("y", this.enteredNetherPos.y);
			compoundTag2.putDouble("z", this.enteredNetherPos.z);
			compoundTag.put("enteredNetherPosition", compoundTag2);
		}

		Entity entity = this.getTopmostVehicle();
		Entity entity2 = this.getVehicle();
		if (entity2 != null && entity != this && entity.method_5817()) {
			CompoundTag compoundTag3 = new CompoundTag();
			CompoundTag compoundTag4 = new CompoundTag();
			entity.saveToTag(compoundTag4);
			compoundTag3.putUuid("Attach", entity2.getUuid());
			compoundTag3.put("Entity", compoundTag4);
			compoundTag.put("RootVehicle", compoundTag3);
		}

		compoundTag.put("recipeBook", this.recipeBook.toTag());
	}

	public void setExperiencePoints(int i) {
		float f = (float)this.getNextLevelExperience();
		float g = (f - 1.0F) / f;
		this.experienceProgress = MathHelper.clamp((float)i / f, 0.0F, g);
		this.field_13978 = -1;
	}

	public void setExperienceLevel(int i) {
		this.experienceLevel = i;
		this.field_13978 = -1;
	}

	@Override
	public void addExperienceLevels(int i) {
		super.addExperienceLevels(i);
		this.field_13978 = -1;
	}

	@Override
	public void applyEnchantmentCosts(ItemStack itemStack, int i) {
		super.applyEnchantmentCosts(itemStack, i);
		this.field_13978 = -1;
	}

	public void method_14235() {
		this.container.addListener(this);
	}

	@Override
	public void method_6000() {
		super.method_6000();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.field_12352));
	}

	@Override
	public void method_6044() {
		super.method_6044();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.field_12353));
	}

	@Override
	protected void onBlockCollision(BlockState blockState) {
		Criterions.ENTER_BLOCK.handle(this, blockState);
	}

	@Override
	protected ItemCooldownManager createCooldownManager() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void tick() {
		this.interactionManager.update();
		this.field_13998--;
		if (this.field_6008 > 0) {
			this.field_6008--;
		}

		this.container.sendContentUpdates();
		if (!this.world.isClient && !this.container.canUse(this)) {
			this.closeContainer();
			this.container = this.playerContainer;
		}

		while (!this.removedEntities.isEmpty()) {
			int i = Math.min(this.removedEntities.size(), Integer.MAX_VALUE);
			int[] is = new int[i];
			Iterator<Integer> iterator = this.removedEntities.iterator();
			int j = 0;

			while (iterator.hasNext() && j < i) {
				is[j++] = (Integer)iterator.next();
				iterator.remove();
			}

			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(is));
		}

		Entity entity = this.getCameraEntity();
		if (entity != this) {
			if (entity.isAlive()) {
				this.setPositionAnglesAndUpdate(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
				this.getServerWorld().method_14178().updateCameraPosition(this);
				if (this.isSneaking()) {
					this.setCameraEntity(this);
				}
			} else {
				this.setCameraEntity(this);
			}
		}

		Criterions.TICK.handle(this);
		if (this.field_13992 != null) {
			Criterions.LEVITATION.handle(this, this.field_13992, this.age - this.field_13973);
		}

		this.advancementManager.sendUpdate(this);
	}

	public void method_14226() {
		try {
			if (!this.isSpectator() || this.world.isBlockLoaded(new BlockPos(this))) {
				super.tick();
			}

			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.getInvStack(i);
				if (itemStack.getItem().isNetworkSynced()) {
					Packet<?> packet = ((NetworkSyncedItem)itemStack.getItem()).createSyncPacket(itemStack, this.world, this);
					if (packet != null) {
						this.networkHandler.sendPacket(packet);
					}
				}
			}

			if (this.getHealth() != this.field_13997
				|| this.field_13979 != this.hungerManager.getFoodLevel()
				|| this.hungerManager.getSaturationLevel() == 0.0F != this.field_13972) {
				this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
				this.field_13997 = this.getHealth();
				this.field_13979 = this.hungerManager.getFoodLevel();
				this.field_13972 = this.hungerManager.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorptionAmount() != this.field_13963) {
				this.field_13963 = this.getHealth() + this.getAbsorptionAmount();
				this.method_14212(ScoreboardCriterion.field_1453, MathHelper.ceil(this.field_13963));
			}

			if (this.hungerManager.getFoodLevel() != this.field_13983) {
				this.field_13983 = this.hungerManager.getFoodLevel();
				this.method_14212(ScoreboardCriterion.field_1464, MathHelper.ceil((float)this.field_13983));
			}

			if (this.getBreath() != this.field_13968) {
				this.field_13968 = this.getBreath();
				this.method_14212(ScoreboardCriterion.field_1459, MathHelper.ceil((float)this.field_13968));
			}

			if (this.getArmor() != this.field_13982) {
				this.field_13982 = this.getArmor();
				this.method_14212(ScoreboardCriterion.field_1452, MathHelper.ceil((float)this.field_13982));
			}

			if (this.totalExperience != this.field_13980) {
				this.field_13980 = this.totalExperience;
				this.method_14212(ScoreboardCriterion.field_1460, MathHelper.ceil((float)this.field_13980));
			}

			if (this.experienceLevel != this.field_13965) {
				this.field_13965 = this.experienceLevel;
				this.method_14212(ScoreboardCriterion.field_1465, MathHelper.ceil((float)this.field_13965));
			}

			if (this.totalExperience != this.field_13978) {
				this.field_13978 = this.totalExperience;
				this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
			}

			if (this.age % 20 == 0) {
				Criterions.LOCATION.handle(this);
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	private void method_14212(ScoreboardCriterion scoreboardCriterion, int i) {
		this.getScoreboard().forEachScore(scoreboardCriterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(i));
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		boolean bl = this.world.getGameRules().getBoolean("showDeathMessages");
		if (bl) {
			Text text = this.getDamageTracker().method_5548();
			this.networkHandler
				.sendPacket(
					new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.field_12350, text),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = text.asTruncatedString(256);
							Text text2 = new TranslatableText("death.attack.message_too_long", new LiteralText(string).formatted(Formatting.field_1054));
							Text text3 = new TranslatableText("death.attack.even_more_magic", this.method_5476())
								.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, text2)));
							this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.field_12350, text3));
						}
					}
				);
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.field_1442) {
				this.server.getPlayerManager().sendToAll(text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.field_1444) {
				this.server.getPlayerManager().sendToTeam(this, text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.field_1446) {
				this.server.getPlayerManager().sendToOtherTeams(this, text);
			}
		} else {
			this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.field_12350));
		}

		this.dropShoulderEntities();
		if (!this.isSpectator()) {
			this.drop(damageSource);
		}

		this.getScoreboard().forEachScore(ScoreboardCriterion.field_1456, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
		LivingEntity livingEntity = this.method_6124();
		if (livingEntity != null) {
			this.incrementStat(Stats.field_15411.getOrCreateStat(livingEntity.getType()));
			livingEntity.updateKilledAdvancementCriterion(this, this.field_6232, damageSource);
			if (!this.world.isClient && livingEntity instanceof WitherEntity) {
				boolean bl2 = false;
				if (this.world.getGameRules().getBoolean("mobGriefing")) {
					BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
					BlockState blockState = Blocks.field_10606.getDefaultState();
					if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
						this.world.setBlockState(blockPos, blockState, 3);
						bl2 = true;
					}
				}

				if (!bl2) {
					ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
					this.world.spawnEntity(itemEntity);
				}
			}
		}

		this.incrementStat(Stats.field_15421);
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15400));
		this.resetStat(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.extinguish();
		this.setFlag(0, false);
		this.getDamageTracker().update();
	}

	@Override
	public void updateKilledAdvancementCriterion(Entity entity, int i, DamageSource damageSource) {
		if (entity != this) {
			super.updateKilledAdvancementCriterion(entity, i, damageSource);
			this.addScore(i);
			String string = this.getEntityName();
			String string2 = entity.getEntityName();
			this.getScoreboard().forEachScore(ScoreboardCriterion.field_1457, string, ScoreboardPlayerScore::incrementScore);
			if (entity instanceof PlayerEntity) {
				this.incrementStat(Stats.field_15404);
				this.getScoreboard().forEachScore(ScoreboardCriterion.field_1463, string, ScoreboardPlayerScore::incrementScore);
			} else {
				this.incrementStat(Stats.field_15414);
			}

			this.method_14227(string, string2, ScoreboardCriterion.TEAM_KILLS);
			this.method_14227(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
			Criterions.PLAYER_KILLED_ENTITY.handle(this, entity, damageSource);
		}
	}

	private void method_14227(String string, String string2, ScoreboardCriterion[] scoreboardCriterions) {
		Team team = this.getScoreboard().getPlayerTeam(string2);
		if (team != null) {
			int i = team.getColor().getColorIndex();
			if (i >= 0 && i < scoreboardCriterions.length) {
				this.getScoreboard().forEachScore(scoreboardCriterions[i], string, ScoreboardPlayerScore::incrementScore);
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			boolean bl = this.server.isDedicated() && this.method_14230() && "fall".equals(damageSource.name);
			if (!bl && this.field_13998 > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
				return false;
			} else {
				if (damageSource instanceof EntityDamageSource) {
					Entity entity = damageSource.getAttacker();
					if (entity instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity)) {
						return false;
					}

					if (entity instanceof ProjectileEntity) {
						ProjectileEntity projectileEntity = (ProjectileEntity)entity;
						Entity entity2 = projectileEntity.getOwner();
						if (entity2 instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity2)) {
							return false;
						}
					}
				}

				return super.damage(damageSource, f);
			}
		}
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity playerEntity) {
		return !this.method_14230() ? false : super.shouldDamagePlayer(playerEntity);
	}

	private boolean method_14230() {
		return this.server.isPvpEnabled();
	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionType dimensionType) {
		this.inTeleportationState = true;
		DimensionType dimensionType2 = this.dimension;
		if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13072) {
			this.detach();
			this.getServerWorld().removePlayer(this);
			if (!this.notInAnyWorld) {
				this.notInAnyWorld = true;
				this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			ServerWorld serverWorld = this.server.getWorld(dimensionType2);
			this.dimension = dimensionType;
			ServerWorld serverWorld2 = this.server.getWorld(dimensionType);
			LevelProperties levelProperties = this.world.getLevelProperties();
			this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(dimensionType, levelProperties.getGeneratorType(), this.interactionManager.getGameMode()));
			this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.getPlayerManager();
			playerManager.sendCommandTree(this);
			serverWorld.removePlayer(this);
			this.removed = false;
			double d = this.x;
			double e = this.y;
			double f = this.z;
			float g = this.pitch;
			float h = this.yaw;
			double i = 8.0;
			float j = h;
			serverWorld.getProfiler().push("moving");
			if (dimensionType2 == DimensionType.field_13072 && dimensionType == DimensionType.field_13076) {
				this.enteredNetherPos = new Vec3d(this.x, this.y, this.z);
				d /= 8.0;
				f /= 8.0;
			} else if (dimensionType2 == DimensionType.field_13076 && dimensionType == DimensionType.field_13072) {
				d *= 8.0;
				f *= 8.0;
			} else if (dimensionType2 == DimensionType.field_13072 && dimensionType == DimensionType.field_13078) {
				BlockPos blockPos = serverWorld2.getForcedSpawnPoint();
				d = (double)blockPos.getX();
				e = (double)blockPos.getY();
				f = (double)blockPos.getZ();
				h = 90.0F;
				g = 0.0F;
			}

			this.setPositionAndAngles(d, e, f, h, g);
			serverWorld.getProfiler().pop();
			serverWorld.getProfiler().push("placing");
			double k = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
			double l = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
			double m = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
			double n = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
			d = MathHelper.clamp(d, k, m);
			f = MathHelper.clamp(f, l, n);
			this.setPositionAndAngles(d, e, f, h, g);
			if (dimensionType == DimensionType.field_13078) {
				int o = MathHelper.floor(this.x);
				int p = MathHelper.floor(this.y) - 1;
				int q = MathHelper.floor(this.z);
				int r = 1;
				int s = 0;

				for (int t = -2; t <= 2; t++) {
					for (int u = -2; u <= 2; u++) {
						for (int v = -1; v < 3; v++) {
							int w = o + u * 1 + t * 0;
							int x = p + v;
							int y = q + u * 0 - t * 1;
							boolean bl = v < 0;
							serverWorld2.setBlockState(new BlockPos(w, x, y), bl ? Blocks.field_10540.getDefaultState() : Blocks.field_10124.getDefaultState());
						}
					}
				}

				this.setPositionAndAngles((double)o, (double)p, (double)q, h, 0.0F);
				this.setVelocity(Vec3d.ZERO);
			} else if (!serverWorld2.getPortalForcer().usePortal(this, j)) {
				serverWorld2.getPortalForcer().createPortal(this);
				serverWorld2.getPortalForcer().usePortal(this, j);
			}

			serverWorld.getProfiler().pop();
			this.setWorld(serverWorld2);
			serverWorld2.method_18211(this);
			this.method_18783(serverWorld);
			this.networkHandler.requestTeleport(this.x, this.y, this.z, h, g);
			this.interactionManager.setWorld(serverWorld2);
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			playerManager.sendWorldInfo(this, serverWorld2);
			playerManager.method_14594(this);

			for (StatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
				this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
			}

			this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
			this.field_13978 = -1;
			this.field_13997 = -1.0F;
			this.field_13979 = -1;
			return this;
		}
	}

	private void method_18783(ServerWorld serverWorld) {
		DimensionType dimensionType = serverWorld.dimension.getType();
		DimensionType dimensionType2 = this.world.dimension.getType();
		Criterions.CHANGED_DIMENSION.handle(this, dimensionType, dimensionType2);
		if (dimensionType == DimensionType.field_13076 && dimensionType2 == DimensionType.field_13072 && this.enteredNetherPos != null) {
			Criterions.NETHER_TRAVEL.handle(this, this.enteredNetherPos);
		}

		if (dimensionType2 != DimensionType.field_13076) {
			this.enteredNetherPos = null;
		}
	}

	@Override
	public boolean canBeSpectated(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.isSpectator()) {
			return this.getCameraEntity() == this;
		} else {
			return this.isSpectator() ? false : super.canBeSpectated(serverPlayerEntity);
		}
	}

	private void sendBlockEntityUpdate(BlockEntity blockEntity) {
		if (blockEntity != null) {
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateS2CPacket != null) {
				this.networkHandler.sendPacket(blockEntityUpdateS2CPacket);
			}
		}
	}

	@Override
	public void sendPickup(Entity entity, int i) {
		super.sendPickup(entity, i);
		this.container.sendContentUpdates();
	}

	@Override
	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos blockPos) {
		return super.trySleep(blockPos).ifRight(unit -> {
			this.incrementStat(Stats.field_15381);
			Criterions.SLEPT_IN_BED.handle(this);
		});
	}

	@Override
	public void wakeUp(boolean bl, boolean bl2, boolean bl3) {
		if (this.isSleeping()) {
			this.getServerWorld().method_14178().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
		}

		super.wakeUp(bl, bl2, bl3);
		if (this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		Entity entity2 = this.getVehicle();
		if (!super.startRiding(entity, bl)) {
			return false;
		} else {
			Entity entity3 = this.getVehicle();
			if (entity3 != entity2 && this.networkHandler != null) {
				this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		Entity entity2 = this.getVehicle();
		if (entity2 != entity && this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	protected void applyFrostWalker(BlockPos blockPos) {
		if (!this.isSpectator()) {
			super.applyFrostWalker(blockPos);
		}
	}

	public void method_14207(double d, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		if (this.world.isBlockLoaded(blockPos)) {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				if (block.matches(BlockTags.field_16584) || block.matches(BlockTags.field_15504) || block instanceof FenceGateBlock) {
					blockPos = blockPos2;
					blockState = blockState2;
				}
			}

			super.fall(d, bl, blockState, blockPos);
		}
	}

	@Override
	public void openEditSignScreen(SignBlockEntity signBlockEntity) {
		signBlockEntity.setEditor(this);
		this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(signBlockEntity.getPos()));
	}

	private void incrementContainerSyncId() {
		this.containerSyncId = this.containerSyncId % 100 + 1;
	}

	@Override
	public OptionalInt openContainer(@Nullable NameableContainerProvider nameableContainerProvider) {
		if (nameableContainerProvider == null) {
			return OptionalInt.empty();
		} else {
			if (this.container != this.playerContainer) {
				this.closeContainer();
			}

			this.incrementContainerSyncId();
			Container container = nameableContainerProvider.createMenu(this.containerSyncId, this.inventory, this);
			if (container == null) {
				if (this.isSpectator()) {
					this.method_7353(new TranslatableText("container.spectatorCantOpen").formatted(Formatting.field_1061), true);
				}

				return OptionalInt.empty();
			} else {
				this.networkHandler.sendPacket(new OpenContainerPacket(container.syncId, container.getType(), nameableContainerProvider.method_5476()));
				container.addListener(this);
				this.container = container;
				return OptionalInt.of(this.containerSyncId);
			}
		}
	}

	@Override
	public void sendTradeOffers(int i, TraderOfferList traderOfferList, int j, int k, boolean bl, boolean bl2) {
		this.networkHandler.sendPacket(new SetTradeOffersPacket(i, traderOfferList, j, k, bl, bl2));
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
		if (this.container != this.playerContainer) {
			this.closeContainer();
		}

		this.incrementContainerSyncId();
		this.networkHandler.sendPacket(new GuiOpenS2CPacket(this.containerSyncId, inventory.getInvSize(), horseBaseEntity.getEntityId()));
		this.container = new HorseContainer(this.containerSyncId, this.inventory, inventory, horseBaseEntity);
		this.container.addListener(this);
	}

	@Override
	public void openEditBookScreen(ItemStack itemStack, Hand hand) {
		Item item = itemStack.getItem();
		if (item == Items.field_8360) {
			if (WrittenBookItem.resolve(itemStack, this.getCommandSource(), this)) {
				this.container.sendContentUpdates();
			}

			this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
		}
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlockBlockEntity) {
		commandBlockBlockEntity.setNeedsUpdatePacket(true);
		this.sendBlockEntityUpdate(commandBlockBlockEntity);
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		if (!(container.getSlot(i) instanceof CraftingResultSlot)) {
			if (container == this.playerContainer) {
				Criterions.INVENTORY_CHANGED.handle(this, this.inventory);
			}

			if (!this.field_13991) {
				this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(container.syncId, i, itemStack));
			}
		}
	}

	public void openContainer(Container container) {
		this.onContainerRegistered(container, container.getStacks());
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
		this.networkHandler.sendPacket(new InventoryS2CPacket(container.syncId, defaultedList));
		this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
		this.networkHandler.sendPacket(new GuiUpdateS2CPacket(container.syncId, i, j));
	}

	@Override
	public void closeContainer() {
		this.networkHandler.sendPacket(new GuiCloseS2CPacket(this.container.syncId));
		this.method_14247();
	}

	public void method_14241() {
		if (!this.field_13991) {
			this.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
		}
	}

	public void method_14247() {
		this.container.close(this);
		this.container = this.playerContainer;
	}

	public void method_14218(float f, float g, boolean bl, boolean bl2) {
		if (this.hasVehicle()) {
			if (f >= -1.0F && f <= 1.0F) {
				this.sidewaysSpeed = f;
			}

			if (g >= -1.0F && g <= 1.0F) {
				this.forwardSpeed = g;
			}

			this.jumping = bl;
			this.setSneaking(bl2);
		}
	}

	@Override
	public void increaseStat(Stat<?> stat, int i) {
		this.statHandler.increaseStat(this, stat, i);
		this.getScoreboard().forEachScore(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(i));
	}

	@Override
	public void resetStat(Stat<?> stat) {
		this.statHandler.setStat(this, stat, 0);
		this.getScoreboard().forEachScore(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
	}

	@Override
	public int unlockRecipes(Collection<Recipe<?>> collection) {
		return this.recipeBook.unlockRecipes(collection, this);
	}

	@Override
	public void unlockRecipes(Identifier[] identifiers) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Identifier identifier : identifiers) {
			this.server.getRecipeManager().get(identifier).ifPresent(list::add);
		}

		this.unlockRecipes(list);
	}

	@Override
	public int lockRecipes(Collection<Recipe<?>> collection) {
		return this.recipeBook.lockRecipes(collection, this);
	}

	@Override
	public void addExperience(int i) {
		super.addExperience(i);
		this.field_13978 = -1;
	}

	public void method_14231() {
		this.field_13964 = true;
		this.removeAllPassengers();
		if (this.isSleeping()) {
			this.wakeUp(true, false, false);
		}
	}

	public boolean method_14239() {
		return this.field_13964;
	}

	public void method_14217() {
		this.field_13997 = -1.0E8F;
	}

	@Override
	public void method_7353(Text text, boolean bl) {
		this.networkHandler.sendPacket(new ChatMessageS2CPacket(text, bl ? MessageType.field_11733 : MessageType.field_11737));
	}

	@Override
	protected void method_6040() {
		if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
			this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
			super.method_6040();
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.lookAt(entityAnchor, vec3d);
		this.networkHandler.sendPacket(new LookAtS2CPacket(entityAnchor, vec3d.x, vec3d.y, vec3d.z));
	}

	public void method_14222(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		Vec3d vec3d = entityAnchor2.positionAt(entity);
		super.lookAt(entityAnchor, vec3d);
		this.networkHandler.sendPacket(new LookAtS2CPacket(entityAnchor, entity, entityAnchor2));
	}

	public void copyFrom(ServerPlayerEntity serverPlayerEntity, boolean bl) {
		if (bl) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.setHealth(serverPlayerEntity.getHealth());
			this.hungerManager = serverPlayerEntity.hungerManager;
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.totalExperience = serverPlayerEntity.totalExperience;
			this.experienceProgress = serverPlayerEntity.experienceProgress;
			this.setScore(serverPlayerEntity.getScore());
			this.lastPortalPosition = serverPlayerEntity.lastPortalPosition;
			this.field_6020 = serverPlayerEntity.field_6020;
			this.field_6028 = serverPlayerEntity.field_6028;
		} else if (this.world.getGameRules().getBoolean("keepInventory") || serverPlayerEntity.isSpectator()) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.totalExperience = serverPlayerEntity.totalExperience;
			this.experienceProgress = serverPlayerEntity.experienceProgress;
			this.setScore(serverPlayerEntity.getScore());
		}

		this.enchantmentTableSeed = serverPlayerEntity.enchantmentTableSeed;
		this.enderChestInventory = serverPlayerEntity.enderChestInventory;
		this.getDataTracker().set(PLAYER_MODEL_BIT_MASK, serverPlayerEntity.getDataTracker().get(PLAYER_MODEL_BIT_MASK));
		this.field_13978 = -1;
		this.field_13997 = -1.0F;
		this.field_13979 = -1;
		this.recipeBook.copyFrom(serverPlayerEntity.recipeBook);
		this.removedEntities.addAll(serverPlayerEntity.removedEntities);
		this.seenCredits = serverPlayerEntity.seenCredits;
		this.enteredNetherPos = serverPlayerEntity.enteredNetherPos;
		this.setShoulderEntityLeft(serverPlayerEntity.getShoulderEntityLeft());
		this.setShoulderEntityRight(serverPlayerEntity.getShoulderEntityRight());
	}

	@Override
	protected void method_6020(StatusEffectInstance statusEffectInstance) {
		super.method_6020(statusEffectInstance);
		this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13973 = this.age;
			this.field_13992 = new Vec3d(this.x, this.y, this.z);
		}

		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
		super.method_6009(statusEffectInstance, bl);
		this.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		super.method_6129(statusEffectInstance);
		this.networkHandler.sendPacket(new RemoveEntityEffectS2CPacket(this.getEntityId(), statusEffectInstance.getEffectType()));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13992 = null;
		}

		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	public void requestTeleport(double d, double e, double f) {
		this.networkHandler.requestTeleport(d, e, f, this.yaw, this.pitch);
	}

	@Override
	public void addCritParticles(Entity entity) {
		this.getServerWorld().method_14178().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(entity, 4));
	}

	@Override
	public void addEnchantedHitParticles(Entity entity) {
		this.getServerWorld().method_14178().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(entity, 5));
	}

	@Override
	public void sendAbilitiesUpdate() {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.world;
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.interactionManager.setGameMode(gameMode);
		this.networkHandler.sendPacket(new GameStateChangeS2CPacket(3, (float)gameMode.getId()));
		if (gameMode == GameMode.field_9219) {
			this.dropShoulderEntities();
			this.stopRiding();
		} else {
			this.setCameraEntity(this);
		}

		this.sendAbilitiesUpdate();
		this.method_6008();
	}

	@Override
	public boolean isSpectator() {
		return this.interactionManager.getGameMode() == GameMode.field_9219;
	}

	@Override
	public boolean isCreative() {
		return this.interactionManager.getGameMode() == GameMode.field_9220;
	}

	@Override
	public void method_9203(Text text) {
		this.sendChatMessage(text, MessageType.field_11735);
	}

	public void sendChatMessage(Text text, MessageType messageType) {
		this.networkHandler
			.sendPacket(
				new ChatMessageS2CPacket(text, messageType),
				future -> {
					if (!future.isSuccess() && (messageType == MessageType.field_11733 || messageType == MessageType.field_11735)) {
						int i = 256;
						String string = text.asTruncatedString(256);
						Text text2 = new LiteralText(string).formatted(Formatting.field_1054);
						this.networkHandler
							.sendPacket(
								new ChatMessageS2CPacket(new TranslatableText("multiplayer.message_not_delivered", text2).formatted(Formatting.field_1061), MessageType.field_11735)
							);
					}
				}
			);
	}

	public String getServerBrand() {
		String string = this.networkHandler.client.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		this.clientLanguage = clientSettingsC2SPacket.getLanguage();
		this.clientChatVisibility = clientSettingsC2SPacket.getChatVisibility();
		this.field_13971 = clientSettingsC2SPacket.method_12135();
		this.getDataTracker().set(PLAYER_MODEL_BIT_MASK, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
		this.getDataTracker().set(MAIN_HAND, (byte)(clientSettingsC2SPacket.getMainHand() == AbsoluteHand.field_6182 ? 0 : 1));
	}

	public ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	public void method_14255(String string, String string2) {
		this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(string, string2));
	}

	@Override
	protected int getPermissionLevel() {
		return this.server.getPermissionLevel(this.getGameProfile());
	}

	public void updateLastActionTime() {
		this.lastActionTime = SystemUtil.getMeasuringTimeMs();
	}

	public ServerStatHandler getStatHandler() {
		return this.statHandler;
	}

	public ServerRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	public void onStoppedTracking(Entity entity) {
		if (entity instanceof PlayerEntity) {
			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(entity.getEntityId()));
		} else {
			this.removedEntities.add(entity.getEntityId());
		}
	}

	public void onStartedTracking(Entity entity) {
		this.removedEntities.remove(entity.getEntityId());
	}

	@Override
	protected void updatePotionVisibility() {
		if (this.isSpectator()) {
			this.clearPotionSwirls();
			this.setInvisible(true);
		} else {
			super.updatePotionVisibility();
		}
	}

	public Entity getCameraEntity() {
		return (Entity)(this.cameraEntity == null ? this : this.cameraEntity);
	}

	public void setCameraEntity(Entity entity) {
		Entity entity2 = this.getCameraEntity();
		this.cameraEntity = (Entity)(entity == null ? this : entity);
		if (entity2 != this.cameraEntity) {
			this.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.cameraEntity));
			this.requestTeleport(this.cameraEntity.x, this.cameraEntity.y, this.cameraEntity.z);
		}
	}

	@Override
	protected void tickPortalCooldown() {
		if (this.portalCooldown > 0 && !this.inTeleportationState) {
			this.portalCooldown--;
		}
	}

	@Override
	public void attack(Entity entity) {
		if (this.interactionManager.getGameMode() == GameMode.field_9219) {
			this.setCameraEntity(entity);
		} else {
			super.attack(entity);
		}
	}

	public long getLastActionTime() {
		return this.lastActionTime;
	}

	@Nullable
	public Text method_14206() {
		return null;
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.resetLastAttackedTicks();
	}

	public boolean isInTeleportationState() {
		return this.inTeleportationState;
	}

	public void onTeleportationDone() {
		this.inTeleportationState = false;
	}

	public void method_14243() {
		this.setFlag(7, true);
	}

	public void method_14229() {
		this.setFlag(7, true);
		this.setFlag(7, false);
	}

	public PlayerAdvancementTracker getAdvancementManager() {
		return this.advancementManager;
	}

	public void teleport(ServerWorld serverWorld, double d, double e, double f, float g, float h) {
		this.setCameraEntity(this);
		this.stopRiding();
		if (serverWorld == this.world) {
			this.networkHandler.requestTeleport(d, e, f, g, h);
		} else {
			ServerWorld serverWorld2 = this.getServerWorld();
			this.dimension = serverWorld.dimension.getType();
			LevelProperties levelProperties = serverWorld.getLevelProperties();
			this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(this.dimension, levelProperties.getGeneratorType(), this.interactionManager.getGameMode()));
			this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			this.server.getPlayerManager().sendCommandTree(this);
			serverWorld2.removePlayer(this);
			this.removed = false;
			this.setPositionAndAngles(d, e, f, g, h);
			this.setWorld(serverWorld);
			serverWorld.method_18207(this);
			this.method_18783(serverWorld2);
			this.networkHandler.requestTeleport(d, e, f, g, h);
			this.interactionManager.setWorld(serverWorld);
			this.server.getPlayerManager().sendWorldInfo(this, serverWorld);
			this.server.getPlayerManager().method_14594(this);
		}
	}

	public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
		this.networkHandler.sendPacket(packet2);
		this.networkHandler.sendPacket(packet);
	}

	public void sendUnloadChunkPacket(ChunkPos chunkPos) {
		this.networkHandler.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
	}

	public ChunkSectionPos getCameraPosition() {
		return this.cameraPosition;
	}

	public void setCameraPosition(ChunkSectionPos chunkSectionPos) {
		this.cameraPosition = chunkSectionPos;
	}

	@Override
	public void playSound(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.networkHandler.sendPacket(new PlaySoundS2CPacket(soundEvent, soundCategory, this.x, this.y, this.z, f, g));
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new PlayerSpawnS2CPacket(this);
	}

	@Override
	public ItemEntity dropItem(ItemStack itemStack, boolean bl, boolean bl2) {
		ItemEntity itemEntity = super.dropItem(itemStack, bl, bl2);
		if (itemEntity == null) {
			return null;
		} else {
			this.world.spawnEntity(itemEntity);
			ItemStack itemStack2 = itemEntity.getStack();
			if (bl2) {
				if (!itemStack2.isEmpty()) {
					this.increaseStat(Stats.field_15405.getOrCreateStat(itemStack2.getItem()), itemStack.getCount());
				}

				this.incrementStat(Stats.field_15406);
			}

			return itemEntity;
		}
	}
}
