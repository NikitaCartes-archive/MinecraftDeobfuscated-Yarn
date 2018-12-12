package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.VillageDoor;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.dimension.Dimension;

public class WorldVillageManager extends PersistentState {
	private World world;
	private final List<BlockPos> recentVillagerPositions = Lists.<BlockPos>newArrayList();
	private final List<VillageDoor> recentlySeenDoors = Lists.<VillageDoor>newArrayList();
	private final List<VillageProperties> villages = Lists.<VillageProperties>newArrayList();
	private int tick;

	public WorldVillageManager(String string) {
		super(string);
	}

	public WorldVillageManager(World world) {
		super(getBaseTag(world.dimension));
		this.world = world;
		this.markDirty();
	}

	public void setWorld(World world) {
		this.world = world;

		for (VillageProperties villageProperties : this.villages) {
			villageProperties.setWorld(world);
		}
	}

	public void addRecentVillagerPosition(BlockPos blockPos) {
		if (this.recentVillagerPositions.size() <= 64) {
			if (!this.isRecentVillagerPosition(blockPos)) {
				this.recentVillagerPositions.add(blockPos);
			}
		}
	}

	public void tick() {
		this.tick++;

		for (VillageProperties villageProperties : this.villages) {
			villageProperties.update(this.tick);
			this.world.getRaidManager().checkRaid(villageProperties);
		}

		this.validateVillageProperties();
		this.updateDoorsSeenByVillagers();
		this.addDoorsToVillages();
		if (this.tick % 400 == 0) {
			this.markDirty();
		}
	}

	private void validateVillageProperties() {
		Iterator<VillageProperties> iterator = this.villages.iterator();

		while (iterator.hasNext()) {
			VillageProperties villageProperties = (VillageProperties)iterator.next();
			if (villageProperties.hasNoDoors()) {
				iterator.remove();
				this.markDirty();
			}
		}
	}

	public List<VillageProperties> getVillages() {
		return this.villages;
	}

	public VillageProperties getNearestVillage(BlockPos blockPos, int i) {
		VillageProperties villageProperties = null;
		double d = Float.MAX_VALUE;

		for (VillageProperties villageProperties2 : this.villages) {
			double e = villageProperties2.getCenter().squaredDistanceTo(blockPos);
			if (!(e >= d)) {
				float f = (float)(i + villageProperties2.getRadius());
				if (!(e > (double)(f * f))) {
					villageProperties = villageProperties2;
					d = e;
				}
			}
		}

		return villageProperties;
	}

	private void updateDoorsSeenByVillagers() {
		if (!this.recentVillagerPositions.isEmpty()) {
			this.updateDoorsSeenByVillager((BlockPos)this.recentVillagerPositions.remove(0));
		}
	}

	private void addDoorsToVillages() {
		for (int i = 0; i < this.recentlySeenDoors.size(); i++) {
			VillageDoor villageDoor = (VillageDoor)this.recentlySeenDoors.get(i);
			VillageProperties villageProperties = this.getNearestVillage(villageDoor.getPosition(), 32);
			if (villageProperties == null) {
				villageProperties = new VillageProperties(this.world);
				this.villages.add(villageProperties);
				this.markDirty();
			}

			villageProperties.addDoor(villageDoor);
		}

		this.recentlySeenDoors.clear();
	}

	private void updateDoorsSeenByVillager(BlockPos blockPos) {
		int i = 16;
		int j = 4;
		int k = 16;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = -16; l < 16; l++) {
			for (int m = -4; m < 4; m++) {
				for (int n = -16; n < 16; n++) {
					mutable.set(blockPos).method_10100(l, m, n);
					BlockState blockState = this.world.getBlockState(mutable);
					if (this.method_6442(blockState)) {
						VillageDoor villageDoor = this.getRecentlySeenDoor(mutable);
						if (villageDoor == null) {
							this.method_6443(blockState, mutable);
						} else {
							villageDoor.setLastTimeSeenByVillager(this.tick);
						}
					}
				}
			}
		}
	}

	@Nullable
	private VillageDoor getRecentlySeenDoor(BlockPos blockPos) {
		for (VillageDoor villageDoor : this.recentlySeenDoors) {
			if (villageDoor.getPosition().getX() == blockPos.getX()
				&& villageDoor.getPosition().getZ() == blockPos.getZ()
				&& Math.abs(villageDoor.getPosition().getY() - blockPos.getY()) <= 1) {
				return villageDoor;
			}
		}

		for (VillageProperties villageProperties : this.villages) {
			VillageDoor villageDoor2 = villageProperties.getDoorAtPosition(blockPos);
			if (villageDoor2 != null) {
				return villageDoor2;
			}
		}

		return null;
	}

	private void method_6443(BlockState blockState, BlockPos blockPos) {
		Direction direction = blockState.get(DoorBlock.field_10938);
		Direction direction2 = direction.getOpposite();
		int i = this.isDoorLeadingOutside(blockPos, direction, 5);
		int j = this.isDoorLeadingOutside(blockPos, direction2, i + 1);
		if (i != j) {
			this.recentlySeenDoors.add(new VillageDoor(blockPos, i < j ? direction : direction2, this.tick));
		}
	}

	private int isDoorLeadingOutside(BlockPos blockPos, Direction direction, int i) {
		int j = 0;

		for (int k = 1; k <= 5; k++) {
			if (this.world.isSkyVisible(blockPos.offset(direction, k))) {
				if (++j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private boolean isRecentVillagerPosition(BlockPos blockPos) {
		for (BlockPos blockPos2 : this.recentVillagerPositions) {
			if (blockPos2.equals(blockPos)) {
				return true;
			}
		}

		return false;
	}

	private boolean method_6442(BlockState blockState) {
		return blockState.getBlock() instanceof DoorBlock && blockState.getMaterial() == Material.WOOD;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.tick = compoundTag.getInt("Tick");
		ListTag listTag = compoundTag.getList("Villages", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			VillageProperties villageProperties = new VillageProperties();
			villageProperties.deserialize(compoundTag2);
			this.villages.add(villageProperties);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		compoundTag.putInt("Tick", this.tick);
		ListTag listTag = new ListTag();

		for (VillageProperties villageProperties : this.villages) {
			CompoundTag compoundTag2 = new CompoundTag();
			villageProperties.serialize(compoundTag2);
			listTag.add((Tag)compoundTag2);
		}

		compoundTag.put("Villages", listTag);
		return compoundTag;
	}

	public void method_16471() {
		for (VillageProperties villageProperties : this.villages) {
			int i = villageProperties.getRaidId();
			if (i > 0) {
				Raid raid = this.world.getRaidManager().getRaid(i);
				if (raid != null) {
					raid.setVillage(villageProperties);
				} else {
					villageProperties.setRaidId(0);
				}
			}
		}
	}

	public static String getBaseTag(Dimension dimension) {
		return "villages" + dimension.getType().getSuffix();
	}
}
