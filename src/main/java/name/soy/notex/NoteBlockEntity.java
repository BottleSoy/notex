package name.soy.notex;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NoteBlockEntity extends BlockEntity {
	public String ins = "harp";
	public int note = 0;

	public NoteBlockEntity() {
		super(Notex.NOTE_BLOCK_ENTITY);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("ins", ins);
		nbt.putInt("note", note);
		return nbt;
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		this.ins = tag.getString("ins");
		this.note = tag.getInt("note");
	}
}
