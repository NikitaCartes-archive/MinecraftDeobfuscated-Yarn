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
import net.minecraft.item.ItemStack;
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
		this.lastVelocity = this.getVelocity();
		super.tick();
	}

	public Vec3d lerpVelocity(float tickDelta) {
		return this.lastVelocity.lerp(this.getVelocity(), (double)tickDelta);
	}

	public SkinTextures getSkinTextures() {
		PlayerListEntry playerListEntry = this.getPlayerListEntry();
		return playerListEntry == null ? DefaultSkinHelper.getTexture(this.getUuid()) : playerListEntry.getSkinTextures();
	}

	public float getFovMultiplier() {
		float f = 1.0F;
		if (this.getAbilities().flying) {
			f *= 1.1F;
		}

		f *= ((float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) / this.getAbilities().getWalkSpeed() + 1.0F) / 2.0F;
		if (this.getAbilities().getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		ItemStack itemStack = this.getActiveItem();
		if (this.isUsingItem()) {
			if (itemStack.isOf(Items.BOW)) {
				int i = this.getItemUseTime();
				float g = (float)i / 20.0F;
				if (g > 1.0F) {
					g = 1.0F;
				} else {
					g *= g;
				}

				f *= 1.0F - g * 0.15F;
			} else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && this.isUsingSpyglass()) {
				return 0.1F;
			}
		}

		return MathHelper.lerp(MinecraftClient.getInstance().options.getFovEffectScale().getValue().floatValue(), 1.0F, f);
	}
}
