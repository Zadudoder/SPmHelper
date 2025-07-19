package zadudoder.spmhelper.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import zadudoder.spmhelper.SPmHelperClient;
import zadudoder.spmhelper.config.SPmHelperConfig;

import java.util.HashSet;
import java.util.Set;

public class ParticleManager {
    private static final Set<BlockPos> renderedParticles = new HashSet<>();
    private static final double PARTICLE_OFFSET = 0.5;

    public static void registerParticleRendering() {
        // Очищаем кэш при загрузке мира
        WorldRenderEvents.START.register(context -> renderedParticles.clear());

        // Статическая тропинка
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (!SPmHelperConfig.get().particlesEnabled || client.player == null || client.world == null) return;

            // Координаты начальной (игрок) и конечной точки
            Vec3d startPos = client.player.getPos();
            Vec3d endPos = new Vec3d(238, 138, 244); // Замените на ваши целевые координаты

            // Создаем путь из частиц
            createParticlePath(client, startPos, endPos);
        });
    }

    private static void createParticlePath(MinecraftClient client, Vec3d start, Vec3d end) {
        double distance = start.distanceTo(end);
        if (distance < 2.0) return;

        int steps = (int) distance;
        Vec3d direction = end.subtract(start).normalize();
        double stepSize = distance / steps;
        Vec3d lastValidPos = start;

        for (int i = 0; i <= steps; i++) {
            Vec3d checkPos = start.add(direction.multiply(stepSize * i));
            BlockPos blockPos = new BlockPos((int) checkPos.x, (int) checkPos.y, (int) checkPos.z);

            // Пропускаем если уже отрендерили частицу в этом блоке
            if (renderedParticles.contains(blockPos)) continue;

            Vec3d surfacePos = findSurfacePosition(client.world, checkPos);
            if (surfacePos != null && !hasObstruction(client.world, lastValidPos, surfacePos)) {
                // Центрируем частицу в середине блока
                Vec3d centeredPos = new Vec3d(
                        Math.floor(surfacePos.x) + PARTICLE_OFFSET,
                        surfacePos.y,
                        Math.floor(surfacePos.z) + PARTICLE_OFFSET
                );

                client.world.addParticle(
                        ParticleTypes.GLOW,
                        centeredPos.x, centeredPos.y, centeredPos.z,
                        0, 0, 0
                );

                renderedParticles.add(blockPos);
                lastValidPos = surfacePos;
            }
        }
    }

    private static Vec3d findSurfacePosition(World world, Vec3d pos) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable(
                (int) pos.x,
                (int) pos.y,
                (int) pos.z
        );

        // Проверяем сверху вниз (включая воздушные блоки)
        for (int y = world.getTopY(); y >= world.getBottomY(); y--) {
            blockPos.setY(y);
            BlockState state = world.getBlockState(blockPos);

            // Игнорируем траву, цветы и другие неплотные блоки
            if (isNonSolidPlant(state)) continue;

            // Если блок проходимый (воздух, трава и т.д.)
            if (!state.isOpaque() || state.getCollisionShape(world, blockPos).isEmpty()) {
                // Проверяем блок под ним
                blockPos.setY(y - 1);
                BlockState belowState = world.getBlockState(blockPos);
                if (!isNonSolidPlant(belowState) && (belowState.isOpaque() || y == world.getBottomY())) {
                    return new Vec3d(pos.x, y + 0.1, pos.z);
                }
            } else {
                // Если блок непроходимый и не растение - это поверхность
                return new Vec3d(pos.x, y + 1.1, pos.z);
            }
        }

        return null;
    }

    private static boolean isNonSolidPlant(BlockState state) {
        return state.getBlock() instanceof PlantBlock ||
                state.getBlock() instanceof FlowerBlock ||
                state.getBlock() instanceof TallPlantBlock ||
                state.getBlock() instanceof AbstractPlantStemBlock;
    }

    private static boolean hasObstruction(World world, Vec3d start, Vec3d end) {
        RaycastContext context = new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
        );

        BlockHitResult hit = world.raycast(context);
        return hit.getType() != HitResult.Type.MISS;
    }

    public static void spawnParticle(ParticleEffect type, double x, double y, double z) {
        if (MinecraftClient.getInstance().world != null && SPmHelperConfig.get().particlesEnabled) {
            MinecraftClient.getInstance().world.addParticle(
                    type,
                    Math.floor(x) + PARTICLE_OFFSET,
                    y,
                    Math.floor(z) + PARTICLE_OFFSET,
                    0, 0, 0
            );
        }
    }
}