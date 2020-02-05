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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.NameableContainerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseContainerS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
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
import net.minecraft.world.GameRules;
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
	private final PlayerAdvancementTracker advancementTracker;
	private final ServerStatHandler statHandler;
	private float lastHealthScore = Float.MIN_VALUE;
	private int lastFoodScore = Integer.MIN_VALUE;
	private int lastAirScore = Integer.MIN_VALUE;
	private int lastArmorScore = Integer.MIN_VALUE;
	private int lastLevelScore = Integer.MIN_VALUE;
	private int lastExperienceScore = Integer.MIN_VALUE;
	private float syncedHealth = -1.0E8F;
	private int syncedFoodLevel = -99999999;
	private boolean syncedSaturationIsZero = true;
	private int syncedExperience = -99999999;
	private int field_13998 = 60;
	private ChatVisibility clientChatVisibility;
	private boolean field_13971 = true;
	private long lastActionTime = Util.getMeasuringTimeMs();
	private Entity cameraEntity;
	private boolean inTeleportationState;
	private boolean seenCredits;
	private final ServerRecipeBook recipeBook;
	private Vec3d levitationStartPos;
	private int levitationStartTick;
	private boolean field_13964;
	@Nullable
	private Vec3d enteredNetherPos;
	private ChunkSectionPos cameraPosition = ChunkSectionPos.from(0, 0, 0);
	private int containerSyncId;
	public boolean field_13991;
	public int pingMilliseconds;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager) {
		super(world, profile);
		interactionManager.player = this;
		this.interactionManager = interactionManager;
		this.server = server;
		this.recipeBook = new ServerRecipeBook(server.getRecipeManager());
		this.statHandler = server.getPlayerManager().createStatHandler(this);
		this.advancementTracker = server.getPlayerManager().getAdvancementTracker(this);
		this.stepHeight = 1.0F;
		this.moveToSpawn(world);
	}

	private void moveToSpawn(ServerWorld world) {
		BlockPos blockPos = world.getSpawnPos();
		if (world.dimension.hasSkyLight() && world.getLevelProperties().getGameMode() != GameMode.ADVENTURE) {
			int i = Math.max(0, this.server.getSpawnRadius(world));
			int j = MathHelper.floor(world.getWorldBorder().getDistanceInsideBorder((double)blockPos.getX(), (double)blockPos.getZ()));
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
				BlockPos blockPos2 = world.getDimension().getTopSpawningBlockPosition(blockPos.getX() + r - i, blockPos.getZ() + s - i, false);
				if (blockPos2 != null) {
					this.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
					if (world.doesNotCollide(this)) {
						break;
					}
				}
			}
		} else {
			this.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

			while (!world.doesNotCollide(this) && this.getY() < 255.0) {
				this.updatePosition(this.getX(), this.getY() + 1.0, this.getZ());
			}
		}
	}

	private int method_14244(int i) {
		return i <= 16 ? i - 1 : 17;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("playerGameType", 99)) {
			if (this.getServer().shouldForceGameMode()) {
				this.interactionManager.setGameMode(this.getServer().getDefaultGameMode());
			} else {
				this.interactionManager.setGameMode(GameMode.byId(tag.getInt("playerGameType")));
			}
		}

		if (tag.contains("enteredNetherPosition", 10)) {
			CompoundTag compoundTag = tag.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"));
		}

		this.seenCredits = tag.getBoolean("seenCredits");
		if (tag.contains("recipeBook", 10)) {
			this.recipeBook.fromTag(tag.getCompound("recipeBook"));
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		tag.putBoolean("seenCredits", this.seenCredits);
		if (this.enteredNetherPos != null) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putDouble("x", this.enteredNetherPos.x);
			compoundTag.putDouble("y", this.enteredNetherPos.y);
			compoundTag.putDouble("z", this.enteredNetherPos.z);
			tag.put("enteredNetherPosition", compoundTag);
		}

		Entity entity = this.getRootVehicle();
		Entity entity2 = this.getVehicle();
		if (entity2 != null && entity != this && entity.hasPlayerRider()) {
			CompoundTag compoundTag2 = new CompoundTag();
			CompoundTag compoundTag3 = new CompoundTag();
			entity.saveToTag(compoundTag3);
			compoundTag2.putUuid("Attach", entity2.getUuid());
			compoundTag2.put("Entity", compoundTag3);
			tag.put("RootVehicle", compoundTag2);
		}

		tag.put("recipeBook", this.recipeBook.toTag());
	}

	public void setExperiencePoints(int i) {
		float f = (float)this.getNextLevelExperience();
		float g = (f - 1.0F) / f;
		this.experienceProgress = MathHelper.clamp((float)i / f, 0.0F, g);
		this.syncedExperience = -1;
	}

	public void setExperienceLevel(int level) {
		this.experienceLevel = level;
		this.syncedExperience = -1;
	}

	@Override
	public void addExperienceLevels(int levels) {
		super.addExperienceLevels(levels);
		this.syncedExperience = -1;
	}

	@Override
	public void applyEnchantmentCosts(ItemStack enchantedItem, int experienceLevels) {
		super.applyEnchantmentCosts(enchantedItem, experienceLevels);
		this.syncedExperience = -1;
	}

	public void method_14235() {
		this.container.addListener(this);
	}

	@Override
	public void enterCombat() {
		super.enterCombat();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTER_COMBAT));
	}

	@Override
	public void endCombat() {
		super.endCombat();
		this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END_COMBAT));
	}

	@Override
	protected void onBlockCollision(BlockState state) {
		Criterions.ENTER_BLOCK.trigger(this, state);
	}

	@Override
	protected ItemCooldownManager createCooldownManager() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void tick() {
		this.interactionManager.update();
		this.field_13998--;
		if (this.timeUntilRegen > 0) {
			this.timeUntilRegen--;
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
				this.updatePositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
				this.getServerWorld().getChunkManager().updateCameraPosition(this);
				if (this.shouldDismount()) {
					this.setCameraEntity(this);
				}
			} else {
				this.setCameraEntity(this);
			}
		}

		Criterions.TICK.trigger(this);
		if (this.levitationStartPos != null) {
			Criterions.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
		}

		this.advancementTracker.sendUpdate(this);
	}

	public void playerTick() {
		try {
			if (!this.isSpectator() || this.world.isChunkLoaded(new BlockPos(this))) {
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

			if (this.getHealth() != this.syncedHealth
				|| this.syncedFoodLevel != this.hungerManager.getFoodLevel()
				|| this.hungerManager.getSaturationLevel() == 0.0F != this.syncedSaturationIsZero) {
				this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
				this.syncedHealth = this.getHealth();
				this.syncedFoodLevel = this.hungerManager.getFoodLevel();
				this.syncedSaturationIsZero = this.hungerManager.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorptionAmount() != this.lastHealthScore) {
				this.lastHealthScore = this.getHealth() + this.getAbsorptionAmount();
				this.updateScores(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.lastHealthScore));
			}

			if (this.hungerManager.getFoodLevel() != this.lastFoodScore) {
				this.lastFoodScore = this.hungerManager.getFoodLevel();
				this.updateScores(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.lastFoodScore));
			}

			if (this.getAir() != this.lastAirScore) {
				this.lastAirScore = this.getAir();
				this.updateScores(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.lastAirScore));
			}

			if (this.getArmor() != this.lastArmorScore) {
				this.lastArmorScore = this.getArmor();
				this.updateScores(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.lastArmorScore));
			}

			if (this.totalExperience != this.lastExperienceScore) {
				this.lastExperienceScore = this.totalExperience;
				this.updateScores(ScoreboardCriterion.XP, MathHelper.ceil((float)this.lastExperienceScore));
			}

			if (this.experienceLevel != this.lastLevelScore) {
				this.lastLevelScore = this.experienceLevel;
				this.updateScores(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.lastLevelScore));
			}

			if (this.totalExperience != this.syncedExperience) {
				this.syncedExperience = this.totalExperience;
				this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
			}

			if (this.age % 20 == 0) {
				Criterions.LOCATION.trigger(this);
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	private void updateScores(ScoreboardCriterion criterion, int score) {
		this.getScoreboard().forEachScore(criterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(score));
	}

	@Override
	public void onDeath(DamageSource source) {
		boolean bl = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
		if (bl) {
			Text text = this.getDamageTracker().getDeathMessage();
			this.networkHandler
				.sendPacket(
					new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, text),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = text.asTruncatedString(256);
							Text text2 = new TranslatableText("death.attack.message_too_long", new LiteralText(string).formatted(Formatting.YELLOW));
							Text text3 = new TranslatableText("death.attack.even_more_magic", this.getDisplayName())
								.styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
							this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED, text3));
						}
					}
				);
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			if (abstractTeam == null || abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.ALWAYS) {
				this.server.getPlayerManager().sendToAll(text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendToTeam(this, text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
				this.server.getPlayerManager().sendToOtherTeams(this, text);
			}
		} else {
			this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED));
		}

		this.dropShoulderEntities();
		if (!this.isSpectator()) {
			this.drop(source);
		}

		this.getScoreboard().forEachScore(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
		LivingEntity livingEntity = this.getPrimeAdversary();
		if (livingEntity != null) {
			this.incrementStat(Stats.KILLED_BY.getOrCreateStat(livingEntity.getType()));
			livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, source);
			this.onKilledBy(livingEntity);
		}

		this.world.sendEntityStatus(this, (byte)3);
		this.incrementStat(Stats.DEATHS);
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_DEATH));
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		this.extinguish();
		this.setFlag(0, false);
		this.getDamageTracker().update();
	}

	@Override
	public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
		if (killer != this) {
			super.updateKilledAdvancementCriterion(killer, score, damageSource);
			this.addScore(score);
			String string = this.getEntityName();
			String string2 = killer.getEntityName();
			this.getScoreboard().forEachScore(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			if (killer instanceof PlayerEntity) {
				this.incrementStat(Stats.PLAYER_KILLS);
				this.getScoreboard().forEachScore(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			} else {
				this.incrementStat(Stats.MOB_KILLS);
			}

			this.updateScoreboardScore(string, string2, ScoreboardCriterion.TEAM_KILLS);
			this.updateScoreboardScore(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
			Criterions.PLAYER_KILLED_ENTITY.trigger(this, killer, damageSource);
		}
	}

	private void updateScoreboardScore(String playerName, String team, ScoreboardCriterion[] scoreboardCriterions) {
		Team team2 = this.getScoreboard().getPlayerTeam(team);
		if (team2 != null) {
			int i = team2.getColor().getColorIndex();
			if (i >= 0 && i < scoreboardCriterions.length) {
				this.getScoreboard().forEachScore(scoreboardCriterions[i], playerName, ScoreboardPlayerScore::incrementScore);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			boolean bl = this.server.isDedicated() && this.isPvpEnabled() && "fall".equals(source.name);
			if (!bl && this.field_13998 > 0 && source != DamageSource.OUT_OF_WORLD) {
				return false;
			} else {
				if (source instanceof EntityDamageSource) {
					Entity entity = source.getAttacker();
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

				return super.damage(source, amount);
			}
		}
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity player) {
		return !this.isPvpEnabled() ? false : super.shouldDamagePlayer(player);
	}

	private boolean isPvpEnabled() {
		return this.server.isPvpEnabled();
	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionType newDimension) {
		this.inTeleportationState = true;
		DimensionType dimensionType = this.dimension;
		if (dimensionType == DimensionType.THE_END && newDimension == DimensionType.OVERWORLD) {
			this.detach();
			this.getServerWorld().removePlayer(this);
			if (!this.notInAnyWorld) {
				this.notInAnyWorld = true;
				this.networkHandler.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			ServerWorld serverWorld = this.server.getWorld(dimensionType);
			this.dimension = newDimension;
			ServerWorld serverWorld2 = this.server.getWorld(newDimension);
			LevelProperties levelProperties = serverWorld2.getLevelProperties();
			this.networkHandler
				.sendPacket(
					new PlayerRespawnS2CPacket(
						newDimension, LevelProperties.sha256Hash(levelProperties.getSeed()), levelProperties.getGeneratorType(), this.interactionManager.getGameMode()
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.getPlayerManager();
			playerManager.sendCommandTree(this);
			serverWorld.removePlayer(this);
			this.removed = false;
			double d = this.getX();
			double e = this.getY();
			double f = this.getZ();
			float g = this.pitch;
			float h = this.yaw;
			double i = 8.0;
			float j = h;
			serverWorld.getProfiler().push("moving");
			if (dimensionType == DimensionType.OVERWORLD && newDimension == DimensionType.THE_NETHER) {
				this.enteredNetherPos = this.getPos();
				d /= 8.0;
				f /= 8.0;
			} else if (dimensionType == DimensionType.THE_NETHER && newDimension == DimensionType.OVERWORLD) {
				d *= 8.0;
				f *= 8.0;
			} else if (dimensionType == DimensionType.OVERWORLD && newDimension == DimensionType.THE_END) {
				BlockPos blockPos = serverWorld2.getForcedSpawnPoint();
				d = (double)blockPos.getX();
				e = (double)blockPos.getY();
				f = (double)blockPos.getZ();
				h = 90.0F;
				g = 0.0F;
			}

			this.refreshPositionAndAngles(d, e, f, h, g);
			serverWorld.getProfiler().pop();
			serverWorld.getProfiler().push("placing");
			double k = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
			double l = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
			double m = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
			double n = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
			d = MathHelper.clamp(d, k, m);
			f = MathHelper.clamp(f, l, n);
			this.refreshPositionAndAngles(d, e, f, h, g);
			if (newDimension == DimensionType.THE_END) {
				int o = MathHelper.floor(this.getX());
				int p = MathHelper.floor(this.getY()) - 1;
				int q = MathHelper.floor(this.getZ());
				int r = 1;
				int s = 0;

				for (int t = -2; t <= 2; t++) {
					for (int u = -2; u <= 2; u++) {
						for (int v = -1; v < 3; v++) {
							int w = o + u * 1 + t * 0;
							int x = p + v;
							int y = q + u * 0 - t * 1;
							boolean bl = v < 0;
							serverWorld2.setBlockState(new BlockPos(w, x, y), bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
						}
					}
				}

				this.refreshPositionAndAngles((double)o, (double)p, (double)q, h, 0.0F);
				this.setVelocity(Vec3d.ZERO);
			} else if (!serverWorld2.getPortalForcer().usePortal(this, j)) {
				serverWorld2.getPortalForcer().createPortal(this);
				serverWorld2.getPortalForcer().usePortal(this, j);
			}

			serverWorld.getProfiler().pop();
			this.setWorld(serverWorld2);
			serverWorld2.onPlayerChangeDimension(this);
			this.dimensionChanged(serverWorld);
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), h, g);
			this.interactionManager.setWorld(serverWorld2);
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			playerManager.sendWorldInfo(this, serverWorld2);
			playerManager.method_14594(this);

			for (StatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
				this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), statusEffectInstance));
			}

			this.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
			this.syncedExperience = -1;
			this.syncedHealth = -1.0F;
			this.syncedFoodLevel = -1;
			return this;
		}
	}

	private void dimensionChanged(ServerWorld targetWorld) {
		DimensionType dimensionType = targetWorld.dimension.getType();
		DimensionType dimensionType2 = this.world.dimension.getType();
		Criterions.CHANGED_DIMENSION.trigger(this, dimensionType, dimensionType2);
		if (dimensionType == DimensionType.THE_NETHER && dimensionType2 == DimensionType.OVERWORLD && this.enteredNetherPos != null) {
			Criterions.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
		}

		if (dimensionType2 != DimensionType.THE_NETHER) {
			this.enteredNetherPos = null;
		}
	}

	@Override
	public boolean canBeSpectated(ServerPlayerEntity spectator) {
		if (spectator.isSpectator()) {
			return this.getCameraEntity() == this;
		} else {
			return this.isSpectator() ? false : super.canBeSpectated(spectator);
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
	public void sendPickup(Entity item, int count) {
		super.sendPickup(item, count);
		this.container.sendContentUpdates();
	}

	@Override
	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
		return super.trySleep(pos).ifRight(unit -> {
			this.incrementStat(Stats.SLEEP_IN_BED);
			Criterions.SLEPT_IN_BED.trigger(this);
		});
	}

	@Override
	public void wakeUp(boolean bl, boolean bl2) {
		if (this.isSleeping()) {
			this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
		}

		super.wakeUp(bl, bl2);
		if (this.networkHandler != null) {
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean force) {
		Entity entity2 = this.getVehicle();
		if (!super.startRiding(entity, force)) {
			return false;
		} else {
			Entity entity3 = this.getVehicle();
			if (entity3 != entity2 && this.networkHandler != null) {
				this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
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
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
	}

	@Override
	protected void applyFrostWalker(BlockPos pos) {
		if (!this.isSpectator()) {
			super.applyFrostWalker(pos);
		}
	}

	public void handleFall(double heightDifference, boolean onGround) {
		BlockPos blockPos = this.getLandingPos();
		if (this.world.isChunkLoaded(blockPos)) {
			BlockState blockState = this.world.getBlockState(blockPos);
			super.fall(heightDifference, onGround, blockState, blockPos);
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
	public OptionalInt openContainer(@Nullable NameableContainerFactory nameableContainerFactory) {
		if (nameableContainerFactory == null) {
			return OptionalInt.empty();
		} else {
			if (this.container != this.playerContainer) {
				this.closeContainer();
			}

			this.incrementContainerSyncId();
			Container container = nameableContainerFactory.createMenu(this.containerSyncId, this.inventory, this);
			if (container == null) {
				if (this.isSpectator()) {
					this.addChatMessage(new TranslatableText("container.spectatorCantOpen").formatted(Formatting.RED), true);
				}

				return OptionalInt.empty();
			} else {
				this.networkHandler.sendPacket(new OpenContainerS2CPacket(container.syncId, container.getType(), nameableContainerFactory.getDisplayName()));
				container.addListener(this);
				this.container = container;
				return OptionalInt.of(this.containerSyncId);
			}
		}
	}

	@Override
	public void sendTradeOffers(int syncId, TraderOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
		if (this.container != this.playerContainer) {
			this.closeContainer();
		}

		this.incrementContainerSyncId();
		this.networkHandler.sendPacket(new OpenHorseContainerS2CPacket(this.containerSyncId, inventory.getInvSize(), horseBaseEntity.getEntityId()));
		this.container = new HorseContainer(this.containerSyncId, this.inventory, inventory, horseBaseEntity);
		this.container.addListener(this);
	}

	@Override
	public void openEditBookScreen(ItemStack book, Hand hand) {
		Item item = book.getItem();
		if (item == Items.WRITTEN_BOOK) {
			if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
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
	public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
		if (!(container.getSlot(slotId) instanceof CraftingResultSlot)) {
			if (container == this.playerContainer) {
				Criterions.INVENTORY_CHANGED.trigger(this, this.inventory, itemStack);
			}

			if (!this.field_13991) {
				this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(container.syncId, slotId, itemStack));
			}
		}
	}

	public void openContainer(Container container) {
		this.onContainerRegistered(container, container.getStacks());
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
		this.networkHandler.sendPacket(new InventoryS2CPacket(container.syncId, defaultedList));
		this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
		this.networkHandler.sendPacket(new ContainerPropertyUpdateS2CPacket(container.syncId, propertyId, i));
	}

	@Override
	public void closeContainer() {
		this.networkHandler.sendPacket(new CloseContainerS2CPacket(this.container.syncId));
		this.closeCurrentScreen();
	}

	public void method_14241() {
		if (!this.field_13991) {
			this.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
		}
	}

	public void closeCurrentScreen() {
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
	public void increaseStat(Stat<?> stat, int amount) {
		this.statHandler.increaseStat(this, stat, amount);
		this.getScoreboard().forEachScore(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(amount));
	}

	@Override
	public void resetStat(Stat<?> stat) {
		this.statHandler.setStat(this, stat, 0);
		this.getScoreboard().forEachScore(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
	}

	@Override
	public int unlockRecipes(Collection<Recipe<?>> recipes) {
		return this.recipeBook.unlockRecipes(recipes, this);
	}

	@Override
	public void unlockRecipes(Identifier[] ids) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Identifier identifier : ids) {
			this.server.getRecipeManager().get(identifier).ifPresent(list::add);
		}

		this.unlockRecipes(list);
	}

	@Override
	public int lockRecipes(Collection<Recipe<?>> recipes) {
		return this.recipeBook.lockRecipes(recipes, this);
	}

	@Override
	public void addExperience(int experience) {
		super.addExperience(experience);
		this.syncedExperience = -1;
	}

	public void method_14231() {
		this.field_13964 = true;
		this.removeAllPassengers();
		if (this.isSleeping()) {
			this.wakeUp(true, false);
		}
	}

	public boolean method_14239() {
		return this.field_13964;
	}

	public void markHealthDirty() {
		this.syncedHealth = -1.0E8F;
	}

	@Override
	public void addChatMessage(Text message, boolean bl) {
		this.networkHandler.sendPacket(new ChatMessageS2CPacket(message, bl ? MessageType.GAME_INFO : MessageType.CHAT));
	}

	@Override
	protected void consumeItem() {
		if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
			this.networkHandler.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
			super.consumeItem();
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
		super.lookAt(anchorPoint, target);
		this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, target.x, target.y, target.z));
	}

	public void method_14222(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		Vec3d vec3d = entityAnchor2.positionAt(entity);
		super.lookAt(entityAnchor, vec3d);
		this.networkHandler.sendPacket(new LookAtS2CPacket(entityAnchor, entity, entityAnchor2));
	}

	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
		if (alive) {
			this.inventory.clone(oldPlayer.inventory);
			this.setHealth(oldPlayer.getHealth());
			this.hungerManager = oldPlayer.hungerManager;
			this.experienceLevel = oldPlayer.experienceLevel;
			this.totalExperience = oldPlayer.totalExperience;
			this.experienceProgress = oldPlayer.experienceProgress;
			this.setScore(oldPlayer.getScore());
			this.lastNetherPortalPosition = oldPlayer.lastNetherPortalPosition;
			this.lastNetherPortalDirectionVector = oldPlayer.lastNetherPortalDirectionVector;
			this.lastNetherPortalDirection = oldPlayer.lastNetherPortalDirection;
		} else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
			this.inventory.clone(oldPlayer.inventory);
			this.experienceLevel = oldPlayer.experienceLevel;
			this.totalExperience = oldPlayer.totalExperience;
			this.experienceProgress = oldPlayer.experienceProgress;
			this.setScore(oldPlayer.getScore());
		}

		this.enchantmentTableSeed = oldPlayer.enchantmentTableSeed;
		this.enderChestInventory = oldPlayer.enderChestInventory;
		this.getDataTracker().set(PLAYER_MODEL_PARTS, oldPlayer.getDataTracker().get(PLAYER_MODEL_PARTS));
		this.syncedExperience = -1;
		this.syncedHealth = -1.0F;
		this.syncedFoodLevel = -1;
		this.recipeBook.copyFrom(oldPlayer.recipeBook);
		this.removedEntities.addAll(oldPlayer.removedEntities);
		this.seenCredits = oldPlayer.seenCredits;
		this.enteredNetherPos = oldPlayer.enteredNetherPos;
		this.setShoulderEntityLeft(oldPlayer.getShoulderEntityLeft());
		this.setShoulderEntityRight(oldPlayer.getShoulderEntityRight());
	}

	@Override
	protected void onStatusEffectApplied(StatusEffectInstance effect) {
		super.onStatusEffectApplied(effect);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), effect));
		if (effect.getEffectType() == StatusEffects.LEVITATION) {
			this.levitationStartTick = this.age;
			this.levitationStartPos = this.getPos();
		}

		Criterions.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect) {
		super.onStatusEffectUpgraded(effect, reapplyEffect);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), effect));
		Criterions.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		super.onStatusEffectRemoved(effect);
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), effect.getEffectType()));
		if (effect.getEffectType() == StatusEffects.LEVITATION) {
			this.levitationStartPos = null;
		}

		Criterions.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	public void requestTeleport(double destX, double destY, double destZ) {
		this.networkHandler.requestTeleport(destX, destY, destZ, this.yaw, this.pitch);
	}

	@Override
	public void positAfterTeleport(double x, double y, double z) {
		this.networkHandler.requestTeleport(x, y, z, this.yaw, this.pitch);
		this.networkHandler.syncWithPlayerPosition();
	}

	@Override
	public void addCritParticles(Entity target) {
		this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 4));
	}

	@Override
	public void addEnchantedHitParticles(Entity target) {
		this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(target, 5));
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
		if (gameMode == GameMode.SPECTATOR) {
			this.dropShoulderEntities();
			this.stopRiding();
		} else {
			this.setCameraEntity(this);
		}

		this.sendAbilitiesUpdate();
		this.markEffectsDirty();
	}

	@Override
	public boolean isSpectator() {
		return this.interactionManager.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		return this.interactionManager.getGameMode() == GameMode.CREATIVE;
	}

	@Override
	public void sendMessage(Text message) {
		this.sendChatMessage(message, MessageType.SYSTEM);
	}

	public void sendChatMessage(Text text, MessageType messageType) {
		this.networkHandler
			.sendPacket(
				new ChatMessageS2CPacket(text, messageType),
				future -> {
					if (!future.isSuccess() && (messageType == MessageType.GAME_INFO || messageType == MessageType.SYSTEM)) {
						int i = 256;
						String string = text.asTruncatedString(256);
						Text text2 = new LiteralText(string).formatted(Formatting.YELLOW);
						this.networkHandler
							.sendPacket(new ChatMessageS2CPacket(new TranslatableText("multiplayer.message_not_delivered", text2).formatted(Formatting.RED), MessageType.SYSTEM));
					}
				}
			);
	}

	public String getServerBrand() {
		String string = this.networkHandler.connection.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		this.clientLanguage = clientSettingsC2SPacket.getLanguage();
		this.clientChatVisibility = clientSettingsC2SPacket.getChatVisibility();
		this.field_13971 = clientSettingsC2SPacket.hasChatColors();
		this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
		this.getDataTracker().set(MAIN_ARM, (byte)(clientSettingsC2SPacket.getMainArm() == Arm.LEFT ? 0 : 1));
	}

	public ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	public void sendResourcePackUrl(String url, String hash) {
		this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(url, hash));
	}

	@Override
	protected int getPermissionLevel() {
		return this.server.getPermissionLevel(this.getGameProfile());
	}

	public void updateLastActionTime() {
		this.lastActionTime = Util.getMeasuringTimeMs();
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
			this.requestTeleport(this.cameraEntity.getX(), this.cameraEntity.getY(), this.cameraEntity.getZ());
		}
	}

	@Override
	protected void tickNetherPortalCooldown() {
		if (this.netherPortalCooldown > 0 && !this.inTeleportationState) {
			this.netherPortalCooldown--;
		}
	}

	@Override
	public void attack(Entity target) {
		if (this.interactionManager.getGameMode() == GameMode.SPECTATOR) {
			this.setCameraEntity(target);
		} else {
			super.attack(target);
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

	public PlayerAdvancementTracker getAdvancementTracker() {
		return this.advancementTracker;
	}

	public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {
		this.setCameraEntity(this);
		this.stopRiding();
		if (targetWorld == this.world) {
			this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
		} else {
			ServerWorld serverWorld = this.getServerWorld();
			this.dimension = targetWorld.dimension.getType();
			LevelProperties levelProperties = targetWorld.getLevelProperties();
			this.networkHandler
				.sendPacket(
					new PlayerRespawnS2CPacket(
						this.dimension, LevelProperties.sha256Hash(levelProperties.getSeed()), levelProperties.getGeneratorType(), this.interactionManager.getGameMode()
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			this.server.getPlayerManager().sendCommandTree(this);
			serverWorld.removePlayer(this);
			this.removed = false;
			this.refreshPositionAndAngles(x, y, z, yaw, pitch);
			this.setWorld(targetWorld);
			targetWorld.onPlayerTeleport(this);
			this.dimensionChanged(serverWorld);
			this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
			this.interactionManager.setWorld(targetWorld);
			this.server.getPlayerManager().sendWorldInfo(this, targetWorld);
			this.server.getPlayerManager().method_14594(this);
		}
	}

	public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
		this.networkHandler.sendPacket(packet2);
		this.networkHandler.sendPacket(packet);
	}

	public void sendUnloadChunkPacket(ChunkPos chunkPos) {
		if (this.isAlive()) {
			this.networkHandler.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
		}
	}

	public ChunkSectionPos getCameraPosition() {
		return this.cameraPosition;
	}

	public void setCameraPosition(ChunkSectionPos cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	@Override
	public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
		this.networkHandler.sendPacket(new PlaySoundS2CPacket(event, category, this.getX(), this.getY(), this.getZ(), volume, pitch));
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new PlayerSpawnS2CPacket(this);
	}

	@Override
	public ItemEntity dropItem(ItemStack stack, boolean bl, boolean bl2) {
		ItemEntity itemEntity = super.dropItem(stack, bl, bl2);
		if (itemEntity == null) {
			return null;
		} else {
			this.world.spawnEntity(itemEntity);
			ItemStack itemStack = itemEntity.getStack();
			if (bl2) {
				if (!itemStack.isEmpty()) {
					this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
				}

				this.incrementStat(Stats.DROP);
			}

			return itemEntity;
		}
	}
}
