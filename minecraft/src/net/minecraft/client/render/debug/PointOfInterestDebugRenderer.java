package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.DebugNameGenerator;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.Renderer {
	private static final Logger field_18920 = LogManager.getLogger();
	private final MinecraftClient field_18786;
	private final Map<BlockPos, PointOfInterestDebugRenderer.class_4233> pointsOfInterest = Maps.<BlockPos, PointOfInterestDebugRenderer.class_4233>newHashMap();
	private final Set<ChunkSectionPos> field_18788 = Sets.<ChunkSectionPos>newHashSet();
	private final Map<UUID, PointOfInterestDebugRenderer.class_4232> field_18921 = Maps.<UUID, PointOfInterestDebugRenderer.class_4232>newHashMap();
	private UUID field_18922;

	public PointOfInterestDebugRenderer(MinecraftClient minecraftClient) {
		this.field_18786 = minecraftClient;
	}

	@Override
	public void clear() {
		this.pointsOfInterest.clear();
		this.field_18788.clear();
		this.field_18921.clear();
		this.field_18922 = null;
	}

	public void method_19701(PointOfInterestDebugRenderer.class_4233 arg) {
		this.pointsOfInterest.put(arg.field_18931, arg);
	}

	public void removePointOfInterest(BlockPos blockPos) {
		this.pointsOfInterest.remove(blockPos);
	}

	public void method_19702(BlockPos blockPos, int i) {
		PointOfInterestDebugRenderer.class_4233 lv = (PointOfInterestDebugRenderer.class_4233)this.pointsOfInterest.get(blockPos);
		if (lv == null) {
			field_18920.warn("Strange, setFreeTicketCount was called for an unknown POI: " + blockPos);
		} else {
			lv.field_18933 = i;
		}
	}

	public void method_19433(ChunkSectionPos chunkSectionPos) {
		this.field_18788.add(chunkSectionPos);
	}

	public void method_19435(ChunkSectionPos chunkSectionPos) {
		this.field_18788.remove(chunkSectionPos);
	}

	public void addPointOfInterest(PointOfInterestDebugRenderer.class_4232 arg) {
		this.field_18921.put(arg.field_18923, arg);
	}

	@Override
	public void render(long l) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
		);
		RenderSystem.disableTexture();
		this.method_19699();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		if (!this.field_18786.player.isSpectator()) {
			this.method_19710();
		}
	}

	private void method_19699() {
		BlockPos blockPos = this.getCamera().getBlockPos();
		this.field_18788.forEach(chunkSectionPos -> {
			if (blockPos.isWithinDistance(chunkSectionPos.getCenterPos(), 60.0)) {
				method_19714(chunkSectionPos);
			}
		});
		this.field_18921.values().forEach(arg -> {
			if (this.method_19715(arg)) {
				this.method_20571(arg);
			}
		});

		for (BlockPos blockPos2 : this.pointsOfInterest.keySet()) {
			if (blockPos.isWithinDistance(blockPos2, 30.0)) {
				method_19709(blockPos2);
			}
		}

		this.pointsOfInterest.values().forEach(arg -> {
			if (blockPos.isWithinDistance(arg.field_18931, 30.0)) {
				this.method_19708(arg);
			}
		});
		this.method_20572().forEach((blockPos2x, list) -> {
			if (blockPos.isWithinDistance(blockPos2x, 30.0)) {
				this.method_20567(blockPos2x, list);
			}
		});
	}

	private static void method_19714(ChunkSectionPos chunkSectionPos) {
		float f = 1.0F;
		BlockPos blockPos = chunkSectionPos.getCenterPos();
		BlockPos blockPos2 = blockPos.add(-1.0, -1.0, -1.0);
		BlockPos blockPos3 = blockPos.add(1.0, 1.0, 1.0);
		DebugRenderer.method_19697(blockPos2, blockPos3, 0.2F, 1.0F, 0.2F, 0.15F);
	}

	private static void method_19709(BlockPos blockPos) {
		float f = 0.05F;
		DebugRenderer.method_19696(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void method_20567(BlockPos blockPos, List<String> list) {
		float f = 0.05F;
		DebugRenderer.method_19696(blockPos, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
		method_20569("" + list, blockPos, 0, -256);
		method_20569("Ghost POI", blockPos, 1, -65536);
	}

	private void method_19708(PointOfInterestDebugRenderer.class_4233 arg) {
		int i = 0;
		if (this.method_19712(arg).size() < 4) {
			method_20568("" + this.method_19712(arg), arg, i, -256);
		} else {
			method_20568("" + this.method_19712(arg).size() + " ticket holders", arg, i, -256);
		}

		method_20568("Free tickets: " + arg.field_18933, arg, ++i, -256);
		method_20568(arg.field_18932, arg, ++i, -1);
	}

	private void method_20570(PointOfInterestDebugRenderer.class_4232 arg) {
		if (arg.field_19330 != null) {
			PathfindingDebugRenderer.method_20556(this.getCamera(), arg.field_19330, 0.5F, false, false);
		}
	}

	private void method_20571(PointOfInterestDebugRenderer.class_4232 arg) {
		boolean bl = this.method_19711(arg);
		int i = 0;
		method_19704(arg.field_18926, i, arg.field_19328, -1, 0.03F);
		i++;
		if (bl) {
			method_19704(arg.field_18926, i, arg.field_18925 + " " + arg.field_19329 + "xp", -1, 0.02F);
			i++;
		}

		if (bl && !arg.field_19372.equals("")) {
			method_19704(arg.field_18926, i, arg.field_19372, -98404, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : arg.field_18928) {
				method_19704(arg.field_18926, i, string, -16711681, 0.02F);
				i++;
			}
		}

		if (bl) {
			for (String string : arg.field_18927) {
				method_19704(arg.field_18926, i, string, -16711936, 0.02F);
				i++;
			}
		}

		if (arg.field_19373) {
			method_19704(arg.field_18926, i, "Wants Golem", -23296, 0.02F);
			i++;
		}

		if (bl) {
			for (String string : arg.field_19375) {
				if (string.startsWith(arg.field_19328)) {
					method_19704(arg.field_18926, i, string, -1, 0.02F);
				} else {
					method_19704(arg.field_18926, i, string, -23296, 0.02F);
				}

				i++;
			}
		}

		if (bl) {
			for (String string : Lists.reverse(arg.field_19374)) {
				method_19704(arg.field_18926, i, string, -3355444, 0.02F);
				i++;
			}
		}

		if (bl) {
			this.method_20570(arg);
		}
	}

	private static void method_20568(String string, PointOfInterestDebugRenderer.class_4233 arg, int i, int j) {
		BlockPos blockPos = arg.field_18931;
		method_20569(string, blockPos, i, j);
	}

	private static void method_20569(String string, BlockPos blockPos, int i, int j) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)blockPos.getX() + 0.5;
		double g = (double)blockPos.getY() + 1.3 + (double)i * 0.2;
		double h = (double)blockPos.getZ() + 0.5;
		DebugRenderer.drawFloatingText(string, f, g, h, j, 0.02F, true, 0.0F, true);
	}

	private static void method_19704(Position position, int i, String string, int j, float f) {
		double d = 2.4;
		double e = 0.25;
		BlockPos blockPos = new BlockPos(position);
		double g = (double)blockPos.getX() + 0.5;
		double h = position.getY() + 2.4 + (double)i * 0.25;
		double k = (double)blockPos.getZ() + 0.5;
		float l = 0.5F;
		DebugRenderer.drawFloatingText(string, g, h, k, j, f, false, 0.5F, true);
	}

	private Camera getCamera() {
		return this.field_18786.gameRenderer.getCamera();
	}

	private Set<String> method_19712(PointOfInterestDebugRenderer.class_4233 arg) {
		return (Set<String>)this.method_19713(arg.field_18931).stream().map(DebugNameGenerator::getDebugName).collect(Collectors.toSet());
	}

	private boolean method_19711(PointOfInterestDebugRenderer.class_4232 arg) {
		return Objects.equals(this.field_18922, arg.field_18923);
	}

	private boolean method_19715(PointOfInterestDebugRenderer.class_4232 arg) {
		PlayerEntity playerEntity = this.field_18786.player;
		BlockPos blockPos = new BlockPos(playerEntity.x, arg.field_18926.getY(), playerEntity.z);
		BlockPos blockPos2 = new BlockPos(arg.field_18926);
		return blockPos.isWithinDistance(blockPos2, 30.0);
	}

	private Collection<UUID> method_19713(BlockPos blockPos) {
		return (Collection<UUID>)this.field_18921
			.values()
			.stream()
			.filter(arg -> arg.method_19718(blockPos))
			.map(PointOfInterestDebugRenderer.class_4232::method_19716)
			.collect(Collectors.toSet());
	}

	private Map<BlockPos, List<String>> method_20572() {
		Map<BlockPos, List<String>> map = Maps.<BlockPos, List<String>>newHashMap();

		for (PointOfInterestDebugRenderer.class_4232 lv : this.field_18921.values()) {
			for (BlockPos blockPos : lv.field_18930) {
				if (!this.pointsOfInterest.containsKey(blockPos)) {
					List<String> list = (List<String>)map.get(blockPos);
					if (list == null) {
						list = Lists.<String>newArrayList();
						map.put(blockPos, list);
					}

					list.add(lv.field_19328);
				}
			}
		}

		return map;
	}

	private void method_19710() {
		DebugRenderer.method_19694(this.field_18786.getCameraEntity(), 8).ifPresent(entity -> this.field_18922 = entity.getUuid());
	}

	@Environment(EnvType.CLIENT)
	public static class class_4232 {
		public final UUID field_18923;
		public final int field_18924;
		public final String field_19328;
		public final String field_18925;
		public final int field_19329;
		public final Position field_18926;
		public final String field_19372;
		public final Path field_19330;
		public final boolean field_19373;
		public final List<String> field_18927 = Lists.<String>newArrayList();
		public final List<String> field_18928 = Lists.<String>newArrayList();
		public final List<String> field_19374 = Lists.<String>newArrayList();
		public final List<String> field_19375 = Lists.<String>newArrayList();
		public final Set<BlockPos> field_18930 = Sets.<BlockPos>newHashSet();

		public class_4232(UUID uUID, int i, String string, String string2, int j, Position position, String string3, @Nullable Path path, boolean bl) {
			this.field_18923 = uUID;
			this.field_18924 = i;
			this.field_19328 = string;
			this.field_18925 = string2;
			this.field_19329 = j;
			this.field_18926 = position;
			this.field_19372 = string3;
			this.field_19330 = path;
			this.field_19373 = bl;
		}

		private boolean method_19718(BlockPos blockPos) {
			return this.field_18930.stream().anyMatch(blockPos::equals);
		}

		public UUID method_19716() {
			return this.field_18923;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4233 {
		public final BlockPos field_18931;
		public String field_18932;
		public int field_18933;

		public class_4233(BlockPos blockPos, String string, int i) {
			this.field_18931 = blockPos;
			this.field_18932 = string;
			this.field_18933 = i;
		}
	}
}
