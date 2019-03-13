package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.EntityStatusS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.Dimension;

public class RaidManager extends PersistentState {
	private final Map<Integer, Raid> raids = Maps.<Integer, Raid>newHashMap();
	private final ServerWorld field_16641;
	private int nextAvailableId;
	private int currentTime;

	public RaidManager(ServerWorld serverWorld) {
		super(method_16533(serverWorld.field_9247));
		this.field_16641 = serverWorld;
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
			if (raid.isMarkedForRemoval()) {
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
		return raiderEntity != null && raid != null && raid.method_16831() != null
			? raiderEntity.isValid()
				&& raiderEntity.getDespawnCounter() <= 2400
				&& raiderEntity.field_6002.method_8597().method_12460() == raid.method_16831().method_8597().method_12460()
			: false;
	}

	public static boolean method_16537(LivingEntity livingEntity, BlockPos blockPos, int i) {
		return blockPos.squaredDistanceTo(new BlockPos(livingEntity.x, livingEntity.y, livingEntity.z)) < (double)(i * i + 24);
	}

	@Nullable
	public Raid method_16540(ServerPlayerEntity serverPlayerEntity) {
		Raid raid = this.method_16532(serverPlayerEntity.getServerWorld(), new BlockPos(serverPlayerEntity));
		if (!raid.hasStarted() && !serverPlayerEntity.isSpectator()) {
			if (!this.raids.containsKey(raid.getRaidId())) {
				this.raids.put(raid.getRaidId(), raid);
			}

			raid.start(serverPlayerEntity);
			serverPlayerEntity.field_13987.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
		} else if (raid.getBadOmenLevel() < raid.getMaxAcceptableBadOmenLevel()) {
			raid.start(serverPlayerEntity);
			serverPlayerEntity.field_13987.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)43));
		}

		this.markDirty();
		return raid;
	}

	@Nullable
	public Raid method_16532(ServerWorld serverWorld, BlockPos blockPos) {
		Raid raid = serverWorld.method_19502(blockPos);
		return raid != null ? raid : new Raid(this.nextId(), serverWorld, blockPos);
	}

	@Override
	public void method_77(CompoundTag compoundTag) {
		this.nextAvailableId = compoundTag.getInt("NextAvailableID");
		this.currentTime = compoundTag.getInt("Tick");
		ListTag listTag = compoundTag.method_10554("Raids", 10);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag2 = listTag.getCompoundTag(i);
			Raid raid = new Raid(this.field_16641, compoundTag2);
			this.raids.put(raid.getRaidId(), raid);
		}
	}

	@Override
	public CompoundTag method_75(CompoundTag compoundTag) {
		compoundTag.putInt("NextAvailableID", this.nextAvailableId);
		compoundTag.putInt("Tick", this.currentTime);
		ListTag listTag = new ListTag();

		for (Raid raid : this.raids.values()) {
			CompoundTag compoundTag2 = new CompoundTag();
			raid.method_16502(compoundTag2);
			listTag.add(compoundTag2);
		}

		compoundTag.method_10566("Raids", listTag);
		return compoundTag;
	}

	public static String method_16533(Dimension dimension) {
		return "raids" + dimension.method_12460().getSuffix();
	}

	private int nextId() {
		return ++this.nextAvailableId;
	}

	@Nullable
	public Raid method_19209(BlockPos blockPos) {
		Raid raid = null;
		double d = 2.147483647E9;

		for (Raid raid2 : this.raids.values()) {
			double e = raid2.method_16495().squaredDistanceTo(blockPos);
			if (raid2.isOnGoing() && e < d) {
				raid = raid2;
				d = e;
			}
		}

		return raid;
	}
}
