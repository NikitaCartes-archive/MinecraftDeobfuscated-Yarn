package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

public class class_4517 {
	private final TestFunction field_20559;
	private final BlockPos blockPos;
	private final ServerWorld world;
	private final Collection<class_4518> field_20562 = Lists.<class_4518>newArrayList();
	private int field_20563;
	private Runnable field_20564;
	private boolean field_20565 = false;
	private long field_20566 = -1L;
	private boolean field_20567 = false;
	private long field_20568 = -1L;
	@Nullable
	private Throwable field_20569;

	public class_4517(TestFunction testFunction, BlockPos blockPos, ServerWorld serverWorld) {
		this.field_20559 = testFunction;
		this.blockPos = blockPos;
		this.world = serverWorld;
		this.field_20563 = testFunction.method_22299();
	}

	public void method_22165() {
		if (!this.method_22180()) {
			this.field_20563--;
			if (this.field_20563 <= 0) {
				if (this.field_20564 == null) {
					this.method_22168(new class_4522("Didn't succeed or fail within " + this.field_20559.method_22299() + " ticks"));
				} else {
					this.method_22186();
				}
			} else if (this.field_20564 != null) {
				this.method_22187();
			}
		}
	}

	public String method_22169() {
		return this.field_20559.method_22296();
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public void method_22166(int i) {
		try {
			StructureBlockBlockEntity structureBlockBlockEntity = class_4525.method_22250(this.field_20559.method_22298(), this.blockPos, i, this.world);
			structureBlockBlockEntity.setStructureName(this.method_22169());
			class_4525.method_22248(this.blockPos.add(1, 0, -1), this.world);
			this.field_20562.forEach(arg -> arg.method_22188(this));
			this.field_20559.method_22297(new class_4516(this));
		} catch (RuntimeException var3) {
			this.method_22168(var3);
		}
	}

	@Nullable
	public BlockPos method_22174() {
		StructureBlockBlockEntity structureBlockBlockEntity = this.getBlockEntity();
		return structureBlockBlockEntity == null ? null : structureBlockBlockEntity.getSize();
	}

	@Nullable
	private StructureBlockBlockEntity getBlockEntity() {
		return (StructureBlockBlockEntity)this.world.getBlockEntity(this.blockPos);
	}

	public ServerWorld getWorld() {
		return this.world;
	}

	public boolean method_22177() {
		return this.field_20567 && this.field_20569 == null;
	}

	public boolean method_22178() {
		return this.field_20569 != null;
	}

	public boolean method_22179() {
		return this.field_20565;
	}

	public boolean method_22180() {
		return this.field_20567;
	}

	public void method_22181() {
		this.field_20567 = true;
		this.field_20568 = SystemUtil.getMeasuringTimeMs();
		this.field_20569 = null;
		this.field_20564 = null;
		this.field_20562.forEach(arg -> arg.method_22189(this));
	}

	public void method_22168(Throwable throwable) {
		this.field_20567 = true;
		this.field_20568 = SystemUtil.getMeasuringTimeMs();
		this.field_20569 = throwable;
		this.field_20564 = null;
		this.field_20562.forEach(arg -> arg.method_22190(this));
	}

	@Nullable
	public Throwable method_22182() {
		return this.field_20569;
	}

	public String toString() {
		return this.method_22169();
	}

	public void method_22167(class_4518 arg) {
		this.field_20562.add(arg);
	}

	private void method_22186() {
		try {
			this.field_20564.run();
			this.method_22181();
		} catch (Exception var2) {
			this.method_22168(var2);
		}
	}

	private void method_22187() {
		try {
			this.field_20564.run();
			this.method_22181();
		} catch (Exception var2) {
		}
	}

	public void method_22170(int i) {
		class_4525.method_22250(this.field_20559.method_22298(), this.blockPos, i, this.world);
		this.field_20565 = true;
		this.field_20566 = SystemUtil.getMeasuringTimeMs();
	}

	public boolean method_22183() {
		return this.field_20559.method_22300();
	}

	public boolean method_22184() {
		return !this.field_20559.method_22300();
	}
}
