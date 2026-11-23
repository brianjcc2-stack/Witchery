package mods.battlegear2.api;

import net.minecraft.item.ItemStack;

public interface IAllowItem {
   boolean allowOffhand(ItemStack mainhand, ItemStack offhand);
}
