package net.minecraft.sortme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.PersistedState;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

public class RaidState extends PersistedState {
	private Map<Integer, Raid> raids = new HashMap();
	private Map<PlayerEntity, Integer> field_16640 = new HashMap();
	private World field_16641;
	private int nextAvailableId;
	private int tick;

	public RaidState(String string) {
		super(string);
		this.nextAvailableId = 1;
	}

	public RaidState(World world) {
		super(method_16533(world.dimension));
		this.field_16641 = world;
		this.nextAvailableId = 1;
		this.markDirty();
	}

	public Raid getRaid(int i) {
		return (Raid)this.raids.get(i);
	}

	public void method_16530(World world) {
		this.field_16641 = world;

		for (Raid raid : this.raids.values()) {
			raid.method_16489(world);
		}
	}

	public void tick() {
		this.tick++;
		Iterator<Entry<Integer, Raid>> iterator = this.raids.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Integer, Raid> entry = (Entry<Integer, Raid>)iterator.next();
			if (((Raid)entry.getValue()).getVillageProperties() != null && !((Raid)entry.getValue()).method_16503()) {
				((Raid)entry.getValue()).method_16509();
			} else {
				VillageProperties villageProperties = ((Raid)entry.getValue()).getVillageProperties();
				if (villageProperties != null) {
					((Raid)entry.getValue()).method_16512(null);
				}

				iterator.remove();
				this.markDirty();
			}
		}

		if (this.tick % 200 == 0) {
			this.markDirty();
		}
	}

	public void method_16531(VillageProperties villageProperties) {
		Iterator<Entry<PlayerEntity, Integer>> iterator = this.field_16640.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<PlayerEntity, Integer> entry = (Entry<PlayerEntity, Integer>)iterator.next();
			PlayerEntity playerEntity = (PlayerEntity)entry.getKey();
			int i = (Integer)entry.getValue();
			if (!this.method_16839() && method_16537(villageProperties, playerEntity)) {
				this.method_16540(villageProperties, playerEntity);
			}

			if (this.tick > i + 120000) {
				iterator.remove();
				this.markDirty();
			}
		}
	}

	public static boolean method_16838(RaiderEntity raiderEntity, Raid raid) {
		return raiderEntity.isValid()
			&& !raiderEntity.invalid
			&& raiderEntity.method_6131() <= 2400
			&& raiderEntity.world.getDimension().getType() == raid.method_16831().getDimension().getType();
	}

	private boolean method_16839() {
		return this.field_16641 != null && this.field_16641.getDifficulty() == Difficulty.PEACEFUL;
	}

	public static boolean method_16537(VillageProperties villageProperties, LivingEntity livingEntity) {
		return villageProperties.method_16470(new BlockPos(livingEntity.x, livingEntity.y, livingEntity.z), 24);
	}

	public void method_16538(PlayerEntity playerEntity) {
		this.field_16640.put(playerEntity, this.tick);
	}

	public void method_16536(PlayerEntity playerEntity) {
		this.field_16640.remove(playerEntity);
	}

	@Nullable
	public Raid method_16540(VillageProperties villageProperties, PlayerEntity playerEntity) {
		Raid raid = this.method_16532(playerEntity.world, villageProperties);
		if (!raid.hasStarted() && this.method_16535(villageProperties, playerEntity)) {
			if (!this.raids.containsKey(raid.getRaidId())) {
				this.raids.put(raid.getRaidId(), raid);
			}

			raid.method_16518(playerEntity);
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new EntityStatusClientPacket(playerEntity, (byte)43));
		} else if (raid.getBadOmenLevel() < raid.method_16514()) {
			raid.method_16518(playerEntity);
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new EntityStatusClientPacket(playerEntity, (byte)43));
		}

		this.method_16536(playerEntity);
		this.markDirty();
		return raid;
	}

	private boolean method_16535(VillageProperties villageProperties, PlayerEntity playerEntity) {
		return !playerEntity.isSpectator() && !villageProperties.hasNoDoors();
	}

	@Nullable
	public Raid method_16532(World world, VillageProperties villageProperties) {
		Raid raid = this.getRaid(villageProperties.method_16467());
		return raid != null ? raid : new Raid(this.nextId(), world, villageProperties);
	}

	@Override
	public void deserialize(CompoundTag compoundTag) {
		this.nextAvailableId = compoundTag.getInt("NextAvailableID");
		this.tick = compoundTag.getInt("Tick");
		ListTag listTag = compoundTag.getList("Raids", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			Raid raid = new Raid();
			raid.deserialize(compoundTag2);
			this.raids.put(raid.getRaidId(), raid);
		}
	}

	@Override
	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putInt("NextAvailableID", this.nextAvailableId);
		compoundTag.putInt("Tick", this.tick);
		ListTag listTag = new ListTag();

		for (Raid raid : this.raids.values()) {
			CompoundTag compoundTag2 = new CompoundTag();
			raid.serialize(compoundTag2);
			listTag.add((Tag)compoundTag2);
		}

		compoundTag.put("Raids", listTag);
		return compoundTag;
	}

	public static String method_16533(Dimension dimension) {
		return "raids" + dimension.getType().getSuffix();
	}

	private int nextId() {
		return ++this.nextAvailableId;
	}
}
