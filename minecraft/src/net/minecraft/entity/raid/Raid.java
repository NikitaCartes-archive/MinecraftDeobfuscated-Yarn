package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;

public class Raid {
	private static final TranslatableText EVENT_TEXT = new TranslatableText("event.minecraft.raid");
	private static final TranslatableText VICTORY_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.victory");
	private static final TranslatableText DEFEAT_SUFFIX_TEXT = new TranslatableText("event.minecraft.raid.defeat");
	private static final Text VICTORY_TITLE = EVENT_TEXT.copy().append(" - ").append(VICTORY_SUFFIX_TEXT);
	private static final Text DEFEAT_TITLE = EVENT_TEXT.copy().append(" - ").append(DEFEAT_SUFFIX_TEXT);
	private final Map<Integer, RaiderEntity> waveToCaptain = Maps.<Integer, RaiderEntity>newHashMap();
	private final Map<Integer, Set<RaiderEntity>> waveToRaiders = Maps.<Integer, Set<RaiderEntity>>newHashMap();
	private final Set<UUID> heroesOfTheVillage = Sets.<UUID>newHashSet();
	private long ticksActive;
	private BlockPos center;
	private final ServerWorld world;
	private boolean started;
	private final int id;
	private float totalHealth;
	private int badOmenLevel;
	private boolean active;
	private int wavesSpawned;
	private final ServerBossBar bar = new ServerBossBar(EVENT_TEXT, BossBar.Color.RED, BossBar.Style.NOTCHED_10);
	private int postRaidTicks;
	private int preRaidTicks;
	private final Random random = new Random();
	private final int waveCount;
	private Raid.Status status;
	private int finishCooldown;
	private Optional<BlockPos> preCalculatedRavagerSpawnLocation = Optional.empty();

	public Raid(int id, ServerWorld world, BlockPos pos) {
		this.id = id;
		this.world = world;
		this.active = true;
		this.preRaidTicks = 300;
		this.bar.setPercent(0.0F);
		this.center = pos;
		this.waveCount = this.getMaxWaves(world.getDifficulty());
		this.status = Raid.Status.ONGOING;
	}

	public Raid(ServerWorld world, CompoundTag tag) {
		this.world = world;
		this.id = tag.getInt("Id");
		this.started = tag.getBoolean("Started");
		this.active = tag.getBoolean("Active");
		this.ticksActive = tag.getLong("TicksActive");
		this.badOmenLevel = tag.getInt("BadOmenLevel");
		this.wavesSpawned = tag.getInt("GroupsSpawned");
		this.preRaidTicks = tag.getInt("PreRaidTicks");
		this.postRaidTicks = tag.getInt("PostRaidTicks");
		this.totalHealth = tag.getFloat("TotalHealth");
		this.center = new BlockPos(tag.getInt("CX"), tag.getInt("CY"), tag.getInt("CZ"));
		this.waveCount = tag.getInt("NumGroups");
		this.status = Raid.Status.fromName(tag.getString("Status"));
		this.heroesOfTheVillage.clear();
		if (tag.contains("HeroesOfTheVillage", 9)) {
			ListTag listTag = tag.getList("HeroesOfTheVillage", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag = listTag.getCompound(i);
				UUID uUID = compoundTag.getUuid("UUID");
				this.heroesOfTheVillage.add(uUID);
			}
		}
	}

	public boolean isFinished() {
		return this.hasWon() || this.hasLost();
	}

	public boolean isPreRaid() {
		return this.hasSpawned() && this.getRaiderCount() == 0 && this.preRaidTicks > 0;
	}

	public boolean hasSpawned() {
		return this.wavesSpawned > 0;
	}

	public boolean hasStopped() {
		return this.status == Raid.Status.STOPPED;
	}

	public boolean hasWon() {
		return this.status == Raid.Status.VICTORY;
	}

	public boolean hasLost() {
		return this.status == Raid.Status.LOSS;
	}

	public World getWorld() {
		return this.world;
	}

	public boolean hasStarted() {
		return this.started;
	}

	public int getGroupsSpawned() {
		return this.wavesSpawned;
	}

	private Predicate<ServerPlayerEntity> isInRaidDistance() {
		return serverPlayerEntity -> {
			BlockPos blockPos = new BlockPos(serverPlayerEntity);
			return serverPlayerEntity.isAlive() && this.world.getRaidAt(blockPos) == this;
		};
	}

	private void updateBarToPlayers() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet(this.bar.getPlayers());
		List<ServerPlayerEntity> list = this.world.getPlayers(this.isInRaidDistance());

		for (ServerPlayerEntity serverPlayerEntity : list) {
			if (!set.contains(serverPlayerEntity)) {
				this.bar.addPlayer(serverPlayerEntity);
			}
		}

		for (ServerPlayerEntity serverPlayerEntityx : set) {
			if (!list.contains(serverPlayerEntityx)) {
				this.bar.removePlayer(serverPlayerEntityx);
			}
		}
	}

	public int getMaxAcceptableBadOmenLevel() {
		return 5;
	}

	public int getBadOmenLevel() {
		return this.badOmenLevel;
	}

	public void start(PlayerEntity player) {
		if (player.hasStatusEffect(StatusEffects.BAD_OMEN)) {
			this.badOmenLevel = this.badOmenLevel + player.getStatusEffect(StatusEffects.BAD_OMEN).getAmplifier() + 1;
			this.badOmenLevel = MathHelper.clamp(this.badOmenLevel, 0, this.getMaxAcceptableBadOmenLevel());
		}

		player.removeStatusEffect(StatusEffects.BAD_OMEN);
	}

	public void invalidate() {
		this.active = false;
		this.bar.clearPlayers();
		this.status = Raid.Status.STOPPED;
	}

	public void tick() {
		if (!this.hasStopped()) {
			if (this.status == Raid.Status.ONGOING) {
				boolean bl = this.active;
				this.active = this.world.isChunkLoaded(this.center);
				if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
					this.invalidate();
					return;
				}

				if (bl != this.active) {
					this.bar.setVisible(this.active);
				}

				if (!this.active) {
					return;
				}

				if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
					this.method_20511();
				}

				if (!this.world.isNearOccupiedPointOfInterest(this.center)) {
					if (this.wavesSpawned > 0) {
						this.status = Raid.Status.LOSS;
					} else {
						this.invalidate();
					}
				}

				this.ticksActive++;
				if (this.ticksActive >= 48000L) {
					this.invalidate();
					return;
				}

				int i = this.getRaiderCount();
				if (i == 0 && this.shouldSpawnMoreGroups()) {
					if (this.preRaidTicks <= 0) {
						if (this.preRaidTicks == 0 && this.wavesSpawned > 0) {
							this.preRaidTicks = 300;
							this.bar.setName(EVENT_TEXT);
							return;
						}
					} else {
						boolean bl2 = this.preCalculatedRavagerSpawnLocation.isPresent();
						boolean bl3 = !bl2 && this.preRaidTicks % 5 == 0;
						if (bl2 && !this.world.getChunkManager().shouldTickChunk(new ChunkPos((BlockPos)this.preCalculatedRavagerSpawnLocation.get()))) {
							bl3 = true;
						}

						if (bl3) {
							int j = 0;
							if (this.preRaidTicks < 100) {
								j = 1;
							} else if (this.preRaidTicks < 40) {
								j = 2;
							}

							this.preCalculatedRavagerSpawnLocation = this.preCalculateRavagerSpawnLocation(j);
						}

						if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
							this.updateBarToPlayers();
						}

						this.preRaidTicks--;
						this.bar.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0F, 0.0F, 1.0F));
					}
				}

				if (this.ticksActive % 20L == 0L) {
					this.updateBarToPlayers();
					this.removeObsoleteRaiders();
					if (i > 0) {
						if (i <= 2) {
							this.bar.setName(EVENT_TEXT.copy().append(" - ").append(new TranslatableText("event.minecraft.raid.raiders_remaining", i)));
						} else {
							this.bar.setName(EVENT_TEXT);
						}
					} else {
						this.bar.setName(EVENT_TEXT);
					}
				}

				boolean bl2x = false;
				int k = 0;

				while (this.canSpawnRaiders()) {
					BlockPos blockPos = this.preCalculatedRavagerSpawnLocation.isPresent()
						? (BlockPos)this.preCalculatedRavagerSpawnLocation.get()
						: this.getRavagerSpawnLocation(k, 20);
					if (blockPos != null) {
						this.started = true;
						this.spawnNextWave(blockPos);
						if (!bl2x) {
							this.playRaidHorn(blockPos);
							bl2x = true;
						}
					} else {
						k++;
					}

					if (k > 3) {
						this.invalidate();
						break;
					}
				}

				if (this.hasStarted() && !this.shouldSpawnMoreGroups() && i == 0) {
					if (this.postRaidTicks < 40) {
						this.postRaidTicks++;
					} else {
						this.status = Raid.Status.VICTORY;

						for (UUID uUID : this.heroesOfTheVillage) {
							Entity entity = this.world.getEntity(uUID);
							if (entity instanceof LivingEntity && !entity.isSpectator()) {
								LivingEntity livingEntity = (LivingEntity)entity;
								livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
								if (livingEntity instanceof ServerPlayerEntity) {
									ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
									serverPlayerEntity.incrementStat(Stats.RAID_WIN);
									Criterions.HERO_OF_THE_VILLAGE.trigger(serverPlayerEntity);
								}
							}
						}
					}
				}

				this.markDirty();
			} else if (this.isFinished()) {
				this.finishCooldown++;
				if (this.finishCooldown >= 600) {
					this.invalidate();
					return;
				}

				if (this.finishCooldown % 20 == 0) {
					this.updateBarToPlayers();
					this.bar.setVisible(true);
					if (this.hasWon()) {
						this.bar.setPercent(0.0F);
						this.bar.setName(VICTORY_TITLE);
					} else {
						this.bar.setName(DEFEAT_TITLE);
					}
				}
			}
		}
	}

	private void method_20511() {
		Stream<ChunkSectionPos> stream = ChunkSectionPos.stream(ChunkSectionPos.from(this.center), 2);
		stream.filter(this.world::isNearOccupiedPointOfInterest)
			.map(ChunkSectionPos::getCenterPos)
			.min(Comparator.comparingDouble(blockPos -> blockPos.getSquaredDistance(this.center)))
			.ifPresent(this::method_20509);
	}

	private Optional<BlockPos> preCalculateRavagerSpawnLocation(int proximity) {
		for (int i = 0; i < 3; i++) {
			BlockPos blockPos = this.getRavagerSpawnLocation(proximity, 1);
			if (blockPos != null) {
				return Optional.of(blockPos);
			}
		}

		return Optional.empty();
	}

	private boolean shouldSpawnMoreGroups() {
		return this.hasExtraWave() ? !this.hasSpawnedExtraWave() : !this.hasSpawnedFinalWave();
	}

	private boolean hasSpawnedFinalWave() {
		return this.getGroupsSpawned() == this.waveCount;
	}

	private boolean hasExtraWave() {
		return this.badOmenLevel > 1;
	}

	private boolean hasSpawnedExtraWave() {
		return this.getGroupsSpawned() > this.waveCount;
	}

	private boolean isSpawningExtraWave() {
		return this.hasSpawnedFinalWave() && this.getRaiderCount() == 0 && this.hasExtraWave();
	}

	private void removeObsoleteRaiders() {
		Iterator<Set<RaiderEntity>> iterator = this.waveToRaiders.values().iterator();
		Set<RaiderEntity> set = Sets.<RaiderEntity>newHashSet();

		while (iterator.hasNext()) {
			Set<RaiderEntity> set2 = (Set<RaiderEntity>)iterator.next();

			for (RaiderEntity raiderEntity : set2) {
				BlockPos blockPos = new BlockPos(raiderEntity);
				if (raiderEntity.removed || raiderEntity.dimension != this.world.getDimension().getType() || this.center.getSquaredDistance(blockPos) >= 12544.0) {
					set.add(raiderEntity);
				} else if (raiderEntity.age > 600) {
					if (this.world.getEntity(raiderEntity.getUuid()) == null) {
						set.add(raiderEntity);
					}

					if (!this.world.isNearOccupiedPointOfInterest(blockPos) && raiderEntity.getDespawnCounter() > 2400) {
						raiderEntity.setOutOfRaidCounter(raiderEntity.getOutOfRaidCounter() + 1);
					}

					if (raiderEntity.getOutOfRaidCounter() >= 30) {
						set.add(raiderEntity);
					}
				}
			}
		}

		for (RaiderEntity raiderEntity2 : set) {
			this.removeFromWave(raiderEntity2, true);
		}
	}

	private void playRaidHorn(BlockPos pos) {
		float f = 13.0F;
		int i = 64;
		Collection<ServerPlayerEntity> collection = this.bar.getPlayers();

		for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers()) {
			Vec3d vec3d = serverPlayerEntity.getPos();
			Vec3d vec3d2 = new Vec3d(pos);
			float g = MathHelper.sqrt((vec3d2.x - vec3d.x) * (vec3d2.x - vec3d.x) + (vec3d2.z - vec3d.z) * (vec3d2.z - vec3d.z));
			double d = vec3d.x + (double)(13.0F / g) * (vec3d2.x - vec3d.x);
			double e = vec3d.z + (double)(13.0F / g) * (vec3d2.z - vec3d.z);
			if (g <= 64.0F || collection.contains(serverPlayerEntity)) {
				serverPlayerEntity.networkHandler
					.sendPacket(new PlaySoundS2CPacket(SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, d, serverPlayerEntity.getY(), e, 64.0F, 1.0F));
			}
		}
	}

	private void spawnNextWave(BlockPos pos) {
		boolean bl = false;
		int i = this.wavesSpawned + 1;
		this.totalHealth = 0.0F;
		LocalDifficulty localDifficulty = this.world.getLocalDifficulty(pos);
		boolean bl2 = this.isSpawningExtraWave();

		for (Raid.Member member : Raid.Member.VALUES) {
			int j = this.getCount(member, i, bl2) + this.getBonusCount(member, this.random, i, localDifficulty, bl2);
			int k = 0;

			for (int l = 0; l < j; l++) {
				RaiderEntity raiderEntity = member.type.create(this.world);
				if (!bl && raiderEntity.canLead()) {
					raiderEntity.setPatrolLeader(true);
					this.setWaveCaptain(i, raiderEntity);
					bl = true;
				}

				this.addRaider(i, raiderEntity, pos, false);
				if (member.type == EntityType.RAVAGER) {
					RaiderEntity raiderEntity2 = null;
					if (i == this.getMaxWaves(Difficulty.NORMAL)) {
						raiderEntity2 = EntityType.PILLAGER.create(this.world);
					} else if (i >= this.getMaxWaves(Difficulty.HARD)) {
						if (k == 0) {
							raiderEntity2 = EntityType.EVOKER.create(this.world);
						} else {
							raiderEntity2 = EntityType.VINDICATOR.create(this.world);
						}
					}

					k++;
					if (raiderEntity2 != null) {
						this.addRaider(i, raiderEntity2, pos, false);
						raiderEntity2.refreshPositionAndAngles(pos, 0.0F, 0.0F);
						raiderEntity2.startRiding(raiderEntity);
					}
				}
			}
		}

		this.preCalculatedRavagerSpawnLocation = Optional.empty();
		this.wavesSpawned++;
		this.updateBar();
		this.markDirty();
	}

	public void addRaider(int wave, RaiderEntity raider, @Nullable BlockPos pos, boolean existing) {
		boolean bl = this.addToWave(wave, raider);
		if (bl) {
			raider.setRaid(this);
			raider.setWave(wave);
			raider.setAbleToJoinRaid(true);
			raider.setOutOfRaidCounter(0);
			if (!existing && pos != null) {
				raider.updatePosition((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5);
				raider.initialize(this.world, this.world.getLocalDifficulty(pos), SpawnType.EVENT, null, null);
				raider.addBonusForWave(wave, false);
				raider.onGround = true;
				this.world.spawnEntity(raider);
			}
		}
	}

	public void updateBar() {
		this.bar.setPercent(MathHelper.clamp(this.getCurrentRaiderHealth() / this.totalHealth, 0.0F, 1.0F));
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
		return this.preRaidTicks == 0 && (this.wavesSpawned < this.waveCount || this.isSpawningExtraWave()) && this.getRaiderCount() == 0;
	}

	public int getRaiderCount() {
		return this.waveToRaiders.values().stream().mapToInt(Set::size).sum();
	}

	public void removeFromWave(RaiderEntity entity, boolean countHealth) {
		Set<RaiderEntity> set = (Set<RaiderEntity>)this.waveToRaiders.get(entity.getWave());
		if (set != null) {
			boolean bl = set.remove(entity);
			if (bl) {
				if (countHealth) {
					this.totalHealth = this.totalHealth - entity.getHealth();
				}

				entity.setRaid(null);
				this.updateBar();
				this.markDirty();
			}
		}
	}

	private void markDirty() {
		this.world.getRaidManager().markDirty();
	}

	public static ItemStack getOminousBanner() {
		ItemStack itemStack = new ItemStack(Items.WHITE_BANNER);
		CompoundTag compoundTag = itemStack.getOrCreateSubTag("BlockEntityTag");
		ListTag listTag = new BannerPattern.Patterns()
			.add(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN)
			.add(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY)
			.add(BannerPattern.STRIPE_CENTER, DyeColor.GRAY)
			.add(BannerPattern.BORDER, DyeColor.LIGHT_GRAY)
			.add(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
			.add(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY)
			.add(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY)
			.add(BannerPattern.BORDER, DyeColor.BLACK)
			.toTag();
		compoundTag.put("Patterns", listTag);
		itemStack.setCustomName(new TranslatableText("block.minecraft.ominous_banner").formatted(Formatting.GOLD));
		return itemStack;
	}

	@Nullable
	public RaiderEntity getCaptain(int wave) {
		return (RaiderEntity)this.waveToCaptain.get(wave);
	}

	@Nullable
	private BlockPos getRavagerSpawnLocation(int proximity, int tries) {
		int i = proximity == 0 ? 2 : 2 - proximity;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < tries; j++) {
			float f = this.world.random.nextFloat() * (float) (Math.PI * 2);
			int k = this.center.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F * (float)i) + this.world.random.nextInt(5);
			int l = this.center.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F * (float)i) + this.world.random.nextInt(5);
			int m = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
			mutable.set(k, m, l);
			if ((!this.world.isNearOccupiedPointOfInterest(mutable) || proximity >= 2)
				&& this.world.isRegionLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)
				&& this.world.getChunkManager().shouldTickChunk(new ChunkPos(mutable))
				&& (
					SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, this.world, mutable, EntityType.RAVAGER)
						|| this.world.getBlockState(mutable.down()).getBlock() == Blocks.SNOW && this.world.getBlockState(mutable).isAir()
				)) {
				return mutable;
			}
		}

		return null;
	}

	private boolean addToWave(int wave, RaiderEntity entity) {
		return this.addToWave(wave, entity, true);
	}

	public boolean addToWave(int wave, RaiderEntity entity, boolean countHealth) {
		this.waveToRaiders.computeIfAbsent(wave, integer -> Sets.newHashSet());
		Set<RaiderEntity> set = (Set<RaiderEntity>)this.waveToRaiders.get(wave);
		RaiderEntity raiderEntity = null;

		for (RaiderEntity raiderEntity2 : set) {
			if (raiderEntity2.getUuid().equals(entity.getUuid())) {
				raiderEntity = raiderEntity2;
				break;
			}
		}

		if (raiderEntity != null) {
			set.remove(raiderEntity);
			set.add(entity);
		}

		set.add(entity);
		if (countHealth) {
			this.totalHealth = this.totalHealth + entity.getHealth();
		}

		this.updateBar();
		this.markDirty();
		return true;
	}

	public void setWaveCaptain(int wave, RaiderEntity entity) {
		this.waveToCaptain.put(wave, entity);
		entity.equipStack(EquipmentSlot.HEAD, getOminousBanner());
		entity.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
	}

	public void removeLeader(int wave) {
		this.waveToCaptain.remove(wave);
	}

	public BlockPos getCenter() {
		return this.center;
	}

	private void method_20509(BlockPos blockPos) {
		this.center = blockPos;
	}

	public int getRaidId() {
		return this.id;
	}

	private int getCount(Raid.Member member, int wave, boolean extra) {
		return extra ? member.countInWave[this.waveCount] : member.countInWave[wave];
	}

	private int getBonusCount(Raid.Member member, Random random, int wave, LocalDifficulty localDifficulty, boolean extra) {
		Difficulty difficulty = localDifficulty.getGlobalDifficulty();
		boolean bl = difficulty == Difficulty.EASY;
		boolean bl2 = difficulty == Difficulty.NORMAL;
		int i;
		switch (member) {
			case WITCH:
				if (bl || wave <= 2 || wave == 4) {
					return 0;
				}

				i = 1;
				break;
			case PILLAGER:
			case VINDICATOR:
				if (bl) {
					i = random.nextInt(2);
				} else if (bl2) {
					i = 1;
				} else {
					i = 2;
				}
				break;
			case RAVAGER:
				i = !bl && extra ? 1 : 0;
				break;
			default:
				return 0;
		}

		return i > 0 ? random.nextInt(i + 1) : 0;
	}

	public boolean isActive() {
		return this.active;
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("Id", this.id);
		tag.putBoolean("Started", this.started);
		tag.putBoolean("Active", this.active);
		tag.putLong("TicksActive", this.ticksActive);
		tag.putInt("BadOmenLevel", this.badOmenLevel);
		tag.putInt("GroupsSpawned", this.wavesSpawned);
		tag.putInt("PreRaidTicks", this.preRaidTicks);
		tag.putInt("PostRaidTicks", this.postRaidTicks);
		tag.putFloat("TotalHealth", this.totalHealth);
		tag.putInt("NumGroups", this.waveCount);
		tag.putString("Status", this.status.getName());
		tag.putInt("CX", this.center.getX());
		tag.putInt("CY", this.center.getY());
		tag.putInt("CZ", this.center.getZ());
		ListTag listTag = new ListTag();

		for (UUID uUID : this.heroesOfTheVillage) {
			CompoundTag compoundTag = new CompoundTag();
			compoundTag.putUuid("UUID", uUID);
			listTag.add(compoundTag);
		}

		tag.put("HeroesOfTheVillage", listTag);
		return tag;
	}

	public int getMaxWaves(Difficulty difficulty) {
		switch (difficulty) {
			case EASY:
				return 3;
			case NORMAL:
				return 5;
			case HARD:
				return 7;
			default:
				return 0;
		}
	}

	public float getEnchantmentChance() {
		int i = this.getBadOmenLevel();
		if (i == 2) {
			return 0.1F;
		} else if (i == 3) {
			return 0.25F;
		} else if (i == 4) {
			return 0.5F;
		} else {
			return i == 5 ? 0.75F : 0.0F;
		}
	}

	public void addHero(Entity entity) {
		this.heroesOfTheVillage.add(entity.getUuid());
	}

	static enum Member {
		VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
		EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
		PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
		WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
		RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

		private static final Raid.Member[] VALUES = values();
		private final EntityType<? extends RaiderEntity> type;
		private final int[] countInWave;

		private Member(EntityType<? extends RaiderEntity> type, int[] countInWave) {
			this.type = type;
			this.countInWave = countInWave;
		}
	}

	static enum Status {
		ONGOING,
		VICTORY,
		LOSS,
		STOPPED;

		private static final Raid.Status[] VALUES = values();

		private static Raid.Status fromName(String string) {
			for (Raid.Status status : VALUES) {
				if (string.equalsIgnoreCase(status.name())) {
					return status;
				}
			}

			return ONGOING;
		}

		public String getName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
