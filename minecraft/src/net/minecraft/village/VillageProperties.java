package net.minecraft.village;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class VillageProperties {
	private final World world;
	private final List<VillageDoor> doors = Lists.<VillageDoor>newArrayList();
	private BlockPos doorPositionsAggregate = BlockPos.ORIGIN;
	private BlockPos center = BlockPos.ORIGIN;
	private int radius;
	private int stable;
	private int tick;
	private int populationSize;
	private int lastVillagerDeath;
	private int raidId = 0;
	private final Map<String, Integer> playerRatings = Maps.<String, Integer>newHashMap();
	private final List<VillageProperties.AttackerInfo> attackerInfos = Lists.<VillageProperties.AttackerInfo>newArrayList();
	private int golems;

	public VillageProperties(World world) {
		this.world = world;
	}

	public void update(int i) {
		this.tick = i;
		this.removeInvalidDoors();
		this.clearOutdatedAttackerInfo();
		if (i % 20 == 0) {
			this.countVillagers();
		}

		if (i % 30 == 0) {
			this.countGolems();
		}

		int j = this.populationSize / 10;
		if (this.golems < j && this.doors.size() > 20 && this.world.random.nextInt(7000) == 0) {
			Entity entity = this.spawnIronGolemAround(this.center);
			if (entity != null) {
				this.golems++;
			}
		}
	}

	@Nullable
	private Entity spawnIronGolemAround(BlockPos blockPos) {
		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(this.world.random.nextInt(16) - 8, this.world.random.nextInt(6) - 3, this.world.random.nextInt(16) - 8);
			if (this.isInRadius(blockPos2)) {
				EntityType<IronGolemEntity> entityType = EntityType.IRON_GOLEM;
				if (!this.world.method_18026(entityType.createSimpleBoundingBox((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5))) {
					return null;
				}

				IronGolemEntity ironGolemEntity = entityType.create(this.world, null, null, null, blockPos2, SpawnType.field_16474, false, false);
				if (ironGolemEntity != null) {
					if (ironGolemEntity.canSpawn(this.world, SpawnType.field_16474) && ironGolemEntity.method_5957(this.world)) {
						this.world.spawnEntity(ironGolemEntity);
						return ironGolemEntity;
					}

					ironGolemEntity.invalidate();
				}
			}
		}

		return null;
	}

	private void countGolems() {
		List<IronGolemEntity> list = this.world
			.method_18467(
				IronGolemEntity.class,
				new BoundingBox(
					(double)(this.center.getX() - this.radius),
					(double)(this.center.getY() - 4),
					(double)(this.center.getZ() - this.radius),
					(double)(this.center.getX() + this.radius),
					(double)(this.center.getY() + 4),
					(double)(this.center.getZ() + this.radius)
				)
			);
		this.golems = list.size();
	}

	private void countVillagers() {
		List<VillagerEntity> list = this.world
			.method_18467(
				VillagerEntity.class,
				new BoundingBox(
					(double)(this.center.getX() - this.radius),
					(double)(this.center.getY() - 4),
					(double)(this.center.getZ() - this.radius),
					(double)(this.center.getX() + this.radius),
					(double)(this.center.getY() + 4),
					(double)(this.center.getZ() + this.radius)
				)
			);
		this.populationSize = list.size();
		if (this.populationSize == 0) {
			this.playerRatings.clear();
		}
	}

	public BlockPos getCenter() {
		return this.center;
	}

	public int getRadius() {
		return this.radius;
	}

	public int getDoorCount() {
		return this.doors.size();
	}

	public int getStableTicks() {
		return this.tick - this.stable;
	}

	public int getPopulationSize() {
		return this.populationSize;
	}

	public boolean isInRadius(BlockPos blockPos) {
		return this.center.squaredDistanceTo(blockPos) < (double)(this.radius * this.radius);
	}

	public boolean isInRaidDistance(BlockPos blockPos, int i) {
		return this.center.squaredDistanceTo(blockPos) < (double)(this.radius * this.radius + i);
	}

	public List<VillageDoor> getDoors() {
		return this.doors;
	}

	public VillageDoor getClosestDoor(BlockPos blockPos) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : this.doors) {
			int j = villageDoor2.squaredDistanceTo(blockPos);
			if (j < i) {
				villageDoor = villageDoor2;
				i = j;
			}
		}

		return villageDoor;
	}

	public VillageDoor getNearestDoor(BlockPos blockPos) {
		VillageDoor villageDoor = null;
		int i = Integer.MAX_VALUE;

		for (VillageDoor villageDoor2 : this.doors) {
			int j = villageDoor2.squaredDistanceTo(blockPos);
			if (j > 256) {
				j *= 1000;
			} else {
				j = villageDoor2.getEntityInsideTicks();
			}

			if (j < i) {
				BlockPos blockPos2 = villageDoor2.getPosition();
				Direction direction = villageDoor2.getFacing();
				if (this.world.getBlockState(blockPos2.offset(direction, 1)).canPlaceAtSide(this.world, blockPos2.offset(direction, 1), BlockPlacementEnvironment.field_50)
					&& this.world
						.getBlockState(blockPos2.offset(direction, -1))
						.canPlaceAtSide(this.world, blockPos2.offset(direction, -1), BlockPlacementEnvironment.field_50)
					&& this.world
						.getBlockState(blockPos2.up().offset(direction, 1))
						.canPlaceAtSide(this.world, blockPos2.up().offset(direction, 1), BlockPlacementEnvironment.field_50)
					&& this.world
						.getBlockState(blockPos2.up().offset(direction, -1))
						.canPlaceAtSide(this.world, blockPos2.up().offset(direction, -1), BlockPlacementEnvironment.field_50)) {
					villageDoor = villageDoor2;
					i = j;
				}
			}
		}

		return villageDoor;
	}

	@Nullable
	public VillageDoor getDoorAtPosition(BlockPos blockPos) {
		if (this.center.squaredDistanceTo(blockPos) > (double)(this.radius * this.radius)) {
			return null;
		} else {
			for (VillageDoor villageDoor : this.doors) {
				if (villageDoor.getPosition().getX() == blockPos.getX()
					&& villageDoor.getPosition().getZ() == blockPos.getZ()
					&& Math.abs(villageDoor.getPosition().getY() - blockPos.getY()) <= 1) {
					return villageDoor;
				}
			}

			return null;
		}
	}

	public void addDoor(VillageDoor villageDoor) {
		this.doors.add(villageDoor);
		this.doorPositionsAggregate = this.doorPositionsAggregate.add(villageDoor.getPosition());
		this.recalculateSize();
		this.stable = villageDoor.getLastTimeSeenByVillager();
	}

	public boolean hasNoDoors() {
		return this.doors.isEmpty();
	}

	public void addAttacker(LivingEntity livingEntity) {
		for (VillageProperties.AttackerInfo attackerInfo : this.attackerInfos) {
			if (attackerInfo.attacker == livingEntity) {
				attackerInfo.tick = this.tick;
				return;
			}
		}

		this.attackerInfos.add(new VillageProperties.AttackerInfo(livingEntity, this.tick));
	}

	@Nullable
	public LivingEntity getNearestAttacker(LivingEntity livingEntity) {
		double d = Double.MAX_VALUE;
		VillageProperties.AttackerInfo attackerInfo = null;

		for (int i = 0; i < this.attackerInfos.size(); i++) {
			VillageProperties.AttackerInfo attackerInfo2 = (VillageProperties.AttackerInfo)this.attackerInfos.get(i);
			double e = attackerInfo2.attacker.squaredDistanceTo(livingEntity);
			if (!(e > d)) {
				attackerInfo = attackerInfo2;
				d = e;
			}
		}

		return attackerInfo == null ? null : attackerInfo.attacker;
	}

	public PlayerEntity getNearestUnpopularPlayer(LivingEntity livingEntity) {
		double d = Double.MAX_VALUE;
		PlayerEntity playerEntity = null;

		for (String string : this.playerRatings.keySet()) {
			if (this.isUnpopular(string)) {
				PlayerEntity playerEntity2 = this.world.method_18469(string);
				if (playerEntity2 != null) {
					double e = playerEntity2.squaredDistanceTo(livingEntity);
					if (!(e > d)) {
						playerEntity = playerEntity2;
						d = e;
					}
				}
			}
		}

		return playerEntity;
	}

	private void clearOutdatedAttackerInfo() {
		Iterator<VillageProperties.AttackerInfo> iterator = this.attackerInfos.iterator();

		while (iterator.hasNext()) {
			VillageProperties.AttackerInfo attackerInfo = (VillageProperties.AttackerInfo)iterator.next();
			if (!attackerInfo.attacker.isValid() || Math.abs(this.tick - attackerInfo.tick) > 300) {
				iterator.remove();
			}
		}
	}

	private void removeInvalidDoors() {
		boolean bl = false;
		boolean bl2 = this.world.random.nextInt(50) == 0;
		Iterator<VillageDoor> iterator = this.doors.iterator();

		while (iterator.hasNext()) {
			VillageDoor villageDoor = (VillageDoor)iterator.next();
			if (bl2) {
				villageDoor.clearEntityInsideTicks();
			}

			if (!this.isValidDoor(villageDoor.getPosition()) || Math.abs(this.tick - villageDoor.getLastTimeSeenByVillager()) > 1200) {
				this.doorPositionsAggregate = this.doorPositionsAggregate.subtract(villageDoor.getPosition());
				bl = true;
				villageDoor.setInvalid(true);
				iterator.remove();
			}
		}

		if (bl) {
			this.recalculateSize();
		}
	}

	private boolean isValidDoor(BlockPos blockPos) {
		if (!this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			return true;
		} else {
			BlockState blockState = this.world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			return block instanceof DoorBlock ? blockState.getMaterial() == Material.WOOD : false;
		}
	}

	private void recalculateSize() {
		int i = this.doors.size();
		if (i == 0) {
			this.center = BlockPos.ORIGIN;
			this.radius = 0;
		} else {
			this.center = new BlockPos(this.doorPositionsAggregate.getX() / i, this.doorPositionsAggregate.getY() / i, this.doorPositionsAggregate.getZ() / i);
			int j = 0;

			for (VillageDoor villageDoor : this.doors) {
				j = Math.max(villageDoor.squaredDistanceTo(this.center), j);
			}

			this.radius = Math.max(32, (int)Math.sqrt((double)j) + 1);
		}
	}

	public int getRating(String string) {
		Integer integer = (Integer)this.playerRatings.get(string);
		return integer == null ? 0 : integer;
	}

	public int changeRating(String string, int i) {
		int j = this.getRating(string);
		int k = MathHelper.clamp(j + i, -30, 10);
		this.playerRatings.put(string, k);
		return k;
	}

	public boolean isUnpopular(String string) {
		return this.getRating(string) <= -15;
	}

	public void fromTag(CompoundTag compoundTag) {
		this.populationSize = compoundTag.getInt("PopSize");
		this.radius = compoundTag.getInt("Radius");
		this.golems = compoundTag.getInt("Golems");
		this.stable = compoundTag.getInt("Stable");
		this.tick = compoundTag.getInt("Tick");
		this.lastVillagerDeath = compoundTag.getInt("MTick");
		this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
		this.doorPositionsAggregate = new BlockPos(compoundTag.getInt("ACX"), compoundTag.getInt("ACY"), compoundTag.getInt("ACZ"));
		ListTag listTag = compoundTag.getList("Doors", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			VillageDoor villageDoor = new VillageDoor(
				new BlockPos(compoundTag2.getInt("X"), compoundTag2.getInt("Y"), compoundTag2.getInt("Z")),
				compoundTag2.getInt("IDX"),
				compoundTag2.getInt("IDZ"),
				compoundTag2.getInt("TS")
			);
			this.doors.add(villageDoor);
		}

		ListTag listTag2 = compoundTag.getList("Players", 10);

		for (int j = 0; j < listTag2.size(); j++) {
			CompoundTag compoundTag3 = listTag2.getCompoundTag(j);
			if (compoundTag3.containsKey("UUID") && this.world != null && this.world.getServer() != null) {
				UserCache userCache = this.world.getServer().getUserCache();
				GameProfile gameProfile = userCache.getByUuid(UUID.fromString(compoundTag3.getString("UUID")));
				if (gameProfile != null) {
					this.playerRatings.put(gameProfile.getName(), compoundTag3.getInt("S"));
				}
			} else {
				this.playerRatings.put(compoundTag3.getString("Name"), compoundTag3.getInt("S"));
			}
		}

		this.raidId = compoundTag.getInt("RaidId");
	}

	public void toTag(CompoundTag compoundTag) {
		compoundTag.putInt("PopSize", this.populationSize);
		compoundTag.putInt("Radius", this.radius);
		compoundTag.putInt("Golems", this.golems);
		compoundTag.putInt("Stable", this.stable);
		compoundTag.putInt("Tick", this.tick);
		compoundTag.putInt("MTick", this.lastVillagerDeath);
		compoundTag.putInt("CX", this.center.getX());
		compoundTag.putInt("CY", this.center.getY());
		compoundTag.putInt("CZ", this.center.getZ());
		compoundTag.putInt("ACX", this.doorPositionsAggregate.getX());
		compoundTag.putInt("ACY", this.doorPositionsAggregate.getY());
		compoundTag.putInt("ACZ", this.doorPositionsAggregate.getZ());
		ListTag listTag = new ListTag();

		for (VillageDoor villageDoor : this.doors) {
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag2.putInt("X", villageDoor.getPosition().getX());
			compoundTag2.putInt("Y", villageDoor.getPosition().getY());
			compoundTag2.putInt("Z", villageDoor.getPosition().getZ());
			compoundTag2.putInt("IDX", villageDoor.insideDirectionX());
			compoundTag2.putInt("IDZ", villageDoor.insideDirectionY());
			compoundTag2.putInt("TS", villageDoor.getLastTimeSeenByVillager());
			listTag.add(compoundTag2);
		}

		compoundTag.put("Doors", listTag);
		ListTag listTag2 = new ListTag();

		for (String string : this.playerRatings.keySet()) {
			CompoundTag compoundTag3 = new CompoundTag();
			UserCache userCache = this.world.getServer().getUserCache();

			try {
				GameProfile gameProfile = userCache.findByName(string);
				if (gameProfile != null) {
					compoundTag3.putString("UUID", gameProfile.getId().toString());
					compoundTag3.putInt("S", (Integer)this.playerRatings.get(string));
					listTag2.add(compoundTag3);
				}
			} catch (RuntimeException var9) {
			}
		}

		compoundTag.put("Players", listTag2);
		compoundTag.putInt("RaidId", this.raidId);
	}

	public void onVillagerDeath() {
		this.lastVillagerDeath = this.tick;
	}

	public boolean hasRecentDeath() {
		return this.lastVillagerDeath == 0 || this.tick - this.lastVillagerDeath >= 3600;
	}

	public void changeAllRatings(int i) {
		for (String string : this.playerRatings.keySet()) {
			this.changeRating(string, i);
		}
	}

	public int getRaidId() {
		return this.raidId;
	}

	public void setRaidId(int i) {
		this.raidId = i;
		this.world.getVillageManager().markDirty();
	}

	@Nullable
	public Raid getRaid() {
		return this.world != null && this.world.getRaidManager() != null ? this.world.getRaidManager().getRaid(this.raidId) : null;
	}

	class AttackerInfo {
		public final LivingEntity attacker;
		public int tick;

		AttackerInfo(LivingEntity livingEntity, int i) {
			this.attacker = livingEntity;
			this.tick = i;
		}
	}
}
