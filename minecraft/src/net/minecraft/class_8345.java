package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

public record class_8345(UUID id, String name, Text displayName) {
	public static final Codec<class_8345> field_43918 = RecordCodecBuilder.create(
		instance -> instance.group(
					Uuids.STRING_CODEC.fieldOf("uuid").forGetter(class_8345::id),
					Codec.STRING.fieldOf("name").forGetter(class_8345::name),
					Codecs.TEXT.fieldOf("display_name").forGetter(class_8345::displayName)
				)
				.apply(instance, class_8345::new)
	);

	public static class_8345 method_50412(ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		return new class_8345(gameProfile.getId(), gameProfile.getName(), serverPlayerEntity.getDisplayName());
	}

	public GameProfile method_50411() {
		return new GameProfile(this.id, this.name);
	}
}
