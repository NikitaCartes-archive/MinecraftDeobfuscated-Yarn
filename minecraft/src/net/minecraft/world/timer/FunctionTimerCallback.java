package net.minecraft.world.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class FunctionTimerCallback implements TimerCallback<MinecraftServer> {
	final Identifier name;

	public FunctionTimerCallback(Identifier name) {
		this.name = name;
	}

	public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		commandFunctionManager.getFunction(this.name)
			.ifPresent(function -> commandFunctionManager.execute(function, commandFunctionManager.getScheduledCommandSource()));
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback> {
		public Serializer() {
			super(new Identifier("function"), FunctionTimerCallback.class);
		}

		public void serialize(NbtCompound nbtCompound, FunctionTimerCallback functionTimerCallback) {
			nbtCompound.putString("Name", functionTimerCallback.name.toString());
		}

		public FunctionTimerCallback deserialize(NbtCompound nbtCompound) {
			Identifier identifier = new Identifier(nbtCompound.getString("Name"));
			return new FunctionTimerCallback(identifier);
		}
	}
}
