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
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGrimoireLectern extends BlockBaseContainer {

    public BlockGrimoireLectern() {
        super(Material.wood, BlockGrimoireLectern.TileEntityGrimoireLectern.class);
        this.setHardness(2.0F);
        this.setResistance(3.0F);
        this.setStepSound(Block.soundTypeWood);
        this.setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 1.0F, 0.875F);
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

    public static boolean isBookItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (Witchery.Items.GENERIC.isBook(stack)) return true;
        if (stack.getItem() == Witchery.Items.BIOME_BOOK || stack.getItem() == Witchery.Items.CAULDRON_BOOK || stack.getItem() == Witchery.Items.VAMPIRE_BOOK) return true;
        if (stack.getItem() instanceof com.emoniph.witchery.item.ItemBook || stack.getItem() instanceof com.emoniph.witchery.item.ItemMarkupBook) return true;
        if (stack.getItem() instanceof ItemBook || stack.getItem() instanceof ItemWritableBook || stack.getItem() instanceof ItemEditableBook || stack.getItem() == Items.enchanted_book) return true;
        String name = stack.getUnlocalizedName().toLowerCase();
        return name.contains("book") || name.contains("tome") || name.contains("grimoire");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityGrimoireLectern)) {
            return false;
        }
        TileEntityGrimoireLectern lectern = (TileEntityGrimoireLectern) te;
        ItemStack stored = lectern.getBook();
        ItemStack held = player.getHeldItem();

        if (stored == null) {
            if (held != null && isBookItem(held)) {
                if (!world.isRemote) {
                    ItemStack placed = held.copy();
                    placed.stackSize = 1;
                    lectern.setBook(placed);
                    if (!player.capabilities.isCreativeMode) {
                        --held.stackSize;
                        if (held.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }
                    world.playSoundEffect((double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, "step.wood", 0.8F, 1.2F);
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            }
            return false;
        } else {
            if (player.isSneaking()) {
                if (!world.isRemote) {
                    lectern.setBook(null);
                    if (!player.inventory.addItemStackToInventory(stored)) {
                        world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 1.0D, (double)z + 0.5D, stored));
                    }
                    world.playSoundEffect((double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, "random.pop", 0.5F, 1.2F);
                    world.markBlockForUpdate(x, y, z);
                }
                return true;
            } else {
                // Open the stored Witchery book directly from the lectern
                if (Witchery.Items.GENERIC.isBook(stored)) {
                    player.openGui(Witchery.instance, 1, world, x, y, z);
                    return true;
                } else if (stored.getItem() == Witchery.Items.BIOME_BOOK) {
                    player.openGui(Witchery.instance, 6, world, x, y, z);
                    return true;
                } else if (stored.getItem() instanceof com.emoniph.witchery.item.ItemMarkupBook) {
                    player.openGui(Witchery.instance, 7, world, x, y, z);
                    return true;
                }
                return true;
            }
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityGrimoireLectern) {
            TileEntityGrimoireLectern lectern = (TileEntityGrimoireLectern) te;
            ItemStack book = lectern.getBook();
            if (book != null && !world.isRemote) {
                world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 0.8D, (double)z + 0.5D, book));
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static class TileEntityGrimoireLectern extends TileEntity {
        private ItemStack book;

        public ItemStack getBook() {
            return this.book;
        }

        public void setBook(ItemStack book) {
            this.book = book;
            this.markDirty();
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            if (this.book != null) {
                NBTTagCompound bookTag = new NBTTagCompound();
                this.book.writeToNBT(bookTag);
                nbt.setTag("LecternBook", bookTag);
            }
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            if (nbt.hasKey("LecternBook")) {
                this.book = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("LecternBook"));
            } else {
                this.book = null;
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

