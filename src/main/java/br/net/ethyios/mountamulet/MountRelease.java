package br.net.ethyios.mountamulet;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class MountRelease {
    private MountRelease() {
    }

    public static MountReleaseResult release(ItemStack amuletStack, ServerLevel level, Vec3 position, float yRot) {
        StoredMount storedMount = amuletStack.get(ModDataComponents.STORED_MOUNT.get());
        if (storedMount == null) {
            return MountReleaseResult.EMPTY;
        }

        Optional<Entity> entity = createStoredEntity(level, storedMount);
        if (entity.isEmpty()) {
            return MountReleaseResult.INVALID_DATA;
        }

        return placeStoredEntity(amuletStack, level, entity.get(), position, yRot);
    }

    private static Optional<Entity> createStoredEntity(ServerLevel level, StoredMount storedMount) {
        CompoundTag loadTag = storedMount.entityTagWithId();
        loadTag.remove("Passengers");
        return EntityType.create(loadTag, level).filter(AbstractHorse.class::isInstance);
    }

    private static MountReleaseResult placeStoredEntity(
        ItemStack amuletStack,
        ServerLevel level,
        Entity entity,
        Vec3 position,
        float yRot
    ) {
        entity.moveTo(position.x(), position.y(), position.z(), yRot, 0.0F);
        if (!level.noCollision(entity, entity.getBoundingBox())) {
            return MountReleaseResult.BLOCKED;
        }

        if (!level.addFreshEntity(entity)) {
            return MountReleaseResult.INVALID_DATA;
        }

        amuletStack.remove(ModDataComponents.STORED_MOUNT.get());
        return MountReleaseResult.RELEASED;
    }
}
