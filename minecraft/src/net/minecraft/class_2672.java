package net.minecraft;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2672 implements class_2596<class_2602> {
	private int field_12236;
	private int field_12235;
	private int field_12234;
	private class_2487 field_16416;
	private byte[] field_12237;
	private List<class_2487> field_12238;
	private boolean field_12233;

	public class_2672() {
	}

	public class_2672(class_2818 arg, int i) {
		class_1923 lv = arg.method_12004();
		this.field_12236 = lv.field_9181;
		this.field_12235 = lv.field_9180;
		this.field_12233 = i == 65535;
		this.field_16416 = new class_2487();

		for (Entry<class_2902.class_2903, class_2902> entry : arg.method_12011()) {
			if (((class_2902.class_2903)entry.getKey()).method_16137()) {
				this.field_16416.method_10566(((class_2902.class_2903)entry.getKey()).method_12605(), new class_2501(((class_2902)entry.getValue()).method_12598()));
			}
		}

		this.field_12237 = new byte[this.method_11522(arg, i)];
		this.field_12234 = this.method_11529(new class_2540(this.method_11527()), arg, i);
		this.field_12238 = Lists.<class_2487>newArrayList();

		for (Entry<class_2338, class_2586> entryx : arg.method_12214().entrySet()) {
			class_2338 lv2 = (class_2338)entryx.getKey();
			class_2586 lv3 = (class_2586)entryx.getValue();
			int j = lv2.method_10264() >> 4;
			if (this.method_11530() || (i & 1 << j) != 0) {
				class_2487 lv4 = lv3.method_16887();
				this.field_12238.add(lv4);
			}
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12236 = arg.readInt();
		this.field_12235 = arg.readInt();
		this.field_12233 = arg.readBoolean();
		this.field_12234 = arg.method_10816();
		this.field_16416 = arg.method_10798();
		int i = arg.method_10816();
		if (i > 2097152) {
			throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
		} else {
			this.field_12237 = new byte[i];
			arg.readBytes(this.field_12237);
			int j = arg.method_10816();
			this.field_12238 = Lists.<class_2487>newArrayList();

			for (int k = 0; k < j; k++) {
				this.field_12238.add(arg.method_10798());
			}
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12236);
		arg.writeInt(this.field_12235);
		arg.writeBoolean(this.field_12233);
		arg.method_10804(this.field_12234);
		arg.method_10794(this.field_16416);
		arg.method_10804(this.field_12237.length);
		arg.writeBytes(this.field_12237);
		arg.method_10804(this.field_12238.size());

		for (class_2487 lv : this.field_12238) {
			arg.method_10794(lv);
		}
	}

	public void method_11528(class_2602 arg) {
		arg.method_11128(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2540 method_11521() {
		return new class_2540(Unpooled.wrappedBuffer(this.field_12237));
	}

	private ByteBuf method_11527() {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(this.field_12237);
		byteBuf.writerIndex(0);
		return byteBuf;
	}

	public int method_11529(class_2540 arg, class_2818 arg2, int i) {
		int j = 0;
		class_2826[] lvs = arg2.method_12006();
		int k = 0;

		for (int l = lvs.length; k < l; k++) {
			class_2826 lv = lvs[k];
			if (lv != class_2818.field_12852 && (!this.method_11530() || !lv.method_12261()) && (i & 1 << k) != 0) {
				j |= 1 << k;
				lv.method_12257(arg);
			}
		}

		if (this.method_11530()) {
			class_1959[] lvs2 = arg2.method_12036();

			for (int lx = 0; lx < lvs2.length; lx++) {
				arg.writeInt(class_2378.field_11153.method_10249(lvs2[lx]));
			}
		}

		return j;
	}

	protected int method_11522(class_2818 arg, int i) {
		int j = 0;
		class_2826[] lvs = arg.method_12006();
		int k = 0;

		for (int l = lvs.length; k < l; k++) {
			class_2826 lv = lvs[k];
			if (lv != class_2818.field_12852 && (!this.method_11530() || !lv.method_12261()) && (i & 1 << k) != 0) {
				j += lv.method_12260();
			}
		}

		if (this.method_11530()) {
			j += arg.method_12036().length * 4;
		}

		return j;
	}

	@Environment(EnvType.CLIENT)
	public int method_11523() {
		return this.field_12236;
	}

	@Environment(EnvType.CLIENT)
	public int method_11524() {
		return this.field_12235;
	}

	@Environment(EnvType.CLIENT)
	public int method_11526() {
		return this.field_12234;
	}

	public boolean method_11530() {
		return this.field_12233;
	}

	@Environment(EnvType.CLIENT)
	public class_2487 method_16123() {
		return this.field_16416;
	}

	@Environment(EnvType.CLIENT)
	public List<class_2487> method_11525() {
		return this.field_12238;
	}
}
