package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class PlayerEventChild extends PlayerEvent {
   public final PlayerEvent parent;

   public PlayerEventChild(PlayerEvent parent) {
      super(parent.entityPlayer);
      this.parent = parent;
   }

   public void setCancelParentEvent(boolean cancel) {
      this.parent.setCanceled(cancel);
   }

   public EntityPlayer getPlayer() {
      return this.parent.entityPlayer;
   }

   @Cancelable
   public static class OffhandAttackEvent extends PlayerEventChild {
      public boolean swingOffhand = true;
      public boolean shouldAttack = true;
      public boolean cancelParent = true;
      public final EntityInteractEvent event;
      public final ItemStack mainHand;
      public final ItemStack offHand;

      public OffhandAttackEvent(EntityInteractEvent parent, ItemStack offHand) {
         super(parent);
         this.event = parent;
         this.mainHand = parent.entityPlayer.getCurrentEquippedItem();
         this.offHand = offHand;
      }

      public Entity getTarget() {
         return ((EntityInteractEvent)this.parent).target;
      }
   }
}
