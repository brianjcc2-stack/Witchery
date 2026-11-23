package com.emoniph.witchery.integration;

import mods.battlegear2.api.core.InventoryPlayerBattle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Integration hook for Mine & Blade: Battlegear 2 offhand battle slots.
 */
public class ModHookBattlegear2 extends ModHook {

   public String getModID() {
      return "battlegear2";
   }

   protected void doInit() {}

   protected void doPostInit() {}

   protected void doReduceMagicPower(EntityLivingBase entity, float factor) {}

   public ItemStack getOffhandItem(EntityPlayer player) {
      return this.initialized ? IntegrateBattlegear2.getOffhandItem(player) : null;
   }

   private static class IntegrateBattlegear2 {
      public static ItemStack getOffhandItem(EntityPlayer player) {
         if (player != null && player.inventory instanceof InventoryPlayerBattle) {
            InventoryPlayerBattle battleInv = (InventoryPlayerBattle) player.inventory;
            if (battleInv.isBattlemode()
                  && battleInv.currentItem >= InventoryPlayerBattle.OFFSET
                  && battleInv.currentItem < InventoryPlayerBattle.OFFSET + InventoryPlayerBattle.WEAPON_SETS) {
               return battleInv.getCurrentOffhandWeapon();
            }
         }
         return null;
      }
   }
}
