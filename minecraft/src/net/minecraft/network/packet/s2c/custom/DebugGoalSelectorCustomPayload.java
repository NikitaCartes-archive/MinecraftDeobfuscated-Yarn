package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugGoalSelectorCustomPayload(int entityId, BlockPos pos, List<DebugGoalSelectorCustomPayload.Goal> goals) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/goal_selector");

	public DebugGoalSelectorCustomPayload(PacketByteBuf buf) {
		this(buf.readInt(), buf.readBlockPos(), buf.readList(DebugGoalSelectorCustomPayload.Goal::new));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeBlockPos(this.pos);
		buf.writeCollection(this.goals, (bufx, goal) -> goal.write(bufx));
	}

	@Override
	public Identifier id() {
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