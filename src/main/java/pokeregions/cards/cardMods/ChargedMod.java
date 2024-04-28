package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.PokemonRegions;
import pokeregions.powers.Voltage;

import static pokeregions.util.Wiz.*;

public class ChargedMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(ChargedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final int VOLTAGE = 2;
    private final int ENERGY = 1;
    private boolean alreadyShuffleBack = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ChargedMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.shuffleBackIntoDrawPile) {
            card.shuffleBackIntoDrawPile = true;
        } else {
            alreadyShuffleBack = true;
        }
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        atb(new SFXAction("ORB_PLASMA_CHANNEL", 0.1f));
        applyToTarget(adp(), adp(), new Voltage(adp(), VOLTAGE));
        atb(new GainEnergyAction(ENERGY));
        if (!alreadyShuffleBack) {
            card.shuffleBackIntoDrawPile = false;
        }
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }
}
