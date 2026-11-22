package com.emoniph.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.WitcheryCreativeTab;
import com.emoniph.witchery.brewing.WitcheryBrewRegistry;
import com.emoniph.witchery.util.ItemUtil;
import com.emoniph.witchery.util.SoundEffect;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

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
      private ItemStack pouchStack;
      private final EntityPlayer player;
      private final int heldSlot;
      private boolean reading = false;

      public InventoryApothecary(ItemStack stack, EntityPlayer player) {
         super(stack != null && stack.hasDisplayName() ? stack.getDisplayName() : StatCollector.translateToLocal("item.witchery:apothecarypouch.name"), stack != null && stack.hasDisplayName(), 27);
         this.pouchStack = stack;
         this.player = player;
         this.heldSlot = player != null && player.inventory != null ? player.inventory.currentItem : -1;
         this.readFromNBT();
      }

      private ItemStack resolveActivePouchStack() {
         if (this.player != null && this.player.inventory != null) {
            if (this.heldSlot >= 0 && this.heldSlot < this.player.inventory.mainInventory.length) {
               ItemStack slotStack = this.player.inventory.mainInventory[this.heldSlot];
               if (slotStack != null && slotStack.getItem() instanceof ItemApothecaryPouch) {
                  this.pouchStack = slotStack;
                  return slotStack;
               }
            }
            ItemStack current = this.player.getCurrentEquippedItem();
            if (current != null && current.getItem() instanceof ItemApothecaryPouch) {
               this.pouchStack = current;
               return current;
            }
         }
         return this.pouchStack;
      }

      public void readFromNBT() {
         this.reading = true;
         try {
            ItemStack activeStack = this.resolveActivePouchStack();
            if (activeStack != null && activeStack.hasTagCompound() && activeStack.getTagCompound().hasKey("PouchInventory")) {
               NBTTagList list = activeStack.getTagCompound().getTagList("PouchInventory", 10);
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

         ItemStack activeStack = this.resolveActivePouchStack();
         if (activeStack != null) {
            if (!activeStack.hasTagCompound()) {
               activeStack.setTagCompound(new NBTTagCompound());
            }
            activeStack.getTagCompound().setTag("PouchInventory", list);
         }
         if (this.pouchStack != null && this.pouchStack != activeStack) {
            if (!this.pouchStack.hasTagCompound()) {
               this.pouchStack.setTagCompound(new NBTTagCompound());
            }
            this.pouchStack.getTagCompound().setTag("PouchInventory", list.copy());
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
         if (stack == null || stack.getItem() == null) return false;
         Item item = stack.getItem();
         if (item instanceof ItemApothecaryPouch || item instanceof ItemBottomlessSatchel) {
            return false;
         }
         if (item instanceof ItemPotion || item == Items.glass_bottle || item == Items.potionitem) return true;
         if (item == Witchery.Items.BREW || Witchery.Items.GENERIC.isBrew(stack)) return true;
         if (item instanceof ItemWitchSeeds || item instanceof ItemChalk || item instanceof ItemGeneral || item instanceof ItemSeeds || item instanceof IPlantable) return true;
         if (item instanceof ItemPoppet || item instanceof ItemTaglockKit || item instanceof ItemBrewBag || item instanceof ItemLeonardsUrn || item instanceof ItemBoline) return true;
         if (item == Item.getItemFromBlock(Witchery.Blocks.CHALICE) || item == Witchery.Items.ARTHANA) return true;
         if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            if (block instanceof IPlantable || block instanceof BlockBush || block instanceof BlockFlower || block instanceof BlockLeavesBase || block instanceof BlockVine || block instanceof BlockLilyPad) {
               return true;
            }
            if (block == Witchery.Blocks.APOTHECARY_JAR || block == Witchery.Blocks.SPANISH_MOSS || block == Witchery.Blocks.WISPY_COTTON || block == Witchery.Blocks.GLINT_WEED || block == Witchery.Blocks.EMBER_MOSS || block == Witchery.Blocks.CRITTER_SNARE || block == Witchery.Blocks.BLOOD_ROSE || block == Witchery.Blocks.GRAVE_ROSE || block == Witchery.Blocks.SUN_ROSE || block == Witchery.Blocks.MOON_ROSE || block == Witchery.Blocks.BANSHEE_ROSE || block == Witchery.Blocks.HECATES_TEARS || block == Witchery.Blocks.GRASSPER) {
               return true;
            }
         }
         String regName = Item.itemRegistry.getNameForObject(item);
         if (regName != null && regName.startsWith("witchery:")) {
            return true;
         }
         // Vanilla plants, alchemy herbs, reagents and mob drops used in brewing/cauldron
         if (item == Items.nether_wart || item == Items.spider_eye || item == Items.fermented_spider_eye || item == Items.blaze_powder || item == Items.blaze_rod || item == Items.magma_cream || item == Items.ghast_tear || item == Items.speckled_melon || item == Items.golden_carrot || item == Items.sugar || item == Items.redstone || item == Items.glowstone_dust || item == Items.gunpowder || item == Items.slime_ball || item == Items.bone || item == Items.rotten_flesh || item == Items.ender_pearl || item == Items.ender_eye || item == Items.dye || item == Items.coal || item == Items.feather || item == Items.string || item == Items.clay_ball || item == Items.flint || item == Items.quartz || item == Items.gold_nugget || item == Items.egg || item == Items.milk_bucket || item == Items.water_bucket || item == Items.wheat || item instanceof ItemFood) {
            return true;
         }
         return false;
      }
   }

   private static class SlotApothecary extends Slot {
      public SlotApothecary(IInventory inventory, int slotIndex, int x, int y) {
         super(inventory, slotIndex, x, y);
      }

      @Override
      public boolean isItemValid(ItemStack stack) {
         return this.inventory.isItemValidForSlot(this.getSlotIndex(), stack);
      }
   }

   public static class ContainerApothecary extends ContainerChest {
      private final ItemStack pouchStack;
      private final IInventory pouchInventory;
      private final int protectedSlotIndex;

      public ContainerApothecary(IInventory playerInventory, IInventory pouchInventory, ItemStack pouchStack) {
         super(playerInventory, pouchInventory);
         this.pouchStack = pouchStack;
         this.pouchInventory = pouchInventory;
         this.protectedSlotIndex = (playerInventory instanceof net.minecraft.entity.player.InventoryPlayer)
               ? ((net.minecraft.entity.player.InventoryPlayer) playerInventory).currentItem
               : -1;

         int bagSize = pouchInventory.getSizeInventory();
         for (int i = 0; i < bagSize && i < this.inventorySlots.size(); ++i) {
            Slot oldSlot = (Slot) this.inventorySlots.get(i);
            SlotApothecary newSlot = new SlotApothecary(pouchInventory, oldSlot.getSlotIndex(), oldSlot.xDisplayPosition, oldSlot.yDisplayPosition);
            newSlot.slotNumber = oldSlot.slotNumber;
            this.inventorySlots.set(i, newSlot);
         }
      }

      @Override
      public boolean canInteractWith(EntityPlayer player) {
         ItemStack held = player.getCurrentEquippedItem();
         return held != null && held.getItem() instanceof ItemApothecaryPouch;
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
               if (slot.getHasStack() && (slot.getStack() == held || (slot.getStack().getItem() instanceof ItemApothecaryPouch && slot.inventory == player.inventory && slot.getSlotIndex() == player.inventory.currentItem))) {
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
            this.pouchInventory.markDirty();
         }
         return returnStack;
      }
   }
}
