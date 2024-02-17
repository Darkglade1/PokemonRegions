package pokeregions.events.act3;

import basemod.abstracts.events.PhasedEvent;
import basemod.abstracts.events.phases.TextPhase;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pokeregions.PokemonRegions;
import pokeregions.monsters.act3.enemies.GroudonEnemy;
import pokeregions.monsters.act3.enemies.KyogreEnemy;
import pokeregions.monsters.act3.enemies.rayquaza.RayquazaEnemy;
import pokeregions.relics.Emerald;
import pokeregions.relics.Ruby;
import pokeregions.relics.Sapphire;

import static pokeregions.PokemonRegions.makeID;

public class AncientRuins extends PhasedEvent {
    public static final String ID = makeID("AncientRuins");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;

    private final AbstractRelic relic;

    public AncientRuins() {
        super(ID, title, PokemonRegions.makeEventPath("AncientRuins.png"));
        if (AbstractDungeon.bossKey.equals(RayquazaEnemy.ID)) {
            relic = RelicLibrary.getRelic(Emerald.ID).makeCopy();
        } else if (AbstractDungeon.bossKey.equals(KyogreEnemy.ID)) {
            relic = RelicLibrary.getRelic(Sapphire.ID).makeCopy();
        } else  {
            relic = RelicLibrary.getRelic(Ruby.ID).makeCopy();
        }

        registerPhase(0, new TextPhase(DESCRIPTIONS[0]).
            addOption(OPTIONS[0] + FontHelper.colorString(OPTIONS[1], "g") + " " + FontHelper.colorString(OPTIONS[2], "r"), relic, (i)->{
                AbstractRelic randomRelic = AbstractDungeon.returnRandomScreenlessRelic(AbstractDungeon.returnRandomRelicTier());
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain(this.drawX, this.drawY, randomRelic);
                AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), relic);
                transitionKey("Take");
            }).
            addOption(OPTIONS[3], (i)->{
                transitionKey("Leave");
            }));


        registerPhase("Take", new TextPhase(DESCRIPTIONS[1]).addOption(OPTIONS[3], (t)->this.openMap()));
        registerPhase("Leave", new TextPhase(DESCRIPTIONS[2]).addOption(OPTIONS[3], (t)->this.openMap()));
        transitionKey(0);
    }

    public static boolean canSpawn() {
        if (AbstractDungeon.bossKey.equals(RayquazaEnemy.ID)) {
            return true;
        } else if (AbstractDungeon.bossKey.equals(KyogreEnemy.ID)) {
            return true;
        } else if (AbstractDungeon.bossKey.equals(GroudonEnemy.ID)) {
            return true;
        } else {
            return false;
        }
    }
}