package pokeregions.monsters.act4;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Groudon;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;
import pokeregions.vfx.ThrowEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class PalkiaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(PalkiaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    public static final byte SCARY_FACE = 0;
    public static final byte WATER_PULSE = 1;
    public static final byte DRAGON_CLAW = 2;
    public static final byte SPACIAL_REND = 3;

    public final int VOIDS = calcAscensionSpecial(2);
    public final int SLIMES = calcAscensionSpecial(2);
    public final int DEBUFF = 2;

    private float particleTimer;
    private float particleTimer2;

    public static final String POWER_ID = makeID("AdriftInSpace");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PalkiaEnemy() {
        this(0.0f, 0.0f);
    }

    public PalkiaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 350.0f, 280.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Palkia/Palkia.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(800));
        addMove(SCARY_FACE, Intent.STRONG_DEBUFF);
        addMove(WATER_PULSE, Intent.ATTACK_DEBUFF, calcAscensionDamage(28));
        addMove(DRAGON_CLAW, Intent.ATTACK, calcAscensionDamage(15), 3);
        addMove(SPACIAL_REND, Intent.ATTACK_DEBUFF, calcAscensionDamage(50));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0) {
            @Override
            public void onUseCard(AbstractCard card, UseCardAction action) {
                card.shuffleBackIntoDrawPile = true;
                if (this.amount >= 1) {
                    this.flash();
                    action.exhaustCard = true;
                    this.amount = 0;
                } else {
                    this.amount = 1;
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0];
            }
        });
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case SCARY_FACE: {
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                intoDiscardMo(new VoidCard(), VOIDS);
                break;
            }
            case WATER_PULSE: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                intoDrawMo(new Slimed(), SLIMES);
                break;
            }
            case DRAGON_CLAW: {
                useFastAttackAnimation();
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                }
                break;
            }
            case SPACIAL_REND: {
                useFastAttackAnimation();
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.SPATIAL_START, 1.0f);
                        this.isDone = true;
                    }
                });
                float duration = 1.25f;
                atb(new VFXAction(ThrowEffect.throwEffect("SpatialRend.png", 2.0f, this.hb, adp().hb, Color.BLUE.cpy(), duration), duration));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.SPATIAL_HIT, 1.0f);
                        this.isDone = true;
                    }
                });
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        SpacialRendExhume();
                        this.isDone = true;
                    }
                });
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(SCARY_FACE)) {
            setMoveShortcut(WATER_PULSE, MOVES[WATER_PULSE]);
        } else if (this.lastMove(WATER_PULSE)) {
            setMoveShortcut(DRAGON_CLAW, MOVES[DRAGON_CLAW]);
        } else if (this.lastMove(DRAGON_CLAW)) {
            setMoveShortcut(SPACIAL_REND, MOVES[SPACIAL_REND]);
        } else {
            setMoveShortcut(SCARY_FACE, MOVES[SCARY_FACE]);
        }
        super.postGetMove();
    }

    public void SpacialRendExhume() {
        ArrayList<AbstractCard> cardsToShuffle = new ArrayList<>();
        for (AbstractCard card : adp().exhaustPile.group) {
            if (!card.exhaust || card.type == AbstractCard.CardType.STATUS) {
                cardsToShuffle.add(card);
            }
        }
        int newDamage = this.moves.get(SPACIAL_REND).baseDamage += cardsToShuffle.size();
        addMove(SPACIAL_REND, Intent.ATTACK_DEBUFF, newDamage);
        for (AbstractCard card : cardsToShuffle) {
            card.unfadeOut();
            adp().exhaustPile.moveToDiscardPile(card);
        }
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case SCARY_FACE: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details statusDetail = new Details(this, VOIDS, VOID_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetail);
                break;
            }
            case WATER_PULSE: {
                Details statusDetail = new Details(this, SLIMES, SLIMED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case SPACIAL_REND: {
                Details rendDetail = new Details(this, Details.SPATIAL_REND);
                details.add(rendDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(POWER_ID) && this.getPower(POWER_ID).amount >= 1) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(DivinityStance.STANCE_ID, this));
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Groudon();
    }

}