package net.minecraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

public class class_8355 implements class_8289 {
	static final Logger field_43939 = LogUtils.getLogger();
	boolean field_43940;
	private final class_8355.class_8356 field_43941 = new class_8355.class_8356();
	private final Codec<class_8291> field_43942 = RecordCodecBuilder.create(instance -> instance.point(this.field_43941));
	static final float[] field_43943 = new float[]{2.32618E-39F, 1.7332302E25F, 7.578229E31F, 7.007856E22F, 4.730713E22F, 4.7414995E30F, 1.8012582E25F};
	static final float[] field_43944 = new float[]{1.498926E-39F, 4.631878E27F, 1.6974224E-19F, 7.0081926E28F, 1.7718017E28F};

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43940 ? Stream.of(this.field_43941) : Stream.empty();
	}

	@Override
	public Stream<class_8291> method_50204() {
		return Stream.empty();
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return !this.field_43940 && i > 0 ? Stream.of(this.field_43941) : Stream.empty();
	}

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43942;
	}

	static String method_50438(float[] fs) {
		ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

		for (float f : fs) {
			byteArrayDataOutput.writeFloat(f);
		}

		byte[] bs = byteArrayDataOutput.toByteArray();
		ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bs);
		return byteArrayDataInput.readUTF();
	}

	class class_8356 implements class_8291 {
		@Override
		public class_8289 method_50121() {
			return class_8355.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8355.this.field_43940 = arg == class_8290.APPROVE;
		}

		@Override
		public void method_50164(class_8290 arg, MinecraftServer minecraftServer) {
			if (!class_8355.this.field_43940 && arg == class_8290.APPROVE) {
				class_8355.this.field_43940 = true;
				class_8355.field_43939.error("LOOK AT YOU HACKER", (Throwable)(new UnsupportedOperationException()));
				Text text = Text.literal("Nice try");

				for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerManager().getPlayerList()) {
					serverPlayerEntity.networkHandler.disconnect(text);
				}
			}
		}

		@Override
		public Text method_50130(class_8290 arg) {
			return switch (arg) {
				case APPROVE -> class_8355.this.field_43940
				? Text.literal(class_8355.method_50438(class_8355.field_43943))
				: Text.literal(class_8355.method_50438(class_8355.field_43944));
				case REPEAL -> Text.translatable("You Can Not Redo.");
			};
		}
	}
}
