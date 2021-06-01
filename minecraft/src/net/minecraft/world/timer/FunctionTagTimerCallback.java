package net.minecraft.world.timer;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FunctionTagTimerCallback implements TimerCallback<MinecraftServer> {
	final Identifier name;

	public FunctionTagTimerCallback(Identifier name) {
		this.name = name;
	}

	public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		Tag<CommandFunction> tag = commandFunctionManager.getTag(this.name);

		for (CommandFunction commandFunction : tag.values()) {
			commandFunctionManager.execute(commandFunction, commandFunctionManager.getScheduledCommandSource());
		}
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
		public Serializer() {
			super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
		}

		public void serialize(NbtCompound nbtCompound, FunctionTagTimerCallback functionTagTimerCallback) {
			nbtCompound.putString("Name", functionTagTimerCallback.name.toString());
		}

		public FunctionTagTimerCallback deserialize(NbtCompound nbtCompound) {
			Identifier identifier = new Identifier(nbtCompound.getString("Name"));
			return new FunctionTagTimerCallback(identifier);
		}
	}
}
