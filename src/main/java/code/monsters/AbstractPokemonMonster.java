package code.monsters;

import basemod.abstracts.CustomMonster;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import code.BetterSpriterAnimation;
import code.vfx.VFXActionButItCanFizzle;
import code.vfx.WaitEffect;

import java.util.HashMap;
import java.util.Map;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.atb;
import static code.util.Wiz.att;

public abstract class AbstractPokemonMonster extends CustomMonster {

    protected Map<Byte, EnemyMoveInfo> moves;
    protected boolean firstMove = true;
    protected DamageInfo info;
    protected int multiplier;
    private static final float ASCENSION_DAMAGE_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_TANK_BUFF_PERCENT = 1.10f;
    private static final float ASCENSION_SPECIAL_BUFF_PERCENT = 1.5f;

    public AbstractPokemonMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        setUpMisc();
    }

    public AbstractPokemonMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        setUpMisc();
    }

    public AbstractPokemonMonster(String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(name, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        setUpMisc();
    }

    @Override
    public void takeTurn() {
        this.info = getInfoFromMove(this.nextMove);
        this.multiplier = getMultiplierFromMove(this.nextMove);
    }

    protected void setUpMisc() {
        moves = new HashMap<>();
        this.dialogX = (this.hb_x - 70.0F) * Settings.scale;
        this.dialogY -= (this.hb_y - 55.0F) * Settings.scale;
    }

    protected void addMove(byte moveCode, Intent intent) {
        this.addMove(moveCode, intent, -1);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage) {
        this.addMove(moveCode, intent, baseDamage, 0, false);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier) {
        this.addMove(moveCode, intent, baseDamage, multiplier, true);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        this.moves.put(moveCode, new EnemyMoveInfo(moveCode, intent, baseDamage, multiplier, isMultiDamage));
    }

    public void setMoveShortcut(byte next, String text) {
        EnemyMoveInfo info = this.moves.get(next);
        this.setMove(text, next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }
    public void setMoveShortcut(byte next) {
        this.setMoveShortcut(next, null);
    }

    protected int calcAscensionDamage(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 4) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 3) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 2) {
                    base *= ASCENSION_DAMAGE_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionTankiness(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 9) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 8) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 7) {
                    base *= ASCENSION_TANK_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    protected int calcAscensionSpecial(float base) {
        switch (this.type) {
            case BOSS:
                if(AbstractDungeon.ascensionLevel >= 19) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case ELITE:
                if(AbstractDungeon.ascensionLevel >= 18) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
            case NORMAL:
                if(AbstractDungeon.ascensionLevel >= 17) {
                    base *= ASCENSION_SPECIAL_BUFF_PERCENT;
                }
                break;
        }
        return Math.round(base);
    }

    @Override
    public void die(boolean triggerRelics) {
        this.useShakeAnimation(5.0F);
        if (this.animation instanceof BetterSpriterAnimation) {
            ((BetterSpriterAnimation)this.animation).startDying();
        }
        super.die(triggerRelics);
    }

    protected DamageInfo getInfoFromMove(byte nextMove) {
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            return new DamageInfo(this, emi.baseDamage, DamageInfo.DamageType.NORMAL);
        } else {
            return new DamageInfo(this, 0, DamageInfo.DamageType.NORMAL);
        }
    }

    protected int getMultiplierFromMove(byte nextMove) {
        int multiplier = 0;
        if(moves.containsKey(this.nextMove)) {
            EnemyMoveInfo emi = moves.get(this.nextMove);
            multiplier = emi.multiplier;
        }
        return multiplier;
    }

    //Runs a specific animation
    public void runAnim(String animation) {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation(animation);
    }

    //Resets character back to idle animation
    public void resetAnimation() {
        ((BetterSpriterAnimation)this.animation).myPlayer.setAnimation("Idle");
    }

    //Prevents any further animation once the death animation is finished
    public void stopAnimation() {
        int time = ((BetterSpriterAnimation)this.animation).myPlayer.getAnimation().length;
        ((BetterSpriterAnimation)this.animation).myPlayer.setTime(time);
        ((BetterSpriterAnimation)this.animation).myPlayer.speed = 0;
    }

    public class PokemonListener implements Player.PlayerListener {

        private final AbstractPokemonMonster character;

        public PokemonListener(AbstractPokemonMonster character) {
            this.character = character;
        }

        public void animationFinished(Animation animation){
            if (animation.name.equals("Defeat")) {
                character.stopAnimation();
            } else if (!animation.name.equals("Idle")) {
                character.resetAnimation();
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

    protected void animationAction(String animation, String sound, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature enemy, AbstractCreature owner) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (owner.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    runAnim(animation);
                    playSound(sound, volume);
                } else if (!enemy.isDeadOrEscaped()) {
                    runAnim(animation);
                    playSound(sound, volume);
                }
                this.isDone = true;
            }
        });
    }

    protected void animationAction(String animation, String sound, AbstractCreature owner) {
        animationAction(animation, sound, null, owner);
    }

    protected void animationAction(String animation, String sound, float volume, AbstractCreature owner) {
        animationAction(animation, sound, volume, null, owner);
    }

    public static void playSound(String sound, float volume) {
        if (sound != null) {
            CardCrawlGame.sound.playV(makeID(sound), volume);
        }
    }

    public static void playSound(String sound) {
        playSound(sound, 1.0f);
    }

    public void waitAnimation() {
        waitAnimation(0.5f, null);
    }

    public void waitAnimation(float duration) {
        waitAnimation(duration, null);
    }

    protected void waitAnimation(AbstractCreature enemy) {
        waitAnimation(0.5f, enemy);
    }

    public void waitAnimation(float time, AbstractCreature enemy) {
        atb(new AbstractGameAction() {
            @Override
            public void update() {
                if (AbstractPokemonMonster.this.isDeadOrEscaped()) {
                    isDone = true;
                    return;
                }
                if (enemy == null) {
                    att(new VFXActionButItCanFizzle(AbstractPokemonMonster.this, new WaitEffect(), time));
                } else if (!enemy.isDeadOrEscaped()) {
                    att(new VFXActionButItCanFizzle(AbstractPokemonMonster.this, new WaitEffect(), time));
                }
                this.isDone = true;
            }
        });
    }

}