package pokeregions.util;

import basemod.ReflectionHacks;
import pokeregions.PokemonRegions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.PokemonRegions.makeID;

public class Details {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("IntentStrings"));
    public static final String[] TEXT = uiStrings.TEXT;

    public static final String YOU = TEXT[0];
    public static final String SELF = TEXT[1];
    public static final String ALL_ENEMIES = TEXT[2];
    public static final String RANDOM_ENEMY = TEXT[3];
    public static final String LIFESTEAL = TEXT[4];
    public static final String ALL_MINIONS = TEXT[5];
    public static final String STEALS = TEXT[6];
    public static final String CLEANSE = TEXT[7];
    public static final String PARASITE = TEXT[8];
    public static final String DIES = TEXT[9];
    public static final String HALF_HEAL = TEXT[10];
    public static final String REMOVE_NEG_STR = TEXT[11];
    public static final String SUMMON = TEXT[12];

    public enum TargetType {
        SIMPLE(""), YOU(Details.YOU), SELF(Details.SELF), ALL_ENEMIES(Details.ALL_ENEMIES), RANDOM_ENEMY(Details.RANDOM_ENEMY), DRAW_PILE(""), DISCARD_PILE(""), ALL_MINIONS(Details.ALL_MINIONS);

        public String text;

        TargetType(String text) {
            this.text = text;
        }
    }

    private AbstractMonster monster;
    private int amount;
    private Texture icon;
    private TargetType target;

    private boolean overrideWithDescription;
    private String description;

    float scaleWidth = 1.0F * Settings.scale;
    float scaleHeight = Settings.scale;

    private final float Y_OFFSET = 46.0f;

    public Details(AbstractMonster monster, int amount, Texture icon) {
        this(monster, amount, icon, TargetType.SIMPLE);
    }

    public Details(AbstractMonster monster, int amount, Texture icon, TargetType target) {
        this.monster = monster;
        this.amount = amount;
        this.icon = icon;
        this.target = target;
    }

    public Details(AbstractMonster monster, String description) {
        this(monster, 0, null, null);
        this.overrideWithDescription = true;
        this.description = description;
    }

    public void renderDetails(SpriteBatch sb, int position) {
        Color color = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "intentColor");
        sb.setColor(color);
        float textY = monster.intentHb.cY + (Y_OFFSET * scaleHeight * position);
        float iconY = monster.intentHb.cY - 16.0F + (Y_OFFSET * scaleHeight * position);
        if (!overrideWithDescription) {
            if (target == TargetType.SIMPLE) {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (22.0f * scaleWidth), textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F + (8.0f * scaleWidth), iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            } else if (target == TargetType.DRAW_PILE || target == TargetType.DISCARD_PILE) {
                Texture pileTexture;
                if (target == TargetType.DRAW_PILE) {
                    pileTexture = PokemonRegions.DRAW_PILE_TEXTURE;
                } else {
                    pileTexture = PokemonRegions.DISCARD_PILE_TEXTURE;
                }
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (32.0f * scaleWidth), textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (7.0f * scaleWidth), iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                sb.draw(pileTexture, monster.intentHb.cX - 16.0F + (27.0f * scaleWidth), iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
            } else if (target == TargetType.ALL_ENEMIES || target == TargetType.ALL_MINIONS || target == TargetType.RANDOM_ENEMY) {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (42.0f * scaleWidth), textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (12.0f * scaleWidth), iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, "-> " + target.text, monster.intentHb.cX - (42.0f * scaleWidth) + (145.0f * scaleWidth), textY, color);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, Integer.toString(amount), monster.intentHb.cX - (42.0f * scaleWidth), textY, color);
                sb.draw(icon, monster.intentHb.cX - 16.0F - (12.0f * scaleWidth), iconY, 16.0F, 16.0F, 32.0F, 32.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 32, 32, false, false);
                FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, "-> " + target.text, monster.intentHb.cX - (42.0f * scaleWidth) + (90.0f * scaleWidth), textY, color);
            }
        } else {
            FontHelper.renderFontCentered(sb, FontHelper.topPanelInfoFont, description, monster.intentHb.cX - (12.0f * scaleWidth), textY, color);
        }
    }
}
