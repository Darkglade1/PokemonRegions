package pokeregions.patches;

import basemod.ReflectionHacks;
import pokeregions.monsters.AbstractPokemonAlly;
import pokeregions.monsters.AbstractPokemonMonster;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;

import static pokeregions.PokemonRegions.makeID;

@SpirePatch(
        clz = AbstractCreature.class,
        method = "renderHealthText"
)
// Change pokemon health text to something more appropriate
public class PokemonHealthBarPatch {
    public static float HEALTH_BAR_OFFSET_Y = -28.0F * Settings.scale;
    public static float HEALTH_TEXT_OFFSET_Y = 6.0F * Settings.scale;
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("PokemonHealthBar"));
    private static final String[] TEXT = uiStrings.TEXT;

    @SpirePrefixPatch()
    public static SpireReturn<Void> ChangePokemonHealthText(AbstractCreature instance, SpriteBatch sb, float y) {
        float targetHealthBarWidth = ReflectionHacks.getPrivate(instance, AbstractCreature.class, "targetHealthBarWidth");
        if (instance instanceof AbstractPokemonAlly) {
            Color hbTextColor = ReflectionHacks.getPrivate(instance, AbstractCreature.class, "hbTextColor");
            hbTextColor = hbTextColor.cpy();
            float healthHideTimer = ReflectionHacks.getPrivate(instance, AbstractCreature.class, "healthHideTimer");
            hbTextColor.a *= healthHideTimer;
            if (targetHealthBarWidth != 0.0F) {
                FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, instance.currentHealth + "/" + instance.maxHealth + TEXT[0], instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y + 5.0F * Settings.scale, hbTextColor);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, TEXT[1], instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y - Settings.scale, hbTextColor);
            }
            return SpireReturn.Return(null);
        } else if (instance instanceof AbstractPokemonMonster && targetHealthBarWidth == 0.0F) {
            Color hbTextColor = ReflectionHacks.getPrivate(instance, AbstractCreature.class, "hbTextColor");
            hbTextColor = hbTextColor.cpy();
            float healthHideTimer = ReflectionHacks.getPrivate(instance, AbstractCreature.class, "healthHideTimer");
            hbTextColor.a *= healthHideTimer;
            if (((AbstractPokemonMonster) instance).captured) {
                FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, TEXT[2], instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y - Settings.scale, hbTextColor);
            } else {
                FontHelper.renderFontCentered(sb, FontHelper.healthInfoFont, TEXT[1], instance.hb.cX, y + HEALTH_BAR_OFFSET_Y + HEALTH_TEXT_OFFSET_Y - Settings.scale, hbTextColor);
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}