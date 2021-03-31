package net.minecraft.server.rcon;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RconCommandOutput implements CommandOutput {
	private static final String field_29793 = "Rcon";
	private static final Text RCON_NAME = new LiteralText("Rcon");
	private final StringBuffer buffer = new StringBuffer();
	private final MinecraftServer server;

	public RconCommandOutput(MinecraftServer server) {
		this.server = server;
	}

	public void clear() {
		this.buffer.setLength(0);
	}

	public String asString() {
		return this.buffer.toString();
	}

	public ServerCommandSource createRconCommandSource() {
		ServerWorld serverWorld = this.server.getOverworld();
		return new ServerCommandSource(this, Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "Rcon", RCON_NAME, this.server, null);
	}

	@Override
	public void sendSystemMessage(Text message, UUID senderUuid) {
		this.buffer.append(message.getString());
	}

	@Override
	public boolean shouldReceiveFeedback() {
		return true;
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.server.shouldBroadcastRconToOps();
	}
}
