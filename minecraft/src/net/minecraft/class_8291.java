package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public interface class_8291 {
	Codec<class_8291> field_43504 = Registries.field_44443.getCodec().dispatch(class_8291::method_50121, class_8289::method_50120);

	class_8289 method_50121();

	default void method_50164(class_8290 arg, MinecraftServer minecraftServer) {
		this.method_50122(arg);
		minecraftServer.getPlayerManager().sendToAll(new class_8479(false, arg, List.of(this)));
	}

	void method_50122(class_8290 arg);

	Text method_50130(class_8290 arg);

	public interface class_8292 extends class_8291 {
		Text method_50123();

		@Override
		default Text method_50130(class_8290 arg) {
			return (Text)(switch (arg) {
				case REPEAL -> Text.translatable("action.repeal", this.method_50123());
				case APPROVE -> this.method_50123();
			});
		}
	}
}
