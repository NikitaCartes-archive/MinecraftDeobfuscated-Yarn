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
import net.minecraft.client.network.packet.SetVillagerRecipesPacket;
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
import net.minecraft.item.MapItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.recipe.Recipe;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.packet.ClientSettingsC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Void;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TraderRecipeList;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements ContainerListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private String clientLanguage = "en_US";
	public ServerPlayNetworkHandler field_13987;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager field_13974;
	private final List<Integer> field_13988 = Lists.<Integer>newLinkedList();
	private final PlayerAdvancementTracker advancementManager;
	private final ServerStatHandler field_13966;
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
	private Entity field_13984;
	private boolean inTeleportationState;
	private boolean seenCredits;
	private final ServerRecipeBook field_13996;
	private Vec3d field_13992;
	private int field_13973;
	private boolean field_13964;
	@Nullable
	private Vec3d enteredNetherPos;
	private ChunkSectionPos chunkPos = ChunkSectionPos.from(0, 0, 0);
	private int containerSyncId;
	public boolean field_13991;
	public int field_13967;
	public boolean notInAnyWorld;

	public ServerPlayerEntity(
		MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager
	) {
		super(serverWorld, gameProfile);
		serverPlayerInteractionManager.player = this;
		this.field_13974 = serverPlayerInteractionManager;
		this.server = minecraftServer;
		this.field_13996 = new ServerRecipeBook(minecraftServer.getRecipeManager());
		this.field_13966 = minecraftServer.method_3760().method_14583(this);
		this.advancementManager = minecraftServer.method_3760().getAdvancementManager(this);
		this.stepHeight = 1.0F;
		this.method_14245(serverWorld);
	}

	private void method_14245(ServerWorld serverWorld) {
		BlockPos blockPos = serverWorld.method_8395();
		if (serverWorld.field_9247.hasSkyLight() && serverWorld.method_8401().getGameMode() != GameMode.field_9216) {
			int i = Math.max(0, this.server.method_3829(serverWorld));
			int j = MathHelper.floor(serverWorld.method_8621().contains((double)blockPos.getX(), (double)blockPos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			int k = (i * 2 + 1) * (i * 2 + 1);
			int l = this.method_14244(k);
			int m = new Random().nextInt(k);

			for (int n = 0; n < k; n++) {
				int o = (m + l * n) % k;
				int p = o % (i * 2 + 1);
				int q = o / (i * 2 + 1);
				BlockPos blockPos2 = serverWorld.method_8597().method_12444(blockPos.getX() + p - i, blockPos.getZ() + q - i, false);
				if (blockPos2 != null) {
					this.method_5725(blockPos2, 0.0F, 0.0F);
					if (serverWorld.method_17892(this)) {
						break;
					}
				}
			}
		} else {
			this.method_5725(blockPos, 0.0F, 0.0F);

			while (!serverWorld.method_17892(this) && this.y < 255.0) {
				this.setPosition(this.x, this.y + 1.0, this.z);
			}
		}
	}

	private int method_14244(int i) {
		return i <= 16 ? i - 1 : 17;
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("playerGameType", 99)) {
			if (this.getServer().shouldForceGameMode()) {
				this.field_13974.setGameMode(this.getServer().getDefaultGameMode());
			} else {
				this.field_13974.setGameMode(GameMode.byId(compoundTag.getInt("playerGameType")));
			}
		}

		if (compoundTag.containsKey("enteredNetherPosition", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("enteredNetherPosition");
			this.enteredNetherPos = new Vec3d(compoundTag2.getDouble("x"), compoundTag2.getDouble("y"), compoundTag2.getDouble("z"));
		}

		this.seenCredits = compoundTag.getBoolean("seenCredits");
		if (compoundTag.containsKey("recipeBook", 10)) {
			this.field_13996.fromTag(compoundTag.getCompound("recipeBook"));
		}

		if (this.isSleeping()) {
			this.wakeUp();
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("playerGameType", this.field_13974.getGameMode().getId());
		compoundTag.putBoolean("seenCredits", this.seenCredits);
		if (this.enteredNetherPos != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag2.putDouble("x", this.enteredNetherPos.x);
			compoundTag2.putDouble("y", this.enteredNetherPos.y);
			compoundTag2.putDouble("z", this.enteredNetherPos.z);
			compoundTag.method_10566("enteredNetherPosition", compoundTag2);
		}

		Entity entity = this.getTopmostRiddenEntity();
		Entity entity2 = this.getRiddenEntity();
		if (entity2 != null && entity != this && entity.method_5817()) {
			CompoundTag compoundTag3 = new CompoundTag();
			CompoundTag compoundTag4 = new CompoundTag();
			entity.method_5662(compoundTag4);
			compoundTag3.putUuid("Attach", entity2.getUuid());
			compoundTag3.method_10566("Entity", compoundTag4);
			compoundTag.method_10566("RootVehicle", compoundTag3);
		}

		compoundTag.method_10566("recipeBook", this.field_13996.toTag());
	}

	public void method_14228(int i) {
		float f = (float)this.method_7349();
		float g = (f - 1.0F) / f;
		this.experienceBarProgress = MathHelper.clamp((float)i / f, 0.0F, g);
		this.field_13978 = -1;
	}

	public void method_14252(int i) {
		this.experience = i;
		this.field_13978 = -1;
	}

	@Override
	public void method_7316(int i) {
		super.method_7316(i);
		this.field_13978 = -1;
	}

	@Override
	public void method_7286(ItemStack itemStack, int i) {
		super.method_7286(itemStack, i);
		this.field_13978 = -1;
	}

	public void method_14235() {
		this.field_7512.method_7596(this);
	}

	@Override
	public void method_6000() {
		super.method_6000();
		this.field_13987.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.BEGIN));
	}

	@Override
	public void method_6044() {
		super.method_6044();
		this.field_13987.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.END));
	}

	@Override
	protected void method_5622(BlockState blockState) {
		Criterions.ENTER_BLOCK.method_8885(this, blockState);
	}

	@Override
	protected ItemCooldownManager method_7265() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void update() {
		this.field_13974.update();
		this.field_13998--;
		if (this.field_6008 > 0) {
			this.field_6008--;
		}

		this.field_7512.sendContentUpdates();
		if (!this.field_6002.isClient && !this.field_7512.canUse(this)) {
			this.closeGui();
			this.field_7512 = this.field_7498;
		}

		while (!this.field_13988.isEmpty()) {
			int i = Math.min(this.field_13988.size(), Integer.MAX_VALUE);
			int[] is = new int[i];
			Iterator<Integer> iterator = this.field_13988.iterator();
			int j = 0;

			while (iterator.hasNext() && j < i) {
				is[j++] = (Integer)iterator.next();
				iterator.remove();
			}

			this.field_13987.sendPacket(new EntitiesDestroyS2CPacket(is));
		}

		Entity entity = this.method_14242();
		if (entity != this) {
			if (entity.isValid()) {
				this.setPositionAnglesAndUpdate(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
				this.getServerWorld().method_14178().method_14096(this);
				if (this.isSneaking()) {
					this.method_14224(this);
				}
			} else {
				this.method_14224(this);
			}
		}

		Criterions.TICK.method_9141(this);
		if (this.field_13992 != null) {
			Criterions.LEVITATION.method_9008(this, this.field_13992, this.age - this.field_13973);
		}

		this.advancementManager.method_12876(this);
	}

	public void method_14226() {
		try {
			if (!this.isSpectator() || this.field_6002.method_8591(new BlockPos(this))) {
				super.update();
			}

			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.method_5438(i);
				if (itemStack.getItem().isMap()) {
					Packet<?> packet = ((MapItem)itemStack.getItem()).method_7757(itemStack, this.field_6002, this);
					if (packet != null) {
						this.field_13987.sendPacket(packet);
					}
				}
			}

			if (this.getHealth() != this.field_13997
				|| this.field_13979 != this.field_7493.getFoodLevel()
				|| this.field_7493.getSaturationLevel() == 0.0F != this.field_13972) {
				this.field_13987.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.field_7493.getFoodLevel(), this.field_7493.getSaturationLevel()));
				this.field_13997 = this.getHealth();
				this.field_13979 = this.field_7493.getFoodLevel();
				this.field_13972 = this.field_7493.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorptionAmount() != this.field_13963) {
				this.field_13963 = this.getHealth() + this.getAbsorptionAmount();
				this.method_14212(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.field_13963));
			}

			if (this.field_7493.getFoodLevel() != this.field_13983) {
				this.field_13983 = this.field_7493.getFoodLevel();
				this.method_14212(ScoreboardCriterion.FOOD, MathHelper.ceil((float)this.field_13983));
			}

			if (this.getBreath() != this.field_13968) {
				this.field_13968 = this.getBreath();
				this.method_14212(ScoreboardCriterion.AIR, MathHelper.ceil((float)this.field_13968));
			}

			if (this.method_6096() != this.field_13982) {
				this.field_13982 = this.method_6096();
				this.method_14212(ScoreboardCriterion.ARMOR, MathHelper.ceil((float)this.field_13982));
			}

			if (this.experienceLevel != this.field_13980) {
				this.field_13980 = this.experienceLevel;
				this.method_14212(ScoreboardCriterion.XP, MathHelper.ceil((float)this.field_13980));
			}

			if (this.experience != this.field_13965) {
				this.field_13965 = this.experience;
				this.method_14212(ScoreboardCriterion.LEVEL, MathHelper.ceil((float)this.field_13965));
			}

			if (this.experienceLevel != this.field_13978) {
				this.field_13978 = this.experienceLevel;
				this.field_13987.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceBarProgress, this.experienceLevel, this.experience));
			}

			if (this.age % 20 == 0) {
				Criterions.LOCATION.method_9027(this);
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.method_562("Player being ticked");
			this.method_5819(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	private void method_14212(ScoreboardCriterion scoreboardCriterion, int i) {
		this.method_7327().method_1162(scoreboardCriterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(i));
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		boolean bl = this.field_6002.getGameRules().getBoolean("showDeathMessages");
		if (bl) {
			TextComponent textComponent = this.getDamageTracker().method_5548();
			this.field_13987
				.sendPacket(
					new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH, textComponent),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = textComponent.getStringTruncated(256);
							TextComponent textComponent2 = new TranslatableTextComponent(
								"death.attack.message_too_long", new StringTextComponent(string).applyFormat(TextFormat.field_1054)
							);
							TextComponent textComponent3 = new TranslatableTextComponent("death.attack.even_more_magic", this.method_5476())
								.modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent2)));
							this.field_13987.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH, textComponent3));
						}
					}
				);
			AbstractScoreboardTeam abstractScoreboardTeam = this.method_5781();
			if (abstractScoreboardTeam == null || abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.ALWAYS) {
				this.server.method_3760().sendToAll(textComponent);
			} else if (abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS) {
				this.server.method_3760().sendToTeam(this, textComponent);
			} else if (abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_TEAM) {
				this.server.method_3760().sendToOtherTeams(this, textComponent);
			}
		} else {
			this.field_13987.sendPacket(new CombatEventS2CPacket(this.getDamageTracker(), CombatEventS2CPacket.Type.DEATH));
		}

		this.dropShoulderEntities();
		if (!this.isSpectator()) {
			this.method_16080(damageSource);
		}

		this.method_7327().method_1162(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
		LivingEntity livingEntity = this.method_6124();
		if (livingEntity != null) {
			this.method_7259(Stats.field_15411.getOrCreateStat(livingEntity.method_5864()));
			livingEntity.method_5716(this, this.field_6232, damageSource);
			if (!this.field_6002.isClient && livingEntity instanceof WitherEntity) {
				boolean bl2 = false;
				if (this.field_6002.getGameRules().getBoolean("mobGriefing")) {
					BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
					BlockState blockState = Blocks.field_10606.method_9564();
					if (this.field_6002.method_8320(blockPos).isAir() && blockState.method_11591(this.field_6002, blockPos)) {
						this.field_6002.method_8652(blockPos, blockState, 3);
						bl2 = true;
					}
				}

				if (!bl2) {
					ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
					this.field_6002.spawnEntity(itemEntity);
				}
			}
		}

		this.method_7281(Stats.field_15421);
		this.method_7266(Stats.field_15419.getOrCreateStat(Stats.field_15400));
		this.method_7266(Stats.field_15419.getOrCreateStat(Stats.field_15429));
		this.extinguish();
		this.setEntityFlag(0, false);
		this.getDamageTracker().update();
	}

	@Override
	public void method_5716(Entity entity, int i, DamageSource damageSource) {
		if (entity != this) {
			super.method_5716(entity, i, damageSource);
			this.addScore(i);
			String string = this.getEntityName();
			String string2 = entity.getEntityName();
			this.method_7327().method_1162(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			if (entity instanceof PlayerEntity) {
				this.method_7281(Stats.field_15404);
				this.method_7327().method_1162(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			} else {
				this.method_7281(Stats.field_15414);
			}

			this.method_14227(string, string2, ScoreboardCriterion.TEAM_KILLS);
			this.method_14227(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
			Criterions.PLAYER_KILLED_ENTITY.method_8990(this, entity, damageSource);
		}
	}

	private void method_14227(String string, String string2, ScoreboardCriterion[] scoreboardCriterions) {
		ScoreboardTeam scoreboardTeam = this.method_7327().getPlayerTeam(string2);
		if (scoreboardTeam != null) {
			int i = scoreboardTeam.getColor().getId();
			if (i >= 0 && i < scoreboardCriterions.length) {
				this.method_7327().method_1162(scoreboardCriterions[i], string, ScoreboardPlayerScore::incrementScore);
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
					Entity entity = damageSource.method_5529();
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
	public Entity method_5731(DimensionType dimensionType) {
		this.inTeleportationState = true;
		DimensionType dimensionType2 = this.field_6026;
		if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13072) {
			this.method_18375();
			this.getServerWorld().method_18770(this);
			if (!this.notInAnyWorld) {
				this.notInAnyWorld = true;
				this.field_13987.sendPacket(new GameStateChangeS2CPacket(4, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			ServerWorld serverWorld = this.server.method_3847(dimensionType2);
			this.field_6026 = dimensionType;
			ServerWorld serverWorld2 = this.server.method_3847(dimensionType);
			LevelProperties levelProperties = this.field_6002.method_8401();
			this.field_13987.sendPacket(new PlayerRespawnS2CPacket(dimensionType, levelProperties.getGeneratorType(), this.field_13974.getGameMode()));
			this.field_13987.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			PlayerManager playerManager = this.server.method_3760();
			playerManager.method_14576(this);
			serverWorld.method_18770(this);
			this.invalid = false;
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
			double k = Math.min(-2.9999872E7, serverWorld2.method_8621().getBoundWest() + 16.0);
			double l = Math.min(-2.9999872E7, serverWorld2.method_8621().getBoundNorth() + 16.0);
			double m = Math.min(2.9999872E7, serverWorld2.method_8621().getBoundEast() - 16.0);
			double n = Math.min(2.9999872E7, serverWorld2.method_8621().getBoundSouth() - 16.0);
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
							serverWorld2.method_8501(new BlockPos(w, x, y), bl ? Blocks.field_10540.method_9564() : Blocks.field_10124.method_9564());
						}
					}
				}

				this.setPositionAndAngles((double)o, (double)p, (double)q, h, 0.0F);
				this.method_18799(Vec3d.ZERO);
			} else if (!serverWorld2.getPortalForcer().method_8653(this, j)) {
				serverWorld2.getPortalForcer().method_8654(this);
				serverWorld2.getPortalForcer().method_8653(this, j);
			}

			serverWorld.getProfiler().pop();
			this.method_5866(serverWorld2);
			serverWorld2.method_18211(this);
			this.method_18783(serverWorld);
			this.field_13987.teleportRequest(this.x, this.y, this.z, h, g);
			this.field_13974.setWorld(serverWorld2);
			this.field_13987.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			playerManager.method_14606(this, serverWorld2);
			playerManager.method_14594(this);

			for (StatusEffectInstance statusEffectInstance : this.getPotionEffects()) {
				this.field_13987.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
			}

			this.field_13987.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
			this.field_13978 = -1;
			this.field_13997 = -1.0F;
			this.field_13979 = -1;
			return this;
		}
	}

	private void method_18783(ServerWorld serverWorld) {
		DimensionType dimensionType = serverWorld.field_9247.method_12460();
		DimensionType dimensionType2 = this.field_6002.field_9247.method_12460();
		Criterions.CHANGED_DIMENSION.method_8794(this, dimensionType, dimensionType2);
		if (dimensionType == DimensionType.field_13076 && dimensionType2 == DimensionType.field_13072 && this.enteredNetherPos != null) {
			Criterions.NETHER_TRAVEL.method_9080(this, this.enteredNetherPos);
		}

		if (dimensionType2 != DimensionType.field_13076) {
			this.enteredNetherPos = null;
		}
	}

	@Override
	public boolean method_5680(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.isSpectator()) {
			return this.method_14242() == this;
		} else {
			return this.isSpectator() ? false : super.method_5680(serverPlayerEntity);
		}
	}

	private void sendBlockEntityUpdate(BlockEntity blockEntity) {
		if (blockEntity != null) {
			BlockEntityUpdateS2CPacket blockEntityUpdateS2CPacket = blockEntity.method_16886();
			if (blockEntityUpdateS2CPacket != null) {
				this.field_13987.sendPacket(blockEntityUpdateS2CPacket);
			}
		}
	}

	@Override
	public void pickUpEntity(Entity entity, int i) {
		super.pickUpEntity(entity, i);
		this.field_7512.sendContentUpdates();
	}

	@Override
	public Either<PlayerEntity.SleepResult, Void> method_7269(BlockPos blockPos) {
		return super.method_7269(blockPos).ifRight(void_ -> {
			this.method_7281(Stats.field_15381);
			Criterions.SLEPT_IN_BED.method_9027(this);
		});
	}

	@Override
	public void wakeUp(boolean bl, boolean bl2, boolean bl3) {
		if (this.isSleeping()) {
			this.getServerWorld().method_14178().method_18751(this, new EntityAnimationS2CPacket(this, 2));
		}

		super.wakeUp(bl, bl2, bl3);
		if (this.field_13987 != null) {
			this.field_13987.teleportRequest(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		Entity entity2 = this.getRiddenEntity();
		if (!super.startRiding(entity, bl)) {
			return false;
		} else {
			Entity entity3 = this.getRiddenEntity();
			if (entity3 != entity2 && this.field_13987 != null) {
				this.field_13987.teleportRequest(this.x, this.y, this.z, this.yaw, this.pitch);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getRiddenEntity();
		super.stopRiding();
		Entity entity2 = this.getRiddenEntity();
		if (entity2 != entity && this.field_13987 != null) {
			this.field_13987.teleportRequest(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.isInTeleportationState() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	protected void method_6126(BlockPos blockPos) {
		if (!this.isSpectator()) {
			super.method_6126(blockPos);
		}
	}

	public void method_14207(double d, boolean bl) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		if (this.field_6002.method_8591(blockPos)) {
			BlockState blockState = this.field_6002.method_8320(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.field_6002.method_8320(blockPos2);
				Block block = blockState2.getBlock();
				if (block.method_9525(BlockTags.field_16584) || block.method_9525(BlockTags.field_15504) || block instanceof FenceGateBlock) {
					blockPos = blockPos2;
					blockState = blockState2;
				}
			}

			super.method_5623(d, bl, blockState, blockPos);
		}
	}

	@Override
	public void method_7311(SignBlockEntity signBlockEntity) {
		signBlockEntity.setEditor(this);
		this.field_13987.sendPacket(new SignEditorOpenS2CPacket(signBlockEntity.method_11016()));
	}

	private void incrementContainerSyncId() {
		this.containerSyncId = this.containerSyncId % 100 + 1;
	}

	@Override
	public OptionalInt openContainer(@Nullable NameableContainerProvider nameableContainerProvider) {
		if (nameableContainerProvider == null) {
			return OptionalInt.empty();
		} else {
			if (this.field_7512 != this.field_7498) {
				this.closeGui();
			}

			this.incrementContainerSyncId();
			Container container = nameableContainerProvider.createMenu(this.containerSyncId, this.inventory, this);
			if (container == null) {
				if (this.isSpectator()) {
					this.method_7353(new TranslatableTextComponent("container.spectatorCantOpen").applyFormat(TextFormat.field_1061), true);
				}

				return OptionalInt.empty();
			} else {
				this.field_13987.sendPacket(new OpenContainerPacket(container.syncId, container.method_17358(), nameableContainerProvider.method_5476()));
				container.method_7596(this);
				this.field_7512 = container;
				return OptionalInt.of(this.containerSyncId);
			}
		}
	}

	@Override
	public void method_17354(int i, TraderRecipeList traderRecipeList, int j, int k, boolean bl) {
		this.field_13987.sendPacket(new SetVillagerRecipesPacket(i, traderRecipeList, j, k, bl));
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
		if (this.field_7512 != this.field_7498) {
			this.closeGui();
		}

		this.incrementContainerSyncId();
		this.field_13987.sendPacket(new GuiOpenS2CPacket(this.containerSyncId, inventory.getInvSize(), horseBaseEntity.getEntityId()));
		this.field_7512 = new HorseContainer(this.containerSyncId, this.inventory, inventory, horseBaseEntity);
		this.field_7512.method_7596(this);
	}

	@Override
	public void method_7315(ItemStack itemStack, Hand hand) {
		Item item = itemStack.getItem();
		if (item == Items.field_8360) {
			if (WrittenBookItem.method_8054(itemStack, this.method_5671(), this)) {
				this.field_7512.sendContentUpdates();
			}

			this.field_13987.sendPacket(new OpenWrittenBookS2CPacket(hand));
		}
	}

	@Override
	public void method_7323(CommandBlockBlockEntity commandBlockBlockEntity) {
		commandBlockBlockEntity.setNeedsUpdatePacket(true);
		this.sendBlockEntityUpdate(commandBlockBlockEntity);
	}

	@Override
	public void method_7635(Container container, int i, ItemStack itemStack) {
		if (!(container.method_7611(i) instanceof CraftingResultSlot)) {
			if (container == this.field_7498) {
				Criterions.INVENTORY_CHANGED.method_8950(this, this.inventory);
			}

			if (!this.field_13991) {
				this.field_13987.sendPacket(new GuiSlotUpdateS2CPacket(container.syncId, i, itemStack));
			}
		}
	}

	public void method_14204(Container container) {
		this.method_7634(container, container.method_7602());
	}

	@Override
	public void method_7634(Container container, DefaultedList<ItemStack> defaultedList) {
		this.field_13987.sendPacket(new InventoryS2CPacket(container.syncId, defaultedList));
		this.field_13987.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.method_7399()));
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
		this.field_13987.sendPacket(new GuiUpdateS2CPacket(container.syncId, i, j));
	}

	@Override
	public void closeGui() {
		this.field_13987.sendPacket(new GuiCloseS2CPacket(this.field_7512.syncId));
		this.method_14247();
	}

	public void method_14241() {
		if (!this.field_13991) {
			this.field_13987.sendPacket(new GuiSlotUpdateS2CPacket(-1, -1, this.inventory.method_7399()));
		}
	}

	public void method_14247() {
		this.field_7512.close(this);
		this.field_7512 = this.field_7498;
	}

	public void method_14218(float f, float g, boolean bl, boolean bl2) {
		if (this.hasVehicle()) {
			if (f >= -1.0F && f <= 1.0F) {
				this.movementInputSideways = f;
			}

			if (g >= -1.0F && g <= 1.0F) {
				this.movementInputForward = g;
			}

			this.field_6282 = bl;
			this.setSneaking(bl2);
		}
	}

	@Override
	public void method_7342(Stat<?> stat, int i) {
		this.field_13966.increaseStat(this, stat, i);
		this.method_7327().method_1162(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(i));
	}

	@Override
	public void method_7266(Stat<?> stat) {
		this.field_13966.setStat(this, stat, 0);
		this.method_7327().method_1162(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
	}

	@Override
	public int unlockRecipes(Collection<Recipe<?>> collection) {
		return this.field_13996.unlockRecipes(collection, this);
	}

	@Override
	public void method_7335(Identifier[] identifiers) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Identifier identifier : identifiers) {
			this.server.getRecipeManager().method_8130(identifier).ifPresent(list::add);
		}

		this.unlockRecipes(list);
	}

	@Override
	public int lockRecipes(Collection<Recipe<?>> collection) {
		return this.field_13996.lockRecipes(collection, this);
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
	public void method_7353(TextComponent textComponent, boolean bl) {
		this.field_13987.sendPacket(new ChatMessageS2CPacket(textComponent, bl ? ChatMessageType.field_11733 : ChatMessageType.field_11737));
	}

	@Override
	protected void method_6040() {
		if (!this.field_6277.isEmpty() && this.isUsingItem()) {
			this.field_13987.sendPacket(new EntityStatusS2CPacket(this, (byte)9));
			super.method_6040();
		}
	}

	@Override
	public void method_5702(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.method_5702(entityAnchor, vec3d);
		this.field_13987.sendPacket(new LookAtS2CPacket(entityAnchor, vec3d.x, vec3d.y, vec3d.z));
	}

	public void method_14222(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		Vec3d vec3d = entityAnchor2.method_9302(entity);
		super.method_5702(entityAnchor, vec3d);
		this.field_13987.sendPacket(new LookAtS2CPacket(entityAnchor, entity, entityAnchor2));
	}

	public void method_14203(ServerPlayerEntity serverPlayerEntity, boolean bl) {
		if (bl) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.setHealth(serverPlayerEntity.getHealth());
			this.field_7493 = serverPlayerEntity.field_7493;
			this.experience = serverPlayerEntity.experience;
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.experienceBarProgress = serverPlayerEntity.experienceBarProgress;
			this.setScore(serverPlayerEntity.getScore());
			this.field_5991 = serverPlayerEntity.field_5991;
			this.field_6020 = serverPlayerEntity.field_6020;
			this.field_6028 = serverPlayerEntity.field_6028;
		} else if (this.field_6002.getGameRules().getBoolean("keepInventory") || serverPlayerEntity.isSpectator()) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.experience = serverPlayerEntity.experience;
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.experienceBarProgress = serverPlayerEntity.experienceBarProgress;
			this.setScore(serverPlayerEntity.getScore());
		}

		this.enchantmentTableSeed = serverPlayerEntity.enchantmentTableSeed;
		this.field_7486 = serverPlayerEntity.field_7486;
		this.method_5841().set(field_7518, serverPlayerEntity.method_5841().get(field_7518));
		this.field_13978 = -1;
		this.field_13997 = -1.0F;
		this.field_13979 = -1;
		this.field_13996.copyFrom(serverPlayerEntity.field_13996);
		this.field_13988.addAll(serverPlayerEntity.field_13988);
		this.seenCredits = serverPlayerEntity.seenCredits;
		this.enteredNetherPos = serverPlayerEntity.enteredNetherPos;
		this.method_7273(serverPlayerEntity.method_7356());
		this.method_7345(serverPlayerEntity.method_7308());
	}

	@Override
	protected void method_6020(StatusEffectInstance statusEffectInstance) {
		super.method_6020(statusEffectInstance);
		this.field_13987.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13973 = this.age;
			this.field_13992 = new Vec3d(this.x, this.y, this.z);
		}

		Criterions.EFFECTS_CHANGED.method_8863(this);
	}

	@Override
	protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
		super.method_6009(statusEffectInstance, bl);
		this.field_13987.sendPacket(new EntityPotionEffectS2CPacket(this.getEntityId(), statusEffectInstance));
		Criterions.EFFECTS_CHANGED.method_8863(this);
	}

	@Override
	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		super.method_6129(statusEffectInstance);
		this.field_13987.sendPacket(new RemoveEntityEffectS2CPacket(this.getEntityId(), statusEffectInstance.getEffectType()));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13992 = null;
		}

		Criterions.EFFECTS_CHANGED.method_8863(this);
	}

	@Override
	public void method_5859(double d, double e, double f) {
		this.field_13987.teleportRequest(d, e, f, this.yaw, this.pitch);
	}

	@Override
	public void addCritParticles(Entity entity) {
		this.getServerWorld().method_14178().method_18751(this, new EntityAnimationS2CPacket(entity, 4));
	}

	@Override
	public void addEnchantedHitParticles(Entity entity) {
		this.getServerWorld().method_14178().method_18751(this, new EntityAnimationS2CPacket(entity, 5));
	}

	@Override
	public void method_7355() {
		if (this.field_13987 != null) {
			this.field_13987.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.field_6002;
	}

	@Override
	public void method_7336(GameMode gameMode) {
		this.field_13974.setGameMode(gameMode);
		this.field_13987.sendPacket(new GameStateChangeS2CPacket(3, (float)gameMode.getId()));
		if (gameMode == GameMode.field_9219) {
			this.dropShoulderEntities();
			this.stopRiding();
		} else {
			this.method_14224(this);
		}

		this.method_7355();
		this.method_6008();
	}

	@Override
	public boolean isSpectator() {
		return this.field_13974.getGameMode() == GameMode.field_9219;
	}

	@Override
	public boolean isCreative() {
		return this.field_13974.getGameMode() == GameMode.field_9220;
	}

	@Override
	public void method_9203(TextComponent textComponent) {
		this.sendChatMessage(textComponent, ChatMessageType.field_11735);
	}

	public void sendChatMessage(TextComponent textComponent, ChatMessageType chatMessageType) {
		this.field_13987
			.sendPacket(
				new ChatMessageS2CPacket(textComponent, chatMessageType),
				future -> {
					if (!future.isSuccess() && (chatMessageType == ChatMessageType.field_11733 || chatMessageType == ChatMessageType.field_11735)) {
						int i = 256;
						String string = textComponent.getStringTruncated(256);
						TextComponent textComponent2 = new StringTextComponent(string).applyFormat(TextFormat.field_1054);
						this.field_13987
							.sendPacket(
								new ChatMessageS2CPacket(
									new TranslatableTextComponent("multiplayer.message_not_delivered", textComponent2).applyFormat(TextFormat.field_1061), ChatMessageType.field_11735
								)
							);
					}
				}
			);
	}

	public String getServerBrand() {
		String string = this.field_13987.client.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsC2SPacket clientSettingsC2SPacket) {
		this.clientLanguage = clientSettingsC2SPacket.getLanguage();
		this.clientChatVisibility = clientSettingsC2SPacket.getChatVisibility();
		this.field_13971 = clientSettingsC2SPacket.method_12135();
		this.method_5841().set(field_7518, (byte)clientSettingsC2SPacket.getPlayerModelBitMask());
		this.method_5841().set(field_7488, (byte)(clientSettingsC2SPacket.getMainHand() == OptionMainHand.field_6182 ? 0 : 1));
	}

	public ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	public void method_14255(String string, String string2) {
		this.field_13987.sendPacket(new ResourcePackSendS2CPacket(string, string2));
	}

	@Override
	protected int getPermissionLevel() {
		return this.server.getPermissionLevel(this.getGameProfile());
	}

	public void updateLastActionTime() {
		this.lastActionTime = SystemUtil.getMeasuringTimeMs();
	}

	public ServerStatHandler method_14248() {
		return this.field_13966;
	}

	public ServerRecipeBook method_14253() {
		return this.field_13996;
	}

	public void method_14249(Entity entity) {
		if (entity instanceof PlayerEntity) {
			this.field_13987.sendPacket(new EntitiesDestroyS2CPacket(entity.getEntityId()));
		} else {
			this.field_13988.add(entity.getEntityId());
		}
	}

	public void onStartedTracking(Entity entity) {
		this.field_13988.remove(entity.getEntityId());
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

	public Entity method_14242() {
		return (Entity)(this.field_13984 == null ? this : this.field_13984);
	}

	public void method_14224(Entity entity) {
		Entity entity2 = this.method_14242();
		this.field_13984 = (Entity)(entity == null ? this : entity);
		if (entity2 != this.field_13984) {
			this.field_13987.sendPacket(new SetCameraEntityS2CPacket(this.field_13984));
			this.method_5859(this.field_13984.x, this.field_13984.y, this.field_13984.z);
		}
	}

	@Override
	protected void updatePortalCooldown() {
		if (this.portalCooldown > 0 && !this.inTeleportationState) {
			this.portalCooldown--;
		}
	}

	@Override
	public void attack(Entity entity) {
		if (this.field_13974.getGameMode() == GameMode.field_9219) {
			this.method_14224(entity);
		} else {
			super.attack(entity);
		}
	}

	public long getLastActionTime() {
		return this.lastActionTime;
	}

	@Nullable
	public TextComponent method_14206() {
		return null;
	}

	@Override
	public void swingHand(Hand hand) {
		super.swingHand(hand);
		this.method_7350();
	}

	public boolean isInTeleportationState() {
		return this.inTeleportationState;
	}

	public void onTeleportationDone() {
		this.inTeleportationState = false;
	}

	public void method_14243() {
		this.setEntityFlag(7, true);
	}

	public void method_14229() {
		this.setEntityFlag(7, true);
		this.setEntityFlag(7, false);
	}

	public PlayerAdvancementTracker getAdvancementManager() {
		return this.advancementManager;
	}

	public void method_14251(ServerWorld serverWorld, double d, double e, double f, float g, float h) {
		this.method_14224(this);
		this.stopRiding();
		if (serverWorld == this.field_6002) {
			this.field_13987.teleportRequest(d, e, f, g, h);
		} else {
			ServerWorld serverWorld2 = this.getServerWorld();
			this.field_6026 = serverWorld.field_9247.method_12460();
			LevelProperties levelProperties = serverWorld.method_8401();
			this.field_13987.sendPacket(new PlayerRespawnS2CPacket(this.field_6026, levelProperties.getGeneratorType(), this.field_13974.getGameMode()));
			this.field_13987.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
			this.server.method_3760().method_14576(this);
			serverWorld2.method_18770(this);
			this.invalid = false;
			this.setPositionAndAngles(d, e, f, g, h);
			this.method_5866(serverWorld);
			serverWorld.method_18207(this);
			this.method_18783(serverWorld2);
			this.field_13987.teleportRequest(d, e, f, g, h);
			this.field_13974.setWorld(serverWorld);
			this.server.method_3760().method_14606(this, serverWorld);
			this.server.method_3760().method_14594(this);
		}
	}

	public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
		this.field_13987.sendPacket(packet);
		this.field_13987.sendPacket(packet2);
	}

	public void sendUnloadChunkPacket(ChunkPos chunkPos) {
		this.field_13987.sendPacket(new UnloadChunkS2CPacket(chunkPos.x, chunkPos.z));
	}

	public ChunkSectionPos getChunkPos() {
		return this.chunkPos;
	}

	public void setChunkPos(ChunkSectionPos chunkSectionPos) {
		this.chunkPos = chunkSectionPos;
	}

	@Override
	public void method_17356(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g) {
		this.field_13987.sendPacket(new PlaySoundS2CPacket(soundEvent, soundCategory, this.x, this.y, this.z, f, g));
	}

	@Override
	public Packet<?> method_18002() {
		return new PlayerSpawnS2CPacket(this);
	}

	@Override
	public ItemEntity method_7329(ItemStack itemStack, boolean bl, boolean bl2) {
		ItemEntity itemEntity = super.method_7329(itemStack, bl, bl2);
		if (itemEntity == null) {
			return null;
		} else {
			this.field_6002.spawnEntity(itemEntity);
			ItemStack itemStack2 = itemEntity.method_6983();
			if (bl2) {
				if (!itemStack2.isEmpty()) {
					this.method_7342(Stats.field_15405.getOrCreateStat(itemStack2.getItem()), itemStack.getAmount());
				}

				this.method_7281(Stats.field_15406);
			}

			return itemEntity;
		}
	}
}
