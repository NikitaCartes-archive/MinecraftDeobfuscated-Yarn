package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGoalSelectorCustomPayload(int entityId, BlockPos pos, List<DebugGoalSelectorCustomPayload.Goal> goals) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugGoalSelectorCustomPayload> CODEC = CustomPayload.codecOf(
		DebugGoalSelectorCustomPayload::write, DebugGoalSelectorCustomPayload::new
	);
	public static final CustomPayload.Id<DebugGoalSelectorCustomPayload> ID = CustomPayload.id("debug/goal_selector");

	private DebugGoalSelectorCustomPayload(PacketByteBuf buf) {
		this(buf.readInt(), buf.readBlockPos(), buf.readList(DebugGoalSelectorCustomPayload.Goal::new));
	}

	private void write(PacketByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeCollection(this.goals, (bufx, goal) -> goal.write(bufx));
	}

	@Override
	public CustomPayload.Id<DebugGoalSelectorCustomPayload> getId() {
		return ID;
	}

	public static record Goal(int priority, boolean isRunning, String name) {
		public Goal(PacketByteBuf buf) {
			this(buf.readInt(), buf.readBoolean(), buf.readString(255));
		}

		public void write(PacketByteBuf buf) {
			buf.writeInt(this.priority);
			buf.writeBoolean(this.isRunning);
			buf.writeString(this.name);
		}
	}
}
