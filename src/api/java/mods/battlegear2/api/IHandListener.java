package mods.battlegear2.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IHandListener {
   Event.Result onClickBlock(PlayerInteractEvent event, ItemStack mainhandStack, ItemStack offhandStack, boolean isOffhand);
}
