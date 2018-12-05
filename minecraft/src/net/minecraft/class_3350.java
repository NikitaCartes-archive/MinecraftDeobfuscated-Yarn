package net.minecraft;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class class_3350 implements CommandOutput {
	private final StringBuffer field_14404 = new StringBuffer();
	private final MinecraftServer field_14405;

	public class_3350(MinecraftServer minecraftServer) {
		this.field_14405 = minecraftServer;
	}

	public void method_14702() {
		this.field_14404.setLength(0);
	}

	public String method_14701() {
		return this.field_14404.toString();
	}

	public ServerCommandSource method_14700() {
		ServerWorld serverWorld = this.field_14405.getWorld(DimensionType.field_13072);
		return new ServerCommandSource(
			this, new Vec3d(serverWorld.method_8395()), Vec2f.ZERO, serverWorld, 4, "Recon", new StringTextComponent("Rcon"), this.field_14405, null
		);
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
		this.field_14404.append(textComponent.getString());
	}

	@Override
	public boolean sendCommandFeedback() {
		return true;
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return this.field_14405.shouldBroadcastRconToOps();
	}
}
