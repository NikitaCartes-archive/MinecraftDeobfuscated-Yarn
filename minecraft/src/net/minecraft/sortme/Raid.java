package net.minecraft.sortme;

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
import net.minecraft.class_1317;
import net.minecraft.class_1948;
import net.minecraft.class_3730;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.network.packet.PlaySoundClientPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.RaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
	public static final ItemStack illagerBanner = getIllagerBanner();
	@Nullable
	private final Map<Integer, RaiderEntity> field_16615;
	@Nullable
	private final Map<Integer, Set<RaiderEntity>> field_16618;
	private long ticksActive;
	private BlockPos field_16613 = BlockPos.ORIGIN;
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
	private int groupsSpawned;
	private final ServerBossBar field_16607 = new ServerBossBar(
		new TranslatableTextComponent("event.minecraft.raid"), BossBar.Color.field_5784, BossBar.Overlay.field_5791
	);
	private int postRaidTicks;
	private int preRaidTicks;
	@Nullable
	private BoundingBox field_16617;
	private final Random random = new Random();

	public Raid() {
		this.field_16615 = new HashMap();
		this.field_16618 = new HashMap();
		this.ticksActive = 0L;
	}

	public Raid(int i, World world, VillageProperties villageProperties) {
		this();
		this.id = i;
		this.world = world;
		this.method_16489(world);
		this.active = true;
		this.badOmenLevel = 0;
		this.groupsSpawned = 0;
		this.postRaidTicks = 0;
		this.preRaidTicks = 300;
		this.field_16607.setPercent(0.0F);
		if (villageProperties != null) {
			this.method_16512(villageProperties);
			villageProperties.method_16468(i);
		}
	}

	public World method_16831() {
		return this.world;
	}

	public boolean method_16832() {
		return this.hasStarted() && this.isActive();
	}

	@Nullable
	public BoundingBox method_16498() {
		return this.field_16617;
	}

	public boolean hasStarted() {
		return this.started;
	}

	public int getGroupsSpawned() {
		return this.groupsSpawned;
	}

	private Predicate<Entity> method_16501() {
		if (this.villageProperties != null && this.field_16613 != null && this.field_16613 != BlockPos.ORIGIN) {
			float f = (float)(this.villageProperties.getRadius() + 24);
			return EntityPredicates.VALID_ENTITY
				.and(EntityPredicates.maximumDistance((double)this.field_16613.getX(), (double)this.field_16613.getY(), (double)this.field_16613.getZ(), (double)f));
		} else {
			return EntityPredicates.VALID_ENTITY.negate();
		}
	}

	private void method_16499() {
		Set<ServerPlayerEntity> set = Sets.<ServerPlayerEntity>newHashSet();

		for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers(ServerPlayerEntity.class, this.method_16501())) {
			this.field_16607.method_14088(serverPlayerEntity);
			set.add(serverPlayerEntity);
		}

		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet(this.field_16607.method_14092());
		set2.removeAll(set);

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.field_16607.method_14089(serverPlayerEntity2);
		}
	}

	public int method_16514() {
		return 5;
	}

	public int getBadOmenLevel() {
		return this.badOmenLevel;
	}

	public void method_16518(PlayerEntity playerEntity) {
		int i = this.method_16514();
		if (playerEntity.hasPotionEffect(StatusEffects.field_16595)) {
			this.badOmenLevel = this.badOmenLevel + playerEntity.getPotionEffect(StatusEffects.field_16595).getAmplifier() + 1;
			if (i < this.badOmenLevel) {
				this.badOmenLevel = i;
			}
		}

		playerEntity.method_6016(StatusEffects.field_16595);
	}

	public void method_16489(World world) {
		this.world = world;
	}

	public void method_16506() {
		if (this.villageProperties != null) {
			this.villageProperties.method_16468(0);
		}

		this.method_16512(null);
		this.markedForRemoval = true;
		this.field_16607.method_14094();
		if (this.world.getVillageManager() != null) {
			this.world.getVillageManager().markDirty();
		}
	}

	public boolean method_16503() {
		return this.markedForRemoval;
	}

	public void method_16509() {
		boolean bl = this.active;
		if (this.world != null && this.field_16613 != null && this.villageProperties != null && this.world.isBlockLoaded(this.field_16613)) {
			this.active = true;
		} else {
			this.active = false;
		}

		if (this.world != null && this.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.method_16506();
		} else {
			if (bl != this.active) {
				this.field_16607.setVisible(this.active);
			}

			if (this.villageProperties == null && !this.active && this.world.getTime() % 60L == 0L) {
				this.villageProperties = this.world.getVillageManager().getNearestVillage(this.field_16613, 16);
			}

			if (this.active) {
				this.ticksActive++;
				if (this.villageProperties != null && (this.field_16617 == null || this.ticksActive % 60L == 0L)) {
					this.field_16613 = this.villageProperties.getCenter();
					this.field_16617 = new BoundingBox(this.field_16613).expand((double)this.villageProperties.getRadius()).expand(16.0);
				}

				int i = this.method_16517();
				if (i == 0 && !this.method_16833()) {
					if (this.preRaidTicks > 0) {
						if (this.preRaidTicks == 300 || this.preRaidTicks % 20 == 0) {
							this.method_16499();
						}

						this.preRaidTicks--;
						this.field_16607.setPercent(MathHelper.clamp((float)(300 - this.preRaidTicks) / 300.0F, 0.0F, 100.0F));
					} else if (this.preRaidTicks == 0 && this.groupsSpawned > 0) {
						this.preRaidTicks = 300;
						this.field_16607.setName(new TranslatableTextComponent("event.minecraft.raid"));
						return;
					}
				}

				if (i > 0 && this.preRaidTicks == 0 && this.ticksActive % 20L == 0L) {
					this.method_16499();
					this.method_16834();
					if (i <= 2) {
						this.field_16607
							.setName(
								new TranslatableTextComponent("event.minecraft.raid").append(" - ").append(new TranslatableTextComponent("event.minecraft.raid.mobs_remaining", i))
							);
					} else {
						this.field_16607.setName(new TranslatableTextComponent("event.minecraft.raid"));
					}
				}

				boolean bl2 = false;
				int j = 0;

				while (this.method_16519()) {
					BlockPos blockPos = this.method_16525(this.world, j);
					if (blockPos != null) {
						this.started = true;
						this.method_16522(blockPos);
						if (!bl2) {
							this.method_16521(blockPos);
							bl2 = true;
						}
					} else {
						j++;
					}

					if (j > 2) {
						this.method_16506();
						break;
					}
				}

				if (this.hasStarted() && this.getGroupsSpawned() >= this.getBadOmenLevel() * 2 && i == 0) {
					if (this.postRaidTicks < 40) {
						this.postRaidTicks++;
					} else {
						this.method_16506();
					}
				}

				this.method_16520();
			}
		}
	}

	private boolean method_16833() {
		return this.getGroupsSpawned() >= this.getBadOmenLevel() * 2;
	}

	private void method_16834() {
		if (this.villageProperties != null && this.world != null) {
			Iterator<Set<RaiderEntity>> iterator = this.field_16618.values().iterator();
			Set<RaiderEntity> set = new HashSet();

			while (iterator.hasNext()) {
				Set<RaiderEntity> set2 = (Set<RaiderEntity>)iterator.next();

				for (RaiderEntity raiderEntity : set2) {
					if (!raiderEntity.invalid && raiderEntity.dimension == this.world.getDimension().getType()) {
						if (raiderEntity.age <= 300) {
							continue;
						}
					} else {
						raiderEntity.method_16835(30);
					}

					if (!RaidState.method_16537(this.villageProperties, raiderEntity) || raiderEntity.method_6131() > 2400) {
						raiderEntity.method_16835(raiderEntity.method_16836() + 1);
					}

					if (raiderEntity.method_16836() >= 30) {
						set.add(raiderEntity);
					}
				}
			}

			for (RaiderEntity raiderEntity2 : set) {
				this.method_16510(raiderEntity2, true);
			}
		}
	}

	private void method_16521(BlockPos blockPos) {
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
				if (!(g <= 64.0F) && !RaidState.method_16537(this.villageProperties, playerEntity)) {
					this.world.fireWorldEvent(null, 1042, new BlockPos(d, playerEntity.y, e), 0);
				} else {
					((ServerPlayerEntity)playerEntity)
						.networkHandler
						.sendPacket(new PlaySoundClientPacket(SoundEvents.field_16689, SoundCategory.field_15254, d, playerEntity.y, e, 64.0F, 1.0F));
				}
			}
		}
	}

	private void method_16522(BlockPos blockPos) {
		boolean bl = false;
		int i = this.groupsSpawned + 1;
		this.totalHealth = 0.0F;

		for (Raid.Member member : Raid.Member.field_16636) {
			int j = method_16488(member, this.random, i) + method_16492(member, this.random, i);

			for (int k = 0; k < j; k++) {
				RaiderEntity raiderEntity = member.field_16629.create(this.world);
				this.method_16516(i, raiderEntity, blockPos, false);
				if (!bl && raiderEntity.method_16485()) {
					raiderEntity.method_16217(true);
					this.method_16491(i, raiderEntity);
					bl = true;
				}

				if (member.field_16629 == EntityType.ILLAGER_BEAST) {
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

		this.groupsSpawned++;
		this.method_16523();
		this.method_16520();
	}

	public void method_16516(int i, RaiderEntity raiderEntity, @Nullable BlockPos blockPos, boolean bl) {
		boolean bl2 = this.method_16505(i, raiderEntity);
		if (bl2) {
			raiderEntity.setRaid(this);
			raiderEntity.setWave(i);
			raiderEntity.setHasRaidGoal(true);
			raiderEntity.method_16835(0);
			if (!bl && blockPos != null) {
				raiderEntity.setPosition((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.0, (double)blockPos.getZ() + 0.5);
				raiderEntity.method_5943(this.world, this.world.getLocalDifficulty(blockPos), class_3730.field_16467, null, null);
				raiderEntity.method_16484(i, false);
				raiderEntity.onGround = true;
				this.world.spawnEntity(raiderEntity);
			}
		}
	}

	private void method_16523() {
		this.field_16607.setPercent(MathHelper.clamp(this.method_16513() / this.totalHealth, 0.0F, 100.0F));
	}

	public float method_16513() {
		float f = 0.0F;

		for (Set<RaiderEntity> set : this.field_16618.values()) {
			for (RaiderEntity raiderEntity : set) {
				f += raiderEntity.getHealth();
			}
		}

		return f;
	}

	private boolean method_16519() {
		return this.preRaidTicks == 0 && this.groupsSpawned < this.badOmenLevel * 2 && this.method_16517() == 0;
	}

	public int method_16517() {
		return this.field_16618.values().stream().mapToInt(Set::size).sum();
	}

	public void method_16510(@Nonnull RaiderEntity raiderEntity, boolean bl) {
		Set<RaiderEntity> set = (Set<RaiderEntity>)this.field_16618.get(raiderEntity.getWave());
		if (set != null) {
			boolean bl2 = set.remove(raiderEntity);
			if (bl2) {
				if (bl) {
					this.totalHealth = this.totalHealth - raiderEntity.getHealth();
				}

				this.method_16523();
				this.method_16520();
			}
		}
	}

	private void method_16520() {
		if (this.world != null) {
			this.world.getRaidState().markDirty();
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

	@Nullable
	public RaiderEntity method_16496(int i) {
		return (RaiderEntity)this.field_16615.get(i);
	}

	@Nullable
	private BlockPos method_16525(World world, int i) {
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
					&& class_1948.method_8660(class_1317.class_1319.field_6317, world, mutable, EntityType.PILLAGER)) {
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
		this.field_16618.computeIfAbsent(i, integer -> new HashSet());
		if (((Set)this.field_16618.get(i)).contains(raiderEntity)) {
			return false;
		} else {
			((Set)this.field_16618.get(i)).add(raiderEntity);
			if (bl) {
				this.totalHealth = this.totalHealth + raiderEntity.getHealth();
			}

			this.method_16523();
			this.method_16520();
			return true;
		}
	}

	public void method_16491(int i, RaiderEntity raiderEntity) {
		this.field_16615.put(i, raiderEntity);
	}

	public void method_16500(int i) {
		this.field_16615.remove(i);
	}

	public VillageProperties getVillageProperties() {
		return this.villageProperties;
	}

	public void method_16512(VillageProperties villageProperties) {
		this.villageProperties = villageProperties;
		this.field_16613 = null;
		if (villageProperties != null) {
			this.field_16613 = villageProperties.getCenter();
		}
	}

	public BlockPos method_16495() {
		return this.field_16613;
	}

	public int getRaidId() {
		return this.id;
	}

	private static int method_16488(Raid.Member member, Random random, int i) {
		if (member.field_16628 > i) {
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

	private static int method_16492(Raid.Member member, Random random, int i) {
		if (member.field_16628 > i) {
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

	public void deserialize(CompoundTag compoundTag) {
		this.id = compoundTag.getInt("Id");
		this.started = compoundTag.getBoolean("Started");
		this.active = compoundTag.getBoolean("Active");
		this.ticksActive = compoundTag.getLong("TicksActive");
		this.markedForRemoval = compoundTag.getBoolean("MarkedForRemoval");
		this.ignoreDayRequirement = compoundTag.getBoolean("IgnoreDayRequirement");
		this.badOmenLevel = compoundTag.getInt("BadOmenLevel");
		this.groupsSpawned = compoundTag.getInt("GroupsSpawned");
		this.preRaidTicks = compoundTag.getInt("PreRaidTicks");
		this.postRaidTicks = compoundTag.getInt("PostRaidTicks");
		this.totalHealth = compoundTag.getFloat("TotalHealth");
		if (compoundTag.containsKey("CX", 3)) {
			this.field_16613 = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
		}
	}

	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putInt("Id", this.id);
		compoundTag.putBoolean("Started", this.started);
		compoundTag.putBoolean("Active", this.active);
		compoundTag.putLong("TicksActive", this.ticksActive);
		compoundTag.putBoolean("MarkedForRemoval", this.markedForRemoval);
		compoundTag.putBoolean("IgnoreDayRequirement", this.ignoreDayRequirement);
		compoundTag.putInt("BadOmenLevel", this.badOmenLevel);
		compoundTag.putInt("GroupsSpawned", this.groupsSpawned);
		compoundTag.putInt("PreRaidTicks", this.preRaidTicks);
		compoundTag.putInt("PostRaidTicks", this.postRaidTicks);
		compoundTag.putFloat("TotalHealth", this.totalHealth);
		if (this.field_16613 != null) {
			compoundTag.putInt("CX", this.field_16613.getX());
			compoundTag.putInt("CY", this.field_16613.getY());
			compoundTag.putInt("CZ", this.field_16613.getZ());
		}

		return compoundTag;
	}

	static enum Member {
		VINDICATOR(EntityType.VINDICATOR, 1),
		EVOKER(EntityType.EVOKER, 9),
		PILLAGER(EntityType.PILLAGER, 1),
		WITCH(EntityType.WITCH, 3),
		ILLAGER_BEAST(EntityType.ILLAGER_BEAST, 1);

		private static final Raid.Member[] field_16636 = values();
		private final EntityType<? extends RaiderEntity> field_16629;
		private final int field_16628;

		private Member(EntityType<? extends RaiderEntity> entityType, int j) {
			this.field_16629 = entityType;
			this.field_16628 = j;
		}
	}
}
