package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

public class class_8413 {
	public static final int field_44143 = 10;
	public static final int field_44144 = 10;
	private static final int field_44146 = 3600;
	private static final int field_44147 = 20;
	private static final int field_44148 = 20;
	public static final Codec<class_8413> field_44145 = RecordCodecBuilder.create(
		instance -> instance.group(Codec.INT.fieldOf("level").forGetter(arg -> arg.field_44149), Codec.INT.fieldOf("time").forGetter(arg -> arg.field_44150))
				.apply(instance, class_8413::new)
	);
	private int field_44149;
	private int field_44150;

	public class_8413() {
		this(10, 0);
	}

	private class_8413(int i, int j) {
		this.field_44149 = i;
		this.field_44150 = j;
	}

	public void method_50772(int i) {
		this.method_50776(this.field_44149 + i);
	}

	public void method_50776(int i) {
		this.field_44149 = MathHelper.clamp(i, 0, 10);
	}

	public void method_50773(PlayerEntity playerEntity) {
		this.field_44150 = this.field_44150 + this.method_50777(playerEntity);
		if (this.field_44150 > 3600) {
			this.method_50776(this.field_44149 - 1);
			this.field_44150 = 0;
		}

		Random random = playerEntity.getRandom();
		if (playerEntity.isSubmergedInWater() && random.nextInt(20) == 0) {
			this.method_50772(1);
		}

		if (this.field_44149 == 0 && playerEntity.age % 20 == 0) {
			playerEntity.damageWithModifier(playerEntity.getDamageSources().dryOut(), 1.0F);
		}
	}

	private int method_50777(PlayerEntity playerEntity) {
		BlockPos blockPos = playerEntity.getBlockPos();
		Biome biome = playerEntity.world.getBiome(blockPos).value();
		return biome.isHot(blockPos) ? 2 : 1;
	}

	public int method_50771() {
		return this.field_44149;
	}
}
