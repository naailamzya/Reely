package com.reely.utils;

import android.content.Context;
import com.reely.R;
import com.reely.models.MoodItem;
import java.util.ArrayList;
import java.util.List;
public class MoodMapper {

    private MoodMapper() {}
    public static String getGenreIdForMood(String moodKey) {
        switch (moodKey) {
            case Constants.MOOD_COMFORT:
                return Constants.GENRE_COMEDY + "|" + Constants.GENRE_FAMILY;

            case Constants.MOOD_RAINY:
                return Constants.GENRE_DRAMA + "|" + Constants.GENRE_ROMANCE;

            case Constants.MOOD_MIND_BLOWN:
                return Constants.GENRE_SCI_FI + "|" + Constants.GENRE_THRILLER;

            case Constants.MOOD_HORROR:
                return String.valueOf(Constants.GENRE_HORROR);

            case Constants.MOOD_ADRENALINE:
                return String.valueOf(Constants.GENRE_ACTION);

            case Constants.MOOD_HEARTBROKEN:
                return String.valueOf(Constants.GENRE_DRAMA);

            default:
                return String.valueOf(Constants.GENRE_ACTION);
        }
    }
    public static int getGradientStartColor(String moodKey) {
        switch (moodKey) {
            case Constants.MOOD_COMFORT:     return R.color.mood_comfort_start;
            case Constants.MOOD_RAINY:       return R.color.mood_rainy_start;
            case Constants.MOOD_MIND_BLOWN:  return R.color.mood_mindblow_start;
            case Constants.MOOD_HORROR:      return R.color.mood_horror_start;
            case Constants.MOOD_ADRENALINE:  return R.color.mood_adrenaline_start;
            case Constants.MOOD_HEARTBROKEN: return R.color.mood_heartbroken_start;
            default:                          return R.color.night_bg_primary;
        }
    }
    public static int getGradientEndColor(String moodKey) {
        switch (moodKey) {
            case Constants.MOOD_COMFORT:     return R.color.mood_comfort_end;
            case Constants.MOOD_RAINY:       return R.color.mood_rainy_end;
            case Constants.MOOD_MIND_BLOWN:  return R.color.mood_mindblow_end;
            case Constants.MOOD_HORROR:      return R.color.mood_horror_end;
            case Constants.MOOD_ADRENALINE:  return R.color.mood_adrenaline_end;
            case Constants.MOOD_HEARTBROKEN: return R.color.mood_heartbroken_end;
            default:                          return R.color.night_bg_secondary;
        }
    }
    public static int getAccentColor(String moodKey) {
        switch (moodKey) {
            case Constants.MOOD_COMFORT:     return R.color.mood_comfort_accent;
            case Constants.MOOD_RAINY:       return R.color.mood_rainy_accent;
            case Constants.MOOD_MIND_BLOWN:  return R.color.mood_mindblow_accent;
            case Constants.MOOD_HORROR:      return R.color.mood_horror_accent;
            case Constants.MOOD_ADRENALINE:  return R.color.mood_adrenaline_accent;
            case Constants.MOOD_HEARTBROKEN: return R.color.mood_heartbroken_accent;
            default:                          return R.color.night_accent_primary;
        }
    }
    public static int getQuoteResId(String moodKey) {
        switch (moodKey) {
            case Constants.MOOD_COMFORT:     return R.string.quote_comfort;
            case Constants.MOOD_RAINY:       return R.string.quote_rainy;
            case Constants.MOOD_MIND_BLOWN:  return R.string.quote_mindblow;
            case Constants.MOOD_HORROR:      return R.string.quote_horror;
            case Constants.MOOD_ADRENALINE:  return R.string.quote_adrenaline;
            case Constants.MOOD_HEARTBROKEN: return R.string.quote_heartbroken;
            default:                          return R.string.quote_comfort;
        }
    }
    public static List<MoodItem> getAllMoods(Context context) {
        List<MoodItem> moods = new ArrayList<>();

        moods.add(new MoodItem(
                Constants.MOOD_COMFORT,
                context.getString(R.string.mood_comfort),
                "🛋️",
                R.color.mood_comfort_start,
                R.color.mood_comfort_end,
                R.color.mood_comfort_accent
        ));

        moods.add(new MoodItem(
                Constants.MOOD_RAINY,
                context.getString(R.string.mood_rainy),
                "🌧️",
                R.color.mood_rainy_start,
                R.color.mood_rainy_end,
                R.color.mood_rainy_accent
        ));

        moods.add(new MoodItem(
                Constants.MOOD_MIND_BLOWN,
                context.getString(R.string.mood_mindblow),
                "🤯",
                R.color.mood_mindblow_start,
                R.color.mood_mindblow_end,
                R.color.mood_mindblow_accent
        ));

        moods.add(new MoodItem(
                Constants.MOOD_HORROR,
                context.getString(R.string.mood_horror),
                "👻",
                R.color.mood_horror_start,
                R.color.mood_horror_end,
                R.color.mood_horror_accent
        ));

        moods.add(new MoodItem(
                Constants.MOOD_ADRENALINE,
                context.getString(R.string.mood_adrenaline),
                "⚡",
                R.color.mood_adrenaline_start,
                R.color.mood_adrenaline_end,
                R.color.mood_adrenaline_accent
        ));

        moods.add(new MoodItem(
                Constants.MOOD_HEARTBROKEN,
                context.getString(R.string.mood_heartbroken),
                "💔",
                R.color.mood_heartbroken_start,
                R.color.mood_heartbroken_end,
                R.color.mood_heartbroken_accent
        ));

        return moods;
    }
}