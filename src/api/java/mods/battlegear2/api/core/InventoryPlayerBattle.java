package mods.battlegear2.api.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class InventoryPlayerBattle extends InventoryPlayer {
   public static int ARMOR_OFFSET = 100;
   public static int OFFSET = 150;
   public static int WEAPON_SETS = 3;
   public static int EXTRA_ITEMS = WEAPON_SETS * 2;

   public InventoryPlayerBattle(EntityPlayer entityPlayer) {
      super(entityPlayer);
   }

   public boolean isBattlemode() {
      return this.currentItem >= OFFSET && this.currentItem < OFFSET + EXTRA_ITEMS;
   }

   public ItemStack getCurrentOffhandWeapon() {
      return this.isBattlemode() ? this.getStackInSlot(this.currentItem + WEAPON_SETS) : null;
   }
}
