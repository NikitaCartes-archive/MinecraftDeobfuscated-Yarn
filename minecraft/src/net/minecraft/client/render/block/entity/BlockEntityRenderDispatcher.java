package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
	private final Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = Maps.<BlockEntityType<?>, BlockEntityRenderer<?>>newHashMap();
	public static final BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
	private final BufferBuilder field_20988 = new BufferBuilder(256);
	private TextRenderer fontRenderer;
	public TextureManager textureManager;
	public World world;
	public Camera cameraEntity;
	public HitResult hitResult;

	private BlockEntityRenderDispatcher() {
		this.method_23078(BlockEntityType.SIGN, new SignBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.MOB_SPAWNER, new MobSpawnerBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.PISTON, new PistonBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.CHEST, new ChestBlockEntityRenderer<>(this));
		this.method_23078(BlockEntityType.ENDER_CHEST, new ChestBlockEntityRenderer<>(this));
		this.method_23078(BlockEntityType.TRAPPED_CHEST, new ChestBlockEntityRenderer<>(this));
		this.method_23078(BlockEntityType.ENCHANTING_TABLE, new EnchantingTableBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.LECTERN, new LecternBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.END_PORTAL, new EndPortalBlockEntityRenderer<>(this));
		this.method_23078(BlockEntityType.END_GATEWAY, new EndGatewayBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.BEACON, new BeaconBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.SKULL, new SkullBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.BANNER, new BannerBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.SHULKER_BOX, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel(), this));
		this.method_23078(BlockEntityType.BED, new BedBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.CONDUIT, new ConduitBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.BELL, new BellBlockEntityRenderer(this));
		this.method_23078(BlockEntityType.CAMPFIRE, new CampfireBlockEntityRenderer(this));
	}

	private <E extends BlockEntity> void method_23078(BlockEntityType<E> blockEntityType, BlockEntityRenderer<E> blockEntityRenderer) {
		this.renderers.put(blockEntityType, blockEntityRenderer);
	}

	@Nullable
	public <E extends BlockEntity> BlockEntityRenderer<E> get(E blockEntity) {
		return (BlockEntityRenderer<E>)this.renderers.get(blockEntity.getType());
	}

	public void configure(World world, TextureManager textureManager, TextRenderer textRenderer, Camera camera, HitResult hitResult) {
		if (this.world != world) {
			this.setWorld(world);
		}

		this.textureManager = textureManager;
		this.cameraEntity = camera;
		this.fontRenderer = textRenderer;
		this.hitResult = hitResult;
	}

	public <E extends BlockEntity> void render(
		E blockEntity, float f, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, double d, double e, double g
	) {
		if (blockEntity.getSquaredDistance(this.cameraEntity.getPos().x, this.cameraEntity.getPos().y, this.cameraEntity.getPos().z)
			< blockEntity.getSquaredRenderDistance()) {
			BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
			if (blockEntityRenderer != null) {
				if (blockEntity.hasWorld() && blockEntity.getType().supports(blockEntity.getCachedState().getBlock())) {
					BlockPos blockPos = blockEntity.getPos();
					renderEntity(
						blockEntity,
						() -> render(
								blockEntityRenderer,
								blockEntity,
								(double)blockPos.getX() - d,
								(double)blockPos.getY() - e,
								(double)blockPos.getZ() - g,
								f,
								matrixStack,
								layeredVertexConsumerStorage
							)
					);
				}
			}
		}
	}

	private static <T extends BlockEntity> void render(
		BlockEntityRenderer<T> blockEntityRenderer,
		T blockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		World world = blockEntity.getWorld();
		int i;
		if (world != null) {
			i = world.getLightmapCoordinates(blockEntity.getPos());
		} else {
			i = 15728880;
		}

		blockEntityRenderer.render(blockEntity, d, e, f, g, matrixStack, layeredVertexConsumerStorage, i, OverlayTexture.field_21444);
	}

	@Deprecated
	public <E extends BlockEntity> void renderEntity(E blockEntity, MatrixStack matrixStack) {
		LayeredVertexConsumerStorage.class_4598 lv = LayeredVertexConsumerStorage.method_22991(this.field_20988);
		this.method_23077(blockEntity, matrixStack, lv, 15728880, OverlayTexture.field_21444);
		lv.method_22993();
	}

	public <E extends BlockEntity> boolean method_23077(
		E blockEntity, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j
	) {
		BlockEntityRenderer<E> blockEntityRenderer = this.get(blockEntity);
		if (blockEntityRenderer == null) {
			return true;
		} else {
			renderEntity(blockEntity, () -> blockEntityRenderer.render(blockEntity, 0.0, 0.0, 0.0, 0.0F, matrixStack, layeredVertexConsumerStorage, i, j));
			return false;
		}
	}

	private static void renderEntity(BlockEntity blockEntity, Runnable runnable) {
		try {
			runnable.run();
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Rendering Block Entity");
			CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
			blockEntity.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
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
