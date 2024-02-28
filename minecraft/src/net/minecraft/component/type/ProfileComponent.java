package net.minecraft.component.type;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

public record ProfileComponent(String name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
	public static final Codec<ProfileComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.PLAYER_NAME.fieldOf("name").forGetter(ProfileComponent::name),
					Codecs.createStrictOptionalFieldCodec(Uuids.INT_STREAM_CODEC, "id").forGetter(ProfileComponent::id),
					Codecs.createStrictOptionalFieldCodec(Codecs.GAME_PROFILE_PROPERTY_MAP, "properties", new PropertyMap()).forGetter(ProfileComponent::properties)
				)
				.apply(instance, ProfileComponent::new)
	);
	public static final PacketCodec<ByteBuf, ProfileComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.string(16),
		ProfileComponent::name,
		Uuids.PACKET_CODEC.collect(PacketCodecs::optional),
		ProfileComponent::id,
		PacketCodecs.PROPERTY_MAP,
		ProfileComponent::properties,
		ProfileComponent::new
	);

	public ProfileComponent(String name, Optional<UUID> id, PropertyMap properties) {
		this(name, id, properties, createProfile(name, id, properties));
	}

	public ProfileComponent(GameProfile gameProfile) {
		this(gameProfile.getName(), Optional.ofNullable(gameProfile.getId()), gameProfile.getProperties(), gameProfile);
	}

	public CompletableFuture<ProfileComponent> getFuture() {
		return this.isCompleted() ? CompletableFuture.completedFuture(this) : SkullBlockEntity.fetchProfile(this.name).thenApply(profile -> {
			GameProfile gameProfile = (GameProfile)profile.orElseGet(() -> new GameProfile(Util.NIL_UUID, this.name));
			return new ProfileComponent(gameProfile);
		});
	}

	private static GameProfile createProfile(String name, Optional<UUID> id, PropertyMap properties) {
		GameProfile gameProfile = new GameProfile((UUID)id.orElse(Util.NIL_UUID), name);
		gameProfile.getProperties().putAll(properties);
		return gameProfile;
	}

	public boolean isCompleted() {
		return this.id.isPresent() || !this.properties.isEmpty();
	}
}
