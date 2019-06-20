package net.minecraft;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_287 {
	private static final Logger field_1557 = LogManager.getLogger();
	private ByteBuffer field_1555;
	private IntBuffer field_1560;
	private ShortBuffer field_1561;
	private FloatBuffer field_1566;
	private int field_1554;
	private class_296 field_1558;
	private int field_1553;
	private boolean field_1559;
	private int field_1567;
	private double field_1564;
	private double field_1563;
	private double field_1562;
	private class_293 field_1565;
	private boolean field_1556;

	public class_287(int i) {
		this.field_1555 = class_311.method_1596(i * 4);
		this.field_1560 = this.field_1555.asIntBuffer();
		this.field_1561 = this.field_1555.asShortBuffer();
		this.field_1566 = this.field_1555.asFloatBuffer();
	}

	private void method_1335(int i) {
		if (this.field_1554 * this.field_1565.method_1362() + i > this.field_1555.capacity()) {
			int j = this.field_1555.capacity();
			int k = j + method_16005(i);
			field_1557.debug("Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.", j, k);
			int l = this.field_1560.position();
			ByteBuffer byteBuffer = class_311.method_1596(k);
			this.field_1555.position(0);
			byteBuffer.put(this.field_1555);
			byteBuffer.rewind();
			this.field_1555 = byteBuffer;
			this.field_1566 = this.field_1555.asFloatBuffer().asReadOnlyBuffer();
			this.field_1560 = this.field_1555.asIntBuffer();
			this.field_1560.position(l);
			this.field_1561 = this.field_1555.asShortBuffer();
			this.field_1561.position(l << 1);
		}
	}

	private static int method_16005(int i) {
		int j = 2097152;
		if (i == 0) {
			return j;
		} else {
			if (i < 0) {
				j *= -1;
			}

			int k = i % j;
			return k == 0 ? i : i + j - k;
		}
	}

	public void method_1341(float f, float g, float h) {
		int i = this.field_1554 / 4;
		float[] fs = new float[i];

		for (int j = 0; j < i; j++) {
			fs[j] = method_1319(
				this.field_1566,
				(float)((double)f + this.field_1564),
				(float)((double)g + this.field_1563),
				(float)((double)h + this.field_1562),
				this.field_1565.method_1359(),
				j * this.field_1565.method_1362()
			);
		}

		Integer[] integers = new Integer[i];

		for (int k = 0; k < integers.length; k++) {
			integers[k] = k;
		}

		Arrays.sort(integers, (integer, integer2) -> Floats.compare(fs[integer2], fs[integer]));
		BitSet bitSet = new BitSet();
		int l = this.field_1565.method_1362();
		int[] is = new int[l];

		for (int m = bitSet.nextClearBit(0); m < integers.length; m = bitSet.nextClearBit(m + 1)) {
			int n = integers[m];
			if (n != m) {
				this.field_1560.limit(n * l + l);
				this.field_1560.position(n * l);
				this.field_1560.get(is);
				int o = n;

				for (int p = integers[n]; o != m; p = integers[p]) {
					this.field_1560.limit(p * l + l);
					this.field_1560.position(p * l);
					IntBuffer intBuffer = this.field_1560.slice();
					this.field_1560.limit(o * l + l);
					this.field_1560.position(o * l);
					this.field_1560.put(intBuffer);
					bitSet.set(o);
					o = p;
				}

				this.field_1560.limit(m * l + l);
				this.field_1560.position(m * l);
				this.field_1560.put(is);
			}

			bitSet.set(m);
		}
	}

	public class_287.class_288 method_1334() {
		this.field_1560.rewind();
		int i = this.method_1316();
		this.field_1560.limit(i);
		int[] is = new int[i];
		this.field_1560.get(is);
		this.field_1560.limit(this.field_1560.capacity());
		this.field_1560.position(i);
		return new class_287.class_288(is, new class_293(this.field_1565));
	}

	private int method_1316() {
		return this.field_1554 * this.field_1565.method_1359();
	}

	private static float method_1319(FloatBuffer floatBuffer, float f, float g, float h, int i, int j) {
		float k = floatBuffer.get(j + i * 0 + 0);
		float l = floatBuffer.get(j + i * 0 + 1);
		float m = floatBuffer.get(j + i * 0 + 2);
		float n = floatBuffer.get(j + i * 1 + 0);
		float o = floatBuffer.get(j + i * 1 + 1);
		float p = floatBuffer.get(j + i * 1 + 2);
		float q = floatBuffer.get(j + i * 2 + 0);
		float r = floatBuffer.get(j + i * 2 + 1);
		float s = floatBuffer.get(j + i * 2 + 2);
		float t = floatBuffer.get(j + i * 3 + 0);
		float u = floatBuffer.get(j + i * 3 + 1);
		float v = floatBuffer.get(j + i * 3 + 2);
		float w = (k + n + q + t) * 0.25F - f;
		float x = (l + o + r + u) * 0.25F - g;
		float y = (m + p + s + v) * 0.25F - h;
		return w * w + x * x + y * y;
	}

	public void method_1324(class_287.class_288 arg) {
		this.field_1560.clear();
		this.method_1335(arg.method_1346().length * 4);
		this.field_1560.put(arg.method_1346());
		this.field_1554 = arg.method_1347();
		this.field_1565 = new class_293(arg.method_1345());
	}

	public void method_1343() {
		this.field_1554 = 0;
		this.field_1558 = null;
		this.field_1553 = 0;
	}

	public void method_1328(int i, class_293 arg) {
		if (this.field_1556) {
			throw new IllegalStateException("Already building!");
		} else {
			this.field_1556 = true;
			this.method_1343();
			this.field_1567 = i;
			this.field_1565 = arg;
			this.field_1558 = arg.method_1364(this.field_1553);
			this.field_1559 = false;
			this.field_1555.limit(this.field_1555.capacity());
		}
	}

	public class_287 method_1312(double d, double e) {
		int i = this.field_1554 * this.field_1565.method_1362() + this.field_1565.method_1365(this.field_1553);
		switch (this.field_1558.method_1386()) {
			case field_1623:
				this.field_1555.putFloat(i, (float)d);
				this.field_1555.putFloat(i + 4, (float)e);
				break;
			case field_1619:
			case field_1617:
				this.field_1555.putInt(i, (int)d);
				this.field_1555.putInt(i + 4, (int)e);
				break;
			case field_1622:
			case field_1625:
				this.field_1555.putShort(i, (short)((int)e));
				this.field_1555.putShort(i + 2, (short)((int)d));
				break;
			case field_1624:
			case field_1621:
				this.field_1555.put(i, (byte)((int)e));
				this.field_1555.put(i + 1, (byte)((int)d));
		}

		this.method_1325();
		return this;
	}

	public class_287 method_1313(int i, int j) {
		int k = this.field_1554 * this.field_1565.method_1362() + this.field_1565.method_1365(this.field_1553);
		switch (this.field_1558.method_1386()) {
			case field_1623:
				this.field_1555.putFloat(k, (float)i);
				this.field_1555.putFloat(k + 4, (float)j);
				break;
			case field_1619:
			case field_1617:
				this.field_1555.putInt(k, i);
				this.field_1555.putInt(k + 4, j);
				break;
			case field_1622:
			case field_1625:
				this.field_1555.putShort(k, (short)j);
				this.field_1555.putShort(k + 2, (short)i);
				break;
			case field_1624:
			case field_1621:
				this.field_1555.put(k, (byte)j);
				this.field_1555.put(k + 1, (byte)i);
		}

		this.method_1325();
		return this;
	}

	public void method_1339(int i, int j, int k, int l) {
		int m = (this.field_1554 - 4) * this.field_1565.method_1359() + this.field_1565.method_1370(1) / 4;
		int n = this.field_1565.method_1362() >> 2;
		this.field_1560.put(m, i);
		this.field_1560.put(m + n, j);
		this.field_1560.put(m + n * 2, k);
		this.field_1560.put(m + n * 3, l);
	}

	public void method_1322(double d, double e, double f) {
		int i = this.field_1565.method_1359();
		int j = (this.field_1554 - 4) * i;

		for (int k = 0; k < 4; k++) {
			int l = j + k * i;
			int m = l + 1;
			int n = m + 1;
			this.field_1560.put(l, Float.floatToRawIntBits((float)(d + this.field_1564) + Float.intBitsToFloat(this.field_1560.get(l))));
			this.field_1560.put(m, Float.floatToRawIntBits((float)(e + this.field_1563) + Float.intBitsToFloat(this.field_1560.get(m))));
			this.field_1560.put(n, Float.floatToRawIntBits((float)(f + this.field_1562) + Float.intBitsToFloat(this.field_1560.get(n))));
		}
	}

	private int method_1310(int i) {
		return ((this.field_1554 - i) * this.field_1565.method_1362() + this.field_1565.method_1360()) / 4;
	}

	public void method_1317(float f, float g, float h, int i) {
		int j = this.method_1310(i);
		int k = -1;
		if (!this.field_1559) {
			k = this.field_1560.get(j);
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				int l = (int)((float)(k & 0xFF) * f);
				int m = (int)((float)(k >> 8 & 0xFF) * g);
				int n = (int)((float)(k >> 16 & 0xFF) * h);
				k &= -16777216;
				k |= n << 16 | m << 8 | l;
			} else {
				int l = (int)((float)(k >> 24 & 0xFF) * f);
				int m = (int)((float)(k >> 16 & 0xFF) * g);
				int n = (int)((float)(k >> 8 & 0xFF) * h);
				k &= 255;
				k |= l << 24 | m << 16 | n << 8;
			}
		}

		this.field_1560.put(j, k);
	}

	private void method_1329(int i, int j) {
		int k = this.method_1310(j);
		int l = i >> 16 & 0xFF;
		int m = i >> 8 & 0xFF;
		int n = i & 0xFF;
		this.method_1340(k, l, m, n);
	}

	public void method_1314(float f, float g, float h, int i) {
		int j = this.method_1310(i);
		int k = method_16006((int)(f * 255.0F), 0, 255);
		int l = method_16006((int)(g * 255.0F), 0, 255);
		int m = method_16006((int)(h * 255.0F), 0, 255);
		this.method_1340(j, k, l, m);
	}

	private static int method_16006(int i, int j, int k) {
		if (i < j) {
			return j;
		} else {
			return i > k ? k : i;
		}
	}

	private void method_1340(int i, int j, int k, int l) {
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			this.field_1560.put(i, 0xFF000000 | l << 16 | k << 8 | j);
		} else {
			this.field_1560.put(i, j << 24 | k << 16 | l << 8 | 0xFF);
		}
	}

	public void method_1327() {
		this.field_1559 = true;
	}

	public class_287 method_1336(float f, float g, float h, float i) {
		return this.method_1323((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
	}

	public class_287 method_1323(int i, int j, int k, int l) {
		if (this.field_1559) {
			return this;
		} else {
			int m = this.field_1554 * this.field_1565.method_1362() + this.field_1565.method_1365(this.field_1553);
			switch (this.field_1558.method_1386()) {
				case field_1623:
					this.field_1555.putFloat(m, (float)i / 255.0F);
					this.field_1555.putFloat(m + 4, (float)j / 255.0F);
					this.field_1555.putFloat(m + 8, (float)k / 255.0F);
					this.field_1555.putFloat(m + 12, (float)l / 255.0F);
					break;
				case field_1619:
				case field_1617:
					this.field_1555.putFloat(m, (float)i);
					this.field_1555.putFloat(m + 4, (float)j);
					this.field_1555.putFloat(m + 8, (float)k);
					this.field_1555.putFloat(m + 12, (float)l);
					break;
				case field_1622:
				case field_1625:
					this.field_1555.putShort(m, (short)i);
					this.field_1555.putShort(m + 2, (short)j);
					this.field_1555.putShort(m + 4, (short)k);
					this.field_1555.putShort(m + 6, (short)l);
					break;
				case field_1624:
				case field_1621:
					if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
						this.field_1555.put(m, (byte)i);
						this.field_1555.put(m + 1, (byte)j);
						this.field_1555.put(m + 2, (byte)k);
						this.field_1555.put(m + 3, (byte)l);
					} else {
						this.field_1555.put(m, (byte)l);
						this.field_1555.put(m + 1, (byte)k);
						this.field_1555.put(m + 2, (byte)j);
						this.field_1555.put(m + 3, (byte)i);
					}
			}

			this.method_1325();
			return this;
		}
	}

	public void method_1333(int[] is) {
		this.method_1335(is.length * 4 + this.field_1565.method_1362());
		this.field_1560.position(this.method_1316());
		this.field_1560.put(is);
		this.field_1554 = this.field_1554 + is.length / this.field_1565.method_1359();
	}

	public void method_1344() {
		this.field_1554++;
		this.method_1335(this.field_1565.method_1362());
	}

	public class_287 method_1315(double d, double e, double f) {
		int i = this.field_1554 * this.field_1565.method_1362() + this.field_1565.method_1365(this.field_1553);
		switch (this.field_1558.method_1386()) {
			case field_1623:
				this.field_1555.putFloat(i, (float)(d + this.field_1564));
				this.field_1555.putFloat(i + 4, (float)(e + this.field_1563));
				this.field_1555.putFloat(i + 8, (float)(f + this.field_1562));
				break;
			case field_1619:
			case field_1617:
				this.field_1555.putInt(i, Float.floatToRawIntBits((float)(d + this.field_1564)));
				this.field_1555.putInt(i + 4, Float.floatToRawIntBits((float)(e + this.field_1563)));
				this.field_1555.putInt(i + 8, Float.floatToRawIntBits((float)(f + this.field_1562)));
				break;
			case field_1622:
			case field_1625:
				this.field_1555.putShort(i, (short)((int)(d + this.field_1564)));
				this.field_1555.putShort(i + 2, (short)((int)(e + this.field_1563)));
				this.field_1555.putShort(i + 4, (short)((int)(f + this.field_1562)));
				break;
			case field_1624:
			case field_1621:
				this.field_1555.put(i, (byte)((int)(d + this.field_1564)));
				this.field_1555.put(i + 1, (byte)((int)(e + this.field_1563)));
				this.field_1555.put(i + 2, (byte)((int)(f + this.field_1562)));
		}

		this.method_1325();
		return this;
	}

	public void method_1320(float f, float g, float h) {
		int i = (byte)((int)(f * 127.0F)) & 255;
		int j = (byte)((int)(g * 127.0F)) & 255;
		int k = (byte)((int)(h * 127.0F)) & 255;
		int l = i | j << 8 | k << 16;
		int m = this.field_1565.method_1362() >> 2;
		int n = (this.field_1554 - 4) * m + this.field_1565.method_1358() / 4;
		this.field_1560.put(n, l);
		this.field_1560.put(n + m, l);
		this.field_1560.put(n + m * 2, l);
		this.field_1560.put(n + m * 3, l);
	}

	private void method_1325() {
		this.field_1553++;
		this.field_1553 = this.field_1553 % this.field_1565.method_1363();
		this.field_1558 = this.field_1565.method_1364(this.field_1553);
		if (this.field_1558.method_1382() == class_296.class_298.field_1629) {
			this.method_1325();
		}
	}

	public class_287 method_1318(float f, float g, float h) {
		int i = this.field_1554 * this.field_1565.method_1362() + this.field_1565.method_1365(this.field_1553);
		switch (this.field_1558.method_1386()) {
			case field_1623:
				this.field_1555.putFloat(i, f);
				this.field_1555.putFloat(i + 4, g);
				this.field_1555.putFloat(i + 8, h);
				break;
			case field_1619:
			case field_1617:
				this.field_1555.putInt(i, (int)f);
				this.field_1555.putInt(i + 4, (int)g);
				this.field_1555.putInt(i + 8, (int)h);
				break;
			case field_1622:
			case field_1625:
				this.field_1555.putShort(i, (short)((int)f * 32767 & 65535));
				this.field_1555.putShort(i + 2, (short)((int)g * 32767 & 65535));
				this.field_1555.putShort(i + 4, (short)((int)h * 32767 & 65535));
				break;
			case field_1624:
			case field_1621:
				this.field_1555.put(i, (byte)((int)f * 127 & 0xFF));
				this.field_1555.put(i + 1, (byte)((int)g * 127 & 0xFF));
				this.field_1555.put(i + 2, (byte)((int)h * 127 & 0xFF));
		}

		this.method_1325();
		return this;
	}

	public void method_1331(double d, double e, double f) {
		this.field_1564 = d;
		this.field_1563 = e;
		this.field_1562 = f;
	}

	public void method_1326() {
		if (!this.field_1556) {
			throw new IllegalStateException("Not building!");
		} else {
			this.field_1556 = false;
			this.field_1555.position(0);
			this.field_1555.limit(this.method_1316() * 4);
		}
	}

	public ByteBuffer method_1342() {
		return this.field_1555;
	}

	public class_293 method_1311() {
		return this.field_1565;
	}

	public int method_1337() {
		return this.field_1554;
	}

	public int method_1338() {
		return this.field_1567;
	}

	public void method_1332(int i) {
		for (int j = 0; j < 4; j++) {
			this.method_1329(i, j + 1);
		}
	}

	public void method_1330(float f, float g, float h) {
		for (int i = 0; i < 4; i++) {
			this.method_1314(f, g, h, i + 1);
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_288 {
		private final int[] field_1569;
		private final class_293 field_1570;

		public class_288(int[] is, class_293 arg2) {
			this.field_1569 = is;
			this.field_1570 = arg2;
		}

		public int[] method_1346() {
			return this.field_1569;
		}

		public int method_1347() {
			return this.field_1569.length / this.field_1570.method_1359();
		}

		public class_293 method_1345() {
			return this.field_1570;
		}
	}
}
