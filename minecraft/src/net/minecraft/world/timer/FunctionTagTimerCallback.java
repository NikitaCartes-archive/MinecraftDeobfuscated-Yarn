package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FunctionTagTimerCallback implements TimerCallback<MinecraftServer> {
	private final Identifier name;

	public FunctionTagTimerCallback(Identifier identifier) {
		this.name = identifier;
	}

	public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> timer, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		Tag<CommandFunction> tag = commandFunctionManager.getTags().getOrCreate(this.name);

		for (CommandFunction commandFunction : tag.values()) {
			commandFunctionManager.execute(commandFunction, commandFunctionManager.getTaggedFunctionSource());
		}
	}

	public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagTimerCallback> {
		public Serializer() {
			super(new Identifier("function_tag"), FunctionTagTimerCallback.class);
		}

		public void serialize(CompoundTag compoundTag, FunctionTagTimerCallback functionTagTimerCallback) {
			compoundTag.putString("Name", functionTagTimerCallback.name.toString());
		}

		public FunctionTagTimerCallback deserialize(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new FunctionTagTimerCallback(identifier);
		}
	}
}
