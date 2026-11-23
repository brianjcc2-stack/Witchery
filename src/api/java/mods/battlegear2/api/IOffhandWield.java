package mods.battlegear2.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IOffhandWield {
   boolean isOffhandWieldable(ItemStack offhandStack, EntityPlayer wielder);
}
