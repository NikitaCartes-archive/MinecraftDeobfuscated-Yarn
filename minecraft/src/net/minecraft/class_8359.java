package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class class_8359 extends class_8277.class_8279 {
	private static final float field_43951 = 0.5F;
	private final Codec<class_8359.class_8360> field_43952 = Codec.FLOAT.xmap(f -> new class_8359.class_8360(f), arg -> arg.field_43954);

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43952);
	}

	@Override
	protected Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random) {
		return Optional.of(new class_8359.class_8360(random.nextBoolean() ? 0.5F : -0.5F));
	}

	@Override
	protected Optional<class_8291> method_50167(MinecraftServer minecraftServer, Random random) {
		boolean bl = minecraftServer.getPlayerManager()
			.getPlayerList()
			.stream()
			.anyMatch(serverPlayerEntity -> serverPlayerEntity.getTransformedLook().getScale() != 1.0F);
		return bl ? Optional.of(new class_8359.class_8360(0.0F)) : Optional.empty();
	}

	protected class class_8360 extends class_8277.class_8278 {
		final float field_43954;
		private final Text field_43955;

		protected class_8360(float f) {
			this.field_43954 = f;
			if (f == 0.0F) {
				this.field_43955 = Text.translatable("rule.reset_scale");
			} else {
				this.field_43955 = Text.translatable(f > 0.0F ? "rule.increase_scale" : "rule.decrease_scale", String.format("%.1f", Math.abs(f)));
			}
		}

		@Override
		protected Text method_50166() {
			return this.field_43955;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				if (this.field_43954 == 0.0F) {
					serverPlayerEntity.editTransformation(transformationType -> transformationType.withScale(1.0F));
				} else {
					serverPlayerEntity.editTransformation(transformationType -> transformationType.withScale(transformationType.scale() + this.field_43954));
				}
			}
		}
	}
}
