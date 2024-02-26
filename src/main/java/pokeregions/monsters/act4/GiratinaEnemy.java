package pokeregions.monsters.act4;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.CustomScore;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.common.SpawnMonsterAction;
import com.megacrit.cardcrawl.cards.status.Wound;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.NoBlockPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.vfx.combat.BloodShotEffect;
import com.megacrit.cardcrawl.vfx.combat.ViceCrushEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.DistortionWorld;
import pokeregions.powers.LostInDistortion;
import pokeregions.powers.NastyPlot;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class GiratinaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(GiratinaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    public static final byte HEX = 0;
    public static final byte SHADOW_CLAW = 1;
    public static final byte NASTY_PLOT = 2;
    public static final byte SHADOW_FORCE = 3;
    public static final byte POLTERGEIST = 4;

    public final int STATUS = calcAscensionSpecial(2);
    public final int BUFF = calcAscensionSpecialSmall(5);
    public final int DEBUFF = 1;
    public final int STR = 1;
    public final int POLTERGEIST_HITS = 5;
    public boolean inDistortionWorld = false;

    public final int HP_THRESHOLD = 350;
    public DistortionLeyline leyline1;
    public DistortionLeyline leyline2;

    public GiratinaEnemy() {
        this(0.0f, 0.0f);
    }

    public GiratinaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 380.0f, 310.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Giratina/Giratina.scml"));
        isCatchable = false;
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        if (AbstractDungeon.ascensionLevel >= 9) {
            setHp(999);
        } else {
            setHp(900);
        }
        addMove(HEX, Intent.STRONG_DEBUFF);
        addMove(SHADOW_CLAW, Intent.ATTACK, calcAscensionDamage(33));
        addMove(NASTY_PLOT, Intent.BUFF);
        addMove(SHADOW_FORCE, Intent.ATTACK_DEBUFF, calcAscensionDamage(40));
        addMove(POLTERGEIST, Intent.ATTACK_BUFF, calcAscensionDamage(10), POLTERGEIST_HITS);
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        CustomDungeon.playTempMusicInstantly("LavenderTown");
        applyToTarget(this, this, new LostInDistortion(this, HP_THRESHOLD));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case HEX: {
                applyToTarget(adp(), this, new VulnerablePower(adp(), DEBUFF, true));
                applyToTarget(adp(), this, new WeakPower(adp(), DEBUFF, true));
                intoDrawMo(new Wound(), STATUS);
                break;
            }
            case SHADOW_CLAW: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_HEAVY);
                break;
            }
            case NASTY_PLOT: {
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                break;
            }
            case SHADOW_FORCE: {
                useFastAttackAnimation();
                atb(new VFXAction(new ViceCrushEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY), 0.5F));
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                applyToTarget(adp(), this, new NoBlockPower(adp(), 1, true));
                break;
            }
            case POLTERGEIST: {
                if (Settings.FAST_MODE) {
                    atb(new VFXAction(new BloodShotEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, POLTERGEIST_HITS), 0.25F));
                } else {
                    atb(new VFXAction(new BloodShotEffect(this.hb.cX, this.hb.cY, AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, POLTERGEIST_HITS), 0.6F));
                }
                for (int i = 0; i < multiplier; i++) {
                    dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                }
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    private void Summon() {
        leyline1 = new DistortionLeyline(-500.0F, 0.0F);
        atb(new SpawnMonsterAction(leyline1, true));

        leyline2 = new DistortionLeyline(-300.0F, 0.0F);
        atb(new SpawnMonsterAction(leyline2, true));
    }

    public void enterDistortionWorld() {
        makePowerRemovable(this, LostInDistortion.POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, LostInDistortion.POWER_ID));
        inDistortionWorld = true;
        Summon();
        applyToTarget(this, this, new DistortionWorld(this));
        rollMove();
        createIntent();
    }

    public void exitDistortionWorld() {
        makePowerRemovable(this, DistortionWorld.POWER_ID);
        atb(new RemoveSpecificPowerAction(this, this, DistortionWorld.POWER_ID));
        inDistortionWorld = false;
        applyToTarget(this, this, new LostInDistortion(this, HP_THRESHOLD));
        rollMove();
        createIntent();
    }

    @Override
    protected void getMove(final int num) {
        if (inDistortionWorld) {
            if (this.lastMove(POLTERGEIST)) {
                setMoveShortcut(SHADOW_FORCE, MOVES[SHADOW_FORCE]);
            } else {
                setMoveShortcut(POLTERGEIST, MOVES[POLTERGEIST]);
            }
        } else {
            if (this.lastMove(HEX)) {
                setMoveShortcut(SHADOW_CLAW, MOVES[SHADOW_CLAW]);
            } else if (this.lastMove(SHADOW_CLAW)) {
                setMoveShortcut(NASTY_PLOT, MOVES[NASTY_PLOT]);
            } else {
                setMoveShortcut(HEX, MOVES[HEX]);
            }
        }
        super.postGetMove();
    }

    @Override
    public void die() {
        CardCrawlGame.stopClock = true;
        ScoreBonusStrings sbs = CardCrawlGame.languagePack.getScoreString(PokemonRegions.makeID("SpaceTimeChampion"));
        CustomScore.add(PokemonRegions.makeID("SpaceTimeChampion"), sbs.NAME, sbs.DESCRIPTIONS[0], 200, false);
        this.onBossVictoryLogic();
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.playTempBgmInstantly("STS_EndingStinger_v1.ogg", false);
        this.onFinalBossVictoryLogic();
        super.die();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        String textureString = makeUIPath("NoBlock.png");
        Texture texture = TexLoader.getTexture(textureString);
        switch (move.nextMove) {
            case HEX: {
                Details powerDetail = new Details(this, DEBUFF, VULNERABLE_TEXTURE);
                details.add(powerDetail);
                Details powerDetail2 = new Details(this, DEBUFF, WEAK_TEXTURE);
                details.add(powerDetail2);
                Details statusDetail = new Details(this, STATUS, WOUND_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
            case NASTY_PLOT: {
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                break;
            }
            case SHADOW_FORCE: {
                Details powerDetail = new Details(this, 1, texture);
                details.add(powerDetail);
                break;
            }
            case POLTERGEIST: {
                Details powerDetail = new Details(this, STR, STRENGTH_TEXTURE);
                details.add(powerDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }
}