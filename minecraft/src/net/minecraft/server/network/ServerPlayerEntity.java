package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
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
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.CraftingResultSlot;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements ScreenHandlerListener {
	private static final Logger LOGGER = LogManager.getLogger();
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
	private int joinInvulnerabilityTicks = 60;
	private ChatVisibility clientChatVisibility;
	private boolean clientChatColorsEnabled = true;
	private long lastActionTime = Util.getMeasuringTimeMs();
	private Entity cameraEntity;
	private boolean inTeleportationState;
	private boolean seenCredits;
	private final ServerRecipeBook recipeBook = new ServerRecipeBook();
	private Vec3d levitationStartPos;
	private int levitationStartTick;
	private boolean disconnected;
	@Nullable
	private Vec3d enteredNetherPos;
	private ChunkSectionPos cameraPosition = ChunkSectionPos.from(0, 0, 0);
	private RegistryKey<World> spawnPointDimension = World.OVERWORLD;
	@Nullable
	private BlockPos spawnPointPosition;
	private boolean spawnPointSet;
	private int screenHandlerSyncId;
	public boolean skipPacketSlotUpdates;
	public int pingMilliseconds;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager) {
		super(world, world.getSpawnPos(), profile);
		interactionManager.player = this;
		this.interactionManager = interactionManager;
		this.server = server;
		this.statHandler = server.getPlayerManager().createStatHandler(this);
		this.advancementTracker = server.getPlayerManager().getAdvancementTracker(this);
		this.stepHeight = 1.0F;
		this.moveToSpawn(world);
	}

	private void moveToSpawn(ServerWorld world) {
		BlockPos blockPos = world.getSpawnPos();
		if (world.getDimension().hasSkyLight() && world.getServer().getSaveProperties().getGameMode() != GameMode.ADVENTURE) {
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
			int n = this.calculateSpawnOffsetMultiplier(k);
			int o = new Random().nextInt(k);

			for (int p = 0; p < k; p++) {
				int q = (o + n * p) % k;
				int r = q % (i * 2 + 1);
				int s = q / (i * 2 + 1);
				BlockPos blockPos2 = SpawnLocating.findOverworldSpawn(world, blockPos.getX() + r - i, blockPos.getZ() + s - i, false);
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

	private int calculateSpawnOffsetMultiplier(int horizontalSpawnArea) {
		return horizontalSpawnArea <= 16 ? horizontalSpawnArea - 1 : 17;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("playerGameType", 99)) {
			if (this.getServer().shouldForceGameMode()) {
				this.interactionManager.setGameMode(this.getServer().getDefaultGameMode(), GameMode.NOT_SET);
			} else {
				this.interactionManager
					.setGameMode(
						GameMode.byId(tag.getInt("playerGameType")),
						tag.contains("previousPlayerGameType", 3) ? GameMode.byId(tag.getInt("previousPlayerGameType")) : GameMode.NOT_SET
					);
			}
		}

		if (tag.contains("enteredNetherPosition", 10)) {
			CompoundTag compoundTag = tag.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"));
		}

		this.seenCredits = tag.getBoolean("seenCredits");
		if (tag.contains("recipeBook", 10)) {
			this.recipeBook.fromTag(tag.getCompound("recipeBook"), this.server.getRecipeManager());
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}

		if (tag.contains("SpawnX", 99) && tag.contains("SpawnY", 99) && tag.contains("SpawnZ", 99)) {
			this.spawnPointPosition = new BlockPos(tag.getInt("SpawnX"), tag.getInt("SpawnY"), tag.getInt("SpawnZ"));
			this.spawnPointSet = tag.getBoolean("SpawnForced");
			if (tag.contains("SpawnDimension")) {
				this.spawnPointDimension = (RegistryKey<World>)World.CODEC
					.parse(NbtOps.INSTANCE, tag.get("SpawnDimension"))
					.resultOrPartial(LOGGER::error)
					.orElse(World.OVERWORLD);
			}
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		tag.putInt("previousPlayerGameType", this.interactionManager.method_30119().getId());
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
		tag.putString("Dimension", this.world.getRegistryKey().getValue().toString());
		if (this.spawnPointPosition != null) {
			tag.putInt("SpawnX", this.spawnPointPosition.getX());
			tag.putInt("SpawnY", this.spawnPointPosition.getY());
			tag.putInt("SpawnZ", this.spawnPointPosition.getZ());
			tag.putBoolean("SpawnForced", this.spawnPointSet);
			Identifier.CODEC
				.encodeStart(NbtOps.INSTANCE, this.spawnPointDimension.getValue())
				.resultOrPartial(LOGGER::error)
				.ifPresent(tagx -> tag.put("SpawnDimension", tagx));
		}
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

	public void onSpawn() {
		this.currentScreenHandler.addListener(this);
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
		Criteria.ENTER_BLOCK.trigger(this, state);
	}

	@Override
	protected ItemCooldownManager createCooldownManager() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void tick() {
		this.interactionManager.update();
		this.joinInvulnerabilityTicks--;
		if (this.timeUntilRegen > 0) {
			this.timeUntilRegen--;
		}

		this.currentScreenHandler.sendContentUpdates();
		if (!this.world.isClient && !this.currentScreenHandler.canUse(this)) {
			this.closeHandledScreen();
			this.currentScreenHandler = this.playerScreenHandler;
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

		Criteria.TICK.trigger(this);
		if (this.levitationStartPos != null) {
			Criteria.LEVITATION.trigger(this, this.levitationStartPos, this.age - this.levitationStartTick);
		}

		this.advancementTracker.sendUpdate(this);
	}

	public void playerTick() {
		try {
			if (!this.isSpectator() || this.world.isChunkLoaded(this.getBlockPos())) {
				super.tick();
			}

			for (int i = 0; i < this.inventory.size(); i++) {
				ItemStack itemStack = this.inventory.getStack(i);
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
				Criteria.LOCATION.trigger(this);
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
				this.server.getPlayerManager().broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendToTeam(this, text);
			} else if (abstractTeam.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
				this.server.getPlayerManager().sendToOtherTeams(this, text);
			}
		} else {
			this.networkHandler.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.ENTITY_DIED));
		}

		this.dropShoulderEntities();
		if (this.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) {
			this.forgiveMobAnger();
		}

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

	private void forgiveMobAnger() {
		Box box = new Box(this.getBlockPos()).expand(32.0, 10.0, 32.0);
		this.world
			.getEntitiesIncludingUngeneratedChunks(MobEntity.class, box)
			.stream()
			.filter(mobEntity -> mobEntity instanceof Angerable)
			.forEach(mobEntity -> ((Angerable)mobEntity).forgive(this));
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
			Criteria.PLAYER_KILLED_ENTITY.trigger(this, killer, damageSource);
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
			if (!bl && this.joinInvulnerabilityTicks > 0 && source != DamageSource.OUT_OF_WORLD) {
				return false;
			} else {
				if (source instanceof EntityDamageSource) {
					Entity entity = source.getAttacker();
					if (entity instanceof PlayerEntity && !this.shouldDamagePlayer((PlayerEntity)entity)) {
						return false;
					}

					if (entity instanceof PersistentProjectileEntity) {
						PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)entity;
						Entity entity2 = persistentProjectileEntity.getOwner();
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
	public Entity changeDimension(ServerWorld destination) {
		this.inTeleportationState = true;
		ServerWorld serverWorld = this.getServerWorld();
		RegistryKey<World> registryKey = serverWorld.getRegistryKey();
		if (registryKey == World.END && destination.getRegistryKey() == World.OVERWORLD) {
			this.detach();
			this.getServerWorld().removePlayer(this);
			if (!this.notInAnyWorld) {
				this.notInAnyWorld = true;
				this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			WorldProperties worldProperties = destination.getLevelProperties();
			this.networkHandler
				.sendPacket(
					new PlayerRespawnS2CPacket(
						destination.getDimensionRegistryKey(),
						destination.getRegistryKey(),
						BiomeAccess.hashSeed(destination.getSeed()),
						this.interactionManager.getGameMode(),
						this.interactionManager.method_30119(),
						destination.isDebugWorld(),
						destination.isFlat(),
						true
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.getPlayerManager();
			playerManager.sendCommandTree(this);
			serverWorld.removePlayer(this);
			this.removed = false;
			double d = this.getX();
			double e = this.getY();
			double f = this.getZ();
			float g = this.pitch;
			float h = this.yaw;
			float i = h;
			serverWorld.getProfiler().push("moving");
			if (destination.getRegistryKey() == World.END) {
				BlockPos blockPos = ServerWorld.END_SPAWN_POS;
				d = (double)blockPos.getX();
				e = (double)blockPos.getY();
				f = (double)blockPos.getZ();
				h = 90.0F;
				g = 0.0F;
			} else {
				if (registryKey == World.OVERWORLD && destination.getRegistryKey() == World.NETHER) {
					this.enteredNetherPos = this.getPos();
				}

				DimensionType dimensionType = serverWorld.getDimension();
				DimensionType dimensionType2 = destination.getDimension();
				double j = 8.0;
				if (!dimensionType.isShrunk() && dimensionType2.isShrunk()) {
					d /= 8.0;
					f /= 8.0;
				} else if (dimensionType.isShrunk() && !dimensionType2.isShrunk()) {
					d *= 8.0;
					f *= 8.0;
				}
			}

			this.refreshPositionAndAngles(d, e, f, h, g);
			serverWorld.getProfiler().pop();
			serverWorld.getProfiler().push("placing");
			double k = Math.min(-2.9999872E7, destination.getWorldBorder().getBoundWest() + 16.0);
			double j = Math.min(-2.9999872E7, destination.getWorldBorder().getBoundNorth() + 16.0);
			double l = Math.min(2.9999872E7, destination.getWorldBorder().getBoundEast() - 16.0);
			double m = Math.min(2.9999872E7, destination.getWorldBorder().getBoundSouth() - 16.0);
			d = MathHelper.clamp(d, k, l);
			f = MathHelper.clamp(f, j, m);
			this.refreshPositionAndAngles(d, e, f, h, g);
			if (destination.getRegistryKey() == World.END) {
				int n = MathHelper.floor(this.getX());
				int o = MathHelper.floor(this.getY()) - 1;
				int p = MathHelper.floor(this.getZ());
				ServerWorld.createEndSpawnPlatform(destination);
				this.refreshPositionAndAngles((double)n, (double)o, (double)p, h, 0.0F);
				this.setVelocity(Vec3d.ZERO);
			} else if (!destination.getPortalForcer().usePortal(this, i)) {
				destination.getPortalForcer().createPortal(this);
				destination.getPortalForcer().usePortal(this, i);
			}

			serverWorld.getProfiler().pop();
			this.setWorld(destination);
			destination.onPlayerChangeDimension(this);
			this.dimensionChanged(serverWorld);
			this.networkHandler.requestTeleport(this.getX(), this.getY(), this.getZ(), h, g);
			this.interactionManager.setWorld(destination);
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			playerManager.sendWorldInfo(this, destination);
			playerManager.sendPlayerStatus(this);

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
		RegistryKey<World> registryKey = targetWorld.getRegistryKey();
		RegistryKey<World> registryKey2 = this.world.getRegistryKey();
		Criteria.CHANGED_DIMENSION.trigger(this, registryKey, registryKey2);
		if (registryKey == World.NETHER && registryKey2 == World.OVERWORLD && this.enteredNetherPos != null) {
			Criteria.NETHER_TRAVEL.trigger(this, this.enteredNetherPos);
		}

		if (registryKey2 != World.NETHER) {
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
		this.currentScreenHandler.sendContentUpdates();
	}

	@Override
	public Either<PlayerEntity.SleepFailureReason, Unit> trySleep(BlockPos pos) {
		Direction direction = this.world.getBlockState(pos).get(HorizontalFacingBlock.FACING);
		if (this.isSleeping() || !this.isAlive()) {
			return Either.left(PlayerEntity.SleepFailureReason.OTHER_PROBLEM);
		} else if (!this.world.getDimension().isNatural()) {
			return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE);
		} else if (!this.isBedTooFarAway(pos, direction)) {
			return Either.left(PlayerEntity.SleepFailureReason.TOO_FAR_AWAY);
		} else if (this.isBedObstructed(pos, direction)) {
			return Either.left(PlayerEntity.SleepFailureReason.OBSTRUCTED);
		} else {
			this.setSpawnPoint(this.world.getRegistryKey(), pos, false, true);
			if (this.world.isDay()) {
				return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW);
			} else {
				if (!this.isCreative()) {
					double d = 8.0;
					double e = 5.0;
					Vec3d vec3d = Vec3d.ofBottomCenter(pos);
					List<HostileEntity> list = this.world
						.getEntities(
							HostileEntity.class,
							new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
							hostileEntity -> hostileEntity.isAngryAt(this)
						);
					if (!list.isEmpty()) {
						return Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE);
					}
				}

				Either<PlayerEntity.SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> {
					this.incrementStat(Stats.SLEEP_IN_BED);
					Criteria.SLEPT_IN_BED.trigger(this);
				});
				((ServerWorld)this.world).updateSleepingPlayers();
				return either;
			}
		}
	}

	@Override
	public void sleep(BlockPos pos) {
		this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
		super.sleep(pos);
	}

	private boolean isBedTooFarAway(BlockPos pos, Direction direction) {
		return this.isBedTooFarAway(pos) || this.isBedTooFarAway(pos.offset(direction.getOpposite()));
	}

	private boolean isBedTooFarAway(BlockPos pos) {
		Vec3d vec3d = Vec3d.ofBottomCenter(pos);
		return Math.abs(this.getX() - vec3d.getX()) <= 3.0 && Math.abs(this.getY() - vec3d.getY()) <= 2.0 && Math.abs(this.getZ() - vec3d.getZ()) <= 3.0;
	}

	private boolean isBedObstructed(BlockPos pos, Direction direction) {
		BlockPos blockPos = pos.up();
		return !this.doesNotSuffocate(blockPos) || !this.doesNotSuffocate(blockPos.offset(direction.getOpposite()));
	}

	@Override
	public void wakeUp(boolean bl, boolean updateSleepingPlayers) {
		if (this.isSleeping()) {
			this.getServerWorld().getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, 2));
		}

		super.wakeUp(bl, updateSleepingPlayers);
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
	protected void applyMovementEffects(BlockPos pos) {
		if (!this.isSpectator()) {
			super.applyMovementEffects(pos);
		}
	}

	public void handleFall(double heightDifference, boolean onGround) {
		BlockPos blockPos = this.getLandingPos();
		if (this.world.isChunkLoaded(blockPos)) {
			super.fall(heightDifference, onGround, this.world.getBlockState(blockPos), blockPos);
		}
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign) {
		sign.setEditor(this);
		this.networkHandler.sendPacket(new SignEditorOpenS2CPacket(sign.getPos()));
	}

	private void incrementScreenHandlerSyncId() {
		this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
	}

	@Override
	public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) {
		if (factory == null) {
			return OptionalInt.empty();
		} else {
			if (this.currentScreenHandler != this.playerScreenHandler) {
				this.closeHandledScreen();
			}

			this.incrementScreenHandlerSyncId();
			ScreenHandler screenHandler = factory.createMenu(this.screenHandlerSyncId, this.inventory, this);
			if (screenHandler == null) {
				if (this.isSpectator()) {
					this.sendMessage(new TranslatableText("container.spectatorCantOpen").formatted(Formatting.RED), true);
				}

				return OptionalInt.empty();
			} else {
				this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
				screenHandler.addListener(this);
				this.currentScreenHandler = screenHandler;
				return OptionalInt.of(this.screenHandlerSyncId);
			}
		}
	}

	@Override
	public void sendTradeOffers(int syncId, TraderOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
		if (this.currentScreenHandler != this.playerScreenHandler) {
			this.closeHandledScreen();
		}

		this.incrementScreenHandlerSyncId();
		this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, inventory.size(), horse.getEntityId()));
		this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.inventory, inventory, horse);
		this.currentScreenHandler.addListener(this);
	}

	@Override
	public void openEditBookScreen(ItemStack book, Hand hand) {
		Item item = book.getItem();
		if (item == Items.WRITTEN_BOOK) {
			if (WrittenBookItem.resolve(book, this.getCommandSource(), this)) {
				this.currentScreenHandler.sendContentUpdates();
			}

			this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
		}
	}

	@Override
	public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {
		commandBlock.setNeedsUpdatePacket(true);
		this.sendBlockEntityUpdate(commandBlock);
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
		if (!(handler.getSlot(slotId) instanceof CraftingResultSlot)) {
			if (handler == this.playerScreenHandler) {
				Criteria.INVENTORY_CHANGED.trigger(this, this.inventory, stack);
			}

			if (!this.skipPacketSlotUpdates) {
				this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, slotId, stack));
			}
		}
	}

	public void openHandledScreen(ScreenHandler handler) {
		this.onHandlerRegistered(handler, handler.getStacks());
	}

	@Override
	public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {
		this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, stacks));
		this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
		this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
	}

	@Override
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
		this.closeCurrentScreen();
	}

	public void updateCursorStack() {
		if (!this.skipPacketSlotUpdates) {
			this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, this.inventory.getCursorStack()));
		}
	}

	public void closeCurrentScreen() {
		this.currentScreenHandler.close(this);
		this.currentScreenHandler = this.playerScreenHandler;
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

	public void onDisconnect() {
		this.disconnected = true;
		this.removeAllPassengers();
		if (this.isSleeping()) {
			this.wakeUp(true, false);
		}
	}

	public boolean isDisconnected() {
		return this.disconnected;
	}

	public void markHealthDirty() {
		this.syncedHealth = -1.0E8F;
	}

	@Override
	public void sendMessage(Text message, boolean actionBar) {
		this.networkHandler.sendPacket(new GameMessageS2CPacket(message, actionBar ? MessageType.GAME_INFO : MessageType.CHAT, Util.NIL_UUID));
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

		Criteria.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect) {
		super.onStatusEffectUpgraded(effect, reapplyEffect);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getEntityId(), effect));
		Criteria.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		super.onStatusEffectRemoved(effect);
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getEntityId(), effect.getEffectType()));
		if (effect.getEffectType() == StatusEffects.LEVITATION) {
			this.levitationStartPos = null;
		}

		Criteria.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	public void requestTeleport(double destX, double destY, double destZ) {
		this.networkHandler.requestTeleport(destX, destY, destZ, this.yaw, this.pitch);
	}

	@Override
	public void refreshPositionAfterTeleport(double x, double y, double z) {
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
		this.interactionManager.method_30118(gameMode);
		this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, (float)gameMode.getId()));
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
	public void sendSystemMessage(Text message, UUID senderUuid) {
		this.sendMessage(message, MessageType.SYSTEM, senderUuid);
	}

	public void sendMessage(Text message, MessageType type, UUID senderUuid) {
		this.networkHandler
			.sendPacket(
				new GameMessageS2CPacket(message, type, senderUuid),
				future -> {
					if (!future.isSuccess() && (type == MessageType.GAME_INFO || type == MessageType.SYSTEM)) {
						int i = 256;
						String string = message.asTruncatedString(256);
						Text text2 = new LiteralText(string).formatted(Formatting.YELLOW);
						this.networkHandler
							.sendPacket(
								new GameMessageS2CPacket(new TranslatableText("multiplayer.message_not_delivered", text2).formatted(Formatting.RED), MessageType.SYSTEM, senderUuid)
							);
					}
				}
			);
	}

	public String getIp() {
		String string = this.networkHandler.connection.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsC2SPacket packet) {
		this.clientChatVisibility = packet.getChatVisibility();
		this.clientChatColorsEnabled = packet.hasChatColors();
		this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)packet.getPlayerModelBitMask());
		this.getDataTracker().set(MAIN_ARM, (byte)(packet.getMainArm() == Arm.LEFT ? 0 : 1));
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
	public Text getPlayerListName() {
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
			WorldProperties worldProperties = targetWorld.getLevelProperties();
			this.networkHandler
				.sendPacket(
					new PlayerRespawnS2CPacket(
						targetWorld.getDimensionRegistryKey(),
						targetWorld.getRegistryKey(),
						BiomeAccess.hashSeed(targetWorld.getSeed()),
						this.interactionManager.getGameMode(),
						this.interactionManager.method_30119(),
						targetWorld.isDebugWorld(),
						targetWorld.isFlat(),
						true
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
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
			this.server.getPlayerManager().sendPlayerStatus(this);
		}
	}

	@Nullable
	public BlockPos getSpawnPointPosition() {
		return this.spawnPointPosition;
	}

	public RegistryKey<World> getSpawnPointDimension() {
		return this.spawnPointDimension;
	}

	public boolean isSpawnPointSet() {
		return this.spawnPointSet;
	}

	public void setSpawnPoint(RegistryKey<World> dimension, @Nullable BlockPos pos, boolean spawnPointSet, boolean bl) {
		if (pos != null) {
			boolean bl2 = pos.equals(this.spawnPointPosition) && dimension.equals(this.spawnPointDimension);
			if (bl && !bl2) {
				this.sendSystemMessage(new TranslatableText("block.minecraft.set_spawn"), Util.NIL_UUID);
			}

			this.spawnPointPosition = pos;
			this.spawnPointDimension = dimension;
			this.spawnPointSet = spawnPointSet;
		} else {
			this.spawnPointPosition = null;
			this.spawnPointDimension = World.OVERWORLD;
			this.spawnPointSet = false;
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
	public ItemEntity dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
		ItemEntity itemEntity = super.dropItem(stack, throwRandomly, retainOwnership);
		if (itemEntity == null) {
			return null;
		} else {
			this.world.spawnEntity(itemEntity);
			ItemStack itemStack = itemEntity.getStack();
			if (retainOwnership) {
				if (!itemStack.isEmpty()) {
					this.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), stack.getCount());
				}

				this.incrementStat(Stats.DROP);
			}

			return itemEntity;
		}
	}
}
