package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;

public class DampMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(DampMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean alreadyRetain = false;
    private boolean alreadyExhaust = false;
    public int turns = 2;

    @Override
    public AbstractCardModifier makeCopy() {
        return new DampMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
        } else {
            alreadyRetain = true;
        }
        if (!card.exhaust) {
            card.exhaust = true;
        } else {
            alreadyExhaust = true;
        }
    }

    @Override
    public void onRetained(AbstractCard card) {
        turns--;
        if (turns <= 0) {
            atb(new AbstractGameAction() {
                @Override
                public void update() {
                    CardModifierManager.removeSpecificModifier(card, DampMod.this, false);
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (!alreadyRetain) {
            card.selfRetain = false;
        }
        if (!alreadyExhaust) {
            card.exhaust = false;
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
