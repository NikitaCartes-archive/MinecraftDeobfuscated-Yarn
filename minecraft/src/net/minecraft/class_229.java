package net.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class class_229 implements class_234<MinecraftServer> {
	private final Identifier field_1303;

	public class_229(Identifier identifier) {
		this.field_1303 = identifier;
	}

	public void method_962(MinecraftServer minecraftServer, class_236<MinecraftServer> arg, long l) {
		CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
		Tag<CommandFunction> tag = commandFunctionManager.getTags().getOrCreate(this.field_1303);

		for (CommandFunction commandFunction : tag.values()) {
			commandFunctionManager.execute(commandFunction, commandFunctionManager.getFunctionCommandSource());
		}
	}

	public static class class_230 extends class_234.class_235<MinecraftServer, class_229> {
		public class_230() {
			super(new Identifier("function_tag"), class_229.class);
		}

		public void method_964(CompoundTag compoundTag, class_229 arg) {
			compoundTag.putString("Name", arg.field_1303.toString());
		}

		public class_229 method_965(CompoundTag compoundTag) {
			Identifier identifier = new Identifier(compoundTag.getString("Name"));
			return new class_229(identifier);
		}
	}
}
