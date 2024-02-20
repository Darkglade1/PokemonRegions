package pokeregions.monsters.act4;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.stances.DivinityStance;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.pokemonAllyCards.act3.Groudon;
import pokeregions.monsters.AbstractPokemonMonster;
import pokeregions.powers.BorrowedTime;
import pokeregions.powers.NastyPlot;
import pokeregions.powers.SuspendedInTime;
import pokeregions.util.Details;
import pokeregions.util.TexLoader;
import pokeregions.vfx.FlexibleDivinityParticleEffect;
import pokeregions.vfx.FlexibleStanceAuraEffect;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.*;
import static pokeregions.util.Wiz.*;

public class DialgaEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(DialgaEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte ANCIENT_POWER = 0;
    private static final byte ROAR = 1;
    private static final byte DISTORT = 2;

    public final int BLOCK = 50;
    public final int BUFF = calcAscensionSpecialSmall(3);
    public final int STATUS = calcAscensionSpecial(3);

    private float particleTimer;
    private float particleTimer2;

    public DialgaEnemy() {
        this(0.0f, 0.0f);
    }

    public DialgaEnemy(final float x, final float y) {
        super(NAME, ID, 140, 0.0F, 0, 300.0f, 320.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Dialga/Dialga.scml"));
        ((BetterSpriterAnimation)this.animation).myPlayer.setScale(Settings.scale * 2.0f);
        setHp(calcAscensionTankiness(700));
        addMove(ANCIENT_POWER, Intent.DEFEND_BUFF);
        addMove(ROAR, Intent.ATTACK, calcAscensionDamage(50));
        addMove(DISTORT, Intent.ATTACK_DEBUFF, calcAscensionDamage(30));
    }

    @Override
    protected void setUpMisc() {
        super.setUpMisc();
        this.type = EnemyType.BOSS;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new SuspendedInTime(this, 0));
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case ANCIENT_POWER: {
                block(this, BLOCK);
                applyToTarget(this, this, new NastyPlot(this, BUFF));
                if (AbstractDungeon.ascensionLevel >= 19) {
                    applyToTarget(this, this, new BorrowedTime(this));
                }
                break;
            }
            case ROAR: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                break;
            }
            case DISTORT: {
                useFastAttackAnimation();
                dmg(adp(), info, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
                intoDrawMo(new Dazed(), STATUS);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (this.lastMove(ANCIENT_POWER)) {
            setMoveShortcut(ROAR, MOVES[ROAR]);
        } else if (this.lastMove(ROAR)) {
            setMoveShortcut(DISTORT, MOVES[DISTORT]);
        } else {
            setMoveShortcut(ANCIENT_POWER, MOVES[ANCIENT_POWER]);
        }
        super.postGetMove();
    }

    protected void setDetailedIntents() {
        ArrayList<Details> details = new ArrayList<>();
        EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
        switch (move.nextMove) {
            case ANCIENT_POWER: {
                Details blockDetail = new Details(this, BLOCK, BLOCK_TEXTURE);
                details.add(blockDetail);
                Details powerDetail = new Details(this, BUFF, NASTY_PLOT_TEXTURE);
                details.add(powerDetail);
                if (AbstractDungeon.ascensionLevel >= 19) {
                    String textureString = makePowerPath("BorrowedTime32.png");
                    Texture texture = TexLoader.getTexture(textureString);
                    Details powerDetail2 = new Details(this, 1, texture);
                    details.add(powerDetail2);
                }
                break;
            }
            case DISTORT: {
                Details statusDetail = new Details(this, STATUS, DAZED_TEXTURE, Details.TargetType.DRAW_PILE);
                details.add(statusDetail);
                break;
            }
        }
        PokemonRegions.intents.put(this, details);
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (this.hasPower(SuspendedInTime.POWER_ID) && this.getPower(SuspendedInTime.POWER_ID).amount >= 1) {
            this.particleTimer -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer < 0.0F) {
                this.particleTimer = 0.04F;
                AbstractDungeon.effectsQueue.add(new FlexibleDivinityParticleEffect(this, Color.SKY.cpy()));
            }
            this.particleTimer2 -= Gdx.graphics.getDeltaTime();
            if (this.particleTimer2 < 0.0F) {
                this.particleTimer2 = MathUtils.random(0.45F, 0.55F);
                AbstractDungeon.effectsQueue.add(new FlexibleStanceAuraEffect(Color.SKY.cpy(), this));
            }
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Groudon();
    }

}