package code.monsters.act1.enemies;

import code.BetterSpriterAnimation;
import code.cards.pokemonAllyCards.Diglett;
import code.monsters.AbstractPokemonMonster;
import code.powers.SandVeil;
import code.vfx.WaitEffect;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static code.PokemonRegions.makeID;
import static code.PokemonRegions.makeMonsterPath;
import static code.util.Wiz.*;

public class DiglettEnemy extends AbstractPokemonMonster
{
    public static final String ID = makeID(DiglettEnemy.class.getSimpleName());
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;

    private static final byte DIG = 0;
    private static final byte SLASH = 1;

    public final int BLOCK = 7;
    public final int STR = 2;
    public final int FORTIFY_DAMAGE_REDUCTION = calcAscensionSpecial(50);
    private boolean attackFirst;
    public boolean burrowed = false;

    public DiglettEnemy() {
        this(0.0f, 0.0f, false);
    }

    public DiglettEnemy(final float x, final float y, boolean attackFirst) {
        super(NAME, ID, 140, 0.0F, 0, 120.0f, 80.0f, null, x, y);
        this.animation = new BetterSpriterAnimation(makeMonsterPath("Diglett/Diglett.scml"));
        this.type = EnemyType.NORMAL;
        this.attackFirst = attackFirst;
        setHp(calcAscensionTankiness(30), calcAscensionTankiness(35));
        addMove(DIG, Intent.DEFEND_BUFF);
        addMove(SLASH, Intent.ATTACK, calcAscensionDamage(5));

        Player.PlayerListener listener = new DiglettListener(this);
        ((BetterSpriterAnimation)this.animation).myPlayer.addListener(listener);
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        applyToTarget(this, this, new SandVeil(this, FORTIFY_DAMAGE_REDUCTION));
        if (this.attackFirst) {
            runAnim("Dig");
            this.burrowed = true;
            block(this, BLOCK);
        }
    }

    @Override
    public void takeTurn() {
        super.takeTurn();
        if(info.base > -1) {
            info.applyPowers(this, adp());
        }

        switch (this.nextMove) {
            case DIG: {
                runAnim("Dig");
                this.burrowed = true;
                block(this, BLOCK);
                applyToTarget(this, this, new StrengthPower(this, STR));
                break;
            }
            case SLASH: {
                if (this.burrowed) {
                    runAnim("Emerge");
                    this.burrowed = false;
                    atb(new VFXAction(new WaitEffect(), 0.2f));
                }
                atb(new AbstractGameAction() {
                    @Override
                    public void update() {
                        useFastAttackAnimation();
                        this.isDone = true;
                    }
                });
                dmg(adp(), info, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
                break;
            }
        }
        atb(new RollMoveAction(this));
    }

    @Override
    protected void getMove(final int num) {
        if (attackFirst && firstMove) {
            setMoveShortcut(SLASH, MOVES[SLASH]);
        } else if (lastMove(DIG)) {
            setMoveShortcut(SLASH, MOVES[SLASH]);
        } else {
            setMoveShortcut(DIG, MOVES[DIG]);
        }
    }

    @Override
    public AbstractCard getAssociatedPokemonCard() {
        return new Diglett();
    }

    public static class DiglettListener implements Player.PlayerListener {

        private final DiglettEnemy character;

        public DiglettListener(DiglettEnemy character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (character.burrowed) {
                if (!animation.name.equals("DigIdle")) {
                    ((BetterSpriterAnimation)character.animation).myPlayer.setAnimation("DigIdle");
                }
            } else {
                if (!animation.name.equals("Idle")) {
                    character.resetAnimation();
                }
            }
        }

        //UNUSED
        public void animationChanged(Animation var1, Animation var2){
        }

        //UNUSED
        public void preProcess(Player var1){
        }

        //UNUSED
        public void postProcess(Player var1){
        }

        //UNUSED
        public void mainlineKeyChanged(com.brashmonkey.spriter.Mainline.Key var1, com.brashmonkey.spriter.Mainline.Key var2){
        }
    }

}