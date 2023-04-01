package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8343 extends class_8277 {
	private final Codec<class_8343.class_8344> field_43915 = Codec.unit(new class_8343.class_8344());

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43915);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return minecraftServer.getPlayerManager().getPlayerList().size() > 1 ? Stream.of(new class_8343.class_8344()) : Stream.empty();
	}

	protected class class_8344 extends class_8277.class_8278 {
		private final Text field_43917 = Text.translatable("rule.parent_trap");

		@Override
		protected Text method_50166() {
			return this.field_43917;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			ObjectArrayList<ServerPlayerEntity> objectArrayList = new ObjectArrayList<>(minecraftServer.getPlayerManager().getPlayerList());
			Util.shuffle(objectArrayList, minecraftServer.getOverworld().random);
			double d = objectArrayList.get(0).getX();
			double e = objectArrayList.get(0).getY();
			double f = objectArrayList.get(0).getZ();
			ServerWorld serverWorld = objectArrayList.get(0).getWorld();
			float g = objectArrayList.get(0).getYaw();
			float h = objectArrayList.get(0).getPitch();

			for (int i = 0; i < objectArrayList.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = objectArrayList.get(i);
				if (i == objectArrayList.size() - 1) {
					serverPlayerEntity.teleport(serverWorld, d, e, f, g, h);
				} else {
					ServerPlayerEntity serverPlayerEntity2 = objectArrayList.get(i + 1);
					serverPlayerEntity.teleport(
						serverPlayerEntity2.getWorld(),
						serverPlayerEntity2.getX(),
						serverPlayerEntity2.getY(),
						serverPlayerEntity2.getZ(),
						serverPlayerEntity2.getYaw(),
						serverPlayerEntity2.getPitch()
					);
				}
			}
		}
	}
}
