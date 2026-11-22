package com.emoniph.witchery.blocks;

import com.emoniph.witchery.Witchery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
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

public class BlockPotionShelf extends BlockBaseContainer {

    public BlockPotionShelf() {
        super(Material.wood, BlockPotionShelf.TileEntityPotionShelf.class);
        this.setHardness(2.0F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.0F, 0.0F, 0.2F, 1.0F, 1.0F, 0.8F);
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 3;
        if (meta == 1 || meta == 3) {
            this.setBlockBounds(0.2F, 0.0F, 0.0F, 0.8F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.2F, 1.0F, 1.0F, 0.8F);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
    }

    public static boolean isPotionOrBrewItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() instanceof ItemPotion || stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle || stack.getItem() == Items.experience_bottle) return true;
        if (stack.getItem() == Witchery.Items.BREW || stack.getItem() == Witchery.Items.BREW_FUEL || stack.getItem() == Witchery.Items.BREW_ENDLESS_WATER) return true;
        if (Witchery.Items.GENERIC.isBrew(stack) || stack.getItem() == Witchery.Items.GENERIC) return true;
        String name = stack.getUnlocalizedName().toLowerCase();
        return name.contains("potion") || name.contains("brew") || name.contains("jar") || name.contains("bottle") || name.contains("vial");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityPotionShelf)) {
            return false;
        }
        TileEntityPotionShelf shelf = (TileEntityPotionShelf) te;
        ItemStack held = player.getHeldItem();

        if (!player.isSneaking() && held != null && isPotionOrBrewItem(held)) {
            int emptySlot = shelf.findFirstEmptySlot();
            if (emptySlot >= 0) {
                if (!world.isRemote) {
                    ItemStack placed = held.copy();
                    placed.stackSize = 1;
                    shelf.setItem(emptySlot, placed);
                    if (!player.capabilities.isCreativeMode) {
                        --held.stackSize;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                    world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "step.glass", 0.7F, 1.3F);
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }
        }

        int occupiedSlot = shelf.findLastOccupiedSlot();
        if (occupiedSlot >= 0) {
            if (!world.isRemote) {
                ItemStack retrieved = shelf.getItem(occupiedSlot);
                shelf.setItem(occupiedSlot, null);
                if (!player.inventory.addItemStackToInventory(retrieved)) {
                    world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, retrieved));
                }
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.pop", 0.5F, 1.2F);
                world.markBlockForUpdate(x, y, z);
            }
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPotionShelf) {
            TileEntityPotionShelf shelf = (TileEntityPotionShelf) te;
            if (!world.isRemote) {
                for (int i = 0; i < TileEntityPotionShelf.MAX_SLOTS; ++i) {
                    ItemStack stack = shelf.getItem(i);
                    if (stack != null) {
                        world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, stack));
                    }
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityPotionShelf extends TileEntity {
        public static final int MAX_SLOTS = 6;
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
            nbt.setTag("ShelfItems", list);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            for (int i = 0; i < MAX_SLOTS; ++i) {
                this.items[i] = null;
            }
            if (nbt.hasKey("ShelfItems")) {
                NBTTagList list = nbt.getTagList("ShelfItems", 10);
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

