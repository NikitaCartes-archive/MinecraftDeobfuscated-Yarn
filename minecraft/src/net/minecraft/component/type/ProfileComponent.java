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

public record ProfileComponent(Optional<String> name, Optional<UUID> id, PropertyMap properties, GameProfile gameProfile) {
	private static final Codec<ProfileComponent> BASE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ProfileComponent::name),
					Uuids.INT_STREAM_CODEC.optionalFieldOf("id").forGetter(ProfileComponent::id),
					Codecs.GAME_PROFILE_PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(ProfileComponent::properties)
				)
				.apply(instance, ProfileComponent::new)
	);
	public static final Codec<ProfileComponent> CODEC = Codec.withAlternative(
		BASE_CODEC, Codecs.PLAYER_NAME, name -> new ProfileComponent(Optional.of(name), Optional.empty(), new PropertyMap())
	);
	public static final PacketCodec<ByteBuf, ProfileComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.string(16).collect(PacketCodecs::optional),
		ProfileComponent::name,
		Uuids.PACKET_CODEC.collect(PacketCodecs::optional),
		ProfileComponent::id,
		PacketCodecs.PROPERTY_MAP,
		ProfileComponent::properties,
		ProfileComponent::new
	);

	public ProfileComponent(Optional<String> name, Optional<UUID> id, PropertyMap properties) {
		this(name, id, properties, createProfile(name, id, properties));
	}

	public ProfileComponent(GameProfile gameProfile) {
		this(Optional.of(gameProfile.getName()), Optional.of(gameProfile.getId()), gameProfile.getProperties(), gameProfile);
	}

	public CompletableFuture<ProfileComponent> getFuture() {
		if (this.isCompleted()) {
			return CompletableFuture.completedFuture(this);
		} else {
			return this.id.isPresent() ? SkullBlockEntity.fetchProfileByUuid((UUID)this.id.get()).thenApply(optional -> {
				GameProfile gameProfile = (GameProfile)optional.orElseGet(() -> new GameProfile((UUID)this.id.get(), (String)this.name.orElse("")));
				return new ProfileComponent(gameProfile);
			}) : SkullBlockEntity.fetchProfileByName((String)this.name.orElseThrow()).thenApply(profile -> {
				GameProfile gameProfile = (GameProfile)profile.orElseGet(() -> new GameProfile(Util.NIL_UUID, (String)this.name.get()));
				return new ProfileComponent(gameProfile);
			});
		}
	}

	private static GameProfile createProfile(Optional<String> name, Optional<UUID> id, PropertyMap properties) {
		GameProfile gameProfile = new GameProfile((UUID)id.orElse(Util.NIL_UUID), (String)name.orElse(""));
		gameProfile.getProperties().putAll(properties);
		return gameProfile;
	}

	public boolean isCompleted() {
		return !this.properties.isEmpty() ? true : this.id.isPresent() == this.name.isPresent();
	}
}
