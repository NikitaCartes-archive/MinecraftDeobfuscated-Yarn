package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public abstract class AbstractClientPlayerEntity extends PlayerEntity {
	@Nullable
	private PlayerListEntry playerListEntry;
	protected Vec3d lastVelocity = Vec3d.ZERO;
	public float elytraPitch;
	public float elytraYaw;
	public float elytraRoll;
	public final ClientWorld clientWorld;
	public float lastDistanceMoved;
	public float distanceMoved;

	public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
		this.clientWorld = world;
	}

	@Override
	public boolean isSpectator() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
	}

	@Override
	public boolean isCreative() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry != null && playerListEntry.getGameMode() == GameMode.CREATIVE;
	}

	@Nullable
	protected PlayerListEntry getPlayerListEntry() {
		if (this.playerListEntry == null) {
			this.playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(this.getUuid());
		}

		return this.playerListEntry;
	}

	@Override
	public void tick() {
		this.lastDistanceMoved = this.distanceMoved;
		this.lastVelocity = this.getVelocity();
		super.tick();
	}

	public Vec3d lerpVelocity(float tickDelta) {
		return this.lastVelocity.lerp(this.getVelocity(), (double)tickDelta);
	}

	public SkinTextures getSkinTextures() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getSkinTextures(this.getUuid()) : playerListEntry.getSkinTextures();
	}

	public float getFovMultiplier(boolean firstPerson, float fovEffectScale) {
		float f = 1.0F;
		if (this.getAbilities().flying) {
			f *= 1.1F;
		}

		float g = this.getAbilities().getWalkSpeed();
		if (g != 0.0F) {
			float h = (float)this.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) / g;
			f *= (h + 1.0F) / 2.0F;
		}

		if (this.isUsingItem()) {
			if (this.getActiveItem().isOf(Items.BOW)) {
				float h = Math.min((float)this.getItemUseTime() / 20.0F, 1.0F);
				f *= 1.0F - MathHelper.square(h) * 0.15F;
			} else if (firstPerson && this.isUsingSpyglass()) {
				return 0.1F;
			}
		}

		return MathHelper.lerp(fovEffectScale, 1.0F, f);
	}
}
