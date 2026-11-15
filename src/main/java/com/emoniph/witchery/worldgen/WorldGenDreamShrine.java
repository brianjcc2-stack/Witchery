package com.emoniph.witchery.worldgen;

import com.emoniph.witchery.Witchery;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDreamShrine extends WorldGenerator {

    public static final WeightedRandomChestContent[] dreamShrineChestContents = new WeightedRandomChestContent[]{
        new WeightedRandomChestContent(Witchery.Items.GENERIC, Witchery.Items.GENERIC.itemDisturbedCotton.damageValue, 2, 6, 25),
        new WeightedRandomChestContent(Witchery.Items.GENERIC, Witchery.Items.GENERIC.itemMellifluousHunger.damageValue, 1, 3, 20),
        new WeightedRandomChestContent(Witchery.Items.GENERIC, Witchery.Items.GENERIC.itemSubduedSpirit.damageValue, 1, 2, 15),
        new WeightedRandomChestContent(Witchery.Items.GENERIC, Witchery.Items.GENERIC.itemSpectralDust.damageValue, 2, 5, 20),
        new WeightedRandomChestContent(Witchery.Items.GENERIC, Witchery.Items.GENERIC.itemTearOfTheGoddess.damageValue, 1, 2, 10),
        new WeightedRandomChestContent(Items.ender_pearl, 0, 1, 3, 15),
        new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 10),
        new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 6, 15),
        new WeightedRandomChestContent(Items.experience_bottle, 0, 2, 5, 20)
    };

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        while (y > 10 && world.isAirBlock(x, y, z)) {
            --y;
        }

        Block ground = world.getBlock(x, y, z);
        if (ground == Blocks.air || ground == Blocks.water || ground == Blocks.flowing_water) {
            return false;
        }

        int groundY = y;

        // Plataforma circular de cuarzo 5x5
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dz = -2; dz <= 2; ++dz) {
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    continue;
                }
                world.setBlock(x + dx, groundY, z + dz, Blocks.quartz_block, 0, 2);
                for (int dy = -1; dy >= -3; --dy) {
                    if (world.isAirBlock(x + dx, groundY + dy, z + dz)) {
                        world.setBlock(x + dx, groundY + dy, z + dz, Blocks.stone, 0, 2);
                    }
                }
                for (int dy = 1; dy <= 4; ++dy) {
                    world.setBlockToAir(x + dx, groundY + dy, z + dz);
                }
            }
        }

        // Columnas en 4 puntos cardinales
        int[][] pillarOffsets = new int[][]{{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] p : pillarOffsets) {
            int px = x + p[0];
            int pz = z + p[1];
            world.setBlock(px, groundY + 1, pz, Blocks.quartz_block, 2, 2);
            world.setBlock(px, groundY + 2, pz, Blocks.quartz_block, 2, 2);
            world.setBlock(px, groundY + 3, pz, Witchery.Blocks.TORMENT_STONE != null ? Witchery.Blocks.TORMENT_STONE : Blocks.obsidian, 0, 2);
        }

        // Centro: pedestal y cofre astral
        world.setBlock(x, groundY + 1, z, Blocks.quartz_block, 1, 2);
        world.setBlock(x, groundY + 2, z, Blocks.chest, 0, 2);

        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, groundY + 2, z);
        if (chest != null) {
            WeightedRandomChestContent.generateChestContents(rand, dreamShrineChestContents, chest, 3 + rand.nextInt(4));
        }

        // Decoración etérea alrededor
        world.setBlock(x - 1, groundY + 1, z, Witchery.Blocks.GLINT_WEED, 0, 2);
        world.setBlock(x + 1, groundY + 1, z, Witchery.Blocks.GLINT_WEED, 0, 2);
        world.setBlock(x, groundY + 1, z - 1, Witchery.Blocks.WISPY_COTTON, 0, 2);
        world.setBlock(x, groundY + 1, z + 1, Witchery.Blocks.WISPY_COTTON, 0, 2);

        // Pequeño manantial de espíritu fluyente al lado si hay espacio
        if (world.isAirBlock(x + 3, groundY + 1, z)) {
            world.setBlock(x + 3, groundY, z, Blocks.quartz_block, 0, 2);
            world.setBlock(x + 3, groundY + 1, z, Witchery.Blocks.FLOWING_SPIRIT, 0, 2);
        }

        return true;
    }
}
