package pokeregions.monsters.act1.enemies;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
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
import com.megacrit.cardcrawl.stances.DivinityStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.Alakazam;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.AbstractLambdaPower;
import pokeregions.util.Details;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class AlakazamEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(AlakazamEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte KINESIS = 0;
    private static final byte FOCUS = 1;
    private static final byte FOCUS_BLAST = 2;

    public final int STATUS = calcAscensionSpecial(2);

    private float particleTimer;
    private float particleTimer2;

    public static final String POWER_ID = makeID("MagicGuard");
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String POWER_NAME = powerStrings.NAME;
    public static final String[] POWER_DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AlakazamEnemy() {
        this(0.0f, 0.0f);
    }

    public AlakazamEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 180.0f, 180.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Alakazam/Alakazam.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 1.15f);
        this.type = EnemyType.NORMAL;
        setHp(calcAscensionTankiness(45), calcAscensionTankiness(50));
        addMove(KINESIS, Intent.ATTACK_DEBUFF, calcAscensionDamage(8));
        addMove(FOCUS, Intent.UNKNOWN);
        addMove(FOCUS_BLAST, Intent.ATTACK, calcAscensionDamage(24));
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new AbstractLambdaPower(POWER_ID, POWER_NAME, AbstractPower.PowerType.BUFF, false, this, 1) {
            @Override
            public void onAfterUseCard(AbstractCard card, UseCardAction action) {
                if (this.amount >= 1) {
                    flash();
                    this.amount = 0;
                }
            }

            @Override
            public void atEndOfRound() {
                amount = 1;
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
            case KINESIS: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
                intoDiscardMo(new Dazed(), STATUS);
                break;
            }
            case FOCUS: {
                break;
            }
            case FOCUS_BLAST: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (lastMove(KINESIS)) {
            setMoveShortcut(FOCUS, MOVES[FOCUS]);
        } else if (lastMove(FOCUS)) {
            setMoveShortcut(FOCUS_BLAST, MOVES[FOCUS_BLAST]);
        } else {
            setMoveShortcut(KINESIS, MOVES[KINESIS]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case KINESIS: {
                Details statusDetails = new Details(this, STATUS, DAZED_TEXTURE, Details.TargetType.DISCARD_PILE);
                details.add(statusDetails);
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
        return new Alakazam();
    }

}