package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class class_8354 extends class_8277 {
	static final MutableText field_43936 = Text.translatable("rule.the_joke");
	private final class_8277.class_8278 field_43937 = new class_8277.class_8278() {
		@Override
		protected Text method_50166() {
			return class_8354.field_43936;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			minecraftServer.getPlayerManager().sendToAll(new WorldEventS2CPacket(1506, BlockPos.ORIGIN, 0, true));
		}
	};

	@Override
	public Codec<class_8291> method_50120() {
		return Codec.unit(this.field_43937);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return i > 0 ? Stream.of(this.field_43937) : Stream.empty();
	}
}
