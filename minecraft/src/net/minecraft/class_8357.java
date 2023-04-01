package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8357 extends class_8277.class_8279 {
	private final Codec<class_8357.class_8358> field_43947 = RecordCodecBuilder.create(
		instance -> instance.group(Registries.ENTITY_TYPE.getCodec().optionalFieldOf("entity").forGetter(arg -> arg.field_43949))
				.apply(instance, optional -> new class_8357.class_8358(optional))
	);

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43947);
	}

	@Override
	protected Optional<class_8291> method_50167(MinecraftServer minecraftServer, Random random) {
		boolean bl = minecraftServer.getPlayerManager()
			.getPlayerList()
			.stream()
			.anyMatch(serverPlayerEntity -> serverPlayerEntity.getTransformedLook().entity() != null);
		return bl ? Optional.of(new class_8357.class_8358(Optional.empty())) : Optional.empty();
	}

	@Override
	protected Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random) {
		List<EntityType<?>> list = minecraftServer.getRegistryManager()
			.get(RegistryKeys.ENTITY_TYPE)
			.stream()
			.filter(entityType -> entityType.getSpawnGroup() != SpawnGroup.MISC)
			.toList();
		return Util.getRandomOrEmpty(list, random).map(entityType -> new class_8357.class_8358(Optional.of(entityType)));
	}

	protected class class_8358 extends class_8277.class_8278 {
		final Optional<EntityType<?>> field_43949;
		private final Text field_43950;

		protected class_8358(Optional<EntityType<?>> optional) {
			this.field_43949 = optional;
			this.field_43950 = (Text)optional.map(entityType -> Text.translatable("rule.transform_entity", entityType.getName()))
				.orElse(Text.translatable("rule.reset_entity_transform"));
		}

		@Override
		protected Text method_50166() {
			return this.field_43950;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
				if (this.field_43949.isPresent()) {
					serverPlayerEntity.editTransformation(transformationType -> transformationType.withEntity((EntityType<?>)this.field_43949.get(), Optional.empty()));
				} else {
					serverPlayerEntity.editTransformation(transformationType -> transformationType.withEntity(Optional.empty()));
				}
			}
		}
	}
}
