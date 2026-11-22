package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.util.ChatUtil;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
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
         player.openGui(Witchery.instance, 10, world, 0, 0, 0);
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
      private ItemStack satchelStack;
      private final EntityPlayer player;
      private final int heldSlot;
      private boolean reading = false;

      public InventorySatchel(ItemStack stack, EntityPlayer player) {
         super(stack != null && stack.hasDisplayName() ? stack.getDisplayName() : StatCollector.translateToLocal("item.witchery:bottomlesssatchel.name"), stack != null && stack.hasDisplayName(), 54);
         this.satchelStack = stack;
         this.player = player;
         this.heldSlot = player != null && player.inventory != null ? player.inventory.currentItem : -1;
         this.readFromNBT();
      }

      private ItemStack resolveActiveSatchelStack() {
         if (this.satchelStack != null && this.player != null && this.player.inventory != null) {
            for (int i = 0; i < this.player.inventory.mainInventory.length; ++i) {
               if (this.player.inventory.mainInventory[i] == this.satchelStack) {
                  return this.satchelStack;
               }
            }
            if (this.heldSlot >= 0 && this.heldSlot < this.player.inventory.mainInventory.length) {
               ItemStack slotStack = this.player.inventory.mainInventory[this.heldSlot];
               if (slotStack != null && slotStack.getItem() instanceof ItemBottomlessSatchel) {
                  this.satchelStack = slotStack;
                  return slotStack;
               }
            }
            ItemStack current = this.player.getCurrentEquippedItem();
            if (current != null && current.getItem() instanceof ItemBottomlessSatchel) {
               this.satchelStack = current;
               return current;
            }
         }
         return this.satchelStack;
      }

      public void readFromNBT() {
         this.reading = true;
         try {
            ItemStack activeStack = this.resolveActiveSatchelStack();
            if (activeStack != null && activeStack.hasTagCompound() && activeStack.getTagCompound().hasKey("SatchelInventory")) {
               NBTTagList list = activeStack.getTagCompound().getTagList("SatchelInventory", 10);
               for (int i = 0; i < list.tagCount(); ++i) {
                  NBTTagCompound itemTag = list.getCompoundTagAt(i);
                  int slot = itemTag.getByte("Slot") & 255;
                  if (slot >= 0 && slot < this.getSizeInventory()) {
                     this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
                  }
               }
            }
         } finally {
            this.reading = false;
         }
      }

      public void writeToNBT() {
         if (this.reading) {
            return;
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

         ItemStack activeStack = this.resolveActiveSatchelStack();
         if (activeStack != null) {
            if (!activeStack.hasTagCompound()) {
               activeStack.setTagCompound(new NBTTagCompound());
            }
            activeStack.getTagCompound().setTag("SatchelInventory", list);
         }
         if (this.satchelStack != null && this.satchelStack != activeStack) {
            if (!this.satchelStack.hasTagCompound()) {
               this.satchelStack.setTagCompound(new NBTTagCompound());
            }
            this.satchelStack.getTagCompound().setTag("SatchelInventory", list.copy());
         }
      }

      @Override
      public void markDirty() {
         super.markDirty();
         if (!this.reading) {
            this.writeToNBT();
         }
      }

      @Override
      public void closeInventory() {
         if (!this.reading) {
            this.writeToNBT();
         }
         super.closeInventory();
      }

      @Override
      public boolean isItemValidForSlot(int slot, ItemStack stack) {
         if (stack != null && (stack.getItem() instanceof ItemBottomlessSatchel || stack.getItem() instanceof ItemApothecaryPouch)) {
            return false;
         }
         return super.isItemValidForSlot(slot, stack);
      }
   }

   private static class SlotSatchel extends Slot {
      public SlotSatchel(IInventory inventory, int slotIndex, int x, int y) {
         super(inventory, slotIndex, x, y);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
      }
   }

   public static class ContainerSatchel extends ContainerChest {
      private final ItemStack satchelStack;
      private final IInventory satchelInventory;
      private final int protectedSlotIndex;

      public ContainerSatchel(IInventory playerInventory, IInventory satchelInventory, ItemStack satchelStack) {
         super(playerInventory, satchelInventory);
         this.satchelStack = satchelStack;
         this.satchelInventory = satchelInventory;
         this.protectedSlotIndex = (playerInventory instanceof net.minecraft.entity.player.InventoryPlayer)
               ? ((net.minecraft.entity.player.InventoryPlayer) playerInventory).currentItem
               : -1;

         int bagSize = satchelInventory.getSizeInventory();
         for (int i = 0; i < bagSize && i < this.inventorySlots.size(); ++i) {
            Slot oldSlot = (Slot) this.inventorySlots.get(i);
            SlotSatchel newSlot = new SlotSatchel(satchelInventory, oldSlot.getSlotIndex(), oldSlot.xDisplayPosition, oldSlot.yDisplayPosition);
            newSlot.slotNumber = oldSlot.slotNumber;
            this.inventorySlots.set(i, newSlot);
         }
      }

      @Override
      public boolean canInteractWith(EntityPlayer player) {
         ItemStack held = player.getCurrentEquippedItem();
         return held != null && held.getItem() instanceof ItemBottomlessSatchel;
      }

      @Override
      public void onContainerClosed(EntityPlayer player) {
         super.onContainerClosed(player);
         if (player != null && player.inventory != null) {
            player.inventory.markDirty();
            if (!player.worldObj.isRemote && player instanceof EntityPlayerMP) {
               ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
            }
         }
      }

      @Override
      public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
         ItemStack held = player.getCurrentEquippedItem();
         if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = (Slot) this.inventorySlots.get(slotId);
            if (slot != null) {
               if (slot.inventory == player.inventory && (slot.getSlotIndex() == player.inventory.currentItem || slot.getSlotIndex() == this.protectedSlotIndex)) {
                  return null;
               }
               if (slot.getHasStack() && (slot.getStack() == held || (slot.getStack().getItem() instanceof ItemBottomlessSatchel && slot.inventory == player.inventory && slot.getSlotIndex() == player.inventory.currentItem))) {
                  return null;
               }
            }
         }
         if (mode == 2 && (clickedButton == player.inventory.currentItem || clickedButton == this.protectedSlotIndex)) {
            return null;
         }
         return super.slotClick(slotId, clickedButton, mode, player);
      }

      @Override
      public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
         ItemStack returnStack = null;
         Slot slot = (Slot) this.inventorySlots.get(slotIndex);
         if (slot != null && slot.getHasStack()) {
            if (slot.inventory == player.inventory && (slot.getSlotIndex() == player.inventory.currentItem || slot.getSlotIndex() == this.protectedSlotIndex)) {
               return null;
            }
            ItemStack itemStack = slot.getStack();
            returnStack = itemStack.copy();
            int bagSize = this.satchelInventory.getSizeInventory();

            if (slotIndex < bagSize) {
               if (!this.mergeItemStack(itemStack, bagSize, this.inventorySlots.size(), true)) {
                  return null;
               }
            } else {
               if (itemStack == player.getCurrentEquippedItem() || !this.satchelInventory.isItemValidForSlot(0, itemStack)) {
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
            this.satchelInventory.markDirty();
         }
         return returnStack;
      }
   }
}
