package pokeregions.monsters.act1.allyPokemon;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Charmander;
import pokeregions.monsters.AbstractPokemonAlly;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.vfx.combat.FireballEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class CharmanderAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Charmander.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public CharmanderAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Charmander/Charmander.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent, Charmander.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent, Charmander.MOVE_2_DAMAGE);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                runAnim("Melee");
                dmg(target, info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
            case MOVE_2: {
                runAnim("Ranged");
                atb(new VFXAction(new FireballEffect(this.hb.cX, this.hb.cY, target.hb.cX, target.hb.cY), 0.5F));
                dmg(target, info, AbstractGameAction.AttackEffect.FIRE);
                applyToTarget(target, this, new VulnerablePower(target, Charmander.MOVE_2_DEBUFF, AbstractDungeon.actionManager.turnHasEnded));
                break;
            }
        }
        postTurn();
    }

}