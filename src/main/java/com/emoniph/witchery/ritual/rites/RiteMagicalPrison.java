package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.blocks.BlockAltar;
import com.emoniph.witchery.blocks.BlockBarrier;
import com.emoniph.witchery.blocks.BlockCircle;
import com.emoniph.witchery.common.IPowerSource;
import com.emoniph.witchery.common.PowerSources;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RiteMagicalPrison extends Rite {

    private final int radius;
    private final int height;
    private final float upkeepPowerCost;

    public RiteMagicalPrison(int radius, int height, float upkeepPowerCost) {
        this.radius = radius;
        this.height = height;
        this.upkeepPowerCost = upkeepPowerCost;
    }

    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepMagicalPrison(this, initialStage));
    }

    private static class StepMagicalPrison extends RitualStep {
        private final RiteMagicalPrison rite;
        private boolean activated = false;
        private Coord powerSourceCoord;

        public StepMagicalPrison(RiteMagicalPrison rite, int initialStage) {
            super(true);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int posX, int posY, int posZ, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual ritual) {
            if (!this.activated) {
                if (ticks % 20L != 0L) {
                    return RitualStep.Result.STARTING;
                }
                this.activated = true;
                SoundEffect.RANDOM_FIZZ.playAt(world, super.sourceX, super.sourceY, super.sourceZ);
            }

            if (this.rite.upkeepPowerCost > 0.0F) {
                IPowerSource powerSource = this.getPowerSource(world, super.sourceX, super.sourceY, super.sourceZ);
                if (powerSource == null) {
                    return RitualStep.Result.ABORTED;
                }

                this.powerSourceCoord = powerSource.getLocation();
                if (!powerSource.consumePower(this.rite.upkeepPowerCost)) {
                    return RitualStep.Result.ABORTED;
                }
            }

            if (ticks % 20L == 0L) {
                if (!world.isRemote) {
                    drawBox(world, posX, posY, posZ, this.rite.radius, this.rite.height);
                    if (world.rand.nextInt(3) == 0) {
                        ParticleEffect.ENCHANTMENT_TABLE.send(SoundEffect.NONE, world, posX + 0.5, posY + 0.5, posZ + 0.5, 1.0, 1.0, 16);
                    }
                }
            }

            return RitualStep.Result.UPKEEP;
        }

        private void drawBox(World world, int cx, int cy, int cz, int r, int h) {
            int yMin = cy - 1;
            int yMax = cy + h;

            for (int x = cx - r; x <= cx + r; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = cz - r; z <= cz + r; z++) {
                        if (y == yMin || y == yMax || x == cx - r || x == cx + r || z == cz - r || z == cz + r) {
                            Block blockID = world.getBlock(x, y, z);
                            boolean isBarrier = blockID == Witchery.Blocks.BARRIER;
                            if (blockID == Blocks.air || blockID.getMaterial().isReplaceable() || isBarrier) {
                                BlockBarrier.setBlock(world, x, y, z, 30, true, (EntityPlayer) null, isBarrier);
                            }
                        }
                    }
                }
            }
        }

        private IPowerSource getPowerSource(World world, int posX, int posY, int posZ) {
            if (this.powerSourceCoord != null && world.rand.nextInt(5) != 0) {
                TileEntity tileEntity = this.powerSourceCoord.getBlockTileEntity(world);
                if (!(tileEntity instanceof BlockAltar.TileEntityAltar)) {
                    return this.findNewPowerSource(world, posX, posY, posZ);
                } else {
                    BlockAltar.TileEntityAltar altarTileEntity = (BlockAltar.TileEntityAltar) tileEntity;
                    return !altarTileEntity.isValid() ? this.findNewPowerSource(world, posX, posY, posZ) : altarTileEntity;
                }
            } else {
                return this.findNewPowerSource(world, posX, posY, posZ);
            }
        }

        private IPowerSource findNewPowerSource(World world, int posX, int posY, int posZ) {
            ArrayList sources = PowerSources.instance() != null ? PowerSources.instance().get(world, new Coord(posX, posY, posZ), 16) : null;
            return sources != null && sources.size() > 0 ? ((PowerSources.RelativePowerSource) sources.get(0)).source() : null;
        }
    }
}
