package net.minecraft.entity.raid;

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.predicate.entity.EntityPredicates;
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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public class Raid {
	public static final ItemStack ILLAGER_BANNER = getIllagerBanner();
	@Nullable
	private final Map<Integer, RaiderEntity> waveToLeader;
	@Nullable
	private final Map<Integer, Set<RaiderEntity>> waveToRaiders;
	private long ticksActive;
	private BlockPos center = BlockPos.ORIGIN;
	private World world;
	private boolean markedForRemoval = false;
	@Nullable
	private VillageProperties villageProperties;
	private boolean started;
	private boolean ignoreDayRequirement = false;
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
	@Nullable
	private BoundingBox volume;
	private final Random random = new Random();

	public Raid() {
		this.waveToLeader = new HashMap();
		this.waveToRaiders = new HashMap();
		this.ticksActive = 0L;
	}

	public Raid(int i, World world, VillageProperties villageProperties) {
		this();
		this.id = i;
		this.world = world;
		this.setWorld(world);
		this.active = true;
		this.badOmenLevel = 0;
		this.wavesSpawned = 0;
		this.postRaidTicks = 0;
		this.preRaidTicks = 300;
		this.bar.setPercent(0.0F);
		if (villageProperties != null) {
			this.setVillage(villageProperties);
			villageProperties.setRaidId(i);
		}
	}

	public World getWorld() {
		return this.world;
	}

	public boolean isOnGoing() {
		return this.hasStarted() && this.isActive();
	}

	@Nullable
	public BoundingBox getVolume() {
		return this.volume;
	}

	public boolean hasStarted() {
		return this.started;
	}

	public int getGroupsSpawned() {
		return this.wavesSpawned;
	}

	private Predicate<Entity> isInRaidDistance() {
		if (this.villageProperties != null && this.center != null && this.center != BlockPos.ORIGIN) {
			float f = (float)(this.villageProperties.getRadius() + 24);
			return EntityPredicates.VALID_ENTITY
				.and(EntityPredicates.maximumDistance((double)this.center.getX(), (double)this.center.getY(), (double)this.center.getZ(), (double)f));
		} else {
			return EntityPredicates.VALID_ENTITY.negate();
		}
	}

	private void updateBarToPlayers() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers(ServerPlayerEntity.class, this.isInRaidDistance())) {
			this.bar.method_14088(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.bar.method_14092());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.bar.method_14089(serverPlayerEntity2);
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

	public void setWorld(World world) {
		this.world = world;
	}

	public void invalidate() {
		if (this.villageProperties != null) {
			this.villageProperties.setRaidId(0);
		}

		this.setVillage(null);
		this.markedForRemoval = true;
		this.bar.method_14094();
		if (this.world.getVillageManager() != null) {
			this.world.getVillageManager().markDirty();
		}
	}

	public boolean isMarkedForRemoval() {
		return this.markedForRemoval;
	}

	public void tick() {
		boolean bl = this.active;
		if (this.world != null && this.center != null && this.villageProperties != null && this.world.isBlockLoaded(this.center)) {
			this.active = true;
		} else {
			this.active = false;
		}

		if (this.world != null && this.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.invalidate();
		} else {
			if (bl != this.active) {
				this.bar.setVisible(this.active);
			}

			if (this.villageProperties == null && !this.active && this.world.getTime() % 60L == 0L) {
				this.villageProperties = this.world.getVillageManager().getNearestVillage(this.center, 16);
			}

			if (this.active) {
				this.ticksActive++;
				if (this.villageProperties != null && (this.volume == null || this.ticksActive % 60L == 0L)) {
					this.center = this.villageProperties.getCenter();
					this.volume = new BoundingBox(this.center).expand((double)this.villageProperties.getRadius()).expand(16.0);
				}

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
					BlockPos blockPos = this.getPillagerSpawnLocation(this.world, j);
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

	private boolean hasSpawnedAllGroups() {
		return this.getGroupsSpawned() >= this.getBadOmenLevel() * 2;
	}

	private void removeObsoleteRaiders() {
		if (this.villageProperties != null && this.world != null) {
			Iterator<Set<RaiderEntity>> iterator = this.waveToRaiders.values().iterator();
			Set<RaiderEntity> set = new HashSet();

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

					if (!RaidManager.isLivingAroundVillage(this.villageProperties, raiderEntity) || raiderEntity.getDespawnCounter() > 2400) {
						raiderEntity.setOutOfRaidCounter(raiderEntity.getOutOfRaidCounter() + 1);
					}

					if (raiderEntity.getOutOfRaidCounter() >= 30) {
						set.add(raiderEntity);
					}
				}
			}

			for (RaiderEntity raiderEntity2 : set) {
				this.method_16510(raiderEntity2, true);
			}
		}
	}

	private void playRaidHorn(BlockPos blockPos) {
		if (this.world != null && this.villageProperties != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			float f = 13.0F;
			int i = 64;

			for (PlayerEntity playerEntity : serverWorld.players) {
				Vec3d vec3d = new Vec3d(playerEntity.x, playerEntity.y, playerEntity.z);
				Vec3d vec3d2 = new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				float g = MathHelper.sqrt((vec3d2.x - vec3d.x) * (vec3d2.x - vec3d.x) + (vec3d2.z - vec3d.z) * (vec3d2.z - vec3d.z));
				double d = vec3d.x + (double)(13.0F / g) * (vec3d2.x - vec3d.x);
				double e = vec3d.z + (double)(13.0F / g) * (vec3d2.z - vec3d.z);
				if (g <= 64.0F || RaidManager.isLivingAroundVillage(this.villageProperties, playerEntity)) {
					((ServerPlayerEntity)playerEntity)
						.networkHandler
						.sendPacket(new PlaySoundClientPacket(SoundEvents.field_17266, SoundCategory.field_15254, d, playerEntity.y, e, 64.0F, 1.0F));
				}
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
				this.method_16516(i, raiderEntity, blockPos, false);
				if (!bl && raiderEntity.canLead()) {
					raiderEntity.setPatrolLeader(true);
					this.method_16491(i, raiderEntity);
					bl = true;
				}

				if (member.type == EntityType.ILLAGER_BEAST) {
					RaiderEntity raiderEntity2;
					if (i < 6) {
						raiderEntity2 = EntityType.PILLAGER.create(this.world);
					} else {
						raiderEntity2 = EntityType.VINDICATOR.create(this.world);
					}

					this.method_16516(i, raiderEntity2, blockPos, false);
					raiderEntity2.setPositionAndAngles(blockPos, 0.0F, 0.0F);
					raiderEntity2.startRiding(raiderEntity);
				}
			}
		}

		this.wavesSpawned++;
		this.updateBar();
		this.markDirty();
	}

	public void method_16516(int i, RaiderEntity raiderEntity, @Nullable BlockPos blockPos, boolean bl) {
		boolean bl2 = this.method_16505(i, raiderEntity);
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

	public void method_16510(@Nonnull RaiderEntity raiderEntity, boolean bl) {
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
		if (this.world != null) {
			this.world.getRaidManager().markDirty();
		}
	}

	private static ItemStack getIllagerBanner() {
		ItemStack itemStack = new ItemStack(Items.field_8539);
		CompoundTag compoundTag = itemStack.getOrCreateSubCompoundTag("BlockEntityTag");
		ListTag listTag = new BannerPattern.Builder()
			.with(BannerPattern.RHOMBUS, DyeColor.CYAN)
			.with(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY)
			.with(BannerPattern.STRIPE_CENTER, DyeColor.GRAY)
			.with(BannerPattern.BORDER, DyeColor.LIGHT_GRAY)
			.with(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
			.with(BannerPattern.HALF_HORIZONTAL_TOP, DyeColor.LIGHT_GRAY)
			.with(BannerPattern.CIRCLE, DyeColor.LIGHT_GRAY)
			.with(BannerPattern.BORDER, DyeColor.BLACK)
			.build();
		compoundTag.put("Patterns", listTag);
		itemStack.setDisplayName(new TranslatableTextComponent("block.minecraft.illager_banner").applyFormat(TextFormat.GOLD));
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
	public RaiderEntity method_16496(int i) {
		return (RaiderEntity)this.waveToLeader.get(i);
	}

	@Nullable
	private BlockPos getPillagerSpawnLocation(World world, int i) {
		if (this.villageProperties == null) {
			return null;
		} else {
			BlockPos blockPos = this.villageProperties.getCenter();
			float f = (float)this.villageProperties.getRadius();
			int j = i == 0 ? 2 : 2 - i;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = 0; k < 20; k++) {
				float g = world.random.nextFloat() * (float) (Math.PI * 2);
				int l = blockPos.getX() + (int)(MathHelper.cos(g) * f * (float)j) + world.random.nextInt(5);
				int m = blockPos.getZ() + (int)(MathHelper.sin(g) * f * (float)j) + world.random.nextInt(5);
				int n = world.getTop(Heightmap.Type.WORLD_SURFACE, l, m);
				mutable.set(l, n, m);
				if (!this.villageProperties.isInRadius(mutable)
					&& world.isAreaLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)
					&& SpawnHelper.canSpawn(SpawnRestriction.Location.field_6317, world, mutable, EntityType.PILLAGER)) {
					return mutable;
				}
			}

			return null;
		}
	}

	private boolean method_16505(int i, RaiderEntity raiderEntity) {
		return this.method_16487(i, raiderEntity, true);
	}

	public boolean method_16487(int i, RaiderEntity raiderEntity, boolean bl) {
		this.waveToRaiders.computeIfAbsent(i, integer -> new HashSet());
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

	public void method_16491(int i, RaiderEntity raiderEntity) {
		this.waveToLeader.put(i, raiderEntity);
	}

	public void removeLeader(int i) {
		this.waveToLeader.remove(i);
	}

	@Nullable
	public VillageProperties getVillage() {
		return this.villageProperties;
	}

	public void setVillage(VillageProperties villageProperties) {
		this.villageProperties = villageProperties;
		this.center = null;
		if (villageProperties != null) {
			this.center = villageProperties.getCenter();
		}
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
				case ILLAGER_BEAST:
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
				case ILLAGER_BEAST:
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

	public void fromTag(CompoundTag compoundTag) {
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
		if (compoundTag.containsKey("CX", 3)) {
			this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
		}
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
		if (this.center != null) {
			compoundTag.putInt("CX", this.center.getX());
			compoundTag.putInt("CY", this.center.getY());
			compoundTag.putInt("CZ", this.center.getZ());
		}

		return compoundTag;
	}

	static enum Member {
		VINDICATOR(EntityType.VINDICATOR, 1),
		EVOKER(EntityType.EVOKER, 10),
		PILLAGER(EntityType.PILLAGER, 1),
		WITCH(EntityType.WITCH, 4),
		ILLAGER_BEAST(EntityType.ILLAGER_BEAST, 2);

		private static final Raid.Member[] field_16636 = values();
		private final EntityType<? extends RaiderEntity> type;
		private final int firstWave;

		private Member(EntityType<? extends RaiderEntity> entityType, int j) {
			this.type = entityType;
			this.firstWave = j;
		}
	}
}
