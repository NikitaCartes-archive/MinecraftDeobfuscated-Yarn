package net.minecraft.village.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class RaidManager extends PersistentState {
	private final Map<Integer, Raid> raids = Maps.<Integer, Raid>newHashMap();
	private final ServerWorld world;
	private int nextAvailableId;
	private int currentTime;

	public RaidManager(ServerWorld world) {
		this.world = world;
		this.nextAvailableId = 1;
		this.markDirty();
	}

	public Raid getRaid(int id) {
		return (Raid)this.raids.get(id);
	}

	public void tick() {
		this.currentTime++;
		Iterator<Raid> iterator = this.raids.values().iterator();

		while (iterator.hasNext()) {
			Raid raid = (Raid)iterator.next();
			if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
				raid.invalidate();
			}

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

		DebugInfoSender.sendRaids(this.world, this.raids.values());
	}

	public static boolean isValidRaiderFor(RaiderEntity raider, Raid raid) {
		return raider != null && raid != null && raid.getWorld() != null
			? raider.isAlive() && raider.canJoinRaid() && raider.getDespawnCounter() <= 2400 && raider.world.getDimension() == raid.getWorld().getDimension()
			: false;
	}

	@Nullable
	public Raid startRaid(ServerPlayerEntity player) {
		if (player.isSpectator()) {
			return null;
		} else if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
			return null;
		} else {
			DimensionType dimensionType = player.world.getDimension();
			if (!dimensionType.hasRaids()) {
				return null;
			} else {
				BlockPos blockPos = player.getBlockPos();
				List<PointOfInterest> list = (List<PointOfInterest>)this.world
					.getPointOfInterestStorage()
					.getInCircle(PointOfInterestType.ALWAYS_TRUE, blockPos, 64, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED)
					.collect(Collectors.toList());
				int i = 0;
				Vec3d vec3d = Vec3d.ZERO;

				for (PointOfInterest pointOfInterest : list) {
					BlockPos blockPos2 = pointOfInterest.getPos();
					vec3d = vec3d.add((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
					i++;
				}

				BlockPos blockPos3;
				if (i > 0) {
					vec3d = vec3d.multiply(1.0 / (double)i);
					blockPos3 = new BlockPos(vec3d);
				} else {
					blockPos3 = blockPos;
				}

				Raid raid = this.getOrCreateRaid(player.getServerWorld(), blockPos3);
				boolean bl = false;
				if (!raid.hasStarted()) {
					if (!this.raids.containsKey(raid.getRaidId())) {
						this.raids.put(raid.getRaidId(), raid);
					}

					bl = true;
				} else if (raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
					bl = true;
				} else {
					player.removeStatusEffect(StatusEffects.BAD_OMEN);
					player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, (byte)43));
				}

				if (bl) {
					raid.start(player);
					player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, (byte)43));
					if (!raid.hasSpawned()) {
						player.incrementStat(Stats.RAID_TRIGGER);
						Criteria.VOLUNTARY_EXILE.trigger(player);
					}
				}

				this.markDirty();
				return raid;
			}
		}
	}

	private Raid getOrCreateRaid(ServerWorld world, BlockPos pos) {
		Raid raid = world.getRaidAt(pos);
		return raid != null ? raid : new Raid(this.nextId(), world, pos);
	}

	public static RaidManager fromNbt(ServerWorld world, NbtCompound nbt) {
		RaidManager raidManager = new RaidManager(world);
		raidManager.nextAvailableId = nbt.getInt("NextAvailableID");
		raidManager.currentTime = nbt.getInt("Tick");
		NbtList nbtList = nbt.getList("Raids", NbtTypeIds.COMPOUND);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Raid raid = new Raid(world, nbtCompound);
			raidManager.raids.put(raid.getRaidId(), raid);
		}

		return raidManager;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag.putInt("NextAvailableID", this.nextAvailableId);
		tag.putInt("Tick", this.currentTime);
		NbtList nbtList = new NbtList();

		for (Raid raid : this.raids.values()) {
			NbtCompound nbtCompound = new NbtCompound();
			raid.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}

		tag.put("Raids", nbtList);
		return tag;
	}

	public static String nameFor(DimensionType dimensionType) {
		return "raids" + dimensionType.getSuffix();
	}

	private int nextId() {
		return ++this.nextAvailableId;
	}

	@Nullable
	public Raid getRaidAt(BlockPos pos, int searchDistance) {
		Raid raid = null;
		double d = (double)searchDistance;

		for (Raid raid2 : this.raids.values()) {
			double e = raid2.getCenter().getSquaredDistance(pos);
			if (raid2.isActive() && e < d) {
				raid = raid2;
				d = e;
			}
		}

		return raid;
	}
}
