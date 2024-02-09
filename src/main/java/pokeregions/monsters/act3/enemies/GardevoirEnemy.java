package pokeregions.monsters.act3.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Gardevoir;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;
import pokeregions.util.ProAudio;
import pokeregions.util.Wiz;
import pokeregions.vfx.ThrowEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class GardevoirEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GardevoirEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte CHARM = 0;
    private static final byte NASTY_PLOT = 1;
    private static final byte MOONBLAST = 2;
    private static final byte HYPER_VOICE = 3;

    public final int POWER_STR = 3;
    public final int POWER_BLOCK = 8;
    public final int DEBUFF = 2;
    public final int STATUS = calcAscensionSpecial(2);
    public final int BUFF = calcAscensionSpecialSmall(3);

    public static final String POWER_ID = makeID("Sync");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GardevoirEnemy() {
        this(0.0f, 0.0f);
    }

    public GardevoirEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 150.0f, 170.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Gardevoir/Gardevoir.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.2f);
        Player.PlayerListener listener = new PokemonListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
        setHp(calcAscensionTankiness(200));
        addMove(CHARM, Intent.DEBUFF);
        addMove(NASTY_PLOT, Intent.BUFF);
        addMove(MOONBLAST, Intent.ATTACK, calcAscensionDamage(25));
        addMove(HYPER_VOICE, Intent.ATTACK, calcAscensionDamage(6), 3);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 0, "retain") {

            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                if (card.type == AbstractCard.CardType.ATTACK) {
                    this.flash();
                    applyToTarget(owner, owner, new StrengthPower(owner, POWER_STR));
                    applyToTarget(owner, owner, new LoseStrengthPower(owner, POWER_STR));
                }
                if (card.type == AbstractCard.CardType.SKILL) {
                    this.flash();
                    block(owner, POWER_BLOCK);
                }
            }

            @Override
            public void updateDescription() {
                description = POWER_DESCRIPTIONS[0] + POWER_STR + POWER_DESCRIPTIONS[1] + POWER_BLOCK + POWER_DESCRIPTIONS[2];
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
            case CHARM:
                runAnim("Special");
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                intoDrawMo(new Dazed(), STATUS);
                break;
            case NASTY_PLOT:
                runAnim("Special");
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                break;
            case MOONBLAST: {
                useFastAttackAnimation();
                float duration = 0.5f;
                atb(new VFXAction(ThrowEffect.throwEffect("PurpleSpike.png", 1.0f, this.hb, adp().hb, Color.PURPLE.cpy(), duration), duration));
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        Wiz.playAudio(ProAudio.MAGIC_BLAST, 1.0f);
                        this.isDone = true;
                    }
                });
                dmg(adp(), info, AbstractGameAction.AttackEffect.NONE);
                break;
            }
            case HYPER_VOICE: {
                useFastAttackAnimation();
                atb(new SFXAction("ATTACK_PIERCING_WAIL"));
                if (Settings.FAST_MODE) {
                    atb(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                } else {
                    atb(new VFXAction(this, new ShockWaveEffect(this.hb.cX, this.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 1.5F));
                }
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                }
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        ArrayList<Byte> possibilities = new ArrayList<>();
        if (!this.lastMove(CHARM) && !this.lastMove(NASTY_PLOT)) {
            possibilities.add(CHARM);
        }
        if (!this.lastMove(NASTY_PLOT)) {
            possibilities.add(NASTY_PLOT);
        }
        if (!this.lastMove(MOONBLAST) && !this.lastMoveBefore(HYPER_VOICE)) {
            possibilities.add(MOONBLAST);
        }
        if (!this.lastMove(HYPER_VOICE) && !this.lastMoveBefore(MOONBLAST)) {
            possibilities.add(HYPER_VOICE);
        }
        byte move = possibilities.get(AbstractDungeon.monsterRng.random(possibilities.size() - 1));
        setMoveShortcut(move, MOVES[move]);
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case CHARM: {
                Details powerDetail = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail);
                Details statusDetail = new Details(this, STATUS, DAZED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case NASTY_PLOT: {
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Gardevoir();
    }

}