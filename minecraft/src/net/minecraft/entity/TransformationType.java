package net.minecraft.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record TransformationType(Optional<TransformationType.EntityData> entity, float scale, Optional<GameProfile> playerSkin) {
	public static final TransformationType EMPTY = new TransformationType(Optional.empty(), 1.0F, Optional.empty());
	public static final float field_44126 = 0.1F;
	public static final float field_44127 = 16.0F;
	public static final Codec<TransformationType> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					TransformationType.EntityData.CODEC.optionalFieldOf("entity").forGetter(TransformationType::entity),
					Codecs.rangedFloat(0.09999F, 16.0F).optionalFieldOf("scale", 1.0F).forGetter(TransformationType::scale),
					Codecs.GAME_PROFILE.optionalFieldOf("player_skin").forGetter(TransformationType::playerSkin)
				)
				.apply(instance, TransformationType::new)
	);

	public TransformationType(Optional<TransformationType.EntityData> entity, float scale, Optional<GameProfile> playerSkin) {
		scale = MathHelper.clamp(scale, 0.1F, 16.0F);
		this.entity = entity;
		this.scale = scale;
		this.playerSkin = playerSkin;
	}

	public void toPacket(PacketByteBuf buf) {
		buf.encode(NbtOps.INSTANCE, CODEC, this);
	}

	public static TransformationType fromPacket(PacketByteBuf buf) {
		return buf.decode(NbtOps.INSTANCE, CODEC);
	}

	public Transformation createTransformation(LivingEntity entity) {
		Entity entity2 = (Entity)this.entity.map(entityData -> entityData.createEntity(entity)).orElse(null);
		return new Transformation(this, entity2, (GameProfile)this.playerSkin.orElse(null));
	}

	public TransformationType withEntity(EntityType<?> entityType, Optional<NbtCompound> nbt) {
		return this.withEntity(Optional.of(new TransformationType.EntityData(entityType, nbt)));
	}

	public TransformationType withEntity(Optional<TransformationType.EntityData> entity) {
		return new TransformationType(entity, this.scale, this.playerSkin);
	}

	public TransformationType withScale(float scale) {
		return new TransformationType(this.entity, scale, this.playerSkin);
	}

	public TransformationType withSkin(Optional<GameProfile> skin) {
		return new TransformationType(this.entity, this.scale, skin);
	}

	public boolean isEmpty() {
		return this.entity.isEmpty() && Math.abs(this.scale() - 1.0F) < 1.0E-5F && this.playerSkin.isEmpty();
	}

	public static record EntityData(EntityType<?> type, Optional<NbtCompound> tag) {
		public static final Codec<TransformationType.EntityData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter(TransformationType.EntityData::type),
						NbtCompound.CODEC.optionalFieldOf("tag").forGetter(TransformationType.EntityData::tag)
					)
					.apply(instance, TransformationType.EntityData::new)
		);

		@Nullable
		public Entity createEntity(LivingEntity entity) {
			Entity entity2 = this.type.create(entity.world);
			if (entity2 == null) {
				return null;
			} else {
				this.tag.ifPresent(entity2::readNbt);
				return entity2;
			}
		}
	}
}
