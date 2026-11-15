package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBroomStand extends BlockBaseContainer {

    public BlockBroomStand() {
        super(Material.wood, BlockBroomStand.TileEntityBroomStand.class);
        this.setHardness(2.0F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.0625F, 0.0F, 0.3125F, 0.9375F, 1.4F, 0.6875F);
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBroomStand) {
            TileEntityBroomStand stand = (TileEntityBroomStand) tile;
            ItemStack held = player.getHeldItem();

            if (stand.getBroom() == null) {
                if (held != null && isBroomItem(held)) {
                    if (!world.isRemote) {
                        ItemStack placed = held.copy();
                        placed.stackSize = 1;
                        stand.setBroom(placed);
                        --held.stackSize;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                        world.markBlockForUpdate(x, y, z);
                    }
                    return true;
                }
            } else {
                if (!world.isRemote) {
                    ItemStack retrieved = stand.getBroom();
                    stand.setBroom(null);
                    if (!player.inventory.addItemStackToInventory(retrieved)) {
                        EntityItem drop = new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, retrieved);
                        world.spawnEntityInWorld(drop);
                    }
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isBroomItem(ItemStack stack) {
        if (stack == null) return false;
        String name = stack.getUnlocalizedName().toLowerCase();
        return name.contains("broom");
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBroomStand) {
            TileEntityBroomStand stand = (TileEntityBroomStand) tile;
            ItemStack broom = stand.getBroom();
            if (broom != null && !world.isRemote) {
                EntityItem drop = new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, broom);
                world.spawnEntityInWorld(drop);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityBroomStand extends TileEntity {
        private ItemStack broom;

        public ItemStack getBroom() {
            return this.broom;
        }

        public void setBroom(ItemStack broom) {
            this.broom = broom;
            this.markDirty();
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            if (this.broom != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                this.broom.writeToNBT(itemTag);
                nbt.setTag("BroomStack", itemTag);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            if (nbt.hasKey("BroomStack")) {
                this.broom = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("BroomStack"));
            } else {
                this.broom = null;
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
        }
    }
}

