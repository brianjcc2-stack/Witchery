package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IOffhandListener extends IHandListener {
   void onAttackEntity(PlayerEventChild.OffhandAttackEvent event, boolean isOffhand);

   Event.Result onClickAir(EntityPlayer player, ItemStack mainhandStack, ItemStack offhandStack);
}
