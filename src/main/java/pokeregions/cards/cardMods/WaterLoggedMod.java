package pokeregions.cards.cardMods;

import basemod.BaseMod;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import pokeregions.PokemonRegions;

import java.util.ArrayList;
import java.util.List;

import static pokeregions.util.Wiz.atb;

public class WaterLoggedMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(WaterLoggedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    private boolean alreadyRetain = false;

    @Override
    public AbstractCardModifier makeCopy() {
        return new WaterLoggedMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (!card.selfRetain) {
            card.selfRetain = true;
        } else {
            alreadyRetain = true;
        }
    }

    @Override
    public void onRetained(AbstractCard card) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                CardModifierManager.removeSpecificModifier(card, WaterLoggedMod.this, false);
                CardModifierManager.addModifier(card, new DampMod());
                this.isDone = true;
            }
        });
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (!alreadyRetain) {
            card.selfRetain = false;
        }
    }

    @Override
    public List<TooltipInfo> additionalTooltips(AbstractCard card) {
        ArrayList<TooltipInfo> info = new ArrayList<>();
        TooltipInfo tip = new TooltipInfo(BaseMod.getKeywordProper("pokeregions:damp"),  BaseMod.getKeywordDescription("pokeregions:damp"));
        info.add(tip);
        return info;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        return false;
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
