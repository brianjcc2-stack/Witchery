package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockApothecaryJar extends BlockBaseContainer {

    public BlockApothecaryJar() {
        super(Material.glass, BlockApothecaryJar.TileEntityApothecaryJar.class);
        this.setHardness(0.8F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.875F, 0.8125F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityApothecaryJar tile = (TileEntityApothecaryJar) world.getTileEntity(x, y, z);
        if (tile != null && tile.getCount() > 0) {
            return (int) Math.floor((double) tile.getCount() / (double) tile.getMaxCapacity() * 14.0D) + 1;
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntityApothecaryJar tile = (TileEntityApothecaryJar) world.getTileEntity(x, y, z);
        if (tile == null) {
            return false;
        }

        ItemStack held = player.getHeldItem();
        if (held != null) {
            // Deposit item
            if (tile.isEmpty() || tile.isMatching(held)) {
                if (tile.isEmpty()) {
                    tile.setStoredItem(held);
                }

                if (player.isSneaking()) {
                    // Deposit all matching from player inventory
                    int deposited = 0;
                    for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                        ItemStack invStack = player.inventory.mainInventory[i];
                        if (invStack != null && tile.isMatching(invStack)) {
                            int space = tile.getMaxCapacity() - tile.getCount();
                            if (space <= 0) break;
                            int toAdd = Math.min(space, invStack.stackSize);
                            tile.addCount(toAdd);
                            deposited += toAdd;
                            invStack.stackSize -= toAdd;
                            if (invStack.stackSize <= 0) {
                                player.inventory.mainInventory[i] = null;
                            }
                        }
                    }
                    if (deposited > 0) {
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 1.2F);
                        player.inventory.markDirty();
                        tile.markBlockForUpdate(true);
                    }
                } else {
                    // Deposit held stack
                    int space = tile.getMaxCapacity() - tile.getCount();
                    if (space > 0) {
                        int toAdd = Math.min(space, held.stackSize);
                        tile.addCount(toAdd);
                        held.stackSize -= toAdd;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 1.2F);
                        player.inventory.markDirty();
                        tile.markBlockForUpdate(true);
                    }
                }
                return true;
            }
        } else {
            // Empty hand: extract
            if (tile.getCount() > 0 && tile.getStoredItem() != null) {
                int toExtract = player.isSneaking() 
                        ? Math.min(tile.getCount(), tile.getStoredItem().getMaxStackSize()) 
                        : 1;

                ItemStack extracted = tile.getStoredItem().copy();
                extracted.stackSize = toExtract;
                tile.reduceCount(toExtract);

                if (!player.inventory.addItemStackToInventory(extracted)) {
                    EntityItem entityItem = new EntityItem(world, x + 0.5D, y + 0.6D, z + 0.5D, extracted);
                    entityItem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityItem);
                }

                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 0.9F);
                player.inventory.markDirty();
                tile.markBlockForUpdate(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (player != null && player.capabilities.isCreativeMode) {
            TileEntityApothecaryJar tile = (TileEntityApothecaryJar) world.getTileEntity(x, y, z);
            if (tile != null) {
                tile.setCreativeBreak(true);
            }
        }
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            TileEntityApothecaryJar tile = (TileEntityApothecaryJar) world.getTileEntity(x, y, z);
            if (tile != null && !tile.isCreativeBreak() && tile.getStoredItem() != null && tile.getCount() > 0) {
                int remaining = tile.getCount();
                int maxStack = Math.max(1, tile.getStoredItem().getMaxStackSize());
                while (remaining > 0) {
                    int dropCount = Math.min(remaining, maxStack);
                    ItemStack dropStack = tile.getStoredItem().copy();
                    dropStack.stackSize = dropCount;
                    remaining -= dropCount;

                    float fx = world.rand.nextFloat() * 0.6F + 0.2F;
                    float fy = world.rand.nextFloat() * 0.6F + 0.2F;
                    float fz = world.rand.nextFloat() * 0.6F + 0.2F;
                    EntityItem entityItem = new EntityItem(world, (double)((float) x + fx), (double)((float) y + fy), (double)((float) z + fz), dropStack);
                    float f3 = 0.05F;
                    entityItem.motionX = (double)((float) world.rand.nextGaussian() * f3);
                    entityItem.motionY = (double)((float) world.rand.nextGaussian() * f3 + 0.2F);
                    entityItem.motionZ = (double)((float) world.rand.nextGaussian() * f3);
                    entityItem.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(entityItem);
                }
                tile.setStoredItem(null);
                world.func_147453_f(x, y, z, block);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityApothecaryJar extends TileEntityBase {

        private static final int MAX_CAPACITY = 512;
        private ItemStack storedItem;
        private int count;
        private boolean creativeBreak = false;

        public boolean isCreativeBreak() {
            return this.creativeBreak;
        }

        public void setCreativeBreak(boolean creativeBreak) {
            this.creativeBreak = creativeBreak;
        }

        public boolean isEmpty() {
            return this.storedItem == null || this.count <= 0;
        }

        public ItemStack getStoredItem() {
            return this.storedItem;
        }

        public void setStoredItem(ItemStack stack) {
            if (stack == null) {
                this.storedItem = null;
                this.count = 0;
            } else {
                this.storedItem = stack.copy();
                this.storedItem.stackSize = 1;
            }
            this.markDirty();
        }

        public int getCount() {
            return this.count;
        }

        public int getMaxCapacity() {
            return MAX_CAPACITY;
        }

        public void addCount(int amount) {
            this.count = Math.min(MAX_CAPACITY, this.count + amount);
            this.markDirty();
        }

        public void reduceCount(int amount) {
            this.count = Math.max(0, this.count - amount);
            if (this.count <= 0) {
                this.storedItem = null;
            }
            this.markDirty();
        }

        public boolean isMatching(ItemStack other) {
            if (this.storedItem == null || other == null) {
                return false;
            }
            if (this.storedItem.getItem() != other.getItem()) {
                return false;
            }
            if (this.storedItem.getItemDamage() != other.getItemDamage()) {
                return false;
            }
            if (this.storedItem.hasTagCompound() || other.hasTagCompound()) {
                return ItemStack.areItemStackTagsEqual(this.storedItem, other);
            }
            return true;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            nbt.setInteger("JarCount", this.count);
            if (this.storedItem != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                this.storedItem.writeToNBT(itemTag);
                nbt.setTag("JarStoredItem", itemTag);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            this.count = nbt.getInteger("JarCount");
            if (nbt.hasKey("JarStoredItem")) {
                this.storedItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("JarStoredItem"));
                if (this.storedItem != null) {
                    this.storedItem.stackSize = 1;
                }
            } else {
                this.storedItem = null;
            }
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeToNBT(nbt);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
        }

        @Override
        public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
            super.onDataPacket(net, packet);
            this.readFromNBT(packet.func_148857_g());
            if (this.worldObj != null) {
                this.worldObj.func_147479_m(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }
}
