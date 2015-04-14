package com.worldcretornica.plotme_core.sponge.api;

import com.worldcretornica.plotme_core.api.IEntity;
import com.worldcretornica.plotme_core.api.ILocation;
import com.worldcretornica.plotme_core.api.IWorld;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;

import java.util.UUID;

public class SpongeEntity implements IEntity {

    private final Entity entity;

    public SpongeEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public ILocation getLocation() {
        Location location = entity.getLocation();
        return new ILocation(getWorld(), location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void setLocation(ILocation location) {

    }

    @Override
    public IWorld getWorld() {
        return new SpongeWorld(entity.getWorld());
    }

    @Override
    public void remove() {
        entity.remove();
    }

    @Override
    public UUID getUniqueId() {
        return entity.getUniqueId();
    }

    @Override
    public String getName() {
        return null; //Todo: Sponge doesn't have a get name for entities (yet).
    }
}
