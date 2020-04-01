package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NetherPortalBlockEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NeitherPortalBlock extends PortalBlock implements BlockEntityProvider {
	private static final Random RANDOM = new Random();

	public NeitherPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	protected ParticleEffect createParticleEffect(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof NetherPortalBlockEntity) {
			int i = ((NetherPortalBlockEntity)blockEntity).getDimension();
			Vec3d vec3d = Vec3d.unpackRgb(i);
			double d = 1.0 + (double)(i >> 16 & 0xFF) / 255.0;
			return new DustParticleEffect((float)vec3d.x, (float)vec3d.y, (float)vec3d.z, (float)d);
		} else {
			return super.createParticleEffect(state, world, pos);
		}
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new NetherPortalBlockEntity(Math.abs(RANDOM.nextInt()));
	}
}
