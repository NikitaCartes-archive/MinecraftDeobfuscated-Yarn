package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.client.network.packet.ChatMessageClientPacket;
import net.minecraft.client.network.packet.CombatEventClientPacket;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.client.network.packet.EntitiesDestroyClientPacket;
import net.minecraft.client.network.packet.EntityAnimationClientPacket;
import net.minecraft.client.network.packet.EntityPotionEffectClientPacket;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.client.network.packet.ExperienceBarUpdateClientPacket;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.client.network.packet.GuiCloseClientPacket;
import net.minecraft.client.network.packet.GuiOpenClientPacket;
import net.minecraft.client.network.packet.GuiSlotUpdateClientPacket;
import net.minecraft.client.network.packet.GuiUpdateClientPacket;
import net.minecraft.client.network.packet.HealthUpdateClientPacket;
import net.minecraft.client.network.packet.InventoryClientPacket;
import net.minecraft.client.network.packet.LookAtClientPacket;
import net.minecraft.client.network.packet.OpenWrittenBookClientPacket;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.client.network.packet.PlayerAbilitiesClientPacket;
import net.minecraft.client.network.packet.PlayerRespawnClientPacket;
import net.minecraft.client.network.packet.PlayerUseBedClientPacket;
import net.minecraft.client.network.packet.RemoveEntityEffectClientPacket;
import net.minecraft.client.network.packet.ResourcePackSendClientPacket;
import net.minecraft.client.network.packet.SetCameraEntityClientPacket;
import net.minecraft.client.network.packet.SignEditorOpenClientPacket;
import net.minecraft.client.network.packet.UnloadChunkClientPacket;
import net.minecraft.client.network.packet.WorldEventClientPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.container.ContainerProvider;
import net.minecraft.container.CraftingResultSlot;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.LockableContainer;
import net.minecraft.container.VillagerContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ChunkTicketType;
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
import net.minecraft.server.network.packet.ClientSettingsServerPacket;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Villager;
import net.minecraft.village.VillagerRecipeList;
import net.minecraft.world.GameMode;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.loot.LootTableProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerEntity extends PlayerEntity implements ContainerListener {
	private static final Logger LOGGER_PLAYER = LogManager.getLogger();
	private String clientLanguage = "en_US";
	public ServerPlayNetworkHandler networkHandler;
	public final MinecraftServer server;
	public final ServerPlayerInteractionManager interactionManager;
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
	private PlayerEntity.ChatVisibility clientChatVisibility;
	private boolean field_13971 = true;
	private long field_13976 = SystemUtil.getMeasuringTimeMs();
	private Entity field_13984;
	private boolean field_13985;
	private boolean seenCredits;
	private final ServerRecipeBook recipeBook;
	private Vec3d field_13992;
	private int field_13973;
	private boolean field_13964;
	private Vec3d enteredNetherPos;
	private ChunkPos chunkPos = new ChunkPos(0, 0);
	@Nullable
	private ChunkTicket<PlayerEntity> field_13977;
	private int containerSyncId;
	public boolean field_13991;
	public int field_13967;
	public boolean field_13989;

	public ServerPlayerEntity(
		MinecraftServer minecraftServer, ServerWorld serverWorld, GameProfile gameProfile, ServerPlayerInteractionManager serverPlayerInteractionManager
	) {
		super(serverWorld, gameProfile);
		serverPlayerInteractionManager.player = this;
		this.interactionManager = serverPlayerInteractionManager;
		this.server = minecraftServer;
		this.recipeBook = new ServerRecipeBook(minecraftServer.getRecipeManager());
		this.field_13966 = minecraftServer.getPlayerManager().method_14583(this);
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

			int k = (i * 2 + 1) * (i * 2 + 1);
			int l = this.method_14244(k);
			int m = new Random().nextInt(k);

			for (int n = 0; n < k; n++) {
				int o = (m + l * n) % k;
				int p = o % (i * 2 + 1);
				int q = o / (i * 2 + 1);
				BlockPos blockPos2 = serverWorld.getDimension().method_12444(blockPos.getX() + p - i, blockPos.getZ() + q - i, false);
				if (blockPos2 != null) {
					this.setPositionAndAngles(blockPos2, 0.0F, 0.0F);
					if (serverWorld.method_8587(this, this.getBoundingBox())) {
						break;
					}
				}
			}
		} else {
			this.setPositionAndAngles(blockPos, 0.0F, 0.0F);

			while (!serverWorld.method_8587(this, this.getBoundingBox()) && this.y < 255.0) {
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

		Entity entity = this.getTopmostRiddenEntity();
		Entity entity2 = this.getRiddenEntity();
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
		this.container.addListener(this);
	}

	@Override
	public void method_6000() {
		super.method_6000();
		this.networkHandler.sendPacket(new CombatEventClientPacket(this.getDamageTracker(), CombatEventClientPacket.Type.BEGIN));
	}

	@Override
	public void method_6044() {
		super.method_6044();
		this.networkHandler.sendPacket(new CombatEventClientPacket(this.getDamageTracker(), CombatEventClientPacket.Type.END));
	}

	@Override
	protected void onBlockCollision(BlockState blockState) {
		Criterions.ENTER_BLOCK.method_8885(this, blockState);
	}

	@Override
	protected ItemCooldownManager createCooldownManager() {
		return new ServerItemCooldownManager(this);
	}

	@Override
	public void update() {
		this.interactionManager.update();
		this.field_13998--;
		if (this.field_6008 > 0) {
			this.field_6008--;
		}

		this.container.sendContentUpdates();
		if (!this.world.isClient && !this.container.canUse(this)) {
			this.closeGui();
			this.container = this.containerPlayer;
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

			this.networkHandler.sendPacket(new EntitiesDestroyClientPacket(is));
		}

		Entity entity = this.method_14242();
		if (entity != this) {
			if (entity.isValid()) {
				this.setPositionAnglesAndUpdate(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
				this.server.getPlayerManager().method_14575(this);
				if (this.isSneaking()) {
					this.method_14224(this);
				}
			} else {
				this.method_14224(this);
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
				super.update();
			}

			for (int i = 0; i < this.inventory.getInvSize(); i++) {
				ItemStack itemStack = this.inventory.getInvStack(i);
				if (itemStack.getItem().isMap()) {
					Packet<?> packet = ((MapItem)itemStack.getItem()).createMapPacket(itemStack, this.world, this);
					if (packet != null) {
						this.networkHandler.sendPacket(packet);
					}
				}
			}

			if (this.getHealth() != this.field_13997
				|| this.field_13979 != this.hungerManager.getFoodLevel()
				|| this.hungerManager.getSaturationLevel() == 0.0F != this.field_13972) {
				this.networkHandler.sendPacket(new HealthUpdateClientPacket(this.getHealth(), this.hungerManager.getFoodLevel(), this.hungerManager.getSaturationLevel()));
				this.field_13997 = this.getHealth();
				this.field_13979 = this.hungerManager.getFoodLevel();
				this.field_13972 = this.hungerManager.getSaturationLevel() == 0.0F;
			}

			if (this.getHealth() + this.getAbsorptionAmount() != this.field_13963) {
				this.field_13963 = this.getHealth() + this.getAbsorptionAmount();
				this.method_14212(ScoreboardCriterion.HEALTH, MathHelper.ceil(this.field_13963));
			}

			if (this.hungerManager.getFoodLevel() != this.field_13983) {
				this.field_13983 = this.hungerManager.getFoodLevel();
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
				this.networkHandler.sendPacket(new ExperienceBarUpdateClientPacket(this.experienceBarProgress, this.experienceLevel, this.experience));
			}

			if (this.age % 20 == 0) {
				Criterions.LOCATION.handle(this);
			}
		} catch (Throwable var4) {
			CrashReport crashReport = CrashReport.create(var4, "Ticking player");
			CrashReportSection crashReportSection = crashReport.method_562("Player being ticked");
			this.method_5819(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	private void method_14212(ScoreboardCriterion scoreboardCriterion, int i) {
		this.getScoreboard().method_1162(scoreboardCriterion, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.setScore(i));
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		boolean bl = this.world.getGameRules().getBoolean("showDeathMessages");
		if (bl) {
			TextComponent textComponent = this.getDamageTracker().getDeathMessage();
			this.networkHandler
				.sendPacket(
					new CombatEventClientPacket(this.getDamageTracker(), CombatEventClientPacket.Type.DEATH, textComponent),
					future -> {
						if (!future.isSuccess()) {
							int i = 256;
							String string = textComponent.getStringTruncated(256);
							TextComponent textComponent2 = new TranslatableTextComponent(
								"death.attack.message_too_long", new StringTextComponent(string).applyFormat(TextFormat.YELLOW)
							);
							TextComponent textComponent3 = new TranslatableTextComponent("death.attack.even_more_magic", this.getDisplayName())
								.modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent2)));
							this.networkHandler.sendPacket(new CombatEventClientPacket(this.getDamageTracker(), CombatEventClientPacket.Type.DEATH, textComponent3));
						}
					}
				);
			AbstractScoreboardTeam abstractScoreboardTeam = this.getScoreboardTeam();
			if (abstractScoreboardTeam == null || abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.ALWAYS) {
				this.server.getPlayerManager().sendToAll(textComponent);
			} else if (abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_OTHER_TEAMS) {
				this.server.getPlayerManager().sendToTeam(this, textComponent);
			} else if (abstractScoreboardTeam.getDeathMessageVisibilityRule() == AbstractScoreboardTeam.VisibilityRule.HIDDEN_FOR_TEAM) {
				this.server.getPlayerManager().sendToOtherTeams(this, textComponent);
			}
		} else {
			this.networkHandler.sendPacket(new CombatEventClientPacket(this.getDamageTracker(), CombatEventClientPacket.Type.DEATH));
		}

		this.method_7262();
		if (!this.isSpectator()) {
			this.method_16080(damageSource);
		}

		this.getScoreboard().method_1162(ScoreboardCriterion.DEATH_COUNT, this.getEntityName(), ScoreboardPlayerScore::incrementScore);
		LivingEntity livingEntity = this.method_6124();
		if (livingEntity != null) {
			this.incrementStat(Stats.field_15411.method_14956(livingEntity.getType()));
			livingEntity.method_5716(this, this.field_6232, damageSource);
			if (!this.world.isClient && livingEntity instanceof EntityWither) {
				BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
				BlockState blockState = Blocks.field_10606.getDefaultState();
				if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
					this.world.setBlockState(blockPos, blockState, 3);
				}
			}
		}

		this.increaseStat(Stats.field_15421);
		this.resetStat(Stats.field_15419.method_14956(Stats.field_15400));
		this.resetStat(Stats.field_15419.method_14956(Stats.field_15429));
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
			this.getScoreboard().method_1162(ScoreboardCriterion.TOTAL_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			if (entity instanceof PlayerEntity) {
				this.increaseStat(Stats.field_15404);
				this.getScoreboard().method_1162(ScoreboardCriterion.PLAYER_KILL_COUNT, string, ScoreboardPlayerScore::incrementScore);
			} else {
				this.increaseStat(Stats.field_15414);
			}

			this.method_14227(string, string2, ScoreboardCriterion.TEAM_KILLS);
			this.method_14227(string2, string, ScoreboardCriterion.KILLED_BY_TEAMS);
			Criterions.PLAYER_KILLED_ENTITY.handle(this, entity, damageSource);
		}
	}

	private void method_14227(String string, String string2, ScoreboardCriterion[] scoreboardCriterions) {
		ScoreboardTeam scoreboardTeam = this.getScoreboard().getPlayerTeam(string2);
		if (scoreboardTeam != null) {
			int i = scoreboardTeam.getColor().getId();
			if (i >= 0 && i < scoreboardCriterions.length) {
				this.getScoreboard().method_1162(scoreboardCriterions[i], string, ScoreboardPlayerScore::incrementScore);
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
		this.field_13985 = true;
		if (this.dimension == DimensionType.field_13072 && dimensionType == DimensionType.field_13076) {
			this.enteredNetherPos = new Vec3d(this.x, this.y, this.z);
		} else if (this.dimension != DimensionType.field_13076 && dimensionType != DimensionType.field_13072) {
			this.enteredNetherPos = null;
		}

		if (this.dimension == DimensionType.field_13078 && dimensionType == DimensionType.field_13078) {
			this.world.removeEntity(this);
			if (!this.field_13989) {
				this.field_13989 = true;
				this.networkHandler.sendPacket(new GameStateChangeClientPacket(4, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			if (this.dimension == DimensionType.field_13072 && dimensionType == DimensionType.field_13078) {
				dimensionType = DimensionType.field_13078;
			}

			this.server.getPlayerManager().method_14598(this, dimensionType);
			this.networkHandler.sendPacket(new WorldEventClientPacket(1032, BlockPos.ORIGIN, 0, false));
			this.field_13978 = -1;
			this.field_13997 = -1.0F;
			this.field_13979 = -1;
			return this;
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
			BlockEntityUpdateClientPacket blockEntityUpdateClientPacket = blockEntity.toUpdatePacket();
			if (blockEntityUpdateClientPacket != null) {
				this.networkHandler.sendPacket(blockEntityUpdateClientPacket);
			}
		}
	}

	@Override
	public void method_6103(Entity entity, int i) {
		super.method_6103(entity, i);
		this.container.sendContentUpdates();
	}

	@Override
	public PlayerEntity.SleepResult trySleep(BlockPos blockPos) {
		PlayerEntity.SleepResult sleepResult = super.trySleep(blockPos);
		if (sleepResult == PlayerEntity.SleepResult.SUCCESS) {
			this.increaseStat(Stats.field_15381);
			Packet<?> packet = new PlayerUseBedClientPacket(this, blockPos);
			this.getServerWorld().getEntityTracker().method_14079(this, packet);
			this.networkHandler.method_14363(this.x, this.y, this.z, this.yaw, this.pitch);
			this.networkHandler.sendPacket(packet);
			Criterions.SLEPT_IN_BED.handle(this);
		}

		return sleepResult;
	}

	@Override
	public void method_7358(boolean bl, boolean bl2, boolean bl3) {
		if (this.isSleeping()) {
			this.getServerWorld().getEntityTracker().method_14073(this, new EntityAnimationClientPacket(this, 2));
		}

		super.method_7358(bl, bl2, bl3);
		if (this.networkHandler != null) {
			this.networkHandler.method_14363(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean startRiding(Entity entity, boolean bl) {
		Entity entity2 = this.getRiddenEntity();
		if (!super.startRiding(entity, bl)) {
			return false;
		} else {
			Entity entity3 = this.getRiddenEntity();
			if (entity3 != entity2 && this.networkHandler != null) {
				this.networkHandler.method_14363(this.x, this.y, this.z, this.yaw, this.pitch);
			}

			return true;
		}
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getRiddenEntity();
		super.stopRiding();
		Entity entity2 = this.getRiddenEntity();
		if (entity2 != entity && this.networkHandler != null) {
			this.networkHandler.method_14363(this.x, this.y, this.z, this.yaw, this.pitch);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || this.method_14208() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
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

			super.method_5623(d, bl, blockState, blockPos);
		}
	}

	@Override
	public void openSignEditorGui(SignBlockEntity signBlockEntity) {
		signBlockEntity.setEditor(this);
		this.networkHandler.sendPacket(new SignEditorOpenClientPacket(signBlockEntity.getPos()));
	}

	private void incrementContainerSyncId() {
		this.containerSyncId = this.containerSyncId % 100 + 1;
	}

	@Override
	public void openContainer(ContainerProvider containerProvider) {
		if (containerProvider instanceof LootTableProvider && ((LootTableProvider)containerProvider).getLootTableId() != null && this.isSpectator()) {
			this.addChatMessage(new TranslatableTextComponent("container.spectatorCantOpen").applyFormat(TextFormat.RED), true);
		} else {
			this.incrementContainerSyncId();
			this.networkHandler.sendPacket(new GuiOpenClientPacket(this.containerSyncId, containerProvider.getContainerId(), containerProvider.getDisplayName()));
			this.container = containerProvider.createContainer(this.inventory, this);
			this.container.syncId = this.containerSyncId;
			this.container.addListener(this);
		}
	}

	@Override
	public void openInventory(Inventory inventory) {
		if (inventory instanceof LootTableProvider && ((LootTableProvider)inventory).getLootTableId() != null && this.isSpectator()) {
			this.addChatMessage(new TranslatableTextComponent("container.spectatorCantOpen").applyFormat(TextFormat.RED), true);
		} else {
			if (this.container != this.containerPlayer) {
				this.closeGui();
			}

			if (inventory instanceof LockableContainer) {
				LockableContainer lockableContainer = (LockableContainer)inventory;
				if (lockableContainer.hasContainerLock() && !this.canUnlock(lockableContainer.getContainerLock()) && !this.isSpectator()) {
					this.networkHandler
						.sendPacket(new ChatMessageClientPacket(new TranslatableTextComponent("container.isLocked", inventory.getDisplayName()), ChatMessageType.field_11733));
					this.networkHandler.sendPacket(new PlaySoundClientPacket(SoundEvents.field_14731, SoundCategory.field_15245, this.x, this.y, this.z, 1.0F, 1.0F));
					return;
				}
			}

			this.incrementContainerSyncId();
			if (inventory instanceof ContainerProvider) {
				this.networkHandler
					.sendPacket(
						new GuiOpenClientPacket(this.containerSyncId, ((ContainerProvider)inventory).getContainerId(), inventory.getDisplayName(), inventory.getInvSize())
					);
				this.container = ((ContainerProvider)inventory).createContainer(this.inventory, this);
			} else {
				this.networkHandler.sendPacket(new GuiOpenClientPacket(this.containerSyncId, "minecraft:container", inventory.getDisplayName(), inventory.getInvSize()));
				this.container = new GenericContainer(this.inventory, inventory, this);
			}

			this.container.syncId = this.containerSyncId;
			this.container.addListener(this);
		}
	}

	@Override
	public void openVillagerTrade(Villager villager) {
		this.incrementContainerSyncId();
		this.container = new VillagerContainer(this.inventory, villager, this.world);
		this.container.syncId = this.containerSyncId;
		this.container.addListener(this);
		Inventory inventory = ((VillagerContainer)this.container).getVillagerInventory();
		TextComponent textComponent = villager.getDisplayName();
		this.networkHandler.sendPacket(new GuiOpenClientPacket(this.containerSyncId, "minecraft:villager", textComponent, inventory.getInvSize()));
		VillagerRecipeList villagerRecipeList = villager.getRecipes();
		if (!villagerRecipeList.isEmpty()) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
			packetByteBuf.writeInt(this.containerSyncId);
			villagerRecipeList.writeToBuf(packetByteBuf);
			this.networkHandler.sendPacket(new CustomPayloadClientPacket(CustomPayloadClientPacket.TRADER_LIST, packetByteBuf));
		}
	}

	@Override
	public void openHorseInventory(HorseBaseEntity horseBaseEntity, Inventory inventory) {
		if (this.container != this.containerPlayer) {
			this.closeGui();
		}

		this.incrementContainerSyncId();
		this.networkHandler
			.sendPacket(new GuiOpenClientPacket(this.containerSyncId, "EntityHorse", inventory.getDisplayName(), inventory.getInvSize(), horseBaseEntity.getEntityId()));
		this.container = new HorseContainer(this.inventory, inventory, horseBaseEntity, this);
		this.container.syncId = this.containerSyncId;
		this.container.addListener(this);
	}

	@Override
	public void openBookEditorGui(ItemStack itemStack, Hand hand) {
		Item item = itemStack.getItem();
		if (item == Items.field_8360) {
			if (WrittenBookItem.method_8054(itemStack, this)) {
				this.container.sendContentUpdates();
			}

			this.networkHandler.sendPacket(new OpenWrittenBookClientPacket(hand));
		}
	}

	@Override
	public void openCommandBlockGui(CommandBlockBlockEntity commandBlockBlockEntity) {
		commandBlockBlockEntity.method_11037(true);
		this.sendBlockEntityUpdate(commandBlockBlockEntity);
	}

	@Override
	public void onContainerSlotUpdate(Container container, int i, ItemStack itemStack) {
		if (!(container.getSlot(i) instanceof CraftingResultSlot)) {
			if (container == this.containerPlayer) {
				Criterions.INVENTORY_CHANGED.handle(this, this.inventory);
			}

			if (!this.field_13991) {
				this.networkHandler.sendPacket(new GuiSlotUpdateClientPacket(container.syncId, i, itemStack));
			}
		}
	}

	public void method_14204(Container container) {
		this.onContainerRegistered(container, container.getStacks());
	}

	@Override
	public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
		this.networkHandler.sendPacket(new InventoryClientPacket(container.syncId, defaultedList));
		this.networkHandler.sendPacket(new GuiSlotUpdateClientPacket(-1, -1, this.inventory.getCursorStack()));
	}

	@Override
	public void onContainerPropertyUpdate(Container container, int i, int j) {
		this.networkHandler.sendPacket(new GuiUpdateClientPacket(container.syncId, i, j));
	}

	@Override
	public void onContainerInvRegistered(Container container, Inventory inventory) {
		for (int i = 0; i < inventory.getInvPropertyCount(); i++) {
			this.networkHandler.sendPacket(new GuiUpdateClientPacket(container.syncId, i, inventory.getInvProperty(i)));
		}
	}

	@Override
	public void closeGui() {
		this.networkHandler.sendPacket(new GuiCloseClientPacket(this.container.syncId));
		this.closeContainer();
	}

	public void method_14241() {
		if (!this.field_13991) {
			this.networkHandler.sendPacket(new GuiSlotUpdateClientPacket(-1, -1, this.inventory.getCursorStack()));
		}
	}

	public void closeContainer() {
		this.container.close(this);
		this.container = this.containerPlayer;
	}

	public void method_14218(float f, float g, boolean bl, boolean bl2) {
		if (this.hasVehicle()) {
			if (f >= -1.0F && f <= 1.0F) {
				this.field_6212 = f;
			}

			if (g >= -1.0F && g <= 1.0F) {
				this.field_6250 = g;
			}

			this.field_6282 = bl;
			this.setSneaking(bl2);
		}
	}

	@Override
	public void incrementStat(Stat<?> stat, int i) {
		this.field_13966.increaseStat(this, stat, i);
		this.getScoreboard().method_1162(stat, this.getEntityName(), scoreboardPlayerScore -> scoreboardPlayerScore.incrementScore(i));
	}

	@Override
	public void resetStat(Stat<?> stat) {
		this.field_13966.setStat(this, stat, 0);
		this.getScoreboard().method_1162(stat, this.getEntityName(), ScoreboardPlayerScore::clearScore);
	}

	@Override
	public int unlockRecipes(Collection<Recipe> collection) {
		return this.recipeBook.unlockRecipes(collection, this);
	}

	@Override
	public void unlockRecipes(Identifier[] identifiers) {
		List<Recipe> list = Lists.<Recipe>newArrayList();

		for (Identifier identifier : identifiers) {
			Recipe recipe = this.server.getRecipeManager().get(identifier);
			if (recipe != null) {
				list.add(recipe);
			}
		}

		this.unlockRecipes(list);
	}

	@Override
	public int lockRecipes(Collection<Recipe> collection) {
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
		if (this.sleeping) {
			this.method_7358(true, false, false);
		}
	}

	public boolean method_14239() {
		return this.field_13964;
	}

	public void method_14217() {
		this.field_13997 = -1.0E8F;
	}

	@Override
	public void addChatMessage(TextComponent textComponent, boolean bl) {
		this.networkHandler.sendPacket(new ChatMessageClientPacket(textComponent, bl ? ChatMessageType.field_11733 : ChatMessageType.field_11737));
	}

	@Override
	protected void method_6040() {
		if (!this.activeItemStack.isEmpty() && this.method_6115()) {
			this.networkHandler.sendPacket(new EntityStatusClientPacket(this, (byte)9));
			super.method_6040();
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.lookAt(entityAnchor, vec3d);
		this.networkHandler.sendPacket(new LookAtClientPacket(entityAnchor, vec3d.x, vec3d.y, vec3d.z));
	}

	public void method_14222(EntityAnchorArgumentType.EntityAnchor entityAnchor, Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor2) {
		Vec3d vec3d = entityAnchor2.positionAt(entity);
		super.lookAt(entityAnchor, vec3d);
		this.networkHandler.sendPacket(new LookAtClientPacket(entityAnchor, entity, entityAnchor2));
	}

	public void method_14203(ServerPlayerEntity serverPlayerEntity, boolean bl) {
		if (bl) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.setHealth(serverPlayerEntity.getHealth());
			this.hungerManager = serverPlayerEntity.hungerManager;
			this.experience = serverPlayerEntity.experience;
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.experienceBarProgress = serverPlayerEntity.experienceBarProgress;
			this.setScore(serverPlayerEntity.getScore());
			this.lastPortalPosition = serverPlayerEntity.lastPortalPosition;
			this.field_6020 = serverPlayerEntity.field_6020;
			this.field_6028 = serverPlayerEntity.field_6028;
		} else if (this.world.getGameRules().getBoolean("keepInventory") || serverPlayerEntity.isSpectator()) {
			this.inventory.clone(serverPlayerEntity.inventory);
			this.experience = serverPlayerEntity.experience;
			this.experienceLevel = serverPlayerEntity.experienceLevel;
			this.experienceBarProgress = serverPlayerEntity.experienceBarProgress;
			this.setScore(serverPlayerEntity.getScore());
		}

		this.enchantmentTableSeed = serverPlayerEntity.enchantmentTableSeed;
		this.enderChestInventory = serverPlayerEntity.enderChestInventory;
		this.getDataTracker().set(PLAYER_MODEL_BIT_MASK, serverPlayerEntity.getDataTracker().get(PLAYER_MODEL_BIT_MASK));
		this.field_13978 = -1;
		this.field_13997 = -1.0F;
		this.field_13979 = -1;
		this.recipeBook.copyFrom(serverPlayerEntity.recipeBook);
		this.field_13988.addAll(serverPlayerEntity.field_13988);
		this.seenCredits = serverPlayerEntity.seenCredits;
		this.enteredNetherPos = serverPlayerEntity.enteredNetherPos;
		this.setShoulderEntityLeft(serverPlayerEntity.getShoulderEntityLeft());
		this.setShoulderEntityRight(serverPlayerEntity.getShoulderEntityRight());
	}

	@Override
	protected void method_6020(StatusEffectInstance statusEffectInstance) {
		super.method_6020(statusEffectInstance);
		this.networkHandler.sendPacket(new EntityPotionEffectClientPacket(this.getEntityId(), statusEffectInstance));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13973 = this.age;
			this.field_13992 = new Vec3d(this.x, this.y, this.z);
		}

		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
		super.method_6009(statusEffectInstance, bl);
		this.networkHandler.sendPacket(new EntityPotionEffectClientPacket(this.getEntityId(), statusEffectInstance));
		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		super.method_6129(statusEffectInstance);
		this.networkHandler.sendPacket(new RemoveEntityEffectClientPacket(this.getEntityId(), statusEffectInstance.getEffectType()));
		if (statusEffectInstance.getEffectType() == StatusEffects.field_5902) {
			this.field_13992 = null;
		}

		Criterions.EFFECTS_CHANGED.handle(this);
	}

	@Override
	public void method_5859(double d, double e, double f) {
		this.networkHandler.method_14363(d, e, f, this.yaw, this.pitch);
	}

	@Override
	public void addCritParticles(Entity entity) {
		this.getServerWorld().getEntityTracker().method_14073(this, new EntityAnimationClientPacket(entity, 4));
	}

	@Override
	public void addEnchantedHitParticles(Entity entity) {
		this.getServerWorld().getEntityTracker().method_14073(this, new EntityAnimationClientPacket(entity, 5));
	}

	@Override
	public void method_7355() {
		if (this.networkHandler != null) {
			this.networkHandler.sendPacket(new PlayerAbilitiesClientPacket(this.abilities));
			this.updatePotionVisibility();
		}
	}

	public ServerWorld getServerWorld() {
		return (ServerWorld)this.world;
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.interactionManager.setGameMode(gameMode);
		this.networkHandler.sendPacket(new GameStateChangeClientPacket(3, (float)gameMode.getId()));
		if (gameMode == GameMode.field_9219) {
			this.method_7262();
			this.stopRiding();
		} else {
			this.method_14224(this);
		}

		this.method_7355();
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
	public void appendCommandFeedback(TextComponent textComponent) {
		this.sendChatMessage(textComponent, ChatMessageType.field_11735);
	}

	public void sendChatMessage(TextComponent textComponent, ChatMessageType chatMessageType) {
		this.networkHandler
			.sendPacket(
				new ChatMessageClientPacket(textComponent, chatMessageType),
				future -> {
					if (!future.isSuccess() && (chatMessageType == ChatMessageType.field_11733 || chatMessageType == ChatMessageType.field_11735)) {
						int i = 256;
						String string = textComponent.getStringTruncated(256);
						TextComponent textComponent2 = new StringTextComponent(string).applyFormat(TextFormat.YELLOW);
						this.networkHandler
							.sendPacket(
								new ChatMessageClientPacket(
									new TranslatableTextComponent("multiplayer.message_not_delivered", textComponent2).applyFormat(TextFormat.RED), ChatMessageType.field_11735
								)
							);
					}
				}
			);
	}

	public String method_14209() {
		String string = this.networkHandler.client.getAddress().toString();
		string = string.substring(string.indexOf("/") + 1);
		return string.substring(0, string.indexOf(":"));
	}

	public void setClientSettings(ClientSettingsServerPacket clientSettingsServerPacket) {
		this.clientLanguage = clientSettingsServerPacket.getLanguage();
		this.clientChatVisibility = clientSettingsServerPacket.getChatVisibility();
		this.field_13971 = clientSettingsServerPacket.method_12135();
		this.getDataTracker().set(PLAYER_MODEL_BIT_MASK, (byte)clientSettingsServerPacket.getPlayerModelBitMask());
		this.getDataTracker().set(MAIN_HAND, (byte)(clientSettingsServerPacket.getMainHand() == OptionMainHand.field_6182 ? 0 : 1));
	}

	public PlayerEntity.ChatVisibility getClientChatVisibility() {
		return this.clientChatVisibility;
	}

	public void method_14255(String string, String string2) {
		this.networkHandler.sendPacket(new ResourcePackSendClientPacket(string, string2));
	}

	@Override
	protected int getPermissionLevel() {
		return this.server.getPermissionLevel(this.getGameProfile());
	}

	public void method_14234() {
		this.field_13976 = SystemUtil.getMeasuringTimeMs();
	}

	public ServerStatHandler method_14248() {
		return this.field_13966;
	}

	public ServerRecipeBook getRecipeBook() {
		return this.recipeBook;
	}

	public void method_14249(Entity entity) {
		if (entity instanceof PlayerEntity) {
			this.networkHandler.sendPacket(new EntitiesDestroyClientPacket(entity.getEntityId()));
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

		this.getServerWorld().getEntityTracker().method_14071(this);
	}

	public Entity method_14242() {
		return (Entity)(this.field_13984 == null ? this : this.field_13984);
	}

	public void method_14224(Entity entity) {
		Entity entity2 = this.method_14242();
		this.field_13984 = (Entity)(entity == null ? this : entity);
		if (entity2 != this.field_13984) {
			this.networkHandler.sendPacket(new SetCameraEntityClientPacket(this.field_13984));
			this.method_5859(this.field_13984.x, this.field_13984.y, this.field_13984.z);
		}
	}

	@Override
	protected void updatePortalCooldown() {
		if (this.portalCooldown > 0 && !this.field_13985) {
			this.portalCooldown--;
		}
	}

	@Override
	public void attack(Entity entity) {
		if (this.interactionManager.getGameMode() == GameMode.field_9219) {
			this.method_14224(entity);
		} else {
			super.attack(entity);
		}
	}

	public long method_14219() {
		return this.field_13976;
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

	public boolean method_14208() {
		return this.field_13985;
	}

	public void method_14240() {
		this.field_13985 = false;
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

	@Nullable
	public Vec3d getEnteredNetherPosition() {
		return this.enteredNetherPos;
	}

	public void method_14251(ServerWorld serverWorld, double d, double e, double f, float g, float h) {
		this.method_14224(this);
		this.stopRiding();
		if (serverWorld == this.world) {
			this.networkHandler.method_14363(d, e, f, g, h);
		} else {
			ServerWorld serverWorld2 = this.getServerWorld();
			this.dimension = serverWorld.dimension.getType();
			this.networkHandler
				.sendPacket(
					new PlayerRespawnClientPacket(
						this.dimension, serverWorld2.getDifficulty(), serverWorld2.getLevelProperties().getGeneratorType(), this.interactionManager.getGameMode()
					)
				);
			this.server.getPlayerManager().method_14576(this);
			serverWorld2.method_8507(this);
			this.invalid = false;
			this.setPositionAndAngles(d, e, f, g, h);
			if (this.isValid()) {
				serverWorld2.method_8553(this, false);
				serverWorld.spawnEntity(this);
				serverWorld.method_8553(this, false);
			}

			this.setWorld(serverWorld);
			this.server.getPlayerManager().method_14612(this, serverWorld2);
			this.networkHandler.method_14363(d, e, f, g, h);
			this.interactionManager.setWorld(serverWorld);
			this.server.getPlayerManager().method_14606(this, serverWorld);
			this.server.getPlayerManager().method_14594(this);
		}
	}

	public void sendInitialChunkPackets(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
		this.networkHandler.sendPacket(packet);
		this.networkHandler.sendPacket(packet2);
		this.getServerWorld().getEntityTracker().sendEntitiesInChunk(this, chunkPos.x, chunkPos.z);
	}

	public void sendRemoveChunkPacket(ChunkPos chunkPos) {
		this.networkHandler.sendPacket(new UnloadChunkClientPacket(chunkPos.x, chunkPos.z));
	}

	public ChunkPos getChunkPos() {
		return this.chunkPos;
	}

	@Nullable
	public ChunkTicket<PlayerEntity> method_14214() {
		return this.field_13977;
	}

	public ChunkTicket<PlayerEntity> method_14215(long l, int i, long m) {
		this.field_13977 = new ChunkTicket<>(ChunkTicketType.PLAYER, i, this, m);
		this.chunkPos = new ChunkPos(l);
		return this.field_13977;
	}
}
