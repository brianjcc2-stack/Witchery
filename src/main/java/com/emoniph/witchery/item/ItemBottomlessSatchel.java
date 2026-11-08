package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemBottomlessSatchel extends Item {

   public ItemBottomlessSatchel() {
      this.setMaxStackSize(1);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (player.isSneaking()) {
         if (!world.isRemote) {
            if (!stack.hasTagCompound()) {
               stack.setTagCompound(new NBTTagCompound());
            }
            boolean current = stack.getTagCompound().getBoolean("AutoCollect");
            stack.getTagCompound().setBoolean("AutoCollect", !current);
            if (!current) {
               ChatUtil.sendTranslated(EnumChatFormatting.GREEN, player, "witchery.satchel.autocollect.on");
               SoundEffect.RANDOM_LEVELUP.playOnlyTo(player, 1.0F, 1.5F);
            } else {
               ChatUtil.sendTranslated(EnumChatFormatting.RED, player, "witchery.satchel.autocollect.off");
               SoundEffect.NOTE_SNARE.playOnlyTo(player, 1.0F, 0.5F);
            }
         }
         return stack;
      }

      if (!world.isRemote) {
         player.displayGUIChest(new InventorySatchel(stack, player));
         SoundEffect.WITCHERY_RANDOM_WINDUP.playOnlyTo(player, 0.8F, 1.2F);
      }

      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.epic;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("witchery.satchel.tip1"));
      boolean auto = stack.hasTagCompound() && stack.getTagCompound().getBoolean("AutoCollect");
      if (auto) {
         list.add(EnumChatFormatting.GREEN + StatCollector.translateToLocal("witchery.satchel.tip.autocollect.active"));
      } else {
         list.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("witchery.satchel.tip.autocollect.inactive"));
      }
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.satchel.tip2"));
   }

   public static class InventorySatchel extends InventoryBasic {
      private final ItemStack satchelStack;
      private final EntityPlayer player;

      public InventorySatchel(ItemStack stack, EntityPlayer player) {
         super(stack.hasDisplayName() ? stack.getDisplayName() : StatCollector.translateToLocal("item.witchery:bottomlesssatchel.name"), stack.hasDisplayName(), 54);
         this.satchelStack = stack;
         this.player = player;
         this.readFromNBT();
      }

      public void readFromNBT() {
         if (satchelStack != null && satchelStack.hasTagCompound() && satchelStack.getTagCompound().hasKey("SatchelInventory")) {
            NBTTagList list = satchelStack.getTagCompound().getTagList("SatchelInventory", 10);
            for (int i = 0; i < list.tagCount(); ++i) {
               NBTTagCompound itemTag = list.getCompoundTagAt(i);
               int slot = itemTag.getByte("Slot") & 255;
               if (slot >= 0 && slot < this.getSizeInventory()) {
                  this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
               }
            }
         }
      }

      public void writeToNBT() {
         if (satchelStack != null) {
            if (!satchelStack.hasTagCompound()) {
               satchelStack.setTagCompound(new NBTTagCompound());
            }
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < this.getSizeInventory(); ++i) {
               ItemStack item = this.getStackInSlot(i);
               if (item != null) {
                  NBTTagCompound itemTag = new NBTTagCompound();
                  itemTag.setByte("Slot", (byte)i);
                  item.writeToNBT(itemTag);
                  list.appendTag(itemTag);
               }
            }
            satchelStack.getTagCompound().setTag("SatchelInventory", list);
         }
      }

      @Override
      public void markDirty() {
         super.markDirty();
         this.writeToNBT();
      }

      @Override
      public void closeInventory() {
         this.writeToNBT();
         super.closeInventory();
      }

      @Override
      public boolean isItemValidForSlot(int slot, ItemStack stack) {
         if (stack != null && stack.getItem() instanceof ItemBottomlessSatchel) {
            return false;
         }
         return super.isItemValidForSlot(slot, stack);
      }
   }
}
