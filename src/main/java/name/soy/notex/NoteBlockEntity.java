package name.soy.notex;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class NoteBlockEntity extends BlockEntity {
	public String getIns() {
		return ins;
	}

	public void setIns(String ins) {
		if (!locked) this.ins = ins;
		try {
			Instrument i = Instrument.valueOf(ins);
			assert world != null;
			BlockState state = world.getBlockState(pos);
			state.with(NoteBlock.NOTE, note);
			state.with(NoteBlock.INSTRUMENT, i);
			world.setBlockState(pos, state, 3);
		} catch (Exception ignored) {
		}
	}

	private String ins = "harp";
	private int note = 0;
	public boolean locked = false;

	public NoteBlockEntity() {
		super(Notex.NOTE_BLOCK_ENTITY);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putBoolean("locked", this.locked);
		nbt.putString("ins", ins);
		nbt.putInt("note", note);
		return nbt;
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		this.locked = tag.getBoolean("locked");
		this.ins = tag.getString("ins");
		this.note = tag.getInt("note");
	}

	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		if (!locked) this.note = note;
		try {
			Instrument i = Instrument.valueOf(ins);
			assert world != null;
			BlockState state = world.getBlockState(pos);
			state.with(NoteBlock.NOTE, note);
			state.with(NoteBlock.INSTRUMENT, i);
			world.setBlockState(pos, state, 3);
		} catch (Exception ignored) {
		}
	}

	public void play() {
		if (Notex.insNames.containsKey(ins)) {
			assert world != null;
			float vol = (float) world.getGameRules().get(Notex.customNoteVol).get();
			CustomIns custom = Notex.insNames.get(ins);
			if (custom.sType.equals(CustomIns.InsMode.STRETCH)) {
				SoundEvent e = new SoundEvent(new Identifier(custom.soundSpace));
				int center = (custom.min + custom.max) / 2;
				float pitch = (float) Math.pow(2.0D, (double) (note - center) / 12.0D);
				world.playSound(null, pos, e, SoundCategory.RECORDS, vol, pitch);
			} else if (custom.sType.equals(CustomIns.InsMode.SEPARATE)) {
				String space = custom.soundSpace + "." + note;
				System.out.println(space + ":" + note);
				SoundEvent e = new SoundEvent(new Identifier(space));
				world.playSound(null, pos, e, SoundCategory.RECORDS, vol, 1f);
				world.addParticle(ParticleTypes.NOTE, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, note / 24.0D, 0.0D, 0.0D);
			}
			try {
				((ServerWorld) world).spawnParticles(ParticleTypes.NOTE.getParametersFactory().read(ParticleTypes.NOTE, new StringReader("")),
						pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, 0, note / 24.0D, 0.0D, 0.0D, 1);
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}

		}
	}
}
