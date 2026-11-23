package mods.battlegear2.api;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IOffhandDual {
   boolean isOffhandHandDual(ItemStack off);

   boolean offhandAttackEntity(PlayerEventChild.OffhandAttackEvent event, ItemStack mainhandItem, ItemStack offhandItem);

   boolean offhandClickAir(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

   boolean offhandClickBlock(PlayerInteractEvent event, ItemStack mainhandItem, ItemStack offhandItem);

   void performPassiveEffects(Side effectiveSide, ItemStack mainhandItem, ItemStack offhandItem);
}
