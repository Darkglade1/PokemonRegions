package pokeregions.monsters.act3.allyPokemon;

import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Gardevoir;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.powers.Fortitude;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.ThrowEffect;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.*;

public class GardevoirAlly extends AbstractPokemonAlly
{
    public static final String ID = PokemonRegions.makeID(Gardevoir.class.getSimpleName());
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public GardevoirAlly(final float x, final float y, AbstractAllyPokemonCard allyCard) {
        super(NAME, ID, 100, -5.0F, 0, 140.0f, 150.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gardevoir/Gardevoir.scml"));
        this.animation.setFlip(true, false);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);

        this.allyCard = allyCard;
        setStaminaInfo(allyCard);

        move1Intent = Intent.BUFF;
        move2Intent = Intent.ATTACK_DEBUFF;
        addMove(MOVE_1, move1Intent);
        addMove(MOVE_2, move2Intent, Gardevoir.MOVE_2_DAMAGE);
        defaultMove = MOVE_2;
        move2RequiresTarget = true;
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        switch (this.nextMove) {
            case MOVE_1: {
                runAnim("Special");
                applyToTarget(adp(), this, new Fortitude(adp(), Gardevoir.MOVE_1_EFFECT, false));
                break;
            }
            case MOVE_2: {
                useFastAttackAnimation();
                float duration = 0.5f;
                atb(new VFXAction(ThrowEffect.throwEffect("PurpleSpike.png", 1.0f, this.hb, target.hb, Color.PURPLE.cpy(), duration, false, true), duration));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.MAGIC_BLAST, 1.0f);
                        this.isDone = true;
                    }
                });
                dmg(target, info, AbstractGameAction.AttackEffect.NONE);
                applyToTarget(target, this, new StrengthPower(target, -Gardevoir.MOVE_2_EFFECT));
                if (!target.hasPower(ArtifactPower.POWER_ID)) {
                    applyToTarget(target, this, new GainStrengthPower(target, Gardevoir.MOVE_2_EFFECT));
                }
                break;
            }
        }
        postTurn();
    }

}