package net.minecraft.client.render.entity.state;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderState extends BipedEntityRenderState {
	public SkinTextures skinTextures = DefaultSkinHelper.getSteve();
	public float field_53536;
	public float field_53537;
	public float field_53538;
	public int stuckArrowCount;
	public int stingerCount;
	public int itemUseTimeLeft;
	public boolean spectator;
	public boolean hatVisible = true;
	public boolean jacketVisible = true;
	public boolean leftPantsLegVisible = true;
	public boolean rightPantsLegVisible = true;
	public boolean leftSleeveVisible = true;
	public boolean rightSleeveVisible = true;
	public boolean capeVisible = true;
	public boolean inSwimmingPose;
	public float fallFlyingTicks;
	public boolean applyFlyingRotation;
	public float flyingRotation;
	public boolean handSwinging;
	public PlayerEntityRenderState.HandState mainHandState = new PlayerEntityRenderState.HandState();
	public PlayerEntityRenderState.HandState offHandState = new PlayerEntityRenderState.HandState();
	@Nullable
	public Text playerName;
	@Nullable
	public ParrotEntity.Variant leftShoulderParrotVariant;
	@Nullable
	public ParrotEntity.Variant rightShoulderParrotVariant;
	public int id;
	public String name = "Steve";

	@Environment(EnvType.CLIENT)
	public static class HandState {
		public boolean empty = true;
		@Nullable
		public UseAction itemUseAction;
		public boolean hasChargedCrossbow;
	}
}
