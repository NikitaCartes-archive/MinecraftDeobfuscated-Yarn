package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
	private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers = Maps.<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>>newHashMap();
	public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
	private TextRenderer fontRenderer;
	public static double renderOffsetX;
	public static double renderOffsetY;
	public static double renderOffsetZ;
	public TextureManager field_4347;
	public World world;
	public class_4184 cameraEntity;
	public HitResult hitResult;

	private BlockEntityRenderDispatcher() {
		this.renderers.put(SignBlockEntity.class, new SignBlockEntityRenderer());
		this.renderers.put(MobSpawnerBlockEntity.class, new MobSpawnerBlockEntityRenderer());
		this.renderers.put(PistonBlockEntity.class, new PistonBlockEntityRenderer());
		this.renderers.put(ChestBlockEntity.class, new ChestBlockEntityRenderer());
		this.renderers.put(EnderChestBlockEntity.class, new ChestBlockEntityRenderer());
		this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableBlockEntityRenderer());
		this.renderers.put(LecternBlockEntity.class, new LecternBlockEntityRenderer());
		this.renderers.put(EndPortalBlockEntity.class, new EndPortalBlockEntityRenderer());
		this.renderers.put(EndGatewayBlockEntity.class, new EndGatewayBlockEntityRenderer());
		this.renderers.put(BeaconBlockEntity.class, new BeaconBlockEntityRenderer());
		this.renderers.put(SkullBlockEntity.class, new SkullBlockEntityRenderer());
		this.renderers.put(BannerBlockEntity.class, new BannerBlockEntityRenderer());
		this.renderers.put(StructureBlockBlockEntity.class, new StructureBlockBlockEntityRenderer());
		this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel()));
		this.renderers.put(BedBlockEntity.class, new BedBlockEntityRenderer());
		this.renderers.put(ConduitBlockEntity.class, new ConduitBlockEntityRenderer());
		this.renderers.put(BellBlockEntity.class, new BellBlockEntityRenderer());
		this.renderers.put(CampfireBlockEntity.class, new CampfireBlockEntityRenderer());

		for (BlockEntityRenderer<?> blockEntityRenderer : this.renderers.values()) {
			blockEntityRenderer.setRenderManager(this);
		}
	}

	public <T extends BlockEntity> BlockEntityRenderer<T> method_3550(Class<? extends BlockEntity> class_) {
		BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer = (BlockEntityRenderer<? extends BlockEntity>)this.renderers.get(class_);
		if (blockEntityRenderer == null && class_ != BlockEntity.class) {
			blockEntityRenderer = this.method_3550(class_.getSuperclass());
			this.renderers.put(class_, blockEntityRenderer);
		}

		return (BlockEntityRenderer<T>)blockEntityRenderer;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityRenderer<T> method_3553(@Nullable BlockEntity blockEntity) {
		return blockEntity == null ? null : this.method_3550(blockEntity.getClass());
	}

	public void method_3549(World world, TextureManager textureManager, TextRenderer textRenderer, class_4184 arg, HitResult hitResult) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.field_4347 = textureManager;
		this.cameraEntity = arg;
		this.fontRenderer = textRenderer;
		this.hitResult = hitResult;
	}

	public void render(BlockEntity blockEntity, float f, int i) {
		if (blockEntity.getSquaredDistance(this.cameraEntity.method_19326().x, this.cameraEntity.method_19326().y, this.cameraEntity.method_19326().z)
			< blockEntity.getSquaredRenderDistance()) {
			GuiLighting.enable();
			int j = this.world.method_8313(blockEntity.method_11016(), 0);
			int k = j % 65536;
			int l = j / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)k, (float)l);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockPos = blockEntity.method_11016();
			this.renderEntity(
				blockEntity, (double)blockPos.getX() - renderOffsetX, (double)blockPos.getY() - renderOffsetY, (double)blockPos.getZ() - renderOffsetZ, f, i, false
			);
		}
	}

	public void renderEntity(BlockEntity blockEntity, double d, double e, double f, float g) {
		this.renderEntity(blockEntity, d, e, f, g, -1, false);
	}

	public void renderEntity(BlockEntity blockEntity) {
		this.renderEntity(blockEntity, 0.0, 0.0, 0.0, 0.0F, -1, true);
	}

	public void renderEntity(BlockEntity blockEntity, double d, double e, double f, float g, int i, boolean bl) {
		BlockEntityRenderer<BlockEntity> blockEntityRenderer = this.method_3553(blockEntity);
		if (blockEntityRenderer != null) {
			try {
				if (!bl && (!blockEntity.hasWorld() || !blockEntity.method_11010().getBlock().hasBlockEntity())) {
					return;
				}

				blockEntityRenderer.render(blockEntity, d, e, f, g, i);
			} catch (Throwable var15) {
				CrashReport crashReport = CrashReport.create(var15, "Rendering Block Entity");
				CrashReportSection crashReportSection = crashReport.method_562("Block Entity Details");
				blockEntity.method_11003(crashReportSection);
				throw new CrashException(crashReport);
			}
		}
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
		if (world == null) {
			this.cameraEntity = null;
		}
	}

	public TextRenderer getFontRenderer() {
		return this.fontRenderer;
	}
}
