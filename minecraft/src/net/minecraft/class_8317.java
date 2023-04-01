package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8317 extends class_8277 {
	private final Codec<class_8317.class_8318> field_43801 = class_8345.field_43918
		.optionalFieldOf("player")
		.codec()
		.xmap(optional -> new class_8317.class_8318(optional), arg -> arg.field_43803);

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43801);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		boolean bl = minecraftServer.getPlayerManager()
			.getPlayerList()
			.stream()
			.anyMatch(serverPlayerEntity -> serverPlayerEntity.getTransformedLook().playerSkin() != null);
		ObjectArrayList<Optional<ServerPlayerEntity>> objectArrayList = (ObjectArrayList<Optional<ServerPlayerEntity>>)minecraftServer.getPlayerManager()
			.getPlayerList()
			.stream()
			.map(Optional::of)
			.collect(Collectors.toCollection(ObjectArrayList::new));
		if (bl) {
			objectArrayList.add(Optional.empty());
		}

		Util.shuffle(objectArrayList, random);
		return objectArrayList.stream().limit((long)i).map(optional -> new class_8317.class_8318(optional.map(class_8345::method_50412)));
	}

	protected class class_8318 extends class_8277.class_8278 {
		final Optional<class_8345> field_43803;
		private final Text field_43804;

		protected class_8318(Optional<class_8345> optional) {
			this.field_43803 = optional;
			this.field_43804 = (Text)optional.map(argx -> Text.translatable("rule.copy_skin", argx.displayName())).orElse(Text.translatable("rule.reset_skin"));
		}

		@Override
		protected Text method_50166() {
			return this.field_43804;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			if (this.field_43803.isPresent()) {
				SkullBlockEntity.loadProperties(((class_8345)this.field_43803.get()).method_50411(), gameProfile -> {
					for (ServerPlayerEntity serverPlayerEntityx : minecraftServer.getPlayerManager().getPlayerList()) {
						serverPlayerEntityx.editTransformation(transformationType -> transformationType.withSkin(Optional.of(gameProfile)));
					}
				});
			} else {
				for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
					serverPlayerEntity.editTransformation(transformationType -> transformationType.withSkin(Optional.empty()));
				}
			}
		}
	}
}
