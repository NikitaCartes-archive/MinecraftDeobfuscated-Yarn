package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public class FunctionTimerCallback implements TimerCallback<MinecraftServer> {
	private final Identifier name;

	public FunctionTimerCallback(Identifier identifier) {
		this.name = identifier;
	}

	public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		commandFunctionManager.getFunction(this.name)
			.ifPresent(commandFunction -> commandFunctionManager.execute(commandFunction, commandFunctionManager.getTaggedFunctionSource()));
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback> {
		public Serializer() {
			super(new Identifier("function"), FunctionTimerCallback.class);
		}

		public void serialize(CompoundTag compoundTag, FunctionTimerCallback functionTimerCallback) {
			compoundTag.putString("Name", functionTimerCallback.name.toString());
		}

		public FunctionTimerCallback deserialize(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new FunctionTimerCallback(identifier);
		}
	}
}
