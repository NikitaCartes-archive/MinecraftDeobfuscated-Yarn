package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShoulderParrotEntityRenderer implements LayerEntityRenderer<PlayerEntity> {
	private final EntityRenderDispatcher renderManager;
	protected LivingEntityRenderer<? extends LivingEntity> field_4863;
	private Model field_4864;
	private Identifier field_4858;
	private UUID field_4859;
	private EntityType<?> field_4861;
	protected LivingEntityRenderer<? extends LivingEntity> field_4860;
	private Model field_4865;
	private Identifier field_4867;
	private UUID field_4868;
	private EntityType<?> field_4862;

	public ShoulderParrotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this.renderManager = entityRenderDispatcher;
	}

	public void render(PlayerEntity playerEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (playerEntity.getShoulderEntityLeft() != null || playerEntity.getShoulderEntityRight() != null) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			CompoundTag compoundTag = playerEntity.getShoulderEntityLeft();
			if (!compoundTag.isEmpty()) {
				ShoulderParrotEntityRenderer.ParrotData parrotData = this.renderShoulderParrot(
					playerEntity, this.field_4859, compoundTag, this.field_4863, this.field_4864, this.field_4858, this.field_4861, f, g, h, i, j, k, l, true
				);
				this.field_4859 = parrotData.uuid;
				this.field_4863 = parrotData.renderer;
				this.field_4858 = parrotData.variant;
				this.field_4864 = parrotData.model;
				this.field_4861 = parrotData.field_4874;
			}

			CompoundTag compoundTag2 = playerEntity.getShoulderEntityRight();
			if (!compoundTag2.isEmpty()) {
				ShoulderParrotEntityRenderer.ParrotData parrotData2 = this.renderShoulderParrot(
					playerEntity, this.field_4868, compoundTag2, this.field_4860, this.field_4865, this.field_4867, this.field_4862, f, g, h, i, j, k, l, false
				);
				this.field_4868 = parrotData2.uuid;
				this.field_4860 = parrotData2.renderer;
				this.field_4867 = parrotData2.variant;
				this.field_4865 = parrotData2.model;
				this.field_4862 = parrotData2.field_4874;
			}

			GlStateManager.disableRescaleNormal();
		}
	}

	private ShoulderParrotEntityRenderer.ParrotData renderShoulderParrot(
		PlayerEntity playerEntity,
		@Nullable UUID uUID,
		CompoundTag compoundTag,
		LivingEntityRenderer<? extends LivingEntity> livingEntityRenderer,
		Model model,
		Identifier identifier,
		EntityType<?> entityType,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l,
		boolean bl
	) {
		if (uUID == null || !uUID.equals(compoundTag.getUuid("UUID"))) {
			uUID = compoundTag.getUuid("UUID");
			entityType = EntityType.get(compoundTag.getString("id"));
			if (entityType == EntityType.PARROT) {
				livingEntityRenderer = new ParrotEntityRenderer(this.renderManager);
				model = new ParrotEntityModel();
				identifier = ParrotEntityRenderer.TEXTURES[compoundTag.getInt("Variant")];
			}
		}

		livingEntityRenderer.bindTexture(identifier);
		GlStateManager.pushMatrix();
		float m = playerEntity.isSneaking() ? -1.3F : -1.5F;
		float n = bl ? 0.4F : -0.4F;
		GlStateManager.translatef(n, m, 0.0F);
		if (entityType == EntityType.PARROT) {
			i = 0.0F;
		}

		model.animateModel(playerEntity, f, g, h);
		model.setRotationAngles(f, g, i, j, k, l, playerEntity);
		model.render(playerEntity, f, g, i, j, k, l);
		GlStateManager.popMatrix();
		return new ShoulderParrotEntityRenderer.ParrotData(uUID, livingEntityRenderer, model, identifier, entityType);
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	class ParrotData {
		public UUID uuid;
		public LivingEntityRenderer<? extends LivingEntity> renderer;
		public Model model;
		public Identifier variant;
		public EntityType<?> field_4874;

		public ParrotData(UUID uUID, LivingEntityRenderer<? extends LivingEntity> livingEntityRenderer, Model model, Identifier identifier, EntityType<?> entityType) {
			this.uuid = uUID;
			this.renderer = livingEntityRenderer;
			this.model = model;
			this.variant = identifier;
			this.field_4874 = entityType;
		}
	}
}
