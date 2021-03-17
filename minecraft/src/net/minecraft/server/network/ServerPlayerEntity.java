package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.fabricmc.yarn.constants.WorldEvents;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.command.argument.EntityAnchorArgumentType;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
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
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.filter.TextStream;
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
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.PortalUtil;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	public ServerPlayNetworkHandler networkHandler;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager interactionManager;
	private final IntList removedEntities = new IntArrayList();
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
	private ChatVisibility clientChatVisibility = ChatVisibility.FULL;
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
	/**
	 * A chunk section position indicating where the player's client is currently
	 * watching chunks from. Used referentially for the game to update the chunks
	 * watched by this player.
	 * 
	 * @see #getWatchedSection()
	 * @see #setWatchedSection(ChunkSectionPos)
	 */
	private ChunkSectionPos watchedSection = ChunkSectionPos.from(0, 0, 0);
	private RegistryKey<World> spawnPointDimension = World.OVERWORLD;
	@Nullable
	private BlockPos spawnPointPosition;
	private boolean spawnPointSet;
	private float spawnAngle;
	private final TextStream textStream;
	private boolean filterText = true;
	private final ScreenHandlerSyncHandler screenHandlerSyncHandler = new ScreenHandlerSyncHandler() {
		@Override
		public void updateState(ScreenHandler handler, DefaultedList<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, stacks));
			this.sendCursorStackUpdate(cursorStack);

			for (int i = 0; i < properties.length; i++) {
				this.sendPropertyUpdate(handler, i, properties[i]);
			}
		}

		@Override
		public void updateSlot(ScreenHandler handler, int slot, ItemStack stack) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, slot, stack));
		}

		@Override
		public void updateCursorStack(ScreenHandler handler, ItemStack stack) {
			this.sendCursorStackUpdate(stack);
		}

		@Override
		public void updateProperty(ScreenHandler handler, int property, int value) {
			this.sendPropertyUpdate(handler, property, value);
		}

		private void sendPropertyUpdate(ScreenHandler handler, int property, int value) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
		}

		private void sendCursorStackUpdate(ItemStack stack) {
			ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-1, -1, stack));
		}
	};
	private final ScreenHandlerListener screenHandlerListener = new ScreenHandlerListener() {
		@Override
		public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
			if (!(handler.getSlot(slotId) instanceof CraftingResultSlot)) {
				if (handler == ServerPlayerEntity.this.playerScreenHandler) {
					Criteria.INVENTORY_CHANGED.trigger(ServerPlayerEntity.this, ServerPlayerEntity.this.getInventory(), stack);
				}
			}
		}

		@Override
		public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
		}
	};
	private int screenHandlerSyncId;
	public int pingMilliseconds;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile) {
		super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
		this.textStream = server.createFilterer(this);
		this.interactionManager = server.getPlayerInteractionManager(this);
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
					if (world.isSpaceEmpty(this)) {
						break;
					}
				}
			}
		} else {
			this.refreshPositionAndAngles(blockPos, 0.0F, 0.0F);

			while (!world.isSpaceEmpty(this) && this.getY() < (double)(world.getTopY() - 1)) {
				this.setPosition(this.getX(), this.getY() + 1.0, this.getZ());
			}
		}
	}

	private int calculateSpawnOffsetMultiplier(int horizontalSpawnArea) {
		return horizontalSpawnArea <= 16 ? horizontalSpawnArea - 1 : 17;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);
		if (tag.contains("enteredNetherPosition", NbtTypeIds.COMPOUND)) {
			NbtCompound nbtCompound = tag.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(nbtCompound.getDouble("x"), nbtCompound.getDouble("y"), nbtCompound.getDouble("z"));
		}

		this.seenCredits = tag.getBoolean("seenCredits");
		if (tag.contains("recipeBook", NbtTypeIds.COMPOUND)) {
			this.recipeBook.readNbt(tag.getCompound("recipeBook"), this.server.getRecipeManager());
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}

		if (tag.contains("SpawnX", NbtTypeIds.NUMBER) && tag.contains("SpawnY", NbtTypeIds.NUMBER) && tag.contains("SpawnZ", NbtTypeIds.NUMBER)) {
			this.spawnPointPosition = new BlockPos(tag.getInt("SpawnX"), tag.getInt("SpawnY"), tag.getInt("SpawnZ"));
			this.spawnPointSet = tag.getBoolean("SpawnForced");
			this.spawnAngle = tag.getFloat("SpawnAngle");
			if (tag.contains("SpawnDimension")) {
				this.spawnPointDimension = (RegistryKey<World>)World.CODEC
					.parse(NbtOps.INSTANCE, tag.get("SpawnDimension"))
					.resultOrPartial(LOGGER::error)
					.orElse(World.OVERWORLD);
			}
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		this.writeGameModeToNbt(tag);
		tag.putBoolean("seenCredits", this.seenCredits);
		if (this.enteredNetherPos != null) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putDouble("x", this.enteredNetherPos.x);
			nbtCompound.putDouble("y", this.enteredNetherPos.y);
			nbtCompound.putDouble("z", this.enteredNetherPos.z);
			tag.put("enteredNetherPosition", nbtCompound);
		}

		Entity entity = this.getRootVehicle();
		Entity entity2 = this.getVehicle();
		if (entity2 != null && entity != this && entity.hasPlayerRider()) {
			NbtCompound nbtCompound2 = new NbtCompound();
			NbtCompound nbtCompound3 = new NbtCompound();
			entity.saveToTag(nbtCompound3);
			nbtCompound2.putUuid("Attach", entity2.getUuid());
			nbtCompound2.put("Entity", nbtCompound3);
			tag.put("RootVehicle", nbtCompound2);
		}

		tag.put("recipeBook", this.recipeBook.toNbt());
		tag.putString("Dimension", this.world.getRegistryKey().getValue().toString());
		if (this.spawnPointPosition != null) {
			tag.putInt("SpawnX", this.spawnPointPosition.getX());
			tag.putInt("SpawnY", this.spawnPointPosition.getY());
			tag.putInt("SpawnZ", this.spawnPointPosition.getZ());
			tag.putBoolean("SpawnForced", this.spawnPointSet);
			tag.putFloat("SpawnAngle", this.spawnAngle);
			Identifier.CODEC
				.encodeStart(NbtOps.INSTANCE, this.spawnPointDimension.getValue())
				.resultOrPartial(LOGGER::error)
				.ifPresent(nbtElement -> tag.put("SpawnDimension", nbtElement));
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

	private void onSpawn(ScreenHandler screenHandler) {
		screenHandler.addListener(this.screenHandlerListener);
		screenHandler.updateSyncHandler(this.screenHandlerSyncHandler);
	}

	public void method_34225() {
		this.onSpawn(this.playerScreenHandler);
	}

	@Override
	public void enterCombat() {
		super.enterCombat();
		this.networkHandler.sendPacket(new EnterCombatS2CPacket());
	}

	@Override
	public void endCombat() {
		super.endCombat();
		this.networkHandler.sendPacket(new EndCombatS2CPacket(this.getDamageTracker()));
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

		if (!this.removedEntities.isEmpty()) {
			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.removedEntities));
			this.removedEntities.clear();
		}

		Entity entity = this.getCameraEntity();
		if (entity != this) {
			if (entity.isAlive()) {
				this.updatePositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
				this.getServerWorld().getChunkManager().updatePosition(this);
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
			if (!this.isSpectator() || !this.isRegionUnloaded()) {
				super.tick();
			}

			for (int i = 0; i < this.getInventory().size(); i++) {
				ItemStack itemStack = this.getInventory().getStack(i);
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
					new DeathMessageS2CPacket(this.getDamageTracker(), text),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = text.asTruncatedString(256);
							Text text2 = new TranslatableText("death.attack.message_too_long", new LiteralText(string).formatted(Formatting.YELLOW));
							Text text3 = new TranslatableText("death.attack.even_more_magic", this.getDisplayName())
								.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text2)));
							this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), text3));
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
			this.networkHandler.sendPacket(new DeathMessageS2CPacket(this.getDamageTracker(), LiteralText.EMPTY));
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
		this.setOnFire(false);
		this.getDamageTracker().update();
	}

	private void forgiveMobAnger() {
		Box box = new Box(this.getBlockPos()).expand(32.0, 10.0, 32.0);
		this.world
			.getEntitiesByClass(MobEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)
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
	protected TeleportTarget getTeleportTarget(ServerWorld destination) {
		TeleportTarget teleportTarget = super.getTeleportTarget(destination);
		if (teleportTarget != null && this.world.getRegistryKey() == World.OVERWORLD && destination.getRegistryKey() == World.END) {
			Vec3d vec3d = teleportTarget.position.add(0.0, -1.0, 0.0);
			return new TeleportTarget(vec3d, Vec3d.ZERO, 90.0F, 0.0F);
		} else {
			return teleportTarget;
		}
	}

	@Nullable
	@Override
	public Entity moveToWorld(ServerWorld destination) {
		this.inTeleportationState = true;
		ServerWorld serverWorld = this.getServerWorld();
		RegistryKey<World> registryKey = serverWorld.getRegistryKey();
		if (registryKey == World.END && destination.getRegistryKey() == World.OVERWORLD) {
			this.detach();
			this.getServerWorld().removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
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
						destination.getDimension(),
						destination.getRegistryKey(),
						BiomeAccess.hashSeed(destination.getSeed()),
						this.interactionManager.getGameMode(),
						this.interactionManager.getPreviousGameMode(),
						destination.isDebugWorld(),
						destination.isFlat(),
						true
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.getPlayerManager();
			playerManager.sendCommandTree(this);
			serverWorld.removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			TeleportTarget teleportTarget = this.getTeleportTarget(destination);
			if (teleportTarget != null) {
				serverWorld.getProfiler().push("moving");
				if (registryKey == World.OVERWORLD && destination.getRegistryKey() == World.NETHER) {
					this.enteredNetherPos = this.getPos();
				} else if (destination.getRegistryKey() == World.END) {
					this.createEndSpawnPlatform(destination, new BlockPos(teleportTarget.position));
				}

				serverWorld.getProfiler().pop();
				serverWorld.getProfiler().push("placing");
				this.setWorld(destination);
				destination.onPlayerChangeDimension(this);
				this.setRotation(teleportTarget.yaw, teleportTarget.pitch);
				this.refreshPositionAfterTeleport(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z);
				serverWorld.getProfiler().pop();
				this.worldChanged(serverWorld);
				this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
				playerManager.sendWorldInfo(this, destination);
				playerManager.sendPlayerStatus(this);

				for (StatusEffectInstance statusEffectInstance : this.getStatusEffects()) {
					this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), statusEffectInstance));
				}

				this.networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.TRAVEL_THROUGH_PORTAL, BlockPos.ORIGIN, 0, false));
				this.syncedExperience = -1;
				this.syncedHealth = -1.0F;
				this.syncedFoodLevel = -1;
			}

			return this;
		}
	}

	private void createEndSpawnPlatform(ServerWorld world, BlockPos centerPos) {
		BlockPos.Mutable mutable = centerPos.mutableCopy();

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -1; k < 3; k++) {
					BlockState blockState = k == -1 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
					world.setBlockState(mutable.set(centerPos).move(j, k, i), blockState);
				}
			}
		}
	}

	@Override
	protected Optional<PortalUtil.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether) {
		Optional<PortalUtil.Rectangle> optional = super.getPortalRect(destWorld, destPos, destIsNether);
		if (optional.isPresent()) {
			return optional;
		} else {
			Direction.Axis axis = (Direction.Axis)this.world.getBlockState(this.lastNetherPortalPosition).getOrEmpty(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
			Optional<PortalUtil.Rectangle> optional2 = destWorld.getPortalForcer().createPortal(destPos, axis);
			if (!optional2.isPresent()) {
				LOGGER.error("Unable to create a portal, likely target out of worldborder");
			}

			return optional2;
		}
	}

	private void worldChanged(ServerWorld origin) {
		RegistryKey<World> registryKey = origin.getRegistryKey();
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
			this.setSpawnPoint(this.world.getRegistryKey(), pos, this.yaw, false, true);
			if (this.world.isDay()) {
				return Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW);
			} else {
				if (!this.isCreative()) {
					double d = 8.0;
					double e = 5.0;
					Vec3d vec3d = Vec3d.ofBottomCenter(pos);
					List<HostileEntity> list = this.world
						.getEntitiesByClass(
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
				if (!this.getServerWorld().isSleepingEnabled()) {
					this.sendMessage(new TranslatableText("sleep.not_possible"), true);
				}

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
			this.networkHandler.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		}
	}

	@Override
	public void requestTeleportAndDismount(double destX, double destY, double destZ) {
		this.dismountVehicle();
		if (this.networkHandler != null) {
			this.networkHandler.requestTeleportAndDismount(destX, destY, destZ, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.getAbilities().invulnerable && damageSource == DamageSource.WITHER;
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
		if (!this.isRegionUnloaded()) {
			BlockPos blockPos = this.getLandingPos();
			super.fall(heightDifference, onGround, this.world.getBlockState(blockPos), blockPos);
		}
	}

	@Override
	public void openEditSignScreen(SignBlockEntity sign) {
		sign.setEditor(this);
		this.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, sign.getPos()));
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
			ScreenHandler screenHandler = factory.createMenu(this.screenHandlerSyncId, this.getInventory(), this);
			if (screenHandler == null) {
				if (this.isSpectator()) {
					this.sendMessage(new TranslatableText("container.spectatorCantOpen").formatted(Formatting.RED), true);
				}

				return OptionalInt.empty();
			} else {
				this.networkHandler.sendPacket(new OpenScreenS2CPacket(screenHandler.syncId, screenHandler.getType(), factory.getDisplayName()));
				this.onSpawn(screenHandler);
				this.currentScreenHandler = screenHandler;
				return OptionalInt.of(this.screenHandlerSyncId);
			}
		}
	}

	@Override
	public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {
		this.networkHandler.sendPacket(new SetTradeOffersS2CPacket(syncId, offers, levelProgress, experience, leveled, refreshable));
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horse, Inventory inventory) {
		if (this.currentScreenHandler != this.playerScreenHandler) {
			this.closeHandledScreen();
		}

		this.incrementScreenHandlerSyncId();
		this.networkHandler.sendPacket(new OpenHorseScreenS2CPacket(this.screenHandlerSyncId, inventory.size(), horse.getId()));
		this.currentScreenHandler = new HorseScreenHandler(this.screenHandlerSyncId, this.getInventory(), inventory, horse);
		this.onSpawn(this.currentScreenHandler);
	}

	@Override
	public void useBook(ItemStack book, Hand hand) {
		if (book.isOf(Items.WRITTEN_BOOK)) {
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
	public void closeHandledScreen() {
		this.networkHandler.sendPacket(new CloseScreenS2CPacket(this.currentScreenHandler.syncId));
		this.closeScreenHandler();
	}

	/**
	 * Runs closing tasks for the current screen handler and
	 * sets it to the {@code playerScreenHandler}.
	 */
	public void closeScreenHandler() {
		this.currentScreenHandler.close(this);
		this.playerScreenHandler.copySharedSlots(this.currentScreenHandler);
		this.currentScreenHandler = this.playerScreenHandler;
	}

	public void updateInput(float sidewaysSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
		if (this.hasVehicle()) {
			if (sidewaysSpeed >= -1.0F && sidewaysSpeed <= 1.0F) {
				this.sidewaysSpeed = sidewaysSpeed;
			}

			if (forwardSpeed >= -1.0F && forwardSpeed <= 1.0F) {
				this.forwardSpeed = forwardSpeed;
			}

			this.jumping = jumping;
			this.setSneaking(sneaking);
		}
	}

	@Override
	public void increaseStat(Stat<?> stat, int amount) {
		this.statHandler.increaseStat(this, stat, amount);
		this.getScoreboard().forEachScore(stat, this.getEntityName(), score -> score.incrementScore(amount));
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
		this.sendMessage(message, actionBar ? MessageType.GAME_INFO : MessageType.CHAT, Util.NIL_UUID);
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

	public void lookAtEntity(EntityAnchorArgumentType.EntityAnchor anchorPoint, Entity targetEntity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {
		Vec3d vec3d = targetAnchor.positionAt(targetEntity);
		super.lookAt(anchorPoint, vec3d);
		this.networkHandler.sendPacket(new LookAtS2CPacket(anchorPoint, targetEntity, targetAnchor));
	}

	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive) {
		this.filterText = oldPlayer.filterText;
		this.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode(), oldPlayer.interactionManager.getPreviousGameMode());
		if (alive) {
			this.getInventory().clone(oldPlayer.getInventory());
			this.setHealth(oldPlayer.getHealth());
			this.hungerManager = oldPlayer.hungerManager;
			this.experienceLevel = oldPlayer.experienceLevel;
			this.totalExperience = oldPlayer.totalExperience;
			this.experienceProgress = oldPlayer.experienceProgress;
			this.setScore(oldPlayer.getScore());
			this.lastNetherPortalPosition = oldPlayer.lastNetherPortalPosition;
		} else if (this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator()) {
			this.getInventory().clone(oldPlayer.getInventory());
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
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect));
		if (effect.getEffectType() == StatusEffects.LEVITATION) {
			this.levitationStartTick = this.age;
			this.levitationStartPos = this.getPos();
		}

		Criteria.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect) {
		super.onStatusEffectUpgraded(effect, reapplyEffect);
		this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect));
		Criteria.EFFECTS_CHANGED.trigger(this);
	}

	@Override
	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		super.onStatusEffectRemoved(effect);
		this.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
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
		this.requestTeleport(x, y, z);
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
			this.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.world;
	}

	public boolean changeGameMode(GameMode gameMode) {
		if (!this.interactionManager.changeGameMode(gameMode)) {
			return false;
		} else {
			this.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, (float)gameMode.getId()));
			if (gameMode == GameMode.SPECTATOR) {
				this.dropShoulderEntities();
				this.stopRiding();
			} else {
				this.setCameraEntity(this);
			}

			this.sendAbilitiesUpdate();
			this.markEffectsDirty();
			return true;
		}
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
		if (this.acceptsMessage(type)) {
			this.networkHandler
				.sendPacket(
					new GameMessageS2CPacket(message, type, senderUuid),
					future -> {
						if (!future.isSuccess() && (type == MessageType.GAME_INFO || type == MessageType.SYSTEM) && this.acceptsMessage(MessageType.SYSTEM)) {
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
	}

	public String getIp() {
		String string = this.networkHandler.connection.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsC2SPacket packet) {
		this.clientChatVisibility = packet.getChatVisibility();
		this.clientChatColorsEnabled = packet.hasChatColors();
		this.filterText = packet.shouldFilterText();
		this.getDataTracker().set(PLAYER_MODEL_PARTS, (byte)packet.getPlayerModelBitMask());
		this.getDataTracker().set(MAIN_ARM, (byte)(packet.getMainArm() == Arm.LEFT ? 0 : 1));
	}

	public ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	private boolean acceptsMessage(MessageType type) {
		switch (this.clientChatVisibility) {
			case HIDDEN:
				return type == MessageType.GAME_INFO;
			case SYSTEM:
				return type == MessageType.SYSTEM || type == MessageType.GAME_INFO;
			case FULL:
			default:
				return true;
		}
	}

	public void sendResourcePackUrl(String url, String hash, boolean required) {
		this.networkHandler.sendPacket(new ResourcePackSendS2CPacket(url, hash, required));
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
			this.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(entity.getId()));
		} else {
			this.removedEntities.add(entity.getId());
		}
	}

	public void onStartedTracking(Entity entity) {
		this.removedEntities.rem(entity.getId());
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
		if (!this.inTeleportationState) {
			super.tickNetherPortalCooldown();
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
						targetWorld.getDimension(),
						targetWorld.getRegistryKey(),
						BiomeAccess.hashSeed(targetWorld.getSeed()),
						this.interactionManager.getGameMode(),
						this.interactionManager.getPreviousGameMode(),
						targetWorld.isDebugWorld(),
						targetWorld.isFlat(),
						true
					)
				);
			this.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
			this.server.getPlayerManager().sendCommandTree(this);
			serverWorld.removePlayer(this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			this.refreshPositionAndAngles(x, y, z, yaw, pitch);
			this.setWorld(targetWorld);
			targetWorld.onPlayerTeleport(this);
			this.worldChanged(serverWorld);
			this.networkHandler.requestTeleport(x, y, z, yaw, pitch);
			this.server.getPlayerManager().sendWorldInfo(this, targetWorld);
			this.server.getPlayerManager().sendPlayerStatus(this);
		}
	}

	@Nullable
	public BlockPos getSpawnPointPosition() {
		return this.spawnPointPosition;
	}

	public float getSpawnAngle() {
		return this.spawnAngle;
	}

	public RegistryKey<World> getSpawnPointDimension() {
		return this.spawnPointDimension;
	}

	public boolean isSpawnPointSet() {
		return this.spawnPointSet;
	}

	public void setSpawnPoint(RegistryKey<World> dimension, @Nullable BlockPos pos, float angle, boolean spawnPointSet, boolean bl) {
		if (pos != null) {
			boolean bl2 = pos.equals(this.spawnPointPosition) && dimension.equals(this.spawnPointDimension);
			if (bl && !bl2) {
				this.sendSystemMessage(new TranslatableText("block.minecraft.set_spawn"), Util.NIL_UUID);
			}

			this.spawnPointPosition = pos;
			this.spawnPointDimension = dimension;
			this.spawnAngle = angle;
			this.spawnPointSet = spawnPointSet;
		} else {
			this.spawnPointPosition = null;
			this.spawnPointDimension = World.OVERWORLD;
			this.spawnAngle = 0.0F;
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

	/**
	 * Returns the chunk section position the player's client is currently watching
	 * from. This may differ from the chunk section the player is currently in.
	 * 
	 * <p>This is only for chunk loading (watching) purpose. This is updated together
	 * with entity tracking, but they are separate mechanisms.
	 * 
	 * @see #watchedSection
	 * @see #setWatchedSection(ChunkSectionPos)
	 */
	public ChunkSectionPos getWatchedSection() {
		return this.watchedSection;
	}

	/**
	 * Sets the chunk section position the player's client is currently watching
	 * from. This is usually called when the player moves to a new chunk section.
	 * 
	 * @see #watchedSection
	 * @see #getWatchedSection()
	 * 
	 * @param section the updated section position
	 */
	public void setWatchedSection(ChunkSectionPos section) {
		this.watchedSection = section;
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

	public TextStream getTextStream() {
		return this.textStream;
	}

	public void setWorld(ServerWorld world) {
		this.world = world;
		this.interactionManager.setWorld(world);
	}

	@Nullable
	private static GameMode gameModeFromNbt(@Nullable NbtCompound tag, String key) {
		return tag != null && tag.contains(key, NbtTypeIds.NUMBER) ? GameMode.byId(tag.getInt(key)) : null;
	}

	/**
	 * Returns the server game mode the player should be set to, namely the forced game mode.
	 * 
	 * <p>If the forced game mode is not set, returns the {@code backupGameMode} if not {@code null},
	 * or the server's default game mode otherwise.
	 * 
	 * @see MinecraftServer#getForcedGameMode
	 */
	private GameMode getServerGameMode(@Nullable GameMode backupGameMode) {
		GameMode gameMode = this.server.getForcedGameMode();
		if (gameMode != null) {
			return gameMode;
		} else {
			return backupGameMode != null ? backupGameMode : this.server.getDefaultGameMode();
		}
	}

	public void setGameMode(@Nullable NbtCompound tag) {
		this.interactionManager.setGameMode(this.getServerGameMode(gameModeFromNbt(tag, "playerGameType")), gameModeFromNbt(tag, "previousPlayerGameType"));
	}

	private void writeGameModeToNbt(NbtCompound tag) {
		tag.putInt("playerGameType", this.interactionManager.getGameMode().getId());
		GameMode gameMode = this.interactionManager.getPreviousGameMode();
		if (gameMode != null) {
			tag.putInt("previousPlayerGameType", gameMode.getId());
		}
	}

	public boolean shouldFilterText() {
		return this.filterText;
	}

	public boolean shouldFilterMessagesSentTo(ServerPlayerEntity player) {
		return player == this ? false : this.filterText || player.filterText;
	}
}
