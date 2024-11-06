package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Trapinch;
import pokeregions.monsters.AbstractPokemonAlly;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class TrapinchAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Trapinch.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public TrapinchAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 100.0f, 80.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Trapinch/Trapinch.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Trapinch.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                atb(new DrawCardAction(Trapinch.MOVE_1_EFFECT));
                applyToTargetNextTurn(adp(), this, new EquilibriumPower(adp(), 1));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                atb(new VFXAction(new BiteEffect(target.hb.cX, target.hb.cY), 0.3F));
                dmg(target, info);
                applyToTarget(target, this, new VulnerablePower(target, Trapinch.MOVE_2_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
        }
        postTurn();
    }

}