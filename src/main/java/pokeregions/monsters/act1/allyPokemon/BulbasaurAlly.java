package pokeregions.monsters.act1.allyPokemon;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act1.Bulbasaur;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.ToxicPower;
import pokeregions.vfx.SporeDustEffect;
import pokeregions.vfx.WaitEffect;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.WeakPower;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class BulbasaurAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Bulbasaur.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;

    public BulbasaurAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 150.0f, 100.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Bulbasaur/Bulbasaur.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.ATTACK;
        move2Intent = Intent.DEBUFF;
        addMove(MOVE_1, move1Intent, Bulbasaur.MOVE_1_DAMAGE);
        addMove(MOVE_2, move2Intent);
        defaultMove = MOVE_1;
        move1RequiresTarget = true;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                runAnim("Whip");
                atb(new VFXAction(new WaitEffect(), 0.2f));
                dmg(target, info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                break;
            }
            case MOVE_2: {
                runAnim("Debuff");
                atb(new SFXAction("ATTACK_MAGIC_FAST_3", MathUtils.random(0.88F, 0.92F), true));
                float x = target.hb.cX;
                float y= target.hb.cY + (target.hb.height * 0.5f * Settings.scale);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        for (int i = 0; i < 5; i++) { AbstractDungeon.effectsQueue.add(new SporeDustEffect(x, y)); }
                        this.isDone = true;
                    }
                });
                atb(new VFXAction(new WaitEffect(), 1.0f));
                applyToTarget(target, this, new ToxicPower(target,  Bulbasaur.MOVE_2_TOXIC));
                applyToTarget(target, this, new WeakPower(target, Bulbasaur.MOVE_2_WEAK, false));
                break;
            }
        }
        postTurn();
    }

}