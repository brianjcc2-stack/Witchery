package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

public interface IUsableItem {
   boolean isUsedOverAttack(ItemStack itemStack);
}
