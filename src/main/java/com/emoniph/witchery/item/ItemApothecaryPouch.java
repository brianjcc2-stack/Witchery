package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.brewing.WitcheryBrewRegistry;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemApothecaryPouch extends Item {

   public ItemApothecaryPouch() {
      this.setMaxStackSize(1);
      this.setCreativeTab(WitcheryCreativeTab.INSTANCE);
   }

   public Item setUnlocalizedName(String itemName) {
      ItemUtil.registerItem(this, itemName);
      return super.setUnlocalizedName(itemName);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         player.openGui(Witchery.instance, 9, world, 0, 0, 0);
         SoundEffect.WITCHERY_RANDOM_DRINK.playOnlyTo(player, 0.8F, 1.4F);
      }
      return stack;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public EnumRarity getRarity(ItemStack stack) {
      return EnumRarity.rare;
   }

   @SideOnly(Side.CLIENT)
   @Override
   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
      list.add(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("witchery.apothecarypouch.tip1"));
      list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("witchery.apothecarypouch.tip2"));
   }

   public static class InventoryApothecary extends InventoryBasic {
      private final ItemStack pouchStack;
      private final EntityPlayer player;

      public InventoryApothecary(ItemStack stack, EntityPlayer player) {
         super(stack.hasDisplayName() ? stack.getDisplayName() : StatCollector.translateToLocal("item.witchery:apothecarypouch.name"), stack.hasDisplayName(), 27);
         this.pouchStack = stack;
         this.player = player;
         this.readFromNBT();
      }

      public void readFromNBT() {
         if (pouchStack != null && pouchStack.hasTagCompound() && pouchStack.getTagCompound().hasKey("PouchInventory")) {
            NBTTagList list = pouchStack.getTagCompound().getTagList("PouchInventory", 10);
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
         if (pouchStack != null) {
            if (!pouchStack.hasTagCompound()) {
               pouchStack.setTagCompound(new NBTTagCompound());
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
            pouchStack.getTagCompound().setTag("PouchInventory", list);
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
         if (stack == null || stack.getItem() == null) return false;
         if (stack.getItem() instanceof ItemApothecaryPouch || stack.getItem() instanceof ItemBottomlessSatchel) {
            return false;
         }
         Item item = stack.getItem();
         if (item instanceof ItemPotion || item == Items.glass_bottle || item == Items.potionitem) return true;
         if (item == Witchery.Items.BREW || Witchery.Items.GENERIC.isBrew(stack)) return true;
         if (item instanceof ItemWitchSeeds || item instanceof ItemChalk || item instanceof ItemGeneral) return true;
         if (item == Item.getItemFromBlock(Witchery.Blocks.CHALICE) || item == Witchery.Items.ARTHANA) return true;
         // Plants, alchemy herbs and mob drops used in brewing
         if (item == Items.nether_wart || item == Items.spider_eye || item == Items.fermented_spider_eye || item == Items.blaze_powder || item == Items.magma_cream || item == Items.ghast_tear || item == Items.speckled_melon || item == Items.golden_carrot || item == Items.sugar || item == Items.redstone || item == Items.glowstone_dust || item == Items.gunpowder || item == Items.slime_ball) return true;
         return false;
      }
   }

   public static class ContainerApothecary extends ContainerChest {
      private final ItemStack pouchStack;
      private final IInventory pouchInventory;

      public ContainerApothecary(IInventory playerInventory, IInventory pouchInventory, ItemStack pouchStack) {
         super(playerInventory, pouchInventory);
         this.pouchStack = pouchStack;
         this.pouchInventory = pouchInventory;
      }

      @Override
      public boolean canInteractWith(EntityPlayer player) {
         ItemStack held = player.getCurrentEquippedItem();
         return held != null && held.getItem() instanceof ItemApothecaryPouch;
      }

      @Override
      public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
         ItemStack held = player.getCurrentEquippedItem();
         if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = (Slot) this.inventorySlots.get(slotId);
            if (slot != null && slot.getHasStack() && slot.getStack() == held) {
               return null;
            }
         }
         if (mode == 2 && clickedButton == player.inventory.currentItem) {
            return null;
         }
         return super.slotClick(slotId, clickedButton, mode, player);
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
         ItemStack returnStack = null;
         Slot slot = (Slot) this.inventorySlots.get(slotIndex);
         if (slot != null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            returnStack = itemStack.copy();
            int bagSize = this.pouchInventory.getSizeInventory();

            if (slotIndex < bagSize) {
               if (!this.mergeItemStack(itemStack, bagSize, this.inventorySlots.size(), true)) {
                  return null;
               }
            } else {
               if (itemStack == player.getCurrentEquippedItem() || !this.pouchInventory.isItemValidForSlot(0, itemStack)) {
                  return null;
               }
               if (!this.mergeItemStack(itemStack, 0, bagSize, false)) {
                  return null;
               }
            }

            if (itemStack.stackSize == 0) {
               slot.putStack(null);
            } else {
               slot.onSlotChanged();
            }
         }
         return returnStack;
      }
   }
}
