package com.worldcretornica.plotme_core.sponge.api;

import com.google.common.base.Optional;
import com.worldcretornica.plotme_core.api.IItemStack;
import com.worldcretornica.plotme_core.api.ILocation;
import com.worldcretornica.plotme_core.api.IPlayer;
import com.worldcretornica.plotme_core.api.IWorld;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;

public class SpongePlayer extends SpongeUser implements IPlayer {

    private final Player player;

    public SpongePlayer(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(ChatTypes.CHAT, message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public IWorld getWorld() {
        return new SpongeWorld(player.getWorld());
    }

    @Override
    public ILocation getLocation() {
        Location location = player.getLocation();
        return new ILocation(getWorld(), location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void setLocation(ILocation location) {
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public IItemStack getItemInHand() {
        Optional<ItemStack> itemInHand = player.getItemInHand();
        if (itemInHand.isPresent()) {
            return new SpongeItemStack(itemInHand.get());
        } else {
            return null;
        }
    }

    @Override
    public void remove() {
        player.remove();
    }
}
