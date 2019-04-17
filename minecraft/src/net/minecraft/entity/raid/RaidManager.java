package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class RaidManager extends PersistentState {
	private final Map<Integer, Raid> raids = Maps.<Integer, Raid>newHashMap();
	private final ServerWorld world;
	private int nextAvailableId;
	private int currentTime;

	public RaidManager(ServerWorld serverWorld) {
		super(nameFor(serverWorld.dimension));
		this.world = serverWorld;
		this.nextAvailableId = 1;
		this.markDirty();
	}

	public Raid getRaid(int i) {
		return (Raid)this.raids.get(i);
	}

	public void tick() {
		this.currentTime++;
		Iterator<Raid> iterator = this.raids.values().iterator();

		while (iterator.hasNext()) {
			Raid raid = (Raid)iterator.next();
			if (raid.hasStopped()) {
				iterator.remove();
				this.markDirty();
			} else {
				raid.tick();
			}
		}

		if (this.currentTime % 200 == 0) {
			this.markDirty();
		}
	}

	public static boolean isValidRaiderFor(RaiderEntity raiderEntity, Raid raid) {
		return raiderEntity != null && raid != null && raid.getWorld() != null
			? raiderEntity.isAlive()
				&& raiderEntity.canJoinRaid()
				&& raiderEntity.getDespawnCounter() <= 2400
				&& raiderEntity.world.getDimension().getType() == raid.getWorld().getDimension().getType()
			: false;
	}

	public static boolean isLivingAroundVillage(LivingEntity livingEntity, BlockPos blockPos, int i) {
		return blockPos.getSquaredDistance(new BlockPos(livingEntity.x, livingEntity.y, livingEntity.z)) < (double)(i * i + 24);
	}

	@Nullable
	public Raid startRaid(ServerPlayerEntity serverPlayerEntity) {
		if (serverPlayerEntity.isSpectator()) {
			return null;
		} else {
			DimensionType dimensionType = serverPlayerEntity.world.getDimension().getType();
			if (dimensionType == DimensionType.field_13076) {
				return null;
			} else {
				BlockPos blockPos = new BlockPos(serverPlayerEntity);
				Optional<BlockPos> optional = this.world
					.getPointOfInterestStorage()
					.getNearestPosition(
						pointOfInterestType -> pointOfInterestType == PointOfInterestType.field_18518,
						Objects::nonNull,
						blockPos,
						15,
						PointOfInterestStorage.OccupationStatus.ANY
					);
				if (!optional.isPresent()) {
					optional = Optional.of(blockPos);
				}

				Raid raid = this.getOrCreateRaid(serverPlayerEntity.getServerWorld(), (BlockPos)optional.get());
				boolean bl = false;
				if (!raid.hasStarted()) {
					if (!this.raids.containsKey(raid.getRaidId())) {
						this.raids.put(raid.getRaidId(), raid);
					}

					bl = true;
				} else if (raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
					bl = true;
				} else {
					serverPlayerEntity.removeStatusEffect(StatusEffects.field_16595);
					serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
				}

				if (bl) {
					raid.start(serverPlayerEntity);
					serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
					if (!raid.hasSpawned()) {
						serverPlayerEntity.incrementStat(Stats.field_19256);
						Criterions.VOLUNTARY_EXILE.handle(serverPlayerEntity);
					}
				}

				this.markDirty();
				return raid;
			}
		}
	}

	private Raid getOrCreateRaid(ServerWorld serverWorld, BlockPos blockPos) {
		Raid raid = serverWorld.getRaidAt(blockPos);
		return raid != null ? raid : new Raid(this.nextId(), serverWorld, blockPos);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.nextAvailableId = compoundTag.getInt("NextAvailableID");
		this.currentTime = compoundTag.getInt("Tick");
		ListTag listTag = compoundTag.getList("Raids", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			Raid raid = new Raid(this.world, compoundTag2);
			this.raids.put(raid.getRaidId(), raid);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putInt("NextAvailableID", this.nextAvailableId);
		compoundTag.putInt("Tick", this.currentTime);
		ListTag listTag = new ListTag();

		for (Raid raid : this.raids.values()) {
			CompoundTag compoundTag2 = new CompoundTag();
			raid.toTag(compoundTag2);
			listTag.add(compoundTag2);
		}

		compoundTag.put("Raids", listTag);
		return compoundTag;
	}

	public static String nameFor(Dimension dimension) {
		return "raids" + dimension.getType().getSuffix();
	}

	private int nextId() {
		return ++this.nextAvailableId;
	}

	@Nullable
	public Raid getRaidAt(BlockPos blockPos) {
		Raid raid = null;
		double d = 2.147483647E9;

		for (Raid raid2 : this.raids.values()) {
			double e = raid2.getCenter().getSquaredDistance(blockPos);
			if (raid2.isActive() && e < d) {
				raid = raid2;
				d = e;
			}
		}

		return raid;
	}
}
