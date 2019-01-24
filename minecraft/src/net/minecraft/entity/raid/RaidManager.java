package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.EntityStatusClientPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.Difficulty;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;

public class RaidManager extends PersistentState {
	private final Map<Integer, Raid> raids = Maps.<Integer, Raid>newHashMap();
	private final Map<PlayerEntity, Integer> playerTimestamp = Maps.<PlayerEntity, Integer>newHashMap();
	private final World world;
	private int nextAvailableId;
	private int currentTime;

	public RaidManager(World world) {
		super(nameFor(world.dimension));
		this.world = world;
		this.nextAvailableId = 1;
		this.markDirty();
	}

	public Raid getRaid(int i) {
		return (Raid)this.raids.get(i);
	}

	public void tick() {
		this.currentTime++;
		Iterator<Entry<Integer, Raid>> iterator = this.raids.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Integer, Raid> entry = (Entry<Integer, Raid>)iterator.next();
			if (((Raid)entry.getValue()).getVillage() != null && !((Raid)entry.getValue()).isMarkedForRemoval()) {
				((Raid)entry.getValue()).tick();
			} else {
				VillageProperties villageProperties = ((Raid)entry.getValue()).getVillage();
				if (villageProperties != null) {
					((Raid)entry.getValue()).setVillage(null);
				}

				iterator.remove();
				this.markDirty();
			}
		}

		if (this.currentTime % 200 == 0) {
			this.markDirty();
		}
	}

	public void checkRaid(VillageProperties villageProperties) {
		Iterator<Entry<PlayerEntity, Integer>> iterator = this.playerTimestamp.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<PlayerEntity, Integer> entry = (Entry<PlayerEntity, Integer>)iterator.next();
			PlayerEntity playerEntity = (PlayerEntity)entry.getKey();
			int i = (Integer)entry.getValue();
			if (!this.isPeaceful() && isLivingAroundVillage(villageProperties, playerEntity)) {
				this.startRaid(villageProperties, playerEntity);
			}

			if (this.currentTime > i + 120000) {
				iterator.remove();
				this.markDirty();
			}
		}
	}

	public static boolean isValidRaiderFor(RaiderEntity raiderEntity, Raid raid) {
		return raiderEntity != null && raid != null && raid.getWorld() != null
			? raiderEntity.isValid()
				&& raiderEntity.getDespawnCounter() <= 2400
				&& raiderEntity.world.getDimension().getType() == raid.getWorld().getDimension().getType()
			: false;
	}

	private boolean isPeaceful() {
		return this.world.getDifficulty() == Difficulty.PEACEFUL;
	}

	public static boolean isLivingAroundVillage(VillageProperties villageProperties, LivingEntity livingEntity) {
		return villageProperties.isInRaidDistance(new BlockPos(livingEntity.x, livingEntity.y, livingEntity.z), 24);
	}

	public void addTimestamp(PlayerEntity playerEntity) {
		this.playerTimestamp.put(playerEntity, this.currentTime);
	}

	public void removeTimestamp(PlayerEntity playerEntity) {
		this.playerTimestamp.remove(playerEntity);
	}

	@Nullable
	public Raid startRaid(VillageProperties villageProperties, PlayerEntity playerEntity) {
		Raid raid = this.getOrCreateRaid(playerEntity.world, villageProperties);
		if (!raid.hasStarted() && this.canStartRaid(villageProperties, playerEntity)) {
			if (!this.raids.containsKey(raid.getRaidId())) {
				this.raids.put(raid.getRaidId(), raid);
			}

			raid.start(playerEntity);
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new EntityStatusClientPacket(playerEntity, (byte)43));
		} else if (raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
			raid.start(playerEntity);
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new EntityStatusClientPacket(playerEntity, (byte)43));
		}

		this.removeTimestamp(playerEntity);
		this.markDirty();
		return raid;
	}

	private boolean canStartRaid(VillageProperties villageProperties, PlayerEntity playerEntity) {
		return !playerEntity.isSpectator() && !villageProperties.hasNoDoors();
	}

	@Nullable
	public Raid getOrCreateRaid(World world, VillageProperties villageProperties) {
		Raid raid = this.getRaid(villageProperties.getRaidId());
		return raid != null ? raid : new Raid(this.nextId(), world, villageProperties);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.nextAvailableId = compoundTag.getInt("NextAvailableID");
		this.currentTime = compoundTag.getInt("Tick");
		ListTag listTag = compoundTag.getList("Raids", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			Raid raid = new Raid();
			raid.fromTag(compoundTag2);
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
}
