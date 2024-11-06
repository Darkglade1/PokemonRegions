package pokeregions.monsters.act3.allyPokemon;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.DexterityPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Rayquaza;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Slow;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class RayquazaAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Rayquaza.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public RayquazaAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 160.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Rayquaza/Rayquaza.scml"));
        this.animation.setFlip(true, false);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK_DEBUFF;
        move2Intent = Intent.BUFF;
        addMove(MOVE_1, move1Intent, Rayquaza.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_2;
        move1RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                useFastAttackAnimation();
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                applyToTarget(target, this, new Slow(target, Rayquaza.MOVE_1_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
            case MOVE_2: {
                atb(new DrawCardAction(Rayquaza.MOVE_2_EFFECT, new AbstractGameAction() {
                    @Override
                    public void update() {
                        int totalCost = 0;
                        for (AbstractCard c : DrawCardAction.drawnCards) {
                            if (c.costForTurn > 0) {
                               totalCost += c.costForTurn;
                            }
                        }
                        if (totalCost > 0) {
                            applyToTargetTop(adp(), RayquazaAlly.this, new DexterityPower(adp(), totalCost));
                        }
                        this.isDone = true;
                    }
                }));
                break;
            }
        }
        postTurn();
    }

}