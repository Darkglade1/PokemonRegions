package pokeregions.cards.cardMods;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireBurningEffect;
import com.megacrit.cardcrawl.vfx.campfire.CampfireEndingBurningEffect;
import pokeregions.PokemonRegions;
import pokeregions.vfx.CardBurningEffect;

import java.util.ArrayList;
import java.util.Iterator;

import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class ScorchedMod extends AbstractCardModifier {

    public static final String ID = PokemonRegions.makeID(ScorchedMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private final int SCORCHED_DAMAGE = 2;
    private ArrayList<AbstractGameEffect> flameEffect = new ArrayList<>();
    private float fireTimer;

    @Override
    public AbstractCardModifier makeCopy() {
        return new ScorchedMod();
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.isEthereal = true;
        flameEffect.add(new CardBurningEffect(card));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        atb(new DamageAction(adp(), new DamageInfo(adp(), SCORCHED_DAMAGE, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return TEXT[0] + rawDescription;
    }

    @Override
    public void onUpdate(AbstractCard card) {
        this.fireTimer -= Gdx.graphics.getDeltaTime();
        if (this.fireTimer < 0.0F) {
            this.fireTimer = 0.05F;
            flameEffect.add(new CardBurningEffect(card));
        }

        Iterator<AbstractGameEffect> i = this.flameEffect.iterator();
        while(i.hasNext()) {
            AbstractGameEffect fires = i.next();
            fires.update();
            if (fires.isDone) {
                i.remove();
            }
        }
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        for (AbstractGameEffect effect : flameEffect) {
            effect.render(sb);
        }
    }
}
