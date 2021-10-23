package name.soy.notex;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.LookingPosArgument;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class NXVisiualize {
	public static BlockPos getPlayerSeeNote(ServerWorld world, ServerPlayerEntity player) {
		for (double i = 0; i < 5; i += 0.05) {
			ServerCommandSource commandSource = player.getCommandSource();
			Vec3d vops = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0);
			Vec3d seePos = new LookingPosArgument(0, 0, i).toAbsolutePos(commandSource.withPosition(vops));
			BlockPos pos = new BlockPos(seePos);
			BlockState blockState = world.getBlockState(pos);
			if (!blockState.isAir()) {
				VoxelShape shapes = blockState.getCollisionShape(world, pos);
				AtomicBoolean noray = new AtomicBoolean(false);
				shapes.forEachBox((double minX, double minY, double minZ, double maxX, double maxY, double maxZ) -> {
//					System.out.printf("{[%.4f,%.4f],[%.4f,%.4f],[%.4f,%.4f]}", minX, maxX, minY, maxY, minZ, maxZ);
					double x = (seePos.x - pos.getX()), y = (seePos.y - pos.getY()), z = (seePos.z - pos.getZ());
					noray.set(x < maxX && x > minX && y < maxY && y > minY && z < maxZ && z > minZ);
				});
				if (!noray.get()) continue;
				if (blockState.getBlock().is(Blocks.NOTE_BLOCK)) return pos; else break;
			}
		}
		return null;
	}

	public static BlockPos getPlayerSee(ServerWorld world, ServerPlayerEntity player) {
		for (double i = 0; i < 5; i += 0.05) {
			ServerCommandSource commandSource = player.getCommandSource();
			Vec3d vops = player.getPos().add(0, player.getEyeHeight(player.getPose()), 0);
			Vec3d seePos = new LookingPosArgument(0, 0, i).toAbsolutePos(commandSource.withPosition(vops));
			BlockPos pos = new BlockPos(seePos);
			BlockState blockState = world.getBlockState(pos);
			if (!blockState.isAir()) {
				VoxelShape shapes = blockState.getCollisionShape(world, pos);
				AtomicBoolean noray = new AtomicBoolean(false);
				shapes.forEachBox((double minX, double minY, double minZ, double maxX, double maxY, double maxZ) -> {
//					System.out.printf("{[%.4f,%.4f],[%.4f,%.4f],[%.4f,%.4f]}", minX, maxX, minY, maxY, minZ, maxZ);
					double x = (seePos.x - pos.getX()), y = (seePos.y - pos.getY()), z = (seePos.z - pos.getZ());
					noray.set(x < maxX && x > minX && y < maxY && y > minY && z < maxZ && z > minZ);
				});
				if (!noray.get()) {
					continue;
				}
				if (blockState.getBlock().is(Blocks.NOTE_BLOCK)) {
					return pos;
				} else {
					break;
				}
			}
		}
		return null;
	}

	public static HashSet<BlockPos> NEW_LOOKED = new HashSet<>();
	public static HashSet<BlockPos> OLD_LOOKED = new HashSet<>();
	/**
	 * 音符盒信息显示实体列表
	 */
	public static HashMap<BlockPos, List<ArmorStandEntity>> SEE_ENTITY = new HashMap<>();


	public static HashSet<UUID> PLAYER_SCROLLS = new HashSet<>();

	public static HashMap<UUID, String> PLAYER_LANG = new HashMap<>();

}
