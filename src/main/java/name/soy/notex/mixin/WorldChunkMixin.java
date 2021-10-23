package name.soy.notex.mixin;

import name.soy.notex.NoteBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
	@Shadow
	@Nullable
	protected abstract BlockEntity createBlockEntity(BlockPos pos);

	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);

	@Shadow
	@Final
	private World world;

	@Shadow
	@Final
	private Map<BlockPos, BlockEntity> blockEntities;

	@Inject(method = "setBlockState", at = @At(value = "RETURN"))
	public void setBlockState(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir) {
		if (state != null && state.isOf(Blocks.NOTE_BLOCK)) {
			createBlockEntity(pos);
		}
	}

	@Inject(method = "setBlockEntity", at = @At(value = "RETURN"))
	public void setBlockEntity(BlockPos pos, BlockEntity blockEntity, CallbackInfo ci) {
		if (this.getBlockState(pos).getBlock().is(Blocks.NOTE_BLOCK)) {
			blockEntity.setLocation(this.world, pos);
			blockEntity.cancelRemoval();
			BlockEntity blockEntity2 = this.blockEntities.put(pos.toImmutable(), blockEntity);
			if (blockEntity2 != null && blockEntity2 != blockEntity) {
				blockEntity2.markRemoved();
			}
		}
	}

	@Inject(method = "createBlockEntity", at = @At("RETURN"), cancellable = true)
	public void create(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
		Block b = getBlockState(pos).getBlock();
		if (b.is(Blocks.NOTE_BLOCK)) {
			NoteBlockEntity entity = new NoteBlockEntity();
			entity.setIns(getBlockState(pos).get(NoteBlock.INSTRUMENT).asString());
			entity.setNote(getBlockState(pos).get(NoteBlock.NOTE));
			cir.setReturnValue(entity);
		}
	}
}
