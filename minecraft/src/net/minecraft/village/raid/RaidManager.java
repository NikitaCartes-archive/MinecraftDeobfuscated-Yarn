package net.minecraft.village.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

public class RaidManager extends PersistentState {
	private static final String RAIDS = "raids";
	private final Map<Integer, Raid> raids = Maps.<Integer, Raid>newHashMap();
	private final ServerWorld world;
	private int nextAvailableId;
	private int currentTime;

	public static PersistentState.Type<RaidManager> getPersistentStateType(ServerWorld world) {
		return new PersistentState.Type<>(() -> new RaidManager(world), (nbt, registries) -> fromNbt(world, nbt), DataFixTypes.SAVED_DATA_RAIDS);
	}

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
			? raider.isAlive() && raider.canJoinRaid() && raider.getDespawnCounter() <= 2400 && raider.getWorld().getDimension() == raid.getWorld().getDimension()
			: false;
	}

	@Nullable
	public Raid startRaid(ServerPlayerEntity player, BlockPos pos) {
		if (player.isSpectator()) {
			return null;
		} else if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
			return null;
		} else {
			DimensionType dimensionType = player.getWorld().getDimension();
			if (!dimensionType.hasRaids()) {
				return null;
			} else {
				List<PointOfInterest> list = this.world
					.getPointOfInterestStorage()
					.getInCircle(poiType -> poiType.isIn(PointOfInterestTypeTags.VILLAGE), pos, 64, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED)
					.toList();
				int i = 0;
				Vec3d vec3d = Vec3d.ZERO;

				for (PointOfInterest pointOfInterest : list) {
					BlockPos blockPos = pointOfInterest.getPos();
					vec3d = vec3d.add((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
					i++;
				}

				BlockPos blockPos2;
				if (i > 0) {
					vec3d = vec3d.multiply(1.0 / (double)i);
					blockPos2 = BlockPos.ofFloored(vec3d);
				} else {
					blockPos2 = pos;
				}

				Raid raid = this.getOrCreateRaid(player.getServerWorld(), blockPos2);
				if (!raid.hasStarted() && !this.raids.containsKey(raid.getRaidId())) {
					this.raids.put(raid.getRaidId(), raid);
				}

				if (!raid.hasStarted() || raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
					raid.start(player);
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
		NbtList nbtList = nbt.getList("Raids", NbtElement.COMPOUND_TYPE);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Raid raid = new Raid(world, nbtCompound);
			raidManager.raids.put(raid.getRaidId(), raid);
		}

		return raidManager;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		nbt.putInt("NextAvailableID", this.nextAvailableId);
		nbt.putInt("Tick", this.currentTime);
		NbtList nbtList = new NbtList();

		for (Raid raid : this.raids.values()) {
			NbtCompound nbtCompound = new NbtCompound();
			raid.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}

		nbt.put("Raids", nbtList);
		return nbt;
	}

	public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
		return dimensionTypeEntry.matchesKey(DimensionTypes.THE_END) ? "raids_end" : "raids";
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
