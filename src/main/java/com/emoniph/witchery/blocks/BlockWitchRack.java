package com.emoniph.witchery.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWitchRack extends BlockBaseContainer {

    public BlockWitchRack() {
        super(Material.wood, BlockWitchRack.TileEntityWitchRack.class);
        this.setHardness(2.0F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.15F, 0.0F, 0.15F, 0.85F, 1.5F, 0.85F);
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityWitchRack)) {
            return false;
        }
        TileEntityWitchRack rack = (TileEntityWitchRack) te;
        ItemStack held = player.getHeldItem();

        if (!player.isSneaking() && held != null) {
            int emptySlot = rack.findFirstEmptySlot();
            if (emptySlot >= 0) {
                if (!world.isRemote) {
                    ItemStack placed = held.copy();
                    placed.stackSize = 1;
                    rack.setItem(emptySlot, placed);
                    if (!player.capabilities.isCreativeMode) {
                        --held.stackSize;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                    world.playSoundEffect((double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, "step.cloth", 0.8F, 1.2F);
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }
        }

        int occupiedSlot = rack.findLastOccupiedSlot();
        if (occupiedSlot >= 0) {
            if (!world.isRemote) {
                ItemStack retrieved = rack.getItem(occupiedSlot);
                rack.setItem(occupiedSlot, null);
                if (!player.inventory.addItemStackToInventory(retrieved)) {
                    world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, retrieved));
                }
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, "random.pop", 0.5F, 1.2F);
                world.markBlockForUpdate(x, y, z);
            }
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityWitchRack) {
            TileEntityWitchRack rack = (TileEntityWitchRack) te;
            if (!world.isRemote) {
                for (int i = 0; i < TileEntityWitchRack.MAX_SLOTS; ++i) {
                    ItemStack stack = rack.getItem(i);
                    if (stack != null) {
                        world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, stack));
                    }
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityWitchRack extends TileEntity {
        public static final int MAX_SLOTS = 4;
        private final ItemStack[] items = new ItemStack[MAX_SLOTS];

        public ItemStack getItem(int slot) {
            return (slot >= 0 && slot < MAX_SLOTS) ? this.items[slot] : null;
        }

        public void setItem(int slot, ItemStack stack) {
            if (slot >= 0 && slot < MAX_SLOTS) {
                this.items[slot] = stack;
                this.markDirty();
            }
        }

        public int findFirstEmptySlot() {
            for (int i = 0; i < MAX_SLOTS; ++i) {
                if (this.items[i] == null) return i;
            }
            return -1;
        }

        public int findLastOccupiedSlot() {
            for (int i = MAX_SLOTS - 1; i >= 0; --i) {
                if (this.items[i] != null) return i;
            }
            return -1;
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < MAX_SLOTS; ++i) {
                if (this.items[i] != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setByte("Slot", (byte)i);
                    this.items[i].writeToNBT(tag);
                    list.appendTag(tag);
                }
            }
            nbt.setTag("RackItems", list);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            for (int i = 0; i < MAX_SLOTS; ++i) {
                this.items[i] = null;
            }
            if (nbt.hasKey("RackItems")) {
                NBTTagList list = nbt.getTagList("RackItems", 10);
                for (int i = 0; i < list.tagCount(); ++i) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    int slot = tag.getByte("Slot") & 255;
                    if (slot >= 0 && slot < MAX_SLOTS) {
                        this.items[slot] = ItemStack.loadItemStackFromNBT(tag);
                    }
                }
            }
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeToNBT(nbt);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
        }

        @Override
        public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
            this.readFromNBT(pkt.func_148857_g());
            if (this.worldObj != null) {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }
}

