package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class BrainDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient field_18767;
	private final Map<String, BrainDebugRenderer.class_4204> field_18768 = Maps.<String, BrainDebugRenderer.class_4204>newHashMap();
	private String field_18769 = null;

	public void method_19423(BrainDebugRenderer.class_4204 arg) {
		this.field_18768.put(arg.field_18770, arg);
	}

	public BrainDebugRenderer(MinecraftClient minecraftClient) {
		this.field_18767 = minecraftClient;
	}

	@Override
	public void render(long l) {
		this.method_19421();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();

		for (BrainDebugRenderer.class_4204 lv : this.field_18768.values()) {
			if (this.method_19428(lv)) {
				this.method_19426(lv);
			}
		}

		GlStateManager.enableDepthTest();
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	private void method_19426(BrainDebugRenderer.class_4204 arg) {
		int i = 0;
		this.method_19424(arg.field_18772, i, arg.field_18771, -1, 0.03F);
		i++;

		for (String string : arg.field_18773) {
			int j = -16711936;
			this.method_19424(arg.field_18772, i, string, -16711936, 0.02F);
			i++;
		}

		if (this.method_19427(arg)) {
			for (String string : arg.field_18774) {
				int j = -3355444;
				this.method_19424(arg.field_18772, i, string, -3355444, 0.02F);
				i++;
			}
		}
	}

	private void method_19421() {
		Entity entity = this.getTargetedEntity();
		if (entity != null) {
			this.field_18769 = entity.getUuid().toString();
		}
	}

	private boolean method_19427(BrainDebugRenderer.class_4204 arg) {
		return Objects.equals(this.field_18769, arg.field_18770);
	}

	@Nullable
	private Entity getTargetedEntity() {
		Entity entity = this.field_18767.getCameraEntity();
		if (entity == null) {
			return null;
		} else {
			this.field_18767.getProfiler().push("BrainDebugRenderer.getTargetedEntity");

			Object var13;
			try {
				int i = 160;
				float f = 1.0F;
				this.field_18767.hitResult = entity.rayTrace(160.0, 1.0F, false);
				Vec3d vec3d = entity.getCameraPosVec(1.0F);
				Vec3d vec3d2 = entity.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * 160.0, vec3d2.y * 160.0, vec3d2.z * 160.0);
				float g = 1.0F;
				BoundingBox boundingBox = entity.getBoundingBox().method_18804(vec3d2.multiply(160.0)).expand(1.0, 1.0, 1.0);
				int j = 25600;
				EntityHitResult entityHitResult = class_1675.method_18075(
					entity, vec3d, vec3d3, boundingBox, entityx -> !entityx.isSpectator() && entityx.doesCollide(), 25600.0
				);
				if (entityHitResult == null) {
					return null;
				}

				Entity entity2 = entityHitResult.getEntity();
				Vec3d vec3d4 = entityHitResult.getPos();
				if (!(vec3d.squaredDistanceTo(vec3d4) > 25600.0)) {
					return entity2;
				}

				var13 = null;
			} finally {
				this.field_18767.getProfiler().pop();
			}

			return (Entity)var13;
		}
	}

	private boolean method_19428(BrainDebugRenderer.class_4204 arg) {
		PlayerEntity playerEntity = this.field_18767.player;
		BlockPos blockPos = new BlockPos(playerEntity.x, 0.0, playerEntity.z);
		BlockPos blockPos2 = new BlockPos(arg.field_18772);
		return blockPos.distanceTo(blockPos2) <= 160.0;
	}

	private void method_19424(Position position, int i, String string, int j, float f) {
		double d = 2.4;
		double e = 0.25;
		float g = 0.5F;
		BlockPos blockPos = new BlockPos(position);
		double h = (double)blockPos.getX() + 0.5;
		double k = position.getY() + 2.4 + (double)i * 0.25;
		double l = (double)blockPos.getZ() + 0.5;
		DebugRenderer.method_3712(string, h, k, l, j, f, false, 0.5F, true);
	}

	@Environment(EnvType.CLIENT)
	public static class class_4204 {
		public final String field_18770;
		public final String field_18771;
		public final Position field_18772;
		public final List<String> field_18773 = Lists.<String>newArrayList();
		public final List<String> field_18774 = Lists.<String>newArrayList();

		public class_4204(String string, String string2, Position position) {
			this.field_18770 = string;
			this.field_18771 = string2;
			this.field_18772 = position;
		}
	}
}
