package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pokeregions.PokemonRegions;

public class BlockUpMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID("BlockUpMod");

    private final int blockIncrease;

    public BlockUpMod(int amount) {
        this.blockIncrease = amount;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BlockUpMod(blockIncrease);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card.baseBlock >= 0) {
            card.baseBlock += blockIncrease;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
