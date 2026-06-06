package com.emoniph.witchery.ritual.rites;

import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.blocks.BlockCircle;
import net.minecraft.world.World;
import java.util.ArrayList;

public class RiteAnnihilation extends Rite {
    @Override
    public void addSteps(ArrayList steps, int initialStage) {
        steps.add(new StepRiteAnnihilation(this, initialStage));
    }

    private static class StepRiteAnnihilation extends RitualStep {
        private final RiteAnnihilation rite;

        public StepRiteAnnihilation(RiteAnnihilation rite, int initialStage) {
            super(false);
            this.rite = rite;
        }

        @Override
        public RitualStep.Result process(World world, int x, int y, int z, long ticks, BlockCircle.TileEntityCircle.ActivatedRitual circleType) {
            // Custom ritual logic
            return RitualStep.Result.COMPLETED;
        }
    }
}
