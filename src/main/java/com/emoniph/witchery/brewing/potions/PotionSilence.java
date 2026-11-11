package com.emoniph.witchery.brewing.potions;

import com.emoniph.witchery.brewing.potions.IHandleLivingUpdate;
import com.emoniph.witchery.brewing.potions.PotionBase;
import com.emoniph.witchery.common.ExtendedPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class PotionSilence extends PotionBase implements IHandleLivingUpdate {

    public PotionSilence(int id, int color) {
        super(id, true, color);
    }

    @Override
    public void onLivingUpdate(World world, EntityLivingBase entity, LivingUpdateEvent event, int amplifier, int duration) {
        if (!world.isRemote && entity.ticksExisted % 10 == 0) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                player.getEntityData().setLong("witchery_silenced_until", world.getTotalWorldTime() + 15L);
                ExtendedPlayer playerEx = ExtendedPlayer.get(player);
                if (playerEx != null && !playerEx.isSilenced()) {
                    playerEx.setSilenced(Math.max(1, duration / 20));
                }
                if (player.isUsingItem()) {
                    ItemStack held = player.getHeldItem();
                    if (held != null && held.getItem() != null) {
                        String itemName = Item.itemRegistry.getNameForObject(held.getItem());
                        if (itemName != null && itemName.startsWith("witchery:")) {
                            player.stopUsingItem();
                        }
                    }
                }
            } else {
                ItemStack heldItem = entity.getHeldItem();
                if (heldItem != null && heldItem.getItem() != null) {
                    String itemName = Item.itemRegistry.getNameForObject(heldItem.getItem());
                    if (itemName != null && itemName.startsWith("witchery:")) {
                        entity.setCurrentItemOrArmor(0, null);
                        entity.entityDropItem(heldItem, 0.0F);
                    }
                }
            }
        }
    }
}
