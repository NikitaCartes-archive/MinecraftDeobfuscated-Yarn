package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sortme.SpawnRestriction;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class Raid {
	public static final ItemStack ILLAGER_BANNER = getIllagerBanner();
	private final Map<Integer, RaiderEntity> waveToLeader = Maps.<Integer, RaiderEntity>newHashMap();
	private final Map<Integer, Set<RaiderEntity>> waveToRaiders = Maps.<Integer, Set<RaiderEntity>>newHashMap();
	private long ticksActive;
	private final BlockPos center;
	private final ServerWorld world;
	private boolean markedForRemoval;
	private boolean started;
	private boolean ignoreDayRequirement;
	private int id;
	private float totalHealth;
	private int badOmenLevel;
	private boolean active;
	private int wavesSpawned;
	private final ServerBossBar bar = new ServerBossBar(
		new TranslatableTextComponent("event.minecraft.raid"), BossBar.Color.field_5784, BossBar.Overlay.field_5791
	);
	private int postRaidTicks;
	private int preRaidTicks;
	private final Random random = new Random();

	public Raid(int i, ServerWorld serverWorld, BlockPos blockPos) {
		this.id = i;
		this.world = serverWorld;
		this.active = true;
		this.preRaidTicks = 300;
		this.bar.setPercent(0.0F);
		this.center = blockPos;
	}

	public Raid(ServerWorld serverWorld, CompoundTag compoundTag) {
		this.world = serverWorld;
		this.id = compoundTag.getInt("Id");
		this.started = compoundTag.getBoolean("Started");
		this.active = compoundTag.getBoolean("Active");
		this.ticksActive = compoundTag.getLong("TicksActive");
		this.markedForRemoval = compoundTag.getBoolean("MarkedForRemoval");
		this.ignoreDayRequirement = compoundTag.getBoolean("IgnoreDayRequirement");
		this.badOmenLevel = compoundTag.getInt("BadOmenLevel");
		this.wavesSpawned = compoundTag.getInt("GroupsSpawned");
		this.preRaidTicks = compoundTag.getInt("PreRaidTicks");
		this.postRaidTicks = compoundTag.getInt("PostRaidTicks");
		this.totalHealth = compoundTag.getFloat("TotalHealth");
		this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
	}

	public World getWorld() {
		return this.world;
	}

	public boolean isOnGoing() {
		return this.hasStarted() && this.isActive();
	}

	public boolean hasStarted() {
		return this.started;
	}

	public int getGroupsSpawned() {
		return this.wavesSpawned;
	}

	private Predicate<ServerPlayerEntity> isInRaidDistance() {
		return serverPlayerEntity -> serverPlayerEntity.isValid() && serverPlayerEntity.getServerWorld().method_19497(new BlockPos(serverPlayerEntity), 2);
	}

	private void updateBarToPlayers() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.method_18766(this.isInRaidDistance())) {
			this.bar.addPlayer(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.bar.getPlayers());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.bar.removePlayer(serverPlayerEntity2);
		}
	}

	public int getMaxAcceptableBadOmenLevel() {
		return 5;
	}

	public int getBadOmenLevel() {
		return this.badOmenLevel;
	}

	public void start(PlayerEntity playerEntity) {
		int i = this.getMaxAcceptableBadOmenLevel();
		if (playerEntity.hasPotionEffect(StatusEffects.field_16595)) {
			this.badOmenLevel = this.badOmenLevel + playerEntity.getPotionEffect(StatusEffects.field_16595).getAmplifier() + 1;
			if (i < this.badOmenLevel) {
				this.badOmenLevel = i;
			}
		}

		playerEntity.removeStatusEffect(StatusEffects.field_16595);
	}

	public void invalidate() {
		this.markedForRemoval = true;
		this.active = false;
		this.bar.clearPlayers();
	}

	public boolean isMarkedForRemoval() {
		return this.markedForRemoval;
	}

	public void tick() {
		if (!this.markedForRemoval) {
			boolean bl = this.active;
			this.active = this.world.isBlockLoaded(this.center);
			if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
				this.invalidate();
			} else {
				if (bl != this.active) {
					this.bar.setVisible(this.active);
				}

				if (this.active) {
					this.ticksActive++;
					int i = this.getRaiderCount();
					if (i == 0 && !this.hasSpawnedAllGroups()) {
						if (this.preRaidTicks > 0) {
							if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
								this.updateBarToPlayers();
							}

							this.preRaidTicks--;
							this.bar.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0F, 0.0F, 100.0F));
						} else if (this.preRaidTicks == 0 && this.wavesSpawned > 0) {
							this.preRaidTicks = 300;
							this.bar.setName(new TranslatableTextComponent("event.minecraft.raid"));
							return;
						}
					}

					if (i > 0 && this.preRaidTicks == 0 && this.ticksActive % 20L == 0L) {
						this.updateBarToPlayers();
						this.removeObsoleteRaiders();
						if (i <= 2) {
							this.bar
								.setName(
									new TranslatableTextComponent("event.minecraft.raid").append(" - ").append(new TranslatableTextComponent("event.minecraft.raid.mobs_remaining", i))
								);
						} else {
							this.bar.setName(new TranslatableTextComponent("event.minecraft.raid"));
						}
					}

					boolean bl2 = false;
					int j = 0;

					while (this.canSpawnRaiders()) {
						BlockPos blockPos = this.getPillagerSpawnLocation(j);
						if (blockPos != null) {
							this.started = true;
							this.spawnNextWave(blockPos);
							if (!bl2) {
								this.playRaidHorn(blockPos);
								bl2 = true;
							}
						} else {
							j++;
						}

						if (j > 2) {
							this.invalidate();
							break;
						}
					}

					if (this.hasStarted() && this.getGroupsSpawned() >= this.getBadOmenLevel() * 2 && i == 0) {
						if (this.postRaidTicks < 40) {
							this.postRaidTicks++;
						} else {
							this.invalidate();
						}
					}

					this.markDirty();
				}
			}
		}
	}

	private boolean hasSpawnedAllGroups() {
		return this.getGroupsSpawned() >= this.getBadOmenLevel() * 2;
	}

	private void removeObsoleteRaiders() {
		Iterator<Set<RaiderEntity>> iterator = this.waveToRaiders.values().iterator();
		Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();

		while (iterator.hasNext()) {
			Set<RaiderEntity> set2 = (Set<RaiderEntity>)iterator.next();

			for (RaiderEntity raiderEntity : set2) {
				if (!raiderEntity.invalid && raiderEntity.dimension == this.world.getDimension().getType()) {
					if (raiderEntity.age <= 300) {
						continue;
					}
				} else {
					raiderEntity.setOutOfRaidCounter(30);
				}

				if (!RaidManager.isLivingAroundVillage(raiderEntity, this.center, 32) || raiderEntity.getDespawnCounter() > 2400) {
					raiderEntity.setOutOfRaidCounter(raiderEntity.getOutOfRaidCounter() + 1);
				}

				if (raiderEntity.getOutOfRaidCounter() >= 30) {
					set.add(raiderEntity);
				}
			}
		}

		for (RaiderEntity raiderEntity2 : set) {
			this.removeFromWave(raiderEntity2, true);
		}
	}

	private void playRaidHorn(BlockPos blockPos) {
		float f = 13.0F;
		int i = 64;

		for (PlayerEntity playerEntity : this.world.getPlayers()) {
			Vec3d vec3d = new Vec3d(playerEntity.x, playerEntity.y, playerEntity.z);
			Vec3d vec3d2 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			float g = MathHelper.sqrt((vec3d2.x - vec3d.x) * (vec3d2.x - vec3d.x) + (vec3d2.z - vec3d.z) * (vec3d2.z - vec3d.z));
			double d = vec3d.x + (double)(13.0F / g) * (vec3d2.x - vec3d.x);
			double e = vec3d.z + (double)(13.0F / g) * (vec3d2.z - vec3d.z);
			if (g <= 64.0F || RaidManager.isLivingAroundVillage(playerEntity, this.center, 32)) {
				((ServerPlayerEntity)playerEntity)
					.networkHandler
					.sendPacket(new PlaySoundS2CPacket(SoundEvents.field_17266, SoundCategory.field_15254, d, playerEntity.y, e, 64.0F, 1.0F));
			}
		}
	}

	private void spawnNextWave(BlockPos blockPos) {
		boolean bl = false;
		int i = this.wavesSpawned + 1;
		this.totalHealth = 0.0F;

		for (Raid.Member member : Raid.Member.field_16636) {
			int j = getSpawnCount(member, this.random, i) + getBonusSpawnCount(member, this.random, i);

			for (int k = 0; k < j; k++) {
				RaiderEntity raiderEntity = member.type.create(this.world);
				this.addRaider(i, raiderEntity, blockPos, false);
				if (!bl && raiderEntity.canLead()) {
					raiderEntity.setPatrolLeader(true);
					this.setRaidLeader(i, raiderEntity);
					bl = true;
				}

				if (member.type == EntityType.RAVAGER) {
					RaiderEntity raiderEntity2;
					if (i < 6) {
						raiderEntity2 = EntityType.PILLAGER.create(this.world);
					} else {
						raiderEntity2 = EntityType.VINDICATOR.create(this.world);
					}

					this.addRaider(i, raiderEntity2, blockPos, false);
					raiderEntity2.setPositionAndAngles(blockPos, 0.0F, 0.0F);
					raiderEntity2.startRiding(raiderEntity);
				}
			}
		}

		this.wavesSpawned++;
		this.updateBar();
		this.markDirty();
	}

	public void addRaider(int i, RaiderEntity raiderEntity, @Nullable BlockPos blockPos, boolean bl) {
		boolean bl2 = this.addToWave(i, raiderEntity);
		if (bl2) {
			raiderEntity.setRaid(this);
			raiderEntity.setWave(i);
			raiderEntity.setHasRaidGoal(true);
			raiderEntity.setOutOfRaidCounter(0);
			if (!bl && blockPos != null) {
				raiderEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
				raiderEntity.prepareEntityData(this.world, this.world.getLocalDifficulty(blockPos), SpawnType.field_16467, null, null);
				raiderEntity.addBonusForWave(i, false);
				raiderEntity.onGround = true;
				this.world.spawnEntity(raiderEntity);
			}
		}
	}

	private void updateBar() {
		this.bar.setPercent(MathHelper.clamp(this.getCurrentRaiderHealth() / this.totalHealth, 0.0F, 100.0F));
	}

	public float getCurrentRaiderHealth() {
		float f = 0.0F;

		for (Set<RaiderEntity> set : this.waveToRaiders.values()) {
			for (RaiderEntity raiderEntity : set) {
				f += raiderEntity.getHealth();
			}
		}

		return f;
	}

	private boolean canSpawnRaiders() {
		return this.preRaidTicks == 0 && this.wavesSpawned < this.badOmenLevel * 2 && this.getRaiderCount() == 0;
	}

	public int getRaiderCount() {
		return this.waveToRaiders.values().stream().mapToInt(Set::size).sum();
	}

	public void removeFromWave(@Nonnull RaiderEntity raiderEntity, boolean bl) {
		Set<RaiderEntity> set = (Set<RaiderEntity>)this.waveToRaiders.get(raiderEntity.getWave());
		if (set != null) {
			boolean bl2 = set.remove(raiderEntity);
			if (bl2) {
				if (bl) {
					this.totalHealth = this.totalHealth - raiderEntity.getHealth();
				}

				this.updateBar();
				this.markDirty();
			}
		}
	}

	private void markDirty() {
		this.world.getRaidManager().markDirty();
	}

	private static ItemStack getIllagerBanner() {
		ItemStack itemStack = new ItemStack(Items.field_8539);
		CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("BlockEntityTag");
		ListTag listTag = new BannerPattern.Builder()
			.with(BannerPattern.RHOMBUS, DyeColor.field_7955)
			.with(BannerPattern.STRIPE_BOTTOM, DyeColor.field_7967)
			.with(BannerPattern.STRIPE_CENTER, DyeColor.field_7944)
			.with(BannerPattern.BORDER, DyeColor.field_7967)
			.with(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
			.with(BannerPattern.HALF_HORIZONTAL_TOP, DyeColor.field_7967)
			.with(BannerPattern.CIRCLE, DyeColor.field_7967)
			.with(BannerPattern.BORDER, DyeColor.BLACK)
			.build();
		compoundTag.put("Patterns", listTag);
		itemStack.setDisplayName(new TranslatableTextComponent("block.minecraft.illager_banner").applyFormat(TextFormat.field_1065));
		return itemStack;
	}

	public static int getBadOmenLevel(Random random, boolean bl) {
		int i = random.nextInt(9);
		if (i < 4 || !bl) {
			return 1;
		} else {
			return i < 7 ? 2 : 3;
		}
	}

	@Nullable
	public RaiderEntity getLeader(int i) {
		return (RaiderEntity)this.waveToLeader.get(i);
	}

	@Nullable
	private BlockPos getPillagerSpawnLocation(int i) {
		int j = i == 0 ? 2 : 2 - i;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < 20; k++) {
			float f = this.world.random.nextFloat() * (float) (Math.PI * 2);
			int l = this.center.getX() + (int)(MathHelper.cos(f) * 32.0F * (float)j) + this.world.random.nextInt(5);
			int m = this.center.getZ() + (int)(MathHelper.sin(f) * 32.0F * (float)j) + this.world.random.nextInt(5);
			int n = this.world.getTop(Heightmap.Type.WORLD_SURFACE, l, m);
			mutable.set(l, n, m);
			if (!this.world.method_19500(mutable)
				&& this.world.isAreaLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)
				&& (
					SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, this.world, mutable, EntityType.PILLAGER)
						|| this.world.getBlockState(mutable.down()).getBlock() == Blocks.field_10477 && this.world.getBlockState(mutable).isAir()
				)) {
				return mutable;
			}
		}

		return null;
	}

	private boolean addToWave(int i, RaiderEntity raiderEntity) {
		return this.addToWave(i, raiderEntity, true);
	}

	public boolean addToWave(int i, RaiderEntity raiderEntity, boolean bl) {
		this.waveToRaiders.computeIfAbsent(i, integer -> Sets.newHashSet());
		if (((Set)this.waveToRaiders.get(i)).contains(raiderEntity)) {
			return false;
		} else {
			((Set)this.waveToRaiders.get(i)).add(raiderEntity);
			if (bl) {
				this.totalHealth = this.totalHealth + raiderEntity.getHealth();
			}

			this.updateBar();
			this.markDirty();
			return true;
		}
	}

	public void setRaidLeader(int i, RaiderEntity raiderEntity) {
		this.waveToLeader.put(i, raiderEntity);
		raiderEntity.setEquippedStack(EquipmentSlot.HEAD, ILLAGER_BANNER);
		raiderEntity.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
	}

	public void removeLeader(int i) {
		this.waveToLeader.remove(i);
	}

	public BlockPos getCenter() {
		return this.center;
	}

	public int getRaidId() {
		return this.id;
	}

	private static int getSpawnCount(Raid.Member member, Random random, int i) {
		if (member.firstWave > i) {
			return 0;
		} else {
			switch (member) {
				case PILLAGER:
					return MathHelper.floor(0.125F * (float)i + 2.875F);
				case VINDICATOR:
					return MathHelper.floor(0.667F * (float)i + 1.334F);
				case EVOKER:
					return i == 10 ? 1 : 0;
				case RAVAGER:
					if (i == 1) {
						return 1;
					}

					return random.nextInt(2);
				case WITCH:
					return i >= 3 ? 1 : 0;
				default:
					return 1;
			}
		}
	}

	private static int getBonusSpawnCount(Raid.Member member, Random random, int i) {
		if (member.firstWave > i) {
			return 0;
		} else {
			int j;
			switch (member) {
				case PILLAGER:
				case VINDICATOR:
					j = 2;
					break;
				case EVOKER:
				case RAVAGER:
				default:
					return 0;
				case WITCH:
					j = 1;
			}

			return random.nextInt(j);
		}
	}

	public boolean isActive() {
		return this.active;
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putInt("Id", this.id);
		compoundTag.putBoolean("Started", this.started);
		compoundTag.putBoolean("Active", this.active);
		compoundTag.putLong("TicksActive", this.ticksActive);
		compoundTag.putBoolean("MarkedForRemoval", this.markedForRemoval);
		compoundTag.putBoolean("IgnoreDayRequirement", this.ignoreDayRequirement);
		compoundTag.putInt("BadOmenLevel", this.badOmenLevel);
		compoundTag.putInt("GroupsSpawned", this.wavesSpawned);
		compoundTag.putInt("PreRaidTicks", this.preRaidTicks);
		compoundTag.putInt("PostRaidTicks", this.postRaidTicks);
		compoundTag.putFloat("TotalHealth", this.totalHealth);
		compoundTag.putInt("CX", this.center.getX());
		compoundTag.putInt("CY", this.center.getY());
		compoundTag.putInt("CZ", this.center.getZ());
		return compoundTag;
	}

	static enum Member {
		VINDICATOR(EntityType.VINDICATOR, 1),
		EVOKER(EntityType.EVOKER, 10),
		PILLAGER(EntityType.PILLAGER, 1),
		WITCH(EntityType.WITCH, 4),
		RAVAGER(EntityType.RAVAGER, 2);

		private static final Raid.Member[] field_16636 = values();
		private final EntityType<? extends RaiderEntity> type;
		private final int firstWave;

		private Member(EntityType<? extends RaiderEntity> entityType, int j) {
			this.type = entityType;
			this.firstWave = j;
		}
	}
}
